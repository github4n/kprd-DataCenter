package com.kprd.date.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 抓取项目jedis工具类
 * @author Administrator
 *
 */
public class JedisUtilForFetch {
	
	/**
	 * 添加方法
	 * @param dbIndex 数据库下标
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("deprecation")
	public static void insert(Integer dbIndex,String key,String value) {
		// 主机地址  
        String host = "192.168.0.200";  
        // 构建连接池配置信息  
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();  
        // 设置最大连接数  
        jedisPoolConfig.setMaxTotal(1024);  
        // 超时时间  
        int timeout = 1000;  
        // 授权密码  
        String password = "123456";  
        // 构建连接池  
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, 6379, timeout, password);  
        // 从连接池中获取连接  
        Jedis jedis = jedisPool.getResource();  
        jedis.select(dbIndex);
        jedis.set(key, value);
        // 将连接还回到连接池中  
        jedisPool.returnResource(jedis);  
        // 释放连接池  
        jedisPool.close();
	}
	
	/**
	 * 删除数据
	 * @param dbIndex
	 * @param key
	 */
	@SuppressWarnings("deprecation")
	public static void remove(Integer dbIndex,String key) {
		// 主机地址  
        String host = "192.168.0.200";  
        // 构建连接池配置信息  
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();  
        // 设置最大连接数  
        jedisPoolConfig.setMaxTotal(1024);  
        // 超时时间  
        int timeout = 1000;  
        // 授权密码  
        String password = "123456";  
        // 构建连接池  
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, 6379, timeout, password);  
        // 从连接池中获取连接  
        Jedis jedis = jedisPool.getResource();  
        jedis.select(dbIndex);
        jedis.del(key);
        // 将连接还回到连接池中  
        jedisPool.returnResource(jedis);  
        // 释放连接池  
        jedisPool.close();
	}
	
	/**
	 * 测试用
	 * @param dbIndex
	 * @param key
	 */
	public static String getKey(Integer dbIndex,String key) {
		// 主机地址  
        String host = "192.168.0.200";  
        // 构建连接池配置信息  
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();  
        // 设置最大连接数  
        jedisPoolConfig.setMaxTotal(1024);  
        // 超时时间  
        int timeout = 1000;  
        // 授权密码  
        String password = "123456";  
        // 构建连接池  
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, 6379, timeout, password);  
        // 从连接池中获取连接  
        Jedis jedis = jedisPool.getResource();  
        jedis.select(dbIndex);
        String result = jedis.get(key);
        // 将连接还回到连接池中  
        jedisPool.returnResource(jedis); 
        // 释放连接池  
        jedisPool.close();
        return result;
	}
	
	public static void flushDb(Integer dbIndex) {
		// 主机地址  
        String host = "192.168.0.200";  
        // 构建连接池配置信息  
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();  
        // 设置最大连接数  
        jedisPoolConfig.setMaxTotal(1024);  
        // 超时时间  
        int timeout = 1000;  
        // 授权密码  
        String password = "123456";  
        // 构建连接池  
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, 6379, timeout, password);  
        // 从连接池中获取连接  
        Jedis jedis = jedisPool.getResource();  
        jedis.select(dbIndex);
        jedis.flushDB();
        // 将连接还回到连接池中  
        jedisPool.returnResource(jedis);  
        // 释放连接池  
        jedisPool.close();
		
	}
	
	public static void main(String[] args) {
		remove(6, "20170906gamecenter");
		String s = getKey(6, "20170906gamecenter");
		System.out.println(s);
		
//		String ss = getKey(1, "20170706gamecenter");
//		for(int i=0;i<16;i++) {
//			flushDb(i);//12345
//		}
	}
}
