package com.uestc.newspaper.www;

import java.util.Date;
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
		NANDU nandu = new NANDU();
		nandu.getNANDU();
		WCC wcc = new WCC();
		wcc.getWCC();
		XMWB xmwb = new XMWB();
		xmwb.getXMWB();
		XWCB xwcb = new XWCB();
		xwcb.getXWCB();
		YNET ynet = new YNET();
		ynet.getYNET();
		System.out.println("�������������е�"+i+"��...");
		System.out.println("����ʱ���ǣ�"+ new Date() +"\n\n");
		i++;
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new newsPaperTimeTask(), 0,1000);
	}
}
