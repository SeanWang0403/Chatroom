package com.sean.chatroom.view;

import com.sean.chatroom.bean.FriendItem;

import java.util.List;

public interface FriendInviteView {
    void setInviteData(List<FriendItem> friendItems);

    void updateAddFriend(int postion);

    void updateNewFirendInvite(List<FriendItem> friendItems);
}
