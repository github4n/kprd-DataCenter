package com.kprd.date.history.odds;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.date.constant.Context;
import com.kprd.date.constant.RegularVerify;
import com.kprd.date.history.odds.entity.Entity201X;
import com.kprd.date.history.odds.entity.Interface201X;
import com.kprd.dic.mapper.DGamingcompanyMapper;
import com.kprd.dic.mapper.DMatchMapper;
import com.kprd.dic.pojo.DGamingcompany;
import com.kprd.dic.pojo.DGamingcompanyExample;
import com.kprd.dic.pojo.DMatch;
import com.kprd.dic.pojo.DMatchExample;
import com.kprd.match.mapper.MGameMapper;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MGame;
import com.kprd.match.pojo.MGameExample;
import com.kprd.match.pojo.MLeaguematch;
import com.kprd.match.pojo.MLeaguematchExample;
import com.kprd.odds.mapper.LBodMapper;
import com.kprd.odds.mapper.LBqcMapper;
import com.kprd.odds.mapper.LDaxMapper;
import com.kprd.odds.mapper.LOupMapper;
import com.kprd.odds.mapper.LYapMapper;
import com.kprd.odds.mapper.LZjqMapper;
import com.kprd.odds.pojo.LBod;
import com.kprd.odds.pojo.LBodExample;
import com.kprd.odds.pojo.LBqc;
import com.kprd.odds.pojo.LBqcExample;
import com.kprd.odds.pojo.LDax;
import com.kprd.odds.pojo.LDaxExample;
import com.kprd.odds.pojo.LOup;
import com.kprd.odds.pojo.LOupExample;
import com.kprd.odds.pojo.LYap;
import com.kprd.odds.pojo.LYapExample;
import com.kprd.odds.pojo.LZjq;
import com.kprd.odds.pojo.LZjqExample;


/***
 * 联赛列表数据更新
 * @author Administrator
 *
 */
public class OddsData {
	
	
	public String insertNationalData(){
		@SuppressWarnings("resource")
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		DGamingcompanyMapper dGamingcompanyMapper = applicationContext.getBean(DGamingcompanyMapper.class);
		DMatchMapper dMatchMapper = applicationContext.getBean(DMatchMapper.class);
		MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
		MGameMapper mGameMapper = applicationContext.getBean(MGameMapper.class);
		LBodMapper lBodMapper = applicationContext.getBean(LBodMapper.class);
		LBqcMapper lBqcMapper = applicationContext.getBean(LBqcMapper.class);
		LDaxMapper lDaxMapper = applicationContext.getBean(LDaxMapper.class);
		LOupMapper lOupMapper = applicationContext.getBean(LOupMapper.class);
		LYapMapper lYapMapper = applicationContext.getBean(LYapMapper.class);
		LZjqMapper lZjqMapper = applicationContext.getBean(LZjqMapper.class);
		List<DMatch> dMatchs = dMatchMapper.selectByExample(new DMatchExample());
		List<DGamingcompany> dGamingcompanys = dGamingcompanyMapper.selectByExample(new DGamingcompanyExample());
		for(DMatch dMatch : dMatchs){//联赛级循环
			MLeaguematchExample leaguematchExample =  new MLeaguematchExample();
			leaguematchExample.createCriteria().andMatchidEqualTo(dMatch.getId());
			List<MLeaguematch> mLeaguematchs = mLeaguematchMapper.selectByExample(leaguematchExample);
			for(MLeaguematch leaguematch : mLeaguematchs){//赛季循环
				MGameExample gameExample = new MGameExample();
				gameExample.createCriteria().andLeaguematchidEqualTo(leaguematch.getId());
				List<MGame> games = mGameMapper.selectByExample(gameExample);
				for(MGame game : games){//比赛循环
					Map<String,String> param = new HashMap<String,String>();
					param.put("mid", game.getYid().toString());
					//////////////////通过 http请求有料接口//////////////////////
					for(DGamingcompany dGamingcompany : dGamingcompanys){
						try{
							//**获取欧赔数据**//
							param.put("comid", dGamingcompany.getYid().toString());
							param.put("oid", "2013");
							String jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							Interface201X interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							if(interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									Date update = null;
									LOupExample lOupExample = new LOupExample();
									com.kprd.odds.pojo.LOupExample.Criteria criteria = lOupExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									try{
										update = new Date(Long.valueOf(entity201x.getTime())*1000);
										criteria.andUpdatetimeEqualTo(update);
									}catch(Exception e){}
									List<LOup> oups = lOupMapper.selectByExample(lOupExample);
									if(oups.size() == 0){//重复判断
										LOup oup = new LOup();
										oup.setId(IDUtils.createUUId());
										oup.setGameid(game.getId());
										oup.setCompanyid(dGamingcompany.getId());
										oup.setGameyid(game.getYid());
										oup.setCompanyyid(dGamingcompany.getYid());
										oup.setCompanyname(dGamingcompany.getName());
										oup.setCompanytype(dGamingcompany.getType());
										oup.setWinodds(RegularVerify.isReal(entity201x.getOh())?new BigDecimal(entity201x.getOh()):new BigDecimal(0));
										oup.setDeuceodds(RegularVerify.isReal(entity201x.getOd())?new BigDecimal(entity201x.getOd()):new BigDecimal(0));
										oup.setLostodds(RegularVerify.isReal(entity201x.getOa())?new BigDecimal(entity201x.getOa()):new BigDecimal(0));
										oup.setUpdatetime(update);
										oup.setClasses(entity201x.getType()=="0"?0:1);
										lOupMapper.insertSelective(oup);
									}
								}
							}
							
							//**获取亚赔数据**//
							param.put("oid", "2013");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							if(interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									Date update = null;
									LYapExample lYapExample = new LYapExample();
									com.kprd.odds.pojo.LYapExample.Criteria criteria = lYapExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									try{
										update = new Date(Long.valueOf(entity201x.getTime())*1000);
										criteria.andUpdatetimeEqualTo(update);
									}catch(Exception e){}
									List<LYap> oups = lYapMapper.selectByExample(lYapExample);
									if(oups.size() == 0){//重复判断
										LYap yap = new LYap();
										yap.setId(IDUtils.createUUId());
										yap.setGameid(game.getId());
										yap.setCompanyid(dGamingcompany.getId());
										yap.setGameyid(game.getYid());
										yap.setCompanyyid(dGamingcompany.getYid());
										yap.setCompanyname(dGamingcompany.getName());
										yap.setCompanytype(dGamingcompany.getType());
										yap.setHomelevel(RegularVerify.isReal(entity201x.getAb())?new BigDecimal(entity201x.getAb()):new BigDecimal(0));
										yap.setBet(entity201x.getBet());
										yap.setAwaylevel(RegularVerify.isReal(entity201x.getBe())?new BigDecimal(entity201x.getBe()):new BigDecimal(0));
										yap.setUpdatetime(update);
										yap.setClasses(entity201x.getType()=="0"?0:1);
										lYapMapper.insertSelective(yap);
									}
								}
							}
							
							//**获取大小球数据**//
							param.put("oid", "2015");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							if(interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									Date update = null;
									LDaxExample lDaxExample = new LDaxExample();
									com.kprd.odds.pojo.LDaxExample.Criteria criteria = lDaxExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									try{
										update = new Date(Long.valueOf(entity201x.getTime())*1000);
										criteria.andUpdatetimeEqualTo(update);
									}catch(Exception e){}
									List<LDax> daxs = lDaxMapper.selectByExample(lDaxExample);
									if(daxs.size() == 0){//重复判断
										LDax dax = new LDax();
										dax.setId(IDUtils.createUUId());
										dax.setGameid(game.getId());
										dax.setCompanyid(dGamingcompany.getId());
										dax.setGameyid(game.getYid());
										dax.setCompanyyid(dGamingcompany.getYid());
										dax.setCompanyname(dGamingcompany.getName());
										dax.setCompanytype(dGamingcompany.getType());
										dax.setHomelevel(RegularVerify.isReal(entity201x.getUp())?new BigDecimal(entity201x.getUp()):new BigDecimal(0));
										dax.setBet(entity201x.getBet());
										dax.setAwaylevel(RegularVerify.isReal(entity201x.getLow())?new BigDecimal(entity201x.getLow()):new BigDecimal(0));
										dax.setUpdatetime(update);
										dax.setClasses(entity201x.getType()=="0"?0:1);
										lDaxMapper.insertSelective(dax);
									}
								}
							}
							
							//**获取波胆数据**//
							param.put("oid", "2016");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							if(interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									Date update = null;
									LBodExample lBodExample = new LBodExample();
									com.kprd.odds.pojo.LBodExample.Criteria criteria = lBodExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									try{
										update = new Date(Long.valueOf(entity201x.getTime())*1000);
										criteria.andUpdatetimeEqualTo(update);
									}catch(Exception e){}
									List<LBod> bods = lBodMapper.selectByExample(lBodExample);
									if(bods.size() == 0){//重复判断
										LBod bod = new LBod();
										bod.setId(IDUtils.createUUId());
										bod.setGameid(game.getId());
										bod.setCompanyid(dGamingcompany.getId());
										bod.setGameyid(game.getYid());
										bod.setCompanyyid(dGamingcompany.getYid());
										bod.setCompanyname(dGamingcompany.getName());
										bod.setCompanytype(dGamingcompany.getType());
										bod.setOdds10(RegularVerify.isReal(entity201x.getH1())?new BigDecimal(entity201x.getH1()):new BigDecimal(0));
										bod.setOdds20(RegularVerify.isReal(entity201x.getH2())?new BigDecimal(entity201x.getH2()):new BigDecimal(0));
										bod.setOdds21(RegularVerify.isReal(entity201x.getH3())?new BigDecimal(entity201x.getH3()):new BigDecimal(0));
										bod.setOdds30(RegularVerify.isReal(entity201x.getH4())?new BigDecimal(entity201x.getH4()):new BigDecimal(0));
										bod.setOdds31(RegularVerify.isReal(entity201x.getH5())?new BigDecimal(entity201x.getH5()):new BigDecimal(0));
										bod.setOdds32(RegularVerify.isReal(entity201x.getH6())?new BigDecimal(entity201x.getH6()):new BigDecimal(0));
										bod.setOdds40(RegularVerify.isReal(entity201x.getH7())?new BigDecimal(entity201x.getH7()):new BigDecimal(0));
										bod.setOdds41(RegularVerify.isReal(entity201x.getH8())?new BigDecimal(entity201x.getH8()):new BigDecimal(0));
										bod.setOdds42(RegularVerify.isReal(entity201x.getH9())?new BigDecimal(entity201x.getH9()):new BigDecimal(0));
										bod.setOdds43(RegularVerify.isReal(entity201x.getH10())?new BigDecimal(entity201x.getH10()):new BigDecimal(0));
										bod.setOdds00(RegularVerify.isReal(entity201x.getO11())?new BigDecimal(entity201x.getO11()):new BigDecimal(0));
										bod.setOdds11(RegularVerify.isReal(entity201x.getO12())?new BigDecimal(entity201x.getO12()):new BigDecimal(0));
										bod.setOdds22(RegularVerify.isReal(entity201x.getO13())?new BigDecimal(entity201x.getO13()):new BigDecimal(0));
										bod.setOdds33(RegularVerify.isReal(entity201x.getO14())?new BigDecimal(entity201x.getO14()):new BigDecimal(0));
										bod.setOdds44(RegularVerify.isReal(entity201x.getO15())?new BigDecimal(entity201x.getO15()):new BigDecimal(0));
										bod.setOther(RegularVerify.isReal(entity201x.getO16())?new BigDecimal(entity201x.getO16()):new BigDecimal(0));
										bod.setOdds01(RegularVerify.isReal(entity201x.getA1())?new BigDecimal(entity201x.getA1()):new BigDecimal(0));
										bod.setOdds02(RegularVerify.isReal(entity201x.getA2())?new BigDecimal(entity201x.getA2()):new BigDecimal(0));
										bod.setOdds12(RegularVerify.isReal(entity201x.getA3())?new BigDecimal(entity201x.getA3()):new BigDecimal(0));
										bod.setOdds03(RegularVerify.isReal(entity201x.getA4())?new BigDecimal(entity201x.getA4()):new BigDecimal(0));
										bod.setOdds13(RegularVerify.isReal(entity201x.getA5())?new BigDecimal(entity201x.getA5()):new BigDecimal(0));
										bod.setOdds23(RegularVerify.isReal(entity201x.getA6())?new BigDecimal(entity201x.getA6()):new BigDecimal(0));
										bod.setOdds04(RegularVerify.isReal(entity201x.getA7())?new BigDecimal(entity201x.getA7()):new BigDecimal(0));
										bod.setOdds14(RegularVerify.isReal(entity201x.getA8())?new BigDecimal(entity201x.getA8()):new BigDecimal(0));
										bod.setOdds24(RegularVerify.isReal(entity201x.getA9())?new BigDecimal(entity201x.getA9()):new BigDecimal(0));
										bod.setOdds34(RegularVerify.isReal(entity201x.getA10())?new BigDecimal(entity201x.getA10()):new BigDecimal(0));
										bod.setUpdatetime(update);
										bod.setClasses(entity201x.getType()=="0"?0:1);
										lBodMapper.insertSelective(bod);
									}
								}
							}
							
							//**获取半全场数据**//
							param.put("oid", "2019");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							if(interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									Date update = null;
									LBqcExample lBqcExample = new LBqcExample();
									com.kprd.odds.pojo.LBqcExample.Criteria criteria = lBqcExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									try{
										update = new Date(Long.valueOf(entity201x.getTime())*1000);
										criteria.andUpdatetimeEqualTo(update);
									}catch(Exception e){}
									List<LBqc> bqcs = lBqcMapper.selectByExample(lBqcExample);
									if(bqcs.size() == 0){//重复判断
										LBqc bqc = new LBqc();
										bqc.setId(IDUtils.createUUId());
										bqc.setGameid(game.getId());
										bqc.setCompanyid(dGamingcompany.getId());
										bqc.setGameyid(game.getYid());
										bqc.setCompanyyid(dGamingcompany.getYid());
										bqc.setCompanyname(dGamingcompany.getName());
										bqc.setCompanytype(dGamingcompany.getType());
										bqc.setSsodds(RegularVerify.isReal(entity201x.getHh())?new BigDecimal(entity201x.getHh()):new BigDecimal(0));
										bqc.setSpodds(RegularVerify.isReal(entity201x.getHd())?new BigDecimal(entity201x.getHd()):new BigDecimal(0));
										bqc.setSfodds(RegularVerify.isReal(entity201x.getHa())?new BigDecimal(entity201x.getHa()):new BigDecimal(0));
										bqc.setPsodds(RegularVerify.isReal(entity201x.getDh())?new BigDecimal(entity201x.getDh()):new BigDecimal(0));
										bqc.setPpodds(RegularVerify.isReal(entity201x.getDd())?new BigDecimal(entity201x.getDd()):new BigDecimal(0));
										bqc.setPfodds(RegularVerify.isReal(entity201x.getDa())?new BigDecimal(entity201x.getDa()):new BigDecimal(0));
										bqc.setFsodds(RegularVerify.isReal(entity201x.getAh())?new BigDecimal(entity201x.getAh()):new BigDecimal(0));
										bqc.setFpodds(RegularVerify.isReal(entity201x.getAd())?new BigDecimal(entity201x.getAd()):new BigDecimal(0));
										bqc.setFfodds(RegularVerify.isReal(entity201x.getAa())?new BigDecimal(entity201x.getAa()):new BigDecimal(0));
										bqc.setUpdatetime(update);
										bqc.setClasses(entity201x.getType()=="0"?0:1);
										lBqcMapper.insertSelective(bqc);
									}
								}
							}
							
							//**获取球队入球数数据**//
							param.put("oid", "2025");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							if(interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									Date update = null;
									LZjqExample lZjqExample = new LZjqExample();
									com.kprd.odds.pojo.LZjqExample.Criteria criteria = lZjqExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									try{
										update = new Date(Long.valueOf(entity201x.getTime())*1000);
										criteria.andUpdatetimeEqualTo(update);
									}catch(Exception e){}
									List<LZjq> zjqs = lZjqMapper.selectByExample(lZjqExample);
									if(zjqs.size() == 0){//重复判断
										LZjq zjq = new LZjq();
										zjq.setId(IDUtils.createUUId());
										zjq.setGameid(game.getId());
										zjq.setCompanyid(dGamingcompany.getId());
										zjq.setGameyid(game.getYid());
										zjq.setCompanyyid(dGamingcompany.getYid());
										zjq.setCompanyname(dGamingcompany.getName());
										zjq.setCompanytype(dGamingcompany.getType());
										zjq.setOdds0(RegularVerify.isReal(entity201x.getG01())?new BigDecimal(entity201x.getG01()):new BigDecimal(0));
										zjq.setOdds1(zjq.getOdds0());
										zjq.setOdds2(RegularVerify.isReal(entity201x.getG23())?new BigDecimal(entity201x.getG23()):new BigDecimal(0));
										zjq.setOdds3(zjq.getOdds2());
										zjq.setOdds4(RegularVerify.isReal(entity201x.getG46())?new BigDecimal(entity201x.getG46()):new BigDecimal(0));
										zjq.setOdds5(zjq.getOdds4());
										zjq.setOdds6(zjq.getOdds4());
										zjq.setOdds7(RegularVerify.isReal(entity201x.getG7())?new BigDecimal(entity201x.getG7()):new BigDecimal(0));
										zjq.setUpdatetime(update);
										zjq.setClasses(entity201x.getType()=="0"?0:1);
										lZjqMapper.insertSelective(zjq);
									}
								}
							}
						}catch(Exception e){
							System.out.println(dGamingcompany.getYid()+"--"+game.getYid());
							continue;
						}
					}
				}
			}
		}
		return "完成";
	}

	public static void main(String[] args) {
		System.out.println(new OddsData().insertNationalData());
	}
}
