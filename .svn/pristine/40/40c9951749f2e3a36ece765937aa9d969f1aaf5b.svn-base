package com.kprd.date.history.game;



import java.text.ParseException;
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
import com.kprd.date.history.game.entity.GameDEntity;
import com.kprd.date.history.game.entity.GameEntity;
import com.kprd.date.history.game.entity.Interface1004;
import com.kprd.date.history.game.entity.Interface1022;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.match.mapper.MGameMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MGame;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;


/***
 * 比赛表
 * @author Administrator
 *
 */
public class GameData {

	public String updateGameData() throws ParseException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季列表
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MLeaguematchExample example = new MLeaguematchExample();
		List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(mLeaguematchs)&&mLeaguematchs.size()>0) {
			for(MLeaguematch mLeaguematch:mLeaguematchs) {
				Map<String,String> param = new HashMap<String,String>();
				param.put("oid", Context.GAMEDATA);
				param.put("sid",mLeaguematch.getYid().toString());
				param.put("lid",mLeaguematch.getMatchyid().toString());
				String url = Context.BASE_URL;
				//////////////////通过 http请求有料接口//////////////////////
				String jsonStr = HttpClientUtil.doGet(url, param);
				Interface1004 interface1004 = JsonUtils.jsonToPojo(jsonStr, Interface1004.class);
				if(ObjectHelper.isNotEmpty(interface1004)&&interface1004.getCode()==0&&interface1004.getRow().size()>0) {
					for(GameEntity gameEntity:interface1004.getRow()) {
						//根据比赛编号验证本地数据库是否有该比赛有则放行没有则新增
						MGameMapper mGameMapper = applicationContext.getBean(MGameMapper.class);
						MGame mGame = mGameMapper.selectByYId(Integer.parseInt(gameEntity.getMid()));
						if(ObjectHelper.isEmpty(mGame)) {
							//通过比赛编号去查询接口比赛详情
							Map<String,String> dparam = new HashMap<String,String>();
							dparam.put("oid", Context.GAMEDDATA);
							dparam.put("mid",gameEntity.getMid().toString());
							String durl = Context.BASE_URL;
							String djsonStr = HttpClientUtil.doGet(durl, dparam);
							Interface1022 interface1022 = JsonUtils.jsonToPojo(djsonStr, Interface1022.class);
							if(ObjectHelper.isNotEmpty(interface1022)&&interface1022.getCode()==0) {
								GameDEntity gameDEntity = interface1022.getRow();
								if(ObjectHelper.isNotEmpty(gameDEntity)) {
									//通过 yid查询本地数据库球队信息
									DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
									//主队
									DTeam  hTeam  = dTeamMapper.selectByYId(Integer.parseInt(gameDEntity.getHtid()));
									//客队
									DTeam  aTeam  = dTeamMapper.selectByYId(Integer.parseInt(gameDEntity.getAtid()));
									if(ObjectHelper.isNotEmpty(hTeam)&&ObjectHelper.isNotEmpty(aTeam)) {
										//新增比赛记录
										mGame = new MGame();
										mGame.setId(IDUtils.createUUId());
										mGame.setAwaybcscore(gameDEntity.getHas());
										mGame.setAwaycn(gameDEntity.getAway());
										
										mGame.setAwaycrad(gameDEntity.getAr());
										mGame.setAwayen(gameDEntity.getAwayen());
										mGame.setAwayid(aTeam.getId());
										mGame.setAwaylogo(gameDEntity.getAwaylogo());
										mGame.setAwayscore(gameDEntity.getAs());
										mGame.setBcscore(gameDEntity.getBc());
										mGame.setCount(interface1004.getC().getCount()!=null?Integer.parseInt(interface1004.getC().getCount()):0);
										mGame.setGroupname(gameDEntity.getGn());
										
										mGame.setHomebcscore(gameDEntity.getHhs());
										mGame.setHomecn(gameDEntity.getHome());
										mGame.setHomecrad(gameDEntity.getHr());
										mGame.setHomeen(gameDEntity.getHomeen());
										
										mGame.setHomeid(hTeam.getId());
										mGame.setHomelogo(gameDEntity.getHomelogo());
										mGame.setHomescore(gameDEntity.getHs());
										mGame.setMatchid(mLeaguematch.getMatchid());
										mGame.setMatchcolor(gameDEntity.getCl());
										mGame.setMatchname(gameDEntity.getLn());
										
										mGame.setOid(gameDEntity.getOid()!=null?Integer.parseInt(gameDEntity.getOid()):null);
										mGame.setOname(gameDEntity.getOname());
										
										mGame.setRound(gameEntity.getRid()!=null?Integer.parseInt(gameEntity.getRid()):null);
										mGame.setStarttime(new Date(1000*Long.valueOf(gameDEntity.getMtime())));
										mGame.setYid(gameDEntity.getMid()!=null?Integer.parseInt(gameDEntity.getMid()):null);
										mGame.setLeaguematchid(mLeaguematch.getId());
										mGame.setQcscore(gameDEntity.getBc());
										mGame.setStatus(gameDEntity.getState()!=null?Integer.parseInt(gameDEntity.getState()):3);
										row += mGameMapper.insertSelective(mGame);
										System.out.println("新增赛事:"+hTeam.getNamecn()+" VS "+aTeam.getNamecn());
									}							
								}
							}
						}
					}
				}
			}
		}
		
		return "新增完毕："+row+"条";	
	}
	
	public static void main(String[] args) {
		int row = 0;
		try {
			System.out.println(new GameData().updateGameData());
		} catch (ParseException e) {
			row+=1;
			e.printStackTrace();
		}
		System.out.println("出错:"+row);
	}
}
