package com.sean.chatroom.view;

import com.sean.chatroom.bean.Message;

import java.io.File;
import java.util.List;


public interface ChatView {
    void setChatData(List<Message> list);

    void msgDeliver(Message message);

    void receiveMessage(Message message);

    void updateMessage(String receiver, String msgID);

    void startFaceTime(String caller,String receiver);

    void startAudio(String caller,String receiver);

    void uploadFileFinish(String fileData);

    void uploaImageFinish(String fileName, File image);

    void downloadProgress(int progress);

    void saveHistorySuccess();
}
