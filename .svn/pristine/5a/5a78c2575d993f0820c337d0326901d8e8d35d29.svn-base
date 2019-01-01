package com.kprd.date.constant;

import java.util.Map;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.JsonUtils;

public class HttpClientInterface {
	
	public static <T> T request(String url ,Map<String,String> param,Class<T> classes){
		String jsonStr = HttpClientUtil.doGet(url, param);
		return JsonUtils.jsonToPojo(jsonStr, classes);
	}

}
