package com.honeywell.cube.db;

import android.content.Context;

import com.google.gson.Gson;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceVentilationObject;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioTriggerInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.plist_parser.xml.plist.domain.Array;


import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by H157925 on 16/5/1. 11:32
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这个类用于组织各种Message类型
 */
public class MessageManager {
    private static final String TAG = MessageManager.class.getSimpleName();
    public static long sequenceId = 0x00000000;
    public Map<String, Object> mainSequence = new LinkedHashMap<String, Object>();

    private Context mContext;

    //单例类型
    private static MessageManager manager = null;

    public static MessageManager getInstance(Context context) {
        if (manager == null) {
            synchronized (MessageManager.class) {
                if (manager == null) {
                    manager = new MessageManager(context);
                }
            }
        }
        return manager;
    }

    private MessageManager(Context context) {
        mContext = context;
    }

    /**
     * 发送 通知 -- IPC 视频
     *
     * @param ip_addr
     * @param time
     * @return
     */
    public String sendIPCVideoButton(String ip_addr, String time) {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "playipc");
        items.put("moduletype", "ipc");
        items.put("moduleipaddr", ip_addr);
        items.put("timestamp", time);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * local 登陆
     *
     * @param context
     * @return
     */
    public String loginCubeFromLocal(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            info = AppInfoFunc.getGuestUser(context);
        }
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "register");
        items.put("cubeid", info.cube_local_id);
        items.put("cubepwd", info.cube_local_password);
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * local 获取本地数据
     *
     * @param context
     * @return
     */
    public String getCubeInfoFromLocal(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null || LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            info = AppInfoFunc.getGuestUser(context);
        }
        //body
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "getdeviceconfig");
        items.put("moduletype", "cube");
        items.put("version", "".equalsIgnoreCase(info.database_version) ? "0" : "" + AppInfoFunc.getCurrentUser(context).database_version);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * local 查询升级
     *
     * @param context
     * @return
     */
    public String checkCubeUpdateFromLocal(Context context) {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "upgrade");
        items.put("moduletype", "cube");
        items.put("upgradecmd", "newversion");
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 请求房间状态
     */


    /**
     * 通过websocket 发送命令
     *
     * @return
     */
    public String getCubeInfo(Context context) {
        //body
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "getdeviceconfig");
        items.put("moduletype", "cube");
        items.put("version", "".equalsIgnoreCase(AppInfoFunc.getCurrentUser(context).database_version) ? 0 : AppInfoFunc.getCurrentUser(context).database_version);
        items = AddToSequenceMsg(items);
        String deviceid = getDeviceId();
        if (!deviceid.equals("") && !deviceid.equals("0")) {
            Map<String, Object> send = new HashMap<String, Object>();
            send.put("type", "Opaque");
            send.put("deviceId", deviceid);
            send.put("cubemessage", items);
            String result = "";
            Gson gson = new Gson();
            result = gson.toJson(send);
            return result;
        } else {
            Loger.print(TAG, "ssd getCubeInfo : error no deviceid", Thread.currentThread());
            return null;
        }

    }

    /**
     * 设置安防密码
     *
     * @param oldpwd
     * @param newpwd
     * @return
     */
    public String setAlarmPwd(String oldpwd, String newpwd) {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "configsecurity");
        items.put("moduletype", "cube");
        items.put("oldpwd", oldpwd);
        items.put("newpwd", newpwd);
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * 通过websocket 发送scenario命令
     *
     * @param scenarioId
     * @return
     */
    public String enableScenarioWithId(int scenarioId, String pwd) {
        if (pwd == null) {
            pwd = NetConstant.MAIN_SCENARIO_PASSWORD;
        }
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "setdevice");
        items.put("moduletype", "scenario");
        items.put("scenarioid", scenarioId);
        items.put("securitypwd", pwd);
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * 组织心跳包
     */
    public String heartPing() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "heartbeat");
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 通过WebSocket 发送删除 room 命令
     *
     * @param primaryId
     * @return
     */
    public String deleteRoomWithId(long primaryId) {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "room");
        items.put("primaryid", primaryId);
        items.put("configtype", "delete");
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }


    /**
     * 通过WebSocket 发送删除scenario 命令
     *
     * @param scenarioId
     * @return
     */
    public String deleteScenarioWithId(int scenarioId) {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "scenario");
        items.put("scenarioid", scenarioId);
        items.put("configtype", "delete");
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * 编辑或者添加Scenario时的命令组织
     *
     * @param loop
     * @param loopmap
     * @return
     */
    public String editOrAddScenario(boolean isEdit, ScenarioLoop loop, ArrayList<Map<String, Object>> loopmap) {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "scenario");
        items.put("imagename", loop.mImageName);
        items.put("aliasname", loop.mScenarioName);
        if (isEdit) {
            items.put("configtype", "modify");
            items.put("scenarioid", loop.mScenarioId);
        } else {
            items.put("configtype", "add");
        }
        items.put("scenarioloopmap", loopmap);
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * 添加或者编辑房间
     *
     * @param isEdit
     * @param loop
     * @return
     */
    public String editOrAddRoom(boolean isEdit, RoomLoop loop) {
        Map<String, Object> items = new HashMap<String, Object>();
        if (isEdit) {
            items.put("action", "request");
            items.put("subaction", "configmodule");
            items.put("moduletype", "room");
            items.put("configtype", "modify");
            items.put("imagename", loop.mImageName);
            items.put("aliasname", loop.mRoomName);
            items.put("primaryid", loop.mPrimaryId);
        } else {
            items.put("action", "request");
            items.put("subaction", "configmodule");
            items.put("moduletype", "room");
            items.put("configtype", "add");
            items.put("imagename", loop.mImageName);
            items.put("aliasname", loop.mRoomName);
        }

        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * 组织查询设备状态的字段   用到了SparkLighting relay wireless315_433
     *
     * @param deviceLoopMap
     * @param moduleType
     * @return
     */
    public String checkOutDeviceListStatus(ArrayList<Map<String, Object>> deviceLoopMap, String moduleType, Map<String, Object> subItem) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "readdevice");
        items.put("moduletype", moduleType);
        items.put("deviceloopmap", deviceLoopMap);
        if (subItem != null) {
            items.putAll(subItem);
        }
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);

    }

    /**
     * 请求房间的状态
     *
     * @param roomLoopMap
     * @return
     */
    public String checkRoomStatus(ArrayList<Map<String, Object>> roomLoopMap) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "readdevice");
        items.put("moduletype", "room");
        items.put("roomloopmap", roomLoopMap);
        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * 修改物业网络
     */
    public String changeEtherNetInfo(String ip, String mask, String router) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "cube");
        items.put("configtype", "ethernet");
        items.put("ethmode", "manual");
        items.put("ethname", "eth1");
        items.put("ethip", ip);
        items.put("ethmask", mask);
        items.put("ethgw", router);
        items.put("ethdns1", "");
        items.put("ethdns2", "");

        items = AddToSequenceMsg(items);

        return sendStrWithBody(items);
    }

    /**
     * 组织查询BackAudio的状态，因为比较特殊，所以单独组织
     *
     * @param serialnum
     * @param loopId
     * @return
     */
    public String checkoutBackAudioStatus(String serialnum, int loopId) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "readdevice");
        items.put("moduletype", "backaudio");
        items.put("moduleserialnum", serialnum);
        items.put("loopid", loopId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /**
     * 组织控制device的命令，
     *
     * @param deviceloopmap－－loop
     * @param moduleType－－类型
     * @param subItem－－其他的头
     * @return
     */
    public String sendDeviceStatus(ArrayList<Map<String, Object>> deviceloopmap, String moduleType, Map<String, Object> subItem) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "setdevice");
        items.put("moduletype", moduleType);
        items.put("deviceloopmap", deviceloopmap);
        if (subItem != null) {
            items.putAll(subItem);
        }
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 组织发送控制 BackAudioLoop状态的命令
     *
     * @param control
     * @param device
     * @param loop
     * @return
     */
    public String sendBackAudioLoopStatus(ArrayList<Map<String, Object>> control, BackaudioDevice device, BackaudioLoop loop) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "setdevice");
        items.put("moduletype", "backaudio");
        items.put("moduleserialnum", device.mSerialNumber);
        items.put("loopid", loop.mLoopId);
        items.put("keytypeloop", control);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 组织发送IR Code
     *
     * @param imageName
     * @param macAddr
     * @param irData
     * @return
     */
    public String sendIRCode(String imageName, String macAddr, ArrayList<Object> irData) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "setdevice");
        items.put("moduletype", "ir");
        items.put("ircommand", "send");
        items.put("name", imageName);
        items.put("imagename", imageName);
        items.put("modulemacaddr", macAddr);
        items.put("wifiirdata", irData);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /**
     * 编辑 删除设备时 发送的命令
     *
     * @param deviceloopmap
     * @param change
     * @param moduleType
     * @return
     */
    public String changeDeviceStatusWithInfo(ArrayList<Map<String, Object>> deviceloopmap, String change, String moduleType) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", moduleType);
        items.put("configtype", change);
        items.put("deviceloopmap", deviceloopmap);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 删除IPCamera
     *
     * @param primaryId
     * @return
     */
    public String deleteIpcameraStatusWithInfo(long primaryId) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "ipc");
        items.put("configtype", "delete");
        items.put("primaryid", primaryId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 编辑IPCamera
     *
     * @param primaryId
     * @param room
     * @param name
     * @return
     */
    public String modifyIpcameraStatusWithInfo(long primaryId, int room, String name) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "ipc");
        items.put("configtype", "modify");
        items.put("primaryid", "" + primaryId);
        items.put(CommonData.JSON_COMMAND_ROOMID, "" + room);
        items.put("aliasname", name);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 编辑IR状态
     *
     * @param primaryId
     * @param room
     * @param name
     * @return
     */
    public String modifyIrStatusWithInfo(long primaryId, int room, String name) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "ir");
        items.put("configtype", "modify");
        items.put("primaryid", primaryId);
        items.put(CommonData.JSON_COMMAND_ROOMID, room);
        items.put("aliasname", name);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 删除 IR 状态
     *
     * @param primaryId
     * @return
     */
    public String deleteIrStatusWithInfo(long primaryId) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "ir");
        items.put("configtype", "delete");
        items.put("primaryid", primaryId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 编辑外围表
     *
     * @param primaryId
     * @param moduleType
     * @param name
     * @return
     */
    public String modifyPeripheraStatusWithInfo(long primaryId, String moduleType, String name) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", moduleType);
        items.put("configtype", "modify");
        items.put("primaryid", primaryId);
        items.put("aliasname", name);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 删除 主外设表
     *
     * @param primaryId
     * @param moduleType
     * @return
     */
    public String deletePeripheraStatusWithInfo(long primaryId, String moduleType) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", moduleType);
        items.put("configtype", "delete");
        items.put("primaryid", primaryId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 编辑 back audio device
     *
     * @param primaryId
     * @param name
     * @return
     */
    public String modifyBackAudioDeviceStatusWithInfo(long primaryId, String name) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "backaudio");
        items.put("configtype", "modify");
        items.put("primaryid", primaryId);
        items.put("aliasname", name);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 删除 back audio device
     *
     * @param primaryId
     * @return
     */
    public String deleteBackAudioDeviceStatusWithInfo(long primaryId) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "backaudio");
        items.put("configtype", "delete");
        items.put("primaryid", primaryId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 对  SparkLighting 部分进行操作， 编辑或者增加
     *
     * @param deviceloop
     * @param editmode
     * @param deviceid
     * @param subdevtype
     * @param primaryid
     * @return
     */
    public String modifySparkLight(ArrayList<Map<String, Object>> deviceloop, boolean editmode, int deviceid, String subdevtype, int primaryid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "sparklighting");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("primaryid", primaryid);
        items.put("deviceid", deviceid);
        items.put("subdevtype", subdevtype);
        items.put("deviceloopmap", deviceloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 改变RelayLoop的状态
     *
     * @param deviceloop
     * @param editmode
     * @param deviceid
     * @return
     */
    public String modifyRelayLoopState(ArrayList<Map<String, Object>> deviceloop, boolean editmode, int deviceid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "relay");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("primaryid", deviceid);
        items.put("deviceloopmap", deviceloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 改变 Wired zone loop 状态
     *
     * @param deviceloop
     * @param editmode
     * @param deviceid
     * @return
     */
    public String modifyWiredZoneLoopState(ArrayList<Map<String, Object>> deviceloop, boolean editmode, int deviceid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "wiredzone");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("primaryid", deviceid);
        items.put("deviceloopmap", deviceloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 改变 IPVDP 状态
     *
     * @param deviceloop
     * @param editmode
     * @param deviceid
     * @return
     */
    public String modifyIPVDPZoneLoopState(ArrayList<Map<String, Object>> deviceloop, boolean editmode, int deviceid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "ipvdp");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("primaryid", deviceid);
        items.put("deviceloopmap", deviceloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 改变 back audio的状态
     *
     * @param deviceloop
     * @param editmode
     * @param deviceid
     * @return
     */
    public String modifyBackaudioLoopState(ArrayList<Map<String, Object>> deviceloop, boolean editmode, long deviceid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "backaudio");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("primaryid", deviceid);
        items.put("deviceloopmap", deviceloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 改变 Bacnet的状态
     *
     * @param deviceloop
     * @param editmode
     * @param deviceid
     * @return
     */
    public String modifyBacnetLoopState(ArrayList<Map<String, Object>> deviceloop, boolean editmode, int deviceid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "bacnet");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("primaryid", deviceid);
        items.put("deviceloopmap", deviceloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 改变 wifi485的状态
     *
     * @param deviceloop
     * @param editmode
     * @param deviceid
     * @return
     */
    public String modifyWifi485LoopState(ArrayList<Map<String, Object>> deviceloop, boolean editmode, int deviceid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "485");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("primaryid", deviceid);
        items.put("deviceloopmap", deviceloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /**
     * 改变 IPC 状态
     *
     * @param editmode
     * @param ip
     * @param port
     * @param type
     * @param main_stream
     * @param sub_stream
     * @param user
     * @param pwd
     * @param name
     * @return
     */
    public String modifyIpcameraState(boolean editmode, String ip, int port, String type, String main_stream, String sub_stream,
                                      String user, String pwd, int roomid, String name) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "ipc");
        items.put("configtype", editmode ? "modify" : "add");
        items.put("moduleport", "" + port);
        items.put("moduleipaddr", ip);
        items.put("ipctype", type);
        items.put("mainstream", main_stream);
        items.put("substream", sub_stream);
        items.put("ipcusername", user);
        items.put("ipcpassword", pwd);
        items.put("roomid", roomid);
        items.put("aliasname", name);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 获取IPC的信息
     *
     * @param info
     * @param device
     * @param UUIdStr－－uuid     使用P2P方法获取
     * @param phoneIp－－手机本机IP地址
     * @param ison－－打开还是关闭
     * @return
     */
    public String updateIPCInfo(IpcStreamInfo info, PeripheralDevice device, String UUIdStr, String phoneIp, boolean ison) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "ipcmonitor");
        items.put("moduletype", "ipc");
        items.put("moduleipaddr", device.mIpAddr);
        items.put("moduleport", info.mStreamPort);
        items.put("ipcurl", info.mSubStream);
        items.put("uuid", UUIdStr);
        items.put("phoneip", phoneIp);
        items.put("status", ison ? "on" : "off");

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 终止通话
     *
     * @param sessionId
     * @return
     */
    public String disconnectCall(String sessionId) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "event");
        items.put("subaction", "call");
        items.put("moduletype", "ipvdp");
        items.put("callmsg", "terminatecall");
        items.put("callsessionid", sessionId);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 接通电话
     *
     * @param videoPort
     * @param uuidStr
     * @param ipStr
     * @param callSession
     * @return
     */
    public String startCallSession(String videoPort, String uuidStr, String ipStr, String callSession) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "call");
        items.put("moduletype", "ipvdp");
        items.put("callmsg", "takecall");
        items.put("videoport", videoPort);
        items.put("audiocodectype", "pcma");
        items.put("videoratio", "320*240");
        items.put("uuid", uuidStr);
        items.put("takecallipaddr", ipStr);
        items.put("callsessionid", callSession);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 打开门
     *
     * @param callSession
     * @return
     */
    public String openDoor(String callSession) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "event");
        items.put("subaction", "call");
        items.put("moduletype", "ipvdp");
        items.put("callmsg", "opendoor");
        items.put("callsessionid", callSession);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 修改Cube的名称 本地端
     *
     * @param aliasName
     * @param local_id
     * @return
     */
    public String setCubeName(String aliasName, String local_id) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "cube");
        items.put("cubeid", local_id);
        items.put("aliasname", aliasName);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 修改Cube 密码 本地端
     *
     * @param oldpwd
     * @param newPwd
     * @param local_id
     * @return
     */
    public String setCubePassword(String oldpwd, String newPwd, String local_id) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "cube");
        items.put("cubeid", local_id);
        items.put("cubeoldpwd", oldpwd);
        items.put("cubepwd", newPwd);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 修改Cube 设置语音识别的状态
     * <p/>
     * 目前接口是不通的 后面再说
     *
     * @param isOn
     * @return
     */
    public String setVoiceRecognizeState(boolean isOn) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configvoicerecognize");
        items.put("moduletype", "cube");
        items.put("status", isOn ? "on" : "off");

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 升级Cube操作
     *
     * @return
     */
    public String updateCubeVersion() {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "upgrade");
        items.put("moduletype", "cube");
        items.put("upgradecmd", "startupgrade");

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 备份Cube版本
     *
     * @param message
     * @return
     */
    public String backupCubeVersion(String message) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configbackup");
        items.put("moduletype", "cube");
        items.put("description", message);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 恢复Cube 到最新版本
     *
     * @return
     */
    public String recoveryCubeVersion(String dataid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configrecovery");
        items.put("moduletype", "cube");
        items.put("dataid", dataid);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 恢复Cube到指定的版本
     *
     * @param dataid
     * @return
     */
    public String recoverySpecificCubeVersion(int dataid) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configrecovery");
        items.put("moduletype", "cube");
        items.put("dataid", "");

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 添加新模块 发现新版本
     *
     * @return
     */
    public String findNewModule() {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "getnewmodulelist");

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 添加新模块 添加SparkLighting模块
     *
     * @param nameStr
     * @param ipStr
     * @param maskStr
     * @return
     */
    public String addModuleSparkLighting(String nameStr, String ipStr, String maskStr) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "sparklighting");
        items.put("maskid", maskStr);
        items.put("configtype", "add");
        items.put("moduleipaddr", ipStr);
        items.put("aliasname", nameStr);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 添加新模块 添加BacnetAC模块
     *
     * @param selfID
     * @param bacnetID
     * @param nameStr
     * @param brandname
     * @return
     */
    public String addModuleBacnetAC(int selfID, int bacnetID, String nameStr, String brandname) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "bacnet");
        items.put("configtype", "add");
        items.put("looptype", "aircondtion");

        items.put("cubebacnetid", "" + selfID);
        items.put("bacnetdeviceid", "" + bacnetID);
        items.put("aliasname", nameStr);
        items.put("brandname", brandname);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 添加新模块 添加IPVDP模块
     *
     * @param ipvdpPassword
     * @param ipStr
     * @param hnsStr
     * @param nameStr
     * @return
     */
    public String addModuleIPVDP(String ipvdpPassword, String ipStr, String hnsStr, String nameStr, ArrayList<Map> roomloop) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configmodule");
        items.put("moduletype", "ipvdp");
        items.put("configtype", "add");
        items.put("subphonepwd", ipvdpPassword);

        items.put("moduleipaddr", ipStr);
        items.put("subphoneid", "8");
        items.put("hnsserveraddr", hnsStr);
        items.put("aliasname", nameStr);
        items.put("ipvdproomloop", roomloop);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /**
     * 发送Rule开关命令
     *
     * @param isOn
     * @param info
     * @return
     */
    public String sendRuleStatus(boolean isOn, ScenarioTriggerInfo info) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "scenariotrigger");
        items.put("configtype", "modify");
        items.put("status", isOn ? "on" : "off");
        items.put("primaryid", info.mPrimaryId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 删除 Rule命令
     *
     * @param info
     * @return
     */
    public String deleteRule(ScenarioTriggerInfo info) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "scenariotrigger");
        items.put("configtype", "delete");
        items.put("primaryid", info.mPrimaryId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 添加 rule
     *
     * @param availableTime
     * @param delayTime
     * @param aliasName
     * @param conditionArray
     * @param actionArray
     * @return
     */
    public String addRule(Map<String, Object> availableTime, int delayTime, String aliasName, ArrayList<Map<String, Object>> conditionArray, ArrayList<Map<String, Object>> actionArray) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "scenariotrigger");
        items.put("configtype", "add");
        items.put("status", "on");
        items.put("availabletime", availableTime);
        items.put("triggertype", "trigger");
        items.put("delaytime", delayTime);
        items.put("aliasname", aliasName);
        items.put("description", aliasName);
        items.put("condition", conditionArray);
        items.put("triggeraction", actionArray);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /**
     * 学习 IR code
     *
     * @param protocol
     * @param macAddr
     * @param name
     * @return
     */
    public String studyIRCode(String protocol, String macAddr, String name) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "setdevice");
        items.put("ircommand", "study");
        items.put("moduletype", "ir");
        items.put("modulemacaddr", macAddr);
        items.put("name", name == null ? protocol : name);
        items.put("imagename", protocol);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 添加IR
     *
     * @param moduleId
     * @param loopName
     * @param roomid
     * @param type
     * @param ircodes
     * @return
     */
    public String addIRCustom(int moduleId, String loopName, int roomid, String type, ArrayList<Map> ircodes) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("configtype", "add");
        items.put("moduletype", "ir");
        items.put("ircommand", "save");
        items.put("primaryid", "" + moduleId);
        items.put("aliasname", loopName);
        items.put("roomid", "" + roomid);
        items.put("type", type);
        items.put("codeloopmap", ircodes);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /**
     * 添加 433 设备
     *
     * @param deviceId
     * @param deviceloopmap
     * @return
     */
    public String add433Device(long deviceId, ArrayList<Map> deviceloopmap) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "315M433M");
        items.put("primaryid", "" + deviceId);
        items.put("configtype", "add");
        items.put("devicetype", "sensor");
        items.put("deviceloopmap", deviceloopmap);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 添加 Maia设备
     *
     * @param modelId
     * @param deviceId
     * @param deviceloopmap
     * @return
     */
    public String addMaiaDevice(long modelId, String deviceId, ArrayList<Map> deviceloopmap) {

        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "315M433M");
        items.put("primaryid", "" + modelId);
        items.put("configtype", "add");
        items.put("devicetype", "maia2");
        items.put("deviceloopmap", deviceloopmap);
        try {
            items.put("deviceid", CommonUtils.getLeadingInteger(URLEncoder.encode(deviceId, "utf-8"), 16));
        } catch (Exception e) {
            e.printStackTrace();
        }
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /**
     * 添加新风系统
     *
     * @param object
     * @return
     */
    public String addVentilation(MenuDeviceVentilationObject object) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "ventilation");
        items.put("controltype", "relay");
        items.put("configtype", "add");
        items.put("aliasname", object.name);
        items.put("roomid", object.roomId);

        //power
        Map<String, Object> powerItem = new HashMap<>();
        powerItem.put("moduletype", "relay");
        powerItem.put("primaryid", "" + object.power.mLoopSelfPrimaryId);
        items.put("power", powerItem);

        //fan_speed
        ArrayList<Map> fanList = new ArrayList<>();

        Map<String, Object> fanItemHigh = new HashMap<>();
        fanItemHigh.put("moduletype", "relay");
        fanItemHigh.put("primaryid", "" + object.fan_speed_high.mLoopSelfPrimaryId);
        fanList.add(fanItemHigh);

        Map<String, Object> fanItemMid = new HashMap<>();
        fanItemMid.put("moduletype", "relay");
        fanItemMid.put("primaryid", "" + object.fan_speed_middle.mLoopSelfPrimaryId);
        fanList.add(fanItemMid);

        Map<String, Object> fanItemLow = new HashMap<>();
        fanItemLow.put("moduletype", "relay");
        fanItemLow.put("primaryid", "" + object.fan_speed_low.mLoopSelfPrimaryId);
        fanList.add(fanItemLow);

        items.put("fanspeed", fanList);

        //cycle
        Map<String, Object> cycleItem = new HashMap<>();
        cycleItem.put("moduletype", "relay");
        cycleItem.put("primaryid", "" + object.cycle.mLoopSelfPrimaryId);
        items.put("cycletype", cycleItem);

        //humidity
        Map<String, Object> humidityItem = new HashMap<>();
        humidityItem.put("moduletype", "relay");
        humidityItem.put("primaryid", "" + object.mode_humidity.mLoopSelfPrimaryId);
        items.put("humidity", humidityItem);

        //degumidity
        Map<String, Object> dehumidityItem = new HashMap<>();
        dehumidityItem.put("moduletype", "relay");
        dehumidityItem.put("primaryid", "" + object.mode_humidity.mLoopSelfPrimaryId);
        items.put("dehumidity", dehumidityItem);


        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 查询新风系统的状态
     */
    public String readVentilationDeviceStatus(long primaryId) {
        Map<String, Object> controlDic = new HashMap<>();
        controlDic.put("primaryid", "" + primaryId);
        ArrayList<Map> controlList = new ArrayList<>();
        controlList.add(controlDic);

        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "readdevice");
        items.put("moduletype", "ventilation");
        items.put("deviceloopmap", controlList);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 控制新风系统
     */
    public String sendControlVentilation(Map controlMap) {
        ArrayList<Map> controlList = new ArrayList<>();
        controlList.add(controlMap);
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "setdevice");
        items.put("moduletype", "ventilation");
        items.put("deviceloopmap", controlList);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 删除新风 Ventilation loop
     *
     * @param loop
     * @return
     */
    public String deleteVentilation(VentilationLoop loop) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "ventilation");
        items.put("configtype", "delete");
        items.put("primaryid", loop.mLoopSelfPrimaryId);
        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 编辑新风系统
     *
     * @param loop
     * @return
     */
    public String modifyVentilation(VentilationLoop loop) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("moduletype", "ventilation");
        items.put("configtype", "modify");
        items.put("primaryid", loop.mLoopSelfPrimaryId);
        items.put("roomid", loop.mRoomId);
        items.put("aliasname", loop.mLoopName);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /************************ 计划 Schedule ***************************/

    /**
     * 执行计划 打开或者关闭
     *
     * @param primaryId
     * @param ison
     * @return
     */
    public String enableSchedule(int primaryId, boolean ison) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("configtype", "modify");
        items.put("moduletype", "schedulerule");
        items.put("status", ison ? "on" : "off");
        items.put("primaryid", primaryId);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 删除 计划 Schedule
     *
     * @param primaryId
     * @return
     */
    public String deleteSchedule(int primaryId) {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "request");
        items.put("subaction", "configdevice");
        items.put("configtype", "delete");
        items.put("moduletype", "schedulerule");
        items.put("primaryid", primaryId);

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }

    /**
     * 编辑 计划
     *
     * @param map
     * @return
     */
    public String modifySchedule(Map map) {
        Map<String, Object> items = map;

        items = AddToSequenceMsg(map);
        return sendStrWithBody(items);
    }

    /**
     * 呼叫电梯
     *
     * @return
     */
    public String callElevator() {
        Map<String, Object> items = new HashMap<>();
        items.put("action", "event");
        items.put("subaction", "call");
        items.put("callmsg", "elevator");
        items.put("moduletype", "ipvdp");
        items.put("callsessionid", "");

        items = AddToSequenceMsg(items);
        return sendStrWithBody(items);
    }


    /********************************************* private method *********************************/
    /**
     * 用于组织最后发送的数据
     *
     * @param items
     * @return
     */
    private String sendStrWithBody(Map<String, Object> items) {
        if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            Gson gson = new Gson();
            String result = gson.toJson(items);
            return result;
        } else {
            String deviceid = getDeviceId();
            if (!deviceid.equals("") && !deviceid.equals("0")) {
                Map<String, Object> send = new HashMap<String, Object>();
                send.put("type", "Opaque");
                send.put("deviceId", deviceid);
                send.put("cubemessage", items);
                Gson gson = new Gson();
                String result = gson.toJson(send);

                return result;
            } else {
                return null;
            }
        }
    }

    /**
     * 获取绑定设备的device id
     *
     * @return
     */
    private String getDeviceId() {
        AppInfo info = AppInfoFunc.getCurrentUser(mContext);
        if (info != null) {
            return "" + info.deviceId;
        }
        return "0";
    }

    private Map<String, Object> AddToSequenceMsg(Map<String, Object> item) {
        if (sequenceId == 0xffffffff) {
            sequenceId = 0x00000000;
        }
        sequenceId += 1;

        String msgid = "" + sequenceId;

        //添加到现有队列中
        mainSequence.put(msgid, item);

        item.put("msgid", msgid);
        return item;
    }

}
