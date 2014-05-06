package com.engc.szeduecard.ui;

import com.engc.szeduecard.R;
import com.engc.szeduecard.common.UIHelper;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;




/**
 * 请假 详情
 * 
 * @ClassName: HolidayRecordDetail
 * @Description: TODO
 * @author wutao
 * @date 2013-10-31 上午10:19:43
 * 
 */
public class HolidayRecordDetail extends BaseActivity {
	private Resources rs = null;
	private Button imgBack;
	private TextView txtUserName, txtBeginDate, txtEndDate, txtHolidays,
			txtRemark, txtAuditStatus, txtAuditType, txtTitle;
	private String userName, beginDate, endDate, holidays, remark, holidayType,
			auditStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.holiday_record_detail);
		getIntentValue();
		initView();
		initData();

	}

	/**
	 * 获取intent 中 传的直
	 */
	private void getIntentValue() {
		userName = getIntent().getStringExtra("userName");
		beginDate = getIntent().getStringExtra("beginDate");
		endDate = getIntent().getStringExtra("endDate");
		holidays = getIntent().getStringExtra("holidays");
		remark = getIntent().getStringExtra("remark");
		holidayType = getIntent().getStringExtra("holidayType");
		auditStatus = getIntent().getStringExtra("auditStatus");
	}

	/**
	 * 初始化 视图
	 */
	private void initView() {
		txtUserName = (TextView) findViewById(R.id.txt_holiday_record_username);
		txtBeginDate = (TextView) findViewById(R.id.txt_holiday_record_begindate);
		txtEndDate = (TextView) findViewById(R.id.txt_holiday_record_enddate);
		txtHolidays = (TextView) findViewById(R.id.txt_holiday_record_days);
		txtRemark = (TextView) findViewById(R.id.txt_holiday_record_remark);
		txtAuditStatus = (TextView) findViewById(R.id.txt_holiday_record_audit_status);
		txtTitle = (TextView) findViewById(R.id.txt_holiday_record_title);
		txtAuditType = (TextView) findViewById(R.id.txt_holiday_record_holidaytype);
		imgBack = (Button) findViewById(R.id.btn_record_detail_back);
		imgBack.setOnClickListener(UIHelper.finish(this));

	}

	/**
	 * 初始化 至
	 */
	private void initData() {
		txtTitle.setText(userName + "的请假申请");
		txtUserName.setText(userName);
		txtBeginDate.setText(beginDate);
		txtEndDate.setText(endDate);
		txtHolidays.setText(holidays + "天");
		txtRemark.setText(remark);
		txtAuditStatus.setText(auditStatus);
		txtAuditType.setText(holidayType);

	}
}
