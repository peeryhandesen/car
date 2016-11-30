package com.d5.john.car.custom.bean;

public class User {
	private String telephone;// 手机号
	private String custumId;//
	private String userId;//
	private String id;//AUTHED_USERID
	private String password;// 密码
	private String userName;// 用户名
	private String headUrl;// 头像保存路径
	private String signature;// 个性签名
	private String sex;// 性别: M男士，W女士
	private String location;// 位置信息
	private String birthday;// 生日
	private String type;// N-正常用户，P-公众账号
	private String chatStatus;//判断是否已超时 name : "EXPIRED"
	private String replyModel;//

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	private String savePath = null; // 图片或语音保存路径

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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChatStatus() {
		return chatStatus;
	}

	public void setChatStatus(String chatStatus) {
		this.chatStatus = chatStatus;
	}

	public String getCustumId() {
		return custumId;
	}

	public void setCustumId(String custumId) {
		this.custumId = custumId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReplyModel() {
		return replyModel;
	}

	public void setReplyModel(String replyModel) {
		this.replyModel = replyModel;
	}
}
