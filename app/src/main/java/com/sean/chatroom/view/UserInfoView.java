package com.sean.chatroom.view;

import com.sean.chatroom.bean.UserInfoItem;

import java.util.List;

public interface UserInfoView {
    void getUserPhoto(String userID, String sticker, String background);
    void setAdapter(List<UserInfoItem> item);
    void onUpdateBackground(String photoName);
    void onUserDataUpdate();
}
