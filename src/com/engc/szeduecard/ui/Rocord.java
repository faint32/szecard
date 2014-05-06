package com.engc.szeduecard.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListAdapter;

import com.engc.szeduecard.R;
import com.engc.szeduecard.adapter.ExpandableListAdapter;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.widget.ExpandableListView;

/**
 * 
 * @ClassName: Rocord
 * @Description: 账单
 * @author wutao
 * @date 2013-10-10 下午5:13:53
 * 
 */
public class Rocord extends BaseActivity {
	private ExpandableListView recordList;
	private ExpandableListAdapter listAdapter;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		prepareListData();
		initView();

	}

	private void initView() {
		recordList = (ExpandableListView) findViewById(R.id.recordList);
		listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild);

		recordList.setAdapter(listAdapter);

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

	/*
	 * 初始化数据
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("本月 ");
		listDataHeader.add("九月");
		listDataHeader.add("八月");

		// Adding child data
		List<String> top250 = new ArrayList<String>();
		top250.add("充值消费500.00元");
		top250.add("乘车消费15.00元");
		top250.add("城市轨道交通8.00元");
		top250.add("学校食堂支出320.00元");

		List<String> nowShowing = new ArrayList<String>();
		nowShowing.add("城市公交消费8.00元");
		nowShowing.add("图书馆消费100.00元");
		nowShowing.add("城市轨道交通12.00");
		nowShowing.add("工商银行充值500.00元");

		List<String> comingSoon = new ArrayList<String>();
		comingSoon.add("支付宝充值1000.00元");
		comingSoon.add("星巴克咖啡32.00元");
		comingSoon.add("博物馆展览10.00元");
		comingSoon.add("科技馆一日展览游200.00元");

		listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
		listDataChild.put(listDataHeader.get(1), nowShowing);
		listDataChild.put(listDataHeader.get(2), comingSoon);
	}

}
