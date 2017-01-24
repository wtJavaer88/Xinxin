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
    private String fsDesc;
    private int isDeleted;
    private String tagNames;
    private String day;
    private String createTime;
    private String updateTime;
    private String updateBy;
    private String createBy;
    private String deviceId;
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
        return fsDesc;
    }

    public void setFs_desc(String desc)
    {
        this.fsDesc = desc;
    }

    public String getCreate_time()
    {
        return createTime;
    }

    public void setCreate_time(String create_time)
    {
        this.createTime = create_time;
    }

    public String getUpdate_time()
    {
        return updateTime;
    }

    public void setUpdate_time(String update_time)
    {
        this.updateTime = update_time;
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
        return tagNames;
    }

    public void setTag_names(String tag_names)
    {
        this.tagNames = tag_names;
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
        return updateBy;
    }

    public void setUpdate_by(String update_by)
    {
        this.updateBy = update_by;
    }

    public String getCreate_by()
    {
        return createBy;
    }

    public void setCreate_by(String create_by)
    {
        this.createBy = create_by;
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
        return isDeleted;
    }

    public void setIs_deleted(int is_deleted)
    {
        this.isDeleted = is_deleted;
    }

    @Override
    public String toString()
    {
        return "{id:\"" + id + "\", uuid:\"" + uuid + "\",deviceId:\""
                + deviceId + "\", fsDesc:\"" + fsDesc + "\", isDeleted:\""
                + isDeleted + "\", tagNames:\"" + tagNames + "\", day:\"" + day
                + "\", createTime:\"" + createTime + "\", updateTime:\""
                + updateTime + "\", updateBy:\"" + updateBy + "\", createBy:\""
                + createBy + "\", medias:" + medias + "}";
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

}
