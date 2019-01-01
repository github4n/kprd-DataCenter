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
import com.kprd.date.history.statistics.entity.Interface1010;
import com.kprd.date.history.statistics.entity.MDxqscoreEntity;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MDxqscoreMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MDxqscore;
import com.kprd.match.pojo.MDxqscoreExample;
import com.kprd.match.pojo.MDxqscoreExample.Criteria;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;

/***
 * 赛季大小球数据更新
 * @author Administrator
 *
 */
public class MDxqscoreData {

	public String updateMDxqscore() throws java.text.ParseException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
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
						MDxqscoreMapper mDxqscoreMapper = applicationContext.getBean(MDxqscoreMapper.class);
						MDxqscoreExample dexample = new MDxqscoreExample();
						Criteria createCriteria = dexample.createCriteria();		
						createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
						createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
						List<MDxqscore> mDxqscores =  mDxqscoreMapper.selectByExample(dexample);
						if(ObjectHelper.isEmpty(mDxqscores)||(ObjectHelper.isNotEmpty(mDxqscores)&&mDxqscores.size()==0)) {//未查到该记录则新增
							//根据球队ID查询球队信息
							DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
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
								row += mDxqscoreMapper.insertSelective(mDxqscore);
								System.out.println("新增联赛入球单双大小统计:"+mLeaguematch.getSname()+" : " +mDxqscore.getTeamname());
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
			System.out.println(new MDxqscoreData().updateMDxqscore());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
