package com.kprd.date.zq;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.liansai.mapper.MMatchMapper;
import com.kprd.liansai.mapper.MSeasonsMapper;
import com.kprd.liansai.pojo.MMatch;
import com.kprd.liansai.pojo.MSeasons;

/**
 * 抓取 联赛 杯赛
 * @author Administrator
 *
 */
public class FetchMajorMatch {
	private String baseUrl = "http://liansai.500.com";
	//文件下载地址
	private String imgPath = "F:\\dowloadImgs\\leagues\\";
	
	public void fetchGames() {
		Document doc = UtilBag.getDocumentByUrl(baseUrl);
		if(null != doc) {
				fetchCups(doc);
				fetchLeagues(doc);
		}
	}
	
	/**
	 * 抓取联赛
	 */
	public void fetchLeagues(Document doc) {
		try {
			MMatchMapper mmatchMapper = Main.applicationContext.getBean(MMatchMapper.class);
			Elements blocks = doc.select(".lallrace_main_list");
			if(null != blocks && blocks.size() > 0) {
				for(int i=1;i<blocks.size();i++) {
					if(blocks.get(i).attr("class").indexOf("clearfix") > -1) {
						MMatch macthFather = new MMatch();
						if(i==1) {
							macthFather.setNameMatch("欧洲赛事");
						} else if(i==2) {
							macthFather.setNameMatch("美洲赛事");
						} else if(i==3) {
							macthFather.setNameMatch("亚洲赛事");
						} else if(i==4) {
							macthFather.setNameMatch("非洲赛事");
						}
						macthFather.setIdMatch(IDUtils.createUUId());
						macthFather.setTypeMatch(2);
						macthFather.setGameLevel(1);
						Elements lis = blocks.get(i).select("li");
						if(null != lis && lis.size() > 0) {
							for(Element li : lis) {
								MMatch countryMatch = new MMatch();
								//国家
								String country = li.select("span").get(0).text();
								try {
									//国旗图片地址
									String flagUrl = li.select("img").get(0).attr("_src").trim();
									String downloadPath = imgPath + macthFather.getNameMatch() + "\\" + country + "flag" + System.currentTimeMillis() + ".jpg";
									UtilBag.downloadPicture(flagUrl, downloadPath);
									countryMatch.setFlagurl(flagUrl);
								} catch (Exception e) {
									e.printStackTrace();
								}
								countryMatch.setNameMatch(macthFather.getNameMatch());
								countryMatch.setCountry(country);
								countryMatch.setTypeMatch(macthFather.getTypeMatch());
								countryMatch.setGameLevel(2);
								countryMatch.setPid(macthFather.getIdMatch());
								countryMatch.setIdMatch(IDUtils.createUUId());
								Elements as = li.select("div a");
								if(null != as && as.size() > 0) {
									for(Element a : as) {
										MMatch leagueMatch = new MMatch();
										//联赛地址
										String leagueUrl = a.attr("href").trim();
										//球队全称
										String full_name = a.attr("title").trim();
										//球队简称
										String short_name = a.text();
										//组装联赛url
										String url = baseUrl + leagueUrl;
										
										leagueUrl = leagueUrl.replace("/", "").split("-")[1];
										leagueMatch.setNameMatch(macthFather.getNameMatch());
										leagueMatch.setIdHref(leagueUrl);
										leagueMatch.setFullName(full_name);
										leagueMatch.setShortName(short_name);
										leagueMatch.setTypeMatch(macthFather.getTypeMatch());
										leagueMatch.setGameLevel(3);
										leagueMatch.setCountry(countryMatch.getCountry());
										leagueMatch.setIdMatch(IDUtils.createUUId());
										leagueMatch.setPid(countryMatch.getIdMatch());
										getSeasonUrls(url, leagueMatch.getIdMatch());
										//联赛入库
										mmatchMapper.insert(leagueMatch);
									}
								}
								//国家入库
								System.out.println(countryMatch);
								mmatchMapper.insert(countryMatch);
							}
						}
						System.out.println(macthFather);
						mmatchMapper.insert(macthFather);
						//赛事入库
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 抓取杯赛
	 */
	public void fetchCups(Document doc) {
		MMatchMapper mmatchMapper = Main.applicationContext.getBean(MMatchMapper.class);
		MMatch macthFather = new MMatch();
		macthFather.setNameMatch("国际赛事");
		macthFather.setTypeMatch(1);
		macthFather.setIdMatch(IDUtils.createUUId());
		macthFather.setGameLevel(1);
		//国际赛事
		Elements divs = doc.select(".lallrace_main");
		if(null != divs && divs.size() > 0) {
			Elements lis = divs.get(0).select("ul li");
			if(null != lis && lis.size() > 0) {
				for(Element li : lis) {
					MMatch macth = new MMatch();
					String cupName = li.select("a span").get(0).text().trim();
					String id_href = li.select("a").get(0).attr("href").trim();
					String url = baseUrl + id_href;
					id_href = id_href.replace("/", "").split("-")[1];
					macth.setNameMatch(cupName);
					macth.setIdHref(id_href);
					macth.setTypeMatch(macthFather.getTypeMatch());
					macth.setIdMatch(IDUtils.createUUId());
					macth.setPid(macthFather.getIdMatch());
					macth.setGameLevel(2);
					getSeasonUrls(url,macth.getIdMatch());
					//杯赛
					mmatchMapper.insert(macth);
				}
			}
		}
		//联赛中的杯赛
		Elements tables = doc.select(".lrace_bei");
		if(null != tables && tables.size() > 0) {
			System.out.println(tables.size());
			for(int i=0;i<tables.size();i++) {
				Elements as = tables.get(i).select("tr td a");
				if(null != as && as.size() > 0) {
					for(Element a : as) {
						MMatch lcMmatch = new MMatch();
						lcMmatch.setIdMatch(IDUtils.createUUId());
						lcMmatch.setIdHref(a.attr("href").replace("/", "").split("-")[1]);
						lcMmatch.setTypeMatch(1);
						lcMmatch.setGameLevel(2);
						lcMmatch.setShortName(a.text().trim());
						if(i==0) {//欧洲杯赛
							lcMmatch.setNameMatch("欧洲杯赛");
						} else if(i==1) {//梅州杯赛
							lcMmatch.setNameMatch("美洲杯赛");
						} else if(i==2) {//亚洲杯赛
							lcMmatch.setNameMatch("亚洲杯赛");
						} else if(i==3) {//非洲杯赛
							lcMmatch.setNameMatch("非洲杯赛");
						}
						getSeasonUrls(baseUrl + a.attr("href"), lcMmatch.getIdMatch());
						mmatchMapper.insert(lcMmatch);
					}
				}
			}
		}
		mmatchMapper.insert(macthFather);
		
	}
	
	/**
	 * 抓取赛季信息
	 * @param url
	 */
	public void getSeasonUrls(String url,String pid) {
		MSeasonsMapper mseasonsMapper = Main.applicationContext.getBean(MSeasonsMapper.class);
		if(!ObjectHelper.isEmpty(url)) {
			Document doc = UtilBag.getDocumentByUrl(url);
			if(null != doc) {
				Elements uls = doc.select(".ldrop_list");
				if(null != uls && uls.size() > 0) {
					Elements lis = uls.get(0).children();
					if(null != lis && lis.size() > 0) {
						for(Element li : lis) {
							MSeasons season = new MSeasons();
							Element a = li.select("a").get(0);
							String href = a.attr("href").trim();
							href = href.replace("/", "").split("-")[1];
							season.setIdSeason(IDUtils.createUUId());
							season.setHref(href);
							season.setYear(a.text().trim());
							season.setPid(pid);
							//入库赛季信息
							mseasonsMapper.insert(season);
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		FetchMajorMatch f = new FetchMajorMatch();
		f.fetchGames();
	}
}
