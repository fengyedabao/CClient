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
 * Created by H157925 on 16/4/12. 13:24
 * Email:Shodong.Sun@honeywell.com
 */
public class TriggerScenarioFunc {

    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;

    public TriggerScenarioFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }

    public synchronized long addTriggerScenario(TriggerScenarioInfo loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_TRIGGERID, loop.mTriggerOrRuleId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_ACTIONINFO, loop.mActionInfo).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_TRIGGER_SCENARIO, null, values);
        db.close();
        return rowId;
    }

    public synchronized long addTriggerScenario(TriggerScenarioInfo loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_TRIGGERID, loop.mTriggerOrRuleId)
                .put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_ACTIONINFO, loop.mActionInfo).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_TRIGGER_SCENARIO, null, values);
        return rowId;
    }

    public synchronized int deleteTriggerScenarioControlInfoByTriggerId(long triggerId) {
        int num = 0;
        if (triggerId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_TRIGGER_SCENARIO,
                    ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_TRIGGERID + "=?", new String[]{String.valueOf(triggerId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public synchronized int updateTriggerScenarioInfo(long primaryId, String actionInfo) {
        if (CommonUtils.ISNULL(actionInfo) || primaryId <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(actionInfo)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_ACTIONINFO, actionInfo);
        }
        num = db.update(ConfigCubeDatabaseHelper.TABLE_TRIGGER_SCENARIO, values,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)});
        db.close();
        return num;
    }

    public synchronized TriggerScenarioInfo getTriggerScenarioInfoByPrimaryId(long primaryId) {
        if (primaryId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_TRIGGER_SCENARIO, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(primaryId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        TriggerScenarioInfo loop = new TriggerScenarioInfo();
        while (cursor.moveToNext()) {
            fillAllFieldsToLoop(cursor, loop);
        }
        cursor.close();
        return loop;
    }

    public synchronized List<TriggerScenarioInfo> getDeviceControlInfoListByTriggerId(long triggerId) {
        if (triggerId <= 0) {
            return null;
        }
        List<TriggerScenarioInfo> infos = new ArrayList<TriggerScenarioInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_TRIGGER_SCENARIO, null,
                ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_TRIGGERID + "=?", new String[]{String.valueOf(triggerId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            TriggerScenarioInfo loop = new TriggerScenarioInfo();
            fillAllFieldsToLoop(cursor, loop);
            infos.add(loop);
        }
        cursor.close();
        return infos;
    }


    public synchronized List<TriggerScenarioInfo> getTriggerScenarioControlInfoAllList() {
        List<TriggerScenarioInfo> TriggerScenarioControlInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_TRIGGER_SCENARIO, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == TriggerScenarioControlInfoList) {
                TriggerScenarioControlInfoList = new ArrayList<TriggerScenarioInfo>();
            }
            TriggerScenarioInfo loop = new TriggerScenarioInfo();
            fillAllFieldsToLoop(cursor, loop);
            TriggerScenarioControlInfoList.add(loop);
        }
        cursor.close();
        return TriggerScenarioControlInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, TriggerScenarioInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mTriggerOrRuleId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_TRIGGERID));
        loop.mActionInfo = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_TRIGGERSCNARIO_ACTIONINFO));
    }
}
