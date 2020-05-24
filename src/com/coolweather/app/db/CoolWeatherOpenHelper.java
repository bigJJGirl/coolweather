package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	

	/*** Province�������*/
	//id ��������������province_name ��ʾʡ����province_code ��ʾʡ������
	public static final String CREATE_PROVINCE = "create table Province ("
		+ "id integer primary key autoincrement, " 
		+ "province_name text, " 
		+ "province_code text)";
	
	/*** City�������*/
	//id ��������������city_name ��ʾ��������city_code ��ʾ�м����ţ�province_id ��City ����� Province ������
	public static final String CREATE_CITY = "create table City ("
		+ "id integer primary key autoincrement, " 
		+ "city_name text, " 
		+ "city_code text, " 
		+ "province_id integer)";

	/*** County�������*/
	//�� id ��������������county_name ��ʾ������county_code ��ʾ�ؼ����ţ�city_id ��County ����� City ������
	public static final String CREATE_COUNTY = "create table County ("
		+ "id integer primary key autoincrement, " 
		+ "county_name text, " 
		+ "county_code text, " 
		+ "city_id integer)";


	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE); // ����Province��
		db.execSQL(CREATE_CITY); // ����City��
		db.execSQL(CREATE_COUNTY); // ����County�� 
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ } 
	
}