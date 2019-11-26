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
 * Created by H157925 on 16/4/11. 10:18
 * Email:Shodong.Sun@honeywell.com
 */
public class BackaudioLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public BackaudioLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    public synchronized long addBackaudioLoop(BackaudioLoop loop) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId).getValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rowId > 0) {
            Util.putPrimaryIdAndModuleType(loop, rowId, loop.mModulePrimaryId, CommonData.MODULE_TYPE_BACKAUDIO);
            Util.addLoopToDefaultScenario(dbHelper, loop, false);
        }
        return rowId;
    }

    public synchronized long addBackaudioLoop(BackaudioLoop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rowId > 0) {
            Util.putPrimaryIdAndModuleType(loop, rowId, loop.mModulePrimaryId, CommonData.MODULE_TYPE_BACKAUDIO);
            Util.addLoopToDefaultScenario(dbHelper, loop, false);
        }
        return rowId;
    }

    public synchronized int deleteBackaudioLoopByDevId(long devId) {
        int num = -1;
        List<BackaudioLoop> loops = getBackaudioLoopByModulePrimaryId(devId);
        num = loops.size();
        if (loops == null || num == 0) {
            return -1;
        }
        for (int i = 0; i < num; i++) {
            deleteBackaudioLoopByPrimaryId(loops.get(i).mLoopSelfPrimaryId);
        }
        return num;
    }

    public synchronized int deleteBackaudioLoopByPrimaryId(long primaryId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        Util.deleteLoopFromScenarios(dbHelper, primaryId, CommonData.MODULE_TYPE_BACKAUDIO);
        return num;
    }

    //主要根据devId，gatewayId，loopId删除记录，subGatewayID可选（只针对三菱空调有用）

    public synchronized int updateBackaudioLoop(BackaudioLoop loop) {
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
        num = db.update(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, values,
                ConfigCubeDatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(loop.mLoopSelfPrimaryId)});
        db.close();
        return num;
    }

    public synchronized BackaudioLoop getBackaudioLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);
        BackaudioLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    /**
     * 通过房间名称获取数据
     *
     * @param room
     * @return
     */
    public synchronized ArrayList<BackaudioLoop> getBackaudioLoopByRoom(int room) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{"" + room}, null, null, null, null);

        ArrayList<BackaudioLoop> returnValue = new ArrayList<>();
        while (cursor.moveToNext()) {
            BackaudioLoop loop = fillDefaultLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        db.close();
        return returnValue;
    }

    public synchronized List<BackaudioLoop> getBackaudioLoopByModulePrimaryId(long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)}, null, null, null, null);
        List<BackaudioLoop> loops = new ArrayList<BackaudioLoop>();
        while (cursor.moveToNext()) {
            BackaudioLoop loop = fillDefaultLoop(cursor);
            loops.add(loop);
        }
        cursor.close();
        db.close();
        return loops;
    }

    public synchronized BackaudioLoop getBackaudioLoop(long devId, int loopId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                new String[]{String.valueOf(devId), String.valueOf(loopId)}, null, null, null, null);
        BackaudioLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    private BackaudioLoop fillDefaultLoop(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
        BackaudioLoop loop = new BackaudioLoop();
        loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.mLoopId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPID));
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = CommonData.MODULE_TYPE_BACKAUDIO;
        return loop;
    }

    //查询(所有列表)
    public synchronized List<BackaudioLoop> getBackaudioLoopAllList() {
        List<BackaudioLoop> backaudioLoopList = new ArrayList<BackaudioLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACKAUDIO_LOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            BackaudioLoop loop = fillDefaultLoop(cursor);
            if (null != loop) {
                backaudioLoopList.add(loop);
            }

        }
        cursor.close();
        return backaudioLoopList;
    }


}
