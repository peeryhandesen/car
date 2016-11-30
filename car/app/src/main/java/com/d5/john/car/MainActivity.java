package com.d5.john.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.d5.john.car.push.SmackService;
import com.d5.john.car.ui.fragment.MainFragment;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class MainActivity extends SupportActivity {
    public static String SHOW_TYPE = "NEW_CHAT_MESSAGE";
    private Intent serviceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wechat_activity_main);
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }
        App.activityCount = 1;
        serviceIntent = new Intent(this, SmackService.class);

        startService(serviceIntent);
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.finish();
            this.overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_right_out);
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        App.activityCount = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*stopService(serviceIntent);*/
    }

}
