package com.engc.szeduecard.bean;

import android.app.Notification;

/**
 * 下载任务 实体
 * @ClassName: DownloadTask 
 * @Description: TODO
 * @author wutao
 * @date 2013-12-15 下午3:11:36 
 *
 */
public class DownloadTask {
	private String url;  
    private int notifyID;  
    private Notification notification;  
  
    public DownloadTask()  
    {  
        // TODO Auto-generated constructor stub  
    }  
  
    public Notification getNotification()  
    {  
        return notification;  
    }  
  
    public void setNotification(Notification notification)  
    {  
        this.notification = notification;  
    }  
  
    public int getNotifyID()  
    {  
        return notifyID;  
    }  
  
    public void setNotifyID(int notifyID)  
    {  
        this.notifyID = notifyID;  
    }  
  
    public String getUrl()  
    {  
        return url;  
    }  
  
    public void setUrl(String url)  
    {  
        this.url = url;  
    }  

}
