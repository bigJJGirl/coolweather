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
	
	
	private List<Province> provinceList;	/** ʡ�б�*/

	private List<City> cityList;	/** ���б�*/

	private List<County> countyList;	/** ���б�*/
	
	

	private Province selectedProvince;	/** ѡ�е�ʡ��*/

	private City selectedCity;	/** ѡ�еĳ���*/

	private int currentLevel;	/** ��ǰѡ�еļ���*/
	
	
	/**
	* �Ƿ��WeatherActivity����ת������
	* ֻ���Ѿ�ѡ���˳����Ҳ��Ǵ� WeatherActivity ��ת������ʱ��Ż�ֱ����ת�� WeatherActivity
	*/
	private boolean isFromWeatherActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		///�� ChooseAreaActivity ��ת�� WeatherActivity
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if (prefs.getBoolean("city_selected", false)&& !isFromWeatherActivity) {
				
				//��ȡ city_selected ��־λ�����Ϊ true ��˵����ǰ�Ѿ�ѡ��������ˣ�ֱ����ת�� WeatherActivity ����
				// �Ѿ�ѡ���˳����Ҳ��Ǵ�WeatherActivity��ת�������Ż�ֱ����ת��
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
				finish();
				return;
			}
				
				
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);//Ԫ�ϼ����д���
		
		
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		
			//��ʼ���� ArrayAdapter����������Ϊ ListView ��������
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		
			//��ȡ���� CoolWeatherDB ��ʵ�������� ListView �����˵���¼�
			/**�����ĳ��ʡ��ʱ�����뵽 ListView �� onItemClick()�����У�
			 * ���ʱ�����ݵ�ǰ�ļ������ж���ȥ���� queryCities()�������� queryCounties()������
			 * queryCities()������ȥ��ѯ�м����ݣ��� queryCounties()������ȥ��ѯ�ؼ����ݣ�
			 * �����������ڲ������̺�queryProvinces()����������ͬ**/
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
					
					//��ǰ������ LEVEL_COUNTY�������� WeatherActivity�����ѵ�ǰѡ���ص��ؼ����Ŵ��ݹ�ȥ
					
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,	WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		
		queryProvinces(); // ����ʡ������
		/**queryProvinces()������
		 * �ڲ������ȵ��� CoolWeatherDB �� loadProvinces()�����������ݿ��ж�ȡʡ�����ݣ�
		 * �����ȡ���˾�ֱ�ӽ�������ʾ�������ϣ����û�ж�ȡ���͵���queryFromServer()�������ӷ������ϲ�ѯ����**/
	}
	
	
	
	/**
	* ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
	*/
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {			//�����ݿ��ж�ȡʡ�����ݣ������ȡ���˾�ֱ�ӽ�������ʾ��������
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} 
		else {									//û�ж�ȡ���͵���queryFromServer()�������ӷ������ϲ�ѯ����
			queryFromServer(null, "province");
			} 
	}

	/**
	* ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
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
	* ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
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
	* ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ�������ݡ�
	*/
	private void queryFromServer(final String code, final String type) {
		String address;
		
		/****
		 * ���ݴ���Ĳ�����ƴװ��ѯ��ַ
		 * **/
		
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
		} 
		
		else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		
		//sendHttpRequest���������������
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				
				//handleProvincesResponse()�����������ʹ�����������ص����ݣ����洢�����ݿ���
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,	response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,	response, selectedCity.getId());
				}
				if (result) {
					// ͨ��runOnUiThread()�����ص����̴߳����߼���ʵ�ִ����߳��л������̣߳�
					//����ʵ��ԭ����ʵҲ�ǻ����첽��Ϣ������Ƶġ�
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							
							/**�ڽ����ʹ���������֮���ٴε����� queryProvinces()����
							 * �����¼���ʡ�����ݣ�
							 * ���� queryProvinces()����ǣ������ UI ��������˱���Ҫ�����߳��е���**/
							//�������ݿ����Ѿ����������ݣ�
							//��˵��� queryProvinces()�ͻ�ֱ�ӽ�������ʾ���������ˡ�
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
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,"����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	
	
	/**
	* ��ʾ���ȶԻ���
	*/
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	
	/**
	* �رս��ȶԻ���
	*/
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		} 
	}
	
	
	
	
	/**
	* ����Back��������д�� onBackPressed()����������Ĭ�� Back ������Ϊ
	* ���ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳���
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
