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

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class QRcodeScannerModel {
    private Context context;

    public QRcodeScannerModel(Context context) {
        this.context = context;
    }

    public String getUserID() {
        SharedPreferences spf = context.getSharedPreferences("user", MODE_PRIVATE);
        return spf.getString("userID", "");
    }

    //向伺服器發起搜尋人物
    public void SearchFriend(String qrcode, final QRcodeScanneListener qRcodeScanneListener) {
        qrcode = qrcode.substring(22, qrcode.length());
        ApiManager.getInstance().searchFriend("qrcode", qrcode, new Observer<FriendItem>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FriendItem friendItem) {
                isMyself(friendItem, qRcodeScanneListener);
            }

            @Override
            public void onError(Throwable e) {
                qRcodeScanneListener.SearchError();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //檢查搜尋的人是不是自己
    private void isMyself(final FriendItem friendItem, final QRcodeScanneListener qRcodeScanneListener) {
        dbManager.getInstance(context).readeOpen();
        dbManager.getInstance(context).isMyself(friendItem.getUserID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            qRcodeScanneListener.FriendData(true, false, friendItem);
                        } else {
                            isFriendExistence(friendItem, qRcodeScanneListener);
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

    //判斷搜尋的人是否已經是自己的好友
    private void isFriendExistence(final FriendItem friendItem, final QRcodeScanneListener qRcodeScanneListener) {
        dbManager.getInstance(context).readeOpen();
        dbManager.getInstance(context).isFriendExistence(friendItem.getUserID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        qRcodeScanneListener.FriendData(false, aBoolean, friendItem);
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

    //將搜尋的人添加為好友更新到資料庫
    public void addNewFriend(String myID, final FriendItem friendItem, final QRcodeScanneListener qRcodeScanneListener) {

        ApiManager.getInstance().addFriend(myID, friendItem.getUserID(), new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    if (responseBody.string().equals("成功")) {
                        dbInsertNewFriend(friendItem, qRcodeScanneListener);
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

    //將搜尋的人添加為好友放到sqlite裡
    private void dbInsertNewFriend(FriendItem friendItem, final QRcodeScanneListener qRcodeScanneListener) {
        friendItem.setShip(1);
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
                            qRcodeScanneListener.addNewFriendSuccess();
                        } else {

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        downloadFriendPhoto("sticker", friendItem.getSticker());
        downloadFriendPhoto("background", friendItem.getBackground());
    }

    //下載好友的sticker、background
    private void downloadFriendPhoto(final String type, final String name) {
        ApiManager.getInstance().downloadFile(type, name + ".jpg", new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                saveFriendPhoto(responseBody, type, name);
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

    public interface QRcodeScanneListener {

        void FriendData(boolean myself, boolean exist, FriendItem friendItem);

        void addNewFriendSuccess();

        void SearchError();
    }
}
