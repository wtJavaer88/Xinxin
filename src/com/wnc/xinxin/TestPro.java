package com.wnc.xinxin;

import java.util.ArrayList;
import java.util.List;

import com.wnc.basic.BasicDateUtil;
import com.wnc.xinxin.dao.FsDao;
import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;

public class TestPro
{
	public void testDb()
	{
		FootStepInfo fsInfo = new FootStepInfo();
		fsInfo.setDesc("测试第一条记录");
		fsInfo.setCreate_time(BasicDateUtil.getCurrentDateTimeString());
		fsInfo.setUpdate_time(BasicDateUtil.getCurrentDateTimeString());
		FsDao.insertFs(fsInfo);

		List<FootStepInfo> findAllFsBySql = FsDao.findAllFsBySql("SELECT * FROM FOOTSTEPS");
		for (FootStepInfo footStepInfo : findAllFsBySql)
		{
			System.out.println(footStepInfo);
		}
	}

	public void testDb2()
	{
		FootStepInfo fsInfo = new FootStepInfo();
		fsInfo.setDesc("测试第2条记录");
		fsInfo.setCreate_time(BasicDateUtil.getCurrentDateTimeString());
		fsInfo.setUpdate_time(BasicDateUtil.getCurrentDateTimeString());

		List<FsMedia> medias = new ArrayList<>();
		for (int i = 0; i < 5; i++)
		{
			FsMedia media = new FsMedia();
			media.setMedia_name("media" + (i + 1));
			media.setMedia_size(100 * i);
			media.setMediapath_id(Config.MEDIAPATH_ID);
			media.setMedia_type("jpg");
			medias.add(media);
		}
		FsDao.insertComplicateFs(fsInfo, medias, null);

		List<FootStepInfo> findAllFsBySql = FsDao.findAllFsBySql("SELECT * FROM FOOTSTEPS");
		for (FootStepInfo footStepInfo : findAllFsBySql)
		{
			System.out.println(footStepInfo);
		}
	}
}
