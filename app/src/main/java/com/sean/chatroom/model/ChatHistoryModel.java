package com.sean.chatroom.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.sean.chatroom.bean.ChatHistoryItem;
import com.sean.chatroom.database.dbManager;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.content.Context.MODE_PRIVATE;

public class ChatHistoryModel {
    private Context context;

    public ChatHistoryModel(Context context) {
        this.context = context;
    }

    public String getUserID() {
        SharedPreferences spf = context.getSharedPreferences("user", MODE_PRIVATE);
        return spf.getString("userID", "");
    }

    //從sqlite抓取最後的聊天紀錄
    public void getChatHisotry(final ChatHistoryListener chatHistoryListener) {
        dbManager.getInstance(context).readeOpen();
        dbManager.getInstance(context).queryChathistory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ChatHistoryItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ChatHistoryItem> chatHistoryItems) {
                        chatHistoryListener.ChathistoryData(chatHistoryItems);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dbManager.getInstance(context).close();
                    }
                });
    }

    public interface ChatHistoryListener {
        void ChathistoryData(List<ChatHistoryItem> list);
    }
}
