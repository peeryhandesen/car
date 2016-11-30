package com.d5.john.car.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.d5.john.car.R;
import com.d5.john.car.ui.fragment.second.Friends_Fragment;
import com.d5.john.car.ui.fragment.second.PendingFragment;


/**
 * Created by YoKeyword on 16/6/5.
 */
public class WechatPagerFragmentAdapter extends FragmentPagerAdapter {
    private String[] mTab = new String[]{"我的客户", "待接入"};

    private Context context;

    public WechatPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public WechatPagerFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return Friends_Fragment.newInstance();
        } else {
            return PendingFragment.newInstance();
        }
    }

    /**
     * 自定义TabLayout title
     * @param position
     * @return
     */
    public View getTabView(int position){
        View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView tv= (TextView) view.findViewById(R.id.textView);
        tv.setText(mTab[position]);
        return view;
    }

    @Override
    public int getCount() {
        return mTab.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTab[position];
    }
}
