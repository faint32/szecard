package com.engc.szeduecard.widget;

import com.engc.szeduecard.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * 
 * @ClassName: LoadingDialog
 * @Description: 加载对话框
 * @author wutao
 * @date 2013-10-17 上午9:49:50
 * 
 */
public class LoadingDialog extends AlertDialog {

	private Context mContext;
	private LayoutInflater inflater;
	private LayoutParams lp;
	private TextView loadtext;

	public LoadingDialog(Context context) {
		super(context, R.style.Dialog);

		this.mContext = context;

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.loadingdialog, null);
		loadtext = (TextView) layout.findViewById(R.id.loading_text);
		setContentView(layout);

		// 设置window属性
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = 0; // 去背景遮盖
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);

	}

	public void setLoadText(String content) {
		loadtext.setText(content);
	}
}