package com.engc.szeduecard.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.engc.szeduecard.R;
import com.engc.szeduecard.bean.CardStatus;
import com.engc.szeduecard.bean.UserInfo;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;
import com.engc.szeduecard.services.MyReceiver;
import com.engc.szeduecard.ui.AppStore.GridViewModemAdapter;

/**
 * 
 * @ClassName: Main
 * @Description: TODO
 * @author wutao
 * @date 2013-10-18 上午9:09:47
 * 
 */
public class Main extends BaseActivity {
	private GridView gvApps; // 应用九宫格
	private List<String> imgtitleList; // 存放应用标题list
	private List<Integer> imgList; // 存放应用图片list

	private View[] itemViews;
	private RadioButton rdoFootIndex, rdoFootRecord, rdoFootMine, rdoFootMore;
	private RadioButton[] mButtons; // footbar 按钮 count=4
	private String[] mHeadTitles; // hearbar 标题
	private int mCurSel; // 视图 索引
	private String appName, appIcon;
	private TextView txtAccountBalance, txtAccountStatus,
			txtLongClickHeaderTitle, txtRemoveApp, txtSendApptoDesktop,
			txtAccountName;
	private AppContext appContext;
	private ImageView imgUserFace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		/*
		 * DisplayMetrics display=new DisplayMetrics();
		 * getWindowManager().getDefaultDisplay().getMetrics(display);
		 */
		appContext = (AppContext) getApplication();
		appName = getIntent().getStringExtra("appName");
		appIcon = getIntent().getStringExtra("appIcon");

		initView();
		initData();
		/*
		 * if (appContext.getUserAppCacheList(appContext.getLoginInfo()
		 * .getUsercode()) != null) gvApps.setAdapter(new
		 * GridViewModemAdapter(imgtitleList, imgList)); else
		 * initGridData(appContext.getLoginInfo().getCardstatus());
		 */
		refreshGridData();
		JPushInterface.init(getApplicationContext());
		registerMessageReceiver(); // 注册信息接受器

	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		txtAccountName
				.setText(appContext.getLoginInfo().getUsername() != null ? appContext
						.getLoginInfo().getUsername() : "");
		txtAccountStatus
				.setText(appContext.getLoginInfo().getEntityname() != null ? appContext
						.getLoginInfo().getEntityname() : "");
		txtAccountBalance.setText(String.valueOf(Double.parseDouble(appContext
				.getLoginInfo().getCurrdbmoney())));
		/*
		 * if (appContext.getLoginInfo().getHeadpic() != null)
		 * UIHelper.showLoadImage(imgUserFace, appContext.getLoginInfo()
		 * .getHeadpic(), "无法找到用户头像");
		 */
		imgtitleList = appContext.getUserAppCacheList(appContext.getLoginInfo()
				.getUsercode());
		imgList = appContext.getUserAppIconCacheList(appContext.getLoginInfo()
				.getUsercode());
		if (imgtitleList != null && imgList != null)
			gvApps.setAdapter(new GridViewModemAdapter(imgtitleList, imgList));
		else
			initGridData(appContext.getLoginInfo().getCardstatus());

	}

	/**
	 * 刷新gridview
	 */
	private void refreshGridData() {
		if (appName != null && appIcon != null) {
			// gvApps = (GridView) findViewById(R.id.gvapps);
			imgList.remove(imgList.size() - 1);
			imgtitleList.remove(imgtitleList.size() - 1);

			imgList.add(getResources().getIdentifier(
					getPackageName() + ":drawable/" + appIcon, null,
					getPackageName()));

			imgtitleList.add(appName);
			imgtitleList.add("更多");
			imgList.add(R.drawable.icon_add_app);
			gvApps.setAdapter(new GridViewModemAdapter(imgtitleList, imgList));
			appContext.addUserAppIconListToCache(imgList, appContext
					.getLoginInfo().getUsercode());
			appContext.addUserAppListToCache(imgtitleList, appContext
					.getLoginInfo().getUsercode());

		}
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

	/**
	 * 根据当前 状态 初始化 gridview
	 * 
	 * @param cardStatus
	 */
	private void initGridData(int cardStatus) {
		imgtitleList = new ArrayList<String>();
		imgList = new ArrayList<Integer>();

		switch (cardStatus) {
		case CardStatus.UN_OPEN_CARD_ACCOUNT: // 表示 未开户 状态\
			imgtitleList.clear();
			imgList.clear();
			imgtitleList.add("充值");
			imgtitleList.add("请假");
			imgtitleList.add("请假记录");
			imgtitleList.add("更多");
			imgList.add(R.drawable.icon_prepaid);
			imgList.add(R.drawable.icon_ask_for_leave);
			imgList.add(R.drawable.icon_safe_campus);
			imgList.add(R.drawable.icon_add_app);
			gvApps.setAdapter(new GridViewModemAdapter(imgtitleList, imgList));
			appContext.addUserAppIconListToCache(imgList, appContext
					.getLoginInfo().getUsercode());
			appContext.addUserAppListToCache(imgtitleList, appContext
					.getLoginInfo().getUsercode());
			break;

		case CardStatus.NORMAL_CARD: // 表示正常状态
			imgtitleList.clear();
			imgList.clear();
			imgtitleList.add("充值");
			imgtitleList.add("挂失");
			imgtitleList.add("请假");
			imgtitleList.add("请假记录");
			imgtitleList.add("更多");
			imgList.add(R.drawable.icon_prepaid);
			imgList.add(R.drawable.icon_loss_reporting);
			imgList.add(R.drawable.icon_ask_for_leave);
			imgList.add(R.drawable.icon_safe_campus);
			imgList.add(R.drawable.icon_add_app);
			gvApps.setAdapter(new GridViewModemAdapter(imgtitleList, imgList));
			appContext.addUserAppIconListToCache(imgList, appContext
					.getLoginInfo().getUsercode());
			appContext.addUserAppListToCache(imgtitleList, appContext
					.getLoginInfo().getUsercode());

			break;

		case CardStatus.REPORT_LOSS_ED_CARD:// 已挂失
			imgtitleList.clear();
			imgList.clear();
			imgtitleList.add("充值");
			imgtitleList.add("补卡");
			imgtitleList.add("解挂");
			imgtitleList.add("请假");
			imgtitleList.add("请假记录");
			imgtitleList.add("更多");
			imgList.add(R.drawable.icon_prepaid);
			imgList.add(R.drawable.icon_loss_reporting);
			imgList.add(R.drawable.icon_curriculum);
			imgList.add(R.drawable.icon_ask_for_leave);
			imgList.add(R.drawable.icon_safe_campus);
			imgList.add(R.drawable.icon_add_app);
			gvApps.setAdapter(new GridViewModemAdapter(imgtitleList, imgList));
			appContext.addUserAppIconListToCache(imgList, appContext
					.getLoginInfo().getUsercode());
			appContext.addUserAppListToCache(imgtitleList, appContext
					.getLoginInfo().getUsercode());

			break;

		default:
			break;
		}

	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		txtAccountBalance = (TextView) findViewById(R.id.txtaccountbalance);
		txtAccountStatus = (TextView) findViewById(R.id.txtaccountstatus);
		txtAccountName = (TextView) findViewById(R.id.txtaccountname);
		imgUserFace = (ImageView) findViewById(R.id.imguserface);

		gvApps = (GridView) findViewById(R.id.gvapps);

		gvApps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				try {
					initGridItemsForDiffereEvent(appContext.getLoginInfo()
							.getCardstatus(), arg2);
				} catch (AppException e) {
					e.printStackTrace();
				}

			}
		});

		// grid 长按事件
		gvApps.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				EditAppOnLongClick(arg2);

				return false;
			}
		});

	}

	/**
	 * 根据用户状态 初始化当前gridview 和事件
	 * 
	 * @param currCardStatus
	 * @param curIndex
	 * @throws AppException
	 */
	private void initGridItemsForDiffereEvent(int currCardStatus, int curIndex)
			throws AppException {
		switch (currCardStatus) {
		case CardStatus.UN_OPEN_CARD_ACCOUNT: // 未开户
			switch (curIndex) { // 未开户状态 gridview 索引状态
			case 0:
				UIHelper.showPrepaid(Main.this);
				break;
			case 1:
				UIHelper.showAskForLeave(Main.this);
				break;
			case 2:
				UIHelper.showHolidayRecord(Main.this); 
				break;

			default:
			//	UIHelper.showAppStore(Main.this);
				UIHelper.showHomeWork(Main.this);
				break;
			}

			break;
		case CardStatus.NORMAL_CARD: // 表示卡状态 正常
			switch (curIndex) {
			case 0:
				UIHelper.showPrepaid(Main.this);
				break;

			case 1:

				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 1) {
							UIHelper.ToastMessage(Main.this, (String) msg.obj);
							txtAccountStatus.setText("已挂失");
							UserInfo user = appContext.getLoginInfo();
							user.setCardstatus(CardStatus.REPORT_LOSS_ED_CARD);
							appContext.saveLoginInfo(user);
							initGridData(CardStatus.REPORT_LOSS_ED_CARD);
						} else {
							UIHelper.ToastMessage(Main.this, (String) msg.obj);

						}

					}
				};
				new Thread() {
					public void run() {
						Message msg = new Message();
						try {
							UserInfo user = appContext.ChangeCardStatus(
									appContext.getLoginInfo().getUsercode(),
									appContext.getLoginInfo().getCardstatus(),
									1);
							if (user.getIsError().equals("false")) {
								msg.what = 1;
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
				break;
			case 2:
				UIHelper.showAskForLeave(Main.this);
				break;
			case 3:
				UIHelper.showHolidayRecord(Main.this);
				break;
			default:
				UIHelper.showAppStore(Main.this);
				break;
			}
			break;

		default:
			switch (curIndex) {
			case 0: // 充值
				UIHelper.showPrepaid(Main.this);

				break;
			case 1: // 补卡
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 1) {
							UIHelper.ToastMessage(Main.this, (String) msg.obj);
							txtAccountStatus.setText("正常");
							UserInfo user = appContext.getLoginInfo();
							user.setCardstatus(CardStatus.NORMAL_CARD);
							appContext.saveLoginInfo(user);

							initGridData(CardStatus.NORMAL_CARD);

						} else {
							UIHelper.ToastMessage(Main.this, (String) msg.obj);

						}
					}
				};
				new Thread() {
					public void run() {
						Message msg = new Message();
						try {
							UserInfo user = appContext.ChangeCardStatus(
									appContext.getLoginInfo().getUsercode(),
									appContext.getLoginInfo().getCardstatus(),
									6);
							if (user.getIsError().equals("false")) {
								msg.what = 1;
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
				break;
			case 2: // 解挂
				final Handler handler1 = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 1) {
							UIHelper.ToastMessage(Main.this, (String) msg.obj);
							txtAccountStatus.setText("正常");
							UserInfo user = appContext.getLoginInfo();
							user.setCardstatus(CardStatus.NORMAL_CARD);
							appContext.saveLoginInfo(user);
							initGridData(CardStatus.NORMAL_CARD);
						} else {
							UIHelper.ToastMessage(Main.this, (String) msg.obj);

						}
					}
				};
				new Thread() {
					public void run() {
						Message msg = new Message();
						try {
							UserInfo user = appContext.ChangeCardStatus(
									appContext.getLoginInfo().getUsercode(),
									appContext.getLoginInfo().getCardstatus(),
									5);
							if (user.getIsError().equals("false")) {
								msg.what = 1;
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
						handler1.sendMessage(msg);
					}
				}.start();
				break;
			case 3: // 请假
				UIHelper.showAskForLeave(Main.this);
				break;
			case 4: // 请假记录
				UIHelper.showHolidayRecord(Main.this);
				break;

			default:// 更多
				UIHelper.showAppStore(Main.this);
				break;
			}
			break;
		}
	}

	/**
	 * 长按弹出编辑应用
	 */
	private void EditAppOnLongClick(final int appIndex) {
		final AlertDialog alg = new AlertDialog.Builder(Main.this).create();
		alg.show();
		Window window = alg.getWindow();
		window.setContentView(R.layout.remove_app_dialog);
		txtLongClickHeaderTitle = (TextView) window
				.findViewById(R.id.txt_longclick_app_header_title);
		txtLongClickHeaderTitle.setText(imgtitleList.get(appIndex));
		txtRemoveApp = (TextView) window.findViewById(R.id.txtremoveapp);
		txtSendApptoDesktop = (TextView) window
				.findViewById(R.id.txtsendapptodesktop);
		txtRemoveApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alg.cancel();
				imgList.remove(appIndex);
				imgtitleList.remove(appIndex);
				gvApps.setAdapter(new GridViewModemAdapter(imgtitleList,
						imgList));
				appContext.addUserAppIconListToCache(imgList, appContext
						.getLoginInfo().getUsercode());
				appContext.addUserAppListToCache(imgtitleList, appContext
						.getLoginInfo().getUsercode());
				
			}
		});
		txtSendApptoDesktop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.ToastMessage(Main.this, "此功能尚未开放");
			}
		});
	}

	/**
	 * 
	 * @ClassName: GridViewModemAdapter
	 * @Description: APPs 九宫格 数据适配源
	 * @author wutao
	 * @date 2013-10-10 上午11:23:54
	 * 
	 */
	public class GridViewModemAdapter extends BaseAdapter {

		public GridViewModemAdapter(List<String> imgTitles, List<Integer> images) {
			itemViews = new View[images.size()];
			for (int i = 0; i < itemViews.length; i++) {
				itemViews[i] = makeItemView(imgTitles.get(i), images.get(i));
			}
		}

		public View makeItemView(String imageTitilsId, int imageId) {
			try {
				LayoutInflater inflater = (LayoutInflater) Main.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View itemView = inflater.inflate(R.layout.grid_apps_item, null);
				TextView title = (TextView) itemView
						.findViewById(R.id.TextItemId);
				title.setText(imageTitilsId);
				ImageView image = (ImageView) itemView
						.findViewById(R.id.ImageItemId);
				image.setImageResource(imageId);
				image.setScaleType(ImageView.ScaleType.FIT_CENTER);
				return itemView;
			} catch (Exception e) {
				System.out.println("makeItemView Exception error"
						+ e.getMessage());
				return null;
			}

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemViews.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemViews[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				return itemViews[position];
			}
			return convertView;
		}

	}

	private MyReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.engc.szeduecard.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

}
