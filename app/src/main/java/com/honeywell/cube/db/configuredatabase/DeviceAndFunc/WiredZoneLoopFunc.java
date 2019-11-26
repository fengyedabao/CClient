package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.Util;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/12. 13:39
 * Email:Shodong.Sun@honeywell.com
 */
public class WiredZoneLoopFunc {

    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;

    public WiredZoneLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
        this.peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
    }

    //增
    public synchronized long addWiredZoneLoop(WiredZoneLoop loop) throws SQLException {
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ZONETYPE, loop.mZoneType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ALARMTYPE, loop.mAlarmType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ALARMTIMER, loop.mDelayTimer)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addWiredZoneLoop(WiredZoneLoop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ZONETYPE, loop.mZoneType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ALARMTYPE, loop.mAlarmType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ALARMTIMER, loop.mDelayTimer)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    // delate all devIds Loop
    public synchronized int deleteWiredZoneLoopByDevId(long devId) {
        int num = -1;
        List<WiredZoneLoop> loops = getWiredZoneLoopByModulePrimaryId(devId);
        num = loops.size();
        if (loops == null || num == 0) {
            return -1;
        }
        for (int i = 0; i < num; i++) {
            deleteWiredZoneLoopByPrimaryId(loops.get(i).mLoopSelfPrimaryId);
        }
        return num;
    }

    private List<WiredZoneLoop> getWiredZoneLoopByModulePrimaryId(long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP,
                null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)}, null, null, null, null);

        List<WiredZoneLoop> loops = new ArrayList<WiredZoneLoop>();
        while (cursor.moveToNext()) {
            WiredZoneLoop loop = new WiredZoneLoop();
            fillAllFieldsToLoop(cursor, loop);
        }
        cursor.close();
        return loops;
    }

    public synchronized int deleteWiredZoneLoopByPrimaryId(long primaryId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将设备从Scenario中清除
        if (num > 0) {
            Util.deleteLoopFromScenarios(dbHelper, primaryId, CommonData.MODULE_TYPE_WIREDZONE);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    // 主要根据devId，gatewayId，loopId更新记录，subGatewayID可选（只针对三菱空调有用）
    public synchronized int updateWiredZoneLoopByPrimaryId(long primaryId, WiredZoneLoop loop) {
        if (null == loop) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(loop.mLoopName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName);
        }
        values.put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId);
        if (loop.mIsEnable == CommonData.ARM_TYPE_DISABLE || loop.mIsEnable == CommonData.ARM_TYPE_ENABLE) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP, values,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
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

    public synchronized WiredZoneLoop getWiredZoneLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP,
                null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);

        WiredZoneLoop loop = null;
        while (cursor.moveToNext()) {
            loop = new WiredZoneLoop();
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    /**
     * 根据房间名称查询
     *
     * @param roomid
     * @return
     */
    public synchronized ArrayList<WiredZoneLoop> getWiredZoneLoopByRoom(int roomid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<WiredZoneLoop> returnValue = new ArrayList<>();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP,
                null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{"" + roomid}, null, null, null, null);

        while (cursor.moveToNext()) {
            WiredZoneLoop loop = new WiredZoneLoop();
            fillAllFieldsToLoop(cursor, loop);
            returnValue.add(loop);
        }
        cursor.close();
        return returnValue;
    }


    // query the record according to devId，gatewayId，loopId更新记录，subGatewayID可选（只针对三菱空调有用）
//    public synchronized WiredZoneLoop getWiredZoneLoop(long devId, int loopId) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP,
//                null,
//                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
//                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
//                new String[]{String.valueOf(devId), String.valueOf(loopId)}, null, null, null, null);
//
//        WiredZoneLoop loop = null;
//        while (cursor.moveToNext()) {
//            loop = new WiredZoneLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            break;
//        }
//        cursor.close();
//        return loop;
//    }

    //查询(所有列表)
    public synchronized List<WiredZoneLoop> getWiredZoneLoopAllList() {
        List<WiredZoneLoop> WiredZoneLoopList = new ArrayList<WiredZoneLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            WiredZoneLoop loop = new WiredZoneLoop();
            fillAllFieldsToLoop(cursor, loop);
            WiredZoneLoopList.add(loop);
        }
        cursor.close();
        return WiredZoneLoopList;
    }

    //查询(所有列表)
//    public synchronized List<BasicLoop> getBasicLoopAllList() {
//        List<BasicLoop> wiredZoneLoopList = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
//        while (cursor.moveToNext()) {
//            if (null == wiredZoneLoopList) {
//                wiredZoneLoopList = new ArrayList<BasicLoop>();
//            }
//            WiredZoneLoop loop = new WiredZoneLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            wiredZoneLoopList.add(loop);
//        }
//        cursor.close();
//        return wiredZoneLoopList;
//    }

    //查询(所有列表)
//    public synchronized List<WiredZoneLoop> getWiredZoneLoopListByDevId(long devId) {
//        List<WiredZoneLoop> wiredZoneLoopList = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRDEDZONELOOP, null,
//                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?", new String[]{String.valueOf(devId)}, null, null,
//                ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
//        while (cursor.moveToNext()) {
//            if (null == wiredZoneLoopList) {
//                wiredZoneLoopList = new ArrayList<WiredZoneLoop>();
//            }
//            WiredZoneLoop loop = new WiredZoneLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            wiredZoneLoopList.add(loop);
//        }
//        cursor.close();
//        return wiredZoneLoopList;
//    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, WiredZoneLoop loop) {
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        PeripheralDevice device = peripheralDeviceFuc.getPeripheralByPrimaryId(loop.mModulePrimaryId);
        if (null != device) {
            loop.mIpAddr = device.mIpAddr;
            loop.mMacAddr = device.mMacAddr;
        }
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.mLoopId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPID));
        loop.mZoneType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ZONETYPE));
        loop.mAlarmType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ALARMTYPE));
        loop.mDelayTimer = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ALARMTIMER));
        loop.mIsEnable = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE));
        loop.mModuleType = CommonData.MODULE_TYPE_WIREDZONE;
    }
}
