package com.coolweather.app.model;

public class City {
	private int id;			//����������
	private String cityName; 	//������
	private String cityCode;	//�м�����
	private int provinceId;		//City ����� Province ������
	
	public int getId() {	return id;	}
	public void setId(int id) {	this.id = id;	}
	
	public String getCityName() {	return cityName;	}
	public void setCityName(String cityName) {	this.cityName = cityName;	}
	
	public String getCityCode() {	return cityCode;	}
	public void setCityCode(String cityCode) {	this.cityCode = cityCode;	}
	
	public int getProvinceId() {	return provinceId;	}
	public void setProvinceId(int provinceId) {	this.provinceId = provinceId;	}

}
