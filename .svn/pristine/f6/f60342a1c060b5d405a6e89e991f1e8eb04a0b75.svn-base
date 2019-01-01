package com.kprd.date.zq;



import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.dic.mapper.GarbledMapper;
import com.kprd.dic.pojo.Garbled;
import com.kprd.dic.pojo.GarbledExample;
import com.kprd.liansai.mapper.MVsMapper;
import com.kprd.liansai.pojo.MVs;
import com.kprd.liansai.pojo.MVsExample;
import com.kprd.odds.mapper.AnalysisgjzlpmMapper;
import com.kprd.odds.mapper.AnalysisheadMapper;
import com.kprd.odds.mapper.AsiaoddsMapper;
import com.kprd.odds.mapper.CupinfoMapper;
import com.kprd.odds.mapper.EuropoddsMapper;
import com.kprd.odds.mapper.MFutureMapper;
import com.kprd.odds.mapper.MXiscorerankingMapper;
import com.kprd.odds.mapper.MrecentMapper;
import com.kprd.odds.mapper.VshistoryMapper;
import com.kprd.odds.pojo.Analysisgjzlpm;
import com.kprd.odds.pojo.AnalysisgjzlpmExample;
import com.kprd.odds.pojo.Analysishead;
import com.kprd.odds.pojo.AnalysisheadExample;
import com.kprd.odds.pojo.Cupinfo;
import com.kprd.odds.pojo.CupinfoExample;
import com.kprd.odds.pojo.MFuture;
import com.kprd.odds.pojo.MFutureExample;
import com.kprd.odds.pojo.MXiscoreranking;
import com.kprd.odds.pojo.MXiscorerankingExample;
import com.kprd.odds.pojo.MXiscorerankingExample.Criteria;

import net.sf.json.JSONObject;

import com.kprd.odds.pojo.Mrecent;
import com.kprd.odds.pojo.MrecentExample;
import com.kprd.odds.pojo.Vshistory;
import com.kprd.odds.pojo.VshistoryExample;


/**
 * 分析页面
 * @author Administrator
 *
 */
public class AnalysisFetch {
	
	private static String baseUrl = "http://odds.500.com/fenxi/shuju-fuck.shtml";
	
	//分析mapper
	static MXiscorerankingMapper mxiMapper = Main.applicationContext.getBean(MXiscorerankingMapper.class);
	//欧赔mapper
	static EuropoddsMapper euroMapper = Main.applicationContext.getBean(EuropoddsMapper.class);
	//压盘mapper
	static AsiaoddsMapper asiaMapper = Main.applicationContext.getBean(AsiaoddsMapper.class);
	//交战mapper
	static VshistoryMapper vshMapper = Main.applicationContext.getBean(VshistoryMapper.class);
	
	//最近战报mapper
	static MrecentMapper mrMapper = Main.applicationContext.getBean(MrecentMapper.class);
	//未来战报mapper
	static MFutureMapper mfMapper = Main.applicationContext.getBean(MFutureMapper.class);
	//头部信息mapper
	static AnalysisheadMapper analysisheadMapper = Main.applicationContext.getBean(AnalysisheadMapper.class);
	//国际足联排名
	static AnalysisgjzlpmMapper analysisgjzlpmMapper = Main.applicationContext.getBean(AnalysisgjzlpmMapper.class);
	//对阵
	static MVsMapper mvsMapper = Main.applicationContext.getBean(MVsMapper.class);
	//乱码
	static GarbledMapper gbMapper = Main.applicationContext.getBean(GarbledMapper.class);
	
	public static void getAnalysis(MVs mvs) {
		try {
			
			/**
			 * 交战历史
			 */
			//左边队伍胜
			String leftTeamWin = null;
			String leftTeamDraw = null;
			String leftTeamLose = null;
			//6场信息
			String sixInfo = null;
			String gameTime = null;
			String gameColor = null;
			String gameName = null;
			String homeScore = null;
			String awayScore = null;
			String result = null;
			//盘路
			String panLu = null;
			String daxiao = null;
			String remark = null;
			
			String left = null;
			String right = null;
			String euroagvwin = null;
			String euroAvgWinStatus = null;
			String euroagvdraw = null;
			String euroAvgDrawStatus = null;
			String euroagvlose = null;
			String euroAvgLoseStatus = null;
			
			String asiaagvwin = null;
			String asiaAvgWinStatus = null;
			String asiaagvdraw = null;
			String asiaAvgDrawStatus = null;
			String asiaagvlose = null;
			String asiaAvgLoseStatus = null;
			
			String stringurl = baseUrl.replace("fuck", mvs.getXiid());
			String html = UtilBag.getHtmlForGZIP(stringurl, "gb2312");
			baseUrl = "http://odds.500.com/fenxi/shuju-fuck.shtml";
			Document dom = Jsoup.parse(html);
			if(ObjectHelper.isNotEmpty(dom)) {
				//获取头部信息
				Analysishead analysishead = getHeadInfo(dom,mvs.getXiid());
				System.out.println("已获取头部信息" + analysishead);
				
				///////////////////////////////////国际足联排名/////////////////////////////////////////////
				Analysisgjzlpm international = getInterNationalInfo(dom, mvs.getXiid());
				System.out.println("国际足联排名" + international);
				//获取赛前杯赛排名
				getScoreRank(dom,mvs);
				
				//赛前联赛积分排名
				getLeagueScore(dom, mvs.getXiid());
				
				///////////////////////////////////两队交战历史/////////////////////////////////////////////
				Element team_jiaozhan = dom.select("#team_jiaozhan").get(0);
				if(ObjectHelper.isNotEmpty(team_jiaozhan)) {
					Elements titles = team_jiaozhan.select(".M_title");
					Elements ems = titles.get(0).select("em");
					if(ObjectHelper.isNotEmpty(ems)) {
						leftTeamWin = ems.get(0).text().replace("胜", "");
						leftTeamDraw =  ems.get(1).text().replace("平", "");
						leftTeamLose = ems.get(2).text().replace("负", "");
					}
					Elements his_infos = titles.get(0).select(".his_info");
					if(ObjectHelper.isNotEmpty(his_infos)) {
						String vsInfo = his_infos.get(0).text();
						if(StringUtils.isEmpty(vsInfo)) {
							sixInfo = "双方无交战历史";
						} else {
							sixInfo = vsInfo.substring(vsInfo.indexOf("进"), vsInfo.length());
							Elements m_contents = team_jiaozhan.select(".M_content");
							if(ObjectHelper.isNotEmpty(m_contents)) {
								Elements trs = m_contents.get(0).select("table tbody tr");
								if(ObjectHelper.isNotEmpty(trs)) {
									List<String> xiList = new ArrayList<String>();
									for(int i = 2; i<trs.size(); i++) {
										String trXiId = trs.get(i).attr("fid");
										xiList.add(trXiId);
										Elements tds = trs.get(i).select("td");
										if(ObjectHelper.isNotEmpty(tds)) {
											//欧赔
											Elements pspans = tds.get(5).select("p span");
											
											euroagvwin = pspans.get(0).text();
											if(pspans.get(0).attr("class").indexOf("pl_yes") > -1) {
												euroAvgWinStatus = "1";
											}
											
											euroagvdraw = pspans.get(1).text();
											if(pspans.get(1).attr("class").indexOf("pl_yes") > -1) {
												euroAvgDrawStatus = "1";
											}
											
											euroagvlose = pspans.get(2).text();
											if(pspans.get(2).attr("class").indexOf("pl_yes") > -1) {
												euroAvgLoseStatus = "1";
											}
											
											//亚盘
											Elements asiaspans = tds.get(6).select("p span");
											
											asiaagvwin = asiaspans.get(0).text();
											if(asiaspans.get(0).attr("class").indexOf("pl_yes") > -1) {
												asiaAvgWinStatus = "1";
											}
											
											asiaagvdraw = asiaspans.get(1).text();
											if(asiaspans.get(1).attr("class").indexOf("pl_yes") > -1) {
												asiaAvgDrawStatus = "1";
											}
											
											asiaagvlose = asiaspans.get(2).text();
											if(asiaspans.get(2).attr("class").indexOf("pl_yes") > -1) {
												asiaAvgLoseStatus = "1";
											}
											
											//联赛颜色
											gameColor = tds.get(0).attr("bgcolor");
											//联赛名称
											gameName = tds.get(0).text();
											//时间
											gameTime = tds.get(1).text();
											
											Elements spans = tds.get(2).select("span");
											if(ObjectHelper.isNotEmpty(spans)) {
												for(Element span : spans) {
													if(span.attr("class").indexOf("dz-l") > -1) {
														if(span.text().indexOf("]") > -1) {
															left = span.text().split("]")[1];
														} else {
															left = span.text();
														}
													}
													if(span.attr("class").indexOf("dz-r") > -1) {
														if(span.text().indexOf("[") > -1) {
															right = span.text().split("\\[")[0];
														} else {
															right = span.text();
														}
														break;
													}
												}
											}
											String isVs = tds.get(2).select("em").text();
											if(isVs.indexOf("VS") > - 1) {
												
											} else {
												String[] points = isVs.split(":");
												homeScore = points[0];
												awayScore = points[1];
											}
											//赛果
											result = tds.get(4).text();
											panLu = tds.get(7).text();
											daxiao = tds.get(8).text();
											remark = tds.get(9).text();
											
											Vshistory vsh = new Vshistory();
											vsh.setXiid(trXiId);
											vsh.setPid(mvs.getXiid());
											vsh.setGamename(gameName);
											vsh.setGamecolor(gameColor);
											vsh.setGametime(gameTime);
											vsh.setHometeam(left);
											vsh.setHomescore(homeScore);
											vsh.setAwayteam(right);
											vsh.setAwayscore(awayScore);
											vsh.setResult(result);
											vsh.setPanlu(panLu);
											vsh.setDaxiao(daxiao);
											vsh.setRemark(remark);
											vsh.setSpfs(leftTeamWin);
											vsh.setSpfp(leftTeamDraw);
											vsh.setSpff(leftTeamLose);
											vsh.setSp1(sixInfo);
											vsh.setEuroagvwin(euroagvwin);
											vsh.setEuroavgwinstatus(euroAvgWinStatus);
											vsh.setEuroavgdraw(euroagvdraw);
											vsh.setEuroavgdrawstatus(euroAvgDrawStatus);
											vsh.setEuroavglose(euroagvlose);
											vsh.setEuroavglosestatus(euroAvgLoseStatus);
											
											vsh.setAisacrownwin(asiaagvwin);
											vsh.setAisacrownwinstatus(asiaAvgWinStatus);
											vsh.setAisacrowndraw(asiaagvdraw);
											vsh.setAisacrowndrawstatus(asiaAvgDrawStatus);
											vsh.setAisacrownlose(asiaagvlose);
											vsh.setAisacrownlosestatus(asiaAvgLoseStatus);
											
											VshistoryExample vshEx = new VshistoryExample();
											com.kprd.odds.pojo.VshistoryExample.Criteria vshCriteria = vshEx.createCriteria();
											vshCriteria.andXiidEqualTo(trXiId);
											List<Vshistory> vsList = vshMapper.selectByExample(vshEx);
											if(ObjectHelper.isEmpty(vsList)) {
												vsh.setIdvssix(IDUtils.createUUId());
												vshMapper.insert(vsh);
											} else {
												vsh.setIdvssix(vsList.get(0).getIdvssix());
												vshMapper.updateByPrimaryKey(vsh);
											}
										}
									}
								}
							}
						}
					}
				}
				
				//////////////////////////球队近期战绩////////////////////////////
				getAwayRecent(dom, mvs);
				getHomeRecent(dom, mvs);
				////////////////////////////未来市场///////////////////////////////
				getNextTen(dom, mvs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取客队近10场信息
	 * @param dom
	 * @param mvs
	 */
	private static void getAwayRecent(Document dom,MVs mvs) {

		Element zj1 = dom.select("#team_zhanji_0").get(0);
		Elements tables = zj1.select("table");
		if(ObjectHelper.isNotEmpty(tables)) {
			Element table0 = tables.get(0); 
			String matchColor;
			String matchName;
			String matchTime;
			String lTeam = null;
			String rTeam = null;
			String lScore = null;
			String rScore = null;
			String pankou = null;
			String jieguo = null;
			String panlu = null;
			String bs = null;
			String spfs = null;
			String spfp = null;
			String spff = null;
			String data = null;
			String xiId = null;
			String aTeamUrl = null;
			String aTeamName = null;
			
			Elements teamIds = dom.select(".odds_hd_cont");
			Elements lis = teamIds.get(0).select("table tbody li");
			if(ObjectHelper.isNotEmpty(lis)) {
				if(lis.size() > 2) {
					aTeamUrl = lis.get(2).select("a").get(0).attr("href").split("team")[1].replace("/", "");
					aTeamName = lis.get(2).select("a").get(0).text();
				} else {
					aTeamUrl = lis.get(1).select("a").get(0).attr("href").split("team")[1].replace("/", "");
					aTeamName = lis.get(1).select("a").get(0).text();
				}
			}
			
			Elements trs = table0.select("tr");
			if(ObjectHelper.isNotEmpty(trs)) {
				for(int i=2;i<trs.size();i++) {
					Elements tds = trs.get(i).children();
					if(ObjectHelper.isNotEmpty(tds)) {
						matchName = tds.get(0).text();
						matchColor = tds.get(0).attr("bgcolor");
						matchTime = tds.get(1).text();
						xiId =  tds.get(2).select("a").get(0).attr("href");
						xiId = xiId.split("-")[1].split("\\.")[0];
						Elements spans = tds.get(2).select("span");
						if(ObjectHelper.isNotEmpty(spans)) {
							for(Element span : spans) {
								if(span.attr("class").indexOf("dz-l") > -1) {
									lTeam = span.text();
								}
								if(span.attr("class").indexOf("dz-r") > -1) {
									rTeam = span.text();
									break;
								}
							}
						}
						String isVs = tds.get(2).select("em").text();
						if(isVs.indexOf("VS") > - 1) {
							
						} else {
							String[] points = isVs.split(":");
							lScore = points[0];
							rScore = points[1];
						}
						pankou = tds.get(3).text();
						jieguo = tds.get(5).text();
						panlu = tds.get(6).text();
						bs = tds.get(7).text();
						Elements bottom_infos = dom.select("div .bottom_info");
						Elements spanss = bottom_infos.get(1).select("p > span");
						if(ObjectHelper.isNotEmpty(spanss)) {
							Elements sps = spanss.get(0).children();
							spfs = sps.get(0).text().replace("胜", "");
							spfp = sps.get(1).text().replace("平", "");
							spff = sps.get(2).text().replace("负", "");
							data = spanss.get(1).text();
							
							Mrecent mr = new Mrecent();
							mr.setPid(mvs.getXiid());
							mr.setTeamurlid(aTeamUrl);
							mr.setTeamname(aTeamName);
							mr.setMatchcolor(matchColor);
							mr.setMatchname(matchName);
							mr.setMatchtime(matchTime);
							mr.setLteam(lTeam);
							mr.setRteam(rTeam);
							mr.setLscore(lScore);
							mr.setRscore(rScore);
							mr.setPankou(pankou);
							mr.setPanlu(panlu);
							mr.setJieguo(jieguo);
							mr.setBs(bs);
							mr.setSpfs(spfs);
							mr.setSpfp(spfp);
							mr.setSpff(spff);
							mr.setData(data);
							mr.setXiid(xiId);
							mr.setSp1("1");//客队
							
							//左队
							if(lTeam.indexOf("�") > -1) {
								Garbled gb = new Garbled();
								gb.setXiid(xiId);
								GarbledExample gbEx = new GarbledExample();
								com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
								gbC.andGarbledEqualTo(lTeam);
								List<Garbled> gbList = gbMapper.selectByExample(gbEx);
								if(ObjectHelper.isEmpty(gbList)) {
									gb.setIdgarbled(IDUtils.createUUId());
									gb.setGarbled(lTeam);
									gbMapper.insert(gb);
								} else {
									gb.setGarbled(gbList.get(0).getGarbled());
									gb.setIdgarbled(gbList.get(0).getIdgarbled());
									if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
										mr.setLteam(gbList.get(0).getRightcode());
										gb.setRightcode(gbList.get(0).getRightcode());
									}
									gbMapper.updateByPrimaryKey(gb);
								}
							}
							
							//右队
							if(rTeam.indexOf("�") > -1) {
								Garbled gb = new Garbled();
								gb.setXiid(xiId);
								GarbledExample gbEx = new GarbledExample();
								com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
								gbC.andGarbledEqualTo(rTeam);
								List<Garbled> gbList = gbMapper.selectByExample(gbEx);
								if(ObjectHelper.isEmpty(gbList)) {
									gb.setIdgarbled(IDUtils.createUUId());
									gb.setGarbled(rTeam);
									gbMapper.insert(gb);
								} else {
									gb.setGarbled(gbList.get(0).getGarbled());
									gb.setIdgarbled(gbList.get(0).getIdgarbled());
									if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
										mr.setRteam(gbList.get(0).getRightcode());
										gb.setRightcode(gbList.get(0).getRightcode());
									}
									gbMapper.updateByPrimaryKey(gb);
								}
							}
							
							MrecentExample mrEx = new MrecentExample();
							com.kprd.odds.pojo.MrecentExample.Criteria criteria = mrEx.createCriteria();
							criteria.andPidEqualTo(mvs.getXiid());
							criteria.andXiidEqualTo(xiId);
							criteria.andTeamurlidEqualTo(aTeamUrl);
							List<Mrecent> list = mrMapper.selectByExample(mrEx);
							if(ObjectHelper.isEmpty(list)) {
								mr.setIdrecent(IDUtils.createUUId());
								mrMapper.insert(mr);
							} else {
								mr.setIdrecent(list.get(0).getIdrecent());
								mrMapper.updateByPrimaryKey(mr);
							}
						}
					}
				}
			}
		}
	
	}

	
	/**
	 * 获取主队近10场数据
	 * @param dom
	 * @param mvs
	 */
	private static void getHomeRecent(Document dom,MVs mvs) {
		Element zj1 = dom.select("#team_zhanji_1").get(0);
		Elements tables = zj1.select("table");
		if(ObjectHelper.isNotEmpty(tables)) {
			Element table0 = tables.get(0); 
			String matchColor;
			String matchName;
			String matchTime;
			String lTeam = null;
			String rTeam = null;
			String lScore = null;
			String rScore = null;
			String pankou = null;
			String jieguo = null;
			String panlu = null;
			String bs = null;
			String spfs = null;
			String spfp = null;
			String spff = null;
			String data = null;
			String xiId = null;
			String hTeamUrl = null;
			String hTeamName = null;
			
			Elements teamIds = dom.select(".odds_hd_cont");
			Elements lis = teamIds.get(0).select("table tbody li");
			if(ObjectHelper.isNotEmpty(lis)) {
				hTeamUrl = lis.get(0).select("a").get(0).attr("href").split("team")[1].replace("/", "");
				hTeamName = lis.get(0).select("a").get(0).text();
			}
			
			Elements trs = table0.select("tr");
			if(ObjectHelper.isNotEmpty(trs)) {
				for(int i=2;i<trs.size();i++) {
					Elements tds = trs.get(i).children();
					if(ObjectHelper.isNotEmpty(tds)) {
						matchName = tds.get(0).text();
						matchColor = tds.get(0).attr("bgcolor");
						matchTime = tds.get(1).text();
						xiId =  tds.get(2).select("a").get(0).attr("href");
						xiId = xiId.split("-")[1].split("\\.")[0];
						Elements spans = tds.get(2).select("span");
						if(ObjectHelper.isNotEmpty(spans)) {
							for(Element span : spans) {
								if(span.attr("class").indexOf("dz-l") > -1) {
									lTeam = span.text();
								}
								if(span.attr("class").indexOf("dz-r") > -1) {
									rTeam = span.text();
									break;
								}
							}
						}
						String isVs = tds.get(2).select("em").text();
						if(isVs.indexOf("VS") > - 1) {
							
						} else {
							String[] points = isVs.split(":");
							lScore = points[0];
							rScore = points[1];
						}
						pankou = tds.get(3).text();
						jieguo = tds.get(5).text();
						panlu = tds.get(6).text();
						bs = tds.get(7).text();
						Elements bottom_infos = dom.select("div .bottom_info");
						Elements spanss = bottom_infos.get(0).select("p > span");
						if(ObjectHelper.isNotEmpty(spanss)) {
							Elements sps = spanss.get(0).children();
							spfs = sps.get(0).text().replace("胜", "");
							spfp = sps.get(1).text().replace("平", "");
							spff = sps.get(2).text().replace("负", "");
							data = spanss.get(1).text();
							
							Mrecent mr = new Mrecent();
							mr.setPid(mvs.getXiid());
							mr.setTeamurlid(hTeamUrl);
							mr.setTeamname(hTeamName);
							mr.setMatchcolor(matchColor);
							mr.setMatchname(matchName);
							mr.setMatchtime(matchTime);
							mr.setLteam(lTeam);
							mr.setRteam(rTeam);
							mr.setLscore(lScore);
							mr.setRscore(rScore);
							mr.setPankou(pankou);
							mr.setPanlu(panlu);
							mr.setJieguo(jieguo);
							mr.setBs(bs);
							mr.setSpfs(spfs);
							mr.setSpfp(spfp);
							mr.setSpff(spff);
							mr.setData(data);
							mr.setXiid(xiId);
							mr.setSp1("0");//主队
							
							//左队
							if(lTeam.indexOf("�") > -1) {
								Garbled gb = new Garbled();
								gb.setXiid(xiId);
								GarbledExample gbEx = new GarbledExample();
								com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
								gbC.andGarbledEqualTo(lTeam);
								List<Garbled> gbList = gbMapper.selectByExample(gbEx);
								if(ObjectHelper.isEmpty(gbList)) {
									gb.setIdgarbled(IDUtils.createUUId());
									gb.setGarbled(lTeam);
									gbMapper.insert(gb);
								} else {
									gb.setGarbled(gbList.get(0).getGarbled());
									gb.setIdgarbled(gbList.get(0).getIdgarbled());
									if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
										mr.setLteam(gbList.get(0).getRightcode());
										gb.setRightcode(gbList.get(0).getRightcode());
									}
									gbMapper.updateByPrimaryKey(gb);
								}
							}
							
							//右队
							if(rTeam.indexOf("�") > -1) {
								Garbled gb = new Garbled();
								gb.setXiid(xiId);
								GarbledExample gbEx = new GarbledExample();
								com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
								gbC.andGarbledEqualTo(rTeam);
								List<Garbled> gbList = gbMapper.selectByExample(gbEx);
								if(ObjectHelper.isEmpty(gbList)) {
									gb.setIdgarbled(IDUtils.createUUId());
									gb.setGarbled(rTeam);
									gbMapper.insert(gb);
								} else {
									gb.setGarbled(gbList.get(0).getGarbled());
									gb.setIdgarbled(gbList.get(0).getIdgarbled());
									if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
										mr.setRteam(gbList.get(0).getRightcode());
										gb.setRightcode(gbList.get(0).getRightcode());
									}
									gbMapper.updateByPrimaryKey(gb);
								}
							}
							
							
							MrecentExample mrEx = new MrecentExample();
							com.kprd.odds.pojo.MrecentExample.Criteria criteria = mrEx.createCriteria();
							criteria.andPidEqualTo(mvs.getXiid());
							criteria.andXiidEqualTo(xiId);
							criteria.andTeamurlidEqualTo(hTeamUrl);
							List<Mrecent> list = mrMapper.selectByExample(mrEx);
							if(ObjectHelper.isEmpty(list)) {
								mr.setIdrecent(IDUtils.createUUId());
								mrMapper.insert(mr);
							} else {
								mr.setIdrecent(list.get(0).getIdrecent());
								mrMapper.updateByPrimaryKey(mr);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 未来10场
	 * @param dom
	 * @param mvs
	 */
	private static void getNextTen(Document dom,MVs mvs) {
		String aTeamName = null;
		String hTeamName = null;
		String matchColor;
		String matchName;
		String matchTime;
		String lTeam = null;
		String rTeam = null;
		String interval = null;
		String leagueRanking = null;
		Elements integrals = dom.select(".integral");
		Elements subTitles = integrals.get(0).select(".M_sub_title").select("div").get(0).children();
		hTeamName = subTitles.get(0).text().split("\\[")[0];
		aTeamName = subTitles.get(1).text().split("\\[")[0];
		//主队排名
		leagueRanking = subTitles.get(0).text().split("\\[")[1].replace("]", "");
		Elements contents = integrals.get(0).select(".M_content");
		if(ObjectHelper.isNotEmpty(contents)) {
			Elements diva = contents.get(0).children();
			
			Elements team_a = diva.select(".team_a");
			if(ObjectHelper.isNotEmpty(team_a)) {
				Elements trs = team_a.get(0).select(" table tbody tr");
				if(ObjectHelper.isNotEmpty(trs)) {
					for(int i=1;i<trs.size();i++) {
						if(trs.get(i).attr("class").indexOf("tr") > -1) {
							Elements tds = trs.get(i).children();
							if(ObjectHelper.isNotEmpty(tds) && tds.size() == 4) {
								matchName = tds.get(0).text();
								matchColor = tds.get(0).attr("bgcolor");
								matchTime = tds.get(1).text();
								if(tds.get(2).text().indexOf("VS") == -1) {
									continue;
								}
								lTeam = tds.get(2).text().split("VS")[0];
								rTeam = tds.get(2).text().split("VS")[1];
								interval = tds.get(3).text().replace("天", "");
								
								MFuture mf = new MFuture();
								mf.setXiid(mvs.getXiid());
								mf.setTeamname(hTeamName);
								mf.setMatchcolor(matchColor);
								mf.setMatchname(matchName);
								mf.setMatchtime(matchTime);
								mf.setLteam(lTeam);
								mf.setRteam(rTeam);
								mf.setLeagueranking(leagueRanking);
								mf.setIntervaldays(interval);
								mf.setSp1("1");//主队
								
								//左队
								if(lTeam.indexOf("�") > -1) {
									Garbled gb = new Garbled();
									gb.setXiid(mvs.getXiid());
									GarbledExample gbEx = new GarbledExample();
									com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
									gbC.andGarbledEqualTo(lTeam);
									List<Garbled> gbList = gbMapper.selectByExample(gbEx);
									if(ObjectHelper.isEmpty(gbList)) {
										gb.setIdgarbled(IDUtils.createUUId());
										gb.setGarbled(lTeam);
										gbMapper.insert(gb);
									} else {
										gb.setGarbled(gbList.get(0).getGarbled());
										gb.setIdgarbled(gbList.get(0).getIdgarbled());
										if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
											mf.setLteam(gbList.get(0).getRightcode());
											gb.setRightcode(gbList.get(0).getRightcode());
										}
										gbMapper.updateByPrimaryKey(gb);
									}
								}
								
								//右队
								if(rTeam.indexOf("�") > -1) {
									Garbled gb = new Garbled();
									gb.setXiid(mvs.getXiid());
									GarbledExample gbEx = new GarbledExample();
									com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
									gbC.andGarbledEqualTo(rTeam);
									List<Garbled> gbList = gbMapper.selectByExample(gbEx);
									if(ObjectHelper.isEmpty(gbList)) {
										gb.setIdgarbled(IDUtils.createUUId());
										gb.setGarbled(rTeam);
										gbMapper.insert(gb);
									} else {
										gb.setGarbled(gbList.get(0).getGarbled());
										gb.setIdgarbled(gbList.get(0).getIdgarbled());
										if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
											mf.setRteam(gbList.get(0).getRightcode());
											gb.setRightcode(gbList.get(0).getRightcode());
										}
										gbMapper.updateByPrimaryKey(gb);
									}
								}
								
								MFutureExample mfEx = new MFutureExample();
								com.kprd.odds.pojo.MFutureExample.Criteria criteria = mfEx.createCriteria();
								criteria.andXiidEqualTo(mvs.getXiid());
								criteria.andMatchtimeEqualTo(matchTime);
								criteria.andTeamnameEqualTo(hTeamName);
								List<MFuture> list = mfMapper.selectByExample(mfEx);
								if(ObjectHelper.isEmpty(list)) {
									mf.setIdfuture(IDUtils.createUUId());
									mfMapper.insert(mf);
								} else {
									mf.setIdfuture(list.get(0).getIdfuture());
									mfMapper.updateByPrimaryKey(mf);
								}
							}
						}
					}
				}
			}
			
			Elements sss = diva.select(".team_b");
			Elements trsx  = sss.select("table tbody tr");
			if(ObjectHelper.isNotEmpty(trsx)) {
				for(int i=1;i<trsx.size();i++) {
					if(trsx.get(i).attr("class").indexOf("tr") > -1) {
						Elements tds = trsx.get(i).children();
						if(ObjectHelper.isNotEmpty(tds) && tds.size() == 4) {
							matchName = tds.get(0).text();
							matchColor = tds.get(0).attr("bgcolor");
							matchTime = tds.get(1).text();
							lTeam = tds.get(2).text().split("VS")[0];
							rTeam = tds.get(2).text().split("VS")[1];
							interval = tds.get(3).text().replace("天", "");
							//客队联赛排名
							leagueRanking = subTitles.get(1).text().split("\\[")[1].replace("]", "");
							
							MFuture mf = new MFuture();
							mf.setXiid(mvs.getXiid());
							mf.setTeamname(aTeamName);
							mf.setMatchcolor(matchColor);
							mf.setMatchname(matchName);
							mf.setMatchtime(matchTime);
							mf.setLteam(lTeam);
							mf.setRteam(rTeam);
							mf.setLeagueranking(leagueRanking);
							mf.setIntervaldays(interval);
							mf.setSp1("0");//客队
							
							//左队
							if(lTeam.indexOf("�") > -1) {
								Garbled gb = new Garbled();
								gb.setXiid(mvs.getXiid());
								GarbledExample gbEx = new GarbledExample();
								com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
								gbC.andGarbledEqualTo(lTeam);
								List<Garbled> gbList = gbMapper.selectByExample(gbEx);
								if(ObjectHelper.isEmpty(gbList)) {
									gb.setIdgarbled(IDUtils.createUUId());
									gb.setGarbled(lTeam);
									gbMapper.insert(gb);
								} else {
									gb.setGarbled(gbList.get(0).getGarbled());
									gb.setIdgarbled(gbList.get(0).getIdgarbled());
									if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
										mf.setLteam(gbList.get(0).getRightcode());
										gb.setRightcode(gbList.get(0).getRightcode());
									}
									gbMapper.updateByPrimaryKey(gb);
								}
							}
							
							//右队
							if(rTeam.indexOf("�") > -1) {
								Garbled gb = new Garbled();
								gb.setXiid(mvs.getXiid());
								GarbledExample gbEx = new GarbledExample();
								com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
								gbC.andGarbledEqualTo(rTeam);
								List<Garbled> gbList = gbMapper.selectByExample(gbEx);
								if(ObjectHelper.isEmpty(gbList)) {
									gb.setIdgarbled(IDUtils.createUUId());
									gb.setGarbled(rTeam);
									gbMapper.insert(gb);
								} else {
									gb.setGarbled(gbList.get(0).getGarbled());
									gb.setIdgarbled(gbList.get(0).getIdgarbled());
									if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
										mf.setRteam(gbList.get(0).getRightcode());
										gb.setRightcode(gbList.get(0).getRightcode());
									}
									gbMapper.updateByPrimaryKey(gb);
								}
							}
							
							MFutureExample mfEx = new MFutureExample();
							com.kprd.odds.pojo.MFutureExample.Criteria criteria = mfEx.createCriteria();
							criteria.andXiidEqualTo(mvs.getXiid());
							criteria.andTeamnameEqualTo(aTeamName);
							criteria.andMatchtimeEqualTo(matchTime);
							List<MFuture> list = mfMapper.selectByExample(mfEx);
							if(ObjectHelper.isEmpty(list)) {
								mf.setIdfuture(IDUtils.createUUId());
								mfMapper.insert(mf);
							} else {
								mf.setIdfuture(list.get(0).getIdfuture());
								mfMapper.updateByPrimaryKey(mf);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取赛前杯赛排名
	 * @param dom
	 * @param mvs
	 */
	private static void getScoreRank(Element dom,MVs mvs) {
		Elements ms = dom.select(".M_box");
		if(ObjectHelper.isNotEmpty(ms)) {
			Element obj = null;
			Elements ps = ms.get(0).select("div p");
			if(ObjectHelper.isNotEmpty(ps)) {
				for(Element p : ps) {
					if(p.text().indexOf("杯赛积分排名") > -1) {
						obj = ms.get(0);
						p.text();
						break;
					}
				}
			}
			if(ObjectHelper.isNotEmpty(obj)) {
				Element mcontent = obj.select(".M_content").get(0);
				Elements trs = mcontent.select("table tr");
				Elements h2cs = obj.select(".h2c");
				String group = null;
				if(h2cs.get(0).text().indexOf("-") > -1) {
					group = h2cs.get(0).text().split("- ")[1];
				}
				if(ObjectHelper.isNotEmpty(trs)) {
					for(int i=1;i<trs.size();i++) {
						Elements tds = trs.get(i).children();
						Cupinfo cup = new Cupinfo();
						cup.setRanking(tds.get(0).text());
						cup.setTeamname(tds.get(1).text());
						cup.setGamedtimes(tds.get(2).text());
						cup.setWin(tds.get(3).text());
						cup.setDraw(tds.get(4).text());
						cup.setLose(tds.get(5).text());
						cup.setJq(tds.get(6).text());
						cup.setSq(tds.get(7).text());
						cup.setJsq(tds.get(8).text());
						cup.setJifen(tds.get(9).text());
						cup.setGroupname(group);
						cup.setXiid(mvs.getXiid());
						
						//处理乱码
						if(tds.get(1).text().indexOf("�") > -1) {
							Garbled gb = new Garbled();
							gb.setXiid(mvs.getXiid());
							GarbledExample gbEx = new GarbledExample();
							com.kprd.dic.pojo.GarbledExample.Criteria gbC = gbEx.createCriteria();
							gbC.andGarbledEqualTo(tds.get(1).text());
							List<Garbled> gbList = gbMapper.selectByExample(gbEx);
							if(ObjectHelper.isEmpty(gbList)) {
								gb.setIdgarbled(IDUtils.createUUId());
								gb.setGarbled(tds.get(1).text());
								gbMapper.insert(gb);
							} else {
								gb.setGarbled(gbList.get(0).getGarbled());
								gb.setIdgarbled(gbList.get(0).getIdgarbled());
								if(ObjectHelper.isNotEmpty(gbList.get(0).getRightcode())) {
									cup.setTeamname(gbList.get(0).getRightcode());
									gb.setRightcode(gbList.get(0).getRightcode());
								}
								gbMapper.updateByPrimaryKey(gb);
							}
						}
						
						CupinfoExample cupEx = new CupinfoExample();
						com.kprd.odds.pojo.CupinfoExample.Criteria criteria = cupEx.createCriteria();
						criteria.andXiidEqualTo(mvs.getXiid());
						criteria.andTeamnameEqualTo(cup.getTeamname());
						
						CupinfoMapper cupMapper = Main.applicationContext.getBean(CupinfoMapper.class);
						List<Cupinfo> list = cupMapper.selectByExample(cupEx);
						if(ObjectHelper.isEmpty(list)) {
							cup.setIdcupinfo(IDUtils.createUUId());
							cupMapper.insert(cup);
						} else {
							cup.setIdcupinfo(list.get(0).getIdcupinfo());
							cupMapper.updateByPrimaryKey(cup);
						} 
					}
				}
			}
		}
	}
	
	/**
	 * 获取欧赔压盘
	 * @param curXiId
	 */
	public static void getEuro(String curXiId) {
		String requestUrl = "http://odds.500.com/fenxi1/inc/ajax.php?_=currentTime&t=oupei&cid=0&fid[]=curXiId";
		requestUrl = requestUrl.replace("curXiId", curXiId);
		requestUrl = requestUrl.replace("currentTime", String.valueOf(System.currentTimeMillis()));
		VshistoryExample vshEx = new VshistoryExample();
		com.kprd.odds.pojo.VshistoryExample.Criteria criteria = vshEx.createCriteria();
		criteria.andPidEqualTo(curXiId);
		vshEx.setOrderByClause(" xiId desc");
		List<Vshistory> list = vshMapper.selectByExample(vshEx);
		if(ObjectHelper.isNotEmpty(list)) {
			StringBuffer sb  = new StringBuffer(requestUrl);
			for(int i=0;i<list.size();i++) {
				sb.append("&fid[]=" + list.get(i).getXiid());
			}
			//判断今日
			//sb.append("&sid[]=1");
			for(int i=0;i<list.size()+1;i++) {
				sb.append("&sid[]=5");
			}
			sb.append("&r=1");
			String url = sb.toString();
			String json = UtilBag.getHtmlForGZIP(url, "utf-8");
			JSONObject jsStr = JSONObject.fromObject(json);
			for(int i=0;i<list.size();i++) {
				Object xi = jsStr.get(list.get(i).getXiid());
				JSONObject js = JSONObject.fromObject(xi);
				list.get(i).setEuroagvwin(String.valueOf(js.get("WIN")));
				list.get(i).setEuroavgdraw(String.valueOf(js.get("DRAW")));
				list.get(i).setEuroavglose(String.valueOf(js.get("LOST")));
				vshMapper.updateByPrimaryKeySelective(list.get(i));
			}
		}
		
	}
	
	/**
	 * 获取亚盘
	 * @param curXiId
	 */
	@SuppressWarnings("deprecation")
	public static void getAsia(String curXiId) {
		String requestUrl = "http://odds.500.com/fenxi1/inc/ajax.php?_=currentTime&t=yapan&cid=5&fid[]=curXiId";
		requestUrl = requestUrl.replace("curXiId", curXiId);
		requestUrl = requestUrl.replace("currentTime", String.valueOf(System.currentTimeMillis()));
		VshistoryExample vshEx = new VshistoryExample();
		com.kprd.odds.pojo.VshistoryExample.Criteria criteria = vshEx.createCriteria();
		criteria.andPidEqualTo(curXiId);
		vshEx.setOrderByClause(" xiId desc");
		List<Vshistory> list = vshMapper.selectByExample(vshEx);
		if(ObjectHelper.isNotEmpty(list)) {
			StringBuffer sb  = new StringBuffer(requestUrl);
			for(int i=0;i<list.size();i++) {
				sb.append("&fid[]=" + list.get(i).getXiid());
			}
			//判断今日
			//sb.append("&sid[]=1");
			for(int i=0;i<list.size()+1;i++) {
				sb.append("&sid[]=5");
			}
			sb.append("&r=1");
			String url = sb.toString();
			String json = UtilBag.getHtmlForGZIP(url, "utf-8");
			JSONObject jsStr = JSONObject.fromObject(json);
			for(int i=0;i<list.size();i++) {
				Object xi = jsStr.get(list.get(i).getXiid());
				JSONObject js = JSONObject.fromObject(xi);
				list.get(i).setAisacrownwin(String.valueOf(js.get("HOMEMONEYLINE")));
				list.get(i).setAisacrowndraw(String.valueOf(js.get("HANDICAPLINE")));
				list.get(i).setAisacrownlose(String.valueOf(js.get("AWAYMONEYLINE")));
				String sp2 = String.valueOf(js.get("HANDICAPLINENAME"));
				sp2 = URLDecoder.decode(sp2);
				list.get(i).setSp2(sp2);
				vshMapper.updateByPrimaryKeySelective(list.get(i));
			}
		}
	}
	
	/**
	 * 获取头部信息
	 * @param doc
	 * @return
	 */
	public static Analysishead getHeadInfo(Document doc,String xiId) {
		Analysishead ahead = new Analysishead();
		if(ObjectHelper.isNotEmpty(doc)) {
			Elements odds_hd_conts = doc.select("div .odds_hd_cont");
			Elements forPics = odds_hd_conts.select(".odds_hd_team img");
			if(ObjectHelper.isNotEmpty(forPics)) {
				ahead.setSp1(forPics.get(0).attr("src").trim());
				ahead.setSp2(forPics.get(1).attr("src").trim());
			}
			Elements odds_hd_lists = odds_hd_conts.get(0).select(".odds_hd_list");
			ahead.setXiid(xiId);
			if(ObjectHelper.isNotEmpty(odds_hd_lists)) {
				ahead.setHometeam(odds_hd_lists.get(0).select("a").get(0).text());
				ahead.setAwayteam(odds_hd_lists.get(1).select("a").get(0).text());
				Element bag = odds_hd_lists.get(0);
				if(bag.select("li").size() == 2) {
					ahead.setHomelastseasonrank(bag.select("li").get(1).text().trim());
				}
				
				Element bag2 = odds_hd_lists.get(1);
				if(bag2.select("li").size() == 2) {
					ahead.setAwaylastseasonrank(bag2.select("li").get(1).text().trim());
				}
				String gameTime = doc.select(".game_time").text();
				gameTime = gameTime.substring(4, gameTime.length());
				ahead.setGametime(gameTime);
				String score = doc.select(".odds_hd_bf").select("strong").text();
				ahead.setMatchname(doc.select("#odds_hd_ls .hd_name").text());
				if(!StringUtils.isEmpty(score)) {
					if("VS".equals(score)) {
						
					} else {
						ahead.setHomescore(score.split(":")[0]);
						ahead.setAwayscore(score.split(":")[1]);
					}
				}
			}
			
			
//			if(ahead.getHometeam().indexOf("�") > -1) {
//				MVsExample mvsEx = new MVsExample();
//				com.kprd.liansai.pojo.MVsExample.Criteria mvsC = mvsEx.createCriteria();
//				mvsC.andXiidEqualTo(xiId);
//				List<MVs> mvsList = mvsMapper.selectByExample(mvsEx);
//				if(ObjectHelper.isNotEmpty(mvsList)) {
//					String mvsFullName = mvsList.get(0).getHomeFullname();
//					ahead.setHometeam(mvsFullName);
//				}
//			}
//			if(ahead.getAwayteam().indexOf("�") > -1) {
//				MVsExample mvsEx = new MVsExample();
//				com.kprd.liansai.pojo.MVsExample.Criteria mvsC = mvsEx.createCriteria();
//				mvsC.andXiidEqualTo(xiId);
//				List<MVs> mvsList = mvsMapper.selectByExample(mvsEx);
//				if(ObjectHelper.isNotEmpty(mvsList)) {
//					String mvsFullName = mvsList.get(0).getAwayFullname();
//					ahead.setAwayteam(mvsFullName);
//				}
//			}
			
			AnalysisheadExample ex = new AnalysisheadExample();
			com.kprd.odds.pojo.AnalysisheadExample.Criteria criteria = ex.createCriteria();
			criteria.andXiidEqualTo(xiId);
			List<Analysishead> list = analysisheadMapper.selectByExample(ex);
			if(ObjectHelper.isEmpty(list)) {
				ahead.setIdheadinfo(IDUtils.createUUId());
				analysisheadMapper.insert(ahead);
			} else {
				ahead.setIdheadinfo(list.get(0).getIdheadinfo());
				analysisheadMapper.updateByPrimaryKey(ahead);
			}
		}
		return ahead;
	}
	
	/**
	 * 获取国际足联排名
	 * @param doc
	 * @return
	 */
	public static Analysisgjzlpm getInterNationalInfo(Document doc,String xiId) {
		Analysisgjzlpm interNationalInfo = new Analysisgjzlpm();
		Elements elements = doc.select(".M_title");
		for(Element element : elements) {
			if(element.select("h4").text().indexOf("国际足联排名") > -1) {
				Element dom = element.nextElementSibling();
				String homeTeam = null;
				String awayTeam = null;
				if(ObjectHelper.isNotEmpty(dom)) {
					Elements h3s = dom.select("h3");
					if(ObjectHelper.isNotEmpty(h3s) && h3s.size() == 2) {
						homeTeam = h3s.get(0).text();
						awayTeam = h3s.get(1).text();
						if(homeTeam.indexOf("[") > -1) { 
							homeTeam = homeTeam.split("\\[")[0];
						}
						if(awayTeam.indexOf("[") > -1) {
							awayTeam = awayTeam.split("\\[")[0];
						}
					}
				}
				Elements tables = dom.select("table");
				if(ObjectHelper.isNotEmpty(tables) && tables.size() == 2) {
					//主队table
					Element table = tables.get(0);
					Elements trs1 = table.select("tr");
					for(int i=1;i<trs1.size();i++) {
						Elements tds = trs1.get(i).children();
						if(ObjectHelper.isNotEmpty(tds) && tds.size() == 5){ 
							interNationalInfo.setMonths(tds.get(0).text().trim());
							interNationalInfo.setWorldranking(tds.get(1).text().trim());
							interNationalInfo.setRankchange(tds.get(2).text().trim());
							interNationalInfo.setJifen(tds.get(3).text().trim());
							interNationalInfo.setJifenbianhua(tds.get(4).text().trim());
							interNationalInfo.setHometeam(homeTeam);
							//主场
							interNationalInfo.setZhuke("1");
							interNationalInfo.setXiid(xiId);
							AnalysisgjzlpmExample ex = new AnalysisgjzlpmExample();
							com.kprd.odds.pojo.AnalysisgjzlpmExample.Criteria criteria = ex.createCriteria();
							criteria.andXiidEqualTo(xiId);
							criteria.andMonthsEqualTo(tds.get(0).text().trim());
							criteria.andWorldrankingEqualTo(tds.get(1).text().trim());
							criteria.andZhukeEqualTo("1");
							List<Analysisgjzlpm> homeList = analysisgjzlpmMapper.selectByExample(ex);
							if(ObjectHelper.isEmpty(homeList)) {
								interNationalInfo.setIdinter(IDUtils.createUUId());
								analysisgjzlpmMapper.insert(interNationalInfo);
							} else {
								interNationalInfo.setIdinter(homeList.get(0).getIdinter());
								analysisgjzlpmMapper.updateByPrimaryKey(interNationalInfo);
							}
						}
					}
					
					interNationalInfo = new Analysisgjzlpm();
					
					//客队table
					Element table2 = tables.get(1);
					Elements trs2 = table2.select("tr");
					for(int i=1;i<trs2.size();i++) {
						Elements tds = trs2.get(i).children();
						if(ObjectHelper.isNotEmpty(tds) && tds.size() == 5){ 
							interNationalInfo.setMonths(tds.get(0).text().trim());
							interNationalInfo.setWorldranking(tds.get(1).text().trim());
							interNationalInfo.setRankchange(tds.get(2).text().trim());
							interNationalInfo.setJifen(tds.get(3).text().trim());
							interNationalInfo.setJifenbianhua(tds.get(4).text().trim());
							interNationalInfo.setAwayteam(awayTeam);
							//客队
							interNationalInfo.setZhuke("2");
							interNationalInfo.setXiid(xiId);
							AnalysisgjzlpmExample ex = new AnalysisgjzlpmExample();
							com.kprd.odds.pojo.AnalysisgjzlpmExample.Criteria criteria = ex.createCriteria();
							criteria.andXiidEqualTo(xiId);
							criteria.andMonthsEqualTo(tds.get(0).text().trim());
							criteria.andWorldrankingEqualTo(tds.get(1).text().trim());
							criteria.andZhukeEqualTo("2");
							List<Analysisgjzlpm> awayList = analysisgjzlpmMapper.selectByExample(ex);
							if(ObjectHelper.isEmpty(awayList)) {
								interNationalInfo.setIdinter(IDUtils.createUUId());
								analysisgjzlpmMapper.insert(interNationalInfo);
							} else {
								interNationalInfo.setIdinter(awayList.get(0).getIdinter());
								analysisgjzlpmMapper.updateByPrimaryKey(interNationalInfo);
							}
						}
					}
				}
				break;
			}
		}
		return interNationalInfo;
	}
	/**
	 * 赛前联赛积分排名
	 * @param doc
	 * @param xiId
	 */
	public static void getLeagueScore(Element doc,String xiId) {
		MXiscoreranking mxi = new MXiscoreranking();
		Elements elements = doc.select(".M_title");
		for(Element element : elements) {
			if(element.select("h4").text().indexOf("赛前联赛积分排名") > -1) {
				Element dom = element.nextElementSibling();
				Elements tmDivs = dom.select(".team_name");
				String homeTeam = null;
				String awayTeam = null;
				String homeRank = null;
				String awayRank = null;
				if(ObjectHelper.isNotEmpty(tmDivs)) {
					homeTeam = tmDivs.get(0).text();
					awayTeam = tmDivs.get(1).text();
					if(homeTeam.indexOf("[") > -1 && awayTeam.indexOf("[") > -1) {
						homeRank = homeTeam.split("\\[")[1].replace("]", "");
						awayRank = awayTeam.split("\\[")[1].replace("]", "");
						homeTeam = homeTeam.split("\\[")[0];
						awayTeam = awayTeam.split("\\[")[0];
					}
				}
				
				dom = dom.nextElementSibling();
				Elements divs = dom.children();
				if(ObjectHelper.isNotEmpty(divs) && divs.size() == 3){
					//主队
					Element homeDiv = divs.get(0);
					Elements homeTables = homeDiv.select("table");
					if(ObjectHelper.isNotEmpty(homeTables)) {
						Elements homeTrs = homeTables.get(0).select("tr");
						if(ObjectHelper.isNotEmpty(homeTrs) && homeTrs.size() == 4) {
							Elements homeTds = homeTrs.get(1).children();
							if(ObjectHelper.isNotEmpty(homeTds)) {
								mxi.setTotaltimes(homeTds.get(1).text().trim());
								mxi.setTotalwin(homeTds.get(2).text().trim());
								mxi.setTotaldraw(homeTds.get(3).text().trim());
								mxi.setTotallose(homeTds.get(4).text().trim());
								mxi.setTotalgoal(homeTds.get(5).text().trim());
								mxi.setTotallost(homeTds.get(6).text().trim());
								mxi.setTotaljing(homeTds.get(7).text().trim());
								mxi.setTotaljifen(homeTds.get(8).text().trim());
								mxi.setTotalranking(homeTds.get(9).text().trim());
								mxi.setTotalwinrate(homeTds.get(10).text().trim());
							}
							
							Elements homeTds2 = homeTrs.get(2).children();
							if(ObjectHelper.isNotEmpty(homeTds2)) {
								mxi.setHgametimes(homeTds2.get(1).text().trim());
								mxi.setHwin(homeTds2.get(2).text().trim());
								mxi.setHdraw(homeTds2.get(3).text().trim());
								mxi.setHlose(homeTds2.get(4).text().trim());
								mxi.setHgoal(homeTds2.get(5).text().trim());
								mxi.setHlost(homeTds2.get(6).text().trim());
								mxi.setHjing(homeTds2.get(7).text().trim());
								mxi.setHjifen(homeTds2.get(8).text().trim());
								mxi.setHranking(homeTds2.get(9).text().trim());
								mxi.setHwinrate(homeTds2.get(10).text().trim());
							}
							
							Elements homeTds3 = homeTrs.get(3).children();
							if(ObjectHelper.isNotEmpty(homeTds3)) {
								mxi.setAgametimes(homeTds3.get(1).text().trim());
								mxi.setAwin(homeTds3.get(2).text().trim());
								mxi.setAdraw(homeTds3.get(3).text().trim());
								mxi.setAlose(homeTds3.get(4).text().trim());
								mxi.setAgoal(homeTds3.get(5).text().trim());
								mxi.setAlost(homeTds3.get(6).text().trim());
								mxi.setAjing(homeTds3.get(7).text().trim());
								mxi.setAjifen(homeTds3.get(8).text().trim());
								mxi.setAranking(homeTds3.get(9).text().trim());
								mxi.setAwinrate(homeTds3.get(10).text().trim());
							}
							mxi.setTeamtype("1");
							mxi.setTeamname(homeTeam);
							mxi.setXiid(xiId);
							mxi.setSp2(homeRank);
							
							MXiscorerankingExample mxiEx1 = new MXiscorerankingExample();
							Criteria c1 = mxiEx1.createCriteria();
							c1.andXiidEqualTo(xiId);
							c1.andTeamnameEqualTo(homeTeam);
							c1.andSp2EqualTo(homeRank);
							List<MXiscoreranking> list1 = mxiMapper.selectByExample(mxiEx1);
							if(ObjectHelper.isEmpty(list1)) {
								mxi.setIdana(IDUtils.createUUId());
								mxiMapper.insert(mxi);
							} else {
								mxi.setIdana(list1.get(0).getIdana());
								mxiMapper.updateByPrimaryKey(mxi);
							}
						}
					}
					//客队
					Element awayDiv = divs.get(1);
					mxi = new MXiscoreranking();
					Elements awayTables = awayDiv.select("table");
					if(ObjectHelper.isNotEmpty(awayTables)) {
						Elements awayTrs = awayTables.get(0).select("tr");
						if(ObjectHelper.isNotEmpty(awayTrs) && awayTrs.size() == 4) {
							Elements awayTds = awayTrs.get(1).children();
							if(ObjectHelper.isNotEmpty(awayTds)) {
								mxi.setTotaltimes(awayTds.get(1).text().trim());
								mxi.setTotalwin(awayTds.get(2).text().trim());
								mxi.setTotaldraw(awayTds.get(3).text().trim());
								mxi.setTotallose(awayTds.get(4).text().trim());
								mxi.setTotalgoal(awayTds.get(5).text().trim());
								mxi.setTotallost(awayTds.get(6).text().trim());
								mxi.setTotaljing(awayTds.get(7).text().trim());
								mxi.setTotaljifen(awayTds.get(8).text().trim());
								mxi.setTotalranking(awayTds.get(9).text().trim());
								mxi.setTotalwinrate(awayTds.get(10).text().trim());
							}
							
							Elements awayTds2 = awayTrs.get(2).children();
							if(ObjectHelper.isNotEmpty(awayTds2)) {
								mxi.setHgametimes(awayTds2.get(1).text().trim());
								mxi.setHwin(awayTds2.get(2).text().trim());
								mxi.setHdraw(awayTds2.get(3).text().trim());
								mxi.setHlose(awayTds2.get(4).text().trim());
								mxi.setHgoal(awayTds2.get(5).text().trim());
								mxi.setHlost(awayTds2.get(6).text().trim());
								mxi.setHjing(awayTds2.get(7).text().trim());
								mxi.setHjifen(awayTds2.get(8).text().trim());
								mxi.setHranking(awayTds2.get(9).text().trim());
								mxi.setHwinrate(awayTds2.get(10).text().trim());
							}
							
							Elements awayTds3 = awayTrs.get(3).children();
							if(ObjectHelper.isNotEmpty(awayTds3)) {
								mxi.setAgametimes(awayTds3.get(1).text().trim());
								mxi.setAwin(awayTds3.get(2).text().trim());
								mxi.setAdraw(awayTds3.get(3).text().trim());
								mxi.setAlose(awayTds3.get(4).text().trim());
								mxi.setAgoal(awayTds3.get(5).text().trim());
								mxi.setAlost(awayTds3.get(6).text().trim());
								mxi.setAjing(awayTds3.get(7).text().trim());
								mxi.setAjifen(awayTds3.get(8).text().trim());
								mxi.setAranking(awayTds3.get(9).text().trim());
								mxi.setAwinrate(awayTds3.get(10).text().trim());
							}
							mxi.setTeamtype("0");
							mxi.setTeamname(awayTeam);
							mxi.setXiid(xiId);
							mxi.setSp2(awayRank);
							
							MXiscorerankingExample mxiEx2 = new MXiscorerankingExample();
							Criteria c2 = mxiEx2.createCriteria();
							c2.andXiidEqualTo(xiId);
							c2.andTeamnameEqualTo(awayTeam);
							c2.andSp2EqualTo(awayRank);
							List<MXiscoreranking> list2 = mxiMapper.selectByExample(mxiEx2);
							if(ObjectHelper.isEmpty(list2)) {
								mxi.setIdana(IDUtils.createUUId());
								mxiMapper.insert(mxi);
							} else {
								mxi.setIdana(list2.get(0).getIdana());
								mxiMapper.updateByPrimaryKey(mxi);
							}
						}
					}
				}
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		
		String xii = "570419";
		
		MVs mVs = new MVs();
		mVs.setXiid(xii);//570413世外亚洲
		getAnalysis(mVs);
//		getAsia(xii);
//		MVsMapper mvsMapper = Main.applicationContext.getBean(MVsMapper.class);
//		List<MVs> mVsList = mvsMapper.selectByExample(null);-
//		if(ObjectHelper.isNotEmpty(mVsList)) {
//			for(MVs m : mVsList) {
//				if("570386".equals(m.getXiid())) {
//					System.out.println("123132132");
//				}
//				getAnalysis(m);
//			}
//		}
	}
}
