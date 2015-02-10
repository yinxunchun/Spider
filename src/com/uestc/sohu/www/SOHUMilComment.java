package com.uestc.sohu.www;

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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.SessionNotFoundException;

import com.uestc.spider.www.CRUT;

public class SOHUMilComment implements SOHUCOMMENT{

	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312	
	//��������links��������ʽ
	private String newsThemeLinksReg ; 
			
	//��������links��������ʽ
	private String newsContentLinksReg ; 
	
	//�Ѿ����ʹ���url
	Vector<String> visitedUrl = new Vector<String>();
	
	//��������link
	private String theme ;
	//time
	Date bufDate = new Date();
	//downloadTime
	private String downloadTime;
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);	
	
	public void getSOHUMilComment(){
		System.out.println("sohumil start...");
		DBName = "SOHUCOMMENT";
		DBTable = "MIL";
		ENCODE = "gb2312";
		CRUT crut = new CRUT(DBName ,DBTable);
		//�������� ��ҳ����
		theme = "http://mil.sohu.com/wojun.shtml";
		
		//��������links��������ʽ��������
		newsThemeLinksReg = "";
		
		//��������links��������ʽ 
		newsContentLinksReg = "http://mil.sohu.com/[0-9]{4}[0-9]{2}[0-9]{2}/n[0-9]{9}.shtml";
		
		//���������������links
		Queue<String>milNewsTheme = new LinkedList<String>();
		milNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
	//				System.out.println(guoNeiNewsTheme);
		
		//��ȡ�����������links
		Queue<String>milNewsContent = new LinkedList<String>();
		milNewsContent = findContentLinks(milNewsTheme,newsContentLinksReg);
		if(milNewsContent == null){
			crut.destory();
			return ;
		}
		//��ȡÿ��������ҳ��html
		//�����ȡ���ŵ�ʱ��
		if( month < 10)
			downloadTime = year+"0"+month;
		else 
			downloadTime = year+""+month;
		if(date < 10)
			downloadTime += "0" + date;
		else 
			downloadTime += date ;
		while(!milNewsContent.isEmpty()){
			String url = milNewsContent.poll();
			if(!crut.query("Url", url)){
				String commenturl = findNewsCommentUrl(url);
//				System.out.println(commenturl);
//				handleNewsComment(commenturl);
				crut.add(url, commenturl, handleNewsComment(commenturl),bufDate );
			}else {
				String commenturl = findNewsCommentUrl(url);
//				System.out.println(commenturl);
//			 	handleNewsComment(commenturl);
				crut.update(url, commenturl, handleNewsComment(commenturl), bufDate);
			}
		}
		crut.destory();
		System.out.println("sohumil over...");
	}
	@Override
	public Queue<String> findThemeLinks(String themeLink, String themeLinkReg) {
		Queue<String> themelinks = new LinkedList<String>();
		String html = findContentHtml(themeLink);
		if(html!=null){
			html = html.replaceAll("\\s+", "");
			String commentReg = "maxPage=(.*?);var";
		
			Pattern newPage = Pattern.compile(commentReg);
		
			Matcher themeMatcher = newPage.matcher(html);
			String mm = "";
			while(themeMatcher.find()){
				mm = themeMatcher.group();
				mm = mm.substring(8, mm.indexOf(";var"));
			}
			//�Ҿ�themelinks
			String s1 = "http://mil.sohu.com/wojun_";
			String s2 = ".shtml";
			themelinks.offer(themeLink);
			int number = Integer.parseInt(mm) - 1;
			int number1 = number - 2 ;
			for(int i = number ; i > number1 ; i--){
				themelinks.offer(s1+i+s2);
//			System.out.println(s1+i +s2);
			}
		}
		//���ʾ���themelinks http://mil.sohu.com/s2005/junshiguonei.shtml
		String themeguojijunshi = "http://mil.sohu.com/s2005/junshiguonei.shtml";
		String s3 = "http://mil.sohu.com/s2005/junshiguonei_";
		String s4 = ".shtml";
		themelinks.offer(themeguojijunshi);
		String html1 = findContentHtml(themeguojijunshi);
		if(html1!=null){
			html1 = html1.replaceAll("\\s+", "");
			String commentReg1 = "maxPage=(.*?);var";
		
			Pattern newPage1 = Pattern.compile(commentReg1);
		
			Matcher themeMatcher1 = newPage1.matcher(html1);
			String mm1 = "";
			while(themeMatcher1.find()){
				mm1 = themeMatcher1.group();
				mm1 = mm1.substring(8, mm1.indexOf(";var"));
			}
			int number2 = Integer.parseInt(mm1) - 1;
			int number3 = number2 - 2 ;
			for(int i = number2 ; i > number3 ; i--){
				themelinks.offer(s3+i+s4);
//			System.out.println(s3+i +s4);
			}
		}
		return themelinks ;
	}

	@Override
	public Queue<String> findContentLinks(Queue<String> themeLink,String ContentLinkReg) {
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
		Exception bufException = null ;
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
				bufException = e ;
			}catch(Exception e){
				bufException = e ;
			}finally{
				if(bufException != null )
					return null;
			}		
		}
//		System.out.println(contentlinks);
		return contentlinks;
	}

	public String findContentHtml(String url){
		
		String html = null;                 //��ҳhtml
		Exception bufException = null ;
		HttpURLConnection httpUrlConnection = null ;
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
		}finally{
			if(bufException!= null )
				return null;
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
//            e.printStackTrace();
        }
//        System.out.println(html);
		return html;
	}
	@Override
	public String findNewsCommentUrl(String url) {
		if(url==null)
			return null;
		String commentUrl = url.substring(url.lastIndexOf("n")+1, url.lastIndexOf("."));
		return "http://quan.sohu.com/pinglun/cyqemw6s1/"+commentUrl;
	}

	@SuppressWarnings("static-access")
	@Override
	public Queue<String> handleNewsComment(String commentUrl) {
		Queue<String> comment = new LinkedList<String>();
		Exception bufException = null ;
		WebDriver driver = new FirefoxDriver();
		try {
			driver.get(commentUrl);
			new Thread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			bufException = e ;
		}catch(TimeoutException e){
			bufException = e ;
			
		}finally{
			if(bufException != null )
				return null;
		}
		  
        WebElement webElement;
        String test = null ;
        try{
        	webElement = driver.findElement(By.xpath("//div[@class='topic-changyan']"));  
        	test = webElement.getText();
        }catch(NoSuchElementException e){
        	bufException = e ;
        }catch(SessionNotFoundException e){
        	bufException = e ;
        }catch(TimeoutException e){
        	bufException = e ;
        }finally{
    		driver.close();  
            driver.quit();
            if(bufException != null )
            	return null;
        } 
        if(test!=null){
        	test = test.replaceAll("\\s+", "");
        	String commentReg = "](.*?)�ظ�����";
		
        	Pattern newPage = Pattern.compile(commentReg);
		
        	Matcher themeMatcher = newPage.matcher(test);
        	while(themeMatcher.find()){
        		String mm = themeMatcher.group();
        		mm = mm.replaceAll("]|(�ظ�����)", "");
        		comment.offer(mm+"--"+bufDate);
//        		System.out.println(mm);
        	} 
        }
        return comment;
	}

	public static void main(String[] args){
		SOHUMilComment test = new SOHUMilComment();
		test.getSOHUMilComment();
	}

}
