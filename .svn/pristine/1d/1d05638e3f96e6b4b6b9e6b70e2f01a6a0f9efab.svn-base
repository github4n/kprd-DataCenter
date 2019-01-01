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
import com.kprd.newliansai.mapper.FoEuropeMapper;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoEurope;
import com.kprd.newliansai.pojo.FoEuropeExample;
import com.kprd.newliansai.pojo.FoEuropeExample.Criteria;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

/**
 * 获取欧赔页面信息
 * @author Administrator
 *
 */
public class WubaiEurop {
	
	//欧赔原始地址
	private static String baseUrl = "http://odds.500.com/fenxi/ouzhi-oid.shtml";
	
	@SuppressWarnings("unused")
	public static FoEurope getEuroOdds(FoMix mvs) {
		//菠菜公司名称
		String coName = null;
		//公司地址
		String coId = null;
		//公司国籍
		String nation = null;
		//初盘即时欧赔胜平负胜
		String jsopSpfS = null;
		//初盘即时欧赔胜平负胜状态
		String jsopSpfSStatus = null;
		//初盘即时欧赔胜平负平
		String jsopSpfP = null;
		//初盘即时欧赔胜平负平状态
		String jsopSpfPStatus = null;
		//初盘即时欧赔胜平负负
		String jsopSpfF = null;
		//初盘即时欧赔胜平负负状态
		String jsopSpfFStatus = null;
		//即时欧赔胜平负胜
		String opSpfS = null;
		//即时欧赔胜平负平
		String opSpfP = null;
		//即时欧赔胜平负负
		String opSpfF = null;
		
		
		//即时概率胜平负胜
		String jsglSpfS = null;
		//即时概率胜平负平
		String jsglSpfP = null;
		//即时概率胜平负负
		String jsglSpfF = null;
		//概率胜平负胜
		String glSpfS = null;
		//概率胜平负平
		String glSpfP = null;
		//概率胜平负负
		String glSpfF = null;
		//即时返还率
		String jsReturnRate = null;
		//返还率
		String returnRate = null;
		//即时凯利指胜
		String jsKailiS = null;
		//即时凯利指胜状态
		String jsKailiSStats = null;
		//即时凯利指平
		String jsKailiP = null;
		//即时凯利指平状态
		String jsKailiPStatus = null;
		//即时凯利指负
		String jsKailiF = null;
		//即时凯利指负状态
		String jsKailiFStatus = null;
		//凯利指数胜
		String kailiS = null;
		//凯利指数平
		String kailiP = null;
		//凯利指数负
		String kailiF = null;
		
		//即时平均胜
		String jsPjS = null;
		//即时平均胜
		String jsPjP = null;
		//即时平均胜
		String jsPjF = null;
		//更新时间
		String dateTime = null;
		//平均值对象
		FoEurope finalAvgOdds = new FoEurope();
		if("649135".equals(mvs.getWubaiid())) {
			System.out.println("**********************************");
		}
		baseUrl = baseUrl.replace("oid", mvs.getWubaiid());
		try {
			String html = getDomString(baseUrl);
			baseUrl = "http://odds.500.com/fenxi/ouzhi-oid.shtml";
			Document doc = Jsoup.parse(html);
			if(ObjectHelper.isNotEmpty(doc)) {
				
				FoEuropeMapper euroMapper = Main.applicationContext.getBean(FoEuropeMapper.class);
				
				Elements trs = doc.select("tr");
				///////////////////////////平均数据///////////////////////////
				if(null != trs && trs.size() > 0) {
					int i=0;
					for(Element tr : trs) {
						if("footer".equals(tr.attr("xls"))) {
							Elements tds = tr.children();
							if(null != tds && tds.size() > 0) {
								if(i == 0) {
									//平均值
									i++;
									FoEurope odds = new FoEurope();
									//初始平均即时欧赔
									Element tr1a = tds.get(2).select("table tbody tr").get(0);
									Elements tds1a = tr1a.children();
									odds.setCsophomewin(tds1a.get(0).text());
									odds.setCsopdraw(tds1a.get(1).text());
									odds.setCsopawaywin(tds1a.get(2).text());
									
									//即时平均即时欧赔
									Element tr1 = tds.get(2).select("table tbody tr").get(1);
									Elements tds1 = tr1.children();
									odds.setNewophomewin(tds1.get(0).text().trim());
									odds.setNewophomedraw(tds1.get(1).text().trim());
									odds.setNewopawaywin(tds1.get(2).text().trim());
									
									//即时平均概率
									Element tr2 = tds.get(3).select("table tbody tr").get(1);
									Elements tds2 = tr2.children();
									odds.setProbabilityhome(tds2.get(0).text().replace("%", ""));
									odds.setProbabilitydraw(tds2.get(1).text().replace("%", ""));
									odds.setProbabilityaway(tds2.get(2).text().replace("%", ""));
									
									//初始平均概率 (为了统一，不采集初始概率)
									/*Element tr2n = tds.get(3).select("table tbody tr").get(0);
									Elements tds2n = tr2n.children();
									odds.setOddss(tds2n.get(0).text().replace("%", ""));
									odds.setOddsp(tds2n.get(1).text().replace("%", ""));
									odds.setOddsf(tds2n.get(2).text().replace("%", ""));*/
									
									//为了统一，没有采集初始返还率
									odds.setReturnrates(tds.get(4).select("table tbody tr td").get(1).text().replace("%", ""));
//									odds.setJsreturnrates(tds.get(4).select("table tbody tr td").get(1).text().replace("%", ""));
									
									//即时平均凯利
									Element trn = tds.get(5).select("table tbody tr").get(1);
									Elements tdsn = trn.children();
									odds.setKailihome(tdsn.get(0).text());
									odds.setKailidraw(tdsn.get(1).text());
									odds.setKailiaway(tdsn.get(2).text());
									
									//为了统一,初始平均凯利
									/*Element tra = tds.get(5).select("table tbody tr").get(0);
									Elements tdsa = tra.children();
									odds.setKailis(tdsa.get(0).text());
									odds.setKailip(tdsa.get(1).text());
									odds.setKailif(tdsa.get(2).text());*/
									odds.setCompanyname("平均值");
									odds.setReserve1(mvs.getIdunique());
									
									finalAvgOdds = odds;
									
									FoEuropeExample euroEx = new FoEuropeExample();
									Criteria criteria = euroEx.createCriteria();
									criteria.andCompanynameEqualTo("平均值");
									criteria.andReserve1EqualTo(mvs.getIdunique());
									List<FoEurope> list = euroMapper.selectByExample(euroEx);
									if(ObjectHelper.isEmpty(list)) {
										odds.setId(IDUtils.createUUId());
										odds.setIdunique(mvs.getIdunique());
										euroMapper.insert(odds);
									} else {
										odds.setId(list.get(0).getId());
										odds.setIdunique(list.get(0).getIdunique());
										euroMapper.updateByPrimaryKeySelective(odds);
									}
								} else if(i==1) {
									//最高值
									i++;
									FoEurope odds = new FoEurope();
									Elements tdsH = tds.get(1).select("table tbody td");
									odds.setCsophomewin(tdsH.get(0).text());
									odds.setCsopdraw(tdsH.get(1).text());
									odds.setCsopawaywin(tdsH.get(2).text());
									odds.setNewophomewin(tdsH.get(3).text());
									odds.setNewophomedraw(tdsH.get(4).text());
									odds.setNewopawaywin(tdsH.get(5).text());
									
									Elements tdsJ = tds.get(2).select("table tbody td");
									odds.setProbabilityhome(tdsJ.get(3).text().replace("%", ""));
									odds.setProbabilitydraw(tdsJ.get(4).text().replace("%", ""));
									odds.setProbabilityaway(tdsJ.get(5).text().replace("%", ""));
									//为了统一
									/*odds.setJsoddss(tdsJ.get(3).text().replace("%", ""));
									odds.setJsoddsp(tdsJ.get(4).text().replace("%", ""));
									odds.setJsoddsf(tdsJ.get(5).text().replace("%", ""));*/

									Elements tdsK = tds.get(3).select("table tbody td");
									odds.setReturnrates(tdsK.get(1).text().replace("%", ""));
									//为了统一
									/*odds.setJsreturnrates(tdsK.get(1).text().replace("%", ""));*/
									
									Elements tdsL = tds.get(4).select("table tbody td");
									/*odds.setKailis(tdsL.get(0).text());
									odds.setKailip(tdsL.get(1).text());
									odds.setKailif(tdsL.get(2).text());*/
									odds.setKailihome(tdsL.get(3).text());
									odds.setKailidraw(tdsL.get(4).text());
									odds.setKailiaway(tdsL.get(5).text());
									odds.setCompanyname("最高值");
									odds.setReserve1(mvs.getIdunique());
									
									FoEuropeExample euroEx = new FoEuropeExample();
									Criteria criteria = euroEx.createCriteria();
									criteria.andCompanynameEqualTo("最高值");
									criteria.andReserve1EqualTo(mvs.getIdunique());
									List<FoEurope> list = euroMapper.selectByExample(euroEx);
									if(ObjectHelper.isEmpty(list)) {
										odds.setId(IDUtils.createUUId());
										odds.setIdunique(mvs.getIdunique());
										euroMapper.insert(odds);
									} else {
										odds.setId(list.get(0).getId());
										odds.setIdunique(list.get(0).getIdunique());
										euroMapper.updateByPrimaryKeySelective(odds);
									}
								} else if(i == 2) {
									i++;
									//最低值
									FoEurope odds = new FoEurope();
									Elements tdsH = tds.get(2).select("table tbody td");
									odds.setCsophomewin(tdsH.get(0).text());
									odds.setCsopdraw(tdsH.get(1).text());
									odds.setCsopawaywin(tdsH.get(2).text());
									odds.setNewophomewin(tdsH.get(3).text());
									odds.setNewophomedraw(tdsH.get(4).text());
									odds.setNewopawaywin(tdsH.get(5).text());
									
									Elements tdsJ = tds.get(3).select("table tbody td");
									//为了统一
									/*odds.setOddss(tdsJ.get(0).text().replace("%", ""));
									odds.setOddsp(tdsJ.get(1).text().replace("%", ""));
									odds.setOddsf(tdsJ.get(2).text().replace("%", ""));*/
									
									odds.setProbabilityhome(tdsJ.get(3).text().replace("%", ""));
									odds.setProbabilitydraw(tdsJ.get(4).text().replace("%", ""));
									odds.setProbabilityaway(tdsJ.get(5).text().replace("%", ""));

									Elements tdsK = tds.get(4).select("table tbody td");
									odds.setReturnrates(tdsK.get(1).text().replace("%", ""));
									//为了统一
									/*odds.setJsreturnrates(tdsK.get(1).text().replace("%", ""));*/
									
									Elements tdsL = tds.get(5).select("table tbody td");
									//为了统一
									/*odds.setKailis(tdsL.get(0).text());
									odds.setKailip(tdsL.get(1).text());
									odds.setKailif(tdsL.get(2).text());*/
									odds.setKailihome(tdsL.get(3).text());
									odds.setKailidraw(tdsL.get(4).text());
									odds.setKailiaway(tdsL.get(5).text());
									odds.setCompanyname("最低值");
									odds.setReserve1(mvs.getIdunique());
									
									FoEuropeExample euroEx = new FoEuropeExample();
									Criteria criteria = euroEx.createCriteria();
									criteria.andCompanynameEqualTo("最低值");
									criteria.andReserve1EqualTo(mvs.getIdunique());
									List<FoEurope> list = euroMapper.selectByExample(euroEx);
									if(ObjectHelper.isEmpty(list)) {
										odds.setId(IDUtils.createUUId());
										odds.setIdunique(mvs.getIdunique());
										euroMapper.insert(odds);
									} else {
										odds.setId(list.get(0).getId());
										odds.setIdunique(list.get(0).getIdunique());
										euroMapper.updateByPrimaryKeySelective(odds);
									}
									break;
								}
							}
						}
					}
				}
				
				if(null != trs && trs.size() > 0) {
					for(Element tr : trs) {
						///////////////////////////博彩公司数据////////////////////////
						if("zy".equals(tr.attr("ttl"))) {
							dateTime = tr.attr("data-time");
							Elements tds = tr.select(" > td");
							if(null != tds && tds.size() >= 2) {
								String company = tds.get(1).attr("title").trim();
								if("皇冠".equals(company) || "Bet365".equals(company) || "立博".equals(company) || "金宝博".equals(company) 
								|| "澳门".equals(company) || "威廉希尔".equals(company) || "竞彩官方".equals(company) || "必发".equals(company)) {
									for(int i=0;i<tds.size();i++) {
										//博彩公司名字
										if(i==1) {
											coName = tds.get(i).attr("title").trim();
											Elements as = tds.get(i).select("a");
											if(null != as && as.size() > 0){
												coId = as.get(0).attr("href").split("=")[1];
											}
											continue;
										}
										
										//即时欧赔
										else if(i==2) {
											Element table = tds.get(i).select("table").get(0);
											Elements tdI = table.select("tr td");
											if(null != tdI && tdI.size() == 6) {
												opSpfS = tdI.get(0).text().trim();
												opSpfP = tdI.get(1).text().trim();
												opSpfF = tdI.get(2).text().trim();
												jsopSpfS = tdI.get(3).text().trim();
												jsopSpfP = tdI.get(4).text().trim();
												jsopSpfF = tdI.get(5).text().trim();
												continue;
											}
										}
										//即时概率
										else if(i==3) {
											Elements tdI =  tds.get(i).select("tr td");
											if(null != tdI && tdI.size() == 6) {
												glSpfS = tdI.get(0).text().trim().replace("%", "");
												glSpfP = tdI.get(1).text().trim().replace("%", "");
												glSpfF = tdI.get(2).text().trim().replace("%", "");
												jsglSpfS = tdI.get(3).text().trim().replace("%", "");
												jsglSpfP = tdI.get(4).text().trim().replace("%", "");
												jsglSpfF = tdI.get(5).text().trim().replace("%", "");
												continue;
											}
										}
										//返还率
										else if(i==4) {
											Elements tdI =  tds.get(i).select("tr td");
											if(null != tdI && tdI.size() == 2) {
												returnRate = tdI.get(0).text().trim().replace("%", "");
												jsReturnRate = tdI.get(1).text().trim().replace("%", "");
												continue;
											}
										}
										//即时凯利
										else if(i==5) {
											Elements tdI =  tds.get(i).select("tr td");
											if(null != tdI && tdI.size() == 6) {
												jsKailiS = tdI.get(3).text().trim();
												jsKailiP =  tdI.get(4).text().trim();
												jsKailiF = tdI.get(5).text().trim();
												
												FoEuropeExample euroExample = new FoEuropeExample();
												Criteria criteria = euroExample.createCriteria();
												criteria.andCompanynameEqualTo(company).andIduniqueEqualTo(mvs.getIdunique());
												List<FoEurope> euroList = euroMapper.selectByExample(euroExample);
												FoEurope odd = new FoEurope();
												
//												odd.setReserve1(dateTime);
												odd.setIdunique(mvs.getIdunique());
												odd.setCompanyname(coName);
												odd.setNewophomewin(jsopSpfS);
												odd.setNewophomewinstatus("0");
												odd.setNewophomedraw(jsopSpfP);
												odd.setNewophomedrawstatus("0");
												odd.setNewopawaywin(jsopSpfF);
												odd.setNewopawaywinstatus("0");
												odd.setCsophomewin(opSpfS);
												odd.setCsopdraw(opSpfP);
												odd.setCsopawaywin(opSpfF);
												odd.setProbabilityhome(jsglSpfS);
												odd.setProbabilitydraw(jsglSpfP);
												odd.setProbabilityaway(jsglSpfF);
												odd.setReturnrates(jsReturnRate);
												odd.setKailihome(jsKailiS);
												odd.setKailihomestatus("0");
												odd.setKailidraw(jsKailiP);
												odd.setKailidrawstatus("0");
												odd.setKailiaway(jsKailiF);
												odd.setKailiawaystatus("0");
												
												//凯利和即时胜平负需要再获取下状态
												
												if(ObjectHelper.isNotEmpty(euroList)) {
													continue;
												} else {
													FoEuropeExample ex = new FoEuropeExample();
													Criteria cr = ex.createCriteria();
													cr.andCompanynameLike(company);
													cr.andIduniqueEqualTo(mvs.getIdunique());
													ex.setOrderByClause(" reserve1 desc ");
													List<FoEurope> eList = euroMapper.selectByExample(ex);
													
													if(ObjectHelper.isEmpty(eList)) {//
														odd.setId(IDUtils.createUUId());
														odd.setIdunique(mvs.getIdunique());
														euroMapper.insert(odd);
													} else {
														String time = eList.get(0).getReserve1();
														if(time.equals(dateTime)) {
															continue;
														}
														String dbData = null;
														dbData = eList.get(0).getNewophomewin();
														//判断即时胜
														BigDecimal bd = new BigDecimal(dbData);
														BigDecimal page = new BigDecimal(jsopSpfS);
														if(page.compareTo(bd) == -1) {//页面数据比库中小
															odd.setNewophomewin("-1");
														} else if(page.compareTo(bd) == 0) {
															odd.setNewophomewin("0");
														} else {
															odd.setNewophomewin("1");
														}
														
														dbData = eList.get(0).getNewophomedraw();
														//判断即时平
														bd = new BigDecimal(dbData);
														page = new BigDecimal(jsopSpfP);
														if(page.compareTo(bd) == -1) {//页面数据比库中小
															odd.setNewophomedrawstatus("-1");
														} else if(page.compareTo(bd) == 0) {
															odd.setNewophomedrawstatus("0");
														} else {
															odd.setNewophomedrawstatus("1");
														}
														
														dbData = eList.get(0).getNewopawaywin();
														//判断即时负
														bd = new BigDecimal(dbData);
														page = new BigDecimal(jsopSpfF);
														if(page.compareTo(bd) == -1) {//页面数据比库中小
															odd.setNewopawaywinstatus("-1");
														} else if(page.compareTo(bd) == 0) {
															odd.setNewopawaywinstatus("0");
														} else {
															odd.setNewopawaywinstatus("1");
														}
														
														dbData = eList.get(0).getKailihome();
														//判断即时凯利胜
														bd = new BigDecimal(dbData);
														page = new BigDecimal(jsKailiS);
														if(page.compareTo(bd) == -1) {//页面数据比库中小
															odd.setKailihomestatus("-1");
														} else if(page.compareTo(bd) == 0) {
															odd.setKailihomestatus("0");
														} else {
															odd.setKailihomestatus("1");
														}
														
														dbData = eList.get(0).getKailidraw();
														//判断即时凯利平
														bd = new BigDecimal(dbData);
														page = new BigDecimal(jsKailiP);
														if(page.compareTo(bd) == -1) {//页面数据比库中小
															odd.setKailidrawstatus("-1");
														} else if(page.compareTo(bd) == 0) {
															odd.setKailidrawstatus("0");
														} else {
															odd.setKailidrawstatus("1");
														}
														
														dbData = eList.get(0).getKailiaway();
														//判断即时凯利负
														bd = new BigDecimal(dbData);
														page = new BigDecimal(jsKailiF);
														if(page.compareTo(bd) == -1) {//页面数据比库中小
															odd.setKailiawaystatus("-1");
														} else if(page.compareTo(bd) == 0) {
															odd.setKailiawaystatus("0");
														} else {
															odd.setKailiawaystatus("1");
														}
														odd.setId(eList.get(0).getId());
														odd.setIdunique(eList.get(0).getIdunique());
														euroMapper.updateByPrimaryKeySelective(odd);
														continue;
													}
												}
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
			e.printStackTrace();
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1
					|| e.getMessage().indexOf("String input must not be null") > -1) {
				try {
					Thread.sleep(60*1000);
				} catch (Exception e1) {
					return getEuroOdds(mvs);
				}
			} else {
			System.exit(0);
			}
		}
		JedisUtilForFetch.remove(6,mvs.getIdunique() + "euro");
    	System.out.println("缓存已删除***********************euro**********************************");
		return finalAvgOdds;
	}
	
	/**
	 * 
	 * @param args
	 */
	private static String getDomString(String url) {
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);  
            System.out.println("executing request " + httpget.getURI());  
            httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
            httpget.setHeader("Host","odds.500.com");
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		return html;
                	}
                }	
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
					getEuroOdds(mix);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
