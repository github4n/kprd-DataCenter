package com.kprd.date.history.match.entity;

import java.util.List;

public class Interface1002 {

	private String msg;
	
	private Integer code;

	private String desc;
	
	private Long timestamp;
	
	private String sid;
	
	private List<MatchEntity> row;
	
	private List<MatchEntity> r;

	public List<MatchEntity> getR() {
		return r;
	}

	public void setR(List<MatchEntity> r) {
		this.r = r;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}	

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public List<MatchEntity> getRow() {
		return row;
	}

	public void setRow(List<MatchEntity> row) {
		this.row = row;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
	
	
}
