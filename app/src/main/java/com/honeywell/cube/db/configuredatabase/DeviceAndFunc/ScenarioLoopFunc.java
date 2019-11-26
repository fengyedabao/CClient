package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.utils.Loger.Loger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 16:16
 * Email:Shodong.Sun@honeywell.com
 */
public class ScenarioLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;

    public ScenarioLoopFunc(ConfigCubeDatabaseHelper instance) {
        this.dbHelper = instance;
        this.peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }

    public synchronized long addScenarioLoop(ScenarioLoop loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mScenarioLoopPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID, loop.mScenarioId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME, loop.mScenarioName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE, loop.mModuleType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mDeviceLoopPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ACTIONINFO, loop.mActionInfo)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IS_ARM, loop.mIsArm)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME, loop.mImageName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_CLICKCOUNT, loop.mClickedCount).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return rowId;
    }

    public synchronized long addScenarioLoop4(ScenarioLoop loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }

        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mScenarioLoopPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID, loop.mScenarioId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME, loop.mScenarioName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE, loop.mModuleType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mDeviceLoopPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ACTIONINFO, loop.mActionInfo)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IS_ARM, loop.mIsArm)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME, loop.mImageName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_CLICKCOUNT, loop.mClickedCount).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null, values);
        } finally {
        }
        return rowId;
    }

    public static String addScenarioLoop2(ScenarioLoop loop) {
        if (loop == null) {
            return "";
        }

        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mScenarioLoopPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID, loop.mScenarioId)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME, loop.mScenarioName)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE, loop.mModuleType)
                .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mDeviceLoopPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ACTIONINFO, loop.mActionInfo)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IS_ARM, loop.mIsArm)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME, loop.mImageName)
                .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_CLICKCOUNT, loop.mClickedCount).getValues();
        return ConfigCubeDatabaseHelper.insertWithOnConflict(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null, values);
    }

    public synchronized void addScenarioLoop3(String sql) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL(sql);
        } finally {
            Loger.print("3333333", "add 02", Thread.currentThread());
        }

    }

    public synchronized long addScenarioLoopList(List<ScenarioLoop> loops, boolean isUpdateVer) {
        long rowId = -1;
        if (loops == null || loops.isEmpty()) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        try {
            for (ScenarioLoop loop : loops) {
                ContentValues values = new ContentValuesFactory()
                        .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mScenarioLoopPrimaryId)
                        .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID, loop.mScenarioId)
                        .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME, loop.mScenarioName)
                        .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE, loop.mModuleType)
                        .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mDeviceLoopPrimaryId)
                        .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ACTIONINFO, loop.mActionInfo)
                        .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IS_ARM, loop.mIsArm)
                        .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME, loop.mImageName)
                        .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_CLICKCOUNT, loop.mClickedCount).getValues();
                rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null, values);
            }
            db.setTransactionSuccessful();//由事务的标志决定是提交事务，还是回滚事务
        } finally {
            db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
//            if(rowId > 0 && isUpdateVer){
//                CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//                cubebaseFuc.updateConfigVer();
//            }
        }
        //todo：到时可能在添加场景的时候，版本号
        return rowId;
    }

    // delete  scenario_ids record!
    public synchronized int deleteScenarioLoopByScenarioId(int scenarioId) {
        int num = 0;
        if (scenarioId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS,
                    ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=?",
                    new String[]{String.valueOf(scenarioId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (num > 0) {
//            ScenarioIdsFunc.getInstance(dbHelper.mContext).deleteByScenarioId(scenarioId);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int deleteScenarioLoop(long devId, int moduleType) {
        int num = -1;
        if (devId <= 0 || moduleType <= 0) {
            return num;
        }

        num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE + "=?",
                new String[]{String.valueOf(devId), String.valueOf(moduleType)});
        return num;
    }

    public synchronized int deletScenarioLoopByPrimaryId(int primaryId) {
        Loger.print("test", "ssd primaryId " + primaryId, Thread.currentThread());
        int num = 0;
        if (primaryId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            ScenarioIdsFunc.getInstance(dbHelper.mContext).deleteByScenarioId(primaryId);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    //主要根据devId，devce_id，loopId删除记录，针对maia（loopid  一般是10以内的数字）
    public synchronized int deleteScenarioLoop(int scenarioId, long devId, int moduleType, boolean bUpdateVer) {
        int num = -1;
        if (scenarioId <= 0 || devId <= 0 || moduleType <= 0) {
            return num;
        }

        num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS,
                ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE + "=?",
                new String[]{String.valueOf(scenarioId), String.valueOf(devId), String.valueOf(moduleType)});
//        if (num > 0 && bUpdateVer) {
//            ScenarioIdsFunc.getInstance(dbHelper.mContext).deleteByScenarioId(scenarioId);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    // only can update:mScenarioName,mAction,mSubAction,mIsArm
    public synchronized int updateScenarioLoop(ScenarioLoop loop, boolean isLast) {
        if (null == loop || loop.mScenarioId <= 0 || loop.mDeviceLoopPrimaryId <= 0 || loop.mModuleType <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(loop.mScenarioName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME, loop.mScenarioName);
        }
        if (!CommonUtils.ISNULL(loop.mImageName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME, loop.mImageName);
        }
        if (!CommonUtils.ISNULL(loop.mActionInfo)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ACTIONINFO, loop.mActionInfo);
        }
        if (loop.mIsArm == CommonData.ARM_TYPE_DISABLE || loop.mIsArm == CommonData.ARM_TYPE_ENABLE) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IS_ARM, loop.mIsArm);
        }
        num = db.update(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, values,
                ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE + "=?",
                new String[]{String.valueOf(loop.mScenarioId), String.valueOf(loop.mDeviceLoopPrimaryId), String.valueOf(loop.mModuleType)});
//        if (num > 0 && isLast) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized ScenarioLoop getScenarioLoop(int scenarioId, long loopPrimaryId, int moduleType) {
        if (scenarioId <= 0 || loopPrimaryId <= 0 || moduleType <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null,
                ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE + "=?",
                new String[]{String.valueOf(scenarioId),
                        String.valueOf(loopPrimaryId), String.valueOf(moduleType)}, null,
                null, null, null);

        ScenarioLoop loop = null;
        while (cursor.moveToNext()) {
            loop = new ScenarioLoop();
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    //查询(所有列表)
    public synchronized List<ScenarioLoop> getScenarioLoopAllList() {
        List<ScenarioLoop> ScenarioLoopList = new ArrayList<ScenarioLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            ScenarioLoop loop = new ScenarioLoop();
            fillAllFieldsToLoop(cursor, loop);
            ScenarioLoopList.add(loop);
        }
        cursor.close();
        return ScenarioLoopList;
    }

    /**
     * 更新 Scenario 点击次数
     *
     * @param scenarioId
     * @param count
     * @return
     */
    public synchronized int updateScenarioClickCount(int scenarioId, int count) {
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_CLICKCOUNT, count);
        num = db.update(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, values,
                ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=?",
                new String[]{String.valueOf(scenarioId)});
        return num;
    }

    //查询(所有列表)
    public synchronized List<ScenarioLoop> getScenarioLoopListByScenarioId(int scenarioId) {
        if (scenarioId <= 0) {
            return null;
        }
        List<ScenarioLoop> scenarioLoopList = new ArrayList<ScenarioLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCENARIOSETTINGS, null,
                ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=?", new String[]{String.valueOf(scenarioId)}, null, null,
                ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            ScenarioLoop loop = new ScenarioLoop();
            fillAllFieldsToLoop(cursor, loop);
            scenarioLoopList.add(loop);
        }
        cursor.close();
        return scenarioLoopList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, ScenarioLoop loop) {
        loop.mScenarioLoopPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mScenarioId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID));
        loop.mScenarioName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME));
        loop.mModuleType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_MODULETYPE));
        loop.mDeviceLoopPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        loop.mActionInfo = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ACTIONINFO));
        loop.mIsArm = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IS_ARM));
        loop.mImageName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME));
        loop.mClickedCount = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_CLICKCOUNT));
    }
}
