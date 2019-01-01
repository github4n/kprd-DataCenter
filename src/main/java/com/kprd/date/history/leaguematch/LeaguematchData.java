package com.kprd.date.history.leaguematch;

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
import com.kprd.date.history.leaguematch.entity.Interface1003;
import com.kprd.date.history.leaguematch.entity.LeaguematchEntity;
import com.kprd.dic.mapper.DMatchMapper;
import com.kprd.dic.pojo.DMatch;
import com.kprd.dic.pojo.DMatchExample;
import com.kprd.match.mapper.MLeaguematchMapper;
import com.kprd.match.pojo.MLeaguematch;

/***
 * 赛季数据更新
 * @author Administrator
 *
 */
public class LeaguematchData {

	public String updateLeaguematchData() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询本地联赛列表
		DMatchMapper dMatchMapper = applicationContext.getBean(DMatchMapper.class);
		DMatchExample example = new DMatchExample();
		List<DMatch> dMatchs = dMatchMapper.selectByExample(example);
		int row = 0;
		if(ObjectHelper.isNotEmpty(dMatchs)&&dMatchs.size()>0) {
			for(DMatch dMatch:dMatchs) {
				Map<String,String> param = new HashMap<String,String>();
				param.put("oid", Context.LEAGUEMATCHDATA);
				param.put("lid",dMatch.getYid().toString());
				String url = Context.BASE_URL;
				//////////////////通过 http请求有料接口//////////////////////
				String jsonStr = HttpClientUtil.doGet(url, param);
				Interface1003 interface1003 = JsonUtils.jsonToPojo(jsonStr, Interface1003.class);
				if(ObjectHelper.isNotEmpty(interface1003)&&interface1003.getCode()==0&&interface1003.getRow().size()>0) {
					for(LeaguematchEntity leaguematchEntity:interface1003.getRow()) {
						//验证是否有该联赛记录，没有则新增
						MLeaguematchMapper mLeaguematchMapper = applicationContext.getBean(MLeaguematchMapper.class);
						MLeaguematch mLeaguematch = mLeaguematchMapper.selectByYId(leaguematchEntity.getSid());
						if(ObjectHelper.isEmpty(mLeaguematch)) {//未查到该记录则新增
							MLeaguematch m = new MLeaguematch();
							m.setId(IDUtils.createUUId());
							m.setYid(leaguematchEntity.getSid());
							m.setMatchid(dMatch.getId());	
							m.setMatchyid(dMatch.getYid());
							m.setSname(leaguematchEntity.getSname());
							row += mLeaguematchMapper.insertSelective(m);
							System.out.println("新增"+dMatch.getName()+":"+mLeaguematch.getSname());
						}
					}
				}
			}
		} 
		return "新增完毕："+row+"条";	
	}
	
	public static void main(String[] args) {
		System.out.println(new LeaguematchData().updateLeaguematchData());
	}
}
