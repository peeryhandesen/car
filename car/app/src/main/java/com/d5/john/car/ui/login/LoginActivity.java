package com.d5.john.car.ui.login;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.d5.john.car.MainActivity;
import com.d5.john.car.R;
import com.d5.john.car.beans.LoginInfo;
import com.d5.john.car.util.ToastUtils;
import com.d5.john.car.util.sign.D5UrlUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends LoginBaseAc implements View.OnClickListener {

    @Bind(R.id.title_login)
    TextView mTitleLogin;
    @Bind(R.id.et_setName)
    EditText mEtSetName;
    @Bind(R.id.img_seePW)
    ImageView mImgSeePW;
    @Bind(R.id.et_setPw)
    EditText mEtSetPw;
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    public  static String signKey = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 监听多个输入框
        mEtSetName.addTextChangedListener(new TextChange());
        mEtSetPw.addTextChangedListener(new TextChange());

        mImgSeePW.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    @OnClick({R.id.et_setName, R.id.img_seePW, R.id.et_setPw, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_setName:
                break;
            case R.id.img_seePW:
                mEtSetName.getText().clear();
                break;
            case R.id.et_setPw:
                break;
            case R.id.btn_login:
                getLogin();
                break;
        }
    }

    private void getLogin() {
        String userName = mEtSetName.getText().toString().trim();
        String password = mEtSetPw.getText().toString().trim();
        getLogin(userName, password);
    }

    private String getSignKey(String value){
        value = value == null ? "" : value;
        int index1 = value.lastIndexOf("signKey".toString());
        String des = value.substring(index1);
        int index2 = des.indexOf('&');
        String des1 = des.substring(0,index2);
        return des1;
    }



    private void getLogin(final String userName, final String password) {
        String url = D5UrlUtils.LoginUrl(userName,password);
        String url2 = D5UrlUtils.Login_URL;
        String url3 = D5UrlUtils.Login_URL_PERSON;
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            OkHttpUtils.get(url)     // 请求方式和请求url
                    .tag("LoginActivity")          // 请求的 tag, 主要用于取消对应的请求
                    .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                    .connTimeOut(2000)
                    .cacheMode(CacheMode.DEFAULT)   // 缓存模式，详细请看缓存介绍
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                //signKey 签名秘钥 loginKey 登录秘钥 userId 客服编
                                JSONObject json = new JSONObject(s);
                                JSONObject data =json.getJSONObject("response");
                                if(data.getBoolean("success")) {
                                    LoginInfo info = new LoginInfo();
                                    info.setLoginKey(data.getString("loginKey"));
                                    info.setLoginName(data.getString("loginName"));
                                    info.setSignKey(data.getString("signKey"));
                                    info.setUserId(data.getString("userId"));
                                    D5UrlUtils.LOGIN_KEY =  data.getString("loginKey");
                                    D5UrlUtils.SIGN_KEY = data.getString("signKey");
                                    D5UrlUtils.AUTHED_USERID = data.getString("userId");
                                    startMainActivity();
                                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                                }else{
                                    JSONObject error =data.getJSONObject("error");
                                    String message = error.getString("message");
                                    ToastUtils.showShort(message);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } else {

        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    // EditText监听器
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            boolean Sign2 = mEtSetName.getText().length() > 0;
            boolean Sign3 = mEtSetPw.getText().length() > 4;
            if(Sign2 ){
                mImgSeePW.setVisibility(View.VISIBLE);
            } else{
                mImgSeePW.setVisibility(View.INVISIBLE);
            }
            if (Sign2 & Sign3) {
                mBtnLogin.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_bg_green));
                mBtnLogin.setEnabled(true);
            } else {
                mBtnLogin.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_enable_green));
                mBtnLogin.setTextColor(0xFFD0EFC6);
                mBtnLogin.setEnabled(false);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();  //点击home键后，再打开就需要调用该方法
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*CommonData.getInstance().cleanData();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("LoginActivity");

    }

}
