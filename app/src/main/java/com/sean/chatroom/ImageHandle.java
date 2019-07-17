package com.sean.chatroom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageHandle {

    //圖片比例壓縮
    public static Bitmap ProportionalCompression(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判斷如果圖片大於1M,進行壓縮避免在生成圖片（BitmapFactory.decodeStream）時溢位
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 這裡壓縮80%，把壓縮後的資料存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options bo = new BitmapFactory.Options();
        bo.inJustDecodeBounds = true;
        bo.inJustDecodeBounds = false;
        int w = bo.outWidth;
        int h = bo.outHeight;
        float ww = 1080f;
        float hh = 1920f;
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (bo.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (bo.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        bo.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, bo);
        //进行 质量压缩
        return QualityCompression(bitmap);//压缩好比例大小后再进行质量压缩

    }

    // 照片品質壓縮
    private static Bitmap QualityCompression(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        int quality = 100;
        while (baos.toByteArray().length / 1024 > 500) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(bais, null, null);
        return bitmap;
    }

}
