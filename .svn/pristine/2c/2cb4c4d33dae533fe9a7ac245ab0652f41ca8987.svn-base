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
import com.kprd.date.history.statistics.entity.Interface1012;
import com.kprd.date.history.statistics.entity.ShooterscoreEntity;
import com.kprd.dic.mapper.DPlayerMapper;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DPlayer;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.mapper.MShooterscoreMapper;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;
import com.kprd.match.pojo.MShooterscore;
import com.kprd.match.pojo.MShooterscoreExample;
import com.kprd.match.pojo.MShooterscoreExample.Criteria;


/***
 * 联赛球队最先入失球统计数据更新
 * @author Administrator
 *
 */
public class ShooterscoreDate {

	public String updateShooterscore() throws java.text.ParseException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
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
						MShooterscoreMapper mShooterscoreMapper = applicationContext.getBean(MShooterscoreMapper.class);
						MShooterscoreExample sexample = new MShooterscoreExample();
						Criteria createCriteria = sexample.createCriteria();		
						createCriteria.andMatchyidEqualTo(mLeaguematch.getMatchyid());
						createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
						List<MShooterscore> mShooterscores =  mShooterscoreMapper.selectByExample(sexample);
						if(ObjectHelper.isEmpty(mShooterscores)||(ObjectHelper.isNotEmpty(mShooterscores)&&mShooterscores.size()==0)) {//未查到该记录则新增
							//根据球队ID查询球队信息
							DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
							DTeam dTeam = dTeamMapper.selectByYId(shooterscoreEntity.getTid());
							DPlayerMapper dPlayerMapper = applicationContext.getBean(DPlayerMapper.class);
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
								
								row+=mShooterscoreMapper.insertSelective(mShooterscore);
								System.out.println("新增联赛射手榜:"+mLeaguematch.getSname()+":"+dTeam.getNamecn()+":"+dPlayer.getNamecn());
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
			System.out.println(new ShooterscoreDate().updateShooterscore());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
