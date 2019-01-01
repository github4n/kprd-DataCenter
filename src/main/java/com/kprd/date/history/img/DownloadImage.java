package com.kprd.date.history.img;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kprd.common.utils.ObjectHelper;
import com.kprd.dic.mapper.DTeamMapper;
import com.kprd.dic.pojo.DTeam;
import com.kprd.dic.pojo.DTeamExample;


/***
 * 圖片下載
 * @author Administrator
 *
 */


public class DownloadImage {  
	  
    /** 
     * @param args 
     * @throws Exception  
     */  
    public static void main(String[] args) throws Exception {  
    	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		//查询联赛赛季表
		DTeamMapper dTeamMapper = applicationContext.getBean(DTeamMapper.class);
		DTeamExample example = new DTeamExample();
		List<DTeam> dTeams = dTeamMapper.selectByExample(example);
		if(ObjectHelper.isNotEmpty(dTeams)&&dTeams.size()>0) {
			for(DTeam dTeam:dTeams) {
				if(ObjectHelper.isNotEmpty(dTeam.getLogo())){
					String fileName = dTeam.getLogo().substring(dTeam.getLogo().lastIndexOf("/")+1, dTeam.getLogo().length());
					download(dTeam.getLogo(), fileName, "d:\\image\\");
				}
			}
		}
    	
    	
    	
        // TODO Auto-generated method stub  
         /*download("http://ui.51bi.com/opt/siteimg/images/fanbei0923/Mid_07.jpg", "51bi.gif","f:\\image\\");  */
    }  
      
    public static void download(String urlString, String filename,String savePath) throws Exception {  
        // 构造URL  
        URL url = new URL(urlString);  
        // 打开连接  
        URLConnection con = url.openConnection();  
        //设置请求超时为5s  
        con.setConnectTimeout(5*1000);  
        // 输入流  
        InputStream is = con.getInputStream();  
      
        // 1K的数据缓冲  
        byte[] bs = new byte[1024];  
        // 读取到的数据长度  
        int len;  
        // 输出的文件流  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);  
        // 开始读取  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        // 完毕，关闭所有链接  
        os.close();  
        is.close();  
    }   
  
}

