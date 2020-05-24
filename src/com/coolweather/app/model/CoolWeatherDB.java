package com.coolweather.app.model;


/**
 * 这个类将会把一些常用的数据库操作封装起来，方便后面使用
 * **/


//单例：保证一个类仅有一个实例，并提供一个访问它的全局访问点。
/***CoolWeatherDB 是一个单例类，我们将它的构造方法私有化，
 * 并提供了一个getInstance()方法来获取 CoolWeatherDB 的实例，
 * 这样就可以保证全局范围内只会有一个CoolWeatherDB 的实例。***/

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	/**
	* 数据库名
	*/
	public static final String DB_NAME = "cool_weather";
	/**
	* 数据库版本
	*/
	public static final int VERSION = 1;
	
	
	
	
	private static CoolWeatherDB coolWeatherDB;
	
	
	private SQLiteDatabase db;// 是一个抽象类，想要使用它的话就需要创建一个自己的帮助类去继承它
		//有两个抽象方法，分别是onCreate()和 onUpgrade()，
		//必须在自己的帮助类里面重写这两个方法，然后分别在这两个方法中去实现创建、升级数据库的逻辑。
	
	
	/**
	* 将构造方法私有化
	*/
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,	DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
		/**getReadableDatabase() 和getWritableDatabase()。
		 * 这两个方法都可以创建或打开一个现有的数据库（如果数据库已存在则直接打开，否则创建一个新的数据库），
		 * 并返回一个可对数据库进行读写操作的对象。
		 * 不同的是，当数据库不可写入的时候（如磁盘空间已满）
		 * getReadableDatabase()方法返回的对象将以只读的方式去打开数据库，
		 * 而 getWritableDatabase()方法则将出现异常。**/
		
		/**第一个参数是 Context，这个没什么好说的，必须要有它才能对数据库进行操作。
		 * 第二个参数是数据库名，创建数据库时使用的就是这里指定的名称。
		 * 第三个参数允许我们在查询数据的时候返回一个自定义的 Cursor，一般都是传入 null。
		 * 第 四 个 参 数 表 示 当 前 数 据 库 的 版 本 号 ， 可 用 于 对 数 据 库 进 行 升 级 操 作 。**/
	}
	
	
	/**
	* 获取CoolWeatherDB的实例。
	*/
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	
	/**
	* 将Province实例存储到数据库。
	* 存储省份数据
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
	* 从数据库读取全国所有的省份信息。
	* 读取所有省份数据
	*/
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);//查询
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
	* 将City实例存储到数据库。存储城市数据
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
	* 从数据库读取某省下所有的城市信息。读取某省内所有城市数据
	*/
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();//对数据进行查询。
		Cursor cursor = db.query("City", null, "province_id = ?",new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
					//getColumnIndex()方法获取到某一列在表中对应的位置索引，
					//然后将这个索引传入到相应的取值方法中，就可以得到从数据库中读取到的数据了
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
	* 将County实例存储到数据库。存储县数据
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
	* 从数据库读取某城市下所有的县信息。读取某市内所有县的数据
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
