package com.wnc.xinxin.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
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
import com.wnc.xinxin.R;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;
import common.app.SysInit;
import common.uihelper.MyAppParams;

public class HomeActivity extends Activity implements UncaughtExceptionHandler
{
    Logger logger = Logger.getLogger(MainActivity.class);
    LinearLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_test);
        Thread.setDefaultUncaughtExceptionHandler(this);
        SysInit.init(HomeActivity.this);

        logger.info("start...");
        try
        {
            initView();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void initView()
    {
        ll_home = (LinearLayout) findViewById(R.id.ll_home);
        for (int i = 13; i >= 10; i--)
        {
            ll_home.addView(getFsLayout(getFsSample(i)));
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
        inner_rl.setId(10000 * footStepInfo.getId() + 2);
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
            imgView.setImageDrawable(Drawable.createFromPath(footStepInfo
                    .getMedias().get(i).getAbsulute_path()));
            absoluteLayout.addView(imgView);
        }
        return absoluteLayout;
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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(200,
                200);
        relativeLayout.setBackgroundResource(R.drawable.home_item_date_bg);
        relativeLayout.setLayoutParams(lp);

        TextView tv_day = new TextView(this);
        tv_day.setId(10000 * footStepInfo.getId() + 1);
        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_tv.addRule(RelativeLayout.CENTER_IN_PARENT);
        tv_day.setLayoutParams(lp_tv);
        String date = footStepInfo.getCreate_time().replace("-", "")
                .substring(0, 8);
        tv_day.setText(date.substring(6, 8));
        tv_day.setTextSize(16);
        TextPaint paint = tv_day.getPaint();
        paint.setFakeBoldText(true);

        TextView tv_month = new TextView(this);
        RelativeLayout.LayoutParams lp_tv2 = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_tv2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp_tv2.addRule(RelativeLayout.BELOW, tv_day.getId());
        tv_month.setLayoutParams(lp_tv2);
        tv_month.setText(date.substring(0, 4) + "." + date.substring(4, 6));
        tv_month.setTextSize(12);

        relativeLayout.addView(tv_day);
        relativeLayout.addView(tv_month);
        return relativeLayout;
    }

    class FsViewClickListener implements OnClickListener
    {
        @Override
        public void onClick(View arg0)
        {
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        logger.error("uncaughtException   ", ex);
    }
}
