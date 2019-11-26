package com.honeywell.cube.db.configuredatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by H157925 on 16/4/11. 09:30
 * Email:Shodong.Sun@honeywell.com
 */
public class CubebaseFuc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public CubebaseFuc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //update the record fields
    public synchronized int updateCubebase(Map<String, String> map) {
        if (null == map)
        {
            return -1;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        int ret = -1;
        try {
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = it.next();
                ContentValues values = new ContentValues();

                String config_name=(String) entry.getKey();
                String config_value=(String) entry.getValue();
                if(config_name != null && config_value != null){
                    values.put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE, config_value);
                    db.update(ConfigCubeDatabaseHelper.TABLE_CUBEBASE,values,
                            ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGNAME + "=?",
                            new String[]{config_name});
                }
                ret = 0;
                db.setTransactionSuccessful();//由事务的标志决定是提交事务，还是回滚事务
            }
        }catch (Exception e) {
            ret = -1;
            e.printStackTrace();
        }
        finally{
            db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
            updateConfigVer();
        }
        return ret;
    }
    public synchronized int updateConfigVer() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        int ret = -1;
        Cursor cursor = null;
        try {
            cursor = db.query(ConfigCubeDatabaseHelper.TABLE_CUBEBASE,null,
                    ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGNAME + "=?",
                    new String[] { ConfigCubeDatabaseHelper.cubebase_confignames[ConfigCubeDatabaseHelper.cubebase_confignames.length-1] }, null, null, null, null);
            long version = -1;
            while(cursor.moveToNext()){
                String config_ver = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE));
                if(!CommonUtils.ISNULL(config_ver)){
                    version = Long.parseLong(config_ver);
                }
                version ++;
                break;
            }

            ContentValues values = new ContentValues();
            values.put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE, String.valueOf(version));
            db.update(ConfigCubeDatabaseHelper.TABLE_CUBEBASE,values,
                    ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGNAME + "=?",
                    new String[]{ConfigCubeDatabaseHelper.cubebase_confignames[ConfigCubeDatabaseHelper.cubebase_confignames.length-1]});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            ret = -1;
            e.printStackTrace();
        }
        finally{
            if(null != cursor){
                cursor.close();
            }
            db.endTransaction();
        }
        return ret;
    }

//    public synchronized List<String> getCubebaseConfig() {
//        List<String>list = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select * from "+ConfigCubeDatabaseHelper.TABLE_CUBEBASE+" order by "
//                        + ConfigCubeDatabaseHelper.COLUMN_ID+" asc limit ?,?",
//                new String[]{String.valueOf(0), String.valueOf(ConfigCubeDatabaseHelper.cubebase_confignames.length)});
//        while(cursor.moveToNext()){
//            if(null == list){
//                list = new ArrayList<String>();
//            }
//            list.add(cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE)));
//        }
//        cursor.close();
//        return list;
//    }
}
