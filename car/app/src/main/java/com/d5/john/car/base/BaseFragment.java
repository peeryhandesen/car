package com.d5.john.car.base;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d5.john.car.R;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by YoKeyword on 16/2/3.
 */
public class BaseFragment extends SupportFragment {
    private static final String TAG = "Fragmentation";

    protected void initToolbarMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.hierarchy);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_hierarchy:
                        _mActivity.showFragmentStackHierarchyView();
                        _mActivity.logFragmentStackHierarchy(TAG);
                        break;
                }
                return true;
            }
        });
    }

    public void setTitleText(TextView tvTitle, String title) {
        tvTitle.setText(title);
    }

    public void setTitleText(TextView tvTitle, String title, ImageView left) {
        tvTitle.setText(title);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
    public void setTitleText(TextView tvTitle, String title, ImageView left, View.OnClickListener l) {
        tvTitle.setText(title);
        left.setOnClickListener(l);
    }

    public void setTitleText(TextView tvTitle, String title, TextView tvRight,int imageRightResource, View.OnClickListener l) {
        tvTitle.setText(title);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setBackgroundResource(imageRightResource);
        tvRight.setOnClickListener(l);
    }

    public void setTitleText(TextView tvTitle, String title, ImageView left, TextView tvRight,String strRight, View.OnClickListener l) {
        tvTitle.setText(title);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(strRight);
        tvRight.setOnClickListener(l);
    }
}
