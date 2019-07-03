package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.model.SearchFriendModel;
import com.sean.chatroom.view.SearchFriendView;


public class SearchFriendPresenter implements SearchFriendModel.addFriendListener {
    private SearchFriendView searchFriendView;
    private SearchFriendModel searchFriendModel;

    public SearchFriendPresenter(SearchFriendView searchFriendView, Context context) {
        this.searchFriendView = searchFriendView;
        this.searchFriendModel = new SearchFriendModel(context);
    }

    public String getUserID() {
        return searchFriendModel.getUserID();
    }

    public void SearchNewFriend(String type, String user) {
        searchFriendModel.SearchNewFriend(type, user, this);
    }

    public void addNewFriend(String myID,FriendItem friendItem) {
        searchFriendModel.addNewFriend(myID,friendItem, this);
    }

    @Override
    public void FriendData(boolean myself, boolean exist, FriendItem friendItem) {
        searchFriendView.FriendData(myself, exist, friendItem);
    }

    @Override
    public void addNewFriendSuccess() {
        searchFriendView.addNewFiendSuccess();
    }

    @Override
    public void SearchError() {
        searchFriendView.SearchError();
    }

    @Override
    public void NoFindUser() {
        searchFriendView.NoFindUser();
    }
}
