package com.d5.john.car.custom.bean;

public class PendingUser {
	private String telephone;// 手机号
	private String id;//
	private String password;// 密码
	private String userName;// 用户名
	private String headUrl;// 头像保存路径
	private String signature;// 个性签名
	private String sex;// 性别: M男士，W女士
	private String titleRF;// 待接入，位置右边的最上边的标签//"WAIT_SWITCH_IN"
	private String titleRE;// 49小时后过期，位置右边的最下边的标签
	private String type;// N-正常用户，P-公众账号
	private String savePath = null; // 图片或语音保存路径
	private String gmtExpired;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTitleRF() {
		return titleRF;
	}

	public void setTitleRF(String titleRF) {
		this.titleRF = titleRF;
	}

	public String getTitleRE() {
		return titleRE;
	}

	public void setTitleRE(String titleRE) {
		this.titleRE = titleRE;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getGmtExpired() {
		return gmtExpired;
	}

	public void setGmtExpired(String gmtExpired) {
		this.gmtExpired = gmtExpired;
	}
}
