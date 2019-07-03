package com.sean.chatroom.presenter;

import com.sean.chatroom.model.ChatCallModel;
import com.sean.chatroom.view.ChatCallView;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class ChatCallPresenter implements ChatCallModel.callback {
    private ChatCallView chatCallView;
    private ChatCallModel chatRtcModel;

    public ChatCallPresenter(ChatCallView chatCallView) {
        this.chatCallView = chatCallView;
        this.chatRtcModel = new ChatCallModel(this);
    }

    public void onCreateSuccess(SessionDescription sessionDescription, String receiver){
        chatRtcModel.onCreateSuccess(sessionDescription,receiver);
    }
    public void onIceCandidate(IceCandidate iceCandidate, String receiver){
        chatRtcModel.onIceCandidate(iceCandidate,receiver);
    }
    @Override
    public void offer(String receiver, SessionDescription sessionDescription) {
        chatCallView.offer(receiver,sessionDescription);
    }

    @Override
    public void answer(String receiver, SessionDescription sessionDescription) {
        chatCallView.answer(receiver,sessionDescription);
    }

    @Override
    public void ice(String receiver, IceCandidate iceCandidate) {
        chatCallView.ice(receiver,iceCandidate);
    }

    public void HangUp(String receiver){
        chatRtcModel.HangUp(receiver);
    }

    @Override
    public void hangup(String receiver) {
        chatCallView.hangup(receiver);
    }
}
