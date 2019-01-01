package com.kprd.date.zq;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kprd.date.util.LotteryInterface;

public class ZucaiGetEpisodes {
	
	/**
	 * 获取期号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getEpisodes(Integer type) {
		List<String> episodesList = new ArrayList<String>();
		try {
			String xml = LotteryInterface.cx_qclb20001(LotteryInterface.switching(type));
			Document dom4j = DocumentHelper.parseText(xml);
			Element root = dom4j.getRootElement();
        	List<Element> listEle = root.elements();
        	Element rowset = listEle.get(listEle.size() - 1);
			List<Element> matchInfo = rowset.elements("rows");
			for(int i=0;i<matchInfo.size();i++) {
				List<Element> contentList = matchInfo.get(i).elements();
				for(int j=0;j<contentList.size();j++) {
					episodesList.add(contentList.get(j).attributeValue("pid"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return episodesList;
	}
	
	
	public static void main(String[] args) {
		List<String> listString = getEpisodes(8);
		for(String str : listString) {
			System.out.println(str);
		}
	}
}
