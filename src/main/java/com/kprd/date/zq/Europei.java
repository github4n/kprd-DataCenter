//package com.kprd.date.zq;
//
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import com.kprd.common.utils.Contant;
//import com.kprd.common.utils.IDUtils;
//import com.kprd.common.utils.ObjectHelper;
//import com.kprd.date.lq.update.Main;
//import com.kprd.date.util.JedisUtilForFetch;
//import com.kprd.date.util.UtilBag;
//import com.kprd.liansai.pojo.MVs;
//import com.kprd.odds.mapper.EuropoddsMapper;
//import com.kprd.odds.pojo.Europodds;
//import com.kprd.odds.pojo.EuropoddsExample;
//import com.kprd.odds.pojo.EuropoddsExample.Criteria;
//
///**
// * 采集8家欧赔
// * @author Administrator
// *
// */
//public class Europei {
//	
//	private static Log logger = LogFactory.getLog("Europei");
//	
//	//欧赔原始地址
//	private static String baseUrl = "http://odds.500.com/fenxi/ouzhi-oid.shtml";
//	
//	public static Europodds getEuroOdds(MVs mvs) {
//		//菠菜公司名称
//		String coName = null;
//		//公司地址
//		String coId = null;
//		//公司国籍
//		String nation = null;
//		//初盘即时欧赔胜平负胜
//		String jsopSpfS = null;
//		//初盘即时欧赔胜平负胜状态
//		String jsopSpfSStatus = null;
//		//初盘即时欧赔胜平负平
//		String jsopSpfP = null;
//		//初盘即时欧赔胜平负平状态
//		String jsopSpfPStatus = null;
//		//初盘即时欧赔胜平负负
//		String jsopSpfF = null;
//		//初盘即时欧赔胜平负负状态
//		String jsopSpfFStatus = null;
//		//即时欧赔胜平负胜
//		String opSpfS = null;
//		//即时欧赔胜平负平
//		String opSpfP = null;
//		//即时欧赔胜平负负
//		String opSpfF = null;
//		
//		
//		//即时概率胜平负胜
//		String jsglSpfS = null;
//		//即时概率胜平负平
//		String jsglSpfP = null;
//		//即时概率胜平负负
//		String jsglSpfF = null;
//		//概率胜平负胜
//		String glSpfS = null;
//		//概率胜平负平
//		String glSpfP = null;
//		//概率胜平负负
//		String glSpfF = null;
//		//即时返还率
//		String jsReturnRate = null;
//		//返还率
//		String returnRate = null;
//		//即时凯利指胜
//		String jsKailiS = null;
//		//即时凯利指胜状态
//		String jsKailiSStats = null;
//		//即时凯利指平
//		String jsKailiP = null;
//		//即时凯利指平状态
//		String jsKailiPStatus = null;
//		//即时凯利指负
//		String jsKailiF = null;
//		//即时凯利指负状态
//		String jsKailiFStatus = null;
//		//凯利指数胜
//		String kailiS = null;
//		//凯利指数平
//		String kailiP = null;
//		//凯利指数负
//		String kailiF = null;
//		
//		//即时平均胜
//		String jsPjS = null;
//		//即时平均胜
//		String jsPjP = null;
//		//即时平均胜
//		String jsPjF = null;
//		//更新时间
//		String dateTime = null;
//		//平均值对象
//		Europodds finalAvgOdds = new Europodds();
//		if("649135".equals(mvs.getXiid())) {
//			System.out.println("**********************************");
//		}
//		baseUrl = baseUrl.replace("oid", mvs.getXiid());
//		try {
//			String html = getDomString(baseUrl);
//			baseUrl = "http://odds.500.com/fenxi/ouzhi-oid.shtml";
//			Document doc = Jsoup.parse(html);
//			if(ObjectHelper.isNotEmpty(doc)) {
//				
//				EuropoddsMapper euroMapper = Main.applicationContext.getBean(EuropoddsMapper.class);
//				
//				Elements trs = doc.select("tr");
//				///////////////////////////平均数据///////////////////////////
//				if(null != trs && trs.size() > 0) {
//					int i=0;
//					for(Element tr : trs) {
//						if("footer".equals(tr.attr("xls"))) {
//							Elements tds = tr.children();
//							if(null != tds && tds.size() > 0) {
//								if(i == 0) {
//									//平均值
//									i++;
//									Europodds odds = new Europodds();
//									//初始平均即时欧赔
//									Element tr1a = tds.get(2).select("table tbody tr").get(0);
//									Elements tds1a = tr1a.children();
//									odds.setCsops(tds1a.get(0).text());
//									odds.setCsopp(tds1a.get(1).text());
//									odds.setCsopf(tds1a.get(2).text());
//									
//									//即时平均即时欧赔
//									Element tr1 = tds.get(2).select("table tbody tr").get(1);
//									Elements tds1 = tr1.children();
//									odds.setJsops(tds1.get(0).text().trim());
//									odds.setJsopp(tds1.get(1).text().trim());
//									odds.setJsopf(tds1.get(2).text().trim());
//									
//									//即时平均概率
//									Element tr2 = tds.get(3).select("table tbody tr").get(1);
//									Elements tds2 = tr2.children();
//									odds.setJsoddss(tds2.get(0).text().replace("%", ""));
//									odds.setJsoddsp(tds2.get(1).text().replace("%", ""));
//									odds.setJsoddsf(tds2.get(2).text().replace("%", ""));
//									
//									//初始平均概率
//									Element tr2n = tds.get(3).select("table tbody tr").get(0);
//									Elements tds2n = tr2n.children();
//									odds.setOddss(tds2n.get(0).text().replace("%", ""));
//									odds.setOddsp(tds2n.get(1).text().replace("%", ""));
//									odds.setOddsf(tds2n.get(2).text().replace("%", ""));
//									
//									
//									odds.setReturnrates(tds.get(4).select("table tbody tr td").get(0).text().replace("%", ""));
//									odds.setJsreturnrates(tds.get(4).select("table tbody tr td").get(1).text().replace("%", ""));
//									
//									//即时平均凯利
//									Element trn = tds.get(5).select("table tbody tr").get(1);
//									Elements tdsn = trn.children();
//									odds.setJskailis(tdsn.get(0).text());
//									odds.setJskailip(tdsn.get(1).text());
//									odds.setJskailif(tdsn.get(2).text());
//									
//									//初始平均凯利
//									Element tra = tds.get(5).select("table tbody tr").get(0);
//									Elements tdsa = tra.children();
//									odds.setKailis(tdsa.get(0).text());
//									odds.setKailip(tdsa.get(1).text());
//									odds.setKailif(tdsa.get(2).text());
//									odds.setCompanyname("平均值");
//									odds.setReserve1(mvs.getXiid());
//									
//									finalAvgOdds = odds;
//									
//									
//									EuropoddsExample euroEx = new EuropoddsExample();
//									Criteria criteria = euroEx.createCriteria();
//									criteria.andCompanynameEqualTo("平均值");
//									criteria.andReserve1EqualTo(mvs.getXiid());
//									List<Europodds> list = euroMapper.selectByExample(euroEx);
//									if(ObjectHelper.isEmpty(list)) {
//										odds.setEuroid(IDUtils.createUUId());
//										euroMapper.insert(odds);
//									} else {
//										odds.setEuroid(list.get(0).getEuroid());
//										euroMapper.updateByPrimaryKey(odds);
//									}
//								} else if(i==1) {
//									//最高值
//									i++;
//									Europodds odds = new Europodds();
//									Elements tdsH = tds.get(1).select("table tbody td");
//									odds.setCsops(tdsH.get(0).text());
//									odds.setCsopp(tdsH.get(1).text());
//									odds.setCsopf(tdsH.get(2).text());
//									odds.setJsops(tdsH.get(3).text());
//									odds.setJsopp(tdsH.get(4).text());
//									odds.setJsopf(tdsH.get(5).text());
//									
//									Elements tdsJ = tds.get(2).select("table tbody td");
//									odds.setOddss(tdsJ.get(0).text().replace("%", ""));
//									odds.setOddsp(tdsJ.get(1).text().replace("%", ""));
//									odds.setOddsf(tdsJ.get(2).text().replace("%", ""));
//									odds.setJsoddss(tdsJ.get(3).text().replace("%", ""));
//									odds.setJsoddsp(tdsJ.get(4).text().replace("%", ""));
//									odds.setJsoddsf(tdsJ.get(5).text().replace("%", ""));
//
//									Elements tdsK = tds.get(3).select("table tbody td");
//									odds.setReturnrates(tdsK.get(0).text().replace("%", ""));
//									odds.setJsreturnrates(tdsK.get(1).text().replace("%", ""));
//									
//									Elements tdsL = tds.get(4).select("table tbody td");
//									odds.setKailis(tdsL.get(0).text());
//									odds.setKailip(tdsL.get(1).text());
//									odds.setKailif(tdsL.get(2).text());
//									odds.setJskailis(tdsL.get(3).text());
//									odds.setJskailip(tdsL.get(4).text());
//									odds.setJskailif(tdsL.get(5).text());
//									odds.setCompanyname("最高值");
//									odds.setReserve1(mvs.getXiid());
//									
//									EuropoddsExample euroEx = new EuropoddsExample();
//									Criteria criteria = euroEx.createCriteria();
//									criteria.andCompanynameEqualTo("最高值");
//									criteria.andReserve1EqualTo(mvs.getXiid());
//									List<Europodds> list = euroMapper.selectByExample(euroEx);
//									if(ObjectHelper.isEmpty(list)) {
//										odds.setEuroid(IDUtils.createUUId());
//										euroMapper.insert(odds);
//									} else {
//										odds.setEuroid(list.get(0).getEuroid());
//										euroMapper.updateByPrimaryKey(odds);
//									}
//								} else if(i == 2) {
//									i++;
//									//最低值
//									Europodds odds = new Europodds();
//									Elements tdsH = tds.get(2).select("table tbody td");
//									odds.setCsops(tdsH.get(0).text());
//									odds.setCsopp(tdsH.get(1).text());
//									odds.setCsopf(tdsH.get(2).text());
//									odds.setJsops(tdsH.get(3).text());
//									odds.setJsopp(tdsH.get(4).text());
//									odds.setJsopf(tdsH.get(5).text());
//									
//									Elements tdsJ = tds.get(3).select("table tbody td");
//									odds.setOddss(tdsJ.get(0).text().replace("%", ""));
//									odds.setOddsp(tdsJ.get(1).text().replace("%", ""));
//									odds.setOddsf(tdsJ.get(2).text().replace("%", ""));
//									odds.setJsoddss(tdsJ.get(3).text().replace("%", ""));
//									odds.setJsoddsp(tdsJ.get(4).text().replace("%", ""));
//									odds.setJsoddsf(tdsJ.get(5).text().replace("%", ""));
//
//									Elements tdsK = tds.get(4).select("table tbody td");
//									odds.setReturnrates(tdsK.get(0).text().replace("%", ""));
//									odds.setJsreturnrates(tdsK.get(1).text().replace("%", ""));
//									
//									Elements tdsL = tds.get(5).select("table tbody td");
//									odds.setKailis(tdsL.get(0).text());
//									odds.setKailip(tdsL.get(1).text());
//									odds.setKailif(tdsL.get(2).text());
//									odds.setJskailis(tdsL.get(3).text());
//									odds.setJskailip(tdsL.get(4).text());
//									odds.setJskailif(tdsL.get(5).text());
//									odds.setCompanyname("最低值");
//									odds.setReserve1(mvs.getXiid());
//									
//									EuropoddsExample euroEx = new EuropoddsExample();
//									Criteria criteria = euroEx.createCriteria();
//									criteria.andCompanynameEqualTo("最低值");
//									criteria.andReserve1EqualTo(mvs.getXiid());
//									List<Europodds> list = euroMapper.selectByExample(euroEx);
//									if(ObjectHelper.isEmpty(list)) {
//										odds.setEuroid(IDUtils.createUUId());
//										euroMapper.insert(odds);
//									} else {
//										odds.setEuroid(list.get(0).getEuroid());
//										euroMapper.updateByPrimaryKey(odds);
//									}
//									break;
//								}
//							}
//						}
//					}
//				}
//				
//				if(null != trs && trs.size() > 0) {
//					for(Element tr : trs) {
//						///////////////////////////博彩公司数据////////////////////////
//						if("zy".equals(tr.attr("ttl"))) {
//							dateTime = tr.attr("data-time");
//							Elements tds = tr.select(" > td");
//							if(null != tds && tds.size() >= 2) {
//								String company = tds.get(1).attr("title").trim();
//								if("皇冠".equals(company) || "Bet365".equals(company) || "立博".equals(company) || "金宝博".equals(company) 
//								|| "澳门".equals(company) || "威廉希尔".equals(company) || "竞彩官方".equals(company) || "必发".equals(company)) {
//									for(int i=0;i<tds.size();i++) {
//										//博彩公司名字
//										if(i==1) {
//											coName = tds.get(i).attr("title").trim();
//											Elements as = tds.get(i).select("a");
//											if(null != as && as.size() > 0){
//												coId = as.get(0).attr("href").split("=")[1];
//											}
//											continue;
//										}
//										
//										//即时欧赔
//										else if(i==2) {
//											Element table = tds.get(i).select("table").get(0);
//											Elements tdI = table.select("tr td");
//											if(null != tdI && tdI.size() == 6) {
//												opSpfS = tdI.get(0).text().trim();
//												opSpfP = tdI.get(1).text().trim();
//												opSpfF = tdI.get(2).text().trim();
//												jsopSpfS = tdI.get(3).text().trim();
//												jsopSpfP = tdI.get(4).text().trim();
//												jsopSpfF = tdI.get(5).text().trim();
//												continue;
//											}
//										}
//										//即时概率
//										else if(i==3) {
//											Elements tdI =  tds.get(i).select("tr td");
//											if(null != tdI && tdI.size() == 6) {
//												glSpfS = tdI.get(0).text().trim().replace("%", "");
//												glSpfP = tdI.get(1).text().trim().replace("%", "");
//												glSpfF = tdI.get(2).text().trim().replace("%", "");
//												jsglSpfS = tdI.get(3).text().trim().replace("%", "");
//												jsglSpfP = tdI.get(4).text().trim().replace("%", "");
//												jsglSpfF = tdI.get(5).text().trim().replace("%", "");
//												continue;
//											}
//										}
//										//返还率
//										else if(i==4) {
//											Elements tdI =  tds.get(i).select("tr td");
//											if(null != tdI && tdI.size() == 2) {
//												returnRate = tdI.get(0).text().trim().replace("%", "");
//												jsReturnRate = tdI.get(1).text().trim().replace("%", "");
//												continue;
//											}
//										}
//										//即时凯利
//										else if(i==5) {
//											Elements tdI =  tds.get(i).select("tr td");
//											if(null != tdI && tdI.size() == 6) {
//												kailiS = tdI.get(0).text().trim();
//												kailiP = tdI.get(1).text().trim();
//												kailiF = tdI.get(2).text().trim();
//												jsKailiS = tdI.get(3).text().trim();
//												jsKailiP =  tdI.get(4).text().trim();
//												jsKailiF = tdI.get(5).text().trim();
//												
//												EuropoddsExample euroExample = new EuropoddsExample();
//												Criteria criteria = euroExample.createCriteria();
//												criteria.andReserve1EqualTo(dateTime);
//												criteria.andCompanynameEqualTo(company);
//												List<Europodds> euroList = euroMapper.selectByExample(euroExample);
//												Europodds odd = new Europodds();
//												
//												odd.setReserve1(dateTime);
//												odd.setXiid(mvs.getXiid());
//												odd.setCompanyname(coName);
//												odd.setJsops(jsopSpfS);
//												odd.setJsopsstatus("0");
//												odd.setJsopp(jsopSpfP);
//												odd.setJsoppstatus("0");
//												odd.setJsopf(jsopSpfF);
//												odd.setJsopfstatus("0");
//												odd.setCsops(opSpfS);
//												odd.setCsopp(opSpfP);
//												odd.setCsopf(opSpfF);
//												odd.setOddss(glSpfS);
//												odd.setOddsp(glSpfP);
//												odd.setOddsf(glSpfF);
//												odd.setJsoddss(jsglSpfS);
//												odd.setJsoddsp(jsglSpfP);
//												odd.setJsoddsf(jsglSpfF);
//												odd.setReturnrates(returnRate);
//												odd.setJsreturnrates(jsReturnRate);
//												odd.setKailis(kailiS);
//												odd.setKailip(kailiP);
//												odd.setKailif(kailiF);
//												odd.setJskailis(jsKailiS);
//												odd.setJskailisstatus("0");
//												odd.setJskailip(jsKailiP);
//												odd.setJskailipstatus("0");
//												odd.setJskailif(jsKailiF);
//												odd.setJskailifstatus("0");
//												
//												if(ObjectHelper.isNotEmpty(euroList)) {
//													continue;
//												} else {
//													EuropoddsExample ex = new EuropoddsExample();
//													Criteria cr = ex.createCriteria();
//													cr.andCompanynameLike(company);
//													cr.andXiidEqualTo(mvs.getXiid());
//													ex.setOrderByClause(" reserve1 desc ");
//													List<Europodds> eList = euroMapper.selectByExample(ex);
//													
//													if(ObjectHelper.isEmpty(eList)) {//
//														odd.setEuroid(IDUtils.createUUId());
//														euroMapper.insert(odd);
//													} else {
//														String time = eList.get(0).getReserve1();
//														if(time.equals(dateTime)) {
//															continue;
//														}
//														String dbData = null;
//														dbData = eList.get(0).getJsops();
//														//判断即时胜
//														BigDecimal bd = new BigDecimal(dbData);
//														BigDecimal page = new BigDecimal(jsopSpfS);
//														if(page.compareTo(bd) == -1) {//页面数据比库中小
//															odd.setJsopsstatus("-1");
//														} else if(page.compareTo(bd) == 0) {
//															odd.setJsopsstatus("0");
//														} else {
//															odd.setJsopsstatus("1");
//														}
//														
//														dbData = eList.get(0).getJsopp();
//														//判断即时平
//														bd = new BigDecimal(dbData);
//														page = new BigDecimal(jsopSpfP);
//														if(page.compareTo(bd) == -1) {//页面数据比库中小
//															odd.setJsoppstatus("-1");
//														} else if(page.compareTo(bd) == 0) {
//															odd.setJsoppstatus("0");
//														} else {
//															odd.setJsoppstatus("1");
//														}
//														
//														dbData = eList.get(0).getJsopf();
//														//判断即时负
//														bd = new BigDecimal(dbData);
//														page = new BigDecimal(jsopSpfF);
//														if(page.compareTo(bd) == -1) {//页面数据比库中小
//															odd.setJsopfstatus("-1");
//														} else if(page.compareTo(bd) == 0) {
//															odd.setJsopfstatus("0");
//														} else {
//															odd.setJsopfstatus("1");
//														}
//														
//														dbData = eList.get(0).getJskailis();
//														//判断即时凯利胜
//														bd = new BigDecimal(dbData);
//														page = new BigDecimal(jsKailiS);
//														if(page.compareTo(bd) == -1) {//页面数据比库中小
//															odd.setJskailisstatus("-1");
//														} else if(page.compareTo(bd) == 0) {
//															odd.setJskailisstatus("0");
//														} else {
//															odd.setJskailisstatus("1");
//														}
//														
//														dbData = eList.get(0).getJskailip();
//														//判断即时凯利平
//														bd = new BigDecimal(dbData);
//														page = new BigDecimal(jsKailiP);
//														if(page.compareTo(bd) == -1) {//页面数据比库中小
//															odd.setJskailipstatus("-1");
//														} else if(page.compareTo(bd) == 0) {
//															odd.setJskailipstatus("0");
//														} else {
//															odd.setJskailipstatus("1");
//														}
//														
//														dbData = eList.get(0).getJskailif();
//														//判断即时凯利负
//														bd = new BigDecimal(dbData);
//														page = new BigDecimal(jsKailiF);
//														if(page.compareTo(bd) == -1) {//页面数据比库中小
//															odd.setJskailifstatus("-1");
//														} else if(page.compareTo(bd) == 0) {
//															odd.setJskailifstatus("0");
//														} else {
//															odd.setJskailifstatus("1");
//														}
//														odd.setEuroid(eList.get(0).getEuroid());
//														euroMapper.updateByPrimaryKey(odd);
//														continue;
//													}
//												}
//											}
//										} 
//									}
//								} 
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1
//					|| e.getMessage().indexOf("String input must not be null") > -1) {
//				try {
//					Thread.sleep(60*1000);
//				} catch (Exception e1) {
//					return getEuroOdds(mvs);
//				}
//			} else {
//			System.exit(0);
//			}
//		}
//		JedisUtilForFetch.remove(Contant.JEDIS_SIX,mvs.getXiid() + "euro");
//    	System.out.println("缓存已删除***********************euro**********************************");
//		return finalAvgOdds;
//	}
//	
//	/**
//	 * 
//	 * @param args
//	 */
//	private static String getDomString(String url) {
//		String html = null;
//		try {
//			CloseableHttpClient httpclient = HttpClients.createDefault();
//			HttpGet httpget = new HttpGet(url);  
//            System.out.println("executing request " + httpget.getURI());  
//            httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//            httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//            httpget.setHeader("Host","odds.500.com");
//            CloseableHttpResponse response = httpclient.execute(httpget);
//            try {
//            	// 获取响应实体    
//                HttpEntity entity = response.getEntity();  
//                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
//                	html = UtilBag.handleEntityCharset(entity, "gb2312");
//                	if(ObjectHelper.isNotEmpty(html)) {
//                		return html;
//                	}
//                }	
//			} finally {
//				response.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return html;
//	}
//	
//	public static void main(String[] args) {
//		MVs mVs = new MVs();
//		mVs.setXiid("648596");
//		Europei.getEuroOdds(mVs);
////		for(int i=0;i<10000;i++) {
////			Europei.getEuroOdds(mVs);
////			System.out.println(i);
////		}
//	}
//}
