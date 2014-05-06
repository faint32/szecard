package com.engc.szeduecard.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户应用缓存实体
 * 
 * @ClassName: UserAppList
 * @Description: TODO
 * @author wutao
 * @date 2013-12-12 下午3:36:59
 * 
 */
public class UserAppList extends Entity {

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
	private Map<String, List<String>> userAppCache = new HashMap<String, List<String>>();
	private Map<String, List<Integer>> userAppIconCache = new HashMap<String, List<Integer>>();

	public Map<String, List<Integer>> getUserAppIconCache() {
		return userAppIconCache;
	}

	public void setUserAppIconCache(Map<String, List<Integer>> userAppIconCache) {
		this.userAppIconCache = userAppIconCache;
	}

	public int getPageSize() {
		return pageSize;
	}

	public Map<String, List<String>> getUserAppCache() {
		return userAppCache;
	}

	public void setUserAppCache(Map<String, List<String>> userAppCache) {
		this.userAppCache = userAppCache;
	}

}
