package com.kprd.date.constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kprd.common.utils.ObjectHelper;

/**
 * 正则表达式验证工具类
 * @author 霍俊
 */
public class RegularVerify {
	
	/**
	 * 判断是否是整数
	 */
	public static boolean isNum(String value){
		if(ObjectHelper.isEmpty(value))return false;
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}
	
	/**
	 * 判断是否是小数
	 */
	public static boolean isReal(String value){
		if(ObjectHelper.isEmpty(value))return false;
		Pattern pattern = Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,10}$");
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}
	
	public static void main(String[] args) {
		System.out.println(isNum("8954"));
		System.out.println(isNum(null));
		System.out.println(isNum(""));
		System.out.println(isNum("6546.5264"));
		System.out.println(isNum("dffg"));
		System.out.println(isNum("6546.52f4"));
		System.out.println(isNum("006546"));
		
		System.out.println(isReal("8954"));
		System.out.println(isReal(null));
		System.out.println(isReal(""));
		System.out.println(isReal("6546.5264"));
		System.out.println(isReal("dffg"));
		System.out.println(isReal("6546.52f4"));
		System.out.println(isReal("006546"));
		
	}

}
