package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//�ͷ������Ľ����Ǳز����ٵ�

/**ͨ�õ����������ȡ��һ�����������
 * ���ṩһ����̬������
 * ����Ҫ�������������ʱ��ֻ��򵥵ص���һ�������������
 * */


public class HttpUtil {
	
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener) {
		//HttpCallbackListener �ӿ����ص����񷵻صĽ��
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
				
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new	InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
				
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
				
					if (listener != null) {
						// �ص�onFinish()����
						/**���߳������޷�ͨ��return ������������ݵģ�
						 * ����������ǽ���������Ӧ�����ݴ����� HttpCallbackListener ��onFinish()�����У�
						 * ����������쳣�ͽ��쳣ԭ���뵽 onError()������**/
						listener.onFinish(response.toString());
					} } 
				catch (Exception e) {
						if (listener != null) {
							// �ص�onError()����
							listener.onError(e);
					} }
				
				finally {
					if (connection != null) {
						connection.disconnect();
					} } 
				}
			}).start();
		}

}
