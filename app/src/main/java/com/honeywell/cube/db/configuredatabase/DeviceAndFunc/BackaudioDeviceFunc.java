package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.utils.Loger.Loger;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 09:26
 * Email:Shodong.Sun@honeywell.com
 */
public class BackaudioDeviceFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public BackaudioDeviceFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //增加
    public synchronized long addBackaudioDevice(BackaudioDevice device) throws SQLException {
        if (null == device) {
            return -1;
        }
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, device.mPrimaryID)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER, device.mSerialNumber)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_NAME, device.mName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_MACHINETYPE, device.mMachineType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_LOOPNUMBER, device.mloopNum)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_ISONLINE, device.mIsOnline).getValues();
            db.beginTransaction();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.endTransaction();
            db.close();
        }
        return rowId;
    }

    public synchronized long addBackaudioDevice(BackaudioDevice device, SQLiteDatabase db) throws SQLException {
        if (null == device) {
            return -1;
        }
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, device.mPrimaryID)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER, device.mSerialNumber)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_NAME, device.mName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_MACHINETYPE, device.mMachineType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_LOOPNUMBER, device.mloopNum)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_ISONLINE, device.mIsOnline).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    //删除，必须传入两个参数  ip_addr和mac_addr
    public synchronized int deleteBackaudioDeviceByPrimaryId(long primaryId) {

        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        try {
            int moduleType = CommonData.MODULE_TYPE_BACKAUDIO;
            num = db.delete(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID
                            + "=?", new String[]{String.valueOf(primaryId)});
            if (num > 0) {
                deleteCorrespondingLoop(primaryId, moduleType);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return num;
    }

    /**
     * 清空表
     */
    public void clearBackAudioDevice() {
        String sql = "delete from " + ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE + " where 1=1";
        dbHelper.getWritableDatabase().execSQL(sql);
    }

    //when delete peripheral device,CorrespondingLoop should be deleted!
    private void deleteCorrespondingLoop(long devId, int moduleType) throws JSONException {
        if (devId <= 0 || moduleType <= 0) {
            return;
        }
        BackaudioLoopFunc fuc = new BackaudioLoopFunc(dbHelper);
        fuc.deleteBackaudioLoopByDevId(devId);
    }


    //修改(更新),参数ip_addr或mac_addr
    public synchronized int updateBackaudioDeviceByPrimaryId(long primaryId, BackaudioDevice device) {
        if (null == device) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(device.mName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_NAME, device.mName);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, values,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    //查询(所有列表)
    public synchronized List<BackaudioDevice> getBackaudioDeviceAllList() {
        List<BackaudioDevice> BackaudioDevices = new ArrayList<BackaudioDevice>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);

        while (cursor.moveToNext()) {
            BackaudioDevice device = fillDefaultDevice(cursor);
            BackaudioDevices.add(device);
        }
        cursor.close();
        return BackaudioDevices;
    }

    private BackaudioDevice fillDefaultDevice(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
        BackaudioDevice device = new BackaudioDevice();
        device.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        device.mSerialNumber = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER));
        device.mName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_NAME));
        device.mMachineType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_MACHINETYPE));
        device.mloopNum = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_LOOPNUMBER));
        device.mIsOnline = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_ISONLINE));
        return device;
    }

    public BackaudioDevice getBackaudioDeviceById(long devId) {
        if (devId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(devId)}, null, null, null, null);
        BackaudioDevice device = null;
        while (cursor.moveToNext()) {
            device = fillDefaultDevice(cursor);
        }
        cursor.close();
        return device;
    }

    public synchronized String getSerialNumberByDevId(long id) {

        String serialNumber = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE,
                null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            serialNumber = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER));
            break;
        }
        cursor.close();
        return serialNumber;
    }

    public synchronized BackaudioDevice getBackaudioDeviceByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(primaryId)}, null, null, null, null);
        BackaudioDevice device = null;
        while (cursor.moveToNext()) {
            device = fillDefaultDevice(cursor);
        }
        cursor.close();
        return device;
    }

    public synchronized BackaudioDevice getBackaudioDeviceBySerialNumber(String serialNumber) {
        if (CommonUtils.ISNULL(serialNumber)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER + "=?", new String[]{serialNumber}, null, null, null, null);
        BackaudioDevice device = null;
        while (cursor.moveToNext()) {
            device = fillDefaultDevice(cursor);
        }
        cursor.close();
        return device;
    }

    public synchronized long getDeviceIdBySerialNumber(String serialNumber) {
        int dev_id = -1;
        if (CommonUtils.ISNULL(serialNumber)) {
            return dev_id;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_BACKAUDIO_DEVICE_SERIALNUMBER + "=?", new String[]{serialNumber}, null, null, null, null);

        while (cursor.moveToNext()) {
            dev_id = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
            break;
        }
        cursor.close();
        return dev_id;
    }
}
