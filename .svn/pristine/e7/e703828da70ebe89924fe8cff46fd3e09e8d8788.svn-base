package com.kprd.date.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumetUtil {

	/**
     * 根据url获取doc对象
     * @param url
     * @return
     */
    public static Document getDocumentByUrl(String url) {
    	Document doc = null;
    	try {
    		Parser parser = new Parser();
    		parser.setEncoding(parser.getEncoding());
    		parser.setURL(url);
    		TagNameFilter filter = new TagNameFilter("html");
    		NodeList list = parser.parse(filter);
    		String html = list.toHtml();
    		doc = Jsoup.parse(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    
    /**
     * 将二进制数组保存到指定物理文件.
     * 
     * @param bytes
     * @param file
     * @return boolean true: 保存成功; false: 保存失败.
     */
    public static boolean toFile(byte[] bytes, File file) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           close(bos);
           close(fos);
        }
        return false;
    }
    
    /**
     * 静默关闭实现 Closeable 接口的对象.<br/>
     * 具体有: Nio Channel、 IO InputStream、 IO OutputStream、 IO Reader、 IO Writer
     * 
     * @param closeables 实现 Closeable 接口的对象.
     */
    public static void close(Closeable... closeables) {
        if (null != closeables) {
            for (Closeable clob : closeables) {
                try {
                    if (null != clob) {
                        clob.close();
                    }
                } catch (Throwable e) {
                	
                }
            }
        }
    }
}
