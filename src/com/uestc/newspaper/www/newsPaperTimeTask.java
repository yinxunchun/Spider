package com.uestc.newspaper.www;

import java.util.Timer;
import java.util.TimerTask;

import com.uestc.local.www.CDQSS;
import com.uestc.local.www.LocalTimeTask;
import com.uestc.local.www.SCNEWS;
import com.uestc.local.www.SCOL;

public class newsPaperTimeTask extends TimerTask{

	private int i = 0;

	@Override
	public void run() {
		System.out.println("�������ʼ��"+i+"������...");
		BJWB bjwb = new BJWB();
		bjwb.getBJWB();
		CDSB cdsb = new CDSB();
		cdsb.getCDSB();
		CDWB cdwb = new CDWB() ;
		cdwb.getCDWB();
		GZRB gzrb = new GZRB() ;
		gzrb.getGZRB();
		JFJB jfjb =new JFJB();
		jfjb.getJFJB();
		System.out.println("�������������е�"+i+"��...");
		i++;
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new newsPaperTimeTask(), 0,20*60*1000);
	}
}
