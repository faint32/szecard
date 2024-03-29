package com.engc.szeduecard.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.engc.szeduecard.R;
import com.engc.szeduecard.bean.DownloadTask;
import com.engc.szeduecard.common.DownloadUtil;
import com.engc.szeduecard.common.DownloadUtil.IOnDownloadListener;
import com.engc.szeduecard.common.NotificationBean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

/**
 * 下载服务
 * @ClassName: DownLoadService 
 * @Description: TODO
 * @author wutao
 * @date 2013-12-15 下午3:06:25 
 *
 */
public class DownLoadService  extends Service{
	 private Context mContext = DownLoadService.this;  
	    /** 正在下载 */  
	    private final int DOWN_LOADING = 0;  
	    /** 下载完成 */  
	    private final int DOWN_COMPLETE = 1;  
	    /** 下载失败 */  
	    private final int DOWN_ERR = 2;  
	    /** Timer 执行时间间隔 */  
	    private final int TIMER_PERIOD = 1500;  
	  
	    protected Timer mTimer;   
	    protected NotificationManager mNotificationManager;  
	    /** 下载任务管理 */  
	    protected Map<String, DownloadTask> map_downloadtask;  
	  
	    @Override  
	    public IBinder onBind(Intent intent)  
	    {  
	        return null;  
	    }  
	  
	    @Override  
	    public void onDestroy()  
	    {  
	        super.onDestroy();  
	    }  
	  
	    @Override  
	    public void onCreate()  
	    {  
	        // TODO Auto-generated method stub  
	        super.onCreate();  
	        mTimer = new Timer();  
	        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
	        map_downloadtask = new HashMap<String, DownloadTask>();  
	    }  
	  
	      
	    @Override  
	    public int onStartCommand(Intent intent, int flags, int startId)  
	    {  
	        String mUrl = intent.getExtras().getString("url");  
	        int mNotifyId = intent.getExtras().getInt("notifyId");  
	        Notification mNotification = new NotificationBean(this, R.drawable.app_icon, "开始下载", System.currentTimeMillis());  
	        System.out.println("NotifyId = " + mNotifyId);  
	          
	        if(map_downloadtask.containsKey(mUrl)){  
	            Toast.makeText(mContext, "已存在此下载任务", Toast.LENGTH_SHORT).show();  
	        }  
	        else{  
	            DownloadTask mDownloadTask = new DownloadTask();  
	            mDownloadTask.setUrl(mUrl);  
	            mDownloadTask.setNotifyID(mNotifyId);  
	            mDownloadTask.setNotification(mNotification);  
	            map_downloadtask.put(mUrl, mDownloadTask);  
	              
	            Runnable mRunnable = new MyRunnable(mDownloadTask);  
	            new Thread(mRunnable).start();  
	        }  
	  
	        return super.onStartCommand(intent, flags, startId);  
	    }  
	  
	  
	      
	    class MyRunnable implements Runnable  
	    {  
	        private DownloadUtil mDownUtil = new DownloadUtil();  
	        private DownloadTask mDownTask;  
	        private Handler mHandler;  
	        private TimerTask mTimerTask;  
	  
	        public MyRunnable(DownloadTask downTask)  
	        {  
	            super();  
	            this.mDownTask = downTask;  
	            this.mHandler = new MyHandler(mDownUtil);  
	            this.mTimerTask = new MyTimerTask(mDownUtil, mHandler, mDownTask);  
	        }  
	  
	        @Override  
	        public void run()  
	        {  
	            mTimer.schedule(mTimerTask, 0, TIMER_PERIOD);  
	            mDownUtil.downloadFile(mDownTask.getUrl());  
	        }  
	  
	    }  
	  
	      
	    class MyTimerTask extends TimerTask  
	    {  
	        private Handler mHandler;  
	        private DownloadUtil mDownUtil;  
	        private DownloadTask mDownTask;  
	        private IOnDownloadListener mListener;  
	          
	        public MyTimerTask(DownloadUtil downUtil, Handler handler, DownloadTask downTask)  
	        {  
	            super();  
	            this.mHandler = handler;  
	            this.mDownUtil = downUtil;  
	            this.mDownTask = downTask;  
	            this.mListener = new IOnDownloadListener()  
	            {  
	                @Override  
	                public void updateNotification(int progress, int totalSize, File downFile)  
	                {  
	                    // TODO Auto-generated method stub  
	                    int rate = 0;  
	                    // 计算百分比  
	                    if (totalSize > 0)  
	                    {  
	                        rate = progress * 100 / totalSize;  
	                        mHandler.obtainMessage(DOWN_LOADING, rate, mDownTask.getNotifyID(), mDownTask.getNotification()).sendToTarget();  
	                    }  
	                    else if (totalSize == 0)  
	                    {  
	                        mHandler.obtainMessage(DOWN_LOADING, 0, mDownTask.getNotifyID(), mDownTask.getNotification()).sendToTarget();  
	                    }  
	                    else {  
	                        cancel();  
	                        mHandler.obtainMessage(DOWN_ERR, mDownTask).sendToTarget();  
	                    }  
	                    // 是否下载结束  
	                    if (totalSize > 0 && null != downFile && totalSize == (int) downFile.length())  
	                    {  
	                        cancel();  
	                        mHandler.obtainMessage(DOWN_COMPLETE, downFile).sendToTarget();  
	                        map_downloadtask.remove(mDownTask.getUrl());// 移除已完成任务  
	                        System.out.println("DOWN_COMPLETE ==> totalSize ==> " + totalSize);  
	                    }  
	                }  
	  
	            };  
	        }  
	  
	        @Override  
	        public void run()  
	        {  
	            mDownUtil.setOnDownloadListener(mListener);  
	        }  
	    }  
	      
	  
	    class MyHandler extends Handler  
	    {  
	        private DownloadUtil mDownUtil;  
	  
	        public MyHandler(DownloadUtil downUtil)  
	        {  
	            super();  
	            this.mDownUtil = downUtil;  
	        }  
	  
	        @Override  
	        public void handleMessage(Message msg)  
	        {  
	            switch (msg.what)  
	            {  
	            case DOWN_LOADING:  
	                ((Notification)msg.obj).contentView.setProgressBar(R.id.pb, 100, msg.arg1, false);  
	                ((Notification)msg.obj).contentView.setTextViewText(R.id.tv, "下载" + msg.arg1 + "%");  
	                mNotificationManager.notify(msg.arg2, ((Notification)msg.obj));  
	                System.out.println("DOWN_LOADING --> mNotifyId --> " + msg.arg2 + " --> " + msg.arg1 + "%");  
	                break;  
	            case DOWN_COMPLETE:  
//	              mNotificationManager.cancel(mNotifyId);  
	                removeMessages(DOWN_LOADING);  
	                Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();  
	                mDownUtil.installApk(mContext, (File)msg.obj);  
	                System.out.println("======================DOWN_COMPLETE================================");  
	                stopService();  
	                break;  
	            case DOWN_ERR:  
	                removeMessages(DOWN_LOADING);  
	                map_downloadtask.remove(((DownloadTask)msg.obj).getUrl());  
	                Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();  
	                stopService();  
	                break;  
	            default:  
	                break;  
	            }  
	        }  
	          
	        /** 
	         * 如果无下载任务，关闭服务 
	         */  
	        private void stopService(){  
	            if(map_downloadtask.isEmpty()){  
	                stopSelf(-1);  
	            }  
	        }  
	  
	    }  
	  
	}  

