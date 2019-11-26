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
 * Created by H157925 on 16/4/11. 16:43
 * Email:Shodong.Sun@honeywell.com
 */
public class ScheduleScenarioFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;

    public ScheduleScenarioFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }

    public synchronized long addScheduleScenario(ScheduleScenarioInfo loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_RULEID, loop.mTriggerOrRuleId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_ACTIONINFO, loop.mActionInfo).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addScheduleScenario(ScheduleScenarioInfo loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_RULEID, loop.mTriggerOrRuleId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_ACTIONINFO, loop.mActionInfo).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public synchronized int deleteScheduleScenarioControlInfo(long id) {
        int num = 0;
        if (id <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int deleteScheduleScenarioControlInfoByRule(long ruleId) {
        int num = 0;
        if (ruleId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO,
                    ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_RULEID + "=?", new String[]{String.valueOf(ruleId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int updateScheduleScenarioInfo(long primaryId, String actionInfo, boolean isLast) {
        if (CommonUtils.ISNULL(actionInfo) || primaryId <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(actionInfo)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_ACTIONINFO, actionInfo);
        }
        num = db.update(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO, values,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)});
//        if(num > 0 && isLast){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        db.close();
        return num;
    }

    public synchronized ScheduleScenarioInfo getScheduleScenarioInfo(long id) {
        if (id <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(id)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        ScheduleScenarioInfo loop = new ScheduleScenarioInfo();
        while (cursor.moveToNext()) {
            fillAllFieldsToLoop(cursor, loop);
        }
        cursor.close();
        return loop;
    }

    public synchronized List<ScheduleScenarioInfo> getScheduleScenarioInfoListByRuleId(long ruleId) {
        if (ruleId <= 0) {
            return null;
        }
        List<ScheduleScenarioInfo> infos = new ArrayList<ScheduleScenarioInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO, null,
                ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_RULEID + "=?", new String[]{String.valueOf(ruleId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            ScheduleScenarioInfo loop = new ScheduleScenarioInfo();
            fillAllFieldsToLoop(cursor, loop);
            infos.add(loop);
        }
        cursor.close();
        return infos;
    }


    public synchronized List<ScheduleScenarioInfo> getScheduleScenarioControlInfoAllList() {
        List<ScheduleScenarioInfo> ScheduleScenarioControlInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCHEDULE_SCENARIO, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == ScheduleScenarioControlInfoList) {
                ScheduleScenarioControlInfoList = new ArrayList<ScheduleScenarioInfo>();
            }
            ScheduleScenarioInfo loop = new ScheduleScenarioInfo();
            fillAllFieldsToLoop(cursor, loop);
            ScheduleScenarioControlInfoList.add(loop);
        }
        cursor.close();
        return ScheduleScenarioControlInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, ScheduleScenarioInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mTriggerOrRuleId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_RULEID));
        loop.mActionInfo = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCHEDULESCENARIO_ACTIONINFO));
    }
}
