package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.model.FriendModel;
import com.sean.chatroom.view.FriendView;

import java.util.List;

public class FriendPresenter implements FriendModel.FriendListener {
    private FriendView friendView;
    private FriendModel friendModel;

    public FriendPresenter(FriendView friendView, Context context) {
        this.friendView = friendView;
        this.friendModel = new FriendModel(context);
    }

    public String getUserID() {
        return friendModel.getUserID();
    }

    public void getDBFriend() {
        friendModel.getDBFriend(this);
    }

    public void getFriendData(String userID, List<FriendItem> list) {
        friendModel.getFriendData(userID, list, this);
    }

    @Override
    public void FriendData(List<FriendItem> list) {
        friendView.getFriend(list);
    }

    @Override
    public void updateData(List<FriendItem> list) {
        friendView.updateData(list);
    }

}
