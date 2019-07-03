package com.sean.chatroom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sean.chatroom.ChatActivity;
import com.sean.chatroom.bean.ChatHistoryItem;
import com.sean.chatroom.R;
import com.sean.chatroom.adapter.ChatHistoryAdapter;
import com.sean.chatroom.presenter.ChatHistoryPresnter;
import com.sean.chatroom.view.ChatHistoryView;

import java.util.List;

public class ChatHistoryFragment extends Fragment implements ChatHistoryView {
    private ListView listView;
    private ChatHistoryAdapter adapter;
    private ChatHistoryPresnter chatHistoryPresnter;
    private String userID;
    private TextView empty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chathistory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        chatHistoryPresnter = new ChatHistoryPresnter(this, getContext());
        userID = chatHistoryPresnter.getUserID();
        chatHistoryPresnter.getChatHistory();
    }

    private void init(View view) {
        listView = (ListView) view.findViewById(R.id.chathistory_listview);
        empty = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(empty);
    }

    @Override
    public void setChathistoryData(final List<ChatHistoryItem> list) {
        adapter = new ChatHistoryAdapter(getContext(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat(list.get(position));
            }
        });
    }

    private void Chat(ChatHistoryItem chatHistoryItem) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("self", userID);
        intent.putExtra("chatter", chatHistoryItem.getRoom());//room是聊天對象的userID
        intent.putExtra("name", chatHistoryItem.getName());
        intent.putExtra("sticker", chatHistoryItem.getSticker());
        startActivity(intent);
    }
}
