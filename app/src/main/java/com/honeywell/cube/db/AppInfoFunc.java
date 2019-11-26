package com.honeywell.cube.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.common.utils.ContentValuesFactory;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H157925 on 16/4/27. 16:09
 * Email:Shodong.Sun@honeywell.com
 */
public class AppInfoFunc {
    ConfigCubeDatabaseHelper dbHelper = null;

    public AppInfoFunc(ConfigCubeDatabaseHelper instance) {
        dbHelper = instance;
    }

    //增加
    public synchronized long addAppInfo(AppInfo info) throws SQLException {
        if (null == info) {
            return -1;
        }
        return addAppInfo(info.deviceToken, info.version, info.database_version, info.cube_location, info.phone_prefix,
                info.username, info.password, info.nickname, info.phoneId,
                info.deviceId, info.user_image_path, info.current_scenario_id,
                info.cube_ip, info.cube_mac, info.cube_port, info.cube_local_nickname,
                info.cube_local_id, info.cube_local_password,
                info.all_header_fields_cookie, info.router_ssid_password,
                info.current_security_status, info.cube_voice_recognize, info.cube_version, info.last_read_time, info.online);
    }

    //清除数据库
    public synchronized static void clearAppInfo(Context context) {
        String sql = "delete from " + ConfigCubeDatabaseHelper.TABLE_APPINFO + " where 1=1";
        Loger.print("ssd", sql, Thread.currentThread());
        ConfigCubeDatabaseHelper.getInstance(context).getWritableDatabase().execSQL(sql);
    }

    public synchronized long addAppInfo(String deviceToken, String version, String databaseVersion, String cubeLocation, String phone_prefix,
                                        String username, String password, String nickname, int phoneId,
                                        int deviceId, String user_image_path, int current_scenario_id,
                                        String cube_ip, String cube_mac, int cube_port, String cube_local_nickname,
                                        String cube_local_id, String cube_local_password,
                                        String all_header_fields_cookie, String router_ssid_password,
                                        int current_security_status, int cube_voice_recognize, String cube_version, String lastReadTime, int online) {
        long rowId = -1;
        try {
            ContentValues values = new ContentValuesFactory()
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DEVICETOKEN, deviceToken)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_VERSION, version)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCATION, cubeLocation)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DATABASE_VERSION, databaseVersion)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PHONE_PREFIX, phone_prefix)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_USERNAME, username)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PASSWORD, password)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_NICKNAME, nickname)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PHONEID, phoneId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DEVICEID, deviceId)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_USER_IMAGE_PATH, user_image_path)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CURRENT_SCENARIO_ID, current_scenario_id)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_IP, cube_ip)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_MAC, cube_mac)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_PORT, cube_port)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_NICKNAME, cube_local_nickname)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_ID, cube_local_id)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_PASSWORD, cube_local_password)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ALL_HEADER_FIELDS_COOKIE, all_header_fields_cookie)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ROUTER_SSID_PASSWORD, router_ssid_password)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CURRENT_SECURITY_STATUS, current_security_status)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_VOICE_RECOGNIZE, cube_voice_recognize)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_VERSION, cube_version)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_LAST_READ_TIME, lastReadTime)
                    .put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ONLINE, online)
                    .getValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            rowId = db.insert(ConfigCubeDatabaseHelper.TABLE_APPINFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    //delete by UserName
    public synchronized int deleteAppInfoByUserName(String userName) {
        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        try {
            num = db.delete(ConfigCubeDatabaseHelper.TABLE_APPINFO,
                    ConfigCubeDatabaseHelper.COLUMN_APPINFO_USERNAME + "=?",
                    new String[]{userName});
            if (num > 0) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            db.endTransaction();
        }
        return num;
    }

    //update(更新) 直接刷新数据
    public synchronized long updateAppInfoByUserName(String userName, AppInfo info) {

        int num = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!CommonUtils.ISNULL(info.deviceToken)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DEVICETOKEN, info.deviceToken);
        }
        if (!CommonUtils.ISNULL(info.version)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_VERSION, info.version);
        }
        if (!CommonUtils.ISNULL(info.database_version)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DATABASE_VERSION, info.database_version);
        }
        if (!CommonUtils.ISNULL(info.router_ssid_password)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ROUTER_SSID_PASSWORD, info.router_ssid_password);
        }

        if (!CommonUtils.ISNULL(info.all_header_fields_cookie)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ALL_HEADER_FIELDS_COOKIE, info.all_header_fields_cookie);
        }
        if (!CommonUtils.ISNULL(info.phone_prefix)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PHONE_PREFIX, info.phone_prefix);
        }
        if (!CommonUtils.ISNULL(info.password)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PASSWORD, info.password);
        }
        if (!CommonUtils.ISNULL(info.nickname)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_NICKNAME, info.nickname);
        }
        values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PHONEID, info.phoneId);
        values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DEVICEID, info.deviceId);

        if (!CommonUtils.ISNULL(info.cube_location)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCATION, info.cube_location);
        }
        if (!CommonUtils.ISNULL(info.user_image_path)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_USER_IMAGE_PATH, info.user_image_path);
        }
        if (!CommonUtils.ISNULL(info.cube_ip)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_IP, info.cube_ip);
        }
        if (!CommonUtils.ISNULL(info.cube_mac)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_MAC, info.cube_mac);
        }
        if (!CommonUtils.ISNULL(info.cube_local_nickname)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_NICKNAME, info.cube_local_nickname);
        }
        if (!CommonUtils.ISNULL(info.cube_local_id)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_ID, info.cube_local_id);
        }
        if (!CommonUtils.ISNULL(info.cube_local_password)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_PASSWORD, info.cube_local_password);
        }
        if (!CommonUtils.ISNULL(info.cube_version)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_VERSION, info.cube_version);
        }
        if (!CommonUtils.ISNULL(info.last_read_time)) {
            values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_LAST_READ_TIME, info.last_read_time);
        }
        values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ONLINE, info.online);
        values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CURRENT_SCENARIO_ID, info.current_scenario_id);
        values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_PORT, info.cube_port);
        values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CURRENT_SECURITY_STATUS, info.current_security_status);
        values.put(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_VOICE_RECOGNIZE, info.cube_voice_recognize);

        num = db.update(ConfigCubeDatabaseHelper.TABLE_APPINFO, values,
                ConfigCubeDatabaseHelper.COLUMN_APPINFO_USERNAME + "=?",
                new String[]{userName});
        return num;
    }

    //查询（所有列表)
    public synchronized List<AppInfo> getAppInfoAllList() {
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_APPINFO, null, null, null, null, null, ConfigCubeDatabaseHelper.COLUMN_ID + " asc", null);

        while (cursor.moveToNext()) {
            AppInfo info = fillDefaultDevice(cursor);
            appInfos.add(info);
        }
        cursor.close();
        return appInfos;
    }

    /**
     * 获取当前登陆的用户
     */
    public static synchronized AppInfo getCurrentUser(Context context) {
        final String[] userInfo = PreferenceUtil.getUserInfo(context);
        AppInfo info = null;
        if (!"".equalsIgnoreCase(userInfo[0])) {
            info = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).getAppInfoByUserName(userInfo[0]);
        }
        if (info == null) {
            Loger.print("ssd test", "ssd get current info --- info is null", Thread.currentThread());
        }
        return info;
    }

    /**
     * 获取访客
     *
     * @param context
     * @return
     */
    public static AppInfo getGuestUser(Context context) {
        AppInfoFunc func = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        AppInfo info = func.getAppInfoByUserName(ModelEnum.GuestNum);
        if (info == null) {
            AppInfo info1 = new AppInfo();
            info1.current_scenario_id = 1;
            info1.username = ModelEnum.GuestNum;
            info1.version = CommonUtils.getVersion(context);
            func.addAppInfo(info1);
            info = info1;
        }
        return info;
    }


    public static int getBindDeviceId(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            info = AppInfoFunc.getGuestUser(context);
        }
        if (info == null)
            return 0;
        return info.deviceId > 0 ? info.deviceId : 0;
    }

    //通过username 查找
    public synchronized AppInfo getAppInfoByUserName(String username) {
        if (CommonUtils.ISNULL(username)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConfigCubeDatabaseHelper.TABLE_APPINFO, null,
                ConfigCubeDatabaseHelper.COLUMN_APPINFO_USERNAME + "=?", new String[]{username}, null, null, null, null);
        AppInfo info = null;
        while (cursor.moveToNext()) {
            info = fillDefaultDevice(cursor);
        }
        cursor.close();
        return info;
    }

    private AppInfo fillDefaultDevice(Cursor cursor) {
        if (null == cursor)
            return null;
        AppInfo info = new AppInfo();
        info.cube_location = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCATION));
        info.database_version = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DATABASE_VERSION));
        info.deviceToken = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DEVICETOKEN));
        info.version = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_VERSION));
        info.phone_prefix = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PHONE_PREFIX));
        info.username = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_USERNAME));
        info.password = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PASSWORD));
        info.nickname = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_NICKNAME));
        info.phoneId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_PHONEID));
        info.deviceId = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_DEVICEID));
        info.user_image_path = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_USER_IMAGE_PATH));
        info.current_scenario_id = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CURRENT_SCENARIO_ID));
        info.cube_ip = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_IP));
        info.cube_mac = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_MAC));
        info.cube_port = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_PORT));
        info.cube_local_nickname = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_NICKNAME));
        info.cube_local_id = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_ID));
        info.cube_local_password = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_LOCAL_PASSWORD));
        info.all_header_fields_cookie = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ALL_HEADER_FIELDS_COOKIE));
        info.router_ssid_password = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ROUTER_SSID_PASSWORD));
        info.current_security_status = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CURRENT_SECURITY_STATUS));
        info.cube_voice_recognize = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_VOICE_RECOGNIZE));
        info.cube_version = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_CUBE_VERSION));
        info.last_read_time = cursor.getString(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_LAST_READ_TIME));
        info.online = cursor.getInt(cursor.getColumnIndex(ConfigCubeDatabaseHelper.COLUMN_APPINFO_ONLINE));
        return info;
    }
}
