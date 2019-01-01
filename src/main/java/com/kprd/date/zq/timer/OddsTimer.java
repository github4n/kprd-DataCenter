package com.kprd.date.zq.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.kprd.date.zq.OddsFetch;

public class OddsTimer {
	public static List<Timer> matchOddsList = new ArrayList<Timer>();
	static {
		matchOddsList.add(0, new Timer());
		matchOddsList.add(1, new Timer());
		matchOddsList.add(2, new Timer());
		matchOddsList.add(3, new Timer());
		matchOddsList.add(4, new Timer());
	}
	
	public static void main(String[] args) {
		oddsRun(1000*2, 0);
	}
	
	public static void oddsRun(final int delay,final int i) {
		Timer timer = matchOddsList.get(i);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				OddsFetch.oddCatch();
				System.out.println("一轮结束，等5秒");
				oddsRun(delay, i);
			}
		}, delay);
	}
}
