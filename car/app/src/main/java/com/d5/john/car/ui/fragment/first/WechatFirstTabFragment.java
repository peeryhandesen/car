package com.d5.john.car.ui.fragment.first;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d5.john.car.App;
import com.d5.john.car.R;
import com.d5.john.car.adapter.OnItemTouchListener;
import com.d5.john.car.adapter.WXAdapterFirst;
import com.d5.john.car.base.BaseLazyMainFragment;
import com.d5.john.car.beans.WXItemInfo;
import com.d5.john.car.ui.login.LoginActivity;
import com.d5.john.car.ui.set.SettingActivity;
import com.d5.john.car.util.CommonUtil;
import com.d5.john.car.util.ToastUtils;
import com.d5.john.car.util.sign.D5UrlUtils;
import com.d5.john.car.wx.WXItemDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by YoKeyword on 16/6/30.
 */
public class WechatFirstTabFragment extends BaseLazyMainFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.wx_recyclerView)
    RecyclerView wxRecyclerView;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    private SwipeRefreshLayout mRefreshLayout;
    private List<WXItemInfo> mList = new ArrayList<>();
    private WXItemInfo wxItemInfo;
    private WXAdapterFirst wxAdapter;
    private int TIME = 5000;
    private boolean mInAtTop = true;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean systemOrArtificial = true;
    private Thread mThreadSetting,mThreadWxItem;

    public static WechatFirstTabFragment newInstance() {

        Bundle args = new Bundle();

        WechatFirstTabFragment fragment = new WechatFirstTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wechat_fragment_tab_first2, container, false);
        ButterKnife.bind(this, view);
        /*setTitleText(mTvTitle, "消息");*/
        setTitleText(mTvTitle, "消息",mTvRight,R.mipmap.bg_message_setting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingActivity();
            }
        });
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        return view;
    }

    private void startSettingActivity(){
        Intent set = new Intent(getActivity(),SettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag",systemOrArtificial);
        set.putExtras(bundle);
        startActivity(set);
    }
    @Override
    public void onResume() {
        super.onResume();
        startTimer();
        getInitSettingData();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        /*mList.clear();*/
        OkHttpUtils.getInstance().cancelTag("WechatFirstTabFragment");
        OkHttpUtils.getInstance().cancelTag("WXAdapterFirst");
        OkHttpUtils.getInstance().cancelTag("SetFragmentInit");
    }

    private void getInitSettingData(){
        if((mThreadSetting == null) || (!mThreadSetting.isAlive())){
            mThreadSetting = new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(1000); //休眠3秒
                        requestSetFragmentInitData();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                };
            };
        }
        mThreadSetting.start();
    }

    private void startTimer() {
        stopTimer();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                requestWXItemData();
            }
        };
        mTimer.schedule(mTimerTask, 0, TIME);//第三个参数代表我们之前定义的休眠的1秒，即我们定义的重复周期
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void requestSetFragmentInitData() {
        String url = D5UrlUtils.installationInitializeUrl(D5UrlUtils.LOGIN_KEY);
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag("SetFragmentInit")          // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.NO_CACHE)   // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            analyzeSetFragmentInitData(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void analyzeSetFragmentInitData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data = json.getJSONObject("response");
            systemOrArtificial = data.getBoolean("systemOrArtificial");
            /*if (data.getBoolean("success")) {
                systemOrArtificial = data.getBoolean("systemOrArtificial");
            }else {
                JSONObject error = data.getJSONObject("error");
                String message = error.getString("message");
                ToastUtils.showShort(message);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestWXItemData() {
        String url = D5UrlUtils.ChatCustomerListUrl(D5UrlUtils.LOGIN_KEY);
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag("WechatFirstTabFragment")          // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .connTimeOut(3000)
                .cacheMode(CacheMode.DEFAULT)   // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            analyzeWXItemData(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void analyzeWXItemData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data = json.getJSONObject("response");
            if (data.getBoolean("success")) {
                JSONObject unreadCountObj = data.getJSONObject("customerIdAndUnreadCountMap");
                JSONObject userInfo = data.getJSONObject("customerIdAndUserInfoMap");
                JSONArray customers = data.getJSONArray("customers");
                JSONObject unreadAlarmCountObj = data.getJSONObject("customerIdAndUnreadAlarmCountMap");
                JSONObject replyModelObj = data.getJSONObject("customerIdAndReplyModelMap");
                int number = customers.length();
                int[] unreadCount = new int[number];
                int[] unreadAlarmCount = new int[number];
                String[] nickName = new String[number];
                String[] replyModel = new String[number];
                String[] userId = new String[number];
                String[] id = new String[number];
                String[] logoUrl = new String[number];
                for (int i = 0; i < number; i++) {
                    JSONObject cust = customers.getJSONObject(i);
                    try {
                        logoUrl[i] = cust.getString("logoUrl");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    id[i] = cust.getString("id");
                    unreadCount[i] = unreadCountObj.getInt(id[i]);
                    unreadAlarmCount[i] = unreadAlarmCountObj.getInt(id[i]);
                    JSONObject model = replyModelObj.getJSONObject(id[i]);
                    replyModel[i] = model.getString("name");
                    JSONObject user = userInfo.getJSONObject(id[i]);
                    nickName[i] = user.getString("nickName");
                    userId[i] = user.getString("userId");
                }
                String[] gmtMessageCreate = new String[number];
                String[] messageContent = new String[number];
                String[] name = new String[number];
                String mesCont = null;
                JSONObject uRecent = data.getJSONObject("userIdAndRecentMessageMap");
                for (int i = 0; i < number; i++) {
                    JSONObject recentMesg = uRecent.getJSONObject(userId[i].toString());
                    gmtMessageCreate[i] = recentMesg.getString("gmtMessageCreate");
                    mesCont = recentMesg.getString("messageContent");
                    if (mesCont.contains("<p>")) {
                        mesCont = mesCont.substring(mesCont.indexOf(">") + 1, mesCont.lastIndexOf("<"));
                    }
                    messageContent[i] = mesCont;
                    JSONObject messageType = recentMesg.getJSONObject("messageType");
                    name[i] = messageType.getString("name");
                }
                mList.clear();
                for (int i = 0; i < number; i++) {
                    wxItemInfo = new WXItemInfo();
                    wxItemInfo.setTitle(nickName[i]);
                    wxItemInfo.setRegId(D5UrlUtils.LOGIN_KEY);
                    wxItemInfo.setNumber(D5UrlUtils.AUTHED_USERID);
                    if (name[i].equals("IMAGE")) {
                        wxItemInfo.setContent("图片");
                    } else {
                        wxItemInfo.setContent(messageContent[i]);
                    }
                    wxItemInfo.setTime(gmtMessageCreate[i]);
                    wxItemInfo.setUserId(userId[i]);
                    wxItemInfo.setCustumId(id[i]);
                    wxItemInfo.setUnReadNum(unreadCount[i]);
                    wxItemInfo.setAlarmCountNum(unreadAlarmCount[i]);
                    wxItemInfo.setReplyModel(replyModel[i]);
                    wxItemInfo.setCurrentAccount(id[i]);
                    wxItemInfo.setPictureUrl(logoUrl[i]);
                    wxItemInfo.setUrl(logoUrl[i]);
                    String smallImagePath = CommonUtil.getMd5Path(logoUrl[i], App.FILE_SAVE_TYPE_IMAGE);
                    wxItemInfo.setSavePath(smallImagePath);
                    //mHelper.insert(wxItemInfo);
                    mList.add(wxItemInfo);
                }
                wxAdapter.setDatas(mList);
            } else {
                JSONObject error = data.getJSONObject("error");
                String message = error.getString("message");
                ToastUtils.showShort(message + "\n" +
                        "请返回登入界面重新登入！");
                try {
                    String name = error.getString("name");
                    if("USER_LOGIN_EXPIRED".equals(name)){
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 *要执行的操作
                                 */
                                startLoginActivity();
                            }
                        }, 3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
    }

    @Override
    protected void initLazyView(@Nullable Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);
        wxAdapter = new WXAdapterFirst(getContext());

        wxRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        wxRecyclerView.setHasFixedSize(true);
        final int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
        wxRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, space);
            }
        });
        if ((mList.size() > 0)  && (TextUtils.equals(mList.get(0).getRegId(),D5UrlUtils.LOGIN_KEY))) {
            wxAdapter.setDatas(mList);
        } else {
            mList.clear();
            getInitWXItemData();
        }
        wxRecyclerView.setAdapter(wxAdapter);

        wxRecyclerView.addItemDecoration(new WXItemDecoration(getContext()));
        wxRecyclerView.addOnItemTouchListener(new OnItemTouchListener(wxRecyclerView) {
            @Override
            public void onItemListener(RecyclerView.ViewHolder vh) {
                Intent intent = new Intent(getActivity(), ChatTTActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("_id", vh.getLayoutPosition());
                try {
                    WXItemInfo wxIfo = mList.get(vh.getLayoutPosition());
                    bundle.putString("_custumId", wxIfo.getCustumId());
                    bundle.putString("_userId", wxIfo.getUserId());
                    bundle.putString("_userName", wxIfo.getTitle());
                    bundle.putString("_savePath", wxIfo.getSavePath());
                    bundle.putString("_replyModel", wxIfo.getReplyModel());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    private void getInitWXItemData(){
        if((mThreadWxItem == null)|| (!mThreadWxItem.isAlive())) {
            mThreadWxItem = new Thread() {
                public void run() {
                    try {
                        requestWXItemData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        mThreadWxItem.start();
    }

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
        wxRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wxRecyclerView.setAdapter(null);
        mList.clear();
     /*   mThreadSetting.destroy();
        mThreadWxItem.destroy();*/
        ButterKnife.unbind(this);
        stopTimer();
        OkHttpUtils.getInstance().cancelTag("WechatFirstTabFragment");
        OkHttpUtils.getInstance().cancelTag("WXAdapterFirst");
        OkHttpUtils.getInstance().cancelTag("SetFragmentInit");

    }
}
