package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.utils.Loger.Loger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 15:50
 * Email:Shodong.Sun@honeywell.com
 */
public class RoomLoopFunc {
    private ConfigCubeDatabaseHelper dbHelper = null;
    private static RoomLoopFunc mRoomLoopFuc = null;

    public static synchronized RoomLoopFunc getInstance(Context context) {

        if (null == mRoomLoopFuc) {
            mRoomLoopFuc = new RoomLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        }
        return mRoomLoopFuc;
    }

    private RoomLoopFunc(ConfigCubeDatabaseHelper instance) {
        this.dbHelper = instance;
    }

    public synchronized long addRoomLoop(RoomLoop loop) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_ROOMNAME, loop.mRoomName)
                .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_IMAGENAME, loop.mImageName).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_ROOM, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return rowId;
    }

    public synchronized long addRoomLoop(RoomLoop loop, SQLiteDatabase db) {
        long rowId = -1;
        if (loop == null) {
            return rowId;
        }
        ContentValues values = new ContentValuesFactory()
                .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                .put(ConfigCubeDatabaseHelper.COLUMN_ROOMNAME, loop.mRoomName)
                .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_IMAGENAME, loop.mImageName).getValues();
        rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_ROOM, null, values);
        return rowId;
    }

    public synchronized long addRoomList(List<RoomLoop> loops) {
        long rowId = -1;
        if (loops == null || loops.isEmpty()) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        try {
            for (RoomLoop loop : loops) {
                ContentValues values = new ContentValuesFactory()
                        .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mPrimaryId)
                        .put(ConfigCubeDatabaseHelper.COLUMN_ROOMNAME, loop.mRoomName)
                        .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_IMAGENAME, loop.mImageName).getValues();
                rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_ROOM, null, values);
            }
            db.setTransactionSuccessful();//由事务的标志决定是提交事务，还是回滚事务
        } finally {
            db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
            db.close();
        }
        return rowId;
    }

    /**
     * 通过PRIMARY——ID删除
     *
     * @param primaryId
     * @return
     */
    public synchronized int deleteRoomByPrimaryId(long primaryId) {
        int num = 0;
        if (primaryId <= 0) {
            return num;
        }
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            num = db.delete(ConfigCubeDatabaseHelper.TABLE_ROOM,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 通过房间名称删除
     *
     * @param roomName
     * @return
     */
    public synchronized int deleteRoomLoopByRoomName(String roomName) {
        int num = 0;
        if (CommonUtils.ISNULL(roomName)) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_ROOM,
                    ConfigCubeDatabaseHelper.COLUMN_ROOMNAME + "=?",
                    new String[]{roomName});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 通过PrimaryId 更新
     *
     * @param primaryId
     * @param loop
     * @return
     */
    public synchronized int updateRoomLoopByPrimaryId(long primaryId, RoomLoop loop) {
        if (null == loop || primaryId <= 0) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(loop.mRoomName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_ROOMNAME, loop.mRoomName);
        }
        if (!CommonUtils.ISNULL(loop.mImageName)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_ROOM_IMAGENAME, loop.mImageName);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_ROOM, values,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.close();
        }
        return num;
    }

    /**
     * 通过PrimaryId 获取
     *
     * @param primaryId
     * @return
     */
    public synchronized RoomLoop getRoomLoopByPrimaryId(long primaryId) {
        if (primaryId <= 0) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_ROOM, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null,
                null, null, null);

        RoomLoop loop = null;
        while (cursor.moveToNext()) {
            loop = new RoomLoop();
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    public synchronized RoomLoop getRoomLoopByRoomName(String roomName) {
        if (CommonUtils.ISNULL(roomName)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ConfigCubeDatabaseHelper.TABLE_ROOM, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOMNAME + "=?",
                new String[]{roomName}, null,
                null, null, null);

        RoomLoop loop = null;
        while (cursor.moveToNext()) {
            loop = new RoomLoop();
            fillAllFieldsToLoop(cursor, loop);
            break;
        }
        cursor.close();
        return loop;
    }

    //查询(所有列表)
    public synchronized List<RoomLoop> getRoomLoopAllList() {
        List<RoomLoop> RoomLoopList = new ArrayList<RoomLoop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_ROOM, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            RoomLoop loop = new RoomLoop();
            fillAllFieldsToLoop(cursor, loop);
            RoomLoopList.add(loop);
        }
        cursor.close();
        return RoomLoopList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor, RoomLoop loop) {
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mPrimaryId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mRoomName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOMNAME));
        loop.mImageName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_IMAGENAME));
    }
}
