package com.kprd.date.zq;

import java.math.BigDecimal;
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
import com.kprd.liansai.pojo.MVs;
import com.kprd.odds.mapper.AsiaoddsMapper;
import com.kprd.odds.pojo.Asiaodds;
import com.kprd.odds.pojo.AsiaoddsExample;
import com.kprd.odds.pojo.AsiaoddsExample.Criteria;

/**
 * 采集亚洲
 * 
 * @author Administrator
 *
 */
public class Asiapei {
	private static String baseUrl = "http://odds.500.com/fenxi/yazhi-aid.shtml";

	public static Asiaodds getAisaOdds(MVs mvs) {

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

		Asiaodds asiaoddsForAvg = new Asiaodds();

		baseUrl = baseUrl.replace("aid", mvs.getXiid());
		try {
			String html = getDomString(baseUrl);
			baseUrl = "http://odds.500.com/fenxi/yazhi-aid.shtml";
			Document doc = Jsoup.parse(html);
		
			if (ObjectHelper.isNotEmpty(doc)) {

				AsiaoddsMapper aisaMapper = Main.applicationContext.getBean(AsiaoddsMapper.class);

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
									Asiaodds asiaodds = new Asiaodds();
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
									asiaodds.setJspkhome(jsPjShuiS);
									asiaodds.setJspan(jsPjPan);
									asiaodds.setJspkaway(jsPjShuiF);
									asiaodds.setCspkhome(csPjShuiS);
									asiaodds.setCspan(csPjPan);
									asiaodds.setCspkaway(csPjShuiF);
									asiaodds.setJschangetime("平均值");
									asiaodds.setCschangetime("平均值");
									asiaodds.setReserve1(mvs.getXiid());
									
									AsiaoddsExample asiaEx = new AsiaoddsExample();
									Criteria criteria = asiaEx.createCriteria();
									criteria.andCompanyEqualTo("平均值");
									criteria.andReserve1EqualTo(mvs.getXiid());
									asiaoddsForAvg = asiaodds;
									
									//根据公司名称（"平均值"）查询库中是否已有数据
									List<Asiaodds> list = aisaMapper.selectByExample(asiaEx);
									if(ObjectHelper.isEmpty(list)) {
										asiaodds.setAsiaid(IDUtils.createUUId());
										aisaMapper.insert(asiaodds);
									} else {
										asiaodds.setAsiaid(list.get(0).getAsiaid());
										aisaMapper.updateByPrimaryKey(asiaodds);
									}
								} else if(i==1) {
									//最高值
									i++;
									Asiaodds asiaodds = new Asiaodds();
									Elements td1 = tds.get(1).select("table tbody tr td");
									asiaodds.setJspkhome(td1.get(0).text());
									asiaodds.setJspan(td1.get(1).text());
									asiaodds.setJspkaway(td1.get(2).text());
									Elements td2 = tds.get(3).select("table tbody tr td");
									asiaodds.setCspkhome(td2.get(0).text());
									asiaodds.setCspan(td2.get(1).text());
									asiaodds.setCspkaway(td2.get(2).text());
									asiaodds.setCompany("最高值");
									asiaodds.setJschangetime("最高值");
									asiaodds.setCschangetime("最高值");
									asiaodds.setReserve1(mvs.getXiid());
									
									AsiaoddsExample asiaEx = new AsiaoddsExample();
									Criteria criteria = asiaEx.createCriteria();
									criteria.andCompanyEqualTo("最高值");
									criteria.andReserve1EqualTo(mvs.getXiid());
									
									//根据公司名称（"平均值"）查询库中是否已有数据
									List<Asiaodds> list = aisaMapper.selectByExample(asiaEx);
									if(ObjectHelper.isEmpty(list)) {
										asiaodds.setAsiaid(IDUtils.createUUId());
										aisaMapper.insert(asiaodds);
									} else {
										asiaodds.setAsiaid(list.get(0).getAsiaid());
										aisaMapper.updateByPrimaryKey(asiaodds);
									}
								} else if(i==2) {
									//最低值
									i++;
									Asiaodds asiaodds = new Asiaodds();
									Elements td1 = tds.get(2).select("table tbody tr td");
									asiaodds.setJspkhome(td1.get(0).text());
									asiaodds.setJspan(td1.get(1).text());
									asiaodds.setJspkaway(td1.get(2).text());
									Elements td2 = tds.get(4).select("table tbody tr td");
									asiaodds.setCspkhome(td2.get(0).text());
									asiaodds.setCspan(td2.get(1).text());
									asiaodds.setCspkaway(td2.get(2).text());
									
									asiaodds.setCompany("最低值");
									asiaodds.setJschangetime("最低值");
									asiaodds.setCschangetime("最低值");
									asiaodds.setReserve1(mvs.getXiid());
									
									AsiaoddsExample asiaEx = new AsiaoddsExample();
									Criteria criteria = asiaEx.createCriteria();
									criteria.andCompanyEqualTo("最低值");
									criteria.andReserve1EqualTo(mvs.getXiid());
									
									//根据公司名称（"最低值"）查询库中是否已有数据
									List<Asiaodds> list = aisaMapper.selectByExample(asiaEx);
									if(ObjectHelper.isEmpty(list)) {
										asiaodds.setAsiaid(IDUtils.createUUId());
										aisaMapper.insert(asiaodds);
									} else {
										asiaodds.setAsiaid(list.get(0).getAsiaid());
										aisaMapper.updateByPrimaryKey(asiaodds);
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
									
									AsiaoddsExample asiaEx = new AsiaoddsExample();
									Criteria criteria = asiaEx.createCriteria();
									//根据即时变换时间查询出一条数据
									criteria.andJschangetimeEqualTo(jsChangeTime);
									criteria.andCompanyEqualTo(company);
									List<Asiaodds> aisaList = aisaMapper.selectByExample(asiaEx);

									System.out.println(company);
									Asiaodds asiaodds = new Asiaodds();
									asiaodds.setJspkhome(jsShuiS);
									asiaodds.setJspkhomestatus("0");

									asiaodds.setJspan(jsPan);
									asiaodds.setJspanstatus("0");

									asiaodds.setJspkaway(jsShuiF);
									asiaodds.setJspkawaystatus("0");

									asiaodds.setXiid(mvs.getXiid());
									asiaodds.setCompany(company);
									asiaodds.setJspanstring(jsPanString);
									asiaodds.setJschangetime(jsChangeTime);
									asiaodds.setCspkhome(csS);
									asiaodds.setCspanstring(csPanString);
									asiaodds.setCspan(csPan);
									asiaodds.setCspkaway(csF);
									asiaodds.setCschangetime(csChangeTime);

									if (ObjectHelper.isNotEmpty(aisaList)) {//没有变化
										
									} else {//新增或刷新
										AsiaoddsExample asiaEx2 = new AsiaoddsExample();
										Criteria criteria2 = asiaEx2.createCriteria();
										criteria2.andCompanyEqualTo(company);
										criteria2.andXiidEqualTo(mvs.getXiid());
										asiaEx.setOrderByClause(" jsChangeTime desc");
										List<Asiaodds> asiaList = aisaMapper.selectByExample(asiaEx2);
										if(ObjectHelper.isEmpty(asiaList)) {//新增数据
											asiaodds.setAsiaid(IDUtils.createUUId());
											aisaMapper.insert(asiaodds);
										} else {
											// 判断即时主队水
											String dbData = null;
											dbData = asiaList.get(0).getJspkhome();
											BigDecimal bd = new BigDecimal(dbData);
											BigDecimal page = new BigDecimal(jsShuiS);
											if (page.compareTo(bd) == 1) {
												asiaodds.setJspkhomestatus("1");
											} else if (page.compareTo(bd) == 0) {
												asiaodds.setJspkhomestatus("0");
											} else {
												asiaodds.setJspkhomestatus("-1");
											}

											// 判断即时盘
											dbData = asiaList.get(0).getJspan();
											bd = new BigDecimal(dbData);
											page = new BigDecimal(jsPan);
											if (page.compareTo(bd) == 1) {
												asiaodds.setJspanstatus("1");
											} else if (page.compareTo(bd) == 0) {
												asiaodds.setJspanstatus("0");
											} else {
												asiaodds.setJspanstatus("-1");
											}

											// 判断即时客队
											dbData = asiaList.get(0).getJspkaway();
											bd = new BigDecimal(dbData);
											page = new BigDecimal(jsShuiF);
											if (page.compareTo(bd) == 1) {
												asiaodds.setJspkawaystatus("1");
											} else if (page.compareTo(bd) == 0) {
												asiaodds.setJspkawaystatus("0");
											} else {
												asiaodds.setJspkawaystatus("-1");
											}
											asiaodds.setAsiaid(asiaList.get(0).getAsiaid());
											aisaMapper.updateByPrimaryKey(asiaodds);
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
		JedisUtilForFetch.remove(Contant.JEDIS_SIX,mvs.getXiid() + "asia");
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
		MVs mVs = new MVs();
		mVs.setXiid("674422");
		getAisaOdds(mVs);
//		for(int i=0;i<10000;i++) {
//			getAisaOdds(mVs);
//		}
	}
}
