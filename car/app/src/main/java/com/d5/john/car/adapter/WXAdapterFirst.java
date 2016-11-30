package com.d5.john.car.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.d5.john.car.R;
import com.d5.john.car.beans.WXItemInfo;
import com.d5.john.car.emoji.EmojiParser;
import com.d5.john.car.emoji.ParseEmojiMsgUtil;
import com.d5.john.car.listener.OnItemClickListener;
import com.d5.john.car.ui.tools.ImageTool;
import com.d5.john.car.ui.tools.MessageBitmapCache;
import com.d5.john.car.util.BadgeViewUtils;
import com.d5.john.car.util.CalendarUtils;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by YoKeyword on 16/6/30.
 */
public class WXAdapterFirst extends RecyclerView.Adapter<WXAdapterFirst.VH> {
    private static final String REGEX_WX_EMOJI_STR = "\\[[a-f0-9]{5}\\]";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<WXItemInfo> mItems = new ArrayList<>();

    private OnItemClickListener mClickListener;

    public WXAdapterFirst(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<WXItemInfo> beans) {
        mItems.clear();
        mItems.addAll(beans);
        notifyDataSetChanged();
    }

    public void refreshMsg(WXItemInfo bean) {
        int index = mItems.indexOf(bean);
        if (index < 0) return;
        notifyItemChanged(index);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.wx_item, parent, false);
        final VH holder = new VH(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(holder.getAdapterPosition(), v, holder);
                }
            }
        });
        return holder;
    }

    private Boolean isContainEmoji(String mesg){
        Boolean bo = false;
        if(Pattern.compile(REGEX_WX_EMOJI_STR).matcher(mesg).find()){
            bo = true;
        }
        return bo;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        WXItemInfo item = mItems.get(position);
        holder.wxItemTitle.setText(item.getTitle());
        SpannableString spannableString = null;
        String content = item.getContent();
        if(isContainEmoji(content)){
            String msgStr = ParseEmojiMsgUtil.convertToMsg(content);
            String unicode = EmojiParser.getInstance(mContext).parseEmoji(msgStr);
            spannableString = ParseEmojiMsgUtil.getExpressionString(mContext, unicode);
            holder.wxItemContent.setText(spannableString);
        }else{
            holder.wxItemContent.setText(content);
        }
        holder.wxItemTime.setText(CalendarUtils.getCurrentDate(item.getTime()));
        Bitmap bmp = MessageBitmapCache.getInstance().get(item.getSavePath());
        if(bmp!= null) {
            holder.wxItemPicture.setImageBitmap(bmp);
        }else{
            ImageTool.getBitmapData(holder.wxItemPicture, item.getPictureUrl(),item.getSavePath(),"WXAdapterFirst");
        }
        String replyModel = item.getReplyModel();
        if("SYSTEM_REPLY".equals(replyModel)) {
            int alarmNum = item.getAlarmCountNum();
            if(alarmNum >0) {
                holder.wxItemWorning.setVisibility(View.VISIBLE);
                holder.wxItemWorning.setText("" + alarmNum);
            }else{
                holder.wxItemWorning.setVisibility(View.INVISIBLE);
            }
        }else{
            holder.wxItemWorning.setVisibility(View.INVISIBLE);
            holder.unReadNum = item.getUnReadNum();
            //回收 防止影响更新
            if (holder.badgeView != null)
                holder.badgeView.hide();
            if (holder.unReadNum > 0) {
                //显示未读信息数
                holder.badgeView = BadgeViewUtils.create(mContext, holder.wxItemPicture, String.valueOf(holder.unReadNum));
            }
        }
        holder.wxItemTitle.getRootView().setId(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public WXItemInfo getMsg(int position) {
        return mItems.get(position);
    }


    static class VH extends RecyclerView.ViewHolder {
        @Bind(R.id.wx_item_picture)
        ImageView wxItemPicture;
        @Bind(R.id.wx_item_title)
        TextView wxItemTitle;
        @Bind(R.id.wx_item_time)
        TextView wxItemTime;
        @Bind(R.id.wx_item_content)
        TextView wxItemContent;
        @Bind(R.id.wx_item_worning)
        TextView wxItemWorning;
        public int unReadNum;
        public BadgeView badgeView;
        VH(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
