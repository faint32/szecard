package com.engc.szeduecard.ui;

import java.util.LinkedHashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.engc.szeduecard.R;
import com.engc.szeduecard.adapter.LeaveTypeAdapter;
import com.engc.szeduecard.api.ApiClient;
import com.engc.szeduecard.bean.UserInfo;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;
import com.engc.szeduecard.widget.LoadingDialog;

/**
 * 
 * @ClassName: Login
 * @Description: 登录Activity
 * @author wutao
 * @date 2013-10-11 上午10:43:29
 * 
 */
public class Login extends BaseActivity implements TagAliasCallback {
	private ImageView imgLogin;
	private final String TAG = "Login";
	private InputMethodManager imm;
	private AlertDialog loginLoadingDialog;
	private AutoCompleteTextView actAccountName, actPassWord; // 用户名密码
	private TextView txtLoadingDialogText;
	private AppContext ac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ac = (AppContext) getApplication();
		initView();

		// actAccountName.setText("0100001036");
		//actAccountName.setText("1230042365");
		//actPassWord.setText("123");

	}



	private void initView() {
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imgLogin = (ImageView) findViewById(R.id.btnlogin);

		actAccountName = (AutoCompleteTextView) findViewById(R.id.login_account);
		actPassWord = (AutoCompleteTextView) findViewById(R.id.login_password);

		imgLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				// 网络连接判断
				if (!ac.isNetworkConnected()) {
					UIHelper.ToastMessage(v.getContext(),
							R.string.network_not_connected);
					return;
				}
				if (StringUtils.isEmpty(actAccountName.getText().toString())) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_accountname_null));
					return;
				}
				if (StringUtils.isEmpty(actPassWord.getText().toString())) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_pwd_null));
					return;
				}

				initLoginDialog();
				login(actAccountName.getText().toString(), actPassWord
						.getText().toString());

			}
		});
	}

	/**
	 * 初始化登录人机交互层
	 */
	private void initLoginDialog() {
		loginLoadingDialog = new AlertDialog.Builder(Login.this).create();
		loginLoadingDialog.show();
		Window window = loginLoadingDialog.getWindow();
		window.setContentView(R.layout.loadingdialog);
		txtLoadingDialogText = (TextView) window
				.findViewById(R.id.loading_text);
		txtLoadingDialogText.setText("正在登录……");

	}

	/**
	 * 设置tag
	 */
	private void setTag() {
		// 检查 tag 的有效性
		// String tag = "0100001036";
		// String tag="1230026993";
		String tag = ac.getLoginInfo().getUsercode();
		// ","隔开的多个 转换成 Set
		String[] sArray = tag.split(",");
		Set<String> tagSet = new LinkedHashSet<String>();
		for (String sTagItme : sArray) {
			/*
			 * if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
			 * Toast.makeText(PushSetActivity.this,R.string.error_tag_gs_empty,
			 * Toast.LENGTH_SHORT).show(); return;
			 */
			// }
			tagSet.add(sTagItme);
		}

		// 调用JPush API设置Tag
		JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet,
				this);

	}

	@Override
	public void gotResult(int code, String alias, Set<String> tags) {
		String logs;
		switch (code) {
		case 0:
			logs = "Set tag and alias success, alias = " + alias + "; tags = "
					+ tags;
			Log.i(TAG, logs);
			break;

		default:
			logs = "Failed with errorCode = " + code + " " + alias
					+ "; tags = " + tags;
			Log.e(TAG, logs);
		}

	}

	/**
	 * 登录方法
	 * 
	 * @param account
	 * @param pwd
	 * @param isRememberMe
	 */
	private void login(final String accountName, final String accountPwd) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {

					UserInfo user = (UserInfo) msg.obj;
					if (user != null) {
					 Log.d("user tag", ac.getLoginInfo().getSoncode()!=null?ac.getLoginInfo().getSoncode():ac.getLoginInfo().getUsercode());
						// 清空原先cookie
						ApiClient.cleanCookie();

						UIHelper.showMain(Login.this);
						loginLoadingDialog.cancel();
						finish();

					}
				} else if (msg.what == 0) {
					loginLoadingDialog.cancel();
					UIHelper.ToastMessage(Login.this, String.valueOf(msg.obj));
				} else if (msg.what == -1) {
					loginLoadingDialog.cancel();
					((AppException) msg.obj).makeToast(Login.this);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				try {
					UserInfo user = ac.Login(accountName, accountPwd);
					if (user != null) {
						if (user.getUsercode() != null) {
							ac.saveLoginInfo(user);
							setTag(); // 设置tag
							msg.what = 1;// 成功
							msg.obj = user;

						} else {
							ac.cleanLoginInfo();
							msg.what = 0;
							msg.obj = user.getMessage();
						}
					} else {
						msg.what = 0;
						msg.obj = getString(R.string.http_exception_error);
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
