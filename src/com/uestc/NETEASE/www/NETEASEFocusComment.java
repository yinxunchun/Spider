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

public class NETEASEFocusComment implements NETEASECOMMENT{
	
		private String downloadTime;
        Date dateTime = new Date();
		Calendar today = Calendar.getInstance();
		private int year = today.get(Calendar.YEAR);
		private int month = today.get(Calendar.MONTH)+1;
		private int date = today.get(Calendar.DATE);
		//��������links��������ʽ
		private String newsThemeLinksReg ; //= "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
					
		//��������links��������ʽ
		private String newsContentLinksReg ; //= "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
				
		//��������link
		private String theme ;
		//��ҳ����
		private String ENCODE;
		//���ݿ�
		private String DBName ;
		private String DBTable;
		//����url
		String commentUrl = null;
		public NETEASEFocusComment(){
			
		}
		
		public void getNETEASEFocusComment(){
			if( month < 10)
				downloadTime = year+"0"+month;
			else 
				downloadTime = year+""+month;
			if(date < 10)
				downloadTime += "0" + date;
			else 
				downloadTime += date ;
			//���ݿ��Լ�����
			DBName = "NETEASECOMMENT";
			DBTable = "focus";
			ENCODE = "gb2312";
			String[] label = new String[]{"class","path"} ; // ���Ա�ǩ
			//��ʼ�����ݿ�
			CRUT crut = new CRUT(DBName ,DBTable);
			//������� ��ҳ����
			theme = "http://focus.news.163.com/";
			
			//��������links��������ʽ
//			newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
			
			//��������links��������ʽ (http://view.163.com/14/1119/10/ABDHAKC500012Q9L.html#f=dlist)
			newsContentLinksReg = "http://focus.news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html";
			
			int state = 0 ;
			IOException bufException = null;
			try{
				HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(theme).openConnection(); //��������
				state = httpUrlConnection.getResponseCode();
				httpUrlConnection.disconnect();
			}catch (MalformedURLException e) {
//	          e.printStackTrace();
				System.out.println("���������Ѿ��޷��������ӣ��޷���ȡ����");
				bufException = e;
			} catch (IOException e) {
	          // TODO Auto-generated catch block
//	          e.printStackTrace();
				System.out.println("���糬�������Ѿ��޷��������ӣ��޷���ȡ����");
				bufException = e;
			}finally{
				if(bufException != null)
					return ;
			}
			if(state != 200 && state != 201){
				System.out.println("����������⣡");
				return;
			}
			
			String focusHtml = findContentHtml(theme);
			if(focusHtml == null)
				return ;
			Queue<String> visitedLinks = new LinkedList<String>();
			//ƥ�������ݵ�links
			Pattern newPage = Pattern.compile(newsContentLinksReg);
	        
	        Matcher themeMatcher = newPage.matcher(focusHtml);

	        int i = 0;
	        if(themeMatcher == null )
	        	return ;
	        while(themeMatcher.find()){
	        	i++;
	        	String url = themeMatcher.group();
	        	if(!crut.query("Url", url)){
	        		if(!visitedLinks.contains(url)){
	        			
	        			String html = findContentHtml(url);
	        			System.out.println(url);
//	        			System.out.println(findNewsOriginalSource(html,newsSourceLabel));
//	        			System.out.println(findNewsTitle(html,newsTitleLabel,"_������������"));
//	        			System.out.println(findNewsTime(html,newsTimeLabel));
	        			Queue<String> buf = findNewsComment(url,html,label);
	        			crut.add(url,commentUrl, buf,dateTime );
	        			visitedLinks.add(url);
	        			commentUrl = null;
	        		}
	        	}else{
	        			if(!visitedLinks.contains(url)){
	        			
	        			String html = findContentHtml(url);
	        			System.out.println(url);
	        			Queue<String> buf = findNewsComment(url,html,label);
	        			crut.update(url,commentUrl, buf,dateTime );
	        			visitedLinks.add(url);
	        			commentUrl = null;
	        		}
	        	}
	        }
	        crut.destory();
	        System.out.println(i);
		}
		
		@Override
		public Queue<String> findThemeLinks(String themeLink, String themeLinkReg) {
			Queue<String> themelinks = new LinkedList<String>();
			Exception bufException = null ;
			Pattern newsThemeLink = Pattern.compile(themeLinkReg);
			themelinks.offer(themeLink);
			
			try {
					Parser parser = new Parser(themeLink);
					parser.setEncoding(ENCODE);
					@SuppressWarnings("serial")
					NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
						public boolean accept(Node node)
						{
							if (node instanceof LinkTag)// ���
								return true;
							return false;
						}});
					
					for (int i = 0; i < nodeList.size(); i++)
					{
					
						LinkTag n = (LinkTag) nodeList.elementAt(i);
//			        	System.out.print(n.getStringText() + "==>> ");
//			       	 	System.out.println(n.extractLink());
						//��������
						Matcher themeMatcher = newsThemeLink.matcher(n.extractLink());
						if(themeMatcher.find()){
							if(!themelinks.contains(n.extractLink()))
								themelinks.offer(n.extractLink());
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
			return themelinks ;
		}

		@Override
		public Queue<String> findContentLinks(Queue<String> themeLink,String ContentLinkReg) {
			
			Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
			Exception bufException = null;
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
							if (node instanceof LinkTag)// ���
								return true;
							return false;
						}
			
					});
				
					for (int i = 0; i < nodeList.size(); i++)
					{
				
						LinkTag n = (LinkTag) nodeList.elementAt(i);
//		        	System.out.print(n.getStringText() + "==>> ");
//		       	 	System.out.println(n.extractLink());
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
//			System.out.println(contentlinks);
			return contentlinks;
		}

		@Override
		public String findContentHtml(String url) {
			String html = null;                 //��ҳhtml
			HttpURLConnection httpUrlConnection = null;
		    InputStream inputStream;
		    BufferedReader bufferedReader;
		    IOException bufException = null ;
			int state = 0 ;
			//�ж�url�Ƿ�Ϊ��Ч����
			try{
				httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //��������
				state = httpUrlConnection.getResponseCode();
				httpUrlConnection.disconnect();
			}catch (MalformedURLException e) {
//	          e.printStackTrace();
				System.out.println("������"+url+"�����й��ϣ��Ѿ��޷��������ӣ��޷���ȡ����");
				bufException = e ;
			} catch (IOException e) {
	          // TODO Auto-generated catch block
//	          e.printStackTrace();
				System.out.println("������"+url+"���糬�������Ѿ��޷��������ӣ��޷���ȡ����");
				bufException = e ;
	      }finally{
	    	  if(bufException != null){
	    		  return null;
	    	  }
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
//	            e.printStackTrace();
	        }
//	        System.out.println(html);
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
							buf = buf.replaceAll("&nbsp;", "\n");
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
							buf = buf.replaceAll("&nbsp;", "\n");
					}
				}
			}catch(Exception e){
	 
			}
			return buf ;
		}
	//��ȡ��������
	 public Queue<String> findNewsComment(String url ,String html ,String[] label) {
				
			/*
				* ���ж��������� ��������
			* */
			String categroyBuf ="";
			if(label[1].equals("")){
				categroyBuf = HandleHtml(html , label[0]);
			}else{
				categroyBuf = HandleHtml(html , label[0],label[1]);
			}
			if (categroyBuf== null) {
				return null;
			}
			if(categroyBuf.contains("&gt;")){
				categroyBuf = categroyBuf.replaceAll("&gt;", "");
				if(categroyBuf.contains("��������")){
					categroyBuf = categroyBuf.substring(categroyBuf.indexOf("��������")+5, categroyBuf.indexOf("����")-1);
				}else if(categroyBuf.contains("����Ƶ��")){
					categroyBuf = categroyBuf.substring(categroyBuf.indexOf("����Ƶ��")+5, categroyBuf.length());
				}
					
				categroyBuf = categroyBuf.replaceAll("\\s+", "");
			}
			//���۱�����
			Queue<String> result = new LinkedList<String>() ;
			//http://comment.news.163.com/news_guoji2_bbs/ABQ1KHA20001121M.html
			String[] s1 = {"http://comment.news.163.com/news_shehui7_bbs/","http://comment.news.163.com/news_guonei8_bbs/","http://comment.news.163.com/news3_bbs/","http://comment.news.163.com/news_guoji2_bbs/","http://comment.news.163.com/news_junshi_bbs/"};
			String s2 = ".html";
			String s3 = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
			if(categroyBuf.equals("�������")){
				commentUrl = s1[0] + s3;
			}else if(categroyBuf.equals("������")){
				commentUrl = s1[0] + s3;
			}else if(categroyBuf.equals("��������")){
				commentUrl = s1[1] + s3 ;
			}else if(categroyBuf.equals("��������")){
				commentUrl = s1[3] + s3 ;
			}else if(categroyBuf.equals("����")){
				commentUrl = s1[4] + s3 ;
			}else if(categroyBuf.equals("��ȱ���")){
				commentUrl = s1[0] + s3 ;
			}else if(categroyBuf.equals("����Ƶ��")){
				commentUrl = s1[2] + s3 ;
			}else
				commentUrl = s1[2] + s3 ;
			result = handleComment(commentUrl);
			if(result == null && categroyBuf.equals("����")){
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
			
		//���˵����۴���
		@SuppressWarnings("null")
		public Queue<String> handleComment(String commentUrl){
				
			Queue<String> result = new LinkedList<String>()  ;
				
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
			//	        ��������ͷ�ֶο��Ը�����Ҫ���
			wc.getCookieManager().setCookiesEnabled(true);//����cookie����
			wc.getOptions().setJavaScriptEnabled(true);//����js���������ڱ�̬��ҳ������Ǳ����
			wc.getOptions().setCssEnabled(true);//����css���������ڱ�̬��ҳ������Ǳ���ġ�
			wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
			wc.getOptions().setThrowExceptionOnScriptError(false);
			wc.getOptions().setTimeout(10000);
			//׼�������Ѿ�������
			HtmlPage page= null;
			try {
				page = wc.getPage(request);
			} catch (FailingHttpStatusCodeException e) {

			} catch (IOException e) {

			}
			if(page==null)
			{
				System.out.println("�ɼ� "+commentUrl+" ʧ��!!!");
				return null;
			}
			String content=page.asText();//��ҳ���ݱ�����content��
			if(content==null)
			{
				System.out.println("�ɼ� "+commentUrl+" ʧ��!!!");
				return null;
			}else;
//				System.out.println(content);
			if(content!=null&&!content.contains("ȥ�����㳡����")){
				System.out.println("��Ȼû�� ȥ�����㳡����"+ commentUrl);
				return null;
			
			}
//			String commentNumber = content.substring(content.indexOf("ȥ�����㳡����")+7, content.indexOf("�����û����ɹ�Լ"));
//			//����ѹ��String ������
//			commentNumber = commentNumber.replaceAll("\\s+", "");
//			result.offer(commentNumber);
//			commentNumber = null; 
			content = content.substring(0, content.indexOf("������ᣬ�����Է�����ʼ��л�����򹥻���"));
			content = content.replaceAll("\\s+", "");
			String commentReg = "����(.*?)��";
			
			Pattern newPage = Pattern.compile(commentReg);
			
			Matcher themeMatcher = newPage.matcher(content);
			while(themeMatcher.find()){
				String mm = themeMatcher.group();
				mm = mm.replaceAll("����", "");
				mm = mm.replaceAll("��", "");
			//	        	System.out.println(mm);
				result.offer(  mm +"--"+ dateTime );
			    mm = null;
			}
			commentReg = null ;
			content = null;
			return result;
		}
		public static void main(String[] args){
			
			NETEASEFocusComment test = new NETEASEFocusComment();
			test.getNETEASEFocusComment();
		}
}
