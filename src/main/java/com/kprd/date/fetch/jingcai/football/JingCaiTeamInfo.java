//package com.kprd.date.fetch.jingcai.football;
//
//import java.util.Iterator;
//import java.util.List;
//
//import net.sf.json.JSONObject;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//
//import com.kprd.common.utils.IDUtils;
//import com.kprd.common.utils.ObjectHelper;
//import com.kprd.date.lq.update.Main;
//import com.kprd.date.util.UtilBag;
//import com.kprd.newliansai.mapper.FoMixMapper;
//import com.kprd.newliansai.mapper.FoTeamMapper;
//import com.kprd.newliansai.pojo.FoMix;
//import com.kprd.newliansai.pojo.FoMixExample;
//import com.kprd.newliansai.pojo.FoTeam;
//import com.kprd.newliansai.pojo.FoTeamExample;
//
///**
// * 竞彩网队伍信息
// * @author Administrator
// *
// * 由于考虑到在每天对阵中抓取可能会影响效率，先暂时统一抓取
// */
//public class JingCaiTeamInfo {
//	//队伍详情地址
//		private static String teamDetailUrl = "http://i.sporttery.cn/api/fb_match_info/get_team_data/?tid=xx&_=yy";
//	//对阵mapper
//	static FoMixMapper mvsMapper = Main.applicationContext.getBean(FoMixMapper.class);
//	
//	//队伍mapper
//	static FoTeamMapper teamMapper = Main.applicationContext.getBean(FoTeamMapper.class);
//	
//	/**
//	 * 获取队伍详细信息(不包含球员)
//	 */
//	public static void getTeamInfo() {
//		try {
//			FoMixExample mixEx = new FoMixExample();
//			mixEx.createCriteria().andIduniqueIsNotNull();
//			mixEx.setOrderByClause(" idUnique desc");
//			List<FoMix> mixList = mvsMapper.selectByExample(mixEx);
//			if(ObjectHelper.isNotEmpty(mixList)) {
//				for(FoMix mix : mixList) {
//					teamDetailUrl = teamDetailUrl.replace("xx", mix.getHometeamid()).replace("yy", String.valueOf(System.currentTimeMillis()));
//					CloseableHttpClient httpclient = HttpClients.createDefault();
//					HttpGet httpget = new HttpGet(teamDetailUrl);
//					CloseableHttpResponse response = httpclient.execute(httpget);
//					String html = "";
//					try {
//						HttpEntity entity = response.getEntity();  
//		                if (entity != null && response.getStatusLine().getStatusCode() == 200) {  
//		                	html = UtilBag.handleEntityCharset(entity, "gbk");
//		                	if(ObjectHelper.isNotEmpty(html)) {
//		                		JSONObject jsonObject = JSONObject.fromObject(html);
//		                		Object result = jsonObject.get("result");
//		                		System.out.println(result.toString());//测试通过前留着
//		                		jsonObject = JSONObject.fromObject(result);
//		                		FoTeam teamInfo = new FoTeam();
//		                		teamInfo.setIdteam(jsonObject.get("team_id").toString());
//		                		teamInfo.setFullnameteam(jsonObject.get("club_name").toString());
//		                		teamInfo.setShortnameteam(jsonObject.get("club_abbr_name").toString());
//		                		teamInfo.setCity(jsonObject.get("team_city").toString());
//		                		teamInfo.setType(jsonObject.get("type").toString());
//		                		teamInfo.setEsttimeteam(jsonObject.get("team_founded").toString());
//		                		teamInfo.setLogoteamurl(jsonObject.get("team_pic").toString());
//		                		if(teamInfo.getIdteam().equals("35583")) {
//		                			System.out.println(teamInfo.getIdteam());
//		                		}
//		                		
//		                		if(ObjectHelper.isNotEmpty(jsonObject.get("venue"))) {
//		                			if(ObjectHelper.isNotEmpty(JSONObject.fromObject(jsonObject.get("venue")))) {
//			                			@SuppressWarnings("rawtypes")
//										Iterator it = JSONObject.fromObject(jsonObject.get("venue")).keys();
//			                			String key = "";
//			                			while(it.hasNext()) {
//			                				key = String.valueOf(it.next());
//			                			}
//			                			Object venue = JSONObject.fromObject(jsonObject.get("venue")).get(key);
//			                			teamInfo.setCourtname(JSONObject.fromObject(venue).get("venue_name").toString());
//			                			teamInfo.setCapacity(JSONObject.fromObject(venue).get("capacity").toString());
//			                		}
//		                		}
//		                		
//		                		FoTeamExample teamEx = new FoTeamExample();
//		                		teamEx.createCriteria().andIdteamEqualTo(jsonObject.get("team_id").toString());
//		                		List<FoTeam> teamList = teamMapper.selectByExample(teamEx);
//		                		if(ObjectHelper.isNotEmpty(teamList) && teamList.size() == 1) {
//		                			teamInfo.setId(teamList.get(0).getId());
//		                			teamMapper.updateByPrimaryKey(teamInfo);
//		                		} else {
//		                			teamInfo.setId(IDUtils.createUUId());
//		                			teamMapper.insert(teamInfo);
//		                		}
//		                	}
//		                }	
//					} finally {
//						response.close();
//					}
//					teamDetailUrl = "http://i.sporttery.cn/api/fb_match_info/get_team_data/?tid=xx&_=yy";
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void main(String[] args) {
//		
//	}
//}
