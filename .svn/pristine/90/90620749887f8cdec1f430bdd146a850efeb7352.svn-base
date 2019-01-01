package com.kprd.date.zq;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.kprd.zucai.mapper.Zucai14Mapper;
import com.kprd.zucai.pojo.Zucai14;
import com.kprd.zucai.pojo.Zucai14Example;
import com.kprd.zucai.pojo.Zucai14Example.Criteria;

/**
 * 足彩14场
 * @author Administrator
 *
 */
public class Zucai14Fetch {
	//14
	private static String baseUrl = "http://trade.500.com/sfc/index.php?lotid=1&playid=1&expect=";
	//9
	@SuppressWarnings("unused")
	private static String nine = "http://trade.500.com/rcjc/";
	
	/**
	 * 抓14
	 */
	public static void get14() {
		
		Zucai14Mapper zucai14Mapper = Main.applicationContext.getBean(Zucai14Mapper.class);
		
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
		String euWin = null;
		String euDraw = null;
		String euLose = null;
		String asWin = null;
		String asDraw = null;
		String asLose = null;
		String episode = null;
		String sp1 = null;
		
		try {
			//查出当前期和未来期号
			List<String> episodesList = ZucaiGetEpisodes.getEpisodes(7);
			if(ObjectHelper.isNotEmpty(episodesList) && episodesList.size() > 0) {
				for(String epi : episodesList) {
					epi = epi.substring(2);
					baseUrl += epi;
					Document doc = UtilBag.getDocumentByUrl(baseUrl);
					System.out.println(123);
					if(ObjectHelper.isNotEmpty(doc)) {
						System.out.println(456);
						Elements topTimes = doc.select(".b-top-time");
						if(ObjectHelper.isNotEmpty(topTimes)) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
							String year = sdf.format(new Date());
							String[] dtimes = topTimes.get(0).text().split(" ");
							sp1 = year + "-" + dtimes[1] + " " + dtimes[2];
						}
						Element vsTable = doc.select("#vsTable").get(0);
						Elements trs = vsTable.select("tr");
						if(ObjectHelper.isNotEmpty(trs)) {
							for(Element tr : trs) {
								if(!StringUtils.isEmpty(tr.attr("data-vs"))) {
									System.out.println(tr.attr("data-vs"));
									Elements tds = tr.children();
									if(ObjectHelper.isNotEmpty(tds)) {
										serial = tds.get(0).text();
										if(tds.get(1).attr("style").split(";")[0].split(":").length == 1) {
											leagueColor = "#ccc";
										} else {
											leagueColor = tds.get(1).attr("style").split(";")[0].split(":")[1];
										}
										
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
//											AnalysisFetch.getAnalysis(mvs);
//											AnalysisFetch.getAsia(xiId);
//											AnalysisFetch.getEuro(xiId);
										}
										Elements spans5 = tds.get(5).select("span");
										if(ObjectHelper.isNotEmpty(spans5)) {
											euWin = spans5.get(0).text();
											euDraw = spans5.get(1).text();
											euLose = spans5.get(2).text();
										}
										Elements spans6 = tds.get(6).select("span");
										if(ObjectHelper.isNotEmpty(spans6)) {
											asWin = spans6.get(0).text();
											asDraw = spans6.get(1).text();
											asLose = spans6.get(2).text();
										}
										
										episode = epi;
//										Elements expect_tabs = doc.select("#expect_tab");
//										if(ObjectHelper.isNotEmpty(expect_tabs)) {
//											episode = expect_tabs.get(0).select("a").get(0).text();
//											episode = episode.replace("当前期", "").replace("期", "").trim();
//										}
										
										Zucai14 z14 = new Zucai14();
										z14.setAisaurl(aisaUrl);
										z14.setAsdraw(asDraw);
										z14.setAslose(asLose);
										z14.setAswin(asWin);
										z14.setAwayrank(awayRank);
										z14.setAwayteam(awayTeam);
										z14.setAwayurl(awayUrl);
										z14.setEudraw(euDraw);
										z14.setEulose(euLose);
										z14.setEurourl(euroUrl);
										z14.setEuwin(euWin);
										z14.setGametime(gameTime);
										z14.setHomerank(homeRank);
										z14.setHometeam(homeTeam);
										z14.setHomeurl(homeUrl);
										z14.setLeaguecolor(leagueColor);
										z14.setLeaguename(leagueName);
										z14.setLeagueurl(leagueUrl);
										z14.setXiurl(xiUrl);
										z14.setXiid(xiId);
										z14.setSerial(serial);
										z14.setEpisode(episode);
										z14.setSp1(sp1);
										
										Zucai14Example z14Ex = new Zucai14Example();
										Criteria criteria = z14Ex.createCriteria();
										criteria.andXiidEqualTo(xiId);
										criteria.andEpisodeEqualTo(episode);
										List<Zucai14> list = zucai14Mapper.selectByExample(z14Ex);
										if(ObjectHelper.isEmpty(list)) {
											z14.setIdzucai(IDUtils.createUUId());
											zucai14Mapper.insert(z14);
										} else {
											Zucai14Example z14example = new Zucai14Example();
											z14example.createCriteria().andEpisodeEqualTo(episode);
											List<Zucai14> z14List = zucai14Mapper.selectByExample(z14example);
											if(z14List.size() > 14) {
												for(int z=0;z<z14List.size();z++) {
													zucai14Mapper.deleteByPrimaryKey(z14List.get(z).getIdzucai());
												}
												continue;
											}
											if(list.get(0).getGametime().equals(gameTime)) {
												z14.setIdzucai(list.get(0).getIdzucai());
												zucai14Mapper.updateByPrimaryKey(z14);
											} else {
												z14.setIdzucai(list.get(0).getIdzucai());
												z14.setGametime(gameTime);
												zucai14Mapper.updateByPrimaryKey(z14);
											}
										}
									}
								}
							}
						}
					}
					baseUrl = "http://trade.500.com/sfc/index.php?lotid=1&playid=1&expect=";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		get14();
	}
}
