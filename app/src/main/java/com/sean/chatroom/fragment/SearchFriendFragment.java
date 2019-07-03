package com.sean.chatroom.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sean.chatroom.ChatActivity;
import com.sean.chatroom.HomeActivity;
import com.sean.chatroom.R;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.presenter.SearchFriendPresenter;
import com.sean.chatroom.view.SearchFriendView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sean.chatroom.api.ApiClient.MY_IP_ADDRESS;

public class SearchFriendFragment extends Fragment implements SearchFriendView, View.OnClickListener {
    private SearchFriendPresenter searchFriendPresenter;
    private EditText searchValue;
    private RadioButton idSearch, phoneSearch;
    private RadioGroup GenderGroup;
    private ImageView search;
    private ViewStub SearchResultViewStub;
    private CircleImageView Sticker;
    private TextView Name, Myself, Nouser;
    private Button event;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addfriend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        searchFriendPresenter = new SearchFriendPresenter(this, getContext());
        userID = searchFriendPresenter.getUserID();
    }

    private void init(View view) {
        searchValue = (EditText) view.findViewById(R.id.addFriend_searchValue);
        idSearch = (RadioButton) view.findViewById(R.id.addFriend_idSearch);
        phoneSearch = (RadioButton) view.findViewById(R.id.addFriend_phoneSearch);
        search = (ImageView) view.findViewById(R.id.addFriend_Search);
        GenderGroup = (RadioGroup) view.findViewById(R.id.addFriend_searchType);
        search.setOnClickListener(this);
        idSearch.setOnClickListener(this);
        phoneSearch.setOnClickListener(this);
        Nouser = (TextView) view.findViewById(R.id.addFriend_Nouser);
        SearchResultViewStub = (ViewStub) view.findViewById(R.id.addFriend_result);
        SearchResultViewStub.inflate();
        SearchResultViewStub.setVisibility(View.GONE);
        Sticker = (CircleImageView) view.findViewById(R.id.addFriend_sticker);
        Name = (TextView) view.findViewById(R.id.addFriend_name);
        Myself = (TextView) view.findViewById(R.id.addFriend_myself);
        event = (Button) view.findViewById(R.id.addFriend_event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addFriend_idSearch:
                searchValue.setText("");
                searchValue.setHint("請輸入要搜尋的ID");
                break;
            case R.id.addFriend_phoneSearch:
                searchValue.setText("");
                searchValue.setHint("請輸入要搜尋的電話號碼");
                break;
            case R.id.addFriend_Search:
                if (checkNetWork()) {
                    setSearchData();
                } else {
                    Toast.makeText(getContext(), "需要網路才能搜尋!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //設置查詢的方法是用id還是phone
    private void setSearchData() {
        String searchType = null;
        String SearchValue = searchValue.getText().toString().trim();
        switch (GenderGroup.getCheckedRadioButtonId()) {
            case R.id.addFriend_idSearch:
                searchType = "id";
                break;
            case R.id.addFriend_phoneSearch:
                searchType = "phone";
                break;
        }
        searchFriendPresenter.SearchNewFriend(searchType, SearchValue);
    }

    @Override
    public void FriendData(boolean myself, boolean exist, final FriendItem friendItem) {
        Nouser.setVisibility(View.GONE);
        SearchResultViewStub.setVisibility(View.VISIBLE);
        if (!friendItem.getSticker().equals("")) {
            Picasso.get().load(MY_IP_ADDRESS + "/sticker/" + friendItem.getSticker() + ".jpg").into(Sticker);
        }
        Name.setText(friendItem.getName());
        if (myself) {
            Myself.setVisibility(View.VISIBLE);
            event.setVisibility(View.GONE);
        } else {
            if (exist) {
                Myself.setVisibility(View.GONE);
                event.setVisibility(View.VISIBLE);
                event.setText("聊天");
                event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Chat(friendItem);
                    }
                });
            } else {
                Myself.setVisibility(View.GONE);
                event.setVisibility(View.VISIBLE);
                event.setText("加入");
                event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchFriendPresenter.addNewFriend(userID, friendItem);
                    }
                });
            }
        }
    }

    private void Chat(FriendItem friendItem) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("self", userID);
        intent.putExtra("chatter", friendItem.getUserID());
        intent.putExtra("name", friendItem.getName());
        intent.putExtra("sticker", friendItem.getSticker());
        startActivity(intent);
    }

    @Override
    public void addNewFiendSuccess() {
       Intent intent=new Intent(getActivity(), HomeActivity.class);
       intent.putExtra("type","friend");
       startActivity(intent);
    }

    @Override
    public void SearchError() {
        Toast.makeText(getContext(), "伺服器忙碌中，稍後再試!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void NoFindUser() {
        Nouser.setVisibility(View.VISIBLE);
        SearchResultViewStub.setVisibility(View.GONE);
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
}
