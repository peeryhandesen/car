package com.d5.john.car.ui.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.BitmapCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/18.
 */
public class ImageTool {
    private final static int MAX_NUM_PIXELS = 320 * 490;
    private final static int MIN_SIDE_LENGTH = 350;

    /**
     *
     * @Description 生成图片的压缩图
     * @param filePath
     * @return
     */
    public static Bitmap createImageThumbnail(String filePath) {
        if (null == filePath || !new File(filePath).exists())
            return null;
        Bitmap bitmap = null;
        int degree = PhotoHandler.readPictureDegree(filePath);
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, MAX_NUM_PIXELS);
            opts.inJustDecodeBounds = false;
            //            if (opts.inSampleSize == 1) {
            //                bitmap = BitmapFactory.decodeFile(filePath, opts);
            //
            //            } else {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
            //            }
        } catch (Exception e) {
            return null;
        }
        Bitmap newBitmap = PhotoHandler.rotaingImageView(degree, bitmap);
        return newBitmap;
    }

    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1)
                ? 1
                : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1)
                ? MIN_SIDE_LENGTH
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h
                / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int getBitmapSize(Bitmap bitmap){
        if(bitmap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
                return bitmap.getAllocationByteCount();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
                return bitmap.getByteCount();
            }
            return bitmap.getRowBytes() * bitmap.getHeight();//earlier version
        }else{
            return 0;
        }
    }

    public static boolean getBitmapData(final ImageView imageView, final String url, final String smallImagePath,final String tag ){
        final boolean[] bo = {false};
        if((imageView!=null)&&(url!=null)) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils.get(url)//
                            .tag(tag)//
                            .cacheMode(CacheMode.NO_CACHE)
                            .execute(new BitmapCallback() {
                                @Override
                                public void onSuccess(Bitmap bitmap, Call call, Response response) {
                                    // bitmap 即为返回的图片数据
                                    Bitmap bmp = ImageTool.createScaleBitmap(bitmap,106,106,true);
                                    if(bmp != null){
                                        imageView.setImageBitmap(bmp);
                                        MessageBitmapCache.getInstance().put(smallImagePath, bmp);
                                    }else {
                                        imageView.setImageBitmap(bitmap);
                                        MessageBitmapCache.getInstance().put(smallImagePath, bitmap);
                                    }
                                    bo[0] = true;
                                    /*bmp.recycle();*/
                                }
                            });
                }
            });
        }
        return bo[0];
    }
    public static Bitmap ratio(String imgPath){
        try{
            int degeree = PhotoHandler.readPictureDegree(imgPath);
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            Bitmap resultBitmap = PhotoHandler.rotaingImageView(degeree, bitmap);
            return resultBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
             be = (int) (newOpts.outWidth / ww);
             } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
             }
        if (be <= 0) be = 1;
         newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
         bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
         // 压缩好比例大小后再进行质量压缩
        //        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
         return bitmap;
    }

    public static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, boolean bo) {
        Bitmap dst = null;
        try {
            Bitmap test = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
            if(getBitmapSize(test) / 1024 > 32) {
                dst = ratio(test, dstWidth, dstHeight);
                test.recycle();
            }else{
                dst = test;
                test.recycle();
            }
            if ((src != dst) && bo) { // 如果没有缩放，那么不回收
                src.recycle(); // 释放Bitmap的native像素数组
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dst;
    }

    public static String bitmap2String(Bitmap image){
        String encodedString = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, os);
            int options = 100;
            while( os.toByteArray().length / 1024>2048) {//判断如果图片大于2M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
                os.reset();//重置baos即清空baos
                options -= 10;
                image.compress(Bitmap.CompressFormat.JPEG, options, os);//这里压缩50%，把压缩后的数据存放到baos中
            }
            encodedString = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
        /*String des = new String(os.toByteArray());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedString;
    }

    public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            image.compress(Bitmap.CompressFormat.JPEG, 100, os);
            int options = 100;
            while( os.toByteArray().length / 1024>32) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
                os.reset();//重置baos即清空baos
                options -= 10;
                image.compress(Bitmap.CompressFormat.JPEG, options, os);//这里压缩50%，把压缩后的数据存放到baos中
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
        //      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static Bitmap getBigBitmapForDisplay(String imagePath, Context context) {
        if (null == imagePath || !new File(imagePath).exists())
            return null;
        try {
            int degeree = PhotoHandler.readPictureDegree(imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap == null)
                return null;
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            float scale = bitmap.getWidth() / (float) dm.widthPixels;
            Bitmap newBitMap = null;
            if (scale > 1) {
                newBitMap = zoomBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale));
                bitmap.recycle();
                Bitmap resultBitmap = PhotoHandler.rotaingImageView(degeree, newBitMap);
                return resultBitmap;
            }
            Bitmap resultBitmap = PhotoHandler.rotaingImageView(degeree, bitmap);
            return resultBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (null == bitmap) {
            return null;
        }
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) width / w);
            float scaleHeight = ((float) height / h);
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            return newbmp;
        } catch (Exception e) {
            return null;
        }
    }
}
