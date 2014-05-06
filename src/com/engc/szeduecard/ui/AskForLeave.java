package com.engc.szeduecard.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.engc.szeduecard.R;
import com.engc.szeduecard.adapter.LeaveTypeAdapter;
import com.engc.szeduecard.api.ApiClient;
import com.engc.szeduecard.bean.UserInfo;
import com.engc.szeduecard.common.SIMCardInfo;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;

/**
 * 
 * @ClassName: AskForLeave
 * @Description: 请假
 * @author wutao
 * @date 2013-10-16 上午11:02:39
 * 
 */
public class AskForLeave extends BaseActivity {
	private TableRow tbrLeaveStartDate, tbrLeaveEndDate, tbrLeaveType;
	private AutoCompleteTextView actLeaveRemark, actLeaveDays;
	private TextView txtLeaveMan, txtLeaveType, txtLeaveStartTime,
			txtDisplayLeaveMan, txtLeaveEndTime;
	private Button btnSubmit, imgBack;
	private ImageView imChooseLeaveMan;
	private ListView listLeaveType;
	private static final List<String> listLeaveTypeResource = new ArrayList<String>();
	private InputMethodManager imm;
	private AppContext ac;
	private Animation mRotate;
	private ImageView mIcon;
	private LinearLayout mLayout;
	private AlertDialog commitDataDialog;
	private int holidaysTypeCode;
	private String leaveBeginTime, leaveEndTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ask_for_leave);
		ac = (AppContext) getApplication();
		listLeaveTypeResource.clear();
		listLeaveTypeResource.add("病假");
		listLeaveTypeResource.add("事假");
		initView();

	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		mRotate = AnimationUtils.loadAnimation(AskForLeave.this, R.anim.rotate);
		mIcon.startAnimation(mRotate);
	}

	/**
	 * 申请假期
	 * 
	 * @param userCode
	 * @param startDate
	 * @param endDate
	 * @param days
	 * @param remark
	 * @param holidayType
	 */
	private void ApplyHoliday(final String startDate, final String endDate,
			final double days, final String telNo, final String remark,
			final int holidayType) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					UIHelper.ToastMessage(AskForLeave.this,
							String.valueOf(msg.obj));
					UIHelper.showMain(AskForLeave.this);
					commitDataDialog.cancel();
					finish();
				} else if (msg.what == 0) {
					commitDataDialog.cancel();
					UIHelper.ToastMessage(AskForLeave.this,
							String.valueOf(msg.obj));
				} else if (msg.what == -1) {
					commitDataDialog.cancel();
					((AppException) msg.obj).makeToast(AskForLeave.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				AppContext ac = (AppContext) getApplication();
				try {
					UserInfo user = ac.ApplyHoliday(ac.getLoginInfo()
							.getUsercode(), telNo, startDate, endDate, days,
							remark, holidaysTypeCode);
					if (user.getIsError().equals("true")) {
						msg.what = 1;// 成功
						msg.obj = user.getMessage();

					} else {
						msg.what = 0;
						msg.obj = user.getMessage();
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
	 * 回调函数
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String name = "";
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:
			if (data != null) {
				leaveBeginTime = data.getExtras().getString("startTime");
				txtLeaveStartTime.setText(data.getExtras().getString(
						"DisplaystartTime"));

			}
			break;
		default:
			if (data != null) {
				leaveEndTime = data.getExtras().getString("endTime");
				String isHalf=data.getExtras().getString("isHalf");
				txtLeaveEndTime.setText(data.getExtras().getString(
						"DisplayendTime"));
				if (txtLeaveStartTime.getText() == null)
					UIHelper.ToastMessage(AskForLeave.this, "请选择开始时间");

				else if(isHalf.equals("AM")||isHalf.equals("PM"))
					actLeaveDays.setText(StringUtils
							.ConvertDateToStringForTimeResult(txtLeaveStartTime
									.getText().toString(), data.getExtras()
									.getString("endTime"))
							+ "天");

			}
			break;

		}

	}

	/**
	 * 选择请假类型
	 */
	private void showChooseLeaveType() {
		final AlertDialog alg = new AlertDialog.Builder(AskForLeave.this)
				.create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.ask_for_leave_type_list);
		listLeaveType = (ListView) window.findViewById(R.id.listleavetype);
		LeaveTypeAdapter adapter = new LeaveTypeAdapter(this,
				listLeaveTypeResource);
		listLeaveType.setAdapter(adapter);

		listLeaveType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				txtLeaveType.setText(listLeaveTypeResource.get(arg2));
				if (arg2 == 0)
					holidaysTypeCode = 2;
				else
					holidaysTypeCode = 1;
				alg.cancel();
			}
		});

	}

	/**
	 * 初始化提交数据等待dialog
	 */
	private void initCommitDataDialog() {
		commitDataDialog = new AlertDialog.Builder(AskForLeave.this).create();
		commitDataDialog.show();
		Window window = commitDataDialog.getWindow();
		window.setContentView(R.layout.apply_holiday_dialog);
		mIcon = (ImageView) window.findViewById(R.id.auth_loading_icon);
		mLayout = (LinearLayout) window
				.findViewById(R.id.holidays_loading_layout);
		initAnim();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		actLeaveDays = (AutoCompleteTextView) findViewById(R.id.actleavedays);
		actLeaveRemark = (AutoCompleteTextView) findViewById(R.id.actleaveremark);
		tbrLeaveStartDate = (TableRow) findViewById(R.id.ask_for_leavestartdate_row);
		tbrLeaveEndDate = (TableRow) findViewById(R.id.ask_for_leaveenddate_row);
		tbrLeaveType = (TableRow) findViewById(R.id.ask_for_leavetype_row);
		imgBack = (Button) findViewById(R.id.btn_leave_back);
		imgBack.setOnClickListener(UIHelper.finish(this));
		txtLeaveMan = (TextView) findViewById(R.id.txt_ask_for_leaveman);
		imChooseLeaveMan = (ImageView) findViewById(R.id.imgchooseleaveman);
		btnSubmit = (Button) findViewById(R.id.btnentryleave);
		txtLeaveType = (TextView) findViewById(R.id.txtleavetype);
		txtLeaveStartTime = (TextView) findViewById(R.id.txtleavestartdate);
		txtLeaveEndTime = (TextView) findViewById(R.id.txtleaveenddate);
		txtLeaveMan = (TextView) findViewById(R.id.txt_ask_for_leaveman);
		txtDisplayLeaveMan = (TextView) findViewById(R.id.txtdisplayleavename);
		txtLeaveMan.setText(!ac.getLoginInfo().getSonname().equals("") ? ac
				.getLoginInfo().getSonname() : ac.getLoginInfo().getUsername());
		txtDisplayLeaveMan.setText("请假人:" + txtLeaveMan.getText());
		actLeaveRemark = (AutoCompleteTextView) findViewById(R.id.actleaveremark);

		// 选择请假人
		imChooseLeaveMan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		// 结束时间
		tbrLeaveEndDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AskForLeave.this, CalendarPick.class);
				intent.putExtra("timeType", "end");
				startActivityForResult(intent, 1);

			}
		});

		// 开始时间
		tbrLeaveStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AskForLeave.this, CalendarPick.class);
				intent.putExtra("timeType", "start");
				startActivityForResult(intent, 1);
			}
		});
		// 请假类型
		tbrLeaveType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showChooseLeaveType();

			}
		});

		// 提交请假事件
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (StringUtils.isEmpty(txtLeaveType.getText().toString())) {
					UIHelper.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaytype_null));
					return;
				}
				if (StringUtils.isEmpty(txtLeaveStartTime.getText().toString())) {
					UIHelper.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaystartdate_null));
					return;
				}
				if (StringUtils.isEmpty(txtLeaveEndTime.getText().toString())) {
					UIHelper.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidayenddate_null));
					return;
				}
				if (!StringUtils
						.isEmpty(txtLeaveStartTime.getText().toString())
						&& !StringUtils.isEmpty(txtLeaveEndTime.getText()
								.toString())) {
					String startTime = txtLeaveStartTime.getText().toString();
					String endTime = txtLeaveEndTime.getText().toString();
					if (StringUtils.toDate(startTime).getTime() > StringUtils
							.toDate(endTime).getTime()) {
						UIHelper.ToastMessage(
								v.getContext(),
								getString(R.string.message_validate_holidays_startdate_enddate_normal));
						return;
					}

				}
				if (StringUtils.isEmpty(actLeaveDays.getText().toString())) {
					UIHelper.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaydays_null));
					return;
				}
				if (StringUtils.isEmpty(actLeaveRemark.getText().toString())) {
					UIHelper.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidaysremark_null));
					return;
				}
				if (!StringUtils.isOnlyDigital(actLeaveDays.getText()
						.toString())) {
					UIHelper.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidays_isdigital));
					return;
				}
				if (actLeaveDays.getText().toString().equals("0")) {
					UIHelper.ToastMessage(
							v.getContext(),
							getString(R.string.message_validate_holidays_more_zero));
					return;
				}

				String PhoneNum = new SIMCardInfo(AskForLeave.this)
						.getNativePhoneNumber();
				if (!PhoneNum.equals(""))
					PhoneNum = PhoneNum;
				/*
				 * else if (ac.getLoginInfo().getParentphone() != null) PhoneNum
				 * = ac.getLoginInfo().getParentphone();
				 */
				else
					PhoneNum = "13245678765";

				initCommitDataDialog();
				String days = actLeaveDays.getText().toString();

				/*
				 * if (days.indexOf("天") != -1) days = days.substring(0,
				 * days.length() - 1);
				 */
				ApplyHoliday(leaveBeginTime, leaveEndTime, Double
						.parseDouble(days), PhoneNum, actLeaveRemark.getText()
						.toString(), holidaysTypeCode);

			}
		});
	}

}
