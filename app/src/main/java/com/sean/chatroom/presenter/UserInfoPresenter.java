package com.sean.chatroom.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.sean.chatroom.bean.UserInfoItem;
import com.sean.chatroom.model.UserInfoModel;
import com.sean.chatroom.view.UserInfoView;

import java.util.List;

public class UserInfoPresenter implements UserInfoModel.UserInfoListener {
    private UserInfoModel userInfoModel;
    private UserInfoView userInfoView;

    public UserInfoPresenter(UserInfoView userInfoView, Context context) {
        this.userInfoView = userInfoView;
        userInfoModel = new UserInfoModel(context);
    }

    public void getUserPhoto(){
        userInfoModel.getUserPhoto(this);
    }

    public void getUserData() {
        userInfoModel.getUserData(this);
    }

    public void updateBackground(String userID,String oldBackground ,Bitmap bitmap){
        userInfoModel.updateBackground(userID,oldBackground,bitmap,this);
    }

    @Override
    public void getUserData(List<UserInfoItem> items) {
        userInfoView.setAdapter(items);
    }

    @Override
    public void onFinish(String photoName) {
        userInfoView.onUpdateBackground(photoName);
    }

    @Override
    public void getUserPhoto(String userID, String sticker, String background) {
        userInfoView.getUserPhoto(userID,sticker,background);
    }

    public void updateIdSearch(String userID,String type,boolean check){
        userInfoModel.updateIdSearch(userID,type,check,this);
    }

    @Override
    public void idUpdateFinish() {
        userInfoView.onUserDataUpdate();
    }
}
