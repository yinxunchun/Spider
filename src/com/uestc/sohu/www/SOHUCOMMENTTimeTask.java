package com.uestc.sohu.www;

import java.util.Timer;
import java.util.TimerTask;

public class SOHUCOMMENTTimeTask extends TimerTask{

	private int i = 0 ;
	@Override
	public void run() {
		System.out.println("搜狐评论程序第"+i+"次运行开始...");
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
		System.out.println("搜狐评论程序第"+i+"次运行结束...");
		i++;
		
	}
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.schedule(new SOHUCOMMENTTimeTask(), 0,4*60*60*1000);
	}

}
