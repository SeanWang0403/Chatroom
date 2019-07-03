package com.sean.chatroom;

import android.util.Log;

import com.sean.chatroom.bean.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.sean.chatroom.api.ApiClient.MY_IP_ADDRESS;

public class SocketImp {
    private Socket socket;
    private EventListener eventListener;
    private CallListener callListener;
    private static SocketImp instance = null;


    public static SocketImp getInstance() {
        if (instance == null) {
            synchronized (SocketImp.class) {
                if (instance == null) {
                    instance = new SocketImp();
                }
            }
        }
        return instance;
    }

    private SocketImp() {
        connect();
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setCallListener(CallListener callListener) {
        this.callListener = callListener;
    }

    public void connect() {
        try {
            socket = IO.socket(MY_IP_ADDRESS);
            socket.on("connection", Connection);
            socket.on("ReceiveMsg", ReceiveMsg);
            socket.on("ReadMessage", ReadMsg);
            socket.on("facetime", facetime);
            socket.on("audio", audio);
            socket.on("offer", offer);
            socket.on("answer", answer);
            socket.on("ice", ice);
            socket.on("hangup", hangup);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        socket.disconnect();
    }

    private Emitter.Listener Connection = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("Sean", "connection: " + args[0].toString());
        }
    };

    private Emitter.Listener ReceiveMsg = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            eventListener.newMessage(args);
        }
    };

    private Emitter.Listener ReadMsg = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            eventListener.readMessage(args);
        }
    };

    private Emitter.Listener facetime = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            eventListener.facetime(args);
        }
    };

    private Emitter.Listener audio = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            eventListener.audio(args);
        }
    };

    private Emitter.Listener offer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            callListener.offer(args);
        }
    };

    private Emitter.Listener answer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            callListener.answer(args);
        }
    };

    private Emitter.Listener ice = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            callListener.ice(args);
        }
    };

    private Emitter.Listener hangup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            callListener.hangup(args);
        }
    };


    public void sendMessage(Message message) {
        JSONObject object = new JSONObject();
        try {
            object.put("sender", message.getSender());
            object.put("receiver", message.getReceiver());
            object.put("message", message.getMessage());
            object.put("time", message.getCreatetime());
            object.put("msgID", message.getMsgID());
            object.put("type",message.getType()+200);
            socket.emit("Message", object);
            eventListener.msgDeliver(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void callReadMessage(String receiver, String msgID) {
        JSONObject object = new JSONObject();
        try {
            object.put("receiver", receiver);
            object.put("msgID", msgID);
            socket.emit("ReadMessage", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void FaceTime(String caller,String receiver) {
        JSONObject object = new JSONObject();
        try {
            object.put("caller", caller);
            object.put("receiver", receiver);
            socket.emit("facetime", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void audio(String caller,String receiver) {
        JSONObject object = new JSONObject();
        try {
            object.put("caller", caller);
            object.put("receiver", receiver);
            socket.emit("audio", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendOffer(JSONObject jsonObject) {
        socket.emit("offer", jsonObject);
    }

    public void sendAnswer(JSONObject jsonObject) {
        socket.emit("answer", jsonObject);
    }

    public void sendIce(JSONObject jsonObject) {
        socket.emit("ice", jsonObject);
    }

    public void HangUp(String receiver) {
        socket.emit("hangup", receiver);
    }

    public interface EventListener {
        void msgDeliver(Message message);

        void newMessage(Object... args);

        void readMessage(Object... args);

        void facetime(Object... args);

        void audio(Object... args);
    }

    public interface CallListener {
        void offer(Object... args);

        void answer(Object... args);

        void ice(Object... args);

        void hangup(Object... args);
    }

}
