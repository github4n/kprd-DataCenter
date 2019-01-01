package com.kprd.date.fetch.jingcai.football;

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
import com.kprd.date.util.JedisUtilForFetch;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.mapper.FoRelationTeamMapper;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

public class JingCaiMixYesterday {
	//昨天混合过关(post传日期)
	private static String baseUrl = "http://info.sporttery.cn/football/match_result.php";
	//球队idurl
	private static String teamUrl = "http://i.sporttery.cn/api/fb_match_info/get_match_info?mid=xx&_=yy";
	//固定奖金
	private static String resultUrl = "http://info.sporttery.cn/football/pool_result.php?id=xx";
	//详情页面队伍头部数据
	private static String teamHead = "http://i.sporttery.cn/api/fb_match_info/get_match_info?mid=teamId&_=timestamp";
	
	static FoMixMapper mixMapper = Main.applicationContext.getBean(FoMixMapper.class);
	//队伍关系表mapper
	static FoRelationTeamMapper rtMapper = Main.applicationContext.getBean(FoRelationTeamMapper.class);
	/**
	 * 获取昨天到目前已完成的比赛
	 */
	public static void getYesterDay(String start_date,String end_date) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			if(ObjectHelper.isNotEmpty(start_date) && ObjectHelper.isNotEmpty(end_date)) {
				params.put("start_date", start_date);
				params.put("end_date", end_date);
				params.put("search_league", "0");
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String today = sdf.format(new Date());
				//竞彩官网的查询条件是当天日期+当天日期查出昨天的所有比赛 (有问题，竞彩和500昨日比赛查询规则不一样，暂定查询从3天前到今天的)
//				String sd = UtilBag.dateUtil(-3);
				params.put("start_date", today);
				params.put("end_date", today);
				params.put("search_league", "0");
			}
			String result = HttpClient.doPost(baseUrl, params,"gbk");
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
				getDataInfo(doc);
				if(ObjectHelper.isNotEmpty(urlList)) {
					for(String url : urlList) {
						String html = UtilBag.urlFetch(url, "gb2312");
						Document dom = Jsoup.parse(html);
						getDataInfo(dom);
					}
				}
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				System.out.println("超时");
				try {
					Thread.sleep(1000*60);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				getYesterDay(start_date,end_date);
			}
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
	private static void getDataInfo(Document doc) {
		try {
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
							Elements tds = tr.children();
							if(ObjectHelper.isNotEmpty(tds)) {
								//通过客队全称
								Elements spans = tds.get(3).select("span");
								if(ObjectHelper.isNotEmpty(spans) && spans.size() == 3) {
									String homeFName = spans.get(0).attr("title");
									FoMixExample mixEx = new FoMixExample();
									if("墨西哥".equals(homeFName)) {
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
										mixEx.createCriteria().andIduniqueEqualTo(idun);
											List<FoMix> mixList = mixMapper.selectByExample(mixEx);
											if(ObjectHelper.isNotEmpty(mixList)) {
												FoMix foMix = new FoMix();
												String[] halfscores = tds.get(4).text().split(":");
												if(ObjectHelper.isNotEmpty(halfscores)) {
													if("取消".equals(halfscores[0])) {
														foMix.setHomehalf("取消");
														foMix.setAwayhalf("取消");
													} else {
														foMix.setHomehalf(halfscores[0]);
														foMix.setAwayhalf(halfscores[1]);
													}
												}
												String jcId = tds.get(3).select("a").get(0).attr("href").split("=")[1];
												foMix.setJingcaiid(jcId);
												String finalScore = tds.get(5).text();
												if(finalScore.indexOf("取消") > -1) {
													
												} else {
													if(!StringUtils.isEmpty(finalScore)) {
														String[] scores = finalScore.split(":");
														foMix.setHomescore(scores[0]);
														foMix.setAwayscore(scores[1]);
														foMix.setId(mixList.get(0).getId());
														mixMapper.updateByPrimaryKeySelective(foMix);
													}
												}
											} else {
												System.out.println(mixList);
													getYesterdayFullData(tr,idun);
												try {
//														throw new Exception("查看是否分页");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												continue;
											}
										}
									}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			getDataInfo(doc);
		}
	}
	
	/**
	 * 获取昨日全部数据（一般情况下不会执行此方法）
	 * @param trs
	 */
	public static void getYesterdayFullData(Element tr,String idun) {
		try {
			if(ObjectHelper.isNotEmpty(tr)) {
					Elements tds = tr.children();
					if(ObjectHelper.isNotEmpty(tds)) {
						FoMix fmix = new FoMix();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						fmix.setStarttime(sdf.parse(tds.get(0).text()));////开始时间可能需要去单独的页面去
						fmix.setIdunique(idun);
						fmix.setLeaguecolor(tds.get(2).attr("bgcolor"));
						fmix.setLeaguename(tds.get(2).text());
						Elements as = tds.get(3).select("a");
						if(ObjectHelper.isNotEmpty(as) && as.size() > 0) {
							String urlStr = as.get(0).attr("href");
							if(!StringUtils.isEmpty(urlStr)) {
								String jingCaiId = urlStr.split("=")[1];
								fmix.setJingcaiid(jingCaiId);
								getSimpleTeamInfo(fmix);
							}
						}
						
//						if(fmix.getJingcaiid().equals("95781")) {
//							System.out.println(123);
//						}
						
						String style = tds.get(3).attr("style");
						if(ObjectHelper.isNotEmpty(style) && style.indexOf("single") > -1) {
							//让去胜平负单关
							System.out.println();
						}
						
						Elements spans = tds.get(3).select("span");
						if(ObjectHelper.isNotEmpty(spans) && spans.size() == 3) {
							fmix.setHomefullname(spans.get(0).attr("title"));
							fmix.setAwayfullname(spans.get(2).attr("title"));
							String homeTeam = spans.get(0).text();
							String awayTeam = spans.get(2).text();
							if(homeTeam.indexOf("(") > -1) {
								fmix.setHomeshortname(homeTeam.split("\\(")[0]);
								homeTeam = homeTeam.split("\\(")[1];
								String rangqiu = homeTeam.substring(0,homeTeam.length()-1);
								fmix.setRqs(rangqiu);
							}
							fmix.setAwayshortname(awayTeam);
						}
						
						String[] halfscores = tds.get(4).text().split(":");
						if(ObjectHelper.isNotEmpty(halfscores)) {
							fmix.setHomehalf(halfscores[0]);
							fmix.setAwayhalf(halfscores[1]);
						}
						
						String[] scores = tds.get(5).text().split(":");
						if(ObjectHelper.isNotEmpty(scores)) {
							fmix.setHomescore(scores[0]);
							fmix.setAwayscore(scores[1]);
						}
						fmix.setId(IDUtils.createUUId());
						//没有排名，截止时间，准确的开始时间
						mixMapper.insert(fmix);
						return;
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取分析头部信息
	 * @param fmix
	 */
	private static void getStartTime(FoMix fmix) {
		teamHead = teamHead.replace("teamId", fmix.getJingcaiid());
		teamHead = teamHead.replace("timestamp",String.valueOf(System.currentTimeMillis()));
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(teamHead);
			CloseableHttpResponse response = httpclient.execute(httpget);
			teamHead = "http://i.sporttery.cn/api/fb_match_info/get_match_info?mid=teamId&_=timestamp";
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
                		fmix.setStarttime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start));
                		Date endtime = getEndTime(fmix.getStarttime());
                		fmix.setEndtime(endtime);
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
	 * 通过数据库查询出昨天到今天的比赛获取5项数据
	 * 单独执行
	 */
	public static void get5Data(String sd1,String sd2) {
		String dateKey = null;
		try {
			FoMixExample yesEx = new FoMixExample();
			List<FoMix> yesList = null;
			if(ObjectHelper.isEmpty(sd1) && ObjectHelper.isEmpty(sd2)) {
				dateKey = UtilBag.dateUtil(-1).replace("-", "");
				//查询出昨天到现在的所有比赛
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String yesterday = UtilBag.dateUtil(-1) + " 11:59:59";
				String today = sdf.format(new Date()) + " 11:59:59";
				yesEx.createCriteria().andStarttimeGreaterThanOrEqualTo(sdf.parse(yesterday)).andStarttimeLessThanOrEqualTo(sdf.parse(today));
				yesEx.setOrderByClause(" startTime asc");
				yesList = mixMapper.selectByExample(yesEx);
			} else {
				dateKey = sd1.replace("-", "");
				//查询出昨天到现在的所有比赛
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String yesterday = sd1 + " 00:00:00";
				String today = sd2 + " 23:59:59";
				yesEx.createCriteria().andStarttimeGreaterThanOrEqualTo(sdf.parse(yesterday)).andStarttimeLessThanOrEqualTo(sdf.parse(today))
				.andJingcaiidIsNotNull();
				yesEx.setOrderByClause(" idUnique asc");
				yesList = mixMapper.selectByExample(yesEx);
			}
			
			if(ObjectHelper.isNotEmpty(yesList) && yesList.size() > 0) {
				for(FoMix mix : yesList) {
					resultUrl = resultUrl.replace("xx", mix.getJingcaiid());
					Map<String, String> map = new HashMap<>();
					map.put("id", mix.getJingcaiid());
					String string = UtilBag.checkCookieRequest(resultUrl, "gbk");
					Document doc = Jsoup.parse(string);
					resultUrl = "http://info.sporttery.cn/football/pool_result.php?id=xx";
					System.out.println(mix.getHomefullname());
					if(ObjectHelper.isNotEmpty(doc)) {
						Elements divs = doc.select("div");
						if(ObjectHelper.isNotEmpty(divs)) {
							for(Element div : divs) {
								if(div.attr("style").equals("clear:both")) {
									Element table = div.nextElementSibling();
									Elements trs = table.select("tr");
									if(ObjectHelper.isNotEmpty(trs)) {
										for(Element tr : trs) {
											if(tr.attr("style").indexOf("repeat scroll") > -1) {
												Elements tds = tr.children();
												if(ObjectHelper.isNotEmpty(tds)) {
													String style1 = tds.get(1).attr("style");
													String style2 = tds.get(2).attr("style");
													if(ObjectHelper.isNotEmpty(style1)) {
														mix.setSpfsingle("1");
													}
													if(ObjectHelper.isNotEmpty(style2)) {
														mix.setRqspfsingle("1");
													}
												}
											}
										}
									}
									break;
								}
							}
						}
						
						getStartTime(mix);
						
						Elements jfs = doc.select(".jf");
						if(ObjectHelper.isNotEmpty(jfs) && jfs.size() == 6) {
							//让球胜平负过关
							Element rqspf = jfs.get(1);
							Elements rqspfTrs = rqspf.select("tr");
							if(ObjectHelper.isNotEmpty(rqspfTrs) && rqspfTrs.size() > 0) {
								Element rqspfTr = rqspfTrs.get(rqspfTrs.size()-1);
								rqspfTr.select("td").get(2).attr("style");
								rqspfTr.select("td").get(3).attr("style");
								mix.setRqspfs(rqspfTr.select("td").get(2).text());
								mix.setRqspfp(rqspfTr.select("td").get(3).text());
								mix.setRqspff(rqspfTr.select("td").get(4).text());
								Elements imgs = rqspfTr.select("td").get(2).select("img");
								if(ObjectHelper.isNotEmpty(imgs)) {
									if(imgs.get(0).attr("src").indexOf("ArrowUp") > -1) {
										mix.setRqspfsstatus("1");
									} else if(imgs.get(0).attr("src").indexOf("ArrowDown") > -1) {
										mix.setRqspfsstatus("-1");
									} else {
										mix.setRqspfsstatus("0");
									}
								}
								Elements imgs2 = rqspfTr.select("td").get(3).select("img");
								if(ObjectHelper.isNotEmpty(imgs2)) {
									if(imgs2.get(0).attr("src").indexOf("ArrowUp") > -1) {
										mix.setRqspfpstatus("1");
									} else if(imgs2.get(0).attr("src").indexOf("ArrowDown") > -1) {
										mix.setRqspfpstatus("-1");
									} else {
										mix.setRqspfpstatus("0");
									}
								}
								Elements imgs3 = rqspfTr.select("td").get(4).select("img");
								if(ObjectHelper.isNotEmpty(imgs3)) {
									if(imgs3.get(0).attr("src").indexOf("ArrowUp") > -1) {
										mix.setRqspffstatus("1");
									} else if(imgs3.get(0).attr("src").indexOf("ArrowDown") > -1) {
										mix.setRqspffstatus("-1");
									} else {
										mix.setRqspffstatus("0");
									}
								}
							}
							Element spf = jfs.get(2);
							Elements trs = spf.select("tr");
							Element tr = trs.get(trs.size()-1);
							Elements tds = tr.select("td");
							mix.setSpfs(tds.get(1).text());
							mix.setSpfp(tds.get(2).text());
							mix.setSpff(tds.get(2).text());
							Elements img = tds.get(1).select("img");
							if(ObjectHelper.isNotEmpty(img)) {
								if(img.get(0).attr("src").indexOf("ArrowUp") > -1) {
									mix.setSpfsstatus("1");
								} else if(img.get(0).attr("src").indexOf("ArrowDown") > -1) {
									mix.setSpfsstatus("-1");
								} else {
									mix.setSpfsstatus("0");
								}
							}
							Elements img2 = tds.get(2).select("img");
							if(ObjectHelper.isNotEmpty(img2)) {
								if(img2.get(0).attr("src").indexOf("ArrowUp") > -1) {
									mix.setSpfpstatus("1");
								} else if(img2.get(0).attr("src").indexOf("ArrowDown") > -1) {
									mix.setSpfpstatus("-1");
								} else {
									mix.setSpfpstatus("0");
								}
							}
							Elements img3 = tds.get(3).select("img");
							if(ObjectHelper.isNotEmpty(img3)) {
								if(img3.get(0).attr("src").indexOf("ArrowUp") > -1) {
									mix.setSpffstatus("1");
								} else if(img3.get(0).attr("src").indexOf("ArrowDown") > -1) {
									mix.setSpffstatus("-1");
								} else {
									mix.setSpffstatus("0");
								}
							}
							Element zjq = jfs.get(3);
							Elements zjqTrs = zjq.select("tr");
							Element zjqtr = zjqTrs.get(zjqTrs.size()-1);
							Elements zjqtds = zjqtr.select("td");
							mix.setZjq0(zjqtds.get(1).text());
							mix.setZjq1(zjqtds.get(2).text());
							mix.setZjq2(zjqtds.get(3).text());
							mix.setZjq3(zjqtds.get(4).text());
							mix.setZjq4(zjqtds.get(5).text());
							mix.setZjq5(zjqtds.get(6).text());
							mix.setZjq6(zjqtds.get(7).text());
							mix.setZjq7(zjqtds.get(8).text());
							
							Element bqcElement = jfs.get(4);
							Elements bqcTrs = bqcElement.select("tr");
							Element bqcTr = bqcTrs.get(bqcTrs.size()-1);
							Elements bqcTds = bqcTr.select("td");
							
							Elements imgs1 = bqcTds.get(1).select("img");
							if(ObjectHelper.isNotEmpty(imgs1)) {
								String img1 = imgs1.attr("src");
								if(img1.indexOf("Up") > -1) {
									mix.setQbcssbingo("1");
								} else if(img1.indexOf("ArrowDown") > -1) {
									mix.setQbcssbingo("-1");
								} else {
									mix.setQbcssbingo("0");
								}
							}
							
							Elements imgs2 = bqcTds.get(2).select("img");
							if(ObjectHelper.isNotEmpty(imgs2)) {
								String pic2 = imgs2.attr("src");
								if(pic2.indexOf("ArrowUp") > -1) {
									mix.setQbcspbingo("1");
								} else if(pic2.indexOf("ArrowDown") > -1) {
									mix.setQbcspbingo("-1");
								} else {
									mix.setQbcspbingo("0");
								}
							}
							
							Elements imgs3 = bqcTds.get(3).select("img");
							if(ObjectHelper.isNotEmpty(imgs3)) {
								String pic3 = imgs3.attr("src");
								if(pic3.indexOf("ArrowUp") > -1) {
									mix.setQbcsfbingo("1");
								} else if(pic3.indexOf("ArrowDown") > -1) {
									mix.setQbcsfbingo("-1");
								} else {
									mix.setQbcsfbingo("0");
								}
							}
							
							Elements imgs4 = bqcTds.get(4).select("img");
							if(ObjectHelper.isNotEmpty(imgs4)) {
								String pic4 = imgs4.attr("src");
								if(pic4.indexOf("ArrowUp") > -1) {
									mix.setQbcpsbingo("1");
								} else if(pic4.indexOf("ArrowDown") > -1) {
									mix.setQbcpsbingo("-1");
								} else {
									mix.setQbcpsbingo("0");
								}
							}
							
							Elements imgs5 = bqcTds.get(5).select("img");
							if(ObjectHelper.isNotEmpty(imgs5)) {
								String pic5 = imgs5.attr("src");
								if(pic5.indexOf("ArrowUp") > -1) {
									mix.setQbcppbingo("1");
								} else if(pic5.indexOf("ArrowDown") > -1) {
									mix.setQbcppbingo("-1");
								} else {
									mix.setQbcppbingo("0");
								}
							}
							
							Elements imgs6 = bqcTds.get(6).select("img");
							if(ObjectHelper.isNotEmpty(imgs6)) {
								String pic6 = imgs6.attr("src");
								if(pic6.indexOf("ArrowUp") > -1) {
									mix.setQbcpfbingo("1");
								} else if(pic6.indexOf("ArrowDown") > -1) {
									mix.setQbcpfbingo("-1");
								} else {
									mix.setQbcpfbingo("0");
								}
							}
							
							Elements imgs7 = bqcTds.get(7).select("img");
							if(ObjectHelper.isNotEmpty(imgs7)) {
								String pic7 = imgs7.attr("src");
								if(pic7.indexOf("ArrowUp") > -1) {
									mix.setQbcfsbingo("1");
								} else if(pic7.indexOf("ArrowDown") > -1) {
									mix.setQbcfsbingo("-1");
								} else {
									mix.setQbcfsbingo("0");
								}
							}
							
							Elements imgs8 = bqcTds.get(8).select("img");
							if(ObjectHelper.isNotEmpty(imgs8)) {
								String pic8 = imgs8.attr("src");
								if(pic8.indexOf("ArrowUp") > -1) {
									mix.setQbcfpbingo("1");
								} else if(pic8.indexOf("ArrowDown") > -1) {
									mix.setQbcfpbingo("-1");
								} else {
									mix.setQbcfpbingo("0");
								}
							}
							
							Elements imgs9 = bqcTds.get(9).select("img");
							if(ObjectHelper.isNotEmpty(imgs9)) {
								String pic9 = imgs9.attr("src");
								if(pic9.indexOf("ArrowUp") > -1) {
									mix.setQbcffbingo("1");
								} else if(pic9.indexOf("ArrowDown") > -1) {
									mix.setQbcffbingo("-1");
								} else {
									mix.setQbcffbingo("0");
								}
							}
							
							
							mix.setQbcss(bqcTds.get(1).text());
							mix.setQbcsp(bqcTds.get(2).text());
							mix.setQbcsf(bqcTds.get(3).text());
							mix.setQbcps(bqcTds.get(4).text());
							mix.setQbcpp(bqcTds.get(5).text());
							mix.setQbcpf(bqcTds.get(6).text());
							mix.setQbcfs(bqcTds.get(7).text());
							mix.setQbcfp(bqcTds.get(8).text());
							mix.setQbcff(bqcTds.get(9).text());
							
							Element bifen = jfs.get(5);//floatL
							Elements floatLs = bifen.select(".floatL");
							Element floatL = floatLs.get(floatLs.size()-1);
							Element fuckTr = floatL.parent().parent().nextElementSibling();
							Element winData = fuckTr.nextElementSibling();
							Element drawData = winData.nextElementSibling().nextElementSibling();
							Element loseData = drawData.nextElementSibling().nextElementSibling();
							mix.setScore10(winData.select("td").get(0).text());
							mix.setScore20(winData.select("td").get(1).text());
							mix.setScore21(winData.select("td").get(2).text());
							mix.setScore30(winData.select("td").get(3).text());
							mix.setScore31(winData.select("td").get(4).text());
							mix.setScore32(winData.select("td").get(5).text());
							mix.setScore40(winData.select("td").get(6).text());
							mix.setScore41(winData.select("td").get(7).text());
							mix.setScore42(winData.select("td").get(8).text());
							mix.setScore50(winData.select("td").get(9).text());
							mix.setScore51(winData.select("td").get(10).text());
							mix.setScore52(winData.select("td").get(11).text());
							mix.setScorewinother(winData.select("td").get(12).text());
							
							mix.setScore00(drawData.select("td").get(0).text());
							mix.setScore11(drawData.select("td").get(1).text());
							mix.setScore22(drawData.select("td").get(2).text());
							mix.setScore33(drawData.select("td").get(3).text());
							mix.setScoredrawother(drawData.select("td").get(4).text());
							
							mix.setScore01(loseData.select("td").get(0).text());
							mix.setScore02(loseData.select("td").get(1).text());
							mix.setScore12(loseData.select("td").get(2).text());
							mix.setScore03(loseData.select("td").get(3).text());
							mix.setScore13(loseData.select("td").get(4).text());
							mix.setScore23(loseData.select("td").get(5).text());
							mix.setScore04(loseData.select("td").get(6).text());
							mix.setScore14(loseData.select("td").get(7).text());
							mix.setScore24(loseData.select("td").get(8).text());
							mix.setScore05(loseData.select("td").get(9).text());
							mix.setScore15(loseData.select("td").get(10).text());
							mix.setScore25(loseData.select("td").get(11).text());
							mix.setScoreloseother(loseData.select("td").get(12).text());//没有插入
							mixMapper.updateByPrimaryKeySelective(mix);
							JedisUtilForFetch.remove(6,dateKey + "gamecenter");
						}
					}
				}
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					get5Data(sd1, sd2);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取队伍基本信息
	 * @param mix
	 */
	public static void getSimpleTeamInfo(FoMix mix) {
		//组装url
		teamUrl = teamUrl.replace("yy", String.valueOf(System.currentTimeMillis()));
		teamUrl = teamUrl.replace("xx", mix.getJingcaiid());
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(teamUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gbk");
                	if(ObjectHelper.isNotEmpty(html)) {
                		JSONObject jsonObject = JSONObject.fromObject(html);
                		Object result = jsonObject.get("result");
                		jsonObject = JSONObject.fromObject(result);
                		String teamIdH = jsonObject.get("h_id_dc").toString();
                		String teamIdA = jsonObject.get("a_id_dc").toString();
                		String leagueId = jsonObject.get("l_id_dc").toString();
                		mix.setHometeamid(teamIdH);
                		mix.setAwayteamid(teamIdA);
                		mix.setLeagueid(leagueId);
                	}
                }	
			} finally {
				response.close();
			}
			teamUrl = "http://i.sporttery.cn/api/fb_match_info/get_match_info?mid=xx&_=yy";
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
			if("星期一".equals(week) || "星期日".equals(week)) {
				System.out.println("周1和周7");
				String st = sdf.format(startTime);
				if(!StringUtils.isEmpty(st)) {
					String hm = st.split(" ")[1];
					String hour = hm.split(":")[0];
					int hr = Integer.valueOf(hour);
					if(9<=hr || 1>=hr) {
						Date afterDate = new Date(startTime .getTime() - 300000);
						System.out.println(sdf.format(afterDate));
						endDate = afterDate;
					} else {
						String dateStr = st.split(" ")[0] + " 01:00";
						Date afterDate = sdf.parse(dateStr);
						afterDate = new Date(afterDate.getTime() - 300000);
//						System.out.println(sdf.format(afterDate));
						endDate = afterDate;
					}
				}
			} else {
				System.out.println("周2至周6");
				String st = sdf.format(startTime);
				if(!StringUtils.isEmpty(st)) {
					String hm = st.split(" ")[1];
					String hour = hm.split(":")[0];
					int hr = Integer.valueOf(hour);
					if(9<=hr) {
						Date afterDate = new Date(startTime.getTime() - 300000);
//						System.out.println(sdf.format(afterDate));
						endDate = afterDate;
					} else {
						String dateStr = st.split(" ")[0] + " 00:00";
						Date afterDate = sdf.parse(dateStr);
						afterDate = new Date(afterDate.getTime() - 300000);
//						System.out.println(sdf.format(afterDate));
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
//		test();
//		getYesterDay("2017-08-06","2017-08-06");
//		get5Data("2017-08-06","2017-08-06");
		getYesterDay(null,null);
		get5Data(null,null);
	}
}
