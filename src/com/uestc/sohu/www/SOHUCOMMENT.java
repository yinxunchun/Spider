package com.uestc.sohu.www;

import java.util.Queue;

public interface SOHUCOMMENT {
	
	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //��ȡ��������
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //��ȡ��������
	
	public String findContentHtml(String url); //��ȡnews Html
	
	public String findNewsCommentUrl(String url);  //����commentUrl
	
	public Queue<String> handleNewsComment(String commentUrl);   //���comments
}
