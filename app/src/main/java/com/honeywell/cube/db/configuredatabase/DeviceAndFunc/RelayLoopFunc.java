package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.Util;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 15:39
 * Email:Shodong.Sun@honeywell.com
 */
public class RelayLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public RelayLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    public synchronized long addRelayLoop(RelayLoop loop) throws SQLException {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        if (loop.mTriggerTime < 0) {
            loop.mTriggerTime = 0;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_REALYLOOP_TRIGGERTIME, loop.mTriggerTime).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addRelayLoop(RelayLoop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        if (loop.mTriggerTime < 0) {
            loop.mTriggerTime = 0;
        }
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_REALYLOOP_TRIGGERTIME, loop.mTriggerTime).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    /**
     * 通过device_id获取数据
     *
     * @param devId
     * @return
     */
    private List<RelayLoop> getRelayLoopByModulePrimaryId(long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?", new String[]{String.valueOf(devId)}, null, null, null, null);
        List<RelayLoop> loops = new ArrayList<RelayLoop>();
        while (cursor.moveToNext()) {
            RelayLoop loop = fillDefaultLoop(cursor);
            loops.add(loop);
        }
        cursor.close();
        return loops;
    }

    /**
     * 通过PrimaryId来获取数据
     *
     * @param primaryId
     * @return
     */
    public synchronized int deleteRelayLoopByPrimaryId(long primaryId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            Util.deleteLoopFromScenarios(dbHelper, primaryId, CommonData.MODULE_TYPE_WIFIRELAY);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    /**
     * 通过 device ID 删除 Relay loop
     *
     * @param devId
     * @return
     */
    public synchronized int deleteRelayLoopByDevID(long devId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                    new String[]{String.valueOf(devId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    // update the record fields according to maskId,loopType and loopId
    public synchronized int updateRelayLoopByPrimaryId(long primaryId, RelayLoop loop) {
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
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP, values,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return num;
    }

    /**
     * 通过primryId 获取数据
     *
     * @param primaryId
     * @return
     */
    public synchronized RelayLoop getRelayLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);
        RelayLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    /**
     * 通过房间名称获取Relay
     *
     * @param room
     * @return
     */

    public synchronized ArrayList<RelayLoop> getRelayLoopByRoom(int room) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<RelayLoop> returnValue = new ArrayList<>();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{"" + room}, null, null, null, null);
        while (cursor.moveToNext()) {
            RelayLoop loop = fillDefaultLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        db.close();
        return returnValue;
    }

    private RelayLoop fillDefaultLoop(Cursor cursor) {
        RelayLoop loop = new RelayLoop();
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.mLoopId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPID));
        ;
        loop.mTriggerTime = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_REALYLOOP_TRIGGERTIME));
        ;
        loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = CommonData.MODULE_TYPE_WIFIRELAY;
        return loop;
    }

    /**
     * 获取primaryId 和 device_id相符合的参数
     *
     * @param devId
     * @param loopId
     * @return
     */
    public synchronized RelayLoop getRelayLoopByPrimaryIdAndLoopID(long devId, int loopId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP,
                null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                new String[]{String.valueOf(devId), String.valueOf(loopId)}, null, null, null, null);
        RelayLoop loop = null;

        while (cursor.moveToNext()) {
            loop = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    //查询(所有列表)
    public synchronized List<RelayLoop> getRelayLoopAllList() {
        List<RelayLoop> RelayLoopList = new ArrayList<RelayLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_RELAYLOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);

        while (cursor.moveToNext()) {
            RelayLoopList.add(fillDefaultLoop(cursor));
        }
        cursor.close();
        return RelayLoopList;
    }
}
