package com.sean.chatroom;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.sean.chatroom.presenter.ChatCallPresenter;
import com.sean.chatroom.view.ChatCallView;
import com.squareup.picasso.Picasso;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import at.markushi.ui.CircleButton;

public class ChatAudioCallActivity extends AppCompatActivity implements ChatCallView {
    private ImageView sticker;
    private ViewStub CallerViewStub;
    public static final String AUDIO_TRACK_ID = "audioPN";
    public static final String LOCAL_MEDIA_STREAM_ID = "localStreamPN";
    private MediaStream localmediaStream;
    private PeerConnection peerConnection;
    private MySdpObserver mySdpObserver;
    private String self, receiver;
    private ChatCallPresenter chatCallPresenter;
    private CircleButton caller_hangup;
    private Timer timer = null;
    private int time = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_audio);
        sticker = (ImageView) findViewById(R.id.chatroom_audio_sticker);
        caller_hangup = (CircleButton) findViewById(R.id.chatroom_audio_refuse);
        String type = getIntent().getExtras().getString("type");
        if (type.equals("callee")) {
            receiver = getIntent().getExtras().getString("caller");
            CallerViewStub = (ViewStub) findViewById(R.id.chatroom_audio_caller);
            CallerViewStub.inflate();
            caller_hangup.setVisibility(View.GONE);
        } else {
            receiver = getIntent().getExtras().getString("callee");
        }
        self = getIntent().getExtras().getString("self");
        String Sticker = getIntent().getExtras().getString("sticker");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/chatroom/sticker/" + Sticker + ".jpg");
        Picasso.get().load(file).into(sticker);
        init();
        chatCallPresenter = new ChatCallPresenter(this);
    }

    private void init() {
        /**PeerConnectionFactory初始化*/
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();
        /**獲得影像及聲音並封裝*/
        AudioSource audioSource =
                peerConnectionFactory.createAudioSource(new MediaConstraints());
        AudioTrack localAudioTrack =
                peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localAudioTrack.setEnabled(true);
        /**創建本地媒體流*/
        localmediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
        localmediaStream.addTrack(localAudioTrack);
        /**設定連線限制*/
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        mySdpObserver = new MySdpObserver();
        /**設置iceServer*/
        PeerConnection.IceServer iceServer = new PeerConnection.IceServer("stun:stun.l.google.com:19302");
        List<PeerConnection.IceServer> servers = new ArrayList<>();
        servers.add(iceServer);
        /**建立連線*/
        peerConnection = peerConnectionFactory.createPeerConnection(servers, mediaConstraints, pcObserver);
        /**添加本地媒體流*/
        peerConnection.addStream(localmediaStream);
    }

    PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            //Signaling狀態改變時觸發
            Log.i("Sean", "onSignalingChange: ");
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            //IceConnection狀態改變時觸發
            Log.i("Sean", "onIceConnectionChange: ");
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            //IceConnectionReceiving狀態改變時觸發
            Log.i("Sean", "onIceConnectionReceivingChange: ");
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            //IceGathering狀態改變時觸發
            Log.i("Sean", "onIceGatheringChange: ");
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            //發現新的IceCandidate時觸發
            Log.i("Sean", "onIceCandidate: ");
            chatCallPresenter.onIceCandidate(iceCandidate, receiver);
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            //接收到對方的媒體流時觸發

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            //對方的媒體流關閉時觸發

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            //另一端開了一個DataChannel
            Log.i("Sean", "onDataChannel: ");
        }

        @Override
        public void onRenegotiationNeeded() {
            //需要重新協商時觸發
            Log.i("Sean", "onRenegotiationNeeded: ");
        }
    };

    private class MySdpObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            //創建 offer or answer 成功時呼叫
            Log.i("Sean", "onCreateSuccess: Offer Answer Success");
            peerConnection.setLocalDescription(mySdpObserver, sessionDescription);
            chatCallPresenter.onCreateSuccess(sessionDescription, receiver);
            if (sessionDescription.type.toString().equals("ANSWER")) {
                timeCount();
            }
        }

        @Override
        public void onSetSuccess() {
            //設置Local or Remote Description成功時呼叫
            Log.i("Sean", "onSetSuccess: ");
        }

        @Override
        public void onCreateFailure(String s) {
            //創建 offer or answer 失敗時呼叫
            Log.i("Sean", "onCreateFailure: ");
        }

        @Override
        public void onSetFailure(String s) {
            //設置Local or Remote Description失敗時呼叫
            Log.i("Sean", "onSetFailure: ");
        }
    }

    public void RefuseCall(View view) {
        peerConnection.close();
        if (timer != null) {
            timer.cancel();
        }
        chatCallPresenter.HangUp(receiver);
        AudioTimeFinish();
    }

    public void AnswerCall(View view) {
        caller_hangup.setVisibility(View.VISIBLE);
        CallerViewStub.setVisibility(View.GONE);
        peerConnection.createOffer(mySdpObserver, new MediaConstraints());
    }

    @Override
    public void offer(String receiver, SessionDescription sessionDescription) {
        if (receiver.equals(self)) {
            peerConnection.setRemoteDescription(mySdpObserver, sessionDescription);
            peerConnection.createAnswer(mySdpObserver, new MediaConstraints());
        }

    }

    @Override
    public void answer(String receiver, SessionDescription sessionDescription) {
        if (receiver.equals(self)) {
            peerConnection.setRemoteDescription(mySdpObserver, sessionDescription);
        }

    }

    @Override
    public void ice(String receiver, IceCandidate iceCandidate) {
        if (receiver.equals(self)) {
            if (peerConnection.getRemoteDescription() != null) {
                peerConnection.addIceCandidate(iceCandidate);
            }
        }
    }

    @Override
    public void hangup(String receiver) {
        if (receiver.equals(self)) {
            peerConnection.close();
            if (timer != null) {
                timer.cancel();
            }
            AudioTimeFinish();
        }
    }

    private void timeCount() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private void AudioTimeFinish() {
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundleBack = new Bundle();
        if (time == 0) {
            bundleBack.putString("audioTime", "取消");
        } else {
            bundleBack.putString("audioTime", TimeFormat(time));
        }
        intent.putExtras(bundleBack);
        setResult(RESULT_OK, intent);
        finish();
    }

    private String TimeFormat(int time) {
        int hour = time / 3600;
        int min = (time % 3600) / 60;
        int sec = (time % 3600) % 60;
        String Hour;
        String Min;
        String Sec;

        Hour = String.format("%02d", hour);
        Min = String.format("%02d", min);
        Sec = String.format("%02d", sec);
        if (hour != 0) {
            return Hour + ":" + Min + ":" + Sec;
        } else {
            return Min + ":" + Sec;
        }
    }
}
