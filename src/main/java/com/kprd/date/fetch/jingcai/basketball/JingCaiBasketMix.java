package com.kprd.date.fetch.jingcai.basketball;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.BaMixMapper;
import com.kprd.newliansai.pojo.BaMix;
import com.kprd.newliansai.pojo.BaMixExample;

public class JingCaiBasketMix {
	//页面url
	private static String basketUrl = "http://info.sporttery.cn/interface/interface_mixed.php?action=bk_list&0.2847424930601794&_=timestamp";
	//全数据url
	private static String fullDataUrl = "http://i.sporttery.cn/odds_calculator/get_odds?i_format=json&poolcode[]=wnm&poolcode[]=hdc&poolcode[]=mnl&poolcode[]=hilo";
	//篮球mapper
	static BaMixMapper baMappmer = Main.applicationContext.getBean(BaMixMapper.class);
	
	public static void getFullBasketData() {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(fullDataUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				String html = "";
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	html = UtilBag.unicode2String(html);
                	JSONObject jsonObject = JSONObject.fromObject(html);
                	Object dataKey = jsonObject.get("data");
                	if(ObjectHelper.isEmpty(dataKey)) {
                		System.out.println("目前没有比赛(全数据)");
                		return;
                	}
                	jsonObject = JSONObject.fromObject(jsonObject.get("data"));
                	BaMixExample baEx = new BaMixExample();
                	String day = UtilBag.dateUtil(0);
        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        			String today = day + " 11:59:59";
        			Date kyo = sdf.parse(today);
        			baEx.createCriteria().andStarttimeGreaterThan(kyo);
        			List<BaMix> baList = baMappmer.selectByExample(baEx);
        			if(ObjectHelper.isNotEmpty(baList)) {
        				for(BaMix mix : baList) {
        					JSONObject game = JSONObject.fromObject(jsonObject.get("_"+mix.getIdjingcai()));
        					System.out.println(game);
        					mix.setLeagueid(game.getString("l_id"));
        					mix.setHometeamid(game.getString("h_id"));
        					mix.setAwayteamid(game.getString("a_id"));
        					//胜负数据
        					if(ObjectHelper.isNotEmpty(game.get("mnl"))) {
        						JSONObject mnl = JSONObject.fromObject(game.get("mnl"));
            					String syobu = mnl.get("cbt").toString();
            					mix.setIsrf(syobu);
            					mix.setHomelose(new BigDecimal(mnl.get("a").toString()));
            					mix.setHomewin(new BigDecimal(mnl.get("h").toString()));
            					mix.setSinglesf(mnl.getString("single"));
        					}
        					//让分胜负
        					if(ObjectHelper.isNotEmpty(game.get("hdc"))) {
            					JSONObject hdc = JSONObject.fromObject(game.get("hdc"));
            					mix.setRfhomelose(new BigDecimal(hdc.get("a").toString()));
            					mix.setRfhomewin(new BigDecimal(hdc.get("h").toString()));//fixedodds
            					mix.setRf(new BigDecimal(hdc.get("fixedodds").toString()));
            					mix.setIsrf(hdc.get("cbt").toString());
            					mix.setSinglerfsf(hdc.get("single").toString());
        					}
        					//大小分
        					if(ObjectHelper.isNotEmpty(game.get("hilo"))) {
            					JSONObject hilo = JSONObject.fromObject(game.get("hilo"));
            					mix.setBigscore(new BigDecimal(hilo.get("h").toString()));
            					mix.setSmallscore(new BigDecimal(hilo.get("l").toString()));
            					mix.setScoresum(new BigDecimal(hilo.get("fixedodds").toString()));
            					mix.setIsbigsmall(hilo.get("cbt").toString());
            					mix.setSinglebigsmall(hilo.get("single").toString());
        					}
        					//胜分差
        					if(ObjectHelper.isNotEmpty(game.get("wnm"))) {
        						JSONObject wnm = JSONObject.fromObject(game.get("wnm"));
            					mix.setSfcHome15Odds(new BigDecimal(wnm.get("w1").toString()));
            					mix.setSfcHome610Odds(new BigDecimal(wnm.get("w2").toString()));
            					mix.setSfcHome1115Odds(new BigDecimal(wnm.get("w3").toString()));
            					mix.setSfcHome1620Odds(new BigDecimal(wnm.get("w4").toString()));
            					mix.setSfcHome2125Odds(new BigDecimal(wnm.get("w5").toString()));
            					mix.setSfcHome26Odds(new BigDecimal(wnm.get("w6").toString()));
            					mix.setSfcAway15Odds(new BigDecimal(wnm.get("l1").toString()));
            					mix.setSfcAway610Odds(new BigDecimal(wnm.get("l2").toString()));
            					mix.setSfcAway1115Odds(new BigDecimal(wnm.get("l3").toString()));
            					mix.setSfcAway1620Odds(new BigDecimal(wnm.get("l4").toString()));
            					mix.setSfcAway2125Odds(new BigDecimal(wnm.get("l5").toString()));
            					mix.setSfcAway26Odds(new BigDecimal(wnm.get("l6").toString()));
            					mix.setIssfc(wnm.get("cbt").toString());
            					mix.setSinglesfc(wnm.get("single").toString());
        					}
        					baMappmer.updateByPrimaryKeySelective(mix);
        				}
        			}
                }
			} finally {
				response.close();
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					getFullBasketData();
				} catch (InterruptedException e1) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 处理请求到的json
	 * @return
	 */
	public static String handleJson(String urlStr) {  
		String html = "";
        @SuppressWarnings("unused")
		int i = 0;  
        StringBuffer sb = new StringBuffer("");  
        URL url;  
        try {  
            url = new URL(urlStr);  
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "gbk"));  
            String s = "";  
            while ((s = br.readLine()) != null) {  
                i++;  
                sb.append(s + "\r\n");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        html = sb.toString();
        if(ObjectHelper.isNotEmpty(sb.toString())) {
    		html = html.substring(html.indexOf("data")+5, html.length());
    		html = html.split(";")[0];
    		return html;
    	}
        return sb.toString();  
    }
	
	@SuppressWarnings("rawtypes")
	public static void getBasketBall() {
		basketUrl = basketUrl.replace("timestamp", String.valueOf(System.currentTimeMillis()));
		try {
			basketUrl = "http://info.sporttery.cn/interface/interface_mixed.php?action=bk_list&0.2847424930601794&_=timestamp";
				//混合过关
			String html = handleJson(basketUrl);
        		if("[]".equals(html) || ObjectHelper.isEmpty(html)) {
        			System.out.println("目前没有比赛(基础数据)");
        			return;
        		}
        		List<List> fatherList = JsonUtils.jsonToList(html, List.class);
        		for(List list : fatherList) {
        			BaMix mix = new BaMix();
        			for(int i=0;i<list.size();i++) {
        				
						List sonList = List.class.cast(list.get(i));
						if(ObjectHelper.isNotEmpty(sonList)) {
    						switch (i) {
							case 0: 
								if(ObjectHelper.isNotEmpty(sonList)) {
									System.out.println(sonList);
									String code = (String) sonList.get(0);
									//拿到编号的前提下再执行下面的
									if(!StringUtils.isEmpty(code) && code.indexOf("周") > -1) {
										String week = code.substring(code.indexOf("周")+1,code.indexOf("周")+2);
										String num = UtilBag.getWeekDay(week);
										if(!StringUtils.isEmpty(num)) {
											String time = (String) sonList.get(4);
											SimpleDateFormat sdff = new SimpleDateFormat("yyyy");
											time = sdff.format(new Date()).substring(0,2) + time + ":00";
											SimpleDateFormat ttt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
											Date startTime = ttt.parse(time);
											Date endTime = getEndTime(startTime);
											String timeStr = (String) sonList.get(12);
											timeStr = timeStr.replace("-", "");
											String year =  timeStr;
											code = year + num + code.substring(2);
											if("201707215007".equals(code)) {
												System.out.println();
											}
											String awayShortName = (String) sonList.get(2);
											String homeShortName = (String) sonList.get(3);
											if(ObjectHelper.isNotEmpty(homeShortName) && ObjectHelper.isNotEmpty(awayShortName)) {
												mix.setAwayshortname(awayShortName);
												mix.setHomeshortname(homeShortName);
											}
											String league = sonList.get(1).toString();
											String color = sonList.get(6).toString();
											if(ObjectHelper.isNotEmpty(league) && ObjectHelper.isNotEmpty(color)) {
												mix.setLeaguename(league);
												mix.setLeaguecolor(color);
											}
											mix.setIdunique(code);
											mix.setStarttime(startTime);
											mix.setEndtime(endTime);
											String awayFull = sonList.get(8).toString();
											String homeFull = sonList.get(9).toString();
											if(ObjectHelper.isNotEmpty(homeFull) && ObjectHelper.isNotEmpty(awayFull)) {
												mix.setHomefullname(homeFull);
												mix.setAwayfullname(awayFull);
											}
											mix.setIdjingcai(sonList.get(5).toString());
										}
									}
								}
								break;
							case 1:
								if(ObjectHelper.isNotEmpty(ObjectHelper.isNotEmpty(sonList))) {
									System.out.println(sonList.size());
									if("1".equals(sonList.get(2))) {
										mix.setIswinorlose(sonList.get(2).toString());
										mix.setHomewin(new BigDecimal(sonList.get(0).toString()));
										mix.setHomelose(new BigDecimal(sonList.get(1).toString()));
									}
								}
								break;
							case 2:
								if(ObjectHelper.isNotEmpty(ObjectHelper.isNotEmpty(sonList))) {
									System.out.println(sonList);
									mix.setRf(new BigDecimal(sonList.get(0).toString()));
									if(ObjectHelper.isNotEmpty(sonList.get(1))) {
										mix.setRfhomewin(new BigDecimal(sonList.get(1).toString()));///等真实数据出现后再核实一遍
									}
									if(ObjectHelper.isNotEmpty(sonList.get(2))) {
										mix.setRfhomelose(new BigDecimal(sonList.get(2).toString()));
									}
									if(ObjectHelper.isNotEmpty(sonList.get(3))) {
										mix.setIsrf(sonList.get(3).toString());
									}
								}
								break;
							/*case 3:
								if(ObjectHelper.isNotEmpty(ObjectHelper.isNotEmpty(sonList))) {
									System.out.println(sonList);
									sonList.get(13);
								}
								break;*/
							default:
								break;
							}
						}
					}
        			BaMixExample baEx = new BaMixExample();
					baEx.createCriteria().andIdjingcaiEqualTo(mix.getIdjingcai());
					List<BaMix> baList = baMappmer.selectByExample(baEx);
					if(ObjectHelper.isEmpty(baList)) {
						mix.setId(IDUtils.createUUId());
						baMappmer.insert(mix);
					} else {
						mix.setId(baList.get(0).getId());
						baMappmer.updateByPrimaryKeySelective(mix);
					}
        		}
		} catch (Exception e) {
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					getBasketBall();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取截止时间
	 * @param startTime
	 * @return
	 */
	public static Date getEndTime(Date startTime) {
		Date endDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String week = UtilBag.getWeekOfDate(startTime);
			if("星期三".equals(week) || "星期四".equals(week)) {
				System.out.println("周3和周4");
				String st = sdf.format(startTime);
				if(!StringUtils.isEmpty(st)) {
					//起始限制时间点
					String limitStart = st.split(" ")[0] + " 07:30" ;
					String limitedEnd = UtilBag.dateUtilWithDate(1, startTime) + " 00:00" ;
					long ls = sdf.parse(limitStart).getTime();
					long le = sdf.parse(limitedEnd).getTime();
					long pageTime = startTime.getTime();
					//可以销售
					if(pageTime - ls > 0 && le - pageTime > 0) {
						System.out.println("可以销售");
						Date afterDate = new Date(startTime.getTime() - 300000);
						endDate = afterDate;
					} else {
						System.out.println("禁止销售");
						String dateStr = st.split(" ")[0] + " 00:00";
						Date afterDate = sdf.parse(dateStr);
						afterDate = new Date(afterDate.getTime() - 300000);
						endDate = afterDate;
					}
				}
			} else if("星期一".equals(week) || "星期二".equals(week) || "星期五".equals(week)) {
				System.out.println("周1和周2周5");
				String st = sdf.format(startTime);
				if(!StringUtils.isEmpty(st)) {
					//起始限制时间点
					String limitStart = st.split(" ")[0] + " 09:00" ;
					String limitedEnd = UtilBag.dateUtilWithDate(1, startTime) + " 00:00" ;
					long ls = sdf.parse(limitStart).getTime();
					long le = sdf.parse(limitedEnd).getTime();
					long pageTime = startTime.getTime();
					//可以销售
					if(pageTime - ls > 0 && le - pageTime > 0) {
						System.out.println("可以销售");
						Date afterDate = new Date(startTime.getTime() - 300000);
						endDate = afterDate;
					} else {
						System.out.println("禁止销售");
						String dateStr = st.split(" ")[0] + " 00:00";
						Date afterDate = sdf.parse(dateStr);
						afterDate = new Date(afterDate.getTime() - 300000);
						endDate = afterDate;
					}
				}
			} else {
				System.out.println("周6和周7");
				String st = sdf.format(startTime);
				if(!StringUtils.isEmpty(st)) {
					//起始限制时间点
					String limitStart = st.split(" ")[0] + " 09:00" ;
					String limitedEnd = UtilBag.dateUtilWithDate(1, startTime) + " 01:00" ;
					long ls = sdf.parse(limitStart).getTime();
					long le = sdf.parse(limitedEnd).getTime();
					long pageTime = startTime.getTime();
					//可以销售
					if(pageTime - ls > 0 && le - pageTime > 0) {
						System.out.println("可以销售");
						Date afterDate = new Date(startTime.getTime() - 300000);
						endDate = afterDate;
					} else {
						System.out.println("禁止销售");
						String dateStr = st.split(" ")[0] + " 01:00";
						Date afterDate = sdf.parse(dateStr);
						afterDate = new Date(afterDate.getTime() - 300000);
						endDate = afterDate;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return endDate;
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//			Date date = sdf.parse("2017-07-29 09:45");
//			getEndTime(date);
			getBasketBall();
			getFullBasketData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		System.out.println("程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
	}
}
