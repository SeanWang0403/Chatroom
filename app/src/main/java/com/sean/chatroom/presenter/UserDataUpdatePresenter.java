package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.model.UserDataUpdateModel;
import com.sean.chatroom.view.UserDataUpdateView;

public class UserDataUpdatePresenter implements UserDataUpdateModel.UserDataUpdateListener {
    private UserDataUpdateModel userDataUpdateModel;
    private UserDataUpdateView userDataUpdateView;

    public UserDataUpdatePresenter(UserDataUpdateView userDataUpdateView, Context context) {
        this.userDataUpdateView = userDataUpdateView;
        this.userDataUpdateModel = new UserDataUpdateModel(context);
    }

    public void updateUserData(String userID, String type, String value){
        userDataUpdateModel.updateUserData(userID,type,value,this);
    }

    @Override
    public void Success() {
        userDataUpdateView.onSuccess();
    }
}
