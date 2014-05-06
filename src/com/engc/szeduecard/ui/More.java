package com.engc.szeduecard.ui;

import android.os.Bundle;
import android.view.KeyEvent;

import com.engc.szeduecard.R;
import com.engc.szeduecard.common.UIHelper;

/**
 * 
 * @ClassName: More
 * @Description: 更多activity
 * @author wutao
 * @date 2013-10-10 下午5:24:48
 * 
 */
public class More extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 是否退出应用
			UIHelper.Exit(this);
		}
		return flag;
	}
}
