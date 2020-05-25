package com.coolweather.app.util;

public interface HttpCallbackListener {
	
	void onFinish(String response);		
	//onFinish()方法表示当服务器成功响应我们请求的时候调用，参数代表着服务器返回的数据
	
	
	void onError(Exception e);		
	//onError()表示当进行网络操作出现错误的时候调用，参数记录着错误的详细信息

}
