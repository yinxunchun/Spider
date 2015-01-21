package com.uestc.ifeng.www;

import java.util.Timer;
import java.util.TimerTask;

public class IfengTimeTask extends TimerTask{

	private int i  = 0 ;
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
		System.out.println("����������е�"+i+"��...");
		i++;
		
	}

	public static void main(String args[]){
		Timer timer = new Timer();
		timer.schedule(new IfengTimeTask(), 0,20*60*1000);
	}
}
