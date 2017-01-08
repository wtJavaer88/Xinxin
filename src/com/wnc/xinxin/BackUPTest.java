package com.wnc.xinxin;

import java.io.File;
import java.util.Collection;

import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.tools.ZipUtils;
import common.uihelper.MyAppParams;
import common.utils.MailUtil;

public class BackUPTest
{

    public boolean testOne()
    {
        String destZip = MyAppParams.getInstance().getWorkPath() + "test/1.zip";
        try
        {
            MailUtil.sendQQMail("时光机备份" + BasicDateUtil.getCurrentDateString(),
                    "备份文件数目:", destZip);
            Thread.sleep(2000);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean testTwo()
    {
        String destZip = MyAppParams.getInstance().getWorkPath() + "test/2.zip";
        System.out.println("备份文件大小:" + BasicFileUtil.getFileSize(destZip));
        try
        {
            MailUtil.sendQQMail("时光机备份" + BasicDateUtil.getCurrentDateString(),
                    "备份文件数目:", destZip);
            Thread.sleep(2000);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean zipAndSendEmailBackGround(Collection<File> list,
            String destZip)
    {
        if (list != null && list.size() > 0)
        {
            try
            {
                ZipUtils.zipFiles(list, new File(destZip));

                MailUtil.sendQQMail(
                        "时光机备份" + BasicDateUtil.getCurrentDateString(),
                        "备份文件数目:" + list.size(), destZip);
            }
            catch (Exception e)
            {
                Log.e("backup", e.getMessage());
                return false;
            }
        }
        else
        {
            Log.e("backup", "没有找到备份数据!");
        }
        return true;
    }
}
