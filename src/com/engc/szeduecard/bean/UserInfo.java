package com.engc.szeduecard.bean;

/**
 * 用户信息实体
 * 
 * @ClassName: UserInfo
 * @Description:
 * @author wutao
 * @date 2013-10-16 下午8:48:45
 * 
 */
public class UserInfo extends Entity {
	private String usercode;
	private String username;
	private int usertype;
	private String password;
	private int ismgr;
	private String headpic;
	private String entityname;
	private String currdbmoney;
	private int cardstatus;
	private String orgid;
	public String getSonname() {
		return sonname;
	}

	public void setSonname(String sonname) {
		this.sonname = sonname;
	}

	public String getSoncode() {
		return soncode;
	}

	public void setSoncode(String soncode) {
		this.soncode = soncode;
	}

	private String sonname;
	private String soncode;
	

	public String getParentphone() {
		return parentphone;
	}

	public void setParentphone(String parentphone) {
		this.parentphone = parentphone;
	}

	private String parentphone;

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIsmgr() {
		return ismgr;
	}

	public void setIsmgr(int ismgr) {
		this.ismgr = ismgr;
	}

	public String getHeadpic() {
		return headpic;
	}

	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}

	public String getEntityname() {
		return entityname;
	}

	public void setEntityname(String entityname) {
		this.entityname = entityname;
	}

	public String getCurrdbmoney() {
		return currdbmoney;
	}

	public void setCurrdbmoney(String currdbmoney) {
		this.currdbmoney = currdbmoney;
	}

	public int getCardstatus() {
		return cardstatus;
	}

	public void setCardstatus(int cardstatus) {
		this.cardstatus = cardstatus;
	}

}
