package com.kprd.date.fetch.jingcai.basketball;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.HttpClient;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.BaMixMapper;
import com.kprd.newliansai.pojo.BaMix;
import com.kprd.newliansai.pojo.BaMixExample;

public class JingCaiBasketYesterday {

	//篮球结果
	private static String yesterdayUrl = "http://info.sporttery.cn/basketball/match_result.php";
	//队伍json
	private static String teamUrl = "http://i.sporttery.cn/api/bk_match_info/get_match_info/?mid=teamId&_=timestamp";
	//历史赔率数据
	private static String dataUrl = "http://info.sporttery.cn/basketball/pool_result.php?id=teamId";
	//篮球mapper
	static BaMixMapper baMappmer = Main.applicationContext.getBean(BaMixMapper.class);
	
	public static void getYesterday(String start_date,String end_date) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			if(ObjectHelper.isNotEmpty(start_date) && ObjectHelper.isNotEmpty(end_date)) {
				params.put("start_date", start_date);
				params.put("end_date", end_date);
				params.put("lid", "0");
				params.put("tid", "0");
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String today = sdf.format(new Date());
				//竞彩官网的查询条件是当天日期+当天日期查出昨天的所有比赛 (有问题，竞彩和500昨日比赛查询规则不一样，暂定查询从3天前到今天的)
				String sd = UtilBag.dateUtil(-3);
				params.put("start_date", sd);
				params.put("end_date", today);
				params.put("lid", "0");
				params.put("tid", "0");
			}
			String result = HttpClient.doPost(yesterdayUrl, params,"gbk");
			if(!StringUtils.isEmpty(result)){ 
				Document doc = Jsoup.parse(result);
				List<String> urlList = new ArrayList<String>();
				Elements m_page = doc.select(".m-page");
				if(ObjectHelper.isNotEmpty(m_page)) {
					Elements as = m_page.get(0).select("ul li a");
					if(ObjectHelper.isNotEmpty(as)) {
						for(Element a : as) {
							if(a.text().indexOf("尾页") == -1) {
								urlList.add("http://info.sporttery.cn/football/" + a.attr("href"));
							}
						}
					}
				}
				getBaDataInfo(doc);
				if(ObjectHelper.isNotEmpty(urlList)) {
					for(String url : urlList) {
						String html = UtilBag.urlFetch(url, "gb2312");
						Document dom = Jsoup.parse(html);
						getBaDataInfo(dom);
					}
				}
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					getYesterday(start_date, end_date);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过历史页面获取idunique
	 * @param date
	 * @param num
	 * @return
	 */
	private static String getRightDate(String date,String num) {
		String rightDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String w = UtilBag.getWeekOfDate(sdf.parse(date));
			w = w.substring(2,3);
			w = UtilBag.getWeekDay(w);
			if(num.equals(w)) {
				rightDate = date;
				return rightDate;
			} else {
				rightDate = UtilBag.dateUtilWithDate(-1, sdf.parse(date));
				getRightDate(rightDate, num);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rightDate;
	}
	
	/**
	 * 处理doc对象
	 * @param doc
	 */
	public static void getBaDataInfo(Document doc) {
		if(ObjectHelper.isNotEmpty(doc)) {
			Elements match_lists = doc.select(".match_list");
			if(ObjectHelper.isNotEmpty(match_lists) && match_lists.size() == 2) {
				Element matches = match_lists.get(1);
				Elements trs = matches.select("table tr");
				if(ObjectHelper.isNotEmpty(trs)) {
					for(Element tr : trs) {
						if("background-color:#FFF".equals(tr.attr("style"))) {
							break;
						}
						Elements tables = tr.select("table");
						if(ObjectHelper.isNotEmpty(tables)) {
							break;
						}
						
						Elements tds = tr.children();
						if(ObjectHelper.isNotEmpty(tds)) {
							Elements spans = tds.get(3).select("span");
							if(ObjectHelper.isNotEmpty(spans) && spans.size() == 3) {
								String homeFName = spans.get(2).attr("title");
								if("康涅狄格太阳".equals(homeFName)) {
									System.out.println();
								}
								
								String code = tds.get(1).text();
								String week = code.substring(code.indexOf("周")+1,code.indexOf("周")+2);
								code = code.substring(2,5);
								String num = UtilBag.getWeekDay(week);
								String start = getRightDate(tds.get(0).text(),num);
								if(ObjectHelper.isNotEmpty(start)) {
									start = start.replace("-", "");
									String idun = start+num+code;
									BaMixExample baEx = new BaMixExample();
									baEx.createCriteria().andIduniqueEqualTo(idun);
									List<BaMix> baList = baMappmer.selectByExample(baEx);
									if(ObjectHelper.isNotEmpty(baList)) {
										String finals = tds.get(7).text();
										String status  = tds.get(8).text();
										if(ObjectHelper.isNotEmpty(finals) && finals.indexOf(":") > -1 && status.equals("已完成")) {
											baList.get(0).setScore(finals);
											baMappmer.updateByPrimaryKeySelective(baList.get(0));
											System.out.println("更新，主队名 " + homeFName + " idu:" + idun);
										}
									} else {
										//比昨天更之前的比赛 属于历史抓取范围
										System.out.println("历史范围");
										getBasketHistory(tr, idun);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 篮球历史
	 * @param trs
	 * @param idun
	 */
	private static void getBasketHistory(Element tr,String idun) {
		try {
			Elements tds = tr.children();
			if(ObjectHelper.isNotEmpty(tds) && tds.size() > 1) {
				BaMix ba = new BaMix();
				ba.setIdunique(idun);
				ba.setLeaguecolor(tds.get(2).attr("bgcolor"));
				ba.setLeaguename(tds.get(2).text());
				Elements as = tds.get(3).select("a");
				if(ObjectHelper.isNotEmpty(as) && as.size() > 0) {
					String urlStr = as.get(0).attr("href");
					if(!StringUtils.isEmpty(urlStr)) {
						String jingCaiId = urlStr.split("=")[1];
						ba.setIdjingcai(jingCaiId);
						getSimpleTeamInfo(ba);
					}
				}
				
				Elements spans = tds.get(3).select("span");
				if(ObjectHelper.isNotEmpty(spans) && spans.size() == 3) {
					ba.setAwayfullname(spans.get(0).attr("title"));
					ba.setHomefullname(spans.get(2).attr("title"));
					String awayTeam = spans.get(0).text();
					String homeTeam = spans.get(2).text();
//							if(awayTeam.indexOf("(") > -1) {
//								ba.setAwayshortname(awayTeam.split("\\(")[0]);
//								awayTeam = awayTeam.split("\\(")[1];
//							} else {
//								ba.setHomeshortname(homeTeam);
//							}
					ba.setHomeshortname(homeTeam);
					ba.setAwayshortname(awayTeam);
				}
				String status = tds.get(8).text();
				if(ObjectHelper.isNotEmpty(status)) {
					String finalScore = tds.get(7).text();
					if(ObjectHelper.isNotEmpty(finalScore) && finalScore.indexOf(":") > -1) {
						ba.setScore(finalScore);
					}
				}
				getHistoryData(ba);
				ba.setId(IDUtils.createUUId());
				BaMixExample bmEx = new BaMixExample();
				bmEx.createCriteria().andIduniqueEqualTo(ba.getIdunique());
				List<BaMix> baList = baMappmer.selectByExample(bmEx);
				if(ObjectHelper.isEmpty(baList)) {
					baMappmer.insert(ba);
					System.out.println("完成一条历史篮球数据");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取历史4项数据
	 * @param ba
	 */
	public static void getHistoryData(BaMix ba) {
		dataUrl = dataUrl.replace("teamId", ba.getIdjingcai());
		try {
			Document doc = UtilBag.getDocumentByUrl(dataUrl);
			dataUrl = "http://info.sporttery.cn/basketball/pool_result.php?id=teamId";
			if(ObjectHelper.isNotEmpty(doc)) {
				Elements tables = doc.select(".kj-table");
				if(ObjectHelper.isNotEmpty(tables) && tables.size() == 4) {
					Element table = tables.get(0);
					Elements trs = table.select("tr");
					Element tr = trs.get(trs.size()-1);
					Elements tds = tr.select("td");
					if(tds.text().indexOf("暂无数据") == -1) {
						ba.setHomelose(new BigDecimal(tds.get(1).text()));
						ba.setHomewin(new BigDecimal(tds.get(2).text()));
					}
					
					table = tables.get(1);
					trs = table.select("tr");
					tr = trs.get(trs.size()-1);
					tds = tr.select("td");
					if(!"暂无数据".equals(tds.text())) {
						ba.setRfhomelose(new BigDecimal(tds.get(1).text()));
						ba.setRf(new BigDecimal(tds.get(2).text()));
						ba.setRfhomewin(new BigDecimal(tds.get(3).text()));
					}
					
					table = tables.get(2);
					trs = table.select("tr");
					tr = trs.get(trs.size()-1);
					tds = tr.select("td");
					if(tds.text().indexOf("暂无数据") == -1) {
						ba.setBigscore(new BigDecimal(tds.get(1).text()));
						ba.setScoresum(new BigDecimal(tds.get(2).text()));
						ba.setSmallscore(new BigDecimal(tds.get(3).text()));
					}
					
					table = tables.get(3);
					trs = table.select("tr");
					tr = trs.get(trs.size()-1);
					tds = tr.select("td");
					if(tds.text().indexOf("暂无数据") == -1) {
						ba.setSfcAway15Odds(new BigDecimal(tds.get(1).text()));
						ba.setSfcAway610Odds(new BigDecimal(tds.get(2).text()));
						ba.setSfcAway1115Odds(new BigDecimal(tds.get(3).text()));
						ba.setSfcAway1620Odds(new BigDecimal(tds.get(4).text()));
						ba.setSfcAway2125Odds(new BigDecimal(tds.get(5).text()));
						ba.setSfcAway26Odds(new BigDecimal(tds.get(6).text()));
						ba.setSfcHome15Odds(new BigDecimal(tds.get(7).text()));
						ba.setSfcHome610Odds(new BigDecimal(tds.get(8).text()));
						ba.setSfcHome1115Odds(new BigDecimal(tds.get(9).text()));
						ba.setSfcHome1620Odds(new BigDecimal(tds.get(10).text()));
						ba.setSfcHome2125Odds(new BigDecimal(tds.get(11).text()));
						ba.setSfcHome26Odds(new BigDecimal(tds.get(12).text()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取之前页面获取不到的队伍联赛信息
	 * @param ba
	 */
	public static void getSimpleTeamInfo(BaMix ba) {
		String html = "";
		teamUrl = teamUrl.replace("teamId", ba.getIdjingcai());
		teamUrl = teamUrl.replace("timestamp", String.valueOf(System.currentTimeMillis()));
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(teamUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			teamUrl = "http://i.sporttery.cn/api/bk_match_info/get_match_info/?mid=teamId&_=timestamp";
			try {
				// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) { 
                	html = UtilBag.handleEntityCharset(entity, "gbk");
                	html = UtilBag.unicode2String(html);
                	html = html.replace("\\/", "/");
                	if(ObjectHelper.isNotEmpty(html)) {
                		JSONObject data = JSONObject.fromObject(html);
                		JSONObject result = JSONObject.fromObject(data.get("result"));
                		String date_cn = result.getString("date_cn");
                		String time_cn = result.getString("time_cn");
                		String start = date_cn + " " + time_cn;
                		if(start.indexOf("null") == -1) {
                			ba.setStarttime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start));
                			Date endtime = getEndTime(ba.getStarttime());
                    		ba.setEndtime(endtime);
                		}
                		
                		String teamIdH = result.get("h_id_dc").toString();
                		String teamIdA = result.get("a_id_dc").toString();
                		String leagueId = result.get("l_id_dc").toString();
                		ba.setHometeamid(teamIdH);
                		ba.setAwayteamid(teamIdA);
                		ba.setLeagueid(leagueId);
                	}
                }
			} finally {
				response.close();
			}
		} catch (Exception e) {
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
//		getYesterday("2017-07-01","2017-07-02");
		getYesterday(null,null);
	}
}
