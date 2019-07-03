package com.sean.chatroom;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;

public class QRcodeActivity extends AppCompatActivity {
    private String qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        qrcode=getIntent().getExtras().getString("qrcode");
        String full_qrcode="http://sean.me/qrcode/"+qrcode;
        genCode(full_qrcode);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("行動條碼");
        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.toolbar_back));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRcodeActivity.this, HomeActivity.class);
                intent.putExtra("type", "userInfo");
                startActivity(intent);
            }
        });
    }

    public void genCode(String qrcode) {
        ImageView ivCode = (ImageView) findViewById(R.id.ivCode);
        BarcodeEncoder encoder = new BarcodeEncoder();
        Map hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try {
            Bitmap bitmap = encoder.encodeBitmap(qrcode, BarcodeFormat.QR_CODE, 250, 250, hints);
            ivCode.setImageBitmap(addLogo(bitmap));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap addLogo(Bitmap bitmapCode) {
        Bitmap bitmapLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon);
        int qrCodeWidth = bitmapCode.getWidth();
        int qrCodeHeight = bitmapCode.getHeight();
        int logoWidth = bitmapLogo.getWidth();
        int logoHeight = bitmapLogo.getHeight();
        Bitmap blankBitmap = Bitmap.createBitmap(qrCodeWidth, qrCodeHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blankBitmap);
        canvas.drawBitmap(bitmapCode, 0, 0, null);
        float scaleSize = 1.0f;
        while ((logoWidth / scaleSize) > (qrCodeWidth / 3) || (logoHeight / scaleSize) > (qrCodeHeight / 3)) {
            scaleSize += 1;
        }
        float sx = 1.0f / scaleSize;
        canvas.scale(sx, sx, qrCodeWidth / 2, qrCodeHeight / 2);
        canvas.drawBitmap(bitmapLogo, (qrCodeWidth - logoWidth) / 2, (qrCodeHeight - logoHeight) / 2, null);
        return blankBitmap;
    }

    public void startQRcodeScanner(View view){
        getCameraPermission();
    }

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, 123);
        }else {
            startScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanner();
                } else {
                    Toast.makeText(this, "沒有權限無法掃描條碼", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startScanner(){
        Intent intent=new Intent(this,QRcodeScannerActivity.class);
        intent.putExtra("qrcode", qrcode);
        startActivity(intent);
    }
}
