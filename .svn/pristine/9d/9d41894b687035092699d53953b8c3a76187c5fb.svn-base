package com.kprd.date.history.match;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kprd.common.utils.HttpClientUtil;
import com.kprd.common.utils.IDUtils;
import com.kprd.common.utils.JsonUtils;
import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.constant.Context;
import com.kprd.date.history.match.entity.Interface1002;
import com.kprd.date.history.match.entity.MatchEntity;
import com.kprd.dic.mapper.DMatchMapper;
import com.kprd.dic.mapper.DNationalMapper;
import com.kprd.dic.pojo.DMatch;
import com.kprd.dic.pojo.DNational;


/***
 * 联赛列表数据更新
 * @author Administrator
 *
 */
public class MatchData {
	
	
	public String insertNationalData(){
		Map<String,String> param = new HashMap<String,String>();
		param.put("oid", Context.MATCHDATA);
		param.put("type", "0");
		String url = Context.BASE_URL;
		//////////////////通过 http请求有料接口//////////////////////
		String jsonStr = HttpClientUtil.doGet(url, param);
		Interface1002 interface1002 = JsonUtils.jsonToPojo(jsonStr, Interface1002.class);
		if(interface1002.getCode()==0&&interface1002.getRow().size()>0) {
			////////////////////数据落地////////////////////////
			//1、获得mapper代理对象
			@SuppressWarnings("resource")
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
			DMatchMapper dMatchMapper = applicationContext.getBean(DMatchMapper.class);
			DNationalMapper dNationalMapper = applicationContext.getBean(DNationalMapper.class);
			int row = 0;
			for(MatchEntity entity:interface1002.getRow()) {
				//System.out.println("entity"+entity.getCid());
				DMatch dMatch = dMatchMapper.selectByYId(entity.getLid());				
				DNational dNational = dNationalMapper.selectByYId(entity.getCid());
				//判断数据库是否有此数据 有则放行，反之则新增
				if(ObjectHelper.isEmpty(dMatch)) {//执行新增操作
					DMatch d = new DMatch();
					d.setId(IDUtils.createUUId());
					d.setColor(entity.getColor());
					d.setYid(entity.getLid());
					d.setKind(entity.getKind());
					d.setName(entity.getCnshort());
					d.setFullname(entity.getCn());
					if(ObjectHelper.isNotEmpty(dNational)) {
						d.setNationalid(dNational.getId());
					}
					row += dMatchMapper.insertSelective(d);
					System.out.println("新增联赛:"+d.getName());
				}
			}
			return "新增完毕："+row+"条";				
		} else {
			return "接口返回code:"+interface1002.getCode()+";msg:"+interface1002.getMsg();	
		}			
	}

	public static void main(String[] args) {
		System.out.println(new MatchData().insertNationalData());
	}
}
