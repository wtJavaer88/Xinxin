package common.app;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import common.db.DatabaseManager;
import common.db.SQLiteHelperOfOpen;
import common.uihelper.MyAppParams;

public class SysInit
{

    public static void init(Activity context)
    {
        Log4jUtil.configLog(MyAppParams.LOG_FOLDER
                + BasicDateUtil.getCurrentDateString() + ".txt");
        SharedPreferenceUtil.init(context);
        MyAppParams.mainActivity = context;
        MyAppParams.getInstance().setPackageName(context.getPackageName());
        MyAppParams.getInstance().setResources(context.getResources());
        MyAppParams.getInstance().setAppPath(context.getFilesDir().getParent());
        MyAppParams.setScreenWidth(BasicPhoneUtil.getScreenWidth(context));
        MyAppParams.setScreenHeight(BasicPhoneUtil.getScreenHeight(context));

        if (isFirstRun())
        {
            if (BasicFileUtil.isExistFile(MyAppParams.XINXIN_DB))
            {
                BasicFileUtil.CopyFile(
                        MyAppParams.XINXIN_DB,
                        MyAppParams.getInstance().getBackupDbPath()
                                + System.currentTimeMillis() + ".db");
                BasicFileUtil.deleteFile(MyAppParams.XINXIN_DB);
            }

        }
        if (!BasicFileUtil.isExistFile(MyAppParams.XINXIN_DB))
        {
            try
            {
                BasicFileUtil.writeFileByte(
                        MyAppParams.XINXIN_DB,
                        IOUtils.toByteArray(context.getAssets().open(
                                "xinxin.db")));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        SQLiteOpenHelper myHelper = new SQLiteHelperOfOpen(context,
                MyAppParams.XINXIN_DB, null, 1);
        DatabaseManager.initializeInstance(myHelper);

    }

    static String FIRST_RUN = "isXinxinFirstRun";

    private static boolean isFirstRun()
    {
        boolean isFirstRun = SharedPreferenceUtil.getShareDataByKey(FIRST_RUN,
                true);
        if (isFirstRun)
        {
            Log.i("Sysinit", "第一次运行");
            SharedPreferenceUtil.changeValue(FIRST_RUN, false);
            return true;
        }
        else
        {
            Log.i("Sysinit", "不是第一次运行");
        }
        return false;
    }

}
