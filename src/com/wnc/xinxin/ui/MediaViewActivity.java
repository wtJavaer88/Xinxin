package com.wnc.xinxin.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.wnc.xinxin.R;
import com.wnc.xinxin.TagService;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;

public class MediaViewActivity extends Activity implements UncaughtExceptionHandler, CtrlableHorGestureDetectorListener
{
	Logger logger = Logger.getLogger(MainActivity.class);
	ImageView imgview_view_pic;
	TextView tv_img_pos;
	ArrayList<String> medias;
	int curIndex = 0;
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediaview);
		Thread.setDefaultUncaughtExceptionHandler(this);
		this.gestureDetector = new GestureDetector(this, new MyCtrlableGestureDetector(this, 0.15, 0, this, null));
		new TagService().init();
		new TagService().findAllTagNames();
		try
		{
			initView();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initView()
	{
		medias = getIntent().getStringArrayListExtra("medias");
		curIndex = getIntent().getIntExtra("index", 0);
		System.out.println(medias);
		imgview_view_pic = (ImageView) findViewById(R.id.imgview_view_pic);
		tv_img_pos = (TextView) findViewById(R.id.tv_img_pos);
		showCurPic();
	}

	private void showCurPic()
	{
		if (medias.size() > 0 && curIndex >= 0 && curIndex < medias.size())
		{
			imgview_view_pic.setImageDrawable(Drawable.createFromPath(medias.get(curIndex)));
			tv_img_pos.setText((curIndex + 1) + "/" + medias.size());
		}
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable ex)
	{
		logger.error("uncaughtException   ", ex);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
	{
		if (!this.gestureDetector.onTouchEvent(paramMotionEvent))
		{
			return super.dispatchTouchEvent(paramMotionEvent);
		}
		return true;
	}

	@Override
	public void doLeft(FlingPoint p1, FlingPoint p2)
	{
		curIndex--;
		if (curIndex < 0)
		{
			curIndex = 0;
		}
		showCurPic();
	}

	@Override
	public void doRight(FlingPoint p1, FlingPoint p2)
	{
		curIndex++;
		if (curIndex >= medias.size())
		{
			curIndex = medias.size() - 1;
		}
		showCurPic();
	}

}
