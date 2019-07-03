package com.sean.chatroom.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

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


public class SearchFriendModel {
    private Context context;

    public SearchFriendModel(Context context) {
        this.context = context;
    }

    public String getUserID() {
        SharedPreferences spf = context.getSharedPreferences("user", MODE_PRIVATE);
        return spf.getString("userID", "");
    }

    //向伺服器發起搜尋人物
    public void SearchNewFriend(String type, String user, final addFriendListener addFriendListener) {
        ApiManager.getInstance().searchFriend(type, user, new Observer<FriendItem>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FriendItem friendItem) {
                if (friendItem.getUserID().equals("fail")) {
                    addFriendListener.NoFindUser();
                } else {
                    isMyself(friendItem, addFriendListener);
                }
            }

            @Override
            public void onError(Throwable e) {
                addFriendListener.SearchError();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //檢查搜尋的人是不是自己
    private void isMyself(final FriendItem friendItem, final addFriendListener addFriendListener) {
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
                            addFriendListener.FriendData(true, false, friendItem);
                        } else {
                            isFriendExistence(friendItem, addFriendListener);
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
    private void isFriendExistence(final FriendItem friendItem, final addFriendListener addFriendListener) {
        dbManager.getInstance(context).readeOpen();
        dbManager.getInstance(context).isFriendExistence(friendItem.getUserID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        addFriendListener.FriendData(false, aBoolean, friendItem);
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
    public void addNewFriend(String myID, final FriendItem friendItem, final addFriendListener addFriendListener) {
        ApiManager.getInstance().addFriend(myID, friendItem.getUserID(), new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    if (responseBody.string().equals("成功")) {
                        dbInsertNewFriend(friendItem, addFriendListener);
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
    private void dbInsertNewFriend(FriendItem friendItem, final addFriendListener addFriendListener) {
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
                            addFriendListener.addNewFriendSuccess();
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

    public interface addFriendListener {
        void FriendData(boolean myself, boolean exist, FriendItem friendItem);

        void addNewFriendSuccess();

        void SearchError();

        void NoFindUser();
    }
}
