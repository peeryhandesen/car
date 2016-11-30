package com.d5.john.car.ui.fragment.second;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.d5.john.car.R;
import com.d5.john.car.adapter.WechatPagerFragmentAdapter;
import com.d5.john.car.base.BaseLazyMainFragment;
import com.d5.john.car.ui.login.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by YoKeyword on 16/6/30.
 */
public class WechatSecondTabFragment extends BaseLazyMainFragment {
    @Bind(R.id.img_back)
    ImageView mImgBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    private TabLayout mTab;
    private ViewPager mViewPager;

    public static WechatSecondTabFragment newInstance() {
        Bundle args = new Bundle();
        WechatSecondTabFragment fragment = new WechatSecondTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wechat_fragment_tab_second, container, false);
        initView(view);
        ButterKnife.bind(this, view);
        setTitleText(mTvTitle,"客户",mImgBack,mTvRight,"退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.push_top_in2, R.anim.push_top_out2);
                /*Friends_Fragment.getInstance().onDestroyView();
                getActivity().finish();*/
                /*android.os.Process.killProcess(android.os.Process.myPid());*/
            }
        });
        return view;
    }

    private void initView(View view) {
        mTab = (TabLayout) view.findViewById(R.id.tab);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mTab.addTab(mTab.newTab().setText("我的客户"));
        mTab.addTab(mTab.newTab().setText("待接入"));
    }

    @Override
    protected void initLazyView(@Nullable Bundle savedInstanceState) {
        WechatPagerFragmentAdapter pagerAdapter = new WechatPagerFragmentAdapter(getChildFragmentManager(),getContext());
        mViewPager.setAdapter(pagerAdapter);
        mTab.setupWithViewPager(mViewPager);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
