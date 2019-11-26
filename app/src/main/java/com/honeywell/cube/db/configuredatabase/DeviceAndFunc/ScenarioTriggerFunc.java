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
 * Created by H157925 on 16/4/11. 16:24
 * Email:Shodong.Sun@honeywell.com
 */
public class ScenarioTriggerFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;

    public ScenarioTriggerFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }

    /**
     * 添加Rule
     *
     * @param loop
     * @return
     */
    public synchronized long addScenarioTriggerInfo(ScenarioTriggerInfo loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_SWITCHSTATUS, loop.mSwitchStatus)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DELAYTIME, loop.mDelayTime)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_AVAIBLETIME, loop.mAvaibleTime)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_TYPE, loop.mType)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_NAME, loop.mName)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DESCRIPTION, loop.mDescription).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCENARIO_TRIGGER, null, values);
        db.close();
        return rowId;
    }

    /**
     * 添加Rule
     *
     * @param loop
     * @return
     */
    public synchronized long addScenarioTriggerInfo(ScenarioTriggerInfo loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_SWITCHSTATUS, loop.mSwitchStatus)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DELAYTIME, loop.mDelayTime)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_AVAIBLETIME, loop.mAvaibleTime)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_TYPE, loop.mType)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_NAME, loop.mName)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DESCRIPTION, loop.mDescription).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCENARIO_TRIGGER, null, values);
        return rowId;
    }

    /**
     * 删除
     *
     * @param primaryId
     * @return
     */
    public synchronized int deleteScenarioTriggerInfoByPrimaryID(int primaryId) {
        int num = 0;
        if (primaryId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCENARIO_TRIGGER,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    /**
     * 更新状态
     *
     * @param id
     * @param info
     * @return
     */
    public synchronized int updateScenarioTriggerInfoByPrimaryId(long id, ScenarioTriggerInfo info) {
        if (id <= 0 || null == info) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!CommonUtils.ISNULL(info.mSwitchStatus)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_SWITCHSTATUS, info.mSwitchStatus);
        }
        if (!CommonUtils.ISNULL(info.mAvaibleTime)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_AVAIBLETIME, info.mAvaibleTime);
        }
        if (!CommonUtils.ISNULL(info.mType)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_TYPE, info.mType);
        }
        if (!CommonUtils.ISNULL(info.mName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_NAME, info.mName);
        }
        if (!CommonUtils.ISNULL(info.mDescription)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DESCRIPTION, info.mDescription);
        }
        if (info.mDelayTime >= 0) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DELAYTIME, info.mDelayTime);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_SCENARIO_TRIGGER,
                    values, ConfigCubeDatabaseHelper.COLUMN_PRIMARYID
                            + "=?", new String[]{String.valueOf(id)});

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    /**
     * 获取Rule
     *
     * @param primaryId
     * @return
     */
    public synchronized ScenarioTriggerInfo getScenarioTriggerInfoByPrimaryId(long primaryId) {
        if (primaryId <= 0) {
            return null;
        }
        ScenarioTriggerInfo loop = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCENARIO_TRIGGER, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?", new String[]{String.valueOf(primaryId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            loop = new ScenarioTriggerInfo();
            fillAllFieldsToLoop(cursor, loop);
        }
        cursor.close();
        return loop;
    }


    /**
     * 获取全部的Rule
     *
     * @return
     */
    public synchronized List<ScenarioTriggerInfo> getScenarioTriggerInfoAllList() {
        List<ScenarioTriggerInfo> scenarioTriggerInfoList = new ArrayList<ScenarioTriggerInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCENARIO_TRIGGER, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            ScenarioTriggerInfo loop = new ScenarioTriggerInfo();
            fillAllFieldsToLoop(cursor, loop);
            scenarioTriggerInfoList.add(loop);
        }
        cursor.close();
        return scenarioTriggerInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, ScenarioTriggerInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mSwitchStatus = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_SWITCHSTATUS));
        loop.mDelayTime = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DELAYTIME));
        loop.mAvaibleTime = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_AVAIBLETIME));
        loop.mType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_TYPE));
        loop.mName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_NAME));
        loop.mDescription = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIOTRIGGER_DESCRIPTION));
    }
}
