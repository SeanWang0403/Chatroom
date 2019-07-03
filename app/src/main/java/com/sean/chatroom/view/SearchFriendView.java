package com.sean.chatroom.view;

import com.sean.chatroom.bean.FriendItem;

public interface SearchFriendView {
    void FriendData(boolean myself, boolean exist, FriendItem friendItem);
    void addNewFiendSuccess();
    void SearchError();
    void NoFindUser();
}
