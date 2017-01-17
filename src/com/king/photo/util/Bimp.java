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
	public static String tags = "";
	public static int fs_id = 0;

	public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>(); // 选择的图片的临时列表

	public static void clearAllBitmap()
	{
		for (ImageItem item : tempSelectBitmap)
		{
			Bitmap bitmap = item.getBitmap();
			if (!bitmap.isRecycled())
			{
				bitmap.recycle(); // 回收图片所占的内存
				bitmap = null;
				// System.gc(); // 提醒系统及时回收
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

	public static Bitmap revitionImageSize(String path, int size) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true)
		{
			if ((options.outWidth >> i <= size) && (options.outHeight >> i <= size))
			{
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
}
