package com.wnc.xinxin.ui;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
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
    ImageView imgView;
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
        imgView = (ImageView) findViewById(R.id.imgview_add_fs);
        findViewById(R.id.bt_test).setOnClickListener(this);
        findViewById(R.id.pick_photo_btn).setOnClickListener(this);
        findViewById(R.id.imgview_add_fs).setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0)
    {
        switch (arg0.getId())
        {
        case R.id.bt_test:
            new TestPro().testDb();
            break;
        case R.id.pick_photo_btn:
            showPicMenu();
            break;
        case R.id.imgview_add_fs:
            showPicMenu();
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
                insertFileToMemo(imgName);
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
                insertFileToMemo(new File(scaledImgPath).getName());
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
                insertFileToMemo(BasicFileUtil.getFileName(moviePath));
            }
        }
    }

    private void insertFileToMemo(String imgPath)
    {
        if (BasicStringUtil.isNullString(imgPath))
        {
            return;
        }

        imgView.setImageDrawable(Drawable.createFromPath(mediaDir + imgPath));
        imgPath = " [" + imgPath + "] ";
        try
        {
            // 获得光标的位置
            int index = this.memoET.getSelectionStart();
            // 将字符串转换为StringBuffer
            StringBuffer sb = new StringBuffer(this.memoET.getText().toString());
            // 将字符插入光标所在的位置
            sb = sb.insert(index, imgPath);
            this.memoET.setText(sb.toString());
            // 设置光标的位置保持不变
            Selection.setSelection(
                    this.memoET.getText(),
                    Math.min(index + imgPath.length(),
                            sb.length() + imgPath.length()));
        }
        catch (Exception ex)
        {
            ToastUtil.showShortToast(this, "下标操作错误! " + ex.getMessage());
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
            insertFileToMemo(new File(destPicPath).getName());
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        logger.error("uncaughtException   ", ex);
    }
}
