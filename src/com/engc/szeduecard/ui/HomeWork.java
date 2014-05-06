package com.engc.szeduecard.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.engc.szeduecard.R;
import com.engc.szeduecard.R.color;
import com.engc.szeduecard.adapter.ListViewHomeWorkAdapter;
import com.engc.szeduecard.bean.HomeWorkList;
import com.engc.szeduecard.bean.Notice;
import com.engc.szeduecard.common.FileUtils;
import com.engc.szeduecard.common.ImageUtils;
import com.engc.szeduecard.common.MediaUtils;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppConfig;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;
import com.engc.szeduecard.widget.NewDataToast;
import com.engc.szeduecard.widget.PullToRefreshListView;

/**
 * 
 * @ClassName: HomeWork
 * @Description: 家庭作业
 * @author wutao
 * @date 2013-10-11 下午4:28:02
 * 
 */
public class HomeWork extends BaseActivity {

	private ListViewHomeWorkAdapter lvhomeWorkAdapter; // 适配器

	private List<com.engc.szeduecard.bean.HomeWork> lvhomeWorkData = new ArrayList<com.engc.szeduecard.bean.HomeWork>();
	// 消息类型
	public final static int TOADY_HOMEWORK = 1;// 今天的家庭作业
	public final static int BEFORE_HOMEWORK = 0; // 历史家庭作业

	private View lvHomeWork_footer; // 记录底部
	private TextView lvHomeWork_foot_more; // 记录更多

	private PullToRefreshListView lvHomeWork; // 记录listview

	private Handler lvHomeWorkHandler; // 记录handler

	private int lvHomeWorkSumData;

	private int curHomeWorkCatalog = HomeWorkList.CATALOG_ALL;

	private AppContext appContext;// 全局Context

	private ProgressBar lvHomeWork_foot_progress; // 新闻 progressbar

	private ViewPager viewPager;// 页卡内容
	private ImageView imageView;// 动画图片
	private TextView txtCurrentHomeWork, txtHistoryHomeWork;
	private List<View> views;// Tab页面列表
	private View view1, view2;// 各个页卡
	private float offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private float bmpW;// 动画图片宽度
	private float one, two;
	private Resources rs = null;
	private ImageView imgUploadFile;
	private Button imgBack;
	private String theLarge;
	private String tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE;
	private ImageView mImage;
	private String theThumbnail;
	private File imgFile;
	private int listviewStatusCode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homework);
		rs = this.getResources();
		appContext = (AppContext) getApplication();
		initView();
		InitImageView();
		InitViewPager();
		initFrameListView(view1, TOADY_HOMEWORK);
	}

	private void initView() {
		imgBack = (Button) findViewById(R.id.homework_head_back);
		imgBack.setOnClickListener(UIHelper.finish(this));
		imgUploadFile = (ImageView) findViewById(R.id.btn_upload_homework);
		imgUploadFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CharSequence[] items = {
						HomeWork.this.getString(R.string.img_from_album),
						HomeWork.this.getString(R.string.img_from_camera) };
				imageChooseItem(items);

			}
		});
		txtCurrentHomeWork = (TextView) findViewById(R.id.txtcurrenthomework);
		txtHistoryHomeWork = (TextView) findViewById(R.id.txthistoryhomework);
		txtCurrentHomeWork.setOnClickListener(new MyOnClickListener(0));
		txtHistoryHomeWork.setOnClickListener(new MyOnClickListener(1));

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
		float screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.currenthomework_item, null);
		view2 = inflater.inflate(R.layout.historyhomework, null);

		views.add(view1);
		views.add(view2);

		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	/**
	 * 初始化所有ListView
	 */
	private void initFrameListView(View view, final int newsType) {
		// 初始化listview控件
		this.initHomeWorkListView(view, newsType);
		this.initFrameListViewData(newsType);
	}

	/**
	 * 初始化所有ListView数据
	 */
	private void initFrameListViewData(final int newsType) {
		// 初始化Handler
		lvHomeWorkHandler = this.getLvHandler(lvHomeWork, lvhomeWorkAdapter,
				lvHomeWork_foot_more, lvHomeWork_foot_progress,
				AppContext.PAGE_SIZE);
		// 加载资讯数据
		lvhomeWorkData.clear();
		if (lvhomeWorkData.isEmpty()) {
			loadlvHolidayRecordData(curHomeWorkCatalog, 0, lvHomeWorkHandler,
					UIHelper.LISTVIEW_ACTION_INIT, newsType);
		}
	}

	/**
	 * 初始化审核记录列表
	 */
	private void initHomeWorkListView(View view, final int recordType) {
		lvhomeWorkAdapter = new ListViewHomeWorkAdapter(this, lvhomeWorkData,
				R.layout.list_holiday_record_item);

		if (listviewStatusCode != 0)
			lvHomeWork.removeFooterView(lvHomeWork_footer);

		lvHomeWork_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvHomeWork_foot_more = (TextView) lvHomeWork_footer
				.findViewById(R.id.listview_foot_more);
		lvHomeWork_foot_progress = (ProgressBar) lvHomeWork_footer
				.findViewById(R.id.listview_foot_progress);
		lvHomeWork = (PullToRefreshListView) view
				.findViewById(R.id.audit_holiday_list);
		lvHomeWork.addFooterView(lvHomeWork_footer);// 添加底部视图
													// 必须在setAdapter前
		lvHomeWork.setAdapter(lvhomeWorkAdapter);

		lvHomeWork
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 点击头部、底部栏无效
						if (position == 0 || view == lvHomeWork_footer)
							return;

						com.engc.szeduecard.bean.HomeWork homework = null;
						// 判断是否是TextView
						if (view instanceof TextView) {
							homework = (com.engc.szeduecard.bean.HomeWork) view
									.getTag();
						} else {
							TextView tv = (TextView) view
									.findViewById(R.id.news_listitem_title);
							homework = (com.engc.szeduecard.bean.HomeWork) tv
									.getTag();
						}
						if (homework == null)
							return;

						// 跳转到家庭作业详情

						UIHelper.showHomeWorkDetail(HomeWork.this, homework);
					}
				});
		lvHomeWork.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvHomeWork.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvhomeWorkData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvHomeWork_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lvHomeWork.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvHomeWork.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvHomeWork_foot_more.setText(R.string.load_ing);
					// lvTeams_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = lvHomeWorkSumData / AppContext.PAGE_SIZE;
					loadlvHolidayRecordData(curHomeWorkCatalog, pageIndex,
							lvHomeWorkHandler, UIHelper.LISTVIEW_ACTION_SCROLL,
							recordType);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvHomeWork.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		lvHomeWork
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadlvHolidayRecordData(curHomeWorkCatalog, 0,
								lvHomeWorkHandler,
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
					// ////
					HomeWorkList list = appContext.getHomeWorkList(pageIndex,
							false, appContext.getLoginInfo().getUsercode(),
							String.valueOf(recordType));
					msg.what = list.getHomeWorkslist().size();
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
				if (curHomeWorkCatalog == catalog)
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
					((AppException) msg.obj).makeToast(HomeWork.this);
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
				HomeWorkList nlist = (HomeWorkList) obj;
				notice = nlist.getNotice();
				lvHomeWorkSumData = what;
				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvhomeWorkData.size() > 0) {
						for (com.engc.szeduecard.bean.HomeWork holidays1 : nlist
								.getHomeWorkslist()) {
							boolean b = false;
							for (com.engc.szeduecard.bean.HomeWork holidays2 : lvhomeWorkData) {
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
				lvhomeWorkData.clear();// 先清除原有数据
				lvhomeWorkData.addAll(nlist.getHomeWorkslist());
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
				HomeWorkList list = (HomeWorkList) obj;
				notice = list.getNotice();
				lvHomeWorkSumData += what;
				if (lvhomeWorkData.size() > 0) {
					for (com.engc.szeduecard.bean.HomeWork holidays1 : list
							.getHomeWorkslist()) {
						boolean b = false;
						for (com.engc.szeduecard.bean.HomeWork holidays2 : lvhomeWorkData) {
							if (holidays1.getId() == holidays2.getId()) {
								b = true;
								break;
							}
						}
						if (!b)
							lvhomeWorkData.add(holidays1);
					}
				} else {
					lvhomeWorkData.addAll(list.getHomeWorkslist());
				}
				break;
			}
		}
		return notice;

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

		// one = offset * 3 + bmpW;// 页卡1 -> 页卡2 偏移量
		// two = one * 3;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			/*
			 * 两种方法，这个是一种，下面还有一种，显然这个比较麻烦 Animation animation = null;
			 */
			// one = offset * 3 + bmpW;// 页卡1 -> 页卡2 偏移量
			// two = one * 3;// 页卡1 -> 页卡3 偏移量

			one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
			two = one * 2;// 页卡1 -> 页卡3 偏移量

			switch (arg0) {

			case 0: // 当前作业

				txtCurrentHomeWork.setTextSize(16);
				txtHistoryHomeWork.setTextSize(14);
				txtCurrentHomeWork.setTextColor(rs
						.getColor(color.detail_node_title));
				txtHistoryHomeWork.setTextColor(rs
						.getColor(color.review_header_title));
				initFrameListView(view1, TOADY_HOMEWORK);
				break;
			case 1:// 历史作业

				txtCurrentHomeWork.setTextSize(14);
				txtHistoryHomeWork.setTextSize(16);
				txtCurrentHomeWork.setTextColor(rs
						.getColor(color.review_header_title));
				txtHistoryHomeWork.setTextColor(rs
						.getColor(color.detail_node_title));
				initFrameListView(view2, BEFORE_HOMEWORK);
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

	/**
	 * 操作选择
	 * 
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.ui_insert_image)
				.setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 手机选图
						if (item == 0) {
							Intent intent = new Intent(
									Intent.ACTION_GET_CONTENT);
							intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/*");
							startActivityForResult(
									Intent.createChooser(intent, "选择图片"),
									ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
						}
						// 拍照
						else if (item == 1) {
							String savePath = "";
							// 判断是否挂载了SD卡
							String storageState = Environment
									.getExternalStorageState();
							if (storageState.equals(Environment.MEDIA_MOUNTED)) {
								savePath = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/szzyz/Camera/";// 存放照片的文件夹
								File savedir = new File(savePath);
								if (!savedir.exists()) {
									savedir.mkdirs();
								}
							}

							// 没有挂载SD卡，无法保存文件
							if (StringUtils.isEmpty(savePath)) {
								UIHelper.ToastMessage(HomeWork.this,
										"无法保存照片，请检查SD卡是否挂载");
								return;
							}

							String timeStamp = new SimpleDateFormat(
									"yyyyMMddHHmmss").format(new Date());
							String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
							File out = new File(savePath, fileName);
							Uri uri = Uri.fromFile(out);

							theLarge = savePath + fileName;// 该照片的绝对路径

							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent,
									ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
						}
					}
				}).create();

		imageDialog.show();
	}

	// 拍照或从图库中选取图片
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					// 显示图片
					mImage.setImageBitmap((Bitmap) msg.obj);
					mImage.setVisibility(View.VISIBLE);
				}
			}
		};

		new Thread() {
			public void run() {
				Bitmap bitmap = null;

				if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
					if (data == null)
						return;

					Uri thisUri = data.getData();
					String thePath = ImageUtils
							.getAbsolutePathFromNoStandardUri(thisUri);

					// 如果是标准Uri
					if (StringUtils.isEmpty(thePath)) {
						theLarge = ImageUtils.getAbsoluteImagePath(
								HomeWork.this, thisUri);
					} else {
						theLarge = thePath;
					}

					String attFormat = FileUtils.getFileFormat(theLarge);
					if (!"photo".equals(MediaUtils.getContentType(attFormat))) {
						Toast.makeText(HomeWork.this,
								getString(R.string.choose_image),
								Toast.LENGTH_SHORT).show();
						return;
					}

					// 获取图片缩略图 只有Android2.1以上版本支持
					if (AppContext
							.isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)) {
						String imgName = FileUtils.getFileName(theLarge);
						bitmap = ImageUtils.loadImgThumbnail(HomeWork.this,
								imgName,
								MediaStore.Images.Thumbnails.MICRO_KIND);
					}

					if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
						bitmap = ImageUtils
								.loadImgThumbnail(theLarge, 100, 100);
					}
				}
				// 拍摄图片
				else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
					if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
						bitmap = ImageUtils
								.loadImgThumbnail(theLarge, 100, 100);
					}
				}

				if (bitmap != null) {
					// 存放照片的文件夹
					String savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/OSChina/Camera/";
					File savedir = new File(savePath);
					if (!savedir.exists()) {
						savedir.mkdirs();
					}

					String largeFileName = FileUtils.getFileName(theLarge);
					String largeFilePath = savePath + largeFileName;
					// 判断是否已存在缩略图
					if (largeFileName.startsWith("thumb_")
							&& new File(largeFilePath).exists()) {
						theThumbnail = largeFilePath;
						imgFile = new File(theThumbnail);
					} else {
						// 生成上传的800宽度图片
						String thumbFileName = "thumb_" + largeFileName;
						theThumbnail = savePath + thumbFileName;
						if (new File(theThumbnail).exists()) {
							imgFile = new File(theThumbnail);
						} else {
							try {
								// 压缩上传的图片
								ImageUtils.createImageThumbnail(HomeWork.this,
										theLarge, theThumbnail, 800, 80);
								imgFile = new File(theThumbnail);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					// 保存动弹临时图片
					((AppContext) getApplication()).setProperty(
							tempTweetImageKey, theThumbnail);

					Message msg = new Message();
					msg.what = 1;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				}
			};
		}.start();
	}
}
