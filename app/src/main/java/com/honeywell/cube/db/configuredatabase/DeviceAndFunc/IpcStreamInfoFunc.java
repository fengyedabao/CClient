package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 11:05
 * Email:Shodong.Sun@honeywell.com
 */
public class IpcStreamInfoFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public IpcStreamInfoFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //增加
    public synchronized long addIpcStreamInfo(IpcStreamInfo info) throws SQLException {
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, info.mPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, info.mDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_TYPE, info.mIpcType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_MAIN_STREAM, info.mMainStream)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_SUB_STREAM, info.mSubStream)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_STREAM_PORT, info.mStreamPort)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_USER, info.mUser)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_PASSWORD, info.mPassword)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, info.mRoomId).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }
    public synchronized long addIpcStreamInfo(IpcStreamInfo info, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, info.mPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, info.mDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_TYPE, info.mIpcType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_MAIN_STREAM, info.mMainStream)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_SUB_STREAM, info.mSubStream)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_STREAM_PORT, info.mStreamPort)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_USER, info.mUser)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_PASSWORD, info.mPassword)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, info.mRoomId).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    /**
     * 清空表
     */
    public void clearIpcstreamInfo() {
        String sql = "delete from " + ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO + " where 1=1";
        dbHelper.getWritableDatabase().execSQL(sql);
    }

    public synchronized int deleteIpcStreamInfoByPrimaryId(long primaryId) {
        int num = -1;
        if (primaryId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public synchronized int deleteIpcStreamInfoByDevId(long devId) {
        int num = -1;
        if (devId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                    new String[]{String.valueOf(devId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //now only offer update by devId
    public synchronized int updateIpcStreamInfo(IpcStreamInfo info) {
        if (null == info || info.mDevId <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (info.mPrimaryId != -1) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, info.mPrimaryId);
        }
        if (!CommonUtils.ISNULL(info.mIpcType)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_TYPE, info.mIpcType);
        }
        if (!CommonUtils.ISNULL(info.mMainStream)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_MAIN_STREAM, info.mMainStream);
        }
        if (!CommonUtils.ISNULL(info.mSubStream)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_SUB_STREAM, info.mSubStream);
        }
        if (info.mStreamPort >= 0) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_STREAM_PORT, info.mStreamPort);
        }
        if (!CommonUtils.ISNULL(info.mUser)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_USER, info.mUser);
        }
        if (!CommonUtils.ISNULL(info.mPassword)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_PASSWORD, info.mPassword);
        }
        values.put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, info.mRoomId);
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO, values,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?", new String[]{String.valueOf(info.mDevId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return num;
    }

    public synchronized IpcStreamInfo getIpcStreamInfoByPrimaryId(long primaryId) {
        if (primaryId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);
        IpcStreamInfo loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultIpcStreamInfo(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

//    public synchronized IpcStreamInfo getIpcStreamInfoByDevId(int devId) {
//        if (devId <= 0) {
//            return null;
//        }
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO, null,
//                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
//                new String[]{String.valueOf(devId)}, null, null, null, null);
//        IpcStreamInfo loop = null;
//        while (cursor.moveToNext()) {
//            loop = fillDefaultIpcStreamInfo(cursor);
//            break;
//        }
//        cursor.close();
//        return loop;
//    }

    public synchronized ArrayList<IpcStreamInfo> getIpcStreamInfoByRoom(int room) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{""+room}, null, null, null, null);
        ArrayList<IpcStreamInfo> returnValue = new ArrayList<>();
        while (cursor.moveToNext()) {
            IpcStreamInfo loop = fillDefaultIpcStreamInfo(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        return returnValue;
    }


    private IpcStreamInfo fillDefaultIpcStreamInfo(Cursor cursor) {
        IpcStreamInfo info = null;
        if (null == cursor) {
            return info;
        }
        info = new IpcStreamInfo();
        info.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        info.mPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        info.mDevId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        info.mIpcType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_TYPE));
        info.mMainStream = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_MAIN_STREAM));
        info.mSubStream = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_SUB_STREAM));
        info.mStreamPort = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_STREAM_PORT));
        info.mUser = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_USER));
        info.mPassword = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_PASSWORD));
        info.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        return info;
    }

    //查询(所有列表)
    public synchronized List<IpcStreamInfo> getIpcStreamInfoAllList() {
        List<IpcStreamInfo> IpcStreamInfoList = new ArrayList<IpcStreamInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPCSTREAM_INFO, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            IpcStreamInfoList.add(fillDefaultIpcStreamInfo(cursor));
        }
        cursor.close();
        return IpcStreamInfoList;
    }
}
