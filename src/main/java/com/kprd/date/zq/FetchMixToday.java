package com.kprd.date.zq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kprd.common.utils.Contant;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.JedisUtilForFetch;
import com.kprd.date.util.UtilBag;
import com.kprd.liansai.mapper.MTeamMapper;
import com.kprd.liansai.mapper.MVsMapper;
import com.kprd.liansai.pojo.MTeam;
import com.kprd.liansai.pojo.MTeamExample;
import com.kprd.liansai.pojo.MVs;
import com.kprd.liansai.pojo.MVsExample;
import com.kprd.liansai.pojo.MVsExample.Criteria;
import com.kprd.odds.pojo.Asiaodds;
import com.kprd.odds.pojo.Bigsmall;
import com.kprd.odds.pojo.Europodds;


public class FetchMixToday {
	
//	private static Logger logger = Logger.getLogger(FetchMixToday.class);
	private static Log logger = LogFactory.getLog("FetchMixToday");
	//混合过关初始地址
	private static String baseUrl = "http://trade.500.com/jczq/index.php?playid=312";
	//获取混合页面tr  id 的 xml
	private final static String ORI = "http://trade.500.com/static/public/jczq/xml/odds/odds.xml";
	//文件下载地址
	private String imgPath = "F:\\dowloadImgs\\";
	
	static MTeamMapper mtMapper = Main.applicationContext.getBean(MTeamMapper.class);
	
	/**
     * 获取zid XML
     * @return
     */
    @SuppressWarnings("unchecked")
	public static LinkedList<String> getZids(boolean flag) {
    	logger.info("执行getZids方法，获取odds.xml");
		LinkedList<String> lList = new LinkedList<String>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String dateStr = null;
		try {
			// 创建httpget.    
            HttpGet httpget = null;
			if(flag) {
				httpget = new HttpGet(ORI);
			} else {
				dateStr = dateUtil(-1);
				String mixOdds = "http://trade.500.com/static/public/jczq/xml/hisdata/year/monthday/odds.xml";
				mixOdds = mixOdds.replace("year", dateStr.split(",")[0]);
				mixOdds = mixOdds.replace("monthday", dateStr.split(",")[1].replace("-", ""));
				httpget = new HttpGet(mixOdds);
			}
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				try {
		            // 获取响应实体    
		            HttpEntity entity = response.getEntity();
		            if(null !=  entity) {
		            	String xml = EntityUtils.toString(entity, "utf-8");
		            	org.dom4j.Document dom4j = DocumentHelper.parseText(xml);
		            	org.dom4j.Element root = dom4j.getRootElement();
		            	List<org.dom4j.Element> listEle = root.elements();
						org.dom4j.Element rowset = listEle.get(listEle.size() - 1);
						List<org.dom4j.Element> matchInfo = rowset.elements("match");
						for(org.dom4j.Element match : matchInfo) {
							lList.add(match.attributeValue("id").toString());
						}
		            }
				} finally {
					response.close();
				}
			} catch (Exception e) {
				if(e.getMessage().indexOf("必须由匹配的结束标记") > -1) {
					logger.info("该日期没有比赛 url =============>" + ORI);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			logger.error("获取当天数据getZids()方法异常，请求地址为============>" + ORI);
			if(e.getMessage().indexOf("UnknownHostException") > -1 || e.getMessage().indexOf("ConnectException") > -1 || e.getMessage().indexOf("Truncated chunk") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					System.out.println("60s");
					Thread.sleep(1000*60*2);
				} catch (InterruptedException e1) {
					return getZids(flag);
				}
			}
			e.printStackTrace();
		}
		logger.info("结果为：" + lList.size());
		if(flag) {
			getMixInfo(lList,null,flag);
		} else {
			getMixInfo(lList,dateStr.replace(",", "-"),flag);
		}
		return lList;
	}
    
    /**
     * 日期天数加减，传入+or-数字，返回当前日期加减参数后的日期字符串
     * @param num
     * @return
     */
    public static String dateUtil(int num) {
    	Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, num);
		date = calendar.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy,MM-dd");//设置日期格式
		return df.format(date);
    }
    
    /**
	 * 抓取比赛类型
	 * 开赛时间
	 * 截止时间
	 * 剩余时间
	 * @param tr
	 */
	private static void getGameTypeAndTime(Element tr,MVs mvs) {
		logger.info("执行getGameTypeAndTime方法，抓取比赛类型,开赛时间,截止时间,剩余时间");
		try {
			//.league_num 获取比赛类型（如：德甲，美洲杯）
			Elements divs = tr.select(".league_num");
			if(null != divs && divs.size() > 0) {
				Elements as = divs.get(0).select("a");
				if(null != as && as.size() > 0) {
					//赛事类型
		    		String gameType = as.get(1).text();
		    		mvs.setGameLeagueName(gameType);
				}
				//联赛颜色
				String style = divs.get(0).select("span").get(0).attr("style");
				style = style.split("#")[1];
				mvs.setGameLeagueColor(style);
			}
			
			//获取拼装场次id
			String pdate = tr.attr("pdate");
			if(ObjectHelper.isNotEmpty(pdate)) {
				pdate = pdate.trim().replace("-", "");
				pdate += tr.attr("pname").trim();
				mvs.setCcId(pdate);
			}
			
			//开赛时间
			String start_time = tr.select(".match_time").get(0).attr("title").split("：")[1].trim();
			//截止时间
			String end_time = tr.select(".end_time").get(0).attr("title").split("：")[1].trim();
			//剩余时间
			String countdown_time = tr.select(".countdown_time").get(0).attr("data-endtime").trim();
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			mvs.setStartTime(simpleDateFormat.parse(start_time));
			mvs.setEndTime(simpleDateFormat.parse(end_time));
			mvs.setCountdownTime(simpleDateFormat.parse(countdown_time));
		} catch (Exception e) {
			logger.error(mvs.getZid() + ",getGameTypeAndTime方法异常:");
			logger.error(e);
			e.printStackTrace();
		}
	}
    
	/**
     * 通过析页面找到平均概率
     * @param url
     * @return
     */
    private static String getAvgRate(Element trParam,MVs mvs) {
    	logger.info("执行getAvgRate方法，通过析页面找到平均概率");
    	System.out.println("通过析页面找到平均概率开始↓↓↓↓↓↓");
    	//析
		String analysisUrl = "";
		StringBuffer result = new StringBuffer();
		//通过析获取平均概率
		Elements tdsx = trParam.select("td");
		if(null != tdsx && tdsx.size() > 0) {
			analysisUrl = tdsx.get(3).select("a").get(0).attr("href").trim();
			analysisUrl = analysisUrl.replace("shuju", "ouzhi");
	    	CloseableHttpClient httpclient = HttpClients.createDefault();
	    	try {
	    		System.out.println("通过析页面找到平均概率,请求路径：" + analysisUrl);
	    		// 创建httpget.    
	            HttpGet httpget = new HttpGet(analysisUrl);  
	            System.out.println("executing request " + httpget.getURI());  
	            httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
	            httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
	            httpget.setHeader("Host","odds.500.com");
	            httpget.setHeader("Accept-Encoding", "GZIP"); 
	            // 执行get请求.    
	            CloseableHttpResponse response = httpclient.execute(httpget);
	            Thread.sleep(5*1000);
	            try {
	            	// 获取响应实体    
	                HttpEntity entity = response.getEntity();  
	                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
	                	String html = EntityUtils.toString(entity, "gb2312");
//	                   UtilBag.toFile(html.getBytes(), new File("C:\\Users\\Administrator\\Desktop\\fetch\\123.txt"));
	                   Document doc = Jsoup.parse(html);
	                   if(null != doc) {
	                	   Elements divs = doc.select("#table_btm");
	                	   if(null != divs && divs.size() > 0) {
	                		   Elements trs = divs.get(0).select("table tr");
	                		   if(null != trs && trs.size() > 0) {
	                			   for(Element tr : trs) {
	                				   //定位到<tr xls="footer">
	                				   if(tr.attr("xls").trim().indexOf("footer") > -1) {
	                					   Elements tables = tr.select("table");
	                					   if(null != tables && tables.size() > 0) {
	                						   Element avgTable = tables.get(1);
	                						   Elements trs2 = avgTable.select("tbody tr");
	                						   if(null != trs2 && trs2.size() > 0) {
	                							   Elements tds = trs2.get(1).children();
	                							   if(null != tds && tds.size() > 0) {
	                								   String avgwin = "";
	                								   String avgdraw = "";
	                								   String avglost = "";
	                								   for(Element td : tds) {
	                									   //胜平均
	                									   if(td.attr("id").indexOf("avwinlj2") > -1) {
	                										   avgwin = td.text().trim();
	                										   mvs.setAvgrateWin(avgwin);
	                									   }
	                									   //平平均
	                									   if(td.attr("id").indexOf("avdrawlj2") > -1) {
	                										   avgdraw = td.text().trim();
	                										   mvs.setAvgrateDraw(avgdraw);
	                									   }
	                									   //负平均
	                									   if(td.attr("id").indexOf("avlostlj2") > -1) {
	                										   avglost = td.text().trim();
	                										   mvs.setAvgrateLose(avglost);
	                									   }
	                								   }
	                								  result.append(avgwin + ",").append(avgdraw + ",").append(avglost);
	                								  System.out.println("平均概率 " + result.toString());
	                								  System.out.println("通过析页面找到平均概率结束↑↑↑↑↑↑");
	                								  return result.toString();
	                							   }
	                						   }
	                					   }
	                				   }
	                			   }
	                		   }
	                	   }
	                   }
	                }  
				} finally {
					response.close();
				}
			} catch (Exception e) {
				logger.error(mvs.getZid() + "getAvgRate方法异常：");
				logger.error(e);
				e.printStackTrace();
				try {
					logger.error(e);
					System.out.println("**********沉睡60秒********");
					Thread.sleep(60*1000*2);
				} catch (Exception e1) {
					return getAvgRate(trParam, mvs);
				}
//				if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1) {
//					try {
//						Thread.sleep(60*1000);
//					} catch (InterruptedException e1) {
//						return getAvgRate(trParam, mvs);
//					}
//				} else {
//				System.exit(0);
//				}
			}
		}
    	return result.toString();
    }
    
    /**
     * 抓取主客队名字，让球和球队详细信息
     * @param tr
     */
    private static void getTeamNameAndInfo(Element tr,MVs mvs) {
		Elements uls = tr.select(".team_vs");
		
		Elements divsOne = tr.select(".league_num");
		if(null != divsOne && divsOne.size() > 0) {
			Elements as = divsOne.get(0).select("a");
			if(null != as && as.size() > 0) {
				//赛事类型
	    		String gameType = as.get(1).text();
	    		String leagueId = as.get(1).attr("href").replace("/", "").split("-")[1];
	    		System.out.println("赛事类型 " + gameType);
	    		mvs.setGameTypeId(leagueId);
			}
		}
		
		if(null != uls && uls.size() > 0) {
			Elements lis = uls.get(0).children();
			if(null != lis && lis.size() == 3) {
				for(int i=0;i<lis.size();i++) {
					//主队
					if(i==0) {
						Element a = lis.get(i).select("a").get(0);
						Elements spans = lis.get(i).select("span");
						if(null != spans && spans.size() > 0) {
							//让球
							String rangqiu = spans.get(0).text().trim();
							rangqiu = rangqiu.substring(1, rangqiu.length()-1);
							mvs.setRqs(rangqiu);
						}
						//主队地址
						String team_url = a.attr("href").trim();
						String [] team_urls = team_url.replace("/", "").split("m");
						team_url = team_urls[team_urls.length-1];
						mvs.setHomeUrlId(team_url);
						MTeamExample mtEx = new MTeamExample();
						com.kprd.liansai.pojo.MTeamExample.Criteria criteria = mtEx.createCriteria();
						criteria.andUrlTeamEqualTo(team_url);
						List<MTeam> mtList = mtMapper.selectByExample(mtEx);
						if(ObjectHelper.isEmpty(mtList)) {
							//主队简称
							String team_nickname = a.text().trim();
							//主队全称
							String team_fullname = a.attr("title").trim();
							mvs.setHomeShortname(team_nickname);
							mvs.setHomeFullname(team_fullname);
						} else {
							mvs.setHomeShortname(mtList.get(0).getShortnameTeam());
							mvs.setHomeFullname(mtList.get(0).getFullnameTeam());
						}
						
					} else if(i==2) {//客队
						Element a = lis.get(i).select("a").get(0);
						//主队地址
						String team_url = a.attr("href").trim();
						
						String [] team_urls = team_url.replace("/", "").split("m");
						team_url = team_urls[team_urls.length-1];
						mvs.setAwayUrlId(team_url);
						MTeamExample mtEx = new MTeamExample();
						com.kprd.liansai.pojo.MTeamExample.Criteria criteria = mtEx.createCriteria();
						criteria.andUrlTeamEqualTo(team_url);
						List<MTeam> mtList = mtMapper.selectByExample(mtEx);
						if(ObjectHelper.isEmpty(mtList)) {
							//主队简称
							String team_nickname = a.text().trim();
							//主队全称
							String team_fullname = a.attr("title").trim();
							mvs.setAwayShortname(team_nickname);
							mvs.setAwayFullname(team_fullname);
						} else {
							mvs.setAwayShortname(mtList.get(0).getShortnameTeam());
							mvs.setAwayFullname(mtList.get(0).getFullnameTeam());
						}
					}
				}
			}
		}
	}
    
    
    
    /**
     * 获取 胜平负，让球胜平负，比分，总进球，全半场
     * @param tr
     */
    private static void get5Data(Element tr,MVs mvs,String li) {
		logger.info("执行get5Data方法，获取 胜平负，让球胜平负，比分，总进球，全半场");
		try {
			Elements dvs = tr.select("td > div");
			if(null != dvs && dvs.size() > 0) {
				for(int i=0;i<dvs.size();i++) {
					if((dvs.get(i).attr("class").indexOf("bet_odds") > -1 && dvs.get(i).attr("class").indexOf("bet_btns") > -1) || (dvs.get(i).attr("class").indexOf("bet_more") > -1)) {
						System.out.println(dvs.get(i) + "====" + i);
						if(i==1) {//胜平负
							//(胜平负)是否中标标示，0:胜，1：平，2：负
							String flag = "";
							Elements spans = dvs.get(i).children();
							if(null != spans && spans.size() > 0 && spans.size() == 3) {
								//odds_bingo
								for(int j=0;j<spans.size();j++) {
									Elements bingoSpan = spans.get(j).select(".odds_bingo");
									if(j==0) {
										//检测是否中标（odds_bingo为红色的样式class）
										if(null != bingoSpan && bingoSpan.size() > 0) {
											mvs.setSpfSBingo("1");
										}
										String basic_spf_win = spans.get(j).select(".odds_item").attr("data-sp").trim();
										mvs.setSpfS(basic_spf_win);
									} else if(j==1) {
										//检测是否中标（odds_bingo为红色的样式class）
										if(null != bingoSpan && bingoSpan.size() > 0) {
											mvs.setSpfPBingo("1");
										}
										String basic_spf_draw = spans.get(j).select(".odds_item").attr("data-sp").trim();
										mvs.setSpfP(basic_spf_draw);
									} else if(j==2) {
										//检测是否中标（odds_bingo为红色的样式class）
										if(null != bingoSpan && bingoSpan.size() > 0) {
											mvs.setSpfFBingo("1");
										}
										String basic_spf_lose = spans.get(j).select(".odds_item").attr("data-sp").trim();
										mvs.setSpfF(basic_spf_lose);
									}
								}
							} else if(null != spans && spans.size() > 0 && spans.size() == 1) {
								//未开售的情况先暂时不管
							}
							System.out.println("(胜平负)是否中标标示，0:胜，1：平，2：负 ===========>" + flag);
						} else if(i==2) {//让球胜平负
							//(让球胜平负)是否中标标示，0:胜，1：平，2：负
							String flag = "";
							Elements spans = dvs.get(i).select(".odds_item");
							if(null != spans && spans.size() > 0) {
								for(int j=0;j<spans.size();j++) {
									if(spans.get(j).attr("class").indexOf("odds_item") > -1) {
										Elements bingoSpan = spans.get(j).select(".odds_bingo");
										if(j==0) {//让球胜
											if(null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setRqSpfSBingo("1");
											}
											String rqs = spans.get(j).attr("data-sp").trim();
											mvs.setRqSpfS(rqs);
										} else if(j==1) {//让球平
											if(null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setRqSpfPBingo("1");
											}
											String rqp = spans.get(j).attr("data-sp").trim();
											mvs.setRqSpfP(rqp);
										} else if(j==2) {//让球负
											if(null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setRqSpfFBingo("1");
											}
											String rqf = spans.get(j).attr("data-sp").trim();
											mvs.setRqSpfF(rqf);
										}
									}
								}
							}
							System.out.println("(让球胜平负)是否中标标示，0:胜，1：平，2：负========>" + flag);
						} else if(i==5) {//比分（跳过2是因为2的数据不全）
							Elements spans = dvs.get(i).children();
							if(null != spans && spans.size() > 0) {
								String longData = spans.get(0).attr("data-sp").trim();
								getScoreData(longData,mvs);
							}
						} else if(i==8) {//总进球
							Elements spans = dvs.get(i).children();
							if(null != spans && spans.size() > 0) {
								String longData = spans.get(0).attr("data-sp");
								getTotalGoalOrQbc(longData,mvs);
							}
						} else if(i==11) {//全半场
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							//抓取半场
							LinkedHashMap<String, String> halfMap = halfData(sdf.format(new Date()),li,mvs);
							if(halfMap.size() == 0) {
								Elements spans = dvs.get(i).children();
								if(null != spans && spans.size() > 0) {
									String longData = spans.get(0).attr("data-sp");
									getQbc(longData,mvs);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(mvs.getZid() + "get5Data方法异常：");
			logger.error(e);
			e.printStackTrace();
		}
    }
    
    /**
	 * 全半场纯数据，没有bingo
	 * @param longData
	 * @param mvs
	 */
	private static void getQbc(String longData,MVs mvs) {
		String[] step1 = longData.split(",");
		System.out.println(step1);
		for(int i=0;i<step1.length;i++) { 
    		if(i==0) {
    			mvs.setQbcSs(step1[i].split("\\|")[1]);
    		} else if(i==1) {
    			mvs.setQbcSp(step1[i].split("\\|")[1]);
    		} else if(i==2) {
    			mvs.setQbcSf(step1[i].split("\\|")[1]);
    		} else if(i==3) {
    			mvs.setQbcPs(step1[i].split("\\|")[1]);
    		} else if(i==4) {
    			mvs.setQbcPp(step1[i].split("\\|")[1]);
    		} else if(i==5) {
    			mvs.setQbcPf(step1[i].split("\\|")[1]);
    		} else if(i==6) {
    			mvs.setQbcFs(step1[i].split("\\|")[1]);
    		} else if(i==7) {
    			mvs.setQbcFp(step1[i].split("\\|")[1]);
    		} else if(i==8) {
    			mvs.setQbcFf(step1[i].split("\\|")[1]);
    		}
    	}
	}
    
    /**
	 * 获取全半场数据包括bingo
	 * @param theday
	 * @param li
	 * @return
	 */
	private static LinkedHashMap<String, String> halfData(String theday,String li,MVs mvs) {
		//半场数据地址
		String halfUrl = "http://trade.500.com/jczq/index.php?playid=272&date=";
		halfUrl += theday;
		System.out.println(halfUrl);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Document doc = UtilBag.getDocumentByUrl(halfUrl);
		if(null != doc) {
			Elements trs = doc.select("tr");
			if(null != trs && trs.size() > 0) {
				for(Element tr : trs) {
					if(li.equals(tr.attr("zid"))) {
						Elements divs = tr.select(".bet_odds");
						if(null != divs && divs.size() > 0) {
							Elements spans = divs.get(0).children();
							if(null != spans && spans.size() > 0) {
								for(Element span : spans) {
									if(span.attr("class").indexOf("odds_bingo") > -1) {//bingo
										String type = span.attr("value").trim();
										String value = span.attr("data-sp").trim() + "bingo";
										map.put(type, value);
										if("33".equals(type)) {
											mvs.setQbcSsBingo("1");
											mvs.setQbcSs(value);
										} else if("31".equals(type)) {
											mvs.setQbcSpBingo("1");
											mvs.setQbcSp(value);
										} else if("30".equals(type)) {
											mvs.setQbcSfBingo("1");
											mvs.setQbcSf(value);
										} else if("13".equals(type)) {
											mvs.setQbcPsBingo("1");
											mvs.setQbcPs(value);
										} else if("11".equals(type)) {
											mvs.setQbcPpBingo("1");
											mvs.setQbcPp(value);
										} else if("10".equals(type)) {
											mvs.setQbcPfBingo("1");
											mvs.setQbcPf(value);
										} else if("03".equals(type)) {
											mvs.setQbcFsBingo("1");
											mvs.setQbcFs(value);
										} else if("01".equals(type)) {
											mvs.setQbcFpBingo("1");
											mvs.setQbcFp(value);
										} else if("00".equals(type)) {
											mvs.setQbcFfBingo("1");
											mvs.setQbcFf(value);
										}
									} else {
										String type = span.attr("value").trim();
										String value = span.attr("data-sp").trim();
										map.put(type, value);
										if("33".equals(type)) {
											mvs.setQbcSs(value);
										} else if("31".equals(type)) {
											mvs.setQbcSp(value);
										} else if("30".equals(type)) {
											mvs.setQbcSf(value);
										} else if("13".equals(type)) {
											mvs.setQbcPs(value);
										} else if("11".equals(type)) {
											mvs.setQbcPp(value);
										} else if("10".equals(type)) {
											mvs.setQbcPf(value);
										} else if("03".equals(type)) {
											mvs.setQbcFs(value);
										} else if("01".equals(type)) {
											mvs.setQbcFp(value);
										} else if("00".equals(type)) {
											mvs.setQbcFf(value);
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		}
//		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
//		while(iter.hasNext()) {
//			Map.Entry entry = (Map.Entry)iter.next();
//        	String urll = (String)entry.getValue();
//        	System.out.println(urll);
//        	if(urll.indexOf("bingo") > -1) {
//        		urll = urll.replace("bingo", "");
//        		System.out.println(urll);
//        	}
//		}
		return map;
	}
    
    /**
     * 总进球或者全半场弹窗数据
     * @param longData
     */
    private static void getTotalGoalOrQbc(String longData,MVs mvs) {
    	String[] step1 = longData.split(",");
    	
    	String finalScore = mvs.getFinalScore();
    	if(ObjectHelper.isNotEmpty(finalScore)) {
    		String[] score = finalScore.split(":");
        	//比分之和
        	int sum = Integer.valueOf(score[0]) + Integer.valueOf(score[1]);
        	for(int i=0;i<step1.length;i++) { 
        		if(i==0) {
        			if(i==sum) {
        				mvs.setZjq0Bingo("1");
        			}
        			mvs.setZjq0(step1[i].split("\\|")[1]);
        		} else if(i==1) {
        			if(i==sum) {
        				mvs.setZjq1Bingo("1");
        			}
        			mvs.setZjq1(step1[i].split("\\|")[1]);
        		} else if(i==2) {
        			if(i==sum) {
        				mvs.setZjq2Bingo("1");
        			}
        			mvs.setZjq2(step1[i].split("\\|")[1]);
        		} else if(i==3) {
        			if(i==sum) {
        				mvs.setZjq3Bingo("1");
        			}
        			mvs.setZjq3(step1[i].split("\\|")[1]);
        		} else if(i==4) {
        			if(i==sum) {
        				mvs.setZjq4Bingo("1");
        			}
        			mvs.setZjq4(step1[i].split("\\|")[1]);
        		} else if(i==5) {
        			if(i==sum) {
        				mvs.setZjq5Bingo("1");
        			}
        			mvs.setZjq5(step1[i].split("\\|")[1]);
        		} else if(i==6) {
        			if(i==sum) {
        				mvs.setZjq6Bingo("1");
        			}
        			mvs.setZjq6(step1[i].split("\\|")[1]);
        		} else if(i==7) {
        			if(i==sum) {
        				mvs.setZjq7Bingo("1");
        			}
        			mvs.setZjq7(step1[i].split("\\|")[1]);
        		}
        	}
    	} else {
    		for(int i=0;i<step1.length;i++) { 
        		if(i==0) {
        			mvs.setZjq0(step1[i].split("\\|")[1]);
        		} else if(i==1) {
        			mvs.setZjq1(step1[i].split("\\|")[1]);
        		} else if(i==2) {
        			mvs.setZjq2(step1[i].split("\\|")[1]);
        		} else if(i==3) {
        			mvs.setZjq3(step1[i].split("\\|")[1]);
        		} else if(i==4) {
        			mvs.setZjq4(step1[i].split("\\|")[1]);
        		} else if(i==5) {
        			mvs.setZjq5(step1[i].split("\\|")[1]);
        		} else if(i==6) {
        			mvs.setZjq6(step1[i].split("\\|")[1]);
        		} else if(i==7) {
        			mvs.setZjq7(step1[i].split("\\|")[1]);
        		}
        	}
    	}
    }
    
    /**
     * 获取比分弹窗详细数据
     * @param longData
     */
private static void getScoreData(String longData, MVs mvs) {
    	
    	LinkedHashMap<String, String> bfLists = new LinkedHashMap<String, String>();
    	LinkedHashMap<String, String> bfListp = new LinkedHashMap<String, String>();
    	LinkedHashMap<String, String> bfListpf = new LinkedHashMap<String, String>();
    	
		String[] longs = longData.split("A");
		for(String lo : longs) {
			System.out.println(lo);
		}
		
		System.out.println();
		
		for(int i=1;i<longs.length;i++) {
			if(i==1) {//胜其他
				String step1 = longs[i].substring(0, longs[i].length()-1);
				String sqtVal = longs[i];
				sqtVal = sqtVal.substring(1, step1.indexOf(","));
				mvs.setScoreWinOther(sqtVal);
				step1 = step1.substring(step1.indexOf(",") + 1);
				
				String[] step2s = step1.split(",");
				for(String st : step2s) {
					bfLists.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
				}
			} else if(i==2) {//平其他
				String step1 = longs[i].substring(0, longs[i].length()-1);
				String pqtVal = longs[i];
				pqtVal = pqtVal.substring(1, step1.indexOf(","));
				mvs.setScoreDrawOther(pqtVal);
				step1 = step1.substring(step1.indexOf(",") + 1);
				
				String[] step2s = step1.split(",");
				for(String st : step2s) {
					bfListp.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
				}
			} else if(i==3) {//负其他
				String step1 = longs[i].substring(0, longs[i].length());
				String fqtVal = longs[i];
				fqtVal = fqtVal.substring(1, step1.indexOf(","));
				mvs.setScoreLoseOther(fqtVal);
				step1 = step1.substring(step1.indexOf(",") + 1);
				
				String[] step2s = step1.split(",");
				for(String st : step2s) {
					bfListpf.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
				}
			}
		}
		
		sealedBfLists(bfLists, mvs);
		sealedBfListp(bfListp, mvs);
		sealedBfListf(bfListpf, mvs);
		
		System.out.println(bfLists);
		System.out.println(bfListp);
		System.out.println(bfListpf);
    }

/**
 * 封装负其他
 * @param bfListpf
 * @param mvs
 */
@SuppressWarnings("rawtypes")
private static void sealedBfListf(LinkedHashMap<String, String> bfListpf,MVs mvs) {
	Iterator<Entry<String, String>> iter = bfListpf.entrySet().iterator();
	String finalScore = mvs.getFinalScore();
	if(ObjectHelper.isNotEmpty(finalScore)) {
		finalScore = mvs.getFinalScore().replace(":", "");
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			if("01".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore01Bingo("1");
				}
				mvs.setScore01((String)entry.getValue());
			} else if("02".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore02Bingo("1");
				}
				mvs.setScore02((String)entry.getValue());
			} else if("12".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore12Bingo("1");
				}
				mvs.setScore12((String)entry.getValue());
			} else if("03".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore03Bingo("1");
				}
				mvs.setScore03((String)entry.getValue());
			} else if("13".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore13Bingo("1");
				}
				mvs.setScore13((String)entry.getValue());
			} else if("23".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore23Bingo("1");
				}
				mvs.setScore23((String)entry.getValue());
			} else if("04".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore04Bingo("1");
				}
				mvs.setScore04((String)entry.getValue());
			} else if("14".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore14Bingo("1");
				}
				mvs.setScore14((String)entry.getValue());
			} else if("24".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore24Bingo("1");
				}
				mvs.setScore24((String)entry.getValue());
			} else if("05".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore05Bingo("1");
				}
				mvs.setScore05((String)entry.getValue());
			} else if("15".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore15Bingo("1");
				}
				mvs.setScore15((String)entry.getValue());
			} else if("25".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore25Bingo("1");
				}
				mvs.setScore25((String)entry.getValue());
			}
		}
	} else {
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			if("01".equals((String)entry.getKey())) {
				mvs.setScore01((String)entry.getValue());
			} else if("02".equals((String)entry.getKey())) {
				mvs.setScore02((String)entry.getValue());
			} else if("12".equals((String)entry.getKey())) {
				mvs.setScore12((String)entry.getValue());
			} else if("03".equals((String)entry.getKey())) {
				mvs.setScore03((String)entry.getValue());
			} else if("13".equals((String)entry.getKey())) {
				mvs.setScore13((String)entry.getValue());
			} else if("23".equals((String)entry.getKey())) {
				mvs.setScore23((String)entry.getValue());
			} else if("04".equals((String)entry.getKey())) {
				mvs.setScore04((String)entry.getValue());
			} else if("14".equals((String)entry.getKey())) {
				mvs.setScore14((String)entry.getValue());
			} else if("24".equals((String)entry.getKey())) {
				mvs.setScore24((String)entry.getValue());
			} else if("05".equals((String)entry.getKey())) {
				mvs.setScore05((String)entry.getValue());
			} else if("15".equals((String)entry.getKey())) {
				mvs.setScore15((String)entry.getValue());
			} else if("25".equals((String)entry.getKey())) {
				mvs.setScore25((String)entry.getValue());
			}
		}
	}
}


/**
 * 封装平其他
 * @param zjqList
 * @param mvs
 */
@SuppressWarnings("rawtypes")
private static void sealedBfListp(LinkedHashMap<String, String> zjqList,MVs mvs) {
	Iterator<Entry<String, String>> iter = zjqList.entrySet().iterator();
	String finalScore = mvs.getFinalScore();
	if(ObjectHelper.isNotEmpty(finalScore)) {
		finalScore = mvs.getFinalScore().replace(":", "");
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			if("00".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore00Bingo("1");
				}
				mvs.setScore00((String)entry.getValue());
			} else if("11".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore11Bingo("1");
				}
				mvs.setScore11((String)entry.getValue());
			} else if("22".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore22Bingo("1");
				}
				mvs.setScore22((String)entry.getValue());
			} else if("33".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore33Bingo("1");
				}
				mvs.setScore33((String)entry.getValue());
			}
		}
	} else {
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			if("00".equals((String)entry.getKey())) {
				mvs.setScore00((String)entry.getValue());
			} else if("11".equals((String)entry.getKey())) {
				mvs.setScore11((String)entry.getValue());
			} else if("22".equals((String)entry.getKey())) {
				mvs.setScore22((String)entry.getValue());
			} else if("33".equals((String)entry.getKey())) {
				mvs.setScore33((String)entry.getValue());
			}
		}
	}
}

/***
 * 封装比分
 * @param bfList
 * @param mvs
 */
@SuppressWarnings("rawtypes")
private static void sealedBfLists(LinkedHashMap<String, String> bfList,MVs mvs) {
	
	Iterator<Entry<String, String>> iter = bfList.entrySet().iterator();
	String finalScore = mvs.getFinalScore();
	if(ObjectHelper.isNotEmpty(finalScore)) {
		finalScore = mvs.getFinalScore().replace(":", "");
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			if("10".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore10Bingo("1");
				}
				mvs.setScore10((String)entry.getValue());
			} else if("20".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore20Bingo("1");
				}
				mvs.setScore20((String)entry.getValue());
			} else if("21".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore21Bingo("1");
				}
				mvs.setScore21((String)entry.getValue());
			} else if("30".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore30Bingo("1");
				}
				mvs.setScore30((String)entry.getValue());
			} else if("31".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore31Bingo("1");
				}
				mvs.setScore31((String)entry.getValue());
			} else if("32".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore32Bingo("1");
				}
				mvs.setScore32((String)entry.getValue());
			} else if("40".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore40Bingo("1");
				}
				mvs.setScore40((String)entry.getValue());
			} else if("41".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore41Bingo("1");
				}
				mvs.setScore41((String)entry.getValue());
			} else if("42".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore42Bingo("1");
				}
				mvs.setScore42((String)entry.getValue());
			} else if("50".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore50Bingo("1");
				}
				mvs.setScore50((String)entry.getValue());
			} else if("51".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore51Bingo("1");
				}
				mvs.setScore51((String)entry.getValue());
			} else if("52".equals((String)entry.getKey())) {
				if(((String)entry.getKey()).equals(finalScore)) {
					mvs.setScore52Bingo("1");
				}
				mvs.setScore52((String)entry.getValue());
			}
		}
	} else {
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			if("10".equals((String)entry.getKey())) {
				mvs.setScore10((String)entry.getValue());
			} else if("20".equals((String)entry.getKey())) {
				mvs.setScore20((String)entry.getValue());
			} else if("21".equals((String)entry.getKey())) {
				mvs.setScore21((String)entry.getValue());
			} else if("30".equals((String)entry.getKey())) {
				mvs.setScore30((String)entry.getValue());
			} else if("31".equals((String)entry.getKey())) {
				mvs.setScore31((String)entry.getValue());
			} else if("32".equals((String)entry.getKey())) {
				mvs.setScore32((String)entry.getValue());
			} else if("40".equals((String)entry.getKey())) {
				mvs.setScore40((String)entry.getValue());
			} else if("41".equals((String)entry.getKey())) {
				mvs.setScore41((String)entry.getValue());
			} else if("42".equals((String)entry.getKey())) {
				mvs.setScore42((String)entry.getValue());
			} else if("50".equals((String)entry.getKey())) {
				mvs.setScore50((String)entry.getValue());
			} else if("51".equals((String)entry.getKey())) {
				mvs.setScore51((String)entry.getValue());
			} else if("52".equals((String)entry.getKey())) {
				mvs.setScore52((String)entry.getValue());
			}
		}
	}
}
    
/**
 * 获取球队信息
 * @param teamInfo
 */
public static void getTeamInfo() {
	logger.info("执行getTeamInfo方法，获取球队信息");
	String vsId = null;
	try {
		//成立时间
		String estTime = null;
		//球场容量
		String capacity = null;
		//国家
		String nation = null;
		//球场名称
		String courtName = null;
		//所在城市
		String city = null;
		//球队身价
		String teamPrice = null;
		// logo url
		String teamLogoUrl = null;
		//球队英文名字
		String team_name_en = null;
		//阵型
		String formation = null;
		
		MVsMapper mVsMapper = Main.applicationContext.getBean(MVsMapper.class);
		MVsExample mvsEx = new MVsExample();
		Criteria mvscriteria = mvsEx.createCriteria();
		String yesterday = UtilBag.dateUtil(-1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		mvscriteria.andEndTimeBetween(sdf.parse(yesterday), sdf.parse(sdf.format(new Date())));
		List<MVs> mVsList = mVsMapper.selectByExample(mvsEx);
		System.out.println(mVsList);
		//球队地址
		String team_url = null;
		
		MTeamMapper mteamMapper = Main.applicationContext.getBean(MTeamMapper.class);
		for(MVs mvs : mVsList) {
			vsId = mvs.getIdVs();
			team_url = "http://liansai.500.com/team/aaa/";
			Document homeDocument = UtilBag.getDocumentByUrl(team_url.replace("aaa", mvs.getHomeUrlId()));
			if(ObjectHelper.isNotEmpty(homeDocument)) {
				String teamId = null;
				try {
					//判断能否入库
					MTeamExample mteamEx = new MTeamExample();
					com.kprd.liansai.pojo.MTeamExample.Criteria criteria = mteamEx.createCriteria();
					criteria.andUrlTeamEqualTo(mvs.getHomeUrlId());
					List<MTeam> teamList = mteamMapper.selectByExample(mteamEx);
					
					if(teamList.size() == 0) {
						//球队名字
						String teamName = "";
						Elements h2s = homeDocument.select("h2");
						if(null != h2s && h2s.size() > 0) {
							for(Element h2 : h2s) {
								if(h2.attr("class").indexOf("lsnav_qdnav_name") > -1) {
									teamName = h2.text().trim();
								}
							}
						}
						Elements nameDivs = homeDocument.select(".itm_logo");
						if(null != nameDivs && nameDivs.size() > 0) {
							Element nameDiv = nameDivs.get(0);
							teamLogoUrl = nameDiv.select("img").get(0).attr("src").trim();
							if(teamLogoUrl.indexOf("notfound") > -1) {
								//如果网上没有相关照片待定
							} else {
								//下载logo
//								String downloadPath = imgPath + "\\" + teamName + "\\logo" + ".jpg";
//								UtilBag.downloadPicture(teamLogoUrl, downloadPath);
								//球队英文名字
								team_name_en = nameDiv.select(".itm_name_en").text().trim();
							}
						}
						//球队详情
						Elements elements1 = homeDocument.select(".itm_bd");
						if(null != elements1 && elements1.size() == 1) {
							Elements trs1 = elements1.get(0).select("table tbody tr");
							if(null != trs1 && trs1.size() > 0) {
								for(int i=0;i<trs1.size();i++) {
									if(i==0) {
										Elements tds = trs1.get(i).children();
										//网站有可能数据不全，导致数组越界，必须先判断
										if(null != tds && tds.size() > 0) {
											if(tds.get(0).text().split("：").length < 2) {
												estTime = "";
											} else {
												estTime = tds.get(0).text().split("：")[1].trim();
											}
											if(tds.get(1).text().split("：").length < 2) {
												capacity = "";
											} else {
												capacity = tds.get(1).text().split("：")[1].trim();
											}
										}
									} else if(i==1) {
										Elements tds = trs1.get(i).children();
										if(tds.get(0).text().split("：").length < 2) {
											nation = "";
										} else {
											nation = tds.get(0).text().split("：")[1].trim();
										}
										if(tds.get(1).text().split("：").length < 2) {
											courtName = "";
										} else {
											courtName = tds.get(1).text().split("：")[1].trim();
										}
									} else if(i==2) {
										Elements tds = trs1.get(i).children();
										if(tds.get(0).text().split("：").length < 2) {
											city = "";
										} else {
											city = tds.get(0).text().split("：")[1].trim();
										}
										if(tds.get(1).text().split("：").length < 2) {
											teamPrice = "";
										} else {
											teamPrice = tds.get(1).text().split("：")[1].trim();
										}
									}
								}
								
								Elements divs = homeDocument.select(".lqiudzr_r");
								//球员信息url集合
								List<String> playerUrlList = new ArrayList<String>();
								for(Element div : divs) {
									if(div.attr("class").indexOf("lqiudzr_r lqiudmd") > -1) {
										//获取所有守门员
										Element keeper = div.select(".itm_smy").get(0);
										Elements dds = keeper.select("dd");
										if(null != dds && dds.size() > 0) {
											for(Element dd : dds) {
												String keeperInfoUrl = dd.select("a").get(0).attr("href");
												playerUrlList.add(keeperInfoUrl);
											}
										}
										//获取后卫
										Element guard = div.select(".itm_hw").get(0);
										Elements guards = guard.select("dd");
										if(null != guards && guards.size() > 0) {
											for(Element hw : guards) {
												String guardInfoUrl = hw.select("a").get(0).attr("href");
												playerUrlList.add(guardInfoUrl);
											}
										}
										//获取中场
										Element mid = div.select(".itm_zc").get(0);
										Elements mids = mid.select("dd");
										if(null != mids && mids.size() > 0) {
											for(Element zc : mids) {
												String midInfoUrl = zc.select("a").get(0).attr("href");
												playerUrlList.add(midInfoUrl);
											}
										}
										//获取前锋
										Element forward = div.select(".itm_qf").get(0);
										Elements qfs = forward.select("dd");
										if(null != qfs && qfs.size() > 0) {
											for(Element qf : qfs) {
												String forwardInfoUrl = qf.select("a").get(0).attr("href");
												playerUrlList.add(forwardInfoUrl);
											}
										}
									}
								}
								logger.info("开始下载球队=====" + teamName + "信息");
//								downLoadPlayerImgs(playerUrlList,teamName,mvs);
//								getPlayerInfo(playerUrlList);
								}
							}
								//阵型
								Elements zhenxingDiv = homeDocument.select(".lzhenrong_zx");
								if(null != zhenxingDiv && zhenxingDiv.size() > 0) {
									formation = zhenxingDiv.get(0).text().split("：")[1].trim();
						    		System.out.println("阵型=====》" + formation);
								}
								
								String team_nickname = null;
								Elements tds = homeDocument.select(".lcur_chart_zj");
								if(null != tds && tds.size() > 0) {
									team_nickname = tds.get(0).select("p b").text();
								}
								
								MTeam t = new MTeam();
								t.setLeagueBelong(mvs.getGameTypeId());
								t.setIdTeam(IDUtils.createUUId());
								t.setShortnameTeam(team_nickname);
								t.setFullnameTeam(teamName);
								t.setUrlTeam(mvs.getHomeUrlId());
								teamId = t.getUrlTeam();
								t.setLogoTeamUrl(teamLogoUrl);
								t.setEnNameTeam(team_name_en);
								t.setEsttimeTeam(estTime);
								t.setCapacity(capacity);
								t.setNation(nation);
								t.setCourtname(courtName);
								t.setCity(city);
								t.setTeamprice(teamPrice);
								t.setFormation(formation);
								t.setLeagueBelong(mvs.getGameTypeId());
								mteamMapper.insert(t);
					}
				} catch (Exception e) {
					logger.error("异常球队id " + teamId);
					logger.error(e);
					e.printStackTrace();
				}
			}
			
			String teamId = null;
			team_url = "http://liansai.500.com/team/aaa/";
			Document awayDocument = UtilBag.getDocumentByUrl(team_url.replace("aaa", mvs.getAwayUrlId()));
			if(ObjectHelper.isNotEmpty(awayDocument)) {
				try {
					//判断能否入库
					MTeamExample mteamEx = new MTeamExample();
					com.kprd.liansai.pojo.MTeamExample.Criteria criteria = mteamEx.createCriteria();
					criteria.andUrlTeamEqualTo(mvs.getAwayUrlId());
					List<MTeam> teamList = mteamMapper.selectByExample(mteamEx);
					if(teamList.size() == 0) {
						//球队名字
						String teamName = "";
						Elements h2s = awayDocument.select("h2");
						if(null != h2s && h2s.size() > 0) {
							for(Element h2 : h2s) {
								if(h2.attr("class").indexOf("lsnav_qdnav_name") > -1) {
									teamName = h2.text().trim();
								}
							}
						}
						Elements nameDivs = awayDocument.select(".itm_logo");
						if(null != nameDivs && nameDivs.size() > 0) {
							Element nameDiv = nameDivs.get(0);
							teamLogoUrl = nameDiv.select("img").get(0).attr("src").trim();
							if(teamLogoUrl.indexOf("notfound") > -1) {
								//如果网上没有相关照片待定
							} else {
								//下载logo
//								String downloadPath = imgPath + "\\" + teamName + "\\logo" + ".jpg";
//								UtilBag.downloadPicture(teamLogoUrl, downloadPath);
								//球队英文名字
								team_name_en = nameDiv.select(".itm_name_en").text().trim();
							}
						}
						//球队详情
						Elements elements1 = awayDocument.select(".itm_bd");
						if(null != elements1 && elements1.size() == 1) {
							Elements trs1 = elements1.get(0).select("table tbody tr");
							if(null != trs1 && trs1.size() > 0) {
								for(int i=0;i<trs1.size();i++) {
									if(i==0) {
										Elements tds = trs1.get(i).children();
										//网站有可能数据不全，导致数组越界，必须先判断
										if(null != tds && tds.size() > 0) {
											if(tds.get(0).text().split("：").length < 2) {
												estTime = "";
											} else {
												estTime = tds.get(0).text().split("：")[1].trim();
											}
											if(tds.get(1).text().split("：").length < 2) {
												capacity = "";
											} else {
												capacity = tds.get(1).text().split("：")[1].trim();
											}
										}
									} else if(i==1) {
										Elements tds = trs1.get(i).children();
										if(tds.get(0).text().split("：").length < 2) {
											nation = "";
										} else {
											nation = tds.get(0).text().split("：")[1].trim();
										}
										if(tds.get(1).text().split("：").length < 2) {
											courtName = "";
										} else {
											courtName = tds.get(1).text().split("：")[1].trim();
										}
									} else if(i==2) {
										Elements tds = trs1.get(i).children();
										if(tds.get(0).text().split("：").length < 2) {
											city = "";
										} else {
											city = tds.get(0).text().split("：")[1].trim();
										}
										if(tds.get(1).text().split("：").length < 2) {
											teamPrice = "";
										} else {
											teamPrice = tds.get(1).text().split("：")[1].trim();
										}
									}
								}
								
								Elements divs = awayDocument.select(".lqiudzr_r");
								//球员信息url集合
								List<String> playerUrlList = new ArrayList<String>();
								for(Element div : divs) {
									if(div.attr("class").indexOf("lqiudzr_r lqiudmd") > -1) {
										//获取所有守门员
										Element keeper = div.select(".itm_smy").get(0);
										Elements dds = keeper.select("dd");
										if(null != dds && dds.size() > 0) {
											for(Element dd : dds) {
												String keeperInfoUrl = dd.select("a").get(0).attr("href");
												playerUrlList.add(keeperInfoUrl);
											}
										}
										//获取后卫
										Element guard = div.select(".itm_hw").get(0);
										Elements guards = guard.select("dd");
										if(null != guards && guards.size() > 0) {
											for(Element hw : guards) {
												String guardInfoUrl = hw.select("a").get(0).attr("href");
												playerUrlList.add(guardInfoUrl);
											}
										}
										//获取中场
										Element mid = div.select(".itm_zc").get(0);
										Elements mids = mid.select("dd");
										if(null != mids && mids.size() > 0) {
											for(Element zc : mids) {
												String midInfoUrl = zc.select("a").get(0).attr("href");
												playerUrlList.add(midInfoUrl);
											}
										}
										//获取前锋
										Element forward = div.select(".itm_qf").get(0);
										Elements qfs = forward.select("dd");
										if(null != qfs && qfs.size() > 0) {
											for(Element qf : qfs) {
												String forwardInfoUrl = qf.select("a").get(0).attr("href");
												playerUrlList.add(forwardInfoUrl);
											}
										}
									}
								}
//								logger.info("开始下载球队=====" + teamName + "信息");
//								downLoadPlayerImgs(playerUrlList,teamName,mvs);
								}
							}
								//阵型
								Elements zhenxingDiv = awayDocument.select(".lzhenrong_zx");
								if(null != zhenxingDiv && zhenxingDiv.size() > 0) {
									formation = zhenxingDiv.get(0).text().split("：")[1].trim();
						    		System.out.println("阵型=====》" + formation);
								}
								
								String team_nickname = null;
								Elements tds = awayDocument.select(".lcur_chart_zj");
								if(null != tds && tds.size() > 0) {
									team_nickname = tds.get(0).select("p b").text();
								}
								
								MTeam t = new MTeam();
								t.setLeagueBelong(mvs.getGameTypeId());
								t.setIdTeam(IDUtils.createUUId());
								t.setShortnameTeam(team_nickname);
								t.setFullnameTeam(teamName);
								t.setUrlTeam(mvs.getAwayUrlId());
								teamId = t.getUrlTeam();
								t.setLogoTeamUrl(teamLogoUrl);
								t.setEnNameTeam(team_name_en);
								t.setEsttimeTeam(estTime);
								t.setCapacity(capacity);
								t.setNation(nation);
								t.setCourtname(courtName);
								t.setCity(city);
								t.setTeamprice(teamPrice);
								t.setFormation(formation);
								t.setLeagueBelong(mvs.getGameTypeId());
								mteamMapper.insert(t);
					}
				
				} catch (Exception e) {
					logger.error("异常球队id " + teamId);
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
		
	} catch (Exception e) {
		logger.error(vsId + "getTeamInfo异常：");
		logger.error(e);
		e.printStackTrace();
	}
}
	/**
	 * 通过球员详情页面下载球员照片
	 */
	private void downLoadPlayerImgs(List<String> playerUrlList,String teamName,MVs mvs) {
		for(String url : playerUrlList) {
			Document doc = UtilBag.getDocumentByUrl(url);
			if(null != doc) {
				Elements divs = doc.select(".lcontent");
				if(null != divs && divs.size() > 0) {
					Elements names = divs.get(0).select(".itm_name");
					//球员中文名，用来下载图片命名时用
					String playerName = "";
					if(null != names && names.size() > 0) {
						playerName = names.get(0).text().trim();
					}
					Elements imgs = divs.get(0).select("img");
					if(null != imgs && imgs.size() > 0) {
						String imgUrl = imgs.get(0).attr("src").trim();
						if(imgUrl.indexOf("notfound") > -1) {
							//如果网上没有相关照片，待定
						} else {
							//开发用的下载路径
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							String downloadPath = imgPath + sdf.format(mvs.getStartTime()) + "\\" + teamName + "\\" + playerName  + ".jpg";
							UtilBag.downloadPicture(imgUrl, downloadPath);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取最终比分
	 * @param tr
	 */
	private static void getFinalScore(Element tr,MVs mvs) {
		logger.info("执行getFinalScore方法，获取最终比分");
		System.out.println("获取最终比分开始↓↓↓↓↓↓");
		try {
			Elements sps = tr.select("td a span");
			if(null != sps && sps.size() > 0) {
				if(sps.get(0).attr("class").indexOf("eng red") > -1) {
					String finalScore = sps.get(0).text().trim();
					System.out.println("最终比分===========》" + finalScore);
					mvs.setFinalScore(finalScore);
				}
			}
			
			//获取析的链接
			Elements anchos = tr.select("td > a");
			if(null != anchos && anchos.size() > 0) {
				for(Element a : anchos) {
					if(a.attr("href").indexOf("http://odds.500.com/fenxi/shuju-") > -1) {
						String href = a.attr("href").trim();
						href = href.split("-")[1];
						if(href.indexOf(".")==-1) {
							mvs.setXiid(href);
						} else {
							href = href.substring(0, href.indexOf("."));
							mvs.setXiid(href);
						}
					}
				}
			}
			
			//判断是否单关 1:是,0:否
			String dgactive = tr.attr("dgactive");
			dgactive = dgactive.replace("{", "").replace("}", "");
			String[] dgactives = dgactive.split(",");
			String spf = dgactives[0];
			String nspf = dgactives[dgactives.length-1];
			spf = spf.split(":")[1].replace("\"", "");
			nspf = nspf.split(":")[1].replace("\"", "");
			mvs.setSfpDanguan(nspf);
			mvs.setRqsfpDanguan(spf);
		} catch (Exception e) {
			logger.error(mvs.getZid() + "getFinalScore方法异常：");
			logger.error(e);
			e.printStackTrace();
		}
		System.out.println("获取最终比分结束↑↑↑↑↑↑");
	}
    
    /**
     * 当前混合过关页面
     * @param list 从xml中获取当天的tr的id
     */
    public static void getMixInfo(LinkedList<String> list,String dateStr,boolean flag) {
    	if(null != list) {
    		//新增计数器
    		int insert_count = 0;
    		MVsMapper mvsMapper = Main.applicationContext.getBean(MVsMapper.class);
        	String start_time = null;
        	try {
        		//获取混合过关页面对象
            	Document doc = null;
        		if(flag) {
        			doc = UtilBag.getDocumentByUrl(baseUrl);
        		} else {
        			doc = UtilBag.getDocumentByUrl(baseUrl + "&date=" + dateStr);
        		}
            	
//            	String html = UtilBag.readTxtFile("C:/Users/Administrator/Desktop/smallTool/code.txt");
//            	doc = Jsoup.parse(html);
        		
            	if(null != doc) {
            		for(String li : list) {
            			Elements trs = doc.select("tr");
            			if(null != trs && trs.size() > 0) {
            				for(Element tr : trs) {
            					if(li.equals(tr.attr("zid"))) {
            						//开赛时间
        	    					start_time = tr.select(".match_time").get(0).attr("title").split("：")[1].trim();
            						MVs mvs = new MVs();
            						//抓取比赛类型 开赛时间 截止时间 剩余时间
            						getGameTypeAndTime(tr,mvs);
            						//获取比分
    								getFinalScore(tr,mvs);
            						//通过析页面找到平均概率
            						getAvgRate(tr,mvs);
            						//抓取主客队名字，让球和球队详细信息
            						getTeamNameAndInfo(tr,mvs);
            						//获取 胜平负，让球胜平负，比分，总进球，全半场
            						get5Data(tr,mvs,li);
            						
            						MVsExample mvsEx = new MVsExample();
            						Criteria criteria = mvsEx.createCriteria();
            						criteria.andCcIdEqualTo(mvs.getCcId());
            						List<MVs> mvsList = mvsMapper.selectByExample(mvsEx);
            						
//            						try {
//            							getXiEuroAsia(mvs);
//            							System.out.println("分析数据抓取完毕");
//									} catch (Exception e) {
//										if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
//												|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1) {
//											getXiEuroAsia(mvs);
//										}
//									}
            						
//            						try {
//            							oddsHandler(mvs);
//									} catch (Exception e) {
//										if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
//												|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
//												|| e.getMessage().indexOf("Read timed out") > -1) {
//											try {
//												Thread.sleep(60*1000*2);
//											} catch (Exception e1) {
//												oddsHandler(mvs);
//											}
//										} else {
//											logger.error("赔率抓取出错，停止！",e);
//											System.out.println(e.getMessage());
//											System.out.println(e.getStackTrace());
//											logger.error(mvs.getZid());
//										System.exit(0);
//										}
//									}
        		                	JedisUtilForFetch.remove(Contant.JEDIS_SIX,mvs.getXiid() +" euro");
        		                	System.out.println("欧佩缓存已删除***********************eu**********************************");
        		                	JedisUtilForFetch.remove(Contant.JEDIS_SIX,mvs.getXiid() +"asia");
        		                	System.out.println("压盘缓存已删除***********************eu**********************************");
            						
            						if(ObjectHelper.isEmpty(dateStr)) {
            							String kyo = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", "");
            		                	JedisUtilForFetch.remove(Contant.JEDIS_SIX,kyo + "gamecenter");
            		                	System.out.println("今日缓存已删除***********************kyo**********************************");
            						} else {
            							String kino = dateStr;
            		                    kino = kino.replace("-", "");
            		            	    JedisUtilForFetch.remove(Contant.JEDIS_SIX,kino);
            		            	    System.out.println("昨日缓存已删除***********************kino**********************************");
            						}
//            						Thread.sleep(60*1000*2); //本地测试就不沉睡
            						if(mvsList.size() == 0) {
            							mvs.setIdVs(IDUtils.createUUId());//抓取当天的对阵信息时，不存zid，在历史的时候再次获取
            							mvs.setZid(li);
            							logger.info("添加对阵信息，对阵信息id：" + mvs.getIdVs());
            							mvsMapper.insert(mvs);
            							insert_count++;
            							break;
            						} else {
            							String id = mvsList.get(0).getIdVs();
            							mvs.setIdVs(id);
            							logger.info("更新对阵信息，对阵信息id：" + mvs.getIdVs());
            							mvs.setZid(li);
            							mvsMapper.updateByPrimaryKey(mvs);
            							break;
            						}
            					}
            				}
            			}
            		}
            	}
    		} catch (Exception e) {
    			logger.error(e);
    			e.printStackTrace();
    		}
        	logger.info("xml中有" + list.size() + "条数据");
        	logger.info("新增" + insert_count + "条数据。");
    	} else {
    		logger.info("当天xml list为空，当天没有数据");
    	}
    }
    
    public static void getXiEuroAsia(MVs mvs) {
    	AnalysisFetch.getAnalysis(mvs);
    	AnalysisFetch.getEuro(mvs.getXiid());
    	AnalysisFetch.getAsia(mvs.getXiid());
    }
    
    /**
     * 处理赔率
     * @param mvs
     * @return
     */
//    private static MVs oddsHandler(MVs mvs) {
//    	//封装百家平均
//		Europodds eu = Europei.getEuroOdds(mvs);
//		if(ObjectHelper.isNotEmpty(eu) && ObjectHelper.isNotEmpty(eu.getJsops())
//				 && ObjectHelper.isNotEmpty(eu.getJsopp())  && ObjectHelper.isNotEmpty(eu.getJsopf())) {
//			mvs.setEurops(eu.getJsops());
//			mvs.setEuropp(eu.getJsopp());
//			mvs.setEuropf(eu.getJsopf());
//		}
//		
//		//封装即使凯利
//		if(ObjectHelper.isNotEmpty(eu) && ObjectHelper.isNotEmpty(eu.getJskailis())
//				 && ObjectHelper.isNotEmpty(eu.getJskailip())  && ObjectHelper.isNotEmpty(eu.getJskailif())) {
//			mvs.setKailis(eu.getJskailis());
//			mvs.setKailip(eu.getJskailip());
//			mvs.setKailif(eu.getJskailif());
//		}
//		
//		//封装亚盘指数
//		Asiaodds asia = Asiapei.getAisaOdds(mvs);
//		if(ObjectHelper.isNotEmpty(asia) && ObjectHelper.isNotEmpty(asia.getJspkhome()) 
//				 && ObjectHelper.isNotEmpty(asia.getJspan())  && ObjectHelper.isNotEmpty(asia.getJspkaway())) {
//			mvs.setAisas(asia.getJspkhome());
//			mvs.setAsiap(asia.getJspan());
//			mvs.setAsiaf(asia.getJspkaway());
//		}
//		
//		Bigsmall bs = BigSmall.getBigSmall(mvs);
//		return mvs;
//    }
    
    public static void main(String[] args) {
    	long startTime = System.currentTimeMillis();
		FetchMixToday today = new FetchMixToday();
		//true:当天，false：昨天
		today.getZids(true);
//		today.getTeamInfo();
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		System.out.println("程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
	}
    
}
