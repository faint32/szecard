package com.engc.szeduecard.bean;

import org.w3c.dom.Comment;


/**
 * 数据操作结果实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */

public class Result extends Entity {

	private int errorCode;
	private String errorMessage;
	
	
	private Comment comment;
	private boolean IsError;
	private String uid;
	private String Message;
	

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		this.Message = message;
	}

	public boolean isIsError() {
		return IsError;
	}

	public void setIsError(boolean isError) {
		this.IsError = isError;
	}
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String url;
	

	public boolean OK() {
		return IsError == false;
	}

	

}
