package com.uestc.NETEASE.www;

import java.util.Queue;

public interface NETEASECOMMENT {

	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //��ȡ��������
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //��ȡ��������
	
	public String findContentHtml(String url);    //��ȡ��������ҳ��html
	
	public String HandleHtml(String html,String one);  //����һ�������ı�ǩ��html
	
	public String HandleHtml(String html ,String one,String two);  //�������������ı�ǩ
	
	public Queue<String> findNewsComment(String url ,String html ,String[] label);
	
	public Queue<String> handleComment(String commentUrl);
}
