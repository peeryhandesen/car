package com.d5.john.car;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.d5.john.car.adapter.GlideImageLoader;
import com.d5.john.car.photoscan.DisplayImageOptionsUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okhttputils.OkHttpUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.List;

/**
 * Created by idisfkj on 16/4/21.
 * Email : idisfkj@qq.com.
 */
public class App extends Application {
    private static Context mContext;
    private static App mApplication;
    public final ImageLoader imageLoader = ImageLoader.getInstance();
    public static synchronized App getInstance() {
        return mApplication;
    }
    public static final String TAG = "com.d5.john.car";
   /* public static final String DEVELOPER_ID = "XgX80PM5G/x0Nq4zMxQM8U5BL4zQQu7viMV1Oahl1YQ=";
    public static final String DEVELOPER_NUMBER = "666666";
    public static final String DEVELOPER_NAME = "John";
    public static final String DEVELOPER_MESSAGE = "欢迎注册懂我,我是该App的开发者，你可以使用添加朋友自行互动测试！如有问题可以在此留言与我。";
    public static final String HELLO_MESSAGE = "你已添加了%s，现在可以开始聊天了";
    public static final String UNREADNUM = "unReadNum";
    public static final String APP_ID = "2882303761517464903";
    public static final String APP_KEY = "5631746467903";
    public static final String APP_SECRET_KEY = "HxMA7STSUQMLEiDX+zo+5A==";
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;*/

    public static final String DEFAULT_IMAGE_FORMAT = ".jpg";
    public static String MD5_KEY = "%032xxnMGJ";
    public static final int FILE_SAVE_TYPE_IMAGE = 0X00013;
    public static final int CAMERA_WITH_DATA = 3023;
    public static final String APPLICATION_PACKAGE_NAME = "com.d5.john.car";
    public static int activityCount = 0;
    public static int maxImgCount = 5;               //允许选择图片最大数



    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initImageLoader(getApplicationContext());
        initImagePicker();
        mContext = getApplicationContext();
        OkHttpUtils.init(this);

    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    public static DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()//
            .showImageOnLoading(R.mipmap.default_image)         //设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.mipmap.default_image)       //设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.mipmap.default_image)            //设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)                                //设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                                  //设置下载的图片是否缓存在SD卡中
            .build();                                           //构建完成

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);// 线程池中线程的个数
        config.denyCacheImageMultipleSizesInMemory();// 拒绝缓存多个图片
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());// 将保存的时候的URI名称用MD5
        // 加密
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);// 设置图片下载和显示的工作队列排序
        // config.writeDebugLogs(); // 打开调试日志,删除不显示日志
        config.defaultDisplayImageOptions(DisplayImageOptionsUtil.defaultOptions);// 显示图片的参数
        // config.diskCache(new UnlimitedDiskCache(cacheDir));//自定义缓存路径

        ImageLoader.getInstance().init(config.build());
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static Context getAppContext(){
        return mContext;
    }
}
