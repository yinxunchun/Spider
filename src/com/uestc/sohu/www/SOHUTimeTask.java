package com.uestc.sohu.www;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SOHUTimeTask extends TimerTask{
	private int i  = 0 ;
	@Override
	public void run() {
		System.out.println("搜狐新闻第"+i+"次运行开始...");
		SOHUGuoJi gJ = new SOHUGuoJi();
		gJ.getSOHUGuoJiNews();
		SOHUGuoNei gn = new SOHUGuoNei();
		gn.getSOHUGuoNeiNews();
		SOHUMil mil = new SOHUMil();
		mil.getSOHUMilNews();
		SOHUSheHui sh = new SOHUSheHui();
		sh.getSOHUSheHuiNews();
		SOHUStar star = new SOHUStar();
		star.getSOHUStarNews();
		System.out.println("搜狐新闻第"+i+"次运行结束...");
		System.out.println("现在时间是："+ new Date());
		i++;
	}
	public static void main(String[] args){
		
		Timer timer = new Timer();
		timer.schedule(new SOHUTimeTask(), 0,5*60*1000);
		
		
		
	}
	
	

}
