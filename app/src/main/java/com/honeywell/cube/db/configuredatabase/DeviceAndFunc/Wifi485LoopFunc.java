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
 * Created by H157925 on 16/4/12. 13:32
 * Email:Shodong.Sun@honeywell.com
 */
public class Wifi485LoopFunc {
    ConfigCubeDatabaseHelper dbHelper= null;
    public Wifi485LoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }
    //增
    public synchronized long addWifi485Loop(Wifi485Loop loop) throws SQLException {
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_485_BRANDNAME, loop.mBrandName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_485_PORTID, loop.mPortId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_485_SLAVEADDR, loop.mSlaveAddr)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId).getValues();
            rowId =  db.insert(ConfigCubeDatabaseHelper.TABLE_485_LOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addWifi485Loop(Wifi485Loop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_485_BRANDNAME, loop.mBrandName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_485_PORTID, loop.mPortId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_485_SLAVEADDR, loop.mSlaveAddr)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId).getValues();
            rowId =  db.insert(ConfigCubeDatabaseHelper.TABLE_485_LOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    // when deleting PeripheralDevice record,we should delete the record of such dev_id
    public synchronized int deleteWifi485LoopByDevId(long devId) {
        int num = -1;
        List<Wifi485Loop> loops = getWifi485LoopByModulePrimaryId(devId);
        if(loops == null || num == 0 ){
            return -1;
        }
        num = loops.size();
        for (int i = 0; i < num; i++) {
            deleteWifi485LoopByPrimaryId(loops.get(i).mLoopSelfPrimaryId);
        }
        return num;
    }
    private List<Wifi485Loop> getWifi485LoopByModulePrimaryId(long devId) {
        if(devId <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP,null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[] { String.valueOf(devId)}, null, null, null, null);
        List<Wifi485Loop> loops = new ArrayList<Wifi485Loop>();
        while(cursor.moveToNext()){
            Wifi485Loop loop  = fillDefaultLoop(cursor);
            loops.add(loop);
        }
        cursor.close();
        return loops;
    }
    public synchronized int deleteWifi485LoopByPrimaryId(long primaryId) {
        int num = -1;
        if(primaryId <= 0){
            return num;
        }
        try {
            num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_485_LOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(num > 0){
            Util.deleteLoopFromScenarios(dbHelper,primaryId,CommonData.MODULE_TYPE_WIFI485);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
        }
        return num;
    }

    public synchronized int deleteWifi485LoopBySlaveAddr(int devId,int slaveAddr) {
        int num = -1;
        if(devId <= 0 || slaveAddr <= 0){
            return num;
        }
        try {
            num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_485_LOOP,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_485_SLAVEADDR + "=?",
                    new String[]{String.valueOf(devId), String.valueOf(slaveAddr)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }
    public synchronized int deleteWifi485Loop(int devId,int loopId) {
        int num = -1;
        if(devId <= 0 || loopId <= 0){
            return num;
        }
        try {
            num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_485_LOOP,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                    new String[]{String.valueOf(devId), String.valueOf(loopId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized int updateWifi485Loop(Wifi485Loop loop) {
        if(null == loop){
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(!CommonUtils.ISNULL(loop.mBrandName)){
            values.put(ConfigCubeDatabaseHelper.COLUMN_485_BRANDNAME, loop.mBrandName);
        }
        if(!CommonUtils.ISNULL(loop.mLoopName)){
            values.put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName);
        }
            values.put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId);
        try {
            num =  db.update(ConfigCubeDatabaseHelper.TABLE_485_LOOP, values,
                    ConfigCubeDatabaseHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(loop.mLoopSelfPrimaryId)});
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
    // query the record according to maskId,sub_dev_id and loopId
    public synchronized Wifi485Loop getWifi485LoopBySlaveAddr(int devId,int slaveAddr) {
        if(devId <= 0 || slaveAddr <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_485_SLAVEADDR + "=?",
                new String[]{String.valueOf(devId), String.valueOf(slaveAddr)}, null, null, null, null);
        Wifi485Loop loop = null;
        while(cursor.moveToNext()){
            loop  = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    // query the record according to maskId,sub_dev_id and loopId
    public synchronized Wifi485Loop getWifi485LoopByPrimaryId(long primaryId) {
        if(primaryId <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP,null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[] { String.valueOf(primaryId)}, null, null, null, null);
        Wifi485Loop loop = null;
        while(cursor.moveToNext()){
            loop  = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }
    // query the record according to maskId,sub_dev_id and loopId
    public synchronized List<Wifi485Loop> getWifi485LoopsByDevId(long devId) {
        if(devId <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP,null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[] { String.valueOf(devId)}, null, null, null, null);
        List<Wifi485Loop>loops = new ArrayList<Wifi485Loop>();
        while(cursor.moveToNext()){
            loops.add(fillDefaultLoop(cursor));
        }
        cursor.close();
        return loops;
    }

    public synchronized ArrayList<Wifi485Loop>  getWifi485LoopByRoom(int roomid){
        if(roomid <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP,null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[] { String.valueOf(roomid)}, null, null, null, null);
        ArrayList<Wifi485Loop>loops = new ArrayList<Wifi485Loop>();
        while(cursor.moveToNext()){
            loops.add(fillDefaultLoop(cursor));
        }
        cursor.close();
        return loops;
    }

    public synchronized ArrayList<Wifi485Loop>  getWifi485LoopByLoopType(String looptype){
        if(looptype == null){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP,null,
                ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE + "=?",
                new String[] { looptype}, null, null, null, null);
        ArrayList<Wifi485Loop>loops = new ArrayList<Wifi485Loop>();
        while(cursor.moveToNext()){
            loops.add(fillDefaultLoop(cursor));
        }
        cursor.close();
        return loops;
    }

    // query the record according to maskId,sub_dev_id and loopId
    public synchronized Wifi485Loop getWifi485Loop(long devId,int portId,int loopId) {
        if(devId <= 0 || loopId <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP,null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_485_PORTID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                new String[] { String.valueOf(devId), String.valueOf(portId),String.valueOf(loopId)}, null, null, null, null);
        Wifi485Loop loop = null;
        while(cursor.moveToNext()){
            loop  = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    public synchronized Wifi485Loop getWifi485Loop(String  roomName,int loopId) {
        if(CommonUtils.ISNULL(roomName) || loopId <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP,null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                new String[] { roomName,String.valueOf(loopId)}, null, null, null, null);
        Wifi485Loop loop = null;
        while(cursor.moveToNext()){
            loop  = fillDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }
    private Wifi485Loop fillDefaultLoop(Cursor cursor) {
        if(null == cursor){
            return null;
        }
        Wifi485Loop loop = new Wifi485Loop();
        loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        loop.mBrandName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_485_BRANDNAME));
        loop.mPortId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_485_PORTID));
        loop.mLoopId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPID));
        loop.mLoopType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE));
        loop.mSlaveAddr = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_485_SLAVEADDR));
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = CommonData.MODULE_TYPE_WIFI485;
        return loop;
    }
    //查询(所有列表)
    public synchronized List<Wifi485Loop> getWifi485LoopAllList() {
        List<Wifi485Loop> Wifi485LoopList = new ArrayList<Wifi485Loop>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_485_LOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID +" asc", null);
        while(cursor.moveToNext()){
            Wifi485LoopList.add(fillDefaultLoop(cursor));
        }
        cursor.close();
        return Wifi485LoopList;
    }
}
