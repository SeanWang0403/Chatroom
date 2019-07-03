package com.sean.chatroom.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.database.dbManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class FriendModel {
    private Context context;

    public FriendModel(Context context) {
        this.context = context;
    }

    public String getUserID() {
        SharedPreferences spf = context.getSharedPreferences("user", MODE_PRIVATE);
        return spf.getString("userID", "");
    }

    //從sqlite抓取好友列表
    public void getDBFriend(final FriendListener friendListener) {
        dbManager.getInstance(context).readeOpen();
        dbManager.getInstance(context).queryFriend()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FriendItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<FriendItem> friendItems) {
                        friendListener.FriendData(friendItems);
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

    //從資料庫裡抓取最新的好友列表
    public void getFriendData(String userID, final List<FriendItem> friendItems, final FriendListener friendListener) {
        ApiManager.getInstance().getFriendData(userID, new Observer<List<FriendItem>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<FriendItem> list) {
                if (list.size()!=0){
                    friendListener.updateData(updte(friendItems, list));
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //更新好友列表
    private List<FriendItem> updte(List<FriendItem> sqlData, List<FriendItem> apiData) {
        Map<String, FriendItem> map = new HashMap<>();
        for (int i = 0; i < sqlData.size(); i++) {
            map.put(sqlData.get(i).getUserID(), sqlData.get(i));
        }
        List<String> updateType = new ArrayList<>();
        List<String> updateValue = new ArrayList<>();
        for (int i = 0; i < apiData.size(); i++) {
            if (map.get(apiData.get(i).getUserID()) != null) {
                FriendItem friendItem = map.get(apiData.get(i).getUserID());
                if (friendItem.getUpdate_time() != apiData.get(i).getUpdate_time()) {
                    if (!friendItem.getName().equals(apiData.get(i).getName())) {
                        updateType.add("name");
                        updateValue.add(apiData.get(i).getName());
                        friendItem.setName(apiData.get(i).getName());
                    }
                    if (!friendItem.getSticker().equals(apiData.get(i).getSticker())) {
                        updateType.add("sticker");
                        updateValue.add(apiData.get(i).getSticker());
                        downloadFriendPhoto("sticker", apiData.get(i).getSticker(), friendItem.getSticker());
                        friendItem.setSticker(apiData.get(i).getSticker());
                        ChatHistoryExist(friendItem.getUserID(), friendItem.getSticker());
                    }
                    if (!friendItem.getBackground().equals(apiData.get(i).getBackground())) {
                        updateType.add("background");
                        updateValue.add(apiData.get(i).getBackground());
                        downloadFriendPhoto("background", apiData.get(i).getBackground(), friendItem.getBackground());
                        friendItem.setBackground(apiData.get(i).getBackground());
                    }
                    updateType.add("update_time");
                    updateValue.add(String.valueOf(apiData.get(i).getUpdate_time()));
                    updateFriend(friendItem.getUserID(), updateType, updateValue);
                    friendItem.setUpdate_time(apiData.get(i).getUpdate_time());
                    for (int j = 0; j < sqlData.size(); j++) {
                        if (sqlData.get(j).getUserID().equals(friendItem.getUserID())) {
                            sqlData.set(j, friendItem);
                            break;
                        }
                    }
                }
            } else {
                apiData.get(i).setRemark("");
                sqlData.add(apiData.get(i));
                insterFriend(apiData.get(i));
                if (!apiData.get(i).getSticker().equals("")) {
                    downloadFriendPhoto("sticker", apiData.get(i).getSticker(), "");
                }
                if (!apiData.get(i).getBackground().equals("")) {
                    downloadFriendPhoto("background", apiData.get(i).getBackground(), "");
                }
            }
        }
        return sqlData;
    }

    //新增新的好友到sqlite
    private void insterFriend(FriendItem friendItem) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).insertFriendData(friendItem)
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        dbManager.getInstance(context).close();
                    }
                });
    }

    //更新sqlie裡舊好友的資料
    private void updateFriend(String userID, List<String> type, List<String> value) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).updatFriend(userID, type, value)
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        dbManager.getInstance(context).close();
                    }
                });
    }

    //檢查是否有最後聊天紀錄
    private void ChatHistoryExist(final String room, final String sticker) {
        dbManager.getInstance(context).readeOpen();
        dbManager.getInstance(context).HistoryExist(room)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            updteChatHistorySticker(room, sticker);
                        }
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

    //更新最後聊天紀錄的大頭貼
    private void updteChatHistorySticker(String room, String sticker) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).updateChatHistorySticker(room, sticker)
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
                        dbManager.getInstance(context).close();
                    }
                });
    }

    //下載好友的sticker、background
    private void downloadFriendPhoto(final String type, final String PhotoName, final String oldFile) {
        ApiManager.getInstance().downloadFile(type, PhotoName + ".jpg", new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                saveFriendPhoto(responseBody, type, PhotoName, oldFile);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    //儲存好友的sticker、background
    private void saveFriendPhoto(ResponseBody body, String type, String PhotoName, String oldPhoto) {
        String FileName = PhotoName + ".jpg";
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/" + type;
        File parent = new File(sdPath);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (!oldPhoto.equals("")) {
            File old = new File(parent, oldPhoto + ".jpg");
            old.delete();
        }
        try {
            File file = new File(parent, FileName);
            InputStream fis = body.byteStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int length;
            byte data[] = new byte[4096];
            try {
                while ((length = bis.read(data)) != -1) {
                    bos.write(data, 0, length);
                }
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface FriendListener {
        void FriendData(List<FriendItem> list);

        void updateData(List<FriendItem> list);

    }
}
