package com.engc.szeduecard.widget;

import com.engc.szeduecard.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LoadingDialogTwo extends Dialog {
	private ImageView mImageView;
	private Animation mAnimation;
	private Context mContext;

	public LoadingDialogTwo(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageView = new ImageView(mContext);
		mImageView.setImageResource(R.drawable.ram_loading_fg);
		mImageView.setBackgroundResource(R.drawable.ram_loading_bg);
		setContentView(mImageView);
		mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
		mImageView.setAnimation(mAnimation);
		mAnimation.startNow();
	}

}
