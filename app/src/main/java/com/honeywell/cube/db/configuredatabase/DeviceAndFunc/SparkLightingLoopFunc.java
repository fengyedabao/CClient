package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.Util;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.CubebaseFuc;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 16:46
 * Email:Shodong.Sun@honeywell.com
 */
public class SparkLightingLoopFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public SparkLightingLoopFunc(ConfigCubeDatabaseHelper instance) {
        super();
        this.dbHelper = instance;
    }

    //增加
    public synchronized long addSparkLightingLoop(SparkLightingLoop loop) throws SQLException {
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID, loop.mSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVTYPE, loop.mSubDevType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }

    public synchronized long addSparkLightingLoop(SparkLightingLoop loop, SQLiteDatabase db) throws SQLException {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, loop.mModulePrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID, loop.mSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVTYPE, loop.mSubDevType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public synchronized long addSparkLightingLoop(long dev_id, SparkLightingLoop loop) throws SQLException {
        long rowId = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID, loop.mLoopSelfPrimaryId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, dev_id)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, loop.mLoopName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, loop.mRoomId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID, loop.mSubDevId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVTYPE, loop.mSubDevType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE, loop.mLoopType)
                    .put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, loop.mLoopId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable).getValues();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
//        if(rowId > 0){
//            Util.putPrimaryIdAndModuleType(loop, rowId, dev_id, CommonData.MODULE_TYPE_SPARKLIGHTING);
//            Util.addLoopToDefaultScenario(dbHelper,loop,loop.mSubDevType.equals(CommonData.SPARKLIGHT_SUBDEVTYPE_HBLSSIR));
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return rowId;
    }

    public synchronized int deleteSparkLightingLoopByPrimary(long primaryId) {
        int num = -1;
        try {
            num = dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP,
                    ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                    new String[]{String.valueOf(primaryId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (num > 0) {
//            Util.deleteLoopFromScenarios(dbHelper, primaryId, CommonData.MODULE_TYPE_SPARKLIGHTING);
//            CubebaseFuc cubebaseFuc = new CubebaseFuc(dbHelper);
//            cubebaseFuc.updateConfigVer();
//        }
        return num;
    }

    // when deleting PeripheralDevice record,we should delete the record of such dev_id
    public synchronized int deleteSparkLightingLoopByDevId(long devId) {
        int num = -1;
        List<SparkLightingLoop> loops = getSparkLightingLoopByModulePrimaryId(devId);
        num = loops.size();
        if (loops == null || num == 0) {
            return -1;
        }
        for (int i = 0; i < num; i++) {
            deleteSparkLightingLoopByPrimary(loops.get(i).mLoopSelfPrimaryId);
        }
        return num;
    }


    /**
     * 通过device id 获取SparkLighting
     *
     * @param devId
     * @return
     */
    private List<SparkLightingLoop> getSparkLightingLoopByModulePrimaryId(long devId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?",
                new String[]{String.valueOf(devId)}, null, null, null, null);
        List<SparkLightingLoop> loops = new ArrayList<SparkLightingLoop>();
        while (cursor.moveToNext()) {
            SparkLightingLoop loop = getDefaultLoop(cursor);
            loops.add(loop);
        }
        cursor.close();
        return loops;
    }

    public synchronized SparkLightingLoop getSparkLightingLoop(int roomid, int subGatewayId, int loopId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (loopId <= 0) {
            return null;
        }
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                new String[]{"" + roomid, String.valueOf(subGatewayId), String.valueOf(loopId)}, null, null, null, null);
        SparkLightingLoop loop = null;
        while (cursor.moveToNext()) {
            loop = getDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    // update the record fields according to maskId,loopType and loopId
    public synchronized int updateSparkLightingLoopByPrimaryId(long primaryId, SparkLightingLoop loop) {
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
        if (loop.mIsEnable == CommonData.ARM_TYPE_ENABLE || loop.mIsEnable == CommonData.ARM_TYPE_DISABLE) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE, loop.mIsEnable);
        }
        try {
            num = db.update(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, values,
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

    public synchronized SparkLightingLoop getSparkLightingLoopByPrimaryId(long primaryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_PRIMARYID + "=?",
                new String[]{String.valueOf(primaryId)}, null, null, null, null);
        SparkLightingLoop loop = null;
        while (cursor.moveToNext()) {
            loop = getDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    /**
     * 通过type来获取数据
     *
     * @param type
     * @return
     */
    public synchronized ArrayList<SparkLightingLoop> getSparkLightingLoopByLoopType(int type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE + "=?",
                new String[]{String.valueOf(type)}, null, null, null, null);
        SparkLightingLoop loop = null;
        ArrayList<SparkLightingLoop> returnValue = new ArrayList<SparkLightingLoop>();
        while (cursor.moveToNext()) {
            loop = getDefaultLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        return returnValue;
    }

    /**
     * 获取loop type 再一定范围内的数据
     *
     * @param type1
     * @param type2
     * @return
     */
    public synchronized ArrayList<SparkLightingLoop> getSparkLightingLoopByType(int type1, int type2) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE + " in (?,?) ",
                new String[]{"" + type1, "" + type2}, null, null, null, null);
        SparkLightingLoop loop = null;
        ArrayList<SparkLightingLoop> returnValue = new ArrayList<SparkLightingLoop>();
        while (cursor.moveToNext()) {
            loop = getDefaultLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        return returnValue;
    }


    /**
     * 通过房间名查询
     *
     * @param roomid
     * @return
     */
    public synchronized ArrayList<SparkLightingLoop> getSparkLightingLoopByRoom(int roomid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_ROOM_ID + "=?",
                new String[]{"" + roomid}, null, null, null, null);
        SparkLightingLoop loop = null;
        ArrayList<SparkLightingLoop> returnValue = new ArrayList<SparkLightingLoop>();
        while (cursor.moveToNext()) {
            loop = getDefaultLoop(cursor);
            returnValue.add(loop);
        }
        cursor.close();
        return returnValue;
    }

    // query the record according to maskId,sub_dev_id and loopId
    public synchronized SparkLightingLoop getSparkLightingLoop(long devId, int subDevId, int loopId, int loopType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null,
                ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE + "=? and "
                        + ConfigCubeDatabaseHelper.COLUMN_LOOPID + "=?",
                new String[]{String.valueOf(devId), String.valueOf(subDevId), String.valueOf(loopType), String.valueOf(loopId)}, null, null, null, null);
        SparkLightingLoop loop = null;

        while (cursor.moveToNext()) {
            loop = getDefaultLoop(cursor);
            break;
        }
        cursor.close();
        return loop;
    }

    private SparkLightingLoop getDefaultLoop(Cursor cursor) {
        SparkLightingLoop loop = new SparkLightingLoop();
        if (null == cursor) {
            return loop;
        }
        loop.mLoopName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME));
        loop.mRoomId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID));
        loop.mSubDevId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVICEID));
        loop.mSubDevType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVTYPE));
        loop.mLoopType = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPTYPE));
        loop.mLoopId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_LOOPID));
        loop.mIsEnable = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_IS_ENABLE));
        loop.mModulePrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_DEVICEID));
        loop.mLoopSelfPrimaryId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_PRIMARYID));
        loop.mModuleType = CommonData.MODULE_TYPE_SPARKLIGHTING;
        return loop;
    }

    //根据dev_id查询(所有列表。bIsSir:表示是否只是返回洗涤红外的)
    public synchronized List<BasicLoop> getBasicLoopAllList(boolean bIsSir) {
        List<BasicLoop> loops = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == loops) {
                loops = new ArrayList<BasicLoop>();
            }
            BasicLoop loop = getDefaultLoop(cursor);
            String subDevType = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SPARKLIGNTINGLOOP_SUBDEVTYPE));
            if (bIsSir && !subDevType.equals(CommonData.SPARKLIGHT_SUBDEVTYPE_HBLSSIR)) {
                continue;
            }
            loops.add(loop);
        }
        cursor.close();
        return loops;
    }

    //根据dev_id查询(所有列表。bIsSir:表示是否只是返回洗涤红外的)
    public synchronized List<SparkLightingLoop> getSparkLightingLoopAllList(boolean bIsSir) {
        List<SparkLightingLoop> sparkLightingLoopList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == sparkLightingLoopList) {
                sparkLightingLoopList = new ArrayList<SparkLightingLoop>();
            }
            SparkLightingLoop loop = getDefaultLoop(cursor);
            if (bIsSir && loop.mSubDevType != null && !loop.mSubDevType.equals(CommonData.SPARKLIGHT_SUBDEVTYPE_HBLSSIR)) {
                continue;
            }
            sparkLightingLoopList.add(loop);
        }
        cursor.close();
        return sparkLightingLoopList;
    }

    //根据dev_id查询(所有列表。bIsSir:表示是否只是返回洗涤红外的)
    public synchronized List<SparkLightingLoop> getSparkLightingLoopByDevId(boolean bIsSir, long devId) {
        List<SparkLightingLoop> sparkLightingLoopList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SPARKLIGNTINGLOOP,
                null, ConfigCubeDatabaseHelper.COLUMN_DEVICEID + "=?", new String[]{String.valueOf(devId)},
                null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);
        while (cursor.moveToNext()) {
            if (null == sparkLightingLoopList) {
                sparkLightingLoopList = new ArrayList<SparkLightingLoop>();
            }
            SparkLightingLoop loop = getDefaultLoop(cursor);
            if (bIsSir && null != loop.mSubDevType && !loop.mSubDevType.equals(CommonData.SPARKLIGHT_SUBDEVTYPE_HBLSSIR)) {
                continue;
            }
            sparkLightingLoopList.add(loop);
        }
        cursor.close();
        return sparkLightingLoopList;
    }
}
