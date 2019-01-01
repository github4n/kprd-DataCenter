package com.kprd.date.fetch.wubai.football;

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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;
import com.kprd.newliansai.pojo.FoMixExample.Criteria;

public class WubaiHistory {
	//混合过关初始地址
		private static String baseUrl = "http://trade.500.com/jczq/index.php?playid=312"; 
		
		static FoMixMapper mVsMapper = Main.applicationContext.getBean(FoMixMapper.class);
		/**
		 * 为odds地址处理日期
		 */
		public static String dateStuff(Integer day) {
			String result = null;
			try {
				Date date = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, day);
				date = calendar.getTime();
				SimpleDateFormat df = new SimpleDateFormat("yyyy@MM-dd");//设置日期格式
				String dateStr = df.format(date);// new Date()为获取当前系统时间
				String year = dateStr.split("@")[0];
				String md = dateStr.split("@")[1].replace("-", "");
				dateStr =  year + "," + md;
				result = dateStr;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
//		private String tranStr(String str) {
//			String[] strs = str.split("-");
//			str = null;
//			str = strs[0] + "," + strs[1] + strs[2];
//			System.out.println(str);
//			return str;
//		}
		
		/**
		 * 获取时间段相差的天数
		 * @param d1
		 * @param d2
		 * @return
		 */
		private static Integer getDays(String d1, String d2) {
			Integer days = null;
			try {
				if(ObjectHelper.isNotEmpty(d2)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					//开始日期
					Date start = sdf.parse(d1);
					//截止日期
					Date end = sdf.parse(d2);
					//相差天数(算上当天需要+1天)
					days = (int)((start.getTime() - end.getTime())/86400000) + 1;
				} else {
					days = 1;
				}
				System.out.println(days);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return days;
		}
		
		private static List<String> getDaysList(String startDate,Integer day) {
			List<String> list = new ArrayList<String>();
			for(int i=0;i<day;i++) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = sdf.parse(startDate);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					calendar.add(Calendar.DAY_OF_MONTH, -i);
					date = calendar.getTime();
					String dateStr = sdf.format(date);
					list.add(dateStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return list;
		}
		
		/**
		 * 获取odds.xml
		 * @param day
		 * @return
		 */
		public static LinkedList<String> getZids(String d1,String d2) {
			Integer days = null;
			fetchHistoryMixed(days,d1,d2);
			return null;
		}

		/**
		 * 获取全半场数据包括bingo
		 * @param theday
		 * @param li
		 * @return
		 */
		private static LinkedHashMap<String, String> halfData(String theday,String li,FoMix mvs) {
			//半场数据地址
			String halfUrl = "http://trade.500.com/jczq/index.php?playid=272&date=";
			halfUrl += theday;
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
												mvs.setQbcssbingo("1");
												mvs.setQbcss(value);
											} else if("31".equals(type)) {
												mvs.setQbcspbingo("1");
												mvs.setQbcsp(value);
											} else if("30".equals(type)) {
												mvs.setQbcsfbingo("1");
												mvs.setQbcsf(value);
											} else if("13".equals(type)) {
												mvs.setQbcpsbingo("1");
												mvs.setQbcps(value);
											} else if("11".equals(type)) {
												mvs.setQbcppbingo("1");
												mvs.setQbcpp(value);
											} else if("10".equals(type)) {
												mvs.setQbcpfbingo("1");
												mvs.setQbcpf(value);
											} else if("03".equals(type)) {
												mvs.setQbcfsbingo("1");
												mvs.setQbcfs(value);
											} else if("01".equals(type)) {
												mvs.setQbcfpbingo("1");
												mvs.setQbcfp(value);
											} else if("00".equals(type)) {
												mvs.setQbcffbingo("1");
												mvs.setQbcff(value);
											}
										} else {
											String type = span.attr("value").trim();
											String value = span.attr("data-sp").trim();
											map.put(type, value);
											if("33".equals(type)) {
												mvs.setQbcss(value);
											} else if("31".equals(type)) {
												mvs.setQbcsp(value);
											} else if("30".equals(type)) {
												mvs.setQbcsf(value);
											} else if("13".equals(type)) {
												mvs.setQbcps(value);
											} else if("11".equals(type)) {
												mvs.setQbcpp(value);
											} else if("10".equals(type)) {
												mvs.setQbcpf(value);
											} else if("03".equals(type)) {
												mvs.setQbcfs(value);
											} else if("01".equals(type)) {
												mvs.setQbcfp(value);
											} else if("00".equals(type)) {
												mvs.setQbcff(value);
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
			return map;
		}
		
		/**
		 * 抓取比赛类型
		 * 开赛时间
		 * 截止时间
		 * 剩余时间
		 * @param tr
		 */
		private static void getGameTypeAndTime(Element tr,FoMix mvs) {
			try {
				//.league_num 获取比赛类型（如：德甲，美洲杯）
				Elements divs = tr.select(".league_num");
				if(null != divs && divs.size() > 0) {
					Elements as = divs.get(0).select("a");
					if(null != as && as.size() > 0) {
						//赛事类型
			    		String gameType = as.get(1).text();
			    		mvs.setLeaguename(gameType);
					}
					//联赛颜色
					String style = divs.get(0).select("span").get(0).attr("style");
					if(style.indexOf("background:") > -1) {
						style = style.replace("background:", "").trim();
					}
					mvs.setLeaguecolor(style);
				}
				
				//获取拼装场次id
				String pdate = tr.attr("pdate");
				if(ObjectHelper.isNotEmpty(pdate)) {
					pdate = pdate.trim().replace("-", "");
					pdate += tr.attr("pname").trim();
					pdate = getIdUniquePartI(pdate);
					mvs.setIdunique(pdate);
				}
				
				//开赛时间
				String start_time = tr.select(".match_time").get(0).attr("title").split("：")[1].trim();
				//截止时间
				String end_time = tr.select(".end_time").get(0).attr("title").split("：")[1].trim();
				//剩余时间
//				String countdown_time = tr.select(".countdown_time").get(0).attr("data-endtime").trim();
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				mvs.setStarttime(simpleDateFormat.parse(start_time));
				mvs.setEndtime(simpleDateFormat.parse(end_time));
//				mvs.setCountdownTime(simpleDateFormat.parse(countdown_time));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 获取idUnique步骤
		 * @param str
		 * @return
		 */
		public static String getIdUniquePartI(String str) {
			String result = null;
			String s = str.substring(0,8);
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date da = sdf.parse(s);
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				s = sdf.format(da);
				String st = str.substring(8,s.length()-1);
				String last3 = str.substring(9,12);
				result = UtilBag.getRightDate(s, st);
				result = result.replace("-", "");
				result = result + st + last3;
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(result);
			return result;
		}
		
		/**
		 * 获取最终比分
		 * @param tr
		 */
		private static void getFinalScore(Element tr,FoMix mvs) {
			try {
				//当日看不到比分
				Elements sps = tr.select("td a span");
				if(null != sps && sps.size() > 0) {
					if(sps.get(0).attr("class").indexOf("eng red") > -1) {
						String finalScore = sps.get(0).text().trim();
						if(ObjectHelper.isNotEmpty(finalScore)) {
							mvs.setHomescore(finalScore.split(":")[0]);
							mvs.setAwayscore(finalScore.split(":")[1]);
						}
					}
				}
				
				//获取析的链接
				Elements anchos = tr.select("td > a");
				if(null != anchos && anchos.size() > 0) {
					for(Element a : anchos) {
						if(a.attr("href").indexOf("http://odds.500.com/fenxi/shuju-") > -1) {
							String href = a.attr("href").trim();
							href = href.split("-")[1];
							href = href.substring(0, href.indexOf("."));
							mvs.setWubaiid(href);
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
				mvs.setSfpdanguan(nspf);
				mvs.setRqsfpdanguan(spf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 抓取主客队名字，让球和球队详细信息
		 * @param tr
		 */
		private static void getTeamNameAndInfo(Element tr,FoMix mvs) {
			Elements uls = tr.select(".team_vs");
			
			Elements divsOne = tr.select(".league_num");
			if(null != divsOne && divsOne.size() > 0) {
				Elements as = divsOne.get(0).select("a");
				if(null != as && as.size() > 0) {
		    		String leagueId = as.get(1).attr("href").replace("/", "").split("-")[1];
		    		mvs.setLeagueid(leagueId);
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
							mvs.setHometeamid(team_url);
							String href = lis.get(i).select("a").get(0).attr("href");
							if(ObjectHelper.isNotEmpty(href)) {
								String[] teams = href.split("/");
								if(ObjectHelper.isNotEmpty(teams) && teams.length > 0) {
									mvs.setHometeamid(teams[teams.length-1]);
								}
							}
							mvs.setHomeshortname(lis.get(i).select("a").text());
							mvs.setHomefullname(lis.get(i).select("a").attr("title"));
						} else if(i==2) {//客队
							Element a = lis.get(i).select("a").get(0);
							//主队地址
							String team_url = a.attr("href").trim();
							
							String [] team_urls = team_url.replace("/", "").split("m");
							team_url = team_urls[team_urls.length-1];
							mvs.setAwayteamid(team_url);
							String href = lis.get(i).select("a").get(0).attr("href");
							if(ObjectHelper.isNotEmpty(href)) {
								String[] teams = href.split("/");
								if(ObjectHelper.isNotEmpty(teams) && teams.length > 0) {
									mvs.setAwayteamid(teams[teams.length-1]);
								}
							}
							mvs.setAwayshortname(lis.get(i).select("a").text());
							mvs.setAwayfullname(lis.get(i).select("a").attr("title"));
						}
					}
				}
			}
		}

		/**
	     * 获取 胜平负，让球胜平负，比分，总进球，全半场
	     * @param tr
	     */
	    private static void get5Data(Element tr,String theday,String li,FoMix mvs) {
			try {
				Elements dvs = tr.select("td > div");
				if(null != dvs && dvs.size() > 0) {
					for(int i=0;i<dvs.size();i++) {
						if((dvs.get(i).attr("class").indexOf("bet_odds") > -1 && dvs.get(i).attr("class").indexOf("bet_btns") > -1) || (dvs.get(i).attr("class").indexOf("bet_more") > -1)) {
							if(i==1) {//胜平负
								//(胜平负)是否中标标示，0:胜，1：平，2：负
								Elements spans = dvs.get(i).children();
								if(null != spans && spans.size() > 0 && spans.size() == 3) {
									//odds_bingo
									for(int j=0;j<spans.size();j++) {
										Elements bingoSpan = spans.get(j).select(".odds_bingo");
										if(j==0) {
											//检测是否中标（odds_bingo为红色的样式class）
											if(null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setSpfsstatus("1");
											}
											String basic_spf_win = spans.get(j).select(".odds_item").attr("data-sp").trim();
											mvs.setSpfs(basic_spf_win);
										} else if(j==1) {
											//检测是否中标（odds_bingo为红色的样式class）
											if(null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setSpfpstatus("1");
											}
											String basic_spf_draw = spans.get(j).select(".odds_item").attr("data-sp").trim();
											mvs.setSpfp(basic_spf_draw);
										} else if(j==2) {
											//检测是否中标（odds_bingo为红色的样式class）
											if(null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setSpffstatus("1");
											}
											String basic_spf_lose = spans.get(j).select(".odds_item").attr("data-sp").trim();
											mvs.setSpff(basic_spf_lose);
										}
									}
								} else if(null != spans && spans.size() > 0 && spans.size() == 1) {
									//未开售的情况先暂时不管
								}
							} else if(i==2) {//让球胜平负
								//(让球胜平负)是否中标标示，0:胜，1：平，2：负
								Elements spans = dvs.get(i).select(".odds_item");
								if(null != spans && spans.size() > 0) {
									for(int j=0;j<spans.size();j++) {
										if(spans.get(j).attr("class").indexOf("odds_item") > -1) {
											Elements bingoSpan = spans.get(j).select(".odds_bingo");
											if(j==0) {//让球胜
												if(null != bingoSpan && bingoSpan.size() > 0) {
													mvs.setRqspfsstatus("1");
												}
												String rqs = spans.get(j).attr("data-sp").trim();
												mvs.setRqspfs(rqs);
											} else if(j==1) {//让球平
												if(null != bingoSpan && bingoSpan.size() > 0) {
													mvs.setRqspfpstatus("1");
												}
												String rqp = spans.get(j).attr("data-sp").trim();
												mvs.setRqspfp(rqp);
											} else if(j==2) {//让球负
												if(null != bingoSpan && bingoSpan.size() > 0) {
													mvs.setRqspffstatus("1");
												}
												String rqf = spans.get(j).attr("data-sp").trim();
												mvs.setRqspff(rqf);
											}
										}
									}
								}
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
				e.printStackTrace();
			}
	    }
		
	    /**
		 * 全半场纯数据，没有bingo
		 * @param longData
		 * @param mvs
		 */
		private static void getQbc(String longData,FoMix mvs) {
			String[] step1 = longData.split(",");
			for(int i=0;i<step1.length;i++) { 
	    		if(i==0) {
	    			mvs.setQbcss(step1[i].split("\\|")[1]);
	    		} else if(i==1) {
	    			mvs.setQbcsp(step1[i].split("\\|")[1]);
	    		} else if(i==2) {
	    			mvs.setQbcsf(step1[i].split("\\|")[1]);
	    		} else if(i==3) {
	    			mvs.setQbcps(step1[i].split("\\|")[1]);
	    		} else if(i==4) {
	    			mvs.setQbcpp(step1[i].split("\\|")[1]);
	    		} else if(i==5) {
	    			mvs.setQbcpf(step1[i].split("\\|")[1]);
	    		} else if(i==6) {
	    			mvs.setQbcfs(step1[i].split("\\|")[1]);
	    		} else if(i==7) {
	    			mvs.setQbcfp(step1[i].split("\\|")[1]);
	    		} else if(i==8) {
	    			mvs.setQbcff(step1[i].split("\\|")[1]);
	    		}
	    	}
		}
		
		/**
	     * 总进球或者全半场弹窗数据
	     * @param longData
	     */
	    private static void getTotalGoalOrQbc(String longData,FoMix mvs) {
	    	String[] step1 = longData.split(",");
	    	
	    	if(ObjectHelper.isNotEmpty(mvs.getHomescore()) && ObjectHelper.isNotEmpty(mvs.getAwayscore())) {
	    		int homeScore = Integer.valueOf(mvs.getHomescore());
	    		int awayScore = Integer.valueOf(mvs.getAwayscore());
	        	//比分之和
	        	int sum = homeScore + awayScore;
	        	for(int i=0;i<step1.length;i++) { 
	        		if(i==0) {
	        			if(i==sum) {
	        				mvs.setZjq0bingo("1");
	        			}
	        			mvs.setZjq0(step1[i].split("\\|")[1]);
	        		} else if(i==1) {
	        			if(i==sum) {
	        				mvs.setZjq1bingo("1");
	        			}
	        			mvs.setZjq1(step1[i].split("\\|")[1]);
	        		} else if(i==2) {
	        			if(i==sum) {
	        				mvs.setZjq2bingo("1");
	        			}
	        			mvs.setZjq2(step1[i].split("\\|")[1]);
	        		} else if(i==3) {
	        			if(i==sum) {
	        				mvs.setZjq3bingo("1");
	        			}
	        			mvs.setZjq3(step1[i].split("\\|")[1]);
	        		} else if(i==4) {
	        			if(i==sum) {
	        				mvs.setZjq4bingo("1");
	        			}
	        			mvs.setZjq4(step1[i].split("\\|")[1]);
	        		} else if(i==5) {
	        			if(i==sum) {
	        				mvs.setZjq5bingo("1");
	        			}
	        			mvs.setZjq5(step1[i].split("\\|")[1]);
	        		} else if(i==6) {
	        			if(i==sum) {
	        				mvs.setZjq6bingo("1");
	        			}
	        			mvs.setZjq6(step1[i].split("\\|")[1]);
	        		} else if(i==7) {
	        			if(i==sum) {
	        				mvs.setZjq7bingo("1");
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
	    	private static void getScoreData(String longData, FoMix mvs) {
	    	
	    	LinkedHashMap<String, String> bfLists = new LinkedHashMap<String, String>();
	    	LinkedHashMap<String, String> bfListp = new LinkedHashMap<String, String>();
	    	LinkedHashMap<String, String> bfListpf = new LinkedHashMap<String, String>();
	    	
			String[] longs = longData.split("A");
			
			for(int i=1;i<longs.length;i++) {
				if(i==1) {//胜其他
					String step1 = longs[i].substring(0, longs[i].length()-1);
					String sqtVal = longs[i];
					sqtVal = sqtVal.substring(1, step1.indexOf(","));
					mvs.setScorewinother(sqtVal);
					step1 = step1.substring(step1.indexOf(",") + 1);
					
					String[] step2s = step1.split(",");
					for(String st : step2s) {
						bfLists.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
					}
				} else if(i==2) {//平其他
					String step1 = longs[i].substring(0, longs[i].length()-1);
					String pqtVal = longs[i];
					pqtVal = pqtVal.substring(1, step1.indexOf(","));
					mvs.setScoredrawother(pqtVal);
					step1 = step1.substring(step1.indexOf(",") + 1);
					
					String[] step2s = step1.split(",");
					for(String st : step2s) {
						bfListp.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
					}
				} else if(i==3) {//负其他
					String step1 = longs[i].substring(0, longs[i].length());
					String fqtVal = longs[i];
					fqtVal = fqtVal.substring(1, step1.indexOf(","));
					mvs.setScoreloseother(fqtVal);
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
	    }
	    
	    	/**
	    	 * 封装负其他
	    	 * @param bfListpf
	    	 * @param mvs
	    	 */
	    	@SuppressWarnings("rawtypes")
	    	private static void sealedBfListf(LinkedHashMap<String, String> bfListpf,FoMix mvs) {
	    		Iterator<Entry<String, String>> iter = bfListpf.entrySet().iterator();
	    		String homeScore = mvs.getHomescore();
	    		String awayScore = mvs.getAwayscore();
	    		String finalScore = homeScore + awayScore;
	    		if(ObjectHelper.isNotEmpty(finalScore)) {
	    			while(iter.hasNext()) {
	    				Map.Entry entry = (Map.Entry)iter.next();
	    				if("01".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore01bingo("1");
	    					}
	    					mvs.setScore01((String)entry.getValue());
	    				} else if("02".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore02bingo("1");
	    					}
	    					mvs.setScore02((String)entry.getValue());
	    				} else if("12".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore12bingo("1");
	    					}
	    					mvs.setScore12((String)entry.getValue());
	    				} else if("03".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore03bingo("1");
	    					}
	    					mvs.setScore03((String)entry.getValue());
	    				} else if("13".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore13bingo("1");
	    					}
	    					mvs.setScore13((String)entry.getValue());
	    				} else if("23".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore23bingo("1");
	    					}
	    					mvs.setScore23((String)entry.getValue());
	    				} else if("04".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore04bingo("1");
	    					}
	    					mvs.setScore04((String)entry.getValue());
	    				} else if("14".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore14bingo("1");
	    					}
	    					mvs.setScore14((String)entry.getValue());
	    				} else if("24".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore24bingo("1");
	    					}
	    					mvs.setScore24((String)entry.getValue());
	    				} else if("05".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore05bingo("1");
	    					}
	    					mvs.setScore05((String)entry.getValue());
	    				} else if("15".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore15bingo("1");
	    					}
	    					mvs.setScore15((String)entry.getValue());
	    				} else if("25".equals((String)entry.getKey())) {
	    					if(((String)entry.getKey()).equals(finalScore)) {
	    						mvs.setScore25bingo("1");
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
	    private static void sealedBfListp(LinkedHashMap<String, String> zjqList,FoMix mvs) {
	    	Iterator<Entry<String, String>> iter = zjqList.entrySet().iterator();
	    	String homeScore = mvs.getHomescore();
	    	String awayScore = mvs.getAwayscore();
	    	String finalScore = homeScore + awayScore;
	    	if(ObjectHelper.isNotEmpty(finalScore)) {
	    		while(iter.hasNext()) {
	    			Map.Entry entry = (Map.Entry)iter.next();
	    			if("00".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore00bingo("1");
	    				}
	    				mvs.setScore00((String)entry.getValue());
	    			} else if("11".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore11bingo("1");
	    				}
	    				mvs.setScore11((String)entry.getValue());
	    			} else if("22".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore22bingo("1");
	    				}
	    				mvs.setScore22((String)entry.getValue());
	    			} else if("33".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore33bingo("1");
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
	    private static void sealedBfLists(LinkedHashMap<String, String> bfList,FoMix mvs) {
	    	
	    	Iterator<Entry<String, String>> iter = bfList.entrySet().iterator();
	    	String homeScore = mvs.getHomescore();
	    	String awayScore = mvs.getAwayscore();
	    	String finalScore = homeScore + awayScore;
	    	if(ObjectHelper.isNotEmpty(finalScore)) {
	    		while(iter.hasNext()) {
	    			Map.Entry entry = (Map.Entry)iter.next();
	    			if("10".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore10bingo("1");
	    				}
	    				mvs.setScore10((String)entry.getValue());
	    			} else if("20".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore20bingo("1");
	    				}
	    				mvs.setScore20((String)entry.getValue());
	    			} else if("21".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore21bingo("1");
	    				}
	    				mvs.setScore21((String)entry.getValue());
	    			} else if("30".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore30bingo("1");
	    				}
	    				mvs.setScore30((String)entry.getValue());
	    			} else if("31".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore31bingo("1");
	    				}
	    				mvs.setScore31((String)entry.getValue());
	    			} else if("32".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore32bingo("1");
	    				}
	    				mvs.setScore32((String)entry.getValue());
	    			} else if("40".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore40bingo("1");
	    				}
	    				mvs.setScore40((String)entry.getValue());
	    			} else if("41".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore41bingo("1");
	    				}
	    				mvs.setScore41((String)entry.getValue());
	    			} else if("42".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore42bingo("1");
	    				}
	    				mvs.setScore42((String)entry.getValue());
	    			} else if("50".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore50bingo("1");
	    				}
	    				mvs.setScore50((String)entry.getValue());
	    			} else if("51".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore51bingo("1");
	    				}
	    				mvs.setScore51((String)entry.getValue());
	    			} else if("52".equals((String)entry.getKey())) {
	    				if(((String)entry.getKey()).equals(finalScore)) {
	    					mvs.setScore52bingo("1");
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
	     * 通过析页面找到平均概率
	     * @param url
	     * @return
	     */
//	    private String getAvgRate(Element trParam,MVs mvs) {
//	    	System.out.println("通过析页面找到平均概率开始↓↓↓↓↓↓");
//	    	//析
//			String analysisUrl = "";
//			StringBuffer result = new StringBuffer();
//			//通过析获取平均概率
//			Elements tdsx = trParam.select("td");
//			if(null != tdsx && tdsx.size() > 0) {
//				if(tdsx.get(3).text().indexOf("未开售") >= -1) {
//					
//				} else {
//					analysisUrl = tdsx.get(3).select("a").get(0).attr("href").trim();
//					analysisUrl = analysisUrl.replace("shuju", "ouzhi");
//			    	CloseableHttpClient httpclient = HttpClients.createDefault();
//			    	try {
//			    		System.out.println("通过析页面找到平均概率,请求路径：" + analysisUrl);
//			    		// 创建httpget.    
//			            HttpGet httpget = new HttpGet(analysisUrl);  
//			            System.out.println("executing request " + httpget.getURI());  
//			            httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//			            httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//			            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//			            httpget.setHeader("Host","odds.500.com");
//			            // 执行get请求.    
//			            CloseableHttpResponse response = httpclient.execute(httpget);
//			            try {
//			            	// 获取响应实体    
//			                HttpEntity entity = response.getEntity();  
//			                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
//			                	String html = EntityUtils.toString(entity, "gb2312");
////			                   UtilBag.toFile(html.getBytes(), new File("C:\\Users\\Administrator\\Desktop\\fetch\\123.txt"));
//			                   Document doc = Jsoup.parse(html);
//			                   if(null != doc) {
//			                	   Elements divs = doc.select("#table_btm");
//			                	   if(null != divs && divs.size() > 0) {
//			                		   Elements trs = divs.get(0).select("table tr");
//			                		   if(null != trs && trs.size() > 0) {
//			                			   for(Element tr : trs) {
//			                				   //定位到<tr xls="footer">
//			                				   if(tr.attr("xls").trim().indexOf("footer") > -1) {
//			                					   Elements tables = tr.select("table");
//			                					   if(null != tables && tables.size() > 0) {
//			                						   Element avgTable = tables.get(1);
//			                						   Elements trs2 = avgTable.select("tbody tr");
//			                						   if(null != trs2 && trs2.size() > 0) {
//			                							   Elements tds = trs2.get(1).children();
//			                							   if(null != tds && tds.size() > 0) {
//			                								   String avgwin = "";
//			                								   String avgdraw = "";
//			                								   String avglost = "";
//			                								   for(Element td : tds) {
//			                									   //胜平均
//			                									   if(td.attr("id").indexOf("avwinlj2") > -1) {
//			                										   avgwin = td.text().trim();
//			                										   mvs.setAvgrateWin(avgwin);
//			                									   }
//			                									   //平平均
//			                									   if(td.attr("id").indexOf("avdrawlj2") > -1) {
//			                										   avgdraw = td.text().trim();
//			                										   mvs.setAvgrateDraw(avgdraw);
//			                									   }
//			                									   //负平均
//			                									   if(td.attr("id").indexOf("avlostlj2") > -1) {
//			                										   avglost = td.text().trim();
//			                										   mvs.setAvgrateLose(avglost);
//			                									   }
//			                								   }
//			                								  result.append(avgwin + ",").append(avgdraw + ",").append(avglost);
//			                								  System.out.println("平均概率 " + result.toString());
//			                								  System.out.println("通过析页面找到平均概率结束↑↑↑↑↑↑");
//			                								  return result.toString();
//			                							   }
//			                						   }
//			                					   }
//			                				   }
//			                			   }
//			                		   }
//			                	   }
//			                   }
//			                }  
//						} finally {
//							response.close();
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//	    	return result.toString();
//	    }
	    
	    /**
		 * 通过球员详情页面下载球员照片
		 */
//		private void downLoadPlayerImgs(List<String> playerUrlList,String teamName,MVs mvs) {
//			for(String url : playerUrlList) {
//				Document doc = UtilBag.getDocumentByUrl(url);
//				if(null != doc) {
//					Elements divs = doc.select(".lcontent");
//					if(null != divs && divs.size() > 0) {
//						Elements names = divs.get(0).select(".itm_name");
//						//球员中文名，用来下载图片命名时用
//						String playerName = "";
//						if(null != names && names.size() > 0) {
//							playerName = names.get(0).text().trim();
//						}
//						Elements imgs = divs.get(0).select("img");
//						if(null != imgs && imgs.size() > 0) {
//							String imgUrl = imgs.get(0).attr("src").trim();
//							if(imgUrl.indexOf("notfound") > -1) {
//								//如果网上没有相关照片，待定
//							} else {
//								String downloadPath = imgPath + "\\" + teamName + "\\" + playerName  + ".jpg";
//								UtilBag.downloadPicture(imgUrl, downloadPath);
//							}
//						}
//					}
//				}
//			}
//		}
		
		
		/**
		 * 抓取历史混合过关数据
		 * @param day
		 * 一定要从至少两天前开始抓才准确
		 */
		public static void fetchHistoryMixed(Integer day,String start,String end) {
			String start_time = null;
			try {
				List<String> daysList = getDaysList(start, getDays(start, end));
				
		    	for(int k=0;k<daysList.size();k++) {
		    		baseUrl = baseUrl + "&date=" + daysList.get(k);
			     	Document doc = UtilBag.getDocumentByUrl(baseUrl);
			     	//请求完成后复原url
			     	baseUrl = "http://trade.500.com/jczq/index.php?playid=312";
		    		
		    			Elements trs = doc.select("tr");
		    			if(null != trs && trs.size() > 0) {
		    				for(Element tr : trs) {
		    					if(tr.hasAttr("zid")) {
		    						if("68739".equals(tr.attr("zid"))) {
		    							System.out.println("hjk");
		    						}
									//对阵对象
			    					FoMix mvs = new FoMix();
			    					mvs.setWubaiid((tr.attr("zid")));
			    					//开赛时间
			    					start_time = tr.select(".match_time").get(0).attr("title").split("：")[1].trim();
		    						//抓取比赛类型 开赛时间 截止时间 剩余时间
		    						getGameTypeAndTime(tr,mvs);
		    						//获取比分
									getFinalScore(tr,mvs);
//									//通过析获取平均概率
//									getAvgRate(tr,mvs);
									//抓取主客队名字，让球和球队详细信息
									getTeamNameAndInfo(tr,mvs);
									//获取 胜平负，让球胜平负，比分，总进球，全半场
									get5Data(tr, daysList.get(k), tr.attr("zid"),mvs);
									
//									try {
//	        							//
//	        							getXiEuroAsia(mvs);
//	        							System.out.println("分析数据抓取完毕");
//									} catch (Exception e) {
//										if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
//												|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1
//												 || e.getMessage().indexOf("Read timed out") > -1) {
//											getXiEuroAsia(mvs);
//										}
//									}
									
//									try {
//	        							oddsHandler(mvs);
//									} catch (Exception e) {
//										e.printStackTrace();
//										if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
//												|| e.getMessage().indexOf("String input must not be null") > -1) {
//											try {
//												Thread.sleep(60*1000*2);
//											} catch (Exception e1) {
//												oddsHandler(mvs);
//											}
//										} else {
//											logger.error("赔率抓取出错，停止！",e);
//										System.exit(0);
//										}
//									}
									
									//判断能否入库
		    						FoMixExample mvsEx = new FoMixExample();
		    						Criteria mvsCriteria = mvsEx.createCriteria();
		    						mvsCriteria.andIduniqueEqualTo(mvs.getIdunique());
		    						List<FoMix> mvsList = mVsMapper.selectByExample(mvsEx);
		    						if(mvsList.size() == 0) {
		    							mvs.setId(IDUtils.createUUId());
		    							mVsMapper.insert(mvs);
		    							continue;
		    						} else {
		    							//更新
		    							String id = mvsList.get(0).getId();
		    							mvs.setId(id);
		    							mVsMapper.updateByPrimaryKey(mvs);
		    							continue;
		    						}
			    				
		    					}
		    				}
		    			}
		    	}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		public static void getXiEuroAsia(FoMix mvs) {
//	    	AnalysisFetch.getAnalysis(mvs);
//	    	AnalysisFetch.getEuro(mvs.getXiid());
//	    	AnalysisFetch.getAsia(mvs.getXiid());
//	    }
//		
//		/**
//	     * 处理赔率
//	     * @param mvs
//	     * @return
//	     */
//	    private static MVs oddsHandler(MVs mvs) {
//	    	//封装百家平均
//			Europodds eu = Europei.getEuroOdds(mvs);
//			if(ObjectHelper.isNotEmpty(eu) && ObjectHelper.isNotEmpty(eu.getJsops())
//					 && ObjectHelper.isNotEmpty(eu.getJsopp())  && ObjectHelper.isNotEmpty(eu.getJsopf())) {
//				mvs.setEurops(eu.getJsops());
//				mvs.setEuropp(eu.getJsopp());
//				mvs.setEuropf(eu.getJsopf());
//			}
//			
//			//封装即使凯利
//			if(ObjectHelper.isNotEmpty(eu) && ObjectHelper.isNotEmpty(eu.getJskailis())
//					 && ObjectHelper.isNotEmpty(eu.getJskailip())  && ObjectHelper.isNotEmpty(eu.getJskailif())) {
//				mvs.setKailis(eu.getJskailis());
//				mvs.setKailip(eu.getJskailip());
//				mvs.setKailif(eu.getJskailif());
//			}
//			
//			//封装亚盘指数
//			Asiaodds asia = Asiapei.getAisaOdds(mvs);
//			if(ObjectHelper.isNotEmpty(asia) && ObjectHelper.isNotEmpty(asia.getJspkhome()) 
//					 && ObjectHelper.isNotEmpty(asia.getJspan())  && ObjectHelper.isNotEmpty(asia.getJspkaway())) {
//				mvs.setAisas(asia.getJspkhome());
//				mvs.setAsiap(asia.getJspan());
//				mvs.setAsiaf(asia.getJspkaway());
//			}
//			
//			Bigsmall bs = BigSmall.getBigSmall(mvs);
//			return mvs;
//	    }
		
		
		
		/**
		 * 只传d1,只抓取d1当天
		 * 传d1和d2，抓取d1到d2时间段的数据
		 * d1 不能为空
		 * @param args
		 */
		public static void main(String[] args) {
			long startTime = System.currentTimeMillis();
			String d1 = "2017-07-24";
			String d2 = "2017-07-16";
			
			try {
				getZids(d1,null);
			} catch (Exception e) {
				if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
						|| e.getMessage().indexOf("String input must not be null") > -1) {
					getZids(d1,d2);
				} else {
					System.out.println(e);
				}
			}
			
			//抓取单天
//			mix.getZids("2017-03-07",null);
			
//			mix.getTeamInfo();
			long endTime = System.currentTimeMillis();
			System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
			System.out.println("程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
		}
}
