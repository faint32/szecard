package com.engc.szeduecard.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TimeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * final ThreadLocal<SimpleDateFormat> dateFormater = new
		 * ThreadLocal<SimpleDateFormat>() {
		 * 
		 * @Override protected SimpleDateFormat initialValue() { return new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm"); } };
		 */
	       receiverStrAddList("1111");

	}

	public static ArrayList<String> receiverStrAddList(String receiverIDStr) {
		ArrayList<String> receiverList = new ArrayList<String>();
		if (receiverIDStr != null) {
			String[] receiverIDArray = receiverIDStr.split(",");
			if (receiverIDArray.length > 0) {
				for (int i = 0; i < receiverIDArray.length; i++) {
					receiverList.add(receiverIDArray[i]);
				}

			}
		}
		return receiverList;
	}

}
