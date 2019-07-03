package com.sean.chatroom.presenter;


import android.content.Context;
import android.graphics.Bitmap;

import com.sean.chatroom.model.CropStickerModel;
import com.sean.chatroom.view.CropStickerView;

public class CropStickerPresenter implements CropStickerModel.CropListener {
    private CropStickerView cropStickerView;
    private CropStickerModel cropStickerModel;

    public CropStickerPresenter(CropStickerView cropStickerView, Context context) {
        this.cropStickerView = cropStickerView;
        this.cropStickerModel = new CropStickerModel(context);
    }

    public void updateSticker(String userID, String oldSticker, Bitmap bitmap) {
        cropStickerModel.updateSticker(userID, oldSticker, bitmap, this);
    }

    @Override
    public void onFinish() {
        cropStickerView.onFinish();
    }

}
