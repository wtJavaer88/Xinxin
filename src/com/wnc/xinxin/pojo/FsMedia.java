package com.wnc.xinxin.pojo;

import java.io.Serializable;

public class FsMedia implements Serializable
{
    /**
     * 
     */
    private static final Long serialVersionUID = 8001226207947247024L;
    private Integer id;
    private String fs_uuid;
    private String mediaName;
    private String mediaFullpath;
    private String mediaType;
    private Integer sn;
    private Long mediaSize;
    private String createTime;
    private Integer isDeleted;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
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

    public Integer getSn()
    {
        return sn;
    }

    public void setSn(Integer sn)
    {
        this.sn = sn;
    }

    public Long getMedia_size()
    {
        return mediaSize;
    }

    public void setMedia_size(Long media_size)
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

    public Integer getIs_deleted()
    {
        return isDeleted;
    }

    public void setIs_deleted(Integer is_deleted)
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
