package com.kprd.date.zq;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.kprd.date.util.UtilBag;

import net.sf.json.JSONObject;

public class ZqTimer {
	
	private static String baseUrl = "http://odds.500.com/fenxi1/inc/ajax.php?_=1492410006803&t=oupei&cid=0&fid[]=578599&fid[]=578255&fid[]=520636&fid[]=520436&fid[]=436550&fid[]=436175&fid[]=400194&sid[]=1&sid[]=5&sid[]=5&sid[]=5&sid[]=5&sid[]=5&sid[]=5&r=1";
	
	public static void aaa() {
		System.out.println(System.currentTimeMillis());
		String abc = UtilBag.getHtmlForGZIP(baseUrl, "utf-8");
		JSONObject jsStr = JSONObject.fromObject(abc);
		Object object = jsStr.get("520636");
		System.out.println(1);
		
	}
	
	public static void main(String[] args) {
		try {
			Date d1 = new Date();
			String yes = UtilBag.dateUtil(-1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d2 = sdf.parse(yes);
			System.out.println(d1.after(d2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
