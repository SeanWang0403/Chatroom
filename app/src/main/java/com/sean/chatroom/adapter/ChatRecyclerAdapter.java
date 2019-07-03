package com.sean.chatroom.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.sean.chatroom.R;
import com.sean.chatroom.bean.Message;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sean.chatroom.api.ApiClient.MY_IP_ADDRESS;


public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<Message> item;
    private Context context;
    private String lastMessageDate = getToday();
    private String lastMessageYear = "";
    private OnItemClickListener mOnItemClickListener;
    private File StickerFile;

    public ChatRecyclerAdapter(List<Message> item, String stickerPath, Context context) {
        this.item = item;
        this.context = context;
        this.StickerFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/chatroom/sticker/" + stickerPath + ".jpg");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            case Message.TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_send_text, parent, false);
                viewHolder = new SendMessageViewHolder(view);
                break;
            case Message.TYPE_IMAGE_SENT:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_send_image, parent, false);
                viewHolder = new SendImageViewHolder(view);
                break;
            case Message.TYPE_FILE_SENT:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_send_file, parent, false);
                viewHolder = new SendFileViewHolder(view);
                break;
            case Message.TYPE_AUDIO_SENT:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_send_audio, parent, false);
                viewHolder = new SendAudioViewHolder(view);
                break;
            case Message.TYPE_FACETIME_SENT:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_send_facetime, parent, false);
                viewHolder = new SendFacetimeViewHolder(view);
                break;
            case Message.TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_receive_text, parent, false);
                viewHolder = new ReceiveMessageViewHolder(view);
                break;
            case Message.TYPE_IMAGE_RECEIVED:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_receive_image, parent, false);
                viewHolder = new ReceiveImageViewHolder(view);
                break;
            case Message.TYPE_FILE_RECEIVED:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_receive_file, parent, false);
                viewHolder = new ReceiveFileViewHolder(view);
                break;
            case Message.TYPE_AUDIO_RECEIVED:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_receive_audio, parent, false);
                viewHolder = new ReceiveAudioViewHolder(view);
                break;
            case Message.TYPE_FACETIME_RECEIVED:
                view = LayoutInflater.from(context).inflate(R.layout.chatroom_message_receive_facetime, parent, false);
                viewHolder = new ReceiveFacetimeViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = item.get(position);
        switch (message.getType()) {
            case Message.TYPE_MESSAGE_SENT:
                setSendMessageViewHolder((SendMessageViewHolder) holder, message, position);
                break;
            case Message.TYPE_IMAGE_SENT:
                setSendImageViewHolder((SendImageViewHolder) holder, message, position);
                break;
            case Message.TYPE_FILE_SENT:
                setSendFileViewHolder((SendFileViewHolder) holder, message, position);
                break;
            case Message.TYPE_AUDIO_SENT:
                setSendAudioViewHolder((SendAudioViewHolder) holder, message, position);
                break;
            case Message.TYPE_FACETIME_SENT:
                setSendFacetimeViewHolder((SendFacetimeViewHolder) holder, message, position);
                break;
            case Message.TYPE_MESSAGE_RECEIVED:
                setReceiveMessageViewHolder((ReceiveMessageViewHolder) holder, message, position);
                break;
            case Message.TYPE_IMAGE_RECEIVED:
                setReceiveImageViewHolder((ReceiveImageViewHolder) holder, message, position);
                break;
            case Message.TYPE_FILE_RECEIVED:
                setReceiveFileViewHolder((ReceiveFileViewHolder) holder, message, position);
                break;
            case Message.TYPE_AUDIO_RECEIVED:
                setReceiveAudioViewHolder((ReceiveAudioViewHolder) holder, message, position);
                break;
            case Message.TYPE_FACETIME_RECEIVED:
                setReceiveFacetimeViewHolder((ReceiveFacetimeViewHolder) holder, message, position);
                break;
        }
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemViewType(int position) {
        return item.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    //設置發送訊息的ViewHolder
    private void setSendMessageViewHolder(SendMessageViewHolder sendMessageViewHolder, Message message, int postion) {
        sendMessageViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        sendMessageViewHolder.Message.setText(message.getMessage());
        sendMessageViewHolder.Time.setText(time(message.getCreatetime()));
        if (postion != 0) {
            sendMessageViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                sendMessageViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
        if (message.getRead() != 0) {
            sendMessageViewHolder.Read.setText("已讀");
        } else {
            sendMessageViewHolder.Read.setText("");
        }
    }

    //設置發送圖片的ViewHolder
    private void setSendImageViewHolder(SendImageViewHolder sendImageViewHolder, Message message, int postion) {
        sendImageViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        if (message.getImageFile() != null) {
            Picasso.get().load(message.getImageFile()).resize(500, 0).into(sendImageViewHolder.Photo);
        } else {
            Picasso.get().load(MY_IP_ADDRESS + "/file/" + message.getMessage())
                    .resize(500, 0)
                    .into(sendImageViewHolder.Photo);
        }
        sendImageViewHolder.Time.setText(time(message.getCreatetime()));
        if (postion != 0) {
            sendImageViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                sendImageViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
        if (message.getRead() != 0) {
            sendImageViewHolder.Read.setText("已讀");
        } else {
            sendImageViewHolder.Read.setText("");
        }
    }

    //設置發送檔案的ViewHolder
    private void setSendFileViewHolder(SendFileViewHolder sendFileViewHolder, Message message, int postion) {
        sendFileViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        String FileData[] = message.getMessage().split(" ");
        sendFileViewHolder.FileName.setText(FileData[0]);
        sendFileViewHolder.FileSize.setText(FileData[1]);
        sendFileViewHolder.Time.setText(time(message.getCreatetime()));
        if (postion != 0) {
            sendFileViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                sendFileViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
        if (message.getRead() != 0) {
            sendFileViewHolder.Read.setText("已讀");
        } else {
            sendFileViewHolder.Read.setText("");
        }
    }

    //設置發送語音通話的ViewHolder
    private void setSendAudioViewHolder(SendAudioViewHolder sendAudioViewHolder, Message message, int postion) {
        sendAudioViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        sendAudioViewHolder.AudioTime.setText(message.getMessage());
        sendAudioViewHolder.Time.setText(time(message.getCreatetime()));
        if (postion != 0) {
            sendAudioViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                sendAudioViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
        if (message.getRead() != 0) {
            sendAudioViewHolder.Read.setText("已讀");
        } else {
            sendAudioViewHolder.Read.setText("");
        }
    }

    //設置發送視訊聊天的ViewHolder
    private void setSendFacetimeViewHolder(SendFacetimeViewHolder sendFacetimeViewHolder, Message message, int postion) {
        sendFacetimeViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        sendFacetimeViewHolder.Facetime_Time.setText(message.getMessage());
        sendFacetimeViewHolder.Time.setText(time(message.getCreatetime()));
        if (postion != 0) {
            sendFacetimeViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                sendFacetimeViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
        if (message.getRead() != 0) {
            sendFacetimeViewHolder.Read.setText("已讀");
        } else {
            sendFacetimeViewHolder.Read.setText("");
        }
    }

    //設置接收訊息的ViewHolder
    private void setReceiveMessageViewHolder(ReceiveMessageViewHolder receiveMessageViewHolder, Message message, int postion) {
        receiveMessageViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        if (!StickerFile.getName().equals("")) {
            Picasso.get().load(StickerFile).into(receiveMessageViewHolder.Sticker);
        }
        receiveMessageViewHolder.Message.setText(message.getMessage());
        receiveMessageViewHolder.Time.setText(time(message.getDeliverime()));
        if (postion != 0) {
            receiveMessageViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                receiveMessageViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
    }

    //設置接收圖片的ViewHolder
    private void setReceiveImageViewHolder(ReceiveImageViewHolder receiveImageViewHolder, Message message, int postion) {
        receiveImageViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        if (!StickerFile.getName().equals("")) {
            Picasso.get().load(StickerFile).into(receiveImageViewHolder.Sticker);
        }
        Picasso.get().load(MY_IP_ADDRESS + "/file/" + message.getMessage())
                .resize(500, 0)
                .into(receiveImageViewHolder.Photo);
        receiveImageViewHolder.Time.setText(time(message.getDeliverime()));
        if (postion != 0) {
            receiveImageViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                receiveImageViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
    }

    //設置接收檔案的ViewHolder
    private void setReceiveFileViewHolder(ReceiveFileViewHolder receiveFileViewHolder, Message message, int postion) {
        receiveFileViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        if (!StickerFile.getName().equals("")) {
            Picasso.get().load(StickerFile).into(receiveFileViewHolder.Sticker);
        }
        String FileData[] = message.getMessage().split(" ");
        receiveFileViewHolder.FileName.setText(FileData[0]);
        receiveFileViewHolder.FileSize.setText(FileData[1]);
        receiveFileViewHolder.Time.setText(time(message.getDeliverime()));
        if (postion != 0) {
            receiveFileViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                receiveFileViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
    }

    //設置接收語音通話的ViewHolder
    private void setReceiveAudioViewHolder(ReceiveAudioViewHolder receiveAudioViewHolder, Message message, int postion) {
        receiveAudioViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        if (!StickerFile.getName().equals("")) {
            Picasso.get().load(StickerFile).into(receiveAudioViewHolder.Sticker);
        }
        receiveAudioViewHolder.AudioTime.setText(message.getMessage());
        receiveAudioViewHolder.Time.setText(time(message.getDeliverime()));
        if (postion != 0) {
            receiveAudioViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                receiveAudioViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
    }

    //設置接收視訊通話的ViewHolder
    private void setReceiveFacetimeViewHolder(ReceiveFacetimeViewHolder receiveFacetimeViewHolder, Message message, int postion) {
        receiveFacetimeViewHolder.Date.setText(setDate(date(message.getCreatetime()), message));
        if (!StickerFile.getName().equals("")) {
            Picasso.get().load(StickerFile).into(receiveFacetimeViewHolder.Sticker);
        }
        receiveFacetimeViewHolder.Facetime_Time.setText(message.getMessage());
        receiveFacetimeViewHolder.Time.setText(time(message.getDeliverime()));
        if (postion != 0) {
            receiveFacetimeViewHolder.Date.setVisibility(View.GONE);
            if (message.isShowDate()) {
                receiveFacetimeViewHolder.Date.setVisibility(View.VISIBLE);
            }
        }
    }

    //將UTC的時間轉換成本地時間
    private String time(long utc) {
        String local = Long.toString(utc + Calendar.getInstance().getTimeZone().getRawOffset());
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            date = sdf.parse(sdf.format(Long.parseLong(local)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String localTime = sdf.format(date);
        return localTime;
    }

    //將UTC的時間轉換成本地日期
    private String date(long utc) {
        String local = Long.toString(utc + Calendar.getInstance().getTimeZone().getRawOffset());
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        try {
            date = sdf.parse(sdf.format(Long.parseLong(local)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String localTime = sdf.format(date);
        return localTime;
    }

    //新增聊天訊息
    public void addNewMessage(Message message) {
        item.add(message);
        notifyItemInserted(item.size() - 1);
    }

    //更新聊天訊息為已讀
    public void updateMessage(String msgID) {
        for (int i = 0; i < item.size(); i++) {
            if (item.get(i).getMsgID().equals(msgID)) {
                item.get(i).setRead(1);
                notifyItemChanged(i);
                break;
            }
        }
    }

    //設置聊天的日期
    private String setDate(String time, Message message) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        String MessageTimeSplit[] = time.split(" ");
        String LocalTimeSplit[] = MessageTimeSplit[0].split("\\.");
        if (MessageTimeSplit[0].equals(lastMessageDate)) {
            message.setShowDate(false);
        } else {
            message.setShowDate(true);
        }
        lastMessageDate = MessageTimeSplit[0];
        if (String.valueOf(year).equals(LocalTimeSplit[0])) {
            lastMessageYear = LocalTimeSplit[0];
            return LocalTimeSplit[1] + "." + LocalTimeSplit[2];
        } else {
            if (LocalTimeSplit[0].equals(lastMessageYear)) {
                lastMessageYear = LocalTimeSplit[0];
                return LocalTimeSplit[1] + "." + LocalTimeSplit[2];
            }
            lastMessageYear = LocalTimeSplit[0];
            return MessageTimeSplit[0];
        }
    }

    //獲取今天的日期
    private String getToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String year = String.format("%02d", cal.get(Calendar.YEAR));
        String mon = String.format("%02d", (cal.get(Calendar.MONTH) + 1));
        String day = String.format("%02d", cal.get(Calendar.DATE));
        return year + "." + mon + "." + day;
    }

    //設定item點擊
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    private class SendMessageViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView Message, Time, Read, Date;

        public SendMessageViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_my_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Message = (TextView) itemView.findViewById(R.id.chatroom_my_message);
            Time = (TextView) itemView.findViewById(R.id.chatroom_my_time);
            Read = (TextView) itemView.findViewById(R.id.chatroom_my_isRead);
        }

    }

    private class SendImageViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView Time, Read, Date;
        private ImageView Photo;

        public SendImageViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_my_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Photo = (ImageView) itemView.findViewById(R.id.chatroom_my_photo);
            Time = (TextView) itemView.findViewById(R.id.chatroom_my_time);
            Read = (TextView) itemView.findViewById(R.id.chatroom_my_isRead);
        }

    }

    private class SendFileViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView FileName, FileSize, Time, Read, Date;

        public SendFileViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_my_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            FileName = (TextView) itemView.findViewById(R.id.chatroom_my_fileName);
            FileSize = (TextView) itemView.findViewById(R.id.chatroom_my_fileSize);
            Time = (TextView) itemView.findViewById(R.id.chatroom_my_time);
            Read = (TextView) itemView.findViewById(R.id.chatroom_my_isRead);
        }

    }

    private class SendAudioViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView AudioTime, Time, Read, Date;

        public SendAudioViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_my_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            AudioTime = (TextView) itemView.findViewById(R.id.chatroom_my_audioTime);
            Time = (TextView) itemView.findViewById(R.id.chatroom_my_time);
            Read = (TextView) itemView.findViewById(R.id.chatroom_my_isRead);
        }

    }

    private class SendFacetimeViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView Facetime_Time, Time, Read, Date;

        public SendFacetimeViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_my_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Facetime_Time = (TextView) itemView.findViewById(R.id.chatroom_my_facetime_Time);
            Time = (TextView) itemView.findViewById(R.id.chatroom_my_time);
            Read = (TextView) itemView.findViewById(R.id.chatroom_my_isRead);
        }

    }

    private class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView Message, Time, Date;
        private CircleImageView Sticker;

        public ReceiveMessageViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_other_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Sticker = (CircleImageView) itemView.findViewById(R.id.chatroom_other_sticker);
            Message = (TextView) itemView.findViewById(R.id.chatroom_other_message);
            Time = (TextView) itemView.findViewById(R.id.chatroom_other_time);

        }

    }

    private class ReceiveImageViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView Time, Date;
        private ImageView Photo;
        private CircleImageView Sticker;

        public ReceiveImageViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_other_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Sticker = (CircleImageView) itemView.findViewById(R.id.chatroom_other_sticker);
            Photo = (ImageView) itemView.findViewById(R.id.chatroom_other_photo);
            Time = (TextView) itemView.findViewById(R.id.chatroom_other_time);
        }

    }

    private class ReceiveFileViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView FileName, FileSize, Time, Date;
        private CircleImageView Sticker;

        public ReceiveFileViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_other_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Sticker = (CircleImageView) itemView.findViewById(R.id.chatroom_other_sticker);
            FileName = (TextView) itemView.findViewById(R.id.chatroom_other_fileName);
            FileSize = (TextView) itemView.findViewById(R.id.chatroom_other_fileSize);
            Time = (TextView) itemView.findViewById(R.id.chatroom_other_time);
        }

    }

    private class ReceiveAudioViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView AudioTime, Time, Date;
        private CircleImageView Sticker;

        public ReceiveAudioViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_other_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Sticker = (CircleImageView) itemView.findViewById(R.id.chatroom_other_sticker);
            AudioTime = (TextView) itemView.findViewById(R.id.chatroom_other_audioTime);
            Time = (TextView) itemView.findViewById(R.id.chatroom_other_time);
        }

    }

    private class ReceiveFacetimeViewHolder extends RecyclerView.ViewHolder {
        private ViewStub timeStub;
        private TextView Facetime_Time, Time, Date;
        private CircleImageView Sticker;

        public ReceiveFacetimeViewHolder(View itemView) {
            super(itemView);
            timeStub = (ViewStub) itemView.findViewById(R.id.chatroom_other_timeStub);
            View view = timeStub.inflate();
            Date = (TextView) view.findViewById(R.id.chatroom_date);
            Sticker = (CircleImageView) itemView.findViewById(R.id.chatroom_other_sticker);
            Facetime_Time = (TextView) itemView.findViewById(R.id.chatroom_other_facetime_Time);
            Time = (TextView) itemView.findViewById(R.id.chatroom_other_time);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }
}
