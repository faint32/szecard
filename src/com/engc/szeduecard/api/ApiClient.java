package com.engc.szeduecard.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.engc.szeduecard.bean.HolidayRecordList;
import com.engc.szeduecard.bean.Holidays;
import com.engc.szeduecard.bean.HomeWork;
import com.engc.szeduecard.bean.HomeWorkList;
import com.engc.szeduecard.bean.Result;
import com.engc.szeduecard.bean.URLs;
import com.engc.szeduecard.bean.Update;
import com.engc.szeduecard.bean.UserInfo;
import com.engc.szeduecard.config.AppContext;
import com.engc.szeduecard.config.AppException;

/**
 * API客户端接口：用于访问网络数据
 * 
 * @ClassName: ApiClient
 * @Description: TODO
 * @author wutao
 * @date 2013-10-9 下午1:25:53
 * 
 */

public class ApiClient {
	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}

	private static String getCookie(AppContext appContext) {
		if (appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}

	private static String getUserAgent(AppContext appContext) {
		if (appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/' + appContext.getPackageInfo().versionName + '_'
					+ appContext.getPackageInfo().versionCode);// App版本
			ua.append("/Android");// 手机系统平台
			ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
			ua.append("/" + android.os.Build.MODEL); // 手机型号
			ua.append("/" + appContext.getAppId());// 客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

	private static GetMethod getHttpGet(String url, String cookie,
			String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	private static PostMethod getHttpPost(String url, String cookie,
			String userAgent) {
		PostMethod httpPost = new PostMethod(url);

		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		httpPost.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");

		return httpPost;
	}

	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		// System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
				bitmap = BitmapFactory.decodeStream(inStream);
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return bitmap;
	}

	/**
	 * 调用服务端推送方法
	 * 
	 * @param msgTitle
	 * @param msgContent
	 * @return
	 */
	public static String pushTest(String msgTitle, String msgContent) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msgTitle", msgTitle);
		map.put("msgContent", msgContent);
		HZHttpClient client = new HZHttpClient();
		return client.postMethod(URLs.PushTest, map);
	}

	/**
	 * 登录
	 * 
	 * @param context
	 * @param userName
	 * @param passWord
	 * @return
	 * @throws AppException
	 */
	public static UserInfo Login(AppContext context, final String userName,
			final String passWord) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		params.put("passWord", passWord);
		HZHttpClient client = new HZHttpClient();
		UserInfo user = null;
		try {
			String result = client.postMethod(URLs.Login, params);
			user = JSON.parseObject(result, UserInfo.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	/**
	 * 请假
	 * 
	 * @param context
	 * @param userCode
	 * @param orgId
	 * @param telNo
	 * @param startDate
	 * @param endDate
	 * @param days
	 * @param remark
	 * @param holidayType
	 * @return
	 * @throws AppException
	 */
	public static UserInfo ApplyHolidays(AppContext context,
			final String userCode, final String orgId, final String telNo,
			final String startDate, final String endDate, final double days,
			final String remark, final int holidayType, final int userType)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("usercode", userCode);
		params.put("orgid", orgId);
		params.put("leavetype", String.valueOf(holidayType));
		params.put("telno", telNo);
		params.put("begindate", startDate);
		params.put("enddate", endDate);
		params.put("dayno", String.valueOf(days));
		params.put("leavereason", remark);
		params.put("usertype", String.valueOf(userType));
		HZHttpClient client = new HZHttpClient();
		UserInfo user = null;
		try {
			String result = client.postMethod(URLs.APPLY_HOLIDAYS, params);
			user = JSON.parseObject(result, UserInfo.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 审核假期
	 * 
	 * @param context
	 * @param auditStatus
	 * @param auditContent
	 * @param userCode
	 * @param recordId
	 * @return
	 * @throws AppException
	 */
	public static UserInfo AuditHolidays(AppContext context,
			final String auditStatus, final String auditContent,
			final String userCode, final String recordId) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("usercode", userCode);
		params.put("id", recordId);
		params.put("status", auditStatus);
		params.put("idea", auditContent);
		HZHttpClient client = new HZHttpClient();
		UserInfo user = null;
		try {
			String result = client.postMethod(URLs.AUDIT_HOLIDAYS, params);
			user = JSON.parseObject(result, UserInfo.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 更具ID 查询 未审核的假期记录
	 * 
	 * @param context
	 * @param id
	 * @return
	 * @throws AppException
	 */
	public static Holidays GetHolidayById(AppContext context, final String id)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		HZHttpClient client = new HZHttpClient();
		Holidays holiday = null;
		try {
			String result = client.postMethod(
					URLs.GET_APPLY_HOLIDAYS_INFO_BY_RECORDID, params);
			holiday = JSON.parseObject(result, Holidays.class);
			return holiday;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 更改当前 卡状态
	 * 
	 * @param context
	 * @param userCode
	 * @param basicStatus
	 * @param cardstatusone
	 * @return
	 * @throws AppException
	 */
	public static UserInfo ChangeCardStatus(final AppContext context,
			final String userCode, final int basicStatus,
			final int cardstatusone) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("usercode", userCode);
		params.put("status", String.valueOf(basicStatus));
		params.put("cardstatusone", String.valueOf(cardstatusone));
		HZHttpClient client = new HZHttpClient();
		UserInfo user = null;
		try {
			String result = client.postMethod(URLs.CHANGE_CARD_STATUS, params);
			user = JSON.parseObject(result, UserInfo.class);
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获得 所有 请假 审核 记录
	 * 
	 * @param appContext
	 * @param pageIndex
	 * @param pageSize
	 * @param userCode
	 * @param status
	 * @return
	 * @throws AppException
	 */
	public static HolidayRecordList getHolidayRecordList(AppContext appContext,
			final int pageIndex, final int pageSize, final String userCode,
			final String status) throws AppException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pageIndex", "1");
		if (userCode != "0")
			map.put("usercode", userCode);
		if (status != null)
			map.put("status", status);
		HZHttpClient client = new HZHttpClient();
		HolidayRecordList recordList = new HolidayRecordList();
		try {

			String result = client.postMethod(
					URLs.GET_HOLIDAYS_RECORF_FOR_STUDENT, map);
			List<Holidays> list = JSON.parseArray(result, Holidays.class);
			recordList.getHolidayslist().addAll(list);
			return recordList;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获得家庭作业
	 * 
	 * @param appContext
	 * @param pageIndex
	 * @param pageSize
	 * @param userCode
	 * @param status
	 * @return
	 * @throws AppException
	 */
	public static HomeWorkList getHomeWorkList(AppContext appContext,
			final int pageIndex, final int pageSize, final String userCode,
			final String status) throws AppException {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put("pageIndex", "1");
		if (userCode != "0")
			map.put("id", userCode);

		HZHttpClient client = new HZHttpClient();
		HomeWorkList hmList = new HomeWorkList();
		String result = "";
		try {

			if (!status.equals("1"))
				result = client.postMethod(URLs.GET_BEFORE_HOMEWORK, map);
			else
				result = client.postMethod(URLs.GET_TODAY_HOMEWORK, map);
			List<HomeWork> list = JSON.parseArray(result, HomeWork.class);
			hmList.getHomeWorkslist().addAll(list);
			return hmList;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 检查版本更新
	 * 
	 * @param url
	 * @return
	 */
	public static Update checkVersion(AppContext appContext)
			throws AppException {
		try {
			HZHttpClient client = new HZHttpClient();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("appName", "SP");
			String result = client.postMethod(URLs.GET_APP_VERSION, map);
			Update update = JSONObject.parseObject(result, Update.class);
			return update;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 更新用户头像
	 * 
	 * @param appContext
	 * @param uid
	 *            当前用户uid
	 * @param portrait
	 *            新上传的头像
	 * @return
	 * @throws AppException
	 */
	public static Result updatePortrait(AppContext appContext, String uid,
			File portrait) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		Map<String, File> files = new HashMap<String, File>();
		files.put("portrait", portrait);
		try {
			HZHttpClient client = new HZHttpClient();
			return http_post(appContext, URLs.BASEHOST, params, files);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	
	/**
	 * 更改用户头像
	 * 
	 * @param userId
	 * @param url
	 * @return
	 * @throws AppException
	 */
	public static String updateUserPic(String userId, String url)
			throws AppException {

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("userId", userId);
		map.put("url", url);

		HZHttpClient client = new HZHttpClient();
		try {
			return client.postMethod(URLs.APPLY_HOLIDAYS, map);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 上传图片 post
	 * 
	 * @param appContext
	 * @param url
	 * @param params
	 * @param files
	 * @return
	 * @throws AppException
	 */
	private static Result _postForUploadPic(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {
		// System.out.println("post_url==> "+url);
		Result result = null;
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;

		// post表单参数处理
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
				// System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// System.out.println("post_key_file==> "+file);
			}

		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);
				httpPost.setRequestEntity(new MultipartRequestEntity(parts,
						httpPost.getParams()));
				int statusCode = httpClient.executeMethod(httpPost);
				// int statusCode=httpClient.
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				} else if (statusCode == HttpStatus.SC_OK) {
					Cookie[] cookies = httpClient.getState().getCookies();
					String tmpcookies = "";
					for (Cookie ck : cookies) {
						tmpcookies += ck.toString() + ";";
					}
					// 保存cookie
					if (appContext != null && tmpcookies != "") {
						appContext.setProperty("cookie", tmpcookies);
						appCookie = tmpcookies;
					}
				}
				responseBody = httpPost.getResponseBodyAsString();
				// System.out.println("XMLDATA=====>"+responseBody);
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		responseBody = responseBody.replaceAll("\\p{Cntrl}", "");

		try {
			result = JSONObject.parseObject(responseBody, Result.class);
			return result;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
	
	/**
	 * post请求URL
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 * @throws IOException
	 * @throws
	 */
	private static Result http_post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException, IOException {
		// return Result.parse(_post(appContext, url, params, files));
		return _postForUploadPic(appContext, url, params, files);
	}




}
