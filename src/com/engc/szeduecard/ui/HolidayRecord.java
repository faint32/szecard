package com.engc.szeduecard.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.engc.szeduecard.R;
import com.engc.szeduecard.adapter.ListViewHoildayRecordAdapter;
import com.engc.szeduecard.bean.HolidayRecordList;
import com.engc.szeduecard.bean.Holidays;
import com.engc.szeduecard.bean.Notice;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;
import com.engc.szeduecard.widget.NewDataToast;
import com.engc.szeduecard.widget.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 老师 审核 假期 中心
 * 
 * @ClassName: AuditHolidayRecord
 * @Description: TODO
 * @author wutao
 * @date 2013-10-29 上午11:14:34
 * 
 */
public class HolidayRecord extends BaseActivity {

	private ListViewHoildayRecordAdapter lvHolidayRecordAdapter; // 适配器

	private List<Holidays> lvHolidayRecordData = new ArrayList<Holidays>();
	// 消息类型
	public final static int AUDIT_ED_HOLIDAY = 3;// 记录 已审核审核状态
	public final static int UN_AUDIT_HOLIDAY = 1; // 记录未审核状态

	private TextView txtHeadTitle;// activity title
	private View lvHolidayRecord_footer; // 记录底部
	private TextView lvHolidayRecord_foot_more; // 记录更多

	private PullToRefreshListView lvHolidayRecord; // 记录listview

	private Handler lvHolidayRecordHandler; // 记录handler

	private int lvHolidayRecordSumData;

	private int curHolidayRecodCatalog = HolidayRecordList.CATALOG_ALL;

	private AppContext appContext;// 全局Context

	private ProgressBar lvHolidayRecord_foot_progress; // 新闻 progressbar
	private Button imBack; // 返回按钮

	private ViewPager viewPager;// 页卡内容
	private ImageView imageView, imgItemIcon;// 动画图片
	private TextView txtReadedMsg, txtUnReadMsg;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1, view2;
	private int listviewStatusCode = 0;
	private Resources rs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.holiday_record);
		appContext = (AppContext) getApplication();
		rs = this.getResources();
		InitImageView();
		InitTextView();
		InitViewPager();
		initFrameListView(view1, UN_AUDIT_HOLIDAY);
	}

	/**
	 * 初始化viewpage
	 * 
	 */
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();

		view1 = inflater.inflate(R.layout.un_audit_holiday_item, null);
		view2 = inflater.inflate(R.layout.audited_holiday_list, null);

		views.add(view1);
		views.add(view2);

		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		imBack = (Button) findViewById(R.id.img_back);
		imBack.setOnClickListener(UIHelper.finish(this)); // 返回事件

	}

	/**
	 * 初始化头标
	 */

	private void InitTextView() {

		txtReadedMsg = (TextView) findViewById(R.id.txtunauditholiday);
		txtUnReadMsg = (TextView) findViewById(R.id.txtauditedholiday);

		txtReadedMsg.setOnClickListener(new MyOnClickListener(0));
		txtUnReadMsg.setOnClickListener(new MyOnClickListener(1));
	}

	/**
	 * 初始化所有ListView
	 */
	private void initFrameListView(View view, final int newsType) {
		// 初始化listview控件
		this.initHolidayRecordListView(view, newsType);
		this.initFrameListViewData(newsType);
	}

	/**
	 * 初始化审核记录列表
	 */
	private void initHolidayRecordListView(View view, final int recordType) {
		lvHolidayRecordAdapter = new ListViewHoildayRecordAdapter(this,
				lvHolidayRecordData, R.layout.list_holiday_record_item);

		if (listviewStatusCode != 0)
			lvHolidayRecord.removeFooterView(lvHolidayRecord_footer);

		lvHolidayRecord_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvHolidayRecord_foot_more = (TextView) lvHolidayRecord_footer
				.findViewById(R.id.listview_foot_more);
		lvHolidayRecord_foot_progress = (ProgressBar) lvHolidayRecord_footer
				.findViewById(R.id.listview_foot_progress);
		lvHolidayRecord = (PullToRefreshListView) view
				.findViewById(R.id.audit_holiday_list);
		lvHolidayRecord.addFooterView(lvHolidayRecord_footer);// 添加底部视图
																// 必须在setAdapter前
		lvHolidayRecord.setAdapter(lvHolidayRecordAdapter);

		lvHolidayRecord
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 点击头部、底部栏无效
						if (position == 0 || view == lvHolidayRecord_footer)
							return;

						Holidays holidays = null;
						// 判断是否是TextView
						if (view instanceof TextView) {
							holidays = (Holidays) view.getTag();
						} else {
							TextView tv = (TextView) view
									.findViewById(R.id.news_listitem_title);
							holidays = (Holidays) tv.getTag();
						}
						if (holidays == null)
							return;

						// 跳转到记录 详情

						UIHelper.showHolidayDetail(HolidayRecord.this, holidays);

					}
				});
		lvHolidayRecord.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvHolidayRecord.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvHolidayRecordData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvHolidayRecord_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lvHolidayRecord.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvHolidayRecord.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvHolidayRecord_foot_more.setText(R.string.load_ing);
					// lvTeams_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvHolidayRecordSumData
							/ AppContext.PAGE_SIZE;
					loadlvHolidayRecordData(curHolidayRecodCatalog, pageIndex,
							lvHolidayRecordHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL, recordType);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvHolidayRecord.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
		});
		lvHolidayRecord
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadlvHolidayRecordData(curHolidayRecodCatalog, 0,
								lvHolidayRecordHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH, recordType);
					}
				});

	}

	/**
	 * 线程加载新闻数据
	 * 
	 * @param catalog
	 *            分类
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	private void loadlvHolidayRecordData(final int catalog,
			final int pageIndex, final Handler handler, final int action,
			final int recordType) {
		// mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {

					HolidayRecordList list = appContext.getHolidaysRecortdList(
							0, false, appContext.getLoginInfo().getUsercode(),
							String.valueOf(recordType));
					msg.what = list.getHolidayslist().size();
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
				if (curHolidayRecodCatalog == catalog)
					handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 获取listview的初始化Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @return
	 */
	private Handler getLvHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress, final int pageSize) {
		return new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					// listview数据处理
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2,
							msg.arg1);
					listviewStatusCode++;

					if (msg.what < pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}

				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(HolidayRecord.this);
				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				// mHeadProgress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					lv.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
	}

	/**
	 * listview数据处理
	 * 
	 * @param what
	 *            数量
	 * @param obj
	 *            数据
	 * @param objtype
	 *            数据类型
	 * @param actiontype
	 *            操作类型
	 * @return notice 通知信息
	 */
	private Notice handleLvData(int what, Object obj, int objtype,
			int actiontype) {
		Notice notice = null;
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_DATATYPE_NEWS:
				HolidayRecordList nlist = (HolidayRecordList) obj;
				notice = nlist.getNotice();
				lvHolidayRecordSumData = what;
				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvHolidayRecordData.size() > 0) {
						for (Holidays holidays1 : nlist.getHolidayslist()) {
							boolean b = false;
							for (Holidays holidays2 : lvHolidayRecordData) {
								if (holidays1.getId() == holidays2.getId()) {
									b = true;
									break;
								}
							}
							if (!b)
								newdata++;
						}
					} else {
						newdata = what;
					}
				}
				lvHolidayRecordData.clear();// 先清除原有数据
				lvHolidayRecordData.addAll(nlist.getHolidayslist());
				break;

			}
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
				// 提示新加载数据
				if (newdata > 0) {
					NewDataToast
							.makeText(
									this,
									getString(R.string.new_data_toast_message,
											newdata), appContext.isAppSound())
							.show();
				} else {
					NewDataToast.makeText(this,
							getString(R.string.new_data_toast_none), false)
							.show();
				}
			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			switch (objtype) {
			case UIHelper.LISTVIEW_DATATYPE_NEWS:
				HolidayRecordList list = (HolidayRecordList) obj;
				notice = list.getNotice();
				lvHolidayRecordSumData += what;
				if (lvHolidayRecordData.size() > 0) {
					for (Holidays holidays1 : list.getHolidayslist()) {
						boolean b = false;
						for (Holidays holidays2 : lvHolidayRecordData) {
							if (holidays1.getId() == holidays2.getId()) {
								b = true;
								break;
							}
						}
						if (!b)
							lvHolidayRecordData.add(holidays1);
					}
				} else {
					lvHolidayRecordData.addAll(list.getHolidayslist());
				}
				break;
			}
		}
		return notice;

	}

	/**
	 * 初始化所有ListView数据
	 */
	private void initFrameListViewData(final int newsType) {
		// 初始化Handler
		lvHolidayRecordHandler = this.getLvHandler(lvHolidayRecord,
				lvHolidayRecordAdapter, lvHolidayRecord_foot_more,
				lvHolidayRecord_foot_progress, AppContext.PAGE_SIZE);
		// 加载资讯数据
		lvHolidayRecordData.clear();
		if (lvHolidayRecordData.isEmpty()) {
			loadlvHolidayRecordData(curHolidayRecodCatalog, 0,
					lvHolidayRecordHandler, UIHelper.LISTVIEW_ACTION_INIT,
					newsType);
		}
	}

	/**
	 * 2 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据 3
	 */

	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.album_box_4).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 
	 * 头标点击监听 3
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}

	}

	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@SuppressLint("ResourceAsColor")
		public void onPageSelected(int arg0) {
			/*
			 * 两种方法，这个是一种，下面还有一种，显然这个比较麻烦 Animation animation = null;
			 */

			switch (arg0) {
			case 0:

				txtReadedMsg.setTextColor(rs
						.getColor(R.color.detail_node_title));
				txtUnReadMsg.setTextColor(rs
						.getColor(R.color.review_header_title));
				txtReadedMsg.setTextSize(16);
				txtUnReadMsg.setTextSize(14);
				initFrameListView(view1, UN_AUDIT_HOLIDAY);

				break;
			case 1:
				txtUnReadMsg.setTextColor(rs
						.getColor(R.color.detail_node_title));
				txtReadedMsg.setTextColor(rs
						.getColor(R.color.review_header_title));
				txtUnReadMsg.setTextSize(16);
				txtReadedMsg.setTextSize(14);
				initFrameListView(view2, AUDIT_ED_HOLIDAY);
				break;

			}

			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);
			// Toast.makeText(WeiBoActivity.this, "您选择了"+
			// viewPager.getCurrentItem()+"页卡", Toast.LENGTH_SHORT).show();
		}

	}
}
