package com.honeywell.cube.ipc;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.util.Log;


public class NetUtil {
    //constant define
    public static final String FIXSTRING_MOBILE = "PHONE_CUBE_PROTOCOL";
    public static final int FIXEDLENGTH_MOBILE = FIXSTRING_MOBILE.length();
    public static final int HEADERLENGTH_MOBILE = (FIXEDLENGTH_MOBILE + 1 + 4);
    public static final int WITHOUTENCRYPT = 0;
    public static final int WITHCHACHAENCRYPT = 1;
    public static int mEncryptType = WITHCHACHAENCRYPT;
    public static final String TAG = "WifiModule.Util";

    public static byte[] encryptData(byte[] bytes) {
        // TODO do encryption here for future use
        switch (mEncryptType) {
            case WITHOUTENCRYPT:
                break;
            case WITHCHACHAENCRYPT:
                break;
        }

        return bytes;
    }

    public static byte[] appendSendingBytes(byte[] bytes, int size) {
        byte[] data;
        if (bytes == null || size == 0) {
            return null;
        }
        if (size >= bytes.length) {
            data = bytes;
        } else {
            data = Arrays.copyOfRange(bytes, 0, size);
        }

        data = wrapArroudData(data);
        return data;
    }

    private static byte[] wrapArroudData(byte[] bytes) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        int size;
        byte[] header = FIXSTRING_MOBILE.getBytes();
        byte[] data = null;

        // write miracle string
        byteArray.write(header, 0, header.length);

        // write encrypt type
        byteArray.write(mEncryptType);

        data = encryptData(bytes);
        size = data.length;
        Log.d(TAG, "Util.java wrapArroudData body length=" + size + ",,,,\r\n");

        // write length
        byteArray.write((size & 0xff000000) >> 24);
        byteArray.write((size & 0x00ff0000) >> 16);
        byteArray.write((size & 0x0000ff00) >> 8);
        byteArray.write(size & 0x000000ff);

        // write body
        byteArray.write(data, 0, size);

        return byteArray.toByteArray();
    }

    public static int getDeviceIdByData(int data) {
        int deviceId = -1;
        Log.d(TAG, "Util.java:data=" + data + ",,,,\r\n");
        int temp = (data >> 8) & 0xffff7f;
        Log.d(TAG, "Util.java:temp=" + temp + ",,,,\r\n");
        deviceId = ((temp & 0xff) << 16) |
                (temp & 0xff00) |
                ((temp & 0xff0000) >> 16);
        Log.d(TAG, "Util.java:deviceId=" + deviceId + ",,,,\r\n");
        return deviceId;
    }

//    public static void parsePeriphaldeviceObject(Context context, JSONArray peripheraldeviceArray) throws JSONException {
//        if (null == context || null == peripheraldeviceArray) {
//            return;
//        }
//        PeripheralDeviceFuc fuc = new PeripheralDeviceFuc(ConfigDatabaseHelper.getInstance(context));
//        for (int i = 0; i < peripheraldeviceArray.length(); i++) {
//            JSONObject object = peripheraldeviceArray.getJSONObject(i);
//            if (null == object) {
//                continue;
//            }
//            String ipAddr = "";
//            String macAddr = "";
//            String brandName = "";
//            int bacnetId = Constants.DEFAULT_BACNETID;
//            int type = -1;
//            String name = "";
//            int port = Constants.DEFAULT_PORT;
//
//            int maskId = -1;
//            if (object.has(Constants.JSON_COMMAND_MODULETYPE)) {
//                type = object.getInt(Constants.JSON_COMMAND_MODULETYPE);
//            }
//
//            if (object.has(Constants.JSON_COMMAND_BRANDNAME)) {
//                brandName = object.getString(Constants.JSON_COMMAND_BRANDNAME);
//            }
//            if (object.has(Constants.JSON_COMMAND_BACNETDEVID)) {
//                bacnetId = object.getInt(Constants.JSON_COMMAND_BACNETDEVID);
//            }
//            if (object.has(Constants.JSON_COMMAND_ALIAS)) {
//                name = object.getString(Constants.JSON_COMMAND_ALIAS);
//            }
//            if (object.has(Constants.JSON_COMMAND_PORT)) {
//                port = object.getInt(Constants.JSON_COMMAND_PORT);
//            } else {
//                port = Constants.DEFAULT_PORT;
//            }
//            if (object.has(Constants.JSON_COMMAND_MODULEIPADDR)) {
//                ipAddr = object.getString(Constants.JSON_COMMAND_MODULEIPADDR);
//            }
//            if (object.has(Constants.JSON_COMMAND_MODULEMACADDR)) {
//                macAddr = object.getString(Constants.JSON_COMMAND_MODULEMACADDR);
//            }
//            if (object.has(Constants.JSON_COMMAND_MASKID)) {
//                maskId = object.getInt(Constants.JSON_COMMAND_MASKID);
//            }
//            int devId = fuc.getDeviceIdByIpOrMac(ipAddr, macAddr);
//            PeripheralDevice device = new PeripheralDevice(type, name, ipAddr, macAddr, port, bacnetId, brandName, maskId, -1);
//
//            if (devId > 0) {
//                fuc.updatePeripheralDevice(device, false);
//            } else {
//                fuc.addPeripheralDevice(device);
//            }
//        }
//    }
//
//    public static void parseIpcInfoObject(Context context, JSONArray ipcStreamInfoArray) throws JSONException {
//        if (null == context || null == ipcStreamInfoArray) {
//            return;
//        }
//        PeripheralDeviceFuc fuc = new PeripheralDeviceFuc(ConfigDatabaseHelper.getInstance(context));
//        IpcStreamInfoFuc infoFuc = new IpcStreamInfoFuc(ConfigDatabaseHelper.getInstance(context));
//        for (int i = 0; i < ipcStreamInfoArray.length(); i++) {
//            JSONObject jsonObject = ipcStreamInfoArray.getJSONObject(i);
//            if (null == jsonObject) {
//                continue;
//            }
//            int ipcStreamPort = Constants.DEFAULTIPCSTREAMPORT;
//            String ipcType = "";
//            String main_stream = "";
//            String sub_stream = "";
//            String ipcUser = Constants.DEFAULTIPCUSER;
//            String ipcPwd = Constants.DEFAULTIPCPWD;
//            String ipAddr = "";
//
//            if (jsonObject.has(Constants.JSON_COMMAND_MODULEIPADDR)) {
//                ipAddr = jsonObject.getString(Constants.JSON_COMMAND_MODULEIPADDR);
//            }
//            if (CommonUtils.ISNULL(ipAddr)) {
//                continue;
//            }
//            if (jsonObject.has(Constants.JSON_COMMAND_MODULEPORT)) {
//                ipcStreamPort = jsonObject.getInt(Constants.JSON_COMMAND_MODULEPORT);
//            }
//            if (jsonObject.has(Constants.JSON_COMMAND_IPCTYPE)) {
//                ipcType = jsonObject.getString(Constants.JSON_COMMAND_IPCTYPE);
//            }
//            if (jsonObject.has(Constants.JSON_COMMAND_MAINSTREAM)) {
//                main_stream = jsonObject.getString(Constants.JSON_COMMAND_MAINSTREAM);
//            }
//            if (jsonObject.has(Constants.JSON_COMMAND_SUBSTREAM)) {
//                sub_stream = jsonObject.getString(Constants.JSON_COMMAND_SUBSTREAM);
//            }
//            if (jsonObject.has(Constants.JSON_COMMAND_IPCUSERNAME)) {
//                ipcUser = jsonObject.getString(Constants.JSON_COMMAND_IPCUSERNAME);
//            }
//            if (jsonObject.has(Constants.JSON_COMMAND_IPCPWD)) {
//                ipcPwd = jsonObject.getString(Constants.JSON_COMMAND_IPCPWD);
//            }
//
//            int devId = fuc.getDeviceIdByIpOrMac(ipAddr, "");
//            if (devId <= 0) {
//                continue;
//            }
//            IpcStreamInfo exitInfo = infoFuc.getIpcStreamInfoByDevId(devId);
//            IpcStreamInfo ipcStreamInfo = new IpcStreamInfo(devId, ipcType, main_stream, sub_stream, ipcStreamPort, ipcUser, ipcPwd);
//            if (exitInfo == null || exitInfo.mDevId != devId) {
//                infoFuc.addIpcStreamInfo(devId, ipcStreamInfo);
//            } else {
//                infoFuc.updateIpcStreamInfo(ipcStreamInfo);
//            }
//        }
//    }
//
//
//
//    public static CallMsgDetailInfo parseCallMsg(JSONObject jsonObject) throws JSONException {
//        CallMsgDetailInfo info = null;
//        if (null == jsonObject) {
//            return info;
//        }
//        String callMsg = jsonObject.getString(Constants.JSON_COMMAND_CALLMSG);
//        String callType = jsonObject.getString(Constants.JSON_COMMAND_CALLTYPE);
//        int videoPort = jsonObject.getInt(Constants.JSON_COMMAND_VIDEOPORT);
//        String videoCodeType = jsonObject.getString(Constants.JSON_COMMAND_VIDEOCODETYPE);
//        int audioPort = jsonObject.getInt(Constants.JSON_COMMAND_AUDIOPORT);
//        String audioCodeType = jsonObject.getString(Constants.JSON_COMMAND_AUDIOCODETYPE);
//        String videoRate = jsonObject.getString(Constants.JSON_COMMAND_VIDEORATE);
//        String uuid = jsonObject.getString(Constants.JSON_COMMAND_DATA_P2P_UUID);
//        String aliasName = jsonObject.getString(Constants.JSON_COMMAND_ALIAS);
//        String callSessionId = jsonObject.getString(Constants.JSON_COMMAND_CALLSESSIONID);
//        info = new CallMsgDetailInfo(callMsg, callType, videoPort, videoCodeType, audioPort,
//                audioCodeType, videoRate, uuid, aliasName, callSessionId);
//        return info;
//    }
}
