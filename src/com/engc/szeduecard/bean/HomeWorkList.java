package com.engc.szeduecard.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: HomeWorkList 
 * @Description: TODO
 * @author wutao
 * @date 2013-12-13 下午2:52:38 
 *
 */
public class HomeWorkList extends Entity {
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
	private int homeWorkCount;
	private List<HomeWork> homeWorkslist = new ArrayList<HomeWork>();

	public int getPageSize() {
		return pageSize;
	}

	public int getHomeWorkCount() {
		return homeWorkCount;
	}

	public void setHomeWorkCount(int homeWorkCount) {
		this.homeWorkCount = homeWorkCount;
	}

	public List<HomeWork> getHomeWorkslist() {
		return homeWorkslist;
	}

	public void setHomeWorkslist(List<HomeWork> homeWorkslist) {
		this.homeWorkslist = homeWorkslist;
	}

	
	

}
