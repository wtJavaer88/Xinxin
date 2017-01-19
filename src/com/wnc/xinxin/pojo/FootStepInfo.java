package com.wnc.xinxin.pojo;

import java.io.Serializable;
import java.util.List;

public class FootStepInfo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -3274963893488139409L;
    private int id;
    private String desc;
    private boolean is_deleted;
    private String tag_names;
    private String day;
    private String create_time;
    private String update_time;
    private List<FsMedia> medias;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getCreate_time()
    {
        return create_time;
    }

    public void setCreate_time(String create_time)
    {
        this.create_time = create_time;
    }

    public String getUpdate_time()
    {
        return update_time;
    }

    public void setUpdate_time(String update_time)
    {
        this.update_time = update_time;
    }

    @Override
    public String toString()
    {
        return "FootStepInfo [id=" + id + ", desc=" + desc + ", tag_names="
                + tag_names + ", create_time=" + create_time + ", update_time="
                + update_time + ", medias=" + medias + "]";
    }

    public List<FsMedia> getMedias()
    {
        return medias;
    }

    public void setMedias(List<FsMedia> medias)
    {
        this.medias = medias;
    }

    public String getTag_names()
    {
        return tag_names;
    }

    public void setTag_names(String tag_names)
    {
        this.tag_names = tag_names;
    }

    public boolean isIs_deleted()
    {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted)
    {
        this.is_deleted = is_deleted;
    }

    public String getDay()
    {
        return day;
    }

    public void setDay(String day)
    {
        this.day = day;
    }
}
