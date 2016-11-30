package com.d5.john.car.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d5.john.car.App;
import com.d5.john.car.R;
import com.d5.john.car.custom.bean.PendingUser;
import com.d5.john.car.listener.OnItemClickListener;
import com.d5.john.car.ui.tools.ImageTool;
import com.d5.john.car.ui.tools.MessageBitmapCache;
import com.d5.john.car.util.CalendarUtils;
import com.d5.john.car.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKeyword on 16/6/30.
 */
public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {
    private List<PendingUser> mItems = new ArrayList<>();
    private LayoutInflater mInflater;

    private OnItemClickListener mClickListener;

    public PendingAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<PendingUser> items) {
        mItems.clear();
        if(items.size() >0) {
            mItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_pager, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (reflect2Click(position)&&(mClickListener != null)) {
                    mClickListener.onItemClick(position, v, holder);
                }
            }
        });
        return holder;
    }
    private Boolean reflect2Click(int position){
        Boolean bool = false;
        PendingUser item = mItems.get(position);
        if("WAIT_SWITCH_IN".equals(item.getTitleRF())){
            bool = true;
        }
        return bool;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PendingUser item = mItems.get(position);
        holder.tvTitle.setText(item.getUserName());
        if(item.getHeadUrl() != ""){
            String smallImagePath = CommonUtil.getMd5Path(item.getHeadUrl(), App.FILE_SAVE_TYPE_IMAGE);
            Bitmap bmp = MessageBitmapCache.getInstance().get(smallImagePath);
            if(bmp!= null) {
                holder.ivTouXiang.setImageBitmap(bmp);
            }else{
                ImageTool.getBitmapData(holder.ivTouXiang, item.getHeadUrl(),smallImagePath,"PendingAdapter");
            }
        }else{
            holder.ivTouXiang.setBackgroundResource(R.mipmap.head);
        }
        /*holder.ivTouXiang.setImageResource(R.drawable.head);*/
        int HH = CalendarUtils.formatDate2Now(item.getGmtExpired());
        if(HH == -1){    //已过期
            holder.tvRightTop.setVisibility(View.GONE);     //待接入
            holder.linLay.setBackgroundResource(R.color.bg_pending_data);
            holder.tvRightBottom.setText("已过期");  //已过期
        } else{
            holder.tvRightTop.setText("待接入");     //待接入
            String str = HH +"小时后过期";
            holder.tvRightBottom.setText(str);  //X小时后过期
        }


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linLay;
        private ImageView ivTouXiang;
        private TextView tvTitle;
        private TextView tvRightTop;
        private TextView tvRightBottom;


        public MyViewHolder(View itemView) {
            super(itemView);
            linLay = (LinearLayout)itemView.findViewById(R.id.pending_layout);
            ivTouXiang= (ImageView)itemView.findViewById(R.id.pending_avatar_iv);
            tvTitle = (TextView) itemView.findViewById(R.id.pending_nick);
            tvRightTop = (TextView) itemView.findViewById(R.id.pending_right_top_nick);
            tvRightBottom = (TextView) itemView.findViewById(R.id.pending_right_bottom_nick);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
