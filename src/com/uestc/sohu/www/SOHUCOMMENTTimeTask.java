package com.uestc.sohu.www;

import java.util.Timer;
import java.util.TimerTask;

public class SOHUCOMMENTTimeTask extends TimerTask{

	private int i = 0 ;
	@Override
	public void run() {
		System.out.println("�Ѻ����۳����"+i+"�����п�ʼ...");
		SOHUGuoJiComment gj =new SOHUGuoJiComment();
		gj.getSOHUGuoJiComment();
		SOHUGuoNeiComment gn = new SOHUGuoNeiComment();
		gn.getSOHUGuoNeiComment();
		SOHUMilComment milComment= new SOHUMilComment();
		milComment.getSOHUMilComment();
		SOHUSheHuiComment sheHuiComment= new SOHUSheHuiComment();
		sheHuiComment.getSOHUSheHuiComment();
		SOHUStarComment starComment = new SOHUStarComment();
		starComment.getSOHUStarComment();
		System.out.println("�Ѻ����۳����"+i+"�����н���...");
		i++;
		
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new SOHUCOMMENTTimeTask(), 0,4*60*60*1000);
	}

}
