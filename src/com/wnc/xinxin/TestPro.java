package com.wnc.xinxin;

import java.util.List;

import com.wnc.basic.BasicDateUtil;
import com.wnc.xinxin.dao.FsDao;
import com.wnc.xinxin.pojo.FootStepInfo;

public class TestPro
{
    public void testDb()
    {
        FootStepInfo fsInfo = new FootStepInfo();
        fsInfo.setDesc("测试第一条记录");
        fsInfo.setCreate_time(BasicDateUtil.getCurrentDateTimeString());
        fsInfo.setUpdate_time(BasicDateUtil.getCurrentDateTimeString());
        FsDao.insertSingleFs(fsInfo);

        List<FootStepInfo> findAllFsBySql = FsDao
                .findAllFsBySql("SELECT * FROM FOOTSTEPS");
        for (FootStepInfo footStepInfo : findAllFsBySql)
        {
            System.out.println(footStepInfo);
        }
    }
}
