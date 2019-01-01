package com.kprd.date.fetch.jingcai.football;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.FoResultMapper;
import com.kprd.newliansai.pojo.FoResult;
import com.kprd.newliansai.pojo.FoResultExample;

import net.sf.json.JSONObject;

/**
 * 足球结果小段
 * @author Administrator
 *
 */
public class SoccerResult {
	
	private static String baseUrl = "http://smart.wowcai.cn/app/score.go?lotid=51";
	
	private static FoResultMapper foResultMapper = Main.applicationContext.getBean(FoResultMapper.class);
	/**
	 * 抓取足球结果
	 */
	public static void getResult() {
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(baseUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(!html.substring(0,1).equals("{")) {
                		html = "{" + html.substring(2,html.length());
                	}
                	html = UtilBag.unicode2String(html);
                	JSONObject jsonObject = JSONObject.fromObject(html);
                	if(ObjectHelper.isNotEmpty(jsonObject) && !jsonObject.isEmpty()) {
                		@SuppressWarnings("rawtypes")
						Iterator it = jsonObject.keys();
                		while(it.hasNext()) {
                			FoResult foRes = new FoResult();
                			String key = it.next().toString();
                			JSONObject jObj = JSONObject.fromObject(jsonObject.get(key));
                			foRes.setPname(jObj.get("pname").toString());
                			foRes.setMtime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(jObj.get("mtime").toString()));
                			foRes.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(jObj.get("date").toString()));
                			foRes.setPid(jObj.get("pid").toString());
                			foRes.setLeague(jObj.get("league").toString());
                			foRes.setHomename(jObj.get("homename").toString());
                			foRes.setAwayname(jObj.get("awayname").toString());
                			foRes.setRq(jObj.get("rq").toString());
                			foRes.setScore(jObj.get("score").toString());
                			foRes.setHalfscore(jObj.get("halfscore").toString());
                			foRes.setSpfname(jObj.get("spf_name").toString());
                			foRes.setRqspfname(jObj.get("rqspf_name").toString());
                			foRes.setCbfname(jObj.get("cbf_name").toString());
                			foRes.setJqsname(jObj.get("jqs_name").toString());
                			foRes.setBqcname(jObj.get("bqc_name").toString());
                			foRes.setSpf(jObj.get("spf").toString());
                			foRes.setRqspf(jObj.get("rqspf").toString());
                			foRes.setBqc(jObj.get("bqc").toString());
                			foRes.setCbf(jObj.get("cbf").toString());
                			foRes.setJqs(jObj.get("jqs").toString());
                			FoResultExample reEx = new FoResultExample();
                			reEx.createCriteria().andPidEqualTo(key);
                			List<FoResult> list = foResultMapper.selectByExample(reEx);
                			if(ObjectHelper.isEmpty(list)) {
                				foRes.setIdresult(IDUtils.createUUId());
                				foResultMapper.insert(foRes);
                			} else {
                				foRes.setIdresult(list.get(0).getIdresult());
                				foResultMapper.updateByPrimaryKeySelective(foRes);
                			}
                		}
                	}
                }
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		while(true) {
			try {
				getResult();
				System.out.println("完成时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); 
				Thread.sleep(1000*60*5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
}
