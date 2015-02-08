package com.uestc.local.www;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LocalTimeTask extends TimerTask{
	
	private int i = 0;

	@Override
	public void run() {
		System.out.println("本地新闻程序开始第"+i+"次运行...");
		CDQSS cdqss = new CDQSS();
		cdqss.getCDQSSNews();
		SCNEWS scnews = new SCNEWS();
		scnews.getSCXWNews();
		SCOL scol = new SCOL();
		scol.getSCOLNews();
		System.out.println("本地新闻程序结束运行第"+i+"次...");
		System.out.println("现在时间是："+ new Date() +"\n\n");
		i++;
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new LocalTimeTask(), 0,5*60*1000);
	}

}
