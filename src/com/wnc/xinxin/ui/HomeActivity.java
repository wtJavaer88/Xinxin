package com.wnc.xinxin.ui;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.king.photo.util.Bimp;
import com.king.photo.util.BitmapCache;
import com.king.photo.util.BitmapCache.ImageCallback;
import com.king.photo.util.PhotoImageCallback;
import com.king.photo.util.PublicWay;
import com.king.photo.util.Res;
import com.wnc.basic.BasicDateUtil;
import com.wnc.xinxin.BackUPTest;
import com.wnc.xinxin.FsService;
import com.wnc.xinxin.ProgressWheel;
import com.wnc.xinxin.R;
import com.wnc.xinxin.TagService;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;
import common.app.SysInit;

public class HomeActivity extends Activity implements UncaughtExceptionHandler
{
    Logger logger = Logger.getLogger(HomeActivity.class);
    LinearLayout ll_home;
    static List<Integer> isExistFs = new ArrayList<Integer>();
    Handler handler = new Handler()
    {
        @Override
        public void dispatchMessage(android.os.Message msg)
        {
            final int what = msg.what;
            if (what == 100 || what == 101)
            {
                final TextView textView = (TextView) dialog
                        .findViewById(R.id.tvBUResult);
                if (what == 100)
                {
                    ((ProgressWheel) dialog.findViewById(R.id.progressBar))
                            .stopSpinning();

                    textView.setVisibility(View.VISIBLE);
                    textView.setText("备份成功");
                }
                if (what == 101)
                {
                    ((ProgressWheel) dialog.findViewById(R.id.progressBar))
                            .stopSpinning();
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("备份失败");
                }
            }
            else if (what == 10)
            {
                for (FootStepInfo footStepInfo : (List<FootStepInfo>) msg.obj)
                {
                    if (!isExistFs.contains(footStepInfo.getId()))
                    {
                        isExistFs.add(footStepInfo.getId());
                        ll_home.addView(getFsLayout(footStepInfo), minHomeDeep);
                    }
                }
            }
        };
    };
    BitmapCache bitmapCache;
    List<String> camera_pics = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_test);
        bitmapCache = new BitmapCache();

        for (File f : new File(Environment.getExternalStorageDirectory()
                .getPath() + "/DCIM/Camera/").listFiles())
        {
            logger.info(f.getAbsolutePath());
            camera_pics.add(f.getAbsolutePath());
        }

        Thread.setDefaultUncaughtExceptionHandler(this);
        Res.init(this);
        SysInit.init(HomeActivity.this);
        logger.info("start...");

        final TagService tagService = new TagService();
        tagService.init();
        tagService.findAllTagNames();
        initActionBar();
        initDrawerLayout();
        initView();

    }

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    private void initDrawerLayout()
    {
        drawerLayout = (DrawerLayout) super.findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.back_move_details_normal, R.string.drawer_open,
                R.string.drawer_close)
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

        };
        drawerLayout.setDrawerListener(toggle);
    }

    private void toggleLeftSliding()
    {// 该方法控制左侧边栏的显示和隐藏
        if (drawerLayout.isDrawerOpen(Gravity.START))
        {
            drawerLayout.closeDrawer(Gravity.START);
        }
        else
        {
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    private void toggleRightSliding()
    {// 该方法控制右侧边栏的显示和隐藏
        if (drawerLayout.isDrawerOpen(Gravity.END))
        {
            drawerLayout.closeDrawer(Gravity.END);
        }
        else
        {
            drawerLayout.openDrawer(Gravity.END);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    Dialog dialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            toggleLeftSliding();
            break;
        case R.id.usersetting:
            toggleRightSliding();
            break;
        case R.id.backup:
            dialog = new Dialog(this, R.style.CustomDialogStyle);
            showDialog();
            new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    boolean ret = new BackUPTest().testTwo();
                    if (ret)
                    {
                        handler.sendEmptyMessage(100);
                    }
                    else
                    {
                        handler.sendEmptyMessage(101);
                    }
                }
            }).start();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog()
    {
        dialog.setContentView(R.layout.common_wdailog);
        dialog.setCanceledOnTouchOutside(true);
        ProgressWheel progressWheel = (ProgressWheel) dialog
                .findViewById(R.id.progressBar);
        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.spin();
        dialog.show();
    }

    ActionBar actionBar;

    private void initActionBar()
    {
        actionBar = super.getActionBar();
        actionBar.show();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.com_btn);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView tvTitle = new TextView(this);
        tvTitle.setText("时 光 机");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(18);
        tvTitle.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        tvTitle.setLayoutParams(params);
        actionBar.setCustomView(tvTitle);
    }

    OnClickListener startNewFsListener = new OnClickListener()
    {
        @Override
        public void onClick(View arg0)
        {
            System.out.println("onclick...start");
            MainActivity.setInsertMode(true);
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
    };

    private void initView()
    {
        findViewById(R.id.start_bt).setOnClickListener(startNewFsListener);
        findViewById(R.id.start_tv).setOnClickListener(startNewFsListener);
        ll_home = (LinearLayout) findViewById(R.id.ll_home);
        minHomeDeep = ll_home.getChildCount();
        refreshFs();
    }

    int minHomeDeep = 0;
    final static float TextScale = 0.75f;
    final static int MAX_ID = 10000;

    private void refreshFs_test()
    {
        handler.post(new Runnable()
        {

            @Override
            public void run()
            {
                final long s = System.currentTimeMillis();
                System.out.println("开始查数据库:" + s);
                List<FootStepInfo> findAll = new FsService().findAll();
                System.out.println("查数据库耗时:" + (System.currentTimeMillis() - s));
                for (FootStepInfo footStepInfo : findAll)
                {
                    for (int i = 0; i < 500; i++)
                    {
                        ll_home.addView(getFsLayout(footStepInfo), minHomeDeep);
                    }
                }
            }
        });

    }

    private void refreshFs()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                final long s = System.currentTimeMillis();
                System.out.println("开始查数据库:" + s);
                List<FootStepInfo> findAll = new FsService().findAll();
                System.out.println("查数据库耗时:" + (System.currentTimeMillis() - s));
                Message msg = new Message();
                msg.what = 10;
                msg.obj = findAll;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private RelativeLayout getFsLayout(FootStepInfo footStepInfo)
    {
        RelativeLayout outer_rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        outer_rl.setLayoutParams(lp);

        TextView tv = new TextView(this);
        tv.setId(footStepInfo.getId());
        outer_rl.addView(tv);
        // 包含月和日, 并为其后的星期和图片区做坐标参考
        RelativeLayout monthday_rl = getInnerRl(tv, footStepInfo);
        monthday_rl.setId(MAX_ID * footStepInfo.getId() + 2);
        outer_rl.addView(monthday_rl);

        // 星期区
        TextView weekdayTv = getWeekDayTv(monthday_rl, footStepInfo);
        outer_rl.addView(weekdayTv);
        // 图片区
        AbsoluteLayout absoluteLayout = getPicZone(monthday_rl, footStepInfo);
        outer_rl.addView(absoluteLayout);
        return outer_rl;
    }

    @SuppressWarnings("deprecation")
    private AbsoluteLayout getPicZone(RelativeLayout inner_rl,
            FootStepInfo footStepInfo)
    {
        AbsoluteLayout absoluteLayout = new AbsoluteLayout(this);
        RelativeLayout.LayoutParams lp_al = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp_al.addRule(RelativeLayout.BELOW, inner_rl.getId());
        absoluteLayout.setLayoutParams(lp_al);
        int padding = 20;
        for (int i = 0; i < footStepInfo.getMedias().size(); i++)
        {
            ImageView imgView = new ImageView(this);
            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                    300, 300, (300 + padding) * (i % 3), (300 + padding)
                            * (i / 3));
            imgView.setLayoutParams(params);
            imgView.setTag(new ImgTag(footStepInfo, i));
            imgView.setOnClickListener(mediaViewClickListener);
            String absulute_path = footStepInfo.getMedias().get(i)
                    .getAbsulute_path();
            // 多图测试
            // absulute_path = camera_pics.get((int) (Math.random() *
            // camera_pics
            // .size()));
            // logger.info(absulute_path);
            // try
            // {
            // imgView.setImageBitmap(Bimp.revitionImageSize(absulute_path,
            // 300));
            // }
            // catch (IOException e)
            // {
            // e.printStackTrace();
            // }

            bitmapCache.displayBmp(imgView, null, absulute_path, callback);
            absoluteLayout.addView(imgView);
        }
        return absoluteLayout;
    }

    ImageCallback callback = new PhotoImageCallback();

    class ImgTag
    {
        private FootStepInfo footStepInfo;
        private int index;

        public ImgTag(FootStepInfo footStepInfo, int index)
        {
            this.footStepInfo = footStepInfo;
            this.index = index;
        }

        public FootStepInfo getFootStepInfo()
        {
            return footStepInfo;
        }

        public void setFootStepInfo(FootStepInfo footStepInfo)
        {
            this.footStepInfo = footStepInfo;
        }

        public int getIndex()
        {
            return index;
        }

        public void setIndex(int index)
        {
            this.index = index;
        }

    }

    private TextView getWeekDayTv(RelativeLayout inner_rl,
            FootStepInfo footStepInfo)
    {
        TextView tv_weekday = new TextView(this);
        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_tv.addRule(RelativeLayout.ALIGN_BOTTOM, inner_rl.getId());
        lp_tv.addRule(RelativeLayout.RIGHT_OF, inner_rl.getId());
        lp_tv.bottomMargin = 30;
        tv_weekday.setTextSize(12);
        String weekdaystr = "星期六";
        weekdaystr = BasicDateUtil.getGBWeekString(
                footStepInfo.getCreate_time().replace("-", "").substring(0, 8))
                .replace("七", "天");
        tv_weekday.setText(weekdaystr);
        tv_weekday.setLayoutParams(lp_tv);
        return tv_weekday;
    }

    private RelativeLayout getInnerRl(TextView tv,
            final FootStepInfo footStepInfo)
    {
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                (int) (200 * TextScale), (int) (200 * TextScale));
        lp.addRule(RelativeLayout.BELOW, tv.getId());
        // 设置上下间隔,看起来美观点
        lp.topMargin = 40;
        lp.bottomMargin = 20;
        relativeLayout.setBackgroundResource(R.drawable.home_item_date_bg);
        relativeLayout.setLayoutParams(lp);

        TextView tv_day = new TextView(this);
        tv_day.setId(MAX_ID * footStepInfo.getId() + 1);
        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_tv.addRule(RelativeLayout.CENTER_IN_PARENT);
        tv_day.setLayoutParams(lp_tv);
        String date = footStepInfo.getCreate_time().replace("-", "")
                .substring(0, 8);
        tv_day.setText(date.substring(6, 8));
        tv_day.setTextSize(20 * TextScale);
        TextPaint paint = tv_day.getPaint();
        paint.setFakeBoldText(true);

        TextView tv_month = new TextView(this);
        RelativeLayout.LayoutParams lp_tv2 = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_tv2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp_tv2.addRule(RelativeLayout.BELOW, tv_day.getId());
        tv_month.setLayoutParams(lp_tv2);
        tv_month.setText(date.substring(0, 4) + "." + date.substring(4, 6));
        tv_month.setTextSize(15 * TextScale);

        relativeLayout.addView(tv_day);
        relativeLayout.addView(tv_month);

        relativeLayout
                .setOnClickListener(new FsEditClickListener(footStepInfo));
        return relativeLayout;
    }

    class FsEditClickListener implements OnClickListener
    {
        FootStepInfo footStepInfo;

        public FsEditClickListener(FootStepInfo footStepInfo)
        {
            this.footStepInfo = footStepInfo;
        }

        @Override
        public void onClick(View arg0)
        {
            ArrayList<String> imgs = new ArrayList<String>();
            for (FsMedia media : footStepInfo.getMedias())
            {
                imgs.add(media.getAbsulute_path());
            }
            // 进入编辑模块
            startActivity(new Intent(HomeActivity.this, MainActivity.class)
                    .putExtra("memo", footStepInfo.getDesc())
                    .putExtra("tags", footStepInfo.getTag_names())
                    .putStringArrayListExtra("medias", imgs));
        }
    };

    OnClickListener mediaViewClickListener = new OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            ImgTag imgTag = ((ImgTag) arg0.getTag());
            System.out.println("该日记的图片总数:"
                    + imgTag.getFootStepInfo().getMedias().size());
            System.out.println("你点击的是第" + imgTag.getIndex() + "个");

            ArrayList<String> imgs = new ArrayList<String>();
            for (FsMedia media : imgTag.getFootStepInfo().getMedias())
            {
                imgs.add(media.getAbsulute_path());
            }
            MainActivity.setInsertMode(false);
            Bimp.fs_id = imgTag.getFootStepInfo().getId();
            // 进入记录页面
            // startActivity(new Intent(HomeActivity.this, MainActivity.class)
            // .putExtra("memo", imgTag.getFootStepInfo().getDesc())
            // .putExtra("tags", imgTag.getFootStepInfo().getTag_names())
            // .putExtra("index", imgTag.getIndex())
            // .putStringArrayListExtra("medias", imgs));

            // 进入图片浏览模块
            startActivity(new Intent(HomeActivity.this, MediaViewActivity.class)
                    .putExtra("memo", imgTag.getFootStepInfo().getDesc())
                    .putExtra("tags", imgTag.getFootStepInfo().getTag_names())
                    .putExtra("index", imgTag.getIndex())
                    .putStringArrayListExtra("medias", imgs));
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        refreshFs();
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        logger.error("uncaughtException   ", ex);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            for (int i = 0; i < PublicWay.activityList.size(); i++)
            {
                if (null != PublicWay.activityList.get(i))
                {
                    PublicWay.activityList.get(i).finish();
                }
            }
            System.exit(0);
        }
        return true;
    }
}
