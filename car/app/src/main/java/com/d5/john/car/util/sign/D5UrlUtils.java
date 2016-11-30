package com.d5.john.car.util.sign;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/17.
 */

public class D5UrlUtils {
    // 主机地址
    private static final String MAIN_ENGINE = "http://api.dong5.com/service.json?";
    public static final String MAIN_IMAGE_ENGINE = "http://www.dong5.com/chatcore/chat/chatImage.resource?imageId=";

    public static String LOGIN_KEY = null;
    public static String SIGN_KEY = null;
    public static String AUTHED_USERID = null;   //authedUserId

    // 用户推送登录
    public static String LoginPushUrl(String loginKey,String authedUserId) {
        if(loginKey == null ){
            loginKey = LOGIN_KEY;
        }
        if(authedUserId == null){
            authedUserId = AUTHED_USERID;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("loginKey",loginKey);
        parameterMap.put("authedUserId",authedUserId);
        parameterMap.put("appUserAgent","android");
        parameterMap.put("appVersion","1.0");
        parameterMap.put("appName","dong5");
        parameterMap.put("service","LOGIN");
        try {
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }


    // 用户登录
    public static String Login_URL = MAIN_ENGINE + "appName=dong5&appUserAgent=android&appVersion=1.0&loginName=admin&loginPassword=huored&service=LOGIN";
    public static String LoginUrl(String admin,String loginPassword) {
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("loginName",admin);
        parameterMap.put("loginPassword",loginPassword);
        parameterMap.put("appUserAgent","android");
        parameterMap.put("appVersion","1.0");
        parameterMap.put("appName","dong5");
        parameterMap.put("service","LOGIN");
        try {
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    public static String Login_URL_PERSON = MAIN_ENGINE + "appName=dong5&appUserAgent=android&appVersion=1.0&loginName=zilong&loginPassword=huored&service=LOGIN";
    //客户列表
    private String CUSTOMER_LIST_URL = MAIN_ENGINE +"service=CUSTOMER_LIST&staffId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
  * 客户列表
  * */
    public static String CustomerListUrl(String loginKey){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","CUSTOMER_LIST");
        parameterMap.put("staffId",AUTHED_USERID);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //待接入客户列表
    private String WAIT_SWITCH_CUSTOMER_LIST = MAIN_ENGINE + "service=WAIT_SWITCH_CUSTOMER_LIST&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
    * 待接入客户列表
    * "service=WAIT_SWITCH_CUSTOMER_LIST&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    * */
    public static String WaitSwitchCustomerListUrl(String loginKey){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","WAIT_SWITCH_CUSTOMER_LIST");
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //聊天客户消息列表
    private String CHAT_CUSTOMER_LIST = MAIN_ENGINE + "service=CHAT_CUSTOMER_LIST&staffId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
    * 聊天客户消息列表
    * "service=CHAT_CUSTOMER_LIST&staffId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    * */
    public static String ChatCustomerListUrl(String loginKey){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","CHAT_CUSTOMER_LIST");
        parameterMap.put("staffId",AUTHED_USERID);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //客户聊天消息加载
    private String MESSAGE_RECORD_LOAD = MAIN_ENGINE + "service=MESSAGE_RECORD_LOAD&customerId=1&authedUserId=1&earlistDate=yyy-mm-dd hh:MM:ss&loginKey=XXXXXXX&sign=XXXXXX";
    /*
   * 客户聊天消息加载
   * "service=MESSAGE_RECORD_LOAD&customerId=1&authedUserId=1&earlistDate=yyy-mm-dd hh:MM:ss&loginKey=XXXXXXX&sign=XXXXXX"
   * */
    public static String MessageRecordLoadUrl(String loginKey,String earlistDate,String customerId){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","MESSAGE_RECORD_LOAD");
        parameterMap.put("customerId",customerId);
        if(!TextUtils.isEmpty(earlistDate)) {
            parameterMap.put("earlistDate", earlistDate);
        }
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //聊天消息发送
    private String MESSAGE_SEND = MAIN_ENGINE + "service=MESSAGE_SEND&customerId=1&authedUserId=1&content=XXXXXX&contentType=TEXT&loginKey=XXXXXXX&sign=XXXXXX";
    /*
   * 聊天消息发送
   * "service=MESSAGE_SEND&customerId=1&authedUserId=1&content=XXXXXX&contentType=TEXT&loginKey=XXXXXXX&sign=XXXXXX";
   * */
    public static String MessageSendUrl(String loginKey,String customerId, String contentData,String contentType){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","MESSAGE_SEND");
        parameterMap.put("customerId",customerId);
        parameterMap.put("content",contentData);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("contentType",contentType);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }

    public static String MessageSendPictureUrl(String loginKey,String customerId, String contentData,String contentType){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","MESSAGE_SEND");
        parameterMap.put("customerId",customerId);
        parameterMap.put("content",contentData);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("contentType",contentType);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            parameterMap.remove("content");
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //客户接入
    private String CUSTOMER_SWITCH_IN = MAIN_ENGINE + "service=CUSTOMER_SWITCH_IN&customerId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
     * 客户接入
     * "service=CUSTOMER_SWITCH_IN&customerId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
     * */
    public static String CustomerSwitchInUrl(String loginKey,String customerId){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","CUSTOMER_SWITCH_IN");
        parameterMap.put("customerId",customerId);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //客户头像地址查询接口
    private String CUSTOMER_LOGO_URL = MAIN_ENGINE + "service=CUSTOMER_LOGO_URL&customerId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
    * 客户头像地址查询接口
    * "service=CUSTOMER_LOGO_URL&customerId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    * */
    public static String CustomerLogoUrl(String loginKey,String customerId){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","CUSTOMER_LOGO_URL");
        parameterMap.put("customerId",customerId);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //移动客户端通知key更新
    private String MOBILE_CLIENT_USER_NOTICE_KEY_UPDATE = MAIN_ENGINE + "service=MOBILE_CLIENT_USER_NOTICE_KEY_UPDATE&authedUserId=1&appUserAgent=android&appVersion=1.0&appName=dong5&loginKey=XXXXXXX&sign=XXXXXX";
    /*
    * 客户头像地址查询接口
    * "service=MOBILE_CLIENT_USER_NOTICE_KEY_UPDATE&authedUserId=1&appUserAgent=android&appVersion=1.0&appName=dong5&loginKey=XXXXXXX&sign=XXXXXX";
    * */
    public static String MobileClientUserNoticeKeyUpdateUrl(String loginKey){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","MOBILE_CLIENT_USER_NOTICE_KEY_UPDATE");
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("appUserAgent","android");
        parameterMap.put("appVersion","1.0");
        parameterMap.put("appName","dong5");
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }
    //竞彩之家退出接口
    private String LOGOUT = MAIN_ENGINE + "service=LOGOUT&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
    * 客户头像地址查询接口
    * ""service=LOGOUT&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    * */
    public static String LogoutUrl(String loginKey){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","LOGOUT");
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }

    //设置初始化接口
    private String INSTALLATION_INITIALIZE = MAIN_ENGINE + "service=INSTALLATION_INITIALIZE&staffId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
       * 设置初始化接口
       * "service=INSTALLATION_INITIALIZE &staffId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
       * */
    public static String installationInitializeUrl(String loginKey){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","INSTALLATION_INITIALIZE");
        parameterMap.put("staffId",AUTHED_USERID);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }

    //聊天模式切换
    private String REPLY_MODEL_CHANGE_BY_CUSTOMER = MAIN_ENGINE + "service=REPLY_MODEL_CHANGE_BY_CUSTOMER&customerId=1&authedUserId=1&replyModel=XXXXXX &loginKey=XXXXXXX&sign=XXXXXX";
    /*
       * 聊天模式切换
       * "service=REPLY_MODEL_CHANGE_BY_CUSTOMER &customerId=1&authedUserId=1&replyModel=XXXXXX
       * &loginKey=XXXXXXX&sign=XXXXXX";
       * */
    public static String replyModelChangeByCustomerUrl(String loginKey,String customerId,String replyModel){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","REPLY_MODEL_CHANGE_BY_CUSTOMER");
        parameterMap.put("customerId",customerId);
        parameterMap.put("replyModel",replyModel);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }

    //全部切换为系统回复接口
    private String REPLY_MODEL_CHANGE_BY_STAFF = MAIN_ENGINE + "service=REPLY_MODEL_CHANGE_BY_STAFF&staffId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
    /*
       * 全部切换为系统回复接口
       * "service=REPLY_MODEL_CHANGE_BY_STAFF&staffId=1&authedUserId=1&loginKey=XXXXXXX&sign=XXXXXX";
       * */
    public static String replyModelChangeByStaff(String loginKey){
        if(loginKey == null){
            loginKey = LOGIN_KEY;
        }
        String content = null;
        HashMap<String,String> parameterMap= new HashMap<>();
        parameterMap.put("service","REPLY_MODEL_CHANGE_BY_STAFF");
        parameterMap.put("staffId",AUTHED_USERID);
        parameterMap.put("authedUserId",AUTHED_USERID);
        parameterMap.put("loginKey",loginKey);
        try {
            String sign = SecurityUtil.md5Sign(SIGN_KEY, parameterMap, "UTF-8");
            parameterMap.put("sign",sign );
            content = SignatureUtils.getSignatureContent(parameterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = MAIN_ENGINE + content;
        return url;
    }


}
