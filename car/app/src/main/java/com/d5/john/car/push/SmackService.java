package com.d5.john.car.push;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

public class SmackService extends Service {

    public static SmackConnection.ConnectionState sConnectionState = SmackConnection.ConnectionState.DISCONNECTED;

    private Thread mThread;

    private SmackConnection mConnection;

    private String ACTION_LOGOUT = "ACTION_LOGOUT";

    private String ACTION_LOGIN = "ACTION_LOGIN";

    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_LOGIN)) {
                if (sConnectionState == SmackConnection.ConnectionState.AUTHORIZED) {
                    return;
                } else {
                    start();
                }
            } else if (intent.getAction().equals(ACTION_LOGOUT)) {
                stop();
            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                //startAuthorized();
            } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
               /* LogUtils.e("调用网络连接状态改变","------------------------");
                if(mConnection!=null){
                    if(!mConnection.isConnect()){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }).start();
                    }
                }*/
            }
        }
    };

    private void startAuthorized() {
        if (mConnection != null && mConnection.isConnect()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (sConnectionState != SmackConnection.ConnectionState.AUTHORIZED) {
                        mConnection.login();
                    }
                }
            }).start();
        }
    }

    public static SmackConnection.ConnectionState getState() {
        if (sConnectionState == null) {
            return SmackConnection.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    @Override
    public void onCreate() {

        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initReceiver();

        start();

        return Service.START_STICKY;

    }

    //初始化广播
    private void initReceiver() {

        IntentFilter filter = new IntentFilter();
        //发送登录
        filter.addAction(ACTION_LOGIN);
        //发送退出登录
        filter.addAction(ACTION_LOGOUT);
        //获得系统每分钟通知
        filter.addAction(Intent.ACTION_TIME_TICK);
        //获得网络连接通知
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(loginReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(loginReceiver);
        } catch (Exception e) {
        }

    }

    public void start() {

        /**
         * 如果已经连接了就不进行继续连接
         */
        if (sConnectionState == SmackConnection.ConnectionState.AUTHORIZED) {
            return;
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                initConnection();
            }
        });

        mThread.start();

    }

    public void stop() {
        Log.e("调用stop", "-----------------");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                }
            }
        }).start();
    }

    private void initConnection() {

        if (mConnection == null) {

            mConnection = SmackConnection.getInstance(getApplicationContext());

            mConnection.connect();

        } else {
            mConnection.connect();
        }


    }
}
