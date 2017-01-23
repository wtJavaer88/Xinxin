package com.wnc.xinxin.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

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

    public synchronized static boolean insertComplicateFs(FootStepInfo fsInfo,
            List<FsMedia> medias, Set<String> tag_names)
    {
        try
        {
            openDatabase();
            database.beginTransaction();
            database.execSQL(
                    "INSERT INTO FOOTSTEPS(uuid,day,fs_desc,tag_names,create_time,update_time,create_by) VALUES (?,?,?,?,?,?,?)",
                    new Object[]
                    { fsInfo.getUuid(), fsInfo.getDay(), fsInfo.getFs_desc(),
                            tag_names.toString(), fsInfo.getCreate_time(),
                            fsInfo.getUpdate_time(), fsInfo.getCreate_by() });
            for (int i = 0; i < medias.size(); i++)
            {
                FsMedia fsMedia = medias.get(i);
                database.execSQL(
                        "INSERT INTO FS_MEDIAS(fs_uuid,media_name,media_size,media_type,sn,create_time,media_fullpath) VALUES (?,?,?,?,?,?,?)",
                        new Object[]
                        { fsInfo.getUuid(), fsMedia.getMedia_name(),
                                fsMedia.getMedia_size(),
                                fsMedia.getMedia_type(), i + 1,
                                fsMedia.getCreate_time(),
                                fsMedia.getMedia_fullpath() });
            }
            database.setTransactionSuccessful();
            return true;
        }
        catch (Exception e)
        {
            log.error(fsInfo.getFs_desc(), e);
        }
        finally
        {
            database.endTransaction();
            closeDatabase();
        }
        return false;
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
                info.setFs_desc(c.getString(c.getColumnIndex("fs_desc")));
                info.setTag_names(c.getString(c.getColumnIndex("tag_names")));
                info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
                info.setUpdate_time(c.getString(c.getColumnIndex("update_time")));
                info.setCreate_by(c.getString(c.getColumnIndex("create_by")));
                info.setUpdate_by(c.getString(c.getColumnIndex("update_by")));
                info.setIs_deleted(c.getInt(c.getColumnIndex("is_deleted")));
                list.add(info);
                List<FsMedia> findMedias = findMedias(info.getUuid());
                info.setMedias(findMedias);
                System.out.println(info.getId());
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
            Cursor c = database.rawQuery(
                    "SELECT fm.* FROM FS_MEDIAS fm WHERE FS_UUID='" + uuid
                            + "'", null);
            c.moveToFirst();
            FsMedia info = new FsMedia();
            while (!c.isAfterLast())
            {
                info = new FsMedia();
                info.setMedia_name(c.getString(c.getColumnIndex("media_name")));
                info.setMedia_size(c.getInt(c.getColumnIndex("media_size")));
                info.setMedia_type(c.getString(c.getColumnIndex("media_type")));
                info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
                info.setMedia_fullpath(c.getString(c
                        .getColumnIndex("media_fullpath")));
                info.setFs_uuid(uuid);
                info.setIs_deleted(c.getInt(c.getColumnIndex("is_deleted")));
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

    public static boolean updateComplicateFs(FootStepInfo fsInfo,
            List<FsMedia> medias, Set<String> tag_names)
    {
        String uuid = fsInfo.getUuid();
        try
        {
            openDatabase();
            database.beginTransaction();
            database.execSQL(
                    "UPDATE FOOTSTEPS SET FS_DESC = ?,TAG_NAMES=?,UPDATE_TIME=?,UPDATE_By=?,DAY=? WHERE UUID=?",
                    new Object[]
                    { fsInfo.getFs_desc(), tag_names.toString(),
                            fsInfo.getUpdate_time(), fsInfo.getUpdate_by(),
                            fsInfo.getDay(), uuid });
            database.delete("FS_MEDIAS", "FS_UUID=?", new String[]
            { uuid });
            for (int i = 0; i < medias.size(); i++)
            {
                FsMedia fsMedia = medias.get(i);
                database.execSQL(
                        "INSERT INTO FS_MEDIAS(fs_uuid,media_name,media_size,media_type,sn,create_time,media_fullpath) VALUES (?,?,?,?,?,?,?)",
                        new Object[]
                        { uuid, fsMedia.getMedia_name(),
                                fsMedia.getMedia_size(),
                                fsMedia.getMedia_type(), i + 1,
                                fsMedia.getCreate_time(),
                                fsMedia.getMedia_fullpath() });
            }
            database.setTransactionSuccessful();
            return true;
        }
        catch (Exception e)
        {
            log.error(fsInfo.getFs_desc(), e);
        }
        finally
        {
            database.endTransaction();
            closeDatabase();
        }
        return false;
    }

}
