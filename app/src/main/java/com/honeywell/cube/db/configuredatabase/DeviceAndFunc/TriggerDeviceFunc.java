package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 16:51
 * Email:Shodong.Sun@honeywell.com
 */
public class TriggerDeviceFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;

    public TriggerDeviceFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }

    public synchronized long addTriggerDevice(TriggerDeviceInfo loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID, loop.mTriggerOrRuleId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_ACTIONINFO, loop.mActionInfo)
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE, loop.mModuleType).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE, null, values);
        db.close();
        return rowId;
    }

    public synchronized long addTriggerDevice(TriggerDeviceInfo loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID, loop.mTriggerOrRuleId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_ACTIONINFO, loop.mActionInfo)
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE, loop.mModuleType).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE, null, values);
        return rowId;
    }

    public synchronized int deleteTriggerDeviceControlInfo(long triggerId, long loopPrimaryId, int moduleType) {
        int num = -1;
        if (triggerId <= 0 || loopPrimaryId <= 0 || moduleType <= 0) {
            return num;
        }
        num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE,
                ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(triggerId), String.valueOf(loopPrimaryId), String.valueOf(moduleType)});
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int deleteTriggerDeviceControlInfoByTriggerId(long triggerId) {
        int num = 0;
        if (triggerId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE,
                    ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID + "=?", new String[]{String.valueOf(triggerId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int updateTriggerDeviceInfo(TriggerDeviceInfo loop) {
        if (null == loop || CommonUtils.ISNULL(loop.mActionInfo) || loop.mTriggerOrRuleId <= 0 || loop.mLoopPrimaryId <= 0 || loop.mModuleType <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(loop.mActionInfo)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_ACTIONINFO, loop.mActionInfo);
        }
        num = db.update(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE, values,
                ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(loop.mTriggerOrRuleId), String.valueOf(loop.mLoopPrimaryId), String.valueOf(loop.mModuleType)});
//        if(num > 0 && isLast){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        db.close();
        return num;
    }

    public synchronized TriggerDeviceInfo getTriggerDeviceInfoInfo(long triggerId, long loopPrimaryId, int moduleType) {
        if (triggerId <= 0 || loopPrimaryId <= 0 || moduleType <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(triggerId), String.valueOf(loopPrimaryId), String.valueOf(moduleType)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        TriggerDeviceInfo loop = new TriggerDeviceInfo();
        while (cursor.moveToNext()) {
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    public synchronized List<TriggerDeviceInfo> getDeviceControlInfoListByTriggerId(long triggerId) {
        if (triggerId <= 0) {
            return null;
        }
        List<TriggerDeviceInfo> infos = new ArrayList<TriggerDeviceInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID + "=?", new String[]{String.valueOf(triggerId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            TriggerDeviceInfo loop = new TriggerDeviceInfo();
            fillAllFieldsToLoop(cursor, loop);
            infos.add(loop);
        }
        cursor.close();
        return infos;
    }

    public synchronized List<TriggerDeviceInfo> getDeviceControlInfoListByModuleType(int moduleType) {
        if (moduleType <= 0) {
            return null;
        }
        List<TriggerDeviceInfo> infos = new ArrayList<TriggerDeviceInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?", new String[]{String.valueOf(moduleType)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            TriggerDeviceInfo loop = new TriggerDeviceInfo();
            fillAllFieldsToLoop(cursor, loop);
            infos.add(loop);
        }
        cursor.close();
        return infos;
    }

    public synchronized List<TriggerDeviceInfo> getTriggerDeviceControlInfoAllList() {
        List<TriggerDeviceInfo> triggerDeviceControlInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_TRIGGER_DEVICE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == triggerDeviceControlInfoList) {
                triggerDeviceControlInfoList = new ArrayList<TriggerDeviceInfo>();
            }
            TriggerDeviceInfo loop = new TriggerDeviceInfo();
            fillAllFieldsToLoop(cursor, loop);
            triggerDeviceControlInfoList.add(loop);
        }
        cursor.close();
        return triggerDeviceControlInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, TriggerDeviceInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_PRIMARYID));
        loop.mTriggerOrRuleId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_TRIGGERID));
        loop.mActionInfo = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_ACTIONINFO));
        loop.mLoopPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE));
    }
}
