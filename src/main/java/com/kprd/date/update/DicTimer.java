package com.kprd.date.update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.constant.Context;
import com.kprd.date.constant.HttpClientInterface;
import com.kprd.date.constant.RegularVerify;
import com.kprd.date.history.game.entity.GameDEntity;
import com.kprd.date.history.game.entity.GameEntity;
import com.kprd.date.history.game.entity.Interface1004;
import com.kprd.date.history.game.entity.Interface1022;
import com.kprd.date.history.leaguematch.entity.Interface1003;
import com.kprd.date.history.leaguematch.entity.LeaguematchEntity;
import com.kprd.date.history.match.entity.Interface1002;
import com.kprd.date.history.match.entity.MatchEntity;
import com.kprd.date.history.plaryer.entity.DPlayerEntity;
import com.kprd.date.history.plaryer.entity.Interface1018;
import com.kprd.date.history.plaryer.entity.Interface1019;
import com.kprd.date.history.plaryer.entity.PlayerEntity;
import com.kprd.date.history.statistics.entity.FirstscoreEntity;
import com.kprd.date.history.statistics.entity.GoalscoreEntity;
import com.kprd.date.history.statistics.entity.Interface1007;
import com.kprd.date.history.statistics.entity.Interface1008;
import com.kprd.date.history.statistics.entity.Interface1009;
import com.kprd.date.history.statistics.entity.Interface1010;
import com.kprd.date.history.statistics.entity.Interface1011;
import com.kprd.date.history.statistics.entity.Interface1012;
import com.kprd.date.history.statistics.entity.Interface1014;
import com.kprd.date.history.statistics.entity.MDxqscoreEntity;
import com.kprd.date.history.statistics.entity.QbcscoreEntity;
import com.kprd.date.history.statistics.entity.ShooterscoreEntity;
import com.kprd.date.history.statistics.entity.SummaryscoreEntity;
import com.kprd.date.history.statistics.entity.TeamScoreEntity;
import com.kprd.date.history.team.entity.DTeamEntity;
import com.kprd.date.history.team.entity.Interface1005;
import com.kprd.date.history.team.entity.Interface1006;
import com.kprd.date.history.team.entity.TeamEntity;
import com.kprd.dic.mapper.DMatchMapper;
import com.kprd.dic.mapper.DNationalMapper;
import com.kprd.dic.mapper.DPlayerMapper;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DMatch;
import com.kprd.dic.pojo.DMatchExample;
import com.kprd.dic.pojo.DNational;
import com.kprd.dic.pojo.DPlayer;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MDxqscoreMapper;
import com.kprd.match.mapper.MFirstscoreMapper;
import com.kprd.match.mapper.MGameMapper;
import com.kprd.match.mapper.MGoalscoreMapper;
import com.kprd.match.mapper.MJoinplayerMapper;
import com.kprd.match.mapper.MJointeamMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.mapper.MQbcscoreMapper;
import com.kprd.match.mapper.MShooterscoreMapper;
import com.kprd.match.mapper.MSummaryscoreMapper;
import com.kprd.match.mapper.MTeamscoreMapper;
import com.kprd.match.pojo.MDxqscore;
import com.kprd.match.pojo.MDxqscoreExample;
import com.kprd.match.pojo.MFirstscore;
import com.kprd.match.pojo.MFirstscoreExample;
import com.kprd.match.pojo.MGame;
import com.kprd.match.pojo.MGoalscore;
import com.kprd.match.pojo.MGoalscoreExample;
import com.kprd.match.pojo.MJoinplayer;
import com.kprd.match.pojo.MJoinplayerExample;
import com.kprd.match.pojo.MJointeam;
import com.kprd.match.pojo.MJointeamExample;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MQbcscore;
import com.kprd.match.pojo.MQbcscoreExample;
import com.kprd.match.pojo.MShooterscore;
import com.kprd.match.pojo.MShooterscoreExample;
import com.kprd.match.pojo.MSummaryscore;
import com.kprd.match.pojo.MSummaryscoreExample;
import com.kprd.match.pojo.MTeamscore;
import com.kprd.match.pojo.MTeamscoreExample;

public class DicTimer {
	
	public static void execute(){
		Date today = new Date();
		DMatchMapper dMatchMapper = RealMain.applicationContext.getBean(DMatchMapper.class);
		MLeaguematchMapper mLeaguematchMapper = RealMain.applicationContext.getBean(MLeaguematchMapper.class);
		MGameMapper mGameMapper = RealMain.applicationContext.getBean(MGameMapper.class);
		
		List<DMatch> dMatchs = dMatchMapper.selectByExample(new DMatchExample());
		//新的赛季的加入，通过赛季数量的增加来判断是否有新赛季（赛季、参赛球队、参赛球员、比赛、统计）
		for(DMatch dMatch:dMatchs) {
			Map<String,String> param = new HashMap<String,String>();
			param.put("oid", Context.LEAGUEMATCHDATA);
			param.put("lid",dMatch.getYid().toString());
			//////////////////通过 http请求有料接口//////////////////////
			String jsonStr = HttpClientUtil.doGet(Context.BASE_URL, param);
			Interface1003 interface1003 = JsonUtils.jsonToPojo(jsonStr, Interface1003.class);
			if(ObjectHelper.isNotEmpty(interface1003)&&interface1003.getCode()==0&&interface1003.getRow().size()>0) {
				for(LeaguematchEntity leaguematchEntity:interface1003.getRow()) {
					//验证是否有该联赛记录，没有则新增
					MLeaguematch mLeaguematch = mLeaguematchMapper.selectByYId(leaguematchEntity.getSid());
					if(ObjectHelper.isEmpty(mLeaguematch)) {//未查到该记录则新增
						MLeaguematch leaguematch = new MLeaguematch();
						leaguematch.setId(IDUtils.createUUId());
						leaguematch.setYid(leaguematchEntity.getSid());
						leaguematch.setMatchid(dMatch.getId());	
						leaguematch.setMatchyid(dMatch.getYid());
						leaguematch.setSname(leaguematchEntity.getSname());
						mLeaguematchMapper.insertSelective(leaguematch);
						//参赛球队-球员
						restJoinTeamData(leaguematch);
						//比赛
						restGameData(leaguematch);
						//统计
						restLeagueMatchStatistics(leaguematch);
					}
				}
			}
		}
	}
	

	public static void restGameData(MLeaguematch mLeaguematch){
		Map<String,String> param = new HashMap<String,String>();
		param.put("oid", Context.GAMEDATA);
		param.put("sid",mLeaguematch.getYid().toString());
		param.put("lid",mLeaguematch.getMatchyid().toString());
		String url = Context.BASE_URL;
		//////////////////通过 http请求有料接口//////////////////////
		String jsonStr = HttpClientUtil.doGet(url, param);
		Interface1004 interface1004 = JsonUtils.jsonToPojo(jsonStr, Interface1004.class);
		if(ObjectHelper.isNotEmpty(interface1004)&&interface1004.getCode()==0&&interface1004.getRow().size()>0) {
			for(GameEntity gameEntity:interface1004.getRow()) {
				//根据比赛编号验证本地数据库是否有该比赛有则放行没有则新增
				MGameMapper mGameMapper = RealMain.applicationContext.getBean(MGameMapper.class);
				MGame mGame = mGameMapper.selectByYId(Integer.parseInt(gameEntity.getMid()));
				if(ObjectHelper.isEmpty(mGame)) {
					//通过比赛编号去查询接口比赛详情
					Map<String,String> dparam = new HashMap<String,String>();
					dparam.put("oid", Context.GAMEDDATA);
					dparam.put("mid",gameEntity.getMid().toString());
					String durl = Context.BASE_URL;
					String djsonStr = HttpClientUtil.doGet(durl, dparam);
					Interface1022 interface1022 = JsonUtils.jsonToPojo(djsonStr, Interface1022.class);
					if(ObjectHelper.isNotEmpty(interface1022)&&interface1022.getCode()==0) {
						GameDEntity gameDEntity = interface1022.getRow();
						if(ObjectHelper.isNotEmpty(gameDEntity)) {
							//通过 yid查询本地数据库球队信息
							DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
							//主队
							DTeam  hTeam  = dTeamMapper.selectByYId(Integer.parseInt(gameDEntity.getHtid()));
							//客队
							DTeam  aTeam  = dTeamMapper.selectByYId(Integer.parseInt(gameDEntity.getAtid()));
							if(ObjectHelper.isNotEmpty(hTeam)&&ObjectHelper.isNotEmpty(aTeam)) {
								//新增比赛记录
								mGame = new MGame();
								mGame.setId(IDUtils.createUUId());
								mGame.setAwaybcscore(gameDEntity.getHas());
								mGame.setAwaycn(gameDEntity.getAway());
								
								mGame.setAwaycrad(gameDEntity.getAr());
								mGame.setAwayen(gameDEntity.getAwayen());
								mGame.setAwayid(aTeam.getId());
								mGame.setAwaylogo(gameDEntity.getAwaylogo());
								mGame.setAwayscore(gameDEntity.getAs());
								mGame.setBcscore(gameDEntity.getBc());
								mGame.setCount(interface1004.getC().getCount()!=null?Integer.parseInt(interface1004.getC().getCount()):0);
								mGame.setGroupname(gameDEntity.getGn());
								
								mGame.setHomebcscore(gameDEntity.getHhs());
								mGame.setHomecn(gameDEntity.getHome());
								mGame.setHomecrad(gameDEntity.getHr());
								mGame.setHomeen(gameDEntity.getHomeen());
								
								mGame.setHomeid(hTeam.getId());
								mGame.setHomelogo(gameDEntity.getHomelogo());
								mGame.setHomescore(gameDEntity.getHs());
								mGame.setMatchid(mLeaguematch.getMatchid());
								mGame.setMatchcolor(gameDEntity.getCl());
								mGame.setMatchname(gameDEntity.getLn());
								
								mGame.setOid(gameDEntity.getOid()!=null?Integer.parseInt(gameDEntity.getOid()):null);
								mGame.setOname(gameDEntity.getOname());
								
								mGame.setRound(gameEntity.getRid()!=null?Integer.parseInt(gameEntity.getRid()):null);
								mGame.setStarttime(new Date(1000*Long.valueOf(gameDEntity.getMtime())));
								mGame.setYid(gameDEntity.getMid()!=null?Integer.parseInt(gameDEntity.getMid()):null);
								mGame.setLeaguematchid(mLeaguematch.getId());
								mGame.setQcscore(gameDEntity.getBc());
								mGame.setStatus(gameDEntity.getState()!=null?Integer.parseInt(gameDEntity.getState()):3);
								System.out.println("新增赛事:"+hTeam.getNamecn()+" VS "+aTeam.getNamecn());
							}							
						}
					}
				}
			}
		}
	
	}
	
	public static void restJoinTeamData(MLeaguematch leaguematch){
		Map<String,String> teamparam = new HashMap<String,String>();
		teamparam.put("oid", Context.TEAMDATA);
		teamparam.put("sid",leaguematch.getYid().toString());
		teamparam.put("lid",leaguematch.getMatchyid().toString());
		//////////////////通过 http请求有料接口//////////////////////
		String jsonStr1005 = HttpClientUtil.doGet(Context.BASE_URL, teamparam);
		Interface1005 interface1005 = JsonUtils.jsonToPojo(jsonStr1005, Interface1005.class);
		if(ObjectHelper.isNotEmpty(interface1005)&&interface1005.getCode()==0&&interface1005.getRow().size()>0) {
			for(TeamEntity teamEntity:interface1005.getRow()) {
				//验证是否有该联赛记录，没有则新增
				MJointeamMapper mJointeamMapper = RealMain.applicationContext.getBean(MJointeamMapper.class);
				MJointeamExample texample = new MJointeamExample();
				com.kprd.match.pojo.MJointeamExample.Criteria createCriteria = texample.createCriteria();
				createCriteria.andTeamyidEqualTo(teamEntity.getTid());
				createCriteria.andLeaguematchyidEqualTo(leaguematch.getYid());
				List<MJointeam> mJointeams = mJointeamMapper.selectByExample(texample);						
				if(ObjectHelper.isEmpty(mJointeams)||(ObjectHelper.isNotEmpty(mJointeams)&&mJointeams.size()==0)) {//未查到该记录则新增
					//根据球队ID查询是否存在此球队 不存在则新增
					DTeamMapper dTeamMapper = RealMain.applicationContext.getBean(DTeamMapper.class);
					DTeam dTeam = dTeamMapper.selectByYId(teamEntity.getTid());
					if(ObjectHelper.isEmpty(dTeam)) {
					//新增球队详情
					Map<String,String> dparam = new HashMap<String,String>();
					dparam.put("oid", Context.DTEAMDATA);
					dparam.put("tid",teamEntity.getTid().toString());
					//////////////////通过 http请求有料接口//////////////////////
					String djsonStr = HttpClientUtil.doGet(Context.BASE_URL, dparam);
					Interface1006 interface1006 = JsonUtils.jsonToPojo(djsonStr, Interface1006.class);
						if(ObjectHelper.isNotEmpty(interface1006)&&interface1006.getCode()==0) {
							DTeamEntity dTeamEntity = interface1006.getRow();
							//创建球队详情
							dTeam = new DTeam();
							dTeam.setId(IDUtils.createUUId());
							dTeam.setAddr(dTeamEntity.getAddr());
							dTeam.setBest(dTeamEntity.getBest());
							dTeam.setCapacity(dTeamEntity.getCapacity());
							dTeam.setCountry(dTeamEntity.getCountry());										
							dTeam.setCreatedate(dTeamEntity.getEdate());																
							dTeam.setEmail(dTeamEntity.getEmail());
							dTeam.setGolry(dTeamEntity.getGolry());
							dTeam.setIntroduce(dTeamEntity.getInfo());
							dTeam.setLogo(dTeamEntity.getLogo());
							dTeam.setNamecn(dTeamEntity.getCnname());
							dTeam.setNameen(dTeamEntity.getEnname());
							dTeam.setStadium(dTeamEntity.getStadium());
							dTeam.setWebside(dTeamEntity.getWeb());
							dTeam.setYid(dTeamEntity.getTid());
							System.out.println("新增球队详情:"+dTeam.getNamecn());
						}							
					} 							
					//新增参赛球队
					MJointeam team = new MJointeam();
					team.setId(IDUtils.createUUId());
					team.setTeamid(dTeam.getId());
					team.setTeamyid(teamEntity.getTid());
					team.setName(teamEntity.getName());
					team.setLeaguematchid(leaguematch.getId());
					team.setLeaguematchyid(leaguematch.getYid());
					team.setInitscore(teamEntity.getInitscore());
					System.out.println("新增参赛球队:"+team.getName());
					//参赛球员
					restJoinPlayerData(team);
				}
			}
		}
	}
	
	public static void restJoinPlayerData(MJointeam team){
		Map<String,String> playerparam = new HashMap<String,String>();
		playerparam.put("oid",Context.PLAYERDATA);
		playerparam.put("tid",team.getTeamyid().toString());
		String jsonStr1018 = HttpClientUtil.doGet(Context.BASE_URL, playerparam);
		Interface1018 interface1018 = JsonUtils.jsonToPojo(jsonStr1018, Interface1018.class);
		if(ObjectHelper.isNotEmpty(interface1018)&&interface1018.getCode()==0&&interface1018.getRow().size()>0) {
			for(PlayerEntity playerEntity:interface1018.getRow()) {
				//验证是否有该参赛球员 没有则新增
				MJoinplayerMapper mJoinplayerMapper = RealMain.applicationContext.getBean(MJoinplayerMapper.class);
				MJoinplayerExample pexample = new MJoinplayerExample();
				com.kprd.match.pojo.MJoinplayerExample.Criteria createCriteria1 = pexample.createCriteria();
				createCriteria1.andPlayeryidEqualTo(playerEntity.getPid());
				createCriteria1.andLeaguematchyidEqualTo(team.getLeaguematchyid());
				createCriteria1.andTeamyidEqualTo(team.getTeamyid());
				List<MJoinplayer> selectByExamples = mJoinplayerMapper.selectByExample(pexample);
				if(ObjectHelper.isEmpty(selectByExamples)||(ObjectHelper.isNotEmpty(selectByExamples)&&selectByExamples.size()==0)) {
				
					//根据接口返回的球员信息去查询本地数据库是否存在该球员如果没有则新增
					DPlayerMapper dPlayerMapper = RealMain.applicationContext.getBean(DPlayerMapper.class);
					DPlayer dPlayer = dPlayerMapper.selectByYId(playerEntity.getPid());
					if(ObjectHelper.isEmpty(dPlayer)) {
						//新增球员详情
						Map<String,String> dparam = new HashMap<String,String>();
						dparam.put("oid", Context.DPLAYERDATA);
						dparam.put("pid",playerEntity.getPid().toString());
						String pjsonStr = HttpClientUtil.doGet(Context.BASE_URL, dparam);
						Interface1019 interface1019 = JsonUtils.jsonToPojo(pjsonStr, Interface1019.class);
						if(ObjectHelper.isNotEmpty(interface1019)&&interface1019.getCode()==0) {
							DPlayerEntity dPlayerEntity = interface1019.getPlayer();
							//创建球员详情
							dPlayer = new DPlayer();
							dPlayer.setBfteam(dPlayerEntity.getBfteam());
							dPlayer.setNowteam(dPlayerEntity.getNowteam());
							dPlayer.setOvteam(dPlayerEntity.getOvteam());
							dPlayer.setBirthday(dPlayerEntity.getBir());
							dPlayer.setHeight(dPlayerEntity.getH()!=null?Integer.parseInt(dPlayerEntity.getH()):null);
							dPlayer.setId(IDUtils.createUUId());
							dPlayer.setIntroduce(dPlayerEntity.getInfo());
							dPlayer.setJointime(dPlayerEntity.getAddtm());
							dPlayer.setNamecn(dPlayerEntity.getPname());				
							dPlayer.setNameen(dPlayerEntity.getEnname());
							dPlayer.setNationality(dPlayerEntity.getNa());
							dPlayer.setNumber(dPlayerEntity.getNumber());
							dPlayer.setPhoto(dPlayerEntity.getLogo());
							dPlayer.setPoint(dPlayerEntity.getPoint());
							dPlayer.setWeight(dPlayerEntity.getW()!=null?Integer.parseInt(dPlayerEntity.getW()):null);
							dPlayer.setYid(dPlayerEntity.getPid());
							System.out.println("新增球员详情:"+dPlayer.getNamecn());
						}
					}
					//新增参赛球员
					MJoinplayer mJoinplayer = new MJoinplayer();
					mJoinplayer.setId(IDUtils.createUUId());
					mJoinplayer.setLeaguematchid(team.getLeaguematchid());
					mJoinplayer.setLeaguematchyid(team.getLeaguematchyid());
					mJoinplayer.setName(playerEntity.getPname());
					mJoinplayer.setNumber(playerEntity.getNumber());
					mJoinplayer.setPlayerid(dPlayer.getId());
					mJoinplayer.setPlayeryid(playerEntity.getPid());
					mJoinplayer.setPoint(playerEntity.getPoint());				
					mJoinplayer.setTeamid(team.getTeamid());
					mJoinplayer.setTeamyid(team.getTeamyid());
					System.out.println("新增参赛球员:"+mJoinplayer.getName());
				}
			}
		}
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
					List<MFirstscore> mGoalscores =  mFirstscoreMapper.selectByExample(fexample);
					if(ObjectHelper.isEmpty(mGoalscores)||(ObjectHelper.isNotEmpty(mGoalscores)&&mGoalscores.size()==0)) {//未查到该记录则新增
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
							System.out.println("新增联赛球队最先入失球数:"+mLeaguematch.getSname()+" : " +mFirstscore.getTeamname());
						}						
					}
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
			Interface1009 interface1009 = JsonUtils.jsonToPojo(jsonStr, Interface1009.class);
			if(ObjectHelper.isNotEmpty(interface1009)&&interface1009.getCode()==0&&interface1009.getRow().size()>0) {
				for(GoalscoreEntity goalscoreEntity:interface1009.getRow()) {
					//验证是否有该联赛新赛季下是否有数据有则不新增 反之新增
					MGoalscoreMapper mGoalscoreMapper = RealMain.applicationContext.getBean(MGoalscoreMapper.class);
					MGoalscoreExample gexample = new MGoalscoreExample();
					com.kprd.match.pojo.MGoalscoreExample.Criteria createCriteria = gexample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					List<MGoalscore> mGoalscores =  mGoalscoreMapper.selectByExample(gexample);
					if(ObjectHelper.isEmpty(mGoalscores)||(ObjectHelper.isNotEmpty(mGoalscores)&&mGoalscores.size()==0)) {//未查到该记录则新增
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
							
							System.out.println("新增联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mGoalscore.getTeamname());
						}						
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
					List<MDxqscore> mDxqscores =  mDxqscoreMapper.selectByExample(dexample);
					if(ObjectHelper.isEmpty(mDxqscores)||(ObjectHelper.isNotEmpty(mDxqscores)&&mDxqscores.size()==0)) {//未查到该记录则新增
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
							System.out.println("新增联赛入球单双大小统计:"+mLeaguematch.getSname()+" : " +mDxqscore.getTeamname());
						}						
					}
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
			Interface1008 interface1008 = JsonUtils.jsonToPojo(jsonStr, Interface1008.class);
			if(ObjectHelper.isNotEmpty(interface1008)&&interface1008.getCode()==0&&interface1008.getRow().size()>0) {
				for(QbcscoreEntity qbcscoreEntity:interface1008.getRow()) {
					
					MQbcscoreMapper mQbcscoreMapper = RealMain.applicationContext.getBean(MQbcscoreMapper.class);
					MQbcscoreExample qexample = new MQbcscoreExample();
					com.kprd.match.pojo.MQbcscoreExample.Criteria createCriteria = qexample.createCriteria();		
					createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
					createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
					List<MQbcscore> mQbcscores =  mQbcscoreMapper.selectByExample(qexample);
					if(ObjectHelper.isEmpty(mQbcscores)||(ObjectHelper.isNotEmpty(mQbcscores)&&mQbcscores.size()==0)) {//未查到该记录则新增
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
							
							System.out.println("新增联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mQbcscore.getTeamname());
						}						
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
					List<MShooterscore> mShooterscores =  mShooterscoreMapper.selectByExample(sexample);
					if(ObjectHelper.isEmpty(mShooterscores)||(ObjectHelper.isNotEmpty(mShooterscores)&&mShooterscores.size()==0)) {//未查到该记录则新增
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
							
							System.out.println("新增联赛射手榜:"+mLeaguematch.getSname()+":"+dTeam.getNamecn()+":"+dPlayer.getNamecn());
						}						
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
				if(ObjectHelper.isNotEmpty(mSummaryscores)&&mSummaryscores.size()==0){
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
					System.out.println("新增联赛汇总统计:"+mLeaguematch.getSname());
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
					List<MTeamscore> mTeamscores =  mTeamscoreMapper.selectByExample(texample);
					if(ObjectHelper.isEmpty(mTeamscores)||(ObjectHelper.isNotEmpty(mTeamscores)&&mTeamscores.size()==0)) {//未查到该记录则新增
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
							
							System.out.println("新增联赛赛季球队积分:"+mLeaguematch.getSname()+" : " +mTeamscore.getTeamname());
						}						
					}
				}
			}
		}
	}
	
	public static void restGameStatistics(){
		
	}
}
