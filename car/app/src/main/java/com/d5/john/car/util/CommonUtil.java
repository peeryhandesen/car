package com.d5.john.car.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.d5.john.car.App;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Administrator on 2016/10/18.
 */

public class CommonUtil {

    /**
     * @Description 判断是否是顶部activity
     * @param context
     * @param activityName
     * @return
     */
    public static boolean isTopActivy(Context context, String activityName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cName = am.getRunningTasks(1).size() > 0 ? am
                .getRunningTasks(1).get(0).topActivity : null;

        if (null == cName)
            return false;
        return cName.getClassName().equals(activityName);
    }

    /**
     * @Description 判断存储卡是否存在
     * @return
     */
    public static boolean checkSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    /**
     * @Description 获取sdcard可用空间的大小
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * @Description 获取sdcard容量
     * @return
     */
    @SuppressWarnings({
            "deprecation", "unused"
    })
    private static long getSDAllSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        // return allBlocks * blockSize; //单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }

    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 将byte数组转换为int数据
     *
     * @param b 字节数组
     * @return 生成的int数据
     */
    public static int byteArray2int(byte[] b) {
        return (((int) b[0]) << 24) + (((int) b[1]) << 16)
                + (((int) b[2]) << 8) + b[3];
    }

    /**
     * @Description 判断是否是url
     * @param text
     * @return
     */
    private static String matchUrl(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        Pattern p = Pattern.compile(
                "[http]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&]]*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    /**
     * @Description 返回匹配到的URL
     * @param text
     * @param cmpUrl
     * @return
     */
    private static String getMatchUrl(String text, String cmpUrl) {
        String url = matchUrl(text);
        if (url != null && url.contains(cmpUrl)) {
            return url;
        } else {
            return null;
        }
    }

    public static int getDefaultPannelHeight(Context context) {
        if (context != null) {
            int size = (int) (getElementSzie(context) * 5.5);
            return size;
        } else {
            return 300;
        }
    }

    public static int getAudioBkSize(int sec, Context context) {
        int size = getElementSzie(context) * 3;
        if (sec <= 0) {
            return -1;
        } else if (sec <= 2) {
            return size;
        } else if (sec <= 8) {
            return (int) (size + ((float) ((sec - 2) / 6.0)) * size);
        } else if (sec <= 60) {
            return (int) (2 * size + ((float) ((sec - 8) / 52.0)) * size);
        } else {
            return -1;
        }
    }

    public static int getElementSzie(Context context) {
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int screenHeight = px2dip(dm.heightPixels, context);
            int screenWidth = px2dip(dm.widthPixels, context);
            int size = screenWidth / 6;
            if (screenWidth >= 800) {
                size = 60;
            } else if (screenWidth >= 650) {
                size = 55;
            } else if (screenWidth >= 600) {
                size = 50;
            } else if (screenHeight <= 400) {
                size = 20;
            } else if (screenHeight <= 480) {
                size = 25;
            } else if (screenHeight <= 520) {
                size = 30;
            } else if (screenHeight <= 570) {
                size = 35;
            } else if (screenHeight <= 640) {
                if (dm.heightPixels <= 960) {
                    size = 35;
                } else if (dm.heightPixels <= 1000) {
                    size = 45;
                }
            }
            return size;
        }
        return 40;
    }

    private static int px2dip(float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isInIm(Context context) {
        try {
            if (context instanceof Activity) {
                ActivityManager am = (ActivityManager) context
                        .getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).size() > 0 ? am
                        .getRunningTasks(1).get(0).topActivity : null;
                if (cn != null && cn.getClassName().contains(App.APPLICATION_PACKAGE_NAME)) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    public static String getMd5Path(String url, int type) {
        if (null == url) {
            return null;
        }
        String path = getSavePath(type) + getMd5(url)
                + App.DEFAULT_IMAGE_FORMAT;
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        return path;
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Context ct) {
        if ((((Activity) ct).getWindow()).getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (((Activity) ct).getCurrentFocus() != null)
                ((InputMethodManager) (((Activity) ct).getSystemService(INPUT_METHOD_SERVICE)))
                        .hideSoftInputFromWindow(((Activity) ct).getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public static void showKeyboard(Context ct) {
        if ((((Activity) ct).getWindow()).getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE) {
            if (((Activity) ct).getCurrentFocus() != null)
                ((InputMethodManager) (((Activity) ct).getSystemService(INPUT_METHOD_SERVICE)))
                        .showSoftInput(((Activity) ct).getCurrentFocus(),InputMethodManager.SHOW_FORCED);
        }
    }

    public static String getThumbnailImagePath(String imagePath) {
        String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
        path += "th" + imagePath.substring(imagePath.lastIndexOf("/")+1, imagePath.length());
        return path;
    }

    public static int getImageMessageItemDefaultWidth(Context context) {
        return CommonUtil.getElementSzie(context) * 5;
    }

    public static int getImageMessageItemDefaultHeight(Context context) {
        return CommonUtil.getElementSzie(context) * 7;
    }

    public static int getImageMessageItemMinWidth(Context context) {
        return CommonUtil.getElementSzie(context) * 3;
    }

    public static int getImageMessageItemMinHeight(Context context) {
        return CommonUtil.getElementSzie(context) * 3;
    }


    public static String getMd5(String input) {
        String output = null;
        if (input != null && input.length() > 0)
            try {
                MessageDigest messagedigest = MessageDigest.getInstance("MD5");
                messagedigest.update(input.getBytes(), 0, input.length());
                output = String.format(App.MD5_KEY, new BigInteger(1,
                        messagedigest.digest()));
            } catch (Exception exception) {
            }
        return output;
    }

    public static String getSavePath(int type) {
        String path;
        String floder = (type == App.FILE_SAVE_TYPE_IMAGE) ? "images"
                : "audio";
        if (CommonUtil.checkSDCard()) {
            path = Environment.getExternalStorageDirectory().toString()
                    + File.separator + "MGJ-IM" + File.separator + floder
                    + File.separator;

        } else {
            path = Environment.getDataDirectory().toString() + File.separator
                    + "MGJ-IM" + File.separator + floder + File.separator;
        }
        return path;
    }

    public static String getImageSavePath(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        final File folder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "MGJ-IM"
                + File.separator
                + "images");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getAbsolutePath() + File.separator + fileName;
    }

}
