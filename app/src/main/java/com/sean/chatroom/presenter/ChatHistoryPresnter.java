package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.bean.ChatHistoryItem;
import com.sean.chatroom.model.ChatHistoryModel;
import com.sean.chatroom.view.ChatHistoryView;

import java.util.List;

public class ChatHistoryPresnter implements ChatHistoryModel.ChatHistoryListener {
    private ChatHistoryView chatHistoryView;
    private ChatHistoryModel chatHistoryModel;

    public ChatHistoryPresnter(ChatHistoryView chatHistoryView, Context context) {
        this.chatHistoryView = chatHistoryView;
        chatHistoryModel = new ChatHistoryModel(context);
    }

    public String getUserID() {
        return chatHistoryModel.getUserID();
    }

    public void getChatHistory() {
        chatHistoryModel.getChatHisotry(this);
    }

    @Override
    public void ChathistoryData(List<ChatHistoryItem> list) {
        chatHistoryView.setChathistoryData(list);
    }
}
