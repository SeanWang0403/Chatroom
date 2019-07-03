package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.model.FriendInviteModel;
import com.sean.chatroom.view.FriendInviteView;

import java.util.List;

public class FriendInvitePresenter implements FriendInviteModel.FriendInviteListener {
    private FriendInviteView friendInviteView;
    private FriendInviteModel friendInviteModel;

    public FriendInvitePresenter(FriendInviteView friendInviteView, Context context) {
        this.friendInviteView = friendInviteView;
        this.friendInviteModel = new FriendInviteModel(context);
    }

    public String getUserID(){
        return friendInviteModel.getUserID();
    }

    public void getInviteData(){
        friendInviteModel.getInviteData(this);
    }

    public void updateFriendShip(String myUserID,String otherUserID,int postion){
        friendInviteModel.updateShip(myUserID,otherUserID,postion,this);
    }

    public void getNewFriendInvite(String userID,List<FriendItem>list){
        friendInviteModel.getFriendInvite(userID,list,this);
    }

    @Override
    public void inviteData(List<FriendItem> friendItems) {
        friendInviteView.setInviteData(friendItems);
    }

    @Override
    public void updateShipSuccess(int postion) {
        friendInviteView.updateAddFriend(postion);
    }

    @Override
    public void newFriendInvite(List<FriendItem> friendItems) {
        friendInviteView.updateNewFirendInvite(friendItems);
    }
}
