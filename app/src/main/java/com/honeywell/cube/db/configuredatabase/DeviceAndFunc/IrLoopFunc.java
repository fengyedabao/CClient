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
 * Created by H157925 on 16/4/11. 14:25
 * Email:Shodong.Sun@honeywell.com
 */
public class IrLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public IrLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    public synchronized long addIrLoop(IrLoop loop) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_LOOP_TYPE, loop.mLoopType).getValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public synchronized long addIrLoop(IrLoop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_LOOP_TYPE, loop.mLoopType).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    // delate all devIds Loop
    public synchronized int deleteIrLoopByDevId(long devId) {
        int num = -1;
        List<IrLoop> loops = getIrLoopByModulePrimaryId(devId);
        num = loops.size();
        if (loops == null || num == 0) {
            return -1;
        }
        for (int i = 0; i < num; i++) {
            deleteIrLoopByPrimaryId(loops.get(i).mLoopSelfPrimaryId);
        }
        return num;
    }

    /**
     * 查询 所有的IrLoop
     *
     * @return
     */
    public synchronized List<IrLoop> getIrLoopAll() {
        List<IrLoop> LoopList = new ArrayList<IrLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_BACNETLOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            IrLoop loop = fillDefaultIrLoop(cursor);
            LoopList.add(loop);
        }
        cursor.close();
        db.close();
        return LoopList;
    }

    private List<IrLoop> getIrLoopByModulePrimaryId(long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)}, null, null, null, null);
        List<IrLoop> loops = new ArrayList<IrLoop>();
        while (cursor.moveToNext()) {
            IrLoop loop = fillDefaultIrLoop(cursor);
            loops.add(loop);
        }
        cursor.close();
        db.close();
        return loops;
    }

    public synchronized int deleteIrLoopByPrimaryId(long primaryId) {
        int num = -1;
        try {

            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IR_LOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            Util.deleteLoopFromScenarios(dbHelper, primaryId, CommonData.MODULE_TYPE_WIFIIR);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    // 主要根据devId，gatewayId，loopId更新记录，subGatewayID可选（只针对三菱空调有用）
    public synchronized int updateIrLoop(IrLoop loop) {
        if (null == loop || loop.mModulePrimaryId <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (loop.mLoopName != null && !loop.mLoopName.equals("")) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName);
        }
        values.put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId);
        try {

            num = db.update(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, values,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(loop.mModulePrimaryId), String.valueOf(loop.mLoopSelfPrimaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.close();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized IrLoop getIrLoop(long _id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(_id)},
                null, null, null, null);
        IrLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultIrLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    public synchronized IrLoop getIrLoop(long devId, String loopName, String roomName) {
        if (CommonUtils.ISNULL(loopName) || CommonUtils.ISNULL(roomName)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_LOOPNAME + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{loopName, roomName, String.valueOf(devId)},
                null, null, null, null);
        IrLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultIrLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    public synchronized IrLoop getIrLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)},
                null, null, null, null);
        IrLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultIrLoop(cursor);
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
    public synchronized ArrayList<IrLoop> getIrLoopByRoom(int room) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{""+room},
                null, null, null, null);
        ArrayList<IrLoop> returnValue = new ArrayList<>();
        while (cursor.moveToNext()) {
            IrLoop loop = fillDefaultIrLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        db.close();
        return returnValue;
    }

    public synchronized IrLoop getIrLoop(int devId, int _id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(devId), String.valueOf(_id)},
                null, null, null, null);

        IrLoop loop = null;
        while (cursor.moveToNext()) {
            loop = fillDefaultIrLoop(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    public synchronized List<IrLoop> getIrLoopListByDevId(long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<IrLoop> irLoopList = new ArrayList<IrLoop>();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)},
                null, null, null, null);

        while (cursor.moveToNext()) {
            irLoopList.add(fillDefaultIrLoop(cursor));
        }
        cursor.close();
        db.close();
        return irLoopList;
    }

    public synchronized List<IrLoop> getIrLoopListByLoopType(String loopType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<IrLoop> irLoopList = new ArrayList<IrLoop>();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_IR_LOOP_TYPE + "=?",
                new String[]{loopType},
                null, null, null, null);

        while (cursor.moveToNext()) {
            irLoopList.add(fillDefaultIrLoop(cursor));
        }
        cursor.close();
        db.close();
        return irLoopList;
    }


    private IrLoop fillDefaultIrLoop(Cursor cursor) {
        IrLoop loop = null;
        if (null == cursor) {
            return loop;
        }
        loop = new IrLoop();
        loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.mLoopType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_LOOP_TYPE));
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = CommonData.MODULE_TYPE_WIFIIR;
        return loop;
    }


}
