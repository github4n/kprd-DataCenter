package com.kprd.date.history.national;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.constant.Context;
import com.kprd.date.history.national.entity.Interface1001;
import com.kprd.date.history.national.entity.NationalEntity;
import com.kprd.dic.mapper.DNationalMapper;
import com.kprd.dic.pojo.DNational;


/***
 * 国家数据更新
 * @author Administrator
 *
 */
public class NationalData {
	
	
	public String insertNationalData(){
		Map<String,String> param = new HashMap<String,String>();
		param.put("oid", Context.NATIONALDATA);
		param.put("type", "0");
		String url = Context.BASE_URL;
		//////////////////通过 http请求有料接口//////////////////////
		String jsonStr = HttpClientUtil.doGet(url, param);
		Interface1001 interface1001 = JsonUtils.jsonToPojo(jsonStr, Interface1001.class);
		if(interface1001.getCode()==0&&interface1001.getRow().size()>0) {
			////////////////////数据落地////////////////////////
			//1、获得mapper代理对象
			@SuppressWarnings("resource")
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
			DNationalMapper dNationalMapper = applicationContext.getBean(DNationalMapper.class);
			int row = 0;
			for(NationalEntity entity:interface1001.getRow()) {
				//System.out.println("entity"+entity.getCid());
				DNational dNational = dNationalMapper.selectByYId(entity.getCid());
				//判断数据库是否有此数据 有则放行，反之则新增
				if(ObjectHelper.isEmpty(dNational)) {//执行新增操作
					DNational d = new DNational();
					d.setId(IDUtils.createUUId());
					d.setLoc(entity.getLoc());
					d.setNamecn(entity.getCn());
					d.setNameen(entity.getEn());
					d.setYid(entity.getCid());
					row += dNationalMapper.insertSelective(d);
					System.out.println("新增赛事国家:"+d.getNamecn());
				}
			}
			return "新增完毕："+row+"条";				
		} else {
			return "接口返回code:"+interface1001.getCode()+";msg:"+interface1001.getMsg();	
		}			
	}

	public static void main(String[] args) {
		System.out.println(new NationalData().insertNationalData());
	}
}
