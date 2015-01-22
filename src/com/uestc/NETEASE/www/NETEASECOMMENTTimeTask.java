package com.uestc.NETEASE.www;

import java.util.Timer;
import java.util.TimerTask;

public class NETEASECOMMENTTimeTask extends TimerTask{
	private int i = 0 ;
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
		System.out.println("‘À––"+i+ "¥Œ");
		i++;
	}
	
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new NETEASECOMMENTTimeTask(), 0,4*60*60*1000);
	}

}
