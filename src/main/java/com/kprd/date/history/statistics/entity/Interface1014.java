package com.kprd.date.history.statistics.entity;

import java.util.List;

import com.kprd.date.history.game.entity.GameCEntity;

public class Interface1014 {

	public List<SummaryscoreEntity> getRow() {
		return row;
	}

	public void setRow(List<SummaryscoreEntity> row) {
		this.row = row;
	}

	public List<SummaryscoreEntity> getR() {
		return r;
	}

	public void setR(List<SummaryscoreEntity> r) {
		this.r = r;
	}
	private String $t;
	private String msg;
	
	private Integer code;

	private String desc;
	
	private String timestamp;
	
	private String sid;
	
	private List<SummaryscoreEntity> row;
	
	private List<SummaryscoreEntity> r;

	private GameCEntity c;

	public GameCEntity getC() {
		return c;
	}

	public void setC(GameCEntity c) {
		this.c = c;
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String get$t() {
		return $t;
	}

	public void set$t(String $t) {
		this.$t = $t;
	}

	
	
	
}
