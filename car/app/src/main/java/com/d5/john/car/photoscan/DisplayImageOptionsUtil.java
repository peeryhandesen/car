package com.d5.john.car.photoscan;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Administrator on 2016/11/10.
 */

public class DisplayImageOptionsUtil {
    public static DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

}
