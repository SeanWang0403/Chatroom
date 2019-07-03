package com.sean.chatroom.model;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;

import com.sean.chatroom.FileUploadObserver;
import com.sean.chatroom.SocketImp;
import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.bean.Message;
import com.sean.chatroom.database.dbManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;


public class ChatModel implements SocketImp.EventListener {
    private SocketImp socketImp;
    private ModelCallBack modelCallBack;
    private Context context;
    private Disposable downladCancel;

    public ChatModel(ModelCallBack modelCallBack, Context context) {
        this.modelCallBack = modelCallBack;
        this.context = context;
        socketImp = SocketImp.getInstance();
        socketImp.setEventListener(this);
    }

    //從sqlite抓取特定對象的所有聊天紀錄
    public void getChat(String room, final ModelCallBack modelCallBack) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).queryChat(room)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Message> list) {
                        modelCallBack.getChatData(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dbManager.getInstance(context).close();
                    }
                });
    }

    public void sendMessage(Message message) {
        socketImp.sendMessage(message);
    }

    //發送者的訊息回調
    @Override
    public void msgDeliver(Message message) {
        modelCallBack.msgDeliver(message);
    }

    //接收者的訊息回調
    @Override
    public void newMessage(Object... args) {
        JSONObject jsonObject = (JSONObject) args[0];
        try {
            int type = jsonObject.getInt("type");
            String msg = jsonObject.getString("message");
            if (type == Message.TYPE_AUDIO_RECEIVED || type == Message.TYPE_FACETIME_RECEIVED) {
                if (msg.equals("取消")) {
                    msg = "未接來電";
                }
            }
            Message message = new Message(jsonObject.getString("sender"), jsonObject.getString("receiver"),
                    msg, jsonObject.getLong("createtime"),
                    jsonObject.getLong("delivertime"), jsonObject.getString("msgID"),
                    0, type);
            modelCallBack.receiveMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //接收者呼叫已讀
    public void callReadMessage(String receiver, String msgID) {
        socketImp.callReadMessage(receiver, msgID);
    }

    //發送者接收哪條訊息被已讀
    @Override
    public void readMessage(Object... args) {
        JSONObject jsonObject = (JSONObject) args[0];
        try {
            String receiver = jsonObject.getString("receiver");
            String msgID = jsonObject.getString("msgID");
            modelCallBack.readMessage(receiver, msgID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //更新sqlite的訊息為已讀
    public void updateReadMessage(String msgID) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).updateChat(msgID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            //成功
                        } else {
                            //失敗處理
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //上傳檔案
    public void UploadFile(final String path, final ModelCallBack modelCallBack) {
        final File file = new File(path);
        ApiManager.getInstance().uploadFile(file, new FileUploadObserver<ResponseBody>() {
            @Override
            public void onUpLoadSuccess(ResponseBody body) {
                try {
                    try {
                        JSONObject jsonObject = new JSONObject(body.string());
                        String fileName = jsonObject.getString("FileName");
                        String FileMsg = file.getName() + " " + FileSizeFormat(file.length()) + " " + fileName;
                        modelCallBack.uploadFileFinish(FileMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUpLoadFail(Throwable e) {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onUpLoadFinish() {

            }
        });

    }

    //上傳圖片
    public void UploadImage(String path, final ModelCallBack modelCallBack) {
        final File file = new File(path);
        ApiManager.getInstance().uploadImage(file, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    String fileName = jsonObject.getString("FileName");
                    if (jsonObject.getString("result").equals("success")) {
                        modelCallBack.uploadImageFinish(fileName, file);
                    } else {
                        //失敗處理
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    //計算檔案的大小事 B、MB、GB
    private String FileSizeFormat(long length) {
        if (length < 1024) {
            return length + "B";
        } else {
            return Formatter.formatFileSize(context, length);
        }
    }

    //下載檔案
    public void DownloadFile(String fileName, int type, final ModelCallBack callBack) {
        String FileName;
        final String saveName;
        if (type == Message.TYPE_IMAGE_SENT || type == Message.TYPE_IMAGE_RECEIVED) {
            FileName = fileName;
            saveName = fileName;
        } else {
            String FileData[] = fileName.split(" ");
            FileName = FileData[2];
            saveName = FileData[0];
        }
        ApiManager.getInstance().FileDownload(FileName, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                downladCancel = d;
            }

            @Override
            public void onNext(ResponseBody body) {
                SaveFile(saveName, body, callBack);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    //儲存檔案
    private void SaveFile(String FileName, ResponseBody body, ModelCallBack callBack) {
        try {
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/file";
            File parent = new File(sdPath);
            if (!parent.exists()) {
                parent.mkdirs();
            }

            File file = new File(parent, FileName);
            InputStream fis = body.byteStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            long max = body.contentLength();
            int length;
            byte data[] = new byte[4096];
            int downloadProgress = 0;
            try {
                while ((length = bis.read(data)) != -1) {
                    bos.write(data, 0, length);
                    downloadProgress += length;
                    Log.i("Sean", "file: " + ((downloadProgress * 100) / max));
                    callBack.progress((int) ((downloadProgress * 100L) / max));
                }
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //取消下載檔案
    public void cancelDownload() {
        downladCancel.dispose();
    }

    //發送者呼叫視訊通話
    public void callFaceTime(String caller, String receiver) {
        socketImp.FaceTime(caller, receiver);
    }

    //發送者呼叫語音通話
    public void callAudio(String caller, String receiver) {
        socketImp.audio(caller, receiver);
    }

    //接收者收到視訊通話得請求
    @Override
    public void facetime(Object... args) {
        JSONObject jsonObject = (JSONObject) args[0];
        try {
            String caller = jsonObject.getString("caller");
            String receiver = jsonObject.getString("receiver");
            modelCallBack.facetime(caller, receiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //接收者收到語音通話得請求
    @Override
    public void audio(Object... args) {
        JSONObject jsonObject = (JSONObject) args[0];
        try {
            String caller = jsonObject.getString("caller");
            String receiver = jsonObject.getString("receiver");
            modelCallBack.audio(caller, receiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //新增聊天紀錄到sqlite
    public void intsertMessage(Message message, String room) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).insertChat(message, room)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.i("Sean", "onNext: " + aBoolean);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dbManager.getInstance(context).close();
                    }
                });
    }

    //檢查特定對象的最後聊天紀錄是否存在
    public void saveHistory(final String room, final String sticker, final String name, final String msg, final String time,
                            final ModelCallBack modelCallBack) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).HistoryExist(room)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            updateHistory(room, msg, time,modelCallBack);
                        } else {
                            insertHistory(room, sticker, name, msg, time,modelCallBack);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //新增最後的聊天紀錄到sqlite
    private void insertHistory(String room, String sticker, String name, String msg, String time, final ModelCallBack modelCallBack) {
        dbManager.getInstance(context).insertChatHistory(room, sticker, name, msg, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                           modelCallBack.saveHistorySuccess();
                        } else {
                            Log.i("Sean", "onNext: fail");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        dbManager.getInstance(context).close();
                    }
                });
    }

    //更新sqlite裡最後的聊天紀錄
    private void updateHistory(String room, String msg, String time, final ModelCallBack modelCallBack) {
        dbManager.getInstance(context).updateChatHistory(room, msg, time)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                           modelCallBack.saveHistorySuccess();
                        } else {
                            Log.i("Sean", "onNext: fail");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        dbManager.getInstance(context).close();
                    }
                });
    }

    public void socketDisconnect(){
        socketImp.disconnect();
    }

    public interface ModelCallBack {
        void getChatData(List<Message> list);

        void msgDeliver(Message message);

        void receiveMessage(Message message);

        void readMessage(String receiver, String msgID);

        void progress(int progress);

        void facetime(String caller, String receiver);

        void audio(String caller, String receiver);

        void uploadFileFinish(String fileData);

        void uploadImageFinish(String imageName, File image);

        void saveHistorySuccess();
    }
}