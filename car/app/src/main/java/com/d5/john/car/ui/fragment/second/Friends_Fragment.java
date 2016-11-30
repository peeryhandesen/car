package com.d5.john.car.ui.fragment.second;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.d5.john.car.App;
import com.d5.john.car.R;
import com.d5.john.car.base.BaseFragment;
import com.d5.john.car.custom.adapter.ContactAdapter;
import com.d5.john.car.custom.bean.User;
import com.d5.john.car.custom.widght.SideBar;
import com.d5.john.car.ui.fragment.first.ChatTTActivity;
import com.d5.john.car.util.CommonUtil;
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


//我的客户
public class Friends_Fragment extends BaseFragment implements OnItemClickListener {
	private Activity ctx;
	private View layout;
	private ListView lvContact;
	private SideBar indexBar;
	private static Friends_Fragment mFragment;
	private TextView mDialogText;
	private WindowManager mWindowManager;
	public List<User> UserInfos = new ArrayList<User>();// 好友信息
	private Thread mThreadList;

	public static Friends_Fragment getInstance() {
		if(mFragment == null) {
			mFragment = new Friends_Fragment();
		}
		if(mFragment.getArguments() == null) {
			Bundle args = new Bundle();
			mFragment.setArguments(args);
		}
		return mFragment;
	}

	public static Friends_Fragment newInstance() {
		Friends_Fragment fragment = new Friends_Fragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		mFragment = fragment;
		return fragment;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (layout == null) {
			ctx = this.getActivity();
			layout = ctx.getLayoutInflater().inflate(R.layout.fragment_friends, null);
			mWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
			initViews();
		} else {
			ViewGroup parent = (ViewGroup) layout.getParent();
			if (parent != null) {
				parent.removeView(layout);
			}
		}
		return layout;
	}

/*	@Override
	public void onResume() {
		super.onResume();
		UserInfos.clear();
	}*/

	@Override
	protected void onEnterAnimationEnd(Bundle savedInstanceState) {
		//UserInfos = CommonData.getInstance().getUserInfos();
		if((UserInfos.size() > 0) && (TextUtils.equals(UserInfos.get(0).getId(),D5UrlUtils.AUTHED_USERID))){
			initData();
		}else {
			UserInfos.clear();
			getInitFriendsData();
		}
		setOnListener();
	}

	private void getInitFriendsData(){
		if((mThreadList == null)|| (!mThreadList.isAlive())) {
			mThreadList = new Thread() {
				public void run() {
					try {
						requestData();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}
		mThreadList.start();
	}

	private void requestData(){
		String url = D5UrlUtils.CustomerListUrl(D5UrlUtils.LOGIN_KEY);
		OkHttpUtils.get(url)     // 请求方式和请求url
				.tag("Friends_Fragment")          // 请求的 tag, 主要用于取消对应的请求
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
				JSONArray customers = data.getJSONArray("customers");
				JSONArray customerStaffChats= data.getJSONArray("customerStaffChats");
				JSONObject replyModelObj = data.getJSONObject("customerIdAndReplyModelMap");
				JSONObject customerIdAndUserInfoMap= data.getJSONObject("customerIdAndUserInfoMap");
				int number = customers.length();
				String[] ids = new String[number];
				String[] logUrl = new String[number];
				String[] name = new String[number];
				String[] userId = new String[number];
				String[] replyModel = new String[number];
				String[] nickName = new String[number];
				for(int i = 0;i < number;i++){
					JSONObject custm = customers.getJSONObject(i);
					ids[i] = custm.getString("id");
					JSONObject cumStaff = customerStaffChats.getJSONObject(i);
					JSONObject chatStatus = cumStaff.getJSONObject("chatStatus");
					name[i] = chatStatus.getString("name");
					try {
						JSONObject model = replyModelObj.getJSONObject(ids[i]);
						replyModel[i] = model.getString("name");
					} catch (JSONException e) {
						replyModel[i] = "SYSTEM_REPLY";
						e.printStackTrace();
					}
					JSONObject obj = customerIdAndUserInfoMap.getJSONObject(ids[i]);
					nickName[i] = obj.getString("nickName");
					userId[i] = obj.getString("userId");
					try {
						logUrl[i]= custm.getString("logoUrl");
					} catch (JSONException e) {
						e.printStackTrace();
						logUrl[i] = "";
					}
				}
				UserInfos.clear();
				for(int i = 0;i < number;i ++) {
					User user = new User();
					user.setCustumId(ids[i]);
					user.setUserId(userId[i]);
					user.setReplyModel(replyModel[i]);
					if(logUrl[i] != "") {
						String smallImagePath = CommonUtil.getMd5Path(logUrl[i], App.FILE_SAVE_TYPE_IMAGE);
						user.setHeadUrl(logUrl[i]);
						user.setSavePath(smallImagePath);
					}else{
						user.setHeadUrl(logUrl[i]);
						user.setSavePath(logUrl[i]);
					}
					user.setUserName(nickName[i]);
					user.setChatStatus(name[i]);
					user.setId(D5UrlUtils.AUTHED_USERID);
					UserInfos.add(user);
				}
				//CommonData.getInstance().setUserInfos(UserInfos);
				initData();
			}else{
				JSONObject error =data.getJSONObject("error");
				String message = error.getString("message");
				ToastUtils.showShort(message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

	private void initViews() {
		lvContact = (ListView) layout.findViewById(R.id.lvContact);
		mDialogText = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		indexBar = (SideBar) layout.findViewById(R.id.sideBar);
		indexBar.setListView(lvContact);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		try {
			mWindowManager.addView(mDialogText, lp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		indexBar.setTextView(mDialogText);
		lvContact.setOnItemClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mDialogText != null) {
			mWindowManager.removeView(mDialogText);
			mDialogText = null;
		}
		/*UserInfos.clear();*/
		OkHttpUtils.getInstance().cancelTag("Friends_Fragment");
		OkHttpUtils.getInstance().cancelTag("Friends_Fragment_getBitmapData");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mDialogText != null) {
			mWindowManager.removeView(mDialogText);
			mDialogText = null;
		}
		UserInfos.clear();
		/*mThreadList.destroy();*/
		OkHttpUtils.getInstance().cancelTag("Friends_Fragment");
		OkHttpUtils.getInstance().cancelTag("Friends_Fragment_getBitmapData");
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		initData();
	}

	private void initData() {
		lvContact.setAdapter(new ContactAdapter(getActivity(), UserInfos));
	}


	private void setOnListener() {
		lvContact.setOnItemClickListener(this);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(getActivity(), ChatTTActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("_id", arg2);
		try {
			User userIfo = UserInfos.get(arg2);
			bundle.putString("_userName", userIfo.getUserName());
			bundle.putString("_savePath", userIfo.getSavePath());
			bundle.putString("_custumId", userIfo.getCustumId());
			bundle.putString("_userId", userIfo.getUserId());
			bundle.putString("_replyModel", userIfo.getReplyModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		intent.putExtras(bundle);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

	}
}
