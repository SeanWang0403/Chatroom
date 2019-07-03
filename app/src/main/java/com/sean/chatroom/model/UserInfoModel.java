package com.sean.chatroom.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.sean.chatroom.ImageHandle;
import com.sean.chatroom.bean.UserInfoItem;
import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.database.dbManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class UserInfoModel {
    private Context context;

    public UserInfoModel(Context context) {
        this.context = context;
    }

    //從SharedPreferences抓取user的資料
    public void getUserPhoto(UserInfoListener userInfoListener) {
        SharedPreferences spf = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String userID = spf.getString("userID", "");
        String Sticker = spf.getString("sticker", "");
        String Background = spf.getString("background", "");
        userInfoListener.getUserPhoto(userID, Sticker, Background);
    }

    //從伺服器裡抓取user的資料
    public void getUserData(final UserInfoListener userInfoListener) {
        dbManager.getInstance(context).readeOpen();
        dbManager.getInstance(context).queryUserData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<UserInfoItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<UserInfoItem> userInfoItems) {
                        userInfoListener.getUserData(userInfoItems);
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

    //更新資料庫裡user的background
    public void updateBackground(final String userID, String oldBackground, Bitmap bitmap, final UserInfoListener userInfoListener) {
        File file = null;
        final String PhotoName = makePhotoName(20);
        try {
            file = new File(saveBackgroundPhoto(oldBackground, PhotoName, ImageHandle.ProportionalCompression(bitmap)));
            Log.i("Sean", "updateBackground: "+file.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", file.getName(), requestBody);
        ApiManager.getInstance().updateUserPhoto(userID, "background", PhotoName, body, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    if (responseBody.string().equals("成功")) {
                        updateDataBase(userID, "background", PhotoName, userInfoListener);
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
                Log.i("Sean", "onComplete: ");
            }
        });

    }

    //儲存user的background
    private String saveBackgroundPhoto(String oldBackground, String PhotoName, Bitmap image) throws IOException {
        SharedPreferences spf = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        spf.edit().putString("background", PhotoName).commit();
        String imgName = PhotoName + ".jpg";
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/background";
        File parent = new File(sdPath);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File old = new File(parent, oldBackground + ".jpg");
        old.delete();
        File file = new File(parent, imgName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        return file.getPath();
    }

    //給定一個亂數的圖片名稱
    private String makePhotoName(int Max) {
        String Big[] = new String[91 - 65];
        String Small[] = new String[123 - 97];
        String Number[] = new String[10 - 0];
        String MsgID = "";
        for (int i = 90; i > 64; i--) {
            Big[i - 64 - 1] = Character.toString((char) i);
        }

        for (int i = 122; i > 96; i--) {
            Small[i - 96 - 1] = Character.toString((char) i);
        }

        for (int i = 9; i >= 0; i--) {
            Number[i - 0] = String.valueOf(i);
        }

        String TypeContainer[][] = {Big, Small, Number};

        for (int i = 0; i < Max; i++) {
            int SelectType = (int) Math.floor(Math.random() * 3);
            int SelectChar;
            if (SelectType == 2) {
                SelectChar = (int) Math.floor(Math.random() * 10);
            } else {
                SelectChar = (int) Math.floor(Math.random() * 26);
            }
            MsgID += TypeContainer[SelectType][SelectChar];
        }
        return MsgID;
    }

    //更新資料庫裡是否能藉由id被搜尋
    public void updateIdSearch(final String userID, final String type, boolean check, final UserInfoListener userInfoListener) {
        final String search = (check == true) ? "1" : "0";
        ApiManager.getInstance().updateUserData(userID, type, search, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    if (responseBody.string().equals("成功")) {
                        updateDataBase(userID, type, search, userInfoListener);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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

    //更新sqlite裡user的資料
    private void updateDataBase(String userID, final String type, final String value, final UserInfoListener userInfoListener) {
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
                            if (type.equals("background")) {
                                userInfoListener.onFinish(value);
                            } else {
                                userInfoListener.idUpdateFinish();
                            }
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

    public interface UserInfoListener {
        void getUserPhoto(String userID, String sticker, String background);

        void getUserData(List<UserInfoItem> items);

        void onFinish(String photoName);

        void idUpdateFinish();

    }
}
