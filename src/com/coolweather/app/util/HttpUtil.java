package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//和服务器的交互是必不可少的

/**通用的网络操作提取到一个公共的类里，
 * 并提供一个静态方法，
 * 当想要发起网络请求的时候只需简单地调用一下这个方法即可
 * */


public class HttpUtil {
	
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener) {
		//HttpCallbackListener 接口来回调服务返回的结果
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
						// 回调onFinish()方法
						/**子线程中是无法通过return 语句来返回数据的，
						 * 因此这里我们将服务器响应的数据传入了 HttpCallbackListener 的onFinish()方法中，
						 * 如果出现了异常就将异常原因传入到 onError()方法中**/
						listener.onFinish(response.toString());
					} } 
				catch (Exception e) {
						if (listener != null) {
							// 回调onError()方法
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
