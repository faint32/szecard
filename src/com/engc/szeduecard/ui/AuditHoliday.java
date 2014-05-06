package com.engc.szeduecard.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.engc.szeduecard.R;
import com.engc.szeduecard.bean.Entity;
import com.engc.szeduecard.bean.Holidays;
import com.engc.szeduecard.bean.UserInfo;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;

/**
 * 审核假期
 * 
 * @ClassName: AuditHoliday
 * @Description: TODO
 * @author wutao
 * @date 2013-10-19 下午2:43:27
 * 
 */
public class AuditHoliday extends BaseActivity {
	private TextView txtHolidayPerson, txtHolidayStartDate, txtHolidayEndDate,
			txtHolidays, txtHolidayRemark, txtHolidayPersonClass,
			txtHolidayType;
	private AutoCompleteTextView actAuditOpinion;
	private RadioButton rdoAuditYes, rdoAuditNo;
	private Button btnSubmit;
	private String recordId;

	private AppContext ac;
	private AlertDialog commitDataDialog;
	private ImageView mIcon;
	private LinearLayout mLayout;
	private Animation mRotate;
	private LinearLayout loadingAuditDataLayout;
	private ImageView imgLoadingDataIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audit_holidays);
		ac = (AppContext) getApplication();
		recordId = getIntent().getStringExtra("recordId");
		initView();
		initAnimForLoadingData();

		try {
			initData();

		} catch (AppException e) {
			e.printStackTrace();
		}
		submitAuditHoliday();

	}

	/**
	 * 初始化加载数据ICon
	 */
	private void initAnimForLoadingData() {
		mRotate = AnimationUtils
				.loadAnimation(AuditHoliday.this, R.anim.rotate);
		imgLoadingDataIcon.startAnimation(mRotate);
	}

	/*
	 * 初始化 视图
	 */
	private void initView() {

		txtHolidayEndDate = (TextView) findViewById(R.id.txt_apply_hoilday_enddate);
		txtHolidayStartDate = (TextView) findViewById(R.id.txt_apply_holiday_startdate);
		txtHolidayPerson = (TextView) findViewById(R.id.txt_apply_holiday_leaveman);
		txtHolidayRemark = (TextView) findViewById(R.id.txt_apply_holiday_remark);
		txtHolidays = (TextView) findViewById(R.id.txt_apply_holiday_days);
		txtHolidayType = (TextView) findViewById(R.id.txt_apply_holiday_type);
		txtHolidayPersonClass = (TextView) findViewById(R.id.txtapply_holiday_person_class);
		actAuditOpinion = (AutoCompleteTextView) findViewById(R.id.actaudit_opinion);
		rdoAuditNo = (RadioButton) findViewById(R.id.rdoholiday_no);
		rdoAuditYes = (RadioButton) findViewById(R.id.rdoholiday_yes);
		btnSubmit = (Button) findViewById(R.id.btnauditholiday);
		loadingAuditDataLayout = (LinearLayout) findViewById(R.id.apply_holidays_data_loading_layout);
		imgLoadingDataIcon = (ImageView) findViewById(R.id.loading_audit_data_icon);

	}

	/**
	 * 初始化提交数据等待dialog
	 */
	private void initCommitDataDialog() {
		commitDataDialog = new AlertDialog.Builder(AuditHoliday.this).create();
		commitDataDialog.show();
		Window window = commitDataDialog.getWindow();
		window.setContentView(R.layout.apply_holiday_dialog);
		mIcon = (ImageView) window.findViewById(R.id.auth_loading_icon);
		mLayout = (LinearLayout) window
				.findViewById(R.id.holidays_loading_layout);
		initAnim();
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		mRotate = AnimationUtils
				.loadAnimation(AuditHoliday.this, R.anim.rotate);
		mIcon.startAnimation(mRotate);
	}

	/**
	 * 将信息显示在 text
	 * 
	 * @param holidays
	 */
	private void setValueToText(Holidays holidays) {
		txtHolidayEndDate.setText(holidays.getEnddate());
		txtHolidayStartDate.setText(holidays.getBegindate());
		txtHolidayPerson.setText(holidays.getUsername());
		txtHolidayPersonClass.setText(holidays.getOrgname());
		txtHolidayRemark.setText(holidays.getHolidayRemark());
		txtHolidays.setText(holidays.getHolidays());
		txtHolidayType.setText(holidays.getLeavetype());

	}

	/*
	 * 初始化记录数据
	 */
	private void initData() throws AppException {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					setValueToText((Holidays) msg.obj);
					loadingAuditDataLayout.setVisibility(LinearLayout.GONE);
				} else if (msg.what == 0) {
					loadingAuditDataLayout.setVisibility(LinearLayout.GONE);
					UIHelper.ToastMessage(AuditHoliday.this,
							String.valueOf(msg.obj));
				} else if (msg.what == -1) {
					loadingAuditDataLayout.setVisibility(LinearLayout.GONE);
					((AppException) msg.obj).makeToast(AuditHoliday.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Holidays holidays = ac.GetHolidaysById(recordId);
					if (holidays != null) {
						msg.what = 1;// 成功
						msg.obj = holidays;

					} else {
						msg.what = 0;
						msg.obj = "没有相应的数据";
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}

				handler.sendMessage(msg);
			}
		}.start();

	}

	/**
	 * 审核假期
	 */
	private void submitAuditHoliday() {
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (StringUtils.isEmpty(actAuditOpinion.getText().toString())) {
					UIHelper.ToastMessage(
							AuditHoliday.this,
							getString(R.string.message_validate_audit_holiday_opion_null));
					return;
				}

				if (!rdoAuditNo.isChecked() && !rdoAuditYes.isChecked()) {
					UIHelper.ToastMessage(
							AuditHoliday.this,
							getString(R.string.message_validate_audit_holiday_status_null));
					return;
				}
				//3 通过   2 表示 拒绝

				initCommitDataDialog();
				
				AuditHoliday(rdoAuditYes.isChecked() ? "3" : "2",
						actAuditOpinion.getText().toString());

			}
		});

	}

	/**
	 * 线程审核假期
	 * 
	 * @param auditStatus
	 * @param auditContent
	 */
	private void AuditHoliday(final String auditStatus,
			final String auditContent) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					UIHelper.ToastMessage(AuditHoliday.this,
							String.valueOf(msg.obj));
					UIHelper.showMain(AuditHoliday.this);
					commitDataDialog.cancel();
					finish();
				} else if (msg.what == 0) {
					commitDataDialog.cancel();
					UIHelper.ToastMessage(AuditHoliday.this,
							String.valueOf(msg.obj));
				} else if (msg.what == -1) {
					commitDataDialog.cancel();
					((AppException) msg.obj).makeToast(AuditHoliday.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				AppContext ac = (AppContext) getApplication();
				try {
					UserInfo entity = ac.AuditHolidays(recordId, auditStatus,
							auditContent);
					if (entity.getIsError().equals("true")) {
						msg.what = 1;// 成功
						msg.obj = entity.getMessage();

					} else {
						msg.what = 0;
						msg.obj = entity.getMessage();
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}

				handler.sendMessage(msg);
			}
		}.start();

	}

}
