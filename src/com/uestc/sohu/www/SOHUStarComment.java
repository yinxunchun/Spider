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
import org.openqa.selenium.remote.SessionNotFoundException;

import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.uestc.spider.www.CRUT;

public class SOHUStarComment implements SOHUCOMMENT{

	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312	
	//��������links��������ʽ
	private String newsThemeLinksReg ; 
			
	//��������links��������ʽ
	private String newsContentLinksReg ; 
		
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
	//ͼƬ����
	private int imageNumber = 1 ;
	
	public void getSOHUStarComment(){
		DBName = "SOHUCOMMENT";
		DBTable = "STAR";
		ENCODE = "gb2312";
		
		CRUT crut = new CRUT(DBName ,DBTable);
		//�������� ��ҳ����
		theme = "http://star.news.sohu.com/";
		
		//��������links��������ʽ��������
		newsThemeLinksReg = "";
		
		//��������links��������ʽ 
		newsContentLinksReg = "http://star.news.sohu.com/[0-9]{4}[0-9]{2}[0-9]{2}/n[0-9]{9}.shtml";
		
		//���������������links
		Queue<String> starNewsTheme = new LinkedList<String>();
		starNewsTheme.offer(theme);
//		System.out.println(guoNeiNewsTheme);
		
		//��ȡ�����������links
		Queue<String>starNewsContent = new LinkedList<String>();
		starNewsContent = findContentLinks(starNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
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
		while(!starNewsContent.isEmpty()){
			String url = starNewsContent.poll();
			if(!crut.query("Url", url)){
				String commenturl = findNewsCommentUrl(url);
				System.out.println(commenturl);
//				handleNewsComment(commenturl);
				crut.add(url, commenturl, handleNewsComment(commenturl), bufDate);
			}else{
				String commenturl = findNewsCommentUrl(url);
				System.out.println(commenturl);
//				handleNewsComment(commenturl);
				crut.update(url, commenturl, handleNewsComment(commenturl), bufDate);
			}
		}
	}
	@Override
	public Queue<String> findThemeLinks(String themeLink, String themeLinkReg) {

		return null;
	}

	@Override
	public Queue<String> findContentLinks(Queue<String> themeLink,String ContentLinkReg) {
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
		Exception bufException = null ;
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
				bufException = e ;
			}catch(Exception e){
				bufException = e ;
			}finally{
				if(bufException!= null )
					return null;
			}		
		}
		return contentlinks;
	}

	public String findContentHtml(String url){
		
		String html = null;                 //��ҳhtml
		Exception bufException = null ;
		HttpURLConnection httpUrlConnection = null ;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state = 0 ;
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
			if(bufException != null )
				return null;
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
        	if(bufException != null )
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

	@Override
	public Queue<String> handleNewsComment(String commentUrl) {
		Queue<String> comment = new LinkedList<String>();
		Exception bufException = null ;
		System.getProperties().setProperty("webdriver.chrome.driver", "./seleniumjar/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
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
        	bufException =e ;
        }catch(SessionNotFoundException e){
        	bufException =e ;
        }catch(TimeoutException e){
        	bufException = e ;
        }finally{
    		driver.close();  
            driver.quit();
            if(bufException!= null )
            	return null;
        } 
        
        test = test.replaceAll("\\s+", "");
        String commentReg = "](.*?)�ظ�����";
		
		Pattern newPage = Pattern.compile(commentReg);
		
		Matcher themeMatcher = newPage.matcher(test);
		while(themeMatcher.find()){
			String mm = themeMatcher.group();
			mm = mm.replaceAll("]|(�ظ�����)", "");
			comment.offer(mm+"--"+bufDate);
			System.out.println(mm);
		} 
        return comment;
	}

	public static void main(String[] args){
		SOHUStarComment test = new SOHUStarComment();
		test.getSOHUStarComment();
		SOHUSheHuiComment test1 = new SOHUSheHuiComment();
		test1.getSOHUSheHuiComment();
		SOHUMilComment test2 = new SOHUMilComment();
		test2.getSOHUMilComment();
		SOHUGuoNeiComment test3 = new SOHUGuoNeiComment();
		test3.getSOHUGuoNeiComment();
		SOHUGuoJiComment test4 = new SOHUGuoJiComment();
		test4.getSOHUGuoJiComment();
	}

}
