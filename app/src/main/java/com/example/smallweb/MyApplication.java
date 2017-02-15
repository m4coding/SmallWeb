package com.example.smallweb;

import android.app.Application;

/**
 * 对应一个应用程序而言的MyApplication类
 */
public class MyApplication extends Application {

	private MainActivity mMainActivity = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	// 保存MainActivity，用于其他之用
	public void setMainActivity(MainActivity mainActivity) {
		mMainActivity = mainActivity;
	}

	// 获取MainActivity
	public MainActivity getMainActivity() {
		return mMainActivity;
	}
}