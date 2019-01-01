package com.kprd.date.history.company;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.constant.Context;
import com.kprd.date.history.company.entity.CompanyEntity;
import com.kprd.date.history.company.entity.Interface2001;
import com.kprd.dic.mapper.DGamingcompanyMapper;
import com.kprd.dic.pojo.DGamingcompany;


/***
 * 联赛列表数据更新
 * @author Administrator
 *
 */
public class CompanyData {
	
	
	public String insertNationalData(){
		@SuppressWarnings("resource")
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		DGamingcompanyMapper dGamingcompanyMapper = applicationContext.getBean(DGamingcompanyMapper.class);

		Map<String,String> param = new HashMap<String,String>();
		param.put("oid", Context.COMPANYDATA);
		param.put("type", "0");
		String url = Context.BASE_URL;
		//////////////////通过 http请求有料接口//////////////////////
		String jsonStr = HttpClientUtil.doGet(url, param);
		Interface2001 interface2001 = JsonUtils.jsonToPojo(jsonStr, Interface2001.class);
		if(interface2001.getCode()==0&&interface2001.getRow().size()>0) {
			////////////////////数据落地////////////////////////
			//1、获得mapper代理对象
			for(CompanyEntity entity:interface2001.getRow()) {
				//System.out.println("entity"+entity.getCid());
				DGamingcompany gamingcompany = dGamingcompanyMapper.selectByYId(entity.getCid());
				//判断数据库是否有此数据 有则放行，反之则新增
				if(ObjectHelper.isEmpty(gamingcompany)) {//执行新增操作
					gamingcompany = new DGamingcompany();
					gamingcompany.setId(IDUtils.createUUId());
					gamingcompany.setType(1);
					gamingcompany.setYid(entity.getCid());
					gamingcompany.setReg(entity.getReg());
					gamingcompany.setName(entity.getCname());
					dGamingcompanyMapper.insert(gamingcompany);
				}
			}
		} else {
			return "接口返回code:"+interface2001.getCode()+";msg:"+interface2001.getMsg();	
		}
		param.put("type", "1");
		jsonStr = HttpClientUtil.doGet(url, param);
		interface2001 = JsonUtils.jsonToPojo(jsonStr, Interface2001.class);
		if(interface2001.getCode()==0&&interface2001.getRow().size()>0) {
			////////////////////数据落地////////////////////////
			//1、获得mapper代理对象
			for(CompanyEntity entity:interface2001.getRow()) {
				//System.out.println("entity"+entity.getCid());
				DGamingcompany gamingcompany = dGamingcompanyMapper.selectByYId(entity.getCid());
				//判断数据库是否有此数据 有则放行，反之则新增
				if(ObjectHelper.isNotEmpty(gamingcompany)) {//执行新增操作
					gamingcompany.setType(0);
					dGamingcompanyMapper.updateByPrimaryKeySelective(gamingcompany);
				}
			}
		} else {
			return "接口返回code:"+interface2001.getCode()+";msg:"+interface2001.getMsg();	
		}
		return "完成";
		
	}

	public static void main(String[] args) {
		System.out.println(new CompanyData().insertNationalData());
	}
}
