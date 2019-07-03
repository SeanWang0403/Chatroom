package com.sean.chatroom.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sean.chatroom.bean.ChatHistoryItem;
import com.sean.chatroom.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<ChatHistoryItem> list;

    public ChatHistoryAdapter(Context context, List<ChatHistoryItem> list) {
        this.context = context;
        this.list = list;
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
            view = layoutInflater.inflate(R.layout.adapter_chathistory, null);
            VH = new ViewHolder();
            VH.Sticker = (CircleImageView) view.findViewById(R.id.chatroom_history_sticker);
            VH.Name = (TextView) view.findViewById(R.id.chatroom_history_name);
            VH.Time = (TextView) view.findViewById(R.id.chatroom_history_time);
            VH.Message = (TextView) view.findViewById(R.id.chatroom_history_message);
        } else {
            VH = (ViewHolder) view.getTag();
        }
        ChatHistoryItem item = list.get(i);
        if (!item.getSticker().equals("")) {
            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/chatroom/sticker/"+item.getSticker()+".jpg");
            Picasso.get().load(file).into(VH.Sticker);
        }
        VH.Name.setText(item.getName());
        VH.Time.setText(setDate(Long.parseLong(item.getTime())));
        VH.Message.setText(item.getMessage());
        return view;
    }

    //設定日期
    private String setDate(long utc) {
        String local = Long.toString(utc + Calendar.getInstance().getTimeZone().getRawOffset());
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        try {
            date = sdf.parse(sdf.format(Long.parseLong(local)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String localTime = sdf.format(date);
        String timeSplit[] = localTime.split(" ");
        String dateSplit[] = timeSplit[0].split("\\.");
        Date thisDay = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy MM dd");
        String thisDaySplit[] = sdf2.format(thisDay).split(" ");
        if (!thisDaySplit[0].equals(dateSplit[0])) {
            return timeSplit[0];
        } else if (thisDaySplit[1].equals(dateSplit[1]) && thisDaySplit[2].equals(dateSplit[2])) {
            return timeSplit[1];
        } else {
            return dateSplit[1] + "." + dateSplit[2];
        }
    }

    private class ViewHolder {
        TextView Name, Message, Time;
        CircleImageView Sticker;
    }
}
