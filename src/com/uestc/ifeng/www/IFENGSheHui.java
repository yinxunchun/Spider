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

public class IFENGSheHui implements IFENG{

	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312	
	//新闻主题links的正则表达式
	private String newsThemeLinksReg ; 
			
	//新闻内容links的正则表达式
	private String newsContentLinksReg ; 
		
	//新闻主题link
	private String theme ;
	//downloadTime
	private String downloadTime;
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);	
	//图片计数
	private int imageNumber = 1 ;
	
	public void getIFENGSheHuiNews(){
		System.out.println("IFENGSHEHUI start...");
		DBName = "IFENG";
		DBTable = "SH";
		ENCODE = "utf-8";
		String[] newsTitleLabel = new String[]{"title",""};     //新闻标题标签 t
		String[] newsContentLabel = new String[]{"id" ,"main_content"};  //新闻内容标签 "id","endText"
		String[] newsTimeLabel = new String[]{"class","p_time"};   //新闻时间"class","ep-time-soure cDGray"  
		String[] newsSourceLabel =new String[]{"class","p_time","凤凰新闻-国际新闻"}; //（3个参数）新闻来源 同新闻时间
		String[] newsCategroyLabel = new String[]{"class","theCurrent cDGray js_crumb"} ; // 属性
		//时间辅助参数
		String yearBuf = ""+year;
		String monthBuf = "";
		String dateBuf = "";
		if(month < 10)  //处理日期问题
			monthBuf = "0"+month;
		else
			monthBuf = "" + month;
		if(date < 10)
			dateBuf = "0" + date;
		else 
			dateBuf = ""+ date;
		CRUT crut = new CRUT(DBName ,DBTable);
		//社会新闻 首页链接http://news.ifeng.com/listpage/7837/20141220/2/rtlist.shtml
		String theme1 = "http://news.ifeng.com/listpage/7837/"+yearBuf+monthBuf+dateBuf+"/"+1+"/rtlist.shtml";
		String theme2 = "http://news.ifeng.com/listpage/7837/"+yearBuf+monthBuf+dateBuf+"/"+2+"/rtlist.shtml";
		//新闻主题links的正则表达式 保留后期使用
//		newsThemeLinksReg ;
		
		//新闻内容links的正则表达式  http://news.ifeng.com/a/20141212/42699636_0.shtml
		newsContentLinksReg = "http://news.ifeng.com/a/"+yearBuf+monthBuf+dateBuf+"/[0-9]{7,9}_0.shtml";
		//保存社会新闻主题links
		Queue<String> sheHuiNewsTheme = new LinkedList<String>();
		sheHuiNewsTheme.offer(theme1);
		sheHuiNewsTheme.offer(theme2);
//		System.out.println(guoNeiNewsTheme);
		
		//获取社会新闻内容links
		Queue<String>sheHuiNewsContent = new LinkedList<String>();
		sheHuiNewsContent = findContentLinks(sheHuiNewsTheme,newsContentLinksReg);
//		crut.destory();
//		System.out.println(guoNeiNewsContent);
		if(sheHuiNewsContent == null ){
			crut.destory();
			return ;
		}
		//获取每个新闻网页的html
		//计算获取新闻的时间
		downloadTime = yearBuf+monthBuf+dateBuf;
		while(!sheHuiNewsContent.isEmpty()){
			String url = sheHuiNewsContent.poll();
			if(!crut.query("Url", url)){
				Date date = new Date();
				String html = findContentHtml(url);  //获取新闻的html
				if(html!=null)
					crut.add(findNewsTitle(html,newsTitleLabel,"_凤凰资讯"), findNewsOriginalTitle(html,newsTitleLabel,"_凤凰资讯"),findNewsOriginalTitle(html,newsTitleLabel,"_凤凰资讯"), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel), findNewsSource(html,newsSourceLabel),
						findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, findNewsImages(html,newsTimeLabel),downloadTime,date);
			}
		}
		crut.destory();
		System.out.println("IFENGSHEHUI over...");
	
	
	}
	

	@Override
	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) {
		 return null;
	}

	public Queue<String> findContentLinks(Queue<String> themeLink ,String contentLinkReg) {
		// TODO Auto-generated method stub
		Queue<String> contentlinks = new LinkedList<String>(); // 临时征用
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
		String html = null;                 //网页html
		Exception bufException = null ;
		HttpURLConnection httpUrlConnection = null;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state = 0;
		//判断url是否为有效连接
		try{
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络有故障，已经无法正常链接，无法获取新闻");
			bufException = e ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络超级慢，已经无法正常链接，无法获取新闻");
			bufException = e ;
		}finally{
			if(bufException != null )
				return null;
		}
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
        	httpUrlConnection.setRequestMethod("GET");
        	httpUrlConnection.setConnectTimeout(3000);
			httpUrlConnection.setReadTimeout(1000);
            httpUrlConnection.setUseCaches(true); //使用缓存
            httpUrlConnection.connect();           //建立连接  链接超时处理
        } catch (IOException e) {
        	System.out.println(url+"该链接访问超时...");
        	bufException = e ;
        }finally{
        	if(bufException != null )
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
		if(titleBuf!=null&&titleBuf.contains(buf))
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf))	;
		return titleBuf;
	}
	//news 未处理标题
	public String findNewsOriginalTitle(String html , String[] label,String buf) {
		// TODO Auto-generated method stub
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf!=null&&titleBuf.contains(buf))
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
			contentBuf = contentBuf.replaceAll("&ldquo;", "“");
			contentBuf = contentBuf.replaceAll("&rdquo;","”");
			contentBuf = contentBuf.replaceAll("&middot;", "·");
			contentBuf = contentBuf.replaceFirst("\\n       \\n        ", "\n");
		}
		return contentBuf;
	}
	@Override
	public String findNewsImages(String html , String[] label) {
			if(html == null)
				return null;
			String bufHtml = "";        //辅助
			String imageNameTime  = "";
//			Queue<String> imageUrl = new LinkedList<String>();  //保存获取的图片链接
			if(html.contains("<!--mainContent begin-->")&&html.contains("<!--mainContent end-->"))
				bufHtml = html.substring(html.indexOf("<!--mainContent begin-->"), html.indexOf("<!--mainContent end-->"));
			else 
				return null;
			//获取图片时间，为命名服务
			imageNameTime = findNewsTime(html,label) ;
			if(imageNameTime == null )
				return null;
			//处理存放条图片的文件夹
		    File f = new File("IFENGSheHui");
		   	if(!f.exists()){
		    	f.mkdir();
		   	}
	    	//加入具体时间 时分秒 防止图片命名重复
	    	Calendar photoTime = Calendar.getInstance();
	    	int photohour = photoTime.get(Calendar.HOUR_OF_DAY); 
	    	int photominute = photoTime.get(Calendar.MINUTE);
	    	int photosecond = photoTime.get(Calendar.SECOND); 
		   	//保存图片文件的位置信息
		   	Queue<String> imageLocation = new LinkedList<String>();
		   	//图片正则表达式
			String imageReg = "http://y[0-9]{1}.ifengimg.com/(.*?).((jpg)|(png))";
			Pattern newsImage = Pattern.compile(imageReg);
			Matcher imageMatcher = newsImage.matcher(bufHtml);
			//处理图片
			int i = 1 ;      //本条新闻图片的个数
			while(imageMatcher.find()){
				String bufUrl = imageMatcher.group();
//				System.out.println(bufUrl);
				File fileBuf;
//				imageMatcher.group();
				String imageNameSuffix = bufUrl.substring(bufUrl.lastIndexOf("."), bufUrl.length());  //图片后缀名
				try{
					URL uri = new URL(bufUrl);  
					
					InputStream in = uri.openStream();
					FileOutputStream fo;
					if(imageNumber < 10){
						fileBuf = new File("IFENGSheHui",imageNameTime+photohour+photominute+photosecond+"000"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf); 
						imageLocation.offer(fileBuf.getPath());
					}else if(imageNumber < 100){
						fileBuf = new File("IFENGSheHui",imageNameTime+photohour+photominute+photosecond+"00"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getPath());
		            
					}else if(imageNumber < 1000){
						fileBuf = new File("IFENGSheHui",imageNameTime+photohour+photominute+photosecond+"0"+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getPath());
		  
					}else{
						fileBuf = new File("IFENGSheHui",imageNameTime+photohour+photominute+photosecond+imageNumber+"000"+i+imageNameSuffix);
						fo = new FileOutputStream(fileBuf);
						imageLocation.offer(fileBuf.getPath());
					}
		           
					byte[] buf = new byte[1024];  
					int length = 0;  
//		          	 System.out.println("开始下载:" + url);  
					while ((length = in.read(buf, 0, buf.length)) != -1) {  
						fo.write(buf, 0, length);  
					}  
					in.close();  
					fo.close();  
//		            System.out.println(imageName + "下载完成"); 
				}catch(Exception e){
					System.out.println("亲，图片下载失败！！");
					System.out.println("请检查网络是否正常！");
				}
				i ++;
					
		       }  
			//如果该条新闻没有图片则图片的编号不再增加
			if(!imageLocation.isEmpty())
				imageNumber ++;
			return imageLocation.toString();
	}
	//新闻时间
	@Override
	public String findNewsTime(String html , String[] label) {
		// TODO Auto-generated method stub
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
		}
		if(timeBuf == null || timeBuf.equals("")){
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
			if(sourceBuf.length() >29)
				sourceBuf = sourceBuf.substring(29, sourceBuf.length());  //根据不同新闻 不同处理
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
		if(categroyBuf!=null&&categroyBuf.contains("社会")&&categroyBuf.contains("正文"))
			categroyBuf = categroyBuf.substring(categroyBuf.indexOf("社会")+3, categroyBuf.indexOf("正文")-1);
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
		
		IFENGSheHui test = new IFENGSheHui();
		test.getIFENGSheHuiNews();
	}

}
