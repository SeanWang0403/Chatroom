package com.sean.chatroom.view;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;


public interface ChatCallView {
    void offer(String receiver, SessionDescription sessionDescription);

    void answer(String receiver, SessionDescription sessionDescription);

    void ice(String receiver, IceCandidate iceCandidate);

    void hangup(String receiver);
}
