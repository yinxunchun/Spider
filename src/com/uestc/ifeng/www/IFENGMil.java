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
 * ��ȡ�������������
 * ��ҳ��http://news.ifeng.com/mil/
 * ��Ϊ����ʱ���� �ۺ����еȵ�
 * ���������ڻ�ȡ���µ����ţ�֮ǰ�����Ž���֮��ģ���п���
 * url:http://news.ifeng.com/listpage/4550/0/1/rtlist.shtml	   
 * ��ȡÿ�����¾�������
 * */
public class IFENGMil implements IFENG{

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
	
	public void getIFENGMilNews(){
		DBName = "IFENG";
		DBTable = "MIL";
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
		//�������� http://news.ifeng.com/listpage/4550/20141221/1/rtlist.shtml
		theme = "http://news.ifeng.com/listpage/4550/"+yearBuf+monthBuf+dateBuf+"/1/rtlist.shtml";
		//��������links��������ʽ ��������ʹ��
//		newsThemeLinksReg ;
		
		//��������links��������ʽ  http://news.ifeng.com/a/20141212/42699636_0.shtml
		newsContentLinksReg = "http://news.ifeng.com/a/"+yearBuf+monthBuf+dateBuf+"/[0-9]{7,9}_0.shtml";
		//���������������links
		Queue<String> milNewsTheme = new LinkedList<String>();
		milNewsTheme.offer(theme);
//		System.out.println(guoNeiNewsTheme);
		
		//��ȡ�����������links
		Queue<String>milNewsContent = new LinkedList<String>();
		milNewsContent = findContentLinks(milNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
		//��ȡÿ��������ҳ��html
		//�����ȡ���ŵ�ʱ��
		downloadTime = yearBuf+monthBuf+dateBuf;
		int i = 0;
		while(!milNewsContent.isEmpty()){
			String url = milNewsContent.poll();
			String html = findContentHtml(url);  //��ȡ���ŵ�html
			System.out.println(url);
//			System.out.println(html);
			i++;
//			System.out.println(findNewsTitle(html,newsTitleLabel,"_�����Ѷ"));
//			System.out.println(findNewsTime(html,newsTimeLabel));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_��˾���"), findNewsOriginalTitle(html,newsTitleLabel,"_��˾���"),findNewsOriginalTitle(html,newsTitleLabel,"_��˾���"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, findNewsImages(html,newsTimeLabel),downloadTime);
		}
		System.out.println(i);
	
	
	}
	

	@Override
	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) {
		 return null;
	}

	public Queue<String> findContentLinks(Queue<String> themeLink ,String contentLinkReg) {
		// TODO Auto-generated method stub
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
		
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
				return null;
			}catch(Exception e){
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
		
		HttpURLConnection httpUrlConnection;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state;
		//�ж�url�Ƿ�Ϊ��Ч����
		try{
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("������"+url+"�����й��ϣ��Ѿ��޷��������ӣ��޷���ȡ����");
			return null ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("������"+url+"���糬�������Ѿ��޷��������ӣ��޷���ȡ����");
			return null ;
      }
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
        	httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setUseCaches(true); //ʹ�û���
            httpUrlConnection.connect();           //��������  ���ӳ�ʱ����
        } catch (IOException e) {
        	System.out.println("�����ӷ��ʳ�ʱ...");
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
						buf = buf.replaceAll("&nbsp;", "\n");
				}
			}
		}catch(Exception e){
		   
		   
		}
		return buf ;
	}
	
	@Override
	public String HandleHtml(String html, String one, String two) {
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
						buf = buf.replaceAll("&nbsp;", "\n");
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
		if(titleBuf.contains(buf))
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
		if(titleBuf.contains(buf))
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
		if(contentBuf == ""){
			contentBuf = HandleHtml(html ,"class","feed-text");
			System.out.println(contentBuf);
		}
		contentBuf = contentBuf.replaceAll("\\n       \\n        ", "");
		contentBuf = contentBuf.replaceAll("&ldquo;", "��");
		contentBuf = contentBuf.replaceAll("&rdquo;","��");
		contentBuf = contentBuf.replaceAll("&middot;", "��");
		
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
			//��������ͼƬ���ļ���
		    File f = new File("IFENGMIL");
		   	if(!f.exists()){
		    	f.mkdir();
		   	}
		   	//����ͼƬ�ļ���λ����Ϣ
		   	Queue<String> imageLocation = new LinkedList<String>();
		   	//ͼƬ������ʽ
			String imageReg = "http://y[0-9]{1}.ifengimg.com/(.*?).jpg";
			Pattern newsImage = Pattern.compile(imageReg);
			Matcher imageMatcher = newsImage.matcher(bufHtml);
			//����ͼƬ
			int i = 1 ;      //��������ͼƬ�ĸ���
			while(imageMatcher.find()){
				String bufUrl = imageMatcher.group();
				System.out.println(bufUrl);
				File fileBuf;
//				imageMatcher.group();
				String imageNameSuffix = bufUrl.substring(bufUrl.lastIndexOf("."), bufUrl.length());  //ͼƬ��׺��
				try{
					URL uri = new URL(bufUrl);  
					
					InputStream in = uri.openStream();
					FileOutputStream fo;
					if(imageNumber < 9){
						fileBuf = new File(".\\IFENGMIL",imageNameTime+"000"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf); 
						imageLocation.offer(fileBuf.getAbsolutePath());
					}else if(imageNumber < 99){
						fileBuf = new File(".\\IFENGMIL",imageNameTime+"00"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getAbsolutePath());
		            
					}else if(imageNumber < 999){
						fileBuf = new File(".\\IFENGMIL",imageNameTime+"0"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getAbsolutePath());
		  
					}else{
						fileBuf = new File(".\\IFENGMIL",imageNameTime+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getAbsolutePath());
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
		timeBuf = timeBuf.replaceAll("[^0-9]", "");
		if(timeBuf.length() > 8)
			timeBuf = timeBuf.substring(0, 8);
		if(timeBuf == "" || timeBuf == null){
			timeBuf = HandleHtml(html,"h4");
			timeBuf = timeBuf.replaceAll("[^0-9]", "");
			if(timeBuf.length() > 8)
				timeBuf = timeBuf.substring(0,8);
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
		
		if(sourceBuf.length() >29)
			sourceBuf = sourceBuf.substring(29, sourceBuf.length());  //���ݲ�ͬ���� ��ͬ����
		sourceBuf= sourceBuf.replaceAll("\\s+", "");
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
		if(categroyBuf.contains("&gt;")){
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
		if(categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			categroyBuf = categroyBuf.replaceAll("\\n", "");
			categroyBuf = categroyBuf.replaceAll("                        ", "");
		}
		return categroyBuf;
	}
	
	public static void main(String[] args){
		
		IFENGMil test = new IFENGMil();
		test.getIFENGMilNews();
	}
}
