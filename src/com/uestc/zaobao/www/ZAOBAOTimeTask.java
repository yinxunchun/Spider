package com.uestc.zaobao.www;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ZAOBAOTimeTask extends TimerTask{
	public void run() {
		System.out.println("�����籨�������п�ʼ...");
		ZAOBAOGuoNei testGuoNei = new ZAOBAOGuoNei();
		testGuoNei.getZAOBAOGuoNei();
		ZAOBAOGuoJi testGuoJi = new ZAOBAOGuoJi();
		testGuoJi.getZAOBAOGuoJi();
		System.out.println("�����籨�����������...");
		System.out.println("����ʱ��Ϊ��"+new Date());

		
	}
	public static void main(String[] args){

		Timer timer = new Timer();
		timer.schedule(new ZAOBAOTimeTask(), 0,60*1000);
	}
}
