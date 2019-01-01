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
import com.kprd.zucai.mapper.Zucai6Mapper;
import com.kprd.zucai.pojo.Zucai6;
import com.kprd.zucai.pojo.Zucai6Example;
import com.kprd.zucai.pojo.Zucai6Example.Criteria;

public class Zucai6Fetch {
private static String baseUrl = "http://trade.500.com/zc6/?lotid=15&playid=1&expect=";
	
	/**
	 * 抓14
	 */
	public static void get6() {
		
		Zucai6Mapper z6Mapper = Main.applicationContext.getBean(Zucai6Mapper.class);
		
		String xiId = null;
		String serial = null;
		String leagueName = null;
		String leagueColor = null;
		String leagueUrl = null;
		String gameTime = null;
		String homeRank = null;
		String homeTeam = null;
		String homeUrl = null;
		String awayRank = null;
		String awayTeam = null;
		String awayUrl = null;
		String xiUrl = null;
		String euroUrl = null;
		String aisaUrl = null;
		String bcWin = null;
		String bcDraw = null;
		String bcLose = null;
		String qcWin = null;
		String qcDraw = null;
		String qcLose = null;
		String episode = null;
		String sp1 = null;
		
		try {
			//查出当前期和未来期号
			List<String> episodesList = ZucaiGetEpisodes.getEpisodes(10);
			if(ObjectHelper.isNotEmpty(episodesList) && episodesList.size() > 0) {
				for(int e=0;e<episodesList.size();e++) {
					String epi = episodesList.get(e).substring(2);
					baseUrl += epi;
					Document doc = UtilBag.getDocumentByUrl(baseUrl);
					if(ObjectHelper.isNotEmpty(doc)) {
						Elements topTimes = doc.select(".b-top-time");
						if(ObjectHelper.isNotEmpty(topTimes)) {
							String[] dtimes = topTimes.get(0).text().split(" ");
							sp1 = dtimes[1] + " " + dtimes[2];
						}
						Element vsTable = doc.select("#vsTable").get(0);
						Elements trs = vsTable.select("tr");
						if(ObjectHelper.isNotEmpty(trs)) {
							for(Element tr : trs) {
								if(!StringUtils.isEmpty(tr.attr("data-vs"))) {
									System.out.println(tr.attr("data-vs"));
									Elements tds = tr.children();
									if(ObjectHelper.isNotEmpty(tds) && (tds.size() == 10 || tds.size() == 8)) {
										serial = tds.get(0).text();
										leagueColor = tds.get(1).attr("style").split(";")[0].split(":")[1];
										leagueUrl = tds.get(1).select("a").get(0).attr("href");
										leagueUrl = leagueUrl.split("zuqiu-")[1].replace("/", "");
										leagueName = tds.get(1).select("a").get(0).text();
										gameTime = tds.get(2).text();
										Elements spans = tds.get(3).select("span");
										if(ObjectHelper.isNotEmpty(spans)) {
											if(spans.get(0).text().indexOf("[") > -1) {
												homeRank = spans.get(0).text().replace("[", "").replace("]", "");
											}
											homeUrl = spans.get(1).select("a").get(0).attr("href");
											homeUrl = homeUrl.split("team/")[1].replace("/", "");
											homeTeam = spans.get(1).text().replace(" ", "").trim();
											awayTeam = spans.get(3).text().replace(" ", "").trim();
											if(spans.get(4).text().indexOf("[") > -1) {
												awayRank = spans.get(4).text().replace("[", "").replace("]", "");
											}
											awayUrl = spans.get(3).select("a").get(0).attr("href");
											awayUrl = awayUrl.split("team/")[1].replace("/", "");
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
										}
										Elements spans5 = tds.get(6).select("span");
										if(ObjectHelper.isNotEmpty(spans5)) {
											bcWin = spans5.get(0).text();
											bcDraw = spans5.get(1).text();
											bcLose = spans5.get(2).text();
										}
										continue;
									} else if (tds.size() == 5 || tds.size() == 3) {
										Elements spans = tds.get(1).select("span");
										if(ObjectHelper.isNotEmpty(spans)) {
											qcWin = spans.get(0).text();
											qcDraw = spans.get(1).text();
											qcLose = spans.get(2).text();
										} else {
											continue;
										}
									}
									
								} else {
									if(tr.children().size() == 3 && tr.children().get(0).text().equals("全场")) {
										Elements spans = tr.children().get(1).select("span");
										if(ObjectHelper.isNotEmpty(spans)) {
											qcWin = spans.get(0).text();
											qcDraw = spans.get(1).text();
											qcLose = spans.get(2).text();
										} else {
											continue;
										}
									} else {
										continue;
									}
								}
							episode = epi;
							
							Zucai6 z6 = new Zucai6();
							z6.setAisaurl(aisaUrl);
							z6.setAwayrank(awayRank);
							z6.setAwayteam(awayTeam);
							z6.setAwayurl(awayUrl);
							z6.setBcdraw(bcDraw);
							z6.setBclose(bcLose);
							z6.setBcwin(bcWin);
							z6.setEurourl(euroUrl);
							z6.setGametime(gameTime);
							z6.setHomerank(homeRank);
							z6.setHometeam(homeTeam);
							z6.setHomeurl(homeUrl);
							z6.setLeaguecolor(leagueColor);
							z6.setLeaguename(leagueName);
							z6.setLeagueurl(leagueUrl);
							z6.setQcdraw(qcDraw);
							z6.setQclose(qcLose);
							z6.setQcwin(qcWin);
							z6.setSerial(serial);
							z6.setXiid(xiId);
							z6.setXiurl(xiUrl);
							z6.setEpisode(episode);
							z6.setSp1(sp1);
							
							Zucai6Example z6Ex = new Zucai6Example();
							Criteria criteria = z6Ex.createCriteria();
							criteria.andXiidEqualTo(xiId);
							criteria.andGametimeEqualTo(gameTime);
							List<Zucai6> list = z6Mapper.selectByExample(z6Ex);
							if(ObjectHelper.isEmpty(list)) {
								z6.setIdzucai6(IDUtils.createUUId());
								z6Mapper.insert(z6);
							} else {
								Zucai6Example zu6Ex = new Zucai6Example();
								zu6Ex.createCriteria().andSerialEqualTo(z6.getSerial()).andEpisodeEqualTo(episode);
								List<Zucai6> list2 = z6Mapper.selectByExample(zu6Ex);
								if(ObjectHelper.isNotEmpty(list2) && list2.size() > 1) {
									z6Mapper.deleteByPrimaryKey(list.get(0).getIdzucai6());
								}
								z6.setIdzucai6(list.get(0).getIdzucai6());
								z6Mapper.updateByPrimaryKey(z6);
							}
							}
						}
					}
					baseUrl = "http://trade.500.com/zc6/?lotid=15&playid=1&expect=";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		get6();
	}
}
