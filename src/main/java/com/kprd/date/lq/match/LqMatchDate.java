package com.kprd.date.lq.match;
/***
 * 篮球赛事-赛季数据采集
 */


import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.ChinaInitial;
import com.kprd.date.util.DocumetUtil;
import com.kprd.dic.mapper.DLqMatchMapper;
import com.kprd.dic.pojo.DLqMatch;
import com.kprd.dic.pojo.DLqMatchExample;
import com.kprd.dic.pojo.DLqMatchExample.Criteria;
import com.kprd.match.mapper.MLqLeaguematchMapper;
import com.kprd.match.pojo.MLqLeaguematch;
import com.kprd.match.pojo.MLqLeaguematchExample;


public class LqMatchDate {
	
	private static String host = "http://liansai.500.com/";
	
	private static String host_lq = "http://liansai.500.com/lq/";
	
	
    
    
	/**
	 * 获取赛事/赛季信息
	 */
	public static void getMatch() {
		int ss_count = 0;
		int sj_count = 0;
		DLqMatchMapper dLqMatchMapper = Main.applicationContext.getBean(DLqMatchMapper.class);
		MLqLeaguematchMapper mLqLeaguematchMapper = Main.applicationContext.getBean(MLqLeaguematchMapper.class);
		Document doc = DocumetUtil.getDocumentByUrl(host_lq);
		Elements elements = doc.select("#area_box");
		Elements es = elements.get(0).children();
		//System.out.println(es);
		if(null != es && es.size() > 0) {
			for(int i=0;i<es.size();i++) {
				if(i!=0&&i!=es.size()-1) {
					Elements ss =es.get(i).select(".title,.t-list ");
					int kind = i;
					for(int e=0;e<ss.size();e++){
						//System.out.println(ss.get(e));
						Elements ss2 =ss.get(e).select(".t-wrap .dis .navmenu > li > ul > li");
						for(int j=0;j<ss2.size();j++) {
							//赛事
							String id = IDUtils.createUUId();
							String fullName = ss2.get(j).getElementsByAttributeValue("href", "javascript:void(0)").text().trim();
							String letter = ChinaInitial.getPYIndexStr(fullName, true);
							
							DLqMatch dLqMatch = new DLqMatch();
							dLqMatch.setId(id);
							dLqMatch.setFullname(fullName);
							dLqMatch.setLetter(letter);
							dLqMatch.setKind(kind);
							/*System.out.println("===============赛事===============");
							System.out.println(id);
							System.out.println(fullName);
							System.out.println(letter);
							System.out.println(kind);*/
							
							Elements ss3 = ss2.get(j).getElementsByAttributeValue("target", "_blank");
							//System.out.println("=========赛季===========");
							for(int c=0;c<ss3.size();c++) {
								String lid = IDUtils.createUUId();
								String fid = ss3.get(c).attr("href").substring(1).replaceAll("/", "");
								String sname = ss3.get(c).text().trim();
								//System.out.println(lid);
								//System.out.println(fid);
								//System.out.println(sname);
								if(ObjectHelper.isNotEmpty(fid)) {
									//验证是否有该赛事
									DLqMatchExample example = new DLqMatchExample();
									Criteria createCriteria = example.createCriteria();		
									createCriteria.andFullnameEqualTo(fullName);
									List<DLqMatch> list = dLqMatchMapper.selectByExample(example);
									if(list.size()==0) {
										if(c==0) {//进入赛季详情获取 赛事相关数据
											String url = host_lq+fid+"/";
											Document doc1 = DocumetUtil.getDocumentByUrl(url);
											//System.out.println(url);
											Elements elements1 = doc1.select(".ls-info");
											Elements es1 = elements1.get(0).children();
											String icon = host+es1.select("img").attr("src");
											String name = es1.select(".dis").text().trim().substring(0, es1.select(".dis").text().trim().indexOf(" "));
											dLqMatch.setName(name);
											dLqMatch.setIcon(icon);
											//新增赛事
											dLqMatchMapper.insert(dLqMatch);
											System.out.println("新增赛事："+name);
											ss_count++;
											//System.out.println(host+icon);
											//System.out.println(name);
										}
								    }
									MLqLeaguematchExample lexample = new MLqLeaguematchExample();
									com.kprd.match.pojo.MLqLeaguematchExample.Criteria createCriteria2 = lexample.createCriteria();
									createCriteria2.andFIdEqualTo(Integer.parseInt(fid));
									List<MLqLeaguematch> list1 = mLqLeaguematchMapper.selectByExample(lexample);
									if(list1.size()==0) {
										MLqLeaguematch mLqLeaguematch = new MLqLeaguematch();
										mLqLeaguematch.setfId(Integer.parseInt(fid));
										mLqLeaguematch.setId(lid);
										mLqLeaguematch.setMatchid(id);
										mLqLeaguematch.setSname(sname);
										mLqLeaguematchMapper.insert(mLqLeaguematch);
										System.out.println(dLqMatch.getName() +"-新增 赛季："+sname);
										sj_count++;
									}
								} else {
									//........
								}
							}
						}
					}
				}
			}
			System.out.println("更新完毕>>>>>>>>>>>");
			System.out.println("共计新增赛事："+ss_count);
			System.out.println("共计新增赛季："+sj_count);
		}
	}
	
	public static void main(String[] args) {
		LqMatchDate.getMatch();
	}
}
