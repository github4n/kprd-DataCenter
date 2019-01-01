package com.kprd.date.history.statistics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.constant.Context;
import com.kprd.date.constant.RegularVerify;
import com.kprd.date.history.statistics.entity.Interface3010;
import com.kprd.date.history.statistics.entity.PlayerEventEntity;
import com.kprd.date.history.statistics.entity.RecordEntity;
import com.kprd.date.history.statistics.entity.TotalEntity;
import com.kprd.match.mapper.MGameMapper;
import com.kprd.match.mapper.MGameeventrecordMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.mapper.MTeamgamerecordMapper;
import com.kprd.match.pojo.MGame;
import com.kprd.match.pojo.MGameExample;
import com.kprd.match.pojo.MGameExample.Criteria;
import com.kprd.match.pojo.MGameeventrecord;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;
import com.kprd.match.pojo.MTeamgamerecord;

/***
 * 比賽賽事記錄数据更新
 * @author Administrator
 *
 */
public class RecodeData {

	public String updateMDxqscore() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
				//通过联赛赛季查询对应赛事
				MGameMapper mGameMapper = applicationContext.getBean(MGameMapper.class);
				MGameExample mGameExample = new MGameExample();				
				Criteria createCriteria = mGameExample.createCriteria();
				createCriteria.andMatchidEqualTo(mLeaguematch.getMatchid());
				createCriteria.andLeaguematchidEqualTo(mLeaguematch.getId());
				List<MGame> mGames = mGameMapper.selectByExample(mGameExample);
				for(MGame mGame:mGames) {
					//根据比赛编号验证本地数据库是否有该比赛的统计事件数据 有则跳过 没有则新增
					MTeamgamerecordMapper  mTeamgamerecordMapper= applicationContext.getBean(MTeamgamerecordMapper.class);					
					List<MTeamgamerecord> mTeamgamerecords = mTeamgamerecordMapper.selectByGameId(mGame.getId());
					for(MTeamgamerecord mTeamgamerecord : mTeamgamerecords) {
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
								String[] shotsOnTarget = totalEntity.getSzqmcs().split(",");
								String[] fouls = totalEntity.getFgcs().split(",");
								String[] corners = totalEntity.getJqcs().split(",");
								String[] offsides = totalEntity.getYwcs().split(",");
								String[] possession = totalEntity.getKqsj().split(",");
								String[] totalSaves = totalEntity.getJq().split(",");
								
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
								
								if(ObjectHelper.isNotEmpty(interface3010.getRow())) {				
									List<PlayerEventEntity> list = interface3010.getRow();
									MGameeventrecordMapper mGameeventrecordMapper = applicationContext.getBean(MGameeventrecordMapper.class);					
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
								
								row += mTeamgamerecordMapper.insertSelective(aTeamgamerecord);
								row += mTeamgamerecordMapper.insertSelective(hTeamgamerecord);
								System.out.println("新增比赛球队统计：主队 "+aTeamgamerecord.getTeamname()+" 客队 "+hTeamgamerecord.getTeamname());
							}
						}
					}
				}
			}
		} 
		return "新增完毕："+row+"条";	
	}
		
	
	public static void main(String[] args) {
		
			System.out.println(new RecodeData().updateMDxqscore());
		
	}
}
