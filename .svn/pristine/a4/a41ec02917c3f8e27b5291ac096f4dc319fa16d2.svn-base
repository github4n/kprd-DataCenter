package com.kprd.date.history.team;

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
import com.kprd.date.history.team.entity.DTeamEntity;
import com.kprd.date.history.team.entity.Interface1005;
import com.kprd.date.history.team.entity.Interface1006;
import com.kprd.date.history.team.entity.TeamEntity;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MJointeamMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MJointeam;
import com.kprd.match.pojo.MJointeamExample;
import com.kprd.match.pojo.MJointeamExample.Criteria;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;

/***
 * 球队数据更新
 * @author Administrator
 *
 */
public class TeamData {

	public String updateTeamData() throws java.text.ParseException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
				Map<String,String> param = new HashMap<String,String>();
				param.put("oid", Context.TEAMDATA);
				param.put("sid",mLeaguematch.getYid().toString());
				param.put("lid",mLeaguematch.getMatchyid().toString());
				String url = Context.BASE_URL;
				//////////////////通过 http请求有料接口//////////////////////
				String jsonStr = HttpClientUtil.doGet(url, param);
				Interface1005 interface1005 = JsonUtils.jsonToPojo(jsonStr, Interface1005.class);
				if(ObjectHelper.isNotEmpty(interface1005)&&interface1005.getCode()==0&&interface1005.getRow().size()>0) {
					for(TeamEntity teamEntity:interface1005.getRow()) {
						//验证是否有该联赛记录，没有则新增
						MJointeamMapper mJointeamMapper = applicationContext.getBean(MJointeamMapper.class);
						MJointeamExample texample = new MJointeamExample();
						Criteria createCriteria = texample.createCriteria();
						createCriteria.andTeamyidEqualTo(teamEntity.getTid());
						createCriteria.andLeaguematchyidEqualTo(mLeaguematch.getYid());
						List<MJointeam> mJointeams = mJointeamMapper.selectByExample(texample);						
						if(ObjectHelper.isEmpty(mJointeams)||(ObjectHelper.isNotEmpty(mJointeams)&&mJointeams.size()==0)) {//未查到该记录则新增
							//根据球队ID查询是否存在此球队 不存在则新增
							DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
							DTeam dTeam = dTeamMapper.selectByYId(teamEntity.getTid());
							if(ObjectHelper.isEmpty(dTeam)) {
							//新增球队详情
							Map<String,String> dparam = new HashMap<String,String>();
							dparam.put("oid", Context.DTEAMDATA);
							dparam.put("tid",teamEntity.getTid().toString());
							//////////////////通过 http请求有料接口//////////////////////
							String djsonStr = HttpClientUtil.doGet(url, dparam);
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
									row += dTeamMapper.insertSelective(dTeam);
									System.out.println("新增球队详情:"+dTeam.getNamecn());
								}							
							} 							
							//新增参赛球队
							MJointeam team = new MJointeam();
							team.setId(IDUtils.createUUId());
							team.setTeamid(dTeam.getId());
							team.setTeamyid(teamEntity.getTid());
							team.setName(teamEntity.getName());
							team.setLeaguematchid(mLeaguematch.getId());
							team.setLeaguematchyid(mLeaguematch.getYid());
							team.setInitscore(teamEntity.getInitscore());
							
							row += mJointeamMapper.insert(team);
							System.out.println("新增参赛球队:"+team.getName());
						}
					}
				}
			}
		} 
		return "新增完毕："+row+"条";	
	}
		
	
	public static void main(String[] args) {
		try {
			System.out.println(new TeamData().updateTeamData());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
}
