package com.uestc.NETEASE.www;
import java.io.BufferedReader;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.uestc.spider.www.CRUT;
public class NETEASEWarComment implements NETEASECOMMENT{

	private String downloadTime;
	Date dateTime = new Date();
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);
	//新闻主题links的正则表达式
	private String newsThemeLinksReg ; 
				
	//新闻内容links的正则表达式
	private String newsContentLinksReg ;
			
	//新闻主题link
	private String theme ;
	//网页编码
	private String ENCODE;
	//数据库
	private String DBName ;
	private String DBTable;
	//评论url
	String commentUrl = null;
	public NETEASEWarComment(){}
	public void getNETEASEWarComment(){
		if( month < 10)
			downloadTime = year+"0"+month;
		else 
			downloadTime = year+""+month;
		if(date < 10)
			downloadTime += "0" + date;
		else 
			downloadTime += date ;
		
		/*初始化各个标签等 编码 gb2312
		 * 
		 * */
		ENCODE = "GB2312";
		DBName = "NETEASECOMMENT";   //数据库名称
		DBTable = "war";   //表名
		
		String[] label = new String[]{"class","ep-crumb JS_NTES_LOG_FE"} ; 
		CRUT crut = new CRUT(DBName,DBTable);
		/*这个模块分三部分抓取
		 * 1、主页"http://war.163.com/index.html"
		 * 2.详细分类一共10叶：http://war.163.com/special/millatestnews/ http://war.163.com/special/millatestnews_06/
		 * 3.军事历史：http://war.163.com/special/historyread/
		 * */
		//主题
		Queue<String> warNewsThemeLinks = new LinkedList<String>();
		//内容
		Queue<String> warNewsContentLinks = new LinkedList<String>();
		
		//处理主页
		theme = "http://war.163.com/index.html";
		//新闻内容的正则表达式http://war.163.com/14/1124/09/ABQAT7EM00011MTO.html
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html";
		warNewsThemeLinks.offer(theme);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			if(!crut.query("Url", url)){
				String html = findContentHtml(url);  //获取新闻的html
				System.out.println(url);
				Queue<String> buf = findNewsComment(url,html,label);
				crut.add(url, commentUrl, buf,dateTime);
				commentUrl = null;
			}else{
				String html = findContentHtml(url);  //获取新闻的html
				System.out.println(url);
				Queue<String> buf = findNewsComment(url,html,label);
				crut.update(url, commentUrl, buf,dateTime);
				commentUrl = null;
			}
		}
		
		//详细分类模块
		theme  = "http://war.163.com/special/millatestnews/";
//		newsThemeLinksReg = "http://war.163.com/special/millatestnews(_[0-9]{2})*/";  //主题正则表达式
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //内容正则表达式
		warNewsThemeLinks.offer(theme);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		int k = 1;
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			if(!crut.query("Url", url)){
				String html = findContentHtml(url);  //获取新闻的html
				System.out.println(url);
				Queue<String> buf = findNewsComment(url,html,label);
				crut.add(url, commentUrl, buf,dateTime);
				commentUrl = null;
			}else{
				String html = findContentHtml(url);  //获取新闻的html
				System.out.println(url);
				Queue<String> buf = findNewsComment(url,html,label);
				crut.update(url, commentUrl, buf,dateTime);
				commentUrl = null;
			}
		}
//		System.out.println(k);
		
		//军事历史
		theme = "http://war.163.com/special/historyread/";
//		newsThemeLinksReg = "http://war.163.com/special/historyread(_[0-9]{2})*/";  //主题正则表达式
		newsContentLinksReg = "http://war.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html"; //内容正则表达式
		warNewsThemeLinks.offer(theme);
		warNewsContentLinks = findContentLinks(warNewsThemeLinks,newsContentLinksReg);
//		int j = 1 ;
		while(!warNewsContentLinks.isEmpty()){
			String url = warNewsContentLinks.poll();
			if(!crut.query("Url", url)){
				String html = findContentHtml(url);  //获取新闻的html
				System.out.println(url);
				Queue<String> buf = findNewsComment(url,html,label);
				crut.add(url, commentUrl, buf,dateTime);
				commentUrl = null;
			}else {
				String html = findContentHtml(url);  //获取新闻的html
				System.out.println(url);
				Queue<String> buf = findNewsComment(url,html,label);
				crut.update(url, commentUrl, buf,dateTime);
				commentUrl = null;
			}
		}
//		System.out.println(j);
	}
	@Override
	public Queue<String> findThemeLinks(String themeLink, String themeLinkReg) {
		Queue<String> themelinks = new LinkedList<String>();
		Pattern newsThemeLink = Pattern.compile(themeLinkReg);
		themelinks.offer(themeLink);
		
		try {
				Parser parser = new Parser(themeLink);
				parser.setEncoding(ENCODE);
				@SuppressWarnings("serial")
				NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
					public boolean accept(Node node)
					{
						if (node instanceof LinkTag)// 标记
							return true;
						return false;
					}});
				
				for (int i = 0; i < nodeList.size(); i++)
				{
				
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//			        	System.out.print(n.getStringText() + "==>> ");
//			       	 	System.out.println(n.extractLink());
					//新闻主题
					Matcher themeMatcher = newsThemeLink.matcher(n.extractLink());
					if(themeMatcher.find()){
						if(!themelinks.contains(n.extractLink()))
							themelinks.offer(n.extractLink());
		        	}
				}
			}catch(ParserException e){
				return null;
			}catch(Exception e){
				return null;
			}
		return themelinks ;
	}

	@Override
	public Queue<String> findContentLinks(Queue<String> themeLink,String ContentLinkReg) {
		
		Queue<String> contentlinks = new LinkedList<String>(); // 临时征用
		
		Pattern newsContent = Pattern.compile(ContentLinkReg);
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
//		        	System.out.print(n.getStringText() + "==>> ");
//		       	 	System.out.println(n.extractLink());
					//新闻主题
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
//			System.out.println(contentlinks);
		return contentlinks;
	}

	@Override
	public String findContentHtml(String url) {
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
//	          e.printStackTrace();
			System.out.println("该连接"+url+"网络有故障，已经无法正常链接，无法获取新闻");
			return null ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//	          e.printStackTrace();
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
//	            e.printStackTrace();
        }
//	        System.out.println(html);
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
//获取新闻评论
 public Queue<String> findNewsComment(String url ,String html ,String[] label) {
			
		/*
			* 先判断新闻类型 再做决定
		* */
		String categroyBuf ="";
		if(label[1].equals("")){
			categroyBuf = HandleHtml(html , label[0]);
		}else{
			categroyBuf = HandleHtml(html , label[0],label[1]);
		}
		if(categroyBuf.contains("&gt;")){
			categroyBuf = categroyBuf.replaceAll("&gt;", "");
			if(categroyBuf.contains("新闻中心")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("新闻中心")+5, categroyBuf.indexOf("正文")-1);
			}else if(categroyBuf.contains("新闻频道")){
				categroyBuf = categroyBuf.substring(categroyBuf.indexOf("新闻频道")+5, categroyBuf.length());
			}
				
			categroyBuf = categroyBuf.replaceAll("\\s+", "");
		}
		//评论保存结果
		Queue<String> result = new LinkedList<String>();
		//http://comment.news.163.com/news_guoji2_bbs/ABQ1KHA20001121M.html
		String[] s1 = {"http://comment.news.163.com/news_shehui7_bbs/","http://comment.news.163.com/news_guonei8_bbs/","http://comment.news.163.com/news3_bbs/","http://comment.news.163.com/news_guoji2_bbs/","http://comment.news.163.com/news_junshi_bbs/"};
		String s2 = ".html";
		String s3 = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		if(categroyBuf.equals("社会新闻")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("易奇闻")){
			commentUrl = s1[0] + s3;
		}else if(categroyBuf.equals("国内新闻")){
			commentUrl = s1[1] + s3 ;
		}else if(categroyBuf.equals("国际新闻")){
			commentUrl = s1[3] + s3 ;
		}else if(categroyBuf.equals("军事")){
			commentUrl = s1[4] + s3 ;
		}else if(categroyBuf.equals("深度报道")){
			commentUrl = s1[0] + s3 ;
		}else if(categroyBuf.equals("评论频道")){
			commentUrl = s1[2] + s3 ;
		}else
			commentUrl = s1[2] + s3 ;
		result = handleComment(commentUrl);
		if(result == null && categroyBuf.equals("军事")){
			commentUrl = null;
			commentUrl = s1[2] +s3 ;
			result = handleComment(commentUrl);
		}
			
		if(result == null && url.contains("war.163.com")){
			commentUrl = null;
			commentUrl = s1[4] + s3 ;
			result = handleComment(commentUrl);
		}
			
		return result;
	}
		
	//烦人的评论处理
	@SuppressWarnings("null")
	public Queue<String> handleComment(String commentUrl){
			
		Queue<String> result = new LinkedList<String>();
			
		URL link = null;
			
		try {
			link = new URL(commentUrl);
		} catch (MalformedURLException e1) {
		System.out.println("what is the fuck!!!");
		return null;
		}
				
		WebClient wc=new WebClient();
		WebRequest request=new WebRequest(link); 
		request.setCharset(ENCODE);
		//	        其他报文头字段可以根据需要添加
		wc.getCookieManager().setCookiesEnabled(true);//开启cookie管理
		wc.getOptions().setJavaScriptEnabled(true);//开启js解析。对于变态网页，这个是必须的
		wc.getOptions().setCssEnabled(true);//开启css解析。对于变态网页，这个是必须的。
		wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
		wc.getOptions().setThrowExceptionOnScriptError(false);
		wc.getOptions().setTimeout(10000);
		//准备工作已经做好了
		HtmlPage page= null;
		try {
			page = wc.getPage(request);
		} catch (FailingHttpStatusCodeException e) {

		} catch (IOException e) {

		}
		if(page==null)
		{
			System.out.println("采集 "+commentUrl+" 失败!!!");
			return null;
		}
		String content=page.asText();//网页内容保存在content里
		if(content==null)
		{
			System.out.println("采集 "+commentUrl+" 失败!!!");
			return null;
		}else;
//				System.out.println(content);
		if(!content.contains("去跟贴广场看看")){
			System.out.println("居然没有 去跟帖广场看看"+ commentUrl);
			return null;
		
		}
//		String commentNumber = content.substring(content.indexOf("去跟贴广场看看")+7, content.indexOf("跟贴用户自律公约"));
//		//条数压入String 数组中
//		commentNumber = commentNumber.replaceAll("\\s+", "");
//		result = commentNumber+"\n";
//		commentNumber = null; 
		content = content.substring(0, content.indexOf("文明社会，从理性发贴开始。谢绝地域攻击。"));
		content = content.replaceAll("\\s+", "");
		String commentReg = "发表(.*?)顶";
		//	        String source = "发表哈哈哈啊哈顶顶顶顶发表家具啊姐姐顶发表哈哈哈顶发表。。。。顶发表；；；；顶发表【【】。；；；顶发表。。、；匹配顶发表(.*?)顶";
		Pattern newPage = Pattern.compile(commentReg);
		
		Matcher themeMatcher = newPage.matcher(content);
		while(themeMatcher.find()){
			String mm = themeMatcher.group();
			mm = mm.replaceAll("发表", "");
			mm = mm.replaceAll("顶", "");
		//	        	System.out.println(mm);
			result.offer(mm+"--"+dateTime);
		    mm = null;
		}
		commentReg = null ;
		content = null;
		return result;
	}
	
	public static void main(String[] args){
		NETEASEWarComment test = new NETEASEWarComment();
		test.getNETEASEWarComment();

	}
}
