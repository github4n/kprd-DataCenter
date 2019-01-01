package com.kprd.date.zq;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.liansai.pojo.MVs;
import com.kprd.zucai.mapper.Zucai4Mapper;
import com.kprd.zucai.pojo.Zucai4;
import com.kprd.zucai.pojo.Zucai4Example;
import com.kprd.zucai.pojo.Zucai4Example.Criteria;

public class ZucaiFourFetch {
	private static String baseUrl = "http://trade.500.com/jq4/?lotid=17&playid=1&expect=";
	
	public static void zucai4() {
		
		Zucai4Mapper z4Mapper = Main.applicationContext.getBean(Zucai4Mapper.class);
		
		String xiId = null;
		String serial = null;
		String leagueName = null;
		String leagueColor = null;
		String leagueUrl = null;
		String gameTime = null;
		String homeTeam = null;
		String homeUrl = null;
		String awayTeam = null;
		String awayUrl = null;
		String xiUrl = null;
		String euroUrl = null;
		String aisaUrl = null;
		String euWin = null;
		String euDraw = null;
		String euLose = null;
		String episode = null;
		String sp1 = null;
		
		try {
			List<String> episodesList = ZucaiGetEpisodes.getEpisodes(7);
			if(ObjectHelper.isNotEmpty(episodesList) && episodesList.size() > 0) {
				for(String epi : episodesList) {
					epi = epi.substring(2);
					baseUrl += epi;
					Document doc = UtilBag.getDocumentByUrl(baseUrl);
					if(ObjectHelper.isNotEmpty(doc)) {
						Elements topTimes = doc.select(".b-top-time");
						if(ObjectHelper.isNotEmpty(topTimes)) {
							String[] dtimes = topTimes.get(0).text().split(" ");
							sp1 = dtimes[1] + " " + dtimes[2];
						}
						Elements vsTable = doc.select("#vsTable");
						if(ObjectHelper.isNotEmpty(vsTable)) {
							Elements trs = vsTable.get(0).select("tr");
							if(ObjectHelper.isNotEmpty(trs)) {
								for(Element tr : trs) {
									if(!StringUtils.isEmpty(tr.attr("data-vs"))){ 
										System.out.println(tr.attr("data-vs"));
										Elements tds = tr.children();
										if(ObjectHelper.isNotEmpty(tds)) {
											if(tds.size() == 9 || tds.size() == 7) {

												serial = tds.get(0).text();
												leagueColor = tds.get(1).attr("style").split(";")[0].split(":")[1];
												leagueUrl = tds.get(1).select("a").get(0).attr("href");
												leagueUrl = leagueUrl.split("zuqiu-")[1].replace("/", "");
												leagueName = tds.get(1).select("a").get(0).text();
												gameTime = tds.get(2).text();
												Element teamTd = tds.get(3);
												if(ObjectHelper.isNotEmpty(teamTd)) {
													homeUrl = teamTd.select("a").attr("href");
													homeUrl = homeUrl.split("team/")[1].replace("/", "");
													String text = teamTd.select("a").get(0).text();
													text = text.replace(" ", "");
//													String teamType = text.substring(0, 1);
													homeTeam = text.substring(1, text.length());
												}
												
												Elements as = tds.get(4).select("a");
												xiId = as.get(0).attr("href").split("-")[1].split("\\.")[0];
												if(ObjectHelper.isNotEmpty(as)) {
													xiUrl = as.get(0).attr("href");
													xiUrl = xiUrl.split("shuju-")[1].split("\\.")[0];
													euroUrl = as.get(1).attr("href");
													euroUrl = euroUrl.split("yazhi-")[1].split("\\.")[0];
													aisaUrl = as.get(2).attr("href");
													aisaUrl = aisaUrl.split("ouzhi-")[1].split("\\.")[0];
													MVs mvs = new MVs();
													mvs.setXiid(xiId);
//													AnalysisFetch.getAnalysis(mvs);
//													AnalysisFetch.getAsia(xiId);
//													AnalysisFetch.getEuro(xiId);
												}
												Elements spans5 = tds.get(5).select("span");
												if(ObjectHelper.isNotEmpty(spans5)) {
													euWin = spans5.get(0).text();
													euDraw = spans5.get(1).text();
													euLose = spans5.get(2).text();
												}
												continue;
											
											} else if(tds.size() == 4 || tds.size() == 2) {
												awayUrl = tds.get(0).select("a").attr("href");
												awayUrl = awayUrl.split("team/")[1].replace("/", "");
												String text = tds.get(0).select("a").get(0).text();
												text = text.replace(" ", "");
//												String teamType = text.substring(0, 1);
												awayTeam = text.substring(1, text.length());
											}
											episode = epi;
											
											Zucai4 z4 = new Zucai4();
											z4.setAsiaurl(aisaUrl);
											z4.setAwayteam(awayTeam);
											z4.setAwayurl(awayUrl);
											z4.setEudraw(euDraw);
											z4.setEulose(euLose);
											z4.setEurourl(euroUrl);
											z4.setEuwin(euWin);
											z4.setGametime(gameTime);
											z4.setHometeam(homeTeam);
											z4.setHomeurl(homeUrl);
											z4.setLeaguecolor(leagueColor);
											z4.setLeaguename(leagueName);
											z4.setLeagueurl(leagueUrl);
											z4.setSerial(serial);
											z4.setXiid(xiId);
											z4.setXiurl(xiUrl);
											z4.setEpisode(episode);
											z4.setSp1(sp1);
											
											Zucai4Example z4Ex = new Zucai4Example();
											Criteria criteria = z4Ex.createCriteria();
											criteria.andXiidEqualTo(xiId);
											criteria.andGametimeEqualTo(gameTime);
											List<Zucai4> list = z4Mapper.selectByExample(z4Ex);
											if(ObjectHelper.isEmpty(list)) {
												z4.setIdzucai4(IDUtils.createUUId());
												z4Mapper.insert(z4);
											} else {
												Zucai4Example zu4Ex = new Zucai4Example();
												zu4Ex.createCriteria().andSerialEqualTo(z4.getSerial()).andEpisodeEqualTo(episode);
												List<Zucai4> list2 = z4Mapper.selectByExample(zu4Ex);
												if(ObjectHelper.isNotEmpty(list2) && list2.size() > 1) {
													z4Mapper.deleteByPrimaryKey(list.get(0).getIdzucai4());
												}
												z4.setIdzucai4(list.get(0).getIdzucai4());
												z4Mapper.updateByPrimaryKey(z4);
											}
										}
									}
								}
							}
						}
					}
					baseUrl = "http://trade.500.com/jq4/?lotid=17&playid=1&expect=";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		zucai4();
	}
}
