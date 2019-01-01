package com.kprd.date.update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * 类名：实时数据更新启动类
 * 功能：启动入口、线程控制、错误记录并处理
 * @author 霍俊
 */
public class RealMain {
	
	public static List<Timer> timerList = new ArrayList<Timer>();
	static{
		timerList.add(0, new Timer());
		timerList.add(1, new Timer());
		timerList.add(2, new Timer());
		timerList.add(3, new Timer());
		timerList.add(4, new Timer());
	}
	
	public static List<Timer> matchList = new ArrayList<Timer>();
	static{
		matchList.add(0, new Timer());
		matchList.add(1, new Timer());
		matchList.add(2, new Timer());
		matchList.add(3, new Timer());
		matchList.add(4, new Timer());
	}
	
	public static ApplicationContext applicationContext = null;
	
	public static void main(String[] args) {
		//启动SPRING
		applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		//1.实时比赛数据（比赛、统计）
//		matchRun(1000*60*60,0);
		matchRun(1000*60*1,1);
//		matchRun(1000*60*60,2);
//		matchRun(1000*60*60,3);
//		matchRun(1000*60*60,4);
		//2.实时赔率数据（博彩、竞彩）
//		oddsRun(1000*60*60,0);
//		oddsRun(1000*60*10,1);
//		oddsRun(1000*60*20,2);
//		oddsRun(1000*60*30,3);
//		oddsRun(1000*60*60,4);
		//4.字典更新检查（国家、联赛、博彩公司、赛季、球队、球员）【手动】
	}
	
	public static void matchRun(final int time,final int i){
		Timer timer = matchList.get(i);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Date date1 = new Date();
				MatchTimer.execute(1);
				Date date2 = new Date();
				System.out.println("耗时（比赛-统计）:"+(date2.getTime()-date1.getTime()));
				matchRun(time,i);
			}
		}, time);
	}
	
	public static void oddsRun(final int time,final int i){
		Timer timer = timerList.get(i);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Date date1 = new Date();
				OddsTimer.execute(i);
				Date date2 = new Date();
				System.out.println("耗时（"+i+"）:"+(date2.getTime()-date1.getTime()));
				oddsRun(time,i);
			}
		}, time);
	}
	
}
