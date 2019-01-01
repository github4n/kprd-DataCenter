package com.kprd.date.zq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.liansai.mapper.MVsMapper;
import com.kprd.liansai.pojo.MVs;
import com.kprd.liansai.pojo.MVsExample;
import com.kprd.liansai.pojo.MVsExample.Criteria;

public class OddsFetch {
	public static void oddCatch() {
		try {
			MVsMapper mvsMapper = Main.applicationContext.getBean(MVsMapper.class);
			String yesterday = UtilBag.dateUtil(-1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date yes = sdf.parse(yesterday);
			MVsExample mvsEx = new MVsExample();
			Criteria criteria = mvsEx.createCriteria();
			criteria.andEndTimeGreaterThanOrEqualTo(yes);
			mvsEx.setOrderByClause(" end_time desc");
			List<MVs> list = mvsMapper.selectByExample(mvsEx);
			if(ObjectHelper.isNotEmpty(list)) {
				for(int i=0;i<list.size();i++) {
					AnalysisFetch.getAnalysis(list.get(i));
					AnalysisFetch.getAsia(list.get(i).getXiid());
					AnalysisFetch.getEuro(list.get(i).getXiid());
					System.out.println("总共" + list.size() + "条");
					System.out.println("已完成" + (i+1) + "条");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		oddCatch();
	}
}
