package com.sean.chatroom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.sean.chatroom.fragment.ChatHistoryFragment;
import com.sean.chatroom.fragment.FriendFragment;
import com.sean.chatroom.fragment.FriendInviteFragment;
import com.sean.chatroom.fragment.SearchFriendFragment;
import com.sean.chatroom.fragment.UserInfoFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private Toolbar myToolbar;
    private Map<String, Fragment> fragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String type = getIntent().getExtras().getString("type");
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        fragmentMap = new HashMap<>();
        Log.i("Sean", "onCreate: ");
        switch (type) {
            case "friend":
                myToolbar.setTitle("朋友");
                navView.setSelectedItemId(R.id.navigation_friend);
                break;
            case "chat":
                myToolbar.setTitle("聊天");
                navView.setSelectedItemId(R.id.navigation_chat);
                break;
            case "userInfo":
                myToolbar.setTitle("個人資料");
                navView.setSelectedItemId(R.id.navigation_userinfo);
                break;
        }
    }

    //fragment 加入很多個 重複add
    private boolean loadFragment(Fragment fragment) {
        Fragment from = getSupportFragmentManager().findFragmentById(R.id.home_container);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (from != null) {
                ft.hide(from);
            }
            if (!fragment.isAdded()) {
                ft.add(R.id.home_container, fragment);
            }
            ft.show(fragment);
            ft.commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_friend:
                myToolbar.setTitle("朋友");
                fragment = initFragment("朋友");
                break;
            case R.id.navigation_chat:
                myToolbar.setTitle("聊天");
                fragment = initFragment("聊天");
                break;
            case R.id.navigation_friendInvite:
                myToolbar.setTitle("朋友邀請");
                fragment = initFragment("朋友邀請");
                break;
            case R.id.navigation_searchFriend:
                myToolbar.setTitle("搜尋朋友");
                fragment = initFragment("搜尋朋友");
                break;
            case R.id.navigation_userinfo:
                myToolbar.setTitle("個人資料");
                fragment = initFragment("個人資料");
                break;
        }
        return loadFragment(fragment);
    }

    private Fragment initFragment(String fragmentType) {
        Fragment fragment = null;
        if (fragmentMap.get(fragmentType) == null) {
            switch (fragmentType) {
                case "朋友":
                    fragment = new FriendFragment();
                    fragmentMap.put("朋友", fragment);
                    break;
                case "聊天":
                    fragment = new ChatHistoryFragment();
                    fragmentMap.put("聊天", fragment);
                    break;
                case "朋友邀請":
                    fragment = new FriendInviteFragment();
                    fragmentMap.put("朋友邀請", fragment);
                    break;
                case "搜尋朋友":
                    fragment = new SearchFriendFragment();
                    fragmentMap.put("搜尋朋友", fragment);
                    break;
                case "個人資料":
                    fragment = new UserInfoFragment();
                    fragmentMap.put("個人資料", fragment);
                    break;
            }
        } else {
            fragment = fragmentMap.get(fragmentType);
        }
        return fragment;
    }
}
