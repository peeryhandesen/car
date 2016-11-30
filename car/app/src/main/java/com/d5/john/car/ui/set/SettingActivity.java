package com.d5.john.car.ui.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d5.john.car.App;
import com.d5.john.car.MainActivity;
import com.d5.john.car.R;
import com.d5.john.car.util.ToastUtils;
import com.d5.john.car.util.sign.D5UrlUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportActivity;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/17.
 */

public class SettingActivity extends SupportActivity {
    @Bind(R.id.setting_title_img_back)
    ImageView mSettingTitleImgBack;
    @Bind(R.id.setting_title_img_click)
    ImageView mSettingTitleImgClick;
    @Bind(R.id.setting_title_tv_auto_on)
    TextView mSettingTitleTvAutoOn;
    private boolean mfalg = true;
    private static final String FLAG = "flag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
        ButterKnife.bind(this);
        App.activityCount = 5;
        Bundle bundle = getIntent().getExtras();
        mfalg = bundle.getBoolean(FLAG);
        if (mfalg) {
            mSettingTitleImgClick.setVisibility(View.GONE);
            mSettingTitleTvAutoOn.setText("已全部开启");
        }else{
            mSettingTitleImgClick.setVisibility(View.VISIBLE);
            mSettingTitleTvAutoOn.setText("未全开启");
        }

    }

    @OnClick({R.id.setting_title_img_back, R.id.setting_title_img_click})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_title_img_back:
                startMainActivity();
                break;
            case R.id.setting_title_img_click:
               /* mSettingTitleImgClick.setVisibility(View.GONE);
                mSettingTitleTvAutoOn.setText("已全部开启");*/
                requestChangeByStaffData();
                break;
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void requestChangeByStaffData() {
        String url = D5UrlUtils.replyModelChangeByStaff(D5UrlUtils.LOGIN_KEY);
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag("ChangeByStaff")          // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.NO_CACHE)   // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            analyzeChangeByStaffData(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void analyzeChangeByStaffData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data = json.getJSONObject("response");
            if (data.getBoolean("success")) {
                mfalg = true;
                mSettingTitleImgClick.setVisibility(View.GONE);
                mSettingTitleTvAutoOn.setText("已全部开启");
            }else {
                JSONObject error = data.getJSONObject("error");
                String message = error.getString("message");
                ToastUtils.showShort(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        App.activityCount = 0;
        OkHttpUtils.getInstance().cancelTag("ChangeByStaff");
    }
}
