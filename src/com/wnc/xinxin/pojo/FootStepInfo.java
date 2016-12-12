package com.wnc.xinxin.pojo;

public class FootStepInfo
{
    private int id;
    private String desc;
    private String create_time;
    private String update_time;

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
        return "FootStepInfo [id=" + id + ", desc=" + desc + ", create_time="
                + create_time + ", update_time=" + update_time + "]";
    }
}
