package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.model.FriendRemarkUpdateModel;
import com.sean.chatroom.model.UserDataUpdateModel;
import com.sean.chatroom.view.FriendRemarkUpdateView;
import com.sean.chatroom.view.UserDataUpdateView;

public class FriendRemarkUpdatePresenter implements FriendRemarkUpdateModel.FriendRemarkUpdateListener {
    private FriendRemarkUpdateModel friendRemarkUpdateModel;
    private FriendRemarkUpdateView friendRemarkUpdateView;

    public FriendRemarkUpdatePresenter(FriendRemarkUpdateView friendRemarkUpdateView, Context context) {
        this.friendRemarkUpdateView = friendRemarkUpdateView;
        this.friendRemarkUpdateModel = new FriendRemarkUpdateModel(context);
    }

    public void updateRemark(String userID, String remark) {
        friendRemarkUpdateModel.updateFreindRemark(userID, remark, this);
    }

    @Override
    public void Success() {
        friendRemarkUpdateView.onSuccess();
    }
}
