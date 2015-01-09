package com.uestc.gov.www;

import java.util.Queue;

public interface GOV {
/*
 * �Ĵ���http://www.sc.gov.cn/10462/10464/10797/index.shtml 
 * http://www.sc.gov.cn/10462/10464/10684/12419/index.shtml
 * 
 * */
	public Queue<String> getThemeLinks(String themeLink ,String themeLinkReg) ; //��ȡ��������
	
	public Queue<String> getContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //��ȡ��������
	
	public String getContentHtml(String url);    //��ȡ��������ҳ��html
	
	public String HandleHtml(String html,String one);  //����һ�������ı�ǩ��html
	
	public String HandleHtml(String html ,String one,String two);  //�������������ı�ǩ
	
	public String getNewsTitle(String html ,String[] label,String buf) ; //��ȡ���ű���
	
	public String getNewsOriginalTitle(String html , String[] label,String buf) ; //��ȡ����ԭʼ����
	
	public String getNewsContent(String html , String[] label) ;    //��ȡ��������
	
	public String getNewsImages(String html , String[] label);     //��ȡ����ͼƬ
	 
	public String getNewsTime(String html , String[] label) ;        //��ȡ���ŷ���ʱ��
	
	public String getNewsSource(String html ,String[] label) ;           //������Դ
	
	public String getNewsOriginalSource(String html ,String[] label) ;     //���ž�����Դ
	 
	public String getNewsCategroy(String html , String[] label) ;  //���Ű�������
	
	public String getNewsOriginalCategroy(String html , String[] label); //���ž����������
}
