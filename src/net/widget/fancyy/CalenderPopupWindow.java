package net.widget.fancyy;

import java.util.ArrayList;
import java.util.List;

import net.widget.fancyy.KCalendar.OnCalendarClickListener;
import net.widget.fancyy.KCalendar.OnCalendarCompleteListener;
import net.widget.fancyy.KCalendar.OnCalendarDateChangedListener;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wnc.xinxin.R;

public class CalenderPopupWindow extends PopupWindow
{

    public CalenderPopupWindow(Context mContext, String date,
            final OnCalendarCompleteListener calendarCompleteListener)
    {

        View view = View.inflate(mContext, R.layout.popupwindow_calendar, null);
        view.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_in));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_bottom_in_1));

        setWidth(LayoutParams.FILL_PARENT);
        setHeight(LayoutParams.FILL_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        // showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        update();

        final TextView popupwindow_calendar_month = (TextView) view
                .findViewById(R.id.popupwindow_calendar_month);
        final KCalendar calendar = (KCalendar) view
                .findViewById(R.id.popupwindow_calendar);
        Button popupwindow_calendar_bt_enter = (Button) view
                .findViewById(R.id.popupwindow_calendar_bt_enter);

        popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年"
                + calendar.getCalendarMonth() + "月");

        if (null != date)
        {

            int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
            int month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
                    date.lastIndexOf("-")));
            popupwindow_calendar_month.setText(years + "年" + month + "月");

            calendar.setSelectedDay(date);
            calendar.showCalendar(years, month);
            calendar.setCalendarDayBgColor(date,
                    R.drawable.calendar_date_focused);
        }

        List<String> list = new ArrayList<String>(); // 设置标记列表
        list.add("2017-01-01");
        list.add("2017-01-02");
        calendar.addMarks(list, 0);

        // 监听所选中的日期
        calendar.setOnCalendarClickListener(new OnCalendarClickListener()
        {

            @Override
            public void onCalendarClick(int row, int col, String dateFormat)
            {
                calendarCompleteListener.onCalendarComplete(calendar
                        .getSelectedDay());
                dismiss();
            }
        });

        // 监听当前月份
        calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener()
        {
            @Override
            public void onCalendarDateChanged(int year, int month)
            {
                popupwindow_calendar_month.setText(year + "年" + month + "月");
            }
        });

        // 上月监听按钮
        RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
                .findViewById(R.id.popupwindow_calendar_last_month);
        popupwindow_calendar_last_month
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        calendar.lastMonth();
                    }

                });

        // 下月监听按钮
        RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
                .findViewById(R.id.popupwindow_calendar_next_month);
        popupwindow_calendar_next_month
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        calendar.nextMonth();
                    }
                });

        // 关闭窗口
        popupwindow_calendar_bt_enter.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                calendarCompleteListener.onCalendarComplete(calendar
                        .getSelectedDay());
                dismiss();
            }
        });
    }
}