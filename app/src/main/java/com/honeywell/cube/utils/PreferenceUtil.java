package com.honeywell.cube.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.honeywell.cube.activities.LoginActivity;
import com.honeywell.cube.activities.RegisterActivity;
import com.honeywell.cube.controllers.AccountController;


/**
 * Created by H157925 on 16/4/15. 16:09
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 序列化存储工具
 */
public class PreferenceUtil {
    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context;

    // 注册倒计时数值存储
    public final static String REGISTER_TIMER_COUNT = "register_timer_count";
    public final static String FIND_PWD_TIMER_COUNT = "find_pwd_timer_count";
    public final static String BIND_PHONE_TIMER_COUNT = "bind_phone_timer_count";

    private static PreferenceUtil instance = new PreferenceUtil();

    private PreferenceUtil() {

    }

    public static void init(Context con) {
        if (null == sharedPreferences) {
            sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(con);
        }
        context = con;
    }

    public static synchronized PreferenceUtil getInstance() {
        if (sharedPreferences == null) {
            sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        }
        return instance;
    }

    public static void removeKey(String key) {
        editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void removeAll() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void commitString(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key, String faillValue) {
        return sharedPreferences.getString(key, faillValue);
    }

    public static void commitInt(String key, int value) {
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key, int failValue) {
        return sharedPreferences.getInt(key, failValue);
    }

    public static void commitLong(String key, long value) {
        editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key, long failValue) {
        return sharedPreferences.getLong(key, failValue);
    }

    public static void commitBoolean(String key, boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(String key, boolean failValue) {
        return sharedPreferences.getBoolean(key, failValue);
    }


    public static void setTimerCount(Context context, long timerCount, int type) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        switch (type) {
            case RegisterActivity.REGISTER_TYPE:
                prefs.edit().putLong(REGISTER_TIMER_COUNT, timerCount).commit();
                break;
            case RegisterActivity.FIND_PWD_TYPE:
                prefs.edit().putLong(FIND_PWD_TIMER_COUNT, timerCount).commit();
                break;

        }
    }


    public static long getTimerCount(Context context, int type) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        switch (type) {
            case RegisterActivity.REGISTER_TYPE:
                return prefs.getLong(REGISTER_TIMER_COUNT, 0);
            case RegisterActivity.FIND_PWD_TYPE:
                return prefs.getLong(FIND_PWD_TIMER_COUNT, 0);

            default:
                return 0;
        }

    }


    public static void saveUserLoginInfo(Context context, String username, String password) {
        SharedPreferences sp = context.getSharedPreferences("cube_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("username", username);
        edit.putString("password", password);
        edit.commit();
    }

    public static void clearUserLoginInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("cube_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("password", "");
        edit.commit();
    }

    public static String[] getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("cube_login", Context.MODE_PRIVATE);
        String[] result = new String[2];
        result[0] = sp.getString("username", "");
        result[1] = sp.getString("password", "");
        return result;
    }

    public static void saveXGToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("cube_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("token", token);
        edit.commit();
    }

    public static String getXGToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("cube_login", Context.MODE_PRIVATE);
        return sp.getString("token", "");
    }

    public static void saveCallNotification(Context context, boolean isCalling, long latestCallMsgId) {
        SharedPreferences sp = context.getSharedPreferences("cube_notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("isCalling", isCalling);
        edit.putLong("latestCallMsgId", latestCallMsgId);
        edit.commit();
    }

    public static void saveCallNotificationCustomConent(Context context, String customContent) {
        SharedPreferences sp = context.getSharedPreferences("cube_notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("customContent", customContent);
        edit.commit();
    }

    public static String getCallNotificationCustomConent(Context context) {
        SharedPreferences sp = context.getSharedPreferences("cube_notification", Context.MODE_PRIVATE);
        return sp.getString("customContent", "");
    }

    public static long getCallNotificationId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("cube_notification", Context.MODE_PRIVATE);
        return sp.getLong("latestCallMsgId", -1);
    }

    public static boolean getCallNotificationState(Context context) {
        SharedPreferences sp = context.getSharedPreferences("cube_notification", Context.MODE_PRIVATE);
        return sp.getBoolean("isCalling", false);
    }

    public static void saveUserHeadPic(Context context, String head_path) {
        SharedPreferences sp = context.getSharedPreferences("head_pic", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        String key = AccountController.getAppInfoLocalId(context);

        Log.e("url_path_set", key + ":::::" + head_path);

        edit.putString(key, head_path);
        edit.commit();
    }

    public static String getUserHeadPic(Context context) {
        SharedPreferences sp = context.getSharedPreferences("head_pic", Context.MODE_PRIVATE);

        String key = AccountController.getAppInfoLocalId(context);
        String result = sp.getString(key, "");
        Log.e("url_path_get", key + ":::::" + result);

        return result;
    }

    public static int getAlarmCount(Context context){
        SharedPreferences sp = context.getSharedPreferences("cube_alarm_count", Context.MODE_PRIVATE);
        int alarmCount = sp.getInt("alarmCount",0);
        return alarmCount;

    }

    public static void setAlarmCount(Context context, int alarmCount){
        SharedPreferences sp = context.getSharedPreferences("cube_alarm_count", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("alarmCount", alarmCount);
        edit.commit();
    }

}
