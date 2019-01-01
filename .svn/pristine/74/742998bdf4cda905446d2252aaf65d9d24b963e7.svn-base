package com.kprd.date.util;

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @Title <>
 * @Description
 * @author Juce
 * @date 2017年3月14日 下午1:43:58
 * @version 1.0
 **/
public final class HttpClient {
	
	private HttpClient() {
	}

	public static String doGet(String url, Map<String, String> params, String charset)
			throws URISyntaxException {
		return service(url, params, new HttpGet(),charset);
	}
	
	public static String doPost(String url, Map<String, String> params, String charset)
			throws URISyntaxException {
		return service(url, params, new HttpPost(), charset);
	} 
	
	private static String service(String url, Map<String, String> params,
			HttpRequestBase hrb, String charset) throws URISyntaxException {
		List<NameValuePair> list = new LinkedList<NameValuePair>();
		Set<Entry<String, String>> set = params.entrySet();
		for (Entry<String, String> ety : set) {
			list.add(new BasicNameValuePair(ety.getKey(), ety.getValue()));
		}
		
		URIBuilder ub = new URIBuilder();
		ub.setPath(url);
		ub.setParameters(list);
		hrb.setURI(ub.build());
		return getResult(hrb,charset);
	}

	private static String getResult(HttpRequestBase request,String charset) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity, charset);
//				UtilBag.toFile(result.toString().getBytes(), new File("C:\\Users\\Administrator\\Desktop\\fetch\\httpClient.txt"));
				response.close();
				return result;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}
