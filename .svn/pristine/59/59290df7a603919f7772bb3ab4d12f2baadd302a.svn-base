package com.kprd.date.fetch.jingcai.football.timer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.date.fetch.jingcai.football.JingCaiAsiaOdds;
import com.kprd.date.fetch.jingcai.football.JingCaiEuropOdds;
import com.kprd.date.lq.update.Main;
import com.kprd.date.util.UtilBag;
import com.kprd.newliansai.mapper.FoMixMapper;
import com.kprd.newliansai.pojo.FoMix;
import com.kprd.newliansai.pojo.FoMixExample;

public class JcOddsTimer {
	
	static FoMixMapper fMixMapper = Main.applicationContext.getBean(FoMixMapper.class);
	
	public static void main(String[] args) {
		while(true) {
			try {
				FoMixExample fmEx = new FoMixExample();
				String day = UtilBag.dateUtil(0);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String today = day + " 11:59:59";
				Date kyo = sdf.parse(today);
				fmEx.createCriteria().andStarttimeGreaterThan(kyo);
				fmEx.setOrderByClause(" idUnique asc");
				List<FoMix> mvsList = fMixMapper.selectByExample(fmEx);
				System.out.println("今天" + mvsList.size() + "场比赛");
				int counter = 0;
				if(ObjectHelper.isNotEmpty(mvsList)) {
					for(FoMix m : mvsList) {
						counter++;
						if(counter%5==0) {
							Thread.sleep(1000*10);
						}
						JingCaiAsiaOdds.getAsiaData(m);
						JingCaiEuropOdds.getEuroOddsHtml(m);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
