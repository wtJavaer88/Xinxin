package com.wnc.xinxin.ui;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.widget.fancyy.CalenderPopupWindow;
import net.widget.fancyy.KCalendar.OnCalendarCompleteListener;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.king.photo.activity.AlbumActivity;
import com.king.photo.activity.GalleryActivity;
import com.king.photo.util.Bimp;
import com.king.photo.util.FileUtils;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.xinxin.FlowLayout;
import com.wnc.xinxin.FsService;
import com.wnc.xinxin.R;
import com.wnc.xinxin.TagService;
import com.wnc.xinxin.dao.TagDao;
import common.app.MyIntentUtil;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.utils.BitmapUtil;
import common.utils.ImageCompressUtil;

/**
 * 首页面activity
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:34
 */
public class MainActivity extends Activity implements UncaughtExceptionHandler,
        OnClickListener
{

    private static boolean is_insert = true;

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;

    EditText memoET;
    FlowLayout tag_vessel;
    ImageView add_tag;
    List<TextView> mTagList = new ArrayList<TextView>();
    List<String> selTagList = new ArrayList<String>();// 保存已选中的标签
    private final int TAG_SEL_COLOR = Color.RED;
    private final int TAG_NOTSEL_COLOR = Color.GRAY;

    Handler mHandler = new Handler();
    Set<String> tag_names = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏

        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_selectimg,
                null);
        setContentView(parentView);
        Thread.setDefaultUncaughtExceptionHandler(this);
        initData();
        InitView();
        resetText();
    }

    private boolean isInsertMode()
    {
        return is_insert;
    }

    public static void setInsertMode(boolean flag)
    {
        System.out.println("setInsertMode:" + flag);
        is_insert = flag;
        if (flag)
        {
            clearStatics();
        }
    }

    private static void clearStatics()
    {
        Bimp.imageChanged = false;
        Bimp.need_save = false;
        Bimp.memo = "";
        Bimp.tags = "[]";
        Bimp.day = "";
    }

    private void initData()
    {
        final Intent intent = getIntent();
        if (intent != null)
        {
            List<String> medias = intent.getStringArrayListExtra("medias");
            if (medias == null)
            {
                return;
            }
            long s = System.currentTimeMillis();
            System.out.println("medias加载开始:" + s);
            for (String pic : medias)
            {
                ImageItem imageItem = new ImageItem();
                imageItem.imagePath = pic;
                Bitmap bitmap = BitmapFactory.decodeFile(pic,
                        Bimp.getBitmapOption(3));
                imageItem.setBitmap(bitmap);
                Bimp.getTempSelectBitmap().add(imageItem);
            }
            System.out
                    .println("medias加载耗时:" + (System.currentTimeMillis() - s));
            if (intent.getStringExtra("memo") != null)
            {
                Bimp.memo = intent.getStringExtra("memo");
            }
            if (intent.getStringExtra("tags") != null)
            {
                Bimp.tags = intent.getStringExtra("tags");
            }
            if (intent.getStringExtra("day") != null)
            {
                Bimp.day = intent.getStringExtra("day");
            }
        }
    }

    Button calendarBt;

    public void InitView()
    {
        calendarBt = (Button) findViewById(R.id.bt_date_sel);
        calendarBt.setOnClickListener(this);
        if (isInsertMode())
        {
            calendarBt.setText(BasicDateUtil.getCurrentDateTimeString()
                    .substring(0, 10));
        }
        else if (BasicStringUtil.isNotNullString(Bimp.day))
        {
            calendarBt.setText(Bimp.day);
        }

        memoET = (EditText) findViewById(R.id.et_fs_desc);
        memoET.setText(Bimp.memo);

        this.tag_vessel = (FlowLayout) findViewById(R.id.tag_vessel);
        this.add_tag = (ImageView) findViewById(R.id.add_tag);
        this.add_tag.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                AddTagDialog();
            }
        });

        for (String tag : Bimp.tags.split("[,\\[\\]]"))
        {
            AddTag(tag, false);
        }

        findViewById(R.id.bt_save).setOnClickListener(this);
        findViewById(R.id.bt_back).setOnClickListener(this);

        pop = new PopupWindow(this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
                null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_video);
        parent.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,
                        AlbumActivity.class);

                checkNeedSave();
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in,
                        R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pop.dismiss();
                ll_popup.clearAnimation();
                // TODO video
                Intent intent = new Intent(MainActivity.this,
                        MovieActivity.class);
                startActivityForResult(intent, VIDEO_RESULT);
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3)
            {
                if (arg2 == Bimp.getTempSelectBitmap().size())
                {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(
                            MainActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                }
                else
                {
                    String imgPath = Bimp.getTempSelectBitmap().get(arg2)
                            .getImagePath();
                    System.out.println(imgPath);
                    if (FileUtils.isVideoThumbPic(imgPath))
                    {
                        Intent videoFileIntent = MyIntentUtil
                                .getVideoFileIntent(imgPath.replace(".jpg", ""));
                        startActivity(videoFileIntent);
                    }
                    else
                    {
                        Intent intent = new Intent(MainActivity.this,
                                GalleryActivity.class);
                        intent.putExtra("position", "1");
                        intent.putExtra("ID", arg2);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape()
        {
            return shape;
        }

        public void setShape(boolean shape)
        {
            this.shape = shape;
        }

        public GridAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        public void update()
        {
            loading();
        }

        @Override
        public int getCount()
        {
            if (Bimp.getTempSelectBitmap().size() == 9)
            {
                return 9;
            }
            return (Bimp.getTempSelectBitmap().size() + 1);
        }

        @Override
        public Object getItem(int arg0)
        {
            return null;
        }

        @Override
        public long getItemId(int arg0)
        {
            return 0;
        }

        public void setSelectedPosition(int position)
        {
            selectedPosition = position;
        }

        public int getSelectedPosition()
        {
            return selectedPosition;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.getTempSelectBitmap().size())
            {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9)
                {
                    holder.image.setVisibility(View.GONE);
                }
            }
            else
            {
                holder.image.setImageBitmap(Bimp.getTempSelectBitmap()
                        .get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder
        {
            public ImageView image;
        }

        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading()
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Bimp.max = 0;
                    while (true)
                    {
                        if (Bimp.max == Bimp.getTempSelectBitmap().size())
                        {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        }
                        else
                        {
                            Bimp.max++;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s)
    {
        String path = null;
        if (s == null)
        {
            return "";
        }
        for (int i = s.length() - 1; i > 0; i++)
        {
            s.charAt(i);
        }
        return path;
    }

    @Override
    protected void onRestart()
    {
        adapter.update();
        System.out.println("onResume>memo: " + Bimp.memo);
        resetText();
        super.onRestart();
    }

    private void resetText()
    {
        if (BasicStringUtil.isNotNullString(Bimp.memo))
        {
            memoET.setText(Bimp.memo);
        }
        if (BasicStringUtil.isNotNullString(Bimp.tags))
        {
            for (String tag : Bimp.tags.split("[,\\[\\]]"))
            {
                AddTag(tag, false);
            }
        }
        if (BasicStringUtil.isNotNullString(Bimp.day))
        {
            calendarBt.setText(Bimp.day);
        }
    }

    private static final int TAKE_PICTURE = 0x000001;
    private static final int VIDEO_RESULT = 0x000010;

    File mPhotoFile;

    public void photo()
    {

        mPhotoFile = new File(MyAppParams.getInstance().getMediaPath(),
                BasicDateUtil.getCurrentDateTimeString() + ".jpg");
        mPhotoFile.delete();
        if (!mPhotoFile.exists())
        {
            try
            {
                mPhotoFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Toast.makeText(getApplication(), "照片创建失败!", Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
        case TAKE_PICTURE:
            if (Bimp.getTempSelectBitmap().size() < 9
                    && resultCode == RESULT_OK)
            {
                if (this.mPhotoFile != null && this.mPhotoFile.exists())
                {
                    Bimp.setImageChanged(true);
                    System.out.println("mPhotoFile::" + this.mPhotoFile);

                    String scaledImgPath = MyAppParams.getInstance()
                            .getMediaPath()
                            + System.currentTimeMillis()
                            + "_s.jpg";
                    if (ImageCompressUtil.transImageToMaxSize(
                            mPhotoFile.getAbsolutePath(), scaledImgPath, 1024,
                            1024, 60))
                    {
                        System.out.println("压缩成功! " + scaledImgPath);

                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setBitmap(BitmapFactory
                                .decodeStream(getClass().getResourceAsStream(
                                        scaledImgPath)));
                        takePhoto.setImagePath(scaledImgPath);
                        Bimp.addImageItem(takePhoto);
                    }
                }
                else
                {
                    System.out.println("img Err!");
                }

            }
            break;
        case VIDEO_RESULT:
            String moviePath = data.getStringExtra(MovieActivity.RECORDED_PATH);
            System.out.println("返回的moviePath:" + moviePath);
            if (BasicFileUtil.isExistFile(moviePath))
            {
                Bimp.setImageChanged(true);
                Bitmap bitmap = null;
                bitmap = ThumbnailUtils.createVideoThumbnail(moviePath,
                        Images.Thumbnails.MICRO_KIND);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 256, 256,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                // Bitmap textBitmap = BitmapUtil.drawTextToCenter(this, bitmap,
                // "视频", 8, Color.RED);
                Bitmap watermarkBitmap = BitmapUtil.createWaterMaskCenter(
                        bitmap, BitmapFactory.decodeResource(getResources(),
                                R.drawable.icon_video_shorcut));

                final String savePath = MyAppParams.getInstance()
                        .getMediaPath()
                        + BasicFileUtil.getFileName(moviePath)
                        + ".jpg";
                BitmapUtil.saveMyBitmap(watermarkBitmap, savePath);
                ImageItem takePhoto = new ImageItem();
                takePhoto.setBitmap(watermarkBitmap);
                takePhoto.setImagePath(savePath);
                Bimp.addImageItem(takePhoto);
            }
            break;
        }
    }

    private void setNeedSave(boolean b)
    {
        Bimp.need_save = b;
    }

    /**
     * 添加标签的对话框
     */
    public void AddTagDialog()
    {
        final Dialog dlg = new Dialog(this, R.style.dialog);
        dlg.show();
        dlg.getWindow().setGravity(Gravity.TOP);
        dlg.getWindow().setLayout((int) (MyAppParams.getScreenWidth() * 0.8),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
        TextView add_tag_dialg_title = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_title);
        add_tag_dialg_title.setText("添加日记标签");
        final AutoCompleteTextView actv = (AutoCompleteTextView) dlg
                .findViewById(R.id.autocomplete_tag);
        List<String> findAllTagNames = new TagService().findAllTagNames();
        System.out.println("findAllTagNames:" + findAllTagNames);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.item_tag, R.id.tv_view_tag_name, findAllTagNames);
        actv.setAdapter(arrayAdapter);
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("定时器开始");
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        actv.showDropDown();
                    }
                });
            }
        }, 300);
        actv.setOnTouchListener(new View.OnTouchListener()
        {
            // 按住和松开的标识
            int touch_flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                touch_flag++;
                if (touch_flag % 3 == 1)
                {
                    // 手动调用
                    actv.showDropDown();
                }
                return false;
            }
        });
        actv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                Object obj = parent.getItemAtPosition(position);
                System.out.println("点击:" + obj);
                AddTag(obj.toString(), false);
                dlg.dismiss();
            }
        });
        TextView add_tag_dialg_no = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_no);
        TextView add_tag_dialg_ok = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_ok);
        add_tag_dialg_no.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dlg.dismiss();
            }
        });

        add_tag_dialg_ok.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) MainActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                        InputMethodManager.HIDE_NOT_ALWAYS);

                AddTag(actv.getText().toString(), true);
                dlg.dismiss();
            }
        });
    }

    /**
     * 添加标签,先要执行数据库操作
     * 
     * @param tag
     * @param i
     */
    @SuppressLint("NewApi")
    public void AddTag(String tag, boolean needInsertFlag)
    {
        if (tag_names.contains(tag))
        {
            return;
        }
        if (BasicStringUtil.isNullString(tag))
        {
            return;
        }
        if (needInsertFlag)
        {
            try
            {
                if (TagDao.insertTag(tag))
                {
                    ToastUtil.showShortToast(getApplicationContext(),
                            "插入新标签成功!");
                }
                else
                {
                    ToastUtil.showShortToast(getApplicationContext(),
                            "插入新标签失败!");
                    return;
                }
            }
            catch (Exception ex)
            {
                String err = ex.getMessage();
                if (err.contains("column name is not unique"))
                {
                    ToastUtil.showLongToast(this, "<" + tag + ">该标签已存在!");
                }
                else
                {
                    ToastUtil.showLongToast(this, ex.getMessage());
                }
                return;
            }
        }
        tag_names.add(tag);

        final TextView mTag = new TextView(MainActivity.this);
        mTag.setText(tag);
        // mTag.setPadding(0, 15, 40, 15);
        mTag.setGravity(Gravity.CENTER);
        mTag.setTextSize(14);
        mTag.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.mylable));
        // mTag.setBackgroundColor(getResources().getColor(R.color.black));
        mTag.setTextColor(this.TAG_NOTSEL_COLOR);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, 80);
        params.setMargins(0, 10, 20, 10);
        this.mTagList.add(mTag);
        this.tag_vessel.addView(mTag, this.mTagList.size(), params);

        mTag.setOnClickListener(new OnClickListener()
        {
            // 利用点击事件在选中/不选中直接切换
            @Override
            public void onClick(View arg0)
            {
                if (mTag.getCurrentTextColor() == TAG_SEL_COLOR)
                {
                    mTag.setTextColor(TAG_NOTSEL_COLOR);
                    selTagList.remove(mTag.getText().toString());
                    // To do disselect
                }
                else
                {
                    mTag.setTextColor(TAG_SEL_COLOR);
                    selTagList.add(mTag.getText().toString());
                    // to do select
                }
            }

        });
        mTag.setOnLongClickListener(new OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {
                // 长按标签删除操作
                final AlertDialog dlg = new AlertDialog.Builder(
                        MainActivity.this).create();
                dlg.show();
                dlg.getWindow().setGravity(Gravity.CENTER);
                dlg.getWindow().setLayout(
                        (int) (MyAppParams.getScreenWidth() * 0.8),
                        android.view.WindowManager.LayoutParams.WRAP_CONTENT);
                dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
                TextView add_tag_dialg_title = (TextView) dlg
                        .findViewById(R.id.add_tag_dialg_title);
                EditText add_tag_dialg_content = (EditText) dlg
                        .findViewById(R.id.autocomplete_tag);
                TextView add_tag_dialg_no = (TextView) dlg
                        .findViewById(R.id.add_tag_dialg_no);
                TextView add_tag_dialg_ok = (TextView) dlg
                        .findViewById(R.id.add_tag_dialg_ok);
                add_tag_dialg_title.setText("标签移除/删除确认");
                add_tag_dialg_content.setText("您确定要移除或删除“"
                        + mTag.getText().toString() + "”这个标签吗？");
                add_tag_dialg_no.setText("移除");
                add_tag_dialg_ok.setText("删除");
                add_tag_dialg_no.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        for (int j = 0; j < mTagList.size(); j++)
                        {
                            if (mTag == mTagList.get(j))
                            {
                                tag_vessel.removeView(mTag);
                                mTagList.remove(j);
                                tag_names.remove(mTag.getText().toString()
                                        .trim());
                                ToastUtil.showShortToast(
                                        getApplicationContext(), "移除成功!");
                            }
                        }
                        dlg.dismiss();
                    }
                });
                add_tag_dialg_ok.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        for (int j = 0; j < mTagList.size(); j++)
                        {
                            if (mTag == mTagList.get(j))
                            {
                                try
                                {
                                    if (TagDao.deleteByName(mTag.getText()
                                            .toString().trim()))
                                    {
                                        tag_vessel.removeView(mTag);
                                        mTagList.remove(j);
                                        tag_names.remove(mTag.getText()
                                                .toString().trim());
                                        ToastUtil.showShortToast(
                                                getApplicationContext(),
                                                "删除成功!");
                                    }
                                    else
                                    {
                                        ToastUtil.showShortToast(
                                                getApplicationContext(),
                                                "删除失败!");
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ToastUtil.showShortToast(
                                            getApplicationContext(),
                                            ex.getMessage());
                                }
                            }
                        }
                        dlg.dismiss();
                    }
                });

                return true;
            }
        });
    }

    Logger logger = Logger.getLogger(MainActivity.class);

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        logger.error("uncaughtException   ", ex);
        ToastUtil.showLongToast(this, "系统发生异常!");
    }

    @Override
    public void onClick(final View arg0)
    {
        switch (arg0.getId())
        {
        case R.id.bt_save:
            // checkNeedSave();
            List<String> fs_files = new ArrayList<String>();
            String fs_desc = memoET.getText().toString();
            String fs_day = ((Button) findViewById(R.id.bt_date_sel)).getText()
                    .toString();
            for (ImageItem item : Bimp.getTempSelectBitmap())
            {
                // copy这些文件到临时目录
                final String backupFile = backupFile(item.getImagePath());
                fs_files.add(backupFile);
            }
            logger.info(fs_files);
            boolean result = false;

            if (isInsertMode())
            {
                result = new FsService().save(fs_day, fs_desc, fs_files,
                        tag_names);
            }
            else
            {
                result = new FsService().update(Bimp.fs_id, fs_day, fs_desc,
                        fs_files, tag_names);
            }
            if (result)
            {
                setNeedSave(false);
                Bimp.day = fs_day;
                Bimp.memo = fs_desc;
                Bimp.tags = tag_names.toString();
                Bimp.setImageChanged(false);
            }
            ToastUtil.showShortToast(this, result ? "保存成功" : "保存失败");
            break;
        case R.id.bt_back:
            quit();
            finish();
            // new FsService().findAll();
            break;
        case R.id.bt_date_sel:
            CalenderPopupWindow calenderPopupWindow = new CalenderPopupWindow(
                    MainActivity.this, this.calendarBt.getText().toString(),
                    new OnCalendarCompleteListener()
                    {

                        @Override
                        public void onCalendarComplete(String dateStr)
                        {
                            calendarBt.setText(dateStr);
                        }
                    });
            calenderPopupWindow.showAtLocation(arg0, Gravity.BOTTOM, 0, 0);
            break;
        default:
            break;
        }
    }

    private String backupFile(String memoFilePath)
    {
        String saveDir = MyAppParams.getInstance().getMediaPath();
        String suffixType = "." + BasicFileUtil.getFileType(memoFilePath);
        if (memoFilePath.contains(saveDir))
        {
            return memoFilePath;
        }

        System.out.println("备注文件:" + memoFilePath);
        String fileName = BasicFileUtil.getFileName(memoFilePath);
        fileName = BasicDateUtil.getCurrentDateTime() + suffixType;
        String destPicPath = saveDir + fileName;
        boolean transImageToMaxSize = ImageCompressUtil.transImageToMaxSize(
                memoFilePath, destPicPath, 1024, 1024, 60);
        if (transImageToMaxSize)
        {
            return destPicPath;
        }
        else
        {
            ToastUtil.showLongToast(this, "拷贝过程中出错!destPath: " + destPicPath);
        }
        return "";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            checkNeedSave();
            if (!Bimp.need_save && !Bimp.isImageChanged())
            {
                quit();
            }
            else
            {
                ToastUtil.showShortToast(getApplicationContext(),
                        "有数据已经修改,请先保存!");
            }
        }
        return true;
    }

    private void checkNeedSave()
    {
        setNeedSave(false);
        if (!calendarBt.getText().toString().equals(Bimp.day))
        {
            System.out.println("ns dayET");
            setNeedSave(true);
        }
        if (!memoET.getText().toString().equals(Bimp.memo))
        {
            System.out.println("ns memoET");
            setNeedSave(true);
        }
        System.out.println(tag_names.toString() + " / " + Bimp.tags);
        if (tag_names.toString().replace(" ", "").length() != Bimp.tags
                .replace(" ", "").length())
        {
            System.out.println("长度不一致..");
            setNeedSave(true);
        }
        else
        {
            for (String t : tag_names)
            {
                if (!Bimp.tags.contains(t.trim()))
                {
                    System.out.println("不包含.." + t);
                    setNeedSave(true);
                }
            }
        }
    }

    private void quit()
    {
        Bimp.clearAllBitmap();

        for (int i = 0; i < PublicWay.activityList.size(); i++)
        {
            if (null != PublicWay.activityList.get(i))
            {
                PublicWay.activityList.get(i).finish();
            }
        }
    }

}
