package com.kprd.date.history.national.entity;

public class NationalEntity {
	/**国家编号*/
	private Integer cid;
	/**国家名*/
	private String cn;
	/***国家名（英文）*/
	private String en;
	/**国家名（繁体）*/
	private String gbk;
	/**所属区域编号*/
	private Integer loc;
	
	
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getEn() {
		return en;
	}
	public void setEn(String en) {
		this.en = en;
	}
	public String getGbk() {
		return gbk;
	}
	public void setGbk(String gbk) {
		this.gbk = gbk;
	}
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public Integer getLoc() {
		return loc;
	}
	public void setLoc(Integer loc) {
		this.loc = loc;
	}
	
	
	
}
