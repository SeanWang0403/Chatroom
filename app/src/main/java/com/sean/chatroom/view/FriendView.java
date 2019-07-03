package com.sean.chatroom.view;

import com.sean.chatroom.bean.FriendItem;

import java.util.List;

public interface FriendView {
    void getFriend(List<FriendItem> list);

    void updateData(List<FriendItem> list);
}
