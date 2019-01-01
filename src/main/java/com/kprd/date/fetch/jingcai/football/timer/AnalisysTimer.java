package com.kprd.date.fetch.jingcai.football.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.fetch.jingcai.football.JingCaiAnalysis;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.date.zq.Get5Datas;
import com.kprd.newliansai.mapper.FoFutureMapper;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.mapper.FoRecentMapper;
import com.kprd.newliansai.mapper.FoVshistoryMapper;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

public class AnalisysTimer {
	
	//初始化timer
	public static List<Timer> analysisTimer = new ArrayList<Timer>();
	static {
		analysisTimer.add(0, new Timer());
	}
	
	//对阵mapper
	static FoMixMapper mvsMapper = Main.applicationContext.getBean(FoMixMapper.class);
	//近期赛事mapper
	static FoRecentMapper recentMapper = Main.applicationContext.getBean(FoRecentMapper.class);
	//未来赛事mapper
	static FoFutureMapper futureMapper = Main.applicationContext.getBean(FoFutureMapper.class);
	//交战历史mapper
	static FoVshistoryMapper vsHistoryMapper = Main.applicationContext.getBean(FoVshistoryMapper.class);
	
	/**
	 * 运行定时器
	 * @param delay 间隔时间
	 * @param index 定时器timer下标
	 */
	public static void DiveDataRun(final int delay,final int index) {
		Timer timer = analysisTimer.get(index);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("开始执行分析");
				System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				methodProcess();
				DiveDataRun(delay, index);
			}
		}, delay);
	}
	
	public static void methodProcess() {
		try {
			FoMixExample fmEx = new FoMixExample();
			String day = UtilBag.dateUtil(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String today = day + " 11:59:59";
			Date kyo = sdf.parse(today);
			fmEx.createCriteria().andStarttimeGreaterThan(kyo);
			fmEx.setOrderByClause(" idUnique asc");
			List<FoMix> mixList = mvsMapper.selectByExample(fmEx);
			if(ObjectHelper.isNotEmpty(mixList)) {
				for(int i=0;i<mixList.size();i++) {
					
					try {
						container(mixList, i);
					} catch (Exception e) {
						if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
							|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1) {
							System.out.println("异常：" + e);
							System.out.println("30秒后重新执行");
							Thread.sleep(1000*30);
							container(mixList, i);
						}
					}
					
					System.out.println("睡5秒");
					Thread.sleep(1000*5);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void container(List<FoMix> mixList , int i) {
		//近期
		JingCaiAnalysis.getRecent(mixList.get(i).getHometeamid(),mixList.get(i).getIdunique(),"0",null);
		JingCaiAnalysis.getRecent(mixList.get(i).getAwayteamid(),mixList.get(i).getIdunique(),"1",null);
		//未来
		JingCaiAnalysis.getFuture(mixList.get(i).getHometeamid(),mixList.get(i).getIdunique(),"0",null);
		JingCaiAnalysis.getFuture(mixList.get(i).getAwayteamid(),mixList.get(i).getIdunique(),"1",null);
		
		JingCaiAnalysis.getvsHistory(mixList.get(i).getJingcaiid(), mixList.get(i).getIdunique());
	}
	
	public static void main(String[] args) {
		DiveDataRun(1000*60*60*24, 0);
	}
}
