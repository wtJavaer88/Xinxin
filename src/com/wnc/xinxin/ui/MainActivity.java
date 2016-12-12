package com.wnc.xinxin.ui;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.xinxin.FlowLayout;
import com.wnc.xinxin.R;
import com.wnc.xinxin.TestPro;
import common.app.SysInit;
import common.app.ToastUtil;
import common.app.UriUtil;
import common.uihelper.MyAppParams;
import common.utils.ImageCompressUtil;

public class MainActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{
    EditText memoET;
    ImageView latest_imgView;
    AbsoluteLayout picZoneLayout;
    FlowLayout tag_vessel;
    ImageView add_tag;
    List<TextView> mTagList = new ArrayList<TextView>();
    List<String> selTagList = new ArrayList<String>();// 保存已选中的标签
    private final int TAG_SEL_COLOR = Color.RED;
    private final int TAG_NOTSEL_COLOR = Color.GRAY;

    Logger logger = Logger.getLogger(MainActivity.class);
    private final String[] picMenu = new String[]
    { "相机", "相册", "录音", "录像" };

    private File mPhotoFile;
    private final int CAMERA_RESULT = 100;
    private final int LOAD_IMAGE_RESULT = 200;
    private final int VOICE_RESULT = 300;
    private final int VIDEO_RESULT = 400;
    private String mediaDir = MyAppParams.getInstance().getMediaPath();

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
        memoET = (EditText) findViewById(R.id.et_memo);
        latest_imgView = (ImageView) findViewById(R.id.imgview_add_fs);
        picZoneLayout = (AbsoluteLayout) findViewById(R.id.ll_piczone);
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
        findViewById(R.id.bt_test).setOnClickListener(this);
        findViewById(R.id.imgview_add_fs).setOnClickListener(
                new PicAddClickListener());
    }

    @Override
    public void onClick(View arg0)
    {
        switch (arg0.getId())
        {
        case R.id.bt_test:
            new TestPro().testDb();
            break;
        default:
            break;
        }
    }

    private void addfs()
    {

    }

    private void showPicMenu()
    {
        logger.info("添加记录");
        AlertDialog.Builder builder = new Builder(this);
        builder.setItems(this.picMenu, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                if (arg1 == 0)
                {
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED))
                    {
                        mPhotoFile = new File(mediaDir, BasicDateUtil
                                .getCurrentDateTimeString() + ".jpg");
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
                                Toast.makeText(getApplication(), "照片创建失败!",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        Intent intent = new Intent(
                                "android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(mPhotoFile));
                        startActivityForResult(intent, CAMERA_RESULT);
                    }
                    else
                    {
                        Toast.makeText(getApplication(), "sdcard无效或没有插入!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else if (arg1 == 1)
                {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, LOAD_IMAGE_RESULT);
                }
                else if (arg1 == 2)
                {
                    Intent intent = new Intent(
                            MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, VOICE_RESULT);
                }
                else if (arg1 == 3)
                {
                    Intent intent = new Intent(MainActivity.this,
                            MovieActivity.class);
                    startActivityForResult(intent, VIDEO_RESULT);
                }
                arg0.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.CAMERA_RESULT && resultCode == RESULT_OK)
        {
            System.out.println("camera OK!");
            if (this.mPhotoFile != null && this.mPhotoFile.exists())
            {
                System.out.println("mPhotoFile::" + this.mPhotoFile);

                String scaledImgPath = (MyAppParams.getInstance()
                        .getMediaPath() + System.currentTimeMillis() + "_s")
                        + ".jpg";
                String imgName = mPhotoFile.getName();
                if (ImageCompressUtil.transImageToMaxSize(
                        mPhotoFile.getAbsolutePath(), scaledImgPath, 1024,
                        1024, 60))
                {
                    System.out.println("压缩成功! " + scaledImgPath);
                    imgName = new File(scaledImgPath).getName();
                }
                changePicZone(imgName);
            }
            else
            {
                System.out.println("img Err!");
            }
        }
        if (requestCode == this.LOAD_IMAGE_RESULT && resultCode == RESULT_OK
                && null != data)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn =
            { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            System.out.println("picturePath::" + picturePath);
            String scaledImgPath = (MyAppParams.getInstance().getMediaPath()
                    + System.currentTimeMillis() + "_s")
                    + ".jpg";
            if (ImageCompressUtil.transImageToMaxSize(picturePath,
                    scaledImgPath, 1024, 1024, 60))
            {
                // 压缩成功就代表已经备份了
                System.out.println("压缩成功! " + scaledImgPath);
                changePicZone(new File(scaledImgPath).getName());
            }
            else
            {
                // 压缩不成功不影响原来的逻辑
                backupAndInsertMemo(this.mediaDir, picturePath, ".jpg");
            }
        }

        if (requestCode == this.VOICE_RESULT && data != null)
        {
            try
            {
                File amrFile = UriUtil.getFileByUri(data.getData(), this);
                if (amrFile != null)
                {
                    System.out.println("audio file:"
                            + amrFile.getAbsolutePath());
                    backupAndInsertMemo(mediaDir, amrFile.getAbsolutePath(),
                            ".amr");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (requestCode == this.VIDEO_RESULT && data != null)
        {
            String moviePath = data.getStringExtra(MovieActivity.RECORDED_PATH);
            System.out.println("返回的moviePath:" + moviePath);
            if (BasicFileUtil.isExistFile(moviePath))
            {
                changePicZone(BasicFileUtil.getFileName(moviePath));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void changePicZone(String imgPath)
    {
        if (BasicStringUtil.isNullString(imgPath))
        {
            return;
        }
        if (imgPath.endsWith(".jpg"))
        {
            // curimgView.setBackgroundColor(Color.WHITE);
            curimgView.setImageDrawable(Drawable.createFromPath(mediaDir
                    + imgPath));
        }
        else if (imgPath.endsWith(".amr"))
        {
            curimgView.setBackgroundResource(R.drawable.icon_fs_audio);
        }
        else if (imgPath.endsWith(".3gp"))
        {
            curimgView.setBackgroundResource(R.drawable.icon_fs_video);
        }
        if (latest_imgView == curimgView)
        {
            latest_imgView = new ImageView(this);
            latest_imgView.setBackgroundResource(R.drawable.icon_fs_add);
            latest_imgView.setOnClickListener(new PicAddClickListener());
            int childCount = picZoneLayout.getChildCount();
            final int padding = 20;
            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                    300, 300, (300 + padding) * (childCount % 3),
                    (300 + padding) * (childCount / 3));
            latest_imgView.setLayoutParams(params);
            picZoneLayout.addView(latest_imgView);
        }
    }

    ImageView curimgView;

    class PicAddClickListener implements OnClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            showPicMenu();
            curimgView = (ImageView) arg0;
        }
    }

    /*
     * 将备注的文件拷贝一遍
     * 
     * @param moviePath
     */
    private void backupAndInsertMemo(String saveDir, String memoFilePath,
            String suffixType)
    {
        System.out.println("备注文件:" + memoFilePath);
        String fileName = BasicFileUtil.getFileName(memoFilePath);
        String destPicPath = saveDir + fileName;
        if (BasicFileUtil.isExistFile(destPicPath))
        {
            fileName = BasicDateUtil.getCurrentDateTime() + suffixType;
            destPicPath = saveDir + fileName;
        }
        if (!BasicFileUtil.CopyFile(memoFilePath, destPicPath))
        {
            fileName = "";
            ToastUtil.showLongToast(this, "拷贝过程中出错!destPath: " + destPicPath);
        }
        else
        {
            changePicZone(new File(destPicPath).getName());
        }
    }

    /**
     * 添加标签的对话框
     */
    public void AddTagDialog()
    {
        final Dialog dlg = new Dialog(this, R.style.dialog);
        dlg.show();
        dlg.getWindow().setGravity(Gravity.CENTER);
        dlg.getWindow().setLayout((int) (MyAppParams.getScreenWidth() * 0.8),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
        TextView add_tag_dialg_title = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_title);
        final EditText add_tag_dialg_content = (EditText) dlg
                .findViewById(R.id.add_tag_dialg_content);
        TextView add_tag_dialg_no = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_no);
        TextView add_tag_dialg_ok = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_ok);
        add_tag_dialg_title.setText("添加个人标签");
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

                AddTag(add_tag_dialg_content.getText().toString(), true);
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
    public void AddTag(String tag, boolean insertFlag)
    {
        if (insertFlag)
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
                if (mTag.getCurrentTextColor() == MainActivity.this.TAG_SEL_COLOR)
                {
                    mTag.setTextColor(MainActivity.this.TAG_NOTSEL_COLOR);
                    MainActivity.this.selTagList.remove(mTag.getText()
                            .toString());
                    // To do disselect
                }
                else
                {
                    mTag.setTextColor(MainActivity.this.TAG_SEL_COLOR);
                    MainActivity.this.selTagList.add(mTag.getText().toString());
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
                        .findViewById(R.id.add_tag_dialg_content);
                TextView add_tag_dialg_no = (TextView) dlg
                        .findViewById(R.id.add_tag_dialg_no);
                TextView add_tag_dialg_ok = (TextView) dlg
                        .findViewById(R.id.add_tag_dialg_ok);
                add_tag_dialg_title.setText("标签删除确认");
                add_tag_dialg_content.setText("您确定要删除“"
                        + mTag.getText().toString() + "”这个标签吗？");
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

                        for (int j = 0; j < MainActivity.this.mTagList.size(); j++)
                        {
                            Log.v("==", mTag.getText().toString()
                                    + "=="
                                    + MainActivity.this.mTagList.get(j)
                                            .toString());
                            if (mTag == MainActivity.this.mTagList.get(j))
                            {
                                try
                                {
                                    if (TagDao.deleteByName(mTag.getText()
                                            .toString().trim()))
                                    {
                                        MainActivity.this.tag_vessel
                                                .removeView(mTag);
                                        MainActivity.this.mTagList.remove(j);
                                        ToastUtil.showShortToast(
                                                getApplicationContext(), "成功!");
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

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        logger.error("uncaughtException   ", ex);
    }
}
