package com.sean.chatroom.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.sean.chatroom.ImageHandle;
import com.sean.chatroom.api.ApiManager;
import com.sean.chatroom.database.dbManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CropStickerModel {
    private Context context;

    public CropStickerModel(Context context) {
        this.context = context;
    }

    //更新資料庫的sticker
    public void updateSticker(final String userID, String oldSticker, Bitmap bitmap, final CropListener cropListener) {
        File file = null;
        final String PhotoName = makePhotoName(20);
        try {
            file = new File(saveSticker(oldSticker, PhotoName, ImageHandle.ProportionalCompression(bitmap)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", file.getName(), requestBody);

        ApiManager.getInstance().updateUserPhoto(userID, "sticker", PhotoName, body, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    if (responseBody.string().equals("成功")) {
                        updateDatabase(userID, PhotoName, cropListener);
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

    //儲存sticker
    private String saveSticker(String oldSticker, String PhotoName, Bitmap image) throws IOException {
        SharedPreferences spf = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        spf.edit().putString("sticker", PhotoName).commit();
        String imgName = PhotoName + ".jpg";
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatroom/sticker";
        File parent = new File(sdPath);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File old = new File(parent, oldSticker + ".jpg");
        old.delete();
        File file = new File(parent, imgName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        return file.getPath();
    }

    //更新sqlite裡的sticker
    private void updateDatabase(String userID, String PhotoName, final CropListener cropListener) {
        dbManager.getInstance(context).writeOpen();
        dbManager.getInstance(context).updateUserData(userID, "sticker", PhotoName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            cropListener.onFinish();
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

    public interface CropListener {
        void onFinish();

    }
}
