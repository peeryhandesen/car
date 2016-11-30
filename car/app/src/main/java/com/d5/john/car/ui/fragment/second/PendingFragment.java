package com.d5.john.car.ui.fragment.second;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.d5.john.car.R;
import com.d5.john.car.adapter.PendingAdapter;
import com.d5.john.car.base.BaseFragment;
import com.d5.john.car.custom.bean.PendingUser;
import com.d5.john.car.listener.OnItemClickListener;
import com.d5.john.car.util.ToastUtils;
import com.d5.john.car.util.sign.D5UrlUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
/**
 * Created by YoKeyword on 16/6/30.
 */
public class PendingFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecy;
    private PendingAdapter mAdapter;
    private List<PendingUser> items = new ArrayList<>();

    private boolean mInAtTop = true;
    private int mScrollTotal;
    private Thread mThreadList,mThreadSwitch;

    public static PendingFragment newInstance() {
        Bundle args = new Bundle();
        PendingFragment fragment = new PendingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wechat_fragment_tab_second_pager_first, container, false);
        initView(view);
        return view;
    }
    @Override
    protected void onEnterAnimationEnd(Bundle savedInstanceState) {
        //items = CommonData.getInstance().getPendingUserInfo();
        if((items.size() > 0) && (TextUtils.equals(items.get(0).getId(),D5UrlUtils.AUTHED_USERID))){
            mAdapter.setDatas(items);
        }else {
            items.clear();
            getSwitchCustomerListData(true);
        }
    }

    private void initView(View view) {

        mRecy = (RecyclerView) view.findViewById(R.id.recy);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter = new PendingAdapter(_mActivity);
        mRecy.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        mRecy.setLayoutManager(manager);
        mRecy.setAdapter(mAdapter);

        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollTotal += dy;
                if (mScrollTotal <= 0) {
                    mInAtTop = true;
                } else {
                    mInAtTop = false;
                }
            }
        });

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder holder) {
                // 通知MainActivity跳转至CycleFragment
                final String custumId = getCustumId(position);
                getInitCustomerSwitchInUrlData(custumId);
            }
        });
    }

    private void getInitCustomerSwitchInUrlData(final String custumId){
        if(mThreadSwitch == null) {
            mThreadSwitch = new Thread() {
                public void run() {
                    try {
                        requestCustomerSwitchInUrlData(custumId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        mThreadSwitch.start();
    }

    private void getSwitchCustomerListData(final boolean bo){
        if((mThreadList == null) ||(!mThreadList.isAlive())) {
            mThreadList = new Thread() {
                public void run() {
                    try {
                        if(bo) {
                            Thread.sleep(2500);
                            requestSwitchCustomerListData();
                        }else{
                            requestSwitchCustomerListData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        mThreadList.start();
    }

    private String getCustumId(int position){
        PendingUser user = items.get(position);
        String id = user.getId();
        return id;
    }

    private void requestCustomerSwitchInUrlData(String custumId){
        String url = D5UrlUtils.CustomerSwitchInUrl(D5UrlUtils.LOGIN_KEY,custumId);
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag("PendingFragmentCustomerSwitchIn")          // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .connTimeOut(3000)
                .cacheMode(CacheMode.DEFAULT)   // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            analyzeSwitchInData(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void analyzeSwitchInData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data =json.getJSONObject("response");
            Boolean success = data.getBoolean("success");
            if(success){
                getSwitchCustomerListData(false);
            }else{
                JSONObject error =data.getJSONObject("error");
                String message = error.getString("message");
                ToastUtils.showShort(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void requestSwitchCustomerListData(){
        String url = D5UrlUtils.WaitSwitchCustomerListUrl(D5UrlUtils.LOGIN_KEY);
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag("PendingFragment")          // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .connTimeOut(3000)
                .cacheMode(CacheMode.DEFAULT)   // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            analyzeData(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void analyzeData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data =json.getJSONObject("response");
            if(data.getBoolean("success")) {
                JSONArray customerStaffChats = data.getJSONArray("customerStaffChats");
                int number = customerStaffChats.length();
                if(number >0 ) {
                    JSONObject customerIdAndNickNameMap = data.getJSONObject("customerIdAndNickNameMap");
                    JSONObject customerIdAndLogoUrlMap = data.getJSONObject("customerIdAndLogoUrlMap");


                    String[] customerId = new String[number];
                    String[] gmtExpired = new String[number];
                    String[] name = new String[number];
                    String[] logoUrl = new String[number];
                    for (int i = 0; i < number; i++) {
                        JSONObject custom = customerStaffChats.getJSONObject(i);
                        customerId[i] = custom.getString("customerId");
                        gmtExpired[i] = custom.getString("gmtExpired");
                        JSONObject chatStatus = custom.getJSONObject("chatStatus");
                        name[i] = chatStatus.getString("name");
                    }
                    String[] nickName = new String[number];
                    for (int i = 0; i < number; i++) {
                        nickName[i] = customerIdAndNickNameMap.getString(customerId[i]);
                        try {
                            logoUrl[i] = customerIdAndLogoUrlMap.getString(customerId[i]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            logoUrl[i] ="";
                        }
                    }
                    items.clear();
                    for (int i = 0; i < number; i++) {
                        PendingUser user = new PendingUser();
                        user.setId(customerId[i]);
                        user.setUserName(nickName[i]);
                        user.setGmtExpired(gmtExpired[i]);
                        user.setHeadUrl(logoUrl[i]);
                        user.setTitleRF(name[i]);
                        user.setId(D5UrlUtils.AUTHED_USERID);
                        items.add(user);
                    }
                }else{
                    items.clear();
                }
                mAdapter.setDatas(items);
                //CommonData.getInstance().setPendingUserInfo(items);
            }else{
                JSONObject error =data.getJSONObject("error");
                String message = error.getString("message");
                ToastUtils.showShort(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

 /*   @Override
    public void onResume() {
        super.onResume();
        items.clear();
    }*/

    @Override
    public void onRefresh() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 2500);
    }

    private void scrollToTop() {
        mRecy.smoothScrollToPosition(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*items.clear();*/
        OkHttpUtils.getInstance().cancelTag("PendingFragment");
        OkHttpUtils.getInstance().cancelTag("PendingAdapter");
        OkHttpUtils.getInstance().cancelTag("PendingFragmentCustomerSwitchIn");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecy.setAdapter(null);
        items.clear();
       /* mThreadList.destroy();
        mThreadSwitch.destroy();*/
        OkHttpUtils.getInstance().cancelTag("PendingFragment");
        OkHttpUtils.getInstance().cancelTag("PendingAdapter");
        OkHttpUtils.getInstance().cancelTag("PendingFragmentCustomerSwitchIn");


    }
}
