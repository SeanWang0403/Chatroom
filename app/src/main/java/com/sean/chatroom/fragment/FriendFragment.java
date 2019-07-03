package com.sean.chatroom.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sean.chatroom.ChatActivity;
import com.sean.chatroom.FriendRemarkUpdateActivity;
import com.sean.chatroom.R;
import com.sean.chatroom.adapter.FriendAdapter;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.presenter.FriendPresenter;
import com.sean.chatroom.presenter.FriendRemarkUpdatePresenter;
import com.sean.chatroom.view.FriendView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendFragment extends Fragment implements FriendView {
    private ListView listView;
    private FriendPresenter friendPresenter;
    private FriendAdapter adapter;
    private String userID;
    private List<FriendItem> friendItems;
    private TextView empty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        friendPresenter = new FriendPresenter(this, getContext());
        userID = friendPresenter.getUserID();
        friendPresenter.getDBFriend();
        setItemClick();
    }

    private void init(View view) {
        listView = (ListView) view.findViewById(R.id.friend_list);
        empty = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(empty);
    }

    private void setItemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view2 = inflater.inflate(R.layout.dialog_friend, null);
                new AlertDialog.Builder(getContext()).setView(view2).show();
                ImageView background = (ImageView) view2.findViewById(R.id.frienddetail_background);
                CircleImageView sticker = (CircleImageView) view2.findViewById(R.id.frienddetail_sticker);
                TextView name = (TextView) view2.findViewById(R.id.frienddetail_name);
                TextView remark = (TextView) view2.findViewById(R.id.frienddetail_remark);
                ImageView chat = (ImageView) view2.findViewById(R.id.frienddetail_chat);
                name.setText(friendItems.get(i).getName());
                if (!friendItems.get(i).getRemark().equals("")) {
                    remark.setText(friendItems.get(i).getRemark());
                }
                if (!friendItems.get(i).getSticker().equals("")) {
                    File Sticker = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/sticker/" + friendItems.get(i).getSticker() + ".jpg");
                    Picasso.get().load(Sticker).into(sticker);
                }
                if (!friendItems.get(i).getBackground().equals("")) {
                    File Background = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/background/" + friendItems.get(i).getBackground() + ".jpg");
                    Picasso.get().load(Background).into(background);
                }
                chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goChat(friendItems.get(i));
                    }
                });
                remark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateRemark(friendItems.get(i).getUserID(), friendItems.get(i).getRemark());
                    }
                });

            }
        });
    }

    @Override
    public void getFriend(List<FriendItem> list) {
        friendItems = list;
        adapter = new FriendAdapter(getContext());
        adapter.setList(friendItems);
        listView.setAdapter(adapter);
        if (checkNetWork()) {
            friendPresenter.getFriendData(userID, list);
        }
    }

    //更新好友
    @Override
    public void updateData(List<FriendItem> list) {
        adapter.refresh(list);
    }

    //檢查是否有網路
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

    private void goChat(FriendItem item) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("self", userID);
        intent.putExtra("chatter", item.getUserID());
        intent.putExtra("name", item.getName());
        intent.putExtra("sticker", item.getSticker());
        startActivity(intent);
    }

    //更新好友備註
    private void updateRemark(String friendID, String remark) {
        Intent intent = new Intent(getActivity(), FriendRemarkUpdateActivity.class);
        intent.putExtra("friendID", friendID);
        intent.putExtra("remark", remark);
        startActivity(intent);
    }
}
