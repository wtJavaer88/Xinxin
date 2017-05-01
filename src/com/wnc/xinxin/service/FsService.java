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

	public boolean deleteFs(String uuid)
	{
		return FsDao.deleteFs(uuid);
	}

	public boolean existFs(String uuid)
	{
		return FsDao.existFs(uuid);
	}

	public boolean existFs(String uuid, String update_tiem)
	{
		return FsDao.existFs(uuid, update_tiem);
	}

	public boolean save(String day, String fs_desc, List<String> media_files,
			Set<String> tags)
	{
		FootStepInfo fsInfo = new FootStepInfo();
		fsInfo.setFsDesc(fs_desc);
		fsInfo.setDay(day);
		fsInfo.setIsDeleted(0);
		fsInfo.setUuid(UUIDUtil.getUUID());
		fsInfo.setCreateBy(Config.LOGIN_ID);
		fsInfo.setDeviceId(Config.DEVICE_ID);
		fsInfo.setCreateTime(BasicDateUtil.getCurrentDateTimeString());
		fsInfo.setUpdateTime(BasicDateUtil.getCurrentDateTimeString());
		List<FsMedia> medias = getFsMedias(media_files);
		fsInfo.setMedias(medias);
		return FsDao.insertComplicateFs(fsInfo, tags);
	}

	public boolean save(FootStepInfo fsInfo)
	{
		return FsDao.insertComplicateFs(fsInfo, fsInfo.getTagNames());
	}

	private List<FsMedia> getFsMedias(List<String> media_files)
	{
		List<FsMedia> medias = new ArrayList<FsMedia>();
		for (String mFileStr : media_files)
		{
			if (FileUtils.isThumbPic(mFileStr))
			{
				mFileStr = mFileStr.replace(".jpg", "");
			}
			FsMedia media = new FsMedia();
			media.setMediaName(BasicFileUtil.getFileName(mFileStr));
			media.setMediaSize(BasicFileUtil.getFileSize(mFileStr));
			media.setMediaType(BasicFileUtil.getFileType(mFileStr));
			media.setMediaFullpath(mFileStr);
			media.setCreateTime(BasicDateUtil.getDateTimeFromLongTime(new File(
					mFileStr).lastModified()));
			medias.add(media);
		}
		return medias;
	}

	public List<FootStepInfo> findAll()
	{
		List<FootStepInfo> findAllFsBySql = FsDao
				.findAllFsBySql("SELECT * FROM FOOTSTEPS where is_deleted=0 order by id asc");
		for (FootStepInfo footStepInfo : findAllFsBySql)
		{
			List<FsMedia> medias = footStepInfo.getMedias();
			for (FsMedia fsMedia : medias)
			{
				final String absulute_path = fsMedia.getMediaFullpath();
				if (FileTypeUtil.isVideoFile(absulute_path)
						|| FileTypeUtil.isVoiceFile(absulute_path))
				{
					System.out.println("需要添加jpg后缀");
					fsMedia.setMediaFullpath(fsMedia.getMediaFullpath()
							+ ".jpg");
				}
			}
		}
		// for (FootStepInfo footStepInfo : findAllFsBySql)
		// {
		// System.out.println(footStepInfo);
		// }
		return findAllFsBySql;
	}

	public List<FootStepInfo> findAllNeedUpload(String last_upload_time)
	{
		List<FootStepInfo> findAllFsBySql = FsDao
				.findAllFsBySql("SELECT * FROM FOOTSTEPS where is_deleted=0 and update_time > '"
						+ last_upload_time
						+ "' and create_by='"
						+ Config.LOGIN_ID
						+ "' and device_id='"
						+ Config.DEVICE_ID + "' order by id asc");
		for (FootStepInfo footStepInfo : findAllFsBySql)
		{
			List<FsMedia> medias = footStepInfo.getMedias();
			for (FsMedia fsMedia : medias)
			{
				final String absulute_path = fsMedia.getMediaFullpath();
				if (FileTypeUtil.isVideoFile(absulute_path)
						|| FileTypeUtil.isVoiceFile(absulute_path))
				{
					System.out.println("需要添加jpg后缀");
					fsMedia.setMediaFullpath(fsMedia.getMediaFullpath()
							+ ".jpg");
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

	public boolean update(FootStepInfo fsInfo)
	{
		return FsDao.updateComplicateFs(fsInfo);

	}

	public boolean update(String uuid, String day, String fs_desc,
			List<String> media_files, Set<String> tags)
	{
		FootStepInfo fsInfo = new FootStepInfo();
		fsInfo.setUuid(uuid);
		fsInfo.setDay(day);
		fsInfo.setFsDesc(fs_desc);
		fsInfo.setUpdateBy(Config.LOGIN_ID);
		fsInfo.setDeviceId(Config.DEVICE_ID);
		fsInfo.setUpdateTime(BasicDateUtil.getCurrentDateTimeString());

		List<FsMedia> medias = getFsMedias(media_files);
		fsInfo.setMedias(medias);
		fsInfo.setTagNames(tags.toString());
		return FsDao.updateComplicateFs(fsInfo);
	}

}
