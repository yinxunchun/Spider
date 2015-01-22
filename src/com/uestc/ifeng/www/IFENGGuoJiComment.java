package com.uestc.ifeng.www;

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

public class IFENGGuoJiComment implements IFENGCOMMENT{

	private String DBName ;   //sql name
	private String DBTable ;  // collections name
	private String ENCODE ;   //html encode gb2312	
	//��������links��������ʽ
	private String newsThemeLinksReg ; 
			
	//��������links��������ʽ
	private String newsContentLinksReg ; 
		
	//��������link
	private String theme ;
	Date dateBufDate = new Date();
	//downloadTime
	private String downloadTime;
	Calendar today = Calendar.getInstance();
	private int year = today.get(Calendar.YEAR);
	private int month = today.get(Calendar.MONTH)+1;
	private int date = today.get(Calendar.DATE);	
	//ͼƬ����
	private int imageNumber = 1 ;
	
	public void getIFENGGuoJiComment(){
		DBName = "IFENGCOMMENT";
		DBTable = "GJ";
		ENCODE = "utf-8";
		String[] label = new String[]{"class","textDet"};     //����
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
		String theme1 = "http://news.ifeng.com/world/rt-channel/rtlist_"+yearBuf+monthBuf+dateBuf+"/"+1+".shtml";
		String theme2 = "http://news.ifeng.com/world/rt-channel/rtlist_"+yearBuf+monthBuf+dateBuf+"/"+2+".shtml";
		//��������links��������ʽ ��������ʹ��
//		newsThemeLinksReg ;
		
		//��������links��������ʽ  http://news.ifeng.com/a/20141212/42699636_0.shtml
		newsContentLinksReg = "http://news.ifeng.com/a/"+yearBuf+monthBuf+dateBuf+"/[0-9]{7,9}_0.shtml";
		//���������������links
		Queue<String> guoJiNewsTheme = new LinkedList<String>();
		guoJiNewsTheme.offer(theme1);
		guoJiNewsTheme.offer(theme2);
//		System.out.println(guoNeiNewsTheme);
		
		//��ȡ�����������links
		Queue<String>guoJiNewsContent = new LinkedList<String>();
		guoJiNewsContent = findContentLinks(guoJiNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
		//��ȡÿ��������ҳ��html
		//�����ȡ���ŵ�ʱ��
		downloadTime = yearBuf+monthBuf+dateBuf;
		int i = 0;
		while(!guoJiNewsContent.isEmpty()){
			String url = guoJiNewsContent.poll();
//			System.out.println(url);
			if(!crut.query("Url", url)){
				String commentUrl = handleCommentUrl(url);
				System.out.println(commentUrl);
				String commentHtml = findCommentHtml(commentUrl);
				handleComment(commentHtml,label);
				crut.add(url, commentUrl, handleComment(commentHtml,label),dateBufDate);
//				System.out.println(html);
				i++;
			}else{
				String commentUrl = handleCommentUrl(url);
				System.out.println(commentUrl);
				String commentHtml = findCommentHtml(commentUrl);
				handleComment(commentHtml,label);
				crut.update(url, commentUrl, handleComment(commentHtml,label),dateBufDate);
			}
		}
		crut.destory();
		System.out.println(i);
	
	
	}
	@Override
	public Queue<String> findThemeLinks(String themeLink, String themeLinkReg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<String> findContentLinks(Queue<String> themeLink,String ContentLinkReg) {
		Queue<String> contentlinks = new LinkedList<String>(); // ��ʱ����
		
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
	public String handleCommentUrl(String url) {

		String s1 = "http://comment.ifeng.com/view.php?docUrl=http%3A%2F%2Fnews.ifeng.com%2Fa%2F";
		String s2 = "%2F";
		String s3 = "_0.shtml"; 
		return s1 + url.substring(url.indexOf("a/")+2, url.lastIndexOf("/"))+s2
				+url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("_0.shtml"))+s3;
	}
	
	@Override
	public String findCommentHtml(String commentUrl) {
		String html = null;                 //��ҳhtml
		
		HttpURLConnection httpUrlConnection;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state;
		//�ж�url�Ƿ�Ϊ��Ч����
		try{
			httpUrlConnection = (HttpURLConnection) new URL(commentUrl).openConnection(); //��������
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("������"+commentUrl+"�����й��ϣ��Ѿ��޷��������ӣ��޷���ȡ����");
			return null ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("������"+commentUrl+"���糬�������Ѿ��޷��������ӣ��޷���ȡ����");
			return null ;
      }
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(commentUrl).openConnection(); //��������
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
					buf += textnode1.toPlainTextString()+"\n";
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
					buf += textnode1.toPlainTextString()+"\n";
					if(buf.contains("&nbsp;"))
						buf = buf.replaceAll("&nbsp;", "\n");
				}
			}
		}catch(Exception e){
 
		}
		return buf ;
	}

	@Override
	public Queue<String> handleComment(String commentHtml , String[] label) {
		Queue<String> result = new LinkedList<String>();
		String comment = "";
		if(label[1] != ""){
			comment = HandleHtml(commentHtml,label[0],label[1]);
		}else{
			comment = HandleHtml(commentHtml,label[0]);
		}
		comment = comment.replaceAll("\\t", "");
		comment = comment.replaceAll("(\n)+", "\n");
		comment = comment.replaceAll("�Ƽ�(.*?)����\n", "");
		comment = comment.replaceAll("��������(.*?)���ѣ�", "");
		comment = comment.replaceAll("\n�ֻ��û�\n","");
		String bufReg = "(.*?)\n";
		Pattern tt = Pattern.compile(bufReg);
		Matcher bufMatcher = tt.matcher(comment);
		while(bufMatcher.find()){
			result.offer(bufMatcher.group()+"--" + dateBufDate);
		}
		return result;
	}

	public static void main(String[] args){
		
		IFENGGuoJiComment test = new IFENGGuoJiComment();
		test.getIFENGGuoJiComment();
	}
}
