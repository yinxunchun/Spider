package com.uestc.NETEASE.www;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.uestc.spider.www.CRUT;

public class NETEASECOMMENTTimeTask extends TimerTask{

	@Override
	public void run() {
		System.err.println("�������۳���ʼ...");
		NETEASEViewComment test = new NETEASEViewComment();
		test.getNETEASEViewComment();
		NETEASEWarComment test1 = new NETEASEWarComment();
		test1.getNETEASEWarComment();
		NETEASESheHuiComment test2 = new NETEASESheHuiComment();
		test2.getNETEASESheHuiComment();
		NETEASEGuoNeiComment test3 = new NETEASEGuoNeiComment();
		test3.getNETEASEGuoNeiComment();
		NETEASEGuoJiComment test4 = new NETEASEGuoJiComment();
		test4.getNETEASEGuoJiComment();
		NETEASEFocusComment test5 = new NETEASEFocusComment();
		test5.getNETEASEFocusComment();
		System.err.println("�������۳������...");
		System.err.println("����ʱ�䣺"+new Date());
	}
	
// 	 public static void SystemOut(){
//  		 
// 		try {
//			System.setOut(new PrintStream(new File("NETEASELog.txt")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
// 	 }
	
	public static void main(String[] args){
//		SystemOut();
		Timer timer = new Timer();
		timer.schedule(new NETEASECOMMENTTimeTask(), 0,5*60*1000);
	}

}
