package com.sean.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.sean.chatroom.presenter.FriendRemarkUpdatePresenter;
import com.sean.chatroom.view.FriendRemarkUpdateView;

public class FriendRemarkUpdateActivity extends AppCompatActivity implements FriendRemarkUpdateView {
    private EditText input;
    private String FriendID, old_remark;
    private FriendRemarkUpdatePresenter friendRemarkUpdatePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata_update);
        FriendID = getIntent().getExtras().getString("friendID");
        old_remark = getIntent().getExtras().getString("remark");
        init();
        friendRemarkUpdatePresenter = new FriendRemarkUpdatePresenter(this, this);

    }

    private void init() {
        input = (EditText) findViewById(R.id.userupdate_input);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("備註");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.toolbar_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFriendHome();
            }
        });
        input.setText(old_remark);
    }

    public void saveNewData(View view) {
        String remark = input.getText().toString().trim();

        if (!old_remark.equals(remark)){
            friendRemarkUpdatePresenter.updateRemark(FriendID, remark);
        }else {
           backFriendHome();
        }
    }

    private void backFriendHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("type", "friend");
        startActivity(intent);
    }

    @Override
    public void onSuccess() {
        backFriendHome();
    }
}
