package com.sean.chatroom;

import io.reactivex.observers.DefaultObserver;

public abstract class FileUploadObserver<T> extends DefaultObserver<T> {
    @Override
    public void onNext(T t) {
        onUpLoadSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onUpLoadFail(e);
    }

    @Override
    public void onComplete() {
        onUpLoadFinish();
    }

    public void onProgressChange(long bytesWritten, long contentLength) {
        onProgress((int) (bytesWritten * 100L / contentLength));
    }

    //上傳成功
    public abstract void onUpLoadSuccess(T t);

    //上傳失敗
    public abstract void onUpLoadFail(Throwable e);

    //上傳進度
    public abstract void onProgress(int progress);

    public abstract void onUpLoadFinish();
}