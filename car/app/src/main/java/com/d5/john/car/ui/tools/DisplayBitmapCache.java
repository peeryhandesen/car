package com.d5.john.car.ui.tools;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/9.
 */

public class DisplayBitmapCache {
    private static DisplayBitmapCache instance = null;
    private HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
    private Context context = null;

    public static synchronized DisplayBitmapCache getInstance(Context c) {
        if (null == instance) {
            instance = new DisplayBitmapCache(c);
        }
        return instance;
    }

    private DisplayBitmapCache(Context c) {
        context = c;
    }

    public void set(String path, Bitmap bmp) {
        map.put(path, bmp);
    }

    public Bitmap get(String path) {
        if (map.containsKey(path)) {
            return map.get(path);
        } else {
            Bitmap bmp = ImageTool.getBigBitmapForDisplay(path, context);
            if (null != bmp) {
                map.put(path, bmp);
            }
            return bmp;
        }
    }
}
