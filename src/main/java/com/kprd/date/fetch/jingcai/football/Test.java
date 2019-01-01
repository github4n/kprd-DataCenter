package com.kprd.date.fetch.jingcai.football;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.kprd.date.util.UtilBag;

public class Test {
	
	public static void A() {
		try {
			int a = 0;
			System.out.println(a/0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过历史页面获取idunique
	 * @param date
	 * @param num
	 * @return
	 */
	private static String getRightDate(String date,String num) {
		String rightDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String w = UtilBag.getWeekOfDate(sdf.parse(date));
			w = w.substring(2,3);
			w = UtilBag.getWeekDay(w);
			if(num.equals(w)) {
				rightDate = date;
				return rightDate;
			} else {
				rightDate = UtilBag.dateUtilWithDate(-1, sdf.parse(date));
				getRightDate(rightDate, num);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rightDate;
	}
	
	public static String getIdUniquePartI(String str) {
		String result = null;
		String s = str.substring(0,8);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date da = sdf.parse(s);
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			s = sdf.format(da);
			String st = str.substring(8,s.length()-1);
			String last3 = str.substring(9,12);
			result = getRightDate(s, st);
			result = result.replace("-", "");
			result = result + st + last3;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	
	public static void main(String[] args) {
//		A();
		getIdUniquePartI("201708067042");
	}
}
