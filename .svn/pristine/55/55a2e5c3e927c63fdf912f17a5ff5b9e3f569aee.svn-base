package com.kprd.date.fetch.jingcai.basketball.timer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.kprd.date.fetch.jingcai.basketball.JingCaiBasketMix;
import com.kprd.date.fetch.jingcai.basketball.JingCaiBasketYesterday;

public class BasketTodayTimer {
	
	/**
	 * 目前只有竞彩
	 */
	private static void proceedFunction() {
		JingCaiBasketMix.getBasketBall();
		JingCaiBasketMix.getFullBasketData();
		JingCaiBasketYesterday.getYesterday(null, null);
	}
	
	public static void main(String[] args) {
		while(true) {
			try {
				long startTime = System.currentTimeMillis();
				proceedFunction();
				long endTime = System.currentTimeMillis();
				System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
				System.out.println("程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
				System.out.println("篮球今日执行完成一遍，5分钟执行下一次");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println("此轮完成时间" + sdf.format(new Date()));
				Thread.sleep(1000*60*5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
