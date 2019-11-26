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
 * Created by H157925 on 16/4/11. 14:41
 * Email:Shodong.Sun@honeywell.com
 */
public class MutexRuleFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;

    public MutexRuleFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }

    public synchronized long addMutexRuleInfo(MutexRuleInfo loop, boolean isUpdateVer) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_MUTEXRULE_SWITCHSTATUS, loop.mSwitchStatus)
                    .put(ConfigCubeDatabaseHelper.COLUMN_MUTEXRULE_NAME, loop.mName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_MUTEXRULE_DESCRIPTION, loop.mDescription).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_MUTEX_RULE, null, values);
        } finally {
            db.close();
//            if(rowId > 0 && isUpdateVer){
//                CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//                cubebaseFuc.updateConfigVer();
//            }
        }
        return rowId;
    }

    public synchronized int deleteMutexRuleInfoByRuleId(long ruleId) {
        int num = 0;
        if (ruleId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_MUTEX_RULE,
                    ConfigCubeDatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(ruleId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int updateMutexRuleInfoByRuleId(long id, String status) {
        if (id <= 0 || CommonUtils.ISNULL(status)) {
            return -1;
        }

        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigCubeDatabaseHelper.COLUMN_MUTEXRULE_SWITCHSTATUS, status);

        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_MUTEX_RULE,
                    values, ConfigCubeDatabaseHelper.COLUMN_ID
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

    public synchronized MutexRuleInfo getMutexRuleInfoByRuleId(long RuleId) {
        if (RuleId <= 0) {
            return null;
        }
        MutexRuleInfo loop = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_MUTEX_RULE, null,
                ConfigCubeDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(RuleId)}, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            loop = new MutexRuleInfo();
            fillAllFieldsToLoop(cursor, loop);
        }
        cursor.close();
        return loop;
    }

    public synchronized List<MutexRuleInfo> getMutexRuleInfoAllList() {
        List<MutexRuleInfo> MutexRuleInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_MUTEX_RULE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == MutexRuleInfoList) {
                MutexRuleInfoList = new ArrayList<MutexRuleInfo>();
            }
            MutexRuleInfo loop = new MutexRuleInfo();
            fillAllFieldsToLoop(cursor, loop);
            MutexRuleInfoList.add(loop);
        }
        cursor.close();
        return MutexRuleInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, MutexRuleInfo loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mSwitchStatus = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MUTEXRULE_SWITCHSTATUS));
        loop.mName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MUTEXRULE_NAME));
        loop.mDescription = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MUTEXRULE_DESCRIPTION));
    }
}
