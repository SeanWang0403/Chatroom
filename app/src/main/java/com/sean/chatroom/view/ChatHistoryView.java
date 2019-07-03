package com.sean.chatroom.view;

import com.sean.chatroom.bean.ChatHistoryItem;

import java.util.List;

public interface ChatHistoryView {
    void setChathistoryData(List<ChatHistoryItem> list);
}
