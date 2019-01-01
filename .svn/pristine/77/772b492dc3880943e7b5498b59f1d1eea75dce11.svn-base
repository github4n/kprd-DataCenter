package com.kprd.date.zq.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.kprd.date.zq.Zucai14Fetch;
import com.kprd.date.zq.Zucai6Fetch;
import com.kprd.date.zq.ZucaiFourFetch;

/**
 * 老足彩定时器
 * @author Administrator
 *
 */
public class LaoZucaiTimer {
	
	public static List<Timer> laoZucaiList = new ArrayList<Timer>();
	static {
		laoZucaiList.add(0, new Timer());
		laoZucaiList.add(1, new Timer());
		laoZucaiList.add(2, new Timer());
		laoZucaiList.add(3, new Timer());
	}
	
	public static void laoZucaiRun(final int delay,final int i) {
		Timer timer = laoZucaiList.get(i);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Zucai14Fetch.get14();
				Zucai6Fetch.get6();
				ZucaiFourFetch.zucai4();
				laoZucaiRun(delay, i);
			}
		}, delay);
	}
	
	public static void main(String[] args) {
		laoZucaiRun(1000 * 1, 0);
	}
}
