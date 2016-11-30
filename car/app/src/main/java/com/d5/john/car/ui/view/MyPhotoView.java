package com.d5.john.car.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/11/16.
 */

public class MyPhotoView extends PhotoView {
    private static final String TAG = MyPhotoView.class.getName();

    public MyPhotoView(Context context){
        super(context);
    }

    public MyPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent.");
        if(event.getAction() == MotionEvent.ACTION_UP ){
            return true;
        }
        return super.onTouchEvent(event);
    }
}
