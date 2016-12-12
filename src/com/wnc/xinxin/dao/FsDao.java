package com.wnc.xinxin.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.xinxin.pojo.FootStepInfo;
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

    public synchronized static void insertSingleFs(FootStepInfo fsInfo)
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

}
