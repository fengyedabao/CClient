package com.honeywell.cube.common.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCodeFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485LoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.voicerecogadapter.ChineseSpelling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.honeywell.cube.R;
import com.sun.jna.platform.win32.WinDef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by H157925 on 16/4/11. 09:35
 * Email:Shodong.Sun@honeywell.com
 */
public class CommonUtils {
    private static final String TAG = "CommonUtils";

    /**
     *
     */
    public static String transferIpcType(Context context, String name) {
        if (context.getString(R.string.device_ipc_phoenix).equalsIgnoreCase(name)) {
            return CommonData.IPCAMERA_TYPE_PHOENIX;
        } else if (context.getString(R.string.device_ipc_super_hd).equalsIgnoreCase(name)) {
            return CommonData.IPCAMERA_TYPE_SUPER_HD;
        }
        return name;
    }

    /**
     * 转换 IPC branch name
     */
    public static String transferBacnetType(Context context, String name) {
        if (context.getString(R.string.device_dakin).equalsIgnoreCase(name)) {
            return ModelEnum.BACNET_TYPE_DAKIN;
        } else if (context.getString(R.string.device_sanling).equalsIgnoreCase(name)) {
            return ModelEnum.BACNET_TYPE_SANLING;
        }
        return name;
    }

    /**
     * 转换 485 设备 branch name
     */
    public static String transferWifi485BranchName(Context context, String name) {
        if (context.getString(R.string.device_ecc_o1).equalsIgnoreCase(name)) {
            return CommonData.WIFI485_AC_TYPE_ECC_O1;
        } else if (context.getString(R.string.device_htc961d3200).equalsIgnoreCase(name)) {
            return CommonData.WIFI485_AC_TYPE_HTC;
        } else if (context.getString(R.string.device_ecc_dt300).equalsIgnoreCase(name)) {
            return CommonData.WIFI485_AC_TYPE_ECC_DT300;
        }
        return name;
    }

    /**
     * 转换485设备 looptype
     */
    public static String transferWifi485LoopType(Context context, String name) {
        if (context.getString(R.string.device_aircondition).equalsIgnoreCase(name)) {
            return CommonData.DEVICETYPE_AIRCONDITION;
        } else if (context.getString(R.string.device_thermostat).equalsIgnoreCase(name)) {
            return CommonData.DEVICETYPE_THERMOSTAT;
        } else if (context.getString(R.string.device_ventilation).equalsIgnoreCase(name)) {
            return CommonData.DEVICETYPE_VENTILATION;
        }
        return name;
    }

    /**
     * 将CallType 转换为显示的数据
     *
     * @param context
     * @param protocol
     * @return
     */
    public static String transferCallTypeFromProtocol(Context context, String protocol) {
        if (protocol == null) return "";
        if (ModelEnum.CALL_TYPE_LOBBYPHONE.equalsIgnoreCase(protocol) || ModelEnum.CALL_TYPE_LOBBYPHONE3.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.pop_phonecall_lobby);
        } else if (ModelEnum.CALL_TYPE_OFFICEPHONE.equalsIgnoreCase(protocol) || ModelEnum.CALL_TYPE_PCOFFICE.equalsIgnoreCase(protocol) || ModelEnum.CALL_TYPE_PCOFFICE3.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.pop_phonecall_office);
        } else if (ModelEnum.CALL_TYPE_GUARDPHONE.equalsIgnoreCase(protocol) || ModelEnum.CALL_TYPE_PCGUARD.equalsIgnoreCase(protocol) || ModelEnum.CALL_TYPE_PCGUARD3.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.pop_phonecall_guard);
        } else if (ModelEnum.CALL_TYPE_DOORCAMERAFRONT.equalsIgnoreCase(protocol) || ModelEnum.CALL_TYPE_DOORCAMERAFRONT3.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.pop_phonecall_frontdoor);
        } else if (ModelEnum.CALL_TYPE_DOORCAMERABACK.equalsIgnoreCase(protocol) || ModelEnum.CALL_TYPE_DOORCAMERABACK3.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.pop_phonecall_reardoor);
        } else if (ModelEnum.TYPE_CALL_EXTENSION.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.pop_phonecall_subphone);
        }
        return "";
    }

    /**
     * 将 protocol转换为名称
     *
     * @param context
     * @param protocol
     * @return
     */
    public static String transferProtocolToName(Context context, String protocol) {
        if (CommonData.SENSOR_TYPE_433_INFRADE.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.sensor_type_infrade);
        } else if (CommonData.SENSOR_TYPE_433_DOORMAGNETI.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.sensor_type_door);
        } else if (CommonData.SENSOR_TYPE_433_KEYFOB.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.sensor_type_keyfob);
        } else if (CommonData.ZONE_TYPE_SECURITY_24HOURS.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.zone_type_24);
        } else if (CommonData.ZONE_TYPE_SECURITY_DELAY.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.zone_type_delay);
        } else if (CommonData.ZONE_TYPE_SECURITY_INSTANT.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.zone_type_instance);
        }
        return protocol;
    }

    public static String transferNameToProtocol(Context context, String name) {
        if (context.getString(R.string.sensor_type_infrade).equalsIgnoreCase(name)) {
            return CommonData.SENSOR_TYPE_433_INFRADE;
        } else if (context.getString(R.string.sensor_type_door).equalsIgnoreCase(name)) {
            return CommonData.SENSOR_TYPE_433_DOORMAGNETI;
        } else if (context.getString(R.string.sensor_type_keyfob).equalsIgnoreCase(name)) {
            return CommonData.SENSOR_TYPE_433_KEYFOB;
        } else if (context.getString(R.string.zone_type_24).equalsIgnoreCase(name)) {
            return CommonData.ZONE_TYPE_SECURITY_24HOURS;
        } else if (context.getString(R.string.zone_type_delay).equalsIgnoreCase(name)) {
            return CommonData.ZONE_TYPE_SECURITY_DELAY;
        } else if (context.getString(R.string.zone_type_instance).equalsIgnoreCase(name)) {
            return CommonData.ZONE_TYPE_SECURITY_INSTANT;
        }
        return name;
    }

    /**
     * 将字符串中的数字转换为数字
     *
     * @param context
     * @param origin
     * @return
     */
    public static String transferNumToChinese(String origin) {
        if (origin == null || "".equalsIgnoreCase(origin)) return origin;
        char num[] = origin.toCharArray();
        StringBuffer returnValue = new StringBuffer();
        for (int i = 0; i < num.length; i++) {
            if (Character.isDigit(num[i])) {
                //如果是数字
                int date = num[i] - '0';
                char chinese = CommonUtils.changeNumToChinese(date);
                returnValue.append(chinese);
            } else {
                returnValue.append(num[i]);
            }
        }
        return returnValue.toString();
    }

    public static char changeNumToChinese(int num) {
        switch (num) {
            case 0:
                return '零';
            case 1:
                return '一';
            case 2:
                return '二';
            case 3:
                return '三';
            case 4:
                return '四';
            case 5:
                return '五';
            case 6:
                return '六';
            case 7:
                return '7';
            case 8:
                return '八';
            case 9:
                return '九';
            default:
                return (char) (num + '0');
        }
    }


    /**
     * 获取Str的前几位
     *
     * @param inputString
     * @param base
     * @return
     */
    public static int getLeadingInteger(String inputString, int base) {
        char[] input = inputString.toLowerCase().toCharArray();
        boolean positive = input[0] != '-';
        int start = input[0] == '+' || input[0] == '-' ? 1 : 0;
        int end = start;
        for (; end < input.length; end++) {
            try {
                // check if still a digit in right base
                Integer.parseInt(Character.toString(input[end]), base);
            } catch (NumberFormatException e) {
                break;
            }
        }
        int length = end - start;
        int result = Integer.parseInt(new String(input, start, length), base);
        return result * (positive ? 1 : -1);
    }

    /**
     * 转换AC 模式 状态
     *
     * @param context
     * @param mode
     * @return
     */
    public static String transferIrAcModeStr(Context context, String mode) {
        if (CommonData.MODE_TYPE_COOL.equalsIgnoreCase(mode)) {
            return context.getString(R.string.ac_mode_cool);
        } else if (CommonData.MODE_TYPE_HEAT.equalsIgnoreCase(mode)) {
            return context.getString(R.string.ac_mode_heat);
        } else if (CommonData.MODE_TYPE_VENLITATION.equalsIgnoreCase(mode)) {
            return context.getString(R.string.ac_mode_venlitation);
        } else if (CommonData.MODE_TYPE_DEHUMIDIFY.equalsIgnoreCase(mode)) {
            return context.getString(R.string.ac_mode_dehumidify);
        } else if (CommonData.MODE_TYPE_AUTO.equalsIgnoreCase(mode)) {
            return context.getString(R.string.ac_mode_auto);
        }
        return mode;
    }

    /**
     * 将AC的模式转换
     *
     * @param context
     * @param str
     * @return
     */
    public static String tansferIrAcModeProtocolFromStr(Context context, String str) {
        if (context.getString(R.string.ac_mode_cool).equalsIgnoreCase(str)) {
            return CommonData.MODE_TYPE_COOL;
        } else if (context.getString(R.string.ac_mode_heat).equalsIgnoreCase(str)) {
            return CommonData.MODE_TYPE_HEAT;
        } else if (context.getString(R.string.ac_mode_venlitation).equalsIgnoreCase(str)) {
            return CommonData.MODE_TYPE_VENLITATION;
        } else if (context.getString(R.string.ac_mode_dehumidify).equalsIgnoreCase(str)) {
            return CommonData.MODE_TYPE_DEHUMIDIFY;
        } else if (context.getString(R.string.ac_mode_auto).equalsIgnoreCase(str)) {
            return CommonData.MODE_TYPE_AUTO;
        }
        return str;
    }


    /**
     * 报警类型 国际化
     *
     * @param context
     * @param type
     * @return
     */
    public static String transferZoneAlarmType(Context context, String type) {
        if (CommonData.ZONE_ALARM_STATUS_FIRE.equalsIgnoreCase(type)) {
            return context.getString(R.string.zone_alarm_fire);
        } else if (CommonData.ZONE_ALARM_STATUS_HELP.equalsIgnoreCase(type)) {
            return context.getString(R.string.zone_alarm_help);
        } else if (CommonData.ZONE_ALARM_STATUS_GAS.equalsIgnoreCase(type)) {
            return context.getString(R.string.zone_alarm_gas);
        } else if (CommonData.ZONE_ALARM_STATUS_THIEF.equalsIgnoreCase(type)) {
            return context.getString(R.string.zone_alarm_thief);
        } else if (CommonData.ZONE_ALARM_STATUS_EMERGENCY.equalsIgnoreCase(type)) {
            return context.getString(R.string.zone_alarm_emergency);
        }
        return "";
    }

    /**
     * 将alarm type image 获取 image id
     *
     * @param context
     * @param type
     * @param select
     * @return
     */
    public static int getAlarmImageIdFromType(Context context, String type, boolean select) {
        if (CommonData.ZONE_ALARM_STATUS_FIRE.equalsIgnoreCase(type)) {
            return select ? R.mipmap.alarm_fire_s : R.mipmap.alarm_fire;
        } else if (CommonData.ZONE_ALARM_STATUS_HELP.equalsIgnoreCase(type)) {
            return select ? R.mipmap.alarm_help_s : R.mipmap.alarm_help;
        } else if (CommonData.ZONE_ALARM_STATUS_GAS.equalsIgnoreCase(type)) {
            return select ? R.mipmap.alarm_gas_s : R.mipmap.alarm_gas;
        } else if (CommonData.ZONE_ALARM_STATUS_THIEF.equalsIgnoreCase(type)) {
            return select ? R.mipmap.alarm_thief_s : R.mipmap.alarm_thief;
        } else if (CommonData.ZONE_ALARM_STATUS_EMERGENCY.equalsIgnoreCase(type)) {
            return select ? R.mipmap.alarm_emergency_s : R.mipmap.alarm_emergency;
        }
        return R.mipmap.alarm_fire;
    }


    //获取版本号
    public static String getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //验证密码
    public static boolean checkPwdFormat(String pwd) {
        //长度限制
        if (pwd == null || pwd.length() < 6) return false;

        //由数字字母组成
        boolean isDorL = true;
        for (int i = 0; i < pwd.length(); i++) {
            if (!((pwd.charAt(i) > '0' && pwd.charAt(i) < '9') || (pwd.charAt(i) > 'a' && pwd.charAt(i) < 'z') || (pwd.charAt(i) > 'A' && pwd.charAt(i) < 'Z'))) {
                isDorL = false;
                break;
            }
        }
        return isDorL;
    }


    //获取手机唯一表示 IMEI
    public static String getIMEI(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }

    //将数组转换为ArrayList
    public static ArrayList<Integer> transferIntArray(int[] array) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i = 0; i < array.length; i++) {
            arrayList.add(array[i]);
        }
        return arrayList;
    }

    public static ArrayList<String> transferStrArray(String[] array) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
            arrayList.add(array[i]);
        }
        return arrayList;
    }

    //判断SDCard是否ok
    public static boolean isSdcardOK() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断网络是否链接
    public static boolean isConnectNetwork(Context context) {
        if (null == context) {
            return false;
        }
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return false;
    }

    //判断wifi链接状态
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断SDCard 是否存在，是否可写
    public static boolean checkSdcard() {
        File sdcard = new File(getSdcardPath());
        return !(!sdcard.exists() || !sdcard.canWrite());
    }

    //获取SDCard路径
    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    //null或空的时候返回true
    public static boolean ISNULL(String str) {
        boolean bRet = false;
        if (str == null || str.equals("")) {
            bRet = true;
        }
        return bRet;
    }

    //获取1970时间错
    public static String genISO8601TimeStampFor1970() {
        String DATEFORMAT_ISO8601_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat TIMEFORMATE = new SimpleDateFormat(DATEFORMAT_ISO8601_UTC);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0000"));
        cal.clear();
        cal.set(1970, 0, 1);
        Date date = cal.getTime();

        String timeStamp = TIMEFORMATE.format(date);

        return timeStamp;
    }

    //获取时间戳
    public static String genISO8601TimeStampForCurrTime() {
        String DATEFORMAT_ISO8601_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat TIMEFORMATE = new SimpleDateFormat(DATEFORMAT_ISO8601_UTC);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0000"));
        Date date = cal.getTime();

        String timeStamp = TIMEFORMATE.format(date);

        return timeStamp;
    }

    public static String[] getDayAndMonthFromDateString(String dataStr) {
        String[] data = dataStr.split("-");
        String day = data[2];
        String month = data[1];
        if ("01".equalsIgnoreCase(month)) {
            month = "Jan";
        } else if ("02".equalsIgnoreCase(month)) {
            month = "Feb";
        } else if ("03".equalsIgnoreCase(month)) {
            month = "Mar";
        } else if ("04".equalsIgnoreCase(month)) {
            month = "Apr";
        } else if ("05".equalsIgnoreCase(month)) {
            month = "May";
        } else if ("06".equalsIgnoreCase(month)) {
            month = "Jun";
        } else if ("07".equalsIgnoreCase(month)) {
            month = "Jul";
        } else if ("08".equalsIgnoreCase(month)) {
            month = "Aug";
        } else if ("09".equalsIgnoreCase(month)) {
            month = "Sep";
        } else if ("10".equalsIgnoreCase(month)) {
            month = "Oct";
        } else if ("11".equalsIgnoreCase(month)) {
            month = "Nov";
        } else if ("12".equalsIgnoreCase(month)) {
            month = "Dec";
        }

        return new String[]{day, month};
    }

    //将Enable字符串转换为数字
    public static int getIntegerZoneEnableType(String enableStr) {
        int armType = CommonData.ARM_TYPE_DISABLE;
        if (enableStr.equalsIgnoreCase(CommonData.ARM_TYPE_ENABLE_STR)) {
            armType = CommonData.ARM_TYPE_ENABLE;
        }
        return armType;
    }

    //将Enable转换为字符串
    public static String getStringZoneEnableType(int enableType) {
        String str = CommonData.ARM_TYPE_DISABLE_STR;
        if (enableType == CommonData.ARM_TYPE_ENABLE) {
            str = CommonData.ARM_TYPE_ENABLE_STR;
        }
        return str;
    }

    //获取arm字符串转换未数字
    public static int getIntegerArmType(String armStr) {
        int armType = CommonData.ARM_TYPE_DISABLE;
        if (armStr.equalsIgnoreCase(CommonData.ARM_TYPE_ARM_STR)) {
            armType = CommonData.ARM_TYPE_ENABLE;
        }
        return armType;
    }

    //将Arm数字转换未数字
    public static String getStringArmType(int armType) {
        String str = CommonData.ARM_TYPE_DISARM_STR;
        if (armType == CommonData.ARM_TYPE_ENABLE) {
            str = CommonData.ARM_TYPE_ARM_STR;
        }
        return str;
    }

    //将Looptype转换为字符串
    public static String getStringLoopType(int type) {
        String loopType = "";
        if (type < 0) {
            return loopType;
        }
        switch (type) {
            case CommonData.LOOP_TYPE_LIGHT_INT:
                loopType = CommonData.LOOP_TYPE_LIGHT;
                break;
            case CommonData.LOOP_TYPE_CURTAIN_INT:
                loopType = CommonData.LOOP_TYPE_CURTAIN;
                ;
                break;
            case CommonData.LOOP_TYPE_RELAY_INT:
                loopType = CommonData.LOOP_TYPE_RELAY;
                break;
            case CommonData.LOOP_TYPE_SWITCH_INT:
                loopType = CommonData.LOOP_TYPE_SWITCH;
                break;
            case CommonData.LOOP_TYPE_SENSOR_INT:
                loopType = CommonData.LOOP_TYPE_SENSOR;
                break;
            case CommonData.LOOP_TYPE_5800PIRAP_INT:
                loopType = CommonData.SENSOR_TYPE_433_INFRADE;
                break;
            case CommonData.LOOP_TYPE_5804EU_INT:
                loopType = CommonData.SENSOR_TYPE_433_DOORMAGNETI;
                break;
            case CommonData.LOOP_TYPE_5816EU_INT:
                loopType = CommonData.SENSOR_TYPE_433_KEYFOB;
                break;
            default:
                break;
        }
        return loopType;
    }

    //判断moduleType是否是loopType  分为 SparkLighting类型和 Wifi315M433M 类型
    public static boolean judgeLoopType(int moduleType, int loopType) {
        boolean bRet = false;
        if (moduleType == CommonData.MODULE_TYPE_SPARKLIGHTING) {
            if (loopType == CommonData.LOOP_TYPE_LIGHT_INT
                    || loopType == CommonData.LOOP_TYPE_SWITCH_INT
                    || loopType == CommonData.LOOP_TYPE_CURTAIN_INT
                    || loopType == CommonData.LOOP_TYPE_RELAY_INT
                    || loopType == CommonData.LOOP_TYPE_SENSOR_INT) {
                bRet = true;
            }
        } else if (moduleType == CommonData.MODULE_TYPE_WIFI315M433M) {
            if (loopType == CommonData.LOOP_TYPE_SWITCH_INT
                    || loopType == CommonData.LOOP_TYPE_CURTAIN_INT
                    || loopType == CommonData.LOOP_TYPE_5800PIRAP_INT
                    || loopType == CommonData.LOOP_TYPE_5804EU_INT
                    || loopType == CommonData.LOOP_TYPE_5816EU_INT) {
                bRet = true;
            }
        }
        return bRet;
    }

    //将loopType字符串转换为数字
    public static int getIntLoopType(String type) {
        int loopType = -1;
        if (null == type || "".equals(type)) {
            return loopType;
        }
        if (type.equals(CommonData.LOOP_TYPE_LIGHT)) {
            loopType = CommonData.LOOP_TYPE_LIGHT_INT;
        } else if (type.equals(CommonData.LOOP_TYPE_CURTAIN)) {
            loopType = CommonData.LOOP_TYPE_CURTAIN_INT;
        } else if (type.equals(CommonData.LOOP_TYPE_RELAY)) {
            loopType = CommonData.LOOP_TYPE_RELAY_INT;
        } else if (type.equals(CommonData.LOOP_TYPE_SWITCH)) {
            loopType = CommonData.LOOP_TYPE_SWITCH_INT;
        } else if (type.equals(CommonData.LOOP_TYPE_SENSOR)) {
            loopType = CommonData.LOOP_TYPE_SENSOR_INT;
        } else if (type.equals(CommonData.SENSOR_TYPE_433_INFRADE)) {
            loopType = CommonData.LOOP_TYPE_5800PIRAP_INT;
        } else if (type.equals(CommonData.SENSOR_TYPE_433_DOORMAGNETI)) {
            loopType = CommonData.LOOP_TYPE_5804EU_INT;
        } else if (type.equals(CommonData.SENSOR_TYPE_433_KEYFOB)) {
            loopType = CommonData.LOOP_TYPE_5816EU_INT;
        }
        return loopType;
    }

    //将Moduletype 字符串 转换为 数字
    public static int getIntegerModuleType(String mType) {
        int moduleType = 0;
        if (null == mType) {
            return moduleType;
        }
        if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT)) {
            moduleType = CommonData.MODULE_TYPE_SPARKLIGHTING;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_BACNET)) {
            moduleType = CommonData.MODULE_TYPE_BACNET;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_IPC)) {
            moduleType = CommonData.MODULE_TYPE_IPC;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_IR)) {
            moduleType = CommonData.MODULE_TYPE_WIFIIR;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_AIR)) {
            moduleType = CommonData.MODULE_TYPE_WIFIAIR;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_RELAY)) {
            moduleType = CommonData.MODULE_TYPE_WIFIRELAY;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_485)) {
            moduleType = CommonData.MODULE_TYPE_WIFI485;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE)) {
            moduleType = CommonData.MODULE_TYPE_WIREDZONE;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_315M433)) {
            moduleType = CommonData.MODULE_TYPE_WIFI315M433M;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_IPVDP)) {
            moduleType = CommonData.MODULE_TYPE_IPVDP;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_SCENARIO)) {
            moduleType = CommonData.MODULE_TYPE_SCENARIO;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_ALARMZONE)) {
            moduleType = CommonData.MODULE_TYPE_ALARMZONE;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO)) {
            moduleType = CommonData.MODULE_TYPE_BACKAUDIO;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_SCENARIOTRIGGER)) {
            moduleType = CommonData.MODULE_TYPE_SCENARIOTRIGGER;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_MUTEXRULE)) {
            moduleType = CommonData.MODULE_TYPE_MUTEXRULE;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_SCHEDULERULE)) {
            moduleType = CommonData.MODULE_TYPE_SCHEDULERULE;
        } else if (mType.equals(CommonData.JSON_COMMAND_MODULETYPE_ROOM)) {
            moduleType = CommonData.MODULE_TYPE_ROOM;
        }
        return moduleType;
    }

    // 将ModuleType转换为字符串
    public static String getStringModuleType(int mType) {
        String moduleType = "";
        switch (mType) {
            case CommonData.MODULE_TYPE_SPARKLIGHTING:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT;
                break;
            case CommonData.MODULE_TYPE_BACNET:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_BACNET;
                break;
            case CommonData.MODULE_TYPE_IPC:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_IPC;
                break;
            case CommonData.MODULE_TYPE_WIFIIR:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_IR;
                break;
            case CommonData.MODULE_TYPE_WIFIAIR:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_AIR;
                break;
            case CommonData.MODULE_TYPE_WIFIRELAY:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_RELAY;
                break;
            case CommonData.MODULE_TYPE_WIFI485:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_485;
                break;
            case CommonData.MODULE_TYPE_WIREDZONE:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE;
                break;
            case CommonData.MODULE_TYPE_WIFI315M433M:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_315M433;
                break;
            case CommonData.MODULE_TYPE_IPVDP:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_IPVDP;
                break;
            case CommonData.MODULE_TYPE_SCENARIO:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_SCENARIO;
                break;
            case CommonData.MODULE_TYPE_ALARMZONE:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_ALARMZONE;
                break;
            case CommonData.MODULE_TYPE_BACKAUDIO:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO;
                break;
            case CommonData.MODULE_TYPE_SCENARIOTRIGGER:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_SCENARIOTRIGGER;
                break;
            case CommonData.MODULE_TYPE_MUTEXRULE:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_MUTEXRULE;
                break;
            case CommonData.MODULE_TYPE_SCHEDULERULE:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_SCHEDULERULE;
                break;
            case CommonData.MODULE_TYPE_ROOM:
                moduleType = CommonData.JSON_COMMAND_MODULETYPE_ROOM;
                break;
            default:
                moduleType = "error moduletype";
                break;
        }
        return moduleType;
    }

    //判断是不是wifi module
    public static boolean judgeIsWifiModule(int moduleType) {
        boolean bRet = false;
        if (moduleType >= CommonData.MODULE_TYPE_WIFIAIR && moduleType <= CommonData.MODULE_TYPE_WIFI315M433M) {
            bRet = true;
        } else if (moduleType == CommonData.MODULE_TYPE_IPVDP) {
            bRet = true;
        }
        return bRet;
    }

    //当前时间
    public static String curFullTime() {
        Date curDate = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        String strCurDate = new String(
                cal.get(Calendar.YEAR) + "." +
                        String.format("%02d", cal.get(Calendar.MONTH) + 1) + "." +
                        String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + "_" +
                        String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + "." +
                        String.format("%02d", cal.get(Calendar.MINUTE)) + "." +
                        String.format("%02d", cal.get(Calendar.SECOND))
        );
        return strCurDate;
    }

    //解析String为JSON
    public static JSONObject parseOrganizeStr(String organizeStr) throws JSONException {
        if (CommonUtils.ISNULL(organizeStr)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        String[] keyValueArray = organizeStr.split(CommonData.SECOND_SEPARATOR);
        if (null == keyValueArray) {
            return null;
        }
        for (int i = 0; i < keyValueArray.length; i++) {
            String keyValueStr = keyValueArray[i];
            String[] keyvaluetemp = keyValueStr.split(CommonData.FIRST_SEPARATOR);
            if (null != keyvaluetemp && keyvaluetemp.length >= 2) {
                if (keyvaluetemp[0].equals(CommonData.JSON_COMMAND_CUSTOMIZEDAYS)) {
                    JSONArray jsonArray = new JSONArray();
                    String daysSplitString = keyvaluetemp[1];
                    String[] days = daysSplitString.split("\\" + CommonData.DAY_SEPARATOR);
                    if (null != days && days.length > 0) {
                        for (int j = 0; j < days.length; j++) {
                            JSONObject dayJsonObject = new JSONObject();
                            dayJsonObject.put(CommonData.FREQUENCY_CUSTOMIZE_DAY, days[j]);
                            jsonArray.put(j, dayJsonObject);
                        }
                    }
                    jsonObject.put(keyvaluetemp[0], jsonArray);
                }
                //主要针对backaudio命令的发送
                else if (keyvaluetemp[0].equals(CommonData.JSON_COMMAND_CONTROLMAP)) {
                    JSONArray jsonArray = new JSONArray();
                    String[] controlsStrings = keyvaluetemp[1].split("\\" + CommonData.DAY_SEPARATOR);
                    if (null != controlsStrings && controlsStrings.length > 0) {
                        for (int j = 0; j < controlsStrings.length; j++) {
                            JSONObject dayJsonObject = new JSONObject();
                            String[] mapsStrings = controlsStrings[j].split(CommonData.CONTROLMAP_SEPARATOR);
                            if (null != mapsStrings && mapsStrings.length >= 2) {
                                dayJsonObject.put(CommonData.JSON_COMMAND_KEYTYPE, mapsStrings[0]);
                                dayJsonObject.put(CommonData.JSON_COMMAND_KEYVALUE, mapsStrings[1]);
                            }
                            jsonArray.put(j, dayJsonObject);
                        }
                    }
                    jsonObject.put(keyvaluetemp[0], jsonArray);
                } else {
                    jsonObject.put(keyvaluetemp[0], keyvaluetemp[1]);
                }
            }
        }
        return jsonObject;
    }

    public static void parseOrganizeStr(String organizeStr, JSONObject jsonObject) throws JSONException {
        if (CommonUtils.ISNULL(organizeStr) || null == jsonObject) {
            return;
        }
        String[] keyValueArray = organizeStr.split(CommonData.SECOND_SEPARATOR);
        if (null == keyValueArray) {
            return;
        }
        for (int i = 0; i < keyValueArray.length; i++) {
            String keyValueStr = keyValueArray[i];
            String[] keyvaluetemp = keyValueStr.split(CommonData.FIRST_SEPARATOR);
            if (null != keyvaluetemp && keyvaluetemp.length >= 2) {
                if (keyvaluetemp[0].equals(CommonData.JSON_COMMAND_CUSTOMIZEDAYS)) {
                    JSONArray jsonArray = new JSONArray();
                    String daysSplitString = keyvaluetemp[1];
                    String[] days = daysSplitString.split("\\" + CommonData.DAY_SEPARATOR);
                    if (null != days && days.length > 0) {
                        for (int j = 0; j < days.length; j++) {
                            JSONObject dayJsonObject = new JSONObject();
                            dayJsonObject.put(CommonData.FREQUENCY_CUSTOMIZE_DAY, days[j]);
                            jsonArray.put(j, dayJsonObject);
                        }
                    }
                    jsonObject.put(keyvaluetemp[0], jsonArray);
                }
                //主要针对backaudio命令的发送
                else if (keyvaluetemp[0].equals(CommonData.JSON_COMMAND_CONTROLMAP)) {
                    JSONArray jsonArray = new JSONArray();
                    String[] controlsStrings = keyvaluetemp[1].split("\\" + CommonData.DAY_SEPARATOR);
                    if (null != controlsStrings && controlsStrings.length > 0) {
                        for (int j = 0; j < controlsStrings.length; j++) {
                            JSONObject dayJsonObject = new JSONObject();
                            String[] mapsStrings = controlsStrings[j].split("\\" + CommonData.CONTROLMAP_SEPARATOR);
                            if (null != mapsStrings && mapsStrings.length >= 2) {
                                dayJsonObject.put(CommonData.JSON_COMMAND_KEYTYPE, mapsStrings[0]);
                                dayJsonObject.put(CommonData.JSON_COMMAND_KEYVALUE, mapsStrings[1]);
                            }
                            jsonArray.put(j, dayJsonObject);
                        }
                    }
                    jsonObject.put(keyvaluetemp[0], jsonArray);
                } else {
                    jsonObject.put(keyvaluetemp[0], keyvaluetemp[1]);
                }
            }
        }
    }


    //将JSON转换为String
    public static final int VALUE_INT = 0;
    public static final int VALUE_STRING = 1;
    public static final int VALUE_LONG = 2;
    public static final int VALUE_OBJECT = 3;
    public static final int VALUE_ARRAY = 4;

    public static String getAndOrganizeStr(JSONObject jsonItem, boolean isOnlyControl) throws JSONException {
        String subAction = "";
        if (null == jsonItem) {
            return subAction;
        }
        HashMap<String, Object> maps = new HashMap<String, Object>();
        if (!isOnlyControl) {
            //parse all module and loop info
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_MODULETYPE, VALUE_STRING, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_MODULEIPADDR, VALUE_STRING, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_MODULEMACADDR, VALUE_STRING, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_BACNETDEVID, VALUE_INT, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_SERIALNO, VALUE_STRING, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_DEVID, VALUE_INT, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_MASKID, VALUE_INT, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_LOOPID, VALUE_INT, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_LOOPTYPE, VALUE_STRING, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_TYPE, VALUE_STRING, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_MODULEPORT, VALUE_INT, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_PORTID, VALUE_INT, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_MUTEXID, VALUE_INT, maps);
            putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_DESCRITPION, VALUE_STRING, maps);
        }
        //parse all control info
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_KEYTYPE, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_KEYVALUE, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_TIMER, VALUE_INT, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_DATA + "1", VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_DATA + "2", VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_NAME, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_SWITCHSTATUS, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_STARTTIME, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_ENDTIME, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_FREQUENCY, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_PERCENT, VALUE_INT, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_SETTEMP, VALUE_INT, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_FREQUENCY, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_AIRCONMODE, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_FANSPEED, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_SLAVEADDR, VALUE_INT, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_CURRTEMP, VALUE_INT, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_POWER, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_VOLUME, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_PLAYSTATUS, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_SINGLECYCLE, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_SOURCE, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_SWITCHSONG, VALUE_STRING, maps);
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_MUTE, VALUE_STRING, maps);

        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_CUSTOMIZEDAYS, VALUE_ARRAY, maps);
        //针对backaudio 命令组织的形式，特殊方式！！！！
        putKeyAndValueToMap(jsonItem, CommonData.JSON_COMMAND_CONTROLMAP, VALUE_ARRAY, maps);


        StringBuffer stringBuffer = new StringBuffer();
        Set<String> set = maps.keySet();
        for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            Object valueObject = maps.get(key);
            String value = null;
            if (valueObject instanceof Integer) {
                int temp = (Integer) maps.get(key);
                value = String.valueOf(temp);
            } else {
                value = (String) maps.get(key);
            }
            stringBuffer.append(key).append(CommonData.FIRST_SEPARATOR).append(value).append(CommonData.SECOND_SEPARATOR);
        }
        int length = stringBuffer.length();
        if (length > 0) {
            stringBuffer.deleteCharAt(length - 1);
            subAction = stringBuffer.toString();
        }
        return subAction;
    }

    private static void putKeyAndValueToMap(JSONObject jsonObject, String key, int keyType, HashMap<String, Object> maps) {
        if (null == jsonObject || null == key || null == maps) {
            return;
        }
        Object object = null;
        if (!jsonObject.has(key)) {
            return;
        }
        try {
            switch (keyType) {
                case VALUE_INT:
                    object = jsonObject.getInt(key);
                    break;
                case VALUE_STRING:
                    object = jsonObject.getString(key);
                    break;
                case VALUE_LONG:
                    object = jsonObject.getLong(key);
                    break;
                case VALUE_ARRAY:
                    JSONArray array = jsonObject.getJSONArray(key);
                    StringBuffer sBuffer = new StringBuffer();
                    if (null != array) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject arrayObject = array.getJSONObject(i);
                            if (key.equals(CommonData.JSON_COMMAND_CUSTOMIZEDAYS)) {
                                //for schedule customizeday format
                                String day = arrayObject.getString(CommonData.FREQUENCY_CUSTOMIZE_DAY);
                                sBuffer.append(day);
                            } else if (key.equals(CommonData.JSON_COMMAND_CONTROLMAP)) {
                                String type = arrayObject.getString(CommonData.JSON_COMMAND_KEYTYPE);
                                String value = arrayObject.getString(CommonData.JSON_COMMAND_KEYVALUE);
                                sBuffer.append(type).append(CommonData.CONTROLMAP_SEPARATOR)
                                        .append(value);
                            }
                            sBuffer.append(CommonData.DAY_SEPARATOR);
                        }
                        int length = sBuffer.length();
                        if (length > 0) {
                            sBuffer.deleteCharAt(length - 1);
                            object = sBuffer.toString();
                        }
                    }
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != object) {
            maps.put(key, object);
        }
    }

    //发送Rule配置通知
    public static void sendRuleConfigNotify(Context context, int moduleType, String configType, long triggerId) throws JSONException {
        if (CommonUtils.ISNULL(configType) || triggerId <= 0) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonData.JSON_COMMAND_ACTION, CommonData.JSON_COMMAND_ACTION_EVENT);
        jsonObject.put(CommonData.JSON_COMMAND_SUBACTION, configType);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        String action = null;
        switch (moduleType) {
            case CommonData.MODULE_TYPE_SCENARIOTRIGGER:
                action = CommonData.ACTION_DEV_MODIFYTRIGGERRULECONFIG;
                jsonObject2.put(CommonData.JSON_COMMAND_TRIGGERID, triggerId);
                break;
            case CommonData.MODULE_TYPE_MUTEXRULE:
                action = CommonData.ACTION_DEV_MODIFYMUTEXRULECONFIG;
                jsonObject2.put(CommonData.JSON_COMMAND_MUTEXID, triggerId);
                break;
            case CommonData.MODULE_TYPE_SCHEDULERULE:
                action = CommonData.ACTION_DEV_MODIFYSCHEDULERULECONFIG;
                jsonObject2.put(CommonData.JSON_COMMAND_SCHEDULEID, triggerId);
                break;
            default:
                break;
        }
        if (CommonUtils.ISNULL(action)) {
            return;
        }
        jsonArray.put(jsonObject2);
        jsonObject.put(CommonData.JSON_COMMAND_CONFIGDATA, jsonArray);
        Intent intent = new Intent(action);
        intent.putExtra(CommonData.EXTRA_DATA_NAME, jsonObject.toString().getBytes());
        intent.putExtra(CommonData.EXTRA_DATA_FROM, CommonData.EXTRA_DATA_FROM_CONFIGSERVICE);
        context.sendBroadcast(intent);
    }

    //通过PrimaryId 和 ModuleType 来获取（查询）对应的Loop
    public static BasicLoop getBasicLoopFromLoopPrimaryId(Context context, int moduleType, long primaryId) {
        BasicLoop loop = null;
        if (null == context || moduleType <= 0 || primaryId <= 0) {
            return loop;
        }
        ConfigCubeDatabaseHelper instance = ConfigCubeDatabaseHelper.getInstance(context);
        switch (moduleType) {
            case CommonData.MODULE_TYPE_SPARKLIGHTING:
                loop = new SparkLightingLoopFunc(instance).getSparkLightingLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_BACNET:
                loop = new BacnetLoopFunc(instance).getBacnetLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_IPC:
                break;
            case CommonData.MODULE_TYPE_WIFIIR:
                loop = new IrLoopFunc(instance).getIrLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_WIFIAIR:
                break;
            case CommonData.MODULE_TYPE_WIFIRELAY:
                loop = new RelayLoopFunc(instance).getRelayLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_WIFI485:
                loop = new Wifi485LoopFunc(instance).getWifi485LoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_WIREDZONE:
                loop = new WiredZoneLoopFunc(instance).getWiredZoneLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_WIFI315M433M:
                loop = new Wireless315M433MLoopFunc(instance).getWireless315M433MLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_IPVDP:
                loop = new IpvdpZoneLoopFunc(instance).getIpvdpZoneLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_SCENARIO:
                break;
            case CommonData.MODULE_TYPE_ALARMZONE:
                break;
            case CommonData.MODULE_TYPE_BACKAUDIO:
                loop = new BackaudioLoopFunc(instance).getBackaudioLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_SCENARIOTRIGGER:
                break;
            case CommonData.MODULE_TYPE_MUTEXRULE:
                break;
            case CommonData.MODULE_TYPE_SCHEDULERULE:
                break;
            default:
                break;
        }
        return loop;
    }


    //将ActionInfo的String 转换为JSON 并 放到 JSON Array 中
    public static void putActionInfo(JSONObject arrayObject, String jsonString) {
        try {
            if (null == arrayObject || CommonUtils.ISNULL(jsonString)) {
                return;
            }
            JSONObject actionInfoObject = new JSONObject(jsonString);
            Iterator<String> keys = actionInfoObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                arrayObject.put(key, actionInfoObject.opt(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean putAllModuleAndLoopInfoForScenarioInfo(JSONObject jsonObject, Context context, int moduleType, long primaryId) throws JSONException {
        boolean isZoneLoop = false;
        if (null == jsonObject || null == context || moduleType <= 0 || primaryId <= 0) {
            return isZoneLoop;
        }
        BasicLoop loop = new BasicLoop();
        ConfigCubeDatabaseHelper instance = ConfigCubeDatabaseHelper.getInstance(context);
        switch (moduleType) {
            case CommonData.MODULE_TYPE_SPARKLIGHTING:
                SparkLightingLoop sparkLightingLoop = new SparkLightingLoopFunc(instance).getSparkLightingLoopByPrimaryId(primaryId);
                loop = sparkLightingLoop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_SUBDEVTYPE, sparkLightingLoop.mSubDevType);
                    jsonObject.put(CommonData.JSON_COMMAND_DEVID, loop.mSubDevId);
                    jsonObject.put(CommonData.JSON_COMMAND_LOOPTYPE, getStringLoopType(sparkLightingLoop.mLoopType));
                    if (null != sparkLightingLoop.mSubDevType &&
                            sparkLightingLoop.mSubDevType.equals(CommonData.SPARKLIGHT_SUBDEVTYPE_HBLSSIR)) {
                        isZoneLoop = true;
                    }
                }
                break;
            case CommonData.MODULE_TYPE_BACNET:
                BacnetLoop backnetLoop = new BacnetLoopFunc(instance).getBacnetLoopByPrimaryId(primaryId);
                loop = backnetLoop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_DEVID, backnetLoop.mSubDevId);
                }
                break;
            case CommonData.MODULE_TYPE_IPC:
                break;
            case CommonData.MODULE_TYPE_WIFIIR:
            /*IrLoop irLoop = new IrLoopFuc(instance).getIrLoopByPrimaryId(primaryId);
            loop = irLoop;
            if(null != loop){
                jsonObject.put(CommonData.JSON_COMMAND_TYPE, irLoop.mLoopType);
            }*/
                IrCode irCode = new IrCodeFunc(instance).getIrCodeByPrimaryId(primaryId);
                if (null == irCode) {
                    break;
                }
                JSONArray irDataArray = new JSONArray();
                JSONObject data1Object = new JSONObject();
                data1Object.put(CommonData.JSON_COMMAND_DATA, irCode.mData1);
                irDataArray.put(data1Object);

                JSONObject data2Object = new JSONObject();
                data2Object.put(CommonData.JSON_COMMAND_DATA, irCode.mData2);
                irDataArray.put(data2Object);

                jsonObject.put(CommonData.JSON_COMMAND_WIFIIRDATA, irDataArray);

                IrLoop irLoop = new IrLoopFunc(instance).getIrLoopByPrimaryId(irCode.mLoopId);
                loop = irLoop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_TYPE, irLoop.mLoopType);
                }
                break;
            case CommonData.MODULE_TYPE_WIFIAIR:
                //这个
                break;
            case CommonData.MODULE_TYPE_WIFIRELAY:
                RelayLoop relayLoop = new RelayLoopFunc(instance).getRelayLoopByPrimaryId(primaryId);
                loop = relayLoop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_TIMER, relayLoop.mTriggerTime);
                }
                break;
            case CommonData.MODULE_TYPE_WIFI485:
                Wifi485Loop wifi485Loop = new Wifi485LoopFunc(instance).getWifi485LoopByPrimaryId(primaryId);
                loop = wifi485Loop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_BRANDNAME, wifi485Loop.mBrandName);
                    jsonObject.put(CommonData.JSON_COMMAND_PORTID, wifi485Loop.mPortId);
                    jsonObject.put(CommonData.JSON_COMMAND_LOOPTYPE, wifi485Loop.mLoopType);
                    jsonObject.put(CommonData.JSON_COMMAND_SLAVEADDR, wifi485Loop.mSlaveAddr);
                }
                break;
            case CommonData.MODULE_TYPE_WIREDZONE:
                WiredZoneLoop wireZoneLoop = new WiredZoneLoopFunc(instance).getWiredZoneLoopByPrimaryId(primaryId);
                loop = wireZoneLoop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_ZONETYPE, wireZoneLoop.mZoneType);
                    jsonObject.put(CommonData.JSON_COMMAND_ALARMTIMER, wireZoneLoop.mDelayTimer);
                    isZoneLoop = true;
                }
                break;
            case CommonData.MODULE_TYPE_WIFI315M433M:
                Wireless315M433MLoop wireless315m433mLoop = new Wireless315M433MLoopFunc(instance).getWireless315M433MLoopByPrimaryId(primaryId);
                loop = wireless315m433mLoop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_DEVICETYPE, wireless315m433mLoop.mDeviceType);
                    if (wireless315m433mLoop.mDeviceType.equals(CommonData.DEVICETYPE_MAIA2)) {
                        jsonObject.put(CommonData.JSON_COMMAND_DEVID, wireless315m433mLoop.mSubDevId);
                    } else if (wireless315m433mLoop.mDeviceType.equals(CommonData.DEVICETYPE_SENSOR)) {
                        jsonObject.put(CommonData.JSON_COMMAND_ZONETYPE, wireless315m433mLoop.mZoneType);
                        jsonObject.put(CommonData.JSON_COMMAND_ALARMTIMER, wireless315m433mLoop.mDelayTimer);
                        isZoneLoop = true;
                    }
                    jsonObject.put(CommonData.JSON_COMMAND_LOOPTYPE, getStringLoopType(wireless315m433mLoop.mLoopType));
                }
                break;
            case CommonData.MODULE_TYPE_IPVDP:
                IpvdpZoneLoop ipvdpZoneLoop = new IpvdpZoneLoopFunc(instance).getIpvdpZoneLoopByPrimaryId(primaryId);
                loop = ipvdpZoneLoop;
                if (null != loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_ZONETYPE, ipvdpZoneLoop.mZoneType);
                    jsonObject.put(CommonData.JSON_COMMAND_ALARMTIMER, ipvdpZoneLoop.mDelayTimer);
                    isZoneLoop = true;
                }
                break;
            case CommonData.MODULE_TYPE_SCENARIO:
                break;
            case CommonData.MODULE_TYPE_ALARMZONE:
                break;
            case CommonData.MODULE_TYPE_BACKAUDIO:
                loop = new BackaudioLoopFunc(instance).getBackaudioLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_SCENARIOTRIGGER:
                break;
            case CommonData.MODULE_TYPE_MUTEXRULE:
                break;
            case CommonData.MODULE_TYPE_SCHEDULERULE:
                break;
            default:
                break;
        }
        if (null == loop) {
            return isZoneLoop;
        }
        PeripheralDevice device = new PeripheralDeviceFunc(instance).getPeripheralByPrimaryId(loop.mModulePrimaryId);
        if (null == device && moduleType != CommonData.MODULE_TYPE_BACKAUDIO) {
            return isZoneLoop;
        }
        BackaudioDevice backaudioDevice = null;

        if (!jsonObject.has(CommonData.JSON_COMMAND_MODULETYPE)) {
            jsonObject.put(CommonData.JSON_COMMAND_MODULETYPE, CommonUtils.getStringModuleType(moduleType));
        }
        if (moduleType == CommonData.MODULE_TYPE_BACNET && null != device) {
            jsonObject.put(CommonData.JSON_COMMAND_BRANDNAME, device.mBrandName);
            jsonObject.put(CommonData.JSON_COMMAND_BACNETDEVID, device.mBacnetId);
        }
        if (moduleType == CommonData.MODULE_TYPE_SPARKLIGHTING && null != device) {
            jsonObject.put(CommonData.JSON_COMMAND_MODULEIPADDR, device.mIpAddr);
            jsonObject.put(CommonData.JSON_COMMAND_MASKID, device.mMaskId);
        } else if (moduleType == CommonData.MODULE_TYPE_BACKAUDIO) {
            backaudioDevice = new BackaudioDeviceFunc(instance).getBackaudioDeviceByPrimaryId(loop.mModulePrimaryId);
            if (null == backaudioDevice) {
                return isZoneLoop;
            }
            jsonObject.put(CommonData.JSON_COMMAND_MODULESERIALNUM, backaudioDevice.mSerialNumber);
        } else {
            if (null != device) {
                jsonObject.put(CommonData.JSON_COMMAND_MODULEMACADDR, device.mMacAddr);
            }
        }
        jsonObject.put(CommonData.JSON_COMMAND_ALIAS, loop.mLoopName);
        jsonObject.put(CommonData.JSON_COMMAND_ROOMNAME, loop.mRoomId);
        jsonObject.put(CommonData.JSON_COMMAND_LOOPID, loop.mLoopId);
        return isZoneLoop;
    }

    public static boolean putAllModuleAndLoopInfo(JSONObject jsonObject, Context context, int moduleType, long primaryId) throws JSONException {
        boolean isZoneLoop = false;
        if (null == jsonObject || null == context || moduleType <= 0 || primaryId <= 0) {
            return isZoneLoop;
        }
        BasicLoop loop = new BasicLoop();
        ConfigCubeDatabaseHelper instance = ConfigCubeDatabaseHelper.getInstance(context);
        switch (moduleType) {
            case CommonData.MODULE_TYPE_SPARKLIGHTING:
                SparkLightingLoop sparkLightingLoop = new SparkLightingLoopFunc(instance).getSparkLightingLoopByPrimaryId(primaryId);
                loop = sparkLightingLoop;
                if (null != sparkLightingLoop) {
                    jsonObject.put(CommonData.JSON_COMMAND_SUBDEVTYPE, sparkLightingLoop.mSubDevType);
                    jsonObject.put(CommonData.JSON_COMMAND_DEVID, sparkLightingLoop.mSubDevId);
                    jsonObject.put(CommonData.JSON_COMMAND_LOOPTYPE, getStringLoopType(sparkLightingLoop.mLoopType));
                    if (null != sparkLightingLoop.mSubDevType &&
                            sparkLightingLoop.mSubDevType.equals(CommonData.SPARKLIGHT_SUBDEVTYPE_HBLSSIR)) {
                        isZoneLoop = true;
                    }
                }
                break;
            case CommonData.MODULE_TYPE_BACNET:
                BacnetLoop backnetLoop = new BacnetLoopFunc(instance).getBacnetLoopByPrimaryId(primaryId);
                loop = backnetLoop;
                if (null != backnetLoop) {
                    jsonObject.put(CommonData.JSON_COMMAND_DEVID, backnetLoop.mSubDevId);
                }
                break;
            case CommonData.MODULE_TYPE_IPC:
                //特殊处理
                IpcStreamInfo ipcStreamInfo = new IpcStreamInfoFunc(instance).getIpcStreamInfoByPrimaryId(primaryId);
                if (null != ipcStreamInfo) {
                    PeripheralDevice device = new PeripheralDeviceFunc(instance).getPeripheralByPrimaryId(ipcStreamInfo.mDevId);
                    if (null != device) {
                        jsonObject.put(CommonData.JSON_COMMAND_MODULETYPE, CommonUtils.getStringModuleType(moduleType));
                        jsonObject.put(CommonData.JSON_COMMAND_MODULEIPADDR, device.mIpAddr);
                        jsonObject.put(CommonData.JSON_COMMAND_MODULEPORT, ipcStreamInfo.mStreamPort);
                        jsonObject.put(CommonData.JSON_COMMAND_IPCURL, ipcStreamInfo.mSubStream);
                    }
                }
                break;
            case CommonData.MODULE_TYPE_WIFIIR:
                IrLoop irLoop = new IrLoopFunc(instance).getIrLoopByPrimaryId(primaryId);
                loop = irLoop;
                if (null != irLoop) {
                    jsonObject.put(CommonData.JSON_COMMAND_TYPE, irLoop.mLoopType);
                }
                break;
            case CommonData.MODULE_TYPE_WIFIAIR:
                break;
            case CommonData.MODULE_TYPE_WIFIRELAY:
                RelayLoop relayLoop = new RelayLoopFunc(instance).getRelayLoopByPrimaryId(primaryId);
                loop = relayLoop;
                if (null != relayLoop) {
                    jsonObject.put(CommonData.JSON_COMMAND_TIMER, relayLoop.mTriggerTime);
                }
                break;
            case CommonData.MODULE_TYPE_WIFI485:
                Wifi485Loop wifi485Loop = new Wifi485LoopFunc(instance).getWifi485LoopByPrimaryId(primaryId);
                loop = wifi485Loop;
                if (null != wifi485Loop) {
                    jsonObject.put(CommonData.JSON_COMMAND_BRANDNAME, wifi485Loop.mBrandName);
                    jsonObject.put(CommonData.JSON_COMMAND_PORTID, wifi485Loop.mPortId);
                    jsonObject.put(CommonData.JSON_COMMAND_LOOPTYPE, wifi485Loop.mLoopType);
                    jsonObject.put(CommonData.JSON_COMMAND_SLAVEADDR, wifi485Loop.mSlaveAddr);
                }
                break;
            case CommonData.MODULE_TYPE_WIREDZONE:
                WiredZoneLoop wireZoneLoop = new WiredZoneLoopFunc(instance).getWiredZoneLoopByPrimaryId(primaryId);
                loop = wireZoneLoop;
                if (null != wireZoneLoop) {
                    jsonObject.put(CommonData.JSON_COMMAND_ZONETYPE, wireZoneLoop.mZoneType);
                    jsonObject.put(CommonData.JSON_COMMAND_ALARMTIMER, wireZoneLoop.mDelayTimer);
                    isZoneLoop = true;
                }
                break;
            case CommonData.MODULE_TYPE_WIFI315M433M:
                Wireless315M433MLoop wireless315m433mLoop = new Wireless315M433MLoopFunc(instance).getWireless315M433MLoopByPrimaryId(primaryId);
                loop = wireless315m433mLoop;
                if (null != wireless315m433mLoop) {
                    jsonObject.put(CommonData.JSON_COMMAND_DEVICETYPE, wireless315m433mLoop.mDeviceType);
                    if (wireless315m433mLoop.mDeviceType.equals(CommonData.DEVICETYPE_MAIA2)) {
                        jsonObject.put(CommonData.JSON_COMMAND_DEVID, wireless315m433mLoop.mSubDevId);
                    } else if (wireless315m433mLoop.mDeviceType.equals(CommonData.DEVICETYPE_SENSOR)) {
                        jsonObject.put(CommonData.JSON_COMMAND_ZONETYPE, wireless315m433mLoop.mZoneType);
                        jsonObject.put(CommonData.JSON_COMMAND_ALARMTIMER, wireless315m433mLoop.mDelayTimer);
                        isZoneLoop = true;
                    }
                    jsonObject.put(CommonData.JSON_COMMAND_LOOPTYPE, getStringLoopType(wireless315m433mLoop.mLoopType));
                }
                break;
            case CommonData.MODULE_TYPE_IPVDP:
                IpvdpZoneLoop ipvdpZoneLoop = new IpvdpZoneLoopFunc(instance).getIpvdpZoneLoopByPrimaryId(primaryId);
                loop = ipvdpZoneLoop;
                if (null != ipvdpZoneLoop) {
                    jsonObject.put(CommonData.JSON_COMMAND_ZONETYPE, ipvdpZoneLoop.mZoneType);
                    jsonObject.put(CommonData.JSON_COMMAND_ALARMTIMER, ipvdpZoneLoop.mDelayTimer);
                    isZoneLoop = true;
                }
                break;
            case CommonData.MODULE_TYPE_SCENARIO:
                break;
            case CommonData.MODULE_TYPE_ALARMZONE:
                break;
            case CommonData.MODULE_TYPE_BACKAUDIO:
                loop = new BackaudioLoopFunc(instance).getBackaudioLoopByPrimaryId(primaryId);
                break;
            case CommonData.MODULE_TYPE_SCENARIOTRIGGER:
                break;
            case CommonData.MODULE_TYPE_MUTEXRULE:
                break;
            case CommonData.MODULE_TYPE_SCHEDULERULE:
                break;
            default:
                break;
        }
        if (null == loop) {
            return isZoneLoop;
        }
        PeripheralDevice device = new PeripheralDeviceFunc(instance).getPeripheralByPrimaryId(loop.mModulePrimaryId);
        if (null == device && moduleType != CommonData.MODULE_TYPE_BACKAUDIO) {
            return isZoneLoop;
        }
        BackaudioDevice backaudioDevice = null;

        if (!jsonObject.has(CommonData.JSON_COMMAND_MODULETYPE)) {
            jsonObject.put(CommonData.JSON_COMMAND_MODULETYPE, CommonUtils.getStringModuleType(moduleType));
        }
        if (moduleType == CommonData.MODULE_TYPE_BACNET) {
            jsonObject.put(CommonData.JSON_COMMAND_BRANDNAME, (null == device) ? "" : device.mBrandName);
            jsonObject.put(CommonData.JSON_COMMAND_BACNETDEVID, (null == device) ? "" : device.mBacnetId);
        }
        if (moduleType == CommonData.MODULE_TYPE_SPARKLIGHTING) {
            jsonObject.put(CommonData.JSON_COMMAND_MODULEIPADDR, (null == device) ? "" : device.mIpAddr);
            jsonObject.put(CommonData.JSON_COMMAND_MASKID, (null == device) ? "" : device.mMaskId);
        } else if (moduleType == CommonData.MODULE_TYPE_BACKAUDIO) {
            backaudioDevice = new BackaudioDeviceFunc(instance).getBackaudioDeviceByPrimaryId(loop.mModulePrimaryId);
            if (null == backaudioDevice) {
                return isZoneLoop;
            }
            jsonObject.put(CommonData.JSON_COMMAND_MODULESERIALNUM, backaudioDevice.mSerialNumber);
        } else {
            jsonObject.put(CommonData.JSON_COMMAND_MODULEMACADDR, device.mMacAddr);
        }

        jsonObject.put(CommonData.JSON_COMMAND_ALIAS, loop.mLoopName);
        jsonObject.put(CommonData.JSON_COMMAND_ROOMNAME, loop.mRoomId);
        jsonObject.put(CommonData.JSON_COMMAND_LOOPID, loop.mLoopId);
        return isZoneLoop;
    }


    public static int specialProcessIrAndBackAudioControl(JSONObject arrayObject, int index, JSONArray allRecordArray) throws JSONException {
        if (null == arrayObject || allRecordArray == null) {
            return index;
        }
        if (arrayObject.has(CommonData.JSON_COMMAND_DATA + 1) && arrayObject.has(CommonData.JSON_COMMAND_DATA + 2)) {
            JSONArray array = new JSONArray();
            for (int j = 1; j < 3; j++) {
                String data = arrayObject.getString(CommonData.JSON_COMMAND_DATA + j);
                arrayObject.remove(CommonData.JSON_COMMAND_DATA + j);
                JSONObject mapObject = new JSONObject();
                mapObject.put(CommonData.JSON_COMMAND_DATA, data);
                array.put(j - 1, mapObject);
            }
            arrayObject.put(CommonData.JSON_COMMAND_WIFIIRDATA, array);
        }

        if (arrayObject.has(CommonData.JSON_COMMAND_CONTROLMAP)) {
            JSONArray array = arrayObject.getJSONArray(CommonData.JSON_COMMAND_CONTROLMAP);
            arrayObject.remove(CommonData.JSON_COMMAND_CONTROLMAP);
            String arrayObjectString = arrayObject.toString();
//			allRecordArray.remove(index);
            for (int j = 0; j < array.length(); j++) {
                JSONObject jsonObject = array.getJSONObject(j);
                JSONObject mapObject = new JSONObject(arrayObjectString);
                mapObject.put(CommonData.JSON_COMMAND_KEYTYPE, jsonObject.get(CommonData.JSON_COMMAND_KEYTYPE));
                mapObject.put(CommonData.JSON_COMMAND_KEYVALUE, jsonObject.get(CommonData.JSON_COMMAND_KEYVALUE));
                allRecordArray.put(index, mapObject);
                index++;
            }

        }
        return index;
    }

    //loopname,roomname,loopid,dev_id,_id
    public static void putCommonLoopFieldForMobileApp(JSONObject arrayObject, BasicLoop basicLoop) throws JSONException {
        if (null == arrayObject || null == basicLoop) {
            return;
        }
        arrayObject.put(ConfigCubeDatabaseHelper.COLUMN_LOOPNAME, basicLoop.mLoopName);
        arrayObject.put(ConfigCubeDatabaseHelper.COLUMN_ROOM_ID, basicLoop.mRoomId);
        // ir loop has no loopid field
        if (basicLoop.mLoopId != -1 && basicLoop.mLoopId != 0) {
            arrayObject.put(ConfigCubeDatabaseHelper.COLUMN_LOOPID, basicLoop.mLoopId);
        }
        arrayObject.put(ConfigCubeDatabaseHelper.COLUMN_DEVICEID, basicLoop.mModulePrimaryId);
        arrayObject.put(ConfigCubeDatabaseHelper.COLUMN_ID, basicLoop.mLoopSelfPrimaryId);
    }

    public static boolean judgeZoneType(String zoneType) {
        boolean bRet = false;
        if (null == zoneType) {
            return bRet;
        }
        if (zoneType.equals(CommonData.ZONE_TYPE_SECURITY_INSTANT)
                || zoneType.equals(CommonData.ZONE_TYPE_SECURITY_24HOURS)
                || zoneType.equals(CommonData.ZONE_TYPE_SECURITY_DELAY)) {
            bRet = true;
        }
        return bRet;
    }

    public static boolean judgeAlarmType(String alarmType) {
        boolean bRet = false;
        if (null == alarmType) {
            return bRet;
        }
        if (alarmType.equals(CommonData.ZONE_ALARM_STATUS_GAS)
                || alarmType.equals(CommonData.ZONE_ALARM_STATUS_FIRE)
                || alarmType.equals(CommonData.ZONE_ALARM_STATUS_HELP)
                || alarmType.equals(CommonData.ZONE_ALARM_STATUS_THIEF)) {
            bRet = true;
        }
        return bRet;
    }


    /**
     * 解压缩一个文件
     *
     * @param zipFile    压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void upZipFile(File zipFile, String folderPath) {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = null;
        try {
            zf = new ZipFile(zipFile);
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null == zf) {
            return;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                //str = new String(str.getBytes("8859_1"), "GB2312");
                //File desFile = new File(str);
                File desFile = new File(str, java.net.URLEncoder.encode(
                        entry.getName(), "UTF-8"));
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                out = new FileOutputStream(desFile);
                byte buffer[] = new byte[1024 * 1024];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                in.close();
                in = null;
                out.close();
                out = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeInputStream(in);
            IOUtils.closeOutputStream(out);
            try {
                zf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件复制
     *
     * @param srcPath
     * @param dstPath
     * @return
     */
    public static boolean copyFile(String srcPath, String dstPath) {
        boolean ret = true;

        File srcFile = new File(srcPath);
        File dstFile = new File(dstPath);
        InputStream input = null;
        FileOutputStream out = null;
        try {
            dstFile.createNewFile();
            input = new FileInputStream(srcFile);
            out = new FileOutputStream(dstFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            input.close();
            out.close();
        } catch (IOException e) {
            ret = false;
            e.printStackTrace();
        } finally {
            IOUtils.closeInputStream(input);
            IOUtils.closeFileOutputStream(out);
        }

        return ret;
    }

    /**
     * 复制文件
     *
     * @param srcFile
     * @param dstFile
     * @return
     */
    public static boolean copyFile(File srcFile, File dstFile) {
        boolean ret = true;
        InputStream input = null;
        FileOutputStream out = null;
        try {
            dstFile.createNewFile();
            input = new FileInputStream(srcFile);
            out = new FileOutputStream(dstFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            ret = false;
            e.printStackTrace();
        } finally {
            IOUtils.closeInputStream(input);
            IOUtils.closeFileOutputStream(out);
        }

        return ret;
    }

    /**
     * 计算文件MD5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeFileInputStream(in);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        // Alwin: if start with '0'，it will be removed, so checkit.
        String retStr = bigInt.toString(16);
        while (retStr.length() < 32) {
            retStr = "0" + retStr;
        }
        return retStr;//bigInt.toString(16);
    }

    public static byte[] getFileMD5Bytes(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeFileInputStream(in);
        }
        return digest.digest();
    }

    public static String getFileSHA256Str(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("SHA256");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeFileInputStream(in);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String retStr = bigInt.toString(16);//16->hex string
        while (retStr.length() < 64) {
            retStr = "0" + retStr;
        }
        return retStr;//bigInt.toString(16);
    }

    public static byte[] getFileSHA256Bytes(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("SHA256");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeFileInputStream(in);
        }
        return digest.digest();
    }

    /**
     * hex string to byte array
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            len = len - 1; //maybe should return null.
        }
        if (len <= 0) {
            return null;
        }
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    /**
     * byte array to hex string
     *
     * @param bytes
     * @return
     */
    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexArray =
                {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;

        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    /**
     * 重启应用
     */
    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e("Utils", "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e("Utils", "Was not able to restart application, PM null");
                }
            } else {
                Log.e("Utils", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e("Utils", "Was not able to restart application");
        }
    }


    public static boolean judgeIsSymbols(String text) {
        boolean bRet = false;
        if (CommonUtils.ISNULL(text)) {
            return bRet;
        }
        if (text.equals("，") || text.equals("。") || text.equals("！") || text.equals("：") || text.equals("；")) {
            bRet = true;
        }
        return bRet;
    }

    //获取本地IP
    public static String getLocalIpAddr(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo info = connectMgr.getActiveNetworkInfo();
        boolean flag = false;
        if (connectMgr.getActiveNetworkInfo() != null) {
            flag = connectMgr.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            return null;
        }
        NetworkInfo.State wifi = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi == NetworkInfo.State.CONNECTED) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        } else {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//    public static void putEnvironmentParam(Context context, String roomName, int moduleType, int subGatewayId, int loopId, String paramtype) {
//        if (null == context || CommonUtils.ISNULL(roomName) || CommonUtils.ISNULL(paramtype)) {
//            return;
//        }
//        RoomLoop roomLoop = RoomLoopFunc.getInstance(context).getRoomLoopByRoomName(roomName);
//        if (null != roomLoop) {
//            EnvironmentLoop environmentLoop = EnvironmentLoopFunc.getInstance(context).getEnvironmentLoop(roomLoop.mId, paramtype);
//            if (null == environmentLoop) {
//                EnvironmentLoopFunc.getInstance(context).addEnvironmentLoop(
//                        new EnvironmentLoop(roomLoop.mId, moduleType, subGatewayId, loopId, paramtype), true);
//            } else {
//                Log.w(TAG, "exit paramtype in this room!!!");
//            }
//        } else {
//            Log.e(TAG, "error roomname!!!");
//        }
//    }

    /**
     * 将GMT时间转换成当前时区时间
     *
     * @param from
     * @return
     */
    public static String transformTimeZoneFromNormalZoneToLocalZone(String from) {
        String to = "";


        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //本地时区
        Calendar nowCal = Calendar.getInstance();
        TimeZone localZone = nowCal.getTimeZone();
        //设定SDF的时区为本地
        simple.setTimeZone(localZone);


        SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        //设置 DateFormat的时间区域为GMT
        simple1.setTimeZone(TimeZone.getTimeZone("GMT"));


        //把字符串转化为Date对象，然后格式化输出这个Date
        Date fromDate = new Date();
        try {
            //时间string解析成GMT时间
            fromDate = simple1.parse(from);
            //GMT时间转成当前时区的时间
            to = simple.format(fromDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return to;
    }

    /**
     * 将当前时区时间转换成GMT时间
     *
     * @param from
     * @return
     */
    public static String transformTimeZoneFromLocalZoneToNormalZone(String from) {
        String to = "";


        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //本地时区
        Calendar nowCal = Calendar.getInstance();
        TimeZone localZone = nowCal.getTimeZone();
        //设定SDF的时区为本地
        simple.setTimeZone(localZone);


        SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        //设置 DateFormat的时间区域为GMT
        simple1.setTimeZone(TimeZone.getTimeZone("GMT"));


        //把字符串转化为Date对象，然后格式化输出这个Date
        Date fromDate = new Date();
        try {
            //时间string解析成GMT时间
            fromDate = simple.parse(from);
            //GMT时间转成当前时区的时间
            to = simple1.format(fromDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return to;
    }


}
