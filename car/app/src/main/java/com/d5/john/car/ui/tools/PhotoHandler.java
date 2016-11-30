package com.d5.john.car.ui.tools;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;
import com.d5.john.car.R;
import com.d5.john.car.util.CommonUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/9.
 */

public class PhotoHandler {
    private Context context;
    private String takePhotoSavePath = null;
    private static PhotoHandler instance = null;

    public static synchronized PhotoHandler getInstance(Context c) {
        if (null == instance) {
            instance = new PhotoHandler(c);
        }
        return instance;
    }

    private PhotoHandler(Context c) {
        context = c;
    }

    public String getTakePhotoSavePath() {
        return takePhotoSavePath;
    }

    public void setTakePhotoSavePath(String takePhotoSavePath) {
        this.takePhotoSavePath = takePhotoSavePath;
    }

    public Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }

    public void takePhoto() {
        if (CommonUtil.checkSDCard()) {
            doTakePhoto(context);
        } else {
            Toast.makeText(context,
                    context.getResources().getString(R.string.no_sdcard),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);// 压缩位图
            byte[] bytes = baos.toByteArray();// 创建分配字节数组
            return bytes;
        } catch (Exception e) {
          /*  Logger.getLogger(PhotoHandler.class).e(e.getMessage());*/
            return null;
        } finally {
            if (null != baos) {
                try {
                    baos.flush();
                    baos.close();
                } catch (IOException e) {
                    /*Logger.getLogger(PhotoHandler.class).e(e.getMessage());*/
                }
            }
        }
    }

    /**
     * @Description 上传服务器前调用该方法进行压缩
     * @param path
     * @return
     * @throws IOException
     */
    public static Bitmap revitionImage(String path) throws IOException {
        if (null == path || TextUtils.isEmpty(path) || !new File(path).exists())
            return null;
        BufferedInputStream in = null;
        try {
            int degree = readPictureDegree(path);
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // options.
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            Bitmap bitmap = null;
            while (true) {
                if ((options.outWidth >> i <= 1000)
                        && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(new FileInputStream(new File(
                            path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    // int height = options.outHeight * 100 / options.outWidth;
                    // options.outWidth = 100;
                    // options.outHeight = height;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
            Bitmap newbitmap = rotaingImageView(degree, bitmap);
            return newbitmap;
        } catch (Exception e) {
           /* Logger.getLogger(PhotoHandler.class).e(e.getMessage());*/
            return null;
        } finally {
            if (null != in) {
                in.close();
                in = null;
            }
        }
    }

    public String getImagePathFromUri(Uri uri) {
        // 如果是file，直接拿
        if (uri.getScheme().equalsIgnoreCase("file")) {
            return uri.getPath();
        }

        String[] projection = {
                MediaStore.Images.Media.DATA
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        int column_index = cursor.getColumnIndex(projection[0]);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();

        return path;
    }

    public void doTakePhoto(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoSavePath = CommonUtil.getImageSavePath(String.valueOf(System
                .currentTimeMillis()) + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(takePhotoSavePath)));
        // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
       /* ((MessageActivity) context).startActivityForResult(intent,
                App.CAMERA_WITH_DATA);*/
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
        }
        return degree;
    }
    /*
    * 旋转图片
    * @param angle
    * @param bitmap
    * @return Bitmap
    */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
