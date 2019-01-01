package com.kprd.date.history.odds;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import com.kprd.date.constant.RegularVerify;
import com.kprd.date.history.odds.entity.Entity2018R;
import com.kprd.date.history.odds.entity.EntityJingCai;
import com.kprd.date.history.odds.entity.Interface2018;
import com.kprd.match.mapper.MGameMapper;
import com.kprd.match.pojo.MGame;
import com.kprd.odds.mapper.LJingcaiMapper;
import com.kprd.odds.pojo.LJingcai;
import com.kprd.odds.pojo.LJingcaiExample;


/***
 * @author Administrator
 *
 */
public class JingcaiData {
	
	
	public void insertNationalData(){
		@SuppressWarnings("resource")
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		LJingcaiMapper lJingcaiMapper = applicationContext.getBean(LJingcaiMapper.class);
		MGameMapper mGameMapper = applicationContext.getBean(MGameMapper.class);
		Date today = new Date(1230739200000l);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		boolean fg = true;
		do {
			int i = 0;
			Date day = new Date(today.getTime()+((i-1)*86400000));
			Map<String,String> param = new HashMap<String,String>();
			param.put("expect", format.format(day));
			param.put("lottid", "6");
			param.put("oid", "2018");
			String jsonStrX = HttpClientUtil.doGet(Context.BASE_URL, param);
			Interface2018 interface2018 = JsonUtils.jsonToPojo(jsonStrX, Interface2018.class);
			if(interface2018.getCode()==0&&interface2018.getR().size()>0) {
				for(Entity2018R entity2018R : interface2018.getR()){
					try{
						EntityJingCai entityJingCai = JsonUtils.jsonToPojo(entity2018R.get$t().replace("&quot;", "\""), EntityJingCai.class);
						//判断是否存在变化
//						LJingcaiExample lJingcaiExample = new LJingcaiExample();
//						com.kprd.odds.pojo.LJingcaiExample.Criteria criteria = lJingcaiExample.createCriteria();
//						criteria.andMatchkeyEqualTo(entityJingCai.getMatchkey());
//						criteria.andWinoddsEqualTo(RegularVerify.isReal(entityJingCai.getSpf_sp3())?new BigDecimal(entityJingCai.getSpf_sp3()):new BigDecimal(0));
//						criteria.andDeuceoddsEqualTo(RegularVerify.isReal(entityJingCai.getSpf_sp1())?new BigDecimal(entityJingCai.getSpf_sp1()):new BigDecimal(0));
//						criteria.andLostoddsEqualTo(RegularVerify.isReal(entityJingCai.getSpf_sp0())?new BigDecimal(entityJingCai.getSpf_sp0()):new BigDecimal(0));
//						List<LJingcai> zjqs = lJingcaiMapper.selectByExample(lJingcaiExample);
//						if(zjqs.size()==0){
							LJingcai jingcai = new LJingcai();
							jingcai.setId(IDUtils.createUUId());
							jingcai.setMatchkey(entityJingCai.getMatchkey());
							jingcai.setGameyid(Integer.valueOf(entity2018R.getMid()));
							MGame game = mGameMapper.selectByYId(jingcai.getGameyid());
							if(ObjectHelper.isEmpty(game)) continue;
							jingcai.setGameid(game.getId());
							jingcai.setStatus(RegularVerify.isNum(entityJingCai.getSt())?Integer.valueOf(entityJingCai.getSt()):3);
							jingcai.setClasses(1);
							jingcai.setUpdatetime(today);
							jingcai.setLetnum(RegularVerify.isNum(entityJingCai.getLet())?Integer.valueOf(entityJingCai.getLet()):0);
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
//						}
					}catch(Exception e){
						System.out.println(format.format(day)+"--"+entity2018R.getMid());
					}
				}
			}
			i++;
			if(day.after(new Date())) fg = false;
		} while (fg);
	}

	public static void main(String[] args) {
		new JingcaiData().insertNationalData();
	}
}
