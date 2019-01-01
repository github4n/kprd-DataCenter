package com.kprd.date.fetch.jingcai.football.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.liansai.mapper.MVsMapper;
import com.kprd.liansai.pojo.MVs;
import com.kprd.liansai.pojo.MVsExample;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.mapper.FoRelationLeagueMapper;
import com.kprd.newliansai.mapper.FoRelationTeamMapper;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;
import com.kprd.newliansai.pojo.FoRelationLeague;
import com.kprd.newliansai.pojo.FoRelationLeagueExample;
import com.kprd.newliansai.pojo.FoRelationTeam;
import com.kprd.newliansai.pojo.FoRelationTeamExample;

public class HandleDifferences {
	
	//新对阵mapper
	static FoMixMapper newMapper = Main.applicationContext.getBean(FoMixMapper.class);
	//老对阵
	static MVsMapper oldMapper = Main.applicationContext.getBean(MVsMapper.class);
	//联赛关系表mapper
	static FoRelationLeagueMapper reMapper = Main.applicationContext.getBean(FoRelationLeagueMapper.class);
	//队伍关系表mapper
	static FoRelationTeamMapper rtMapper = Main.applicationContext.getBean(FoRelationTeamMapper.class);
	
	public static void showTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	handleLeaguesAndTeams();
            }
        };

        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, 17, 00, 00);
        Date date = calendar.getTime();
        Timer timer = new Timer();
        System.out.println(date);
        //每天的date时刻执行task，每隔1小时重复执行
        long delay = 1000*60*60;
        timer.schedule(task, date,delay);
        //每天的date时刻执行task, 仅执行一次
        //timer.schedule(task, date);
    }
	
	/**
	 * 处理联赛、队伍信息
	 */
	public static void handleLeaguesAndTeams() {
		try {
			System.out.println("开始处理联赛、队伍信息");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String newDate = sdf.format(new Date()) + " 11:59:59";
			Date startDate = fff.parse(newDate);
			//获取新当日对阵信息
			FoMixExample newEx = new FoMixExample();
			newEx.createCriteria().andStarttimeGreaterThan(startDate);//.andIsspfopenNotEqualTo("2").andIsrqspfopenNotEqualTo("2");
			newEx.setOrderByClause(" idUnique asc");
			List<FoMix> newList = newMapper.selectByExample(newEx);
			//获取原500对阵信息
			MVsExample oldEx = new MVsExample();
			oldEx.createCriteria().andStartTimeGreaterThan(startDate);
			oldEx.setOrderByClause(" cc_id asc;");
			List<MVs> oldList = oldMapper.selectByExample(oldEx);
			if(ObjectHelper.isNotEmpty(newList) && ObjectHelper.isNotEmpty(oldList)/* && newList.size() == oldList.size()*/) {//考虑到7月13号的情况（500更新不出临时添加比赛，导致2个网站数据个数不同，所以取消最后一个判断）
				System.out.println("竞彩今日比赛场次" + newList.size());
				System.out.println("老抓取程序500今日比赛场次" + oldList.size());
				//以数量小的为准
				int size = newList.size() < oldList.size() ? newList.size() : oldList.size();
				for(int i=0;i<size;i++) {
					if(newList.get(i).getIdunique().equals(oldList.get(i).getCcId())) {
						
						//联赛
						FoRelationLeagueExample reEx = new FoRelationLeagueExample();
						reEx.createCriteria().andJcleagueidEqualTo(newList.get(i).getLeagueid());
						List<FoRelationLeague> relationList = reMapper.selectByExample(reEx);
						if(ObjectHelper.isEmpty(relationList)) {
							FoRelationLeague relation = new FoRelationLeague();
							relation.setJcleaguecolor(newList.get(i).getLeaguecolor());
							relation.setJcleagueid(newList.get(i).getLeagueid());
							relation.setJcleaguename(newList.get(i).getLeaguename());
							relation.setWubaijcleaguecolor(oldList.get(i).getGameLeagueColor());
							relation.setWubaileagueid(oldList.get(i).getGameTypeId());
							relation.setWubaileaguename(oldList.get(i).getGameLeagueName());
							relation.setId(IDUtils.createUUId());
							reMapper.insert(relation);
						}
						
						//队伍(主队)
						FoRelationTeamExample rtEx = new FoRelationTeamExample();
						rtEx.createCriteria().andJcteamidEqualTo(newList.get(i).getHometeamid());
						List<FoRelationTeam> rtList = rtMapper.selectByExample(rtEx);
						if(ObjectHelper.isEmpty(rtList)) {
							if(newList.get(i).getHometeamid().equals("2157")) {
								System.out.println(123);
							}
							FoRelationTeam rteam = new FoRelationTeam();
							rteam.setJcteamshortname(newList.get(i).getHomeshortname());
							rteam.setJcteamfullname(newList.get(i).getHomefullname());
							rteam.setJcteamid(newList.get(i).getHometeamid());
							rteam.setWubaiteamshortname(oldList.get(i).getHomeShortname());
							rteam.setWubaiteamfullname(oldList.get(i).getHomeFullname());
							rteam.setWubaiteamid(oldList.get(i).getHomeUrlId());
							rteam.setId(IDUtils.createUUId());
							rtMapper.insert(rteam);
						}
						//队伍(客队)
						FoRelationTeamExample awayEx = new FoRelationTeamExample();
						awayEx.createCriteria().andJcteamidEqualTo(newList.get(i).getAwayteamid());
						List<FoRelationTeam> awayList = rtMapper.selectByExample(awayEx);
						if(ObjectHelper.isEmpty(awayList)) {
							if(newList.get(i).getHometeamid().equals("2157")) {
								System.out.println(123);
							}
							FoRelationTeam rteam = new FoRelationTeam();
							rteam.setJcteamshortname(newList.get(i).getAwayshortname());
							rteam.setJcteamfullname(newList.get(i).getAwayfullname());
							rteam.setJcteamid(newList.get(i).getAwayteamid());
							rteam.setWubaiteamshortname(oldList.get(i).getAwayShortname());
							rteam.setWubaiteamfullname(oldList.get(i).getAwayFullname());
							rteam.setWubaiteamid(oldList.get(i).getAwayUrlId());
							rteam.setId(IDUtils.createUUId());
							rtMapper.insert(rteam);
						}
					}
				}
			}
			System.out.println("联赛、队伍信息处理完毕,准备执行500数据抓取");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		handleLeaguesAndTeams();
//		showTimer();
	}
}
