package com.d5.john.car.push;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/11/7.
 */

public class NotificationBean implements Parcelable {
    private String appLoginKey;
    private String alert;
    private String properties;
    private int badge;
    private String gmtMessageCreate;




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public NotificationBean() {
    }

    protected NotificationBean(Parcel in) {
    }

    public static final Parcelable.Creator<NotificationBean> CREATOR = new Parcelable.Creator<NotificationBean>() {
        @Override
        public NotificationBean createFromParcel(Parcel source) {
            return new NotificationBean(source);
        }

        @Override
        public NotificationBean[] newArray(int size) {
            return new NotificationBean[size];
        }
    };

    public String getAppLoginKey() {
        return appLoginKey;
    }

    public void setAppLoginKey(String appLoginKey) {
        this.appLoginKey = appLoginKey;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public String getGmtMessageCreate() {
        return gmtMessageCreate;
    }

    public void setGmtMessageCreate(String gmtMessageCreate) {
        this.gmtMessageCreate = gmtMessageCreate;
    }
}
