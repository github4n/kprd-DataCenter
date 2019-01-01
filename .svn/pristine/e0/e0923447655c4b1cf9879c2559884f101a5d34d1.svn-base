package com.kprd.date.history.statistics;

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
import com.kprd.date.history.statistics.entity.Interface1007;
import com.kprd.date.history.statistics.entity.TeamScoreEntity;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.mapper.MTeamscoreMapper;

import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;
import com.kprd.match.pojo.MTeamscore;
import com.kprd.match.pojo.MTeamscoreExample;
import com.kprd.match.pojo.MTeamscoreExample.Criteria;

/***
 * 球队积分数据更新
 * @author Administrator
 *
 */
public class TeamScoreData {

	public String updateTeamScore() throws java.text.ParseException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
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
						MTeamscoreMapper mTeamscoreMapper = applicationContext.getBean(MTeamscoreMapper.class);
						MTeamscoreExample texample = new MTeamscoreExample();
						Criteria createCriteria = texample.createCriteria();		
						createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
						createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
						List<MTeamscore> mTeamscores =  mTeamscoreMapper.selectByExample(texample);
						if(ObjectHelper.isEmpty(mTeamscores)||(ObjectHelper.isNotEmpty(mTeamscores)&&mTeamscores.size()==0)) {//未查到该记录则新增
							//根据球队ID查询球队信息
							DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
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
								
								row += mTeamscoreMapper.insertSelective(mTeamscore);
								System.out.println("新增联赛赛季球队积分:"+mLeaguematch.getSname()+" : " +mTeamscore.getTeamname());
							}						
						}
					}
				}
			}
		} 
		return "新增完毕："+row+"条";	
	}
		
	
	public static void main(String[] args) {
		try {
			System.out.println(new TeamScoreData().updateTeamScore());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
