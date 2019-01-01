package com.kprd.date.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.kprd.common.utils.HttpClientUtil;

public class XXX {

	public static void main(String[] args) throws Exception {
//		Map<String,String> param = new HashMap<String,String>();
//		param.put("Type","CHD");
//		param.put("ID","0022512");
//		param.put("lastnum","300");
//		param.put("r",String.valueOf(Math.random()));
//		//////////////////通过 http请求有料接口//////////////////////
//		
//		String jsonStr = HttpClientUtil.doGet("http://hq2fls.eastmoney.com/EM_Quote2010PictureApplication/Flash.aspx", param);
//		System.out.println(jsonStr);
		
		URL url = new URL("http://hq2fls.eastmoney.com/EM_Quote2010PictureApplication/Flash.aspx?Type=CHD&ID=0022512&lastnum=300&r=0.8216137830168009");  
	    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();  
	    httpConn.setRequestMethod("GET");    
	    httpConn.setDoInput(true);    
//	    httpConn.setDoOutput(true);    
//	    httpConn.setInstanceFollowRedirects(true);    
//	    httpConn.setUseCaches(false);
//	    httpConn.setRequestProperty("content-type", "text/html;charset=gbk");
//	    httpConn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//	    httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36");
//	    httpConn.setRequestProperty("Connection", "Keep-Alive");
//	    httpConn.setRequestProperty("Accept-Language", Locale.getDefault().toString());
//        httpConn.setRequestProperty("Accept-Charset", "gbk");
	    httpConn.connect();
	    InflaterInputStream iis = new InflaterInputStream(httpConn.getInputStream());
	    ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
	    int i = 1024;  
        byte[] buf = new byte[i];  

        while ((i = iis.read(buf, 0, i)) > 0) {  
            o.write(buf, 0, i);  
        }  
        byte[] data = o.toByteArray();
        String dataResult = new String(data);
        o.close();  
        iis.close();    
        httpConn.disconnect();
        
	}
}
