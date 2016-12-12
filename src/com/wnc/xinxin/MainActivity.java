package com.wnc.xinxin;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import common.app.SysInit;

public class MainActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{

    Logger logger = Logger.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(this);

        SysInit.init(MainActivity.this);

        logger.info("start...");
        initView();
    }

    private void initView()
    {
        findViewById(R.id.bt_test).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View arg0)
    {
        switch (arg0.getId())
        {
        case R.id.bt_test:
            System.out.println("test");
            logger.info("Hello, Xinxin!");
            break;

        default:
            break;
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        logger.error("uncaughtException   ", ex);
    }
}
