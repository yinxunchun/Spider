package com.uestc.NETEASE.www;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.uestc.spider.www.CRUT;

public class NETEASETimeTask extends TimerTask{
	private int i  = 0 ;
	@Override
	public void run() {
		System.out.println("网易新闻运行第"+i+"次开始...");
		NETEASEGuoNei test = new NETEASEGuoNei();
		test.getNETEASEGuoNeiNews();
		NETEASEGuoJi test1 = new NETEASEGuoJi();
		test1.getNETEASEGuoJiNews();
		NETEASESheHui test2 = new NETEASESheHui();
		test2.getNETEASESheHuiNews();
		NETEASEView test3 = new NETEASEView();
		test3.getNETEASEViewNews();
		NETEASEWar test4 = new NETEASEWar();
		test4.getNETEASEWarNews();
		NETEASEFocus test5 = new NETEASEFocus();
		test5.getNETEASEFocusNews();
		System.out.println("网易新闻程序运行第"+i+"次完成...");
		System.out.println("现在时间为："+new Date());
		i++;
		
	}
	public static void main(String[] args){

		Timer timer = new Timer();
		timer.schedule(new NETEASETimeTask(), 0,1000);

	}
	
}
