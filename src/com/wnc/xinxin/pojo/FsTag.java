package com.wnc.xinxin.pojo;

public class FsTag
{
	private int id;
	private String tag_name;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTag_name()
	{
		return tag_name;
	}

	public void setTag_name(String tag_name)
	{
		this.tag_name = tag_name;
	}

	@Override
	public String toString()
	{
		return "FsTag [id=" + id + ", tag_name=" + tag_name + "]";
	}
}
