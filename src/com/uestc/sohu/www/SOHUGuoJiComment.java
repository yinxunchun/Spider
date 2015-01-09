package com.uestc.sohu.www;

import java.io.BufferedReader;
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
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.SessionNotFoundException;

import com.uestc.spider.www.CRUT;

public class SOHUGuoJiComment implements SOHUCOMMENT{
	
	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312	
	//��������links���������ʽ
	private String newsThemeLinksReg ; 
			
	//��������links���������ʽ
	private String newsContentLinksReg ; 
	
	//�Ѿ����ʹ���url
	Vector<String> visitedUrl = new Vector<String>();
	
	//��������link
	private String theme ;
	//downloadTime
	private String downloadTime;
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);	
	
	public void getSOHUGuoJiComment(){
		
		DBName = "SOHUCOMMENT1";
		DBTable = "GJ";
		ENCODE = "gb2312";
		CRUT crut = new CRUT(DBName ,DBTable);
		//�������� ��ҳ����
		theme = "http://news.sohu.com/guojixinwen.shtml";
		
		//���������������̫��Ҫ��
		newsThemeLinksReg = "";
		
		//��������links���������ʽ http://news.sohu.com/20141217/n407032613.shtml
		newsContentLinksReg = "http://news.sohu.com/[0-9]{4}[0-9]{2}[0-9]{2}/n[0-9]{9}.shtml";
		
		//���������������links
		Queue<String> guoJiNewsTheme = new LinkedList<String>();
		guoJiNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
//		System.out.println(guoNeiNewsTheme);
		
		//��ȡ������������links
		Queue<String>guoJiNewsContent = new LinkedList<String>();
		guoJiNewsContent = findContentLinks(guoJiNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
		if( month < 10)
			downloadTime = year+"0"+month;
		else 
			downloadTime = year+""+month;
		if(date < 10)
			downloadTime += "0" + date;
		else 
			downloadTime += date ;
		while(!guoJiNewsContent.isEmpty()){
			String url = guoJiNewsContent.poll();
			String commenturl = findNewsCommentUrl(url);
			System.out.println(commenturl);
//			handleNewsComment(commenturl);
			crut.add(url, commenturl, handleNewsComment(commenturl), downloadTime);
		}
	}
	@Override
	public Queue<String> findThemeLinks(String themeLink, String themeLinkReg) {
		Queue<String> themelinks = new LinkedList<String>();
		String html = findContentHtml(themeLink);
		html = html.replaceAll("\\s+", "");
		String commentReg = "maxPage=(.*?);var";
		
		Pattern newPage = Pattern.compile(commentReg);
		
		Matcher themeMatcher = newPage.matcher(html);
		String mm = "";
		while(themeMatcher.find()){
			mm = themeMatcher.group();
			mm = mm.substring(8, mm.indexOf(";var"));
		}
		
		String s1 = "http://news.sohu.com/guojixinwen_";
		String s2 = ".shtml";
		themelinks.offer(themeLink);
		int number = Integer.parseInt(mm) - 1;
		int number1 = number - 2 ;
		for(int i = number ; i > number1 ; i--){
			themelinks.offer(s1+i+s2);
		}
		return themelinks ;
	}

	@Override
	public Queue<String> findContentLinks(Queue<String> themeLink,String ContentLinkReg) {
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����	
		Pattern newsContent = Pattern.compile(ContentLinkReg);
		while(!themeLink.isEmpty()){
			try {
				Parser parser = new Parser(themeLink.poll());
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
			
				for (int i = 0 ; i < nodeList.size(); i++)
				{
			
					LinkTag n = (LinkTag) nodeList.elementAt(i);
					//��������
					Matcher themeMatcher = newsContent.matcher(n.extractLink());
					if(themeMatcher.find()){
						if(!contentlinks.contains(n.extractLink())){
							
							contentlinks.offer(n.extractLink());
						}
					}
				}
			}catch(ParserException e){
				if(contentlinks.isEmpty())
					return null;
				else
					return contentlinks;
			}catch(Exception e){
				if(contentlinks.isEmpty())
					return null;
				else
					return contentlinks;
			}		
		}
		return contentlinks;
	}

	public String findContentHtml(String url){
		
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
	public String findNewsCommentUrl(String url) {
		// http://quan.sohu.com/pinglun/cyqemw6s1/407248469
		String commentUrl = url.substring(url.lastIndexOf("n")+1, url.lastIndexOf("."));
		return "http://quan.sohu.com/pinglun/cyqemw6s1/"+commentUrl;
	}

	@SuppressWarnings({ "static-access", "finally" })
	@Override
	public Queue<String> handleNewsComment(String commentUrl) {
		Queue<String> comment = new LinkedList<String>() ;
		System.getProperties().setProperty("webdriver.chrome.driver", "./seleniumjar/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		try {
			driver.get(commentUrl);
			new Thread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return null;
		}catch(TimeoutException e){
			return null;
			
		}
		  
        WebElement webElement;
        String test;
        try{
        	webElement = driver.findElement(By.xpath("//div[@class='topic-changyan']"));  
        	test = webElement.getText();
        }catch(NoSuchElementException e){
        	return null;
        }catch(SessionNotFoundException e){
        	return null;
        }catch(TimeoutException e){
        	return null;
        }finally{
    		driver.close();  
            driver.quit();
        } 
        
        test = test.replaceAll("\\s+", "");
        String commentReg = "](.*?)�ظ�����";
		
		Pattern newPage = Pattern.compile(commentReg);
		
		Matcher themeMatcher = newPage.matcher(test);
		while(themeMatcher.find()){
			String mm = themeMatcher.group();
			mm = mm.replaceAll("]|(�ظ�����)", "");
			comment.offer(mm+"--"+downloadTime);
		} 
        return comment;
	}

	public static void main(String[] args){
		SOHUGuoJiComment test = new SOHUGuoJiComment();
		test.getSOHUGuoJiComment();
	}
}