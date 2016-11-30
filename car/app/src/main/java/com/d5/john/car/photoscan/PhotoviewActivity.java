package com.d5.john.car.photoscan;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.d5.john.car.App;
import com.d5.john.car.R;
import com.d5.john.car.ui.fragment.first.ChatTTActivity;

import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * Created by Administrator on 2016/11/10.
 */

public class PhotoviewActivity extends SupportActivity implements OnViewTapListener{
    private PhotoViewPager mViewPager;
    private PhotoView mPhotoView;
    private List<String> mImgUrls;
    private PhotoViewAdapter mAdapter;
    private PhotoViewAttacher mAttacher;
    private int id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewpager);
        App.activityCount = 3;
        id = getIntent().getIntExtra(ChatTTActivity.PHOTO_IMAGEID,0);
        setupView();
        setupData();
    }

    private void setupView(){
        mViewPager = (PhotoViewPager) findViewById(R.id.view_pager);
        if(id == 0) {
            mPhotoView = (PhotoView) findViewById(R.id.chat_image_receive_content);
        }else{
            mPhotoView = (PhotoView) findViewById(R.id.chat_image_send_content);
        }
    }

    public void setUrls(List<String> urls){
        if(urls != null) {
            mImgUrls.clear();
            mImgUrls.addAll(urls);
        }
    }

    private void setupData(){
        int mCurrentUrl = getIntent().getIntExtra(ChatTTActivity.PHOTO_POSITION,0);
        mImgUrls = getIntent().getStringArrayListExtra(ChatTTActivity.PHOTO_URLS);
        mAdapter = new PhotoViewAdapter();
        mViewPager.setAdapter(mAdapter);
        //设置当前需要显示的图片
        mViewPager.setCurrentItem(mCurrentUrl);
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.activityCount = 0;
    }

    class PhotoViewAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = container.inflate(PhotoviewActivity.this,
                    R.layout.item_photo_view,null);
            mPhotoView = (PhotoView) view.findViewById(R.id.photo);
            /*mPhotoView = new PhotoView(container.getContext());*/
           /* if(id == 0) {
                mPhotoView = (PhotoView) findViewById(R.id.chat_image_receive_content);
            }else{
                mPhotoView = (PhotoView) findViewById(R.id.chat_image_send_content);
            }*/
            //使用ImageLoader加载图片
            App.getInstance().imageLoader.displayImage(
                    mImgUrls.get(position), mPhotoView, DisplayImageOptionsUtil.defaultOptions);

            //给图片增加点击事件
            mAttacher = new PhotoViewAttacher(mPhotoView);
            mAttacher.setOnViewTapListener(PhotoviewActivity.this);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mAttacher = null;
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return mImgUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
