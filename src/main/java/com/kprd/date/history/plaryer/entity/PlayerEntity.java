package com.kprd.date.history.plaryer.entity;
/***
 * 参赛球员
 * @author Administrator
 *
 */
public class PlayerEntity {
	
	/**球员编号*/
	private Integer pid;
	/**球员名称*/
	private String pname;
	/**位置*/
	private String point;
	/**球衣号*/
	private Integer number;
	
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	

}
