package com.uestc.NETEASE.www;

import java.util.Timer;
import java.util.TimerTask;

public class NETEASECOMMENTTimeTask extends TimerTask{
	private int i = 0 ;
	@Override
	public void run() {
		System.out.println("网易评论程序第"+i+"次运行开始...");
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
		System.out.println("网易评论程序第"+i+ "次运行结束...");
		i++;
	}
	
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new NETEASECOMMENTTimeTask(), 0,4*60*60*1000);
	}

}
