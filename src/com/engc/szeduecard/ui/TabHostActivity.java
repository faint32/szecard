package com.engc.szeduecard.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.TabHost;

import com.engc.szeduecard.R;
import com.engc.szeduecard.widget.BadgeView;

/**
 * 
 * @ClassName: TabHostActivity
 * @Description: 动态构造底部菜单栏
 * @author wutao
 * @date 2013-10-10 下午2:43:26
 * 
 */
public class TabHostActivity extends TabActivity {
	TabHost tabHost;
	private RadioButton main_tab_home, main_tab_record, main_tab_mine,
			main_tab_more;
	private Resources rs = null;
	private String appName, appIcon;
	private BadgeView bv_audit_holiday;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabmenu);
		appName = getIntent().getStringExtra("appName");
		appIcon = getIntent().getStringExtra("appIcon");

		rs = this.getResources();
		initTab();
		init();
		//initBadgeView();
	}

	/**
	 * 初始化通知信息标签控件
	 */
	private void initBadgeView() {
		bv_audit_holiday = new BadgeView(this, main_tab_home);
		bv_audit_holiday.setBackgroundResource(R.drawable.widget_count_bg);
		bv_audit_holiday.setIncludeFontPadding(false);
		bv_audit_holiday.setGravity(Gravity.CENTER);
		bv_audit_holiday.setTextSize(8f);
		bv_audit_holiday.setText("2");
		bv_audit_holiday.setTextColor(Color.WHITE);
		TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		anim.setInterpolator(new BounceInterpolator());
		anim.setDuration(1000);
		bv_audit_holiday.show(anim);

	}

	/**
	 * 初始化tabhost 视图
	 */
	public void init() {
		main_tab_home = (RadioButton) findViewById(R.id.main_footbar_index);
		main_tab_record = (RadioButton) findViewById(R.id.main_footbar_record);
		main_tab_mine = (RadioButton) findViewById(R.id.main_footbar_mine);
		main_tab_more = (RadioButton) findViewById(R.id.main_footbar_more);
		main_tab_home.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag("home");
				initRadioButton(0x01);

			}
		});

		main_tab_record.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag("record");
				initRadioButton(0x02);

			}
		});
		main_tab_mine.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag("mine");
				initRadioButton(0x03);

			}
		});
		main_tab_more.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag("more");
				initRadioButton(0x04);

			}
		});

	}

	/**
	 * 初始化tabhost
	 */
	public void initTab() {
		tabHost = getTabHost();
		tabHost.addTab(tabHost
				.newTabSpec("home")
				.setIndicator("home")
				.setContent(
						new Intent(this, Main.class).putExtra("appName",
								appName).putExtra("appIcon", appIcon)));
		tabHost.addTab(tabHost.newTabSpec("record").setIndicator("record")
				.setContent(new Intent(this, Rocord.class)));
		tabHost.addTab(tabHost.newTabSpec("mine").setIndicator("mine")
				.setContent(new Intent(this, Mine.class)));
		tabHost.addTab(tabHost.newTabSpec("more").setIndicator("more")
				.setContent(new Intent(this, More.class)));

	}

	/**
	 * 根据状态设置radiobutton
	 * 
	 * @param code
	 */
	public void initRadioButton(int code) {

		switch (code) {
		case 0x01: // 首页 home
			main_tab_home.setChecked(true);
			main_tab_record.setChecked(false);
			main_tab_mine.setChecked(false);
			main_tab_more.setChecked(false);
			main_tab_home.setTextColor(rs.getColor(R.color.footbar_title));
			main_tab_record.setTextColor(Color.WHITE);
			main_tab_mine.setTextColor(Color.WHITE);
			main_tab_more.setTextColor(Color.WHITE);
			break;
		case 0x02: // 账单 record
			main_tab_home.setChecked(false);
			main_tab_record.setChecked(true);
			main_tab_mine.setChecked(false);
			main_tab_more.setChecked(false);
			main_tab_home.setTextColor(Color.WHITE);
			main_tab_record.setTextColor(rs.getColor(R.color.footbar_title));
			main_tab_mine.setTextColor(Color.WHITE);
			main_tab_more.setTextColor(Color.WHITE);
			break;
		case 0x03: // 我的 mine
			main_tab_home.setChecked(false);
			main_tab_record.setChecked(false);
			main_tab_mine.setChecked(true);
			main_tab_more.setChecked(false);
			main_tab_home.setTextColor(Color.WHITE);
			main_tab_record.setTextColor(Color.WHITE);
			main_tab_mine.setTextColor(rs.getColor(R.color.footbar_title));
			main_tab_more.setTextColor(Color.WHITE);

			break;

		default: // 更多 more
			main_tab_home.setChecked(false);
			main_tab_record.setChecked(false);
			main_tab_mine.setChecked(false);
			main_tab_more.setChecked(true);
			main_tab_home.setTextColor(Color.WHITE);
			main_tab_record.setTextColor(Color.WHITE);
			main_tab_mine.setTextColor(Color.WHITE);
			main_tab_more.setTextColor(rs.getColor(R.color.footbar_title));
			break;
		}
	}
}
