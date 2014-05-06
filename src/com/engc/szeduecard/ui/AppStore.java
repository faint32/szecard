package com.engc.szeduecard.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.engc.szeduecard.R;
import com.engc.szeduecard.R.color;
import com.engc.szeduecard.common.UIHelper;

/**
 * 
 * @ClassName: AppStore
 * @Description: 应用中心
 * @author wutao
 * @date 2013-10-11 下午5:37:09
 * 
 */
public class AppStore extends BaseActivity {
	private ViewPager viewPager;// 页卡内容
	private ImageView imageView;// 动画图片
	private TextView txtNewsApps, txtHotApps;
	private List<View> views;// Tab页面列表
	private View view1, view2;// 各个页卡
	private float offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private float bmpW;// 动画图片宽度
	private float one, two;
	private Resources rs = null;
	private GridView gvNewsApps, gvHotApps; // 应用九宫格
	private List<String> imgtitleList = new ArrayList<String>(); // 存放应用标题list
	private List<Integer> imgList = new ArrayList<Integer>(); // 存放应用图片list

	private View[] itemViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstore);
		rs = this.getResources();
		initView();
		InitImageView();
		InitViewPager();
		initGridView(0);

	}

	private void initView() {
		txtNewsApps = (TextView) findViewById(R.id.txtnewapp);
		txtHotApps = (TextView) findViewById(R.id.txthotapp);
		txtNewsApps.setOnClickListener(new MyOnClickListener(0));
		txtHotApps.setOnClickListener(new MyOnClickListener(1));
		

	}

	/**
	 * 监听返回--是否退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 是否退出应用
			UIHelper.showMain(AppStore.this);
		}
		return flag;
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (event.getAction() == KeyEvent.ACTION_DOWN) { switch (keyCode) { case
	 * KeyEvent.KEYCODE_BACK: finish(); return true; }
	 * 
	 * } return super.onKeyDown(keyCode, event); }
	 */

	/**
	 * 初始化视图
	 */
	private void initGridView(int codeStatus) {
		switch (codeStatus) {
		case 0:
			imgtitleList.clear();
			imgList.clear();
			imgtitleList.add("技能证书");
			imgtitleList.add("任务中心");
			imgtitleList.add("平安校园");
			imgtitleList.add("家庭作业");
			imgList.add(R.drawable.icon_tech_certificate);
			imgList.add(R.drawable.icon_task_center);
			imgList.add(R.drawable.icon_safe_campus);
			imgList.add(R.drawable.icon_homework);
			gvNewsApps.setAdapter(new GridViewModemAdapter(imgtitleList,
					imgList));

			gvNewsApps.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0:
						UIHelper.showAppDetail(AppStore.this, "技能证书",
								"icon_tech_certificate", "1.0", "758kB",
								"个人资格技能证书查询");
						break;
					case 1:
						UIHelper.showAppDetail(AppStore.this, "任务中心",
								"icon_task_center", "1.0", "235kB",
								"记录、保存、自己的日程，任务安排");
						break;
					case 2:
						UIHelper.showAppDetail(AppStore.this, "家庭作业",
								"icon_homework", "1.0", "564kb",
								"即时查看下载最新的家庭作业");
						break;

					default:
						UIHelper.showAppDetail(AppStore.this, "平安校园",
								"icon_safe_campus", "1.1", "243kB",
								"我们都有一个美丽安全的校园");

						break;
					}

				}
			});
			break;

		default:

			imgtitleList.clear();
			imgList.clear();
			imgtitleList.add("我的课程表");
			imgtitleList.add("优秀作文");
			imgtitleList.add("成长记录");
			imgList.add(R.drawable.icon_curriculum);
			imgList.add(R.drawable.icon_good_atricle);
			imgList.add(R.drawable.icon_growing_record);
			gvHotApps
					.setAdapter(new GridViewModemAdapter(imgtitleList, imgList));

			gvHotApps.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0:
						UIHelper.showAppDetail(AppStore.this, "我的课程表",
								"icon_curriculum", "1.1", "658kB",
								"year!有了我的课程表，妈妈再也不用担心我的学习呢");
						break;
					case 1:
						UIHelper.showAppDetail(AppStore.this, "优秀作文",
								"icon_good_atricle", "1.2", "658kB",
								"全国新概念作文展示，学习");
						break;

					default:
						UIHelper.showAppDetail(AppStore.this, "成长记录",
								"icon_growing_record", "1.0", "758kB",
								"成长的烦恼、幸福、甜蜜、童真");
						break;
					}

				}
			});

			break;
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
		view1 = inflater.inflate(R.layout.new_app_item, null);
		view2 = inflater.inflate(R.layout.hotapp_item, null);

		views.add(view1);
		views.add(view2);
		gvNewsApps = (GridView) view1.findViewById(R.id.gvnewapps);
		gvHotApps = (GridView) view2.findViewById(R.id.gvhotapps);

		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

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

			case 0: // 最新应用

				txtNewsApps.setTextSize(16);
				txtHotApps.setTextSize(14);
				txtNewsApps.setTextColor(rs.getColor(color.detail_node_title));
				txtHotApps.setTextColor(rs.getColor(color.review_header_title));
				initGridView(0);
				break;
			case 1:// 热门应用

				txtNewsApps.setTextSize(14);
				txtHotApps.setTextSize(16);
				txtNewsApps
						.setTextColor(rs.getColor(color.review_header_title));
				txtHotApps.setTextColor(rs.getColor(color.detail_node_title));
				initGridView(1);
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
				LayoutInflater inflater = (LayoutInflater) AppStore.this
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

}
