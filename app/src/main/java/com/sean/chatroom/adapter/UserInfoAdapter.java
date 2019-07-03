package com.sean.chatroom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.sean.chatroom.R;
import com.sean.chatroom.bean.UserInfoItem;

import java.util.List;

public class UserInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    List<UserInfoItem> list;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public UserInfoAdapter(List<UserInfoItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 123:
                View sinView = layoutInflater.inflate(R.layout.adapter_userdata_sin, parent, false);
                viewHolder = new SinViewHolder(sinView);
                break;
            case 456:
                View CBView = layoutInflater.inflate(R.layout.adapter_userdata_cb, parent, false);
                viewHolder = new CBViewHolder(CBView);
                break;
            case 789:
                View NorView = layoutInflater.inflate(R.layout.adapter_userdata, parent, false);
                viewHolder = new NorViewHolder(NorView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (position == 4) {
            setSinViewHolder((SinViewHolder) holder, position);
        } else if (position == 3) {
            setCBViewHolder((CBViewHolder) holder, position, list.get(position).isCheck());
        } else {
            setNorViewHolder((NorViewHolder) holder, position);
        }
        holder.itemView.setTag(position);
        if (position == 2) {
            if (list.get(position).getData().equals("")){
                holder.itemView.setOnClickListener(this);
            }
        } else {
            holder.itemView.setOnClickListener(this);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    //設置有checkbok Item的ViewHolder
    private void setCBViewHolder(CBViewHolder cbViewHolder, int position, boolean IsChecked) {
        UserInfoItem item = list.get(position);
        cbViewHolder.title.setText(item.getTitle());
        cbViewHolder.data.setText(item.getData());
        cbViewHolder.checkedTextView.setChecked(IsChecked);
    }
    //設置只有一個textview Item的ViewHolder
    private void setSinViewHolder(SinViewHolder sinViewHolder, int position) {
        UserInfoItem item = list.get(position);
        sinViewHolder.title.setText(item.getTitle());
    }
    //設置只有兩個textview Item的ViewHolder
    private void setNorViewHolder(NorViewHolder norViewHolder, int position) {
        UserInfoItem item = list.get(position);
        norViewHolder.title.setText(item.getTitle());
        if (position==2){
            if (item.getData().equals("")){
                norViewHolder.data.setText("新增id");
            }else {
                norViewHolder.data.setText(item.getData());
            }
        }else {
            norViewHolder.data.setText(item.getData());
        }

    }

    private class CBViewHolder extends RecyclerView.ViewHolder {
        TextView title, data;
        CheckedTextView checkedTextView;

        public CBViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.useradapter_title);
            data = (TextView) itemView.findViewById(R.id.useradapter_data);
            checkedTextView = (CheckedTextView) itemView.findViewById(R.id.chkSelected);
        }
    }


    private class NorViewHolder extends RecyclerView.ViewHolder {
        TextView title, data;

        public NorViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.useradapter_title);
            data = (TextView) itemView.findViewById(R.id.useradapter_data);
        }
    }

    private class SinViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public SinViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.useradapter_title);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 4) {
            return 123;
        } else if (position == 3) {
            return 456;
        } else {
            return 789;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }


}
