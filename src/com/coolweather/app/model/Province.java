package com.coolweather.app.model;

public class Province {
	private int id;		//自增长主键
	private String provinceName; //省名
	private String provinceCode;	//省级代号
	
	public int getId() {	return id;	}
	public void setId(int id) {	this.id = id;	}
	
	public String getProvinceName() {	return provinceName;	}
	public void setProvinceName(String provinceName) {	this.provinceName = provinceName;	}
	
	public String getProvinceCode() {	return provinceCode;	}
	public void setProvinceCode(String provinceCode) {	this.provinceCode = provinceCode;	}

}
