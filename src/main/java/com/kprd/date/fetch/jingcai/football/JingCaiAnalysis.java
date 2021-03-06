package com.kprd.date.fetch.jingcai.football;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.FoFutureMapper;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.mapper.FoRecentMapper;
import com.kprd.newliansai.mapper.FoVshistoryMapper;
import com.kprd.newliansai.pojo.FoFuture;
import com.kprd.newliansai.pojo.FoFutureExample;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;
import com.kprd.newliansai.pojo.FoRecent;
import com.kprd.newliansai.pojo.FoRecentExample;
import com.kprd.newliansai.pojo.FoVshistory;
import com.kprd.newliansai.pojo.FoVshistoryExample;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JingCaiAnalysis {
	//主队历史对阵
	private static String vsUrl = "http://i.sporttery.cn/api/fb_match_info/get_team_rec_data?tid=teamId&md=dateStr&is_ha=all&limit=page&c_id=0&ptype[]=three_-1&ptype[]=asia_229_e";
	//未来数据
	private static String futureUrl = "http://i.sporttery.cn/api/fb_match_info/get_future_matches?tid=teamId&md=dateStr&limit=4&_=stimestamp";
	//交锋历史
	private static String vsHis = "http://i.sporttery.cn/api/fb_match_info/get_result_his?limit=10&is_ha=all&limit=10&c_id=0&mid=teamId&ptype[]=three_-1&ptype[]=asia_229&&_=stimestamp";
	//对阵mapper
	static FoMixMapper mvsMapper = Main.applicationContext.getBean(FoMixMapper.class);
	//近期赛事mapper
	static FoRecentMapper recentMapper = Main.applicationContext.getBean(FoRecentMapper.class);
	//未来赛事mapper
	static FoFutureMapper futureMapper = Main.applicationContext.getBean(FoFutureMapper.class);
	//交战历史mapper
	static FoVshistoryMapper vsHistoryMapper = Main.applicationContext.getBean(FoVshistoryMapper.class);
	
	/**
	 * 获取未来赛事
	 * @param teamId
	 * @param idUnique
	 * @param ha
	 */
	public static void getFuture(String teamId,String idUnique,String ha,String stade) {
		futureUrl = futureUrl.replace("teamId", teamId);
		if(ObjectHelper.isEmpty(stade)) {
			futureUrl = futureUrl.replace("dateStr", UtilBag.dateUtil(1));
		} else {
			futureUrl = futureUrl.replace("dateStr", stade);
		}
		futureUrl = futureUrl.replace("stimestamp", String.valueOf(System.currentTimeMillis()));
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(futureUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			futureUrl = "http://i.sporttery.cn/api/fb_match_info/get_future_matches?tid=teamId&md=dateStr&limit=4&_=stimestamp";
			try {
				String html = "";
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		html = UtilBag.unicode2String(html);
                		JSONObject jsonObject = JSONObject.fromObject(html);
                		String code = JSONObject.fromObject(jsonObject.get("status")).get("code").toString();
                		if(code.equals("20002")) {
                			return;
                		}
                		JSONArray jArray = jsonObject.getJSONArray("result");
                		for(int i=0;i<jArray.size();i++) {
                			FoFuture future = new FoFuture();
                			JSONObject ja = JSONObject.fromObject(jArray.get(i));
                			future.setIdjingcai(ja.get("match_id").toString());
                			String date_cn = ja.get("date_cn").toString();
                			String time_cn = ja.get("time_cn").toString();
                			future.setGametime(date_cn + " " + time_cn);
                			//联赛
                			future.setLeagueid(ja.get("l_id_dc").toString());
                			future.setLeaguecolor(ja.get("l_color").toString());
                			future.setLeaguename(ja.get("l_cn_abbr").toString());
                			//队伍
                			future.setHometeamfull(ja.get("h_cn").toString());
                			future.setHometeamshort(ja.get("h_cn_abbr").toString());
                			future.setHometeamid(ja.get("h_id_dc").toString());
                			
                			future.setAwayteamfull(ja.get("a_cn").toString());
                			future.setAwayteamshort(ja.get("a_cn_abbr").toString());
                			future.setAwayteamid(ja.get("a_id_dc").toString());
                			future.setSp1(ja.getString("match_id").toString());
                			//主队还是客队
                			future.setHomeoraway(ha);
                			future.setIdunique(idUnique);
                			FoFutureExample fuEx = new FoFutureExample();
                			fuEx.createCriteria().andGametimeEqualTo(date_cn + " " + time_cn).andHometeamfullEqualTo(future.getHometeamfull()).andAwayteamfullEqualTo(future.getAwayteamfull()).
                			andIduniqueEqualTo(idUnique);
                			List<FoFuture> fuList = futureMapper.selectByExample(fuEx);
                			if(ObjectHelper.isEmpty(fuList)) {
                				future.setId(IDUtils.createUUId());
                				futureMapper.insert(future);
                			} else {
                				future.setId(fuList.get(0).getId());
                				futureMapper.updateByPrimaryKeySelective(future);
                			}
                		}
                	}
                }
                
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					getFuture(teamId, idUnique, ha, stade);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取两队历史对阵
	 * @param teamId
	 * @param idUnique
	 * @param ha
	 */
	public static void getvsHistory(String teamId,String idUnique) {
		vsHis = vsHis.replace("teamId", teamId);
		vsHis = vsHis.replace("stimestamp", String.valueOf(System.currentTimeMillis()));
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(vsHis);
			CloseableHttpResponse response = httpclient.execute(httpget);
			vsHis = "http://i.sporttery.cn/api/fb_match_info/get_result_his?limit=10&is_ha=all&limit=10&c_id=0&mid=teamId&ptype[]=three_-1&ptype[]=asia_229&&_=stimestamp";
			try {
				String html = "";
				// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		html = UtilBag.unicode2String(html);
                		JSONObject jsonObject = JSONObject.fromObject(html);
                		JSONObject result = JSONObject.fromObject(jsonObject.get("result"));
                		JSONArray data = result.getJSONArray("data");
                		if(ObjectHelper.isNotEmpty(data)) {
                			for(int i=0;i<data.size();i++) {
                				JSONObject dataJson = JSONObject.fromObject(data.get(i));
                				if(ObjectHelper.isNotEmpty(dataJson) && !dataJson.isEmpty()) {
                					FoVshistory vs = new FoVshistory();
                					vs.setGameidjingcai(dataJson.getString("match_id"));
                					vs.setPidjingcai(dataJson.getString("sporttery_matchid"));
                					vs.setLeagueid(dataJson.getString("l_id_dc"));
                					vs.setLeaguename(dataJson.getString("l_cn_abbr"));
                					vs.setLeaguecolor(dataJson.getString("l_color"));
                					String date_cn = dataJson.getString("date_cn");
//                					String time_cn = dataJson.getString("time_cn");由于500没有具体时分秒，不入库
                					vs.setGametime(date_cn);
                					vs.setHometeamid(dataJson.getString("h_id_dc"));
                					vs.setHometeamshort(dataJson.getString("h_cn_abbr"));
                					vs.setHometeamfull(dataJson.getString("h_cn"));
                					String half = dataJson.getString("half");
                					String finalScore = dataJson.getString("final");
                					
                					if(ObjectHelper.isNotEmpty(half)) {
                						vs.setHomehalf(half.split(":")[0]);
                						vs.setAwayhalf(half.split(":")[1]);
                					}
                					
                					if(ObjectHelper.isNotEmpty(finalScore)) {
                						vs.setHomescore(finalScore.split(":")[0]);
                						vs.setAwayscore(finalScore.split(":")[1]);
                					}
                					
                					vs.setAwayteamid(dataJson.getString("a_id_dc"));
                					vs.setAwayteamshort(dataJson.getString("a_cn_abbr"));
                					vs.setAwayteamfull(dataJson.getString("h_cn"));
                					
                					String mac_data = dataJson.getString("mac_data");
                					if(ObjectHelper.isNotEmpty(mac_data)) {
                						if("w".equals(mac_data)) {
                							vs.setPanlu("赢");
                						} else if("l".equals(mac_data)) {
                							vs.setPanlu("输");
                						} else {
                							vs.setPanlu("走");
                						}
                					}
                					vs.setIdunique(idUnique);
                					FoVshistoryExample vsEx = new FoVshistoryExample();
                					vsEx.createCriteria().andGametimeEqualTo(vs.getGametime()).andIduniqueEqualTo(idUnique);
                					List<FoVshistory> vsList  = vsHistoryMapper.selectByExample(vsEx);
                					if(ObjectHelper.isEmpty(vsList)) {
                						vs.setId(IDUtils.createUUId());
                						vsHistoryMapper.insert(vs);
                					} else {
                						vs.setId(vsList.get(0).getId());
                						vsHistoryMapper.updateByPrimaryKeySelective(vs);
                					}
                				}
                			}
                		}
                	}
                }
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					getvsHistory(teamId, idUnique);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取近期对阵数据
	 * @param teamId 队伍id
	 */
	public static void getRecent(String teamId,String idUnique,String ha,String sdate) {
//		if("1".equals(ha)) {
//			System.out.println("主" + idUnique);
//		} else { 
//			System.out.println("客" + idUnique);
//		}
		vsUrl = vsUrl.replace("teamId", teamId);
		if(ObjectHelper.isEmpty(sdate)) {
			vsUrl = vsUrl.replace("dateStr",  UtilBag.dateUtil(1));
		} else {
			vsUrl = vsUrl.replace("dateStr",  sdate);
		}
		vsUrl = vsUrl.replace("page", "50");//竞彩网应该是可以写无限大，它只会查出库中最多数据
//		vsUrl = vsUrl.replace("stimestamp", String.valueOf(System.currentTimeMillis()));
//		String json = UtilBag.urlFetch(homeVsUrl, "gb2312");
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(vsUrl);
			System.out.println(vsUrl);
			CloseableHttpResponse response = httpclient.execute(httpget);
			vsUrl = "http://i.sporttery.cn/api/fb_match_info/get_team_rec_data?tid=teamId&md=dateStr&is_ha=all&limit=page&c_id=0&ptype[]=three_-1&ptype[]=asia_229_e";
			try {
				String html = "";
            	// 获取响应实体    
                HttpEntity entity = response.getEntity();  
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
                	html = UtilBag.handleEntityCharset(entity, "gb2312");
                	if(ObjectHelper.isNotEmpty(html)) {
                		html = UtilBag.unicode2String(html);
                		JSONObject jsonObject = JSONObject.fromObject(html);
                		JSONObject result = JSONObject.fromObject(jsonObject.get("result"));
                		if(result.isEmpty()) {
                			System.out.println(idUnique +"没有请求到数据");
                			return;
                		}
                		JSONArray data = result.getJSONArray("data");
                		if(ObjectHelper.isNotEmpty(data)) {
                			for(int i=0;i<data.size();i++) {//下表从1开始，排除本本场比赛，因为灭有数据
                				JSONObject history = JSONObject.fromObject(data.get(i));
                				FoRecent vs = new FoRecent();
                				vs.setGametime(history.getString("date_cn")/* + " " + history.getString("time_cn")*/);
                				//比赛id，一个特殊的id
                				vs.setIdjingcai(history.getString("match_id"));
                				//联赛id
                				vs.setLeagueid(history.getString("l_id_dc"));
                				//主队id
                				vs.setHometeamid(history.getString("h_id_dc"));
                				//客队id
                				vs.setAwayteamid(history.getString("a_id_dc"));
                				//联赛名（简称）
                				vs.setLeaguename(history.getString("l_cn_abbr"));
                				//联赛颜色
                				vs.setLeaguecolor(history.getString("l_color"));
                				//主队名称
                				vs.setHometeamfull(history.getString("h_cn"));
                				//客队名称
                				vs.setHometeamshort(history.getString("h_cn_abbr"));
                				vs.setAwayteamfull(history.getString("a_cn"));
                				vs.setAwayteamshort(history.getString("a_cn_abbr"));
                				vs.setHomeoraway(ha);
                				//半场得分
                				String half = history.getString("half");
                				if(ObjectHelper.isNotEmpty(half)) {
                					vs.setHomehalf(half.split(":")[0]);
                					vs.setAwayhalf(half.split(":")[1]);
                				}
                				//最终得分
                				String finalScore = history.getString("final");
                				if(ObjectHelper.isNotEmpty(finalScore)) {
                					vs.setHomescore(finalScore.split(":")[0]);
                					vs.setAwayscore(finalScore.split(":")[1]);
                				}
                				//盘路
                				vs.setPanlu(history.getString("mac_data"));
                				vs.setIdunique(idUnique);
                				FoRecentExample vsEx = new FoRecentExample();
                				vsEx.createCriteria().andGametimeEqualTo(vs.getGametime()).andHometeamfullEqualTo(vs.getHometeamfull())
                				.andAwayteamfullEqualTo(vs.getAwayteamfull()).andIduniqueEqualTo(idUnique);
                				List<FoRecent> vsList = recentMapper.selectByExample(vsEx);
                				if(ObjectHelper.isEmpty(vsList)) {
                					vs.setIdrecent(IDUtils.createUUId());
                					recentMapper.insert(vs);
                				} else {
                					vs.setIdrecent(vsList.get(0).getIdrecent());
                					recentMapper.updateByPrimaryKeySelective(vs);
                				}
                			}
                		}
                	}
                }	
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("Truncated chunk") > -1 || e.getMessage().indexOf("connect") > -1 || e.getMessage().indexOf("odds.500.com") > -1 
					|| e.getMessage().indexOf("String input must not be null") > -1 || e.getMessage().indexOf("Could not get a resource from the pool") > -1 
					|| e.getMessage().indexOf("Read timed out") > -1) {
				try {
					Thread.sleep(1000*30);
					getRecent(teamId, idUnique, ha, sdate);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		while(true) {
			try {
				FoMixExample fmEx = new FoMixExample();
				String day = UtilBag.dateUtil(0);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String today = day + " 11:59:59";
				Date kyo = sdf.parse(today);
				fmEx.createCriteria().andStarttimeGreaterThan(kyo);
				fmEx.setOrderByClause(" idUnique asc");
				List<FoMix> mixList = mvsMapper.selectByExample(fmEx);
				if(ObjectHelper.isNotEmpty(mixList)) {
					for(int i=0;i<mixList.size();i++) {
						String homeId = mixList.get(i).getHometeamid();
						String awayId = mixList.get(i).getAwayteamid();
						String idUnique = mixList.get(i).getIdunique();
						
						
						//近期
						getRecent(homeId,idUnique,"0",null);
						getRecent(awayId,idUnique,"1",null);
//						//未来
						getFuture(homeId,idUnique,"0",null);
						getFuture(awayId,idUnique,"1",null);
						
						getvsHistory(mixList.get(i).getJingcaiid(), mixList.get(i).getIdunique());
						
						System.out.println("睡5秒");
						Thread.sleep(1000*5);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(9999999);
		}
		
	}
}
