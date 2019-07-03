package com.sean.chatroom.model;

import android.content.Context;
import android.util.Log;

import com.sean.chatroom.database.dbManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class FriendRemarkUpdateModel {
    private Context context;

    public FriendRemarkUpdateModel(Context context) {
        this.context = context;
    }

    //更新sqlite裡的好友備註
    public void updateFreindRemark(String userID, String remark, final FriendRemarkUpdateListener friendRemarkUpdateListener) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).updateFriendRemark(userID, remark)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            friendRemarkUpdateListener.Success();
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

    public interface FriendRemarkUpdateListener {
        void Success();
    }
}
