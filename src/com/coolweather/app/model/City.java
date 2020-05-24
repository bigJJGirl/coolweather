package com.coolweather.app.model;

public class City {
	private int id;			//自增长主键
	private String cityName; 	//城市名
	private String cityCode;	//市级代号
	private int provinceId;		//City 表关联 Province 表的外键
	
	public int getId() {	return id;	}
	public void setId(int id) {	this.id = id;	}
	
	public String getCityName() {	return cityName;	}
	public void setCityName(String cityName) {	this.cityName = cityName;	}
	
	public String getCityCode() {	return cityCode;	}
	public void setCityCode(String cityCode) {	this.cityCode = cityCode;	}
	
	public int getProvinceId() {	return provinceId;	}
	public void setProvinceId(int provinceId) {	this.provinceId = provinceId;	}

}
