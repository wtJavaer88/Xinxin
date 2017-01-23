package com.wnc.xinxin.service;

import java.util.ArrayList;
import java.util.List;

import com.wnc.xinxin.dao.TagDao;
import com.wnc.xinxin.pojo.FsTag;

public class TagService
{
    public List<FsTag> findAllTags()
    {
        List<FsTag> findAllTags = TagDao.findAllTags();
        System.out.println(findAllTags);
        return findAllTags;
    }

    public List<String> findAllTagNames()
    {
        List<FsTag> findAllTags = TagDao.findAllTags();
        List<String> list = new ArrayList<String>();
        for (FsTag tag : findAllTags)
        {
            list.add(tag.getTag_name());
        }
        System.out.println(list);
        return list;
    }

    public void init()
    {
        // TagDao.deleteAll();
        TagDao.insertTag("快乐");
        TagDao.insertTag("睡眠");
        TagDao.insertTag("游戏");
        TagDao.insertTag("哭闹");
        TagDao.insertTag("体温");
        TagDao.insertTag("户外");
        TagDao.insertTag("进食");
    }
}
