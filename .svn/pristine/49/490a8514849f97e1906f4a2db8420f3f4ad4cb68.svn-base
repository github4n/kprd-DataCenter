package com.kprd.date.history.plaryer;

import java.io.FileWriter;
import java.io.IOException;
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
import com.kprd.date.history.plaryer.entity.DPlayerEntity;
import com.kprd.date.history.plaryer.entity.Interface1018;
import com.kprd.date.history.plaryer.entity.Interface1019;
import com.kprd.date.history.plaryer.entity.Interface1020;
import com.kprd.date.history.plaryer.entity.Interface1021;
import com.kprd.date.history.plaryer.entity.PlayerEntity;
import com.kprd.dic.mapper.DPlayerMapper;
import com.kprd.dic.pojo.DPlayer;
import com.kprd.match.mapper.MJoinplayerMapper;
import com.kprd.match.mapper.MJointeamMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MJoinplayer;
import com.kprd.match.pojo.MJoinplayerExample;
import com.kprd.match.pojo.MJoinplayerExample.Criteria;
import com.kprd.match.pojo.MJointeam;
import com.kprd.match.pojo.MJointeamExample;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;

/***
 * 球员数据更新
 * @author Administrator
 *
 */
public class PlaryerData {

	public String updateTeamData() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		StringBuffer sb = new StringBuffer();
		try {
			FileWriter fw = new FileWriter("D:/error.txt");		
			if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
				for(MLeaguematch mLeaguematch:mLeaguematchs) {
					//查询参赛球队列表
					MJointeamMapper mJointeamMapper = applicationContext.getBean(MJointeamMapper.class);
					MJointeamExample mexample = new MJointeamExample();
					com.kprd.match.pojo.MJointeamExample.Criteria createCriteria1 = mexample.createCriteria();
					createCriteria1.andLeaguematchidEqualTo(mLeaguematch.getId());
					List<MJointeam> mJointeams = mJointeamMapper.selectByExample(mexample);
					if(ObjectHelper.isNotEmpty(mJointeams)&&mJointeams.size()>0) {
						for(MJointeam mJointeam:mJointeams) {
							System.out.println(mJointeam.getTeamyid()+"~~~~~~~~~~~~~~~~");
							Map<String,String> param = new HashMap<String,String>();
							param.put("oid",Context.PLAYERDATA);
							param.put("tid",mJointeam.getTeamyid().toString());
							String url = Context.BASE_URL;
							String jsonStr = HttpClientUtil.doGet(url, param);
							System.out.println(mJointeam.getTeamyid()+"=================");
							System.out.println(jsonStr);
							Interface1018 interface1018 = JsonUtils.jsonToPojo(jsonStr, Interface1018.class);
							if(ObjectHelper.isNotEmpty(interface1018)&&interface1018.getCode()==0&&interface1018.getRow().size()>0) {
								for(PlayerEntity playerEntity:interface1018.getRow()) {
									//验证是否有该参赛球员 没有则新增
									MJoinplayerMapper mJoinplayerMapper = applicationContext.getBean(MJoinplayerMapper.class);
									MJoinplayerExample pexample = new MJoinplayerExample();
									Criteria createCriteria = pexample.createCriteria();
									createCriteria.andPlayeryidEqualTo(playerEntity.getPid());
									createCriteria.andLeaguematchyidEqualTo(mJointeam.getLeaguematchyid());
									createCriteria.andTeamyidEqualTo(mJointeam.getTeamyid());
									List<MJoinplayer> selectByExamples = mJoinplayerMapper.selectByExample(pexample);
									if(ObjectHelper.isEmpty(selectByExamples)||(ObjectHelper.isNotEmpty(selectByExamples)&&selectByExamples.size()==0)) {
									
										//根据接口返回的球员信息去查询本地数据库是否存在该球员如果没有则新增
										DPlayerMapper dPlayerMapper = applicationContext.getBean(DPlayerMapper.class);
										DPlayer dPlayer = dPlayerMapper.selectByYId(playerEntity.getPid());
										if(ObjectHelper.isEmpty(dPlayer)) {
											//新增球员详情
											Map<String,String> dparam = new HashMap<String,String>();
											dparam.put("oid", Context.DPLAYERDATA);
											dparam.put("pid",playerEntity.getPid().toString());
											String pjsonStr = HttpClientUtil.doGet(url, dparam);	
											System.out.println(playerEntity.getPid()+"......");
											System.out.println(pjsonStr);
											Interface1019 interface1019 = null;
											if(!pjsonStr.contains("code:CY02")) {
												if(pjsonStr.contains("\"row\":[{\"")) {
													interface1019 = JsonUtils.jsonToPojo(pjsonStr, Interface1021.class);
												} else {
													interface1019 = JsonUtils.jsonToPojo(pjsonStr, Interface1020.class);
												} 																							
											} else {
												System.err.println("出错了");
												sb.append(playerEntity.getPid()+"\n");
												break;						
											}
											if(ObjectHelper.isNotEmpty(interface1019)&&interface1019.getCode()==0) {
												DPlayerEntity dPlayerEntity = interface1019.getPlayer();
												
												//创建球员详情
												dPlayer = new DPlayer();
												dPlayer.setBfteam(dPlayerEntity.getBfteam());
												dPlayer.setNowteam(dPlayerEntity.getNowteam());
												dPlayer.setOvteam(dPlayerEntity.getOvteam());
												dPlayer.setBirthday(dPlayerEntity.getBir());
												dPlayer.setHeight(ObjectHelper.isNotEmpty(dPlayerEntity.getH())?Integer.parseInt(dPlayerEntity.getH()):null);
												dPlayer.setId(IDUtils.createUUId());
												dPlayer.setIntroduce(dPlayerEntity.getInfo());
												dPlayer.setJointime(dPlayerEntity.getAddtm());
												dPlayer.setNamecn(dPlayerEntity.getPname());				
												dPlayer.setNameen(dPlayerEntity.getEnname());
												dPlayer.setNationality(dPlayerEntity.getNa());
												dPlayer.setNumber(dPlayerEntity.getNumber());
												dPlayer.setPhoto(dPlayerEntity.getLogo());
												dPlayer.setPoint(dPlayerEntity.getPoint());
												dPlayer.setWeight(ObjectHelper.isNotEmpty(dPlayerEntity.getW())?Integer.parseInt(dPlayerEntity.getW()):null);
												dPlayer.setYid(dPlayerEntity.getPid());
												row += dPlayerMapper.insertSelective(dPlayer);
												System.out.println("新增球员详情:"+dPlayer.getNamecn());
											}
										}
										//新增参赛球员
										MJoinplayer mJoinplayer = new MJoinplayer();
										mJoinplayer.setId(IDUtils.createUUId());
										mJoinplayer.setLeaguematchid(mJointeam.getLeaguematchid());
										mJoinplayer.setLeaguematchyid(mJointeam.getLeaguematchyid());
										mJoinplayer.setName(playerEntity.getPname());
										mJoinplayer.setNumber(playerEntity.getNumber());
										mJoinplayer.setPlayerid(dPlayer.getId());
										mJoinplayer.setPlayeryid(playerEntity.getPid());
										mJoinplayer.setPoint(playerEntity.getPoint());				
										mJoinplayer.setTeamid(mJointeam.getTeamid());
										mJoinplayer.setTeamyid(mJointeam.getTeamyid());
										
										row += mJoinplayerMapper.insertSelective(mJoinplayer);
										System.out.println("新增参赛球员:"+mJoinplayer.getName());
									}
								}
							}
						}
					}
				}
			}
			fw.write(sb.toString());
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "新增完毕："+row+"条";	
	}
	
	public static void main(String[] args) {
		System.out.println(new PlaryerData().updateTeamData());
	}
}
