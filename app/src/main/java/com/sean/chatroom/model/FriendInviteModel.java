package com.sean.chatroom.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.database.dbManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class FriendInviteModel {
    private Context context;

    public FriendInviteModel(Context context) {
        this.context = context;
    }

    public String getUserID() {
        SharedPreferences spf = context.getSharedPreferences("user", MODE_PRIVATE);
        return spf.getString("userID", "");
    }

    //從sqlite抓取是否有好友邀請
    public void getInviteData(final FriendInviteListener friendInviteListener) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).queryFriendInvite()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FriendItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<FriendItem> friendItems) {
                        friendInviteListener.inviteData(friendItems);
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

    //從資料庫裡抓取是否有新的好友邀請
    public void getFriendInvite(String userID, final List<FriendItem> list, final FriendInviteListener friendInviteListener) {
        ApiManager.getInstance().getFriendInvite(userID, new Observer<List<FriendItem>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<FriendItem> friendItems) {
                if (friendItems.size() != 0) {
                    friendInviteListener.newFriendInvite(updte(list, friendItems));
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

    //更新好友列表
    private List<FriendItem> updte(List<FriendItem> sqlData, List<FriendItem> apiData) {
        Map<String, FriendItem> map = new HashMap<>();
        for (int i = 0; i < sqlData.size(); i++) {
            map.put(sqlData.get(i).getUserID(), sqlData.get(i));
        }
        for (int i = 0; i < apiData.size(); i++) {
            if (map.get(apiData.get(i).getUserID()) == null) {
                apiData.get(i).setRemark("");
                sqlData.add(apiData.get(i));
                insterFriend(apiData.get(i));
                if (!apiData.get(i).getSticker().equals("")) {
                    downloadFriendPhoto("sticker", apiData.get(i).getSticker());
                }
                if (!apiData.get(i).getBackground().equals("")) {
                    downloadFriendPhoto("background", apiData.get(i).getBackground());
                }
            }
        }
        return sqlData;
    }

    //新增好友邀請到sqlite
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

    //下載邀請人的sticker
    private void downloadFriendPhoto(final String type, final String PhotoName) {
        ApiManager.getInstance().downloadFile(type, PhotoName + ".jpg", new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                saveFriendPhoto(responseBody, type, PhotoName);
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

    //儲存邀請人的sticker
    private void saveFriendPhoto(ResponseBody body, String type, String PhotoName) {
        String FileName = PhotoName + ".jpg";
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/" + type;
        File parent = new File(sdPath);
        if (!parent.exists()) {
            parent.mkdirs();
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

    //更新資料庫裡的ship為好友
    public void updateShip(final String myUserID, final String otherUserID, final int postion, final FriendInviteListener friendInviteListener) {
        ApiManager.getInstance().updateFriendShip(myUserID, otherUserID, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    if (responseBody.string().equals("成功")) {
                        updateDBShip(otherUserID, postion, friendInviteListener);
                    }
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

    //更新sqlite裡的ship為好友
    private void updateDBShip(String userID, final int postion, final FriendInviteListener friendInviteListener) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).updatFriendShip(userID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            friendInviteListener.updateShipSuccess(postion);
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

    public interface FriendInviteListener {
        void inviteData(List<FriendItem> friendItems);

        void updateShipSuccess(int postion);

        void newFriendInvite(List<FriendItem> friendItems);
    }
}
