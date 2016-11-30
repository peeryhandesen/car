package com.d5.john.car.ui.common;

import com.d5.john.car.beans.WXItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class CommonData {
    private static CommonData instance = new CommonData();

    private List<WXItemInfo> mWXItemInfosItems = new ArrayList<>();

    public static synchronized CommonData getInstance() {
        if(instance == null) {
            instance = new CommonData();
        }
        return instance;
    }

    public void cleanData(){
        mWXItemInfosItems.clear();
    }

    public List<WXItemInfo> getWXItemInfosItems() {
        return mWXItemInfosItems;
    }

    public void setWXItemInfosItems(List<WXItemInfo> WXItemInfosItems) {
        mWXItemInfosItems.clear();
        mWXItemInfosItems = WXItemInfosItems;
    }

}
