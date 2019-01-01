package com.kprd.date.zq.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.kprd.date.zq.Get5Datas;

public class FiveDataTimer {
	//初始化timer
	public static List<Timer> fiveDataTimer = new ArrayList<Timer>();
	static {
		fiveDataTimer.add(0, new Timer());
		fiveDataTimer.add(1, new Timer());
		fiveDataTimer.add(2, new Timer());
		fiveDataTimer.add(3, new Timer());
	}
	
	/**
	 * 运行定时器
	 * @param delay 间隔时间
	 * @param index 定时器timer下标
	 */
	public static void DiveDataRun(final int delay,final int index) {
		Timer timer = fiveDataTimer.get(index);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Get5Datas.get5Info();
				DiveDataRun(delay, index);
			}
		}, delay);
	}
	
	public static void main(String[] args) {
		//暂定10秒跑一次
		DiveDataRun(1000*10, 0);
	}
}
