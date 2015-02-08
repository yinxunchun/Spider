package com.uestc.ifeng.www;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.uestc.spider.www.CRUT;
/*
 * ��飺������Ĵ�½���ż�Ϊ��������
 * �������Ǵ�20100101��ʼ һֱ�����ڣ�2010��ǰ�������Ѿ��Ҳ�����
 * ���ŵ����������磺http://news.ifeng.com/mainland/rt-channel/rtlist_20101231/1.shtml
 * �ó����ȡ��20100101��ʼһֱ�����ڵ�����
 * ������ÿ�ո������ŵĳ��򣨼����µ��յ����ţ�
 * 
 * */
public class IFENGGuoNei implements IFENG{
	
	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312	
	//��������links��������ʽ
	private String newsThemeLinksReg ; 
			
	//��������links��������ʽ
	private String newsContentLinksReg ; 
		
	//��������link
	private String theme ;
	//downloadTime
	private String downloadTime;
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);	
	//ͼƬ����
	private int imageNumber = 1 ;
	
	public void getIFENGGuoNeiNews(){
		System.out.println("IFENGGUONEI start...");
		DBName = "IFENG";
		DBTable = "GN";
		ENCODE = "utf-8";
		String[] newsTitleLabel = new String[]{"title",""};     //���ű����ǩ t
		String[] newsContentLabel = new String[]{"id" ,"main_content"};  //�������ݱ�ǩ "id","endText"
		String[] newsTimeLabel = new String[]{"class","p_time"};   //����ʱ��"class","ep-time-soure cDGray"  
		String[] newsSourceLabel =new String[]{"class","p_time","�������-��������"}; //��3��������������Դ ͬ����ʱ��
		String[] newsCategroyLabel = new String[]{"class","theCurrent cDGray js_crumb"} ; // ����
		//ʱ�丨������
		String yearBuf = ""+year;
		String monthBuf = "";
		String dateBuf = "";
		if(month < 10)  //������������
			monthBuf = "0"+month;
		else
			monthBuf = "" + month;
		if(date < 10)
			dateBuf = "0" + date;
		else 
			dateBuf = ""+ date;
		CRUT crut = new CRUT(DBName ,DBTable);
		//�������� ��ҳ����http://news.ifeng.com/mainland/rt-channel/rtlist_20141212/1.shtml
		theme = "http://news.ifeng.com/mainland/";
		String theme1 = "http://news.ifeng.com/mainland/rt-channel/rtlist_"+yearBuf+monthBuf+dateBuf+"/"+1+".shtml";
		String theme2 = "http://news.ifeng.com/mainland/rt-channel/rtlist_"+yearBuf+monthBuf+dateBuf+"/"+2+".shtml";
		//��������links��������ʽ ��������ʹ��
//		newsThemeLinksReg ;
		
		//��������links��������ʽ  http://news.ifeng.com/a/20141212/42699636_0.shtml
		newsContentLinksReg = "http://news.ifeng.com/a/"+yearBuf+monthBuf+dateBuf+"/[0-9]{7,9}_0.shtml";
		//���������������links
		Queue<String> guoNeiNewsTheme = new LinkedList<String>();
		guoNeiNewsTheme.offer(theme1);
		guoNeiNewsTheme.offer(theme2);
//		System.out.println(guoNeiNewsTheme);
		
		//��ȡ�����������links
		Queue<String>guoNeiNewsContent = new LinkedList<String>();
		guoNeiNewsContent = findContentLinks(guoNeiNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
		//��ȡÿ��������ҳ��html
		//�����ȡ���ŵ�ʱ��
		downloadTime = yearBuf+monthBuf+dateBuf;
		while(!guoNeiNewsContent.isEmpty()){
			String url = guoNeiNewsContent.poll();
				if(!crut.query("Url", url)){
					Date date = new Date();
					String html = findContentHtml(url);  //��ȡ���ŵ�html
					
					if(html!=null)
					   crut.add(findNewsTitle(html,newsTitleLabel,"_�����Ѷ"), findNewsOriginalTitle(html,newsTitleLabel,"_�����Ѷ"),findNewsOriginalTitle(html,newsTitleLabel,"_�����Ѷ"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel), findNewsSource(html,newsSourceLabel),
							findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, findNewsImages(html,newsTimeLabel),downloadTime,date);
			}
		}
		crut.destory();
		System.out.println("IFENGGUONEI over...");
	
	
	}
	

	@Override
	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) {
		 return null;
	}

	public Queue<String> findContentLinks(Queue<String> themeLink ,String contentLinkReg) {
		// TODO Auto-generated method stub
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
		Exception bufException = null ;
		Pattern newsContent = Pattern.compile(contentLinkReg);
		while(!themeLink.isEmpty()){
			
			String buf = themeLink.poll();
		
			try {
				Parser parser = new Parser(buf);
				parser.setEncoding(ENCODE);
				@SuppressWarnings("serial")
				NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
					public boolean accept(Node node)
					{
						if (node instanceof LinkTag)// ���
							return true;
						return false;
					}
		
				});
			
				for (int i = 0; i < nodeList.size(); i++)
				{
			
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        	System.out.print(n.getStringText() + "==>> ");
//	       	 	System.out.println(n.extractLink());
					//��������
					Matcher themeMatcher = newsContent.matcher(n.extractLink());
					if(themeMatcher.find()){
					
						if(!contentlinks.contains(n.extractLink()))
							contentlinks.offer(n.extractLink());
					}
				}
			}catch(ParserException e){
				bufException = e ;
			}catch(Exception e){
				bufException = e ;
			}finally{
				if(bufException != null)
					return null;
			}		
		}
//		System.out.println(contentlinks);
		return contentlinks;
	}
	
	@Override
	public String findContentHtml(String url) {
		// TODO Auto-generated method stub
		String html = null;                 //��ҳhtml
		Exception bufException = null ;
		HttpURLConnection httpUrlConnection = null;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state = 0;
		//�ж�url�Ƿ�Ϊ��Ч����
		try{
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("������"+url+"�����й��ϣ��Ѿ��޷��������ӣ��޷���ȡ����");
			bufException = e ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("������"+url+"���糬�������Ѿ��޷��������ӣ��޷���ȡ����");
			bufException = e ;
      }
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
        	httpUrlConnection.setRequestMethod("GET");
        	httpUrlConnection.setConnectTimeout(3000);
			httpUrlConnection.setReadTimeout(1000);
            httpUrlConnection.setUseCaches(true); //ʹ�û���
            httpUrlConnection.connect();           //��������  ���ӳ�ʱ����
        } catch (IOException e) {
        	System.out.println(url+"�����ӷ��ʳ�ʱ...");
        	bufException = e ;
        }finally{
        	if(bufException != null)
        		return null;
        }
  
        try {
            inputStream = httpUrlConnection.getInputStream(); //��ȡ������
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, ENCODE)); 
            String string;
            StringBuffer sb = new StringBuffer();
            while ((string = bufferedReader.readLine()) != null) {
            	sb.append(string);
            	sb.append("\n");
            }
            html = sb.toString();
        } catch (IOException e) {
//            e.printStackTrace();
        }
//        System.out.println(html);
		return html;
	}
	
	@Override
	public String HandleHtml(String html, String one) {
		if(html == null)
			return null;
		// TODO Auto-generated method stub
		NodeFilter filter = new HasAttributeFilter(one);
		String buf = "";
		try{
			Parser parser = Parser.createParser(html, ENCODE);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
   		
			if(nodes!=null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node textnode1 = (Node) nodes.elementAt(i);
					buf += textnode1.toPlainTextString();
					if(buf.contains("&nbsp;"))
						buf = buf.replaceAll("&nbsp;", " ");
				}
			}
		}catch(Exception e){
		   
		   
		}
		return buf ;
	}
	
	@Override
	public String HandleHtml(String html, String one, String two) {
		if(html == null)
			return null;
		// TODO Auto-generated method stub
		NodeFilter filter = new HasAttributeFilter(one,two);
		String buf = "";
		try{
			Parser parser = Parser.createParser(html, ENCODE);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
   		
			if(nodes!=null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node textnode1 = (Node) nodes.elementAt(i);
					buf += textnode1.toPlainTextString();
					if(buf.contains("&nbsp;"))
						buf = buf.replaceAll("&nbsp;", " ");
				}
			}
		}catch(Exception e){
 
		}
		return buf ;
	}
	//news title
	public String findNewsTitle(String html , String[] label,String buf) {
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf!=null && titleBuf.contains(buf))
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf))	;
		return titleBuf;
	}
	//news δ�������
	public String findNewsOriginalTitle(String html , String[] label,String buf) {
		// TODO Auto-generated method stub
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf != null && titleBuf.contains(buf))
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf)+buf.length())	;
		return titleBuf;
	}
	@Override
	public String findNewsContent(String html , String[] label) {
		// TODO Auto-generated method stub
		String contentBuf;
		if(label[1].equals("")){
			contentBuf = HandleHtml(html,label[0]);
		}else{
			contentBuf = HandleHtml(html,label[0],label[1]);
		}
		if(contentBuf!=null){
			contentBuf = contentBuf.replaceAll("&ldquo;", "��");
			contentBuf = contentBuf.replaceAll("&rdquo;","��");
			contentBuf = contentBuf.replaceAll("&middot;", "��");
			contentBuf = contentBuf.replaceFirst("\\n       \\n        ", "\n");
		}
		return contentBuf;
	}
	@Override
	public String findNewsImages(String html , String[] label) {
		// TODO Auto-generated method stub
			String bufHtml = "";        //����
			String imageNameTime  = "";
//			Queue<String> imageUrl = new LinkedList<String>();  //�����ȡ��ͼƬ����
			if(html.contains("<!--mainContent begin-->")&&html.contains("<!--mainContent end-->"))
				bufHtml = html.substring(html.indexOf("<!--mainContent begin-->"), html.indexOf("<!--mainContent end-->"));
			else 
				return null;
			//��ȡͼƬʱ�䣬Ϊ��������
			imageNameTime = findNewsTime(html,label) ;
			if(imageNameTime== null || imageNameTime.equals(""))
				return null;
			//��������ͼƬ���ļ���
		    File f = new File("IFENGGuoNei");
		   	if(!f.exists()){
		    	f.mkdir();
		   	}
	    	//�������ʱ�� ʱ���� ��ֹͼƬ�����ظ�
	    	Calendar photoTime = Calendar.getInstance();
	    	int photohour = photoTime.get(Calendar.HOUR_OF_DAY); 
	    	int photominute = photoTime.get(Calendar.MINUTE);
	    	int photosecond = photoTime.get(Calendar.SECOND); 
		   	//����ͼƬ�ļ���λ����Ϣ
		   	Queue<String> imageLocation = new LinkedList<String>();
		   	//ͼƬ������ʽ
			String imageReg = "http://y[0-9]{1}.ifengimg.com/(.*?).((jpg)|(png))";
			Pattern newsImage = Pattern.compile(imageReg);
			Matcher imageMatcher = newsImage.matcher(bufHtml);
			//����ͼƬ
			int i = 1 ;      //��������ͼƬ�ĸ���
			while(imageMatcher.find()){
				String bufUrl = imageMatcher.group();
//				System.out.println(bufUrl);
				File fileBuf;
//				imageMatcher.group();
				String imageNameSuffix = bufUrl.substring(bufUrl.lastIndexOf("."), bufUrl.length());  //ͼƬ��׺��
				try{
					URL uri = new URL(bufUrl);  
					
					InputStream in = uri.openStream();
					FileOutputStream fo;
					if(imageNumber < 10){
						fileBuf = new File("IFENGGuoNei",imageNameTime+photohour+photominute+photosecond+"000"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf); 
						imageLocation.offer(fileBuf.getPath());
					}else if(imageNumber < 100){
						fileBuf = new File("IFENGGuoNei",imageNameTime+photohour+photominute+photosecond+"00"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getPath());
		            
					}else if(imageNumber < 1000){
						fileBuf = new File("IFENGGuoNei",imageNameTime+photohour+photominute+photosecond+"0"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getPath());
		  
					}else{
						fileBuf = new File("IFENGGuoNei",imageNameTime+photohour+photominute+photosecond+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getPath());
					}
		           
					byte[] buf = new byte[1024];  
					int length = 0;  
//		          	 System.out.println("��ʼ����:" + url);  
					while ((length = in.read(buf, 0, buf.length)) != -1) {  
						fo.write(buf, 0, length);  
					}  
					in.close();  
					fo.close();  
//		            System.out.println(imageName + "�������"); 
				}catch(Exception e){
					System.out.println("�ף�ͼƬ����ʧ�ܣ���");
					System.out.println("���������Ƿ�������");
				}
				i ++;
					
		       }  
			//�����������û��ͼƬ��ͼƬ�ı�Ų�������
			if(!imageLocation.isEmpty())
				imageNumber ++;
			return imageLocation.toString();
	}
	//����ʱ��
	@Override
	public String findNewsTime(String html , String[] label) {
		// TODO Auto-generated method stub
		String timeBuf ="";
		if(label[1].equals("")){
			timeBuf = HandleHtml(html , label[0]);
		}else{
			timeBuf = HandleHtml(html , label[0],label[1]);
		}
		if(timeBuf != null ){
			timeBuf = timeBuf.replaceAll("[^0-9]", "");
			if(timeBuf.length() >= 8)
				timeBuf = timeBuf.substring(0, 8);	
		}
		if(timeBuf == null|| timeBuf.equals("")){
			timeBuf = HandleHtml(html,"h4");
			if(timeBuf!=null){
				timeBuf = timeBuf.replaceAll("[^0-9]", "");
				if(timeBuf.length() >= 8)
					timeBuf = timeBuf.substring(0,8);
			}
		}
		return timeBuf;
	}
	@Override
	public String findNewsSource(String html ,String[] label) {
		// TODO Auto-generated method stub
		if(label.length == 3 && (!label[2].equals("")))
			return label[2];
		else
			return null;
	}
	@Override
	public String findNewsOriginalSource(String html ,String[] label) {
		// TODO Auto-generated method stub
		String sourceBuf;
		if(label[1].equals("")){
			sourceBuf = HandleHtml(html , label[0]);
		}else{
			sourceBuf = HandleHtml(html , label[0],label[1]);
		}
		if(sourceBuf!=null){
			if(sourceBuf.length() >=29)
				sourceBuf = sourceBuf.substring(29, sourceBuf.length());  //���ݲ�ͬ���� ��ͬ����
			sourceBuf= sourceBuf.replaceAll("\\s+", "");
		}
		if(label.length == 3 && (!label[2].equals("")))
			return label[2]+"-"+sourceBuf;
		else
			return sourceBuf;
	}
	@Override
	public String findNewsCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf!=null&&categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			categroyBuf = categroyBuf.replaceAll("\\n", "");
			categroyBuf = categroyBuf.replaceAll("                        ", "");
		}
		
		return categroyBuf;
	}
	@Override
	public String findNewsOriginalCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf!=null&&categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			categroyBuf = categroyBuf.replaceAll("\\n", "");
			categroyBuf = categroyBuf.replaceAll("                        ", "");
		}
		return categroyBuf;
	}
	
	public static void main(String[] args){
		
		IFENGGuoNei test = new IFENGGuoNei();
		test.getIFENGGuoNeiNews();
	}
}
