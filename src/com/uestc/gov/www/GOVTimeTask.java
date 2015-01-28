package com.uestc.gov.www;

import java.util.Timer;
import java.util.TimerTask;


public class GOVTimeTask extends TimerTask{
	private int i = 0 ;
	@Override
	public void run() {
		System.out.println("政府网新闻运行第"+i+"次开始...");
		AHGOV test = new AHGOV();
		test.getAHGOVNews();
		BJGOV test1 = new  BJGOV();
		test1.getBJGOVNews();
		CHINAGOV test2 = new CHINAGOV();
		test2.getCHINAGOVNews();
		CQGOV test3 = new CQGOV();
		test3.getCQGOVNews();
		GDGOV test4 = new GDGOV();
		test4.getGDGOVNews();
		HBGOV test5 = new HBGOV();
		test5.getHBGOVNews();
		HKGOV test6 = new HKGOV();
		test6.getHKGOVNews();
		HNGOV test7 = new HNGOV();
		test7.getHNGOVNews();
		JSGOV test8 = new JSGOV();
		test8.getJSGOVNews();
		SCGOV test9 = new SCGOV();
		test9.getSCGOVNews();
		XZGOV test10 = new XZGOV();
		test10.getXZGOVNews();
		ZJGOV test11 = new ZJGOV();
		test11.getZJGOVNews();
		SHGOV test12 = new SHGOV();
		test12.getSHGOVNews();
			
		System.out.println("政府网站程序运行第"+i+"次结束...");
		i++;
	}

	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new GOVTimeTask(), 0,30*60*1000);
		
	}
}
