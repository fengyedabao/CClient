package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 11:36
 * Email:Shodong.Sun@honeywell.com
 */
public class IrInfoFunc {
    ConfigCubeDatabaseHelper dbHelper= null;
    public IrInfoFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //增加
    public synchronized long addIrInfo(IrInfo info) throws SQLException {
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, info.mDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_TYPE, info.mIrType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_NAME, info.mIrName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_LOCK, info.mIrLock)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_PASSWORD, info.mIrPwd)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_ID, info.mIrId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_SUBDEV, info.mIrSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_KEY, info.mIrKey).getValues();
            rowId =  db.insert(ConfigCubeDatabaseHelper.TABLE_IR_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.close();
        }
        return rowId;
    }
    //增加
    public synchronized long addIrInfo(IrInfo info, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, info.mDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_TYPE, info.mIrType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_NAME, info.mIrName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_LOCK, info.mIrLock)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_PASSWORD, info.mIrPwd)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_ID, info.mIrId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_SUBDEV, info.mIrSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_KEY, info.mIrKey).getValues();
            rowId =  db.insert(ConfigCubeDatabaseHelper.TABLE_IR_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }
    //增加
    public synchronized long addIrInfo(long dev_id,IrInfo info) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, dev_id)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_TYPE, info.mIrType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_NAME, info.mIrName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_LOCK, info.mIrLock)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_PASSWORD, info.mIrPwd)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_ID, info.mIrId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_SUBDEV, info.mIrSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_KEY, info.mIrKey).getValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            rowId =  db.insert(ConfigCubeDatabaseHelper.TABLE_IR_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    /**
     * 清空表
     */
    public void clearIRInfo()
    {
        String sql = "delete from " + ConfigCubeDatabaseHelper.TABLE_IR_INFO + " where 1=1";
        dbHelper.getWritableDatabase().execSQL(sql);
    }

    // delate all devIds Loop
    public synchronized int deleteIrInfoByDevId(long devId) {
        int num = -1;
        if(devId <= 0){
            return num;
        }
        try {
            num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IR_INFO,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                    new String[]{String.valueOf(devId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public synchronized int deleteIrInfo(int devId,int irId) {
        int num = -1;
        if(devId <= 0){
            return num;
        }
        try {
            num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IR_INFO,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_IR_INFO_ID + "=?",
                    new String[]{String.valueOf(devId), String.valueOf(irId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }
    //now only offer update by devId
    public synchronized int updateIrInfo(IrInfo info) {
        if(null == info || info.mDevId <= 0 ){
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(!CommonUtils.ISNULL(info.mIrType)){
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_TYPE, info.mIrType);
        }

        if(!CommonUtils.ISNULL(info.mIrName)){
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_NAME, info.mIrName);
        }
        if(info.mIrLock >= 0){
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_LOCK, info.mIrLock);
        }
        if(!CommonUtils.ISNULL(info.mIrPwd)){
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_PASSWORD, info.mIrPwd);
        }
        if(info.mIrId >= 0){
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_ID, info.mIrId);
        }
        if(info.mIrSubDevId >= 0){
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_SUBDEV, info.mIrSubDevId);
        }
        if(!CommonUtils.ISNULL(info.mIrKey)){
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_KEY, info.mIrKey);
        }
        try {
            num =  db.update(ConfigCubeDatabaseHelper.TABLE_IR_INFO, values,
                    ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?", new String[]{String.valueOf(info.mDevId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return num;
    }
    public synchronized IrInfo getIrInfoByDevId(long devId) {
        if(devId <= 0){
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_INFO, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[] { String.valueOf(devId)},null, null, null, null);

        IrInfo loop = null;
        while(cursor.moveToNext()){
            loop = fillDefaultIrInfo(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    public synchronized IrInfo getIrInfo(int devId,int irId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_INFO, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_IR_INFO_ID + "=?",
                new String[] { String.valueOf(devId), String.valueOf(irId) },
                null, null, null, null);

        IrInfo loop = null;
        while(cursor.moveToNext()){
            loop = fillDefaultIrInfo(cursor);
            break;
        }
        cursor.close();
        db.close();
        return loop;
    }

    private IrInfo fillDefaultIrInfo(Cursor cursor) {
        IrInfo info = null;
        if(null == cursor){
            return info;
        }
        info = new IrInfo();
        info.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        info.mDevId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        info.mIrType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_TYPE));
        info.mIrName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_NAME));
        info.mIrLock = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_LOCK));
        info.mIrPwd = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_PASSWORD));
        info.mIrId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_ID));
        info.mIrSubDevId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_SUBDEV));
        info.mIrKey = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_INFO_KEY));
        return info;
    }
    //查询(所有列表)
    public synchronized List<IrInfo> getIrInfoAllList() {
        List<IrInfo> IrInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_INFO, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID +" asc", null);
        while(cursor.moveToNext()){
            if(null == IrInfoList){
                IrInfoList = new ArrayList<IrInfo>();
            }
            IrInfoList.add(fillDefaultIrInfo(cursor));
        }
        cursor.close();
        return IrInfoList;
    }

}
