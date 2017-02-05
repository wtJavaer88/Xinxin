package com.wnc.xinxin.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.xinxin.pojo.FootStepInfo;
import com.wnc.xinxin.pojo.FsMedia;
import common.db.DatabaseManager;

public class FsDao
{
	static Logger log = Logger.getLogger(FsDao.class);
	static SQLiteDatabase database;

	public static void openDatabase()
	{
		database = DatabaseManager.getInstance().openDatabase();
	}

	public static void closeDatabase()
	{
		DatabaseManager.getInstance().closeDatabase();
	}

	public synchronized static boolean existFs(String uuid)
	{
		try
		{
			openDatabase();
			Cursor c = database.rawQuery("select * from FOOTSTEPS where uuid='" + uuid + "'", null);
			c.moveToFirst();
			while (!c.isAfterLast())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			log.error(uuid, e);
		}
		finally
		{
			closeDatabase();
		}
		return false;
	}

	public synchronized static boolean existFs(String uuid, String update_time)
	{
		try
		{
			openDatabase();
			Cursor c = database.rawQuery("select * from FOOTSTEPS where uuid='" + uuid + "' and update_time='" + update_time + "'", null);
			c.moveToFirst();
			while (!c.isAfterLast())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			log.error(uuid, e);
		}
		finally
		{
			closeDatabase();
		}
		return false;
	}

	public synchronized static boolean insertComplicateFs(FootStepInfo fsInfo, String tag_names)
	{
		try
		{
			openDatabase();
			database.beginTransaction();
			database.execSQL("INSERT INTO FOOTSTEPS(uuid,day,fs_desc,tag_names,create_time,update_time,create_by,device_id) VALUES (?,?,?,?,?,?,?,?)",
					new Object[] { fsInfo.getUuid(), fsInfo.getDay(), fsInfo.getFsDesc(), tag_names.toString(), fsInfo.getCreateTime(), fsInfo.getUpdateTime(), fsInfo.getCreateBy(), fsInfo.getDeviceId() });
			List<FsMedia> medias = fsInfo.getMedias();
			for (int i = 0; i < medias.size(); i++)
			{
				FsMedia fsMedia = medias.get(i);
				database.execSQL("INSERT INTO FS_MEDIAS(fs_uuid,media_name,media_size,media_type,sn,create_time,media_fullpath) VALUES (?,?,?,?,?,?,?)",
						new Object[] { fsInfo.getUuid(), fsMedia.getMediaName(), fsMedia.getMediaSize(), fsMedia.getMediaType(), i + 1, fsMedia.getCreateTime(), fsMedia.getMediaFullpath() });
			}
			database.setTransactionSuccessful();
			return true;
		}
		catch (Exception e)
		{
			log.error(fsInfo.getFsDesc(), e);
		}
		finally
		{
			database.endTransaction();
			closeDatabase();
		}
		return false;
	}

	public synchronized static boolean insertComplicateFs(FootStepInfo fsInfo, Set<String> tag_names)
	{
		return insertComplicateFs(fsInfo, tag_names.toString());
	}

	public synchronized static List<FootStepInfo> findAllFsBySql(String sql)
	{
		List<FootStepInfo> list = new ArrayList<FootStepInfo>();
		try
		{
			openDatabase();
			Cursor c = database.rawQuery(sql, null);
			c.moveToFirst();
			FootStepInfo info = new FootStepInfo();
			while (!c.isAfterLast())
			{
				info = new FootStepInfo();
				info.setId(c.getInt(c.getColumnIndex("id")));
				info.setUuid(c.getString(c.getColumnIndex("uuid")));
				info.setDay(c.getString(c.getColumnIndex("day")));
				info.setFsDesc(c.getString(c.getColumnIndex("fs_desc")));
				info.setTagNames(c.getString(c.getColumnIndex("tag_names")));
				info.setCreateTime(c.getString(c.getColumnIndex("create_time")));
				info.setUpdateTime(c.getString(c.getColumnIndex("update_time")));
				info.setCreateBy(c.getString(c.getColumnIndex("create_by")));
				info.setUpdateBy(c.getString(c.getColumnIndex("update_by")));
				info.setIsDeleted(c.getInt(c.getColumnIndex("is_deleted")));
				info.setDeviceId(c.getString(c.getColumnIndex("device_id")));
				list.add(info);
				List<FsMedia> findMedias = findMedias(info.getUuid());
				info.setMedias(findMedias);
				// System.out.println("找到的媒体数:" + findMedias.size());
				c.moveToNext();
			}
		}
		catch (Exception e)
		{
			log.error("findall", e);
		}
		finally
		{
			closeDatabase();
		}
		return list;
	}

	private static List<FsMedia> findMedias(String uuid)
	{
		List<FsMedia> list = new ArrayList<FsMedia>();
		try
		{
			openDatabase();
			Cursor c = database.rawQuery("SELECT * FROM FS_MEDIAS WHERE FS_UUID='" + uuid + "'", null);
			c.moveToFirst();
			FsMedia info = new FsMedia();
			while (!c.isAfterLast())
			{
				info = new FsMedia();
				info.setMediaName(c.getString(c.getColumnIndex("media_name")));
				info.setMediaSize(c.getLong(c.getColumnIndex("media_size")));
				info.setMediaType(c.getString(c.getColumnIndex("media_type")));
				info.setCreateTime(c.getString(c.getColumnIndex("create_time")));
				info.setSn(c.getInt(c.getColumnIndex("sn")));
				info.setMediaFullpath(c.getString(c.getColumnIndex("media_fullpath")));
				info.setFsUuid(uuid);
				info.setIsDeleted(c.getInt(c.getColumnIndex("is_deleted")));
				list.add(info);

				c.moveToNext();
			}
		}
		catch (Exception e)
		{
			log.error("findall", e);
		}
		finally
		{
			closeDatabase();
		}
		return list;
	}

	public static void deleteAll()
	{
		openDatabase();
		database.execSQL("DELETE FROM FOOTSTEPS");
		closeDatabase();
	}

	public static boolean updateComplicateFs(FootStepInfo fsInfo)
	{
		String uuid = fsInfo.getUuid();
		try
		{
			openDatabase();
			database.beginTransaction();
			database.execSQL("UPDATE FOOTSTEPS SET FS_DESC = ?,TAG_NAMES=?,UPDATE_TIME=?,UPDATE_By=?,DAY=?,DEVICE_ID=?,IS_DELETED=? WHERE UUID=?",
					new Object[] { fsInfo.getFsDesc(), fsInfo.getTagNames(), fsInfo.getUpdateTime(), fsInfo.getUpdateBy(), fsInfo.getDay(), fsInfo.getDeviceId(), fsInfo.getIsDeleted(), uuid });
			database.delete("FS_MEDIAS", "FS_UUID=?", new String[] { uuid });
			for (int i = 0; i < fsInfo.getMedias().size(); i++)
			{
				FsMedia fsMedia = fsInfo.getMedias().get(i);
				database.execSQL("INSERT INTO FS_MEDIAS(fs_uuid,media_name,media_size,media_type,sn,create_time,media_fullpath) VALUES (?,?,?,?,?,?,?)", new Object[] { uuid, fsMedia.getMediaName(), fsMedia.getMediaSize(), fsMedia.getMediaType(), i + 1, fsMedia.getCreateTime(),
						fsMedia.getMediaFullpath() });
			}
			database.setTransactionSuccessful();
			return true;
		}
		catch (Exception e)
		{
			log.error(fsInfo.getFsDesc(), e);
		}
		finally
		{
			database.endTransaction();
			closeDatabase();
		}
		return false;
	}

	public static boolean deleteFs(String uuid)
	{

		try
		{
			openDatabase();
			ContentValues cv = new ContentValues();
			cv.put("IS_DELETED", 1);
			if (database.update("FOOTSTEPS", cv, "UUID = ?", new String[] { uuid }) == 0)
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException("修改成员表时异常," + ex.getMessage());
		}
		return true;
	}
}
