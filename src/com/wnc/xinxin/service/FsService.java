package com.wnc.xinxin.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.king.photo.util.FileUtils;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.tools.UUIDUtil;
import com.wnc.xinxin.Config;
import com.wnc.xinxin.dao.FsDao;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;
import common.utils.FileTypeUtil;

public class FsService
{

	public boolean save(String day, String fs_desc, List<String> media_files, Set<String> tags)
	{
		FootStepInfo fsInfo = new FootStepInfo();
		fsInfo.setFs_desc(fs_desc);
		fsInfo.setDay(day);
		fsInfo.setUuid(UUIDUtil.getUUID());
		fsInfo.setCreate_by(Config.LOGIN_ID);
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
			if (FileUtils.isVideoThumbPic(mFileStr) || FileUtils.isVoiceThumbPic(mFileStr))
			{
				mFileStr = mFileStr.replace(".jpg", "");
			}
			FsMedia media = new FsMedia();
			media.setMedia_name(BasicFileUtil.getFileName(mFileStr));
			media.setMedia_size(BasicFileUtil.getFileSize(mFileStr));
			media.setMedia_type(BasicFileUtil.getFileType(mFileStr));
			media.setMedia_fullpath(mFileStr);
			media.setCreate_time(BasicDateUtil.getDateTimeFromLongTime(new File(mFileStr).lastModified()));
			medias.add(media);
		}
		return medias;
	}

	public List<FootStepInfo> findAll()
	{
		List<FootStepInfo> findAllFsBySql = FsDao.findAllFsBySql("SELECT * FROM FOOTSTEPS where is_deleted=0 order by id asc limit 0,20");
		for (FootStepInfo footStepInfo : findAllFsBySql)
		{
			List<FsMedia> medias = footStepInfo.getMedias();
			for (FsMedia fsMedia : medias)
			{
				final String absulute_path = fsMedia.getMedia_fullpath();
				if (FileTypeUtil.isVideoFile(absulute_path) || FileTypeUtil.isVoiceFile(absulute_path))
				{
					System.out.println("需要添加jpg后缀");
					fsMedia.setMedia_fullpath(fsMedia.getMedia_fullpath() + ".jpg");
				}
			}
		}
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

	public boolean update(String uuid, String day, String fs_desc, List<String> media_files, Set<String> tags)
	{
		FootStepInfo fsInfo = new FootStepInfo();
		fsInfo.setUuid(uuid);
		fsInfo.setDay(day);
		fsInfo.setFs_desc(fs_desc);
		fsInfo.setUpdate_by(Config.LOGIN_ID);
		fsInfo.setUpdate_time(BasicDateUtil.getCurrentDateTimeString());

		List<FsMedia> medias = getFsMedias(media_files);
		return FsDao.updateComplicateFs(fsInfo, medias, tags);
	}

}