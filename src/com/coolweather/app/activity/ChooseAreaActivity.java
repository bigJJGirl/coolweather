package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	
	private List<Province> provinceList;	/** 省列表*/

	private List<City> cityList;	/** 市列表*/

	private List<County> countyList;	/** 县列表*/
	
	

	private Province selectedProvince;	/** 选中的省份*/

	private City selectedCity;	/** 选中的城市*/

	private int currentLevel;	/** 当前选中的级别*/
	
	
	/**
	* 是否从WeatherActivity中跳转过来。
	* 只有已经选择了城市且不是从 WeatherActivity 跳转过来的时候才会直接跳转到 WeatherActivity
	*/
	private boolean isFromWeatherActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		///从 ChooseAreaActivity 跳转到 WeatherActivity
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if (prefs.getBoolean("city_selected", false)&& !isFromWeatherActivity) {
				
				//读取 city_selected 标志位，如果为 true 就说明当前已经选择过城市了，直接跳转到 WeatherActivity 即可
				// 已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
				finish();
				return;
			}
				
				
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);//元老级三行代码
		
		
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		
			//初始化了 ArrayAdapter，将它设置为 ListView 的适配器
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		
			//获取到了 CoolWeatherDB 的实例，并给 ListView 设置了点击事件
			/**点击了某个省的时候会进入到 ListView 的 onItemClick()方法中，
			 * 这个时候会根据当前的级别来判断是去调用 queryCities()方法还是 queryCounties()方法，
			 * queryCities()方法是去查询市级数据，而 queryCounties()方法是去查询县级数据，
			 * 这两个方法内部的流程和queryProvinces()方法基本相同**/
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,	long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} 
				else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					
					//当前级别是 LEVEL_COUNTY，就启动 WeatherActivity，并把当前选中县的县级代号传递过去
					
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,	WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		
		queryProvinces(); // 加载省级数据
		/**queryProvinces()方法的
		 * 内部会首先调用 CoolWeatherDB 的 loadProvinces()方法来从数据库中读取省级数据，
		 * 如果读取到了就直接将数据显示到界面上，如果没有读取到就调用queryFromServer()方法来从服务器上查询数据**/
	}
	
	
	
	/**
	* 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {			//从数据库中读取省级数据，如果读取到了就直接将数据显示到界面上
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} 
		else {									//没有读取到就调用queryFromServer()方法来从服务器上查询数据
			queryFromServer(null, "province");
			} 
	}

	/**
	* 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}
		
		else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		} 
	}
	
	
	
	/**
	* 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} 
		
		else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	
	/**
	* 根据传入的代号和类型从服务器上查询省市县数据。
	*/
	private void queryFromServer(final String code, final String type) {
		String address;
		
		/****
		 * 根据传入的参数来拼装查询地址
		 * **/
		
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
		} 
		
		else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		
		//sendHttpRequest向服务器发送请求
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				
				//handleProvincesResponse()方法来解析和处理服务器返回的数据，并存储到数据库中
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,	response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,	response, selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑，实现从子线程切换到主线程，
					//它的实现原理其实也是基于异步消息处理机制的。
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							
							/**在解析和处理完数据之后，再次调用了 queryProvinces()方法
							 * 来重新加载省级数据，
							 * 由于 queryProvinces()方法牵扯到了 UI 操作，因此必须要在主线程中调用**/
							//现在数据库中已经存在了数据，
							//因此调用 queryProvinces()就会直接将数据显示到界面上了。
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							} 
						}
					});
				}

			}	
			
			
			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,"加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	
	
	/**
	* 显示进度对话框
	*/
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	
	/**
	* 关闭进度对话框
	*/
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		} 
	}
	
	
	
	
	/**
	* 捕获Back按键，重写了 onBackPressed()方法来覆盖默认 Back 键的行为
	* 根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	*/
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
				}
			finish();
		} 
	}
}
