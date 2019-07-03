package com.sean.chatroom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.sean.chatroom.presenter.CropStickerPresenter;
import com.sean.chatroom.view.CropStickerView;


public class CropStickerActivity extends AppCompatActivity implements View.OnClickListener,CropStickerView{
    private CropImageView CropView;
    private ImageView ResultPhoto;
    private Button CropPhoto, BackUser, Determine, Cancel;
    private String uriString,userID,oldSticker;
    private CropStickerPresenter cropStickerPresenter;
    private Bitmap cropBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropsticker);
        userID = getIntent().getExtras().getString("userID");
        oldSticker = getIntent().getExtras().getString("oldSticker");
        uriString = getIntent().getExtras().getString("uriString");
        init();
        cropStickerPresenter = new CropStickerPresenter(this,this);
    }

    private void init() {
        CropView = (CropImageView) findViewById(R.id.cropsticker_sticker);
        CropPhoto = (Button) findViewById(R.id.cropsticker_cropphoto);
        BackUser = (Button) findViewById(R.id.cropsticker_backuser);
        CropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
        CropView.load(Uri.parse(uriString)).execute(null);
        CropPhoto.setOnClickListener(this);
        BackUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cropsticker_cropphoto:
                crop();
                break;
            case R.id.cropsticker_backuser:
                backUserInfoHome();
                break;
            case R.id.cropresult_determine:
                cropStickerPresenter.updateSticker(userID,oldSticker,cropBitmap);
                break;
            case R.id.cropresult_cancel:
                resultCancel();
                break;

        }
    }

    private void crop() {
        CropView.crop(Uri.parse(uriString)).execute(new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                cropBitmap=cropped;
                result_init();
                ResultPhoto.setImageBitmap(cropped);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void backUserInfoHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("type", "userInfo");
        startActivity(intent);
    }

    private void result_init() {
        setContentView(R.layout.cropresult);
        ResultPhoto = findViewById(R.id.cropresult_sticker);
        Determine = (Button) findViewById(R.id.cropresult_determine);
        Cancel = (Button) findViewById(R.id.cropresult_cancel);
        Determine.setOnClickListener(this);
        Cancel.setOnClickListener(this);
    }

    private void resultCancel() {
        setContentView(R.layout.activity_cropsticker);
        init();
    }

    @Override
    public void onFinish() {
        backUserInfoHome();
    }
}
