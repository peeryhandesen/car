package com.d5.john.car.beans;

import java.io.Serializable;

/**
 * Created by idisfkj on 16/4/25.
 * Email : idisfkj@qq.com.
 */
public class ChatMessageInfo implements Serializable {
    private String message;   //messageContent
    /**
     * 接收与发送的标识
     */
    private int flag;
    private String time;   //gmtMessageCreate
    /**
     * 接收该信息的账号
     */
    private String receiverNumber;   //receiverId
    private String regId;            //loginKey


    private String custumId;         //custumId
    private String name;             //消息类型
    private long messageId;
    private boolean readed;
    private int height;
    private int width;
    /**
     * 发送者的账号
     */
    private String sendNumber;    //senderId

    private double  matchRate = -1.0;     //消息匹配度

    private boolean isRed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessageInfo info = (ChatMessageInfo) o;

        if (flag != info.flag) return false;
        if (messageId != info.messageId) return false;
        if (readed != info.readed) return false;
        if (message != null ? !message.equals(info.message) : info.message != null) return false;
        if (time != null ? !time.equals(info.time) : info.time != null) return false;
        if (receiverNumber != null ? !receiverNumber.equals(info.receiverNumber) : info.receiverNumber != null)
            return false;
        if (regId != null ? !regId.equals(info.regId) : info.regId != null) return false;
        if (custumId != null ? !custumId.equals(info.custumId) : info.custumId != null)
            return false;
        if (name != null ? !name.equals(info.name) : info.name != null) return false;
        return sendNumber != null ? sendNumber.equals(info.sendNumber) : info.sendNumber == null;

    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + flag;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (receiverNumber != null ? receiverNumber.hashCode() : 0);
        result = 31 * result + (regId != null ? regId.hashCode() : 0);
        result = 31 * result + (custumId != null ? custumId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (messageId ^ (messageId >>> 32));
        result = 31 * result + (readed ? 1 : 0);
        result = 31 * result + (sendNumber != null ? sendNumber.hashCode() : 0);
        return result;
    }

    public ChatMessageInfo() {
    }

    public ChatMessageInfo(String message, int flag, String time, String receiverNumber, String regId, String sendNumber) {
        this.message = message;
        this.flag = flag;
        this.time = time;
        this.receiverNumber = receiverNumber;
        this.regId = regId;
        this.sendNumber = sendNumber;
    }

   public ChatMessageInfo(String message, int flag, String time, String receiverNumber, String regId, String sendNumber,String custumId,String name,long messageId,boolean readed) {
        this.message = message;
        this.flag = flag;
        this.time = time;
        this.receiverNumber = receiverNumber;
        this.regId = regId;
        this.sendNumber = sendNumber;
        this.custumId = custumId;
        this.name = name;
        this.messageId = messageId;
        this.readed = readed;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getSendNumber() {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber) {
        this.sendNumber = sendNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getCustumId() {
        return custumId;
    }

    public void setCustumId(String custumId) {
        this.custumId = custumId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getMatchRate() {
        return matchRate;
    }

    public void setMatchRate(double matchRate) {
        this.matchRate = matchRate;
    }

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }
}
