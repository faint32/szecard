package com.engc.szeduecard.bean;

/**
 * 家庭作业实体
 * @ClassName: HomeWork 
 * @Description: TODO
 * @author wutao
 * @date 2013-12-13 下午2:47:47 
 *
 */
public class HomeWork extends Entity {
	
	private String classn;
	private int delestatus;
	private String descp;
	private String graden;
	private String hid;
	private String issuedate;
	private String issuer;
	private String name;
	private String tname;
	private String url;
	private String subjectName;
	private String subject;
	private String type;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClassn() {
		return classn;
	}
	public void setClassn(String classn) {
		this.classn = classn;
	}
	public int getDelestatus() {
		return delestatus;
	}
	public void setDelestatus(int delestatus) {
		this.delestatus = delestatus;
	}
	public String getDescp() {
		return descp;
	}
	public void setDescp(String descp) {
		this.descp = descp;
	}
	public String getGraden() {
		return graden;
	}
	public void setGraden(String graden) {
		this.graden = graden;
	}

	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public String getIssuedate() {
		return issuedate;
	}
	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	
	
	

	
}
