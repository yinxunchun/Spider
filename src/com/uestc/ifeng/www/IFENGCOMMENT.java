package com.uestc.ifeng.www;

import java.util.Queue;

public interface IFENGCOMMENT {
	
	
	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //��ȡ��������
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //��ȡ��������
	
	public String handleCommentUrl(String url);          //��ȡcomment url
	
	public String findCommentHtml(String commentUrl);    //��ȡ��������ҳ��html
	
	public String HandleHtml(String html,String one);  //����һ�������ı�ǩ��html
	
	public String HandleHtml(String html ,String one,String two);  //�������������ı�ǩ
	
	public Queue<String> handleComment(String commentHtml, String[] label);
}
