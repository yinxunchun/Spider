package com.uestc.local.www;

import java.util.Timer;
import java.util.TimerTask;

public class LocalTimeTask extends TimerTask{
	
	private int i = 0;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		CDQSS cdqss = new CDQSS();
		cdqss.getCDQSSNews();
		SCNEWS scnews = new SCNEWS();
		scnews.getSCXWNews();
		SCOL scol = new SCOL();
		scol.getSCOLNews();
		System.out.println("本地新闻程序运行第"+i+"次");
		i++;
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new LocalTimeTask(), 0,20*60*1000);
	}

}
