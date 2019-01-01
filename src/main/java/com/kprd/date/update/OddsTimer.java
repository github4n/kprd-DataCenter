package com.kprd.date.update;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.constant.Context;
import com.kprd.date.constant.RegularVerify;
import com.kprd.date.history.odds.entity.Entity200X;
import com.kprd.date.history.odds.entity.Entity2018R;
import com.kprd.date.history.odds.entity.Entity201X;
import com.kprd.date.history.odds.entity.EntityJingCai;
import com.kprd.date.history.odds.entity.Interface200X;
import com.kprd.date.history.odds.entity.Interface2018;
import com.kprd.date.history.odds.entity.Interface201X;
import com.kprd.dic.mapper.DGamingcompanyMapper;
import com.kprd.dic.pojo.DGamingcompany;
import com.kprd.match.mapper.MGameMapper;
import com.kprd.match.pojo.MGame;
import com.kprd.odds.mapper.LBodMapper;
import com.kprd.odds.mapper.LBqcMapper;
import com.kprd.odds.mapper.LDaxMapper;
import com.kprd.odds.mapper.LJingcaiMapper;
import com.kprd.odds.mapper.LOupMapper;
import com.kprd.odds.mapper.LYapMapper;
import com.kprd.odds.mapper.LZjqMapper;
import com.kprd.odds.pojo.LBod;
import com.kprd.odds.pojo.LBodExample;
import com.kprd.odds.pojo.LBqc;
import com.kprd.odds.pojo.LBqcExample;
import com.kprd.odds.pojo.LDax;
import com.kprd.odds.pojo.LDaxExample;
import com.kprd.odds.pojo.LJingcai;
import com.kprd.odds.pojo.LJingcaiExample;
import com.kprd.odds.pojo.LOup;
import com.kprd.odds.pojo.LOupExample;
import com.kprd.odds.pojo.LYap;
import com.kprd.odds.pojo.LYapExample;
import com.kprd.odds.pojo.LZjq;
import com.kprd.odds.pojo.LZjqExample;

public class OddsTimer {
	
	public static void execute(int i){
		MGameMapper mGameMapper = RealMain.applicationContext.getBean(MGameMapper.class);
		DGamingcompanyMapper dGamingcompanyMapper = RealMain.applicationContext.getBean(DGamingcompanyMapper.class);
		LBodMapper lBodMapper = RealMain.applicationContext.getBean(LBodMapper.class);
		LBqcMapper lBqcMapper = RealMain.applicationContext.getBean(LBqcMapper.class);
		LDaxMapper lDaxMapper = RealMain.applicationContext.getBean(LDaxMapper.class);
		LOupMapper lOupMapper = RealMain.applicationContext.getBean(LOupMapper.class);
		LYapMapper lYapMapper = RealMain.applicationContext.getBean(LYapMapper.class);
		LZjqMapper lZjqMapper = RealMain.applicationContext.getBean(LZjqMapper.class);
		LJingcaiMapper lJingcaiMapper = RealMain.applicationContext.getBean(LJingcaiMapper.class);
		Date today = new Date();
		//获取当前时间（-1天至+7天），会开始的比赛列表
//		MGameExample gameExample = new MGameExample();
//		gameExample.createCriteria().andStarttimeBetween(new Date(today.getTime()-86400000), new Date(today.getTime()+(7*86400000)));
//		List<MGame> games = mGameMapper.selectByExample(gameExample);
//		for(MGame game : games){
//			
//		}
		/* 竞彩赔率 */
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd"); 
		Date day = new Date(today.getTime()+((i-1)*86400000));
		Map<String,String> params = new HashMap<String,String>();
		params.put("expect", format.format(day));
		params.put("lottid", "6");
		params.put("oid", "2018");
		String jsonStrXs = HttpClientUtil.doGet(Context.BASE_URL, params);
		Interface2018 interface2018 = JsonUtils.jsonToPojo(jsonStrXs, Interface2018.class);
		if(interface2018.getCode()==0&&interface2018.getR().size()>0) {
			for(Entity2018R entity2018R : interface2018.getR()){
				try{
					MGame game = mGameMapper.selectByYId(Integer.valueOf(entity2018R.getMid()));
					if(ObjectHelper.isEmpty(game)) continue;
					EntityJingCai entityJingCai = JsonUtils.jsonToPojo(entity2018R.get$t().replace("&quot;", "\""), EntityJingCai.class);
					//判断是否存在变化
					LJingcaiExample lJingcaiExample = new LJingcaiExample();
					com.kprd.odds.pojo.LJingcaiExample.Criteria criterias = lJingcaiExample.createCriteria();
					criterias.andMatchkeyEqualTo(entityJingCai.getMatchkey());
					List<LJingcai> jingcais = lJingcaiMapper.selectByExample(lJingcaiExample);
					boolean flag = false;
					for(LJingcai jingcai : jingcais){
						if(jingcai.getWinodds().equals(RegularVerify.isReal(entityJingCai.getSpf_sp3())?new BigDecimal(entityJingCai.getSpf_sp3()):new BigDecimal(0))
								&&jingcai.getDeuceodds().equals(RegularVerify.isReal(entityJingCai.getSpf_sp1())?new BigDecimal(entityJingCai.getSpf_sp1()):new BigDecimal(0))
								&&jingcai.getLostodds().equals(RegularVerify.isReal(entityJingCai.getSpf_sp0())?new BigDecimal(entityJingCai.getSpf_sp0()):new BigDecimal(0))){
							flag = true;
							break;
						}
					}
					if(!flag){
						LJingcai jingcai = new LJingcai();
						jingcai.setId(IDUtils.createUUId());
						jingcai.setMatchkey(entityJingCai.getMatchkey());
						jingcai.setGameyid(Integer.valueOf(entity2018R.getMid()));
						jingcai.setGameid(game.getId());
						jingcai.setStatus(RegularVerify.isNum(entityJingCai.getSt())?Integer.valueOf(entityJingCai.getSt()):3);
						jingcai.setClasses(1);
						jingcai.setUpdatetime(today);
						jingcai.setLetnum(Integer.valueOf(entityJingCai.getLet()));
						jingcai.setWinodds(RegularVerify.isReal(entityJingCai.getSpf_sp3())?new BigDecimal(entityJingCai.getSpf_sp3()):new BigDecimal(0));
						jingcai.setDeuceodds(RegularVerify.isReal(entityJingCai.getSpf_sp1())?new BigDecimal(entityJingCai.getSpf_sp1()):new BigDecimal(0));
						jingcai.setLostodds(RegularVerify.isReal(entityJingCai.getSpf_sp0())?new BigDecimal(entityJingCai.getSpf_sp0()):new BigDecimal(0));
						jingcai.setWinletodds(RegularVerify.isReal(entityJingCai.getRqspf_sp3())?new BigDecimal(entityJingCai.getRqspf_sp3()):new BigDecimal(0));
						jingcai.setDeuceletodds(RegularVerify.isReal(entityJingCai.getRqspf_sp1())?new BigDecimal(entityJingCai.getRqspf_sp1()):new BigDecimal(0));
						jingcai.setLostletodds(RegularVerify.isReal(entityJingCai.getRqspf_sp0())?new BigDecimal(entityJingCai.getRqspf_sp0()):new BigDecimal(0));
						jingcai.setSsodds(RegularVerify.isReal(entityJingCai.getBqc_sp33())?new BigDecimal(entityJingCai.getBqc_sp33()):new BigDecimal(0));
						jingcai.setSpodds(RegularVerify.isReal(entityJingCai.getBqc_sp13())?new BigDecimal(entityJingCai.getBqc_sp13()):new BigDecimal(0));
						jingcai.setSfodds(RegularVerify.isReal(entityJingCai.getBqc_sp03())?new BigDecimal(entityJingCai.getBqc_sp03()):new BigDecimal(0));
						jingcai.setPsodds(RegularVerify.isReal(entityJingCai.getBqc_sp13())?new BigDecimal(entityJingCai.getBqc_sp13()):new BigDecimal(0));
						jingcai.setPpodds(RegularVerify.isReal(entityJingCai.getBqc_sp11())?new BigDecimal(entityJingCai.getBqc_sp11()):new BigDecimal(0));
						jingcai.setPfodds(RegularVerify.isReal(entityJingCai.getBqc_sp10())?new BigDecimal(entityJingCai.getBqc_sp10()):new BigDecimal(0));
						jingcai.setFsodds(RegularVerify.isReal(entityJingCai.getBqc_sp03())?new BigDecimal(entityJingCai.getBqc_sp03()):new BigDecimal(0));
						jingcai.setFpodds(RegularVerify.isReal(entityJingCai.getBqc_sp01())?new BigDecimal(entityJingCai.getBqc_sp01()):new BigDecimal(0));
						jingcai.setFfodds(RegularVerify.isReal(entityJingCai.getBqc_sp00())?new BigDecimal(entityJingCai.getBqc_sp00()):new BigDecimal(0));
						jingcai.setOdds0(RegularVerify.isReal(entityJingCai.getJqs_sp0())?new BigDecimal(entityJingCai.getJqs_sp0()):new BigDecimal(0));
						jingcai.setOdds1(RegularVerify.isReal(entityJingCai.getJqs_sp1())?new BigDecimal(entityJingCai.getJqs_sp1()):new BigDecimal(0));
						jingcai.setOdds2(RegularVerify.isReal(entityJingCai.getJqs_sp2())?new BigDecimal(entityJingCai.getJqs_sp2()):new BigDecimal(0));
						jingcai.setOdds3(RegularVerify.isReal(entityJingCai.getJqs_sp3())?new BigDecimal(entityJingCai.getJqs_sp3()):new BigDecimal(0));
						jingcai.setOdds4(RegularVerify.isReal(entityJingCai.getJqs_sp4())?new BigDecimal(entityJingCai.getJqs_sp4()):new BigDecimal(0));
						jingcai.setOdds5(RegularVerify.isReal(entityJingCai.getJqs_sp5())?new BigDecimal(entityJingCai.getJqs_sp5()):new BigDecimal(0));
						jingcai.setOdds6(RegularVerify.isReal(entityJingCai.getJqs_sp6())?new BigDecimal(entityJingCai.getJqs_sp6()):new BigDecimal(0));
						jingcai.setOdds7(RegularVerify.isReal(entityJingCai.getJqs_sp7())?new BigDecimal(entityJingCai.getJqs_sp7()):new BigDecimal(0));
						jingcai.setOdds10(RegularVerify.isReal(entityJingCai.getBf_sp10())?new BigDecimal(entityJingCai.getBf_sp10()):new BigDecimal(0));
						jingcai.setOdds20(RegularVerify.isReal(entityJingCai.getBf_sp20())?new BigDecimal(entityJingCai.getBf_sp20()):new BigDecimal(0));
						jingcai.setOdds21(RegularVerify.isReal(entityJingCai.getBf_sp21())?new BigDecimal(entityJingCai.getBf_sp21()):new BigDecimal(0));
						jingcai.setOdds30(RegularVerify.isReal(entityJingCai.getBf_sp30())?new BigDecimal(entityJingCai.getBf_sp30()):new BigDecimal(0));
						jingcai.setOdds31(RegularVerify.isReal(entityJingCai.getBf_sp31())?new BigDecimal(entityJingCai.getBf_sp31()):new BigDecimal(0));
						jingcai.setOdds32(RegularVerify.isReal(entityJingCai.getBf_sp32())?new BigDecimal(entityJingCai.getBf_sp32()):new BigDecimal(0));
						jingcai.setOdds40(RegularVerify.isReal(entityJingCai.getBf_sp40())?new BigDecimal(entityJingCai.getBf_sp40()):new BigDecimal(0));
						jingcai.setOdds41(RegularVerify.isReal(entityJingCai.getBf_sp41())?new BigDecimal(entityJingCai.getBf_sp41()):new BigDecimal(0));
						jingcai.setOdds42(RegularVerify.isReal(entityJingCai.getBf_sp42())?new BigDecimal(entityJingCai.getBf_sp42()):new BigDecimal(0));
						jingcai.setOdds50(RegularVerify.isReal(entityJingCai.getBf_sp50())?new BigDecimal(entityJingCai.getBf_sp50()):new BigDecimal(0));
						jingcai.setOdds51(RegularVerify.isReal(entityJingCai.getBf_sp51())?new BigDecimal(entityJingCai.getBf_sp51()):new BigDecimal(0));
						jingcai.setOdds52(RegularVerify.isReal(entityJingCai.getBf_sp52())?new BigDecimal(entityJingCai.getBf_sp52()):new BigDecimal(0));
						jingcai.setOdds00(RegularVerify.isReal(entityJingCai.getBf_sp00())?new BigDecimal(entityJingCai.getBf_sp00()):new BigDecimal(0));
						jingcai.setOdds11(RegularVerify.isReal(entityJingCai.getBf_sp11())?new BigDecimal(entityJingCai.getBf_sp11()):new BigDecimal(0));
						jingcai.setOdds22(RegularVerify.isReal(entityJingCai.getBf_sp22())?new BigDecimal(entityJingCai.getBf_sp22()):new BigDecimal(0));
						jingcai.setOdds33(RegularVerify.isReal(entityJingCai.getBf_sp33())?new BigDecimal(entityJingCai.getBf_sp33()):new BigDecimal(0));
						jingcai.setOdds01(RegularVerify.isReal(entityJingCai.getBf_sp01())?new BigDecimal(entityJingCai.getBf_sp01()):new BigDecimal(0));
						jingcai.setOdds02(RegularVerify.isReal(entityJingCai.getBf_sp02())?new BigDecimal(entityJingCai.getBf_sp02()):new BigDecimal(0));
						jingcai.setOdds12(RegularVerify.isReal(entityJingCai.getBf_sp12())?new BigDecimal(entityJingCai.getBf_sp12()):new BigDecimal(0));
						jingcai.setOdds03(RegularVerify.isReal(entityJingCai.getBf_sp03())?new BigDecimal(entityJingCai.getBf_sp03()):new BigDecimal(0));
						jingcai.setOdds13(RegularVerify.isReal(entityJingCai.getBf_sp13())?new BigDecimal(entityJingCai.getBf_sp13()):new BigDecimal(0));
						jingcai.setOdds23(RegularVerify.isReal(entityJingCai.getBf_sp23())?new BigDecimal(entityJingCai.getBf_sp23()):new BigDecimal(0));
						jingcai.setOdds04(RegularVerify.isReal(entityJingCai.getBf_sp04())?new BigDecimal(entityJingCai.getBf_sp04()):new BigDecimal(0));
						jingcai.setOdds14(RegularVerify.isReal(entityJingCai.getBf_sp14())?new BigDecimal(entityJingCai.getBf_sp14()):new BigDecimal(0));
						jingcai.setOdds24(RegularVerify.isReal(entityJingCai.getBf_sp24())?new BigDecimal(entityJingCai.getBf_sp24()):new BigDecimal(0));
						jingcai.setOdds05(RegularVerify.isReal(entityJingCai.getBf_sp05())?new BigDecimal(entityJingCai.getBf_sp05()):new BigDecimal(0));
						jingcai.setOdds15(RegularVerify.isReal(entityJingCai.getBf_sp15())?new BigDecimal(entityJingCai.getBf_sp15()):new BigDecimal(0));
						jingcai.setOdds25(RegularVerify.isReal(entityJingCai.getBf_sp25())?new BigDecimal(entityJingCai.getBf_sp25()):new BigDecimal(0));
						jingcai.setWinother(RegularVerify.isReal(entityJingCai.getBf_sp93())?new BigDecimal(entityJingCai.getBf_sp93()):new BigDecimal(0));
						jingcai.setDeuceother(RegularVerify.isReal(entityJingCai.getBf_sp91())?new BigDecimal(entityJingCai.getBf_sp91()):new BigDecimal(0));
						jingcai.setLostother(RegularVerify.isReal(entityJingCai.getBf_sp90())?new BigDecimal(entityJingCai.getBf_sp90()):new BigDecimal(0));
						lJingcaiMapper.insertSelective(jingcai);
					}
					/* 赔率公司 */
					Map<String,String> param = new HashMap<String,String>();
					param.put("mid", game.getYid().toString());
					//**获取欧赔数据**//
					param.put("oid", "2002");
					String jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
					Interface200X interface200X = null;
					try{
						interface200X = JsonUtils.jsonToPojo(jsonStrX, Interface200X.class);
					}catch(Exception e){}
					if(interface200X!=null&&interface200X.getCode()==0&&interface200X.getRow().size()>0) {
						for(Entity200X entity200x : interface200X.getRow()){
							DGamingcompany dGamingcompany = dGamingcompanyMapper.selectByYId(entity200x.getCid());
							if(ObjectHelper.isEmpty(dGamingcompany))break;
							param.put("comid", dGamingcompany.getYid().toString());
							param.put("oid", "2013");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							Interface201X interface201X = null;
							try{
								interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							}catch(Exception e){}
							if(interface201X!=null&&interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									LOupExample lOupExample = new LOupExample();
									com.kprd.odds.pojo.LOupExample.Criteria criteria = lOupExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									criteria.andUptEqualTo(Integer.valueOf(entity201x.getTime()));
									Date update = new Date(Long.valueOf(entity201x.getTime())*1000);
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
										oup.setUpt(Integer.valueOf(entity201x.getTime()));
										lOupMapper.insertSelective(oup);
									}
								}
							}
						}
					}
					
					//**获取亚赔数据**//
					param.put("oid", "2003");
					jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
					try{
						interface200X = JsonUtils.jsonToPojo(jsonStrX, Interface200X.class);
					}catch(Exception e){}
					if(interface200X!=null&&interface200X.getCode()==0&&interface200X.getRow().size()>0) {
						for(Entity200X entity200x : interface200X.getRow()){
							DGamingcompany dGamingcompany = dGamingcompanyMapper.selectByYId(entity200x.getCid());
							if(ObjectHelper.isEmpty(dGamingcompany))break;
							param.put("comid", dGamingcompany.getYid().toString());
							param.put("oid", "2013");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							Interface201X interface201X = null;
							try{
								interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							}catch(Exception e){}
							if(interface201X!=null&&interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									LYapExample lYapExample = new LYapExample();
									com.kprd.odds.pojo.LYapExample.Criteria criteria = lYapExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									criteria.andUptEqualTo(Integer.valueOf(entity201x.getTime()));
									Date update = new Date(Long.valueOf(entity201x.getTime())*1000);
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
										yap.setUpt(Integer.valueOf(entity201x.getTime()));
										lYapMapper.insertSelective(yap);
									}
								}
							}
						}
					}
					
					//**获取大小球数据**//
					param.put("oid", "2004");
					jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
					try{
						interface200X = JsonUtils.jsonToPojo(jsonStrX, Interface200X.class);
					}catch(Exception e){}
					if(interface200X!=null&&interface200X.getCode()==0&&interface200X.getRow().size()>0) {
						for(Entity200X entity200x : interface200X.getRow()){
							DGamingcompany dGamingcompany = dGamingcompanyMapper.selectByYId(entity200x.getCid());
							if(ObjectHelper.isEmpty(dGamingcompany))break;
							param.put("comid", dGamingcompany.getYid().toString());
							param.put("oid", "2015");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							Interface201X interface201X = null;
							try{
								interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							}catch(Exception e){}
							if(interface201X!=null&&interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									LDaxExample lDaxExample = new LDaxExample();
									com.kprd.odds.pojo.LDaxExample.Criteria criteria = lDaxExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									criteria.andUptEqualTo(Integer.valueOf(entity201x.getTime()));
									Date update = new Date(Long.valueOf(entity201x.getTime())*1000);
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
										dax.setUpt(Integer.valueOf(entity201x.getTime()));
										lDaxMapper.insertSelective(dax);
									}
								}
							}
						}
					}
					
					//**获取波胆数据**//
					param.put("oid", "2005");
					jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
					try{
						interface200X = JsonUtils.jsonToPojo(jsonStrX, Interface200X.class);
					}catch(Exception e){}
					if(interface200X!=null&&interface200X.getCode()==0&&interface200X.getRow().size()>0) {
						for(Entity200X entity200x : interface200X.getRow()){
							DGamingcompany dGamingcompany = dGamingcompanyMapper.selectByYId(entity200x.getCid());
							if(ObjectHelper.isEmpty(dGamingcompany))break;
							param.put("comid", dGamingcompany.getYid().toString());
							param.put("oid", "2016");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							Interface201X interface201X = null;
							try{
								interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							}catch(Exception e){}
							if(interface201X!=null&&interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									LBodExample lBodExample = new LBodExample();
									com.kprd.odds.pojo.LBodExample.Criteria criteria = lBodExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									criteria.andUptEqualTo(Integer.valueOf(entity201x.getTime()));
									Date update = new Date(Long.valueOf(entity201x.getTime())*1000);
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
										bod.setUpt(Integer.valueOf(entity201x.getTime()));
										lBodMapper.insertSelective(bod);
									}
								}
							}
						}
					}
					
					
					//**获取半全场数据**//
					param.put("oid", "2006");
					jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
					try{
						interface200X = JsonUtils.jsonToPojo(jsonStrX, Interface200X.class);
					}catch(Exception e){}
					if(interface200X!=null&&interface200X.getCode()==0&&interface200X.getRow().size()>0) {
						for(Entity200X entity200x : interface200X.getRow()){
							DGamingcompany dGamingcompany = dGamingcompanyMapper.selectByYId(entity200x.getCid());
							if(ObjectHelper.isEmpty(dGamingcompany))break;
							param.put("comid", dGamingcompany.getYid().toString());
							param.put("oid", "2019");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							Interface201X interface201X = null;
							try{
								interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							}catch(Exception e){}
							if(interface201X!=null&&interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									LBqcExample lBqcExample = new LBqcExample();
									com.kprd.odds.pojo.LBqcExample.Criteria criteria = lBqcExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									criteria.andUptEqualTo(Integer.valueOf(entity201x.getTime()));
									Date update = new Date(Long.valueOf(entity201x.getTime())*1000);
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
										bqc.setUpt(Integer.valueOf(entity201x.getTime()));
										lBqcMapper.insertSelective(bqc);
									}
								}
							}
						}
					}
					
					
					//**获取球队入球数数据**//
					param.put("oid", "2009");
					jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
					try{
						interface200X = JsonUtils.jsonToPojo(jsonStrX, Interface200X.class);
					}catch(Exception e){}
					if(interface200X!=null&&interface200X.getCode()==0&&interface200X.getRow().size()>0) {
						for(Entity200X entity200x : interface200X.getRow()){
							DGamingcompany dGamingcompany = dGamingcompanyMapper.selectByYId(entity200x.getCid());
							if(ObjectHelper.isEmpty(dGamingcompany))break;
							param.put("comid", dGamingcompany.getYid().toString());
							param.put("oid", "2025");
							jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
							Interface201X interface201X = null;
							try{
								interface201X = JsonUtils.jsonToPojo(jsonStrX, Interface201X.class);
							}catch(Exception e){}
							if(interface201X!=null&&interface201X.getCode()==0&&interface201X.getRow().size()>0) {
								for(Entity201X entity201x : interface201X.getRow()){
									LZjqExample lZjqExample = new LZjqExample();
									com.kprd.odds.pojo.LZjqExample.Criteria criteria = lZjqExample.createCriteria();
									criteria.andCompanyyidEqualTo(dGamingcompany.getYid());
									criteria.andGameyidEqualTo(game.getYid());
									criteria.andUptEqualTo(Integer.valueOf(entity201x.getTime()));
									Date update = new Date(Long.valueOf(entity201x.getTime())*1000);
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
										zjq.setUpt(Integer.valueOf(entity201x.getTime()));
										lZjqMapper.insertSelective(zjq);
									}
								}
							}
						}
					}
					
					
				}catch(Exception e){
					System.out.println("------------error-------------");
					System.out.println(format.format(day)+"--"+entity2018R.getMid());
					continue;
				}
				System.out.println(format.format(day)+"--"+entity2018R.getMid());
			}
		}
	}
	
	
}
