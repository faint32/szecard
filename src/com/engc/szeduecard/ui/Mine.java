package com.engc.szeduecard.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.engc.szeduecard.R;
import com.engc.szeduecard.bean.Result;
import com.engc.szeduecard.bean.UserInfo;
import com.engc.szeduecard.common.FileUtils;
import com.engc.szeduecard.common.ImageUtils;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;
import com.engc.szeduecard.ui.Main.GridViewModemAdapter;
import com.engc.szeduecard.widget.LoadingDialog;



/**
 * 
 * @ClassName: Mine
 * @Description: 个人中心activity
 * @author wutao
 * @date 2013-10-10 下午5:24:09
 * 
 */
public class Mine extends BaseActivity {
	private ImageView imgSendDynamic;
	private ImageView imgNotification;
	private ImageView imgUserFace;
	private TextView txtUserName,txtDilogTitle,txtOneNodeTitle,txtTwoNodeTitle;
	private AppContext ac;
	private Button btnUserLogout;

	private Handler mHandler;

	private final static int CROP = 200;
	private final static String FILE_SAVEPATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/szcloud/Portrait/";
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;
	private LoadingDialog loading;

	private UserInfo user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine);
		ac = (AppContext) getApplication();
		initView();

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

	/**
	 * 退出登录
	 */
	private OnClickListener userLogoutClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			UIHelper.Logout(Mine.this);
			finish();

		}
	};

	private void initView() {
		imgSendDynamic = (ImageView) findViewById(R.id.mine_send_dynamic);
		imgNotification = (ImageView) findViewById(R.id.notification_message);
		imgUserFace = (ImageView) findViewById(R.id.imguserface);
		imgUserFace.setOnClickListener(chooiceUserFaceEvent);
		txtUserName = (TextView) findViewById(R.id.txt_mine_user_name);
		btnUserLogout = (Button) findViewById(R.id.btnuserlogout);
		btnUserLogout.setOnClickListener(userLogoutClick);
		UIHelper.showLoadRoundedImage(imgUserFace, null, "加载头像异常");
		txtUserName.setText(ac.getLoginInfo().getUsername());

		imgSendDynamic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showTweetPub(Mine.this);
			}
		});

		imgNotification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "此功能暂未开放^^", 500).show();
			}
		});
	}
	//更改用户头像
	private OnClickListener chooiceUserFaceEvent=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			CharSequence[] items = { getString(R.string.img_from_album),
					getString(R.string.img_from_camera) };
			imageChooseItem(items);
			
		}
	};
	
	
	
	/**
	 * 操作选择
	 * 
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(this)
				.setTitle("上传头像").setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 判断是否挂载了SD卡
						String storageState = Environment
								.getExternalStorageState();
						if (storageState.equals(Environment.MEDIA_MOUNTED)) {
							File savedir = new File(FILE_SAVEPATH);
							if (!savedir.exists()) {
								savedir.mkdirs();
							}
						} else {
							UIHelper.ToastMessage(Mine.this,
									"无法保存上传的头像，请检查SD卡是否挂载");
							return;
						}

						// 输出裁剪的临时文件
						String timeStamp = new SimpleDateFormat(
								"yyyyMMddHHmmss").format(new Date());
						// 照片命名
						String origFileName = "osc_" + timeStamp + ".jpg";
						String cropFileName = "osc_crop_" + timeStamp + ".jpg";

						// 裁剪头像的绝对路径
						protraitPath = FILE_SAVEPATH + cropFileName;
						protraitFile = new File(protraitPath);

						origUri = Uri.fromFile(new File(FILE_SAVEPATH,
								origFileName));
						cropUri = Uri.fromFile(protraitFile);

						// 相册选图
						if (item == 0) {
							startActionPickCrop(cropUri);
						}
						// 手机拍照
						else if (item == 1) {
							startActionCamera(origUri);
						}
					}
				}).create();

		imageDialog.show();
	}

	/**
	 * 选择图片裁剪
	 * 
	 * @param output
	 */
	private void startActionPickCrop(Uri output) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		startActivityForResult(Intent.createChooser(intent, "选择图片"),
				ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}

	/**
	 * 相机拍照
	 * 
	 * @param output
	 */
	private void startActionCamera(Uri output) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	/**
	 * 拍照后裁剪
	 * 
	 * @param data
	 *            原始图片
	 * @param output
	 *            裁剪后图片
	 */
	private void startActionCrop(Uri data, Uri output) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}

	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto() {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loading != null)
					loading.dismiss();
				if (msg.what == 1 && msg.obj != null) {
					Result res = (Result) msg.obj;
					// 提示信息
					UIHelper.ToastMessage(Mine.this, res.getMessage());
					if (res.OK()) {
						// 显示新头像
						imgUserFace.setImageBitmap(protraitBitmap);
					}
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException) msg.obj).makeToast(Mine.this);
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("正在上传头像···");
			loading.show();
		}

		new Thread() {
			public void run() {
				// 获取头像缩略图
				if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
					protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath,
							200, 200);
				}

				if (protraitBitmap != null) {
					Message msg = new Message();
					try {
						Result res = ((AppContext) getApplication())
								.updatePortrait(protraitFile);
						if (res != null && res.OK()) {
							if (res.isIsError() != false)
								res.setMessage("操作异常");
							else
								res.setMessage("操作成功");
							// 保存新头像到缓存
							String filename = FileUtils.getFileName(user
									.getHeadpic());
							String result = ((AppContext) getApplication())
									.updateUserPic(ac
											.getLoginUid(), res.getUrl());
							ImageUtils.saveImage(Mine.this, filename,
									protraitBitmap);
						}
						msg.what = 1;
						msg.obj = res;
					} catch (AppException e) {
						e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
					} catch (IOException e) {
						e.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			startActionCrop(origUri, cropUri);// 拍照后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			uploadNewPhoto();// 上传新照片
			break;
		}
	}
	
}
