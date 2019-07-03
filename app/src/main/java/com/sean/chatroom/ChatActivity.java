package com.sean.chatroom;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sean.chatroom.adapter.ChatRecyclerAdapter;
import com.sean.chatroom.bean.Message;
import com.sean.chatroom.presenter.ChatPresenter;
import com.sean.chatroom.view.ChatView;

import java.io.File;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;


public class ChatActivity extends AppCompatActivity implements ChatView, View.OnClickListener {
    private ChatPresenter chatPresenter;
    private ChatRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private EditText input_message;
    private ImageButton send;
    private ImageView selectPhoto, selectFile, downloadCancel;
    private static String self = null;
    private static String chatter = null;
    private static String name = null;
    private static String sticker = null;
    private ViewStub call_select;
    private boolean click = false;
    private LinearLayout audio_block, facetime_block;
    private AlertDialog DownloadDialog;
    private ProgressBar downloadProgressBar;
    private List<Message> chatList;
    private NetBroadcastReceive receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        getIntentData();
        init();
        chatPresenter = new ChatPresenter(this, this);
        chatPresenter.getChat(chatter);
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.chatroom_recyclerview);
        input_message = (EditText) findViewById(R.id.input_msg);
        send = (ImageButton) findViewById(R.id.input_bt);
        call_select = (ViewStub) findViewById(R.id.chatroom_call_select);
        call_select.inflate();
        call_select.setVisibility(View.GONE);
        audio_block = (LinearLayout) findViewById(R.id.audio_call_block);
        facetime_block = (LinearLayout) findViewById(R.id.facetime_call_block);
        audio_block.setOnClickListener(this);
        facetime_block.setOnClickListener(this);
        selectPhoto = (ImageView) findViewById(R.id.input_photo);
        selectFile = (ImageView) findViewById(R.id.input_file);
        selectPhoto.setOnClickListener(this);
        selectFile.setOnClickListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chatroom_title);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(name);
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.toolbar_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHistory();
            }
        });
    }

    @Override
    public void setChatData(List<Message> list) {
        chatList = list;
        adapter = new ChatRecyclerAdapter(chatList, sticker, this);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        adapter.setOnItemClickListener(new ChatRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (chatList.get(postion).getType() == Message.TYPE_IMAGE_SENT || chatList.get(postion).getType() == Message.TYPE_FILE_SENT
                        || chatList.get(postion).getType() == Message.TYPE_IMAGE_RECEIVED || chatList.get(postion).getType() == Message.TYPE_FILE_RECEIVED) {
                    showDownloadDialog(chatList.get(postion));
                }
            }
        });
    }

    public void sendMsg(View view) {
        String msg = input_message.getText().toString().trim();
        if (!msg.equals("")) {
            messageData(msg, Message.TYPE_MESSAGE_SENT);
            input_message.setText("");
        }
    }

    private void addNesMessage(Message message) {
        if (message.getType() == Message.TYPE_MESSAGE_SENT || message.getType() == Message.TYPE_IMAGE_SENT ||
                message.getType() == Message.TYPE_FILE_SENT || message.getType() == Message.TYPE_AUDIO_SENT ||
                message.getType() == Message.TYPE_FACETIME_SENT || message.getReceiver().equals(self)) {
            chatPresenter.saveMessage(message, chatter);//這邊的chatter 是 room
            adapter.addNewMessage(message);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    public void receiveMessage(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getReceiver().equals(self)) {
                    addNesMessage(message);
                }
            }
        });
        chatPresenter.callReadMessage(message.getSender(), message.getMsgID());
    }

    @Override
    public void updateMessage(final String receiver, final String msgID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (receiver.equals(self)) {
                    adapter.updateMessage(msgID);
                    chatPresenter.updateReadMessage(msgID);
                }
            }
        });
    }

    @Override
    public void msgDeliver(Message message) {
        addNesMessage(message);
    }

    private void callFaceTime() {
        call_select.setVisibility(View.GONE);
        click = false;
        chatPresenter.callFaceTime(self, chatter);
        Intent intent = new Intent(this, ChatVideoCallActivity.class);
        intent.putExtra("type", "caller");
        intent.putExtra("self", self);
        intent.putExtra("callee", chatter);
        startActivityForResult(intent, 777);
    }

    private void callAudio() {
        call_select.setVisibility(View.GONE);
        click = false;
        chatPresenter.callAudio(self, chatter);
        Intent intent = new Intent(this, ChatAudioCallActivity.class);
        intent.putExtra("type", "caller");
        intent.putExtra("self", self);
        intent.putExtra("callee", chatter);
        intent.putExtra("sticker", sticker);
        startActivityForResult(intent, 888);
    }

    @Override
    public void startFaceTime(String caller, String receiver) {
        if (receiver.equals(self)) {
            getPermission("facetime", 258);
        }

    }

    private void faceTime() {
        call_select.setVisibility(View.GONE);
        click = false;
        Intent intent = new Intent(this, ChatVideoCallActivity.class);
        intent.putExtra("type", "callee");
        intent.putExtra("self", self);
        intent.putExtra("caller", chatter);
        startActivity(intent);
    }

    @Override
    public void startAudio(String caller, String receiver) {
        if (receiver.equals(self)) {
            getPermission("audio", 148);

        }
    }

    private void audio() {
        call_select.setVisibility(View.GONE);
        click = false;
        Intent intent = new Intent(this, ChatAudioCallActivity.class);
        intent.putExtra("type", "callee");
        intent.putExtra("self", self);
        intent.putExtra("caller", chatter);
        intent.putExtra("sticker", sticker);
        startActivity(intent);
    }

    private void getIntentData() {
        if (self == null) {
            self = getIntent().getExtras().getString("self");
        }
        if (chatter == null) {
            chatter = getIntent().getExtras().getString("chatter");
        }
        if (name == null) {
            name = getIntent().getExtras().getString("name");
        }
        if (sticker == null) {
            sticker = getIntent().getExtras().getString("sticker");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.title_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_favorite:
                if (click) {
                    call_select.setVisibility(View.GONE);
                    click = false;
                } else {
                    call_select.setVisibility(View.VISIBLE);
                    click = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio_call_block:
                getPermission("audio", 147);
                break;
            case R.id.facetime_call_block:
                getPermission("facetime", 257);
                break;
            case R.id.input_photo:
                openGallery();
                break;
            case R.id.input_file:
                selectFile();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 567) {
                chatPresenter.uploadFile(FileUtils.getFilePathByUri(this, data.getData()));
            } else if (requestCode == 999) {
                chatPresenter.uploadFile(FileUtils.getFilePathByUri(this, data.getData()));
            } else if (requestCode == 888) {
                Bundle bundleResult = data.getExtras();
                messageData(bundleResult.getString("audioTime"), Message.TYPE_AUDIO_SENT);
            } else if (requestCode == 777) {
                Bundle bundleResult = data.getExtras();
                messageData(bundleResult.getString("facetime_Time"), Message.TYPE_FACETIME_SENT);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 567);
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 999);
    }

    private void getPermission(String type, int request) {
        if (type.equals("audio")) {
            if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, request);
            } else if (request == 147) {
                callAudio();
            } else {
                audio();
            }
        } else if (type.equals("facetime")) {
            if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA, RECORD_AUDIO}, request);
            } else if (request == 257) {
                callFaceTime();
            } else {
                faceTime();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 147:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callAudio();
                } else {
                    Toast.makeText(this, "沒有權限無法進行語音通話!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 148:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    audio();
                } else {
                    Toast.makeText(this, "沒有權限無法進行語音通話!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 257:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    callFaceTime();
                } else {
                    Toast.makeText(this, "沒有權限無法進行視訊聊天!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 258:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    faceTime();
                } else {
                    Toast.makeText(this, "沒有權限無法進行視訊聊天!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void messageData(String msg, int type) {
        Message message = new Message(self, chatter, msg, type);
        chatPresenter.sendMessage(message);
    }

    //發送檔案的訊息
    private void messageData(String msg, File imageFile, int type) {
        Message message = new Message(self, chatter, msg, imageFile, type);
        chatPresenter.sendMessage(message);
    }

    @Override
    public void uploadFileFinish(String fileData) {
        messageData(fileData, Message.TYPE_FILE_SENT);
    }

    @Override
    public void uploaImageFinish(String fileName, File image) {
        messageData(fileName, image, Message.TYPE_IMAGE_SENT);
    }

    private void showDownloadDialog(final Message message) {
        View view = getLayoutInflater().inflate(R.layout.dialog_downloadfile, null);
        DownloadDialog = new AlertDialog.Builder(this).setView(view).create();
        DownloadDialog.show();
        downloadProgressBar = (ProgressBar) view.findViewById(R.id.chatroom_downloadProgress);
        downloadCancel = (ImageView) view.findViewById(R.id.chatroom_downloadCancel);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chatPresenter.downloadFIle(message.getMessage(), message.getType());
            }
        }, 1000);
        downloadCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPresenter.cancelDownload();
                DownloadDialog.dismiss();
            }
        });
    }

    @Override
    public void downloadProgress(int progress) {
        downloadProgressBar.setProgress(progress);
        if (progress == 100) {
            DownloadDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNetBroadcastReceive();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void setNetBroadcastReceive() {
        receiver = new NetBroadcastReceive();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(receiver, filter);
        receiver.setNetConnectedListener(new NetBroadcastReceive.NetConnectedListener() {
            @Override
            public void netContent(boolean isConnected) {
                if (isConnected) {
                    send.setClickable(true);
                    audio_block.setClickable(true);
                    facetime_block.setClickable(true);
                } else {
                    send.setClickable(false);
                    audio_block.setClickable(false);
                    facetime_block.setClickable(false);
                    Toast.makeText(ChatActivity.this, "沒有網路無法發送訊息與通話!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //儲存最後的聊天訊息
    private void saveHistory() {
        if (chatList.size() != 0) {
            int lastChat = chatList.size() - 1;
            String msg = null;
            String time = null;
            switch (chatList.get(lastChat).getType()) {
                case Message.TYPE_MESSAGE_SENT:
                    msg = chatList.get(lastChat).getMessage();
                    time = String.valueOf(chatList.get(lastChat).getCreatetime());
                    break;
                case Message.TYPE_IMAGE_SENT:
                    msg = "發送圖片";
                    time = String.valueOf(chatList.get(lastChat).getCreatetime());
                    break;
                case Message.TYPE_FILE_SENT:
                    msg = "傳送檔案";
                    time = String.valueOf(chatList.get(lastChat).getCreatetime());
                    break;
                case Message.TYPE_AUDIO_SENT:
                    msg = "語音通話";
                    time = String.valueOf(chatList.get(lastChat).getCreatetime());
                    break;
                case Message.TYPE_FACETIME_SENT:
                    msg = "視訊通話";
                    time = String.valueOf(chatList.get(lastChat).getCreatetime());
                    break;
                case Message.TYPE_MESSAGE_RECEIVED:
                    msg = chatList.get(lastChat).getMessage();
                    time = String.valueOf(chatList.get(lastChat).getDeliverime());
                    break;
                case Message.TYPE_IMAGE_RECEIVED:
                    msg = name + "發送圖片";
                    time = String.valueOf(chatList.get(lastChat).getDeliverime());
                    break;
                case Message.TYPE_FILE_RECEIVED:
                    msg = name + "傳送檔案";
                    time = String.valueOf(chatList.get(lastChat).getDeliverime());
                    break;
                case Message.TYPE_AUDIO_RECEIVED:
                    msg = "和" + name + "語音通話";
                    time = String.valueOf(chatList.get(lastChat).getDeliverime());
                    break;
                case Message.TYPE_FACETIME_RECEIVED:
                    msg = "和" + name + "視訊通話";
                    time = String.valueOf(chatList.get(lastChat).getDeliverime());
                    break;
            }
            chatPresenter.saveHistory(chatter, sticker, name, msg, time);
        }
    }

    @Override
    public void saveHistorySuccess() {
        chatPresenter.socketDisconnect();
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        intent.putExtra("type", "chat");
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveHistory();
        }
        return true;
    }
}