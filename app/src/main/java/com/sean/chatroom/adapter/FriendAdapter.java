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

import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sean.chatroom.api.ApiClient.MY_IP_ADDRESS;

public class FriendAdapter extends BaseAdapter {
    private Context context;
    private List<FriendItem> list;

    public FriendAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setList(List<FriendItem> friendItems) {
        for (int i = 0; i < friendItems.size(); i++) {
            if (friendItems.get(i).getShip() == 1) {
                list.add(friendItems.get(i));
            }
        }
    }

    //更新好友數據
    public void refresh(List<FriendItem> friendItems) {
        list.clear();
        for (int i = 0; i < friendItems.size(); i++) {
            if (friendItems.get(i).getShip() == 1) {
                list.add(friendItems.get(i));
            }
        }
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder VH;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.adapter_friend, null);
            VH = new ViewHolder();
            VH.Sticker = (CircleImageView) view.findViewById(R.id.friend_sticker);
            VH.Name = (TextView) view.findViewById(R.id.friend_name);
            view.setTag(VH);
        } else {
            VH = (ViewHolder) view.getTag();
        }
        FriendItem item = list.get(i);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/sticker/" + item.getSticker() + ".jpg";
        File file = new File(path);
        if (file.exists()) {
            Picasso.get().load(file).into(VH.Sticker);
        } else {
            Picasso.get().load(MY_IP_ADDRESS+"/sticker/" + file.getName()).into(VH.Sticker);
        }
        VH.Name.setText(item.getName());
        return view;
    }

    private class ViewHolder {
        TextView Name;
        CircleImageView Sticker;
    }
}
