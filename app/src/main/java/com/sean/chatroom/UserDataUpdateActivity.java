package com.sean.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.sean.chatroom.presenter.UserDataUpdatePresenter;
import com.sean.chatroom.view.UserDataUpdateView;

public class UserDataUpdateActivity extends AppCompatActivity implements UserDataUpdateView {
    private EditText input;
    private String userID, title, type, old_value;
    private UserDataUpdatePresenter userDataUpdatePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata_update);
        userID = getIntent().getExtras().getString("userID");
        title = getIntent().getExtras().getString("title");
        type = getIntent().getExtras().getString("type");
        old_value = getIntent().getExtras().getString("value");
        init();
        userDataUpdatePresenter = new UserDataUpdatePresenter(this, this);
    }

    private void init() {
        input = (EditText) findViewById(R.id.userupdate_input);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(title);
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.toolbar_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backUserInfo();
            }
        });
        input.setText(old_value);
    }

    public void saveNewData(View view) {
        String value = input.getText().toString().trim();
        if (!old_value.equals(value)){
            userDataUpdatePresenter.updateUserData(userID, type, value);
        }else {
            backUserInfo();
        }
    }

    @Override
    public void onSuccess() {
        backUserInfo();
    }

    private void backUserInfo() {
        Intent intent = new Intent(UserDataUpdateActivity.this, HomeActivity.class);
        intent.putExtra("type","userInfo");
        startActivity(intent);
    }
}
