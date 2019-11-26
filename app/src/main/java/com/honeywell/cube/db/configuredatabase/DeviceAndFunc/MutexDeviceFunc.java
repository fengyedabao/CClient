package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 14:34
 * Email:Shodong.Sun@honeywell.com
 */
public class MutexDeviceFunc {
    ConfigCubeDatabaseHelper dbHelper= null;
    PeripheralDeviceFunc peripheralDeviceFuc = null;
    Wireless315M433MLoopFunc mWireless315m433mLoopFuc = null;
    SparkLightingLoopFunc mSparkLightingLoopFuc = null;
    public MutexDeviceFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
        peripheralDeviceFuc = new PeripheralDeviceFunc(dbHelper);
        mWireless315m433mLoopFuc = new Wireless315M433MLoopFunc(dbHelper);
        mSparkLightingLoopFuc = new SparkLightingLoopFunc(dbHelper);
    }
    public synchronized long addMutexDeviceInfo(MutexDeviceInfo loop,boolean isUpdateVer){
        long rowId = -1;
        if(loop == null){
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_MUTEXDEVICEMAP_MUTEXID, loop.mMutexId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mDeviceLoopPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE, loop.mModuleType).getValues();
            rowId =  db.insert(ConfigCubeDatabaseHelper.TABLE_MUTEX_DEVICE_MAP, null, values);
        }
        finally{
//            if(rowId > 0 && isUpdateVer){
//                CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//                cubebaseFuc.updateConfigVer();
//            }
        }
        return rowId;
    }

    public synchronized int deleteMutexDeviceInfo(long mutexId, long loopPrimaryId,int moduleType) {
        int num = -1;
        if(mutexId <= 0 || loopPrimaryId <= 0 || moduleType <= 0 ){
            return num;
        }
        num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_MUTEX_DEVICE_MAP,
                ConfigCubeDatabaseHelper.COLUMN_MUTEXDEVICEMAP_MUTEXID + "=? and "
                        +ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_MODULETYPE + "=?",
                new String[]{String.valueOf(mutexId),String.valueOf(loopPrimaryId),String.valueOf(moduleType)});
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }
    public synchronized int deleteMutexDeviceInfoByMutexId(long mutexId) {
        int num = 0;
        if(mutexId <= 0){
            return num;
        }
        try {
            num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_MUTEX_DEVICE_MAP,
                    ConfigCubeDatabaseHelper.COLUMN_MUTEXDEVICEMAP_MUTEXID + "=?",new String[]{String.valueOf(mutexId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(num > 0){
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    public synchronized List<MutexDeviceInfo> getMutexDeviceInfoListByMutexId(long mutexId){
        if(mutexId <= 0){
            return null;
        }
        List<MutexDeviceInfo> infos  = new ArrayList<MutexDeviceInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_MUTEX_DEVICE_MAP, null,
                ConfigCubeDatabaseHelper.COLUMN_MUTEXDEVICEMAP_MUTEXID + "=?",new String[]{String.valueOf(mutexId)}, null, null,ConfigCubeDatabaseHelper.COLUMN_ID +" asc", null);
        while(cursor.moveToNext()){
            MutexDeviceInfo loop = new MutexDeviceInfo();
            fillAllFieldsToLoop(cursor,loop);
            infos.add(loop);
        }
        cursor.close();
        return infos;
    }


    public synchronized List<MutexDeviceInfo> getMutexDeviceInfoAllList() {
        List<MutexDeviceInfo> mutexDeviceInfoList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_MUTEX_DEVICE_MAP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID +" asc", null);
        while(cursor.moveToNext()){
            if(null == mutexDeviceInfoList){
                mutexDeviceInfoList = new ArrayList<MutexDeviceInfo>();
            }
            MutexDeviceInfo loop = new MutexDeviceInfo();
            fillAllFieldsToLoop(cursor,loop);
            mutexDeviceInfoList.add(loop);
        }
        cursor.close();
        return mutexDeviceInfoList;
    }

    private synchronized void fillAllFieldsToLoop(Cursor cursor,MutexDeviceInfo loop){
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mMutexId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MUTEXDEVICEMAP_MUTEXID));
        loop.mDeviceLoopPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_MODULETYPE));
    }
}
