package com.engc.szeduecard.common;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.engc.szeduecard.R;
import com.engc.szeduecard.api.ApiClient;
import com.engc.szeduecard.bean.Holidays;
import com.engc.szeduecard.config.AppException;
import com.engc.szeduecard.config.AppManager;
import com.engc.szeduecard.ui.AppDetail;
import com.engc.szeduecard.ui.AppStore;
import com.engc.szeduecard.ui.AskForLeave;
import com.engc.szeduecard.ui.HolidayRecord;
import com.engc.szeduecard.ui.HolidayRecordDetail;
import com.engc.szeduecard.ui.HomeWork;
import com.engc.szeduecard.ui.HomeWorkDetail;
import com.engc.szeduecard.ui.Login;
import com.engc.szeduecard.ui.Prepaid;
import com.engc.szeduecard.ui.TabHostActivity;
import com.engc.szeduecard.ui.Tweet;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author samwu
 * @version 1.0
 * 
 */
public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
	public final static int LISTVIEW_DATATYPE_POST = 0x03;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	private int isEntryOrCancle;

	/**
	 * 显示登录页面
	 * 
	 * @param activity
	 */
	public static void showLoginDialog(Context context) {
		/*
		 * Intent intent = new Intent(context, LoginDialog.class); if (context
		 * instanceof Main) intent.putExtra("LOGINTYPE",
		 * LoginDialog.LOGIN_MAIN);
		 * 
		 * else if (context instanceof Setting) intent.putExtra("LOGINTYPE",
		 * LoginDialog.LOGIN_SETTING); else
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * context.startActivity(intent);
		 */
	}

	/**
	 * 加载显示用户头像
	 * 
	 * @param imgFace
	 * @param faceURL
	 */
	public static void showUserFace(final ImageView imgFace,
			final String faceURL) {
		showLoadImage(imgFace, faceURL,
				imgFace.getContext().getString(R.string.msg_load_userface_fail));
	}

	/**
	 * 记载图片，并将其图片设置为 imgview 的背景图片
	 * 
	 * @param imgView
	 * @param imgURL
	 * @param errMsg
	 */
	public static void showLoadBackGroundImage(final ImageView imgView,
			final String imgURL, final String errMsg) {
		// 读取本地图片
		if (StringUtils.isEmpty(imgURL) || imgURL.endsWith("portrait.gif")) {
			Bitmap bmp = BitmapFactory.decodeResource(imgView.getResources(),
					R.drawable.widget_dface);
			Drawable d = new BitmapDrawable(imgView.getResources(), bmp); // 将读取的
																			// 网络图片
																			// 转为drawable
			imgView.setBackgroundDrawable(d); // 设置imageview 背景图片
			return;
		}

		// 是否有缓存图片
		final String filename = FileUtils.getFileName(imgURL);
		// Environment.getExternalStorageDirectory();返回/sdcard
		String filepath = imgView.getContext().getFilesDir() + File.separator
				+ filename;
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			Drawable d = new BitmapDrawable(imgView.getResources(), bmp);
			imgView.setBackgroundDrawable(d);
			return;
		}

		// 从网络获取&写入图片缓存
		String _errMsg = imgView.getContext().getString(
				R.string.msg_load_image_fail);
		if (!StringUtils.isEmpty(errMsg))
			_errMsg = errMsg;
		final String ErrMsg = _errMsg;
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					// imgView.setImageBitmap((Bitmap) msg.obj);
					Drawable d = new BitmapDrawable(imgView.getResources(),
							(Bitmap) msg.obj);
					imgView.setBackgroundDrawable(d);
					try {
						// 写图片缓存
						ImageUtils.saveImage(imgView.getContext(), filename,
								(Bitmap) msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					ToastMessage(imgView.getContext(), ErrMsg);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = ApiClient.getNetBitmap(imgURL);
					msg.what = 1;
					msg.obj = bmp;
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
	 * 加载显示图片
	 * 
	 * @param imgFace
	 * @param faceURL
	 * @param errMsg
	 */
	public static void showLoadImage(final ImageView imgView,
			final String imgURL, final String errMsg) {
		// 读取本地图片
		if (StringUtils.isEmpty(imgURL) || imgURL.endsWith("portrait.gif")) {
			Bitmap bmp = BitmapFactory.decodeResource(imgView.getResources(),
					R.drawable.widget_dface);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 是否有缓存图片
		final String filename = FileUtils.getFileName(imgURL);
		// Environment.getExternalStorageDirectory();返回/sdcard
		String filepath = imgView.getContext().getFilesDir() + File.separator
				+ filename;
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 从网络获取&写入图片缓存
		String _errMsg = imgView.getContext().getString(
				R.string.msg_load_image_fail);
		if (!StringUtils.isEmpty(errMsg))
			_errMsg = errMsg;
		final String ErrMsg = _errMsg;
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					imgView.setImageBitmap((Bitmap) msg.obj);
					try {
						// 写图片缓存
						ImageUtils.saveImage(imgView.getContext(), filename,
								(Bitmap) msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					ToastMessage(imgView.getContext(), ErrMsg);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = ApiClient.getNetBitmap(imgURL);
					msg.what = 1;
					msg.obj = bmp;
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
	 * 加载圆角图片
	 * 
	 * @param imgView
	 * @param imgURL
	 * @param errMsg
	 */
	public static void showLoadRoundedImage(final ImageView imgView,
			final String imgURL, final String errMsg) {
		// 读取本地图片
		if (StringUtils.isEmpty(imgURL) || imgURL.endsWith("portrait.gif")) {
			Bitmap bmp = BitmapFactory.decodeResource(imgView.getResources(),
					R.drawable.default_face_1);
			if (bmp != null)
				bmp = ImageUtils.getRoundedCornerBitmap(bmp, 100);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 是否有缓存图片
		final String filename = FileUtils.getFileName(imgURL);
		// Environment.getExternalStorageDirectory();返回/sdcard
		String filepath = imgView.getContext().getFilesDir() + File.separator
				+ filename;
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			if (bmp != null)
				bmp = ImageUtils.getRoundedCornerBitmap(bmp, 100);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 从网络获取&写入图片缓存
		String _errMsg = imgView.getContext().getString(
				R.string.msg_load_image_fail);
		if (!StringUtils.isEmpty(errMsg))
			_errMsg = errMsg;
		final String ErrMsg = _errMsg;
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					imgView.setImageBitmap((Bitmap) msg.obj);
					try {
						// 写图片缓存
						ImageUtils.saveImage(imgView.getContext(), filename,
								(Bitmap) msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					ToastMessage(imgView.getContext(), ErrMsg);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = ApiClient.getNetBitmap(imgURL);
					if (bmp != null)
						bmp = ImageUtils.getRoundedCornerBitmap(bmp, 24);
					msg.what = 1;
					msg.obj = bmp;
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
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			ToastMessage(context, "无法浏览此网页", 500);
		}
	}

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	/*
	 * public static WebViewClient getWebViewClient() { return new
	 * WebViewClient() {
	 * 
	 * @Override public boolean shouldOverrideUrlLoading(WebView view, String
	 * url) { showUrlRedirect(view.getContext(), url); return true; } }; }
	 */

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "jxsmallmouse@163.com" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"开源中国Android客户端 - 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 出现异常崩溃，kill 掉进程
						// AppManager.getAppManager().AppExit(cont);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		builder.show();
	}

	/**
	 * 退出程序
	 * 
	 * @param cont
	 */
	public static void Exit(final Context cont) {
		final AlertDialog alg = new AlertDialog.Builder(cont).create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.dialog);
		Button btnEntry = (Button) window
				.findViewById(R.id.dialog_button_entry);
		Button btnCancle = (Button) window
				.findViewById(R.id.dialog_button_cancle);
		btnEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alg.cancel();
				// 退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		btnCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alg.dismiss();
			}
		});

	}
	/**
	 * 退出登录 注销 
	 * @param cont
	 */
	public static void Logout(final Context cont) {
		final AlertDialog alg = new AlertDialog.Builder(cont).create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.dialog);
		Button btnEntry = (Button) window
				.findViewById(R.id.dialog_button_entry);
		Button btnCancle = (Button) window
				.findViewById(R.id.dialog_button_cancle);
		btnEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alg.cancel();
				// 注销
				Intent intent = new Intent(cont, Login.class);
				cont.startActivity(intent);
				
			}
		});
		btnCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alg.dismiss();
			}
		});

	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

	/**
	 * 跳转至主界面
	 * 
	 * @param context
	 */
	public static void showMain(Context context) {
		Intent intent = new Intent(context, TabHostActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 跳转至发表动态
	 * 
	 * @param context
	 */
	public static void showTweetPub(Context context) {
		Intent intent = new Intent(context, Tweet.class);
		context.startActivity(intent);
	}

	/**
	 * 跳转至充值
	 * 
	 * @param context
	 */
	public static void showPrepaid(Context context) {
		Intent intent = new Intent(context, Prepaid.class);
		context.startActivity(intent);
	}

	/**
	 * 跳转至家庭作业
	 * 
	 * @param context
	 */
	public static void showHomeWork(Context context) {
		Intent intent = new Intent(context, HomeWork.class);
		context.startActivity(intent);
	}

	/**
	 * 跳转至添加应用
	 * 
	 * @param context
	 */
	public static void showAppStore(Activity activity) {
		Intent intent = new Intent(activity, AppStore.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 显示首页
	 * 
	 * @param activity
	 */
	public static void showIndex(Context context, String appName, String appIcon) {
		Intent intent = new Intent(context, TabHostActivity.class);
		intent.putExtra("appName", appName);
		intent.putExtra("appIcon", appIcon);
		context.startActivity(intent);

	}

	/**
	 * 跳转至请假
	 * 
	 * @param context
	 * @param appName
	 * @param appIcon
	 */
	public static void showAskForLeave(Context context) {
		Intent intent = new Intent(context, AskForLeave.class);
		context.startActivity(intent);

	}
	
	/**
	 * 请假记录 
	 * @param context
	 */
	public static void showHolidayRecord(Context context) {
		Intent intent = new Intent(context, HolidayRecord.class);
		context.startActivity(intent);

	}

	/**
	 * 应用详情
	 * 
	 * @param activity
	 */
	public static void showAppDetail(Context context, String appName,
			String appIcon, String appVersion, String appSize, String appDesc) {
		Intent intent = new Intent(context, AppDetail.class);
		intent.putExtra("appName", appName);
		intent.putExtra("appIcon", appIcon);
		intent.putExtra("appVersion", appVersion);
		intent.putExtra("appSize", appSize);
		intent.putExtra("appDesc", appDesc);
		context.startActivity(intent);
	}
	
	/**
	 *  请假详细
	 * @param context
	 * @param holiday
	 */
	public static void showHolidayDetail(Context context, Holidays holiday) {
		Intent intent = new Intent(context, HolidayRecordDetail.class);
		intent.putExtra("userName", holiday.getUsername());
		intent.putExtra("beginDate", holiday.getBegindate());
		intent.putExtra("endDate", holiday.getEnddate());
		intent.putExtra("holidays", holiday.getHolidays());
		intent.putExtra("remark", holiday.getHolidayRemark());
		intent.putExtra("holidayType", holiday.getLeavetype());
		intent.putExtra("auditStatus", holiday.getLeavestatus());
		context.startActivity(intent);

	}

	/**
	 * 家庭作业详细
	 * @param context
	 * @param hm
	 */
	public static void showHomeWorkDetail(Context context,com.engc.szeduecard.bean.HomeWork hm){
		Intent intent = new Intent(context, HomeWorkDetail.class);
		intent.putExtra("hwName", hm.getName());
		intent.putExtra("hwDesc", hm.getDescp());
		intent.putExtra("hwType", hm.getType());
		intent.putExtra("hwSubject", hm.getSubject());
		intent.putExtra("hwURL", hm.getUrl());
		context.startActivity(intent);
	}
}
