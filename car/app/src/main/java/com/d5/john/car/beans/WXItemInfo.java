package com.d5.john.car.beans;

/**
 * Created by idisfkj on 16/4/22.
 * Email : idisfkj@qq.com.
 */
public class WXItemInfo{
    private String pictureUrl;
    private String title;
    private String content;
    private String time;
    private String regId;
    private String userId;
    private String custumId;
    private String number;
    private String currentAccount;
    private String savePath; // 图片或语音保存路径
    private String url; // 图片或语音链接
    private int unReadNum;
    private String replyModel;   //客户聊天模式SYSTEM_REPLY（系统回复）；  ARTIFICIAL_REPLY（人工回复）
    private int alarmCountNum;   //客户未读警报信息

    public int getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }

    public String getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getReplyModel() {
        return replyModel;
    }

    public void setReplyModel(String replyModel) {
        this.replyModel = replyModel;
    }

    public int getAlarmCountNum() {
        return alarmCountNum;
    }

    public void setAlarmCountNum(int alarmCountNum) {
        this.alarmCountNum = alarmCountNum;
    }

    @Override
    public String toString() {
        return "WXItemInfo{" +
                "pictureUrl='" + pictureUrl + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", regId='" + regId + '\'' +
                ", userId='" + userId + '\'' +
                ", custumId='" + custumId + '\'' +
                ", number='" + number + '\'' +
                ", currentAccount='" + currentAccount + '\'' +
                ", savePath='" + savePath + '\'' +
                ", url='" + url + '\'' +
                ", unReadNum=" + unReadNum +
                ", replyModel='" + replyModel + '\'' +
                ", alarmCountNum=" + alarmCountNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WXItemInfo info = (WXItemInfo) o;

        if (unReadNum != info.unReadNum) return false;
        if (alarmCountNum != info.alarmCountNum) return false;
        if (pictureUrl != null ? !pictureUrl.equals(info.pictureUrl) : info.pictureUrl != null)
            return false;
        if (title != null ? !title.equals(info.title) : info.title != null) return false;
        if (content != null ? !content.equals(info.content) : info.content != null) return false;
        if (time != null ? !time.equals(info.time) : info.time != null) return false;
        if (regId != null ? !regId.equals(info.regId) : info.regId != null) return false;
        if (userId != null ? !userId.equals(info.userId) : info.userId != null) return false;
        if (custumId != null ? !custumId.equals(info.custumId) : info.custumId != null)
            return false;
        if (number != null ? !number.equals(info.number) : info.number != null) return false;
        if (currentAccount != null ? !currentAccount.equals(info.currentAccount) : info.currentAccount != null)
            return false;
        if (savePath != null ? !savePath.equals(info.savePath) : info.savePath != null)
            return false;
        if (url != null ? !url.equals(info.url) : info.url != null) return false;
        return replyModel != null ? replyModel.equals(info.replyModel) : info.replyModel == null;

    }

    @Override
    public int hashCode() {
        int result = pictureUrl != null ? pictureUrl.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (regId != null ? regId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (custumId != null ? custumId.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (currentAccount != null ? currentAccount.hashCode() : 0);
        result = 31 * result + (savePath != null ? savePath.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + unReadNum;
        result = 31 * result + (replyModel != null ? replyModel.hashCode() : 0);
        result = 31 * result + alarmCountNum;
        return result;
    }
}
