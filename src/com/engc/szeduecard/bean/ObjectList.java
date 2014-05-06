package com.engc.szeduecard.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有List 实体 基类
 * 
 * @ClassName: ObjectList
 * @Description: TODO
 * @author wutao
 * @date 2013-10-31 上午9:34:32
 * 
 */
public class ObjectList {

	public final static int CATALOG_ALL = 1;
	public final static int CATALOG_INTEGRATION = 2;
	public final static int CATALOG_SOFTWARE = 3;

	private int catalog;

	public int getCatalog() {
		return catalog;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}

	private int pageSize;
	private int holidaysCount;
	private List<Object> objectList = new ArrayList<Object>();

	public List<Object> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<Object> objectList) {
		this.objectList = objectList;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getHolidaysCount() {
		return holidaysCount;
	}

	public void setHolidaysCount(int holidaysCount) {
		this.holidaysCount = holidaysCount;
	}

}
