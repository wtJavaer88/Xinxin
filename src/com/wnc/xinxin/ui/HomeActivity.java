package com.wnc.xinxin.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.xinxin.Config;
import com.wnc.xinxin.FsService;
import com.wnc.xinxin.R;
import com.wnc.xinxin.TagService;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;
import common.app.SysInit;
import common.uihelper.MyAppParams;

public class HomeActivity extends Activity implements UncaughtExceptionHandler
{
    Logger logger = Logger.getLogger(HomeActivity.class);
    LinearLayout ll_home, ll_start_record;
    List<FootStepInfo> findAll = new ArrayList<FootStepInfo>();
    static List<Integer> isExistFs = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_test);
        Thread.setDefaultUncaughtExceptionHandler(this);
        SysInit.init(HomeActivity.this);

        logger.info("start...");
        final TagService tagService = new TagService();
        tagService.init();
        tagService.findAllTagNames();
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
        ll_start_record = (LinearLayout) findViewById(R.id.ll_start_record);
        ll_start_record.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                System.out.println("onclick...start");
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });
        ll_home = (LinearLayout) findViewById(R.id.ll_home);
        minHomeDeep = ll_home.getChildCount();
        refreshFs();
    }

    int minHomeDeep = 0;
    final static float TextScale = 0.75f;
    final static int MAX_ID = 10000;

    private void refreshFs()
    {
        findAll = new FsService().findAll();
        System.out.println(isExistFs);
        for (FootStepInfo footStepInfo : findAll)
        {
            if (!isExistFs.contains(footStepInfo.getId()))
            {
                isExistFs.add(footStepInfo.getId());
                ll_home.addView(getFsLayout(footStepInfo), minHomeDeep);
            }
        }
        for (int i = 1300; i >= 10; i--)
        {
            // ll_home.addView(getFsLayout(getFsSample(i)));
        }
    }

    private FootStepInfo getFsSample(int index)
    {
        FootStepInfo footStepInfo = new FootStepInfo();
        footStepInfo.setId(index);
        footStepInfo.setCreate_time("2016-12-" + index + " 11:11:11");
        List<FsMedia> medias = new ArrayList<FsMedia>();
        for (int i = 0; i < 3; i++)
        {
            FsMedia media = new FsMedia();
            media.setMedia_name("media" + (i + 1));
            media.setMedia_size(100 * i);
            media.setMediapath_id(Config.MEDIAPATH_ID);
            media.setMedia_type("jpg");
            media.setAbsulute_path(MyAppParams.getInstance().getWorkPath() + i
                    + ".jpg");
            medias.add(media);
        }
        footStepInfo.setMedias(medias);
        return footStepInfo;
    }

    private RelativeLayout getFsLayout(FootStepInfo footStepInfo)
    {
        RelativeLayout outer_rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        outer_rl.setLayoutParams(lp);
        RelativeLayout inner_rl = getInnerRl(footStepInfo);
        inner_rl.setId(MAX_ID * footStepInfo.getId() + 2);
        outer_rl.addView(inner_rl);
        TextView weekdayTv = getWeekDayTv(inner_rl, footStepInfo);
        outer_rl.addView(weekdayTv);
        AbsoluteLayout absoluteLayout = getPicZone(inner_rl, footStepInfo);
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
            // imgView.setImageResource(R.drawable.icon_fs_add);
            imgView.setTag(new ImgTag(footStepInfo, i));
            imgView.setOnClickListener(fsViewClickListener);
            imgView.setImageDrawable(Drawable.createFromPath(footStepInfo
                    .getMedias().get(i).getAbsulute_path()));
            absoluteLayout.addView(imgView);
        }
        return absoluteLayout;
    }

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

    private RelativeLayout getInnerRl(FootStepInfo footStepInfo)
    {
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                (int) (200 * TextScale), (int) (200 * TextScale));
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
        return relativeLayout;
    }

    OnClickListener fsViewClickListener = new OnClickListener()
    {

        @Override
        public void onClick(View arg0)
        {
            ImgTag imgTag = ((ImgTag) arg0.getTag());
            // System.out.println("该日记的图片总数:" +
            // imgTag.getFootStepInfo().getMedias().size());
            // System.out.println("你点击的是第" + imgTag.getIndex() + "个");
            ArrayList<String> imgs = new ArrayList<String>();
            for (FsMedia media : imgTag.getFootStepInfo().getMedias())
            {
                imgs.add(media.getAbsulute_path());
            }
            startActivity(new Intent(HomeActivity.this, MediaViewActivity.class)
                    .putExtra("memo", imgTag.getFootStepInfo().getDesc())
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
}
