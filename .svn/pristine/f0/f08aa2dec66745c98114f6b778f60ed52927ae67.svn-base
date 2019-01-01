package com.kprd.date.fetch.jingcai.football.timer;

import java.text.SimpleDateFormat;
import java.util.List;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.fetch.jingcai.football.JingCaiAnalysis;
import com.kprd.date.fetch.jingcai.football.JingCaiAsiaOdds;
import com.kprd.date.fetch.jingcai.football.JingCaiEuropOdds;
import com.kprd.date.fetch.jingcai.football.JingCaiMixYesterday;
import com.kprd.date.lq.update.Main;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

public class ExJingcai {
	
	//对阵mapper
	static FoMixMapper mvsMapper = Main.applicationContext.getBean(FoMixMapper.class);
	
	private static void exjc() {
		String startTime = "2017-08-25";
		String endTime = "2017-08-27";
		try {
			//先执行赛事数组抓取
			JingCaiMixYesterday.getYesterDay(startTime,endTime);
			JingCaiMixYesterday.get5Data(startTime,endTime);
//			WubaiHistory.getZids(startTime, null);//500和竞彩的历史要分开
			//在执行欧赔亚盘分析数据抓取
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FoMixExample mixEx = new FoMixExample();
			startTime += " 00:00:00";
			endTime += " 23:59:59";
			mixEx.createCriteria().andStarttimeGreaterThanOrEqualTo(sdf.parse(startTime)).andEndtimeLessThanOrEqualTo(sdf.parse(endTime));
			List<FoMix> mixList =  mvsMapper.selectByExample(mixEx);
			if(ObjectHelper.isNotEmpty(mixList)) {
				for(int i=0;i<mixList.size();i++) {
					FoMix mix = mixList.get(i);
					//这个判断是为了防止500的历史跑在前面，下面的方法报空指针
					if(ObjectHelper.isNotEmpty(mix.getJingcaiid())) {
						/**
						 * 分析页
						 */
						String sdate = sdf.format(mix.getStarttime());
						sdate = sdate.split(" ")[0];
						//竞彩未来赛事
						JingCaiAnalysis.getFuture(mix.getHometeamid(), mix.getIdunique(), "0",sdate);
						JingCaiAnalysis.getFuture(mix.getAwayteamid(), mix.getIdunique(), "1",sdate);
						//竞彩近期赛事
						JingCaiAnalysis.getRecent(mix.getHometeamid(), mix.getIdunique(), "0",sdate);
						JingCaiAnalysis.getRecent(mix.getAwayteamid(), mix.getIdunique(), "1",sdate);
						//竞彩历史对阵
						JingCaiAnalysis.getvsHistory(mix.getJingcaiid(), mix.getIdunique());
						
						/**
						 * 欧赔压盘
						 */
						//亚盘
						JingCaiAsiaOdds.getAsiaData(mix);
						//欧赔
						JingCaiEuropOdds.getEuroOddsHtml(mix);
						System.out.println("一条历史数据抓取完毕,id: " + mix.getIdunique() + " 开赛时间：" + mix.getStarttime());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		exjc();
	}
}
