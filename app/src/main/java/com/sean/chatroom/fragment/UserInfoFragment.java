package com.sean.chatroom.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sean.chatroom.CropStickerActivity;
import com.sean.chatroom.QRcodeActivity;
import com.sean.chatroom.R;
import com.sean.chatroom.UserDataUpdateActivity;
import com.sean.chatroom.adapter.UserInfoAdapter;
import com.sean.chatroom.bean.UserInfoItem;
import com.sean.chatroom.presenter.UserInfoPresenter;
import com.sean.chatroom.view.UserInfoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public class UserInfoFragment extends Fragment implements UserInfoView, View.OnClickListener {
    private ImageView background;
    private CircleImageView sticker;
    private UserInfoPresenter userInfoPresenter;
    private UserInfoAdapter userInfoAdapter;
    private RecyclerView recyclerView;
    private ImageView selectBackground, selectSticker;
    private Uri outputFileUri;
    private boolean startCrop = false;
    private String userID, Sticker, Background;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userdata, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        userInfoPresenter = new UserInfoPresenter(this, getContext());
        userInfoPresenter.getUserPhoto();
        userInfoPresenter.getUserData();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        selectBackground.setOnClickListener(this);
        selectSticker.setOnClickListener(this);
        if (!checkNetWork()) {
            Toast.makeText(getContext(), "沒有網路無法更新資料!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void init(View view) {
        background = (ImageView) view.findViewById(R.id.userdata_background);
        sticker = (CircleImageView) view.findViewById(R.id.userdata_sticker);
        selectBackground = (ImageView) view.findViewById(R.id.userdata_select_background);
        selectSticker = (ImageView) view.findViewById(R.id.userdata_select_sticker);
        recyclerView = (RecyclerView) view.findViewById(R.id.userdata_recyclerview);
    }

    public void setUserPhoto() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/chatroom/";
        if (!Background.equals("")) {
            File BackGround = new File(path + "background/" + Background + ".jpg");
            Picasso.get().load(BackGround).into(background);
        }
        if (!Sticker.equals("")) {
            File StickerFile = new File(path + "sticker/" + Sticker + ".jpg");
            Picasso.get().load(StickerFile).into(sticker);
        }
    }

    @Override
    public void setAdapter(final List<UserInfoItem> item) {
        userInfoAdapter = new UserInfoAdapter(item, getContext());
        recyclerView.setAdapter(userInfoAdapter);
        userInfoAdapter.setOnItemClickListener(new UserInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (postion == 4) {
                    goQRcode(item.get(postion).getData());
                } else {
                    if (checkNetWork()) {
                        if (postion == 3) {
                            item.get(postion).setCheck(!item.get(postion).isCheck());
                            userInfoPresenter.updateIdSearch(userID, "id_search", item.get(postion).isCheck());
                        } else if (postion == 4) {
                            goQRcode(item.get(postion).getData());
                        } else {
                            updateData(item, postion);
                        }
                    } else {
                        Toast.makeText(getContext(), "沒有網路無法更新資料!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userdata_select_sticker:
                startCrop = true;
                if (checkNetWork()) {
                    SelectPhoto();
                } else {
                    Toast.makeText(getContext(), "沒有網路無法更新資料!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.userdata_select_background:
                startCrop = false;
                if (checkNetWork()) {
                    getStoragePermission();
                } else {
                    Toast.makeText(getContext(), "沒有網路無法更新資料!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void SelectPhoto() {
        String[] item = {"選擇照片", "拍攝照片"};
        AlertDialog alert = new AlertDialog.Builder(getContext())
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            getStoragePermission();
                        } else {
                            getCameraPermission();
                        }
                    }
                }).show();
    }

    private void getStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 123);
        } else {
            openGallery();
        }
    }

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, 456);
        } else {
            openCamera();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 567);
    }

    /**
     * android 7.0開始 不允許使用file:// 的方式，傳遞一個 File否則會throw FileUriExposedException 造成Crash
     * 所以必須使用FileProvider 通過 content://的模式替換掉 file:// 避免產生 FileUriExposedException而使APP Crash
     **/
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File tmpFile = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        outputFileUri = FileProvider.getUriForFile(getContext(), "com.sean.chatroom", tmpFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 678);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String uriString = null;
            if (requestCode == 567) {
                uriString = data.getData().toString();
            } else if (requestCode == 678) {
                uriString = outputFileUri.toString();
            }
            if (startCrop) {
                Intent intent = new Intent(getActivity(), CropStickerActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("oldSticker", Sticker);
                intent.putExtra("uriString", uriString);
                startActivity(intent);
            } else {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    userInfoPresenter.updateBackground(userID, Background, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onUpdateBackground(String photoName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/chatroom/background/" + photoName + ".jpg";
        File Background = new File(path);
        Picasso.get().load(Background).into(background);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(getContext(), "需要權限才能讀寫圖片", Toast.LENGTH_SHORT).show();
                }
                break;
            case 456:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "需要權限才能使用相機", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    //檢查網路
    private boolean checkNetWork() {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isConnected();
        } else {
            return false;
        }
    }

    @Override
    public void getUserPhoto(String userID, String sticker, String background) {
        this.userID = userID;
        this.Sticker = sticker;
        this.Background = background;
        Log.i("Sean", "getUserPhoto: " + background);
        setUserPhoto();
    }

    @Override
    public void onUserDataUpdate() {
        userInfoAdapter.notifyDataSetChanged();
    }

    private void updateData(List<UserInfoItem> item, int postion) {
        Intent intent = new Intent(getActivity(), UserDataUpdateActivity.class);
        intent.putExtra("userID", userID);
        intent.putExtra("title", item.get(postion).getTitle());
        intent.putExtra("value", item.get(postion).getData());
        switch (postion) {
            case 0:
                intent.putExtra("type", "name");
                break;
            case 1:
                intent.putExtra("type", "phone");
                break;
            case 2:
                intent.putExtra("type", "id");
                break;
        }
        startActivity(intent);
    }

    private void goQRcode(String qrcode) {
        Intent intent = new Intent(getActivity(), QRcodeActivity.class);
        intent.putExtra("qrcode", qrcode);
        startActivity(intent);
    }
}
