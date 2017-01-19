package com.wnc.xinxin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.xinxin.dao.FsDao;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;

public class FsService
{

    public boolean save(String day, String fs_desc, List<String> media_files,
            Set<String> tags)
    {
        FootStepInfo fsInfo = new FootStepInfo();
        fsInfo.setDesc(fs_desc);
        fsInfo.setDay(day);
        fsInfo.setCreate_time(BasicDateUtil.getCurrentDateTimeString());
        fsInfo.setUpdate_time(BasicDateUtil.getCurrentDateTimeString());

        List<FsMedia> medias = getFsMedias(media_files);
        return FsDao.insertComplicateFs(fsInfo, medias, tags);
    }

    private List<FsMedia> getFsMedias(List<String> media_files)
    {
        List<FsMedia> medias = new ArrayList<FsMedia>();
        for (String mFileStr : media_files)
        {
            FsMedia media = new FsMedia();
            media.setMedia_name(BasicFileUtil.getFileName(mFileStr));
            media.setMedia_size(BasicFileUtil.getFileSize(mFileStr));
            media.setMediapath_id(Config.MEDIAPATH_ID);
            media.setMedia_type(BasicFileUtil.getFileType(mFileStr));
            media.setAbsulute_path(mFileStr);
            media.setCreate_time(BasicDateUtil
                    .getDateTimeFromLongTime(new File(mFileStr).lastModified()));
            medias.add(media);
        }
        return medias;
    }

    public List<FootStepInfo> findAll()
    {
        List<FootStepInfo> findAllFsBySql = FsDao
                .findAllFsBySql("SELECT * FROM FOOTSTEPS where is_deleted=0 order by id asc limit 0,20");
        // for (FootStepInfo footStepInfo : findAllFsBySql)
        // {
        // System.out.println(footStepInfo);
        // }
        return findAllFsBySql;
    }

    public void deleteAll()
    {
        FsDao.deleteAll();
    }

    public boolean update(int id, String day, String fs_desc,
            List<String> media_files, Set<String> tags)
    {
        FootStepInfo fsInfo = new FootStepInfo();
        fsInfo.setId(id);
        fsInfo.setDay(day);
        fsInfo.setDesc(fs_desc);
        fsInfo.setUpdate_time(BasicDateUtil.getCurrentDateTimeString());

        List<FsMedia> medias = getFsMedias(media_files);
        return FsDao.updateComplicateFs(fsInfo, medias, tags);
    }

}
