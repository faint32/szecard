package com.engc.szeduecard.widget;




import com.engc.szeduecard.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 升级版 toast 提示控件
 * @ClassName: NewDataToast 
 * @Description: TODO
 * @author wutao
 * @date 2013-10-29 下午2:33:06 
 *
 */
public class NewDataToast extends Toast{
	
	private MediaPlayer mPlayer;
	private boolean isSound;
	
	public NewDataToast(Context context) {
		this(context, false);
	}
	
	public NewDataToast(Context context, boolean isSound) {
		super(context);
		
		this.isSound = isSound;

        mPlayer = MediaPlayer.create(context, R.raw.newdatatoast);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}        	
        });

    }

	@Override
	public void show() {
		super.show();
		
		if(isSound){
			mPlayer.start();
		}
	}
	
	/**
	 * 设置是否播放声音
	 */
	public void setIsSound(boolean isSound) {
		this.isSound = isSound;
	}
	
	/**
	 * 获取控件实例
	 * @param context
	 * @param text 提示消息
	 * @param isSound 是否播放声音
	 * @return
	 */
	public static NewDataToast makeText(Context context, CharSequence text, boolean isSound) {
		NewDataToast result = new NewDataToast(context, isSound);
		
        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        
        View v = inflate.inflate(R.layout.new_data_toast, null);
        v.setMinimumWidth(dm.widthPixels);//设置控件最小宽度为手机屏幕宽度
        
        TextView tv = (TextView)v.findViewById(R.id.new_data_toast_message);
        tv.setText(text);
        
        result.setView(v);
        result.setDuration(600);
        result.setGravity(Gravity.TOP, 0, (int)(dm.density*75));

        return result;
    }
	
}
