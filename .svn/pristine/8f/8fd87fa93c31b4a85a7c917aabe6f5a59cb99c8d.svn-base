package com.kprd.date.fetch.jingcai.football;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.JedisUtilForFetch;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.FoEuropeMapper;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoEurope;
import com.kprd.newliansai.pojo.FoEuropeExample;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JingCaiEuropOdds {
	private static String baseUrl = "http://i.sporttery.cn/api/fb_match_info/get_europe/?mid=xx&_=yy";
	
	private static String matchUrl = "http://i.sporttery.cn/api/fb_match_info/get_match_info?mid=xx&&_=yy";
	
	//对阵
	static FoMixMapper fMixMapper = Main.applicationContext.getBean(FoMixMapper.class);
	
	//欧赔
	static FoEuropeMapper europMapper = Main.applicationContext.getBean(FoEuropeMapper.class);
	
	public static String getMatchId(FoMix m) {
		String html = null;
		String matchId = null;
		try {
			matchUrl = matchUrl.replace("xx", m.getJingcaiid());
			matchUrl = matchUrl.replace("yy", String.valueOf(System.currentTimeMillis()));
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(matchUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			matchUrl = "http://i.sporttery.cn/api/fb_match_info/get_match_info?mid=xx&&_=yy";
			try {
				// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		JSONObject jsonObject = JSONObject.fromObject(html);
                		if(ObjectHelper.isNotEmpty(jsonObject)) {
                			jsonObject = JSONObject.fromObject(jsonObject.get("result"));
                			String match_id = jsonObject.get("match_id").toString();
                			matchId = match_id;
                		}
                	}
                }
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matchId;
	}
	
	public static void getEuroOddsHtml(FoMix m) {
		String matchId = getMatchId(m);
		String html = null;
		try {
			baseUrl = baseUrl.replace("xx", m.getJingcaiid());
			baseUrl = baseUrl.replace("yy", String.valueOf(System.currentTimeMillis()));
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(baseUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			baseUrl = "http://i.sporttery.cn/api/fb_match_info/get_europe/?mid=xx&_=yy";
			try {
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		parseHtml(html,m,matchId);
                		//封装平均值，最高值，最低值
                		get3Data(m,matchId);
                		
                		JedisUtilForFetch.remove(6,m.getIdunique() + "euro");
                	}
                }	
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					getEuroOddsHtml(m);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 解析json
	 * @param json
	 * @param idUnique
	 * @throws InterruptedException 
	 */
	public static void parseHtml(String json,FoMix m,String matchId) throws InterruptedException {
		System.out.println("开始解析" + m.getIdunique());
		if(ObjectHelper.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.fromObject(json);
			Object result = jsonObject.get("result");
			jsonObject = JSONObject.fromObject(result);
			if(jsonObject.isEmpty()) {
				return;
			}
			JSONArray data = jsonObject.getJSONArray("data");
			for(int i=0;i<data.size();i++) {
				JSONObject obj = JSONObject.fromObject(data.get(i));
				//没有bet365和竞彩官方自己
				if("皇冠".equals(obj.get("cn")) || "Bet365".equals(obj.get("cn")) || "立博".equals(obj.get("cn")) || "金宝博".equals(obj.get("cn")) 
						|| "澳门".equals(obj.get("cn")) || "威廉希尔".equals(obj.get("cn")) || "竞彩官方".equals(obj.get("cn")) || "必发".equals(obj.get("cn"))) {
					FoEurope europ = new FoEurope();
					europ.setCompanyname(obj.get("cn").toString());
					europ.setNewophomewin(obj.get("win").toString());
					europ.setNewophomedraw(obj.get("draw").toString());
					europ.setNewopawaywin(obj.get("lose").toString());
					//胜状态
					String statusS = obj.get("win_change").toString();
					if("down".equals(statusS)) {
						europ.setNewophomewinstatus("-1");
					} else if("equal".equals(statusS)) {
						europ.setNewophomewinstatus("0");
					} else if("up".equals(statusS)) {
						europ.setNewophomewinstatus("1");
					}
					//平状态
					String statusP = obj.get("draw_change").toString();
					if("down".equals(statusP)) {
						europ.setNewophomedrawstatus("-1");
					} else if("equal".equals(statusP)) {
						europ.setNewophomedrawstatus("0");
					} else if("up".equals(statusP)) {
						europ.setNewophomedrawstatus("1");
					}
					
					//负状态
					String statusF = obj.get("lose_change").toString();
					if("down".equals(statusF)) {
						europ.setNewopawaywinstatus("-1");
					} else if("equal".equals(statusF)) {
						europ.setNewopawaywinstatus("0");
					} else if("up".equals(statusF)) {
						europ.setNewopawaywinstatus("1");
					}
					//返还率 （猜的）
					europ.setReturnrates(obj.get("per").toString());
					
					//初始欧赔主胜
					europ.setCsophomewin(obj.get("win_s").toString());
					europ.setCsopdraw(obj.get("draw_s").toString());
					europ.setCsopawaywin(obj.get("lose_s").toString());
					//胜率
					europ.setProbabilityhome(obj.getString("win_ratio"));
					europ.setProbabilitydraw(obj.getString("draw_ratio"));
					europ.setProbabilityaway(obj.getString("lose_ratio"));
					//凯利
					europ.setKailihome(obj.getString("win_index"));
					europ.setKailidraw(obj.getString("draw_index"));
					europ.setKailiaway(obj.getString("lose_index"));
					europ.setIdunique(m.getIdunique());
					europ.setReserve3(matchId);
					FoEuropeExample euEx = new FoEuropeExample();
					euEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanynameEqualTo(obj.get("cn").toString());
					List<FoEurope> europList = europMapper.selectByExample(euEx);
					if(ObjectHelper.isEmpty(europList)) {
						europ.setId(IDUtils.createUUId());
						europMapper.insert(europ);
					} else {
						europ.setId(europList.get(0).getId());
						//判断凯利指数增减
						String kailiWin = europList.get(0).getKailihome();
						String kailiDraw = europList.get(0).getKailidraw();
						String kailiLose = europList.get(0).getKailiaway();
						BigDecimal bKailiWin = new BigDecimal(kailiWin);
						BigDecimal pageWin = new BigDecimal(obj.getString("win_index"));
						if(pageWin.compareTo(bKailiWin) > 0) {
							europ.setKailihomestatus("1");
						} else if(pageWin.compareTo(bKailiWin) == 0) {
							europ.setKailihomestatus("0");
						} else if(pageWin.compareTo(bKailiWin) < 0) {
							europ.setKailihomestatus("-1");
						}
						
						BigDecimal bKailiDraw = new BigDecimal(kailiDraw);
						BigDecimal pageDraw = new BigDecimal(obj.getString("draw_index"));
						if(pageDraw.compareTo(bKailiDraw) > 0) {
							europ.setKailidrawstatus("1");
						} else if(pageDraw.compareTo(bKailiDraw) == 0) {
							europ.setKailidrawstatus("0");
						} else if(pageDraw.compareTo(bKailiDraw) < 0) {
							europ.setKailidrawstatus("-1");
						}
						
						BigDecimal bKailiLose = new BigDecimal(kailiLose);
						BigDecimal pageLose = new BigDecimal(obj.getString("lose_index"));
						if(pageLose.compareTo(bKailiLose) > 0) {
							europ.setKailiawaystatus("1");
						} else if(pageLose.compareTo(bKailiLose) == 0) {
							europ.setKailiawaystatus("0");
						}  else if(pageLose.compareTo(bKailiLose) < 0) {
							europ.setKailiawaystatus("-1");
						} 
						europMapper.updateByPrimaryKey(europ);
					}
				}
			}
		}
	}
	
	private static void get3Data(FoMix m,String matchId) {
//		//最高值
		FoEuropeExample maxEx = new FoEuropeExample();
		maxEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanynameEqualTo("最高值");
		List<FoEurope> maxList = europMapper.selectByExample(maxEx);
		Map<String, Object> max = europMapper.getMax(m.getIdunique());
		if(ObjectHelper.isNotEmpty(max)) {
			FoEurope maxEu = new FoEurope();
			maxEu.setCompanyname("最高值");
			maxEu.setIdunique(m.getIdunique());
			maxEu.setReserve1(m.getIdunique());
			maxEu.setNewophomewin(max.get("newOpHomeWin").toString());
			maxEu.setNewophomedraw(max.get("newOpHomeDraw").toString());
			maxEu.setNewopawaywin(max.get("newOpAwayWin").toString());
			maxEu.setCsophomewin(max.get("csOpHomeWin").toString());
			maxEu.setCsopdraw(max.get("csOpDraw").toString());
			maxEu.setCsopawaywin(max.get("csOpAwayWin").toString());
			maxEu.setKailihome(max.get("kailiHome").toString());
			maxEu.setKailidraw(max.get("kailiDraw").toString());
			maxEu.setKailiaway(max.get("kailiAway").toString());
			maxEu.setProbabilityhome(max.get("probabilityHome").toString());
			maxEu.setProbabilitydraw(max.get("probabilityDraw").toString());
			maxEu.setProbabilityaway(max.get("probabilityAway").toString());
			maxEu.setReturnrates(max.get("returnRates").toString());
			maxEu.setReserve3(matchId);
			if(ObjectHelper.isEmpty(maxList)) {
				maxEu.setId(IDUtils.createUUId());
				europMapper.insert(maxEu);
			} else {
				maxEu.setId(maxList.get(0).getId());
				europMapper.updateByPrimaryKeySelective(maxEu);
			}
		}
		
		//最低值
		FoEuropeExample minEx = new FoEuropeExample();
		minEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanynameEqualTo("最低值");
		List<FoEurope> minList = europMapper.selectByExample(minEx);
		Map<String, Object> min = europMapper.getMin(m.getIdunique());
		if(ObjectHelper.isNotEmpty(min)) {
			FoEurope minEu = new FoEurope();
			minEu.setCompanyname("最低值");
			minEu.setIdunique(m.getIdunique());
			minEu.setReserve1(m.getIdunique());
			minEu.setNewophomewin(min.get("newOpHomeWin").toString());
			minEu.setNewophomedraw(min.get("newOpHomeDraw").toString());
			minEu.setNewopawaywin(min.get("newOpAwayWin").toString());
			minEu.setCsophomewin(min.get("csOpHomeWin").toString());
			minEu.setCsopdraw(min.get("csOpDraw").toString());
			minEu.setCsopawaywin(min.get("csOpAwayWin").toString());
			minEu.setKailihome(min.get("kailiHome").toString());
			minEu.setKailidraw(min.get("kailiDraw").toString());
			minEu.setKailiaway(min.get("kailiAway").toString());
			minEu.setProbabilityhome(min.get("probabilityHome").toString());
			minEu.setProbabilitydraw(min.get("probabilityDraw").toString());
			minEu.setProbabilityaway(min.get("probabilityAway").toString());
			minEu.setReturnrates(min.get("returnRates").toString());
			minEu.setReserve3(matchId);
			if(ObjectHelper.isEmpty(minList)) {
				minEu.setId(IDUtils.createUUId());
				europMapper.insert(minEu);
			} else {
				minEu.setId(minList.get(0).getId());
				europMapper.updateByPrimaryKeySelective(minEu);
			}
		}
		
		//平均值
		FoEuropeExample avgEx = new FoEuropeExample();
		avgEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanynameEqualTo("平均值");
		List<FoEurope> avgList = europMapper.selectByExample(avgEx);
		FoEurope avg = europMapper.getAvg(m.getIdunique());
		if(ObjectHelper.isNotEmpty(avg)) {
			FoEurope avgEu = new FoEurope();
			avgEu.setCompanyname("平均值");
			avgEu.setIdunique(m.getIdunique());
			avgEu.setReserve1(m.getIdunique());
			avgEu.setNewophomewin(avg.getNewophomewin());
			avgEu.setNewophomedraw(avg.getNewophomedraw());
			avgEu.setNewopawaywin(avg.getNewopawaywin());
			avgEu.setCsophomewin(avg.getCsophomewin());
			avgEu.setCsopdraw(avg.getCsopdraw());
			avgEu.setCsopawaywin(avg.getCsopawaywin());
			avgEu.setKailihome(avg.getKailihome());
			avgEu.setKailidraw(avg.getKailidraw());
			avgEu.setKailiaway(avg.getKailiaway());
			avgEu.setProbabilityhome(avg.getProbabilityhome());
			avgEu.setProbabilitydraw(avg.getProbabilitydraw());
			avgEu.setProbabilityaway(avg.getProbabilityaway());
			avgEu.setReturnrates(avg.getReturnrates());
			avgEu.setReserve3(matchId);
			if(ObjectHelper.isEmpty(avgList)) {
				avgEu.setId(IDUtils.createUUId());
				europMapper.insert(avgEu);
			} else {
				avgEu.setId(avgList.get(0).getId());
				europMapper.updateByPrimaryKeySelective(avgEu);
			}
		}
	}
	
//	public static void main(String[] args) {
//		long startTime = System.currentTimeMillis();
//		try {
//			FoMixExample fmEx = new FoMixExample();
//			String day = "2017-08-29";
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String today = day + " 11:59:59";
//			Date kyo = sdf.parse(today);
//			String tomorrow = UtilBag.dateUtilWithDate(1, kyo)+ " 11:59:59";
//			Date kino = sdf.parse(tomorrow);
//			fmEx.createCriteria().andStarttimeGreaterThan(kyo).andStarttimeLessThan(kino);;
//			fmEx.setOrderByClause(" idUnique asc");
//			List<FoMix> mvsList = fMixMapper.selectByExample(fmEx);
//			System.out.println("今天" + mvsList.size() + "场比赛");
//			if(ObjectHelper.isNotEmpty(mvsList)) {
//				int conunter = 0;
//				for(FoMix m : mvsList) {
//					conunter++;
//					if(conunter%20==0) {
//						Thread.sleep(1000*30);
//					}
//					getEuroOddsHtml(m);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		long endTime = System.currentTimeMillis();
//		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
//		System.out.println("程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
//	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
			FoMixExample fmEx = new FoMixExample();
			String day = UtilBag.dateUtil(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String today = day + " 11:59:59";
			Date kyo = sdf.parse(today);
			fmEx.createCriteria().andStarttimeGreaterThan(kyo);
			fmEx.setOrderByClause(" idUnique asc");
			List<FoMix> mvsList = fMixMapper.selectByExample(fmEx);
			System.out.println("今天" + mvsList.size() + "场比赛");
			if(ObjectHelper.isNotEmpty(mvsList)) {
				int conunter = 0;
				for(FoMix m : mvsList) {
					conunter++;
					if(conunter%20==0) {
						Thread.sleep(1000*30);
					}
					getEuroOddsHtml(m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		System.out.println("程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
	}
}
