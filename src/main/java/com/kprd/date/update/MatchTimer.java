package com.kprd.date.update;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.constant.Context;
import com.kprd.date.constant.RegularVerify;
import com.kprd.date.history.game.entity.GameDEntity;
import com.kprd.date.history.game.entity.GameEntity;
import com.kprd.date.history.game.entity.Interface1004;
import com.kprd.date.history.game.entity.Interface1022;
import com.kprd.date.history.img.DownloadImage;
import com.kprd.date.history.leaguematch.entity.Interface1003;
import com.kprd.date.history.leaguematch.entity.LeaguematchEntity;
import com.kprd.date.history.odds.entity.Entity2018R;
import com.kprd.date.history.odds.entity.Interface2018;
import com.kprd.date.history.statistics.entity.FirstscoreEntity;
import com.kprd.date.history.statistics.entity.GoalscoreEntity;
import com.kprd.date.history.statistics.entity.Interface1007;
import com.kprd.date.history.statistics.entity.Interface1008;
import com.kprd.date.history.statistics.entity.Interface1009;
import com.kprd.date.history.statistics.entity.Interface1010;
import com.kprd.date.history.statistics.entity.Interface1011;
import com.kprd.date.history.statistics.entity.Interface1012;
import com.kprd.date.history.statistics.entity.Interface1014;
import com.kprd.date.history.statistics.entity.Interface3010;
import com.kprd.date.history.statistics.entity.MDxqscoreEntity;
import com.kprd.date.history.statistics.entity.PlayerEventEntity;
import com.kprd.date.history.statistics.entity.QbcscoreEntity;
import com.kprd.date.history.statistics.entity.RecordEntity;
import com.kprd.date.history.statistics.entity.ShooterscoreEntity;
import com.kprd.date.history.statistics.entity.SummaryscoreEntity;
import com.kprd.date.history.statistics.entity.TeamScoreEntity;
import com.kprd.date.history.statistics.entity.TotalEntity;
import com.kprd.dic.mapper.DMatchMapper;
import com.kprd.dic.mapper.DPlayerMapper;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DMatch;
import com.kprd.dic.pojo.DPlayer;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MDxqscoreMapper;
import com.kprd.match.mapper.MFirstscoreMapper;
import com.kprd.match.mapper.MGameMapper;
import com.kprd.match.mapper.MGameeventrecordMapper;
import com.kprd.match.mapper.MGoalscoreMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.mapper.MQbcscoreMapper;
import com.kprd.match.mapper.MShooterscoreMapper;
import com.kprd.match.mapper.MSummaryscoreMapper;
import com.kprd.match.mapper.MTeamgamerecordMapper;
import com.kprd.match.mapper.MTeamscoreMapper;
import com.kprd.match.pojo.MDxqscore;
import com.kprd.match.pojo.MDxqscoreExample;
import com.kprd.match.pojo.MFirstscore;
import com.kprd.match.pojo.MFirstscoreExample;
import com.kprd.match.pojo.MGame;
import com.kprd.match.pojo.MGameExample;
import com.kprd.match.pojo.MGameExample.Criteria;
import com.kprd.match.pojo.MGameeventrecord;
import com.kprd.match.pojo.MGoalscore;
import com.kprd.match.pojo.MGoalscoreExample;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MQbcscore;
import com.kprd.match.pojo.MQbcscoreExample;
import com.kprd.match.pojo.MShooterscore;
import com.kprd.match.pojo.MShooterscoreExample;
import com.kprd.match.pojo.MSummaryscore;
import com.kprd.match.pojo.MSummaryscoreExample;
import com.kprd.match.pojo.MTeamgamerecord;
import com.kprd.match.pojo.MTeamscore;
import com.kprd.match.pojo.MTeamscoreExample;


/**
 * 检查更新最新赛季的所有数据
 * @author 霍俊
 */
public class MatchTimer {

	public static void execute(int i){
		MLeaguematchMapper mLeaguematchMapper = RealMain.applicationContext.getBean(MLeaguematchMapper.class);
		Date today = new Date();
		List<String> lmIds = new ArrayList<String>();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd"); 
		Date day = new Date(today.getTime()+((i-1)*86400000));
		Map<String,String> params = new HashMap<String,String>();
		params.put("expect", format.format(day));
		params.put("lottid", "6");
		params.put("oid", "2018");
		String jsonStrXs = HttpClientUtil.doGet(Context.BASE_URL, params);
		Interface2018 interface2018 = JsonUtils.jsonToPojo(jsonStrXs, Interface2018.class);
		if(interface2018.getCode()==0&&interface2018.getR().size()>0) {
			for(Entity2018R entity2018R : interface2018.getR()){
				//更新比赛数据
				String id = restGameData(entity2018R.getMid(),entity2018R.getLid());
				//获取比赛的赛季数据YID
				boolean fg = false;
				for(String lmId : lmIds){
					if(ObjectHelper.isNotEmpty(id)&&lmId.equals(id)){ 
						fg = true;
						break;
					}
				}
				if(!fg&&ObjectHelper.isNotEmpty(id))lmIds.add(id);
			}
		}
		//正在进行中赛季的更新
		for(String lmId : lmIds){
			MLeaguematch leaguematch = mLeaguematchMapper.selectByPrimaryKey(lmId);
			//统计
			restLeagueMatchStatistics(leaguematch);
			//
			restGameStatistics(leaguematch);
		}
	}
	
	
	public static String restGameData(String mid,String lid){
		//验证联赛是否存在
		MGameMapper mGameMapper = RealMain.applicationContext.getBean(MGameMapper.class);
		DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
		MLeaguematch leaguematch = getLeagueMatchId(Integer.valueOf(mid));
		if(ObjectHelper.isEmpty(leaguematch)){return null;}
		Map<String,String> param = new HashMap<String,String>();
		param.put("oid", Context.GAMEDATA);
		param.put("sid",leaguematch.getYid().toString());
		param.put("lid",leaguematch.getMatchyid().toString());
		String jsonStr = HttpClientUtil.doGet(Context.BASE_URL, param);
		Interface1004 interface1004 = JsonUtils.jsonToPojo(jsonStr, Interface1004.class);
		if(ObjectHelper.isNotEmpty(interface1004)&&interface1004.getCode()==0&&interface1004.getRow().size()>0) {
			for(GameEntity gameEntity:interface1004.getRow()) {
				//根据比赛编号验证本地数据库是否有该比赛有则放行没有则新增
				MGame mGame = mGameMapper.selectByYId(Integer.parseInt(gameEntity.getMid()));
				Map<String,String> dparam = new HashMap<String,String>();
				dparam.put("oid", Context.GAMEDDATA);
				dparam.put("mid",gameEntity.getMid().toString());
				String djsonStr = HttpClientUtil.doGet(Context.BASE_URL, dparam);
				Interface1022 interface1022 = JsonUtils.jsonToPojo(djsonStr, Interface1022.class);
				if(ObjectHelper.isNotEmpty(interface1022)&&interface1022.getCode()==0) {
					GameDEntity gameDEntity = interface1022.getRow();
					if(ObjectHelper.isNotEmpty(gameDEntity)) {
						DTeam  hTeam  = dTeamMapper.selectByYId(Integer.parseInt(gameDEntity.getHtid()));//主队
						DTeam  aTeam  = dTeamMapper.selectByYId(Integer.parseInt(gameDEntity.getAtid()));//客队
						if(ObjectHelper.isEmpty(mGame)) {
							if(ObjectHelper.isNotEmpty(hTeam)&&ObjectHelper.isNotEmpty(aTeam)) {
								//新增比赛记录
								mGame = new MGame();
								mGame.setId(IDUtils.createUUId());
								mGame.setAwaybcscore(gameDEntity.getHas());
								mGame.setAwaycn(gameDEntity.getAway());
								
								mGame.setAwaycrad(gameDEntity.getAr());
								mGame.setAwayen(gameDEntity.getAwayen());
								mGame.setAwayid(aTeam.getId());
								
								try {
									String fileName = gameDEntity.getAwaylogo().substring(gameDEntity.getAwaylogo().lastIndexOf("/")+1, gameDEntity.getAwaylogo().length());
									DownloadImage.download(gameDEntity.getAwaylogo(), fileName, "d:\\image\\");
									mGame.setAwaylogo(fileName);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								mGame.setAwayscore(gameDEntity.getAs());
								mGame.setBcscore(gameDEntity.getBc());
								mGame.setCount(interface1004.getC().getCount()!=null?Integer.parseInt(interface1004.getC().getCount()):0);
								mGame.setGroupname(gameDEntity.getGn());
								
								mGame.setHomebcscore(gameDEntity.getHhs());
								mGame.setHomecn(gameDEntity.getHome());
								mGame.setHomecrad(gameDEntity.getHr());
								mGame.setHomeen(gameDEntity.getHomeen());
								
								mGame.setHomeid(hTeam.getId());
								
								try {
									String fileName = gameDEntity.getHomelogo().substring(gameDEntity.getHomelogo().lastIndexOf("/")+1, gameDEntity.getHomelogo().length());
									DownloadImage.download(gameDEntity.getAwaylogo(), fileName, "d:\\image\\");
									mGame.setHomelogo(fileName);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								mGame.setHomescore(gameDEntity.getHs());
								mGame.setMatchid(leaguematch.getMatchid());
								mGame.setMatchcolor(gameDEntity.getCl());
								mGame.setMatchname(gameDEntity.getLn());
								
								mGame.setOid(gameDEntity.getOid()!=null?Integer.parseInt(gameDEntity.getOid()):null);
								mGame.setOname(gameDEntity.getOname());
								
								mGame.setRound(gameEntity.getRid()!=null?Integer.parseInt(gameEntity.getRid()):null);
								mGame.setStarttime(new Date(1000*Long.valueOf(gameDEntity.getMtime())));
								mGame.setYid(gameDEntity.getMid()!=null?Integer.parseInt(gameDEntity.getMid()):null);
								mGame.setLeaguematchid(leaguematch.getId());
								mGame.setQcscore(gameDEntity.getBc());
								mGame.setStatus(gameDEntity.getState()!=null?Integer.parseInt(gameDEntity.getState()):3);
								mGameMapper.insertSelective(mGame);
								System.out.println("新增比赛："+mGame.getMatchname());
							}
						}else{
							if(ObjectHelper.isNotEmpty(hTeam)&&ObjectHelper.isNotEmpty(aTeam)) {
								mGame.setAwaybcscore(gameDEntity.getHas());
								mGame.setAwaycn(gameDEntity.getAway());
								
								mGame.setAwaycrad(gameDEntity.getAr());
								mGame.setAwayen(gameDEntity.getAwayen());
								mGame.setAwayid(aTeam.getId());
								try {
									String fileName = gameDEntity.getAwaylogo().substring(gameDEntity.getAwaylogo().lastIndexOf("/")+1, gameDEntity.getAwaylogo().length());
									if(!mGame.getAwaylogo().equals(fileName)){
										DownloadImage.download(gameDEntity.getAwaylogo(), fileName, "d:\\image\\");
										mGame.setAwaylogo(fileName);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								mGame.setAwayscore(gameDEntity.getAs());
								mGame.setBcscore(gameDEntity.getBc());
								mGame.setCount(interface1004.getC().getCount()!=null?Integer.parseInt(interface1004.getC().getCount()):0);
								mGame.setGroupname(gameDEntity.getGn());
								
								mGame.setHomebcscore(gameDEntity.getHhs());
								mGame.setHomecn(gameDEntity.getHome());
								mGame.setHomecrad(gameDEntity.getHr());
								mGame.setHomeen(gameDEntity.getHomeen());
								
								mGame.setHomeid(hTeam.getId());
								
								try {
									String fileName = gameDEntity.getHomelogo().substring(gameDEntity.getHomelogo().lastIndexOf("/")+1, gameDEntity.getHomelogo().length());
									if(!mGame.getAwaylogo().equals(fileName)){
										DownloadImage.download(gameDEntity.getAwaylogo(), fileName, "d:\\image\\");
										mGame.setHomelogo(fileName);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								mGame.setHomescore(gameDEntity.getHs());
								mGame.setMatchid(leaguematch.getMatchid());
								mGame.setMatchcolor(gameDEntity.getCl());
								mGame.setMatchname(gameDEntity.getLn());
								
								mGame.setOid(gameDEntity.getOid()!=null?Integer.parseInt(gameDEntity.getOid()):null);
								mGame.setOname(gameDEntity.getOname());
								
								mGame.setRound(gameEntity.getRid()!=null?Integer.parseInt(gameEntity.getRid()):null);
								mGame.setStarttime(new Date(1000*Long.valueOf(gameDEntity.getMtime())));
								mGame.setYid(gameDEntity.getMid()!=null?Integer.parseInt(gameDEntity.getMid()):null);
								mGame.setLeaguematchid(leaguematch.getId());
								mGame.setQcscore(gameDEntity.getBc());
								mGame.setStatus(gameDEntity.getState()!=null?Integer.parseInt(gameDEntity.getState()):3);
								MGameExample example = new MGameExample();
								example.createCriteria().andIdEqualTo(mGame.getId());
								mGameMapper.updateByExampleSelective(mGame, example);
								System.out.println("更新比赛："+mGame.getMatchname());
							}
						}
					}
				}
			}
		}
		return leaguematch.getId();
	}
	
	
	public static MLeaguematch getLeagueMatchId(Integer mid){
		MLeaguematchMapper mLeaguematchMapper = RealMain.applicationContext.getBean(MLeaguematchMapper.class);
		MGameMapper mGameMapper = RealMain.applicationContext.getBean(MGameMapper.class);
		MGame game = mGameMapper.selectByYId(mid);
		MLeaguematch leaguematch = null; 
		if(ObjectHelper.isEmpty(game)){
			Map<String,String> dparam = new HashMap<String,String>();
			dparam.put("oid",Context.GAMEDDATA);
			dparam.put("mid",mid.toString());
			String djsonStr = HttpClientUtil.doGet(Context.BASE_URL, dparam);
			Interface1022 interface1022 = JsonUtils.jsonToPojo(djsonStr, Interface1022.class);
			if(ObjectHelper.isNotEmpty(interface1022)&&interface1022.getCode()==0) {
				GameDEntity gameDEntity = interface1022.getRow();
				if(ObjectHelper.isNotEmpty(gameDEntity)) {
					leaguematch = mLeaguematchMapper.selectByYId(Integer.valueOf(gameDEntity.getSid()));
					if(ObjectHelper.isEmpty(leaguematch)){
						//如果没有联赛信息则添加联赛信息
						leaguematch = restLeaguematch(gameDEntity.getLid(),gameDEntity.getSid());
					}
				}
			}
		}else{
			leaguematch = mLeaguematchMapper.selectByPrimaryKey(game.getLeaguematchid());
			if(ObjectHelper.isEmpty(leaguematch)){
				//如果没有联赛信息则添加联赛信息
				leaguematch = restLeaguematch2(game.getMatchid(),game.getLeaguematchid());
			}
		}
		return leaguematch;
	}
	
	
	public static MLeaguematch restLeaguematch(String lid,String sid){
		DMatchMapper dMatchMapper = RealMain.applicationContext.getBean(DMatchMapper.class);
		MLeaguematchMapper mLeaguematchMapper = RealMain.applicationContext.getBean(MLeaguematchMapper.class);
		DMatch dMatch = dMatchMapper.selectByYId(Integer.valueOf(lid));
		
		Map<String,String> param = new HashMap<String,String>();
		param.put("oid", Context.LEAGUEMATCHDATA);
		param.put("lid",lid);
		String url = Context.BASE_URL;
		//////////////////通过 http请求有料接口//////////////////////
		String jsonStr = HttpClientUtil.doGet(url, param);
		Interface1003 interface1003 = JsonUtils.jsonToPojo(jsonStr, Interface1003.class);
		if(ObjectHelper.isNotEmpty(interface1003)&&interface1003.getCode()==0&&interface1003.getRow().size()>0) {
			for(LeaguematchEntity leaguematchEntity:interface1003.getRow()) {
				//验证是否有该联赛记录，没有则新增
				MLeaguematch mLeaguematch = mLeaguematchMapper.selectByYId(leaguematchEntity.getSid());
				if(ObjectHelper.isEmpty(mLeaguematch)&&leaguematchEntity.getSid().equals(Integer.valueOf(sid))) {//未查到该记录则新增
					MLeaguematch m = new MLeaguematch();
					m.setId(IDUtils.createUUId());
					m.setYid(leaguematchEntity.getSid());
					m.setMatchid(dMatch.getId());	
					m.setMatchyid(dMatch.getYid());
					m.setSname(leaguematchEntity.getSname());
					mLeaguematchMapper.insertSelective(m);
					return m;
				}
			}
		}
		return null;
	}
	
	public static MLeaguematch restLeaguematch2(String mid,String lmid){
		DMatchMapper dMatchMapper = RealMain.applicationContext.getBean(DMatchMapper.class);
		MLeaguematchMapper mLeaguematchMapper = RealMain.applicationContext.getBean(MLeaguematchMapper.class);
		DMatch dMatch = dMatchMapper.selectByPrimaryKey(mid);
		Map<String,String> param = new HashMap<String,String>();
		param.put("oid", Context.LEAGUEMATCHDATA);
		param.put("lid",dMatch.getYid().toString());
		String url = Context.BASE_URL;
		//////////////////通过 http请求有料接口//////////////////////
		String jsonStr = HttpClientUtil.doGet(url, param);
		Interface1003 interface1003 = JsonUtils.jsonToPojo(jsonStr, Interface1003.class);
		if(ObjectHelper.isNotEmpty(interface1003)&&interface1003.getCode()==0&&interface1003.getRow().size()>0) {
			for(LeaguematchEntity leaguematchEntity:interface1003.getRow()) {
				//验证是否有该联赛记录，没有则新增
				MLeaguematch mLeaguematch = mLeaguematchMapper.selectByPrimaryKey(lmid);
				if(ObjectHelper.isEmpty(mLeaguematch)) {//未查到该记录则新增
					MLeaguematch m = new MLeaguematch();
					m.setId(lmid);
					m.setYid(leaguematchEntity.getSid());
					m.setMatchid(dMatch.getId());	
					m.setMatchyid(dMatch.getYid());
					m.setSname(leaguematchEntity.getSname());
					mLeaguematchMapper.insertSelective(m);
					return m;
				}
			}
		}
		return null;
	}
	
	public static void restLeagueMatchStatistics(MLeaguematch mLeaguematch){
		{
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid", Context.FIRSTSCOREDATE);
			param.put("sid",mLeaguematch.getYid().toString());
			param.put("lid",mLeaguematch.getMatchyid().toString());
			String url = Context.BASE_URL;
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(url, param);
			Interface1011 interface1011 = JsonUtils.jsonToPojo(jsonStr, Interface1011.class);
			if(ObjectHelper.isNotEmpty(interface1011)&&interface1011.getCode()==0&&interface1011.getRow().size()>0) {
				for(FirstscoreEntity firstscoreEntity:interface1011.getRow()) {
					//验证是否有该联赛新赛季下是否有数据有则不新增 反之新增
					MFirstscoreMapper mFirstscoreMapper = RealMain.applicationContext.getBean(MFirstscoreMapper.class);
					MFirstscoreExample fexample = new MFirstscoreExample();
					com.kprd.match.pojo.MFirstscoreExample.Criteria createCriteria = fexample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					createCriteria.andTeamyidEqualTo(firstscoreEntity.getTid());
					List<MFirstscore> mGoalscores =  mFirstscoreMapper.selectByExample(fexample);
					if(ObjectHelper.isEmpty(mGoalscores)) {//未查到该记录则新增
						
						//根据球队ID查询球队信息
						DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
						DTeam dTeam = dTeamMapper.selectByYId(firstscoreEntity.getTid());
						if(ObjectHelper.isNotEmpty(dTeam)) {
							MFirstscore mFirstscore = new MFirstscore();
							mFirstscore.setId(IDUtils.createUUId());
							mFirstscore.setLeaguematchid(mLeaguematch.getId());
							mFirstscore.setLeaguematchyid(mLeaguematch.getYid());
							
							mFirstscore.setMatchid(mLeaguematch.getMatchid());
							mFirstscore.setMatchyid(mLeaguematch.getMatchyid());
							mFirstscore.setMissfirstaway(RegularVerify.isNum(firstscoreEntity.getAl())?Integer.parseInt(firstscoreEntity.getAl()):0);
							mFirstscore.setMissfirsthome(RegularVerify.isNum(firstscoreEntity.getHl())?Integer.parseInt(firstscoreEntity.getHl()):0);
							
							mFirstscore.setObtainfirstaway(RegularVerify.isNum(firstscoreEntity.getAs())?Integer.parseInt(firstscoreEntity.getAs()):0);
							mFirstscore.setObtainfirsthome(RegularVerify.isNum(firstscoreEntity.getHs())?Integer.parseInt(firstscoreEntity.getHs()):0);
							mFirstscore.setTeamid(dTeam.getId());
							mFirstscore.setTeamname(dTeam.getNamecn());
							mFirstscore.setTeamyid(dTeam.getYid());
							mFirstscoreMapper.insertSelective(mFirstscore);
							System.out.println("新增联赛球队最先入失球数:"+mLeaguematch.getSname()+" : " +mFirstscore.getTeamname());
						}						
					}else{
						MFirstscore mFirstscore = mGoalscores.get(0);
						mFirstscore.setMissfirstaway(RegularVerify.isNum(firstscoreEntity.getAl())?Integer.parseInt(firstscoreEntity.getAl()):0);
						mFirstscore.setMissfirsthome(RegularVerify.isNum(firstscoreEntity.getHl())?Integer.parseInt(firstscoreEntity.getHl()):0);
						mFirstscore.setObtainfirstaway(RegularVerify.isNum(firstscoreEntity.getAs())?Integer.parseInt(firstscoreEntity.getAs()):0);
						mFirstscore.setObtainfirsthome(RegularVerify.isNum(firstscoreEntity.getHs())?Integer.parseInt(firstscoreEntity.getHs()):0);
						MFirstscoreExample example = new MFirstscoreExample();
						example.createCriteria().andIdEqualTo(mFirstscore.getId());
						mFirstscoreMapper.updateByExampleSelective(mFirstscore, example);
						System.out.println("更新联赛球队最先入失球数:"+mLeaguematch.getSname()+" : " +mFirstscore.getTeamname());
					}
				}
			}
		}
		{
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid", "1009");
			param.put("sid",mLeaguematch.getYid().toString());
			param.put("lid",mLeaguematch.getMatchyid().toString());
			String url = Context.BASE_URL;
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(url, param);
			Interface1009 interface1009 = JsonUtils.jsonToPojo(jsonStr, Interface1009.class);
			if(ObjectHelper.isNotEmpty(interface1009)&&interface1009.getCode()==0&&interface1009.getRow().size()>0) {
				for(GoalscoreEntity goalscoreEntity:interface1009.getRow()) {
					//验证是否有该联赛新赛季下是否有数据有则不新增 反之新增
					MGoalscoreMapper mGoalscoreMapper = RealMain.applicationContext.getBean(MGoalscoreMapper.class);
					MGoalscoreExample gexample = new MGoalscoreExample();
					com.kprd.match.pojo.MGoalscoreExample.Criteria createCriteria = gexample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					createCriteria.andTeamyidEqualTo(goalscoreEntity.getTid());
					List<MGoalscore> mGoalscores =  mGoalscoreMapper.selectByExample(gexample);
					if(ObjectHelper.isEmpty(mGoalscores)) {//未查到该记录则新增
						//根据球队ID查询球队信息
						DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
						DTeam dTeam = dTeamMapper.selectByYId(goalscoreEntity.getTid());
						if(ObjectHelper.isNotEmpty(dTeam)) {
							MGoalscore mGoalscore = new MGoalscore();
							mGoalscore.setId(IDUtils.createUUId());
							mGoalscore.setAway01(RegularVerify.isNum(goalscoreEntity.getA01())?Integer.parseInt(goalscoreEntity.getA01()):0);			
							mGoalscore.setAway23(RegularVerify.isNum(goalscoreEntity.getA23())?Integer.parseInt(goalscoreEntity.getA23()):0);
							mGoalscore.setAway46(RegularVerify.isNum(goalscoreEntity.getA46())?Integer.parseInt(goalscoreEntity.getA46()):0);
							mGoalscore.setAway7(RegularVerify.isNum(goalscoreEntity.getA7())?Integer.parseInt(goalscoreEntity.getA7()):0);
							mGoalscore.setHome01(RegularVerify.isNum(goalscoreEntity.getH01())?Integer.parseInt(goalscoreEntity.getH01()):0);
							mGoalscore.setHome23(RegularVerify.isNum(goalscoreEntity.getH23())?Integer.parseInt(goalscoreEntity.getH23()):0);
							mGoalscore.setHome46(RegularVerify.isNum(goalscoreEntity.getH46())?Integer.parseInt(goalscoreEntity.getH46()):0);
							mGoalscore.setHome7(RegularVerify.isNum(goalscoreEntity.getH7())?Integer.parseInt(goalscoreEntity.getH7()):0);
							mGoalscore.setLeaguematchid(mLeaguematch.getId());
							mGoalscore.setLeaguematchyid(mLeaguematch.getYid());
							mGoalscore.setMatchid(mLeaguematch.getMatchid());
							mGoalscore.setMatchyid(mLeaguematch.getMatchyid());
							
							mGoalscore.setTeamid(dTeam.getId());
							mGoalscore.setTeamyid(dTeam.getYid());
							mGoalscore.setTeamname(dTeam.getNamecn());
							mGoalscoreMapper.insertSelective(mGoalscore);
							System.out.println("新增联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mGoalscore.getTeamname());
						}						
					}else{
						MGoalscore mGoalscore = mGoalscores.get(0);
						mGoalscore.setAway01(RegularVerify.isNum(goalscoreEntity.getA01())?Integer.parseInt(goalscoreEntity.getA01()):0);			
						mGoalscore.setAway23(RegularVerify.isNum(goalscoreEntity.getA23())?Integer.parseInt(goalscoreEntity.getA23()):0);
						mGoalscore.setAway46(RegularVerify.isNum(goalscoreEntity.getA46())?Integer.parseInt(goalscoreEntity.getA46()):0);
						mGoalscore.setAway7(RegularVerify.isNum(goalscoreEntity.getA7())?Integer.parseInt(goalscoreEntity.getA7()):0);
						mGoalscore.setHome01(RegularVerify.isNum(goalscoreEntity.getH01())?Integer.parseInt(goalscoreEntity.getH01()):0);
						mGoalscore.setHome23(RegularVerify.isNum(goalscoreEntity.getH23())?Integer.parseInt(goalscoreEntity.getH23()):0);
						mGoalscore.setHome46(RegularVerify.isNum(goalscoreEntity.getH46())?Integer.parseInt(goalscoreEntity.getH46()):0);
						mGoalscore.setHome7(RegularVerify.isNum(goalscoreEntity.getH7())?Integer.parseInt(goalscoreEntity.getH7()):0);
						MGoalscoreExample example = new MGoalscoreExample();
						example.createCriteria().andIdEqualTo(mGoalscore.getId());
						mGoalscoreMapper.updateByExampleSelective(mGoalscore,example);
						System.out.println("更新联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mGoalscore.getTeamname());
					}
				}
			}
		}
		{
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid", Context.DXQSCOREDATA);
			param.put("sid",mLeaguematch.getYid().toString());
			param.put("lid",mLeaguematch.getMatchyid().toString());
			String url = Context.BASE_URL;
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(url, param);
			Interface1010 interface1010 = JsonUtils.jsonToPojo(jsonStr, Interface1010.class);
			if(ObjectHelper.isNotEmpty(interface1010)&&interface1010.getCode()==0&&interface1010.getRow().size()>0) {
				for(MDxqscoreEntity mDxqscoreEntity:interface1010.getRow()) {
					//验证是否有该联赛新赛季下是否有数据有则不新增 反之新增
					MDxqscoreMapper mDxqscoreMapper = RealMain.applicationContext.getBean(MDxqscoreMapper.class);
					MDxqscoreExample dexample = new MDxqscoreExample();
					com.kprd.match.pojo.MDxqscoreExample.Criteria createCriteria = dexample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					createCriteria.andTeamyidEqualTo(mDxqscoreEntity.getTid());
					List<MDxqscore> mDxqscores =  mDxqscoreMapper.selectByExample(dexample);
					if(ObjectHelper.isEmpty(mDxqscores)) {//未查到该记录则新增
						//根据球队ID查询球队信息
						DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
						DTeam dTeam = dTeamMapper.selectByYId(mDxqscoreEntity.getTid());
						if(ObjectHelper.isNotEmpty(dTeam)) {
							MDxqscore mDxqscore = new MDxqscore();
							mDxqscore.setId(IDUtils.createUUId());
							mDxqscore.setDbaway(RegularVerify.isNum(mDxqscoreEntity.getAo())?Integer.parseInt(mDxqscoreEntity.getAo()):0);
							mDxqscore.setDbhome(RegularVerify.isNum(mDxqscoreEntity.getAo())?Integer.parseInt(mDxqscoreEntity.getHo()):0);					
							mDxqscore.setLargeaway(RegularVerify.isNum(mDxqscoreEntity.getAd())?Integer.parseInt(mDxqscoreEntity.getAd()):0);
							mDxqscore.setLargehome(RegularVerify.isNum(mDxqscoreEntity.getHd())?Integer.parseInt(mDxqscoreEntity.getHd()):0);
							mDxqscore.setSlaway(RegularVerify.isNum(mDxqscoreEntity.getHs())?Integer.parseInt(mDxqscoreEntity.getHs()):0);
							mDxqscore.setSlhome(RegularVerify.isNum(mDxqscoreEntity.getAs())?Integer.parseInt(mDxqscoreEntity.getAs()):0);
							mDxqscore.setSmallaway(RegularVerify.isNum(mDxqscoreEntity.getHx())?Integer.parseInt(mDxqscoreEntity.getHx()):0);
							mDxqscore.setSmallhome(RegularVerify.isNum(mDxqscoreEntity.getAx())?Integer.parseInt(mDxqscoreEntity.getAx()):0);
							mDxqscore.setLeaguematchid(mLeaguematch.getId());
							mDxqscore.setLeaguematchyid(mLeaguematch.getYid());
							mDxqscore.setMatchid(mLeaguematch.getMatchid());
							mDxqscore.setMatchyid(mLeaguematch.getMatchyid());
							mDxqscore.setTeamid(dTeam.getId());			
							mDxqscore.setTeamyid(dTeam.getYid());
							mDxqscore.setTeamname(dTeam.getNamecn());
							mDxqscoreMapper.insertSelective(mDxqscore);
							System.out.println("新增联赛入球单双大小统计:"+mLeaguematch.getSname()+" : " +mDxqscore.getTeamname());
						}						
					}else{
						MDxqscore mDxqscore = mDxqscores.get(0);
						mDxqscore.setDbaway(RegularVerify.isNum(mDxqscoreEntity.getAo())?Integer.parseInt(mDxqscoreEntity.getAo()):0);
						mDxqscore.setDbhome(RegularVerify.isNum(mDxqscoreEntity.getAo())?Integer.parseInt(mDxqscoreEntity.getHo()):0);					
						mDxqscore.setLargeaway(RegularVerify.isNum(mDxqscoreEntity.getAd())?Integer.parseInt(mDxqscoreEntity.getAd()):0);
						mDxqscore.setLargehome(RegularVerify.isNum(mDxqscoreEntity.getHd())?Integer.parseInt(mDxqscoreEntity.getHd()):0);
						mDxqscore.setSlaway(RegularVerify.isNum(mDxqscoreEntity.getHs())?Integer.parseInt(mDxqscoreEntity.getHs()):0);
						mDxqscore.setSlhome(RegularVerify.isNum(mDxqscoreEntity.getAs())?Integer.parseInt(mDxqscoreEntity.getAs()):0);
						mDxqscore.setSmallaway(RegularVerify.isNum(mDxqscoreEntity.getHx())?Integer.parseInt(mDxqscoreEntity.getHx()):0);
						mDxqscore.setSmallhome(RegularVerify.isNum(mDxqscoreEntity.getAx())?Integer.parseInt(mDxqscoreEntity.getAx()):0);
						MDxqscoreExample example = new MDxqscoreExample();
						example.createCriteria().andIdEqualTo(mDxqscore.getId());
						mDxqscoreMapper.updateByExampleSelective(mDxqscore,example);
						System.out.println("更新联赛入球单双大小统计:"+mLeaguematch.getSname()+" : " +mDxqscore.getTeamname());
					}
				}
			}
		}
		{
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid","1008");
			param.put("sid",mLeaguematch.getYid().toString());
			param.put("lid",mLeaguematch.getMatchyid().toString());
			String url = Context.BASE_URL;
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(url, param);
			Interface1008 interface1008 = JsonUtils.jsonToPojo(jsonStr, Interface1008.class);
			if(ObjectHelper.isNotEmpty(interface1008)&&interface1008.getCode()==0&&interface1008.getRow().size()>0) {
				for(QbcscoreEntity qbcscoreEntity:interface1008.getRow()) {
					
					MQbcscoreMapper mQbcscoreMapper = RealMain.applicationContext.getBean(MQbcscoreMapper.class);
					MQbcscoreExample qexample = new MQbcscoreExample();
					com.kprd.match.pojo.MQbcscoreExample.Criteria createCriteria = qexample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					createCriteria.andTeamyidEqualTo(qbcscoreEntity.getTid());
					List<MQbcscore> mQbcscores =  mQbcscoreMapper.selectByExample(qexample);
					if(ObjectHelper.isEmpty(mQbcscores)) {//未查到该记录则新增
						//根据球队ID查询球队信息
						DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
						DTeam dTeam = dTeamMapper.selectByYId(qbcscoreEntity.getTid());
						if(ObjectHelper.isNotEmpty(dTeam)) {
							MQbcscore mQbcscore = new MQbcscore();
							mQbcscore.setId(IDUtils.createUUId());		
							mQbcscore.setFfaway(RegularVerify.isNum(qbcscoreEntity.getAaa())?Integer.parseInt(qbcscoreEntity.getAaa()):0);
							mQbcscore.setFfhome(RegularVerify.isNum(qbcscoreEntity.getHaa())?Integer.parseInt(qbcscoreEntity.getHaa()):0);
							mQbcscore.setFpaway(RegularVerify.isNum(qbcscoreEntity.getAad())?Integer.parseInt(qbcscoreEntity.getAad()):0);
							mQbcscore.setFphome(RegularVerify.isNum(qbcscoreEntity.getHad())?Integer.parseInt(qbcscoreEntity.getHad()):0);
							mQbcscore.setFsaway(RegularVerify.isNum(qbcscoreEntity.getAad())?Integer.parseInt(qbcscoreEntity.getAad()):0);
							mQbcscore.setFshome(RegularVerify.isNum(qbcscoreEntity.getHah())?Integer.parseInt(qbcscoreEntity.getHah()):0);
							mQbcscore.setLeaguematchid(mLeaguematch.getId());
							mQbcscore.setLeaguematchyid(mLeaguematch.getYid());
							mQbcscore.setMatchid(mLeaguematch.getMatchid());
							
							mQbcscore.setMatchyid(mLeaguematch.getMatchyid());
							mQbcscore.setPfaway(RegularVerify.isNum(qbcscoreEntity.getAda())?Integer.parseInt(qbcscoreEntity.getAda()):0);
							mQbcscore.setPfhome(RegularVerify.isNum(qbcscoreEntity.getHda())?Integer.parseInt(qbcscoreEntity.getHda()):0);
							mQbcscore.setPpaway(RegularVerify.isNum(qbcscoreEntity.getAdd())?Integer.parseInt(qbcscoreEntity.getAdd()):0);
							mQbcscore.setPphome(RegularVerify.isNum(qbcscoreEntity.getHdd())?Integer.parseInt(qbcscoreEntity.getHdd()):0);
							mQbcscore.setPsaway(RegularVerify.isNum(qbcscoreEntity.getAdh())?Integer.parseInt(qbcscoreEntity.getAdh()):0);
							mQbcscore.setPshome(RegularVerify.isNum(qbcscoreEntity.getHdh())?Integer.parseInt(qbcscoreEntity.getHdh()):0);
							mQbcscore.setSfaway(RegularVerify.isNum(qbcscoreEntity.getAha())?Integer.parseInt(qbcscoreEntity.getAha()):0);
							mQbcscore.setSfhome(RegularVerify.isNum(qbcscoreEntity.getHha())?Integer.parseInt(qbcscoreEntity.getHha()):0);
							mQbcscore.setSpaway(RegularVerify.isNum(qbcscoreEntity.getAhd())?Integer.parseInt(qbcscoreEntity.getAhd()):0);
							mQbcscore.setSphome(RegularVerify.isNum(qbcscoreEntity.getHhd())?Integer.parseInt(qbcscoreEntity.getHhd()):0);
							mQbcscore.setSsaway(RegularVerify.isNum(qbcscoreEntity.getAhh())?Integer.parseInt(qbcscoreEntity.getAhh()):0);
							mQbcscore.setSshome(RegularVerify.isNum(qbcscoreEntity.getHhh())?Integer.parseInt(qbcscoreEntity.getHhh()):0);
							mQbcscore.setTeamid(dTeam.getId());
							mQbcscore.setTeamname(dTeam.getNamecn());
							mQbcscore.setTeamyid(dTeam.getYid());
							mQbcscoreMapper.insertSelective(mQbcscore);
							System.out.println("新增联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mQbcscore.getTeamname());
						}						
					}else{
						MQbcscore mQbcscore = mQbcscores.get(0);
						mQbcscore.setFfaway(RegularVerify.isNum(qbcscoreEntity.getAaa())?Integer.parseInt(qbcscoreEntity.getAaa()):0);
						mQbcscore.setFfhome(RegularVerify.isNum(qbcscoreEntity.getHaa())?Integer.parseInt(qbcscoreEntity.getHaa()):0);
						mQbcscore.setFpaway(RegularVerify.isNum(qbcscoreEntity.getAad())?Integer.parseInt(qbcscoreEntity.getAad()):0);
						mQbcscore.setFphome(RegularVerify.isNum(qbcscoreEntity.getHad())?Integer.parseInt(qbcscoreEntity.getHad()):0);
						mQbcscore.setFsaway(RegularVerify.isNum(qbcscoreEntity.getAad())?Integer.parseInt(qbcscoreEntity.getAad()):0);
						mQbcscore.setFshome(RegularVerify.isNum(qbcscoreEntity.getHah())?Integer.parseInt(qbcscoreEntity.getHah()):0);
						mQbcscore.setPfaway(RegularVerify.isNum(qbcscoreEntity.getAda())?Integer.parseInt(qbcscoreEntity.getAda()):0);
						mQbcscore.setPfhome(RegularVerify.isNum(qbcscoreEntity.getHda())?Integer.parseInt(qbcscoreEntity.getHda()):0);
						mQbcscore.setPpaway(RegularVerify.isNum(qbcscoreEntity.getAdd())?Integer.parseInt(qbcscoreEntity.getAdd()):0);
						mQbcscore.setPphome(RegularVerify.isNum(qbcscoreEntity.getHdd())?Integer.parseInt(qbcscoreEntity.getHdd()):0);
						mQbcscore.setPsaway(RegularVerify.isNum(qbcscoreEntity.getAdh())?Integer.parseInt(qbcscoreEntity.getAdh()):0);
						mQbcscore.setPshome(RegularVerify.isNum(qbcscoreEntity.getHdh())?Integer.parseInt(qbcscoreEntity.getHdh()):0);
						mQbcscore.setSfaway(RegularVerify.isNum(qbcscoreEntity.getAha())?Integer.parseInt(qbcscoreEntity.getAha()):0);
						mQbcscore.setSfhome(RegularVerify.isNum(qbcscoreEntity.getHha())?Integer.parseInt(qbcscoreEntity.getHha()):0);
						mQbcscore.setSpaway(RegularVerify.isNum(qbcscoreEntity.getAhd())?Integer.parseInt(qbcscoreEntity.getAhd()):0);
						mQbcscore.setSphome(RegularVerify.isNum(qbcscoreEntity.getHhd())?Integer.parseInt(qbcscoreEntity.getHhd()):0);
						mQbcscore.setSsaway(RegularVerify.isNum(qbcscoreEntity.getAhh())?Integer.parseInt(qbcscoreEntity.getAhh()):0);
						mQbcscore.setSshome(RegularVerify.isNum(qbcscoreEntity.getHhh())?Integer.parseInt(qbcscoreEntity.getHhh()):0);
						MQbcscoreExample example = new MQbcscoreExample();
						example.createCriteria().andIdEqualTo(mQbcscore.getId());
						mQbcscoreMapper.updateByExampleSelective(mQbcscore,example);
						System.out.println("更新联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mQbcscore.getTeamname());
					}
				}
			}
		}
		{
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid", Context.SHOOTERSCOREDATE);
			param.put("sid",mLeaguematch.getYid().toString());
			param.put("lid",mLeaguematch.getMatchyid().toString());
			String url = Context.BASE_URL;
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(url, param);
			Interface1012 interface1012 = JsonUtils.jsonToPojo(jsonStr, Interface1012.class);
			if(ObjectHelper.isNotEmpty(interface1012)&&interface1012.getCode()==0&&interface1012.getRow().size()>0) {
				for(ShooterscoreEntity shooterscoreEntity:interface1012.getRow()) {
					//验证是否有该联赛新赛季下是否有数据有则不新增 反之新增
					MShooterscoreMapper mShooterscoreMapper = RealMain.applicationContext.getBean(MShooterscoreMapper.class);
					MShooterscoreExample sexample = new MShooterscoreExample();
					com.kprd.match.pojo.MShooterscoreExample.Criteria createCriteria = sexample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					createCriteria.andTeamyidEqualTo(shooterscoreEntity.getTid());
					List<MShooterscore> mShooterscores =  mShooterscoreMapper.selectByExample(sexample);
					if(ObjectHelper.isEmpty(mShooterscores)) {//未查到该记录则新增
						//根据球队ID查询球队信息
						DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
						DTeam dTeam = dTeamMapper.selectByYId(shooterscoreEntity.getTid());
						DPlayerMapper dPlayerMapper = RealMain.applicationContext.getBean(DPlayerMapper.class);
						DPlayer dPlayer = dPlayerMapper.selectByYId(shooterscoreEntity.getPid());
						if(ObjectHelper.isNotEmpty(dTeam)&&ObjectHelper.isNotEmpty(dPlayer)) {
							MShooterscore mShooterscore = new MShooterscore();
							mShooterscore.setId(IDUtils.createUUId());
							mShooterscore.setLeaguematchid(mLeaguematch.getId());
							mShooterscore.setLeaguematchyid(mLeaguematch.getYid());
							mShooterscore.setMatchid(mLeaguematch.getMatchid());
							
							mShooterscore.setMatchyid(mLeaguematch.getMatchyid());
							mShooterscore.setGoal(RegularVerify.isNum(shooterscoreEntity.getJq())?Integer.parseInt(shooterscoreEntity.getJq()):0);
							mShooterscore.setPenalty(RegularVerify.isNum(shooterscoreEntity.getDq())?Integer.parseInt(shooterscoreEntity.getDq()):0);
							mShooterscore.setPlayerid(dPlayer.getId());
							mShooterscore.setPlayername(dPlayer.getNamecn());
							mShooterscore.setPlayeryid(dPlayer.getYid());
							mShooterscore.setTeamid(dTeam.getId());
							mShooterscore.setTeamname(dTeam.getNamecn());
							mShooterscore.setTeamyid(dTeam.getYid());
							mShooterscoreMapper.insertSelective(mShooterscore);
							System.out.println("新增联赛射手榜:"+mLeaguematch.getSname()+":"+dTeam.getNamecn()+":"+dPlayer.getNamecn());
						}						
					}else{
						MShooterscore mShooterscore = mShooterscores.get(0);
						mShooterscore.setGoal(RegularVerify.isNum(shooterscoreEntity.getJq())?Integer.parseInt(shooterscoreEntity.getJq()):0);
						mShooterscore.setPenalty(RegularVerify.isNum(shooterscoreEntity.getDq())?Integer.parseInt(shooterscoreEntity.getDq()):0);
						MShooterscoreExample example = new MShooterscoreExample();
						example.createCriteria().andIdEqualTo(mShooterscore.getId());
						mShooterscoreMapper.updateByExampleSelective(mShooterscore,example);
						System.out.println("更新联赛射手榜:"+mLeaguematch.getSname());
					}
				}
			}
		}
		{
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid", Context.SUMMARYSCOREDATE);
			param.put("sid",mLeaguematch.getYid().toString());
			param.put("lid",mLeaguematch.getMatchyid().toString());
			String url = Context.BASE_URL;
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(url, param);
			Interface1014 interface1014 = JsonUtils.jsonToPojo(jsonStr, Interface1014.class);
			if(ObjectHelper.isNotEmpty(interface1014)&&interface1014.getCode()==0&&ObjectHelper.isNotEmpty(interface1014.getRow())) {
				//通过联赛ID和赛季验证是否有该条统计 有就不做处理 没有则新增
				MSummaryscoreMapper mSummaryscoreMapper = RealMain.applicationContext.getBean(MSummaryscoreMapper.class);
				MSummaryscoreExample sexample = new MSummaryscoreExample();
				com.kprd.match.pojo.MSummaryscoreExample.Criteria createCriteria = sexample.createCriteria();		
				createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
				createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
				List<MSummaryscore> mSummaryscores =  mSummaryscoreMapper.selectByExample(sexample);
				if(ObjectHelper.isEmpty(mSummaryscores)){
					SummaryscoreEntity summaryscoreEntity = interface1014.getRow().get(0);
					
					MSummaryscore mSummaryscore = new MSummaryscore();
					mSummaryscore.setAttawaystrongest(summaryscoreEntity.getGja());
					mSummaryscore.setAttawayweakest(summaryscoreEntity.getBadgja());
					
					mSummaryscore.setAtthomestrongest(summaryscoreEntity.getGjh());
					mSummaryscore.setAtthomeweakest(summaryscoreEntity.getBadgjh());
					mSummaryscore.setAttstrongest(summaryscoreEntity.getGjall());
					mSummaryscore.setAttweakest(summaryscoreEntity.getBadgjall());
					mSummaryscore.setAvgawaygoal(RegularVerify.isReal(summaryscoreEntity.getAvga())?new BigDecimal(summaryscoreEntity.getAvga()):new BigDecimal("0"));
					mSummaryscore.setAvggoal(RegularVerify.isReal(summaryscoreEntity.getAvgall())?new BigDecimal(summaryscoreEntity.getAvgall()):new BigDecimal("0"));
					mSummaryscore.setAvghomegoal(RegularVerify.isReal(summaryscoreEntity.getAvgh())?new BigDecimal(summaryscoreEntity.getAvgh()):new BigDecimal("0"));
					mSummaryscore.setAwaywincount(RegularVerify.isNum(summaryscoreEntity.getHa())?Integer.parseInt(summaryscoreEntity.getHa()):0);
					
					mSummaryscore.setCompcount(RegularVerify.isNum(summaryscoreEntity.getFnum())?Integer.parseInt(summaryscoreEntity.getFnum()):0);
					mSummaryscore.setCount(RegularVerify.isNum(summaryscoreEntity.getAll())?Integer.parseInt(summaryscoreEntity.getAll()):0);
					mSummaryscore.setDefawaystrongest(summaryscoreEntity.getFsa());
					mSummaryscore.setDefawayweakest(summaryscoreEntity.getBadfsa());
					mSummaryscore.setDefhomestrongest(summaryscoreEntity.getFsh());
					mSummaryscore.setDefweakest(summaryscoreEntity.getBadfsall());
					mSummaryscore.setDefstrongest(summaryscoreEntity.getFsall());
					mSummaryscore.setDrawcount(RegularVerify.isNum(summaryscoreEntity.getHd())?Integer.parseInt(summaryscoreEntity.getHd()):0);
					mSummaryscore.setHomewincount(RegularVerify.isNum(summaryscoreEntity.getHh())?Integer.parseInt(summaryscoreEntity.getHh()):0);
					mSummaryscore.setId(IDUtils.createUUId());
					mSummaryscore.setLeaguematchid(mLeaguematch.getId());
					mSummaryscore.setLeaguematchyid(mLeaguematch.getYid());
					mSummaryscore.setMatchid(mLeaguematch.getMatchid());
					mSummaryscore.setMatchyid(mLeaguematch.getMatchyid());
					mSummaryscoreMapper.insertSelective(mSummaryscore);
					System.out.println("新增联赛汇总统计:"+mLeaguematch.getSname());
				}else{
					SummaryscoreEntity summaryscoreEntity = interface1014.getRow().get(0);
					
					MSummaryscore mSummaryscore = mSummaryscores.get(0);
					mSummaryscore.setAttawaystrongest(summaryscoreEntity.getGja());
					mSummaryscore.setAttawayweakest(summaryscoreEntity.getBadgja());
					
					mSummaryscore.setAtthomestrongest(summaryscoreEntity.getGjh());
					mSummaryscore.setAtthomeweakest(summaryscoreEntity.getBadgjh());
					mSummaryscore.setAttstrongest(summaryscoreEntity.getGjall());
					mSummaryscore.setAttweakest(summaryscoreEntity.getBadgjall());
					mSummaryscore.setAvgawaygoal(RegularVerify.isReal(summaryscoreEntity.getAvga())?new BigDecimal(summaryscoreEntity.getAvga()):new BigDecimal("0"));
					mSummaryscore.setAvggoal(RegularVerify.isReal(summaryscoreEntity.getAvgall())?new BigDecimal(summaryscoreEntity.getAvgall()):new BigDecimal("0"));
					mSummaryscore.setAvghomegoal(RegularVerify.isReal(summaryscoreEntity.getAvgh())?new BigDecimal(summaryscoreEntity.getAvgh()):new BigDecimal("0"));
					mSummaryscore.setAwaywincount(RegularVerify.isNum(summaryscoreEntity.getHa())?Integer.parseInt(summaryscoreEntity.getHa()):0);
					
					mSummaryscore.setCompcount(RegularVerify.isNum(summaryscoreEntity.getFnum())?Integer.parseInt(summaryscoreEntity.getFnum()):0);
					mSummaryscore.setCount(RegularVerify.isNum(summaryscoreEntity.getAll())?Integer.parseInt(summaryscoreEntity.getAll()):0);
					mSummaryscore.setDefawaystrongest(summaryscoreEntity.getFsa());
					mSummaryscore.setDefawayweakest(summaryscoreEntity.getBadfsa());
					mSummaryscore.setDefhomestrongest(summaryscoreEntity.getFsh());
					mSummaryscore.setDefweakest(summaryscoreEntity.getBadfsall());
					mSummaryscore.setDefstrongest(summaryscoreEntity.getFsall());
					mSummaryscore.setDrawcount(RegularVerify.isNum(summaryscoreEntity.getHd())?Integer.parseInt(summaryscoreEntity.getHd()):0);
					mSummaryscore.setHomewincount(RegularVerify.isNum(summaryscoreEntity.getHh())?Integer.parseInt(summaryscoreEntity.getHh()):0);
					MSummaryscoreExample example = new MSummaryscoreExample();
					example.createCriteria().andIdEqualTo(mSummaryscore.getId());
					mSummaryscoreMapper.updateByExampleSelective(mSummaryscore,example);
					System.out.println("更新联赛汇总统计:"+mLeaguematch.getSname());
				}
			}
		
		}
		{
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid", Context.TEANSCOREDATA);
			param.put("sid",mLeaguematch.getYid().toString());
			param.put("lid",mLeaguematch.getMatchyid().toString());
			String url = Context.BASE_URL;
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(url, param);
			Interface1007 interface1007 = JsonUtils.jsonToPojo(jsonStr, Interface1007.class);
			if(ObjectHelper.isNotEmpty(interface1007)&&interface1007.getCode()==0&&interface1007.getRow().size()>0) {
				for(TeamScoreEntity teamScoreEntity:interface1007.getRow()) {
					//验证是否有该联赛新赛季下是否有数据有则不新增 反之新增
					MTeamscoreMapper mTeamscoreMapper = RealMain.applicationContext.getBean(MTeamscoreMapper.class);
					MTeamscoreExample texample = new MTeamscoreExample();
					com.kprd.match.pojo.MTeamscoreExample.Criteria createCriteria = texample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					createCriteria.andTeamyidEqualTo(teamScoreEntity.getTid());
					List<MTeamscore> mTeamscores =  mTeamscoreMapper.selectByExample(texample);
					if(ObjectHelper.isEmpty(mTeamscores)) {//未查到该记录则新增
						//根据球队ID查询球队信息
						DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
						DTeam dTeam = dTeamMapper.selectByYId(teamScoreEntity.getTid());
						if(ObjectHelper.isNotEmpty(dTeam)) {
							MTeamscore mTeamscore = new MTeamscore();
							mTeamscore.setId(IDUtils.createUUId());
							mTeamscore.setCompall(RegularVerify.isNum(teamScoreEntity.getEnum())?Integer.parseInt(teamScoreEntity.getEnum()):0);
							mTeamscore.setCompaway(RegularVerify.isNum(teamScoreEntity.getAenum())?Integer.parseInt(teamScoreEntity.getAenum()):0);
							mTeamscore.setComphome(RegularVerify.isNum(teamScoreEntity.getHenum())?Integer.parseInt(teamScoreEntity.getHenum()):0);
							mTeamscore.setDeuceall(RegularVerify.isNum(teamScoreEntity.getD())?Integer.parseInt(teamScoreEntity.getD()):0);
							mTeamscore.setDeuceaway(RegularVerify.isNum(teamScoreEntity.getAd())?Integer.parseInt(teamScoreEntity.getAd()):0);
							mTeamscore.setDeucehome(RegularVerify.isNum(teamScoreEntity.getHd())?Integer.parseInt(teamScoreEntity.getHd()):0);
							mTeamscore.setLeaguematchid(mLeaguematch.getId());
							mTeamscore.setLeaguematchyid(mLeaguematch.getYid());
							mTeamscore.setLostall(RegularVerify.isNum(teamScoreEntity.getL())?Integer.parseInt(teamScoreEntity.getL()):0);
							mTeamscore.setLostaway(RegularVerify.isNum(teamScoreEntity.getAl())?Integer.parseInt(teamScoreEntity.getAl()):0);
							mTeamscore.setLosthome(RegularVerify.isNum(teamScoreEntity.getHl())?Integer.parseInt(teamScoreEntity.getHl()):0);
							mTeamscore.setMatchid(mLeaguematch.getMatchid());
							mTeamscore.setMatchyid(mLeaguematch.getMatchyid());
							mTeamscore.setMissall(RegularVerify.isNum(teamScoreEntity.getLoss())?Integer.parseInt(teamScoreEntity.getLoss()):0);
							mTeamscore.setMissaway(RegularVerify.isNum(teamScoreEntity.getAloss())?Integer.parseInt(teamScoreEntity.getAloss()):0);
							mTeamscore.setMisshome(RegularVerify.isNum(teamScoreEntity.getHloss())?Integer.parseInt(teamScoreEntity.getHloss()):0);
							mTeamscore.setObtainall(RegularVerify.isNum(teamScoreEntity.getGoal())?Integer.parseInt(teamScoreEntity.getGoal()):0);
							mTeamscore.setObtainaway(RegularVerify.isNum(teamScoreEntity.getAgoal())?Integer.parseInt(teamScoreEntity.getAgoal()):0);
							mTeamscore.setObtainhome(RegularVerify.isNum(teamScoreEntity.getHgoal())?Integer.parseInt(teamScoreEntity.getHgoal()):0);
							mTeamscore.setTeamid(dTeam.getId());
							mTeamscore.setTeamname(dTeam.getNamecn());
							mTeamscore.setTeamyid(dTeam.getYid());
							mTeamscore.setWinall(RegularVerify.isNum(teamScoreEntity.getW())?Integer.parseInt(teamScoreEntity.getW()):0);
							mTeamscore.setWinaway(RegularVerify.isNum(teamScoreEntity.getAw())?Integer.parseInt(teamScoreEntity.getAw()):0);
							mTeamscore.setWinhome(RegularVerify.isNum(teamScoreEntity.getHw())?Integer.parseInt(teamScoreEntity.getHw()):0);
							mTeamscore.setTeamscord(RegularVerify.isNum(teamScoreEntity.getScore())?Integer.parseInt(teamScoreEntity.getScore()):0);
							mTeamscoreMapper.insertSelective(mTeamscore);
							System.out.println("新增联赛赛季球队积分:"+mLeaguematch.getSname()+" : " +mTeamscore.getTeamname());
						}						
					}else{
						MTeamscore mTeamscore = mTeamscores.get(0);
						mTeamscore.setCompall(RegularVerify.isNum(teamScoreEntity.getEnum())?Integer.parseInt(teamScoreEntity.getEnum()):0);
						mTeamscore.setCompaway(RegularVerify.isNum(teamScoreEntity.getAenum())?Integer.parseInt(teamScoreEntity.getAenum()):0);
						mTeamscore.setComphome(RegularVerify.isNum(teamScoreEntity.getHenum())?Integer.parseInt(teamScoreEntity.getHenum()):0);
						mTeamscore.setDeuceall(RegularVerify.isNum(teamScoreEntity.getD())?Integer.parseInt(teamScoreEntity.getD()):0);
						mTeamscore.setDeuceaway(RegularVerify.isNum(teamScoreEntity.getAd())?Integer.parseInt(teamScoreEntity.getAd()):0);
						mTeamscore.setDeucehome(RegularVerify.isNum(teamScoreEntity.getHd())?Integer.parseInt(teamScoreEntity.getHd()):0);
						mTeamscore.setLostall(RegularVerify.isNum(teamScoreEntity.getL())?Integer.parseInt(teamScoreEntity.getL()):0);
						mTeamscore.setLostaway(RegularVerify.isNum(teamScoreEntity.getAl())?Integer.parseInt(teamScoreEntity.getAl()):0);
						mTeamscore.setLosthome(RegularVerify.isNum(teamScoreEntity.getHl())?Integer.parseInt(teamScoreEntity.getHl()):0);
						mTeamscore.setMissall(RegularVerify.isNum(teamScoreEntity.getLoss())?Integer.parseInt(teamScoreEntity.getLoss()):0);
						mTeamscore.setMissaway(RegularVerify.isNum(teamScoreEntity.getAloss())?Integer.parseInt(teamScoreEntity.getAloss()):0);
						mTeamscore.setMisshome(RegularVerify.isNum(teamScoreEntity.getHloss())?Integer.parseInt(teamScoreEntity.getHloss()):0);
						mTeamscore.setObtainall(RegularVerify.isNum(teamScoreEntity.getGoal())?Integer.parseInt(teamScoreEntity.getGoal()):0);
						mTeamscore.setObtainaway(RegularVerify.isNum(teamScoreEntity.getAgoal())?Integer.parseInt(teamScoreEntity.getAgoal()):0);
						mTeamscore.setObtainhome(RegularVerify.isNum(teamScoreEntity.getHgoal())?Integer.parseInt(teamScoreEntity.getHgoal()):0);
						mTeamscore.setWinall(RegularVerify.isNum(teamScoreEntity.getW())?Integer.parseInt(teamScoreEntity.getW()):0);
						mTeamscore.setWinaway(RegularVerify.isNum(teamScoreEntity.getAw())?Integer.parseInt(teamScoreEntity.getAw()):0);
						mTeamscore.setWinhome(RegularVerify.isNum(teamScoreEntity.getHw())?Integer.parseInt(teamScoreEntity.getHw()):0);
						mTeamscore.setTeamscord(RegularVerify.isNum(teamScoreEntity.getScore())?Integer.parseInt(teamScoreEntity.getScore()):0);
						MTeamscoreExample example = new MTeamscoreExample();
						example.createCriteria().andIdEqualTo(mTeamscore.getId());
						mTeamscoreMapper.updateByExampleSelective(mTeamscore,example);
						System.out.println("更新联赛赛季球队积分:"+mLeaguematch.getSname()+" : " +mTeamscore.getTeamname());
					}
				}
			}
		}
	}
	
	public static void restGameStatistics(MLeaguematch leaguematch){
		//通过联赛赛季查询对应赛事
		MGameMapper mGameMapper = RealMain.applicationContext.getBean(MGameMapper.class);
		MGameExample mGameExample = new MGameExample();				
		Criteria createCriteria = mGameExample.createCriteria();
		createCriteria.andMatchidEqualTo(leaguematch.getMatchid());
		createCriteria.andLeaguematchidEqualTo(leaguematch.getId());
		List<MGame> mGames = mGameMapper.selectByExample(mGameExample);
		for(MGame mGame:mGames) {
			//根据比赛编号验证本地数据库是否有该比赛的统计事件数据 有则跳过 没有则新增
			MTeamgamerecordMapper  mTeamgamerecordMapper= RealMain.applicationContext.getBean(MTeamgamerecordMapper.class);					
			List<MTeamgamerecord> mTeamgamerecord = mTeamgamerecordMapper.selectByGameId(mGame.getId());
			if(ObjectHelper.isEmpty(mTeamgamerecord)) {
				Map<String,String> param = new HashMap<String,String>();
				param.put("oid", Context.RECODEDATE);
				param.put("mid",mGame.getYid().toString());
				String url = Context.BASE_URL;
				String jsonStr = HttpClientUtil.doGet(url, param);
				Interface3010 interface3010 = JsonUtils.jsonToPojo(jsonStr, Interface3010.class);
				if(ObjectHelper.isNotEmpty(interface3010)&&interface3010.getCode()==0) {
					RecordEntity recordEntity = interface3010.getMatch();
					TotalEntity totalEntity = interface3010.getTotal();
					if(ObjectHelper.isNotEmpty(recordEntity)&&ObjectHelper.isNotEmpty(totalEntity)) {
						//什么总次数
						String[] totalShots =  totalEntity.getSmcu().split(",");
						if(ObjectHelper.isEmpty(totalShots)) totalShots = new String[] {"0","0"};
						String[] shotsOnTarget = totalEntity.getSzqmcs().split(",");
						if(ObjectHelper.isEmpty(shotsOnTarget)) shotsOnTarget = new String[] {"0","0"};
						String[] fouls = totalEntity.getFgcs().split(",");
						if(ObjectHelper.isEmpty(fouls)) fouls = new String[] {"0","0"};
						String[] corners = totalEntity.getJqcs().split(",");
						if(ObjectHelper.isEmpty(corners)) corners = new String[] {"0","0"};
						String[] offsides = totalEntity.getYwcs().split(",");
						if(ObjectHelper.isEmpty(offsides)) offsides = new String[] {"0","0"};
						String[] possession = totalEntity.getKqsj().split(",");
						if(ObjectHelper.isEmpty(possession)) possession = new String[] {"0","0"};
						String[] totalSaves = totalEntity.getJq().split(",");
						if(ObjectHelper.isEmpty(totalSaves)) totalSaves = new String[] {"0","0"};
						
						MTeamgamerecord hTeamgamerecord = new MTeamgamerecord();
						MTeamgamerecord aTeamgamerecord = new MTeamgamerecord();
						//主队
						hTeamgamerecord.setId(IDUtils.createUUId());
						hTeamgamerecord.setTeamid(mGame.getHomeid());
						hTeamgamerecord.setTeamname(mGame.getHomecn());
						hTeamgamerecord.setGameid(mGame.getId());
						hTeamgamerecord.setGameyid(mGame.getYid());
						hTeamgamerecord.setTotalshots(RegularVerify.isNum(totalShots[0])?Integer.parseInt(totalShots[0]):0);
						hTeamgamerecord.setShotsontarget(RegularVerify.isNum(shotsOnTarget[0])?Integer.parseInt(shotsOnTarget[0]):0);
						
						hTeamgamerecord.setFouls(RegularVerify.isNum(fouls[0])?Integer.parseInt(fouls[0]):0);
						hTeamgamerecord.setCorners(RegularVerify.isNum(corners[0])?Integer.parseInt(corners[0]):0);
						hTeamgamerecord.setOffsides(RegularVerify.isNum(offsides[0])?Integer.parseInt(offsides[0]):0);
						hTeamgamerecord.setPossession(RegularVerify.isNum(possession[0])?Integer.parseInt(possession[0]):0);
						hTeamgamerecord.setTotalsaves(RegularVerify.isNum(totalSaves[0])?Integer.parseInt(totalSaves[0]):0);
						hTeamgamerecord.setCreatetime(new Date());
						//客队
						aTeamgamerecord.setId(IDUtils.createUUId());
						aTeamgamerecord.setTeamid(mGame.getAwayid());
						aTeamgamerecord.setTeamname(mGame.getAwaycn());
						aTeamgamerecord.setGameid(mGame.getId());
						aTeamgamerecord.setGameyid(mGame.getYid());
						aTeamgamerecord.setTotalshots(RegularVerify.isNum(totalShots[1])?Integer.parseInt(totalShots[1]):0);
						aTeamgamerecord.setShotsontarget(RegularVerify.isNum(shotsOnTarget[1])?Integer.parseInt(shotsOnTarget[1]):0);
						
						aTeamgamerecord.setFouls(RegularVerify.isNum(fouls[1])?Integer.parseInt(fouls[1]):0);
						aTeamgamerecord.setCorners(RegularVerify.isNum(corners[1])?Integer.parseInt(corners[1]):0);
						aTeamgamerecord.setOffsides(RegularVerify.isNum(offsides[1])?Integer.parseInt(offsides[1]):0);
						aTeamgamerecord.setPossession(RegularVerify.isNum(possession[1])?Integer.parseInt(possession[1]):0);
						aTeamgamerecord.setTotalsaves(RegularVerify.isNum(totalSaves[1])?Integer.parseInt(totalSaves[1]):0);
						aTeamgamerecord.setCreatetime(new Date());
						
						if(ObjectHelper.isNotEmpty(interface3010.getEvent().getRow())) {				
							List<PlayerEventEntity> list = interface3010.getEvent().getRow();
							MGameeventrecordMapper mGameeventrecordMapper = RealMain.applicationContext.getBean(MGameeventrecordMapper.class);					
							for(PlayerEventEntity playerEventEntity:list) {
								if(ObjectHelper.isNotEmpty(playerEventEntity.getLa_img()))
								{
									MGameeventrecord hGameeventrecord = new MGameeventrecord();
									hGameeventrecord.setId(IDUtils.createUUId());
									hGameeventrecord.setEvent(RegularVerify.isNum(playerEventEntity.getLa_img())?Integer.parseInt(playerEventEntity.getLa_img()):0);
									hGameeventrecord.setEventtime(RegularVerify.isNum(playerEventEntity.getTm())?Integer.valueOf(playerEventEntity.getTm()):-1);
									hGameeventrecord.setPlayername(playerEventEntity.getLa());
									hGameeventrecord.setGameid(mGame.getId());
									hGameeventrecord.setGameyid(mGame.getYid());
									hGameeventrecord.setTeamid(mGame.getHomeid());
									hGameeventrecord.setTeamname(mGame.getHomecn());
									mGameeventrecordMapper.insertSelective(hGameeventrecord);
								}
								
								if(ObjectHelper.isNotEmpty(playerEventEntity.getLb_img()))
								{
									MGameeventrecord aGameeventrecord = new MGameeventrecord();
									aGameeventrecord.setId(IDUtils.createUUId());
									aGameeventrecord.setEvent(RegularVerify.isNum(playerEventEntity.getLb_img())?Integer.parseInt(playerEventEntity.getLb_img()):0);
									aGameeventrecord.setEventtime(RegularVerify.isNum(playerEventEntity.getTm())?Integer.valueOf(playerEventEntity.getTm()):-1);
									
									aGameeventrecord.setPlayername(playerEventEntity.getLb());
									aGameeventrecord.setGameid(mGame.getId());
									aGameeventrecord.setGameyid(mGame.getYid());
									aGameeventrecord.setTeamid(mGame.getAwayid());
									aGameeventrecord.setTeamname(mGame.getAwaycn());
									mGameeventrecordMapper.insertSelective(aGameeventrecord);
								}					
							}							
						}
						mTeamgamerecordMapper.insertSelective(aTeamgamerecord);
						mTeamgamerecordMapper.insertSelective(hTeamgamerecord);
						System.out.println("新增比赛球队统计：主队 "+aTeamgamerecord.getTeamname()+" 客队 "+hTeamgamerecord.getTeamname());
					}
				}
			}
		}
	}
}
