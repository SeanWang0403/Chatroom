package com.sean.chatroom.model;


import android.util.Log;

import com.sean.chatroom.SocketImp;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class ChatCallModel implements SocketImp.CallListener {
    private SocketImp socketImp;
    private callback callback;

    public ChatCallModel(ChatCallModel.callback callback) {
        this.callback = callback;
        socketImp = SocketImp.getInstance();
        socketImp.setCallListener(this);
    }

    public void onCreateSuccess(SessionDescription sessionDescription, String receiver) {
        JSONObject object = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            object.put("sdp", sessionDescription.description);
            object.put("type", sessionDescription.type);
            jsonObject.put("sdp", object);
            jsonObject.put("receiver", receiver);//對方
            if (sessionDescription.type.toString().equals("OFFER")) {
                socketImp.sendOffer(jsonObject);
            } else {
                socketImp.sendAnswer(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onIceCandidate(IceCandidate iceCandidate, String receiver) {
        JSONObject object = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            object.put("sdpMid", iceCandidate.sdpMid);
            object.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            object.put("candidate", iceCandidate.sdp);
            jsonObject.put("candidate", object);
            jsonObject.put("receiver", receiver);//對方
            Log.i("Sean", "onIceCandidate: " + jsonObject.toString());
            socketImp.sendIce(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void offer(Object... args) {
        JSONObject object = (JSONObject) args[0];
        try {
            String receiver = object.getString("receiver");
            JSONObject sdpObject = object.getJSONObject("sdp");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(sdpObject.getString("type")),
                    sdpObject.getString("sdp"));
            callback.offer(receiver, sdp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void answer(Object... args) {
        JSONObject object = (JSONObject) args[0];
        try {
            String receiver = object.getString("receiver");
            JSONObject sdpObject = object.getJSONObject("sdp");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(sdpObject.getString("type")),
                    sdpObject.getString("sdp"));
            callback.answer(receiver, sdp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ice(Object... args) {
        JSONObject object = (JSONObject) args[0];
        try {
            String receiver = object.getString("receiver");
            JSONObject jsonObject = object.getJSONObject("candidate");
            IceCandidate ice = new IceCandidate(jsonObject.getString("sdpMid"),
                    jsonObject.getInt("sdpMLineIndex"),
                    jsonObject.getString("candidate"));
            callback.ice(receiver, ice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void HangUp(String receiver){
        socketImp.HangUp(receiver);
    }

    @Override
    public void hangup(Object... args) {
        String receiver=args[0].toString();
        callback.hangup(receiver);
    }

    public interface callback {
        void offer(String receiver, SessionDescription sessionDescription);

        void answer(String receiver, SessionDescription sessionDescription);

        void ice(String receiver, IceCandidate iceCandidate);

        void hangup(String receiver);
    }
}
