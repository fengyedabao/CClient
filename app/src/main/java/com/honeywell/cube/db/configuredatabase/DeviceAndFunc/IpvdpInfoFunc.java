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
 * Created by H157925 on 16/4/11. 11:09
 * Email:Shodong.Sun@honeywell.com
 */
public class IpvdpInfoFunc {

    ConfigCubeDatabaseHelper dbHelper = null;

    public IpvdpInfoFunc(ConfigCubeDatabaseHelper instance) {
        this.dbHelper = instance;
    }

    //增加
//    public synchronized long addIpvdpInfo(long dev_id, IpvdpInfo info) throws SQLException {
//        long rowId = -1;
//        try {
//            ContentValues values = new ContentValuesFactory()
//                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, dev_id)
//                    .put(ConfigCubeDatabaseHelper.COLUMN_IPVDP_INFO_DEVICEID, info.mDeviceId)
//                    .put(ConfigCubeDatabaseHelper.COLUMN_IPVDP_INFO_HNSSERVERADDR, info.mHnsserveraddr).getValues();
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IPVDP_INFO, null, values);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return rowId;
//    }

    public synchronized int deleteIpvdpInfoByDevId(long devId) {
        int num = -1;
        if (devId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IPVDP_INFO,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                    new String[]{String.valueOf(devId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //now only offer update by devId
//    public synchronized int updateIpvdpInfo(IpvdpInfo info) {
//        if (null == info || info.mDevId <= 0) {
//            return -1;
//        }
//        int num = -1;
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        if (!CommonUtils.ISNULL(info.mHnsserveraddr)) {
//            values.put(ConfigCubeDatabaseHelper.COLUMN_IPCSTREAN_INFO_TYPE, info.mHnsserveraddr);
//        }
//        try {
//            num = db.update(ConfigCubeDatabaseHelper.TABLE_IPVDP_INFO, values,
//                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?", new String[]{String.valueOf(info.mDevId)});
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return num;
//    }

//    public synchronized IpvdpInfo getIpvdpInfoByPrimaryId(long primaryId) {
//        if (primaryId <= 0) {
//            return null;
//        }
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDP_INFO, null,
//                ConfigCubeDatabaseHelper.COLUMN_ID + "=?",
//                new String[]{String.valueOf(primaryId)}, null, null, null, null);
//        IpvdpInfo loop = null;
//        while (cursor.moveToNext()) {
//            loop = fillDefaultIpvdpInfo(cursor);
//            break;
//        }
//        cursor.close();
//        return loop;
//    }

    public synchronized IpvdpInfo getIpvdpInfoByDevId(long devId) {
        if (devId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDP_INFO, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)}, null, null, null, null);
        IpvdpInfo loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultIpvdpInfo(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    private IpvdpInfo fillDefaultIpvdpInfo(Cursor cursor) {
        IpvdpInfo info = null;
        if (null == cursor) {
            return info;
        }
        info = new IpvdpInfo();
        info.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        info.mDevId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        info.mDeviceId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPVDP_INFO_DEVICEID));
        info.mHnsserveraddr = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IPVDP_INFO_HNSSERVERADDR));
        return info;
    }

    //查询(所有列表)
    public synchronized List<IpvdpInfo> getIpvdpInfoAllList() {
        List<IpvdpInfo> IpvdpInfoList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDP_INFO, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            IpvdpInfoList.add(fillDefaultIpvdpInfo(cursor));
        }
        cursor.close();
        return IpvdpInfoList;
    }
}
