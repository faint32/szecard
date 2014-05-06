package com.engc.szeduecard.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.szeduecard.R;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppContext;

/**
 * 
 * @ClassName: Prepaid
 * @Description: 充值activity
 * @author wutao
 * @date 2013-10-11 下午4:19:51
 * 
 */

public class Prepaid extends BaseActivity {
	private Button btnPrepaid;
	private TextView txtPrepaiMan;
	private AppContext ac;
	private Button imgBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prepaid);
		ac = (AppContext) getApplication();
		initView();

	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		imgBack = (Button) findViewById(R.id.ic_back);
		imgBack.setOnClickListener(UIHelper.finish(this));
		btnPrepaid = (Button) findViewById(R.id.btnprepaid);
		txtPrepaiMan = (TextView) findViewById(R.id.txtprepaidman);
		txtPrepaiMan.setText(ac.getLoginInfo().getUsername());
		btnPrepaid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}
}
