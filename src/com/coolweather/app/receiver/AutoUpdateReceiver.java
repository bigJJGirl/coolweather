package com.coolweather.app.receiver;

import com.coolweather.app.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {
	
	//ֻ���� onReceive()�������ٴ�ȥ���� AutoUpdateService��
	//�Ϳ���ʵ�ֺ�̨��ʱ���µĹ�����
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
		}
}
