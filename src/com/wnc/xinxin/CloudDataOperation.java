package com.wnc.xinxin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;
import common.uihelper.MyAppParams;
import common.utils.FileTypeUtil;
import common.utils.ZipUtils;

public class CloudDataOperation
{
    SyncHttpClient client = new SyncHttpClient();
    private boolean up_flag = false;

    public CloudDataOperation()
    {

    }

    public boolean upload()
    {

        try
        {
            File file = freshDataToFile();
            String urlpath = "http://139.199.183.98:8080/superword/rest/file/mbupload";
            urlpath = "http://192.168.56.1:8081/rest/file/mbupload";
            final long stime = System.currentTimeMillis();
            RequestParams params = new RequestParams();

            System.out.println("备份文件大小:"
                    + BasicFileUtil.getFileSize(file.getAbsolutePath()));

            SyncHttpClient client = new SyncHttpClient();
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
                        up_flag = true;
                        System.out.println("耗时:"
                                + (System.currentTimeMillis() - stime) / 1000);
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return up_flag;
    }

    /**
     * 返回一个zip文件,包含一个数据json文件,和多媒体文件
     * 
     * @return
     * @throws IOException
     */
    private File freshDataToFile() throws IOException
    {
        // TODO STEP 1:去服务器查上次上传时间T

        // STEP 2:将T之后的数据写入json上传
        final String dataFile = MyAppParams.getInstance().getWorkPath()
                + "data.json";
        final String zipFilePath = MyAppParams.getInstance().getWorkPath()
                + "uplaod" + System.currentTimeMillis() + ".zip";
        FsService fsService = new FsService();
        List<FootStepInfo> findAll = fsService.findAll();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", Config.LOGIN_ID);
        jsonObject.put("update_time", BasicDateUtil.getCurrentDateTimeString());
        jsonObject.put("data", findAll);

        BasicFileUtil.writeFileString(dataFile, jsonObject.toString(), null,
                false);
        List<File> resFileList = new ArrayList<File>();
        resFileList.add(new File(dataFile));

        for (FootStepInfo footStepInfo : findAll)
        {
            final List<FsMedia> medias = footStepInfo.getMedias();
            if (medias != null)
            {
                for (FsMedia media : medias)
                {
                    String media_fullpath = media.getMedia_fullpath();
                    if (FileTypeUtil.isVideoFile(media_fullpath.replace(".jpg",
                            ""))
                            || FileTypeUtil.isVoiceFile(media_fullpath.replace(
                                    ".jpg", "")))
                    {
                        media_fullpath = media_fullpath.replace(".jpg", "");
                    }
                    resFileList.add(new File(media_fullpath));
                }
            }
        }
        final File zipFile = new File(zipFilePath);
        ZipUtils.zipFiles(resFileList, zipFile);
        return zipFile;
    }
}
