package com.uestc.sohu.www;

import java.util.Timer;
import java.util.TimerTask;

public class SOHUTimeTask extends TimerTask{
	private int i  = 0 ;
	@Override
	public void run() {
		System.out.println("�Ѻ����ŵ�"+i+"�����п�ʼ...");
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
		System.out.println("�Ѻ����ŵ�"+i+"�����н���...");
		i++;
	}
	public static void main(String[] args){
		
		Timer timer = new Timer();
		timer.schedule(new SOHUTimeTask(), 0,20*60*1000);
		
	}

}
