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
 * Created by H157925 on 16/4/11. 16:36
 * Email:Shodong.Sun@honeywell.com
 */
public class ScheduleDeviceFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;

    public ScheduleDeviceFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
    }

    public synchronized long addScheduleRuleDeviceControlInfo(ScheduleDeviceInfo loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ID, loop.mPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID, loop.mTriggerOrRuleId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ACTIONINFO, loop.mActionInfo)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE, loop.mModuleType).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addScheduleRuleDeviceControlInfo(ScheduleDeviceInfo loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ID, loop.mPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID, loop.mTriggerOrRuleId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ACTIONINFO, loop.mActionInfo)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE, loop.mModuleType).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public synchronized int deleteScheduleRuleDeviceByPrimaryId(long primaryId) {
        int num = -1;
        if (primaryId <= 0) {
            return num;
        }
        num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE,
                ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ID + "=?",
                new String[]{String.valueOf(primaryId)});
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int deleteScheduleRuleDeviceControl(long triggerId, long loopPrimaryId, int moduleType) {
        int num = -1;
        if (triggerId <= 0 || loopPrimaryId <= 0 || moduleType <= 0) {
            return num;
        }
        num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE,
                ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(triggerId), String.valueOf(loopPrimaryId), String.valueOf(moduleType)});
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int deleteScheduleRuleDeviceControlInfoByRule(long ruleId) {
        int num = 0;
        if (ruleId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE,
                    ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID + "=?", new String[]{String.valueOf(ruleId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int updateScheduleDeviceInfo(ScheduleDeviceInfo loop, boolean isLast) {
        if (null == loop || CommonUtils.ISNULL(loop.mActionInfo) || loop.mTriggerOrRuleId <= 0 || loop.mLoopPrimaryId <= 0 || loop.mModuleType <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(loop.mActionInfo)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERDEVICE_ACTIONINFO, loop.mActionInfo);
        }
        num = db.update(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE, values,
                ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(loop.mTriggerOrRuleId), String.valueOf(loop.mLoopPrimaryId), String.valueOf(loop.mModuleType)});
//        if(num > 0 && isLast){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized ScheduleDeviceInfo getScheduleDeviceInfoByPrimaryId(long primaryId) {
        if (primaryId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        ScheduleDeviceInfo loop = new ScheduleDeviceInfo();
        while (cursor.moveToNext()) {
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    public synchronized ScheduleDeviceInfo getScheduleDeviceInfo(long scheduleId, long loopPrimaryId, int moduleType) {
        if (scheduleId <= 0 || loopPrimaryId <= 0 || moduleType <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(scheduleId), String.valueOf(loopPrimaryId), String.valueOf(moduleType)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        ScheduleDeviceInfo loop = new ScheduleDeviceInfo();
        while (cursor.moveToNext()) {
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    public synchronized List<ScheduleDeviceInfo> getDeviceControlInfoListByTriggerId(long ruleId) {
        if (ruleId <= 0) {
            return null;
        }
        List<ScheduleDeviceInfo> infos = new ArrayList<ScheduleDeviceInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE, null,
                ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID + "=?", new String[]{String.valueOf(ruleId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            ScheduleDeviceInfo loop = new ScheduleDeviceInfo();
            fillAllFieldsToLoop(cursor, loop);
            infos.add(loop);
        }
        cursor.close();
        return infos;
    }


    public synchronized List<ScheduleDeviceInfo> getScheduleRuleDeviceControlInfoAllList() {
        List<ScheduleDeviceInfo> ScheduleRuleDeviceControlInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_DEVICE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == ScheduleRuleDeviceControlInfoList) {
                ScheduleRuleDeviceControlInfoList = new ArrayList<ScheduleDeviceInfo>();
            }
            ScheduleDeviceInfo loop = new ScheduleDeviceInfo();
            fillAllFieldsToLoop(cursor, loop);
            ScheduleRuleDeviceControlInfoList.add(loop);
        }
        cursor.close();
        return ScheduleRuleDeviceControlInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, ScheduleDeviceInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ID));
        loop.mTriggerOrRuleId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_RULEID));
        loop.mActionInfo = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULEDEVICE_ACTIONINFO));
        loop.mLoopPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE));
    }
}
