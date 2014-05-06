package com.engc.szeduecard.adapter;





import com.engc.szeduecard.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
/**
 * 
 * @ClassName: GridViewFaceAdapter 
 * @Description:表情适配器
 * @author wutao
 * @date 2013-8-28 下午1:10:21 
 *
 */
public class GridViewFaceAdapter extends BaseAdapter {

	// 定义Context
		private Context	mContext;
		// 定义整型数组 即图片源
		private static int[] mImageIds = new int[]{
				R.drawable.f001,R.drawable.f002,R.drawable.f003,R.drawable.f004,R.drawable.f005,R.drawable.f006,
				R.drawable.f007,R.drawable.f008,R.drawable.f009,R.drawable.f010,R.drawable.f011,R.drawable.f012,
				R.drawable.f013,R.drawable.f014,R.drawable.f015,R.drawable.f016,R.drawable.f017,R.drawable.f018,
				R.drawable.f019,R.drawable.f020,R.drawable.f021,R.drawable.f022,R.drawable.f023,R.drawable.f024,
				R.drawable.f025,R.drawable.f026,R.drawable.f027,R.drawable.f028,R.drawable.f029,R.drawable.f030,
				R.drawable.f031,R.drawable.f032,R.drawable.f033,R.drawable.f034,R.drawable.f035,R.drawable.f036,
				R.drawable.f037,R.drawable.f038,R.drawable.f039,R.drawable.f040,R.drawable.f041,R.drawable.f042,
				R.drawable.f043,R.drawable.f044,R.drawable.f045,R.drawable.f046,R.drawable.f047,R.drawable.f048,
				R.drawable.f049,R.drawable.f050,R.drawable.f051,R.drawable.f052,R.drawable.f053,R.drawable.f054,
				R.drawable.f055
			};

		public static int[] getImageIds()
		{
			return mImageIds;
		}
		
		public GridViewFaceAdapter(Context c)
		{
			mContext = c;
		}
		
		// 获取图片的个数
		public int getCount()
		{
			return mImageIds.length;
		}

		// 获取图片在库中的位置
		public Object getItem(int position)
		{
			return position;
		}


		// 获取图片ID
		public long getItemId(int position)
		{
			return mImageIds[position];
		}


		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView imageView;
			if (convertView == null)
			{
				imageView = new ImageView(mContext);
				// 设置图片n×n显示
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				// 设置显示比例类型
				imageView.setScaleType(ImageView.ScaleType.CENTER);
			}
			else
			{
				imageView = (ImageView) convertView;
			}
			
			imageView.setImageResource(mImageIds[position]);
			if(position < 65)
				imageView.setTag("["+(position+1)+"]");
		/*	else if(position < 100)
				imageView.setTag("["+(position+1)+"]");*/
			else
				imageView.setTag("["+(position+2)+"]");
			
			return imageView;
		}
}
