package com.uestc.local.www;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LocalTimeTask extends TimerTask{
	

	@Override
	public void run() {
		System.out.println("�������ų�������...");
		CDQSS cdqss = new CDQSS();
		cdqss.getCDQSSNews();
		SCNEWS scnews = new SCNEWS();
		scnews.getSCXWNews();
		SCOL scol = new SCOL();
		scol.getSCOLNews();
		System.out.println("�������ų������...");
		System.out.println("����ʱ���ǣ�"+ new Date() +"\n\n");

	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new LocalTimeTask(), 0,5*60*1000);
	}

}
