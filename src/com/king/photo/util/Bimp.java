package com.king.photo.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class Bimp
{
    public static int max = 0;
    public static String memo = "";
    public static String day = "";
    public static String tags = "[]";
    public static int fs_id = 0;
    // 是否有已修改的数据没保存
    public static boolean need_save = false;
    public static boolean imageChanged = false;

    private static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>(); // 选择的图片的临时列表

    public static void setImageChanged(boolean b)
    {
        imageChanged = b;
    }

    public static boolean isImageChanged()
    {
        return imageChanged;
    }

    public static void addImageItem(ImageItem item)
    {
        tempSelectBitmap.add(item);
        imageChanged = true;
    }

    public static void removeImageItem(ImageItem item)
    {
        tempSelectBitmap.remove(item);
        imageChanged = true;
    }

    public static void removeImageItem(int index)
    {
        tempSelectBitmap.remove(index);
        need_save = true;
    }

    public static void clearAllBitmap()
    {
        imageChanged = false;
        for (ImageItem item : getTempSelectBitmap())
        {
            Bitmap bitmap = item.getBitmap();
            if (!bitmap.isRecycled())
            {
                bitmap.recycle(); // 回收图片所占的内存
                bitmap = null;
                System.gc(); // 提醒系统及时回收
            }
        }
        tempSelectBitmap.clear();
        System.gc();
    }

    public static Options getBitmapOption(int inSampleSize)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public static Bitmap revitionImageSize(String path, int size)
            throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true)
        {
            if ((options.outWidth >> i <= size)
                    && (options.outHeight >> i <= size))
            {
                in = new BufferedInputStream(
                        new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    public static ArrayList<ImageItem> getTempSelectBitmap()
    {
        return tempSelectBitmap;
    }

    public static void setTempSelectBitmap(ArrayList<ImageItem> tempSelectBitmap)
    {
        Bimp.tempSelectBitmap = tempSelectBitmap;
    }
}
