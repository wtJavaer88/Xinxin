package common.uihelper;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;

import com.wnc.basic.BasicFileUtil;

public class MyAppParams
{
    private String packageName;
    private Resources resources;
    private String appPath;

    private final static String workPath = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/app/xinxin/";

    public final static String NEWS_DB = workPath + "xinxin.db";
    public final static String LOG_FOLDER = workPath + "/logs/";

    public static Activity mainActivity;

    private String backupDbPath;
    private String zipPath;
    private String mediaPath;

    private static int screenWidth;
    private static int screenHeight;

    private static MyAppParams singletonMyAppParams = new MyAppParams();

    private MyAppParams()
    {
        this.backupDbPath = workPath + "backupdb/";
        this.zipPath = workPath + "zip/";
        this.mediaPath = workPath + "media/";

        BasicFileUtil.makeDirectory(this.backupDbPath);
        BasicFileUtil.makeDirectory(LOG_FOLDER);
        BasicFileUtil.makeDirectory(this.zipPath);
        BasicFileUtil.makeDirectory(this.mediaPath);
    }

    public String getMediaPath()
    {
        return this.mediaPath;
    }

    public static MyAppParams getInstance()
    {
        return singletonMyAppParams;
    }

    public String getZipPath()
    {
        return this.zipPath;
    }

    public String getBackupDbPath()
    {
        return this.backupDbPath;
    }

    public static int getScreenWidth()
    {
        return screenWidth;
    }

    public static void setScreenWidth(int screenWidth)
    {
        MyAppParams.screenWidth = screenWidth;
    }

    public static int getScreenHeight()
    {
        return screenHeight;
    }

    public static void setScreenHeight(int screenHeight)
    {
        MyAppParams.screenHeight = screenHeight;
    }

    public void setPackageName(String name)
    {
        if (name == null)
        {
            return;
        }
        if (this.packageName == null)
        {
            this.packageName = name;
        }
    }

    public String getPackageName()
    {
        return this.packageName;
    }

    public void setAppPath(String path)
    {
        if (path == null)
        {
            return;
        }
        if (this.appPath == null)
        {
            this.appPath = path;
        }
    }

    public void setResources(Resources res)
    {
        if (res == null)
        {
            return;
        }
        if (this.resources == null)
        {
            this.resources = res;
        }
    }

    public Resources getResources()
    {
        return this.resources;
    }

    public String getWorkPath()
    {
        return this.workPath;
    }

    public String getAppPath()
    {
        return this.appPath;
    }

}
