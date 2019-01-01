//package com.kprd.date.zq;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//
//public class FootTimer {
//	public static List<Timer> matchList = new ArrayList<Timer>();
//	static{
//		matchList.add(0, new Timer());
//		matchList.add(1, new Timer());
//		matchList.add(2, new Timer());
//		matchList.add(3, new Timer());
//		matchList.add(4, new Timer());
//	}
//	
//	public static void main(String[] args) {
//		matchRun(1000*5,0);
//	}
//	
//	public static void matchRun(final int time,final int i) {
//		Timer timer = matchList.get(i);
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				Date date1 = new Date();
//				try {
//					FetchMixToday.getZids(true);
//					FetchMixToday.getZids(false);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				Date date2 = new Date();
//				System.out.println("耗时:"+(date2.getTime()-date1.getTime()));
//				matchRun(time,i);
//			}
//		}, time);
//	}
//}
