package com.engc.szeduecard.ui;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.engc.szeduecard.config.AppManager;

/**
 * 应用程序Activity的基类
 * 
 * @author samwu	
 * @version 1.0
 * @created 2013.4.1
 */
public class BaseActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

}
