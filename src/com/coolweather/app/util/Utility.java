package com.coolweather.app.util;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.text.TextUtils;



/***
 * 由于服务器返回的省市县数据都是
 * “代号|城市,代号|城市”这种格式的，
 * 最好再提供一个工具类来解析和处理这种数据**/


//解析的规则就是先按逗号分隔，再按单竖线分隔，接着将解析出来的数据设置到实体类中，
//最后调用 CoolWeatherDB 中的三个 save()方法将数据存储到相应的表中

public class Utility {
	
	
	/**
	* 解析和处理服务器返回的省级数据
	*/
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
		
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					
					// 将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
					}
				return true;
				} 
			}
		return false;
	}
	
	
	
	
	/**
	* 解析和处理服务器返回的市级数据
	*/
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,	String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
	
					// 将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
					}
				return true;
				} 
			}
		return false;
	}
	
	
	/**
	* 解析和处理服务器返回的县级数据
	*/
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
	
						// 将解析出来的数据存储到County表
					coolWeatherDB.saveCounty(county);
					}
				return true;
				} 
			}
		return false;
	}

}
