package com.kprd.date.zq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonObject;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.liansai.mapper.MVsMapper;
import com.kprd.liansai.pojo.MVs;
import com.kprd.liansai.pojo.MVsExample;
import com.kprd.odds.pojo.Europodds;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JingCaiOdds {
	private static String baseUrl = "http://i.sporttery.cn/api/fb_match_info/get_europe/?mid=xx&_=yy";
	
	static MVsMapper mvsMapper = Main.applicationContext.getBean(MVsMapper.class);
	
	public static String getEuroOddsHtml() {
		String html = null;
		try {
			MVsExample mvsEx = new MVsExample();
			String day = UtilBag.dateUtil(+1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = day + " 12:00:00";
			Date kyo = sdf.parse(today);
			mvsEx.createCriteria().andStartTimeGreaterThan(kyo);
			mvsEx.setOrderByClause(" cc_id asc");
			List<MVs> mvsList = mvsMapper.selectByExample(mvsEx);
			System.out.println(mvsList.size());
			if(ObjectHelper.isNotEmpty(mvsList)) {
				for(MVs m : mvsList) {
//					baseUrl = baseUrl.replace("xx", m.getJingcaiid());
					baseUrl = baseUrl.replace("yy", String.valueOf(System.currentTimeMillis()));
					CloseableHttpClient httpclient = HttpClients.createDefault();
					HttpGet httpget = new HttpGet(baseUrl);
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
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static void parseHtml() {
		String json = getEuroOddsHtml();
		if(ObjectHelper.isNotEmpty(json)) {
			JSONObject jsonObject = JSONObject.fromObject(json);
			Object result = jsonObject.get("result");
			jsonObject = JSONObject.fromObject(result);
			JSONArray data = jsonObject.getJSONArray("data");
			for(int i=0;i<data.size();i++) {
				JSONObject obj = JSONObject.fromObject(data.get(i));
				if("皇冠".equals(obj.get("cn")) || "Bet365".equals(obj.get("cn")) || "立博".equals(obj.get("cn")) || "金宝博".equals(obj.get("cn")) 
						|| "澳门".equals(obj.get("cn")) || "威廉希尔".equals(obj.get("cn")) || "竞彩官方".equals(obj.get("cn")) || "必发".equals(obj.get("cn"))) {
					Europodds europodds = new Europodds();
					europodds.setCompanyname(obj.get("cn").toString());
					europodds.setJsops(obj.get("win").toString());
					europodds.setJsopp(obj.get("draw").toString());
					europodds.setJsopf(obj.get("lose").toString());
					String status = obj.get("win_change").toString();
					if("down".equals(status)) {
						europodds.setJsopsstatus("-1");
					} else if("equal".equals(status)) {
						europodds.setJsopsstatus("0");
					} else if("up".equals(status)) {
						europodds.setJsopsstatus("1");
					}
					europodds.setCsops(obj.get("win_s").toString());
					europodds.setCsopp(obj.get("draw_s").toString());
					europodds.setCsopf(obj.get("lose_s").toString());
					europodds.setOddss(obj.getString("win_ratio"));
					europodds.setOddsp(obj.getString("draw_ratio"));
					europodds.setOddsf(obj.getString("lose_ratio"));
					europodds.setJskailis(obj.getString("win_index"));
					europodds.setJskailip(obj.getString("draw_index"));
					europodds.setJskailif(obj.getString("lose_index"));
					//不能和原表重用
				}
			}
		}
	}
	
	public static void main(String[] args) {
		parseHtml();
	}
}
