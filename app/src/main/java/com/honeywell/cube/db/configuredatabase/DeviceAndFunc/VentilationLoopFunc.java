package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/8/2. 11:00
 * Email:Shodong.Sun@honeywell.com
 */
public class VentilationLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public VentilationLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //add ventilation loop
    public synchronized long addVentilationLoop(VentilationLoop loop) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CONTROLTYPE, loop.controltype)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_POWER, loop.power)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_FANSPEED, loop.fanspeed)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CYCLETYPE, loop.cycletype)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_HUMIDITY, loop.humidity)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_DEHUMIDITY, loop.dehumidity).getValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_VENTILATIONLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public synchronized long addVentilationLoop(VentilationLoop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CONTROLTYPE, loop.controltype)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_POWER, loop.power)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_FANSPEED, loop.fanspeed)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CYCLETYPE, loop.cycletype)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_HUMIDITY, loop.humidity)
                    .put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_DEHUMIDITY, loop.dehumidity).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_VENTILATIONLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    //delete
    public synchronized int deleteVentilationLoopByPrimaryId(long primaryId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_VENTILATIONLOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public synchronized VentilationLoop getVentilationLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_VENTILATIONLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);
        VentilationLoop loop = null;
        while (cursor.moveToNext()) {
            loop = getDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    //获取所有的 ventilationLoop list
    public synchronized ArrayList<VentilationLoop> getVentilationLoopAllList() {
        ArrayList<VentilationLoop> ventilationLoops = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_VENTILATIONLOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            VentilationLoop loop = getDefaultLoop(cursor);
            ventilationLoops.add(loop);
        }
        cursor.close();
        return ventilationLoops;
    }

    public synchronized ArrayList<VentilationLoop> getVentilationLoopByRoomId(int roomid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_VENTILATIONLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{"" + roomid}, null, null, null, null);

        ArrayList<VentilationLoop> returnValue = new ArrayList<>();
        while (cursor.moveToNext()) {
            VentilationLoop loop = getDefaultLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        return returnValue;
    }

    //update the ventilation loop
    public synchronized int updateVentilationLoopByPrimary(long primaryId, VentilationLoop loop) {
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
        if (!CommonUtils.ISNULL(loop.controltype)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CONTROLTYPE, loop.controltype);
        }
        if (!CommonUtils.ISNULL(loop.power)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_POWER, loop.power);
        }
        if (!CommonUtils.ISNULL(loop.fanspeed)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_FANSPEED, loop.fanspeed);
        }
        if (!CommonUtils.ISNULL(loop.cycletype)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CYCLETYPE, loop.cycletype);
        }
        if (!CommonUtils.ISNULL(loop.humidity)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_HUMIDITY, loop.humidity);
        }
        if (!CommonUtils.ISNULL(loop.dehumidity)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_DEHUMIDITY, loop.dehumidity);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_VENTILATIONLOOP, values,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
//        if (num > 0) {
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    private VentilationLoop getDefaultLoop(Cursor cursor) {
        VentilationLoop loop = new VentilationLoop();
        if (null == cursor) {
            return loop;
        }
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.controltype = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CONTROLTYPE));
        loop.power = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_POWER));
        loop.fanspeed = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_FANSPEED));
        loop.cycletype = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_CYCLETYPE));
        loop.humidity = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_HUMIDITY));
        loop.dehumidity = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_VENTILATION_DEHUMIDITY));
        return loop;
    }

}
