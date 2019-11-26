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
 * Created by H157925 on 16/4/11. 16:39
 * Email:Shodong.Sun@honeywell.com
 */
public class ScheduleRuleFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;

    public ScheduleRuleFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
    }

    public synchronized long addScheduleRuleInfo(ScheduleRuleInfo loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_SWITCHSTATUS, loop.mSwitchStatus)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_AVAIBLETIME, loop.mAvaibleTime)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_NAME, loop.mName)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_DESCRIPTION, loop.mDescription).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_RULE, null, values);
        db.close();
        return rowId;
    }

    public synchronized long addScheduleRuleInfo(ScheduleRuleInfo loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_SWITCHSTATUS, loop.mSwitchStatus)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_AVAIBLETIME, loop.mAvaibleTime)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_NAME, loop.mName)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_DESCRIPTION, loop.mDescription).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_RULE, null, values);
        return rowId;
    }

    // delete  scenario_ids record!
    public synchronized int deleteScheduleRuleInfoByScheduleRuleId(long scheduleRuleId) {
        int num = 0;
        if (scheduleRuleId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_RULE,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(scheduleRuleId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int updateScheduleRuleInfo(long id, ScheduleRuleInfo info) {
        if (id <= 0 || null == info) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!CommonUtils.ISNULL(info.mSwitchStatus)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_SWITCHSTATUS, info.mSwitchStatus);
        }
        if (!CommonUtils.ISNULL(info.mAvaibleTime)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_AVAIBLETIME, info.mAvaibleTime);
        }
        if (!CommonUtils.ISNULL(info.mName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_NAME, info.mName);
        }
        if (!CommonUtils.ISNULL(info.mDescription)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_DESCRIPTION, info.mDescription);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_RULE,
                    values, ConfigCubeDatabaseHelper.COLUMN_PRIMARYID
                            + "=?", new String[]{String.valueOf(id)});

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int updateScheduleRuleInfoByScheduleRuleId(long id, String status) {
        if (id <= 0 || CommonUtils.ISNULL(status)) {
            return -1;
        }

        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_SWITCHSTATUS, status);

        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_RULE,
                    values, ConfigCubeDatabaseHelper.COLUMN_PRIMARYID
                            + "=?", new String[]{String.valueOf(id)});

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized ScheduleRuleInfo getScheduleRuleInfoByScheduleRuleId(long scheduleRuleId) {
        if (scheduleRuleId <= 0) {
            return null;
        }
        ScheduleRuleInfo loop = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_RULE, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(scheduleRuleId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            loop = new ScheduleRuleInfo();
            fillAllFieldsToLoop(cursor, loop);
        }
        cursor.close();
        return loop;
    }

    public synchronized List<ScheduleRuleInfo> getScheduleRuleInfoAllList() {
        List<ScheduleRuleInfo> ScheduleRuleInfoList = new ArrayList<ScheduleRuleInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_RULE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            ScheduleRuleInfo loop = new ScheduleRuleInfo();
            fillAllFieldsToLoop(cursor, loop);
            ScheduleRuleInfoList.add(loop);
        }
        cursor.close();
        return ScheduleRuleInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, ScheduleRuleInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mSwitchStatus = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_SWITCHSTATUS));
        loop.mAvaibleTime = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_AVAIBLETIME));
        loop.mName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_NAME));
        loop.mDescription = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULERULE_DESCRIPTION));
    }
}
