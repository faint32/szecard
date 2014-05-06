package com.engc.szeduecard.adapter;

import java.util.List;

import com.engc.szeduecard.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @ClassName: LeaveTypeAdapter
 * @Description: 请假类型adapter
 * @author wutao
 * @date 2013-10-16 下午1:49:53
 * 
 */

public class LeaveTypeAdapter extends BaseAdapter {

	private Context context;// 运行上下文
	private List<String> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源

	static class ListItemView { // 自定义控件集合
		public TextView id;
		public TextView title;

	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public LeaveTypeAdapter(Context context, List<String> data) {
		this.context = context;
		// this.itemViewResource = resource;
		this.listItems = data;
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return listItems.get(arg0);
	}

	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.leave_type_text, null);
			holder = new ViewHolder();

			convertView.setTag(holder);
			holder.groupItem = (TextView) convertView
					.findViewById(R.id.tv_text);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.groupItem.setText(listItems.get(position));

		return convertView;
	}

	static class ViewHolder {
		TextView groupItem;
	}

}
