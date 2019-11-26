package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/8/5. 11:34
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeBaseConfigFunc {

    ConfigCubeDatabaseHelper dbHelper = null;

    public CubeBaseConfigFunc(ConfigCubeDatabaseHelper instance) {
        this.dbHelper = instance;
    }

    //add
    public synchronized long addCubeBaseConfig(CubeBaseConfig cubeBaseConfig) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, cubeBaseConfig.primaryid)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGNAME, cubeBaseConfig.conf_name)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE, cubeBaseConfig.conf_value).getValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_CUBEBASECONFIG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    //add
    public synchronized long addCubeBaseConfig(CubeBaseConfig cubeBaseConfig, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, cubeBaseConfig.primaryid)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGNAME, cubeBaseConfig.conf_name)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE, cubeBaseConfig.conf_value).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_CUBEBASECONFIG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    //delete
    public synchronized int deleteCubeBaseConfig(long devId) {
        int num = -1;
        if (devId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_CUBEBASECONFIG,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(devId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //更新
    public synchronized int updateCubeBaseConfig(CubeBaseConfig info) {
        if (null == info) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(info.conf_name)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGNAME, info.conf_name);
        }
        if (!CommonUtils.ISNULL(info.conf_value)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE, info.conf_value);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_CUBEBASECONFIG, values,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(info.primaryid)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //获取
    public synchronized CubeBaseConfig getCubeBaseConfigByPrimaryid(long primaryId) {
        if (primaryId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_CUBEBASECONFIG, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);
        CubeBaseConfig loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultCubeBaseConfig(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    //获取list
    public synchronized List<CubeBaseConfig> getCubeBaseConfigList() {
        List<CubeBaseConfig> infolist = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_CUBEBASECONFIG, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            infolist.add(fillDefaultCubeBaseConfig(cursor));
        }
        cursor.close();
        return infolist;
    }

    private CubeBaseConfig fillDefaultCubeBaseConfig(Cursor cursor) {
        CubeBaseConfig info = null;
        if (null == cursor) {
            return info;
        }
        info = new CubeBaseConfig();
        info.primaryid = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        info.conf_name = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGNAME));
        info.conf_value = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBEBASE_CONFIGVALUE));
        return info;
    }
}
