package com.uestc.gov.www;

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
//����
public class HNGOV implements GOV{

	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312
	
	//��������links��������ʽ
	private String newsThemeLinksReg ; 		
	//��������links��������ʽ
	private String newsContentLinksReg ; 		
	//��������link ����
	private String theme ;
	//downloadTime
	private String downloadTime;
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);	
	//ͼƬ����
	private int imageNumber = 1 ;
	
	public void getHNGOVNews(){
		DBName = "GOV";
		DBTable = "HNGOV";
		CRUT crut =  new CRUT(DBName,DBTable);
		
		String[] newsTitleLabel = new String[]{"class","title"};     //���ű����ǩ t
		String[] newsContentLabel = new String[]{"class" ,"content"};  //�������ݱ�ǩ "id","endText"
		String[] newsTimeLabel = new String[]{"bgcolor","#E7E7E7"};   //����ʱ��"class","ep-time-soure cDGray"  
		String[] newsSourceLabel =new String[]{"bgcolor","#E7E7E7","�������������Ż���վ"}; //��3��������������Դ ͬ����ʱ��"class","ep-time-soure cDGray" �ټ���һ��"��������-��������"
		String[] newsCategroyLabel = new String[]{"width","593"} ; //
		
		String monthBuf ;
		String dateBuf ;
      //�����ȡ���ŵ�ʱ��
  		if( month < 10){
  			downloadTime = year+"0"+month;
  			monthBuf = "0" + month;
  		}else{ 
  			downloadTime = year+""+month;
  			monthBuf = "" + month ;
  		}
  		if(date < 10){
  			downloadTime += "0" + date;
  			dateBuf = "0" + date ;
  		}else{ 
  			downloadTime += date ;
  			dateBuf = "" + date;
  		}
		
		ENCODE = "gb2312";
		//��ҳlinks
		Queue<String> themeLinks = new LinkedList<String>();
		//Ҫ����ҳlink 
		themeLinks.offer("http://www.henan.gov.cn/jrhn/hnyw/");
		//�ȵ���ҳ link
		themeLinks.offer("http://www.henan.gov.cn/jrhn/ldhd/");
		//������ҳlink
		themeLinks.offer("http://www.henan.gov.cn/zwgk/zwdt/zfdt/");
		//�ط���ҳlink
		themeLinks.offer("http://www.henan.gov.cn/zwgk/zwdt/bmdt/");
		//ִ����ҳlink
		themeLinks.offer("http://www.henan.gov.cn/zwgk/rsrm/");
		// ����link ����http://www.henan.gov.cn/jrhn/system/2015/01/12/010519344.shtml
		newsContentLinksReg = "http://www.henan.gov.cn/(.*?)/system/"+year+"/"+monthBuf+"/"+dateBuf+"/"+"[0-9]{9,10}.shtml";
	
		//����links
		Queue<String> contentLinks = new LinkedList<String>();
		contentLinks = getContentLinks(themeLinks,newsContentLinksReg);
		int i = 1 ;
		while(!contentLinks.isEmpty()){
			String url = contentLinks.poll();
			if(!crut.query("Url", url)){
				Date date = new Date();
				String html = getContentHtml(url);  //��ȡ���ŵ�html
				System.out.println(url);
//			System.out.println(getNewsTitle(html,newsTitleLabel,""));
//			System.out.println(getNewsContent(html,newsContentLabel));
				i++;
//			System.out.println(findNewsComment(url));
//			System.out.println("\n");
				crut.add(getNewsTitle(html,newsTitleLabel,""), getNewsOriginalTitle(html,newsTitleLabel,""),getNewsOriginalTitle(html,newsTitleLabel,""), getNewsTime(html,newsTimeLabel),getNewsContent(html,newsContentLabel), getNewsSource(html,newsSourceLabel),
						getNewsOriginalSource(html,newsSourceLabel), getNewsCategroy(html,newsCategroyLabel), getNewsOriginalCategroy(html,newsCategroyLabel), url, getNewsImages(html,newsTimeLabel),downloadTime,date);
			}
		}
		System.out.println(i);
		crut.destory();
		
	}
	
	@Override
	public Queue<String> getThemeLinks(String themeLink, String themeLinkReg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<String> getContentLinks(Queue<String> themeLink,String ContentLinkReg) {
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
		Pattern newsContent = Pattern.compile(ContentLinkReg);
		while(!themeLink.isEmpty()){
			
			String buf = themeLink.poll();
			System.out.println(buf);
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
				System.out.println(".1");
				return null;
			}catch(Exception e){
				System.out.println(".2");
				return null;
			}		
		}
//		System.out.println(contentlinks);
		return contentlinks;
	}

	@Override
	public String getContentHtml(String url) {
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
		if(html == null)
			return null;
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

	@Override
	public String getNewsTitle(String html, String[] label, String buf) {
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf!= null)
			titleBuf = titleBuf.replaceAll("\n\n", "");
		return titleBuf;
	}

	@Override
	public String getNewsOriginalTitle(String html, String[] label, String buf) {
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf!=null)
			titleBuf = titleBuf.replaceAll("\n\n", "");
		return titleBuf;
	}

	@Override
	public String getNewsContent(String html, String[] label) {
		String contentBuf;
		if(label[1].equals("")){
			contentBuf = HandleHtml(html,label[0]);
		}else{
			contentBuf = HandleHtml(html,label[0],label[1]);
		}
		if(contentBuf!=null){
			contentBuf = contentBuf.replaceAll("\n\n", "\n");
			contentBuf = contentBuf.replaceAll("&#160;", "");
			contentBuf = contentBuf.replaceFirst("\\s+", "");
		}
		return contentBuf;
	}

	@Override
	public String getNewsImages(String html, String[] label) {
		String bufHtml = html;        //����
		String imageNameTime  = "";
		//��ȡͼƬʱ�䣬Ϊ��������
		imageNameTime = getNewsTime(html,label);
		if(imageNameTime==null)
			return null;
		//��������ͼƬ���ļ���
    	File f = new File("HNGOV");
    	if(!f.exists()){
    		f.mkdir();
    	}
    	//����ͼƬ�ļ���λ����Ϣ
    	Queue<String> imageLocation = new LinkedList<String>();
    	//ͼƬ������ʽ
		String imageReg = "http://www.henan.gov.cn/pic/[0-9]{1}/[0-9]{2}/[0-9]{2}/[0-9]{2}/[0-9]{8}_[0-9]{6}.jpg";
		Pattern newsImage = Pattern.compile(imageReg);
		Matcher imageMatcher = newsImage.matcher(bufHtml);
		//����ͼƬ
		int i = 1 ;      //��������ͼƬ�ĸ���
		while(imageMatcher.find()){
			String bufUrl = imageMatcher.group();
			System.out.println(bufUrl);
			File fileBuf;
//			imageMatcher.group();
			String imageNameSuffix = bufUrl.substring(bufUrl.lastIndexOf("."), bufUrl.length());  //ͼƬ��׺��
			try{
				URL uri = new URL(bufUrl);  
			
				InputStream in = uri.openStream();
				FileOutputStream fo;
				if(imageNumber < 9){
					fileBuf = new File("HNGOV",imageNameTime+"000"+imageNumber+"000"+i+imageNameSuffix);
					fo = new FileOutputStream(fileBuf); 
					imageLocation.offer(fileBuf.getAbsolutePath());
				}else if(imageNumber < 99){
					fileBuf = new File("HNGOV",imageNameTime+"00"+imageNumber+"000"+i+imageNameSuffix);
					fo = new FileOutputStream(fileBuf);
					imageLocation.offer(fileBuf.getAbsolutePath());
            
				}else if(imageNumber < 999){
					fileBuf = new File("HNGOV",imageNameTime+"0"+imageNumber+"000"+i+imageNameSuffix);
					fo = new FileOutputStream(fileBuf);
					imageLocation.offer(fileBuf.getAbsolutePath());
  
				}else{
					fileBuf = new File("HNGOV",imageNameTime+imageNumber+"000"+i+imageNameSuffix);
					fo = new FileOutputStream(fileBuf);
					imageLocation.offer(fileBuf.getAbsolutePath());
				}
            
				byte[] buf = new byte[1024];  
				int length = 0;  
//           	 System.out.println("��ʼ����:" + url);  
				while ((length = in.read(buf, 0, buf.length)) != -1) {  
					fo.write(buf, 0, length);  
				}  
				in.close();  
				fo.close();  
//          	  System.out.println(imageName + "�������"); 
			}catch(Exception e){
				System.out.println(e);
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

	@Override
	public String getNewsTime(String html, String[] label) {
		String timeBuf ="";
		if(label[1].equals("")){
			timeBuf = HandleHtml(html , label[0]);
		}else{
			timeBuf = HandleHtml(html , label[0],label[1]);
		}
		if(timeBuf!=null){
			timeBuf = timeBuf.replaceAll("[^0-9]", "");
			if(timeBuf.length() >= 8)
				timeBuf = timeBuf.substring(0, 8);
			else
				timeBuf = null;
		}
		return timeBuf;
	}

	@Override
	public String getNewsSource(String html, String[] label) {
		if(label.length == 3 && (!label[2].equals("")))
			return label[2];
		else
			return null;
	}

	@Override
	public String getNewsOriginalSource(String html, String[] label) {
		String sourceBuf;
		if(label[1].equals("")){
			sourceBuf = HandleHtml(html , label[0]);
		}else{
			sourceBuf = HandleHtml(html , label[0],label[1]);
		}
		if(sourceBuf!=null)
			sourceBuf = sourceBuf.replaceAll("\n\n", "");
		return label[2]+"-"+sourceBuf;
	}

	@Override
	public String getNewsCategroy(String html, String[] label) {
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf!=null){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");

			if(categroyBuf.contains("����ǰ��λ�� ��"))
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("����ǰ��λ�� ��")+8, categroyBuf.length());
			if(categroyBuf.contains("-����"))
				categroyBuf = categroyBuf.substring(0, categroyBuf.indexOf("-����"));
		
			categroyBuf = categroyBuf.replaceAll("\\s+", "-");
		}
		return categroyBuf;
	}

	@Override
	public String getNewsOriginalCategroy(String html, String[] label) {
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf!=null){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			categroyBuf = categroyBuf.replaceAll("\\s+", "-");
		}
		return categroyBuf;
	}

	public static void main(String[] args){
		
		HNGOV test = new HNGOV();
		test.getHNGOVNews();
	}
}
