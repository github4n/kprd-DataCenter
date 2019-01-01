package com.kprd.date.fetch.wubai.football;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.FoFutureMapper;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.mapper.FoRecentMapper;
import com.kprd.newliansai.mapper.FoRelationTeamMapper;
import com.kprd.newliansai.mapper.FoVshistoryMapper;
import com.kprd.newliansai.pojo.FoFuture;
import com.kprd.newliansai.pojo.FoFutureExample;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;
import com.kprd.newliansai.pojo.FoRecent;
import com.kprd.newliansai.pojo.FoRecentExample;
import com.kprd.newliansai.pojo.FoRelationTeam;
import com.kprd.newliansai.pojo.FoRelationTeamExample;
import com.kprd.newliansai.pojo.FoVshistory;
import com.kprd.newliansai.pojo.FoVshistoryExample;

public class WubaiAnalysis {

	//分析页面
	private static String analysisUrl = "http://odds.500.com/fenxi/shuju-xiId.shtml";
	
	//对阵mapper
	static FoMixMapper mvsMapper = Main.applicationContext.getBean(FoMixMapper.class);
	//交战历史mapper
	static FoRecentMapper vsMapper = Main.applicationContext.getBean(FoRecentMapper.class);
	//队伍名字过滤
	static FoRelationTeamMapper rtMapper = Main.applicationContext.getBean(FoRelationTeamMapper.class);
	//未来赛事mapper
	static FoFutureMapper futureMapper = Main.applicationContext.getBean(FoFutureMapper.class);
	//交战历史mapper
	static FoVshistoryMapper vsHistoryMapper = Main.applicationContext.getBean(FoVshistoryMapper.class);
	
	private static Document getDom(String xiid) {
		Document doc = null;
		analysisUrl = analysisUrl.replace("xiId", xiid);
		String html = UtilBag.getHtmlForGZIP(analysisUrl, "gb2312");
		analysisUrl = "http://odds.500.com/fenxi/shuju-xiId.shtml";
		if(ObjectHelper.isNotEmpty(html)) {
		    doc = Jsoup.parse(html);
		}
		return doc;
	}
	
	/**
	 * 获取未来比赛
	 * @param idUnique
	 * @param xiid
	 * @param ha
	 */
	public static void getFutureGames(String idUnique,String xiid,String ha) {
		try {
			if(ObjectHelper.isEmpty(idUnique) || ObjectHelper.isEmpty(xiid)) {
				System.out.println("获取未来比赛参数 为空");
				return;
			}
			Document dom = getDom(xiid);
			if(ObjectHelper.isNotEmpty(dom)) {
				if("0".equals(ha)) {
					Elements integrals = dom.select(".integral");
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
											FoFuture fu = new FoFuture();
											fu.setLeaguename(tds.get(0).text());
											fu.setLeaguecolor(tds.get(0).attr("bgcolor"));
											String href = tds.get(0).select("a").get(0).attr("href");
											if(ObjectHelper.isNotEmpty(href)) {
												href = href.split("zuqiu-")[1].replace("/", "");
												fu.setLeagueid(href);
											}
											fu.setGametime(tds.get(1).text());
											Elements as = tds.get(2).select("a");
											String fn = getJcFullName(as.get(0).attr("title"));
											if(ObjectHelper.isNotEmpty(fn)) {
												fu.setHometeamfull(as.get(0).attr("title"));
											}
											fu.setHometeamshort(as.get(0).text());
											String teamHref = as.get(0).attr("href");
											teamHref = teamHref.split("team/")[1].replace("/", "");
											fu.setHometeamid(teamHref);
											
											Elements as1 = tds.get(2).select("a");
											String an = getJcFullName(as1.get(1).attr("title"));
											if(ObjectHelper.isNotEmpty(an)) {
												fu.setAwayteamfull(an);
											}
											fu.setAwayteamshort(as1.get(1).text());
											String teamHre = as1.get(1).attr("href");
											teamHre = teamHre.split("team/")[1].replace("/", "");
											fu.setAwayteamid(teamHre);
											fu.setHomeoraway(ha);
											FoFutureExample fuEx = new FoFutureExample();
											if(ObjectHelper.isEmpty(fu.getHometeamfull()) || ObjectHelper.isEmpty(fu.getAwayteamfull())) {
												System.out.println("数据量不够，未来赛事队伍不能入库");
												continue;
											}
											fuEx.createCriteria().andHometeamfullEqualTo(fu.getHometeamfull())
											.andAwayteamfullEqualTo(fu.getAwayteamfull()).andGametimeEqualTo(fu.getGametime());
											List<FoFuture> fuList = futureMapper.selectByExample(fuEx);
											if(ObjectHelper.isEmpty(fuList)) {
												fu.setId(IDUtils.createUUId());
												fu.setIdunique(idUnique);
												futureMapper.insert(fu);
											} else { 
												fu.setId(fuList.get(0).getId());
												fu.setIdunique(fuList.get(0).getIdunique());
												futureMapper.updateByPrimaryKeySelective(fu);
											}
										}
									}
								}
							}
						}
					}
				} else {
					Elements integrals = dom.select(".integral");
					Elements contents = integrals.get(0).select(".M_content");
					if(ObjectHelper.isNotEmpty(contents)) {
						Elements diva = contents.get(0).children();
						Elements team_a = diva.select(".team_b");
						if(ObjectHelper.isNotEmpty(team_a)) {
							Elements trs = team_a.get(0).select(" table tbody tr");
							if(ObjectHelper.isNotEmpty(trs)) {
								for(int i=1;i<trs.size();i++) {
									if(trs.get(i).attr("class").indexOf("tr") > -1) {
										Elements tds = trs.get(i).children();
										if(ObjectHelper.isNotEmpty(tds) && tds.size() == 4) {
											FoFuture fu = new FoFuture();
											fu.setLeaguename(tds.get(0).text());
											fu.setLeaguecolor(tds.get(0).attr("bgcolor"));
											String href = tds.get(0).select("a").get(0).attr("href");
											if(ObjectHelper.isNotEmpty(href)) {
												href = href.split("zuqiu-")[1].replace("/", "");
												fu.setLeagueid(href);
											}
											fu.setGametime(tds.get(1).text());
											Elements as = tds.get(2).select("a");
											String fn = getJcFullName(as.get(0).attr("title"));
											if(ObjectHelper.isNotEmpty(fn)) {
												fu.setHometeamfull(fn);
											}
											fu.setHometeamshort(as.get(0).text());
											String teamHref = as.get(0).attr("href");
											teamHref = teamHref.split("team/")[1].replace("/", "");
											fu.setHometeamid(teamHref);
											
											Elements as1 = tds.get(2).select("a");
											String an = getJcFullName(as1.get(1).attr("title"));
											if(ObjectHelper.isNotEmpty(an)) {
												fu.setAwayteamfull(an);
											}
											fu.setAwayteamshort(as1.get(1).text());
											String teamHre = as1.get(1).attr("href");
											teamHre = teamHre.split("team/")[1].replace("/", "");
											fu.setAwayteamid(teamHre);
											fu.setHomeoraway(ha);
											FoFutureExample fuEx = new FoFutureExample();
											if(ObjectHelper.isEmpty(fu.getHometeamfull()) || ObjectHelper.isEmpty(fu.getAwayteamfull())) {
												System.out.println("数据量不够，未来赛事队伍不能入库");
												continue;
											}
											fuEx.createCriteria().andHometeamfullEqualTo(fu.getHometeamfull())
											.andAwayteamfullEqualTo(fu.getAwayteamfull()).andGametimeEqualTo(fu.getGametime());
											List<FoFuture> fuList = futureMapper.selectByExample(fuEx);
											if(ObjectHelper.isEmpty(fuList)) {
												fu.setId(IDUtils.createUUId());
												fu.setIdunique(idUnique);
												futureMapper.insert(fu);
											} else { 
												fu.setId(fuList.get(0).getId());
												fu.setIdunique(fuList.get(0).getIdunique());
												futureMapper.updateByPrimaryKeySelective(fu);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("ConnectException") > -1) {
				try {
					System.out.println("获取未来比赛请求超时，等待1分钟");
					Thread.sleep(1000*60);
					getvsHistory(idUnique, xiid);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取交战历史
	 * @param idUnique
	 * @param xiid
	 */
	public static void getvsHistory(String idUnique,String xiid) {
		try {
			if(ObjectHelper.isEmpty(idUnique) || ObjectHelper.isEmpty(xiid)) {
				System.out.println("获取交战历史 参数为空！");
				return;
			}
			Document dom = getDom(xiid);
			if(ObjectHelper.isNotEmpty(dom)) {
				Element team_jiaozhan = dom.select("#team_jiaozhan").get(0);
				if(ObjectHelper.isNotEmpty(team_jiaozhan)) {
					Elements m_contents = team_jiaozhan.select(".M_content");
					if(ObjectHelper.isNotEmpty(m_contents)) {
						Elements trs = m_contents.select("table > tbody > tr");
						if(ObjectHelper.isNotEmpty(trs)) {
							for(int i=2;i<trs.size();i++) {
								if(ObjectHelper.isNotEmpty(trs.get(i).attr("fid"))) {
									Elements tds = trs.get(i).children();
									if(ObjectHelper.isNotEmpty(tds)) {
										FoVshistory vs = new FoVshistory();
										vs.setGameid500(trs.get(i).attr("fid"));
										vs.setLeaguecolor(tds.get(0).attr("bgcolor"));
										Element aElement = tds.get(0).select("a").get(0);
										String href = aElement.attr("href");
										href = href.split("zuqiu-")[1];
										href = href.replace("/", "");
										vs.setLeagueid(href);
										vs.setLeaguename(aElement.text());
										vs.setGametime(tds.get(1).text());
										Elements ems = tds.get(2).select("em");
										if(ObjectHelper.isNotEmpty(ems)) {
											vs.setHomescore(ems.text().split(":")[0]);
											vs.setAwayscore(ems.text().split(":")[1]);
										}
										String teamsStr = tds.get(2).text();
										if(ObjectHelper.isNotEmpty(teamsStr) && teamsStr.indexOf("VS") == -1) {
											Elements spans = tds.get(2).select("span");
											Element dz_l = spans.select(".dz-l").get(0);
											String dxl = dz_l.text();
											
											if(dxl.indexOf("]") > -1) {
												vs.setHometeamshort(dxl.split("\\]")[1]);
											}
											Element dz_r = spans.select(".dz-r").get(0);
											String dzr = dz_r.text();
											if(dzr.indexOf("[") > -1) {
												vs.setAwayteamshort(dzr.split("\\[")[0]);
											}
											String finalScore = tds.get(3).text();
											if(ObjectHelper.isNotEmpty(finalScore) && finalScore.indexOf(":") > -1) {
												vs.setHomehalf(finalScore.split(":")[0]);
												vs.setAwayhalf(finalScore.split(":")[1]);
											}
											vs.setIdunique(idUnique);
											vs.setPanlu(tds.get(7).text());
											FoVshistoryExample vsEx = new FoVshistoryExample();
											vsEx.createCriteria().andGametimeEqualTo(vs.getGametime());
											List<FoVshistory> vsList = vsHistoryMapper.selectByExample(vsEx);
											if(ObjectHelper.isEmpty(vsList)) {
												vs.setId(IDUtils.createUUId());
												vsHistoryMapper.insert(vs);
											} else {
												vs.setId(vsList.get(0).getId());
												vsHistoryMapper.updateByPrimaryKeySelective(vs);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("ConnectException") > -1) {
				try {
					System.out.println("获取交战历史请求超时，等待1分钟");
					Thread.sleep(1000*60);
					getvsHistory(idUnique, xiid);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取近期比赛
	 * @param idUnique
	 * @param xiid
	 * @param ha
	 */
	public static void getRecentGames(String idUnique,String xiid,String ha) {
		try {
			if(ObjectHelper.isEmpty(idUnique) || ObjectHelper.isEmpty(xiid)) {
				System.out.println("获取近期比赛参数为空！");
				return;
			}
			Document doc = getDom(xiid);
				Element zj1 = null;
				if("0".equals(ha)) {
					//客队
					zj1 = doc.select("#team_zhanji_0").get(0);
					Elements tables = zj1.select("table");
					if(ObjectHelper.isNotEmpty(tables)) {
						Element table = tables.get(0);
						Elements trs = table.select("tr");
						if(ObjectHelper.isNotEmpty(trs)) {
							for(int i=2;i<trs.size();i++) {
								Elements tds = trs.get(i).select("td");
								if(ObjectHelper.isNotEmpty(tds)) {
									FoRecent recentHome = new FoRecent();
									
									String xiId =  tds.get(2).select("a").get(0).attr("href");
									xiId = xiId.split("-")[1].split("\\.")[0];
									recentHome.setId500(xiId);
									
									Elements spans = tds.get(2).select("span");
									if(ObjectHelper.isNotEmpty(spans)) {
										for(Element span : spans) {
											if(span.attr("class").indexOf("dz-l") > -1) {
												String lTeam = span.text();
												String homef = getJcFullName(lTeam);
												if(ObjectHelper.isNotEmpty(homef)) {
													recentHome.setHometeamfull(homef);
												}
											}
											if(span.attr("class").indexOf("dz-r") > -1) {
												String rTeam = span.text();
												String awayF = getJcFullName(rTeam);
												if(ObjectHelper.isNotEmpty(awayF)) {
													recentHome.setAwayteamfull(awayF);
												}
												break;
											}
										}
									}
									String isVs = tds.get(2).select("em").text();
									if(isVs.indexOf("VS") > - 1) {
										
									} else {
										String[] points = isVs.split(":");
										recentHome.setHomescore(points[0]);
										recentHome.setAwayscore(points[1]);
									}
									
									
									recentHome.setLeaguecolor(tds.get(0).attr("bgcolor"));
									String leagueId = tds.get(0).select("a").get(0).attr("href");
									leagueId = leagueId.split("-")[1].replace("/", "");
									recentHome.setLeaguename(tds.get(0).select("a").text());
									recentHome.setLeagueid(leagueId);
									String year = UtilBag.dateUtil(0);
									year = year.substring(0, 2);
									year = year + tds.get(1).text();
									recentHome.setGametime(year);
									recentHome.setPanlu(tds.get(6).text());
									recentHome.setHomeoraway(ha);
									FoRecentExample reEx = new FoRecentExample();
									if(ObjectHelper.isEmpty(recentHome.getHometeamfull()) || ObjectHelper.isEmpty(recentHome.getAwayteamfull())) {
										//目前为空的原因是因为数据量太少
										System.out.println(spans.text() + "数据太少，无法录入队名，跳过");
										continue;
									}
									reEx.createCriteria().andGametimeEqualTo(year).andHometeamfullEqualTo(recentHome.getHometeamfull())
									.andAwayteamfullEqualTo(recentHome.getAwayteamfull());
									List<FoRecent> reList = vsMapper.selectByExample(reEx);
									if(ObjectHelper.isEmpty(reList)) {
										recentHome.setIdrecent(IDUtils.createUUId());
										recentHome.setIdunique(idUnique);
										vsMapper.insert(recentHome);
									} else {
										recentHome.setIdrecent(reList.get(0).getIdrecent());
										recentHome.setIdunique(reList.get(0).getIdunique());
										vsMapper.updateByPrimaryKeySelective(recentHome);
									}
								}
							}
						}
					}
				} else if("1".equals(ha)) {
					//主队
					zj1 = doc.select("#team_zhanji_1").get(0);
					Elements tables = zj1.select("table");
					if(ObjectHelper.isNotEmpty(tables)) {
						Element table = tables.get(0);
						Elements trs = table.select("tr");
						if(ObjectHelper.isNotEmpty(trs)) {
							for(int i=2;i<trs.size();i++) {
								Elements tds = trs.get(i).select("td");
//								System.out.println(tds);
								if(ObjectHelper.isNotEmpty(tds)) {
									FoRecent recentHome = new FoRecent();
									
									String xiId =  tds.get(2).select("a").get(0).attr("href");
									xiId = xiId.split("-")[1].split("\\.")[0];
									recentHome.setId500(xiId);
									
									Elements spans = tds.get(2).select("span");
									if(ObjectHelper.isNotEmpty(spans)) {
										for(Element span : spans) {
											if(span.attr("class").indexOf("dz-l") > -1) {
												String lTeam = span.text();
												recentHome.setHometeamfull(getJcFullName(lTeam));
											}
											if(span.attr("class").indexOf("dz-r") > -1) {
												String rTeam = span.text();
												recentHome.setAwayteamfull(getJcFullName(rTeam));
												break;
											}
										}
									}
									String isVs = tds.get(2).select("em").text();
									if(isVs.indexOf("VS") > - 1) {
										
									} else {
										String[] points = isVs.split(":");
										recentHome.setHomescore(points[0]);
										recentHome.setAwayscore(points[1]);
									}
									
									recentHome.setLeaguecolor(tds.get(0).attr("bgcolor"));
									String leagueId = tds.get(0).select("a").get(0).attr("href");
									leagueId = leagueId.split("-")[1].replace("/", "");
									recentHome.setLeaguename(tds.get(0).select("a").text());
									recentHome.setLeagueid(leagueId);
									String year = UtilBag.dateUtil(0);
									year = year.substring(0, 2);
									year = year + tds.get(1).text();
									recentHome.setGametime(year);
									recentHome.setPanlu(tds.get(6).text());
									recentHome.setHomeoraway(ha);
									FoRecentExample reEx = new FoRecentExample();
									if(ObjectHelper.isEmpty(recentHome.getHometeamfull()) || ObjectHelper.isEmpty(recentHome.getAwayteamfull())) {
										//目前为空的原因是因为数据量太少
										System.out.println(spans.text() + "数据太少，无法录入队名，跳过");
										continue;
									}
									reEx.createCriteria().andGametimeEqualTo(year).andHometeamfullEqualTo(recentHome.getHometeamfull())
									.andAwayteamfullEqualTo(recentHome.getAwayteamfull());
									List<FoRecent> reList = vsMapper.selectByExample(reEx);
									if(ObjectHelper.isEmpty(reList)) {
										recentHome.setIdrecent(IDUtils.createUUId());
										recentHome.setIdunique(idUnique);
										vsMapper.insert(recentHome);
									} else {
										recentHome.setIdrecent(reList.get(0).getIdrecent());
										recentHome.setIdunique(reList.get(0).getIdunique());
										vsMapper.updateByPrimaryKeySelective(recentHome);
									}
								}
							}
						}
					}
				}
		} catch (Exception e) {
			if(e.getMessage().indexOf("ConnectException") > -1) {
				try {
					System.out.println("获取近期比赛请求超时，等待1分钟");
					Thread.sleep(1000*60);
					getvsHistory(idUnique, xiid);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取队伍统一名称
	 * @param nickName
	 * @return
	 */
	private static String getJcFullName(String nickName) {
		String fullName = null;
		FoRelationTeamExample rtEx = new FoRelationTeamExample();
		rtEx.createCriteria().andWubaiteamshortnameEqualTo(nickName);
		List<FoRelationTeam> rtList = rtMapper.selectByExample(rtEx);
		if(ObjectHelper.isNotEmpty(rtList)) {
			fullName = rtList.get(0).getJcteamfullname();
		}
		return fullName;
	}
	
	public static void main(String[] args) {
		FoMixExample mixEx = new FoMixExample();
		mixEx.createCriteria().andIduniqueEqualTo("201708255001");
		List<FoMix> mixList = mvsMapper.selectByExample(mixEx);
		if(ObjectHelper.isNotEmpty(mixList)) {
			FoMix mix = mixList.get(0);
//			//近期
			getRecentGames(mix.getIdunique(),mix.getWubaiid(),"0");
			getRecentGames(mix.getIdunique(),mix.getWubaiid(),"1");
//			//未来
			getFutureGames(mix.getIdunique(),mix.getWubaiid(),"0");
			getFutureGames(mix.getIdunique(),mix.getWubaiid(),"1");
			//交战历史
			getvsHistory(mix.getIdunique(),mix.getWubaiid());
		}
	}
}
