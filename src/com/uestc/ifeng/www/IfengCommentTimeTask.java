package com.uestc.ifeng.www;

import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;

public class IfengCommentTimeTask extends TimerTask{
	private int i = 0 ;
	@Override
	public void run(){
		System.out.println("����������۵�"+i+"�����п�ʼ��...");
		IFENGGuoNeiComment test = new IFENGGuoNeiComment();
		test.getIFENGGuoNeiNews();
		IFENGMilComment test1 = new IFENGMilComment();
		test1.getIFENGMilComment();
		IFENGGuoJiComment test2 = new IFENGGuoJiComment();
		test2.getIFENGGuoJiComment();
		IFENGOpinionComment test3 = new IFENGOpinionComment();
		test3.getIFENGOpinionComment();
		IFENGSheHuiComment test4 = new IFENGSheHuiComment();
		test4.getIFENGSheHuiComment();
		IFENGShenDuComment test5 = new IFENGShenDuComment();
		test5.getIFENGShenDuComment();
		System.out.println("����������۳����"+i+"�����н���...");
		System.out.println("����ʱ���ǣ�"+ new Date());
		
		i++;
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new IfengCommentTimeTask(), 0,1000);
	}
}
