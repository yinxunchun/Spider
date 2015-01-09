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
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
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

public class SCGOV implements GOV{


	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312
	
	//新闻主题links的正则表达式
	private String newsThemeLinksReg ; 		
	//新闻内容links的正则表达式
	private String newsContentLinksReg ; 		
	//新闻主题link 保留
	private String theme ;
	//downloadTime
	private String downloadTime;
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);	
	//图片计数
	private int imageNumber = 1 ;
	
	public void getSCGOVNews(){
		DBName = "GOV";
		DBTable = "SCGOV";
		CRUT crut =  new CRUT(DBName,DBTable);
		
		String[] newsTitleLabel = new String[]{"title",""};     //新闻标题标签 t
		String[] newsContentLabel = new String[]{"id" ,"cmsArticleContent"};  //新闻内容标签 "id","endText"
		String[] newsTimeLabel = new String[]{"",""};   //新闻时间 暂时找不到。。
		String[] newsSourceLabel =new String[]{"id","articleattribute","四川省人民政府网站"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		String[] newsCategroyLabel = new String[]{"id","articleattribute"} ; //
		
		String monthBuf ;
		String dateBuf ;
      //计算获取新闻的时间
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
		//首页links
		Queue<String> themeLinks = new LinkedList<String>();
		//新闻聚焦首页link 
		themeLinks.offer("http://www.sc.gov.cn/10462/10464/10797/index.shtml");
		//文件发布首页 link
		themeLinks.offer("http://www.sc.gov.cn/10462/10883/11066/2015/1/3/10322456.shtml");
		// 内容link 正则http://www.sc.gov.cn/10462/10883/11066/2015/1/3/10322456.shtml
		newsContentLinksReg = "http://www.sc.gov.cn/[0-9]{5}/[0-9]{5}/[0-9]{5}/"+year+"/"+month+"/"+date+"/"+"[0-9]{8}.shtml";
	
		//内容links
		Queue<String> contentLinks = new LinkedList<String>();
		contentLinks = getContentLinks(themeLinks,newsContentLinksReg);
		int i = 1 ;
		while(!contentLinks.isEmpty()){
			String url = contentLinks.poll();
			String html = getContentHtml(url);  //获取新闻的html
			System.out.println(url);
//			System.out.println(getNewsTitle(html,newsTitleLabel,""));
//			System.out.println(getNewsContent(html,newsContentLabel));
			i++;
//			System.out.println(findNewsComment(url));
//			System.out.println("\n");
			crut.add(getNewsTitle(html,newsTitleLabel,""), getNewsOriginalTitle(html,newsTitleLabel,""),getNewsOriginalTitle(html,newsTitleLabel,""), getNewsTime(html,newsTimeLabel),getNewsContent(html,newsContentLabel), getNewsSource(html,newsSourceLabel),
					getNewsOriginalSource(html,newsSourceLabel), getNewsCategroy(html,newsCategroyLabel), getNewsOriginalCategroy(html,newsCategroyLabel), url, getNewsImages(html,newsTimeLabel),downloadTime);
		}
		System.out.println(i);
		
	}
	
	@Override
	public Queue<String> getThemeLinks(String themeLink, String themeLinkReg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<String> getContentLinks(Queue<String> themeLink,String ContentLinkReg) {
		Queue<String> contentlinks = new LinkedList<String>(); // 临时征用
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
						if (node instanceof LinkTag)// 标记
							return true;
						return false;
					}
		
				});
			
				for (int i = 0; i < nodeList.size(); i++)
				{
			
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        	System.out.print(n.getStringText() + "==>> ");
//	       	 	System.out.println(n.extractLink());
					//新闻主题
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
		String html = null;                 //网页html
		
		HttpURLConnection httpUrlConnection;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state;
		//判断url是否为有效连接
		try{
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络有故障，已经无法正常链接，无法获取新闻");
			return null ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络超级慢，已经无法正常链接，无法获取新闻");
			return null ;
      }
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
        	httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setUseCaches(true); //使用缓存
            httpUrlConnection.connect();           //建立连接  链接超时处理
        } catch (IOException e) {
        	System.out.println("该链接访问超时...");
        	return null;
        }
  
        try {
            inputStream = httpUrlConnection.getInputStream(); //读取输入流
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

	@Override
	public String getNewsTitle(String html, String[] label, String buf) {
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		
		if(titleBuf.contains(" - 四川省人民政府网站")){
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(" - 四川省人民政府网站")) ;
			
		}
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
		if(titleBuf.contains(" - 四川省人民政府网站")){
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(" - 四川省人民政府网站")+12) ;	
		}
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
		contentBuf = contentBuf.replaceAll("&#160;", "");
		return contentBuf;
	}

	@Override
	public String getNewsImages(String html, String[] label) {
		String bufHtml = html;        //辅助
		String imageNameTime  = "";
		//获取图片时间，为命名服务
		imageNameTime = getNewsTime(html,label);
		//处理存放条图片的文件夹
    	File f = new File("SCGOV");
    	if(!f.exists()){
    		f.mkdir();
    	}
    	//保存图片文件的位置信息
    	Queue<String> imageLocation = new LinkedList<String>();
    	//图片正则表达式
		String imageReg = "http://imgs.sc.gov.cn/[0-9]{5}/[0-9]{5}/[0-9]{5}/"+year+"/"+month+"/"+date+"/"+year+month+date+"[0-9]{5}_[0-9]{7}.jpg";
		Pattern newsImage = Pattern.compile(imageReg);
		Matcher imageMatcher = newsImage.matcher(bufHtml);
		//处理图片
		Vector<String> visitedImageUrl = new Vector<String>();
		int i = 1 ;      //本条新闻图片的个数
		while(imageMatcher.find()){
			
			String bufUrl = imageMatcher.group();
			if(!visitedImageUrl.contains(bufUrl)){
				System.out.println(bufUrl);
				File fileBuf;
		//			imageMatcher.group();
				String imageNameSuffix = bufUrl.substring(bufUrl.lastIndexOf("."), bufUrl.length());  //图片后缀名
				try{
					URL uri = new URL(bufUrl);  
				
					InputStream in = uri.openStream();
					FileOutputStream fo;
					if(imageNumber < 9){
						fileBuf = new File(".\\SCGOV",imageNameTime+"000"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf); 
						imageLocation.offer(fileBuf.getAbsolutePath());
					}else if(imageNumber < 99){
						fileBuf = new File(".\\SCGOV",imageNameTime+"00"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getAbsolutePath());
		        
					}else if(imageNumber < 999){
						fileBuf = new File(".\\SCGOV",imageNameTime+"0"+imageNumber+"000"+i+imageNameSuffix);
							fo = new FileOutputStream(fileBuf);
							imageLocation.offer(fileBuf.getAbsolutePath());
		  
						}else{
							fileBuf = new File(".\\SCGOV",imageNameTime+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getAbsolutePath());
					}
		        
					byte[] buf = new byte[1024];  
					int length = 0;  
		//           	 System.out.println("开始下载:" + url);  
					while ((length = in.read(buf, 0, buf.length)) != -1) {  
						fo.write(buf, 0, length);  
					}  
					in.close();  
					fo.close();  
		//          	  System.out.println(imageName + "下载完成"); 
				}catch(Exception e){
					System.out.println("亲，图片下载失败！！");
					System.out.println("请检查网络是否正常！");
				}
				i ++;
			}
			visitedImageUrl.add(bufUrl);
        }  
		//如果该条新闻没有图片则图片的编号不再增加
		if(!imageLocation.isEmpty())
			imageNumber ++;
		return imageLocation.toString();
	}

	@Override
	public String getNewsTime(String html, String[] label) {
		String timeBuf = "";
		if(month < 10)
			timeBuf = timeBuf+year+"0"+month;
		else
			timeBuf = timeBuf+year+month ;
		
		if(date < 10)
			timeBuf += "0"+date;
		else
			timeBuf += date;
				
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
		sourceBuf = sourceBuf.replaceAll("\\s+", "");
		sourceBuf = sourceBuf.substring(0, sourceBuf.indexOf("【字体"));
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
		categroyBuf = categroyBuf.replaceAll("\\s+", "");
		categroyBuf = categroyBuf.substring(0, categroyBuf.indexOf("【字体"));
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
		categroyBuf = categroyBuf.replaceAll("\\s+", "");
		categroyBuf = categroyBuf.substring(0, categroyBuf.indexOf("【字体"));
		return categroyBuf;
	}

	public static void main(String[] args){
		
		SCGOV test = new SCGOV();
		test.getSCGOVNews();
	}

}
