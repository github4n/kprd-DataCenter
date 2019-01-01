package com.kprd.date.fetch.jingcai.basketball;

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
import com.kprd.newliansai.mapper.BaResultMapper;
import com.kprd.newliansai.pojo.BaResult;
import com.kprd.newliansai.pojo.BaResultExample;

import net.sf.json.JSONObject;

/**
 * 篮球赛果
 * @author Administrator
 *
 */
public class BastekResult {
	
	private static String baseUrl = "http://smart.wowcai.cn/app/score.go?lotid=52";
	
	private static BaResultMapper baMapper = Main.applicationContext.getBean(BaResultMapper.class);
	
	public static void getBasketResult() {
		String html = "";
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
                			BaResult ba = new BaResult();
                			String key = it.next().toString();
                			JSONObject jObj = JSONObject.fromObject(jsonObject.get(key));
                			ba.setPname(jObj.get("pname").toString());
                			ba.setLeague(jObj.get("league").toString());
                			ba.setMtime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(jObj.get("mtime").toString()));
                			ba.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(jObj.get("date").toString()));
                			ba.setPid(jObj.get("pid").toString());
                			ba.setHomename(jObj.get("homename").toString());
                			ba.setAwayname(jObj.get("awayname").toString());
                			ba.setScore(jObj.get("score").toString());
                			ba.setTotal(jObj.get("total").toString());
                			ba.setSf(jObj.get("sf").toString());
                			ba.setSfname(jObj.get("sf_name").toString());
                			ba.setCz(jObj.get("cz").toString());
                			ba.setSfc(jObj.get("sfc").toString());
                			ba.setSfcname(jObj.get("sfc_name").toString());
                			ba.setRfsf(jObj.get("rfsf").toString());
                			ba.setRfsfname(jObj.get("rfsf_name").toString());
                			ba.setDxf(jObj.get("dxf").toString());
                			ba.setDxfname(jObj.get("dxf_name").toString());
                			BaResultExample ex = new BaResultExample();
                			ex.createCriteria().andPidEqualTo(key);
                			List<BaResult> list = baMapper.selectByExample(ex);
                			if(ObjectHelper.isEmpty(list)) {
                				ba.setIdbasketresult(IDUtils.createUUId());
                				baMapper.insert(ba);
                			} else {
                				ba.setIdbasketresult(list.get(0).getIdbasketresult());
                				baMapper.updateByPrimaryKeySelective(ba);
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
		try {
			getBasketResult();
			System.out.println("完成时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); 
			Thread.sleep(1000*60*5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
