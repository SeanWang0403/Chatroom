package com.sean.chatroom.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sean.chatroom.R;
import com.sean.chatroom.adapter.FriendInviteAdapter;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.presenter.FriendInvitePresenter;
import com.sean.chatroom.view.FriendInviteView;

import java.util.List;

public class FriendInviteFragment extends Fragment implements FriendInviteView, FriendInviteAdapter.clickCallBack {
    private ListView listView;
    private FriendInvitePresenter friendInvitePresenter;
    private FriendInviteAdapter friendInviteAdapter;
    private String userID;
    private TextView empty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friendinvite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        friendInvitePresenter = new FriendInvitePresenter(this, getContext());
        userID = friendInvitePresenter.getUserID();
        friendInvitePresenter.getInviteData();
    }

    private void init(View view) {
        listView = (ListView) view.findViewById(R.id.friendinvite_listview);
        empty = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(empty);
    }

    @Override
    public void setInviteData(List<FriendItem> friendItems) {
        friendInviteAdapter = new FriendInviteAdapter(friendItems, getContext());
        listView.setAdapter(friendInviteAdapter);
        friendInviteAdapter.setClickCallBack(this);
        if (checkNetWork()) {
            friendInvitePresenter.getNewFriendInvite(userID, friendItems);
        }
    }

    @Override
    public void clickItem(String otherUserID, int postion) {
        if (checkNetWork()) {
            friendInvitePresenter.updateFriendShip(userID, otherUserID, postion);
        } else {
            Toast.makeText(getContext(), "需要網路才能新增好友!!", Toast.LENGTH_SHORT).show();
        }
    }

    //將添加為好友的人從列表中移除
    @Override
    public void updateAddFriend(int postion) {
        friendInviteAdapter.addRefresh(postion);
    }

    private boolean checkNetWork() {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isConnected();
        } else {
            return false;
        }
    }

    //更新從網路抓取下來的好友邀請列表
    @Override
    public void updateNewFirendInvite(List<FriendItem> friendItems) {
        friendInviteAdapter.notifyDataSetChanged();
    }
}
