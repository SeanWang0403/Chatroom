package com.sean.chatroom;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.king.zxing.CaptureActivity;
import com.king.zxing.ViewfinderView;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.presenter.QRcodeScannerPresenter;
import com.sean.chatroom.view.QRcodeScannerView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sean.chatroom.api.ApiClient.MY_IP_ADDRESS;

public class QRcodeScannerActivity extends CaptureActivity implements QRcodeScannerView {
    private QRcodeScannerPresenter qRcodeScannerPresenter;
    private CircleImageView sticker;
    private TextView name, Myself;
    private Button event;
    private String userID, qrcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qRcodeScannerPresenter = new QRcodeScannerPresenter(this, this);
        userID = qRcodeScannerPresenter.getUserID();
        qrcode = getIntent().getExtras().getString("qrcode");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qecode_scanner;
    }

    @Override
    public void initUI() {
        super.initUI();
        SurfaceView surfaceView = findViewById(getSurfaceViewId());
        ViewfinderView viewfinderView = findViewById(getViewfinderViewId());
        getCaptureHelper().continuousScan(true);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("行動條碼掃描器");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.toolbar_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backUserInfoHome();
            }
        });
    }

    @Override
    public int getViewfinderViewId() {
        return R.id.qrcode_scanner_viewfinderView;
    }

    @Override
    public int getSurfaceViewId() {
        return R.id.qrcode_scanner_surfaceView;
    }

    @Override
    public boolean onResultCallback(String result) {
        if (checkNetWork()) {
            qRcodeScannerPresenter.SearchFriend(result);
        } else {
            Toast.makeText(this, "需要網路才能搜尋", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void FriendData(boolean myself, boolean exist, FriendItem friendItem) {
        setContentView(R.layout.activity_qrcode_scanner_result);
        result_init();
        setResult(myself, exist, friendItem);
    }

    private void result_init() {
        sticker = (CircleImageView) findViewById(R.id.addFriend_sticker);
        name = (TextView) findViewById(R.id.addFriend_name);
        Myself = (TextView) findViewById(R.id.addFriend_myself);
        event = (Button) findViewById(R.id.addFriend_event);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("自動加入好友");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.toolbar_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backUserInfoHome();
            }
        });

    }

    private void setResult(boolean myself, boolean exist, final FriendItem friendItem) {
        if (!friendItem.getSticker().equals("")) {
            Picasso.get().load(MY_IP_ADDRESS+"/sticker/" + friendItem.getSticker() + ".jpg").into(sticker);
        }
        name.setText(friendItem.getName());
        if (myself) {
            Myself.setVisibility(View.VISIBLE);
            event.setVisibility(View.GONE);
        } else {
            if (exist) {
                event.setText("聊天");
                event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Chat(friendItem);
                    }
                });
            } else {
                event.setText("加入");
                event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qRcodeScannerPresenter.addNewFriend(userID, friendItem);
                    }
                });
            }
        }
    }

    private void Chat(FriendItem friendItem) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("self", userID);
        intent.putExtra("chatter", friendItem.getUserID());
        intent.putExtra("name", friendItem.getName());
        intent.putExtra("sticker", friendItem.getSticker());
        startActivity(intent);
    }

    @Override
    public void addNewFiendSuccess() {
        backUserInfoHome();
    }

    private boolean checkNetWork() {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isConnected();
        } else {
            return false;
        }
    }

    public void showQRcode(View view) {
        Intent intent = new Intent(this, QRcodeActivity.class);
        intent.putExtra("qrcode", qrcode);
        startActivity(intent);
    }

    @Override
    public void SearchError() {
        Toast.makeText(this, "伺服器忙碌中，稍後再試!!", Toast.LENGTH_SHORT).show();
    }

    private void backUserInfoHome() {
        Intent intent = new Intent(QRcodeScannerActivity.this, HomeActivity.class);
        intent.putExtra("type", "userInfo");
        startActivity(intent);
    }
}
