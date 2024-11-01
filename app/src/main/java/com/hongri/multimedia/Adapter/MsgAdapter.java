package com.hongri.multimedia.Adapter;

import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.hongri.multimedia.R;
import com.hongri.multimedia.util.Msg;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> list;
    public MsgAdapter(List<Msg> list){
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
       ConstraintLayout leftLayout;
        TextView left_msg,message_time_left;
        ImageView ai_head,user_head;
        ConstraintLayout rightLayout;
        TextView right_msg,message_time_right;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            left_msg = view.findViewById(R.id.left_msg);
            message_time_left=view.findViewById(R.id.message_time_left);
            rightLayout = view.findViewById(R.id.right_layout);
            right_msg = view.findViewById(R.id.right_msg);
            message_time_right=view.findViewById(R.id.message_time_right);
            ai_head=view.findViewById(R.id.ai_head);
            user_head=view.findViewById(R.id.user_head);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Msg msg = list.get(position);
        if(msg.getType() == Msg.TYPE_RECEIVED){
            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏  ai
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.left_msg.setText(msg.getContent());
            holder.message_time_left.setText(msg.getTime());
            holder.ai_head.setImageResource(R.mipmap.ais);
            //注意此处隐藏右面的消息布局用的是 View.GONE
            holder.rightLayout.setVisibility(View.GONE);

        }else if(msg.getType() == Msg.TYPE_SENT){
            //如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏   user
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.right_msg.setText(msg.getContent());
            holder.message_time_right.setText(msg.getTime());
            holder.user_head.setImageResource(R.mipmap.user_2);
            //同样使用View.GONE
            holder.leftLayout.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
