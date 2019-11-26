package com.honeywell.cube.ipc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class CommonUtils {

    private static final String TAG = "CommonUtils";

    public static boolean isSdcardOK() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

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

    public static boolean checkSdcard() {
        File sdcard = new File(getSdcardPath());
        return !(!sdcard.exists() || !sdcard.canWrite());
    }

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

    public static int getIntegerZoneEnableType(String enableStr) {
        int armType = Constants.ARM_TYPE_DISABLE;
        if (enableStr.equalsIgnoreCase(Constants.ARM_TYPE_ENABLE_STR)) {
            armType = Constants.ARM_TYPE_ENABLE;
        }
        return armType;
    }

    public static String getStringZoneEnableType(int enableType) {
        String str = Constants.ARM_TYPE_DISABLE_STR;
        if (enableType == Constants.ARM_TYPE_ENABLE) {
            str = Constants.ARM_TYPE_ENABLE_STR;
        }
        return str;
    }

    public static int getIntegerArmType(String armStr) {
        int armType = Constants.ARM_TYPE_DISABLE;
        if (armStr.equalsIgnoreCase(Constants.ARM_TYPE_ARM_STR)) {
            armType = Constants.ARM_TYPE_ENABLE;
        }
        return armType;
    }

    public static String getStringArmType(int armType) {
        String str = Constants.ARM_TYPE_DISARM_STR;
        if (armType == Constants.ARM_TYPE_ENABLE) {
            str = Constants.ARM_TYPE_ARM_STR;
        }
        return str;
    }

    public static String getStringLoopType(int type) {
        String loopType = "";
        if (type < 0) {
            return loopType;
        }
        switch (type) {
            case Constants.LOOP_TYPE_LIGHT_INT:
                loopType = Constants.LOOP_TYPE_LIGHT;
                break;
            case Constants.LOOP_TYPE_CURTAIN_INT:
                loopType = Constants.LOOP_TYPE_CURTAIN;
                ;
                break;
            case Constants.LOOP_TYPE_RELAY_INT:
                loopType = Constants.LOOP_TYPE_RELAY;
                break;
            case Constants.LOOP_TYPE_SWITCH_INT:
                loopType = Constants.LOOP_TYPE_SWITCH;
                break;
            case Constants.LOOP_TYPE_SENSOR_INT:
                loopType = Constants.LOOP_TYPE_SENSOR;
                break;
            case Constants.LOOP_TYPE_5800PIRAP_INT:
                loopType = Constants.LOOP_TYPE_5800PIRAP;
                break;
            case Constants.LOOP_TYPE_5804EU_INT:
                loopType = Constants.LOOP_TYPE_5804EU;
                break;
            case Constants.LOOP_TYPE_5816EU_INT:
                loopType = Constants.LOOP_TYPE_5816EU;
                break;
            default:
                break;
        }
        return loopType;
    }

    public static int getIntLoopType(String type) {
        int loopType = -1;
        if (null == type || "".equals(type)) {
            return loopType;
        }
        if (type.equals(Constants.LOOP_TYPE_LIGHT)) {
            loopType = Constants.LOOP_TYPE_LIGHT_INT;
        } else if (type.equals(Constants.LOOP_TYPE_CURTAIN)) {
            loopType = Constants.LOOP_TYPE_CURTAIN_INT;
        } else if (type.equals(Constants.LOOP_TYPE_RELAY)) {
            loopType = Constants.LOOP_TYPE_RELAY_INT;
        } else if (type.equals(Constants.LOOP_TYPE_SWITCH)) {
            loopType = Constants.LOOP_TYPE_SWITCH_INT;
        } else if (type.equals(Constants.LOOP_TYPE_SENSOR)) {
            loopType = Constants.LOOP_TYPE_SENSOR_INT;
        } else if (type.equals(Constants.LOOP_TYPE_5800PIRAP)) {
            loopType = Constants.LOOP_TYPE_5800PIRAP_INT;
        } else if (type.equals(Constants.LOOP_TYPE_5804EU)) {
            loopType = Constants.LOOP_TYPE_5804EU_INT;
        } else if (type.equals(Constants.LOOP_TYPE_5816EU)) {
            loopType = Constants.LOOP_TYPE_5816EU_INT;
        }
        return loopType;
    }

    public static int getIntegerModuleType(String mType) {
        int moduleType = 0;
        if (null == mType) {
            return moduleType;
        }
        if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_SPARKLIGHT)) {
            moduleType = Constants.MODULE_TYPE_SPARKLIGHTING;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_BACNET)) {
            moduleType = Constants.MODULE_TYPE_BACNET;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_IPC)) {
            moduleType = Constants.MODULE_TYPE_IPC;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_IR)) {
            moduleType = Constants.MODULE_TYPE_WIFIIR;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_AIR)) {
            moduleType = Constants.MODULE_TYPE_WIFIAIR;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_RELAY)) {
            moduleType = Constants.MODULE_TYPE_WIFIRELAY;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_485)) {
            moduleType = Constants.MODULE_TYPE_WIFI485;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_WIREDZONE)) {
            moduleType = Constants.MODULE_TYPE_WIREDZONE;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_315N433)) {
            moduleType = Constants.MODULE_TYPE_WIFI315M433M;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_IPVDP)) {
            moduleType = Constants.MODULE_TYPE_IPVDP;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_SCENARIO)) {
            moduleType = Constants.MODULE_TYPE_SCENARIO;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_ALARMZONE)) {
            moduleType = Constants.MODULE_TYPE_ALARMZONE;
        } else if (mType.equals(Constants.JSON_COMMAND_MODULETYPE_BACKAUDIO)) {
            moduleType = Constants.MODULE_TYPE_BACKAUDIO;
        }
        return moduleType;
    }

    public static String getStringModuleType(int mType) {
        String moduleType = "";
        switch (mType) {
            case Constants.MODULE_TYPE_SPARKLIGHTING:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_SPARKLIGHT;
                break;
            case Constants.MODULE_TYPE_BACNET:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_BACNET;
                break;
            case Constants.MODULE_TYPE_IPC:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_IPC;
                break;
            case Constants.MODULE_TYPE_WIFIIR:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_IR;
                break;
            case Constants.MODULE_TYPE_WIFIAIR:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_AIR;
                break;
            case Constants.MODULE_TYPE_WIFIRELAY:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_RELAY;
                break;
            case Constants.MODULE_TYPE_WIFI485:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_485;
                break;
            case Constants.MODULE_TYPE_WIREDZONE:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_WIREDZONE;
                break;
            case Constants.MODULE_TYPE_WIFI315M433M:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_315N433;
                break;
            case Constants.MODULE_TYPE_IPVDP:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_IPVDP;
                break;
            case Constants.MODULE_TYPE_SCENARIO:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_SCENARIO;
                break;
            case Constants.MODULE_TYPE_ALARMZONE:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_ALARMZONE;
                break;
            case Constants.MODULE_TYPE_BACKAUDIO:
                moduleType = Constants.JSON_COMMAND_MODULETYPE_BACKAUDIO;
                break;
            default:
                moduleType = "error moduletype";
                break;
        }
        return moduleType;
    }

    public static boolean judgeIsWifiModule(int moduleType) {
        boolean bRet = false;
        if (moduleType >= Constants.MODULE_TYPE_WIFIAIR
                && moduleType <= Constants.MODULE_TYPE_WIFI315M433M) {
            bRet = true;
        }
        return bRet;
    }

    public static byte[] inputStreamToBytes(InputStream is) throws IOException {
        byte[] bytes = null;
        if (null == is) {
            return bytes;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bs = new byte[1024];
        int len = -1;
        while ((len = is.read(bs)) != -1) {
            bos.write(bs, 0, len);
        }
        bytes = bos.toByteArray();
        bos.close();
        return bytes;
    }

    public static String getLocalIpAddr(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        boolean flag = false;
        if (connectMgr.getActiveNetworkInfo() != null) {
            flag = connectMgr.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            return null;
        }
        State wifi = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi == State.CONNECTED) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
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

    public static String getGenerateJsonMessageId() {
        /*SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",Locale.getDefault());
        Date date = new Date();
		String msgId = format.format(date);
		Random r = new Random();
		msgId += r.nextInt();
		msgId = msgId.substring(0, 15);
		Log.i(TAG, "getGenerateJsonMessageId() messageId:" + msgId);
		return msgId;*/
        return UUID.randomUUID().toString();
    }

//    public static JSONObject gernerateBasicConfigJson(String msgId, BasicModuleConfigInfo mConfigInfo) throws JSONException {
//        if (null == mConfigInfo) {
//            return null;
//        }
//        JSONObject request = new JSONObject();
//        Log.i(TAG, "gernerateBasicConfigJson() 111111111111");
////		String msgId = CommonUtils.getGenerateJsonMessageId();
//        request.put(Constants.JSON_COMMAND_MESSAGEID, msgId);
//        request.put(Constants.JSON_COMMAND_ACTION, Constants.JSON_COMMAND_ACTION_REQUEST);
//        request.put(Constants.JSON_COMMAND_SUBACTION, Constants.JSON_COMMAND_SUBACTION_CONFIGMODULE);
//        request.put(Constants.JSON_COMMAND_MODULETYPE, CommonUtils.getStringModuleType(mConfigInfo.mModuleType));
//        if (!CommonUtils.ISNULL(mConfigInfo.mIpAddr)) {
//            request.put(Constants.JSON_COMMAND_MODULEIPADDR, mConfigInfo.mIpAddr);
//        }
//        if (!CommonUtils.ISNULL(mConfigInfo.mMacAddr)) {
//            request.put(Constants.JSON_COMMAND_MODULEMACADDR, mConfigInfo.mMacAddr);
//        }
//        request.put(Constants.JSON_COMMAND_CONFIGTYPE, mConfigInfo.mConfigType);
//        request.put(Constants.JSON_COMMAND_ALIAS, mConfigInfo.mAliasName);
//        return request;
//    }
//
//    public static long addPerieralDevice(Context context, BasicModuleConfigInfo configInfo, int bacnetId, String brandName, int maskId) {
//        if (null == context || null == configInfo) {
//            return -1;
//        }
//        ConfigDatabaseHelper databaseHelper = ConfigDatabaseHelper.getInstance(context);
//        PeripheralDeviceFuc fuc = new PeripheralDeviceFuc(databaseHelper);
//        long id = fuc.addPeripheralDevice(Constants.MODULE_TYPE_SPARKLIGHTING, configInfo.mAliasName, configInfo.mIpAddr, configInfo.mMacAddr,
//                Constants.DEFAULT_PORT, Constants.HASCONFIG, Constants.ONLINE, bacnetId, brandName, maskId);
//        return id;
//    }
//
//    public static BasicModuleConfigInfo PerieralDeviceToBasicInfo(
//            PeripheralDevice peripheralDevice) {
//        if (null == peripheralDevice) {
//            return null;
//        }
//        BasicModuleConfigInfo info = new BasicModuleConfigInfo(Constants.CONFIG_TYPE_ADD,
//                peripheralDevice.mName, peripheralDevice.mIpAddr, peripheralDevice.mMacAddr,
//                peripheralDevice.mType, peripheralDevice.mMaskId, peripheralDevice.mIsOnline);
//        return info;
//    }

    public static void setWindowAlpha(Window window, float f) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = f;
        window.setAttributes(lp);
    }

    public static boolean parseIpAddress(String ip) {
        boolean ret = false;
        StringTokenizer tok = new StringTokenizer(ip, ".");
        if (tok.countTokens() != 4) {
            return ret;
        }
        ret = true;
        while (tok.hasMoreTokens()) {
            String strVal = tok.nextToken();
            int val = Integer.parseInt(strVal, 10);
            if (val < 0 || val > 255) {
                ret = false;
                break;
            }
        }
        return ret;
    }
}
