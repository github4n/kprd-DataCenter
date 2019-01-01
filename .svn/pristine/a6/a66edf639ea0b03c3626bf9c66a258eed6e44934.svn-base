package com.kprd.date.lq.match;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.DocumetUtil;
import com.kprd.date.util.UtilBag;
import com.kprd.match.mapper.MLqGameMapper;
import com.kprd.match.pojo.MLqGame;
import com.kprd.match.pojo.MLqGameExample;
import com.kprd.match.pojo.MLqGameExample.Criteria;

import net.sf.json.JSONObject;

/***
 * 篮球对阵信息录入
 * @author Administrator
 *
 */
public class LqGameDate {
    /**篮球赛事对战*/
	private static String lq_game = "http://trade.500.com/jclq/index.php?playid=313&date=";
	/**篮球*/
	private static String lq_prefix = "http://liansai.500.com/lq/";
	/**分析、指数*/
	private static String lq_odds = "http://odds.500.com/lq/";
	
	static Logger logger = Logger.getLogger(LqGameDate.class);
	
	/**让分**/
	private static String rf_url = "http://i.sporttery.cn/odds_calculator/get_odds?i_format=json&poolcode[]=hdc&_=1494814915995";
	/**大小分**/
	private static String dx_url = "http://i.sporttery.cn/odds_calculator/get_odds?i_format=json&poolcode[]=hilo&_=1494815055940";
	/**胜负**/
	private static String sf_url = "http://i.sporttery.cn/odds_calculator/get_odds?i_format=json&poolcode[]=mnl&_=1494815104634";

	/**
	 * 获取让分单关
	 * @return
	 */
	private static Map<String, String> getSingle(String url,String keyword) {
		Map<String, String> singleMap = new HashMap<String, String>();
		JSONObject jsonObject = UtilBag.httpRequestForJson(url, "GET", null);
		Map map = (Map)jsonObject;
		if(ObjectHelper.isNotEmpty(map)) {
			Object obj = map.get("data");
			if(ObjectHelper.isNotEmpty(obj)) {
				Map mp = (Map)obj;
				Iterator<Entry<Object, Object>> it = mp.entrySet().iterator();  
				while (it.hasNext()) {  
		            Entry<Object, Object> entry = it.next();  
		            Object key = entry.getKey();  
		            JSONObject o = jsonObject.getJSONObject("data");
		    		o = o.getJSONObject(String.valueOf(key));
		    		String num = (String)o.get("num");
		    		o = o.getJSONObject(keyword);
		    		String single = String.valueOf(o.get("single"));
		    		singleMap.put(num, single);
		        }  
			}
		}
		return singleMap;
	}
	
	/**
	 * 获取大小分
	 * @return
	 */
	private static Map<String, String> getDxf(String url,String keyword) {
		Map<String, String> singleMap = new HashMap<String, String>();
		JSONObject jsonObject = UtilBag.httpRequestForJson(url, "GET", null);
		Map map = (Map)jsonObject;
		if(ObjectHelper.isNotEmpty(map)) {
			Object obj = map.get("data");
			if(ObjectHelper.isNotEmpty(obj)) {
				Map mp = (Map)obj;
				Iterator<Entry<Object, Object>> it = mp.entrySet().iterator();  
				while (it.hasNext()) {  
		            Entry<Object, Object> entry = it.next();  
		            Object key = entry.getKey();  
		            JSONObject o = jsonObject.getJSONObject("data");
		    		o = o.getJSONObject(String.valueOf(key));
		    		String num = (String)o.get("num");
		    		o = o.getJSONObject(keyword);
		    		String fixedodds = String.valueOf(o.get("fixedodds"));
		    		singleMap.put(num, fixedodds);
		        }  
			}
		}
		return singleMap;
	}
	
	/**
	 * 获取让分
	 * @return
	 */
	private static Map<String, String> getRs(String url,String keyword) {
		Map<String, String> singleMap = new HashMap<String, String>();
		JSONObject jsonObject = UtilBag.httpRequestForJson(url, "GET", null);
		Map map = (Map)jsonObject;
		if(ObjectHelper.isNotEmpty(map)) {
			Object obj = map.get("data");
			if(ObjectHelper.isNotEmpty(obj)) {
				Map mp = (Map)obj;
				Iterator<Entry<Object, Object>> it = mp.entrySet().iterator();  
				while (it.hasNext()) {  
		            Entry<Object, Object> entry = it.next();  
		            Object key = entry.getKey();  
		            JSONObject o = jsonObject.getJSONObject("data");
		    		o = o.getJSONObject(String.valueOf(key));
		    		String num = (String)o.get("num");
		    		o = o.getJSONObject(keyword);
		    		String fixedodds = String.valueOf(o.get("fixedodds"));
		    		singleMap.put(num, fixedodds);
		        }  
			}
		}
		return singleMap;
	}
	
	/**
	 * 抓取历史对阵信息
	 * @param btime 抓取开始日期
	 * @param etime 抓取结束日期
	 * @throws ParseException 
	 */
	public static void getIngGame() throws ParseException {
		System.out.println("开始执行篮球");
		logger.info("1111111111");
		try {
		MLqGameMapper mLqGameMapper = Main.applicationContext.getBean(MLqGameMapper.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		List<String> times = new ArrayList<>();
		String time = (new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		calendar.add(Calendar.DATE, -1);
		String time1 = (new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		times.add(time);
		times.add(time1);
		for(int t=0;t<times.size();t++) {
			if(t==0) {

				Map<String, String> singleMap = getSingle(rf_url,"hdc");
				Map<String, String> singleMap2 =  getSingle(dx_url,"hilo");
				Map<String, String> singleMap3 =  getSingle(sf_url,"mnl");
				Map<String, String> rfMap = getRs(rf_url,"hdc");
				Map<String, String> dxfMap2 =  getDxf(dx_url,"hilo");
				
				Document doc = DocumetUtil.getDocumentByUrl(lq_game+times.get(t));
				Elements elements = doc.select("#vsTable");
				Elements es = elements.get(0).children();
				//System.out.println(es);
				if(null != es && es.size() > 0) {
					for(int i=0;i<es.size();i++) {
						Elements games_tb =es.get(i).select(".dc_tb_lq");
						for(int e=0;e<games_tb.size();e++) {
							Elements games_tr = games_tb.get(e).select("tr");
							//System.out.println();
							for (int f=0;f<games_tr.size();f++) {
								String isend = games_tr.get(f).select("tr").attr("isend");
								if(isend.equals("0")) {
									String guestteam = games_tr.get(f).select("tr").attr("guestteam");
									String zid = games_tr.get(f).select("tr").attr("zid");
									String hometeam = games_tr.get(f).select("tr").attr("hometeam");
									String round = games_tr.get(f).select("tr").attr("pname");
									String matchid = games_tr.get(f).select("tr").attr("fid");
									String guestteam_id = games_tr.get(f).select(".dc_tdul_vs a").get(0).attr("href").replaceAll(lq_prefix+matchid+"/team/","").replace("/", "");
									String hometeam_id = games_tr.get(f).select(".dc_tdul_vs a").get(1).attr("href").replaceAll(lq_prefix+matchid+"/team/","").replace("/", "");
									
									String matchName = games_tr.get(f).select("tr").attr("lg");
									String color = games_tr.get(f).select("tr > .dc_tb_bgred").attr("style");
									String[] matchColors = color.split(";");
									String matchColor = matchColors[0].substring(matchColors[0].indexOf(":")+1);
									String roundName = games_tr.get(f).select("tr > .dc_tb_bgred .game_time").text().trim();
									String endtime = games_tr.get(f).select("tr > .h_bb_1px .endtime").attr("title").trim();
									String matchtime = games_tr.get(f).select("tr > .h_bb_1px .matchtime").attr("title").trim();
									endtime = endtime.substring(endtime.indexOf("：")+1);
									matchtime = matchtime.substring(matchtime.indexOf("：")+1);
									String bf = games_tr.get(f).select("tr > .h_bb_1px").get(3).select("a").text();
									String fxid = games_tr.get(f).select("tr > .h_bb_1px").get(3).select("a").attr("href").replaceAll(lq_odds+"shuju.php\\?id=","").replaceAll("&r=1", "");
									
									String avgOWinOdds = games_tr.get(f).select("tr > .h_bb_1px").get(4).select(".pjpl > li").get(0).text().trim();
									String avgOLostOdds = games_tr.get(f).select("tr > .h_bb_1px").get(4).select(".pjpl > li").get(1).text().trim();
							
									
									String sf_away_odds = "";
									String sf_home_odds = "";
									String sf_outcome = "";
									String sf_outcome_odds = "";
									
									String rsf_away_odds = "";
									String rsf_home_odds = "";
									String rsf_away_qs = "";
									String rsf_home_qs = "";
									String rsf_outcome = "";
									String rsf_outcome_odds = "";
									
									String dxf_away_odds = "";
									String dxf_home_odds = "";
									String dxf_num = "";
									String dxf_outcome = "";
									String dxf_outcome_odds = "";
									
									String sfc_away15_odds="";
									String sfc_away610_odds="";
									String sfc_away1115_odds="";
									String sfc_away1620_odds="";
									String sfc_away2125_odds="";
									String sfc_away26_odds="";
									
									String sfc_home15_odds="";
									String sfc_home610_odds="";
									String sfc_home1115_odds="";
									String sfc_home1620_odds="";
									String sfc_home2125_odds="";
									String sfc_home26_odds="";
									
									String sfc_outcome = "";
									String sfc_outcome_odds = "";
									
									//收集赔率信息 根据状态不同 插入不同字段
									Elements sf = games_tr.get(f).select("tr > .h_br_eee").get(0).select(".dc_tdul");
									if(null != sf && sf.size() > 0) {
										Elements element = sf.select(".tb_tdul_pl");
										if(null != element && element.size() > 0) {
											sf_away_odds = sf.select(".tb_tdul_pl").get(0).text().trim();
											sf_home_odds = sf.select(".tb_tdul_pl").get(1).text().trim();
										} else {
											//System.out.println("未出结果");
										}
									} else {
										sf = games_tr.get(f).select("tr > .dc_tb_bg").get(0).select(".game_outcome");
										if(null != sf && sf.size() > 0) {
											sf_outcome = sf.select(".red").get(0).text().trim();
											sf_outcome_odds = sf.text().trim().replace(sf_outcome, "");
										}
										//System.out.println("结束");
									}
									
									Elements rfsf = games_tr.get(f).select("tr > .h_br_eee").get(1).select(".dc_tdul");
									if(null != rfsf && rfsf.size() > 0) {
										Elements element = rfsf.select(".tb_tdul_pl");
										if(null != element && element.size() > 0) {
											rsf_away_odds = rfsf.select(".tb_tdul_pl").get(0).text().trim();
											rsf_home_odds = rfsf.select(".tb_tdul_pl").get(1).text().trim();
											rsf_away_qs = rfsf.select(".tb_tz_s").get(0).text().trim().replace(rsf_away_odds, "");
											rsf_home_qs = rfsf.select(".tb_tz_s").get(1).text().trim().replace(rsf_home_odds, "");
										} else {
											//System.out.println("未出结果");
										}
									} else {
										rfsf = games_tr.get(f).select("tr > .dc_tb_bg").get(1).select(".game_outcome");
										if(null != rfsf && rfsf.size() > 0) {
											rsf_outcome = rfsf.select(".red").get(0).text().trim();
											rsf_outcome_odds = rfsf.text().trim().replace(rsf_outcome, "");
										}
										//System.out.println("结束");
									}
									
									Elements dxf = games_tr.get(f).select("tr > .h_br_eee").get(2).select(".dc_tdul");
									if(null != dxf && dxf.size() > 0) {
										Elements element = dxf.select(".tb_tdul_pl");
										if(null != element && element.size() > 0) {
											dxf_away_odds = dxf.select(".tb_tdul_pl").get(0).text().trim();
											dxf_home_odds = dxf.select(".tb_tdul_pl").get(1).text().trim();
											dxf_num = dxf.select(".tb_tz_s").get(0).text().trim().replace(dxf_away_odds+"大", "");
										} else {
											//System.out.println("未出结果");
										}
									} else {
										dxf = games_tr.get(f).select("tr > .dc_tb_bg").get(2).select(".game_outcome");
										if(null != dxf && dxf.size() > 0) {
											dxf_outcome = dxf.select(".red").get(0).text().trim();
											dxf_outcome_odds = dxf.text().trim().replace(dxf_outcome, "");
										}
										//System.out.println("结束");
									}
									
									
									Elements sfc = games_tr.get(f).select("tr > .dc_tb_bg").get(3).select(".tb_sfc_s");
									if(null != sfc && sfc.size() > 0) {
											sfc_away15_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(0).text().trim();
											sfc_away610_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(1).text().trim();
											sfc_away1115_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(2).text().trim();
											sfc_away1620_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(3).text().trim();
											sfc_away2125_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(4).text().trim();
											sfc_away26_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(5).text().trim();
											
											sfc_home15_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(0).text().trim();
											sfc_home610_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(1).text().trim();
											sfc_home1115_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(2).text().trim();
											sfc_home1620_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(3).text().trim();
											sfc_home2125_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(4).text().trim();
											sfc_home26_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(5).text().trim();
									} else {
										sfc = games_tr.get(f).select("tr > .dc_tb_bg").get(3).select(".game_outcome");
										if(null != sfc && sfc.size() > 0) {
											if(null !=sfc.select(".red")&&sfc.select(".red").size() > 0) {
												sfc_outcome = sfc.select(".red").get(0).text().trim();
												sfc_outcome_odds = sfc.text().trim().replace(sfc_outcome, "");
											}
										}
									}
									
									MLqGame mLqGame =  new MLqGame();
									mLqGame.setId(IDUtils.createUUId());
									mLqGame.setAwayid(IDUtils.createUUId());
									mLqGame.setAwayname(guestteam);
									if(ObjectHelper.isNotEmpty(guestteam_id))mLqGame.setAwayid500(Integer.parseInt(guestteam_id));
									mLqGame.setHomeid(IDUtils.createUUId());
									mLqGame.setHomename(hometeam);
									if(ObjectHelper.isNotEmpty(hometeam_id))mLqGame.setHomeid500(Integer.parseInt(hometeam_id));
									try {
										if(ObjectHelper.isNotEmpty(round))mLqGame.setRound(Integer.parseInt(round));
									} catch (Exception e2) {
										e2.getStackTrace();
									}
									
									mLqGame.setRoundname(roundName);
									mLqGame.setMatchid(matchid);
									mLqGame.setMatchname(matchName);
									mLqGame.setMatchcolor(matchColor);
									mLqGame.setZid(zid);
									
									mLqGame.setEndtime(sdf.parse(endtime));
									mLqGame.setStarttime(sdf.parse(matchtime));
									mLqGame.setBf(bf);
									mLqGame.setFxid(fxid);
									if(ObjectHelper.isNotEmpty(avgOWinOdds)&&!avgOWinOdds.equals("--")) mLqGame.setAvgolostOdds(new BigDecimal(avgOWinOdds));
									if(ObjectHelper.isNotEmpty(avgOLostOdds)&&!avgOLostOdds.equals("--")) mLqGame.setAvgowinOdds(new BigDecimal(avgOLostOdds));
									
									if(ObjectHelper.isNotEmpty(sf_away_odds))mLqGame.setSfAwayOdds(new BigDecimal(sf_away_odds));
									if(ObjectHelper.isNotEmpty(sf_home_odds))mLqGame.setSfHomeOdds(new BigDecimal(sf_home_odds));
									mLqGame.setSfOutcome(sf_outcome);
									if(ObjectHelper.isNotEmpty(sf_outcome_odds))mLqGame.setSfOutcomeOdds(new BigDecimal(sf_outcome_odds));
									if(ObjectHelper.isNotEmpty(rsf_away_odds))mLqGame.setRsfAwayOdds(new BigDecimal(rsf_away_odds));
									if(ObjectHelper.isNotEmpty(rsf_home_odds))mLqGame.setRsfHomeOdds(new BigDecimal(rsf_home_odds));
									mLqGame.setRsfAwayQs(rsf_away_qs);
									mLqGame.setRsfHomeQs(rsf_home_qs);
									mLqGame.setRsfOutcome(rsf_outcome);
									if(ObjectHelper.isNotEmpty(rsf_outcome_odds))mLqGame.setRsfOutcomeOdds(new BigDecimal(rsf_outcome_odds));
									if(ObjectHelper.isNotEmpty(dxf_away_odds))mLqGame.setDxfAwayOdds(new BigDecimal(dxf_away_odds));
									if(ObjectHelper.isNotEmpty(dxf_home_odds))mLqGame.setDxfHomeOdds(new BigDecimal(dxf_home_odds));
									mLqGame.setDxfNum(dxf_num);
									mLqGame.setDxfOutcome(dxf_outcome);
									if(ObjectHelper.isNotEmpty(dxf_outcome_odds))mLqGame.setDxfOutcomeOdds(new BigDecimal(dxf_outcome_odds));
									if(ObjectHelper.isNotEmpty(sfc_home15_odds))mLqGame.setSfcAway15Odds(new BigDecimal(sfc_away15_odds));
									if(ObjectHelper.isNotEmpty(sfc_away610_odds))mLqGame.setSfcAway610Odds(new BigDecimal(sfc_away610_odds));
									if(ObjectHelper.isNotEmpty(sfc_away1115_odds))mLqGame.setSfcAway1115Odds(new BigDecimal(sfc_away1115_odds));
									if(ObjectHelper.isNotEmpty(sfc_away1620_odds))mLqGame.setSfcAway1620Odds(new BigDecimal(sfc_away1620_odds));
									if(ObjectHelper.isNotEmpty(sfc_away2125_odds))mLqGame.setSfcAway2125Odds(new BigDecimal(sfc_away2125_odds));
									if(ObjectHelper.isNotEmpty(sfc_away26_odds))mLqGame.setSfcAway26Odds(new BigDecimal(sfc_away26_odds));
									
									if(ObjectHelper.isNotEmpty(sfc_home15_odds))mLqGame.setSfcHome15Odds(new BigDecimal(sfc_home15_odds));
									if(ObjectHelper.isNotEmpty(sfc_home610_odds))mLqGame.setSfcHome610Odds(new BigDecimal(sfc_home610_odds));
									if(ObjectHelper.isNotEmpty(sfc_home1115_odds))mLqGame.setSfcHome1115Odds(new BigDecimal(sfc_home1115_odds));
									if(ObjectHelper.isNotEmpty(sfc_home1620_odds))mLqGame.setSfcHome1620Odds(new BigDecimal(sfc_home1620_odds));
									if(ObjectHelper.isNotEmpty(sfc_home2125_odds))mLqGame.setSfcHome2125Odds(new BigDecimal(sfc_home2125_odds));
									if(ObjectHelper.isNotEmpty(sfc_home26_odds))mLqGame.setSfcHome26Odds(new BigDecimal(sfc_home26_odds));
									
									mLqGame.setSfcOutcome(sfc_outcome);
									if(ObjectHelper.isNotEmpty(sfc_outcome_odds))mLqGame.setSfcOutcomeOdds(new BigDecimal(sfc_outcome_odds));
									
									
									Iterator<Entry<String, String>> it = singleMap.entrySet().iterator();  
									while (it.hasNext()) {  
							            Entry<String, String> entry = it.next();  
							            Object key = entry.getKey();
							            if(key.equals(roundName)) {
							            	mLqGame.setSingle1(entry.getValue());
							            	break;
							            }
							        }  
									Iterator<Entry<String, String>> it2 = singleMap2.entrySet().iterator();  
									while (it2.hasNext()) {  
										Entry<String, String> entry = it2.next();  
										Object key = entry.getKey();
										if(key.equals(roundName)) {
											mLqGame.setSingle2(entry.getValue());
											break;
										}
									}  
									Iterator<Entry<String, String>> it3 = singleMap3.entrySet().iterator();  
									while (it3.hasNext()) {  
										Entry<String, String> entry = it3.next();  
										Object key = entry.getKey();
										if(key.equals(roundName)) {
											mLqGame.setSingle3(entry.getValue());
											break;
										}
									}  
									
									Iterator<Entry<String, String>> it4 = rfMap.entrySet().iterator(); 
									while (it4.hasNext()) {  
										Entry<String, String> entry = it4.next();  
										Object key = entry.getKey();
										if(key.equals(roundName)) {
											mLqGame.setRsfHomeQs(entry.getValue());
											float rf = Float.valueOf(entry.getValue());
											String nString = String.valueOf(rf*-1);
											if(nString.indexOf("-") == -1) {
												nString = "+" + nString;
											}
											mLqGame.setRsfAwayQs(nString);
											break;
										}
									}
									
									Iterator<Entry<String, String>> it5 = dxfMap2.entrySet().iterator(); 
									while (it5.hasNext()) {  
										Entry<String, String> entry = it5.next();  
										Object key = entry.getKey();
										if(key.equals(roundName)) {
											mLqGame.setDxfNum(entry.getValue());
											break;
										}
									}
									
									MLqGameExample example = new MLqGameExample();
									Criteria createCriteria = example.createCriteria();	
									createCriteria.andFxidEqualTo(mLqGame.getFxid());
									List<MLqGame> list = mLqGameMapper.selectByExample(example);
									if(list.size()>0) {
										mLqGame.setId(list.get(0).getId());
										mLqGameMapper.updateByPrimaryKeySelective(mLqGame);
									} else {
										mLqGameMapper.insert(mLqGame);
										System.out.println("新增对阵："+guestteam+":"+hometeam);
									}
								}
							}
						}
					}
				}
				
			} else {

				Document doc = DocumetUtil.getDocumentByUrl(lq_game+times.get(t));
				Elements elements = doc.select("#vsTable");
				Elements es = elements.get(0).children();
				//System.out.println(es);
				if(null != es && es.size() > 0) {
					for(int i=0;i<es.size();i++) {
						Elements games_tb =es.get(i).select(".dc_tb_lq");
						for(int e=0;e<games_tb.size();e++) {
							Elements games_tr = games_tb.get(e).select("tr");
							//System.out.println();
							for (int f=0;f<games_tr.size();f++) {
								String isend = games_tr.get(f).select("tr").attr("isend");
								if(isend.equals("0")) {
									String guestteam = games_tr.get(f).select("tr").attr("guestteam");
									String hometeam = games_tr.get(f).select("tr").attr("hometeam");
									String round = games_tr.get(f).select("tr").attr("pname");
									String matchid = games_tr.get(f).select("tr").attr("fid");
									String guestteam_id = games_tr.get(f).select(".dc_tdul_vs a").get(0).attr("href").replaceAll(lq_prefix+matchid+"/team/","").replace("/", "");
									String hometeam_id = games_tr.get(f).select(".dc_tdul_vs a").get(1).attr("href").replaceAll(lq_prefix+matchid+"/team/","").replace("/", "");
									
									String matchName = games_tr.get(f).select("tr").attr("lg");
									String color = games_tr.get(f).select("tr > .dc_tb_bgred").attr("style");
									String[] matchColors = color.split(";");
									String matchColor = matchColors[0].substring(matchColors[0].indexOf(":")+1);
									String roundName = games_tr.get(f).select("tr > .dc_tb_bgred .game_time").text().trim();
									String endtime = games_tr.get(f).select("tr > .h_bb_1px .endtime").attr("title").trim();
									String matchtime = games_tr.get(f).select("tr > .h_bb_1px .matchtime").attr("title").trim();
									endtime = endtime.substring(endtime.indexOf("：")+1);
									matchtime = matchtime.substring(matchtime.indexOf("：")+1);
									String bf = games_tr.get(f).select("tr > .h_bb_1px").get(3).select("a").text();
									String fxid = games_tr.get(f).select("tr > .h_bb_1px").get(3).select("a").attr("href").replaceAll(lq_odds+"shuju.php\\?id=","").replaceAll("&r=1", "");
									
									String avgOWinOdds = games_tr.get(f).select("tr > .h_bb_1px").get(4).select(".pjpl > li").get(0).text().trim();
									String avgOLostOdds = games_tr.get(f).select("tr > .h_bb_1px").get(4).select(".pjpl > li").get(1).text().trim();
							
									
									String sf_away_odds = "";
									String sf_home_odds = "";
									String sf_outcome = "";
									String sf_outcome_odds = "";
									
									String rsf_away_odds = "";
									String rsf_home_odds = "";
									String rsf_away_qs = "";
									String rsf_home_qs = "";
									String rsf_outcome = "";
									String rsf_outcome_odds = "";
									
									String dxf_away_odds = "";
									String dxf_home_odds = "";
									String dxf_num = "";
									String dxf_outcome = "";
									String dxf_outcome_odds = "";
									
									String sfc_away15_odds="";
									String sfc_away610_odds="";
									String sfc_away1115_odds="";
									String sfc_away1620_odds="";
									String sfc_away2125_odds="";
									String sfc_away26_odds="";
									
									String sfc_home15_odds="";
									String sfc_home610_odds="";
									String sfc_home1115_odds="";
									String sfc_home1620_odds="";
									String sfc_home2125_odds="";
									String sfc_home26_odds="";
									
									String sfc_outcome = "";
									String sfc_outcome_odds = "";
									
									//收集赔率信息 根据状态不同 插入不同字段
									Elements sf = games_tr.get(f).select("tr > .h_br_eee").get(0).select(".dc_tdul");
									if(null != sf && sf.size() > 0) {
										Elements element = sf.select(".tb_tdul_pl");
										if(null != element && element.size() > 0) {
											sf_away_odds = sf.select(".tb_tdul_pl").get(0).text().trim();
											sf_home_odds = sf.select(".tb_tdul_pl").get(1).text().trim();
										} else {
											//System.out.println("未出结果");
										}
									} else {
										sf = games_tr.get(f).select("tr > .dc_tb_bg").get(0).select(".game_outcome");
										if(null != sf && sf.size() > 0) {
											sf_outcome = sf.select(".red").get(0).text().trim();
											sf_outcome_odds = sf.text().trim().replace(sf_outcome, "");
										}
										//System.out.println("结束");
									}
									
									Elements rfsf = games_tr.get(f).select("tr > .h_br_eee").get(1).select(".dc_tdul");
									if(null != rfsf && rfsf.size() > 0) {
										Elements element = rfsf.select(".tb_tdul_pl");
										if(null != element && element.size() > 0) {
											rsf_away_odds = rfsf.select(".tb_tdul_pl").get(0).text().trim();
											rsf_home_odds = rfsf.select(".tb_tdul_pl").get(1).text().trim();
											rsf_away_qs = rfsf.select(".tb_tz_s").get(0).text().trim().replace(rsf_away_odds, "");
											rsf_home_qs = rfsf.select(".tb_tz_s").get(1).text().trim().replace(rsf_home_odds, "");
										} else {
											//System.out.println("未出结果");
										}
									} else {
										rfsf = games_tr.get(f).select("tr > .dc_tb_bg").get(1).select(".game_outcome");
										if(null != rfsf && rfsf.size() > 0) {
											rsf_outcome = rfsf.select(".red").get(0).text().trim();
											rsf_outcome_odds = rfsf.text().trim().replace(rsf_outcome, "");
										}
										//System.out.println("结束");
									}
									
									Elements dxf = games_tr.get(f).select("tr > .h_br_eee").get(2).select(".dc_tdul");
									if(null != dxf && dxf.size() > 0) {
										Elements element = dxf.select(".tb_tdul_pl");
										if(null != element && element.size() > 0) {
											dxf_away_odds = dxf.select(".tb_tdul_pl").get(0).text().trim();
											dxf_home_odds = dxf.select(".tb_tdul_pl").get(1).text().trim();
											dxf_num = dxf.select(".tb_tz_s").get(0).text().trim().replace(dxf_away_odds+"大", "");
										} else {
											//System.out.println("未出结果");
										}
									} else {
										dxf = games_tr.get(f).select("tr > .dc_tb_bg").get(2).select(".game_outcome");
										if(null != dxf && dxf.size() > 0) {
											dxf_outcome = dxf.select(".red").get(0).text().trim();
											dxf_outcome_odds = dxf.text().trim().replace(dxf_outcome, "");
										}
										//System.out.println("结束");
									}
									
									
									Elements sfc = games_tr.get(f).select("tr > .dc_tb_bg").get(3).select(".tb_sfc_s");
									if(null != sfc && sfc.size() > 0) {
											sfc_away15_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(0).text().trim();
											sfc_away610_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(1).text().trim();
											sfc_away1115_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(2).text().trim();
											sfc_away1620_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(3).text().trim();
											sfc_away2125_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(4).text().trim();
											sfc_away26_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(5).text().trim();
											
											sfc_home15_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(0).text().trim();
											sfc_home610_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(1).text().trim();
											sfc_home1115_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(2).text().trim();
											sfc_home1620_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(3).text().trim();
											sfc_home2125_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(4).text().trim();
											sfc_home26_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(5).text().trim();
									} else {
										sfc = games_tr.get(f).select("tr > .dc_tb_bg").get(3).select(".game_outcome");
										if(null != sfc && sfc.size() > 0) {
											if(null !=sfc.select(".red")&&sfc.select(".red").size() > 0) {
												sfc_outcome = sfc.select(".red").get(0).text().trim();
												sfc_outcome_odds = sfc.text().trim().replace(sfc_outcome, "");
											}
										}
									}
									
									MLqGame mLqGame =  new MLqGame();
									mLqGame.setId(IDUtils.createUUId());
									mLqGame.setAwayid(IDUtils.createUUId());
									mLqGame.setAwayname(guestteam);
									if(ObjectHelper.isNotEmpty(guestteam_id))mLqGame.setAwayid500(Integer.parseInt(guestteam_id));
									mLqGame.setHomeid(IDUtils.createUUId());
									mLqGame.setHomename(hometeam);
									if(ObjectHelper.isNotEmpty(hometeam_id))mLqGame.setHomeid500(Integer.parseInt(hometeam_id));
									try {
										if(ObjectHelper.isNotEmpty(round))mLqGame.setRound(Integer.parseInt(round));
									} catch (Exception e2) {
										e2.getStackTrace();
									}
									
									mLqGame.setRoundname(roundName);
									mLqGame.setMatchid(matchid);
									mLqGame.setMatchname(matchName);
									mLqGame.setMatchcolor(matchColor);
									
									
									mLqGame.setEndtime(sdf.parse(endtime));
									mLqGame.setStarttime(sdf.parse(matchtime));
									mLqGame.setBf(bf);
									mLqGame.setFxid(fxid);
									if(ObjectHelper.isNotEmpty(avgOWinOdds)&&!avgOWinOdds.equals("--")) mLqGame.setAvgolostOdds(new BigDecimal(avgOWinOdds));
									if(ObjectHelper.isNotEmpty(avgOLostOdds)&&!avgOLostOdds.equals("--")) mLqGame.setAvgowinOdds(new BigDecimal(avgOLostOdds));
									
									if(ObjectHelper.isNotEmpty(sf_away_odds))mLqGame.setSfAwayOdds(new BigDecimal(sf_away_odds));
									if(ObjectHelper.isNotEmpty(sf_home_odds))mLqGame.setSfHomeOdds(new BigDecimal(sf_home_odds));
									mLqGame.setSfOutcome(sf_outcome);
									if(ObjectHelper.isNotEmpty(sf_outcome_odds))mLqGame.setSfOutcomeOdds(new BigDecimal(sf_outcome_odds));
									if(ObjectHelper.isNotEmpty(rsf_away_odds))mLqGame.setRsfAwayOdds(new BigDecimal(rsf_away_odds));
									if(ObjectHelper.isNotEmpty(rsf_home_odds))mLqGame.setRsfHomeOdds(new BigDecimal(rsf_home_odds));
									mLqGame.setRsfAwayQs(rsf_away_qs);
									mLqGame.setRsfHomeQs(rsf_home_qs);
									mLqGame.setRsfOutcome(rsf_outcome);
									if(ObjectHelper.isNotEmpty(rsf_outcome_odds))mLqGame.setRsfOutcomeOdds(new BigDecimal(rsf_outcome_odds));
									if(ObjectHelper.isNotEmpty(dxf_away_odds))mLqGame.setDxfAwayOdds(new BigDecimal(dxf_away_odds));
									if(ObjectHelper.isNotEmpty(dxf_home_odds))mLqGame.setDxfHomeOdds(new BigDecimal(dxf_home_odds));
									mLqGame.setDxfNum(dxf_num);
									mLqGame.setDxfOutcome(dxf_outcome);
									if(ObjectHelper.isNotEmpty(dxf_outcome_odds))mLqGame.setDxfOutcomeOdds(new BigDecimal(dxf_outcome_odds));
									if(ObjectHelper.isNotEmpty(sfc_home15_odds))mLqGame.setSfcAway15Odds(new BigDecimal(sfc_away15_odds));
									if(ObjectHelper.isNotEmpty(sfc_away610_odds))mLqGame.setSfcAway610Odds(new BigDecimal(sfc_away610_odds));
									if(ObjectHelper.isNotEmpty(sfc_away1115_odds))mLqGame.setSfcAway1115Odds(new BigDecimal(sfc_away1115_odds));
									if(ObjectHelper.isNotEmpty(sfc_away1620_odds))mLqGame.setSfcAway1620Odds(new BigDecimal(sfc_away1620_odds));
									if(ObjectHelper.isNotEmpty(sfc_away2125_odds))mLqGame.setSfcAway2125Odds(new BigDecimal(sfc_away2125_odds));
									if(ObjectHelper.isNotEmpty(sfc_away26_odds))mLqGame.setSfcAway26Odds(new BigDecimal(sfc_away26_odds));
									
									if(ObjectHelper.isNotEmpty(sfc_home15_odds))mLqGame.setSfcHome15Odds(new BigDecimal(sfc_home15_odds));
									if(ObjectHelper.isNotEmpty(sfc_home610_odds))mLqGame.setSfcHome610Odds(new BigDecimal(sfc_home610_odds));
									if(ObjectHelper.isNotEmpty(sfc_home1115_odds))mLqGame.setSfcHome1115Odds(new BigDecimal(sfc_home1115_odds));
									if(ObjectHelper.isNotEmpty(sfc_home1620_odds))mLqGame.setSfcHome1620Odds(new BigDecimal(sfc_home1620_odds));
									if(ObjectHelper.isNotEmpty(sfc_home2125_odds))mLqGame.setSfcHome2125Odds(new BigDecimal(sfc_home2125_odds));
									if(ObjectHelper.isNotEmpty(sfc_home26_odds))mLqGame.setSfcHome26Odds(new BigDecimal(sfc_home26_odds));
									
									mLqGame.setSfcOutcome(sfc_outcome);
									if(ObjectHelper.isNotEmpty(sfc_outcome_odds))mLqGame.setSfcOutcomeOdds(new BigDecimal(sfc_outcome_odds));
									
									
									MLqGameExample example = new MLqGameExample();
									Criteria createCriteria = example.createCriteria();	
									createCriteria.andFxidEqualTo(mLqGame.getFxid());
									List<MLqGame> list = mLqGameMapper.selectByExample(example);
									if(list.size()>0) {
										mLqGame.setId(list.get(0).getId());
										mLqGameMapper.updateByPrimaryKeySelective(mLqGame);
									} else {
										mLqGameMapper.insert(mLqGame);
										System.out.println("新增对阵："+guestteam+":"+hometeam);
									}
								}
							}
						}
					}
				}
				
			}
		}
		}catch (Exception e) {
			logger.error(e.getStackTrace());
		}
	}
	
	
	public static void getHistoryGame(String btime,int days) throws ParseException {
		MLqGameMapper mLqGameMapper = Main.applicationContext.getBean(MLqGameMapper.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(btime));
		for(int j=0;j>-days;j--) {
			calendar.add(Calendar.DATE, -1);
			String time = (new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		
			Document doc = DocumetUtil.getDocumentByUrl(lq_game+time);
			Elements elements = doc.select("#vsTable");
			Elements es = elements.get(0).children();
			//System.out.println(es);
			if(null != es && es.size() > 0) {
				for(int i=0;i<es.size();i++) {
					Elements games_tb =es.get(i).select(".dc_tb_lq");
					for(int e=0;e<games_tb.size();e++) {
						Elements games_tr = games_tb.get(e).select("tr");
						//System.out.println();
						for (int f=0;f<games_tr.size();f++) {
							String isend = games_tr.get(f).select("tr").attr("isend");
							if(isend.equals("0")) {
								String guestteam = games_tr.get(f).select("tr").attr("guestteam");
								String hometeam = games_tr.get(f).select("tr").attr("hometeam");
								String round = games_tr.get(f).select("tr").attr("pname");
								String matchid = games_tr.get(f).select("tr").attr("fid");
								String guestteam_id = games_tr.get(f).select(".dc_tdul_vs a").get(0).attr("href").replaceAll(lq_prefix+matchid+"/team/","").replace("/", "");
								String hometeam_id = games_tr.get(f).select(".dc_tdul_vs a").get(1).attr("href").replaceAll(lq_prefix+matchid+"/team/","").replace("/", "");
								
								String matchName = games_tr.get(f).select("tr").attr("lg");
								String color = games_tr.get(f).select("tr > .dc_tb_bgred").attr("style");
								String[] matchColors = color.split(";");
								String matchColor = matchColors[0].substring(matchColors[0].indexOf(":")+1);
								String roundName = games_tr.get(f).select("tr > .dc_tb_bgred .game_time").text().trim();
								String endtime = games_tr.get(f).select("tr > .h_bb_1px .endtime").attr("title").trim();
								String matchtime = games_tr.get(f).select("tr > .h_bb_1px .matchtime").attr("title").trim();
								endtime = endtime.substring(endtime.indexOf("：")+1);
								matchtime = matchtime.substring(matchtime.indexOf("：")+1);
								String bf = games_tr.get(f).select("tr > .h_bb_1px").get(3).select("a").text();
								String fxid = games_tr.get(f).select("tr > .h_bb_1px").get(3).select("a").attr("href").replaceAll(lq_odds+"shuju.php\\?id=","").replaceAll("&r=1", "");
								
								String avgOWinOdds = games_tr.get(f).select("tr > .h_bb_1px").get(4).select(".pjpl > li").get(0).text().trim();
								String avgOLostOdds = games_tr.get(f).select("tr > .h_bb_1px").get(4).select(".pjpl > li").get(1).text().trim();
						
								
								String sf_away_odds = "";
								String sf_home_odds = "";
								String sf_outcome = "";
								String sf_outcome_odds = "";
								
								String rsf_away_odds = "";
								String rsf_home_odds = "";
								String rsf_away_qs = "";
								String rsf_home_qs = "";
								String rsf_outcome = "";
								String rsf_outcome_odds = "";
								
								String dxf_away_odds = "";
								String dxf_home_odds = "";
								String dxf_num = "";
								String dxf_outcome = "";
								String dxf_outcome_odds = "";
								
								String sfc_away15_odds="";
								String sfc_away610_odds="";
								String sfc_away1115_odds="";
								String sfc_away1620_odds="";
								String sfc_away2125_odds="";
								String sfc_away26_odds="";
								
								String sfc_home15_odds="";
								String sfc_home610_odds="";
								String sfc_home1115_odds="";
								String sfc_home1620_odds="";
								String sfc_home2125_odds="";
								String sfc_home26_odds="";
								
								String sfc_outcome = "";
								String sfc_outcome_odds = "";
								
								//收集赔率信息 根据状态不同 插入不同字段
								Elements sf = games_tr.get(f).select("tr > .h_br_eee").get(0).select(".dc_tdul");
								if(null != sf && sf.size() > 0) {
									Elements element = sf.select(".tb_tdul_pl");
									if(null != element && element.size() > 0) {
										sf_away_odds = sf.select(".tb_tdul_pl").get(0).text().trim();
										sf_home_odds = sf.select(".tb_tdul_pl").get(1).text().trim();
									} else {
										//System.out.println("未出结果");
									}
								} else {
									sf = games_tr.get(f).select("tr > .dc_tb_bg").get(0).select(".game_outcome");
									if(null != sf && sf.size() > 0) {
										sf_outcome = sf.select(".red").get(0).text().trim();
										sf_outcome_odds = sf.text().trim().replace(sf_outcome, "");
									}
									//System.out.println("结束");
								}
								
								Elements rfsf = games_tr.get(f).select("tr > .h_br_eee").get(1).select(".dc_tdul");
								if(null != rfsf && rfsf.size() > 0) {
									Elements element = rfsf.select(".tb_tdul_pl");
									if(null != element && element.size() > 0) {
										rsf_away_odds = rfsf.select(".tb_tdul_pl").get(0).text().trim();
										rsf_home_odds = rfsf.select(".tb_tdul_pl").get(1).text().trim();
										rsf_away_qs = rfsf.select(".tb_tz_s").get(0).text().trim().replace(rsf_away_odds, "");
										rsf_home_qs = rfsf.select(".tb_tz_s").get(1).text().trim().replace(rsf_home_odds, "");
									} else {
										//System.out.println("未出结果");
									}
								} else {
									rfsf = games_tr.get(f).select("tr > .dc_tb_bg").get(1).select(".game_outcome");
									if(null != rfsf && rfsf.size() > 0) {
										rsf_outcome = rfsf.select(".red").get(0).text().trim();
										rsf_outcome_odds = rfsf.text().trim().replace(rsf_outcome, "");
									}
									//System.out.println("结束");
								}
								
								Elements dxf = games_tr.get(f).select("tr > .h_br_eee").get(2).select(".dc_tdul");
								if(null != dxf && dxf.size() > 0) {
									Elements element = dxf.select(".tb_tdul_pl");
									if(null != element && element.size() > 0) {
										dxf_away_odds = dxf.select(".tb_tdul_pl").get(0).text().trim();
										dxf_home_odds = dxf.select(".tb_tdul_pl").get(1).text().trim();
										dxf_num = dxf.select(".tb_tz_s").get(0).text().trim().replace(dxf_away_odds+"大", "");
									} else {
										//System.out.println("未出结果");
									}
								} else {
									dxf = games_tr.get(f).select("tr > .dc_tb_bg").get(2).select(".game_outcome");
									if(null != dxf && dxf.size() > 0) {
										dxf_outcome = dxf.select(".red").get(0).text().trim();
										dxf_outcome_odds = dxf.text().trim().replace(dxf_outcome, "");
									}
									//System.out.println("结束");
								}
								
								
								Elements sfc = games_tr.get(f).select("tr > .dc_tb_bg").get(3).select(".tb_sfc_s");
								if(null != sfc && sfc.size() > 0) {
										sfc_away15_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(0).text().trim();
										sfc_away610_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(1).text().trim();
										sfc_away1115_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(2).text().trim();
										sfc_away1620_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(3).text().trim();
										sfc_away2125_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(4).text().trim();
										sfc_away26_odds = sfc.select("tr").get(0).select(".tb_tdul_pl").get(5).text().trim();
										
										sfc_home15_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(0).text().trim();
										sfc_home610_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(1).text().trim();
										sfc_home1115_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(2).text().trim();
										sfc_home1620_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(3).text().trim();
										sfc_home2125_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(4).text().trim();
										sfc_home26_odds = sfc.select("tr").get(1).select(".tb_tdul_pl").get(5).text().trim();
								} else {
									sfc = games_tr.get(f).select("tr > .dc_tb_bg").get(3).select(".game_outcome");
									if(null != sfc && sfc.size() > 0) {
										if(null !=sfc.select(".red")&&sfc.select(".red").size() > 0) {
											sfc_outcome = sfc.select(".red").get(0).text().trim();
											sfc_outcome_odds = sfc.text().trim().replace(sfc_outcome, "");
										}
									}
								}
								
								MLqGame mLqGame =  new MLqGame();
								mLqGame.setId(IDUtils.createUUId());
								mLqGame.setAwayid(IDUtils.createUUId());
								mLqGame.setAwayname(guestteam);
								if(ObjectHelper.isNotEmpty(guestteam_id))mLqGame.setAwayid500(Integer.parseInt(guestteam_id));
								mLqGame.setHomeid(IDUtils.createUUId());
								mLqGame.setHomename(hometeam);
								if(ObjectHelper.isNotEmpty(hometeam_id))mLqGame.setHomeid500(Integer.parseInt(hometeam_id));
								try {
									if(ObjectHelper.isNotEmpty(round))mLqGame.setRound(Integer.parseInt(round));
								} catch (Exception e2) {
									e2.getStackTrace();
								}
								mLqGame.setRoundname(roundName);
								mLqGame.setMatchid(matchid);
								mLqGame.setMatchname(matchName);
								mLqGame.setMatchcolor(matchColor);
								
								
								mLqGame.setEndtime(sdf.parse(endtime));
								mLqGame.setStarttime(sdf.parse(matchtime));
								mLqGame.setBf(bf);
								mLqGame.setFxid(fxid);
								if(ObjectHelper.isNotEmpty(avgOWinOdds)&&!avgOWinOdds.equals("--")) mLqGame.setAvgolostOdds(new BigDecimal(avgOWinOdds));
								if(ObjectHelper.isNotEmpty(avgOLostOdds)&&!avgOLostOdds.equals("--")) mLqGame.setAvgowinOdds(new BigDecimal(avgOLostOdds));
								
								if(ObjectHelper.isNotEmpty(sf_away_odds))mLqGame.setSfAwayOdds(new BigDecimal(sf_away_odds));
								if(ObjectHelper.isNotEmpty(sf_home_odds))mLqGame.setSfHomeOdds(new BigDecimal(sf_home_odds));
								mLqGame.setSfOutcome(sf_outcome);
								if(ObjectHelper.isNotEmpty(sf_outcome_odds))mLqGame.setSfOutcomeOdds(new BigDecimal(sf_outcome_odds));
								if(ObjectHelper.isNotEmpty(rsf_away_odds))mLqGame.setRsfAwayOdds(new BigDecimal(rsf_away_odds));
								if(ObjectHelper.isNotEmpty(rsf_home_odds))mLqGame.setRsfHomeOdds(new BigDecimal(rsf_home_odds));
								mLqGame.setRsfAwayQs(rsf_away_qs);
								mLqGame.setRsfHomeQs(rsf_home_qs);
								mLqGame.setRsfOutcome(rsf_outcome);
								if(ObjectHelper.isNotEmpty(rsf_outcome_odds))mLqGame.setRsfOutcomeOdds(new BigDecimal(rsf_outcome_odds));
								if(ObjectHelper.isNotEmpty(dxf_away_odds))mLqGame.setDxfAwayOdds(new BigDecimal(dxf_away_odds));
								if(ObjectHelper.isNotEmpty(dxf_home_odds))mLqGame.setDxfHomeOdds(new BigDecimal(dxf_home_odds));
								mLqGame.setDxfNum(dxf_num);
								mLqGame.setDxfOutcome(dxf_outcome);
								if(ObjectHelper.isNotEmpty(dxf_outcome_odds))mLqGame.setDxfOutcomeOdds(new BigDecimal(dxf_outcome_odds));
								if(ObjectHelper.isNotEmpty(sfc_home15_odds))mLqGame.setSfcAway15Odds(new BigDecimal(sfc_away15_odds));
								if(ObjectHelper.isNotEmpty(sfc_away610_odds))mLqGame.setSfcAway610Odds(new BigDecimal(sfc_away610_odds));
								if(ObjectHelper.isNotEmpty(sfc_away1115_odds))mLqGame.setSfcAway1115Odds(new BigDecimal(sfc_away1115_odds));
								if(ObjectHelper.isNotEmpty(sfc_away1620_odds))mLqGame.setSfcAway1620Odds(new BigDecimal(sfc_away1620_odds));
								if(ObjectHelper.isNotEmpty(sfc_away2125_odds))mLqGame.setSfcAway2125Odds(new BigDecimal(sfc_away2125_odds));
								if(ObjectHelper.isNotEmpty(sfc_away26_odds))mLqGame.setSfcAway26Odds(new BigDecimal(sfc_away26_odds));
								
								if(ObjectHelper.isNotEmpty(sfc_home15_odds))mLqGame.setSfcHome15Odds(new BigDecimal(sfc_home15_odds));
								if(ObjectHelper.isNotEmpty(sfc_home610_odds))mLqGame.setSfcHome610Odds(new BigDecimal(sfc_home610_odds));
								if(ObjectHelper.isNotEmpty(sfc_home1115_odds))mLqGame.setSfcHome1115Odds(new BigDecimal(sfc_home1115_odds));
								if(ObjectHelper.isNotEmpty(sfc_home1620_odds))mLqGame.setSfcHome1620Odds(new BigDecimal(sfc_home1620_odds));
								if(ObjectHelper.isNotEmpty(sfc_home2125_odds))mLqGame.setSfcHome2125Odds(new BigDecimal(sfc_home2125_odds));
								if(ObjectHelper.isNotEmpty(sfc_home26_odds))mLqGame.setSfcHome26Odds(new BigDecimal(sfc_home26_odds));
								
								mLqGame.setSfcOutcome(sfc_outcome);
								if(ObjectHelper.isNotEmpty(sfc_outcome_odds))mLqGame.setSfcOutcomeOdds(new BigDecimal(sfc_outcome_odds));
								
								
								MLqGameExample example = new MLqGameExample();
								Criteria createCriteria = example.createCriteria();	
								createCriteria.andFxidEqualTo(mLqGame.getFxid());
								List<MLqGame> list = mLqGameMapper.selectByExample(example);
								if(list.size()>0) {
									mLqGame.setId(list.get(0).getId());
									mLqGameMapper.updateByPrimaryKeySelective(mLqGame);
								} else {
									mLqGameMapper.insert(mLqGame);
									System.out.println("新增对阵："+guestteam+":"+hometeam);
								}
							}
						}
					}
				}
			}
			
		}
		
		
	}
	
	public static void main(String[] args) {
//		getSingle(dx_url);
		try {
			Date date1 = new Date();
			LqGameDate.getIngGame();
			Date date2 = new Date();
			System.out.println("耗时（篮球比赛-实时对阵）:"+(date2.getTime()-date1.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
