package com.sean.chatroom.presenter;

import android.content.Context;
import android.util.Log;

import com.sean.chatroom.bean.Message;
import com.sean.chatroom.model.ChatModel;
import com.sean.chatroom.view.ChatView;

import java.io.File;
import java.util.List;


public class ChatPresenter implements ChatModel.ModelCallBack {
    private ChatModel chatModel;
    private ChatView chatView;

    public ChatPresenter(ChatView chatView, Context context) {
        this.chatView = chatView;
        this.chatModel = new ChatModel(this, context);
    }

    public void getChat(String room) {
        chatModel.getChat(room, this);
    }

    public void sendMessage(Message message) {
        chatModel.sendMessage(message);
    }

    @Override
    public void msgDeliver(Message message) {
        chatView.msgDeliver(message);
    }

    @Override
    public void receiveMessage(Message message) {
        chatView.receiveMessage(message);
    }

    public void callReadMessage(String receiver, String msgID) {
        chatModel.callReadMessage(receiver, msgID);
    }

    public void downloadFIle(String fileName, int type) {
        chatModel.DownloadFile(fileName, type, this);
    }

    public void uploadFile(String path) {
        String fileType = getFileSubName(path);
        if (fileType.equals("jpg") || fileType.equals("png")) {
            chatModel.UploadImage(path, this);
        } else {
            chatModel.UploadFile(path, this);
        }
    }

    private String getFileSubName(String path) {
        String file[] = path.split("\\.");
        return file[1];
    }

    @Override
    public void getChatData(List<Message> list) {
        chatView.setChatData(list);
    }

    public void saveMessage(Message message, String room) {
        chatModel.intsertMessage(message, room);
    }

    @Override
    public void readMessage(String receiver, String msgID) {
        chatView.updateMessage(receiver, msgID);
    }

    public void updateReadMessage(String msgID) {
        chatModel.updateReadMessage(msgID);
    }

    @Override
    public void progress(int progress) {
        chatView.downloadProgress(progress);
    }

    public void callFaceTime(String caller, String receiver) {
        chatModel.callFaceTime(caller, receiver);
    }

    public void callAudio(String caller, String receiver) {
        chatModel.callAudio(caller, receiver);
    }

    @Override
    public void facetime(String caller, String receiver) {
        chatView.startFaceTime(caller, receiver);
    }

    @Override
    public void audio(String caller, String receiver) {
        chatView.startAudio(caller, receiver);
    }

    @Override
    public void uploadFileFinish(String fileData) {
        chatView.uploadFileFinish(fileData);
    }

    @Override
    public void uploadImageFinish(String imageName, File image) {
        chatView.uploaImageFinish(imageName, image);

    }

    public void cancelDownload() {
        chatModel.cancelDownload();
    }

    public void saveHistory(String room, String sticker, String name, String msg, String time) {
        chatModel.saveHistory(room, sticker, name, msg, time, this);
    }

    @Override
    public void saveHistorySuccess() {
        chatView.saveHistorySuccess();
    }
    public void socketDisconnect(){
        chatModel.socketDisconnect();
    }
}
