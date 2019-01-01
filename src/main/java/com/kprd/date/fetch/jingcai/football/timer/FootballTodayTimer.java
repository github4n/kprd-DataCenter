package com.kprd.date.fetch.jingcai.football.timer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.kprd.date.fetch.jingcai.football.JingCaiMixToday;
import com.kprd.date.fetch.wubai.football.WubaiMixToday;

public class FootballTodayTimer {
	
	/**
	 * 按顺序执行获取当日足彩数据
	 */
	private static void methodProcess() {
		JingCaiMixToday.getJingCaiData();
		JingCaiMixToday.getFullJson();
		HandleDifferences.handleLeaguesAndTeams();
		WubaiMixToday.getZids(true);
	}
	
	public static void main(String[] args) {
		while(true) {
			try {
				long startTime = System.currentTimeMillis();
				methodProcess();
				long endTime = System.currentTimeMillis();
				System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
				System.out.println("程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
				System.out.println("完成时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); 
				System.out.println("足球今日执行完成一遍，5分钟执行下一次");
				Thread.sleep(1000*60*2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
