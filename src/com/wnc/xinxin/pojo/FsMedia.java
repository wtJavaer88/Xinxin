package com.wnc.xinxin.pojo;

import java.io.Serializable;

public class FsMedia implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 8001226207947247024L;
    private int id;
    private String fs_uuid;
    private String media_name;
    private String media_fullpath;
    private String media_type;
    private int sn;
    private long media_size;
    private String create_time;
    private int is_deleted;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFs_uuid()
    {
        return fs_uuid;
    }

    public void setFs_uuid(String fs_uuid)
    {
        this.fs_uuid = fs_uuid;
    }

    public String getMedia_name()
    {
        return media_name;
    }

    public void setMedia_name(String media_name)
    {
        this.media_name = media_name;
    }

    public String getMedia_fullpath()
    {
        return media_fullpath;
    }

    public void setMedia_fullpath(String media_fullpath)
    {
        this.media_fullpath = media_fullpath;
    }

    public String getMedia_type()
    {
        return media_type;
    }

    public void setMedia_type(String media_type)
    {
        this.media_type = media_type;
    }

    public int getSn()
    {
        return sn;
    }

    public void setSn(int sn)
    {
        this.sn = sn;
    }

    public long getMedia_size()
    {
        return media_size;
    }

    public void setMedia_size(long media_size)
    {
        this.media_size = media_size;
    }

    public String getCreate_time()
    {
        return create_time;
    }

    public void setCreate_time(String create_time)
    {
        this.create_time = create_time;
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
        return "FsMedia [id=" + id + ", media_name=" + media_name
                + ", media_fullpath=" + media_fullpath + ", media_type="
                + media_type + ", sn=" + sn + ", media_size=" + media_size
                + ", create_time=" + create_time + ", is_deleted=" + is_deleted
                + "]";
    }

}
