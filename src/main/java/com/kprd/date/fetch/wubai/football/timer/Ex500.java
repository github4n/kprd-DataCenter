package com.kprd.date.fetch.wubai.football.timer;

import java.text.SimpleDateFormat;
import java.util.List;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.fetch.wubai.football.WubaiAnalysis;
import com.kprd.date.fetch.wubai.football.WubaiAsia;
import com.kprd.date.fetch.wubai.football.WubaiEurop;
import com.kprd.date.fetch.wubai.football.WubaiHistory;
import com.kprd.date.lq.update.Main;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

public class Ex500 {
	//对阵mapper
	static FoMixMapper mvsMapper = Main.applicationContext.getBean(FoMixMapper.class);
	/**
	 * 500历史数据某一天
	 */
	private static void exwbOneDay() {
		String startTime = "2017-07-24";
		try {
			//先获取500对阵数据
			WubaiHistory.getZids(startTime, startTime);
			
			//在执行欧赔亚盘分析数据抓取
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FoMixExample mixEx = new FoMixExample();
			String sTime =startTime + " 00:00:00";
			String endTime = startTime + " 23:59:59";
			mixEx.createCriteria().andStarttimeGreaterThanOrEqualTo(sdf.parse(sTime)).andEndtimeLessThanOrEqualTo(sdf.parse(endTime));
			List<FoMix> mixList =  mvsMapper.selectByExample(mixEx);
			if(ObjectHelper.isNotEmpty(mixList)) {
				for(int i=0;i<mixList.size();i++) {
					FoMix mix = mixList.get(i);
					//通过500网站抓取过的数据才能执行以下方法
					if(ObjectHelper.isNotEmpty(mix.getWubaiid())) {
						//500分析
						WubaiAnalysis.getFutureGames(mix.getIdunique(), mix.getWubaiid(), "0");
						WubaiAnalysis.getFutureGames(mix.getIdunique(), mix.getWubaiid(), "1");
						
						WubaiAnalysis.getRecentGames(mix.getIdunique(), mix.getWubaiid(), "0");
						WubaiAnalysis.getRecentGames(mix.getIdunique(), mix.getWubaiid(), "1");
						
						WubaiAnalysis.getvsHistory(mix.getIdunique(), mix.getWubaiid());
						
						//500亚盘历史
						WubaiAsia.getAisaOdds(mix);
						//500欧赔历史
						WubaiEurop.getEuroOdds(mix);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 500历史数据
	 */
	private static void exwbDays(String startTime,String endTime) {
		try {
			//先获取500对阵数据
			WubaiHistory.getZids(startTime, null);
			
			//在执行欧赔亚盘分析数据抓取
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FoMixExample mixEx = new FoMixExample();
			startTime += " 00:00:00";
			endTime += " 23:59:59";
			mixEx.createCriteria().andStarttimeGreaterThanOrEqualTo(sdf.parse(startTime)).andEndtimeLessThanOrEqualTo(sdf.parse(endTime));
			List<FoMix> mixList =  mvsMapper.selectByExample(mixEx);
			if(ObjectHelper.isNotEmpty(mixList)) {
				for(int i=0;i<mixList.size();i++) {
					FoMix mix = mixList.get(0);
					//通过500网站抓取过的数据才能执行以下方法
					if(ObjectHelper.isNotEmpty(mix.getWubaiid())) {
						//500分析
						WubaiAnalysis.getFutureGames(mix.getIdunique(), mix.getWubaiid(), "0");
						WubaiAnalysis.getFutureGames(mix.getIdunique(), mix.getWubaiid(), "1");
						
						WubaiAnalysis.getRecentGames(mix.getIdunique(), mix.getWubaiid(), "0");
						WubaiAnalysis.getRecentGames(mix.getIdunique(), mix.getWubaiid(), "1");
						
						WubaiAnalysis.getvsHistory(mix.getIdunique(), mix.getWubaiid());
						
						//500亚盘历史
						WubaiAsia.getAisaOdds(mix);
						//500欧赔历史
						WubaiEurop.getEuroOdds(mix);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//特定某一天
		exwbOneDay();
		//时间段
		String startTime = "2017-07-24";
		String endTime = "2017-08-01";
		exwbDays(startTime,endTime);
	}
}
