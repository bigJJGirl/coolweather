package com.coolweather.app.util;

public interface HttpCallbackListener {
	
	void onFinish(String response);		
	//onFinish()������ʾ���������ɹ���Ӧ���������ʱ����ã����������ŷ��������ص�����
	
	
	void onError(Exception e);		
	//onError()��ʾ����������������ִ����ʱ����ã�������¼�Ŵ������ϸ��Ϣ

}
