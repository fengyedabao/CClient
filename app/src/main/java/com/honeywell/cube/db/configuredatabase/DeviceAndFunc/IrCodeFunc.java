package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 11:31
 * Email:Shodong.Sun@honeywell.com
 */
public class IrCodeFunc {

    ConfigCubeDatabaseHelper dbHelper = null;

    public IrCodeFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    public synchronized long addIrCode(IrCode code) {
        long rowId = -1;
        if (null == code || code.mLoopId <= 0) {
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_ID, code.mId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID, code.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_NAME, code.mName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_IMAGENAME, code.mImageName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA1, code.mData1)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA2, code.mData2).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IR_CODE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addIrCode(IrCode code, SQLiteDatabase db) {
        long rowId = -1;
        if (null == code || code.mLoopId <= 0) {
            return rowId;
        }
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_ID, code.mId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID, code.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_NAME, code.mName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_IMAGENAME, code.mImageName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA1, code.mData1)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA2, code.mData2).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_IR_CODE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    // delate all devIds Loop
    public synchronized int deleteIrCodeByLoopId(long loopId) {
        int num = -1;
        if (loopId <= 0) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IR_CODE,
                    ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID + "=?",
                    new String[]{String.valueOf(loopId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    // delate all devIds Loop
    public synchronized int deleteIrCodeByLoopIdAndKeyName(long loopId, String keyName) {
        int num = -1;
        if (loopId <= 0 || CommonUtils.ISNULL(keyName)) {
            return num;
        }
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_IR_CODE,
                    ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_IR_CODE_NAME + "=?",
                    new String[]{String.valueOf(loopId), keyName});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    // 主要根据devId，gatewayId，loopId更新记录，subGatewayID可选（只针对三菱空调有用）
    public synchronized int updateIrCode(IrCode code) {
        if (null == code || code.mLoopId <= 0 || CommonUtils.ISNULL(code.mName)) {
            return -1;
        }
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(code.mData1)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA1, code.mData1);
        }
        if (!CommonUtils.ISNULL(code.mData2)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA2, code.mData2);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_IR_CODE, values,
                    ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID + "=? and "
                            + ConfigCubeDatabaseHelper.COLUMN_IR_CODE_NAME + "=?",
                    new String[]{String.valueOf(code.mLoopId), code.mName});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 根据loop id 获取IrCode
     *
     * @param loopId
     * @return
     */
    public synchronized List<IrCode> getIrCodeByLoopId(int loopId) {
        List<IrCode> list = new ArrayList<IrCode>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_CODE, null,
                ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID + "=?",
                new String[]{String.valueOf(loopId)}, null, null, null, null);
        while (cursor.moveToNext()) {
            list.add(fillDefaultIrCode(cursor));
        }
        cursor.close();
        return list;
    }

    public synchronized List<IrCode> getIrCode(long loopId, long primaryid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_CODE, null,
                ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(loopId), String.valueOf(primaryid)}, null, null, null, null);
        IrCode code = null;
        List<IrCode> list = new ArrayList<IrCode>();
        while (cursor.moveToNext()) {
            code = fillDefaultIrCode(cursor);
            list.add(code);
        }
        cursor.close();
        return list;
    }

    public synchronized IrCode getIrCodeByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_CODE, null,
                ConfigCubeDatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);
        IrCode code = null;
        while (cursor.moveToNext()) {
            code = fillDefaultIrCode(cursor);
            break;
        }
        cursor.close();
        return code;
    }

    private IrCode fillDefaultIrCode(Cursor cursor) {
        IrCode loop = null;
        if (null == cursor) {
            return loop;
        }
        loop = new IrCode();
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mLoopId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_LOOPID));
        loop.mName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_NAME));
        loop.mImageName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_IMAGENAME));
        loop.mData1 = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA1));
        loop.mData2 = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IR_CODE_DATA2));
        return loop;
    }

    //查询(所有列表)
    public synchronized List<IrCode> getIrCodeAllList() {
        List<IrCode> IrCodeList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_IR_CODE, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == IrCodeList) {
                IrCodeList = new ArrayList<IrCode>();
            }
            IrCodeList.add(fillDefaultIrCode(cursor));
        }
        cursor.close();
        return IrCodeList;
    }
}
