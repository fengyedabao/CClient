package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.CubeApplication;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.UIItem.Scan433UIItem;
import com.honeywell.cube.controllers.UIItem.ScanMaiaLoopObject;
import com.honeywell.cube.controllers.UIItem.ScanMaiaUIItem;
import com.honeywell.cube.controllers.UIItem.ScanUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.Socket.SocketController;
import com.honeywell.cube.net.easylink.EasyLinkManager;
import com.honeywell.cube.net.http.HttpClientHelper;
import com.honeywell.cube.net.http.MyHttpResponseHandler;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScanEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/7/7. 17:11
 * Email:Shodong.Sun@honeywell.com
 */
public class ScanController {
    public static final String TAG = ScanController.class.getSimpleName();

    /**
     * 检测扫描到的结果是否是正确的扫描结果
     * <p/>
     * 判断条件是 ScanUIItem.isCorrect
     * 设备类型 分为Cube ,Maia, 433
     *
     * @param scanStr
     * @return
     */
    public static ScanUIItem checkScanResult(String scanStr) {
        ScanUIItem uiItem = new ScanUIItem();
        if (scanStr == null || "".equalsIgnoreCase(scanStr)) {
            Loger.print(TAG, "ssd scan parameters is null", Thread.currentThread());
            return uiItem;
        }

        if (scanStr.indexOf("001F55") >= 0) {
            //扫描到CUBE
            uiItem.scanType = ModelEnum.SCAN_TYPE_CUBE;
            uiItem.panelID = scanStr;
            uiItem.isCorrect = true;
            return uiItem;
        }

        if (scanStr.length() == 7) {
            //扫描到 433 Sensor
            uiItem.scanType = ModelEnum.SCAN_TYPE_433;
            uiItem.id = scanStr;
            uiItem.isCorrect = true;
            return uiItem;
        }

        //Maia 过滤
        Map map = ScanController.getMaiaBody(scanStr);
        if (map == null) return uiItem;

        String number = (String) map.get("number");
        if (number == null || "".equalsIgnoreCase(number)) {
            return uiItem;
        }

        //type
        String type = (String) map.get("type");

        //Model
        String model = (String) map.get("model");

        uiItem.scanType = ModelEnum.SCAN_TYPE_MAIA;
        uiItem.loopCount = Integer.parseInt(String.valueOf(number.charAt(3)));
        uiItem.type = type;
        uiItem.model = model;
        uiItem.id = number;
        uiItem.isCorrect = true;
        return uiItem;
    }

    /**
     * 扫描添加 433 设备
     *
     * @param context
     * @param id
     * @return
     */
    public static Scan433UIItem getDefault433UIItem(Context context, String id) {
        Scan433UIItem scan433UIItem = new Scan433UIItem();

        scan433UIItem.id = id;
        scan433UIItem.section = context.getString(R.string.main_loop) + " " + (1);
        scan433UIItem.name = context.getString(R.string.main_zone) + " " + (1);
        ArrayList<Integer> roomIds = CommonCache.getRoomIdList(context);
        ArrayList<String> roomNames = CommonCache.getRoomNameList(context);
        scan433UIItem.roomName = roomNames.size() > 1 ? roomNames.get(1) : "";
        scan433UIItem.roomId = roomIds.size() > 1 ? roomIds.get(1) : 0;

        ArrayList<PeripheralDevice> moduleDevices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_WIFI315M433M);

        if (moduleDevices.size() > 0) {
            scan433UIItem.mainDeviceName = moduleDevices.get(0).mName;
            scan433UIItem.mainDevice = moduleDevices.get(0);
        }

        //传感器类型
        scan433UIItem.sensor_433_type = CommonUtils.transferProtocolToName(context, scan433UIItem.sensor_433_type);
        //防区类型
        scan433UIItem.zoneType = CommonUtils.transferProtocolToName(context, scan433UIItem.zoneType);
        return scan433UIItem;
    }

    /**
     * 扫描添加 433 设备
     *
     * @param context
     * @param scan433UIItem
     */
    public static void add433Device(Context context, Scan433UIItem scan433UIItem) {
        if (scan433UIItem == null) {
            Loger.print(TAG, "ssd add 433 device parameter is null", Thread.currentThread());
            return;
        }
        if (scan433UIItem.mainDevice == null) {
            Loger.print(TAG, "ssd add 433 device main device is null", Thread.currentThread());
            return;
        }
        long loopid = 0;
        String id = scan433UIItem.id;
        for (int i = 0; i < id.length(); i++) {
            int value = Integer.parseInt(id.substring(id.length() - i - 1, id.length() - i));
            loopid += value * Math.pow(10, i);
        }
        ArrayList<Map> deviceloopmap = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("loopid", "" + loopid);
        map.put("roomid", "" + scan433UIItem.roomId);
        map.put("aliasname", "" + scan433UIItem.name);
        map.put("zonetype", "" + CommonUtils.transferNameToProtocol(context, scan433UIItem.zoneType));
        map.put("alarmtype", "" + scan433UIItem.alarmType);
        map.put("alarmtimer", "" + scan433UIItem.delaytime);
        map.put("looptype", "" + CommonUtils.transferNameToProtocol(context, scan433UIItem.sensor_433_type));
        map.put("alarmenable", "on");
        deviceloopmap.add(map);

        String message = MessageManager.getInstance(context).add433Device(scan433UIItem.mainDevice.mPrimaryID, deviceloopmap);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 获取默认的Maia灯光
     *
     * @param context
     * @param uiItem
     * @return
     */
    public static ScanMaiaUIItem getDefaultMaiaUIItem(Context context, ScanUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd getdefault maia parameter is null", Thread.currentThread());
            return null;
        }
        ScanMaiaUIItem maiaUIItem = new ScanMaiaUIItem();
        maiaUIItem.id = uiItem.id;
        maiaUIItem.type = uiItem.type;
        maiaUIItem.loopCount = uiItem.loopCount;
        maiaUIItem.model_num = uiItem.model;
        ArrayList<PeripheralDevice> moduleDevices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_WIFI315M433M);

        if (moduleDevices.size() > 0) {
            maiaUIItem.mainDeviceName = moduleDevices.get(0).mName;
            maiaUIItem.mainDevice = moduleDevices.get(0);
        }

        for (int i = 0; i < maiaUIItem.loopCount; i++) {
            ScanMaiaLoopObject object = new ScanMaiaLoopObject();
            object.section = context.getString(R.string.main_loop) + " " + (i + 1);
            object.name = context.getString(R.string.device_type_light) + " " + (i + 1);
            ArrayList<Integer> roomIds = CommonCache.getRoomIdList(context);
            ArrayList<String> roomNames = CommonCache.getRoomNameList(context);
            object.roomName = roomNames.size() > 1 ? roomNames.get(1) : "";
            object.roomId = roomIds.size() > 1 ? roomIds.get(1) : 0;
            if (Integer.parseInt(maiaUIItem.type) == ModelEnum.LOOP_TYPE_LIGHT_INT) {
                object.name = context.getString(R.string.device_type_light) + " " + (i + 1);
            } else if (Integer.parseInt(maiaUIItem.type) == ModelEnum.LOOP_TYPE_CURTAIN_INT) {
                object.name = context.getString(R.string.device_type_curtain) + " " + (i + 1);
            }
            maiaUIItem.deviceloops.add(object);
        }
        return maiaUIItem;
    }

    /**
     * 添加玛雅设备
     *
     * @param context
     * @param maiaUIItem
     */
    public static void addMaiaDevice(Context context, ScanMaiaUIItem maiaUIItem) {
        if (maiaUIItem == null) {
            Loger.print(TAG, "ssd add maia parameter is null", Thread.currentThread());
            return;
        }
        if (maiaUIItem.mainDevice == null) {
            Loger.print(TAG, "ssd add maia device main device is null", Thread.currentThread());
            return;
        }
        int type = Integer.parseInt(maiaUIItem.type);
        String typeStr = DeviceManager.getDeviceTypeStringFromInt(type);

        ArrayList<Map> deviceloopmap = new ArrayList<>();
        for (int i = 0; i < maiaUIItem.deviceloops.size(); i++) {
            ScanMaiaLoopObject object = maiaUIItem.deviceloops.get(i);
            if (object.enable == true) {
                Map<String, Object> map = new HashMap<>();
                map.put("loopid", "" + (i + 1));
                map.put("roomid", "" + (object.roomId));
                map.put("aliasname", object.name);
                map.put("looptype", typeStr);
                deviceloopmap.add(map);
            }
        }

        if (deviceloopmap.size() == 0) {
            Loger.print(TAG, "ssd add maia 至少增加一个回路", Thread.currentThread());
            return;
        }
        String scanid = maiaUIItem.id;
        if (scanid.length() < 8) return;
        String deviceid = scanid.substring(scanid.length() - 8, scanid.length());

        String message = MessageManager.getInstance(context).addMaiaDevice(maiaUIItem.mainDevice.mPrimaryID, deviceid, deviceloopmap);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 获取防区类型
     *
     * @return
     */
    public static ArrayList<String> get433SensorType(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(CommonUtils.transferProtocolToName(context, CommonData.SENSOR_TYPE_433_INFRADE));
        returnValue.add(CommonUtils.transferProtocolToName(context, CommonData.SENSOR_TYPE_433_KEYFOB));
        returnValue.add(CommonUtils.transferProtocolToName(context, CommonData.SENSOR_TYPE_433_DOORMAGNETI));
        return returnValue;
    }

    /**
     * 扫描到Cube 之后进行的结果处理
     *
     * @param context
     * @param number
     * @param password
     * @param scanedType--区分扫描的类型 CUBE Setting 扫描；绑定CUBE扫描；局域网登陆扫描
     */
    public static void connectToCube(Context context, String number, String password, int scanedType) {
        if (password == null || password.length() == 0) {
            Loger.print(TAG, "ssd connect to cube password is null", Thread.currentThread());
            return;
        }

        AppInfoFunc func = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        AppInfo info1 = AppInfoFunc.getGuestUser(context);
        if (info != null) {
            //保存
            info.cube_local_id = number;
            info.current_scenario_id = 1;
            info.cube_local_password = password;
            func.updateAppInfoByUserName(info.username, info);
        }

        if (info1 != null) {
            //保存
            info1.cube_local_id = number;
            info1.current_scenario_id = 1;
            info1.cube_local_password = password;
            func.updateAppInfoByUserName(info1.username, info1);
        }

        if (scanedType == ModelEnum.SCANED_TYPE_BIND_CUBE) {
            //绑定CUBE
            ScanController.bindCube(context);
        } else if (scanedType == ModelEnum.SCANED_TYPE_REPLACE_CUBE) {
            //替换CUBE
            ScanController.replaceCube(context);
        } else {
            //本地登陆
            ScanController.localLogin(context, number);
        }
    }

    /**
     * 扫描到433设备
     */

    /***************************** private method ****************************/

    /**
     * 替换 Cube
     */
    private static void replaceCube(final Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            Loger.print(TAG, "ssd repalce cube info is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, false, context.getString(R.string.error_operation_failed)));
            return;
        }
        Map map = new HashMap();
        map.put("type", "ReplaceDevice");
        map.put("oldDeviceId", "" + AppInfoFunc.getBindDeviceId(context));
        map.put("newDeviceSerial", ModelEnum.CUBE_PREFIX_DEVICE_SERIAL + info.cube_local_id);
        map.put("newDevicePassword", info.cube_local_password);

        MyHttpResponseHandler responseHandler = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = "";
                if (bytes != null) {
                    str = new String(bytes);
                }
                Loger.print(TAG, "ssd 替换Cube  信息的:" + str, Thread.currentThread());
                //停止一切请求
                CubeApplication application = CubeApplication.getInstance();
                application.stopAllRequest("replace cube");

                EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, true, context.getString(R.string.cube_setting_replace_cube_success)));
                //现在是直接登陆
                LoginController.getInstance(context).startLogin();
                super.onSuccess(i, headers, bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                Loger.print(TAG, "ssd 替换Cube  失败 信息 : " + message, Thread.currentThread());
                EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, false, message));
                super.onFailure(i, headers, bytes, throwable);
            }
        };
        try {
            HttpClientHelper.newInstance().httpRequest(context, NetConstant.URI_DEVICE_LIST, map, HttpClientHelper.newInstance().COOKIE, responseHandler, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接本地登陆
     */
    private static void localLogin(Context context, String number) {
        Loger.print(TAG, "ssd local login cube Array :" + EasyLinkManager.findCubeList, Thread.currentThread());
        boolean getCubeIp = false;
        for (int i = 0; i < EasyLinkManager.findCubeList.size(); i++) {
            JSONObject object = EasyLinkManager.findCubeList.get(i);
            if (object != null) {
                String mac = object.optString(EasyLinkManager.FIND_MAC);
                mac = mac.replace(":", "");
                Loger.print(TAG, "ssd local login mac : " + mac, Thread.currentThread());
                if (number.equalsIgnoreCase(mac)) {
                    AppInfo info = AppInfoFunc.getGuestUser(context);
                    info.cube_ip = object.optString(EasyLinkManager.FIND_IP);
                    info.cube_port = NetConstant.TCP_IP_PORT;
                    new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
                    getCubeIp = true;
                    break;
                }
            }
        }
        if (getCubeIp) {
            //发现本地CUBE
            Loger.print(TAG, "登陆本地Socket", Thread.currentThread());
            SocketController.newInstance(context).loginToTCPSocket();
        } else {
            Loger.print(TAG, "ssd local login 开启 easy link", Thread.currentThread());
            EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.START_EASY_LINK, true, "开启Easy link"));
        }
    }

    /**
     * 绑定Cube
     */
    private static void bindCube(final Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            Loger.print(TAG, "ssd bind cube info is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, false, context.getString(R.string.error_operation_failed)));
            return;
        }
        Map map = new HashMap();
        map.put("type", "BindDevice");
        map.put("deviceSerial", ModelEnum.CUBE_PREFIX_DEVICE_SERIAL + info.cube_local_id);
        map.put("devicePassword", info.cube_local_password);

        MyHttpResponseHandler responseHandler = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = "";
                if (bytes != null) {
                    str = new String(bytes);
                }
                Loger.print(TAG, "ssd 绑定Cube  信息的:" + str, Thread.currentThread());
                try {
                    JSONObject object = new JSONObject(str);
                    if (object != null) {
                        AppInfo info = AppInfoFunc.getCurrentUser(context);
                        info.deviceId = object.optInt("deviceId");
                        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
                        //发送Event事件 绑定成功
                        EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, true, "绑定CUBE成功"));
                        LoginController.getInstance(context).startLogin();

                    }

                } catch (Exception e) {
                    EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, false, "绑定CUBE失败"));
                    e.printStackTrace();
                }
                super.onSuccess(i, headers, bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, false, message));
                super.onFailure(i, headers, bytes, throwable);
            }
        };
        try {
            HttpClientHelper.newInstance().httpRequest(context, NetConstant.URI_DEVICE_LIST, map, HttpClientHelper.newInstance().NO_COOKIE, responseHandler, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析Maia 主体
     *
     * @param str
     * @return
     */
    private static Map getMaiaBody(String str) {
        Map<String, Object> map = new HashMap<>();

        if (str.length() < 12) return null;

        //2开头
        if (!String.valueOf(str.charAt(0)).equalsIgnoreCase("2")) {
            return null;
        }
        //开关 Switch
        if (String.valueOf(str.charAt(2)).equalsIgnoreCase("1")) {
            map.put("type", "" + ModelEnum.LOOP_TYPE_SWITCH_INT);
        } else if (String.valueOf(str.charAt(2)).equalsIgnoreCase("2")) {
            map.put("type", "" + ModelEnum.LOOP_TYPE_LIGHT_INT);
        } else if (String.valueOf(str.charAt(2)).equalsIgnoreCase("3")) {
            map.put("type", "" + ModelEnum.LOOP_TYPE_CURTAIN_INT);
        } else {
            return null;
        }
        //Add MainId 前四后八
        String numberStr = str.substring(0, 4) + str.substring(str.length() - 8, str.length());
        map.put("number", numberStr);

        //设备型号
        String modelStr = str.substring(0, 5).toUpperCase();

        //不是“D" 或者 "S" 就只能取前4位
        String lastChara = String.valueOf(modelStr.charAt(modelStr.length() - 1)).toUpperCase();
        if (!"D".equalsIgnoreCase(lastChara) && !"S".equalsIgnoreCase(lastChara)) {
            modelStr = modelStr.substring(0, 4);
        }
        map.put("model", modelStr);
        return map;
    }
}
