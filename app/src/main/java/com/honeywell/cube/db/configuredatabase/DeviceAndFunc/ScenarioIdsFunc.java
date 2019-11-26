package com.honeywell.cube.db.configuredatabase.DeviceAndFunc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/11. 16:06
 * Email:Shodong.Sun@honeywell.com
 */
public class ScenarioIdsFunc {
    private Context mContext = null;
    private static ScenarioIdsFunc instance  = null;
    ConfigCubeDatabaseHelper dbHelper= null;
    public static synchronized ScenarioIdsFunc getInstance(Context context) {
        if(instance == null){
            instance = new ScenarioIdsFunc(context);
        }
        return instance;
    }
    private ScenarioIdsFunc(Context context) {
        mContext = context;
        dbHelper= ConfigCubeDatabaseHelper.getInstance(mContext);
    }

    public synchronized long addOneScenarioId(int scenarioId,String  scenarioName,String imageName) throws SQLException {
        long rowId = -1;
        if(scenarioId <= CommonData.SCENARIO_ID_BASIC
                || CommonUtils.ISNULL(scenarioName)){
            return rowId;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID, scenarioId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME, scenarioName)
                    .put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME, imageName).getValues();
            rowId =  db.insert(ConfigCubeDatabaseHelper.TABLE_SCENARIOIDS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowId;
    }
    public synchronized long updateOneScenarioId(int scenarioId,String scenarioName,String imageName) throws SQLException {
        int num  = -1;
        if(CommonUtils.ISNULL(scenarioName) || scenarioId <= 0){
            return num;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME, scenarioName);
        if(!CommonUtils.ISNULL(imageName)){
            values.put(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME, imageName);
        }
        try {
            num =  db.update(ConfigCubeDatabaseHelper.TABLE_SCENARIOIDS, values,
                    ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=?",
                    new String[]{String.valueOf(scenarioId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return num;
    }
    // delate all devIds Loop
    public synchronized int deleteByScenarioId(int scenarioId) {
        int num = -1;
        try {
            num =  dbHelper.getWritableDatabase().delete(ConfigCubeDatabaseHelper.TABLE_SCENARIOIDS,
                    ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID + "=?",
                    new String[]{String.valueOf(scenarioId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }
    //查询(所有列表)
    public synchronized List<ScenarioIdsLoop> getAllScenarioIdsList() {
        List<ScenarioIdsLoop> lists = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_SCENARIOIDS, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID +" asc", null);
        while(cursor.moveToNext()){
            if(null == lists){
                lists = new ArrayList<ScenarioIdsLoop>();
            }
            ScenarioIdsLoop loop = new ScenarioIdsLoop();
            fillDefaultLoop(loop,cursor);
            lists.add(loop);
        }
        cursor.close();
        return lists;
    }
    private void fillDefaultLoop(ScenarioIdsLoop loop, Cursor cursor) {
        if(null == loop || null == cursor){
            return;
        }
        loop.mId = cursor.getLong(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_ID));
        loop.mScenarioId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_ID));

        loop.mScenarioName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_NAME));
        if(null == loop.mScenarioName){
            loop.mScenarioName = "";
        }
        String imageName = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_SCENARIO_IMAGENAME));
        if(CommonUtils.ISNULL(imageName)){
            imageName = "";
        }
        loop.mImageName = imageName;
    }
    public class ScenarioIdsLoop{
        public long mId = -1;
        public int mScenarioId = -1;
        public String mScenarioName = "";
        public String mImageName = "";
    }
}
