package com.d5.john.car.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.d5.john.car.R;
import com.d5.john.car.base.BaseFragment;
import com.d5.john.car.ui.fragment.first.WechatFirstTabFragment;
import com.d5.john.car.ui.fragment.second.WechatSecondTabFragment;
import com.d5.john.car.ui.view.BottomBar;
import com.d5.john.car.ui.view.BottomBarTab;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by YoKeyword on 16/6/30.
 */
public class MainFragment extends BaseFragment {
    private static final int REQ_MSG = 10;
    public static final int FIRST = 0;
    public static final int SECOND = 1;

    private SupportFragment[] mFragments = new SupportFragment[2];

    private BottomBar mBottomBar;


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wechat_fragment_main, container, false);
        if (savedInstanceState == null) {
            mFragments[FIRST] = WechatFirstTabFragment.newInstance();
            mFragments[SECOND] = WechatSecondTabFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.getFragments()自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = findChildFragment(WechatFirstTabFragment.class);
            mFragments[SECOND] = findChildFragment(WechatSecondTabFragment.class);
        }

        initView(view);

        return view;
    }

    private void initView(View view) {
        mBottomBar = (BottomBar) view.findViewById(R.id.bottomBar);

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.mipmap.message_choosed, "消息"))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.custum_choosed, "客户"));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 这里推荐使用EventBus来实现 -> 解耦
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
            }
        });
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MSG && resultCode == RESULT_OK) {

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
