package com.engc.szeduecard.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;

/**
 *  将Imageview 转换成 圆角 视图
 * @author samwu
 *
 */
public class ImageCorner  {
	
	// 将图片圆角显示的函数,返回Bitmap
		public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			// 根据原来图片大小画一个矩形
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			// 圆角弧度参数,数值越大圆角越大,甚至可以画圆形
			final float roundPx = 12;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			// 画出一个圆角的矩形
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			// 取两层绘制交集,显示上层
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			// 显示图片
			canvas.drawBitmap(bitmap, rect, rect, paint);
			// 返回Bitmap对象
			return output;
		}
}
