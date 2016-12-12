package common.app;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

public class UriUtil
{
    /**
     * 通过Uri返回File文件 注意：通过相机的是类似content://media/external/images/media/97596
     * 通过相册选择的：file:///storage/sdcard0/DCIM/Camera/IMG_20150423_161955.jpg
     * 通过查询获取实际的地址
     * 
     * @param uri
     * @return
     */
    public static File getFileByUri(Uri uri, Activity activity)
    {
        String path = null;
        if ("file".equals(uri.getScheme()))
        {
            path = uri.getEncodedPath();
            if (path != null)
            {
                path = Uri.decode(path);
                ContentResolver cr = activity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]
                        { Images.ImageColumns._ID, Images.ImageColumns.DATA },
                        buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext())
                {
                    index = cur.getColumnIndex(Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0)
                {
                }
                else
                {
                    Uri u = Uri.parse("content://media/external/images/media/"
                            + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null)
            {
                return new File(path);
            }
        }
        else if ("content".equals(uri.getScheme()))
        {
            // 4.2.2以后
            String[] proj =
            { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.getContentResolver().query(uri, proj,
                    null, null, null);
            if (cursor.moveToFirst())
            {
                int columnIndex = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        }
        else
        {
            Log.i("uri", "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }
}
