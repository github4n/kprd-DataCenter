package com.kprd.date.history.statistics;

import java.math.BigDecimal;
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
import com.kprd.date.history.statistics.entity.Interface1014;
import com.kprd.date.history.statistics.entity.SummaryscoreEntity;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.mapper.MSummaryscoreMapper;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;
import com.kprd.match.pojo.MSummaryscore;
import com.kprd.match.pojo.MSummaryscoreExample;
import com.kprd.match.pojo.MSummaryscoreExample.Criteria;

/***
 * 联赛球队最先入失球统计数据更新
 * @author Administrator
 *
 */
public class SummaryscoreDate {

	public String updateSummaryscore() throws java.text.ParseException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
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
					MSummaryscoreMapper mSummaryscoreMapper = applicationContext.getBean(MSummaryscoreMapper.class);
					MSummaryscoreExample sexample = new MSummaryscoreExample();
					Criteria createCriteria = sexample.createCriteria();		
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
						row +=mSummaryscoreMapper.insertSelective(mSummaryscore);
						System.out.println("新增联赛汇总统计:"+mLeaguematch.getSname());
					}
				}
			}
		} 
		return "新增完毕："+row+"条";	
	}
		
	
	public static void main(String[] args) {
		try {
			System.out.println(new SummaryscoreDate().updateSummaryscore());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
