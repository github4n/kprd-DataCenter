package com.kprd.date.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

import com.kprd.common.utils.Contant;
import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.MD5Util;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.common.utils.TimeUtil;

public class LotteryInterface {
	
	private static String WAGENT = "6009";
	
	private static String WAGENT_KEY = "1234566009012345";
	
//	private static String WAGENT_URL = "http://219.234.83.38:8080/ctm/service.go";
//	private static String WAGENT_URL = "http://114.215.255.177:8080/ctm/service.go";
	private static String WAGENT_URL = "http://47.97.30.190/ctm/service.go";

	private static Document initPostConfig(String sid,String messageid,String memo,Element body){
		Document document = DocumentHelper.createDocument(); 
		Element request = document.addElement("request");
		Element head = request.addElement("head");
		head.addAttribute("sid", sid);
		head.addAttribute("agent", WAGENT);
		head.addAttribute("messageid", messageid);
		head.addAttribute("timestamp", "");//先为空
		head.addAttribute("memo", memo);
		if(ObjectHelper.isEmpty(body)){
			request.addElement("body");
		}else{
			request.add(body);
		}
		return document;
	}
	
	public static Boolean vsign(String xml,String sign){
		return MD5Util.encryption(xml+WAGENT_KEY).equals(sign);
	}
	
	private static Map<String,String> sign(Document document){
		Map<String,String> linkMap = new HashMap<String,String>();
		String xml = document.asXML();
		linkMap.put("xml", xml);
		linkMap.put("sign", MD5Util.encryption(xml+WAGENT_KEY));
		return linkMap;
	}
	
	
	
	
	/** 代理余额查询 */
	public static String balance(){
		Document document = initPostConfig("20010",null,null,null);
		Map<String,String> linkMap = sign(document);
		return HttpClientUtil.doPost(WAGENT_URL,linkMap);
	}
	
	/** *模拟* 出票结果查询【数字、足球、篮球】 */
	public static String lotteryResultsAnalog(String gid,String apply){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("apply", apply);
		Document document = initPostConfig("20008",null,null,body);
		return doPost(document);
	}
	
	/** 出票结果查询【数字、足球、篮球】 */
	public static String lotteryResults(String gid,String apply){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("apply", apply);
		Document document = initPostConfig("20008",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.err.println(xml);
		return xml;
	}
	
	/** 查询赛果【足球、篮球】 */
	public static String matchResults(String jk,String pid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("pid", pid);
		Document document = initPostConfig(jk,null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		return xml;
	}
	
	/** 查询赛果【足球、篮球】 */
	public static String matchResultsAnalog(String jk,String pid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("pid", pid);
		Document document = initPostConfig(jk,null,null,body);
		return doPost(document);
	}
	
	
	/** 查询当前期次 */
	public static String cx_dqqc20000(String gid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		Document document = initPostConfig("20000",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	
	/** 查询期次列表 */
	public static String cx_qclb20001(String gid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		Document document = initPostConfig("20001",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	
	/** 查询当前期次 */
	public static String cx_kjhm20002(String gid,String pid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("pid", pid);
		Document document = initPostConfig("20002",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	/** 查询当前期次 */
	public static String cx_kjgg20003(String gid,String pid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("pid", pid);
		Document document = initPostConfig("20003",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	/** 查询当前期次 */
	public static String cx_qczt20004(String gid,String pid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("pid", pid);
		Document document = initPostConfig("20004",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	/** 查询中奖名单 */
	public static String cx_zjmd20006(String gid,String pid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("pid", pid);
		query.addAttribute("pn", "1");
		Document document = initPostConfig("20006",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	/** 查询期次汇总 */
	public static String cx_qchz20007(String gid,String pid){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("pid", pid);
		Document document = initPostConfig("20007",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	/** 查询中奖 */
	////////////////////////
	//已确认竞彩-足球：WINFO：中奖注数|过关方式
	////////////////////////
	public static String cx_zj20009(String gid,String apply){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("gid", gid);
		query.addAttribute("apply", apply);
		Document document = initPostConfig("20009",null,null,body);
		Map<String,String> linkMap = sign(document);
		String xml = HttpClientUtil.doPost(WAGENT_URL,linkMap);
		System.out.println(xml);
		return xml;
	}
	
	public static void css(){
		Element body = new DOMElement("body");
		Element query = body.addElement("query");
		query.addAttribute("lotyid", "1");
		Document document = initPostConfig("30001",null,null,body);
		Map<String,String> linkMap = sign(document);
		System.out.println(document.asXML());
		System.out.println(HttpClientUtil.doPost(WAGENT_URL,linkMap));
	}
	
	public static void main(String[] args) {
//		css();
//		System.out.println(balance());
//		List<Ticket> list = new ArrayList<Ticket>();
//		Ticket ticket = new Ticket();
//		ticket.setApply("CS000000001-01");
//		ticket.setCodes("07,11,17,23,31|02,06:1:1");
//		ticket.setMoney(2);
//		ticket.setMulity(1);
//		ticket.setPid(2017044);
//		list.add(ticket);
//		lottery("10001","50","CS000000001",list);//数字彩种投注 (10001)
		cx_dqqc20000(switching(Contant.CATE_SZ_DLT));
		
//		cx_zjmd20006("50","2017044");//中奖名单
//		cx_kjhm20002("81","2017059");
//		cx_qczt20004("50","2017044");
//		lotteryResults("50","CS000000001-01");
//		cx_kjhm20002(switching(Contant.CATE_SZ_QXC),"2017050");
//		cx_kjgg20003(switching(Contant.CATE_SZ_QXC),"2017050");
//		cx_kjhm20002(switching(Contant.CATE_SZ_PL5),"2017114");
//		cx_kjgg20003(switching(Contant.CATE_SZ_PL5),"2017114");
//		cx_kjhm20002(switching(Contant.CATE_SZ_PL3),"2017114");
//		cx_kjgg20003(switching(Contant.CATE_SZ_PL3),"2017114");
//		cx_kjhm20002(switching(Contant.CATE_ZC_9),"2017064");
//		cx_kjgg20003(switching(Contant.CATE_SZ_DLT),"2017055");
//		cx_kjhm20002(switching(Contant.CATE_ZC_6),"2017064");
//		cx_kjgg20003(switching(Contant.CATE_ZC_6),"2017064");
//		cx_kjhm20002(switching(Contant.CATE_ZC_4),"2017064");
//		cx_kjgg20003(switching(Contant.CATE_ZC_4),"2017064");
		cx_zj20009(switching(Contant.CATE_SZ_QXC),"e54b7c7de92d4c95bd41c0d0231c6b5b");//中奖查询
	}
	
	/**////////////**以下代码用于模拟测试**/////////////**/
	
	public static String doPost(Document document){
		Element request = document.getRootElement();
		Element head_element = request.element("head");
		String head_sid = head_element.attributeValue("sid");//总体结果
		String head_messageid = head_element.attributeValue("messageid");//总体消息
		if(head_sid.equals("10001")||head_sid.equals("10002")||head_sid.equals("10003")){//投注，模拟出票成功
			Element body_element = request.element("body");
			Element tickets_element = body_element.element("tickets");
			List<Element> ticket_elements = tickets_element.elements("ticket");
			Document document_rp = DocumentHelper.createDocument(); 
			Element response = document_rp.addElement("response");
			Element head = response.addElement("head");
			head.addAttribute("sid", head_sid);
			head.addAttribute("agent", WAGENT);
			head.addAttribute("messageid", head_messageid);
			Element result = response.addElement("result");
			result.addAttribute("code", "0");
			result.addAttribute("desc", "成功");
			Element body = response.addElement("body");
			Element tickets = body.addElement("tickets");
			for(Element ticket_ele: ticket_elements){
				String head_apply = ticket_ele.attributeValue("apply");
				Element ticket = tickets.addElement("ticket");
				ticket.addAttribute("code", "0");
				ticket.addAttribute("desc", "成功");
				ticket.addAttribute("apply", head_apply);
			}
			return document_rp.asXML();
		}else if(head_sid.equals("20008")){//出票结果查询
			Element body_element = request.element("body");
			Element query_element = body_element.element("query");
			String query_apply = query_element.attributeValue("apply");
			
			Document document_rp = DocumentHelper.createDocument(); 
			Element response = document_rp.addElement("response");
			Element head = response.addElement("head");
			head.addAttribute("sid", head_sid);
			head.addAttribute("agent", WAGENT);
			head.addAttribute("messageid", head_messageid);
			Element result = response.addElement("result");
			result.addAttribute("code", "0");
			result.addAttribute("desc", "成功");
			Element body = response.addElement("body");
			Element rows = body.addElement("rows");
			Element row = rows.addElement("row");
			row.addAttribute("apply", query_apply);
			row.addAttribute("tcode", "0");
			row.addAttribute("tdesc", "成功");
			row.addAttribute("tid", query_apply);
			row.addAttribute("tdate", TimeUtil.format("yyyy-MM-dd HH:mm:ss"));
			row.addAttribute("memo", "");//10
			row.addAttribute("state", "2");
			return document_rp.asXML();
		}else if(head_sid.equals("30002")){//查询竞足赛果
			Element body_element = request.element("body");
			Element query_element = body_element.element("query");
			String query_pid = query_element.attributeValue("pid");
			Document document_rp = DocumentHelper.createDocument(); 
			Element response = document_rp.addElement("response");
			Element head = response.addElement("head");
			head.addAttribute("sid", head_sid);
			head.addAttribute("agent", WAGENT);
			head.addAttribute("messageid", head_messageid);
			Element result = response.addElement("result");
			result.addAttribute("code", "0");
			result.addAttribute("desc", "成功");
			Element body = response.addElement("body");
			Element rows = body.addElement("rows");
			Element row = rows.addElement("row");
			row.addAttribute("pid", query_pid);
			Integer hms = 0;
			Integer hss = 0;
			Integer ms = 1 + hms;
			Integer ss = 0 + hss;
			row.addAttribute("ms",String.valueOf(ms));
			row.addAttribute("ss",String.valueOf(ss));
			row.addAttribute("hms",String.valueOf(hms));
			row.addAttribute("hss",String.valueOf(hss));
			row.addAttribute("state", "2");//已开奖
			row.addAttribute("cancel", "0");//未取消
			row.addAttribute("iaudit", "1");//已审核
			return document_rp.asXML();
		}else if(head_sid.equals("30012")){//查询篮球赛果
			Element body_element = request.element("body");
			Element query_element = body_element.element("query");
			String query_pid = query_element.attributeValue("pid");
			Document document_rp = DocumentHelper.createDocument(); 
			Element response = document_rp.addElement("response");
			Element head = response.addElement("head");
			head.addAttribute("sid", head_sid);
			head.addAttribute("agent", WAGENT);
			head.addAttribute("messageid", head_messageid);
			Element result = response.addElement("result");
			result.addAttribute("code", "0");
			result.addAttribute("desc", "成功");
			Element body = response.addElement("body");
			Element rows = body.addElement("rows");
			Element row = rows.addElement("row");
			row.addAttribute("pid", query_pid);
			Integer ms = 99;
			Integer ss = 88;
			row.addAttribute("ms",String.valueOf(ms));
			row.addAttribute("ss",String.valueOf(ss));
			row.addAttribute("state", "2");//已开奖
			row.addAttribute("cancel", "0");//未取消
			row.addAttribute("iaudit", "1");//已审核
			return document_rp.asXML();
		}else if(head_sid.equals("2002")){//数字彩种-中奖情况
			
		}
		return "";
	}
	
	public static String switching(int category){
		switch (category) {
		case Contant.CATE_JC_ZQ:return "30";
		case Contant.CATE_JC_LQ:return "31";
		case Contant.CATE_SZ_DLT:return "50";
		case Contant.CATE_SZ_QXC:return "51";
		case Contant.CATE_SZ_PL3:return "53";
		case Contant.CATE_SZ_PL5:return "52";
		case Contant.CATE_ZC_14:return "80";
		case Contant.CATE_ZC_9:return "81";
		case Contant.CATE_ZC_4:return "82";
		case Contant.CATE_ZC_6:return "83";
		default:return null;
		}
	}
	
}
