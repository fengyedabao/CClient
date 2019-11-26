package com.honeywell.cube.controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.controllers.DeviceControllers.BackAudioController;
import com.honeywell.cube.controllers.DeviceControllers.BacnetController;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.DeviceControllers.IPCameraController;
import com.honeywell.cube.controllers.DeviceControllers.IRLoopController;
import com.honeywell.cube.controllers.DeviceControllers.IpvdpZoneController;
import com.honeywell.cube.controllers.DeviceControllers.RelayController;
import com.honeywell.cube.controllers.DeviceControllers.SparkLightingController;
import com.honeywell.cube.controllers.DeviceControllers.VentilationController;
import com.honeywell.cube.controllers.DeviceControllers.Wifi485Controller;
import com.honeywell.cube.controllers.DeviceControllers.WiredZoneController;
import com.honeywell.cube.controllers.DeviceControllers.Wireless315M433MController;
import com.honeywell.cube.controllers.menus.MenuModuleController;
import com.honeywell.cube.controllers.menus.MenuRuleController;
import com.honeywell.cube.controllers.menus.MenuScheduleController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeBaseConfig;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeBaseConfigFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoopFunc;
import com.honeywell.cube.net.Socket.SocketController;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ConditionFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ConditionInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCodeFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioTriggerFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioTriggerInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleDeviceInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleRuleFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleRuleInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleScenarioFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleScenarioInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerScenarioFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerScenarioInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485LoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.webSocket.cube_websocket.CubeAutoBahnWebsocketClient;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/1. 14:26
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这个类主要用于处理接收到消息后各种处理，分发
 */
public class ResponderController {

    private static final String TAG = ResponderController.class.getSimpleName();
    private static ResponderController handler = null;
    private Context mContext;


    private ResponderController(Context context) {
        mContext = context;
    }

    /**
     * 单例模式
     */
    public static ResponderController newInstance(Context context) {
        if (handler == null) {
            handler = new ResponderController(context);
        }
        return handler;
    }


    /**
     * 总的处理接口，Web socket 回传的所有数据都走这里，进行分发处理
     *
     * @param jsonStr
     */
    public void dealWithWebSocketResponce(String jsonStr) {
        try {
            JSONObject object = new JSONObject(jsonStr);
            Loger.print(TAG, "11111111-ssd start deal json object", Thread.currentThread());
            String type = object.getString(CommonData.JSON_COMMAND_TYPE);
            if (type.equals("Opaque")) {
                //透传
                dealNetData(object.get("cubemessage"));
            } else if ("OnlineStatus".equalsIgnoreCase(type)) {
                boolean state = object.optBoolean("online");
                int online = state ? 1 : 0;
                Loger.print(TAG, "ssd online : " + online, Thread.currentThread());
                AppInfo info = AppInfoFunc.getCurrentUser(mContext);
                if (info.online != online) {
                    info.online = online;
                    new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(mContext)).updateAppInfoByUserName(info.username, info);
                    //不在线状态转到在线状态
                    if (online == 1) {
                        SocketController.newInstance(mContext).setRequestAfterLogin();
                    }
                }
            } else {
                if (object.has(CommonData.JSON_COMMAND_DATA)) {
                    JSONObject data = (JSONObject) object.get(CommonData.JSON_COMMAND_DATA);
                    JSONObject content = (JSONObject) data.get("content");
                    Loger.print(TAG, "ssd start write SQL", Thread.currentThread());
                    dealNetData(content);
                } else {
                    Loger.print(TAG, "ssd dealWithWebSocketResponce error", Thread.currentThread());
                    EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.WEBSOCKET_EVENT, false, "响应数据格式错误，请重试"));
                }
            }
        } catch (JSONException e) {
            Loger.print(TAG, "ssd dealWithWebSocketResponce jSON error", Thread.currentThread());
            e.printStackTrace();
            return;
        }
    }


    public void dealNetData(Object object) {
        JSONObject data = (JSONObject) object;

        if (data == null) {
            Loger.print(TAG, "ssd dealNetData the param is null", Thread.currentThread());
            return;
        }

        //将超时部分关掉
        CommandQueueManager.getInstance(mContext).timeoutMonitor = null;

        try {
            //重要字段
            String action = data.optString(CommonData.JSON_COMMAND_ACTION);
            String subaction = data.optString(CommonData.JSON_COMMAND_SUBACTION);
            String muduleType = data.optString(CommonData.JSON_COMMAND_MODULETYPE);

            //错误码
//            int errorCode = MessageErrorCode.MESSAGE_ERROR_CODE_OK;
//            if (data.has(CommonData.JSON_COMMAND_ERRORCODE)) {
//                errorCode = Integer.parseInt(data.getString(CommonData.JSON_COMMAND_ERRORCODE));
//                if (errorCode != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
//                    String str = MessageErrorCode.transferErrorCode(mContext, errorCode);
//                    Loger.print(TAG, "ssd responce error error code: " + errorCode + " message : " + str, Thread.currentThread());
//                }
//            }


            if (CommonData.JSON_COMMAND_ACTION_RESPONSE.equalsIgnoreCase(action) || CommonData.JSON_COMMAND_ACTION_REQUEST.equalsIgnoreCase(action)) {
                //登陆
                if (CommonData.JSON_COMMAND_SUBACTION_REGISTER.equalsIgnoreCase(subaction)) {
                    //远端登陆不会进入，只有本地Socket链接才会进入
                    LoginController.getInstance(mContext).handleResponceLoginWithBody(mContext, data);
                }
                if (CommonData.JSON_COMMAND_SUBACTION_CONFIGSECURITY.equalsIgnoreCase(subaction)) {
                    //修改安防密码
                    AlarmController.getInstance(mContext).ResponceForChangeAlarmPasswordWithBody(data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_UPGRADE.equalsIgnoreCase(subaction)) {
                    //Cube 新版本
                    CubeController.handleRequestCubeUpgrade(mContext, data);
                } else if (subaction.equals(CommonData.JSON_COMMAND_SUBACTION_IPCMONITOR)) {
                    //IPC 查看控制
                    IPCameraController.handleResponceWithBody(mContext, data);
                } else if (subaction.equals("playipc")) {
                    //IPC 播放录像
                    NotificationController.handleResponceForIpcPlay(mContext, data);
                } else if (subaction.equals(CommonData.JSON_COMMAND_SUBACTION_CONFIGDEV)) {
                    // 配置设备
                    if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT)) {
                        //Spark Lighting
                        SparkLightingController.handleSparkLightingConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_RELAY)) {
                        //WifiRelay
                        RelayController.handleRelayConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE)) {
                        //有线防区
                        WiredZoneController.handleWiredZoneConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_IPVDP)) {
                        //IPVDP 防区
                        IpvdpZoneController.handleWiredZoneConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO)) {
                        //Back Audio
                        BackAudioController.handleBackAudioConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_BACNET)) {
                        //Bacnet
                        BacnetController.handleBacnetConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_485)) {
                        //485
                        Wifi485Controller.handleWifi485ConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_315M433)) {
                        //315_433
                        Wireless315M433MController.handle315M433MConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_IR)) {
                        //ir
                        IRLoopController.handleIRLoopConfigDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_SCENARIO)) {
                        //场景
                        ScenarioController.handleResponceScenarioWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_SCHEDULERULE)) {
                        //schedule
                        MenuScheduleController.handleScheduleResponce(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_SCENARIOTRIGGER)) {
                        //scenario trigger
                        MenuRuleController.handleRuleResponceConfigDeviceWithInfo(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_VENTILATION)) {
                        //新风
                        VentilationController.handleConfigDeviceState(mContext, data);
                    }
                } else if (subaction.equalsIgnoreCase(CommonData.JSON_COMMAND_SUBACTION_CONFIGMODULE)) {
                    //配置小模块
                    if (muduleType.equalsIgnoreCase(CommonData.JSON_COMMAND_MODULETYPE_IPC)) {
                        //IPC
                        IPCameraController.handleConfigModuleWithBody(mContext, data);
                    } else if (muduleType.equalsIgnoreCase(CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO)) {
                        //back_audio
                        BackAudioController.handleBackAudioConfigModuleWithBody(mContext, data);
                    } else if (muduleType.equalsIgnoreCase(CommonData.JSON_COMMAND_MODULETYPE_ROOM)) {
                        //room
                        RoomController.handleResponceRoomConfigModuleWithBody(mContext, data);
                    } else if (muduleType.equalsIgnoreCase(CommonData.JSON_COMMAND_MODULETYPE_CUBE)) {
                        //修改登陆信息
                        CubeController.handleConfigModuleResponceWithInfo(mContext, data);
                    } else {
                        //其他
                        MenuModuleController.handleResponceConfigModuleWithBody(mContext, data);
                    }
                } else if (subaction.equals(CommonData.JSON_COMMAND_SUBACTION_READDEV)) {
                    //读取设备信息
                    if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT)) {
                        //Spark灯光
                        SparkLightingController.handleSparkLightingReadDeviceWithBody(data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_RELAY)) {
                        //WIFI RELAY
                        RelayController.handleRelayReadDeviceWithBody(data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO)) {
                        //back audio
                        BackAudioController.handleBackAudioReadDeviceWithBody(data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_315M433)) {
                        //315M 433M
                        Wireless315M433MController.handle315433MReadDeviceWithBody(data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_485)) {
                        //485
                        Wifi485Controller.handle485ReadDeviceWithBody(data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_BACNET)) {
                        //bacnet
                        BacnetController.handleBacnetReadDeviceWithBody(data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_ROOM)) {
                        //room
                        RoomController.handleResponceRoomReadDeviceWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_VENTILATION)) {
                        //新风
                        VentilationController.handleReadDeviceStatus(mContext, data);
                    }
                } else if (CommonData.JSON_COMMAND_SUBACTION_SETDEV.equalsIgnoreCase(subaction)) {
                    //设置设备
                    //IR
                    if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_IR)) {
                        String irCommand = (String) data.get(CommonData.JSON_COMMAND_WIFIIRCMD);
                        if (irCommand.equals(CommonData.JSON_COMMAND_ACTION_WIFIIR_STDY)) {
                            //IR study
                            IRLoopController.handleStudyIRDeviceWithBody(mContext, data);
                        } else if (irCommand.equals(CommonData.JSON_COMMAND_ACTION_WIFIIR_SND)) {
                            //IR study
                            IRLoopController.handleSendIrWithBody(mContext, data);
                        }
                    }
                    //执行场景
                    else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_SCENARIO)) {
                        ScenarioController.handleControlResponceScenarioWithBody(mContext, data);
                    } else if (muduleType.equals(CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO)) {
                        BackAudioController.handleBackAudioSetDeviceWithBody(mContext, data);
                    } else {
                        DeviceController.handleSetDeviceResponce(mContext, data.getJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP));
                    }
                } else if (CommonData.JSON_COMMAND_SUBACTION_GETNEWMODULELIST.equalsIgnoreCase(subaction)) {
                    //读取新添加模块列表
                    MenuModuleController.handleResponceFindNewModule(mContext, data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_BACKUPCONF.equalsIgnoreCase(subaction)) {
                    //备份消息
                    CubeController.handleResponceBackUp(mContext, data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_REVERTCONF.equalsIgnoreCase(subaction)) {
                    //恢复
                    CubeController.handleResponceRecovery(mContext, data);
                } else if ("configvoicerecognize".equalsIgnoreCase(subaction)) {
                    //语音识别
                    CubeController.handleResponceVoiceRecgnize(mContext, data);
                } else if (data.has(CommonData.JSON_COMMAND_CONFIGDATA)) {
                    AppInfo info = AppInfoFunc.getCurrentUser(mContext);
                    if (info == null) {
                        Loger.print(TAG, "ssd get config info is null", Thread.currentThread());
                        info = AppInfoFunc.getGuestUser(mContext);
                    }

                    if (data.has(CommonData.JSON_COMMAND_ALIAS))
                        info.cube_local_nickname = data.optString(CommonData.JSON_COMMAND_ALIAS);
                    if (data.has(CommonData.JSON_COMMAND_SCENARIOID)) {
                        info.current_scenario_id = data.optInt(CommonData.JSON_COMMAND_SCENARIOID);
                    }
                    if (data.has("armstatus"))
                        info.current_security_status = data.optInt("armstatus");
                    if (data.has("moduleipaddr"))
                        info.cube_ip = data.optString("moduleipaddr");
                    if (data.has("modulemacaddr"))
                        info.cube_mac = data.optString("modulemacaddr");
                    if (data.has("moduleversion"))
                        info.cube_version = data.optString("moduleversion");
                    if (data.has("voicerecognizestatus"))
                        info.cube_voice_recognize = data.optInt("voicerecognizestatus");

                    if ((!CommonUtils.ISNULL(info.database_version)) && (Integer.parseInt(info.database_version) == (data.optInt(CommonData.JSON_COMMAND_VERSION)))) {
                        Loger.print(TAG, "ssd get config info version is ok : " + info.database_version, Thread.currentThread());
                        if (!LoginController.getInstance(mContext).isUpdateConfig) {
                            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_WEBSOCKET_SUCCESS, true, "登录成功"));
                        } else {
                            LoginController.getInstance(mContext).isUpdateConfig = false;
                            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_UPDATE_CONFIG, true, "更新数据成功"));
                        }
                        ScenarioLoop scenarioLoop = ScenarioController.getScenarioFromScenarioID(mContext, info.current_scenario_id);
                        if (scenarioLoop != null) {
                            scenarioLoop.mClickedCount++;
                            new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(mContext)).updateScenarioClickCount(info.current_scenario_id, scenarioLoop.mClickedCount);
                        }
                        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(mContext)).updateAppInfoByUserName(info.username, info);
                        return;
                    }
                    if (data.has(CommonData.JSON_COMMAND_VERSION))
                        info.database_version = "" + data.optInt(CommonData.JSON_COMMAND_VERSION);
                    new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(mContext)).updateAppInfoByUserName(info.username, info);
                    JSONArray array = data.getJSONArray(CommonData.JSON_COMMAND_CONFIGDATA);
                    if (array != null && array.length() > 0) {
                        //清空表数据
                        ConfigCubeDatabaseHelper.getInstance(mContext).clearTable();
                        //获取所有的配置信息，更新数据库
                        ResponderController.handleGetConfigWithBody(mContext, array);
                        ScenarioLoop scenarioLoop = ScenarioController.getScenarioFromScenarioID(mContext, info.current_scenario_id);
                        if (scenarioLoop != null) {
                            scenarioLoop.mClickedCount++;
                            new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(mContext)).updateScenarioClickCount(info.current_scenario_id, scenarioLoop.mClickedCount);
                        }
                        //发送通知 更新数据库完成
                        if (!LoginController.getInstance(mContext).isUpdateConfig) {
                            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_WEBSOCKET_SUCCESS, true, "登录成功"));
                        } else {
                            LoginController.getInstance(mContext).isUpdateConfig = false;
                            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_UPDATE_CONFIG, true, "更新数据成功"));
                        }
                        //更新Cache数据
                        CommonCache.updateRooomList(mContext);
                        Loger.print(TAG, "11111111-ssd stop write SQL", Thread.currentThread());

                    }
                }

            } else if (action.equals(CommonData.JSON_COMMAND_ACTION_EVENT)) {
                //接收到报警消息
                if (CommonData.JSON_COMMAND_SUBACTION_ALARMINFO.equalsIgnoreCase(subaction)) {
                    AlarmController.getInstance(mContext).handleEventAlarmWithBody(data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_CALL.equalsIgnoreCase(subaction)) {
                    //接收到呼叫
                    CallController.getInstance(mContext).handleEventCallWithBody(data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_BACKAUDIOINFO.equalsIgnoreCase(subaction)) {
                    //接收到Backaudio更新 发送通知更新设备信息，这部分在做设备时需要利用起来
                    BackAudioController.handleBackAudioUpdateInfoWithBody(mContext, data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_CUBEDEVEVENT.equalsIgnoreCase(subaction)) {
                    //Cube简单推送消息，只显示一下
                    String msg = mContext.getString(R.string.notification) + " : " + data.getString("eventtype");
                    EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.NORMAL_MSG_EVENT, true, msg));
                } else if (CommonData.JSON_COMMAND_SUBACTION_UPGRADE.equalsIgnoreCase(subaction)) {
                    //接受到升级消息
                    CubeController.handleEventCubeUpgrade(mContext, data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_SYSTEMSECURITYSTATE.equalsIgnoreCase(subaction)) {
                    //接受到Security场景变更
                    ScenarioController.handleEventSystemScenarioState(mContext, data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_REVERTCONF.equalsIgnoreCase(subaction)) {
                    //恢复
                    CubeController.handleResponceRecovery(mContext, data);
                } else if (CommonData.JSON_COMMAND_SUBACTION_BACKUPCONF.equalsIgnoreCase(subaction)) {
                    //备份
                    CubeController.handleResponceBackUp(mContext, data);
                }
            }

            if (data.has("msgid")) {
                //删除队列中的对象
                String msgid = data.getString("msgid");
                if (CommonUtils.ISNULL(msgid)) {
                    MessageManager.getInstance(mContext).mainSequence.remove(msgid);
                } else {
                    MessageManager.getInstance(mContext).mainSequence.clear();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 更新所有的配置信息，更新表
     */
    public static synchronized void handleGetConfigWithBody(Context context, JSONArray body) {
        SQLiteDatabase db = ConfigCubeDatabaseHelper.getInstance(context).getWritableDatabase();
        db.beginTransaction();
        try {
            Loger.print(TAG, "start config db", Thread.currentThread());
            for (int i = 0; i < body.length(); i++) {
                JSONObject object = (JSONObject) body.get(i);
                Loger.print(TAG, "handleGetConfigWithBody + get parts:" + object.toString(), Thread.currentThread());
                String tableName = object.keys().next();
                JSONArray itemArray = object.getJSONArray(tableName);
                if (tableName.equals("peripheraldevice")) {
                    final int size = itemArray.length();
                    //模块表 主表
                    for (int j = 0; j < size; j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        PeripheralDevice device = new PeripheralDevice();
                        device.mPrimaryID = object1.optInt("_id");
                        device.mId = object1.optLong("_id");
                        device.mType = object1.optInt("type");
                        device.mName = object1.optString("name");
                        device.mIpAddr = object1.optString("ip_addr");
                        device.mMacAddr = object1.optString("mac_addr");
                        device.mPort = object1.optInt("port");
                        device.mIsConfig = object1.optInt("isconfig");
                        device.mIsOnline = object1.optInt("isonline");
                        device.mVersion = object1.optString("version");
                        if (object1.has("bacnetid")) {
                            device.mBacnetId = object1.optInt("bacnetid");
                        }
                        if (object1.has("brandname")) {
                            device.mBrandName = object1.optString("brandname");
                        }
                        if (object1.has("mask_id")) {
                            device.mMaskId = object1.optInt("mask_id");
                        }
                        (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).addPeripheralDevice(device, db);
                    }
                } else if (tableName.equals("irinfo")) {
                    //IR info
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        IrInfo device = new IrInfo();
                        device.mId = object1.getLong("_id");
                        device.mDevId = object1.getInt("dev_id");
                        device.mIrName = object1.getString("ir_name");
                        device.mIrType = object1.getString("ir_type");
                        device.mIrLock = object1.getInt("ir_lock");
                        device.mIrPwd = object1.getString("ir_pwd");
                        device.mIrId = object1.getInt("ir_id");
                        device.mIrSubDevId = object1.getInt("ir_sub_dev");
                        device.mIrKey = object1.getString("ir_key");
                        (new IrInfoFunc(ConfigCubeDatabaseHelper.getInstance(context))).addIrInfo(device, db);
                    }
                } else if (tableName.equals("sparklightingloopmap")) {
                    //SparkLightingLoop
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        SparkLightingLoop device = new SparkLightingLoop();
                        device.mLoopSelfPrimaryId = object1.optLong("_id");
                        device.mModulePrimaryId = object1.optInt("dev_id");
                        device.mLoopName = object1.optString("loop_name");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mSubDevId = object1.optInt("sub_dev_id");
                        device.mSubDevType = object1.optString("sub_dev_type");
                        device.mLoopType = object1.optInt("loop_type");
                        device.mLoopId = object1.optInt("loop_id");
                        device.mIsEnable = object1.optInt("is_enable");
                        (new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addSparkLightingLoop(device, db);
                    }
                } else if (tableName.equals("relayloopmap")) {
                    //Wifi relay
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        RelayLoop device = new RelayLoop();
                        device.mLoopSelfPrimaryId = object1.optLong("_id");
                        device.mModulePrimaryId = object1.optInt("dev_id");
                        device.mLoopName = object1.optString("loop_name");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopType = object1.optInt("loop_type");
                        device.mLoopId = object1.optInt("loop_id");
                        device.mTriggerTime = object1.optInt("trigger_time");
                        (new RelayLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addRelayLoop(device, db);
                    }
                } else if (tableName.equals("wiredzoneloopmap")) {
                    //Wired zone loop
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        WiredZoneLoop device = new WiredZoneLoop();
                        device.mLoopSelfPrimaryId = object1.optLong("_id");
                        device.mModulePrimaryId = object1.optInt("dev_id");
                        device.mLoopName = object1.optString("loop_name");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopId = object1.optInt("loop_id");
                        device.mZoneType = object1.optString("zone_type");
                        device.mAlarmType = object1.optString("alarm_type");
                        device.mIsEnable = object1.optInt("is_enable");
                        (new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addWiredZoneLoop(device, db);
                    }
                } else if (tableName.equals("ipvdpzoneloopmap")) {
                    //IPVDP 防区表
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        IpvdpZoneLoop device = new IpvdpZoneLoop();
                        device.mLoopSelfPrimaryId = object1.optLong("_id");
                        device.mModulePrimaryId = object1.optInt("dev_id");
                        device.mLoopName = object1.optString("loop_name");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopId = object1.optInt("loop_id");
                        device.mZoneType = object1.optString("zone_type");
                        device.mAlarmType = object1.optString("alarm_type");
                        device.mDelayTimer = object1.optInt("alarm_timer");
                        device.mIsEnable = object1.optInt("is_enable");
                        (new IpvdpZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addIpvdpZoneLoop(device, db);
                    }
                } else if (tableName.equals("ipcstreaminfo")) {
                    //IPC stream
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        IpcStreamInfo device = new IpcStreamInfo();
                        device.mId = object1.getLong("_id");
                        device.mPrimaryId = object1.optLong("_id");
                        device.mDevId = object1.optInt("dev_id");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mIpcType = object1.optString("type");
                        device.mMainStream = object1.optString("main_stream");
                        device.mSubStream = object1.optString("sub_stream");
                        device.mUser = object1.optString("user");
                        device.mPassword = object1.optString("password");
                        (new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context))).addIpcStreamInfo(device, db);
                    }
                } else if (tableName.equals("bacnetloopmap")) {
                    //bacnet
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        BacnetLoop device = new BacnetLoop();
                        device.mLoopSelfPrimaryId = object1.getLong("_id");
                        device.mModulePrimaryId = object1.getInt("dev_id");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopName = object1.getString("loop_name");
                        device.mLoopId = object1.getInt("loop_id");
                        device.mSubDevId = object1.getInt("sub_gateway_id");
                        (new BacnetLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addBacnetLoop(device, db);
                    }
                } else if (tableName.equals("485loopmap")) {
                    //485 AC 表
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        Wifi485Loop device = new Wifi485Loop();
                        device.mLoopSelfPrimaryId = object1.getLong("_id");
                        device.mBrandName = object1.getString("brandname");
                        device.mModulePrimaryId = object1.optInt("dev_id");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopName = object1.getString("loop_name");
                        device.mLoopId = object1.getInt("loop_id");
                        device.mLoopType = object1.getString("loop_type");
                        device.mPortId = object1.getInt("portid");
                        device.mSlaveAddr = object1.getInt("slaveaddr");
                        (new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addWifi485Loop(device, db);
                    }
                } else if (tableName.equals("backaudiodevice")) {
                    //back audio
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        BackaudioDevice device = new BackaudioDevice();
                        device.mPrimaryID = object1.getLong("_id");
                        device.mSerialNumber = object1.getString("serialnumber");
                        device.mName = object1.getString("name");
                        device.mMachineType = object1.getInt("machinetype");
                        device.mloopNum = object1.getInt("loopnumber");
                        device.mIsOnline = object1.getInt("isonline");
                        (new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).addBackaudioDevice(device, db);
                    }
                } else if (tableName.equals("backaudioloopmap")) {
                    //back audio loop
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        BackaudioLoop device = new BackaudioLoop();
                        device.mLoopSelfPrimaryId = object1.getLong("_id");
                        device.mModulePrimaryId = object1.getInt("dev_id");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopName = object1.getString("loop_name");
                        device.mLoopId = object1.getInt("loop_id");
                        (new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addBackaudioLoop(device, db);
                    }
                } else if (tableName.equals("wireless315m433mloop")) {
                    //315 433
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        Wireless315M433MLoop device = new Wireless315M433MLoop();
                        device.mLoopSelfPrimaryId = object1.getLong("_id");
                        device.mModulePrimaryId = object1.getInt("dev_id");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopName = object1.getString("loop_name");
                        device.mLoopId = object1.getInt("loop_id");
                        device.mZoneType = object1.getString("zone_type");
                        device.mAlarmType = object1.getString("alarm_type");
                        device.mDelayTimer = object1.getInt("alarm_timer");
                        device.mIsEnable = object1.getInt("is_enable");
                        device.mSubDevId = object1.getInt("device_id");
                        device.mSerialnumber = object1.getString("serial_number");
                        device.mDeviceType = object1.getString("device_type");
                        device.mLoopType = object1.getInt("loop_type");
                        (new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addWireless315M433MLoop(device, db);
                    }
                } else if (tableName.equals("irloopmap")) {
                    //IR Loop
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        IrLoop device = new IrLoop();
                        device.mLoopSelfPrimaryId = object1.getLong("_id");
                        device.mModulePrimaryId = object1.getInt("dev_id");
                        device.mRoomId = object1.optInt(CommonData.JSON_COMMAND_ROOMID);
                        device.mLoopName = object1.getString("loop_name");
                        device.mLoopType = object1.getString("looptype");
                        (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addIrLoop(device, db);
                    }
                } else if (tableName.equals("codeloopmap")) {
                    //IR Code
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        IrCode device = new IrCode();
                        device.mId = object1.getLong("_id");
                        device.mLoopId = object1.getInt("ir_loop_id");
                        device.mName = object1.getString("name");
                        device.mImageName = object1.getString("imagename");
                        device.mData1 = object1.getString("data1");
                        device.mData2 = object1.getString("data2");
                        (new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(context))).addIrCode(device, db);
                    }
                } else if (tableName.equals("scenarioloopmap")) {
                    //Scenario Loop
                    //这个部分还有问题，没有考虑预存的内容 需要过滤 去掉已经存在的scenario
                    final int size = itemArray.length();
                    for (int j = 0; j < size; j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        ScenarioLoop device = new ScenarioLoop();
                        device.mScenarioLoopPrimaryId = object1.optInt("_id");
                        device.mScenarioId = object1.optInt("scenario_id");
                        device.mDeviceLoopPrimaryId = object1.optInt("dev_id");
                        device.mScenarioName = object1.optString("scenario_name");
                        device.mModuleType = object1.optInt("moduletype");
                        device.mActionInfo = object1.optString("actioninfo");
                        device.mIsArm = object1.optInt("isarm");
                        device.mImageName = object1.optString("imagename");
                        String itemSql = ScenarioLoopFunc.addScenarioLoop2(device);
                        (new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).addScenarioLoop4(device, db);
                    }
                } else if (tableName.equals("scheduleruleloopmap")) {
                    //schedule rule
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        ScheduleRuleInfo device = new ScheduleRuleInfo();
                        device.mId = object1.optInt("_id");
                        device.mPrimaryId = object1.optInt("_id");
                        device.mSwitchStatus = object1.optString("switchstatus");
                        device.mName = object1.optString("name");
                        device.mAvaibleTime = object1.optString("avaibletime");
                        device.mDescription = object1.optString("description");
                        (new ScheduleRuleFunc(ConfigCubeDatabaseHelper.getInstance(context))).addScheduleRuleInfo(device, db);
                    }
                } else if (tableName.equals("schedulescenarioloopmap")) {
                    //schedule scenario
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        ScheduleScenarioInfo device = new ScheduleScenarioInfo();
                        device.mId = object1.getLong("_id");
                        device.mPrimaryId = object1.getInt("_id");
                        device.mActionInfo = object1.getString("actioninfo");
                        device.mTriggerOrRuleId = object1.getInt("schedulerule_id");
                        (new ScheduleScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context))).addScheduleScenario(device, db);
                    }
                } else if (tableName.equals("scheduledeviceloopmap")) {
                    //schedule device
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        ScheduleDeviceInfo device = new ScheduleDeviceInfo();
                        device.mId = object1.getLong("_id");
                        device.mPrimaryId = object1.getInt("_id");
                        device.mActionInfo = object1.getString("actioninfo");
                        device.mTriggerOrRuleId = object1.getInt("schedulerule_id");
                        device.mLoopPrimaryId = object1.getInt("primaryid");
                        device.mModuleType = object1.getInt("moduletype");
                        (new ScheduleDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).addScheduleRuleDeviceControlInfo(device, db);
                    }
                } else if (tableName.equals("scenariotriggerloopmap")) {
                    //scenario trigger
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        ScenarioTriggerInfo device = new ScenarioTriggerInfo();
                        device.mId = object1.getLong("_id");
                        device.mPrimaryId = object1.getInt("_id");
                        device.mAvaibleTime = object1.getString("avaibletime");
                        device.mDelayTime = object1.getInt("delaytime");
                        device.mDescription = object1.getString("description");
                        device.mName = object1.getString("name");
                        device.mSwitchStatus = object1.getString("switchstatus");
                        device.mType = object1.getString("type");
                        (new ScenarioTriggerFunc(ConfigCubeDatabaseHelper.getInstance(context))).addScenarioTriggerInfo(device, db);
                    }
                } else if (tableName.equals("conditionloopmap")) {
                    //condition 输入表
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        ConditionInfo device = new ConditionInfo();
                        device.mId = object1.optLong("_id");
                        device.mPrimaryId = object1.optInt("_id");
                        device.mActionInfo = object1.optString("actioninfo");
                        device.mTriggerOrRuleId = object1.optInt("triggerid");
                        device.mLoopPrimaryId = object1.optInt("primaryid");
                        device.mModuleType = object1.optInt("moduletype");
                        (new ConditionFunc(ConfigCubeDatabaseHelper.getInstance(context))).addTriggerConditionInfo(device, db);
                    }
                } else if (tableName.equals("triggerscenarioloopmap")) {
                    //condition 输出 Scenario表
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        TriggerScenarioInfo device = new TriggerScenarioInfo();
                        device.mId = object1.getLong("_id");
                        device.mPrimaryId = object1.getInt("_id");
                        device.mActionInfo = object1.getString("actioninfo");
                        device.mTriggerOrRuleId = object1.getInt("triggerid");
                        (new TriggerScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context))).addTriggerScenario(device, db);
                    }
                } else if (tableName.equals("triggerdeviceloopmap")) {
                    //condition 输出 Scenario表
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        TriggerDeviceInfo device = new TriggerDeviceInfo();
                        device.mId = object1.optLong("_id");
                        device.mPrimaryId = object1.optInt("_id");
                        device.mActionInfo = object1.optString("actioninfo");
                        device.mTriggerOrRuleId = object1.optInt("triggerid");
                        device.mModuleType = object1.optInt("moduletype");
                        device.mLoopPrimaryId = object1.optInt("primaryid");
                        (new TriggerDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).addTriggerDevice(device, db);
                    }
                } else if (tableName.equals("roomloopmap")) {
                    //condition 输出 Scenario表
                    //这个部分还有问题，没有考虑预存的内容
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.get(j);
                        RoomLoop device = new RoomLoop();
                        device.mId = object1.optLong("_id");
                        device.mPrimaryId = object1.optInt("_id");
                        device.mRoomName = object1.optString("room_name");
                        device.mImageName = object1.optString("imagename");
                        (RoomLoopFunc.getInstance(context)).addRoomLoop(device, db);
                    }
                } else if ("ventilationloopmap".equalsIgnoreCase(tableName)) {
                    //新风
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.opt(j);
                        if (object1 == null) continue;
                        VentilationLoop ventilationLoop = new VentilationLoop();
                        ventilationLoop.mLoopSelfPrimaryId = object1.optLong("_id");
                        ventilationLoop.mLoopName = object1.optString("loop_name");
                        ventilationLoop.mRoomId = object1.optInt("roomid");
                        ventilationLoop.controltype = object1.optString("controltype");
                        ventilationLoop.power = object1.optString("power");
                        ventilationLoop.fanspeed = object1.optString("fanspeed");
                        ventilationLoop.cycletype = object1.optString("cycletype");
                        ventilationLoop.humidity = object1.optString("humidity");
                        ventilationLoop.dehumidity = object1.optString("dehumidity");

                        new VentilationLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).addVentilationLoop(ventilationLoop, db);
                    }
                } else if ("cubebase".equalsIgnoreCase(tableName)) {
                    //cube base config
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject object1 = (JSONObject) itemArray.opt(j);
                        if (object1 == null) continue;
                        CubeBaseConfig cubeBaseConfig = new CubeBaseConfig();
                        cubeBaseConfig.primaryid = object1.optInt("_id");
                        cubeBaseConfig.conf_value = object1.optString("conf_value");
                        cubeBaseConfig.conf_name = object1.optString("conf_name");
                        new CubeBaseConfigFunc(ConfigCubeDatabaseHelper.getInstance(context)).addCubeBaseConfig(cubeBaseConfig, db);
                    }
                }

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            Loger.print(TAG, "end config db", Thread.currentThread());
        }
    }

    /**
     * 判断是否存在错误码，存在 返回true 并发送通知， 不存在返回false, 用于在各个设备中
     *
     * @param body
     * @return
     */

    public static boolean checkErrorCodeWithBody(JSONObject body) {
        try {
            String errorCode = body.optString("errorcode");
            if (errorCode == null) {
                return false;
            }

            int errorcode = 0;
            if (!body.has("deviceloopmap")) {
                return false;
            }
            JSONArray array = body.optJSONArray("deviceloopmap");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    String errorStr = ((JSONObject) array.get(i)).getString("errorcode");
                    int error = Integer.parseInt(errorStr);
                    if (error != 0) {
                        errorcode = error;
                        break;
                    }
                }
            }
            //输出通知
            if (errorcode != 0) {
                //发送通知，页面更新
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用在 返回数据中 readdevice 有一个成功，就返回成功
     *
     * @param body
     * @return
     */
    public static boolean checkHaveOneSuccessWithBody(JSONObject body) {
        if (body.has("errorcode")) {
            int error = body.optInt("errorcode");
            if (error == MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                return true;
            }
        }
        // 判断loop
        if (body.has("deviceloopmap")) {
            JSONArray array = body.optJSONArray("deviceloopmap");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.opt(i);
                int err = object.optInt("errorcode");
                if (err == MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                    return true;
                }
            }
        }
        if (body.has("scenarioloopmap")) {
            // 判断loop
            JSONArray array = body.optJSONArray("scenarioloopmap");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.opt(i);
                int err = object.optInt("errorcode");
                if (err == MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                    return true;
                }
            }
        }
        if (!body.has("errorcode")) {
            return true;
        }
        return false;
    }

    /**
     * 检查字段，有一个error 就返回 true
     *
     * @param body
     * @return
     */
    public static int checkHaveOneFailWithBody(JSONObject body) {
        try {
            int errorCode = MessageErrorCode.MESSAGE_ERROR_CODE_OK;
            int error = body.optInt(CommonData.JSON_COMMAND_ERRORCODE);
            if (error != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                errorCode = error;
            }
            if (errorCode == MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                // 判断loop
                if (body.has("deviceloopmap")) {
                    JSONArray array = body.optJSONArray("deviceloopmap");
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = (JSONObject) array.get(i);
                            int err = object.optInt(CommonData.JSON_COMMAND_ERRORCODE);
                            if (err != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                                errorCode = err;
                                break;
                            }
                        }
                        if (errorCode != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                            return errorCode;
                        }
                    }
                }
                if (body.has("scenarioloopmap")) {
                    JSONArray array = body.optJSONArray("deviceloopmap");
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = (JSONObject) array.get(i);
                            int err = object.optInt(CommonData.JSON_COMMAND_ERRORCODE);
                            if (err == MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                                return MessageErrorCode.MESSAGE_ERROR_CODE_OK;
                            }
                        }
                        if (errorCode != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
                            return errorCode;
                        }
                    }
                }
                return MessageErrorCode.MESSAGE_ERROR_CODE_OK;
            } else {
                return errorCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MessageErrorCode.MESSAGE_ERROR_CODE_NO_ROOM_DEVICEVALUE;
        }
    }
}
