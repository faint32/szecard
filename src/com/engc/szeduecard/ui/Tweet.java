package com.engc.szeduecard.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.engc.szeduecard.R;
import com.engc.szeduecard.adapter.GridViewFaceAdapter;
import com.engc.szeduecard.api.ApiClient;
import com.engc.szeduecard.common.FileUtils;
import com.engc.szeduecard.common.ImageUtils;
import com.engc.szeduecard.common.MediaUtils;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;
import com.engc.szeduecard.config.AppConfig;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.widget.MenuItemView;
import com.engc.szeduecard.widget.MyAnimations;
import com.engc.szeduecard.widget.OnItemClickListener;

/**
 * 
 * @ClassName: Tweet
 * @Description: 发表动态activity
 * 
 * @author wutao
 * @date 2013-10-11 下午2:58:57
 * 
 */

public class Tweet extends BaseActivity implements OnItemClickListener {
	private ImageView imgBack;
	private ImageView imgSendTweet;

	private InputMethodManager imm;
	private EditText edtTweetContent;
	private FrameLayout mForm;
	public static LinearLayout mMessage;
	private GridViewFaceAdapter mGVFaceAdapter;
	private GridView mGridView;
	private MenuItemView myViewLB;
	private ImageView imgPlusLB;
	private ImageView mImage;
	private String theThumbnail;
	private LinearLayout tweetFaceLayout;
	private String theLarge;
	private String tempTweetKey = AppConfig.TEMP_TWEET;
	private String tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE;
	private File imgFile;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweetpub);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		initView(); // 初始化视图
		initGridFaceView(); // 初始化表情视图

	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		imgBack = (ImageView) findViewById(R.id.btn_tweet_pub_back);
		imgBack.setOnClickListener(UIHelper.finish(this));
		imgSendTweet = (ImageView) findViewById(R.id.btn_tweet_pub_send);
		imgSendTweet.setOnClickListener(realeseOnClick);
		edtTweetContent = (EditText) findViewById(R.id.tweet_pub_content);
		mForm = (FrameLayout) findViewById(R.id.tweet_pub_form);
		mMessage = (LinearLayout) findViewById(R.id.tweet_pub_message);
		tweetFaceLayout = (LinearLayout) findViewById(R.id.tweet_face_layout);
		mImage = (ImageView) findViewById(R.id.tweet_pub_image);
		mImage.setOnLongClickListener(imageLongClickListener);

		myViewLB = (MenuItemView) findViewById(R.id.myViewLB);
		myViewLB.setPosition(MenuItemView.POSITION_LEFT_BOTTOM);
		myViewLB.setRadius(70);
		imgPlusLB = (ImageView) findViewById(R.id.imgPlusLB);
		findViewById(R.id.relLayLB).setOnClickListener(addOtherView);
		setMenuItemView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mGridView.getVisibility() == View.VISIBLE) {
				// 隐藏表情
				hideFace();
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return true;
	}

	/**
	 * 显示表情
	 */
	private void showFace() {
		tweetFaceLayout.setVisibility(View.VISIBLE);
		mGridView.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏表情
	 */
	private void hideFace() {
		tweetFaceLayout.setVisibility(View.GONE);
		mGridView.setVisibility(View.GONE);
	}

	// 初始化表情控件
	private void initGridFaceView() {
		mGVFaceAdapter = new GridViewFaceAdapter(this);
		mGridView = (GridView) findViewById(R.id.tweet_pub_faces);
		mGridView.setAdapter(mGVFaceAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 插入的表情
				SpannableString ss = new SpannableString(view.getTag()
						.toString());
				Drawable d = getResources().getDrawable(
						(int) mGVFaceAdapter.getItemId(position));
				d.setBounds(0, 0, 35, 35);// 设置表情图片的显示大小
				ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
				ss.setSpan(span, 0, view.getTag().toString().length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// 在光标所在处插入表情
				edtTweetContent.getText().insert(
						edtTweetContent.getSelectionStart(), ss);
				hideFace();
			}
		});
	}

	// 初始化动态添加按钮
	private void setMenuItemView() {

		ImageButton imgBtnCamera = new ImageButton(this);
		imgBtnCamera.setBackgroundResource(R.drawable.composer_camera);
		ImageButton imgBtnFace = new ImageButton(this);
		imgBtnFace.setBackgroundResource(R.drawable.composer_place);
		ImageButton imgBtnRandom = new ImageButton(this);
		imgBtnRandom.setBackgroundResource(R.drawable.composer_sleep);

		myViewLB.addView(imgBtnCamera);
		myViewLB.addView(imgBtnFace);
		myViewLB.addView(imgBtnRandom);

	}

	// 动态添加视图
	private OnClickListener addOtherView = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MyAnimations.getRotateAnimation(imgPlusLB, 0f, 270f, 300);
			MyAnimations.startAnimations(Tweet.this, myViewLB, 300);
		}

	};

	// 点击弹出视图中每个item
	@Override
	public void onclick(int item) {
		switch (item) {
		case 0: // 拍照
			imm.hideSoftInputFromWindow(edtTweetContent.getWindowToken(), 0);
			CharSequence[] items = {
					Tweet.this.getString(R.string.img_from_album),
					Tweet.this.getString(R.string.img_from_camera) };
			imageChooseItem(items);
			break;
		case 1: // 表情
			imm.hideSoftInputFromWindow(edtTweetContent.getWindowToken(), 0);
			showFace();
			break;
		default:
			break;
		}
	}

	// 长按清除照片
	private View.OnLongClickListener imageLongClickListener = new View.OnLongClickListener() {
		public boolean onLongClick(View v) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			new AlertDialog.Builder(v.getContext())
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(getString(R.string.delete_image))
					.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// 清除之前保存的编辑图片
									((AppContext) getApplication())
											.removeProperty(tempTweetImageKey);

									// imgFile = null;
									mImage.setVisibility(View.GONE);
									dialog.dismiss();
								}
							})
					.setNegativeButton(R.string.cancle,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();
			return true;
		}

	};

	/**
	 * 操作选择
	 * 
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.ui_insert_image)
				.setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 手机选图
						if (item == 0) {
							Intent intent = new Intent(
									Intent.ACTION_GET_CONTENT);
							intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/*");
							startActivityForResult(
									Intent.createChooser(intent, "选择图片"),
									ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
						}
						// 拍照
						else if (item == 1) {
							String savePath = "";
							// 判断是否挂载了SD卡
							String storageState = Environment
									.getExternalStorageState();
							if (storageState.equals(Environment.MEDIA_MOUNTED)) {
								savePath = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath()
										+ "/szzyz/Camera/";// 存放照片的文件夹
								File savedir = new File(savePath);
								if (!savedir.exists()) {
									savedir.mkdirs();
								}
							}

							// 没有挂载SD卡，无法保存文件
							if (StringUtils.isEmpty(savePath)) {
								UIHelper.ToastMessage(Tweet.this,
										"无法保存照片，请检查SD卡是否挂载");
								return;
							}

							String timeStamp = new SimpleDateFormat(
									"yyyyMMddHHmmss").format(new Date());
							String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
							File out = new File(savePath, fileName);
							Uri uri = Uri.fromFile(out);

							theLarge = savePath + fileName;// 该照片的绝对路径

							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent,
									ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
						}
					}
				}).create();

		imageDialog.show();
	}

	// 拍照或从图库中选取图片
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					// 显示图片
					mImage.setImageBitmap((Bitmap) msg.obj);
					mImage.setVisibility(View.VISIBLE);
				}
			}
		};

		new Thread() {
			public void run() {
				Bitmap bitmap = null;

				if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) {
					if (data == null)
						return;

					Uri thisUri = data.getData();
					String thePath = ImageUtils
							.getAbsolutePathFromNoStandardUri(thisUri);

					// 如果是标准Uri
					if (StringUtils.isEmpty(thePath)) {
						theLarge = ImageUtils.getAbsoluteImagePath(Tweet.this,
								thisUri);
					} else {
						theLarge = thePath;
					}

					String attFormat = FileUtils.getFileFormat(theLarge);
					if (!"photo".equals(MediaUtils.getContentType(attFormat))) {
						Toast.makeText(Tweet.this,
								getString(R.string.choose_image),
								Toast.LENGTH_SHORT).show();
						return;
					}

					// 获取图片缩略图 只有Android2.1以上版本支持
					if (AppContext
							.isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)) {
						String imgName = FileUtils.getFileName(theLarge);
						bitmap = ImageUtils.loadImgThumbnail(Tweet.this,
								imgName,
								MediaStore.Images.Thumbnails.MICRO_KIND);
					}

					if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
						bitmap = ImageUtils
								.loadImgThumbnail(theLarge, 100, 100);
					}
				}
				// 拍摄图片
				else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
					if (bitmap == null && !StringUtils.isEmpty(theLarge)) {
						bitmap = ImageUtils
								.loadImgThumbnail(theLarge, 100, 100);
					}
				}

				if (bitmap != null) {
					// 存放照片的文件夹
					String savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/OSChina/Camera/";
					File savedir = new File(savePath);
					if (!savedir.exists()) {
						savedir.mkdirs();
					}

					String largeFileName = FileUtils.getFileName(theLarge);
					String largeFilePath = savePath + largeFileName;
					// 判断是否已存在缩略图
					if (largeFileName.startsWith("thumb_")
							&& new File(largeFilePath).exists()) {
						theThumbnail = largeFilePath;
						imgFile = new File(theThumbnail);
					} else {
						// 生成上传的800宽度图片
						String thumbFileName = "thumb_" + largeFileName;
						theThumbnail = savePath + thumbFileName;
						if (new File(theThumbnail).exists()) {
							imgFile = new File(theThumbnail);
						} else {
							try {
								// 压缩上传的图片
								ImageUtils.createImageThumbnail(Tweet.this,
										theLarge, theThumbnail, 800, 80);
								imgFile = new File(theThumbnail);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					// 保存动弹临时图片
					((AppContext) getApplication()).setProperty(
							tempTweetImageKey, theThumbnail);

					Message msg = new Message();
					msg.what = 1;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	// 发布动态
	private View.OnClickListener realeseOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			if (StringUtils.isEmpty(edtTweetContent.getText().toString())) {
				UIHelper.ToastMessage(v.getContext(), "请输入动态内容");
				return;
			}
			mMessage.setVisibility(View.VISIBLE);
			mForm.setVisibility(View.GONE);

			final AppContext ac = (AppContext) getApplication();
			final Handler hander = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						finish();
						UIHelper.ToastMessage(Tweet.this,
								"发布成功");
					} else {
						mMessage.setVisibility(View.GONE);
						mForm.setVisibility(View.VISIBLE);
					}

				}
			};

			new Thread() {
				public void run() {
					Message msg = new Message();
					int what = 0;
					String result = "";

					try{
						result = ApiClient.pushTest("智慧教育云提醒您", edtTweetContent.getText().toString());
						what = 1;
						msg.what = 1;
						msg.obj = result.replace("\"", "");
					}catch(Exception e){
						e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
					}
					
					hander.sendMessage(msg);
				}
			}.start();
		}
	};

	public static final int MAX = Integer.MAX_VALUE;
	public static final int MIN = (int) MAX / 2;

	/**
	 * 保持 sendNo 的唯一性是有必要的 It is very important to keep sendNo unique.
	 * 
	 * @return sendNo
	 */
	public static int getRandomSendNo() {
		return (int) (MIN + Math.random() * (MAX - MIN));
	}

}
