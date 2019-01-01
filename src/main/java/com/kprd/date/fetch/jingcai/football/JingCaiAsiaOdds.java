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
import com.kprd.newliansai.mapper.FoAsiaMapper;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoAsia;
import com.kprd.newliansai.pojo.FoAsiaExample;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JingCaiAsiaOdds {
	//亚盘数据接口
	private static String asiaUrl = "http://i.sporttery.cn/api/fb_match_info/get_asia/?mid=xx&_=yy";
	
	private static String matchUrl = "http://i.sporttery.cn/api/fb_match_info/get_match_info?mid=xx&&_=yy";
	
	//对阵
	static FoMixMapper fMixMapper = Main.applicationContext.getBean(FoMixMapper.class);
	//亚盘mapper
	static FoAsiaMapper aMapper = Main.applicationContext.getBean(FoAsiaMapper.class);
	
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
	
	public static void getAsiaData(FoMix m) {
		String matchId = getMatchId(m);
		String html = null;
		try {
			if("98765".equals(m.getJingcaiid())) {
				System.out.println();
			}
			asiaUrl = asiaUrl.replace("xx", m.getJingcaiid());
			asiaUrl = asiaUrl.replace("yy", String.valueOf(System.currentTimeMillis()));
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(asiaUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			asiaUrl ="http://i.sporttery.cn/api/fb_match_info/get_asia/?mid=xx&_=yy";
			try {
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		//封装博彩公司
                		parseJson(html, m,matchId);
                		//封装平均值，最高值，最低值
                		get3Data(m,matchId);
                		
                		JedisUtilForFetch.remove(6,m.getIdunique() + "asia");
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
					getAsiaData(m);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 解析json
	 * @param html
	 * @param idUnique
	 */
	/**
	 * @param html
	 * @param idUnique
	 */
	private static void parseJson(String html,FoMix m,String matchId) {
		System.out.println("开始解析" + m.getIdunique());
		JSONObject jsonOnject = JSONObject.fromObject(html);
		if(jsonOnject.isEmpty()) {
			return;
		}
		String codeJson = JSONObject.fromObject(jsonOnject.get("status")).get("code").toString();
		if("20002".equals(codeJson)) {
			return;
		}
		Object ob = jsonOnject.get("result");
		Object object = JSONObject.fromObject(ob).get("data");
		JSONArray jarray = null;
		if(ObjectHelper.isNotEmpty(object)) {
			jarray = JSONArray.fromObject(object);
		}
//		JSONArray jarray = jsonOnject.getJSONArray("result");
		for(int i=0;i<jarray.size();i++) {
			JSONObject obj = JSONObject.fromObject(jarray.get(i));
			if("皇冠".equals(obj.get("cn")) || "Bet 365".equals(obj.get("cn")) || "立博".equals(obj.get("cn")) || "金宝博".equals(obj.get("cn")) 
					|| "澳门".equals(obj.get("cn")) || "威廉希尔".equals(obj.get("cn")) || "竞彩官方".equals(obj.get("cn")) || "必发".equals(obj.get("cn"))) {
				FoAsia asia = new FoAsia();
				asia.setCshomewin(obj.get("o1_s").toString());
				asia.setCsdraw(obj.get("o3_s").toString());
				String csDraw = getBet(asia.getCsdraw()).toString();
				asia.setCspannum(csDraw);
				asia.setCsawaywin(obj.get("o2_s").toString());
				asia.setJshomewin(obj.get("o1").toString());
				asia.setJsdraw(obj.get("o3").toString());
				String jsDraw = getBet(asia.getJsdraw()).toString();
				asia.setJspannum(jsDraw);
				asia.setJsawaywin(obj.get("o2").toString());
				if("up".equals(obj.get("o1_change").toString())){
					asia.setJshomewinstatus("1");
				} else if("down".equals(obj.get("o1_change").toString())) {
					asia.setJshomewinstatus("-1");
				} else if("equal".equals(obj.get("o1_change").toString())) {
					asia.setJshomewinstatus("0");
				}
				if("up".equals(obj.get("o2_change").toString())){
					asia.setJsawaywinstatus("1");
				} else if("down".equals(obj.get("o2_change").toString())) {
					asia.setJsawaywinstatus("-1");
				} else if("equal".equals(obj.get("o2_change").toString())) {
					asia.setJsawaywinstatus("0");
				}
				asia.setKailihome(obj.get("o1_index").toString());
				asia.setKailiaway(obj.get("o2_index").toString());
				asia.setCompany(obj.get("cn").toString());
				asia.setIdunique(m.getIdunique());
				asia.setReserve2(m.getId());
				asia.setReserve3(matchId);
				
				FoAsiaExample aEx = new FoAsiaExample();
				aEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanyEqualTo(obj.get("cn").toString());
				List<FoAsia> asiaList = aMapper.selectByExample(aEx);
				if(ObjectHelper.isEmpty(asiaList)) {
					asia.setId(IDUtils.createUUId());
					aMapper.insert(asia);
				} else {
					asia.setId(asiaList.get(0).getId());
					aMapper.updateByPrimaryKeySelective(asia);
				}
			}
		}
	}
	
	private static void get3Data(FoMix m,String matchId) {
		//最高值
		FoAsiaExample maxEx = new FoAsiaExample();
		maxEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanyEqualTo("最高值");
		List<FoAsia> maxList = aMapper.selectByExample(maxEx);
		Map<String, Object> max = aMapper.getMax(m.getIdunique());
		if(ObjectHelper.isNotEmpty(max)) {
			FoAsia maxAsia = new FoAsia();
			maxAsia.setCompany("最高值");
			maxAsia.setIdunique(m.getIdunique());
			maxAsia.setReserve1(m.getIdunique());
			maxAsia.setReserve2(m.getId());
			maxAsia.setJshomewin(max.get("jsHomeWin").toString());
			maxAsia.setJsawaywin(max.get("jsAwayWin").toString());
			maxAsia.setCshomewin(max.get("csHomeWin").toString());
			maxAsia.setCsawaywin(max.get("csAwayWin").toString());
			maxAsia.setJspannum(max.get("jsPanNum").toString());
			maxAsia.setCspannum(max.get("csPanNum").toString());
			maxAsia.setJsdraw(getBetNumToStr(maxAsia.getJspannum()));
			maxAsia.setCsdraw(getBetNumToStr(maxAsia.getCspannum()));
			maxAsia.setKailihome(max.get("kailiHome").toString());
			maxAsia.setKailiaway(max.get("kailiAway").toString());
			maxAsia.setReserve3(matchId);
			if(ObjectHelper.isEmpty(maxList)) {
				maxAsia.setId(IDUtils.createUUId());
				aMapper.insert(maxAsia);
			} else {
				maxAsia.setId(maxList.get(0).getId());
				aMapper.updateByPrimaryKeySelective(maxAsia);
			}
		}
		
		//最低值
		FoAsiaExample minEx = new FoAsiaExample();
		minEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanyEqualTo("最低值");
		List<FoAsia> minList = aMapper.selectByExample(minEx);
		Map<String, Object> min = aMapper.getMin(m.getIdunique());
		if(ObjectHelper.isNotEmpty(min)) {
			FoAsia minAsia = new FoAsia();
			minAsia.setCompany("最低值");
			minAsia.setIdunique(m.getIdunique());
			minAsia.setReserve1(m.getIdunique());
			minAsia.setReserve2(m.getId());
			minAsia.setJshomewin(min.get("jsHomeWin").toString());
			minAsia.setJsawaywin(min.get("jsAwayWin").toString());
			minAsia.setCshomewin(min.get("csHomeWin").toString());
			minAsia.setCsawaywin(min.get("csAwayWin").toString());
			minAsia.setJspannum(min.get("jsPanNum").toString());
			minAsia.setCspannum(min.get("csPanNum").toString());
			minAsia.setJsdraw(getBetNumToStr(minAsia.getJspannum()));
			minAsia.setCsdraw(getBetNumToStr(minAsia.getCspannum()));
			minAsia.setKailihome(min.get("kailiHome").toString());
			minAsia.setKailiaway(min.get("kailiAway").toString());
			minAsia.setReserve3(matchId);
			if(ObjectHelper.isEmpty(minList)) {
				minAsia.setId(IDUtils.createUUId());
				aMapper.insert(minAsia);
			} else {
				minAsia.setId(minList.get(0).getId());
				aMapper.updateByPrimaryKeySelective(minAsia);
			}
		}
		
		//平均值
		FoAsiaExample avgEx = new FoAsiaExample();
		avgEx.createCriteria().andIduniqueEqualTo(m.getIdunique()).andCompanyEqualTo("平均值");
		List<FoAsia> avgList = aMapper.selectByExample(avgEx);
		FoAsia avg = aMapper.getAvg(m.getIdunique());
		if(ObjectHelper.isNotEmpty(avg)) {
			FoAsia avgAsia = new FoAsia();
			avgAsia.setCompany("平均值");
			avgAsia.setIdunique(m.getIdunique());
			avgAsia.setReserve1(m.getIdunique());
			avgAsia.setReserve2(m.getId());
			avgAsia.setJshomewin(avg.getJshomewin());
			avgAsia.setJsawaywin(avg.getJsawaywin());
			avgAsia.setCshomewin(avg.getCshomewin());
			avgAsia.setCsawaywin(avg.getCsawaywin());
			avgAsia.setJspannum(avg.getJspannum());
			avgAsia.setCspannum(avg.getCspannum());
			avgAsia.setJsdraw(getBetNumToStr(avg.getJspannum()));
			avgAsia.setCsdraw(getBetNumToStr(avg.getCspannum()));
			avgAsia.setKailihome(avg.getKailihome());
			avgAsia.setKailiaway(avg.getKailiaway());
			avgAsia.setReserve3(matchId);
			if(ObjectHelper.isEmpty(avgList)) {
				avgAsia.setId(IDUtils.createUUId());
				aMapper.insert(avgAsia);
			} else {
				avgAsia.setId(avgList.get(0).getId());
				aMapper.updateByPrimaryKeySelective(avgAsia);
			}
		}
		
	}
	
	/**
	 * 判断盘口（中文转数字）
	 * @param bet
	 * @return
	 */
	private static BigDecimal getBet(String bet) {
	    if(bet.equals("平手")){
	      return new BigDecimal(0);
	    }
	    if(bet.equals("平手/半球")){
	      return new BigDecimal(-0.25);
	    }
	    if(bet.equals("半球")){
	      return new BigDecimal(-0.5);
	    }
	    if(bet.equals("半球/一球")){
	      return new BigDecimal(-0.75);
	    }
	    if(bet.equals("一球")){
	      return new BigDecimal(-1);
	    }
	    if(bet.equals("一球/球半")){
	      return new BigDecimal(-1.25);
	    }
	    if(bet.equals("球半")){
	      return new BigDecimal(-1.5);
	    }
	    if(bet.equals("球半/两球")){
	      return new BigDecimal(-1.75);
	    }
	    if(bet.equals("两球")){
	      return new BigDecimal(-2);
	    }
	    if(bet.equals("两球/两球半")){
	      return new BigDecimal(-2.25);
	    }
	    if(bet.equals("两球半")){
	      return new BigDecimal(-2.5);
	    }
	    if(bet.equals("两球半/三球")){
	      return new BigDecimal(-2.75);
	    }
	    if(bet.equals("三球")){
	      return new BigDecimal(-3);
	    }
	    if(bet.equals("三球/三球半")){
	      return new BigDecimal(-3.25);
	    }
	    if(bet.equals("三球半")){
	      return new BigDecimal(-3.5);
	    }
	    if(bet.equals("三球半/四球")){
	      return new BigDecimal(-3.75);
	    }
	    if(bet.equals("受平手/半球")){
	      return new BigDecimal(0.25);
	    }
	    if(bet.equals("受半球")){
	      return new BigDecimal(0.5);
	    }
	    if(bet.equals("受半球/一球")){
	      return new BigDecimal(0.75);
	    }
	    if(bet.equals("受一球")){
	      return new BigDecimal(1);
	    }
	    if(bet.equals("受一球/球半")){
	      return new BigDecimal(1.25);
	    }
	    if(bet.equals("受球半")){
	      return new BigDecimal(1.5);
	    }
	    if(bet.equals("受球半/两球")){
	      return new BigDecimal(1.75);
	    }
	    if(bet.equals("受两球")){
	      return new BigDecimal(2);
	    }
	    if(bet.equals("受两球/两球半")){
	      return new BigDecimal(2.25);
	    }
	    if(bet.equals("受两球半")){
	      return new BigDecimal(2.5);
	    }
	    if(bet.equals("受两球半/三球")){
	      return new BigDecimal(2.75);
	    }
	    if(bet.equals("受三球")){
	      return new BigDecimal(3);
	    }
	    if(bet.equals("受三球/三球半")){
	      return new BigDecimal(3.25);
	    }
	    if(bet.equals("受三球半")){
	      return new BigDecimal(3.5);
	    }
	    if(bet.equals("受三球半/四球")){
	      return new BigDecimal(3.75);
	    }
	    return new BigDecimal(0);
	  }
	
	/**
	 * 判断盘口（数字转中文）
	 * @param bet
	 * @return
	 */
	private static String getBetNumToStr(String bet) {
	    if(bet.equals("0")){
	      return "平手";
	    }
	    if(bet.equals("-0.25")){
	      return "平手/半球";
	    }
	    if(bet.equals("-0.5")){
	      return "半球";
	    }
	    if(bet.equals("-0.75")){
	      return "半球/一球";
	    }
	    if(bet.equals("-1")){
	      return "一球";
	    }
	    if(bet.equals("-1.25")){
	      return "一球/球半";
	    }
	    if(bet.equals("-1.5")){
	      return "球半";
	    }
	    if(bet.equals("-1.75")){
	      return "球半/两球";
	    }
	    if(bet.equals("-2")){
	      return "两球";
	    }
	    if(bet.equals("-2.25")){
	      return "两球/两球半";
	    }
	    if(bet.equals("-2.5")){
	      return "两球半";
	    }
	    if(bet.equals("-2.75")){
	      return "两球半/三球";
	    }
	    if(bet.equals("-3")){
	      return "三球";
	    }
	    if(bet.equals("-3.25")){
	      return "三球/三球半";
	    }
	    if(bet.equals("-3.5")){
	      return "三球半";
	    }
	    if(bet.equals("-3.75")){
	      return "三球半/四球";
	    }
	    if(bet.equals("0.25")){
	      return "受平手/半球";
	    }
	    if(bet.equals("0.5")){
	      return "受半球";
	    }
	    if(bet.equals("0.75")){
	      return "受半球/一球";
	    }
	    if(bet.equals("1")){
	      return "受一球";
	    }
	    if(bet.equals("1.25")){
	      return "受一球/球半";
	    }
	    if(bet.equals("1.5")){
	      return "受球半";
	    }
	    if(bet.equals("1.75")){
	      return "受球半/两球";
	    }
	    if(bet.equals("2")){
	      return "受两球";
	    }
	    if(bet.equals("2.25")){
	      return "受两球/两球半";
	    }
	    if(bet.equals("2.5")){
	      return "受两球半";
	    }
	    if(bet.equals("2.75")){
	      return "受两球半/三球";
	    }
	    if(bet.equals("3")){
	      return "受三球";
	    }
	    if(bet.equals("3.25")){
	      return "受三球/三球半";
	    }
	    if(bet.equals("3.5")){
	      return "受三球半";
	    }
	    if(bet.equals("3.75")){
	      return "受三球半/四球";
	    }
	    return bet;
	  }
	
//	public static void main(String[] args) {
////		get3Data("201707193018");// 测试用，实际在方法内已调用
//		try {
//			FoMixExample fmEx = new FoMixExample();
////			String day = UtilBag.dateUtil(0);
//			String day = "2017-08-29";
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String today = day + " 11:59:59";
//			Date kyo = sdf.parse(today);
//			String tomorrow = UtilBag.dateUtilWithDate(1, kyo)+ " 11:59:59";
//			Date kino = sdf.parse(tomorrow);
//			fmEx.createCriteria().andStarttimeGreaterThan(kyo).andStarttimeLessThan(kino);
//			fmEx.setOrderByClause(" idUnique asc");
//			List<FoMix> mvsList = fMixMapper.selectByExample(fmEx);
//			System.out.println("今天" + mvsList.size() + "场比赛");
//			int counter = 0;
//			if(ObjectHelper.isNotEmpty(mvsList)) {
//				for(FoMix m : mvsList) {
////					if("201708292009".equals(m.getIdunique())) {
////						System.out.println();
////					}
//					counter++;
//					if(counter%20==0) {
//						Thread.sleep(1000*30);
//					}
//					getAsiaData(m);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	public static void main(String[] args) {
//		get3Data("201707193018");// 测试用，实际在方法内已调用
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
			int counter = 0;
			if(ObjectHelper.isNotEmpty(mvsList)) {
				for(FoMix m : mvsList) {
					counter++;
					if(counter%20==0) {
						Thread.sleep(1000*30);
					}
					getAsiaData(m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
