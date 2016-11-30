package com.d5.john.car.custom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.d5.john.car.R;
import com.d5.john.car.custom.bean.User;
import com.d5.john.car.custom.common.PingYinUtil;
import com.d5.john.car.custom.common.PinyinComparator;
import com.d5.john.car.custom.common.ViewHolder;
import com.d5.john.car.ui.tools.ImageTool;
import com.d5.john.car.ui.tools.MessageBitmapCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactAdapter extends BaseAdapter implements SectionIndexer {
	private Context mContext;
	private List<User> UserInfos = new ArrayList<>();// 好友信息

	public ContactAdapter(Context mContext, List<User> UserInfos) {
		this.mContext = mContext;
		this.UserInfos = UserInfos;
		// 排序(实现了中英文混排)
		Collections.sort(UserInfos, new PinyinComparator());
	}

	@Override
	public int getCount() {
		return UserInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return UserInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User user = UserInfos.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.contact_item, null);
		}
		LinearLayout contactitem_layout=ViewHolder.get(convertView,
				R.id.contactitem_layout);
		ImageView ivAvatar = ViewHolder.get(convertView,
				R.id.contactitem_avatar_iv);
		TextView tvCatalog = ViewHolder.get(convertView,
				R.id.contactitem_catalog);
		TextView tvNick = ViewHolder.get(convertView, R.id.contactitem_nick);
		TextView tvRightNick = ViewHolder.get(convertView, R.id.contactitem_right_nick);
		String catalog = PingYinUtil.converterToFirstSpell(user.getUserName());
		if("".equals(catalog)){
			catalog = "#";
		}else {
			catalog = catalog.substring(0, 1);
		}
		if (position == 0) {
			tvCatalog.setVisibility(View.VISIBLE);
			tvCatalog.setText(catalog);
		} else {
			User Nextuser = UserInfos.get(position - 1);
			String lastCatalog = PingYinUtil.converterToFirstSpell(Nextuser.getUserName());
			if("".equals(lastCatalog)){
				catalog = "#";
			}else {
				lastCatalog = lastCatalog.substring(0, 1);
			}
			if (catalog.equals(lastCatalog)) {
				tvCatalog.setVisibility(View.GONE);
			} else {
				tvCatalog.setVisibility(View.VISIBLE);
				tvCatalog.setText(catalog);
			}
		}
		if(user.getSavePath() != "") {
			Bitmap bmp = MessageBitmapCache.getInstance().get(user.getSavePath());
			if (bmp != null) {
				ivAvatar.setImageBitmap(bmp);
			} else {
				ImageTool.getBitmapData(ivAvatar, user.getHeadUrl(), user.getSavePath(), "Friends_Fragment_getBitmapData");
			}
		}else{
			ivAvatar.setBackgroundResource(R.mipmap.head);
		}
		tvNick.setText(user.getUserName());
		try {
			if("EXPIRED".equals(user.getChatStatus().toString())) {
                tvRightNick.setVisibility(View.VISIBLE);
                contactitem_layout.setBackgroundResource(R.color.bg_pending_data);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < UserInfos.size(); i++) {
			User user = UserInfos.get(i);
			String l = PingYinUtil.converterToFirstSpell(user.getUserName());
			if("".equals(l)){
				l = "#";
			}else {
				l = l.substring(0, 1);
			}
			char firstChar = l.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
