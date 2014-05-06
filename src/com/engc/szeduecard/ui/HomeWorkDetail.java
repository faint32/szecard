package com.engc.szeduecard.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.engc.szeduecard.R;
import com.engc.szeduecard.common.Constant;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.services.DownLoadService;

/**
 * 家庭作业详细
 * 
 * @ClassName: HomeWorkDetail
 * @Description: TODO
 * @author wutao
 * @date 2013-12-15 下午1:24:01
 * 
 */
public class HomeWorkDetail extends BaseActivity {
	private Button btnBack, btnDownload;
	private TextView txtHName, txtHType, txtSubject, txtDesc;
	private String hwName, hwType, hwSubject, hwDesc, hwURL, savePath,
			hwFilePath;
	// 下载线程
	private Thread downLoadThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homework_detial_main);

		initView();
		initData();

	}

	/**
	 * 初始化 数据
	 */
	private void initData() {
		hwName = getIntent().getStringExtra("hwName");
		hwSubject = getIntent().getStringExtra("hwSubject");
		hwType = getIntent().getStringExtra("hwType");
		hwDesc = getIntent().getStringExtra("hwDesc");
		hwURL = getIntent().getStringExtra("hwURL");
		txtHName.setText(hwName);
		txtHType.setText(hwType.equals("1") ? "课外作业" : "家庭作业");
		txtDesc.setText(hwDesc != null ? hwDesc : "");
		txtSubject.setText(hwSubject);

	}

	/**
	 * 初始化视图
	 */
	private void initView() {

		txtHName = (TextView) findViewById(R.id.txt_homework_detail_name);
		txtHType = (TextView) findViewById(R.id.txt_homework_detail_type);
		txtSubject = (TextView) findViewById(R.id.txt_homework_detail_subject);
		txtDesc = (TextView) findViewById(R.id.txt_homework_detail_desc);
		btnBack = (Button) findViewById(R.id.btn_home_detail_back);
		btnBack.setOnClickListener(UIHelper.finish(this));
		btnDownload = (Button) findViewById(R.id.btn_downloadHM);
		btnDownload.setOnClickListener(downLoadHM);

	}

	/**
	 * 下载作业
	 */
	private OnClickListener downLoadHM = new OnClickListener() {

		@Override
		public void onClick(View v) {
			/*downLoadThread = new Thread(mdownApkRunnable);
			downLoadThread.start();*/
			Intent i = new Intent(HomeWorkDetail.this, DownLoadService.class);
			i.putExtra("url", Constant.FILESERVERPATHBASEHOST+hwURL);
			i.putExtra("notifyId", 0);
			startService(i);
		}
	};

	/**
	 * 下载文件
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {

				// 判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/szedu/homework/";
					File file = new File(savePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					hwFilePath = savePath + hwName;
				}

				// 没有挂载SD卡，无法下载文件
				if (hwFilePath == null || hwFilePath == "") {

					return;
				}

				File ApkFile = new File(hwFilePath);

				// 输出临时下载文件
				File tmpFile = new File(hwFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);

				URL url = new URL(Constant.FILESERVERPATHBASEHOST + hwURL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				// 显示文件大小格式：2个小数点显示
				DecimalFormat df = new DecimalFormat("0.00");
				// 进度条下面显示的总文件大小
				// apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

				int count = 0;
				byte buf[] = new byte[1024];

				int numread = is.read(buf);
				count += numread;

				if (numread <= 0) {
					// 下载完成 - 将临时下载文件转成APK文件
					if (tmpFile.renameTo(ApkFile)) {
						// 通知安装
						// mHandler.sendEmptyMessage(DOWN_OVER);
					}
				}
				fos.write(buf, 0, numread);

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};
}
