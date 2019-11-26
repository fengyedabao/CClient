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
 * Created by H157925 on 16/4/12. 13:43
 * Email:Shodong.Sun@honeywell.com
 */
public class Wireless315M433MLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;

    public Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper instance) {
        this.dbHelper = instance;
        this.peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
    }

    public synchronized long addWireless315M433MLoop(
            Wireless315M433MLoop loop) throws SQLException {
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
                    .put(ConfigCubeDatabaseHelper.COLUMN_ALARMTIMER,
                            loop.mDelayTimer)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable)
                    .put(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICEID,
                            loop.mSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433SERIALNO,
                            loop.mSerialnumber)
                    .put(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICETYPE,
                            loop.mDeviceType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType)
                    .getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP,
                    null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }
    public synchronized long addWireless315M433MLoop(Wireless315M433MLoop loop, SQLiteDatabase db) throws SQLException {
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
                    .put(ConfigCubeDatabaseHelper.COLUMN_ALARMTIMER,
                            loop.mDelayTimer)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable)
                    .put(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICEID,
                            loop.mSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433SERIALNO,
                            loop.mSerialnumber)
                    .put(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICETYPE,
                            loop.mDeviceType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType)
                    .getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP,
                    null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    // delate all devIds Loop
    public synchronized int deleteWireless315M433MLoopByDevId(long devId) {
        int num = -1;
        List<Wireless315M433MLoop> loops = getWireless315M433MLoopByModulePrimaryId(devId);
        num = loops.size();
        if (loops == null || num == 0) {
            return -1;
        }
        for (int i = 0; i < num; i++) {
            deleteWireless315M433MLoopByPrimaryId(loops.get(i).mLoopSelfPrimaryId);
        }
        return num;
    }

    private List<Wireless315M433MLoop> getWireless315M433MLoopByModulePrimaryId(
            long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)}, null, null, null);
        List<Wireless315M433MLoop> loops = new ArrayList<Wireless315M433MLoop>();
        while (cursor.moveToNext()) {
            Wireless315M433MLoop loop = new Wireless315M433MLoop();
            fillAllFieldsToLoop(cursor, loop);
            loops.add(loop);
        }
        cursor.close();
        return loops;
    }

    public synchronized int deleteWireless315M433MLoopByPrimaryId(long primaryId) {
        int num = dbHelper.getWritableDatabase().delete(
                ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)});
        if (num > 0) {
            Util.deleteLoopFromScenarios(dbHelper, primaryId,
                    CommonData.MODULE_TYPE_WIFI315M433M);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    // 主要根据devId，gatewayId，loopId更新记录，subGatewayID可选（只针对三菱空调有用）
    public synchronized int updateWireless315M433M(
            Wireless315M433MLoop loop) {
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
        if (!CommonUtils.ISNULL(loop.mZoneType)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_ZONETYPE, loop.mZoneType);
        }
        if (loop.mDelayTimer >= 0) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_ALARMTIMER, loop.mDelayTimer);
        }
        if (loop.mIsEnable == CommonData.ARM_TYPE_DISABLE
                || loop.mIsEnable == CommonData.ARM_TYPE_ENABLE) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable);
        }
        /*
         * if(!CommonUtils.ISNULL(loop.mSerialnumber)){
         * values.put(ConfigDatabaseHelper.COLUMN_WIREDLESS315433SERIALNO,
         * loop.mSerialnumber); } if(!CommonUtils.ISNULL(loop.mDeviceType)){
         * values.put(ConfigDatabaseHelper.COLUMN_WIREDLESS315433DEVICETYPE,
         * loop.mDeviceType); }
         */
        if (loop.mLoopType >= 0) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType);
        }

        num = db.update(ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP,
                values, ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(loop.mLoopSelfPrimaryId)});

//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        db.close();
        return num;
    }

    public synchronized Wireless315M433MLoop getWireless315M433MLoopByPrimaryId(
            long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null);
        Wireless315M433MLoop loop = new Wireless315M433MLoop();
        while (cursor.moveToNext()) {
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    /**
     * 根据room查询
     *
     * @return
     */
    public synchronized ArrayList<Wireless315M433MLoop> getWireless315M433MByRoom(
            int roomid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{""+roomid}, null, null, null);
        ArrayList<Wireless315M433MLoop> arrayList = new ArrayList<Wireless315M433MLoop>();
        while (cursor.moveToNext()) {
            Wireless315M433MLoop loop = new Wireless315M433MLoop();
            fillAllFieldsToLoop(cursor, loop);
            arrayList.add(loop);
        }
        cursor.close();
        return arrayList;
    }


    public synchronized ArrayList<Wireless315M433MLoop> getWireless315M433MByLoopLoopType(
            long loopType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE + "=?",
                new String[]{String.valueOf(loopType)}, null, null, null);
        ArrayList<Wireless315M433MLoop> arrayList = new ArrayList<Wireless315M433MLoop>();
        while (cursor.moveToNext()) {
            Wireless315M433MLoop loop = new Wireless315M433MLoop();
            fillAllFieldsToLoop(cursor, loop);
            arrayList.add(loop);
        }
        cursor.close();
        return arrayList;
    }

    public synchronized ArrayList<Wireless315M433MLoop> getWireless315M433MByTypes(
            long loopType1, long loopType2) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE + " in (?,?)",
                new String[]{String.valueOf(loopType1), String.valueOf(loopType2)}, null, null, null);
        ArrayList<Wireless315M433MLoop> arrayList = new ArrayList<Wireless315M433MLoop>();
        while (cursor.moveToNext()) {
            Wireless315M433MLoop loop = new Wireless315M433MLoop();
            fillAllFieldsToLoop(cursor, loop);
            arrayList.add(loop);
        }
        cursor.close();
        return arrayList;
    }

    // maia的时候，deviceid > 0 ,sensor <= 0
    public synchronized Wireless315M433MLoop getWireless315M433MLoop(
            long devId, int subDevId, int loopId, String deviceType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (CommonUtils.ISNULL(deviceType)) {
            return null;
        }
        Cursor cursor = null;
        if (deviceType.equals(CommonData.DEVICETYPE_MAIA2)) {
            cursor = db.query(ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICEID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                    new String[]{
                            String.valueOf(devId), String.valueOf(subDevId), String.valueOf(loopId)
                    }, null, null, null);
        } else {
            cursor = db.query(
                    ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP,
                    null,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                    new String[]{String.valueOf(devId),
                            String.valueOf(loopId)}, null, null, null, null);
        }
        Wireless315M433MLoop loop = null;
        while (cursor.moveToNext()) {
            loop = new Wireless315M433MLoop();
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

//    public List<BasicLoop> getBasicLoopAllList(boolean bSensor) {
//        List<BasicLoop> loops = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db
//                .query(ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
//                        null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID
//                                + " asc", null);
//        while (cursor.moveToNext()) {
//            if (null == loops) {
//                loops = new ArrayList<BasicLoop>();
//            }
//            String devType = cursor
//                    .getString(cursor
//                            .getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICETYPE));
//            if (bSensor && !devType.equals(CommonData.DEVICETYPE_SENSOR)) {
//                continue;
//            }
//            Wireless315M433MLoop loop = new Wireless315M433MLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            loops.add(loop);
//        }
//        cursor.close();
//        return loops;
//    }

    // 查询(所有列表)
//    public synchronized List<Wireless315M433MLoop> getWireless315M433MLoopAllList() {
//        List<Wireless315M433MLoop> wireless315M433MLoopList = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db
//                .query(ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
//                        null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID
//                                + " asc", null);
//        while (cursor.moveToNext()) {
//            if (null == wireless315M433MLoopList) {
//                wireless315M433MLoopList = new ArrayList<Wireless315M433MLoop>();
//            }
//            Wireless315M433MLoop loop = new Wireless315M433MLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            wireless315M433MLoopList.add(loop);
//        }
//        cursor.close();
//        return wireless315M433MLoopList;
//    }

    // 查询(所有列表)
//    public synchronized List<Wireless315M433MLoop> getWireless315M433MLoopListByDevId(long devId) {
//        List<Wireless315M433MLoop> wireless315M433MLoopList = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(
//                ConfigCubeDatabaseHelper.TABLE_WIRELESS315M433MLOOP, null,
//                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
//                new String[]{String.valueOf(devId)}, null, null,
//                ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
//        while (cursor.moveToNext()) {
//            if (null == wireless315M433MLoopList) {
//                wireless315M433MLoopList = new ArrayList<Wireless315M433MLoop>();
//            }
//            Wireless315M433MLoop loop = new Wireless315M433MLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            wireless315M433MLoopList.add(loop);
//        }
//        cursor.close();
//        return wireless315M433MLoopList;
//    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor,
                                                  Wireless315M433MLoop loop) {
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
        loop.mSerialnumber = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433SERIALNO));
        loop.mSubDevId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICEID));
        loop.mDeviceType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_WIREDLESS315433DEVICETYPE));
        loop.mLoopType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE));
        loop.mModuleType = CommonData.MODULE_TYPE_WIFI315M433M;
    }
}
