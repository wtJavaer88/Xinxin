package com.king.photo.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.wnc.xinxin.ui.MainActivity;

public class BitmapCache
{

    public Handler h = new Handler();
    public final String TAG = getClass().getSimpleName();
    private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

    private void put(String path, Bitmap bmp)
    {
        if (!TextUtils.isEmpty(path) && bmp != null)
        {
            imageCache.put(path, new SoftReference<Bitmap>(bmp));
        }
    }

    public void displayBmp(final ImageView iv, final String thumbPath,
            final String sourcePath, final ImageCallback callback)
    {
        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath))
        {
            Log.e(TAG, "no paths pass in");
            return;
        }

        final String path;
        final boolean isThumbPath;
        if (!TextUtils.isEmpty(thumbPath))
        {
            path = thumbPath;
            isThumbPath = true;
        }
        else if (!TextUtils.isEmpty(sourcePath))
        {
            path = sourcePath;
            isThumbPath = false;
        }
        else
        {
            // iv.setImageBitmap(null);
            return;
        }

        if (imageCache.containsKey(path))
        {
            SoftReference<Bitmap> reference = imageCache.get(path);
            Bitmap bmp = reference.get();
            if (bmp != null)
            {
                if (callback != null)
                {
                    callback.imageLoad(iv, bmp);
                }
                iv.setImageBitmap(bmp);
                Log.d(TAG, "hit cache");
                return;
            }
            // 如果已经被回收了, 重新生成
            Log.e(TAG, "recreate cache");

        }
        iv.setImageBitmap(null);

        new Thread()
        {
            Bitmap thumb;

            @Override
            public void run()
            {

                try
                {
                    if (isThumbPath)
                    {
                        thumb = BitmapFactory.decodeFile(thumbPath);
                        if (thumb == null)
                        {
                            thumb = Bimp.revitionImageSize(sourcePath, 256);
                        }
                    }
                    else
                    {
                        thumb = Bimp.revitionImageSize(sourcePath, 256);
                    }
                }
                catch (Exception e)
                {

                }
                if (thumb == null)
                {
                    thumb = MainActivity.bimap;
                }
                Log.d(TAG, "-------thumb creating------" + thumb);
                put(path, thumb);

                if (callback != null)
                {
                    h.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            callback.imageLoad(iv, thumb);
                        }
                    });
                }
            }
        }.start();

    }

    public interface ImageCallback
    {
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                Object... params);
    }

}
