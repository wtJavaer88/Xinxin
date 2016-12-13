package com.wnc.xinxin.dao;

import java.util.ArrayList;
import java.util.List;

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

    public synchronized static void insertFs(FootStepInfo fsInfo)
    {
        try
        {
            openDatabase();
            database.execSQL(
                    "INSERT INTO FOOTSTEPS(fs_desc,create_time,update_time) VALUES (?,?,?)",
                    new Object[]
                    { fsInfo.getDesc(), fsInfo.getCreate_time(),
                            fsInfo.getUpdate_time() });
        }
        catch (Exception e)
        {
            log.error(fsInfo.getDesc(), e);
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void insertComplicateFs(FootStepInfo fsInfo,
            List<FsMedia> medias, List<Integer> tagIDs)
    {
        try
        {
            openDatabase();
            database.beginTransaction();
            database.execSQL(
                    "INSERT INTO FOOTSTEPS(fs_desc,create_time,update_time) VALUES (?,?,?)",
                    new Object[]
                    { fsInfo.getDesc(), fsInfo.getCreate_time(),
                            fsInfo.getUpdate_time() });
            int fs_id = getId(database);
            log.info("当前fs_id:" + fs_id);
            for (int i = 0; i < medias.size(); i++)
            {
                FsMedia fsMedia = medias.get(i);
                database.execSQL(
                        "INSERT INTO FS_MEDIAS(fs_id,mediapath_id,media_name,media_size,media_type,sn,create_time) VALUES (?,?,?,?,?,?,?)",
                        new Object[]
                        { fs_id, fsMedia.getMediapath_id(),
                                fsMedia.getMedia_name(),
                                fsMedia.getMedia_size(),
                                fsMedia.getMedia_type(), i + 1,
                                fsMedia.getCreate_time() });
            }
            database.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            log.error(fsInfo.getDesc(), e);
        }
        finally
        {
            database.endTransaction();
            closeDatabase();
        }
    }

    private static int getId(SQLiteDatabase database2)
    {
        Cursor c = database.rawQuery("SELECT MAX(ID) CUR_ID FROM FOOTSTEPS",
                null);
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            return c.getInt(c.getColumnIndex("CUR_ID"));
        }
        return 0;
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
                info.setDesc(c.getString(c.getColumnIndex("fs_desc")));
                info.setId(c.getInt(c.getColumnIndex("id")));
                info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
                info.setUpdate_time(c.getString(c.getColumnIndex("update_time")));
                list.add(info);
                List<FsMedia> findMedias = findMedias(info);
                info.setMedias(findMedias);
                System.out.println("找到的媒体数:" + findMedias.size());
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

    private static List<FsMedia> findMedias(FootStepInfo fs)
    {
        List<FsMedia> list = new ArrayList<FsMedia>();
        try
        {
            openDatabase();
            Cursor c = database
                    .rawQuery(
                            "SELECT fm.*,mc.path||fm.media_name absolute_path FROM FS_MEDIAS fm,media_path_config mc WHERE fm.mediapath_id=mc.id and FS_ID="
                                    + fs.getId(), null);
            c.moveToFirst();
            FsMedia info = new FsMedia();
            while (!c.isAfterLast())
            {
                info = new FsMedia();
                info.setMediapath_id(c.getInt(c.getColumnIndex("mediapath_id")));
                info.setMedia_name(c.getString(c.getColumnIndex("media_name")));
                info.setMedia_size(c.getInt(c.getColumnIndex("media_size")));
                info.setMedia_type(c.getString(c.getColumnIndex("media_type")));
                info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
                info.setAbsulute_path(c.getString(c
                        .getColumnIndex("absolute_path")));
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

}
