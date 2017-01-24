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
    private String mediaName;
    private String mediaFullpath;
    private String mediaType;
    private int sn;
    private long mediaSize;
    private String createTime;
    private int isDeleted;

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
        return mediaName;
    }

    public void setMedia_name(String media_name)
    {
        this.mediaName = media_name;
    }

    public String getMedia_fullpath()
    {
        return mediaFullpath;
    }

    public void setMedia_fullpath(String media_fullpath)
    {
        this.mediaFullpath = media_fullpath;
    }

    public String getMedia_type()
    {
        return mediaType;
    }

    public void setMedia_type(String media_type)
    {
        this.mediaType = media_type;
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
        return mediaSize;
    }

    public void setMedia_size(long media_size)
    {
        this.mediaSize = media_size;
    }

    public String getCreate_time()
    {
        return createTime;
    }

    public void setCreate_time(String create_time)
    {
        this.createTime = create_time;
    }

    public int getIs_deleted()
    {
        return isDeleted;
    }

    public void setIs_deleted(int is_deleted)
    {
        this.isDeleted = is_deleted;
    }

    @Override
    public String toString()
    {
        return "{id:\"" + id + "\", mediaName:\"" + mediaName
                + "\", mediaFullpath:\"" + mediaFullpath + "\", mediaType:\""
                + mediaType + "\", sn:\"" + sn + "\", mediaSize:\"" + mediaSize
                + "\", createTime:\"" + createTime + "\", isDeleted:\""
                + isDeleted + "\"}";
    }

}
