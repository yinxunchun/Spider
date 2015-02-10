package com.uestc.ifeng.www;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class IfengTimeTask extends TimerTask{

	@Override
	public void run() {
		System.out.println("�����������");
		IFENGGuoJi gj = new IFENGGuoJi();
		gj.getIFENGGuoJiNews();
		IFENGGuoNei gn = new IFENGGuoNei();
		gn.getIFENGGuoNeiNews();
		IFENGMil mil = new IFENGMil();
		mil.getIFENGMilNews();
		IFENGSheHui sh = new IFENGSheHui();
		sh.getIFENGSheHuiNews();
		IFENGShenDu sd = new IFENGShenDu();
		sd.getIFENGShenDuNews();
		IFENGOpinion opinion = new IFENGOpinion();
		opinion.getIFENGOpinionNews();
		System.out.println("������Ž���...");
		System.out.println("����ʱ���ǣ�" + new Date());
		
	}

	public static void main(String args[]){
		Timer timer = new Timer();
		timer.schedule(new IfengTimeTask(), 0,60*1000);
	}
}
