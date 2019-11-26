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
 * Created by H157925 on 16/4/11. 10:35
 * Email:Shodong.Sun@honeywell.com
 */
public class BacnetLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public BacnetLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //增加
    public synchronized long addBacnetLoop(BacnetLoop loop) throws SQLException {
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACNET_SUBGATEWAYID, loop.mSubDevId).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return rowId;
    }

    public synchronized long addBacnetLoop(BacnetLoop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_BACNET_SUBGATEWAYID, loop.mSubDevId).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    // delate all devIds Loop
    public synchronized int deleteBacnetLoopByDevId(long devId) {
        int num = -1;
        List<BacnetLoop> loops = getBacnetLoopByModulePrimaryId(devId);
        num = loops.size();
        if (loops == null || num == 0) {
            return -1;
        }
        for (int i = 0; i < num; i++) {
            deleteBacnetLoopByPrimaryId(loops.get(i).mLoopSelfPrimaryId);
        }
        return num;
    }

    private List<BacnetLoop> getBacnetLoopByModulePrimaryId(long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)}, null, null, null, null);
        List<BacnetLoop> loops = new ArrayList<BacnetLoop>();
        while (cursor.moveToNext()) {
            BacnetLoop loop = fillDefaultLoop(cursor);
            loops.add(loop);
        }
        cursor.close();
        db.close();
        return loops;
    }

    // delate all devIds Loop
    public synchronized int deleteBacnetLoopByPrimaryId(long primaryId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            Util.deleteLoopFromScenarios(dbHelper, primaryId, CommonData.MODULE_TYPE_BACNET);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    // 主要根据devId，gatewayId，loopId更新记录，subGatewayID可选（只针对三菱空调有用）
    public synchronized int updateBacnetLoop(BacnetLoop loop) {
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
            num = db.update(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, values,
                    ConfigCubeDatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(loop.mLoopSelfPrimaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        db.close();
        return num;
    }

    public synchronized BacnetLoop getBacnetLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);

        BacnetLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    public synchronized ArrayList<BacnetLoop> getBacnetLoopByRoom(int room) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{"" + room}, null, null, null, null);

        ArrayList<BacnetLoop> returnValue = new ArrayList<>();
        while (cursor.moveToNext()) {
            BacnetLoop loop = fillDefaultLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        db.close();
        return returnValue;
    }

    private BacnetLoop fillDefaultLoop(Cursor cursor) {
        BacnetLoop loop = new BacnetLoop();
        if (null == cursor) {
            return loop;
        }
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.mLoopId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPID));
        loop.mSubDevId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACNET_SUBGATEWAYID));
        loop.mModuleType = CommonData.MODULE_TYPE_BACNET;
        return loop;
    }

    // query the record according to devId，gatewayId，loopId更新记录，subGatewayID可选（只针对三菱空调有用）
    public synchronized BacnetLoop getBacnetLoop(long devId, int loopId, int subDevId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_BACNET_SUBGATEWAYID + "=?",
                new String[]{String.valueOf(devId),
                        String.valueOf(loopId), String.valueOf(subDevId)}, null, null, null, null);

        BacnetLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

//    public synchronized BacnetLoop getBacnetLoop(String roomName, int subGatewayId, int loopId) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        if (CommonUtils.ISNULL(roomName) || loopId <= 0) {
//            return null;
//        }
//        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null,
//                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=? and "
//                        + ConfigCubeDatabaseHelper.COLUMN_BACNET_SUBGATEWAYID + "=? and "
//                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
//                new String[]{roomName, String.valueOf(subGatewayId), String.valueOf(loopId)}, null, null, null, null);
//
//        BacnetLoop loop = null;
//        while (cursor.moveToNext()) {
//            loop = fillDefaultLoop(cursor);
//            break;
//        }
//        cursor.close();
//        return loop;
//    }

    //查询(所有列表)
    public synchronized List<BacnetLoop> getBacnetLoopAllList() {
        List<BacnetLoop> bacnetLoopList = new ArrayList<BacnetLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            BacnetLoop loop = new BacnetLoop();
            loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
            loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
            loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
            loop.mLoopId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPID));
            loop.mSubDevId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_BACNET_SUBGATEWAYID));
            loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
            bacnetLoopList.add(loop);
        }
        cursor.close();
        return bacnetLoopList;
    }
}
