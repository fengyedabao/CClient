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
 * Created by H157925 on 16/4/11. 11:23
 * Email:Shodong.Sun@honeywell.com
 */
public class IpvdpZoneLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;

    public IpvdpZoneLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
        this.peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
    }

    public synchronized long addIpvdpZoneLoop(IpvdpZoneLoop loop) throws SQLException {
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
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addIpvdpZoneLoop(IpvdpZoneLoop loop, SQLiteDatabase db) throws SQLException {
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
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    // delate all devIds Loop
//    public synchronized int deleteIpvdpZoneLoopByDevId(long devId) {
//        int num = -1;
//        List<IpvdpZoneLoop> loops = getIpvdpZoneLoopByModulePrimaryId(devId);
//        num = loops.size();
//        if (loops == null || num == 0) {
//            return -1;
//        }
//        for (int i = 0; i < num; i++) {
//            deleteIpvdpZoneLoopByPrimaryId(loops.get(i).mLoopSelfPrimaryId);
//        }
//        return num;
//    }

//    private List<IpvdpZoneLoop> getIpvdpZoneLoopByModulePrimaryId(long devId) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null,
//                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
//                new String[]{String.valueOf(devId)}, null, null, null, null);
//
//        List<IpvdpZoneLoop> loops = new ArrayList<IpvdpZoneLoop>();
//        while (cursor.moveToNext()) {
//            IpvdpZoneLoop loop = new IpvdpZoneLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            loops.add(loop);
//        }
//        cursor.close();
//        return loops;
//    }

    public synchronized int deleteIpvdpZoneLoopByPrimaryId(long primaryId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            Util.deleteLoopFromScenarios(dbHelper, primaryId, CommonData.MODULE_TYPE_IPVDP);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    /**
     * 更新IPVDPzone type
     *
     * @param loop
     * @return
     */
    public synchronized int updateIpvdpZoneLoop(IpvdpZoneLoop loop) {
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
            num = db.update(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, values,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(loop.mLoopSelfPrimaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return num;
    }

    public synchronized IpvdpZoneLoop getIpvdpZoneLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);

        IpvdpZoneLoop loop = null;
        while (cursor.moveToNext()) {
            loop = new IpvdpZoneLoop();
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    /**
     * 通过房间名称获取列表
     *
     * @param room
     * @return
     */
    public synchronized ArrayList<IpvdpZoneLoop> getIpvdpZoneLoopByRoom(int room) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<IpvdpZoneLoop> returnValue = new ArrayList<>();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{"" + room}, null, null, null, null);

        while (cursor.moveToNext()) {
            IpvdpZoneLoop loop = new IpvdpZoneLoop();
            fillAllFieldsToLoop(cursor, loop);
            returnValue.add(loop);
        }
        cursor.close();
        return returnValue;
    }

//    public synchronized IpvdpZoneLoop getIpvdpZoneLoop(long devId, int loopId) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null,
//                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
//                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
//                new String[]{String.valueOf(devId), String.valueOf(loopId)}, null, null, null, null);
//
//        IpvdpZoneLoop loop = null;
//        while (cursor.moveToNext()) {
//            loop = new IpvdpZoneLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            break;
//        }
//        cursor.close();
//        return loop;
//    }

    //查询(所有列表)
    public synchronized List<IpvdpZoneLoop> getIpvdpZoneLoopAllList() {
        List<IpvdpZoneLoop> IpvdpZoneLoopList = new ArrayList<IpvdpZoneLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            IpvdpZoneLoop loop = new IpvdpZoneLoop();
            fillAllFieldsToLoop(cursor, loop);
            IpvdpZoneLoopList.add(loop);
        }
        cursor.close();
        return IpvdpZoneLoopList;
    }

    //查询(所有列表)
//    public synchronized List<BasicLoop> getBasicLoopAllList() {
//        List<BasicLoop> IpvdpZoneLoopList = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
//        while (cursor.moveToNext()) {
//            if (null == IpvdpZoneLoopList) {
//                IpvdpZoneLoopList = new ArrayList<BasicLoop>();
//            }
//            IpvdpZoneLoop loop = new IpvdpZoneLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            IpvdpZoneLoopList.add(loop);
//        }
//        cursor.close();
//        return IpvdpZoneLoopList;
//    }

    //查询(所有列表)
//    public synchronized List<IpvdpZoneLoop> getIpvdpZoneLoopListByDevId(long devId) {
//        List<IpvdpZoneLoop> IpvdpZoneLoopList = null;
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IPVDPZONELOOP, null,
//                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?", new String[]{String.valueOf(devId)}, null, null,
//                ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
//        while (cursor.moveToNext()) {
//            if (null == IpvdpZoneLoopList) {
//                IpvdpZoneLoopList = new ArrayList<IpvdpZoneLoop>();
//            }
//            IpvdpZoneLoop loop = new IpvdpZoneLoop();
//            fillAllFieldsToLoop(cursor, loop);
//            IpvdpZoneLoopList.add(loop);
//        }
//        cursor.close();
//        return IpvdpZoneLoopList;
//    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, IpvdpZoneLoop loop) {
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
        loop.mModuleType = CommonData.MODULE_TYPE_IPVDP;
    }


}
