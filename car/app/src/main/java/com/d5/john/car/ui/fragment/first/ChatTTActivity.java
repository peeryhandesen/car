package com.d5.john.car.ui.fragment.first;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d5.john.car.App;
import com.d5.john.car.MainActivity;
import com.d5.john.car.R;
import com.d5.john.car.adapter.ChatAdapterFirst;
import com.d5.john.car.adapter.OnItemTouchListener;
import com.d5.john.car.beans.ChatMessageInfo;
import com.d5.john.car.emoji.SelectFaceHelper;
import com.d5.john.car.listener.MyItemClickListener;
import com.d5.john.car.listener.MyItemLongClickListener;
import com.d5.john.car.log.Logger;
import com.d5.john.car.photoscan.PhotoviewActivity;
import com.d5.john.car.ui.login.LoginActivity;
import com.d5.john.car.ui.tools.ImageTool;
import com.d5.john.car.ui.tools.MessageBitmapCache;
import com.d5.john.car.ui.view.PasteEditText;
import com.d5.john.car.util.CalendarUtils;
import com.d5.john.car.util.CommonUtil;
import com.d5.john.car.util.ToastUtils;
import com.d5.john.car.util.sign.D5UrlUtils;
import com.d5.john.car.wx.MyItemAnimator;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.BitmapCallback;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.fragmentation.SupportActivity;
import okhttp3.Call;
import okhttp3.Response;

import static com.d5.john.car.App.maxImgCount;

/**
 * 聊天界面
 * Created by idisfkj on 16/4/25.
 * Email : idisfkj@qq.com.
 */
public class ChatTTActivity extends SupportActivity implements View.OnTouchListener, View.OnFocusChangeListener, SwipeRefreshLayout.OnRefreshListener, MyItemClickListener, MyItemLongClickListener {

    @Bind(R.id.chat_view)
    RecyclerView chatView;
    @Bind(R.id.chat_tv_title)
    TextView mChatTvTitle;
    @Bind(R.id.chat_img_autoreplyon)
    ImageView mChatImgAutoreplyon;
    @Bind(R.id.chat_img_autoreplyoff)
    ImageView mChatImgAutoreplyoff;
    @Bind(R.id.btn_set_mode_voice)
    Button mBtnSetModeVoice;
    @Bind(R.id.btn_set_mode_keyboard)
    Button mBtnSetModeKeyboard;
    @Bind(R.id.et_sendmessage)
    PasteEditText mEtSendmessage;
    @Bind(R.id.iv_emoticons_normal)
    ImageView mIvEmoticonsNormal;
    @Bind(R.id.btn_more)
    Button mBtnMore;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    @Bind(R.id.edittext_layout)
    RelativeLayout mEdittextLayout;
    @Bind(R.id.btn_set_emoji_keyboard)
    Button mBtnSetEmojiKeyboard;
    @Bind(R.id.view_photo)
    LinearLayout mViewPhoto;
    @Bind(R.id.view_camera)
    LinearLayout mViewCamera;
    @Bind(R.id.view_location)
    LinearLayout mViewLocation;
    @Bind(R.id.custom_botton)
    LinearLayout mCustomBotton;
    @Bind(R.id.view_file)
    LinearLayout mViewFile;
    @Bind(R.id.view_audio)
    LinearLayout mViewAudio;
    @Bind(R.id.view_video)
    LinearLayout mViewVideo;
    private static final String ACTION_FILTER = "com.d5.john.car.chat";
    public static final String COPY_IMAGE = "EASEMOBIMG";
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private static final String TAG = ChatTTActivity.class.getSimpleName();
    public static final String PHOTO_POSITION = "photo_position";
    public static final String PHOTO_URLS = "photo_urls";
    public static final String PHOTO_IMAGEID = "photo_imageid";
    private static Logger logger = Logger.getLogger(ChatTTActivity.class);
    private String EARLIST_DATE = null;  //earlistDate
    private int mAdd_Date_times = 0;
    private String message = null;
    private String mChatContent;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private LinearLayout btnContainer;
    private View addFaceToolView;
    private View buttonPressToSpeak;
    private View more;
    private LinearLayout sendToolLayout;
    private ChatAdapterFirst mChatAdapter;
    private SelectFaceHelper mFaceHelper;
    private BroadcastReceiver receiver;
    private InputMethodManager manager;
    private String userName;
    private String custumId;
    private String userId;
    private int _id;
    private List<ChatMessageInfo> mChatMessageList = new ArrayList<>();
    private List<ChatMessageInfo> mAddMessageList = new ArrayList<>();
    private ArrayList<ImageItem> selImageList = new ArrayList<>(App.maxImgCount);//当前选择的所有图片
    private List<String> mCommunitURLLList = new ArrayList<>();  //保存当前用户交流的所有图片
    private int TIME = 5000;
    private int mScrollTotal;
    private Timer mTimer;
    private TimerTask mTimerTask = null;
    private Bitmap mHostPic;
    private boolean isVisbilityFace;
    private boolean isautoreply = true;
    private double similarityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_bottom_layout);
        ButterKnife.bind(this);
        App.activityCount = 2;
        Bundle bundle = getIntent().getExtras();
        getDataFromBundle(bundle);

        NotificationManager managerNot = (NotificationManager) App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerNot.cancel(_id);
        init();

        mChatTvTitle.setText(userName);

    }
    private void getDataFromBundle(Bundle bundle){
        try {
            _id = bundle.getInt("_id");
            userName = bundle.getString("_userName");
            String savePath = bundle.getString("_savePath");
            mHostPic = MessageBitmapCache.getInstance().get(savePath);
            custumId = bundle.getString("_custumId");
            userId = bundle.getString("_userId");
            String replyModel= bundle.getString("_replyModel");
            if("SYSTEM_REPLY".equals(replyModel)) {
                isautoreply =true;
            }else{
                isautoreply =false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        addFaceToolView = findViewById(R.id.add_tool);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        sendToolLayout = (LinearLayout) findViewById(R.id.send_tool_layout);
        more = findViewById(R.id.more);
        if(isautoreply) {
            mChatImgAutoreplyoff.setVisibility(View.GONE);
            mChatImgAutoreplyon.setVisibility(View.VISIBLE);
        }else {
            mChatImgAutoreplyoff.setVisibility(View.VISIBLE);
            mChatImgAutoreplyon.setVisibility(View.GONE);

        }
        mEtSendmessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollToBottomListItem();
                    mEdittextLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    hideKeyboard();
                    mEdittextLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });
        mEtSendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.setVisibility(View.GONE);
                mEdittextLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                mIvEmoticonsNormal.setVisibility(View.VISIBLE);
                mBtnSetEmojiKeyboard.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                addFaceToolView.setVisibility(View.GONE);
                scrollToBottomListItem();
            }
        });
        // 监听文字框
        mEtSendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnMore.setVisibility(View.GONE);
                    mBtnSend.setVisibility(View.VISIBLE);
                } else {
                    mBtnMore.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEtSendmessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isVisbilityFace = false;
                addFaceToolView.setVisibility(View.GONE);
                return false;
            }
        });
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        chatView.setLayoutManager(mLayoutManager);
        mChatAdapter = new ChatAdapterFirst(this);
        mChatAdapter.setHostBitmap(mHostPic);
        chatView.setHasFixedSize(true);
        ((SimpleItemAnimator) chatView.getItemAnimator()).setSupportsChangeAnimations(false);
        //设置Item增加、移除动画
        chatView.setItemAnimator(new MyItemAnimator());
        chatView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mScrollTotal + 1 > mChatAdapter.getItemCount()) {
                    mSwipeRefreshWidget.setRefreshing(true);
                    mSwipeRefreshWidget.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshWidget.setRefreshing(false);
                        }
                    }, 1000);
                    // 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
                    requestMessageLoadData(custumId, null);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollTotal = mLayoutManager.findLastVisibleItemPosition();
            }

        });
        chatView.addOnItemTouchListener(new OnItemTouchListener(chatView) {
            @Override
            public void onItemListener(RecyclerView.ViewHolder vh) {
                int position = vh.getLayoutPosition();
                ChatMessageInfo info = mChatMessageList.get(position);
                if ("IMAGE".equals(info.getName())) {
                    String url = info.getMessage();
                    if (url.contains("src=")) {
                        url = url.substring(url.indexOf("src=") + 5, url.indexOf("/>") - 1);
                    } else {
                        url = null;
                        url = D5UrlUtils.MAIN_IMAGE_ENGINE;
                        url += info.getMessageId();
                    }
                    if (!mCommunitURLLList.contains(url)) {
                        mCommunitURLLList.add(url);
                    } else {
                        int index = mCommunitURLLList.indexOf(url);
                        if (mCommunitURLLList.contains(url)) {
                            Intent intent = new Intent(ChatTTActivity.this, PhotoviewActivity.class);
                            intent.putExtra(PHOTO_POSITION, index);
                            intent.putExtra(PHOTO_IMAGEID, info.getFlag());
                            intent.putStringArrayListExtra(PHOTO_URLS, (ArrayList<String>) mCommunitURLLList);
                            startActivity(intent);
                        }
                    }
                }
            }
        });


        manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        receiver = new ChatBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FILTER);
        this.registerReceiver(receiver, filter);
        chatView.setAdapter(mChatAdapter);
        this.mChatAdapter.setOnItemClickListener(this);
        this.mChatAdapter.setOnItemLongClickListener(this);
        if ((mChatMessageList.size() > 0)&&(TextUtils.equals(mChatMessageList.get(0).getRegId(),D5UrlUtils.LOGIN_KEY))) {
            mChatAdapter.setDatas(mChatMessageList);
            scrollToBottomListItem();
        } else {
            mChatMessageList.clear();
            mCommunitURLLList.clear();
            requestMessageLoadData(custumId, null);
        }
        chatView.setOnTouchListener(this);
        /*startTimer();*/
        hideKeyboard();
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(mEtSendmessage.getWindowToken(), 0);
              /*  manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/

        }
    }

    /**
     * 显示软键盘
     */
    private void showKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE) {
            if (getCurrentFocus() != null)
                manager.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
        }
    }

    // 隐藏软键盘
    public void hideInputManager(Context ct) {
        try {
            manager.hideSoftInputFromWindow(((Activity) ct)
                    .getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, "hideInputManager Catch error,skip it!", e);
        }
    }

    /**
     * @Description 滑动到列表底部
     */
    private void scrollToBottomListItem() {
        chatView.smoothScrollToPosition(mChatMessageList.size());
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        mEdittextLayout.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        mBtnSetModeKeyboard.setVisibility(View.VISIBLE);
        mBtnSend.setVisibility(View.GONE);
        mBtnMore.setVisibility(View.VISIBLE);
        mIvEmoticonsNormal.setVisibility(View.VISIBLE);
        mBtnSetEmojiKeyboard.setVisibility(View.INVISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        addFaceToolView.setVisibility(View.GONE);
        btnContainer.setVisibility(View.GONE);
    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void onClickMore(View view) {
        if (more.getVisibility() == View.GONE) {
            System.out.println("more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            addFaceToolView.setVisibility(View.GONE);
            scrollToBottomListItem();
        } else {
            if (addFaceToolView.getVisibility() == View.VISIBLE) {
                addFaceToolView.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                mIvEmoticonsNormal.setVisibility(View.VISIBLE);
                mBtnSetEmojiKeyboard.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        mEdittextLayout.setVisibility(View.VISIBLE);
        mEdittextLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
        view.setVisibility(View.GONE);
        mEtSendmessage.requestFocus();
        mIvEmoticonsNormal.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        mBtnSetModeVoice.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEtSendmessage.getText())) {
            mBtnMore.setVisibility(View.VISIBLE);
            mBtnSend.setVisibility(View.GONE);
        } else {
            mBtnMore.setVisibility(View.GONE);
            mBtnSend.setVisibility(View.VISIBLE);
        }
        showKeyboard();
        scrollToBottomListItem();
    }

    /**
     * 显示表情图片框
     *
     * @param view
     */
    public void setEmoji(View view) {
        switch (view.getId()) {
            case R.id.iv_emoticons_normal:
                // 点击显示表情框
                more.setVisibility(View.VISIBLE);
                view.setVisibility(View.INVISIBLE);
                mBtnSetEmojiKeyboard.setVisibility(View.VISIBLE);
                addFaceToolView.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);
                hideKeyboard();
                break;
            case R.id.btn_set_emoji_keyboard:
                view.setVisibility(View.INVISIBLE);
                mIvEmoticonsNormal.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);
                addFaceToolView.setVisibility(View.GONE);
                more.setVisibility(View.GONE);
                showKeyboard();
                scrollToBottomListItem();
                break;
        }
        if (null == mFaceHelper) {
            mFaceHelper = new SelectFaceHelper(ChatTTActivity.this, addFaceToolView);
            mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
        }
        if (isVisbilityFace) {
            isVisbilityFace = false;
            addFaceToolView.setVisibility(View.GONE);
        } else {
            isVisbilityFace = true;
            addFaceToolView.setVisibility(View.VISIBLE);
            hideInputManager(ChatTTActivity.this);
            scrollToBottomListItem();
        }
    }

    private void startTimer() {
        stopTimer();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                requestMessageLoadData(custumId, null);
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

    /**
     * Item click
     */
    @Override
    public void onItemClick(View view, int postion) {
        ChatMessageInfo info = mChatMessageList.get(postion);
        if (info != null) {
            if ("IMAGE".equals(info.getName())) {
                String url = info.getMessage();
                if (url.contains("src=")) {
                    url = url.substring(url.indexOf("src=") + 5, url.indexOf("/>") - 1);
                } else {
                    url = D5UrlUtils.MAIN_IMAGE_ENGINE;
                    url += info.getMessage().toString();
                }
                if (!mCommunitURLLList.contains(url)) {
                    mCommunitURLLList.add(url);
                } else {
                    int index = mCommunitURLLList.indexOf(url);
                    if (mCommunitURLLList.contains(url)) {
                        Intent intent = new Intent(ChatTTActivity.this, PhotoviewActivity.class);
                        intent.putExtra(PHOTO_POSITION, index);
                        intent.putExtra(PHOTO_IMAGEID, info.getFlag());
                        intent.putStringArrayListExtra(PHOTO_URLS, (ArrayList<String>) mCommunitURLLList);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int postion) {
        ChatMessageInfo bean = mChatMessageList.get(postion);
        if (bean != null) {

        }
    }


    private void requestMessageLoadData(final String custumId, final String earlistDate) {
        OkHttpUtils.get(D5UrlUtils.MessageRecordLoadUrl(D5UrlUtils.LOGIN_KEY, earlistDate, custumId))     // 请求方式和请求url
                .tag("ChatTTActivity")          // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .connTimeOut(3000)
                .cacheMode(CacheMode.DEFAULT)   // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            if (TextUtils.isEmpty(earlistDate)) {
                                analyzeMessageLoadData(s);
                            } else {
                                analyzeAddMessageLoadData(s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void analyzeAddMessageLoadData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data = json.getJSONObject("response");
            if (data.getBoolean("success")) {
                JSONArray messageRecordList = data.getJSONArray("messageRecordList");
                String custumId = data.getString("customerId");
                EARLIST_DATE = data.getString("earlistDate");
                mAdd_Date_times += 1;
                int number = messageRecordList.length();
                mAddMessageList.clear();
                ChatMessageInfo chatMesg = null;
                for (int i = 0; i < number; i++) {
                    JSONObject MessageRecordInfo = messageRecordList.getJSONObject(i);
                    chatMesg = new ChatMessageInfo();
                    chatMesg.setTime(MessageRecordInfo.getString("gmtMessageCreate"));
                    String messageContent = MessageRecordInfo.getString("messageContent");
                    if (messageContent.contains("<p>")) {
                        messageContent = messageContent.substring(messageContent.indexOf(">") + 1, messageContent.lastIndexOf("<"));
                    }
                    chatMesg.setMessage(messageContent);
                    try {
                        chatMesg.setReceiverNumber(MessageRecordInfo.getString("receiverId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        chatMesg.setReceiverNumber("000000000");
                    }
                    chatMesg.setSendNumber(MessageRecordInfo.getString("senderId"));
                    chatMesg.setReaded(MessageRecordInfo.getBoolean("readed"));
                    chatMesg.setMessageId(MessageRecordInfo.getLong("messageId"));
                    JSONObject messageType = MessageRecordInfo.getJSONObject("messageType");
                    String name = messageType.getString("name");
                    chatMesg.setName(name);
                    if ("IMAGE".equals(name)) {
                        JSONObject properties = MessageRecordInfo.getJSONObject("properties");
                        chatMesg.setHeight(Integer.parseInt(properties.getString("height")));
                        chatMesg.setWidth(Integer.parseInt(properties.getString("width")));
                    }
                    chatMesg.setRegId(D5UrlUtils.LOGIN_KEY);
                    chatMesg.setCustumId(custumId);
                    int flag = 1;
                    if (userId.equals(chatMesg.getSendNumber())) {
                        flag = 0;
                    }
                    chatMesg.setFlag(flag);
                    mAddMessageList.add(chatMesg);
                }
                /*把查询到的前面的记录加到上次查询得到的记录的前面*/
                /*mChatMessageList -》当前用户的最实时的聊天记录*/
                for (int i = 0; i < mAddMessageList.size(); i++) {
                    ChatMessageInfo info = mAddMessageList.get(i);
                    if (!mChatMessageList.contains(info)) {
                        mChatMessageList.add(i, info);
                    }
                }
                mChatAdapter.setDatas(mChatMessageList);
                /*跳到最早的一条记录上面*/
                chatView.smoothScrollToPosition(0);
            } else {
                JSONObject error = data.getJSONObject("error");
                String message = error.getString("message");
                ToastUtils.showShort(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void analyzeMessageLoadData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data = json.getJSONObject("response");
            if (data.getBoolean("success")) {
                JSONArray messageRecordList = data.getJSONArray("messageRecordList");
                JSONObject matchRateMap = data.getJSONObject("messageIdAndMatchRateMap");
                similarityValue = data.getDouble("similarityValue");
                String custumId = data.getString("customerId");
                if ((TextUtils.isEmpty(EARLIST_DATE)) && (mAdd_Date_times == 0)) {
                    EARLIST_DATE = data.getString("earlistDate");
                }
                int number = messageRecordList.length();
                mAddMessageList.clear();
                ChatMessageInfo chatMesg = null;
                Long messageId;
                double rateMap;
                for (int i = 0; i < number; i++) {
                    JSONObject MessageRecordInfo = messageRecordList.getJSONObject(i);
                    chatMesg = new ChatMessageInfo();
                    chatMesg.setTime(MessageRecordInfo.getString("gmtMessageCreate"));
                    String messageContent = MessageRecordInfo.getString("messageContent");
                    if (messageContent.contains("<p>")) {
                        messageContent = messageContent.substring(messageContent.indexOf(">") + 1, messageContent.lastIndexOf("<"));
                    }
                    chatMesg.setMessage(messageContent);
                    try {
                        chatMesg.setReceiverNumber(MessageRecordInfo.getString("receiverId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        chatMesg.setReceiverNumber("000000000");
                    }
                    chatMesg.setSendNumber(MessageRecordInfo.getString("senderId"));
                    chatMesg.setReaded(MessageRecordInfo.getBoolean("readed"));
                    messageId = MessageRecordInfo.getLong("messageId");
                    chatMesg.setMessageId(messageId);
                    try {
                        rateMap = matchRateMap.getDouble(messageId.toString());
                        chatMesg.setMatchRate(rateMap);
                        if(rateMap > similarityValue){
                            chatMesg.setRed(true);
                        }else{
                            chatMesg.setRed(false);
                        }
                    } catch (JSONException e) {
                        chatMesg.setMatchRate(-1.0);
                        /*e.printStackTrace();*/
                    }
                    JSONObject messageType = MessageRecordInfo.getJSONObject("messageType");
                    String name = messageType.getString("name");
                    chatMesg.setName(name);
                    if ("IMAGE".equals(name)) {
                        JSONObject properties = MessageRecordInfo.getJSONObject("properties");
                        chatMesg.setHeight(Integer.parseInt(properties.getString("height")));
                        chatMesg.setWidth(Integer.parseInt(properties.getString("width")));
                        String url = messageContent.substring(messageContent.indexOf("src=") + 5, messageContent.indexOf("/>") - 1);
                        add2URLList(url);
                    }
                    chatMesg.setRegId(D5UrlUtils.LOGIN_KEY);
                    chatMesg.setCustumId(custumId);
                    int flag = 1;
                    if (userId.equals(chatMesg.getSendNumber())) {
                        flag = 0;
                    }
                    chatMesg.setFlag(flag);
                    mAddMessageList.add(chatMesg);
                }
                if (TextUtils.isEmpty(message) || (!TextUtils.equals(message, chatMesg.getTime()))) {
                    message = chatMesg.getTime();
                    if (mAdd_Date_times == 0) {
                        if (!containsAll(mChatMessageList, mAddMessageList)) {
                            /*第一次请求数据时的消息，mChatMessageList.size为0*/
                            for (ChatMessageInfo info : mAddMessageList) {
                                if (!containsItems(mChatMessageList, info)) {
                                    mChatMessageList.add(info);
                                }
                            }
                            //sortMessageByLastChatTime(mChatMessageList);
                            scrollToBottomListItem();
                            mChatAdapter.setDatas(mChatMessageList);
                        }
                    } else {
                        if (!containsAll(mChatMessageList, mAddMessageList)) {
                            /*第一次请求数据时的消息，mChatMessageList.size为0*/
                            for (ChatMessageInfo info : mAddMessageList) {
                                if (!containsItems(mChatMessageList, info)) {
                                    mChatMessageList.add(info);
                                }
                            }
                            //sortMessageByLastChatTime(mChatMessageList);
                            scrollToBottomListItem();
                            mChatAdapter.setDatas(mChatMessageList);
                        }
                    }
                    //saveMessageData2DateBase(mChatMessageList);
                }
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
        Intent intent = new Intent(ChatTTActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
        this.overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
    }

    private void add2URLList(String strUrl) {
        if (!mCommunitURLLList.contains(strUrl)) {
            mCommunitURLLList.add(strUrl);
            getBitmapData(strUrl);
        }
    }

    private void getBitmapData(final String url) {
        if (url != null) {
            final String smallImagePath = CommonUtil.getMd5Path(url, App.FILE_SAVE_TYPE_IMAGE);
            OkHttpUtils.get(url)//
                    .cacheMode(CacheMode.DEFAULT)
                    .execute(new BitmapCallback() {
                        @Override
                        public void onSuccess(Bitmap bitmap, Call call, Response response) {
                            // bitmap 即为返回的图片数据
                            Bitmap bitmapSmall2 = ImageTool.ratio(bitmap, 240, 180);
                            if (bitmapSmall2 != null) {
                                MessageBitmapCache.getInstance().put(smallImagePath, bitmapSmall2);
                            } else {
                                MessageBitmapCache.getInstance().put(smallImagePath, bitmap);
                            }
                        }
                    });
        }
    }

    private Boolean containsAll(List<ChatMessageInfo> intfo1, List<ChatMessageInfo> intfo2) {
        if ((intfo1.size() != 0) && (intfo2.size() != 0)) {
            if (intfo1.get(intfo1.size() - 1).getMessageId() == intfo2.get(intfo2.size() - 1).getMessageId()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private Boolean containsItems(List<ChatMessageInfo> intfo1, ChatMessageInfo item) {
        for (ChatMessageInfo data : intfo1) {
            if (data.getMessageId() == item.getMessageId()) {
                return true;
            }
        }
        return false;
    }

    private void sortMessageByLastChatTime(List<ChatMessageInfo> conversationList) {
        Collections.sort(conversationList, new Comparator<ChatMessageInfo>() {
            @Override
            public int compare(final ChatMessageInfo con1, final ChatMessageInfo con2) {
                Long con2LastMessage = CalendarUtils.getLongTime(con2.getTime());
                Long con1LastMessage = CalendarUtils.getLongTime(con1.getTime());
                if (con2LastMessage == con1LastMessage) {
                    return 0;
                } else if (con2LastMessage > con1LastMessage) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        // 获取根布局的可视区域r
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        // 本来的实际底部距离 - 可视的底部距离
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isKeyboardShown(mEtSendmessage.getRootView()))
            manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
       /* chatLine.setBackgroundColor(getResources().getColor(R.color.tab_color_s));*/
    }

    SelectFaceHelper.OnFaceOprateListener mOnFaceOprateListener = new SelectFaceHelper.OnFaceOprateListener() {
        @Override
        public void onFaceSelected(SpannableString spanEmojiStr) {
            if (null != spanEmojiStr) {
                mEtSendmessage.append(spanEmojiStr);
            }
        }

        @Override
        public void onFaceDeleted() {
            int selection = mEtSendmessage.getSelectionStart();
            String text = mEtSendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    mEtSendmessage.getText().delete(start, end);
                    return;
                }
                mEtSendmessage.getText().delete(selection - 1, selection);
            }
        }

    };

    public void onBackPressed() {
        if (isVisbilityFace) {// 好吧,隐藏表情菜单再退出
            isVisbilityFace = false;
            addFaceToolView.setVisibility(View.GONE);
            return;
        }
        finish();

    }

    private void requestModelChangeByCustomerData(String replyModel) {
        String url = D5UrlUtils.replyModelChangeByCustomerUrl(D5UrlUtils.LOGIN_KEY, custumId,replyModel);
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag("ModelChangeByCustomer")          // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.NO_CACHE)   // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            analyzeModelChangeByCustomerData(s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void analyzeModelChangeByCustomerData(String resD) {
        try {
            JSONObject json = new JSONObject(resD);
            JSONObject data = json.getJSONObject("response");
            if (data.getBoolean("success")) {
                 if(!isautoreply){
                     //点击了自动回复的按钮
                     mChatImgAutoreplyoff.setVisibility(View.GONE);
                     mChatImgAutoreplyon.setVisibility(View.VISIBLE);
                     isautoreply = true;
                 }else{
                     //点击了人工回复
                     mChatImgAutoreplyoff.setVisibility(View.VISIBLE);
                     mChatImgAutoreplyon.setVisibility(View.GONE);
                     isautoreply = false;
                 }
            }else {
              /*  JSONObject error = data.getJSONObject("error");
                String message = error.getString("message");
                ToastUtils.showShort(message);*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ;

    @OnClick({R.id.chat_img_back, R.id.chat_img_autoreplyon, R.id.chat_img_autoreplyoff, R.id.btn_send, R.id.view_photo, R.id.view_audio, R.id.view_camera, R.id.view_file, R.id.view_location, R.id.view_video})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_img_back:
                Intent intentMain = new Intent(ChatTTActivity.this, MainActivity.class);
                startActivity(intentMain);
                overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
                break;
            case R.id.chat_img_autoreplyoff:
                requestModelChangeByCustomerData("SYSTEM_REPLY");
             /*   mChatImgAutoreplyoff.setVisibility(View.GONE);
                mChatImgAutoreplyon.setVisibility(View.VISIBLE);
                isautoreply = true;*/
                break;
            case R.id.chat_img_autoreplyon:
                requestModelChangeByCustomerData("ARTIFICIAL_REPLY");
               /* mChatImgAutoreplyoff.setVisibility(View.VISIBLE);
                mChatImgAutoreplyon.setVisibility(View.GONE);
                isautoreply = false;*/
                break;
            case R.id.btn_send:
                mChatContent = mEtSendmessage.getText().toString();
                if (mChatContent.equals("")) {
                    return;
                }
                String name = "TEXT";
                if (mChatContent != null) {
                    sendData2Servers(custumId, mChatContent, name, "");
                }
                mEtSendmessage.setText("");
                break;
            case R.id.view_photo:
              /*  Intent intentWX = new Intent(this, WxDemoActivity.class);
                startActivity(intentWX);*/
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount);
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.view_audio:
                break;
            case R.id.view_camera:
                break;
            case R.id.view_file:
                break;
            case R.id.view_location:
                break;
            case R.id.view_video:
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                selImageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                logger.i("pic#selImageList:%s", selImageList);
                for (int i = 0; i < selImageList.size(); i++) {
                    ImageItem item = selImageList.get(i);
                    if (item.size != 0) {
                        Bitmap bmpBig = ImageTool.ratio(item.path, item.width, item.height);
                        String smallpath = CommonUtil.getMd5Path(item.path, App.FILE_SAVE_TYPE_IMAGE);
                        logger.i("pic#bmpBig's size:%s", ImageTool.getBitmapSize(bmpBig));
                        String imageCont = ImageTool.bitmap2String(bmpBig);
                        logger.i("pic#imageCont:%s", imageCont.length());
                        Bitmap bmp = ImageTool.createScaleBitmap(bmpBig, 200, 180, true);
                        logger.i("pic#bmp's size:%s", ImageTool.getBitmapSize(bmp));
                        if (bmp != null) {
                            MessageBitmapCache.getInstance().put(smallpath, bmp);
                        }
                        if (imageCont != null) {
                            sendData2Servers(custumId, imageCont, "IMAGE", item.path);
                        }
                    }
                }
                selImageList.clear();
                /*adapter.setImages(selImageList);*/
            }
        }
    }

    private void sendData2Servers(final String customId, final String chatContent, final String name, final String path) {
        final String url = D5UrlUtils.MessageSendPictureUrl(null, customId, chatContent, name);
        logger.i("OkHttp#url:%s", url);
         new Thread() {
            public void run() {
                try {
                    OkHttpUtils.post(url)     // 请求方式和请求url
                            .params("content", chatContent)
                            .tag("RequestData")          // 请求的 tag, 主要用于取消对应的请求
                            .cacheKey("cacheKey")          // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                            .connTimeOut(3000)
                            .cacheMode(CacheMode.DEFAULT)   // 缓存模式，详细请看缓存介绍
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    try {
                                        JSONObject json = new JSONObject(s);
                                        JSONObject data = json.getJSONObject("response");
                                        if (data.getBoolean("success")) {
                                            Long messageId = data.getLong("messageId");
                                            String nowDate = data.getString("nowDate");
                                            ChatMessageInfo mChatMessageInfo = null;
                                            if ("TEXT".equals(name)) {
                                                mChatMessageInfo = new ChatMessageInfo(chatContent, 1, nowDate,
                                                        userId, D5UrlUtils.LOGIN_KEY, D5UrlUtils.AUTHED_USERID, customId, name, messageId, true);
                                            } else {
                                                mChatMessageInfo = new ChatMessageInfo(path, 1, nowDate,
                                                        userId, D5UrlUtils.LOGIN_KEY, D5UrlUtils.AUTHED_USERID, customId, name, messageId, true);
                                            }
                                            try {
                                                mChatMessageList.add(mChatMessageList.size(), mChatMessageInfo);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            //CommonData.getInstance().setMessageList(mChatMessageList);
                                            mChatAdapter.addData(mChatMessageList, mChatMessageList.size());
                                            scrollToBottomListItem();
                                        } else {
                                            JSONObject error = data.getJSONObject("error");
                                            String meassge = error.getString("meassge");
                                            ToastUtils.showShort(meassge);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        logger.i("OkHttp#e:%s", e);
                                    }

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.i("OkHttp#e:%s", e);
                }
            }
        }.start();

    }

    @Override
    public void onRefresh() {
        mSwipeRefreshWidget.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTimer();
                mSwipeRefreshWidget.setRefreshing(false);
            }
        }, 1500);
        stopTimer();
        requestMessageLoadData(custumId, EARLIST_DATE);
    }

    private class ChatBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*mChatPresenter.receiveData(intent, chatHelper);*/
        }
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        System.exit(0);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.finish();
            this.overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_right_out);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sendToolLayout.setVisibility(View.VISIBLE);
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.activityCount = 0;
        hideKeyboard();
        stopTimer();
        sendToolLayout.setVisibility(View.GONE);
        OkHttpUtils.getInstance().cancelTag("ChatTTActivity");
        OkHttpUtils.getInstance().cancelTag("RequestData");
        OkHttpUtils.getInstance().cancelTag("ModelChangeByCustomer");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //更新数据
       /* mChatPresenter.updateLasterContent(this, App.mRegId, App.mNumber);*/
        //重置数据
        stopTimer();
        mChatMessageList.clear();
        mAddMessageList.clear();
        selImageList.clear();
        mCommunitURLLList.clear();
        hideKeyboard();
        //CommonData.getInstance().cleanMessageData();
        chatView.setAdapter(null);
        this.unregisterReceiver(receiver);
        ButterKnife.unbind(this);
        OkHttpUtils.getInstance().cancelTag("ChatTTActivity");
        OkHttpUtils.getInstance().cancelTag("RequestData");
        OkHttpUtils.getInstance().cancelTag("ModelChangeByCustomer");
        /*android.os.Process.killProcess(android.os.Process.myPid());*/
    }
}
