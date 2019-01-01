package com.kprd.date.fetch.wubai.football;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import com.kprd.newliansai.mapper.FoAsiaMapper;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoAsia;
import com.kprd.newliansai.pojo.FoAsiaExample;
import com.kprd.newliansai.pojo.FoMixExample;
import com.kprd.newliansai.pojo.FoAsiaExample.Criteria;
import com.kprd.newliansai.pojo.FoMix;

public class WubaiAsia {
	
	private static String baseUrl = "http://odds.500.com/fenxi/yazhi-aid.shtml";
	
	static FoAsiaMapper aisaMapper = Main.applicationContext.getBean(FoAsiaMapper.class);
	
	@SuppressWarnings("unused")
	public static FoAsia getAisaOdds(FoMix mvs) {

		// 即时水胜
		String jsShuiS = null;
		// 即时水胜状态
		String jsShuiSStatus = null;
		// 即时盘文字
		String jsPanString = null;
		// 即时盘
		String jsPan = null;
		// 即时盘状态
		String jsPanStatus = null;
		// 即时水负
		String jsShuiF = null;
		// 即时水负状态
		String jsShuiFStatus = null;
		// 即时变化时间
		String jsChangeTime = null;

		// 即时平均水胜
		String jsPjShuiS = null;
		// 即时平均盘
		String jsPjPan = null;
		// 即时平均水负
		String jsPjShuiF = null;
		
		// 初始平均水胜
		String csPjShuiS = null;
		// 初始平均盘
		String csPjPan = null;
		// 初始平均水负
		String csPjShuiF = null;

		// 初始盘口水胜
		String csS = null;
		// 初始水胜状态
		String csSStatus = null;
		// 初始盘口
		String csPan = null;
		// 初始盘文字
		String csPanString = null;
		// 初始盘口水负
		String csF = null;
		// 初始水负状态
		String csFStatus = null;
		// 初始变化时间
		String csChangeTime = null;
		// 公司名称
		String company = null;

		FoAsia asiaoddsForAvg = new FoAsia();

		baseUrl = baseUrl.replace("aid", mvs.getWubaiid());
		try {
			String html = getDomString(baseUrl);
			baseUrl = "http://odds.500.com/fenxi/yazhi-aid.shtml";
			Document doc = Jsoup.parse(html);
		
			if (ObjectHelper.isNotEmpty(doc)) {

				Elements ttr = doc.select("tr");
				if (null != ttr && ttr.size() > 0) {
					int i=0;
					for (Element t : ttr) {
						/////////////////////////// 平均数据///////////////////////////
						if ("footer".equals(t.attr("xls"))) {
							Elements tds = t.children();
							if (null != tds && tds.size() > 0) {
								if(i==0) {
									i++;
									FoAsia asiaodds = new FoAsia();
									//平均值
									Element tr1 = tds.get(2).select("table tbody tr").get(0);
									Elements tds1 = tr1.children();
									jsPjShuiS = tds1.get(0).text().trim();
									jsPjPan = tds1.get(1).text().trim();
									jsPjShuiF = tds1.get(2).text().trim();
									Element tr2 = tds.get(4).select("table tbody tr").get(0);
									Elements tds2 = tr2.children();
									csPjShuiS = tds2.get(0).text().trim();
									csPjPan = tds2.get(1).text().trim();
									csPjShuiF = tds2.get(2).text().trim();
									
									asiaodds.setCompany("平均值");
									asiaodds.setJshomewin(jsPjShuiS);
									asiaodds.setJsdraw(jsPjPan);
									asiaodds.setJsawaywin(jsPjShuiF);
									asiaodds.setCshomewin(csPjShuiS);
									asiaodds.setCsdraw(csPjPan);
									asiaodds.setCsawaywin(csPjShuiF);
//									asiaodds.setJschangetime("平均值");  暂时不知道留着还有用么，测试时看
//									asiaodds.setCschangetime("平均值");
									asiaodds.setReserve1(mvs.getIdunique());
									
									FoAsiaExample asiaEx = new FoAsiaExample();
									Criteria criteria = asiaEx.createCriteria();
									criteria.andCompanyEqualTo("平均值");
									criteria.andReserve1EqualTo(mvs.getIdunique());
									asiaoddsForAvg = asiaodds;
									
									//根据公司名称（"平均值"）查询库中是否已有数据
									List<FoAsia> list = aisaMapper.selectByExample(asiaEx);
									if(ObjectHelper.isEmpty(list)) {
										asiaodds.setId(IDUtils.createUUId());
										asiaodds.setIdunique(mvs.getIdunique());
										aisaMapper.insert(asiaodds);
									} else {
										asiaodds.setId(list.get(0).getId());
										asiaodds.setIdunique(mvs.getIdunique());
										aisaMapper.updateByPrimaryKeySelective(asiaodds);
									}
								} else if(i==1) {
									//最高值
									i++;
									FoAsia asiaodds = new FoAsia();
									Elements td1 = tds.get(1).select("table tbody tr td");
									asiaodds.setJshomewin(td1.get(0).text());
									asiaodds.setJsdraw(td1.get(1).text());
									asiaodds.setJsawaywin(td1.get(2).text());
									Elements td2 = tds.get(3).select("table tbody tr td");
									asiaodds.setCshomewin(td2.get(0).text());
									asiaodds.setCsdraw(td2.get(1).text());
									asiaodds.setCsawaywin(td2.get(2).text());
									asiaodds.setCompany("最高值");
//									asiaodds.setJschangetime("最高值");
//									asiaodds.setCschangetime("最高值");
									asiaodds.setReserve1(mvs.getIdunique());
									
									FoAsiaExample asiaEx = new FoAsiaExample();
									Criteria criteria = asiaEx.createCriteria();
									criteria.andCompanyEqualTo("最高值");
									criteria.andReserve1EqualTo(mvs.getIdunique());
									
									//根据公司名称（"平均值"）查询库中是否已有数据
									List<FoAsia> list = aisaMapper.selectByExample(asiaEx);
									if(ObjectHelper.isEmpty(list)) {
										asiaodds.setId(IDUtils.createUUId());
										asiaodds.setIdunique(mvs.getIdunique());
										aisaMapper.insert(asiaodds);
									} else {
										asiaodds.setId(list.get(0).getId());
										asiaodds.setIdunique(mvs.getIdunique());
										aisaMapper.updateByPrimaryKeySelective(asiaodds);
									}
								} else if(i==2) {
									//最低值
									i++;
									FoAsia asiaodds = new FoAsia();
									Elements td1 = tds.get(2).select("table tbody tr td");
									asiaodds.setJshomewin(td1.get(0).text());
									asiaodds.setJsdraw(td1.get(1).text());
									asiaodds.setJsawaywin(td1.get(2).text());
									Elements td2 = tds.get(4).select("table tbody tr td");
									asiaodds.setCshomewin(td2.get(0).text());
									asiaodds.setCsdraw(td2.get(1).text());
									asiaodds.setCsawaywin(td2.get(2).text());
									
									asiaodds.setCompany("最低值");
//									asiaodds.setJschangetime("最低值");
//									asiaodds.setCschangetime("最低值");
									asiaodds.setReserve1(mvs.getIdunique());
									
									FoAsiaExample asiaEx = new FoAsiaExample();
									Criteria criteria = asiaEx.createCriteria();
									criteria.andCompanyEqualTo("最低值");
									criteria.andReserve1EqualTo(mvs.getIdunique());
									
									//根据公司名称（"最低值"）查询库中是否已有数据
									List<FoAsia> list = aisaMapper.selectByExample(asiaEx);
									if(ObjectHelper.isEmpty(list)) {
										asiaodds.setId(IDUtils.createUUId());
										asiaodds.setIdunique(mvs.getIdunique());
										aisaMapper.insert(asiaodds);
									} else {
										asiaodds.setId(list.get(0).getId());
										asiaodds.setIdunique(mvs.getIdunique());
										aisaMapper.updateByPrimaryKeySelective(asiaodds);
									}
									break;
								}
							}
						}
					}
				}

				Elements fatherTrs = doc.select("tr");
				if (null != fatherTrs && fatherTrs.size() > 0) {
					for (Element fatherTr : fatherTrs) {

						/////////////////////////// 博彩公司数据////////////////////////
						if (("row".equals(fatherTr.attr("xls")) && "tr1".equals(fatherTr.attr("class")))
								|| (("row".equals(fatherTr.attr("xls")) && "tr2".equals(fatherTr.attr("class"))))) {
							Elements spans = fatherTr.select("> td > p > a >span");
							if (null != spans && spans.size() > 0) {
								company = spans.get(0).text();
								if ("皇冠".equals(company) || "Bet365".equals(company) || "立博".equals(company)
										|| "金宝博".equals(company) || "澳门".equals(company) || "威廉希尔".equals(company)
										|| "竞彩官方".equals(company) || "必发".equals(company)) {

									Elements tds = fatherTr.select(" > td");
									if (null != tds && tds.size() > 0) {
										// 即时
										Elements tdss = tds.get(2).select("table tbody tr td");
										if (null != tdss && tdss.size() == 4) {
											String special = tdss.get(0).text();
											special = special.substring(0, 5);
											jsShuiS = special;
											// 入库前判断跟上一次的升降情况
											jsPan = tdss.get(1).attr("ref");
											jsPanString = tdss.get(1).text();
											// 入库前判断跟上一次的升降情况
											String special2 = tdss.get(2).text();
											special2 = special2.substring(0, 5);
											jsShuiF = special2;
											// 暂时不要
											// tdss.get(4).text();
										}
									}
									jsChangeTime = tds.get(3).text();

									// 初始
									Elements tdss2 = tds.get(4).select("table tbody tr td");
									if (null != tdss2 && tdss2.size() == 3) {
										csS = tdss2.get(0).text().trim();
										csPanString = tdss2.get(1).text();
										csPan = tdss2.get(1).attr("ref");
										csF = tdss2.get(2).text().trim();
									}
									csChangeTime = tds.get(5).text().trim();
									
									//判断入库
									
									FoAsiaExample asiaEx = new FoAsiaExample();
									Criteria criteria = asiaEx.createCriteria();
									//根据即时变换时间查询出一条数据
//									criteria.andJschangetimeEqualTo(jsChangeTime);
									criteria.andCompanyEqualTo(company).andIduniqueEqualTo(mvs.getIdunique());
									List<FoAsia> aisaList = aisaMapper.selectByExample(asiaEx);
									//盘是数字，panStr是文字，也有可能有数字
									System.out.println(company);
									FoAsia asiaodds = new FoAsia();
									asiaodds.setJshomewin(jsShuiS);
									asiaodds.setJshomewinstatus("0");

									asiaodds.setJspannum(jsPan);

									asiaodds.setJsawaywin(jsShuiF);
									asiaodds.setJsawaywinstatus("0");

									asiaodds.setCompany(company);
									asiaodds.setJsdraw(jsPanString);
//									asiaodds.setJschangetime(jsChangeTime);
									asiaodds.setCshomewin(csS);
									asiaodds.setCsdraw(csPanString);
									asiaodds.setCspannum(csPan);
									asiaodds.setCsawaywin(csF);
//									asiaodds.setCschangetime(csChangeTime);

									if (ObjectHelper.isNotEmpty(aisaList)) {//没有变化
										
									} else {//新增或刷新
										FoAsiaExample asiaEx2 = new FoAsiaExample();
										Criteria criteria2 = asiaEx2.createCriteria();
										criteria2.andCompanyEqualTo(company).andIduniqueEqualTo(mvs.getIdunique());
										asiaEx.setOrderByClause(" jsChangeTime desc");
										List<FoAsia> asiaList = aisaMapper.selectByExample(asiaEx2);
										if(ObjectHelper.isEmpty(asiaList)) {//新增数据
											asiaodds.setId(IDUtils.createUUId());
											asiaodds.setIdunique(mvs.getIdunique());
											aisaMapper.insert(asiaodds);
										} else {
											// 判断即时主队水
											String dbData = null;
											dbData = asiaList.get(0).getJshomewin();
											BigDecimal bd = new BigDecimal(dbData);
											BigDecimal page = new BigDecimal(jsShuiS);
											if (page.compareTo(bd) == 1) {
												asiaodds.setJshomewinstatus("1");
											} else if (page.compareTo(bd) == 0) {
												asiaodds.setJshomewinstatus("0");
											} else {
												asiaodds.setJshomewinstatus("-1");
											}

											// 判断即时盘 由于只有一个网站有这个字段，不抓取了
//											dbData = asiaList.get(0).getJspannum();
//											bd = new BigDecimal(dbData);
//											page = new BigDecimal(jsPan);
//											if (page.compareTo(bd) == 1) {
//												asiaodds.setJspanstatus("1");
//											} else if (page.compareTo(bd) == 0) {
//												asiaodds.setJspanstatus("0");
//											} else {
//												asiaodds.setJspanstatus("-1");
//											}

											// 判断即时客队
											dbData = asiaList.get(0).getJsawaywin();
											bd = new BigDecimal(dbData);
											page = new BigDecimal(jsShuiF);
											if (page.compareTo(bd) == 1) {
												asiaodds.setJsawaywinstatus("1");
											} else if (page.compareTo(bd) == 0) {
												asiaodds.setJsawaywinstatus("0");
											} else {
												asiaodds.setJsawaywinstatus("-1");
											}
											asiaodds.setId(asiaList.get(0).getId());
											asiaodds.setIdunique(mvs.getIdunique());
											aisaMapper.updateByPrimaryKeySelective(asiaodds);
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
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1) {
				try {
					Thread.sleep(60*1000);
				} catch (Exception e1) {
					return getAisaOdds(mvs);
				}
			} else {
			System.exit(0);
			}
		}
		JedisUtilForFetch.remove(6,mvs.getIdunique() + "asia");
    	System.out.println("缓存已删除***********************asia**********************************");
		return asiaoddsForAvg;
	}
	
	private static String getDomString(String url) throws InterruptedException {
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpget.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
			httpget.setHeader("Host", "odds.500.com");
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null && response.getStatusLine().getStatusCode() == 200) {
					html = UtilBag.handleEntityCharset(entity, "gb2312");
					if (ObjectHelper.isNotEmpty(html)) {
						return html;
					}
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1) {
				Thread.sleep(60*1000);
				return getDomString(url);
			}
		}
		return html;
	}
	
	public static void main(String[] args) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = UtilBag.dateUtil(0) + " 11:59:59";
			Date startDate = sdf.parse(date);
			FoMixExample mixEx = new FoMixExample();
			mixEx.createCriteria().andStarttimeGreaterThan(startDate);
			FoMixMapper mixMapper = Main.applicationContext.getBean(FoMixMapper.class);
			List<FoMix> mixList = mixMapper.selectByExample(mixEx);
			if(ObjectHelper.isNotEmpty(mixList)) {
				for(FoMix mix : mixList) {
					getAisaOdds(mix);
				}
			}
			System.out.println(mixList.size() + "场比赛");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
