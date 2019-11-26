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
 * Created by H157925 on 16/4/11. 10:46
 * Email:Shodong.Sun@honeywell.com
 */
public class ConditionFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;

    public ConditionFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }

    public synchronized long addTriggerConditionInfo(ConditionInfo loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_CONDITION_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID, loop.mTriggerOrRuleId)
                .put(ConfigCubeDatabaseHelper.COLUMN_CONDITION_ACTIONINFO, loop.mActionInfo)
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE, loop.mModuleType).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_CONDITION, null, values);
        db.close();
        return rowId;
    }

    public synchronized long addTriggerConditionInfo(ConditionInfo loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_CONDITION_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID, loop.mTriggerOrRuleId)
                .put(ConfigCubeDatabaseHelper.COLUMN_CONDITION_ACTIONINFO, loop.mActionInfo)
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE, loop.mModuleType).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_CONDITION, null, values);
        return rowId;
    }

    public synchronized int deleteConditionInfoLoop(long triggerId, long loopPrimaryId, int moduleType) {
        int num = -1;
        if (triggerId <= 0 || loopPrimaryId <= 0 || moduleType <= 0) {
            return num;
        }
        num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_CONDITION,
                ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(triggerId), String.valueOf(loopPrimaryId), String.valueOf(moduleType)});
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int deleteTriggerConditionInfoByTriggerId(int triggerId) {
        int num = 0;
        if (triggerId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_CONDITION,
                    ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID + "=?", new String[]{String.valueOf(triggerId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    // only can update:mScenarioName,mAction,mSubAction,mIsArm
    public synchronized int updateTriggerConditionInfo(ConditionInfo loop) {
        if (null == loop || loop.mTriggerOrRuleId <= 0 || loop.mLoopPrimaryId <= 0 || loop.mModuleType <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(loop.mActionInfo)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_CONDITION_ACTIONINFO, loop.mActionInfo);
        }
        num = db.update(ConfigCubeDatabaseHelper.TABLE_CONDITION, values,
                ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID + "=? and "
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

    public synchronized ConditionInfo getTriggerConditionInfo(long triggerId, long loopPrimaryId, int moduleType) {
        if (triggerId <= 0 || loopPrimaryId <= 0 || moduleType <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_CONDITION, null,
                ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(triggerId), String.valueOf(loopPrimaryId), String.valueOf(moduleType)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        ConditionInfo loop = new ConditionInfo();
        while (cursor.moveToNext()) {
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    public synchronized List<ConditionInfo> getTriggerConditionInfoListByTriggerId(long triggerId) {
        if (triggerId <= 0) {
            return null;
        }
        List<ConditionInfo> conditionInfos = new ArrayList<ConditionInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_CONDITION, null,
                ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID + "=?", new String[]{String.valueOf(triggerId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            ConditionInfo loop = new ConditionInfo();
            fillAllFieldsToLoop(cursor, loop);
            conditionInfos.add(loop);
        }
        cursor.close();
        db.close();
        return conditionInfos;
    }

    public synchronized List<ConditionInfo> getTriggerConditionInfoAllList() {
        List<ConditionInfo> triggerConditionInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_CONDITION, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == triggerConditionInfoList) {
                triggerConditionInfoList = new ArrayList<ConditionInfo>();
            }
            ConditionInfo loop = new ConditionInfo();
            fillAllFieldsToLoop(cursor, loop);
            triggerConditionInfoList.add(loop);
        }
        cursor.close();
        db.close();
        return triggerConditionInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, ConditionInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CONDITION_PRIMARYID));
        loop.mTriggerOrRuleId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CONDITION_TRIGGERID));
        loop.mActionInfo = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_CONDITION_ACTIONINFO));
        loop.mLoopPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE));
    }
}
