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
import com.kprd.date.util.UtilBag;import com.kprd.liansai.pojo.MVs;
import com.kprd.odds.mapper.BigsmallMapper;
import com.kprd.odds.pojo.Bigsmall;
import com.kprd.odds.pojo.BigsmallExample;
import com.kprd.odds.pojo.BigsmallExample.Criteria;

/**
 * 大小球
 * @author Administrator
 *
 */
public class BigSmall {
	
	private static String baseUrl = "http://odds.500.com/fenxi/daxiao-bs.shtml";
	
	public static Bigsmall getBigSmall(MVs mvs) {
		
		//即时大
		String jsBig = null;
		//即时大状态
		String jsBigStatus = null;
		//即时pan
		String jsPan = null;
		//即时pan状态
		String jsPanStatus = null;
		//即时小
		String jsSmall = null;
		//即时小状态
		String jsSmallStatus = null;
		//即时时间
		String jsChangeTime = null;
		//初始时间
		String csChangeTime = null;
		//初始大
		String csBig = null;
		
		String csPan = null;
		
		String csSmall = null;
		
		//平均大
		 String pjBig = null;
		//平均盘
		 String pjPan = null;
		//平均小
		 String pjSmall = null;
		 
		 String company = null;
		 //及时盘数字
		 String jsRef = null;
		
		Bigsmall bs = new Bigsmall();
		 
		baseUrl = baseUrl.replace("bs", mvs.getXiid());
		try {
			String html = getDomString(baseUrl);
			Document doc = Jsoup.parse(html);
			baseUrl = "http://odds.500.com/fenxi/daxiao-bs.shtml";
			if(ObjectHelper.isNotEmpty(doc)) {
				BigsmallMapper bsMapper = Main.applicationContext.getBean(BigsmallMapper.class);
				
				Elements avgTrs = doc.select("tr");
				if(null != avgTrs && avgTrs.size() > 0) {
					for(Element avgTr : avgTrs) {
						///////////////////////////平均数据///////////////////////////
						if("footer".equals(avgTr.attr("xls"))) {
							Elements tds = avgTr.children();
							if(null != tds && tds.size() == 7) {
								//即时大小
								Element tr1 = tds.get(2).select("table tbody tr").get(0);
								Elements tds1 = tr1.children();
								pjBig = tds1.get(0).text().trim();
								pjPan = tds1.get(1).text().trim();
								pjSmall = tds1.get(2).text().trim();
								
								//初始大小
								Element tr2 = tds.get(4).select("table tbody tr").get(0);
								Elements tds2 = tr2.children();
								csBig = tds2.get(0).text().trim();
								csPan = tds2.get(1).text().trim();
								csSmall = tds2.get(2).text().trim();
								
								bs.setCompany("平均值");
								bs.setJsbig(pjBig);
								bs.setJspan(pjPan);
								bs.setJssmall(pjSmall);
								bs.setCsbig(csBig);
								bs.setCspan(csPan);
								bs.setCssmall(csSmall);
								bs.setReserve1(mvs.getXiid());
								//判断入库
								BigsmallExample bsEx = new BigsmallExample();
								Criteria c = bsEx.createCriteria();
								c.andCompanyEqualTo("平均值");
								c.andReserve1EqualTo(mvs.getXiid());
								//根据公司名称（"平均值"）查询库中是否已有数据
								List<Bigsmall> list = bsMapper.selectByExample(bsEx);
								if(ObjectHelper.isEmpty(list)) {
									bs.setBsid(IDUtils.createUUId());
									bsMapper.insert(bs);
								} else {
									bs.setBsid(list.get(0).getBsid());
									bsMapper.updateByPrimaryKey(bs);
								}
								break;
							} 
						}
					}
				}
				
				
				Elements fatherTrs = doc.select("tr");
				if(null != fatherTrs && fatherTrs.size() > 0) {
					for(Element fatherTr : fatherTrs) {
						///////////////////////////博彩公司数据////////////////////////
						if(("row".equals(fatherTr.attr("xls")) && "tr1".equals(fatherTr.attr("class"))) 
						|| (("row".equals(fatherTr.attr("xls")) && "tr2".equals(fatherTr.attr("class")))) ) {
							Elements spans = fatherTr.select("> td > p > a >span");
							if(null != spans && spans.size() > 0) {
								company = spans.get(0).text();
								if("皇冠".equals(company) || "Bet365".equals(company) || "立博".equals(company) || "金宝博".equals(company) 
								|| "澳门".equals(company) || "威廉希尔".equals(company) || "竞彩官方".equals(company) || "必发".equals(company)) {
									
									Elements tds = fatherTr.select(" > td");
									if(null != tds && tds.size() > 0) {
										//即时
										Elements tdss = tds.get(2).select("table tbody tr td");
										if(null != tdss && tdss.size() == 4) {
											jsRef = tdss.get(1).attr("ref");
											if(tdss.get(0).text().trim().length() == 4) {
												jsBig = tdss.get(0).text().trim();
											} else {
												jsBig = tdss.get(0).text().substring(0, 4);
											}
											jsPan = tdss.get(1).text().trim();
											if(tdss.get(2).text().length() == 4) {
												jsSmall = tdss.get(2).text().trim();
											}
											jsSmall = tdss.get(2).text().substring(0, 4);
											//暂时不要
//											tdss.get(4).text();
										}
									}
									jsChangeTime = tds.get(3).text();
									
									//初始
									Elements tdss2 = tds.get(4).select("table tbody tr td");
									if(null != tdss2 && tdss2.size() == 3) {
										csBig = tdss2.get(0).text().trim();
										csPan = tdss2.get(1).text().trim();
										csSmall = tdss2.get(2).text().trim();
									}
									
									csChangeTime = tds.get(5).text().trim();
									
									BigsmallExample bsEx = new BigsmallExample();
									Criteria c = bsEx.createCriteria();
									c.andCompanyEqualTo(company);
									c.andXiidEqualTo(mvs.getXiid());
									List<Bigsmall> bsList = bsMapper.selectByExample(bsEx);
									
									bs.setXiid(mvs.getXiid());
									bs.setCompany(company);;
									bs.setJsbig(jsBig);
									bs.setJsbigstatus("0");
									bs.setJspan(jsPan);
									bs.setJspanstatus("0");
									bs.setJssmall(jsSmall);
									bs.setJssmallstatus("0");
									bs.setJschangetime(jsChangeTime);
									bs.setCsbig(csBig);
									bs.setCspan(csPan);
									bs.setCssmall(csSmall);
									bs.setCschangetime(csChangeTime);
									bs.setAvgbig(pjBig);
									bs.setAvgpan(pjPan);
									bs.setAvgsmall(pjSmall);
									bs.setReserve2(jsRef);
									
									if(ObjectHelper.isNotEmpty(bsList)) {
										
									} else {
										//新增或刷新
										BigsmallExample bsEx1 = new BigsmallExample();
										Criteria criteria1 = bsEx1.createCriteria();
										criteria1.andCompanyEqualTo(company);
										criteria1.andJschangetimeEqualTo(jsChangeTime);
										//根据即时改变时间和公司名称查看库中是否已有数据
										List<Bigsmall> bigsmalls = bsMapper.selectByExample(bsEx1);
										if(ObjectHelper.isEmpty(bigsmalls)) {
											bs.setBsid(IDUtils.createUUId());
											bsMapper.insert(bs);
										} else {
											//判断即时大
											String dbData = bigsmalls.get(0).getJsbig();
											BigDecimal bd = new BigDecimal(dbData);
											BigDecimal page = new BigDecimal(jsBig);
											if(page.compareTo(bd) == 1) {
												bs.setJsbigstatus("1");
											} else if(page.compareTo(bd) == 0) {
												bs.setJsbigstatus("0");
											} else {
												bs.setJsbigstatus("-1");
											}
											
											//判断即时盘
											dbData = bigsmalls.get(0).getReserve2();
											bd = new BigDecimal(dbData);
											page = new BigDecimal(jsRef);
											if(page.compareTo(bd) == 1) {
												bs.setJspanstatus("1");
											} else if(page.compareTo(bd) == 0) {
												bs.setJspanstatus("0");
											} else {
												bs.setJspanstatus("-1");
											}
											
											//判断即时盘
											dbData = bigsmalls.get(0).getJssmall();
											bd = new BigDecimal(dbData);
											page = new BigDecimal(jsSmall);
											if(page.compareTo(bd) == 1) {
												bs.setJssmallstatus("1");
											} else if(page.compareTo(bd) == 0) {
												bs.setJssmallstatus("0");
											} else {
												bs.setJssmallstatus("-1");
											}
											bs.setBsid(bigsmalls.get(0).getBsid());
											bsMapper.updateByPrimaryKey(bs);
											return bs;
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
					return getBigSmall(mvs);
				}
			}
		}
//		JedisUtilForFetch.remove(Contant.JEDIS_SIX,mvs.getXiid() + "bigsmall");
//    	System.out.println("缓存已删除***********************bigsmall**********************************");
		return bs;
	}
	
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
		MVs mVs = new MVs();
		mVs.setXiid("648596");
		getBigSmall(mVs);
//		for(int i=0;i<10000;i++) {
//			getBigSmall(mVs);
//			System.out.println("大小" + i);
//		}
	}
}
