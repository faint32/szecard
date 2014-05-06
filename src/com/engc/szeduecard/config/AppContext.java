package com.engc.szeduecard.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.webkit.CacheManager;

import com.engc.szeduecard.R;
import com.engc.szeduecard.api.ApiClient;
import com.engc.szeduecard.bean.HolidayRecordList;
import com.engc.szeduecard.bean.Holidays;
import com.engc.szeduecard.bean.HomeWorkList;
import com.engc.szeduecard.bean.Result;
import com.engc.szeduecard.bean.UserAppList;
import com.engc.szeduecard.bean.UserInfo;
import com.engc.szeduecard.common.ImageUtils;
import com.engc.szeduecard.common.MethodsCompat;
import com.engc.szeduecard.common.StringUtils;
import com.engc.szeduecard.common.UIHelper;

/**
 * 
 * @ClassName: AppContext
 * @Description: 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author wutao
 * @date 2013-10-9 下午1:20:29
 * 
 */
public class AppContext extends Application {

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 20;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	private boolean login = false; // 登录状态
	private String loginUid = ""; // 登录用户的id

	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	// 全局图片文件url 头部
	// public static final String PIC_URL_HEADER =
	// "http://172.16.17.27:8080/vcp/file/loadfile.do?fileurl=http://172.16.17.27:8090";

	// private static LocationMapClient mInstance = null;
	// public boolean m_bKeyRight = true;
	// public BMapManager mBMapManager = null;
	// public AppContext appcontext=null;

	// public static final String strKey =
	// "E9C9F8FC70B1125A11A8B5F843CE3DA9CBEFE64B"; //百度地图key

	private Handler unLoginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIHelper.ToastMessage(AppContext.this,
						getString(R.string.msg_login_error));
				UIHelper.showLoginDialog(AppContext.this);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());
		// appcontext=this;
		// try {
		// initEngineManager(this.getApplicationContext());
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * 
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}

	/**
	 * 获取登录用户id
	 * 
	 * @return
	 */
	public String getLoginUid() {
		return this.loginUid;
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// 清除webview缓存
		File file = CacheManager.getCacheFileBaseDir();
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				item.delete();
			}
			file.delete();
		}
		deleteDatabase("webview.db");
		deleteDatabase("webview.db-shm");
		deleteDatabase("webview.db-wal");
		deleteDatabase("webviewCache.db");
		deleteDatabase("webviewCache.db-shm");
		deleteDatabase("webviewCache.db-wal");
		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
					System.currentTimeMillis());
		}
		// 清除编辑器保存的临时内容
		Properties props = getProperties();
		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 将对象保存到内存缓存中
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * 从内存缓存中获取对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * 保存磁盘缓存
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 是否发出提示音
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// 默认是开启提示声音
		if (StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}

	/**
	 * 设置是否发出提示音
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 应用程序是否发出提示音
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * 保存登录信息
	 * 
	 * @param user
	 */
	public void saveLoginInfo(final UserInfo user) {
		this.loginUid = user.getUsercode();
		this.login = true;
		setProperties(new Properties() {
			{
				String accountName = user.getUsername();
				setProperty("user.usercode",
						String.valueOf(user.getUsercode() != null ? user
								.getUsercode() : ""));
				setProperty("user.name",
						user.getUsername() != null ? user.getUsername() : "");
				setProperty("user.cardstatusName", String.valueOf(user
						.getEntityname() != null ? user.getEntityname() : "0"));
				setProperty("user.cardstatusCode", String.valueOf(user
						.getCardstatus() != 0 ? user.getCardstatus() : 0));
				setProperty("user.accountBalance", String.valueOf(user
						.getCurrdbmoney() != null ? user.getCurrdbmoney() : 0));
				setProperty("user.orgid",
						user.getOrgid() != null ? user.getOrgid() : "");
				setProperty("user.face",
						String.valueOf(user.getHeadpic() != null ? user
								.getHeadpic() : ""));
				setProperty("user.parentphone",
						user.getParentphone() != null ? user.getParentphone()
								: "");
				setProperty("user.sonname",
						user.getSonname() != null ? user.getSonname() : "");
				setProperty("user.soncode",
						user.getSoncode() != null ? user.getSoncode() : "");
				setProperty("user.usertype",
						String.valueOf(user.getUsertype() != 0 ? user
								.getUsertype() : 0));
			}
		});
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.loginUid = "";
		this.login = false;
		removeProperty("user.usercode", "user.name", "user.cardstatusName",
				"user.cardstatusCode", "user.accountBalance", "user.face",
				"user.orgid", "user.parentphone", "user.sonname",
				"user.soncode", "user.usertype");
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public UserInfo getLoginInfo() {
		UserInfo lu = new UserInfo();

		lu.setUsercode(getProperty("user.usercode"));
		lu.setUsername(getProperty("user.name"));
		lu.setEntityname(getProperty("user.cardstatusName"));
		lu.setCardstatus(Integer.parseInt(getProperty("user.cardstatusCode")));
		lu.setCurrdbmoney(getProperty("user.accountBalance"));
		lu.setHeadpic(getProperty("user.face"));
		lu.setOrgid(getProperty("user.orgid"));
		lu.setParentphone(getProperty("user.parentphone"));
		lu.setSoncode(getProperty("user.soncode"));
		lu.setSonname(getProperty("user.sonname"));
		lu.setUsertype(Integer.parseInt(getProperty("user.usertype")));
		return lu;
	}

	/**
	 * 保存用户头像
	 * 
	 * @param fileName
	 * @param bitmap
	 */
	public void saveUserFace(String fileName, Bitmap bitmap) {
		try {
			ImageUtils.saveImage(this, fileName, bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户头像 t
	 * 
	 * @param key
	 * @return
	 * @throws AppException
	 */
	public Bitmap getUserFace(String key) throws AppException {
		FileInputStream fis = null;
		try {
			fis = openFileInput(key);
			return BitmapFactory.decodeStream(fis);
		} catch (Exception e) {
			throw AppException.run(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 发布动态
	 * 
	 * @param valueOf
	 * @param string
	 * @param key
	 * @param text
	 * @return
	 */
	public String RealseTweet(String valueOf, String string, String key,
			Editable text) {

		return null;
	}

	/**
	 * 登录
	 * 
	 * @param userName
	 * @param passWord
	 * @return
	 * @throws AppException
	 */
	public UserInfo Login(String userName, String passWord) throws AppException {
		return ApiClient.Login(this, userName, passWord);
	}

	/**
	 * 申请假期
	 * 
	 * @param userCode
	 * @param startDate
	 * @param endDate
	 * @param days
	 * @param remark
	 * @param holidayType
	 * @return
	 * @throws AppException
	 */
	public UserInfo ApplyHoliday(String userCode, String telNo,
			String startDate, String endDate, double days, String remark,
			int holidayType) throws AppException {
		return ApiClient.ApplyHolidays(this, userCode, getLoginInfo()
				.getOrgid(), telNo, startDate, endDate, days, remark,
				holidayType, getLoginInfo().getUsertype());
	}

	/**
	 * 审核假期
	 * 
	 * @param userCode
	 * @param recordId
	 * @param auditStatus
	 * @param auditContent
	 * @return
	 * @throws AppException
	 */
	public UserInfo AuditHolidays(String recordId, String auditStatus,
			String auditContent) throws AppException {

		return ApiClient.AuditHolidays(this, auditStatus, auditContent,
				getLoginInfo().getUsercode(), recordId);

	}

	/**
	 * 更具记录ID 查询未审核的假期
	 * 
	 * @param id
	 * @return
	 * @throws AppException
	 */
	public Holidays GetHolidaysById(String id) throws AppException {
		return ApiClient.GetHolidayById(this, id);
	}

	/**
	 * 更改 卡状态
	 * 
	 * @param userCode
	 * @param basicStatus
	 * @param cardStatus
	 * @return
	 * @throws AppException
	 */
	public UserInfo ChangeCardStatus(String userCode, int basicStatus,
			int cardStatus) throws AppException {
		return ApiClient.ChangeCardStatus(this, userCode, basicStatus,
				cardStatus);
	}

	/**
	 * 获取 请假审批记录
	 * 
	 * @param pageIndex
	 * @param isRefresh
	 * @param userCode
	 * @param status
	 * @return
	 * @throws AppException
	 */
	public HolidayRecordList getHolidaysRecortdList(int pageIndex,
			boolean isRefresh, String userCode, String status)
			throws AppException {
		HolidayRecordList list = null;

		// if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
		try {

			list = ApiClient.getHolidayRecordList(this, pageIndex, 0, userCode,
					status);

			/*
			 * if (list != null && pageIndex == 0) { Notice notice =
			 * list.getNotice(); list.setNotice(null); list.setCacheKey(key);
			 * saveObject(list, key); list.setNotice(notice); }
			 */
		} catch (AppException e) {
			if (list == null)
				throw e;

		}
		return list;
	}
	
	/**
	 * 更改用户头像
	 * 
	 * @param userId
	 * @param url
	 * @return
	 * @throws AppException
	 */
	public String updateUserPic(String userId, String url) throws AppException {
		return ApiClient.updateUserPic(userId, url);
	}

	/**
	 * 获得家庭作业
	 * 
	 * @param pageIndex
	 * @param isRefresh
	 * @param userCode
	 * @param status
	 * @return
	 * @throws AppException
	 */
	public HomeWorkList getHomeWorkList(int pageIndex, boolean isRefresh,
			String userCode, String status) throws AppException {
		HomeWorkList list = null;

		// if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
		try {

			list = ApiClient.getHomeWorkList(this, pageIndex, 0, userCode, status);

			/*
			 * if (list != null && pageIndex == 0) { Notice notice =
			 * list.getNotice(); list.setNotice(null); list.setCacheKey(key);
			 * saveObject(list, key); list.setNotice(notice); }
			 */
		} catch (AppException e) {
			if (list == null)
				throw e;

		}
		return list;
	}

	/**
	 * 从缓存中获取当前用户添加应用的list
	 * 
	 * @param contains
	 * @return
	 */
	public List<String> getUserAppCacheList(String contains) {
		String key = "userAppsList" + contains;
		UserAppList list = (UserAppList) readObject(key);
		if (list != null)
			return list.getUserAppCache().get(contains);
		else
			return null;

	}

	/**
	 * 将用户app信息加入到本机缓存中
	 * 
	 * @param appList
	 * @param keyWord
	 * @return
	 */
	public boolean addUserAppListToCache(List<String> appList, String keyWord) {
		String key = "userAppsList" + keyWord;
		UserAppList userAppCache;
		List<String> uaList;
		boolean result;
		try {

			userAppCache = (UserAppList) readObject(key);

			if (userAppCache != null) {
				uaList = userAppCache.getUserAppCache().get(keyWord);
				uaList.clear();
				userAppCache.getUserAppCache().put(keyWord, appList);
				if (saveObject(userAppCache, key))
					return false;

			} else {
				userAppCache = new UserAppList();
				userAppCache.getUserAppCache().put(keyWord, appList);
				if (saveObject(userAppCache, key))
					return false;

			}
		} catch (Exception e) {
			e.getMessage();
		}
		return true;

	}

	/**
	 * 从缓存中获取当前用户添加应用图标的的list
	 * 
	 * @param contains
	 * @return
	 */
	public List<Integer> getUserAppIconCacheList(String contains) {
		String key = "userAppsIconList" + contains;
		UserAppList list = (UserAppList) readObject(key);
		if (list != null)
			return list.getUserAppIconCache().get(contains);
		else
			return null;

	}

	/**
	 * 将用户app信息加入到本机缓存中
	 * 
	 * @param appList
	 * @param keyWord
	 * @return
	 */
	public boolean addUserAppIconListToCache(List<Integer> appIconList,
			String keyWord) {
		String key = "userAppsIconList" + keyWord;
		UserAppList userAppCache;
		List<Integer> iconList;
		boolean result;
		try {

			userAppCache = (UserAppList) readObject(key);

			if (userAppCache != null) {
				iconList = userAppCache.getUserAppIconCache().get(keyWord);
				iconList.clear();
				userAppCache.getUserAppIconCache().put(keyWord, appIconList);
				if (saveObject(userAppCache, key))
					return false;

			} else {

				userAppCache = new UserAppList();
				userAppCache.getUserAppIconCache().put(keyWord, appIconList);
				if (saveObject(userAppCache, key))
					return false;

			}
		} catch (Exception e) {
			e.getMessage();
		}
		return true;

	}

	/**
	 * 更新用户头像
	 * 
	 * @param portrait
	 *            新上传的头像
	 * @return
	 * @throws AppException
	 */
	public Result updatePortrait(File portrait) throws AppException {

		return ApiClient.updatePortrait(this, getLoginUid(), portrait);
	}
}
