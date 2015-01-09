package com.uestc.ifeng.www;

import java.util.Queue;

public interface IFENG {
	//�и���ʱ���� ò�ƾ���ÿ�����������http://news.ifeng.com/listpage/11502/20141216/1/rtlist.shtml

	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //��ȡ��������
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //��ȡ��������
	
	public String findContentHtml(String url);    //��ȡ��������ҳ��html
	
	public String HandleHtml(String html,String one);  //����һ�������ı�ǩ��html
	
	public String HandleHtml(String html ,String one,String two);  //�������������ı�ǩ
	
	public String findNewsTitle(String html ,String[] label,String buf) ; //��ȡ���ű���
	
	public String findNewsOriginalTitle(String html , String[] label,String buf) ; //��ȡ����ԭʼ����
	
	public String findNewsContent(String html , String[] label) ;    //��ȡ��������
	
	public String findNewsImages(String html , String[] label);     //��ȡ����ͼƬ
	 
	public String findNewsTime(String html , String[] label) ;        //��ȡ���ŷ���ʱ��
	
	public String findNewsSource(String html ,String[] label) ;           //������Դ
	
	public String findNewsOriginalSource(String html ,String[] label) ;     //���ž�����Դ
	 
	public String findNewsCategroy(String html , String[] label) ;  //���Ű�������
	
	public String findNewsOriginalCategroy(String html , String[] label); //���ž����������
}
