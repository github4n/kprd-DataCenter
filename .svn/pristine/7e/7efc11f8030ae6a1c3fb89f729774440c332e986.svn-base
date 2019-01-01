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
import com.kprd.date.history.statistics.entity.FirstscoreEntity;
import com.kprd.date.history.statistics.entity.Interface1011;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MFirstscoreMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MFirstscore;
import com.kprd.match.pojo.MFirstscoreExample;
import com.kprd.match.pojo.MFirstscoreExample.Criteria;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;

/***
 * 联赛球队最先入失球统计数据更新
 * @author Administrator
 *
 */
public class FirstscoreDate {

	public String updateFirstscore() throws java.text.ParseException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
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
						MFirstscoreMapper mFirstscoreMapper = applicationContext.getBean(MFirstscoreMapper.class);
						MFirstscoreExample fexample = new MFirstscoreExample();
						Criteria createCriteria = fexample.createCriteria();		
						createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
						createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
						List<MFirstscore> mGoalscores =  mFirstscoreMapper.selectByExample(fexample);
						if(ObjectHelper.isEmpty(mGoalscores)||(ObjectHelper.isNotEmpty(mGoalscores)&&mGoalscores.size()==0)) {//未查到该记录则新增
							//根据球队ID查询球队信息
							DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
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
								row += mFirstscoreMapper.insertSelective(mFirstscore);
								System.out.println("新增联赛球队最先入失球数:"+mLeaguematch.getSname()+" : " +mFirstscore.getTeamname());
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
			System.out.println(new FirstscoreDate().updateFirstscore());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
