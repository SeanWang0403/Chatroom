package com.sean.chatroom.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.bean.LoginData;
import com.sean.chatroom.bean.UserData;
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

public class LoginModel {
    private Context context;

    public LoginModel(Context context) {
        this.context = context;
    }

    //向伺服器請求登入
    public void getLoginData(String account, String password, final LoginListener loginListener) {
        ApiManager.getInstance().postLogin(account, password, new Observer<LoginData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginData loginData) {
                if (loginData.getType().equals("成功")) {
                    saveUserData(loginData.getUserdata(), loginListener);
                } else {
                    loginListener.onFail(loginData.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                loginListener.onError();
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                loginListener.onFinish();
            }
        });
    }

    //儲存user的資料
    private void saveUserData(UserData userData, final LoginListener loginListener) {
        SharedPreferences spf = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        spf.edit().putString("userID", userData.getUserID())
                .putString("sticker", userData.getSticker())
                .putString("background", userData.getBackground())
                .commit();
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).insertUserData(userData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            loginListener.onSuccess();
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
        if (!userData.getSticker().equals("")) {
            downloadUserPhoto("sticker", userData.getSticker());
        }
        if (!userData.getBackground().equals("")) {
            downloadUserPhoto("background", userData.getBackground());
        }
    }

    //下載user的sticker、background
    private void downloadUserPhoto(final String type, final String PhotoName) {
        ApiManager.getInstance().downloadFile(type, PhotoName + ".jpg", new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                saveUserPhoto(responseBody, type, PhotoName);
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

    //儲存user的sticker、background
    private void saveUserPhoto(ResponseBody body, String type, String PhotoName) {
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

    //檢查是否有user的資料判斷是否要再登入
    public boolean UserDataExist() {
        SharedPreferences spf = context.getSharedPreferences("user", MODE_PRIVATE);
        if (spf.getString("userID", "").equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public interface LoginListener {
        void onSuccess();

        void onFail(String msg);

        void onError();

        void onFinish();
    }
}
