package com.wnc.xinxin.pojo;

public class FsMedia
{
    private int mediapath_id;
    private String media_name;
    private String absulute_path;
    private String media_type;
    private long media_size;
    private String create_time;

    public String getMedia_name()
    {
        return media_name;
    }

    public void setMedia_name(String media_name)
    {
        this.media_name = media_name;
    }

    public String getMedia_type()
    {
        return media_type;
    }

    public void setMedia_type(String media_type)
    {
        this.media_type = media_type;
    }

    public long getMedia_size()
    {
        return media_size;
    }

    public void setMedia_size(long media_size)
    {
        this.media_size = media_size;
    }

    @Override
    public String toString()
    {
        return "FsMedia [mediapath_id=" + mediapath_id + ", media_name="
                + media_name + ", absulute_path=" + absulute_path
                + ", media_type=" + media_type + ", media_size=" + media_size
                + ", create_time=" + create_time + "]";
    }

    public String getCreate_time()
    {
        return create_time;
    }

    public void setCreate_time(String create_time)
    {
        this.create_time = create_time;
    }

    public int getMediapath_id()
    {
        return mediapath_id;
    }

    public void setMediapath_id(int mediapath_id)
    {
        this.mediapath_id = mediapath_id;
    }

    public String getAbsulute_path()
    {
        return absulute_path;
    }

    public void setAbsulute_path(String absulute_path)
    {
        this.absulute_path = absulute_path;
    }
}
