package com.kprd.date.zq.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.nodes.Document;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.util.UtilBag;

public class TestClass {
	private static String url2 = "http://live.500.com/detail.php?fid=661503&r=1";
	
	private static String getDomString(String url) throws InterruptedException {
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
//			System.out.println("executing request " + httpget.getURI());
//			httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//			httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//			httpget.setHeader("User-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//			httpget.setHeader("Host", "odds.500.com");
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
	
	public static void fafa() {
		String string = "塞�加20";
		if(string.indexOf("�") > -1) {
			System.out.println(132);
		}
	}
	
	public static void main(String[] args) {
		try {
			fafa();
//			getDomString(url2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
