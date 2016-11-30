package com.d5.john.car.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.d5.john.car.App;
import com.d5.john.car.R;
import com.d5.john.car.beans.ChatMessageInfo;
import com.d5.john.car.emoji.EmojiParser;
import com.d5.john.car.emoji.ParseEmojiMsgUtil;
import com.d5.john.car.listener.MyItemClickListener;
import com.d5.john.car.listener.MyItemLongClickListener;
import com.d5.john.car.photoscan.PhotoviewActivity;
import com.d5.john.car.ui.tools.ImageTool;
import com.d5.john.car.ui.tools.MessageBitmapCache;
import com.d5.john.car.ui.view.MyPhotoView;
import com.d5.john.car.util.CalendarUtils;
import com.d5.john.car.util.CommonUtil;
import com.d5.john.car.util.sign.D5UrlUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.BitmapCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoView;

import static com.d5.john.car.ui.fragment.first.ChatTTActivity.PHOTO_IMAGEID;
import static com.d5.john.car.ui.fragment.first.ChatTTActivity.PHOTO_POSITION;
import static com.d5.john.car.ui.fragment.first.ChatTTActivity.PHOTO_URLS;


/**
 * 聊天适配器
 * Created by idisfkj on 16/4/25.
 * Email : idisfkj@qq.com.
 */
public class ChatAdapterFirst extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private static final String REGEX_CHAT_EMOJI_STR = "\\[[a-f0-9]{5}\\]";
    private static final int MESSAGE_TYPE_RECV_TXET = 0;
    private static final int MESSAGE_TYPE_SENT_TXET = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
   /* private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;*/
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    /*private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;*/
    private static final int SYSTEM_MESSAGE = 16;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private View view;
    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;
    private Bitmap mSendBitmap;
    private Bitmap mHostBitmap;
    private List<ChatMessageInfo> mMessageList = new ArrayList<>();
    private List<String> mURLLList = new ArrayList<>();  //保存当前用户交流的所有图片
    private String time = null;

    public ChatAdapterFirst(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);

    }

    public ChatAdapterFirst(ArrayList<ChatMessageInfo>  items) {
        mMessageList = items;
    }

    public void setHostBitmap(Bitmap bitmap){
        mHostBitmap = bitmap;
    }


    public void setURLLList(List<String> beans) {
        try {
            mURLLList.clear();
            mURLLList.addAll(beans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDatas(List<ChatMessageInfo> beans) {
        try {
            mMessageList.clear();
            mMessageList.addAll(beans);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public void addData(List<ChatMessageInfo> beans,int position){
        mMessageList.clear();
        mMessageList.addAll(beans);
        notifyItemInserted(position -1);
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            int position = view.getVerticalScrollbarPosition();
            mItemClickListener.onItemClick(v.getRootView(),position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_RECV_TXET) {
            view = mLayoutInflater.inflate(R.layout.chat_receive, parent, false);
            return new ChatAdapterFirst.ChatReceiveViewHolder(view,mItemClickListener,mItemLongClickListener);
        } else if (viewType == MESSAGE_TYPE_SENT_TXET) {
            view = mLayoutInflater.inflate(R.layout.chat_send, parent, false);
            return new ChatAdapterFirst.ChatSendViewHolder(view,mItemClickListener,mItemLongClickListener);
        } else if (viewType == MESSAGE_TYPE_RECV_IMAGE) {
            view = mLayoutInflater.inflate(R.layout.chat_picture_receive, parent, false);
            return new ChatAdapterFirst.ChatImageReceiveViewHolder(view,mItemClickListener,mItemLongClickListener);
        } else if (viewType == MESSAGE_TYPE_SENT_IMAGE) {
            view = mLayoutInflater.inflate(R.layout.chat_picture_send, parent, false);
            return new ChatAdapterFirst.ChatImageSendViewHolder(view,mItemClickListener,mItemLongClickListener);
        } else {
            view = mLayoutInflater.inflate(R.layout.chat_system, parent, false);
            return new ChatAdapterFirst.ChatSystemViewHolder(view);
        }
    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        /*this.mItemClickListener = listener;*/
    }

    public void setOnItemLongClickListener(MyItemLongClickListener listener){
        /*this.mItemLongClickListener = listener;*/
    }

    private Boolean isContainEmoji(String mesg){
        Boolean bo = false;
        if(Pattern.compile(REGEX_CHAT_EMOJI_STR).matcher(mesg).find()){
            bo = true;
        }
        return bo;
    }

    public void showImageByThread(final PhotoView imageView, final String url) {
        final Handler mHandlerBit = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bitmap bitmap = (Bitmap) msg.obj;
                imageView.setImageBitmap(bitmap);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmapFromUrl(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandlerBit.sendMessage(message);
            }
        }.start();
    }

    private Bitmap getBitmapFromUrl(final String url){
        final String smallImagePath = CommonUtil.getMd5Path(url, App.FILE_SAVE_TYPE_IMAGE);
        final Bitmap[] bm = new Bitmap[1];
        OkHttpUtils.get(url)//
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        // bitmap 即为返回的图片数据
                        Bitmap bitmapSmall2 = ImageTool.ratio(bitmap,240,180);
                        if(bitmapSmall2 != null) {
                            MessageBitmapCache.getInstance().put(smallImagePath, bitmapSmall2);
                        }else{
                            MessageBitmapCache.getInstance().put(smallImagePath, bitmap);
                        }
                    }
                });
        return bm[0];

    }

    private Bitmap getBitmapFromFile(String smallImagePath){
        Bitmap[] bm = new Bitmap[1];
        bm[0] =  MessageBitmapCache.getInstance().get(smallImagePath);
        if(bm[0] != null){
            return bm[0];
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ChatMessageInfo info = mMessageList.get(position);
        String name = info.getName();
        String timeChat = info.getTime();
        if("IMAGE".equals(name)){
            String url = info.getMessage();
            String smallImagePath = null;
            if(url.contains("src=")) {
                url = url.substring(url.indexOf("src=") + 5, url.indexOf("/>") - 1);
            }
            smallImagePath = CommonUtil.getMd5Path(url, App.FILE_SAVE_TYPE_IMAGE);
            Bitmap bmp = getBitmapFromFile(smallImagePath);
            if (holder instanceof ChatAdapterFirst.ChatImageReceiveViewHolder){
                if((time == null)||timeCompare(time,timeChat)){
                    time = timeChat;
                    ((ChatAdapterFirst.ChatImageReceiveViewHolder) holder).chatImageReceiveTime.setVisibility(View.VISIBLE);
                    ((ChatAdapterFirst.ChatImageReceiveViewHolder) holder).chatImageReceiveTime.setText(CalendarUtils.getCurrentDate(timeChat));
                }else{
                    ((ChatAdapterFirst.ChatImageReceiveViewHolder) holder).chatImageReceiveTime.setVisibility(View.GONE);
                }
                if (mHostBitmap != null) {
                    ((ChatImageReceiveViewHolder) holder).chatImageReceivePicture.setImageBitmap(mHostBitmap);
                }
                if(url != null) {
                    if(bmp != null){
                        ((ChatAdapterFirst.ChatImageReceiveViewHolder) holder).chatImageReceiveContent.setImageBitmap(bmp);
                    }else{
                        showImageByThread(((ChatAdapterFirst.ChatImageReceiveViewHolder) holder).chatImageReceiveContent,url);
                    }
                }
                /*((ChatAdapterFirst.ChatImageReceiveViewHolder) holder).chatImageReceiveContent
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                mItemClickListener.onItemClick(((ChatAdapterFirst.ChatImageReceiveViewHolder) holder).chatImageReceiveContent,position);
                            }
                        });*/
                /*new BtnImageListener(info, position, false)*/
            }else if (holder instanceof ChatAdapterFirst.ChatImageSendViewHolder) {
                if ((time == null) || timeCompare(time, timeChat)) {
                    time = timeChat;
                    ((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendTime.setVisibility(View.VISIBLE);
                    ((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendTime.setText(CalendarUtils.getCurrentDate(timeChat));
                } else {
                    ((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendTime.setVisibility(View.GONE);
                }
                if (mSendBitmap != null) {
                    ((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendPicture.setImageBitmap(mSendBitmap);
                } else {
                    ((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendPicture.setBackgroundResource(R.mipmap.head);
                }
                if (url != null) {
                    if(bmp != null){
                        ((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendContent.setImageBitmap(bmp);
                    }else{
                        showImageByThread(((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendContent,url);
                    }
                }
                /*((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendContent.setOnClickListener(new BtnImageListener(info, position, true));*/
                /*((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendContent
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                mItemClickListener.onItemClick(((ChatAdapterFirst.ChatImageSendViewHolder) holder).chatImageSendContent,position);
                            }
                        });*/
            }
        }else if("TEXT".equals(name)){
            SpannableString spannableString = null;
            String content = info.getMessage();
            if(isContainEmoji(content)){
                String msgStr = ParseEmojiMsgUtil.convertToMsg(content);
                String unicode = EmojiParser.getInstance(mContext).parseEmoji(msgStr);
                spannableString = ParseEmojiMsgUtil.getExpressionString(mContext, unicode);
            }
            if (holder instanceof ChatAdapterFirst.ChatReceiveViewHolder) {
                if((time == null)||timeCompare(time,timeChat)){
                    time = timeChat;
                    ((ChatAdapterFirst.ChatReceiveViewHolder) holder).chatReceiveTime.setVisibility(View.VISIBLE);
                    ((ChatAdapterFirst.ChatReceiveViewHolder) holder).chatReceiveTime.setText(CalendarUtils.getCurrentDate(timeChat));
                }else{
                    ((ChatAdapterFirst.ChatReceiveViewHolder) holder).chatReceiveTime.setVisibility(View.GONE);
                }
                if (mHostBitmap != null)
                    ((ChatReceiveViewHolder) holder).chatReceivePicture.setImageBitmap(mHostBitmap);
                if(spannableString != null) {
                    ((ChatAdapterFirst.ChatReceiveViewHolder) holder).chatReceiveContent.setText(spannableString);
                }else{
                    ((ChatAdapterFirst.ChatReceiveViewHolder) holder).chatReceiveContent.setText(content);
                }
                if(info.getMatchRate() != -1.0) {
                    ((ChatReceiveViewHolder) holder).chatReceiveMatchrate.setVisibility(View.VISIBLE);
                    ((ChatReceiveViewHolder) holder).chatReceiveMatchrate.setText(info.getMatchRate() + "");
                    if(info.isRed()){
                        ((ChatReceiveViewHolder) holder).chatReceiveMatchrate.setTextColor(mContext.getResources().getColor(R.color.receive_materate_green));
                    }else{
                        ((ChatReceiveViewHolder) holder).chatReceiveMatchrate.setTextColor(mContext.getResources().getColor(R.color.receive_materate_red));
                    }
                }else{
                    ((ChatReceiveViewHolder) holder).chatReceiveMatchrate.setVisibility(View.INVISIBLE);
                }

            } else if (holder instanceof ChatAdapterFirst.ChatSendViewHolder) {
                if ((time == null) || timeCompare(time, timeChat)) {
                    time = timeChat;
                    ((ChatAdapterFirst.ChatSendViewHolder) holder).chatSendTime.setVisibility(View.VISIBLE);
                    ((ChatAdapterFirst.ChatSendViewHolder) holder).chatSendTime.setText(CalendarUtils.getCurrentDate(timeChat));
                } else {
                    ((ChatAdapterFirst.ChatSendViewHolder) holder).chatSendTime.setVisibility(View.GONE);
                }
                if (mSendBitmap != null) {
                    ((ChatAdapterFirst.ChatSendViewHolder) holder).chatSendPicture.setImageBitmap(mSendBitmap);
                } else {
                    ((ChatAdapterFirst.ChatSendViewHolder) holder).chatSendPicture.setBackgroundResource(R.mipmap.head);
                }
                if (spannableString != null) {
                    ((ChatAdapterFirst.ChatSendViewHolder) holder).chatSendContent.setText(spannableString);
                } else {
                    ((ChatAdapterFirst.ChatSendViewHolder) holder).chatSendContent.setText(content);
                }
            }
        }else{
            if((time == null)||timeCompare(time,timeChat)){
                time = timeChat;
                ((ChatAdapterFirst.ChatSystemViewHolder) holder).chatSystemTime.setVisibility(View.VISIBLE);
                ((ChatAdapterFirst.ChatSystemViewHolder) holder).chatSystemTime.setText(CalendarUtils.getCurrentDate(timeChat));
            }else{
                ((ChatAdapterFirst.ChatSystemViewHolder) holder).chatSystemTime.setVisibility(View.GONE);
            }
            ((ChatAdapterFirst.ChatSystemViewHolder) holder).chatSystemContent.setText(info.getMessage());
        }
    }

    private Boolean timeCompare(String time,String nowTime){
        Boolean bool = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long time1 = format.parse(time).getTime();
            long time2 = format.parse(nowTime).getTime();
            long gtime1 = time2 > time1 ? (time2 - time1) : (time1 - time2);
            long gtime2 = 600000;
            if(gtime1 > gtime2) {
                bool = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bool;
    }

    @Override
    public int getItemViewType(int position) {
        int flag = mMessageList.get(position).getFlag();
        String name = mMessageList.get(position).getName();
        switch (name){
            case "TEXT":
                return (flag == 0)? MESSAGE_TYPE_RECV_TXET : MESSAGE_TYPE_SENT_TXET;
            case "IMAGE":
                return (flag == 0)?MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
            default:
                return SYSTEM_MESSAGE;
        }
    }

    class BtnImageListener implements View.OnClickListener {
        private ChatMessageInfo msgInfo = null;
        private int position = 0;
        private boolean isMine = false;

        public BtnImageListener(ChatMessageInfo msg, int p, boolean me) {
            this.msgInfo = msg;
            this.position = p;
            this.isMine = me;
        }

        @Override
        public void onClick(View v) {
            if (msgInfo.getName() == "IMAGE") {
                if (!isMine) {
                    String url = msgInfo.getMessage();
                    if (url.contains("src=")) {
                        url = url.substring(url.indexOf("src=") + 5, url.indexOf("/>") - 1);
                    }
                    int index = mURLLList.indexOf(url);
                    if (mURLLList.contains(url)) {
                        Intent intent = new Intent(mContext, PhotoviewActivity.class);
                        intent.putExtra(PHOTO_POSITION, index);
                        intent.putExtra(PHOTO_IMAGEID, msgInfo.getFlag());
                        intent.putStringArrayListExtra(PHOTO_URLS, (ArrayList<String>) mURLLList);
                        mContext.startActivity(intent);
                    }
                }else{
                    String messageId = msgInfo.getMessage();
                    String url = D5UrlUtils.MAIN_IMAGE_ENGINE;
                    url += messageId.toString();
                    if(!mURLLList.contains(url)){
                        mURLLList.add(url);
                    }else{
                        int index = mURLLList.indexOf(url);
                        Intent intent = new Intent(mContext, PhotoviewActivity.class);
                        intent.putExtra(PHOTO_POSITION, index);
                        intent.putExtra(PHOTO_IMAGEID, msgInfo.getFlag());
                        intent.putStringArrayListExtra(PHOTO_URLS, (ArrayList<String>) mURLLList);
                        mContext.startActivity(intent);
                    }
                }


            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public static class ChatSendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        @Bind(R.id.chat_send_time)
        TextView chatSendTime;
        @Bind(R.id.chat_send_picture)
        ImageView chatSendPicture;
        @Bind(R.id.chat_send_content)
        TextView chatSendContent;
        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        ChatSendViewHolder(View view,MyItemClickListener listener,MyItemLongClickListener longClickListener) {
            super(view);
            ButterKnife.bind(this,view);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        /**
         * 点击监听
         */
        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }

        /**
         * 长按监听
         */
        @Override
        public boolean onLongClick(View arg0) {
            if(mLongClickListener != null){
                mLongClickListener.onItemLongClick(arg0, getPosition());
            }
            return true;
        }
    }

    public static class ChatReceiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        @Bind(R.id.chat_receive_time)
        TextView chatReceiveTime;
        @Bind(R.id.chat_receive_picture)
        ImageView chatReceivePicture;
        @Bind(R.id.chat_receive_content)
        TextView chatReceiveContent;
        @Bind(R.id.chat_receive_match_rate)
        TextView chatReceiveMatchrate;
        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        ChatReceiveViewHolder(View view,MyItemClickListener listener,MyItemLongClickListener longClickListener) {
            super(view);
            ButterKnife.bind(this, view);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        /**
         * 点击监听
         */
        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }

        /**
         * 长按监听
         */
        @Override
        public boolean onLongClick(View arg0) {
            if(mLongClickListener != null){
                mLongClickListener.onItemLongClick(arg0, getPosition());
            }
            return true;
        }
    }

    public static class ChatImageReceiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        @Bind(R.id.chat_image_receive_time)
        TextView chatImageReceiveTime;
        @Bind(R.id.chat_image_receive_picture)
        ImageView chatImageReceivePicture;
        @Bind(R.id.chat_image_receive_content)
        MyPhotoView chatImageReceiveContent;
        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        ChatImageReceiveViewHolder(View view,MyItemClickListener listener,MyItemLongClickListener longClickListener) {
            super(view);
            ButterKnife.bind(this, view);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            chatImageReceiveContent.setClickable(false);
        }

       /* public boolean onInterceptTouchEvent(MotionEvent ev) {
            return super.onInterceptTouchEvent(ev);
        }

        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }*/
        /**
         * 点击监听
         */
        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }

        /**
         * 长按监听
         */
        @Override
        public boolean onLongClick(View arg0) {
            if(mLongClickListener != null){
                mLongClickListener.onItemLongClick(arg0, getPosition());
            }
            return true;
        }

    }
    //</editor-fold>

    public static class ChatImageSendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        @Bind(R.id.chat_image_send_time)
        TextView chatImageSendTime;
        @Bind(R.id.chat_image_send_picture)
        ImageView chatImageSendPicture;
        @Bind(R.id.chat_image_send_content)
        MyPhotoView chatImageSendContent;
        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        ChatImageSendViewHolder(View view,MyItemClickListener listener,MyItemLongClickListener longClickListener) {
            super(view);
            ButterKnife.bind(this, view);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            chatImageSendContent.setClickable(false);
        }
        /**
         * 点击监听
         */
        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }

        /**
         * 长按监听
         */
        @Override
        public boolean onLongClick(View arg0) {
            if(mLongClickListener != null){
                mLongClickListener.onItemLongClick(arg0, getPosition());
            }
            return true;
        }
    }

    public static class ChatSystemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chat_system_time)
        TextView chatSystemTime;
        @Bind(R.id.chat_system_content)
        TextView chatSystemContent;

        ChatSystemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
