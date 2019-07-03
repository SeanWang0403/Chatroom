package com.sean.chatroom.model;

import android.content.Context;

import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.database.dbManager;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class UserDataUpdateModel {
    private Context context;


    public UserDataUpdateModel(Context context) {
        this.context = context;
    }

    //更新資料庫裡user的資料
    public void updateUserData(final String userID, final String type, final String value, final UserDataUpdateListener userDataUpdateListener) {
        ApiManager.getInstance().updateUserData(userID, type, value, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    if (responseBody.string().equals("成功")) {
                        updateDataBase(userID, type, value, userDataUpdateListener);
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

    //更新sqlite裡user的資料
    private void updateDataBase(String userID, String type, String value, final UserDataUpdateListener userDataUpdateListener) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).updateUserData(userID, type, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            userDataUpdateListener.Success();
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

    public interface UserDataUpdateListener {
        void Success();
    }
}
