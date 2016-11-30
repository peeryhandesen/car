package com.d5.john.car.push;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.d5.john.car.App;
import com.d5.john.car.MainActivity;
import com.d5.john.car.R;
import com.d5.john.car.util.CalendarUtils;
import com.d5.john.car.util.sign.D5UrlUtils;
import com.d5.john.car.util.sign.Md5Encrypt;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zhouyi on 2016/1/29 17:39.
 */
public class SmackConnection implements ConnectionListener, PingFailedListener {

    public static enum ConnectionState {
        CONNECTED, CONNECTING, RECONNECTING, DISCONNECTED, AUTHORIZED;
    }

    private int VALIDATE_LOGIN_KEY = 1;

    private String TAG = SmackConnection.class.getName();

    private XMPPTCPConnection mConnection;

    private Context context;

    private static SmackConnection instance;

    private int notifyId = 0;

    private Presence.Type mPresence = Presence.Type.unavailable;

    private boolean isLoginKeyEnable = true;

    private int mRetryCount = 0;

    //最大重连次数
    private final int MAX_RETRY_COUNT = 5;


    private SmackConnection(Context context) {

        this.context = context;


    }

    public static synchronized SmackConnection getInstance(Context context) {

        if (instance == null) {

            instance = new SmackConnection(context.getApplicationContext());

        }

        return instance;

    }

    public synchronized boolean login() {

        if (!isConnect()) {
            return false;
        }

        if (!isLoginKeyEnable) {
            return false;
        }

        if (mRetryCount == MAX_RETRY_COUNT) {
            return false;
        }

        if (StringUtils.isBlank(D5UrlUtils.AUTHED_USERID) || StringUtils.isBlank(D5UrlUtils.LOGIN_KEY)) {
            return false;
        }

        String userId = "";

        String loginKey = "";

        userId = D5UrlUtils.AUTHED_USERID;

        loginKey = D5UrlUtils.LOGIN_KEY;

        if (!mConnection.isAuthenticated()) {

            Log.e("用户名", userId + " ");

            Log.e("密码", loginKey + " ");

            try {

                mConnection.login(userId, loginKey, "");

                return true;

            } catch (SASLErrorException e) {

                syncValidateUser();

            } catch (XMPPException e) {
                Log.e("XMPPException", e.toString());
            } catch (SmackException e) {
                Log.e("SmackException", e.toString());
            } catch (IOException e) {
                Log.e("IOException", e.toString());
            }
        }
        return false;
    }

    private void syncValidateUser() {
        reLogin();
     /*   JczjURLEnum urlEnum = JczjURLEnum.LOGIN_KEY_ENABLED;

        MyJsonHttpResponseHandler responseHandler = createJsonHttpResponseHandler();

        responseHandler.setShowPg(false);

        HttpUtil.syncHttpClient.post(context.getApplicationContext(), AppParam.API_DOMAIN + urlEnum.getUrl(), MyHttpUtils.getSignHashMapParam(context, urlEnum), responseHandler);*/

    }

  /*  private MyJsonHttpResponseHandler createJsonHttpResponseHandler() {
        return new MyJsonHttpResponseHandler(context) {
            @Override
            public void successResponse(JSONObject responseJo) {
                reLogin();
            }
            @Override
            public void falseResponse(JSONObject responseJo) {
                ErrorBean bean = null;
                try {
                    bean = new ErrorBean(responseJo);
                    if (!"USER_LOGIN_EXPIRED".equals(bean.getError().getName())) {
                        reLogin();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure() {
                reLogin();
            }
        };
    }*/

    public void reLogin() {
        disconnect();
        if (ReconnectionManager.getInstanceFor(mConnection).isAutomaticReconnectEnabled()) {
            ReconnectionManager.getInstanceFor(mConnection).disableAutomaticReconnection();
        }
        mRetryCount++;
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        connect();
        login();
    }


    /**
     * 断开连接
     */
    public void disconnect() {
        if (mConnection.isConnected()) {
            try {
                //mConnection.sendStanza(new Presence(Presence.Type.unavailable));
                mConnection.disconnect();
            } catch (Exception exception) {
                exception.printStackTrace();
                mConnection.disconnect();
            }
        }
    }

    public boolean isConnect() {

        if (mConnection == null) {
            return false;
        }
        return mConnection.isConnected();

    }

    public void connect() {

        Log.e("当前调用connect", "------------------------------");
        //mRetryCount = 0;
        if (mConnection == null) {
            mConnection = createConnection();
        } else if (mConnection.isConnected()) {
            disconnect();
        }
        try {
            mConnection.connect();
        } catch (Exception e) {
            Log.e("连接异常", "----------------");
            //e.printStackTrace();
        }
    }


    @SuppressLint("TrulyRandom")
    private XMPPTCPConnection createConnection() {

        SSLContext sc = null;

        EasyX509TrustManager mtm = null;

        try {
            mtm = new EasyX509TrustManager(null);
            sc = SSLContext.getInstance("TLS");
            try {
                sc.init(null, new X509TrustManager[]{mtm}, new SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        /*String net = "10.2.40.1";
        String host = "www.dong5.com";
        String name = "10.0.170.1";*/
        builder.setHost("10.2.40.1");
        builder.setPort(5222);
        builder.setConnectTimeout(20000);
        builder.setServiceName("10.2.40.1");
        builder.setCustomSSLContext(sc);

        SASLMechanism mechanism = new SASLDigestMD5Mechanism();

        SASLAuthentication.registerSASLMechanism(mechanism);

        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");

        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");

        SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());

        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        builder.setSendPresence(false);

        XMPPTCPConnectionConfiguration configuration = builder.build();

        XMPPTCPConnection connection = new XMPPTCPConnection(configuration);

        connection.addConnectionListener(this);

        PingManager.setDefaultPingInterval(600); //Ping every 10 minutes

        PingManager pingManager = PingManager.getInstanceFor(connection);

        pingManager.registerPingFailedListener(this);

        MessagePacketListener messagePacketListener;

        messagePacketListener = new MessagePacketListener();

        connection.addSyncStanzaListener(messagePacketListener, new StanzaTypeFilter(Message.class));

        connection.addSyncStanzaListener(new PresencePacketListener(), new StanzaTypeFilter(Presence.class));

        return connection;
    }

    private void onConnectionEstablished(XMPPConnection con) {

        Log.e("发送登录信息", "-----------------------");

        try {

            Presence presence = new Presence(Presence.Type.available);

            con.sendStanza(presence);

        } catch (SmackException.NotConnectedException e) {
            Log.e("打印错误", e.toString());
        }


    }

    private class PresencePacketListener implements StanzaListener {

        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

            Presence presence = (Presence) packet;

            if (presence.getError() != null) {

                Log.e("Error", presence.getError().toString());

            }

            /*Presence presence = (Presence) packet;

            String to = presence.getTo();

            String from = presence.getFrom();

            Log.e("to", to);

            Log.e("from", from);

            if (presence.getType() != null) {

                if (mPresence == Presence.Type.unavailable) {
                    if (presence.getType() == Presence.Type.available) {
                        mPresence = presence.getType();
                    }
                } else if (mPresence == Presence.Type.available) {
                    if (presence.getType() == Presence.Type.available) {
                        disconnect();
                    }
                }

                Log.e("当前其他地方登录状态", presence.getType().name());

            }

            presence.getStanzaId();

            presence.getStatus();

            Log.e("当前状态", mPresence.name());*/

        }
    }

    private void processOfflineMessages() {

        Log.i(getClass().getName(), "Begin retrieval of offline messages from server");

        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(mConnection);

        try {
            if (!offlineMessageManager.supportsFlexibleRetrieval()) {
                Log.d(getClass().getName(), "Offline messages not supported");
                return;
            }

            List<Message> msgs = offlineMessageManager.getMessages();

            for (Message msg : msgs) {
                String body = msg.getBody();
                Log.e("body", body);
            }

            offlineMessageManager.deleteMessages();
        } catch (Exception e) {
            Log.e(getClass().getName(), "handle offline messages error ", e);
        }

        Log.i(getClass().getName(), "End of retrieval of offline messages from server");

    }

    private class MessagePacketListener implements StanzaListener {

        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

            Message msg = (Message) packet;

            MessageBean bean = new MessageBean();

            bean.setBody(msg.getBody());

            bean.setFrom(msg.getFrom());

            Log.e("当前推送消息", bean.getBody());

            if (App.activityCount == 0) {
                showNotification(bean);
            }
        }

    }


    private void showNotification(MessageBean bean) {

        NotificationBean notificationBean = new Gson().fromJson(bean.getBody(), NotificationBean.class);

        if (notificationBean.getAppLoginKey() != null) {
            String s = Md5Encrypt.md5Simple(D5UrlUtils.LOGIN_KEY);
            if (s != null && !s.equalsIgnoreCase(notificationBean.getAppLoginKey())) {
                disconnect();
                return;
            }
        }

        String type = notificationBean.getProperties();

        String messageType = "";

        String gmtMessageCreate = notificationBean.getGmtMessageCreate();

        if (type != null) {
            try {
                JSONObject jsonObject = new JSONObject(type);
                if (jsonObject.has("messageType")) {
                    messageType = jsonObject.getString("messageType");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //如果消息没有类型，就不弹出通知
        if (StringUtils.isBlank(messageType)) {
            return;
        }

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentText(notificationBean.getAlert());
        builder.setContentTitle("懂我");
        builder.setTicker(notificationBean.getAlert());
        builder.setWhen(CalendarUtils.getLongTime(gmtMessageCreate));
        builder.setSmallIcon(R.mipmap.d5icon);

        Intent notificationIntent = new Intent();
        notificationIntent.setClass(context, MainActivity.class);
        notificationIntent.putExtra(MainActivity.SHOW_TYPE, messageType);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
        notificationIntent.addFlags(launchFlags);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.getNotification();

        //notification.defaults = Notification.DEFAULT_ALL;

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        notification.priority = Notification.PRIORITY_DEFAULT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyId++, notification);
    }

    public void showPushConnectionSuccess() {
       /* if (!Log.DEBUG) {
            return;
        }*/
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentText("推送连接成功");
        builder.setContentTitle("推送连接成功");
        builder.setTicker("推送连接成功");

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.d5icon);

        Intent notificationIntent = new Intent();
        notificationIntent.setClass(context, MainActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
        notificationIntent.addFlags(launchFlags);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.getNotification();

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        notification.priority = Notification.PRIORITY_DEFAULT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notifyId++, notification);

    }


    private void writeToFile(String str) {
      /*  if (!Log.DEBUG) {
            return;
        }*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");//可以方便地修改日期格式

        String nowDate = dateFormat.format(new Date().getTime());

        //添加文件写入和创建的权限 
        String aaa = Environment.getExternalStorageDirectory() + File.separator + "connection.txt";

        File file = new File(aaa);

        try {

            if (!file.exists()) {

                file.createNewFile();
            }

            FileWriter pw = new FileWriter(file, true);

            pw.write(nowDate + "   :   " + str + "\n");

            pw.flush();

            pw.close();

            Log.e("file", file.getAbsolutePath().toString());

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.e(TAG, "connected");
        writeToFile("connected");
        SmackService.sConnectionState = ConnectionState.CONNECTED;
        login();
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.e(TAG, "authenticated");
        writeToFile("authenticated");
        if (!ReconnectionManager.getInstanceFor(mConnection).isAutomaticReconnectEnabled()) {
            ReconnectionManager.getInstanceFor(mConnection).enableAutomaticReconnection();
        }
        showPushConnectionSuccess();
        onConnectionEstablished(connection);
        SmackService.sConnectionState = ConnectionState.AUTHORIZED;
    }

    @Override
    public void connectionClosed() {
        SmackService.sConnectionState = ConnectionState.DISCONNECTED;
        mPresence = Presence.Type.unavailable;
        Log.e(TAG, "connectionClosed");
        writeToFile("connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception ex) {
        if (ex != null && ex instanceof XMPPException) {
            XMPPException xmppEx = (XMPPException) ex;
            Log.e("当前错误", xmppEx.getMessage());
            writeToFile("connectionClosedOnError" + "::::xmppEx.getMessage()::" + xmppEx.getMessage());
        }
        SmackService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.e(TAG, "connectionClosedOnError");

    }

    @Override
    public void reconnectionSuccessful() {
        SmackService.sConnectionState = ConnectionState.CONNECTED;
        Log.e(TAG, "reconnectionSuccessful");
        writeToFile("reconnectionSuccessful");
    }

    @Override
    public void reconnectingIn(int seconds) {
        SmackService.sConnectionState = ConnectionState.CONNECTING;
        Log.e(TAG, "reconnectingIn");
        writeToFile("reconnectingIn");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        SmackService.sConnectionState = ConnectionState.DISCONNECTED;
        ReconnectionManager.getInstanceFor(mConnection).disableAutomaticReconnection();
        mPresence = Presence.Type.unavailable;
        Log.e(TAG, "reconnectionFailed");
        writeToFile("reconnectionFailed");
    }


    @Override
    public void pingFailed() {
        Log.e(TAG, "pingFailed()");
    }
}
