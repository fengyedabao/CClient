package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.db.configuredatabase.Util;
import com.honeywell.cube.utils.Loger.Loger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/5/27. 11:08
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeDeviceFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public CubeDeviceFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //add
    public synchronized long addCubeDevice(CubeDevice device) throws SQLException {
        if (null == device) {
            return -1;
        }
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEID, device.mDeviceId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICE_SERIAL, device.mDeviceSerial)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_SERIALNUMBER, device.mInfo_serialNumber)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_FIRMWAREVERSION, device.mInfo_firmWareVersion)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_APPLICATIONVERSION, device.mInfo_applicationVersion)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_MACADDRESS, device.mInfo_macAddress)
                    .put(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_ALIASNAME, device.mInfo_aliasName).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_CUBE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    //删除所有的数据
    public synchronized void deleteAllCubeDevice()
    {
        String sql = "delete from " + ConfigCubeDatabaseHelper.TABLE_CUBE + " where 1=1";
        dbHelper.getWritableDatabase().execSQL(sql);
    }
    //删除 通过device id
    public synchronized int deleteCubeByDeviceId(int deviceId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_CUBE,
                    ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEID + "=?",
                    new String[]{String.valueOf(deviceId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //查询 所有的列表
    public synchronized List<CubeDevice> getAllCubeDevices() {
        List<CubeDevice> CubeDevices = new ArrayList<CubeDevice>();
        ;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_CUBE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            CubeDevice device = fillDefaultDevice(cursor);
            CubeDevices.add(device);
        }
        cursor.close();
        return CubeDevices;
    }

    private CubeDevice fillDefaultDevice(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
        CubeDevice device = new CubeDevice();
        device.mId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        device.mDeviceId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEID));
        device.mDeviceSerial = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICE_SERIAL));
        device.mInfo_serialNumber = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_SERIALNUMBER));
        device.mInfo_firmWareVersion = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_FIRMWAREVERSION));
        device.mInfo_applicationVersion = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_APPLICATIONVERSION));
        device.mInfo_macAddress = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_MACADDRESS));
        device.mInfo_aliasName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CUBE_DEVICEINFO_ALIASNAME));

        return device;
    }
}
