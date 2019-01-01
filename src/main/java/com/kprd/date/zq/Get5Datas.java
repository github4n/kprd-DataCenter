package com.kprd.date.zq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.liansai.mapper.MVsMapper;
import com.kprd.liansai.pojo.MVs;
import com.kprd.liansai.pojo.MVsExample;
import com.kprd.liansai.pojo.MVsExample.Criteria;
/**
 * 获取 胜平负，让球胜平负，比分，总进球，全半场
 * @author Administrator
 *
 */
public class Get5Datas {

	private static String baseUrl = "http://trade.500.com/jczq/index.php?playid=312";

	public static void get5Info() {
		MVsMapper mvsMapper = Main.applicationContext.getBean(MVsMapper.class);
		MVsExample mvsEx = new MVsExample();
		Criteria criteria = mvsEx.createCriteria();
		String zid = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			criteria.andEndTimeGreaterThanOrEqualTo(sdf.parse(date));
			List<MVs> mvsList = mvsMapper.selectByExample(mvsEx);
			if (ObjectHelper.isNotEmpty(mvsList)) {
				Document doc = UtilBag.getDocumentByUrl(baseUrl);
				if (ObjectHelper.isNotEmpty(doc)) {
					Element element = doc.select("#bet_content").get(0);
					Elements elements = element.select("table tr");
					if (ObjectHelper.isNotEmpty(elements)) {
						for (Element tr : elements) {
							for (int i = 0; i < mvsList.size(); i++) {
								zid = mvsList.get(i).getZid();
								if (tr.attr("zid").endsWith(zid)) {
									MVs mvsData = get5Data(tr, mvsList.get(i), zid);
									mvsMapper.updateByPrimaryKey(mvsData);
									System.out.println("完成一行" + mvsData.getXiid());
								}
							}
						}
					}
				}
			}
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("当天数据执行完一遍" + sdf.format(new Date()));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("重新执行");
			get5Info();
		}
	}

	/**
	 * 获取 胜平负，让球胜平负，比分，总进球，全半场
	 * 
	 * @param tr
	 */
	private static MVs get5Data(Element tr, MVs mvs, String li) {
		try {
			Elements dvs = tr.select("td > div");
			if (null != dvs && dvs.size() > 0) {
				for (int i = 0; i < dvs.size(); i++) {
					if ((dvs.get(i).attr("class").indexOf("bet_odds") > -1
							&& dvs.get(i).attr("class").indexOf("bet_btns") > -1)
							|| (dvs.get(i).attr("class").indexOf("bet_more") > -1)) {
						if (i == 1) {// 胜平负
							// (胜平负)是否中标标示，0:胜，1：平，2：负
							Elements spans = dvs.get(i).children();
							if (null != spans && spans.size() > 0 && spans.size() == 3) {
								// odds_bingo
								for (int j = 0; j < spans.size(); j++) {
									Elements bingoSpan = spans.get(j).select(".odds_bingo");
									if (j == 0) {
										// 检测是否中标（odds_bingo为红色的样式class）
										if (null != bingoSpan && bingoSpan.size() > 0) {
											mvs.setSpfSBingo("1");
										}
										String basic_spf_win = spans.get(j).select(".odds_item").attr("data-sp").trim();
										mvs.setSpfS(basic_spf_win);
									} else if (j == 1) {
										// 检测是否中标（odds_bingo为红色的样式class）
										if (null != bingoSpan && bingoSpan.size() > 0) {
											mvs.setSpfPBingo("1");
										}
										String basic_spf_draw = spans.get(j).select(".odds_item").attr("data-sp")
												.trim();
										mvs.setSpfP(basic_spf_draw);
									} else if (j == 2) {
										// 检测是否中标（odds_bingo为红色的样式class）
										if (null != bingoSpan && bingoSpan.size() > 0) {
											mvs.setSpfFBingo("1");
										}
										String basic_spf_lose = spans.get(j).select(".odds_item").attr("data-sp")
												.trim();
										mvs.setSpfF(basic_spf_lose);
									}
								}
							} else if (null != spans && spans.size() > 0 && spans.size() == 1) {
								// 未开售的情况先暂时不管
							}
						} else if (i == 2) {// 让球胜平负
							// (让球胜平负)是否中标标示，0:胜，1：平，2：负
							Elements spans = dvs.get(i).select(".odds_item");
							if (null != spans && spans.size() > 0) {
								for (int j = 0; j < spans.size(); j++) {
									if (spans.get(j).attr("class").indexOf("odds_item") > -1) {
										Elements bingoSpan = spans.get(j).select(".odds_bingo");
										if (j == 0) {// 让球胜
											if (null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setRqSpfSBingo("1");
											}
											String rqs = spans.get(j).attr("data-sp").trim();
											mvs.setRqSpfS(rqs);
										} else if (j == 1) {// 让球平
											if (null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setRqSpfPBingo("1");
											}
											String rqp = spans.get(j).attr("data-sp").trim();
											mvs.setRqSpfP(rqp);
										} else if (j == 2) {// 让球负
											if (null != bingoSpan && bingoSpan.size() > 0) {
												mvs.setRqSpfFBingo("1");
											}
											String rqf = spans.get(j).attr("data-sp").trim();
											mvs.setRqSpfF(rqf);
										}
									}
								}
							}
						} else if (i == 5) {// 比分（跳过2是因为2的数据不全）
							Elements spans = dvs.get(i).children();
							if (null != spans && spans.size() > 0) {
								String longData = spans.get(0).attr("data-sp").trim();
								getScoreData(longData, mvs);
							}
						} else if (i == 8) {// 总进球
							Elements spans = dvs.get(i).children();
							if (null != spans && spans.size() > 0) {
								String longData = spans.get(0).attr("data-sp");
								getTotalGoalOrQbc(longData, mvs);
							}
						} else if (i == 11) {// 全半场
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							// 抓取半场
							LinkedHashMap<String, String> halfMap = halfData(sdf.format(new Date()), li, mvs);
							if (halfMap.size() == 0) {
								Elements spans = dvs.get(i).children();
								if (null != spans && spans.size() > 0) {
									String longData = spans.get(0).attr("data-sp");
									getQbc(longData, mvs);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mvs;
	}
	
	/**
	 * 获取全半场数据包括bingo
	 * @param theday
	 * @param li
	 * @return
	 */
	private static LinkedHashMap<String, String> halfData(String theday,String li,MVs mvs) {
		//半场数据地址
		String halfUrl = "http://trade.500.com/jczq/index.php?playid=272&date=";
		halfUrl += theday;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Document doc = UtilBag.getDocumentByUrl(halfUrl);
		if(null != doc) {
			Elements trs = doc.select("tr");
			if(null != trs && trs.size() > 0) {
				for(Element tr : trs) {
					if(li.equals(tr.attr("zid"))) {
						Elements divs = tr.select(".bet_odds");
						if(null != divs && divs.size() > 0) {
							Elements spans = divs.get(0).children();
							if(null != spans && spans.size() > 0) {
								for(Element span : spans) {
									if(span.attr("class").indexOf("odds_bingo") > -1) {//bingo
										String type = span.attr("value").trim();
										String value = span.attr("data-sp").trim() + "bingo";
										map.put(type, value);
										if("33".equals(type)) {
											mvs.setQbcSsBingo("1");
											mvs.setQbcSs(value);
										} else if("31".equals(type)) {
											mvs.setQbcSpBingo("1");
											mvs.setQbcSp(value);
										} else if("30".equals(type)) {
											mvs.setQbcSfBingo("1");
											mvs.setQbcSf(value);
										} else if("13".equals(type)) {
											mvs.setQbcPsBingo("1");
											mvs.setQbcPs(value);
										} else if("11".equals(type)) {
											mvs.setQbcPpBingo("1");
											mvs.setQbcPp(value);
										} else if("10".equals(type)) {
											mvs.setQbcPfBingo("1");
											mvs.setQbcPf(value);
										} else if("03".equals(type)) {
											mvs.setQbcFsBingo("1");
											mvs.setQbcFs(value);
										} else if("01".equals(type)) {
											mvs.setQbcFpBingo("1");
											mvs.setQbcFp(value);
										} else if("00".equals(type)) {
											mvs.setQbcFfBingo("1");
											mvs.setQbcFf(value);
										}
									} else {
										String type = span.attr("value").trim();
										String value = span.attr("data-sp").trim();
										map.put(type, value);
										if("33".equals(type)) {
											mvs.setQbcSs(value);
										} else if("31".equals(type)) {
											mvs.setQbcSp(value);
										} else if("30".equals(type)) {
											mvs.setQbcSf(value);
										} else if("13".equals(type)) {
											mvs.setQbcPs(value);
										} else if("11".equals(type)) {
											mvs.setQbcPp(value);
										} else if("10".equals(type)) {
											mvs.setQbcPf(value);
										} else if("03".equals(type)) {
											mvs.setQbcFs(value);
										} else if("01".equals(type)) {
											mvs.setQbcFp(value);
										} else if("00".equals(type)) {
											mvs.setQbcFf(value);
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		return map;
	}
	
	/**
     * 总进球或者全半场弹窗数据
     * @param longData
     */
    private static void getTotalGoalOrQbc(String longData,MVs mvs) {
    	String[] step1 = longData.split(",");
    	
    	String finalScore = mvs.getFinalScore();
    	if(ObjectHelper.isNotEmpty(finalScore)) {
    		String[] score = finalScore.split(":");
        	//比分之和
        	int sum = Integer.valueOf(score[0]) + Integer.valueOf(score[1]);
        	for(int i=0;i<step1.length;i++) { 
        		if(i==0) {
        			if(i==sum) {
        				mvs.setZjq0Bingo("1");
        			}
        			mvs.setZjq0(step1[i].split("\\|")[1]);
        		} else if(i==1) {
        			if(i==sum) {
        				mvs.setZjq1Bingo("1");
        			}
        			mvs.setZjq1(step1[i].split("\\|")[1]);
        		} else if(i==2) {
        			if(i==sum) {
        				mvs.setZjq2Bingo("1");
        			}
        			mvs.setZjq2(step1[i].split("\\|")[1]);
        		} else if(i==3) {
        			if(i==sum) {
        				mvs.setZjq3Bingo("1");
        			}
        			mvs.setZjq3(step1[i].split("\\|")[1]);
        		} else if(i==4) {
        			if(i==sum) {
        				mvs.setZjq4Bingo("1");
        			}
        			mvs.setZjq4(step1[i].split("\\|")[1]);
        		} else if(i==5) {
        			if(i==sum) {
        				mvs.setZjq5Bingo("1");
        			}
        			mvs.setZjq5(step1[i].split("\\|")[1]);
        		} else if(i==6) {
        			if(i==sum) {
        				mvs.setZjq6Bingo("1");
        			}
        			mvs.setZjq6(step1[i].split("\\|")[1]);
        		} else if(i==7) {
        			if(i==sum) {
        				mvs.setZjq7Bingo("1");
        			}
        			mvs.setZjq7(step1[i].split("\\|")[1]);
        		}
        	}
    	} else {
    		for(int i=0;i<step1.length;i++) { 
        		if(i==0) {
        			mvs.setZjq0(step1[i].split("\\|")[1]);
        		} else if(i==1) {
        			mvs.setZjq1(step1[i].split("\\|")[1]);
        		} else if(i==2) {
        			mvs.setZjq2(step1[i].split("\\|")[1]);
        		} else if(i==3) {
        			mvs.setZjq3(step1[i].split("\\|")[1]);
        		} else if(i==4) {
        			mvs.setZjq4(step1[i].split("\\|")[1]);
        		} else if(i==5) {
        			mvs.setZjq5(step1[i].split("\\|")[1]);
        		} else if(i==6) {
        			mvs.setZjq6(step1[i].split("\\|")[1]);
        		} else if(i==7) {
        			mvs.setZjq7(step1[i].split("\\|")[1]);
        		}
        	}
    	}
    }

	/**
	 * 全半场纯数据，没有bingo
	 * @param longData
	 * @param mvs
	 */
	private static void getQbc(String longData,MVs mvs) {
		String[] step1 = longData.split(",");
		for(int i=0;i<step1.length;i++) { 
    		if(i==0) {
    			mvs.setQbcSs(step1[i].split("\\|")[1]);
    		} else if(i==1) {
    			mvs.setQbcSp(step1[i].split("\\|")[1]);
    		} else if(i==2) {
    			mvs.setQbcSf(step1[i].split("\\|")[1]);
    		} else if(i==3) {
    			mvs.setQbcPs(step1[i].split("\\|")[1]);
    		} else if(i==4) {
    			mvs.setQbcPp(step1[i].split("\\|")[1]);
    		} else if(i==5) {
    			mvs.setQbcPf(step1[i].split("\\|")[1]);
    		} else if(i==6) {
    			mvs.setQbcFs(step1[i].split("\\|")[1]);
    		} else if(i==7) {
    			mvs.setQbcFp(step1[i].split("\\|")[1]);
    		} else if(i==8) {
    			mvs.setQbcFf(step1[i].split("\\|")[1]);
    		}
    	}
	}
	
	/**
	 * 获取比分弹窗详细数据
	 * 
	 * @param longData
	 */
	private static void getScoreData(String longData, MVs mvs) {

		LinkedHashMap<String, String> bfLists = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> bfListp = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> bfListpf = new LinkedHashMap<String, String>();

		String[] longs = longData.split("A");

		for (int i = 1; i < longs.length; i++) {
			if (i == 1) {// 胜其他
				String step1 = longs[i].substring(0, longs[i].length() - 1);
				String sqtVal = longs[i];
				sqtVal = sqtVal.substring(1, step1.indexOf(","));
				mvs.setScoreWinOther(sqtVal);
				step1 = step1.substring(step1.indexOf(",") + 1);

				String[] step2s = step1.split(",");
				for (String st : step2s) {
					bfLists.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
				}
			} else if (i == 2) {// 平其他
				String step1 = longs[i].substring(0, longs[i].length() - 1);
				String pqtVal = longs[i];
				pqtVal = pqtVal.substring(1, step1.indexOf(","));
				mvs.setScoreDrawOther(pqtVal);
				step1 = step1.substring(step1.indexOf(",") + 1);

				String[] step2s = step1.split(",");
				for (String st : step2s) {
					bfListp.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
				}
			} else if (i == 3) {// 负其他
				String step1 = longs[i].substring(0, longs[i].length());
				String fqtVal = longs[i];
				fqtVal = fqtVal.substring(1, step1.indexOf(","));
				mvs.setScoreLoseOther(fqtVal);
				step1 = step1.substring(step1.indexOf(",") + 1);

				String[] step2s = step1.split(",");
				for (String st : step2s) {
					bfListpf.put(st.split("\\|")[0].trim(), st.split("\\|")[1].trim());
				}
			}
		}

		sealedBfLists(bfLists, mvs);
		sealedBfListp(bfListp, mvs);
		sealedBfListf(bfListpf, mvs);

	}

	/**
	 * 封装负其他
	 * 
	 * @param bfListpf
	 * @param mvs
	 */
	@SuppressWarnings("rawtypes")
	private static void sealedBfListf(LinkedHashMap<String, String> bfListpf, MVs mvs) {
		Iterator<Entry<String, String>> iter = bfListpf.entrySet().iterator();
		String finalScore = mvs.getFinalScore();
		if (ObjectHelper.isNotEmpty(finalScore)) {
			finalScore = mvs.getFinalScore().replace(":", "");
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if ("01".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore01Bingo("1");
					}
					mvs.setScore01((String) entry.getValue());
				} else if ("02".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore02Bingo("1");
					}
					mvs.setScore02((String) entry.getValue());
				} else if ("12".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore12Bingo("1");
					}
					mvs.setScore12((String) entry.getValue());
				} else if ("03".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore03Bingo("1");
					}
					mvs.setScore03((String) entry.getValue());
				} else if ("13".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore13Bingo("1");
					}
					mvs.setScore13((String) entry.getValue());
				} else if ("23".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore23Bingo("1");
					}
					mvs.setScore23((String) entry.getValue());
				} else if ("04".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore04Bingo("1");
					}
					mvs.setScore04((String) entry.getValue());
				} else if ("14".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore14Bingo("1");
					}
					mvs.setScore14((String) entry.getValue());
				} else if ("24".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore24Bingo("1");
					}
					mvs.setScore24((String) entry.getValue());
				} else if ("05".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore05Bingo("1");
					}
					mvs.setScore05((String) entry.getValue());
				} else if ("15".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore15Bingo("1");
					}
					mvs.setScore15((String) entry.getValue());
				} else if ("25".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore25Bingo("1");
					}
					mvs.setScore25((String) entry.getValue());
				}
			}
		} else {
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if ("01".equals((String) entry.getKey())) {
					mvs.setScore01((String) entry.getValue());
				} else if ("02".equals((String) entry.getKey())) {
					mvs.setScore02((String) entry.getValue());
				} else if ("12".equals((String) entry.getKey())) {
					mvs.setScore12((String) entry.getValue());
				} else if ("03".equals((String) entry.getKey())) {
					mvs.setScore03((String) entry.getValue());
				} else if ("13".equals((String) entry.getKey())) {
					mvs.setScore13((String) entry.getValue());
				} else if ("23".equals((String) entry.getKey())) {
					mvs.setScore23((String) entry.getValue());
				} else if ("04".equals((String) entry.getKey())) {
					mvs.setScore04((String) entry.getValue());
				} else if ("14".equals((String) entry.getKey())) {
					mvs.setScore14((String) entry.getValue());
				} else if ("24".equals((String) entry.getKey())) {
					mvs.setScore24((String) entry.getValue());
				} else if ("05".equals((String) entry.getKey())) {
					mvs.setScore05((String) entry.getValue());
				} else if ("15".equals((String) entry.getKey())) {
					mvs.setScore15((String) entry.getValue());
				} else if ("25".equals((String) entry.getKey())) {
					mvs.setScore25((String) entry.getValue());
				}
			}
		}
	}

	/**
	 * 封装平其他
	 * 
	 * @param zjqList
	 * @param mvs
	 */
	@SuppressWarnings("rawtypes")
	private static void sealedBfListp(LinkedHashMap<String, String> zjqList, MVs mvs) {
		Iterator<Entry<String, String>> iter = zjqList.entrySet().iterator();
		String finalScore = mvs.getFinalScore();
		if (ObjectHelper.isNotEmpty(finalScore)) {
			finalScore = mvs.getFinalScore().replace(":", "");
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if ("00".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore00Bingo("1");
					}
					mvs.setScore00((String) entry.getValue());
				} else if ("11".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore11Bingo("1");
					}
					mvs.setScore11((String) entry.getValue());
				} else if ("22".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore22Bingo("1");
					}
					mvs.setScore22((String) entry.getValue());
				} else if ("33".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore33Bingo("1");
					}
					mvs.setScore33((String) entry.getValue());
				}
			}
		} else {
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if ("00".equals((String) entry.getKey())) {
					mvs.setScore00((String) entry.getValue());
				} else if ("11".equals((String) entry.getKey())) {
					mvs.setScore11((String) entry.getValue());
				} else if ("22".equals((String) entry.getKey())) {
					mvs.setScore22((String) entry.getValue());
				} else if ("33".equals((String) entry.getKey())) {
					mvs.setScore33((String) entry.getValue());
				}
			}
		}
	}

	/***
	 * 封装比分
	 * 
	 * @param bfList
	 * @param mvs
	 */
	@SuppressWarnings("rawtypes")
	private static void sealedBfLists(LinkedHashMap<String, String> bfList, MVs mvs) {

		Iterator<Entry<String, String>> iter = bfList.entrySet().iterator();
		String finalScore = mvs.getFinalScore();
		if (ObjectHelper.isNotEmpty(finalScore)) {
			finalScore = mvs.getFinalScore().replace(":", "");
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if ("10".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore10Bingo("1");
					}
					mvs.setScore10((String) entry.getValue());
				} else if ("20".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore20Bingo("1");
					}
					mvs.setScore20((String) entry.getValue());
				} else if ("21".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore21Bingo("1");
					}
					mvs.setScore21((String) entry.getValue());
				} else if ("30".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore30Bingo("1");
					}
					mvs.setScore30((String) entry.getValue());
				} else if ("31".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore31Bingo("1");
					}
					mvs.setScore31((String) entry.getValue());
				} else if ("32".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore32Bingo("1");
					}
					mvs.setScore32((String) entry.getValue());
				} else if ("40".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore40Bingo("1");
					}
					mvs.setScore40((String) entry.getValue());
				} else if ("41".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore41Bingo("1");
					}
					mvs.setScore41((String) entry.getValue());
				} else if ("42".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore42Bingo("1");
					}
					mvs.setScore42((String) entry.getValue());
				} else if ("50".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore50Bingo("1");
					}
					mvs.setScore50((String) entry.getValue());
				} else if ("51".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore51Bingo("1");
					}
					mvs.setScore51((String) entry.getValue());
				} else if ("52".equals((String) entry.getKey())) {
					if (((String) entry.getKey()).equals(finalScore)) {
						mvs.setScore52Bingo("1");
					}
					mvs.setScore52((String) entry.getValue());
				}
			}
		} else {
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if ("10".equals((String) entry.getKey())) {
					mvs.setScore10((String) entry.getValue());
				} else if ("20".equals((String) entry.getKey())) {
					mvs.setScore20((String) entry.getValue());
				} else if ("21".equals((String) entry.getKey())) {
					mvs.setScore21((String) entry.getValue());
				} else if ("30".equals((String) entry.getKey())) {
					mvs.setScore30((String) entry.getValue());
				} else if ("31".equals((String) entry.getKey())) {
					mvs.setScore31((String) entry.getValue());
				} else if ("32".equals((String) entry.getKey())) {
					mvs.setScore32((String) entry.getValue());
				} else if ("40".equals((String) entry.getKey())) {
					mvs.setScore40((String) entry.getValue());
				} else if ("41".equals((String) entry.getKey())) {
					mvs.setScore41((String) entry.getValue());
				} else if ("42".equals((String) entry.getKey())) {
					mvs.setScore42((String) entry.getValue());
				} else if ("50".equals((String) entry.getKey())) {
					mvs.setScore50((String) entry.getValue());
				} else if ("51".equals((String) entry.getKey())) {
					mvs.setScore51((String) entry.getValue());
				} else if ("52".equals((String) entry.getKey())) {
					mvs.setScore52((String) entry.getValue());
				}
			}
		}
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		get5Info();
		long endTime = System.currentTimeMillis();
		System.out.println("get5Info程序运行时间：" + (endTime - startTime) + "ms");
		System.out.println("get5Info程序运行时间：" + (endTime - startTime) /1000 /60 + "m");
	}
}
