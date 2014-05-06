package com.engc.szeduecard.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.engc.szeduecard.R;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.ui.Login;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * 
 * @author wutao
 * @version 1.0
 * @created 2012-3-21
 */
public class AppStart extends Activity {

	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 2000;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	private AppContext appContext;

	// private Handler mHandler;

	/**
	 * Handler:跳转到不同界面
	 */
	/*
	 * private Handler mHandler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { switch (msg.what) {
	 * case GO_HOME: goHome(); break; case GO_GUIDE: goGuide(); break; }
	 * super.handleMessage(msg); } };
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
		appContext = (AppContext) getApplication();
		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
				// mHandler = new Handler() {
				//
				// @Override
				// public void handleMessage(Message msg) {
				// switch (msg.what) {
				// case GO_HOME:
				// goHome();
				// break;
				// case GO_GUIDE:
				// goGuide();
				// break;
				// }
				// super.handleMessage(msg);
				// }
				// };
				// init();

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

		// 兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
		AppContext appContext = (AppContext) getApplication();
		String cookie = appContext.getProperty("cookie");
		if (StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if (!StringUtils.isEmpty(cookie_name)
					&& !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain", "cookie_name",
						"cookie_value", "cookie_version", "cookie_path");
			}
		}

	}

	/**
	 * 跳转到...
	 */
	private void redirectTo() {
		Intent intent = new Intent(this, Login.class);
		startActivity(intent);
		finish();
	}

	

	/*
	 * private void init() { // 读取SharedPreferences中需要的数据 //
	 * 使用SharedPreferences来记录程序的使用次数 SharedPreferences preferences =
	 * getSharedPreferences( SHAREDPREFERENCES_NAME, MODE_PRIVATE);
	 * 
	 * // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值 isFirstIn =
	 * preferences.getBoolean("isFirstIn", true);
	 * 
	 * // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面 if (!isFirstIn) { //
	 * 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
	 * mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS); } else {
	 * mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS); }
	 * 
	 * }
	 */
	/*
	 * private void goHome() {
	 * 
	 * Intent intent = null; AdsList adsList = appContext
	 * .getAdsListForFlash(AppConfig.MOBILE_LOADING_SPLASH_ACTIVITY_ADS); if
	 * (adsList.getadsList().size() > 0) { intent = new Intent(AppStart.this,
	 * ShowAds.class); intent.putExtra("adsIMGURL",
	 * adsList.getadsList().get(0).getImp()); } else { intent = new
	 * Intent(AppStart.this, Main.class); }
	 * 
	 * AppStart.this.startActivity(intent); AppStart.this.finish(); }
	 */

	/*
	 * private void goGuide() { Intent intent = new Intent(AppStart.this,
	 * GuideActivity.class); AppStart.this.startActivity(intent);
	 * AppStart.this.finish(); }
	 */
}