package com.wnc.xinxin.pojo;

import java.io.Serializable;

public class FsMedia implements Serializable
{
	/**
     * 
     */
	private static final Long serialVersionUID = 8001226207947247024L;
	private Integer id;
	private String fsUuid;
	private String mediaName;
	private String mediaFullpath;
	private String mediaType;
	private Long mediaSize;
	private Integer isDeleted;
	private Integer sn;
	private String createTime;
	private String saveFolder;

	public String getMediaName()
	{
		return mediaName;
	}

	public void setMediaName(String mediaName)
	{
		this.mediaName = mediaName;
	}

	public String getMediaType()
	{
		return mediaType;
	}

	public void setMediaType(String mediaType)
	{
		this.mediaType = mediaType;
	}

	public Long getMediaSize()
	{
		return mediaSize;
	}

	public void setMediaSize(Long mediaSize)
	{
		this.mediaSize = mediaSize;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	public String getFsUuid()
	{
		return fsUuid;
	}

	public void setFsUuid(String fsUuid)
	{
		this.fsUuid = fsUuid;
	}

	public Integer getIsDeleted()
	{
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted)
	{
		this.isDeleted = isDeleted;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	@Override
	public String toString()
	{
		return "{id:\"" + id + "\", mediaName:\"" + mediaName + "\", mediaFullpath:\"" + mediaFullpath + "\", mediaType:\"" + mediaType + "\", sn:\"" + sn + "\", mediaSize:\"" + mediaSize + "\", createTime:\"" + createTime + "\", isDeleted:\"" + isDeleted + "\"}";
	}

	public String getMediaFullpath()
	{
		return mediaFullpath;
	}

	public void setMediaFullpath(String mediaFullpath)
	{
		this.mediaFullpath = mediaFullpath;
	}

	public Integer getSn()
	{
		return sn;
	}

	public void setSn(Integer sn)
	{
		this.sn = sn;
	}

	public String getSaveFolder()
	{
		return saveFolder;
	}

	public void setSaveFolder(String saveFolder)
	{
		this.saveFolder = saveFolder;
	}

}
