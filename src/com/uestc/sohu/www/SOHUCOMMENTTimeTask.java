package com.uestc.sohu.www;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SOHUCOMMENTTimeTask extends TimerTask{

	@Override
	public void run() {
		System.out.println("�Ѻ����۳������п�ʼ...");
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
		System.out.println("�Ѻ����۳������н���...");
		System.out.println("time:"+ new Date());
		
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new SOHUCOMMENTTimeTask(), 0,5*60*1000);
	}

}
