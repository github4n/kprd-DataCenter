package com.kprd.date.zq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentHelper;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.util.UtilBag;

public class FetchOdds {
	
	private static Log logger = LogFactory.getLog("FetchOdds");
	
	private String tranStr(String str) {
		String[] strs = str.split("-");
		str = null;
		str = strs[0] + "," + strs[1] + strs[2];
		System.out.println(str);
		return str;
	}
	
	/**
	 * 返回查询多天的字符串
	 * @param startDate
	 * @param day
	 * @return
	 */
	private List<String[]> getStrList(String startDate,int day) {
		List<String[]> list = new ArrayList<String[]>();
		for(int i=0;i<day;i++) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(startDate);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, -i);
				date = calendar.getTime();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy@MM-dd");
				String dateStr = sf.format(date);
				String[] dateStrs = dateStr.split("@");
				list.add(dateStrs);
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * 获取时间段相差的天数
	 * @param d1
	 * @param d2
	 * @return
	 */
	private Integer getDays(String d1, String d2) {
		Integer days = null;
		try {
			if(ObjectHelper.isNotEmpty(d2)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				//开始日期
				Date start = sdf.parse(d1);
				//截止日期
				Date end = sdf.parse(d2);
				//相差天数(算上当天需要+1天)
				days = (int)((start.getTime() - end.getTime())/86400000) + 1;
			} else {
				days = 1;
			}
			System.out.println(days);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}

	@SuppressWarnings("unchecked")
	public void getZids(String d1,String d2) {
		logger.info("执行getZids方法，获取odds.xml");
		String mixOdds = null;
		LinkedList<List<String>> fatherList = new LinkedList<List<String>>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		Integer days = null;
		try {
			//odds地址
		    mixOdds = "http://trade.500.com/static/public/jczq/xml/odds/odds.xml";
			
		    if(ObjectHelper.isNotEmpty(d1) && ObjectHelper.isEmpty(d2)) {//只查询某一天
		    	mixOdds = "http://trade.500.com/static/public/jczq/xml/odds/odds.xml";
				System.out.println("odds请求地址 ===========>" + mixOdds);
				HttpGet httpget = new HttpGet(mixOdds);
				logger.info("请求地址为：" + mixOdds);
				
	           try {
	        	   // 执行get请求 
		            CloseableHttpResponse response = httpclient.execute(httpget);
					try {
						LinkedList<String> lList = new LinkedList<String>();
			            // 获取响应实体    
			            HttpEntity entity = response.getEntity();
			            if(null !=  entity) {
			            	String xml = EntityUtils.toString(entity, "utf-8");
			            	org.dom4j.Document dom4j = DocumentHelper.parseText(xml);
			            	org.dom4j.Element root = dom4j.getRootElement();
			            	List<org.dom4j.Element> listEle = root.elements();
							org.dom4j.Element rowset = listEle.get(listEle.size() - 1);
							List<org.dom4j.Element> matchInfo = rowset.elements("match");
							for(org.dom4j.Element match : matchInfo) {
								lList.add(match.attributeValue("id").toString());
							}
			            }
			            fatherList.add(lList);
					} finally {
						response.close();
					}
			} catch (Exception e) {
				if(e.getMessage().indexOf("必须由匹配的结束标记") > -1) {
					logger.info("该日期没有比赛 url =============>" + mixOdds);
				}
			}
		    	
		    } else if(ObjectHelper.isNotEmpty(d1) && ObjectHelper.isNotEmpty(d2)) {//查询时间段
		    	//相差天数
		    	days = getDays(d1, d2);
		    	//返回查询多天的字符串
		    	List<String[]> strings = getStrList(d1, days);
		    	
		    	for(int i=0;i<strings.size();i++) {
		    		mixOdds = mixOdds.replace("year", strings.get(i)[0]);
					mixOdds = mixOdds.replace("monthday", strings.get(i)[1].replace("-", ""));
					System.out.println("odds请求地址 ===========>" + mixOdds);
					HttpGet httpget = new HttpGet(mixOdds);
					//设置超时时间为1分钟
					RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
					httpget.setConfig(requestConfig);
					logger.info("请求地址为：" + mixOdds);
					
		            // 执行get请求 
		            CloseableHttpResponse response = httpclient.execute(httpget);
		            LinkedList<String> lList = new LinkedList<String>();
					try {
						try {
				            // 获取响应实体    
				            HttpEntity entity = response.getEntity();
				            if(null !=  entity) {
				            	String xml = EntityUtils.toString(entity, "utf-8");
				            	org.dom4j.Document dom4j = DocumentHelper.parseText(xml);
				            	org.dom4j.Element root = dom4j.getRootElement();
				            	List<org.dom4j.Element> listEle = root.elements();
								org.dom4j.Element rowset = listEle.get(listEle.size() - 1);
								List<org.dom4j.Element> matchInfo = rowset.elements("match");
								for(org.dom4j.Element match : matchInfo) {
									lList.add(match.attributeValue("id").toString());
								}
				            }
				            fatherList.add(lList);
						} finally {
							response.close();
						} 
						
					} catch (Exception e) {
						//捕获没有比赛的情况
						if(e.getMessage().indexOf("必须由匹配的结束标记") > -1) {
							logger.info("该日期没有比赛 url =============>" + mixOdds);
							mixOdds = "http://trade.500.com/static/public/jczq/xml/hisdata/year/monthday/odds.xml";
							fatherList.add(lList);
							continue;
						}
					}
					mixOdds = "http://trade.500.com/static/public/jczq/xml/hisdata/year/monthday/odds.xml";
		    	}
		    }
		    
		} catch (Exception e) {
			logger.error(e);
			logger.error("获取历史数据getZids()方法异常，请求地址为===============>" + mixOdds);
			e.printStackTrace();
		}
		logger.info("结果为：" + fatherList);
		if(ObjectHelper.isNotEmpty(fatherList) && fatherList.size() > 0) {
//			fetchHistoryMixed(fatherList, days,d1,d2);
		}
	}
	
	public void getOdds() {
		String url = "http://trade.500.com/jczq/?date=2017-03-03&playtype=both";
		Document doc = UtilBag.getDocumentByUrl(url);
		if(null != doc) {
			Elements tds = doc.select(".over_game");
			if(null != tds && tds.size() > 0) {
				tds.get(0).siblingElements();
			}
		}
	}
	
	public static void main(String[] args) {
		FetchOdds odds = new FetchOdds();
		odds.getOdds();
	}
}
