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
import com.kprd.date.history.statistics.entity.Interface1008;
import com.kprd.date.history.statistics.entity.Interface1009;
import com.kprd.date.history.statistics.entity.QbcscoreEntity;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MGoalscoreMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.mapper.MQbcscoreMapper;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;
import com.kprd.match.pojo.MQbcscore;
import com.kprd.match.pojo.MQbcscoreExample;
import com.kprd.match.pojo.MQbcscoreExample.Criteria;

/***
 * 联赛半全场统计数据更新
 * @author Administrator
 *
 */
public class QbcscoreData {

	public String updateQbcscoreData() throws java.text.ParseException {
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
				Interface1008 interface1008 = JsonUtils.jsonToPojo(jsonStr, Interface1008.class);
				if(ObjectHelper.isNotEmpty(interface1008)&&interface1008.getCode()==0&&interface1008.getRow().size()>0) {
					for(QbcscoreEntity qbcscoreEntity:interface1008.getRow()) {
						
						MQbcscoreMapper mQbcscoreMapper = applicationContext.getBean(MQbcscoreMapper.class);
						MQbcscoreExample qexample = new MQbcscoreExample();
						Criteria createCriteria = qexample.createCriteria();		
						createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
						createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
						List<MQbcscore> mQbcscores =  mQbcscoreMapper.selectByExample(qexample);
						if(ObjectHelper.isEmpty(mQbcscores)||(ObjectHelper.isNotEmpty(mQbcscores)&&mQbcscores.size()==0)) {//未查到该记录则新增
							//根据球队ID查询球队信息
							DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
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
								
								row += mQbcscoreMapper.insertSelective(mQbcscore);
								System.out.println("新增联赛赛季球队总进球数:"+mLeaguematch.getSname()+" : " +mQbcscore.getTeamname());
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
			System.out.println(new QbcscoreData().updateQbcscoreData());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
