package com.engc.szeduecard.ui;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.szeduecard.R;
import com.engc.szeduecard.common.UIHelper;

/**
 * 
 * @ClassName: AppDetail
 * @Description: 应用详情
 * @author wutao
 * @date 2013-10-12 上午9:02:00
 * 
 */
public class AppDetail extends BaseActivity {
	private ImageView imgAppIcon;
	private TextView txtAppDetailHeaderTitle, txtAppName, txtAppVersion,
			txtAppSize,txtAppDesc;
	private Button btnAddAppToMineAPP;
	private String appName, appVersion, appSize, appIcon,appDesc;
    private Resources res=null; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appdetail);
		 res = getResources();
		appName = this.getIntent().getStringExtra("appName");
		appVersion = this.getIntent().getStringExtra("appVersion");
		appSize = this.getIntent().getStringExtra("appSize");
		appIcon = this.getIntent().getStringExtra("appIcon");
		appDesc=this.getIntent().getStringExtra("appDesc");
		
		initView();

	}

	private void initView() {
		imgAppIcon = (ImageView) findViewById(R.id.app_detail_icon);
		txtAppDetailHeaderTitle = (TextView) findViewById(R.id.txt_app_detial_header_tile);
		txtAppName = (TextView) findViewById(R.id.txt_app_detail_name);
		txtAppVersion = (TextView) findViewById(R.id.txt_app_detail_version);
		txtAppSize = (TextView) findViewById(R.id.txt_app_detail_size);
		txtAppDesc=(TextView) findViewById(R.id.txt_app_detail_desc);
		
		btnAddAppToMineAPP = (Button) findViewById(R.id.btn_add_app);
        int id=res.getIdentifier(
				getPackageName() +":drawable/"+appIcon, null, getPackageName());
		imgAppIcon.setImageResource(id);
		txtAppDetailHeaderTitle.setText(appName);
		txtAppSize.setText("大小:"+appSize);
		txtAppVersion.setText("版本:"+appVersion);
		txtAppName.setText(appName);
		txtAppDesc.setText(appDesc);

		btnAddAppToMineAPP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showIndex(AppDetail.this, appName, appIcon);
			}
		});

	}
}
