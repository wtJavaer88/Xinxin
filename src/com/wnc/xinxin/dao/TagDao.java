package com.wnc.xinxin.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.xinxin.pojo.FsTag;
import common.db.DatabaseManager;

public class TagDao
{
    static Logger log = Logger.getLogger(TagDao.class);
    static SQLiteDatabase database;

    public static void openDatabase()
    {
        database = DatabaseManager.getInstance().openDatabase();
    }

    public static void closeDatabase()
    {
        DatabaseManager.getInstance().closeDatabase();
    }

    public synchronized static boolean insertTag(String tag_name)
    {
        try
        {
            openDatabase();
            if (database.rawQuery(
                    "SELECT * FROM TAGS WHERE IS_DELETED=0 AND NAME='"
                            + tag_name + "'", null).getCount() > 0)
            {
                return true;
            }
            database.execSQL("INSERT INTO TAGS(NAME) VALUES (?)", new Object[]
            { tag_name });
            return true;
        }
        catch (Exception e)
        {
            log.error(tag_name, e);
        }
        finally
        {
            closeDatabase();
        }
        return false;
    }

    public synchronized static List<FsTag> findAllTags()
    {
        List<FsTag> list = new ArrayList<FsTag>();
        try
        {
            openDatabase();
            Cursor c = database.rawQuery(
                    "SELECT * FROM TAGS WHERE IS_DELETED=0", null);
            c.moveToFirst();
            FsTag tag = new FsTag();
            while (!c.isAfterLast())
            {
                tag = new FsTag();
                tag.setId(c.getInt(c.getColumnIndex("id")));
                tag.setTag_name(c.getString(c.getColumnIndex("name")));
                list.add(tag);
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
        database.execSQL("UPDATE TAGS SET IS_DELETED=1");
        closeDatabase();
    }

    public static boolean deleteByName(String tag_name)
    {
        try
        {
            openDatabase();
            database.execSQL("UPDATE TAGS SET IS_DELETED=1 WHERE NAME='"
                    + tag_name + "'");
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            closeDatabase();
        }
        return false;
    }

}
