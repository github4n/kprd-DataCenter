package com.kprd.date.history.odds.entity;

import java.util.List;

public class Interface2018 {
	
public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

private String msg;
	
	private Integer code;

	private String desc;
	
	private String sid;
	
	private Long timestamp;
	
	private String error;
	
	private Entity2018C c;
	
	private List<Object> row;
	
	private List<Entity2018R> r;

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

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Entity2018C getC() {
		return c;
	}

	public void setC(Entity2018C c) {
		this.c = c;
	}

	public List<Entity2018R> getR() {
		return r;
	}

	public void setR(List<Entity2018R> r) {
		this.r = r;
	}

	public void setRow(List<Object> row) {
		this.row = row;
	}

	public List<Object> getRow() {
		return row;
	}

}
