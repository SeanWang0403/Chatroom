package com.sean.chatroom;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sean.chatroom.fragment.ChatHistoryFragment;
import com.sean.chatroom.fragment.FriendFragment;
import com.sean.chatroom.fragment.FriendInviteFragment;
import com.sean.chatroom.fragment.SearchFriendFragment;
import com.sean.chatroom.fragment.UserInfoFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String type = getIntent().getExtras().getString("type");
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_container, fragment)
                    .commit();
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
                fragment = new FriendFragment();
                break;
            case R.id.navigation_chat:
                myToolbar.setTitle("聊天");
                fragment = new ChatHistoryFragment();
                break;
            case R.id.navigation_friendInvite:
                myToolbar.setTitle("朋友邀請");
                fragment = new FriendInviteFragment();
                break;
            case R.id.navigation_searchFriend:
                myToolbar.setTitle("搜尋朋友");
                fragment = new SearchFriendFragment();
                break;
            case R.id.navigation_userinfo:
                myToolbar.setTitle("個人資料");
                fragment = new UserInfoFragment();
                break;
        }
        return loadFragment(fragment);
    }
}
