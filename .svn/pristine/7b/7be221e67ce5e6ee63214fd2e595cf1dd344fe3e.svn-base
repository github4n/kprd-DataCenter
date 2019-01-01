package com.kprd.date.zq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.liansai.mapper.MVsMapper;
import com.kprd.liansai.pojo.MVs;
import com.kprd.liansai.pojo.MVsExample;
import com.sun.mail.handlers.text_html;

/**
 * 竞彩官网采集
 * @author Administrator
 *
 */
public class JingCaiOfficial {
	//混合过关
	private static String baseUrl = "http://info.sporttery.cn/interface/interface_mixed.php?action=fb_list&pke=0.004698456683856156&_=yy";
	//比分直播
	private static String yesterday = "http://i.sporttery.cn/api/match_live_2/get_match_list?callback=?&_=xx";
	
	static MVsMapper mvsMapper = Main.applicationContext.getBean(MVsMapper.class);
	/**
	 * 获取xhr数据
	 */
	public static String getJingCaiHtml() {
		baseUrl = baseUrl.replace("yy", String.valueOf(System.currentTimeMillis()));
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(baseUrl);
//			httpget.setHeader("Accept","text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
//			httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//			httpget.setHeader("Accept-Encoding", "gzip, deflate, sdch");
//			httpget.setHeader("Cache-Control", "no-cache");
//			httpget.setHeader("Connection", "keep-alive");
//			httpget.setHeader("Cookie","BIGipServerPool_apache_web=1291911434.20480.0000; PHPSESSID=0ffcb47pfmmt4nui1i73hfv3s5; Hm_lvt_860f3361e3ed9c994816101d37900758=1498095354,1498181637,1498441369; Hm_lpvt_860f3361e3ed9c994816101d37900758=1498546939");
//			httpget.setHeader("Host","info.sporttery.cn");
//			httpget.setHeader("Referer","http://info.sporttery.cn/mixed/mixed.htm?gameIndex=0");
//            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
//            httpget.setHeader("X-Requested-With","XMLHttpRequest");
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gbk");
                	if(ObjectHelper.isNotEmpty(html)) {
                		html = html.substring(html.indexOf("data")+5, html.length());
                		html = html.split(";")[0];
                		return html;
                	}
                }	
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	/**
	 * 获取比分html
	 * @return
	 */
	public static String getBifenHtml() {
		yesterday = yesterday.replace("xx", String.valueOf(System.currentTimeMillis()));
		String html = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(yesterday);
			httpget.setHeader("Accept","text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
			httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpget.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpget.setHeader("Cache-Control", "no-cache");
			httpget.setHeader("Connection", "keep-alive");
			httpget.setHeader("Cookie","BIGipServerPool_apache_web=1291911434.20480.0000; PHPSESSID=0ffcb47pfmmt4nui1i73hfv3s5; Hm_lvt_860f3361e3ed9c994816101d37900758=1498095354,1498181637,1498441369; Hm_lpvt_860f3361e3ed9c994816101d37900758=1498546939");
			httpget.setHeader("Host","i.sporttery.cn");
			httpget.setHeader("Referer","http://info.sporttery.cn/livescore/fb_livescore.html");
            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		html = html.split("=")[1];
                		html = html.substring(0, html.length()-1);
                		System.out.println(html);
                		html = UtilBag.unicode2String(html);
                		html = html.substring(1,html.length());
                		html = html.replace("\\/", "\\");
                		return html;
                	}
                }	
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	/**
	 * 获取竞彩数据
	 */
	@SuppressWarnings("rawtypes")
	public static void getJingCaiData() {
		//混合过关
		String html = getJingCaiHtml();
		//比分页面
//		String bifenHtml = getBifenHtml();
		if(!StringUtils.isEmpty(html)) {
			try {
				System.out.println(html);
				List<List> fatherList = JsonUtils.jsonToList(html, List.class);
				for(List list : fatherList) {
					MVs mvs = new MVs();
					for(int i=0;i<list.size();i++) {
						List sonList = List.class.cast(list.get(i));
						if(ObjectHelper.isNotEmpty(sonList)) {
							switch (i) {
							case 0: 
								System.out.println(sonList);
								String code = (String) sonList.get(0);
								//拿到编号的前提下再执行下面的
								if(!StringUtils.isEmpty(code) && code.indexOf("周") > -1) {
									String week = code.substring(code.indexOf("周")+1,code.indexOf("周")+2);
									String num = UtilBag.getWeekDay(week);
									if(!StringUtils.isEmpty(num)) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
										String nyr = sdf.format(new Date());
										code = nyr + num + code.substring(2);
										String time = (String) sonList.get(3);
										SimpleDateFormat sdff = new SimpleDateFormat("yyyy");
										time = sdff.format(new Date()).substring(0,2) + time + ":00";
										SimpleDateFormat ttt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
										Date startTime = ttt.parse(time);
										Date endTime = getEndTime(startTime);
										
										
										
										String team = (String) sonList.get(2);
										if(!StringUtils.isEmpty(team)) {
											String[] teams = team.split("\\$");
											if(teams.length > 1) {
												mvs.setHomeFullname(teams[0]);
												mvs.setAwayFullname(teams[2]);
												mvs.setRqs(teams[1]);
											}
										}
										
										mvs.setCcId(code);
										mvs.setGameLeagueName(String.valueOf(sonList.get(1)));
										mvs.setGameLeagueColor(String.valueOf(sonList.get(5)));
										mvs.setStartTime(startTime);
//										mvs.setJingcaiid(String.valueOf(sonList.get(4)));
//										mvs.setHomerank(String.valueOf(sonList.get(9)));
//										mvs.setAwayrank(String.valueOf(sonList.get(10)));
										mvs.setEndTime(endTime);
										mvs.setCountdownTime(endTime);
									}
								}
								break;
							case 1://让球胜平负
								System.out.println(sonList);
								if(ObjectHelper.isNotEmpty(sonList)) {
									mvs.setRqSpfS(sonList.get(0).toString());
									mvs.setRqSpfP(sonList.get(1).toString());
									mvs.setRqSpfF(sonList.get(2).toString());
								}
								break;
							case 2://比分
								System.out.println(sonList);
								if(ObjectHelper.isNotEmpty(sonList)) {
									mvs.setScore10(sonList.get(0).toString());
									mvs.setScore20(sonList.get(1).toString());
									mvs.setScore21(sonList.get(2).toString());
									mvs.setScore30(sonList.get(3).toString());
									mvs.setScore31(sonList.get(4).toString());
									mvs.setScore32(sonList.get(5).toString());
									mvs.setScore40(sonList.get(6).toString());
									mvs.setScore41(sonList.get(7).toString());
									mvs.setScore42(sonList.get(8).toString());
									mvs.setScore50(sonList.get(9).toString());
									mvs.setScore51(sonList.get(10).toString());
									mvs.setScore52(sonList.get(11).toString());
									mvs.setScoreWinOther(sonList.get(12).toString());
									mvs.setScore00(sonList.get(13).toString());
									mvs.setScore11(sonList.get(14).toString());
									mvs.setScore22(sonList.get(15).toString());
									mvs.setScore33(sonList.get(16).toString());
									mvs.setScoreDrawOther(sonList.get(17).toString());
									mvs.setScore01(sonList.get(18).toString());
									mvs.setScore02(sonList.get(19).toString());
									mvs.setScore12(sonList.get(20).toString());
									mvs.setScore03(sonList.get(21).toString());
									mvs.setScore13(sonList.get(22).toString());
									mvs.setScore23(sonList.get(23).toString());
									mvs.setScore04(sonList.get(24).toString());
									mvs.setScore14(sonList.get(25).toString());
									mvs.setScore24(sonList.get(26).toString());
									mvs.setScore05(sonList.get(27).toString());
									mvs.setScore15(sonList.get(28).toString());
									mvs.setScore25(sonList.get(29).toString());
									mvs.setScoreLoseOther(sonList.get(30).toString());
								}
								break;
							case 3: //总进球
								System.out.println(sonList);
								if(ObjectHelper.isNotEmpty(sonList)) {
									mvs.setZjq0(sonList.get(0).toString());
									mvs.setZjq1(sonList.get(1).toString());
									mvs.setZjq2(sonList.get(2).toString());
									mvs.setZjq3(sonList.get(3).toString());
									mvs.setZjq4(sonList.get(4).toString());
									mvs.setZjq5(sonList.get(5).toString());
									mvs.setZjq6(sonList.get(6).toString());
									mvs.setZjq7(sonList.get(7).toString());
								}
								break;
							case 4://全半场
								System.out.println(sonList);
								if(ObjectHelper.isNotEmpty(sonList)) {
									mvs.setQbcSs(sonList.get(0).toString());
									mvs.setQbcSp(sonList.get(1).toString());
									mvs.setQbcSf(sonList.get(2).toString());
									mvs.setQbcPs(sonList.get(3).toString());
									mvs.setQbcPp(sonList.get(4).toString());
									mvs.setQbcPf(sonList.get(5).toString());
									mvs.setQbcFs(sonList.get(6).toString());
									mvs.setQbcFp(sonList.get(7).toString());
									mvs.setQbcFf(sonList.get(8).toString());
								}
								break;
							case 5: //胜平负
								System.out.println(sonList);
								mvs.setSpfS(sonList.get(0).toString());
								mvs.setSpfP(sonList.get(1).toString());
								mvs.setSpfF(sonList.get(2).toString());
								
							default:
								break;
							}
						}
					}
					//页面数据封装完成，开始处理特殊字段
					if(ObjectHelper.isNotEmpty(mvs.getHomeFullname())) {
						MVsExample mvsEx = new MVsExample();
						mvsEx.createCriteria().andCcIdEqualTo(mvs.getCcId());
						List<MVs> mvsList = mvsMapper.selectByExample(mvsEx);
						if(ObjectHelper.isNotEmpty(mvsList)) {
							//判断主队客队名字
							if(ObjectHelper.isNotEmpty(mvsList.get(0).getHomeFullname()) && ObjectHelper.isNotEmpty(mvsList.get(0).getAwayFullname())) {
								if(!mvsList.get(0).getHomeFullname().equals(mvs.getHomeFullname())) {
									mvs.setHomeFullname(mvsList.get(0).getHomeFullname());
								}
								if(!mvsList.get(0).getAwayFullname().equals(mvs.getAwayFullname())) {
									mvs.setAwayFullname(mvsList.get(0).getAwayFullname());
								}
							}
							//判断联赛名称
							if(ObjectHelper.isNotEmpty(mvsList.get(0).getGameLeagueName())) {
								if(!mvsList.get(0).getGameLeagueName().equals(mvs.getGameLeagueName())) {
									mvs.setGameLeagueName(mvsList.get(0).getGameLeagueName());
								}
							}
							mvs.setZid(mvsList.get(0).getZid());
							mvs.setXiid(mvsList.get(0).getXiid());
							mvs.setIdVs(mvsList.get(0).getIdVs());
							mvsMapper.updateByPrimaryKeySelective(mvs);
						} else {
							mvs.setIdVs(IDUtils.createUUId());
							mvsMapper.insert(mvs);
						}
					}
					System.out.println(mvs);
				}
				System.out.println("今日抓取一轮结束");
			} catch (Exception e) {
				e.printStackTrace();
				if(e.getMessage().indexOf("ConnectException") > -1) {
					try {
						System.out.println("链接超时，等待5分钟");
						Thread.sleep(1000*60*5);
					} catch (Exception e2) {
						getJingCaiData();
					}
				}
			}
		}
	}
	
	/**
	 * 获取截止时间
	 * @param startTime
	 * @return
	 */
	public static Date getEndTime(Date startTime) {
		Date endDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String week = UtilBag.getWeekOfDate(startTime);
			if("星期一".equals(week) || "星期日".equals(week)) {
				System.out.println("周1和周7");
				String st = sdf.format(startTime);
				if(!StringUtils.isEmpty(st)) {
					String hm = st.split(" ")[1];
					String hour = hm.split(":")[0];
					int hr = Integer.valueOf(hour);
					if(9<=hr || 1>=hr) {
						Date afterDate = new Date(startTime .getTime() - 300000);
						System.out.println(sdf.format(afterDate));
						endDate = afterDate;
					} else {
						String dateStr = st.split(" ")[0] + " 01:00";
						Date afterDate = sdf.parse(dateStr);
						afterDate = new Date(afterDate.getTime() - 300000);
//						System.out.println(sdf.format(afterDate));
						endDate = afterDate;
					}
				}
			} else {
				System.out.println("周2至周6");
				String st = sdf.format(startTime);
				if(!StringUtils.isEmpty(st)) {
					String hm = st.split(" ")[1];
					String hour = hm.split(":")[0];
					int hr = Integer.valueOf(hour);
					if(9<=hr) {
						Date afterDate = new Date(startTime.getTime() - 300000);
//						System.out.println(sdf.format(afterDate));
						endDate = afterDate;
					} else {
						String dateStr = st.split(" ")[0] + " 00:00";
						Date afterDate = sdf.parse(dateStr);
						afterDate = new Date(afterDate.getTime() - 300000);
//						System.out.println(sdf.format(afterDate));
						endDate = afterDate;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return endDate;
	}
	
	public static void test() {
		try {
			String a = "2017-06-18 02:45";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = sdf.parse(a);
			getEndTime(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws ParseException {
//		test();
		while(true) {
			try {
				System.out.println("一圈睡5分钟");
//				Thread.sleep(1000*60*1);
				getJingCaiData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
