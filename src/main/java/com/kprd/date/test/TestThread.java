package com.kprd.date.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.util.UtilBag;

import net.sf.json.JSONArray;

public class TestThread extends Thread {

	private static String url = "http://192.168.1.109:8086/a/getQQinfo?number=2";
	
	public TestThread(String name) {
		super(name);
	}
	
	@Override
	public void run() {
		try {
			String html = "";
			try {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet httpget = new HttpGet(url);
				CloseableHttpResponse response = httpclient.execute(httpget);
				try {
					// 获取响应实体    
	                HttpEntity entity = response.getEntity();  
	                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
	                	html = UtilBag.handleEntityCharset(entity, "utf-8");
	                	if(ObjectHelper.isNotEmpty(html)) {
	                		JSONArray jsonObject = JSONArray.fromObject(html);
	                		if(ObjectHelper.isNotEmpty(jsonObject)) {
	                			System.out.println(jsonObject);
	                		}
	                	}
	                }
				} finally {
					response.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
