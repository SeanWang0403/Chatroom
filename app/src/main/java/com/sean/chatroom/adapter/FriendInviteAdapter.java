package com.sean.chatroom.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sean.chatroom.R;
import com.sean.chatroom.bean.FriendItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sean.chatroom.api.ApiClient.MY_IP_ADDRESS;

public class FriendInviteAdapter extends BaseAdapter {
    List<FriendItem> list;
    Context context;
    private clickCallBack clickCallBack;

    public FriendInviteAdapter(List<FriendItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setClickCallBack(clickCallBack clickCallBack){
        this.clickCallBack=clickCallBack;
    }

    //新增好友後將他從好友邀請中移除
    public void addRefresh(int postion){
        this.list.remove(postion);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder VH;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.friend_invite_block, null);
            VH = new ViewHolder();
            VH.Sticker = (CircleImageView) view.findViewById(R.id.friendinvite_sticker);
            VH.Name = (TextView) view.findViewById(R.id.friendinvite_name);
            VH.add = (Button) view.findViewById(R.id.friendinvite_add);
            view.setTag(VH);
        } else {
            VH = (ViewHolder) view.getTag();
        }
        final FriendItem item = list.get(i);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/sticker/" + item.getSticker() + ".jpg";
        File file = new File(path);
        if (file.exists()){
            Picasso.get().load(file).into(VH.Sticker);
        }else {
            Picasso.get().load(MY_IP_ADDRESS+"/sticker/" + file.getName()).into(VH.Sticker);
        }

        VH.Name.setText(item.getName());
        VH.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallBack.clickItem(item.getUserID(),i);
            }
        });
        return view;
    }

    private class ViewHolder {
        TextView Name;
        CircleImageView Sticker;
        Button add;
    }

    public interface clickCallBack{
        void clickItem(String otherUserID,int postion);
    }
}
