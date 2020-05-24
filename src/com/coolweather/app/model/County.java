package com.coolweather.app.model;

public class County {
	private int id;		//自增长主键
	private String countyName;	//县名
	private String countyCode;	//县级代号
	private int cityId;	//County 表关联 City 表的外键
	
	
	
	public int getId() {	return id;	}
	public void setId(int id) {	this.id = id;	}
	
	public String getCountyName() {	return countyName;	}
	public void setCountyName(String countyName) {	this.countyName = countyName;	}
	
	public String getCountyCode() {	return countyCode;	}
	public void setCountyCode(String countyCode) {	this.countyCode = countyCode;	}	
	
	public int getCityId() {	return cityId;	}
	public void setCityId(int cityId) {	this.cityId = cityId;	}

}
