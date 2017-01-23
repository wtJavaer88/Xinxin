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
    private String uuid;
    private String fs_desc;
    private int is_deleted;
    private String tag_names;
    private String day;
    private String create_time;
    private String update_time;
    private String update_by;
    private String create_by;
    private List<FsMedia> medias;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFs_desc()
    {
        return fs_desc;
    }

    public void setFs_desc(String desc)
    {
        this.fs_desc = desc;
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

    public String getDay()
    {
        return day;
    }

    public void setDay(String day)
    {
        this.day = day;
    }

    public String getUpdate_by()
    {
        return update_by;
    }

    public void setUpdate_by(String update_by)
    {
        this.update_by = update_by;
    }

    public String getCreate_by()
    {
        return create_by;
    }

    public void setCreate_by(String create_by)
    {
        this.create_by = create_by;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public int getIs_deleted()
    {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted)
    {
        this.is_deleted = is_deleted;
    }

    @Override
    public String toString()
    {
        return "FootStepInfo [id=" + id + ", uuid=" + uuid + ", fs_desc="
                + fs_desc + ", is_deleted=" + is_deleted + ", tag_names="
                + tag_names + ", day=" + day + ", create_time=" + create_time
                + ", update_time=" + update_time + ", update_by=" + update_by
                + ", create_by=" + create_by + ", medias=" + medias + "]";
    }

}
