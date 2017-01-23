package com.wnc.xinxin;

import java.io.File;
import java.util.Collection;

import org.apache.http.Header;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.tools.ZipUtils;
import common.uihelper.MyAppParams;
import common.utils.MailUtil;
import common.utils.UrlPicDownloader;

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

    boolean uploadFlag = false;
    boolean downloadFlag = false;

    public boolean testThree()
    {
        try
        {
            String urlpath = "http://139.199.183.98:8080/superword/rest/file/mbupload";
            final long stime = System.currentTimeMillis();
            RequestParams params = new RequestParams();
            urlpath = "http://192.168.56.1:8081/rest/file/mbupload";
            String testFile = "test/upload_data.zip";
            // testFile = "test/voa.db";
            File file = new File(MyAppParams.getInstance().getWorkPath()
                    + testFile);
            System.out.println("备份文件大小:"
                    + BasicFileUtil.getFileSize(file.getAbsolutePath()));

            SyncHttpClient client = new SyncHttpClient();
            params.put("userName", "wnc");
            params.put("file", file);
            client.post(urlpath, params, new AsyncHttpResponseHandler()
            {

                @Override
                public void onProgress(int bytesWritten, int totalSize)
                {
                    super.onProgress(bytesWritten, totalSize);
                    int rate = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                    System.out.println("已传输百分比: " + rate);
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                        Throwable arg3)
                {

                }

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
                {
                    final String resp = new String(arg2);
                    if (resp.equals("1"))
                    {
                        uploadFlag = true;
                        System.out.println("耗时:"
                                + (System.currentTimeMillis() - stime) / 1000);
                    }
                }
            });
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uploadFlag;
    }

    public boolean testFour()
    {
        try
        {
            // final String domain = "http://139.199.183.98:8080/superword";
            final String domain = "http://192.168.56.1:8081";
            String urlpath = domain + "/rest/file/download";
            final long stime = System.currentTimeMillis();
            SyncHttpClient client = new SyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("day", "2017-01-20");
            params.add("count", "5");
            client.post(urlpath, params, new AsyncHttpResponseHandler()
            {
                @Override
                public void onFailure(int statusCode, Header[] headers,
                        byte[] errorResponse, Throwable e)
                {
                    Log.e("download", "download获取数据异常 ", e);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                        byte[] response)
                {
                    final String resp = new String(response);
                    final String string = domain + resp;
                    System.out.println("download成功显示:" + string);
                    System.out.println("耗时:"
                            + (System.currentTimeMillis() - stime) / 1000);
                    try
                    {
                        UrlPicDownloader.download(string, MyAppParams
                                .getInstance().getWorkPath()
                                + "/test/"
                                + BasicFileUtil.getFileName(string));
                        downloadFlag = true;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return downloadFlag;
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
