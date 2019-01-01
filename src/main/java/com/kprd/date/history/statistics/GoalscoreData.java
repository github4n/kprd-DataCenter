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
import com.kprd.date.history.statistics.entity.GoalscoreEntity;
import com.kprd.date.history.statistics.entity.Interface1009;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MGoalscoreMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MGoalscore;
import com.kprd.match.pojo.MGoalscoreExample;
import com.kprd.match.pojo.MGoalscoreExample.Criteria;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;

/***
 * 赛季总进球数数据更新
 * @author Administrator
 *
 */
public class GoalscoreData {

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
				Interface1009 interface1009 = JsonUtils.jsonToPojo(jsonStr, Interface1009.class);
				if(ObjectHelper.isNotEmpty(interface1009)&&interface1009.getCode()==0&&interface1009.getRow().size()>0) {
					for(GoalscoreEntity goalscoreEntity:interface1009.getRow()) {
						//验证是否有该联赛新赛季下是否有数据有则不新增 反之新增
						MGoalscoreMapper mGoalscoreMapper = applicationContext.getBean(MGoalscoreMapper.class);
						MGoalscoreExample gexample = new MGoalscoreExample();
						Criteria createCriteria = gexample.createCriteria();		
						createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
						createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
						List<MGoalscore> mGoalscores =  mGoalscoreMapper.selectByExample(gexample);
						if(ObjectHelper.isEmpty(mGoalscores)||(ObjectHelper.isNotEmpty(mGoalscores)&&mGoalscores.size()==0)) {//未查到该记录则新增
							//根据球队ID查询球队信息
							DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
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
								
								
								row += mGoalscoreMapper.insertSelective(mGoalscore);
								System.out.println("新增联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mGoalscore.getTeamname());
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
			System.out.println(new GoalscoreData().updateTeamScore());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
