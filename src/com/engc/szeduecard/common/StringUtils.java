package com.engc.szeduecard.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private final static Pattern onlyDitital=Pattern.compile("^[0-9]+(.[0-9]{0,1})?$");
     // private final static Pattern onlyDitital=Pattern.compile("\\[^//d//.]/g,''");
	// private final static SimpleDateFormat dateFormater = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// private final static SimpleDateFormat dateFormater2 = new
	// SimpleDateFormat("yyyy-MM-dd");

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {

			return dateFormater2.get().parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将字符串转为日期+事件类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDateWithHourMin(String sdate) {
		try {

			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater.get().format(cal.getTime());
		String paramDate = dateFormater.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = dateFormater2.get().format(time);
		}
		return ftime;
	}

	/**
	 * 判断 日期是否在15天以内
	 * 
	 * @param sdate
	 * @return
	 */
	public static boolean isWithInFifteen(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return false;
		}
		Calendar cal = Calendar.getInstance();
		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days < 15) {
			return true;
		} else {
			return false;
		}

	}

	public static int ConvertDateToStringForTimeResult(String startDate,
			String endDate) {
		Date startTime = toDateWithHourMin(startDate);
		Date endTime = toDateWithHourMin(endDate);
		if (startTime == null || endTime == null) {
			return 0;
		}
		// 判断是否是同一天
		String sDate = dateFormater.get().format(startTime);
		String eDate = dateFormater.get().format(endTime);
		if (sDate.equals(eDate)) {
			return 1;
		}

		long lt = startTime.getTime() / 86400000;
		long ct = endTime.getTime() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {

			int hour = (int) ((endTime.getTime() - startTime.getTime()) / 3600000);
			/*
			 * if (hour == 0) ftime = Math.max( (cal.getTimeInMillis() -
			 * time.getTime()) / 60000, 1) + "分钟前"; else ftime = hour + "小时前";
			 */
			return 0;
		} else {
			return days;
		}
		// return ftime;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.get().format(today);
			String timeDate = dateFormater2.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}
	
	

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}
	
	/**
	 * 判断只能输入数字
	 * @param text
	 * @return
	 */
	public static boolean isOnlyDigital(String text){
		if(text==null||text.trim().length()==0)
			return false;
		return onlyDitital.matcher(text).matches();
		
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 根据key得到列表
	 * 
	 * @param json
	 * @param key
	 * @return
	 */

	public static String getJsonByKey(String json, String key) {
		int index = json.indexOf(key + ":");
		int index1 = json.indexOf("]", index);
		String result = "";
		if (index != -1) {
			result = json.substring(key.length() + index + 1, index1 + 1);
		}

		return result;
	}

	/**
	 * 根据开始，结束，周期解析拼接服务时间串
	 * 
	 * @param startTime
	 * @param endTime
	 * @param week
	 * @return
	 */
	public static String getServiceTimeByTimeWithWeek(String startTime,
			String endTime, String week) {
		String serviceTime = "";
		if (startTime != null && endTime != null && week != null) {
			String serviceWeek = "";
			String[] weekDay = week.split(",");
			for (int i = 0; i < weekDay.length; i++) {
				switch (Integer.parseInt(weekDay[i])) {
				case 1:
					serviceWeek += "周一" + ",";
					break;

				case 2:
					serviceWeek += "周二" + ",";
					break;

				case 3:

					serviceWeek += "周三" + ",";
					break;

				case 4:
					serviceWeek += "周四" + ",";
					break;

				case 5:
					serviceWeek += "周五" + ",";
					break;

				case 6:
					serviceWeek += "周六" + ",";
					break;
				default:
					serviceWeek += "周日" + ",";
					break;
				}
			}

			String timeStr = startTime + "--" + endTime;
			serviceTime = serviceWeek
					.substring(0, serviceWeek.lastIndexOf(","))
					+ "\t"
					+ timeStr;

		}
		return serviceTime;
	}

}
