package com.d5.john.car.base;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.d5.john.car.R;

import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

/**
 * Created by YoKeyword on 16/2/7.
 */
public class BaseBackFragment extends SwipeBackFragment {
    private static final String TAG = "Fragmentation";

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });

        initToolbarMenu(toolbar);
    }

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
}
