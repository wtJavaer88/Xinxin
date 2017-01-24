package com.wnc.xinxin.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.alibaba.fastjson.JSONArray;
import com.king.photo.util.BitmapCache;
import com.king.photo.util.BitmapCache.ImageCallback;
import com.king.photo.util.FileUtils;
import com.king.photo.util.PhotoImageCallback;
import com.king.photo.util.PublicWay;
import com.king.photo.util.Res;
import com.wnc.basic.BasicDateUtil;
import com.wnc.xinxin.CloudDataOperation;
import com.wnc.xinxin.Config;
import com.wnc.xinxin.ProgressWheel;
import com.wnc.xinxin.R;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;
import com.wnc.xinxin.service.FsService;
import com.wnc.xinxin.service.TagService;
import common.app.ClickFileIntentFactory;
import common.app.SysInit;

public class HomeActivity extends Activity implements UncaughtExceptionHandler
{
    Logger logger = Logger.getLogger(HomeActivity.class);
    LinearLayout ll_home;
    static Map<String, FootStepInfo> isExistFs = new HashMap<String, FootStepInfo>();
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
                    final String uuid = footStepInfo.getUuid();
                    if (!isExistFs.containsKey(uuid))
                    {
                        isExistFs.put(uuid, footStepInfo);
                        ll_home.addView(getFsLayout(footStepInfo), minHomeDeep);
                    }
                    else
                    {
                        FootStepInfo footStepInfo2 = isExistFs.get(uuid);
                        footStepInfo2.setMedias(footStepInfo.getMedias());
                        footStepInfo2.setDay(footStepInfo.getDay());
                        footStepInfo2.setFs_desc(footStepInfo.getFs_desc());
                        footStepInfo2.setTag_names(footStepInfo.getTag_names());
                        updateFsLayout(footStepInfo);
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

        // for (File f : new File(Environment.getExternalStorageDirectory()
        // .getPath() + "/DCIM/Camera/").listFiles())
        // {
        // logger.info(f.getAbsolutePath());
        // camera_pics.add(f.getAbsolutePath());
        // }

        Thread.setDefaultUncaughtExceptionHandler(this);
        Res.init(this);
        SysInit.init(HomeActivity.this);
        System.out.println(Config.DEVICE_ID);
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
            showBackupDialog();
            new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    // boolean ret = new BackUPTest().testThree();
                    boolean ret = new CloudDataOperation().upload();
                    // System.out.println("queryLastUploadTime:"
                    // + new CloudDataOperation().queryLastUploadTime());
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

    public void showBackupDialog()
    {
        dialog.setContentView(R.layout.backup_dailog);
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
                System.out.println("findAll:" + findAll);
                List<FootStepInfo> parseObject = JSONArray.parseArray(
                        findAll.toString(), FootStepInfo.class);
                for (FootStepInfo footStepInfo : parseObject)
                {
                    System.out.println("uuid:" + footStepInfo.getUuid());
                }
                System.out.println("查数据库耗时:" + (System.currentTimeMillis() - s));
                Message msg = new Message();
                msg.what = 10;
                msg.obj = findAll;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private final static int memo_offset = -1;
    private final static int weekday_offset = -2;
    private final static int day_offset = 1;
    private final static int month_offset = 2;
    private final static int monthday_rl_offset = 3;
    private final static int absoluteLayout_offset = 4;

    // private final static int memo_offset = -1;

    private int generateId(FootStepInfo footStepInfo, int offset)
    {
        return MAX_ID * footStepInfo.getId() + offset;
    }

    /**
     * 根据一个新的实例来更新布局
     * 
     * @param footStepInfo
     */
    private void updateFsLayout(FootStepInfo footStepInfo)
    {
        TextView memoTv = (TextView) findViewById(generateId(footStepInfo,
                memo_offset));
        System.out.println("memoTv:" + memoTv.getText().toString());
        memoTv.setText(footStepInfo.getFs_desc());

        TextView wdTv = (TextView) findViewById(generateId(footStepInfo,
                weekday_offset));
        System.out.println("wdTv:" + wdTv.getText().toString());

        String weekdaystr = "星期六";
        weekdaystr = BasicDateUtil.getGBWeekString(
                footStepInfo.getDay().replace("-", "").substring(0, 8))
                .replace("七", "天");
        wdTv.setText(weekdaystr);

        TextView tv_day = (TextView) findViewById(generateId(footStepInfo,
                day_offset));
        TextView tv_month = (TextView) findViewById(generateId(footStepInfo,
                month_offset));
        String date = footStepInfo.getDay().replace("-", "").substring(0, 8);
        tv_day.setText(date.substring(6, 8));
        tv_month.setText(date.substring(0, 4) + "." + date.substring(4, 6));
        AbsoluteLayout absoluteLayout = (AbsoluteLayout) findViewById(generateId(
                footStepInfo, absoluteLayout_offset));
        absoluteLayout.removeAllViewsInLayout();
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
                    .getMedia_fullpath();
            bitmapCache.displayBmp(imgView, null, absulute_path, callback);
            absoluteLayout.addView(imgView);
        }
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
        monthday_rl.setId(generateId(footStepInfo, monthday_rl_offset));
        outer_rl.addView(monthday_rl);

        // 星期区
        TextView weekdayTv = getWeekDayTv(monthday_rl, footStepInfo);
        outer_rl.addView(weekdayTv);

        // 备注区
        TextView memoTv = getMemoTv(weekdayTv, footStepInfo);
        outer_rl.addView(memoTv);
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
        absoluteLayout.setId(generateId(footStepInfo, absoluteLayout_offset));
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
                    .getMedia_fullpath();
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

    private TextView getMemoTv(TextView weekdayTv, FootStepInfo footStepInfo)
    {
        TextView tv_memo = new TextView(this);
        tv_memo.setId(generateId(footStepInfo, memo_offset));
        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_tv.addRule(RelativeLayout.ALIGN_BOTTOM, weekdayTv.getId());
        lp_tv.addRule(RelativeLayout.RIGHT_OF, weekdayTv.getId());
        lp_tv.leftMargin = 30;
        tv_memo.setTextSize(12);
        tv_memo.setText(footStepInfo.getFs_desc());
        tv_memo.setLayoutParams(lp_tv);
        return tv_memo;
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
        tv_weekday.setId(generateId(footStepInfo, weekday_offset));
        String weekdaystr = "星期六";
        weekdaystr = BasicDateUtil.getGBWeekString(
                footStepInfo.getDay().replace("-", "").substring(0, 8))
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
        tv_day.setId(generateId(footStepInfo, day_offset));
        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_tv.addRule(RelativeLayout.CENTER_IN_PARENT);
        tv_day.setLayoutParams(lp_tv);
        String date = footStepInfo.getDay().replace("-", "").substring(0, 8);
        tv_day.setText(date.substring(6, 8));
        tv_day.setTextSize(20 * TextScale);
        TextPaint paint = tv_day.getPaint();
        paint.setFakeBoldText(true);

        TextView tv_month = new TextView(this);
        tv_month.setId(generateId(footStepInfo, month_offset));

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

            MainActivity.setInsertMode(false);
            // 进入编辑模块
            startActivity(new Intent(HomeActivity.this, MainActivity.class)
                    .putExtra("fsinfo", footStepInfo));
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
                imgs.add(media.getMedia_fullpath());
            }
            MainActivity.setInsertMode(false);
            // 进入记录页面
            // startActivity(new Intent(HomeActivity.this, MainActivity.class)
            // .putExtra("memo", imgTag.getFootStepInfo().getDesc())
            // .putExtra("tags", imgTag.getFootStepInfo().getTag_names())
            // .putExtra("index", imgTag.getIndex())
            // .putStringArrayListExtra("medias", imgs));
            String imgPath = imgs.get(imgTag.getIndex());
            if (FileUtils.isThumbPic(imgPath))
            {
                startActivity(ClickFileIntentFactory.getIntentByFile(imgPath
                        .replace(".jpg", "")));
            }
            else
            {
                // 进入图片浏览模块
                startActivity(new Intent(HomeActivity.this,
                        MediaViewActivity.class)
                        .putExtra("memo", imgTag.getFootStepInfo().getFs_desc())
                        .putExtra("tags",
                                imgTag.getFootStepInfo().getTag_names())
                        .putExtra("index", imgTag.getIndex())
                        .putStringArrayListExtra("medias", imgs));
            }
        }
    };

    @Override
    protected void onRestart()
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
