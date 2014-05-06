package com.engc.szeduecard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.engc.szeduecard.R;

/**
 * 
 * @ClassName: Notifications
 * @Description: 推送消息activity
 * @author wutao
 * @date 2013-10-12 下午3:52:27
 * 
 */
public class Notifications extends BaseActivity {
	private TextView txtNotificationsContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);
		txtNotificationsContent = (TextView) findViewById(R.id.txtnotication);
		Intent intent = getIntent();
		if (null != intent) {
			Bundle bundle = getIntent().getExtras();
			String title = bundle
					.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			txtNotificationsContent.setText(title + "提醒您：" + content);
		}

	}

}
