package com.king.photo.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.king.photo.util.BitmapCache.ImageCallback;

public class PhotoImageCallback implements ImageCallback
{
    final String TAG = getClass().getSimpleName();

    @Override
    public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params)
    {
        if (imageView != null && bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            Log.e(TAG, "callback, bmp null");
        }
    }

}