package com.coolweather.app.model;


/**
 * ����ཫ���һЩ���õ����ݿ������װ�������������ʹ��
 * **/


//��������֤һ�������һ��ʵ�������ṩһ����������ȫ�ַ��ʵ㡣
/***CoolWeatherDB ��һ�������࣬���ǽ����Ĺ��췽��˽�л���
 * ���ṩ��һ��getInstance()��������ȡ CoolWeatherDB ��ʵ����
 * �����Ϳ��Ա�֤ȫ�ַ�Χ��ֻ����һ��CoolWeatherDB ��ʵ����***/

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	/**
	* ���ݿ���
	*/
	public static final String DB_NAME = "cool_weather";
	/**
	* ���ݿ�汾
	*/
	public static final int VERSION = 1;
	
	
	
	
	private static CoolWeatherDB coolWeatherDB;
	
	
	private SQLiteDatabase db;// ��һ�������࣬��Ҫʹ�����Ļ�����Ҫ����һ���Լ��İ�����ȥ�̳���
		//���������󷽷����ֱ���onCreate()�� onUpgrade()��
		//�������Լ��İ�����������д������������Ȼ��ֱ���������������ȥʵ�ִ������������ݿ���߼���
	
	
	/**
	* �����췽��˽�л�
	*/
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,	DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
		/**getReadableDatabase() ��getWritableDatabase()��
		 * ���������������Դ������һ�����е����ݿ⣨������ݿ��Ѵ�����ֱ�Ӵ򿪣����򴴽�һ���µ����ݿ⣩��
		 * ������һ���ɶ����ݿ���ж�д�����Ķ���
		 * ��ͬ���ǣ������ݿⲻ��д���ʱ������̿ռ�������
		 * getReadableDatabase()�������صĶ�����ֻ���ķ�ʽȥ�����ݿ⣬
		 * �� getWritableDatabase()�����򽫳����쳣��**/
		
		/**��һ�������� Context�����ûʲô��˵�ģ�����Ҫ�������ܶ����ݿ���в�����
		 * �ڶ������������ݿ������������ݿ�ʱʹ�õľ�������ָ�������ơ�
		 * �������������������ڲ�ѯ���ݵ�ʱ�򷵻�һ���Զ���� Cursor��һ�㶼�Ǵ��� null��
		 * �� �� �� �� �� �� ʾ �� ǰ �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� ��**/
	}
	
	
	/**
	* ��ȡCoolWeatherDB��ʵ����
	*/
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	
	/**
	* ��Provinceʵ���洢�����ݿ⡣
	* �洢ʡ������
	*/
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		} 
	}
	
	
	/**
	* �����ݿ��ȡȫ�����е�ʡ����Ϣ��
	* ��ȡ����ʡ������
	*/
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);//��ѯ
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	
	
	/**
	* ��Cityʵ���洢�����ݿ⡣�洢��������
	*/
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		} 
	}
	
	
	/**
	* �����ݿ��ȡĳʡ�����еĳ�����Ϣ����ȡĳʡ�����г�������
	*/
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();//�����ݽ��в�ѯ��
		Cursor cursor = db.query("City", null, "province_id = ?",new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
					//getColumnIndex()������ȡ��ĳһ���ڱ��ж�Ӧ��λ��������
					//Ȼ������������뵽��Ӧ��ȡֵ�����У��Ϳ��Եõ������ݿ��ж�ȡ����������
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	
	/**
	* ��Countyʵ���洢�����ݿ⡣�洢������
	*/
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		} 
	}
	
	
	
	/**
	* �����ݿ��ȡĳ���������е�����Ϣ����ȡĳ���������ص�����
	*/
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?",new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
	return list;
	}

}
