package com.engc.szeduecard.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息列表实体类
 * 
 * @author wutao
 * @version 1.0
 * @created
 */
public class HolidayRecordList extends Entity {
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
	private List<Holidays> holidayslist = new ArrayList<Holidays>();

	public int getPageSize() {
		return pageSize;
	}

	public int getHolidaysCount() {
		return holidaysCount;
	}

	public void setHolidaysCount(int holidaysCount) {
		this.holidaysCount = holidaysCount;
	}

	public List<Holidays> getHolidayslist() {
		return holidayslist;
	}

	public void setHolidayslist(List<Holidays> holidayslist) {
		this.holidayslist = holidayslist;
	}

	

	
}
