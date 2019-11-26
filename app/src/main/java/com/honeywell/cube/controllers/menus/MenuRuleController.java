package com.honeywell.cube.controllers.menus;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleAddDetails;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleConditionRoom;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ConditionFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ConditionInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioTriggerFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioTriggerInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerScenarioFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerScenarioInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PlistUtil;
import com.honeywell.cube.utils.RoomManager;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRuleEvent;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ResourceUtil;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/2. 11:35
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuRuleController {

    public static final String TAG = MenuRuleController.class.getSimpleName();
    private static boolean isCheckEnable = false;
    private static boolean isConfigRule = false;

    /**
     * 获取首页Rule列表
     *
     * @param context
     */
    public static void getRuleList(Context context) {
        //details-存储info需要的信息
        ArrayList<MenuRuleUIItem> info_details = new ArrayList<>();

        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)){
            EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.GET_RULE_LIST, false, info_details));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD){
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1){
                EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.GET_RULE_LIST, false, info_details));
                return;
            }
        }
        ScenarioTriggerFunc func = new ScenarioTriggerFunc(ConfigCubeDatabaseHelper.getInstance(context));
        //主界面 info
        ArrayList<ScenarioTriggerInfo> infos = (ArrayList<ScenarioTriggerInfo>) func.getScenarioTriggerInfoAllList();

        if (infos.size() == 0) {
            EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.GET_RULE_LIST, false, info_details));
        }
        for (int i = 0; i < infos.size(); i++) {
            ScenarioTriggerInfo info = infos.get(i);
            MenuRuleUIItem ruleObject = new MenuRuleUIItem();
            ruleObject.info = info;
            ruleObject.info_states = info.mSwitchStatus.equalsIgnoreCase("on");
            ruleObject.info_title = info.mDescription;

            try {
                JSONObject availableTime = new JSONObject(info.mAvaibleTime);
                String detailStr = "" + availableTime.optString("starttime") + "-" + availableTime.optString("endtime");
                JSONArray customizedDays = availableTime.optJSONArray("customizedays");
                String customDays = DeviceManager.getCellWeekShortNameFromProtocol(context, customizedDays);
                if (customDays.length() > 0) {
                    detailStr = detailStr + " " + customDays;
                }
                ruleObject.info_details = detailStr;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "ssd get rule list", true);
            }


            //Input
            ArrayList<ConditionInfo> conditionInfos = (ArrayList<ConditionInfo>) new ConditionFunc(ConfigCubeDatabaseHelper.getInstance(context)).getTriggerConditionInfoListByTriggerId(info.mPrimaryId);
            if (conditionInfos == null || conditionInfos.size() == 0) {
                Loger.print(TAG, "ssd getRuleList conditionInfos is null", Thread.currentThread());
            } else {
                ConditionInfo conditionInfo = conditionInfos.get(conditionInfos.size() - 1);
                if (conditionInfo != null) {
                    if (conditionInfo.mModuleType == ModelEnum.MODULE_TYPE_ROOM) {
                        ruleObject.info_input_imagename = R.mipmap.rules_room;
                        RoomLoop roomLoop = RoomLoopFunc.getInstance(context).getRoomLoopByPrimaryId(conditionInfo.mLoopPrimaryId);
                        if (roomLoop != null) {
                            ruleObject.info_input_name = roomLoop.mRoomName;
                        } else {
                            ruleObject.info_input_name = "";
                        }
                    } else {
                        //Sensor
                        ruleObject.info_input_imagename = R.mipmap.rules_sensor;
                        Object sensor = DeviceManager.getDeviceFromModuleTypeWithTypeAndID(context, conditionInfo.mModuleType, conditionInfo.mLoopPrimaryId);
                        if (sensor != null) {
                            if (sensor instanceof BasicLoop) {
                                BasicLoop sensorLoop = (BasicLoop) sensor;
                                ruleObject.info_input_name = sensorLoop.mLoopName;
                            } else {
                                ruleObject.info_input_name = "";
                            }
                        }
                    }
                }
            }

            //OutPut
            //scenario
            ArrayList<TriggerScenarioInfo> triggerScenarioInfos = (ArrayList<TriggerScenarioInfo>) new TriggerScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context)).getDeviceControlInfoListByTriggerId(info.mPrimaryId);
            if (triggerScenarioInfos != null && triggerScenarioInfos.size() > 0) {
                TriggerScenarioInfo triggerScenarioInfo = triggerScenarioInfos.get(triggerScenarioInfos.size() - 1);
                if (triggerScenarioInfo != null) {
                    try {
                        JSONObject actionInfo = new JSONObject(triggerScenarioInfo.mActionInfo);
                        int scenarioId = actionInfo.optInt("scenarioid");
                        ScenarioLoop loop = ScenarioController.getScenarioFromScenarioID(context, scenarioId);
                        int id = ResourceUtil.getResIdFromName(context, "rule_" + loop.mImageName);
                        ruleObject.info_output_imagename = loop == null ? R.mipmap.rule_scenario_home : (id == -1 ? R.mipmap.rule_scenario_home : id);
                        ruleObject.info_output_name = loop == null ? "" : loop.mScenarioName;
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e(TAG, "ssd get rule list", true);
                    }

                }
            }

            //Device
            ArrayList<TriggerDeviceInfo> triggerDeviceInfos = (ArrayList<TriggerDeviceInfo>) new TriggerDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getDeviceControlInfoListByTriggerId(info.mPrimaryId);
            if (triggerDeviceInfos != null && triggerDeviceInfos.size() > 0) {
                TriggerDeviceInfo triggerDeviceInfo = triggerDeviceInfos.get(triggerDeviceInfos.size() - 1);
                Object model = DeviceManager.getDeviceFromModuleTypeWithTypeAndID(context, triggerDeviceInfo.mModuleType, triggerDeviceInfo.mLoopPrimaryId);

                //IPC
                if (model instanceof IpcStreamInfo) {
                    IpcStreamInfo info1 = (IpcStreamInfo) model;
                    //periphera
                    PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(info1.mDevId);
                    ruleObject.info_output_imagename = R.mipmap.rule_device_type_ip_camera;
                    ruleObject.info_output_name = device.mName;
                } else {
                    //other
                    //Device type
                    int deviceType = DeviceManager.getDeviceTypeFromLoop(model);
                    //Loop type
                    BasicLoop loop = null;
                    if (model instanceof BasicLoop) {
                        loop = (BasicLoop) model;
                    }
                    ruleObject.info_output_imagename = DeviceManager.getTitleImageFromDeviceType(deviceType, true);
                    ruleObject.info_output_name = loop == null ? "" : loop.mLoopName;
                }
            }
            info_details.add(ruleObject);
        }
        //发送通知
        EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.GET_RULE_LIST, true, info_details));
    }

    /**
     * 编辑 或者 添加 返回二级页面 需要的参数
     *
     * @param context
     * @param info--info null 则为添加新的规则，否则为编辑规则
     * @return
     */
    public static MenuRuleAddDetails getRuleDetails(Context context, ScenarioTriggerInfo info) {
        MenuRuleAddDetails newDetails = new MenuRuleAddDetails();
        if (info == null) {
            //添加新规则
            newDetails.name = context.getString(R.string.rule_default_title);
            newDetails.repeat = context.getString(R.string.rule_repeat_type_never);
            newDetails = initRuleConditionRoomPart(context, newDetails);
            return newDetails;
        } else {
            newDetails.name = info.mName;
            newDetails.delay_time = info.mDelayTime;
            newDetails.info = info;
            String availabletime = info.mAvaibleTime;
            String customDays = context.getString(R.string.rule_repeat_type_never);
            if (availabletime != null && availabletime != "") {
                try {
                    JSONObject available = new JSONObject(availabletime);
                    JSONArray customizedays = available.optJSONArray("customizedays");
                    if (customizedays == null || customizedays.length() == 0) {
                        Loger.print(TAG, "ssd get rule details error customize days nil", Thread.currentThread());
                    } else {
                        customDays = DeviceManager.getCellWeekShortNameFromProtocol(context, customizedays);
                    }
                    newDetails.repeat = customDays;

                    //Time
                    String startTime = available.optString("starttime");
                    String[] startTimeArr = startTime.split(":");
                    if (startTimeArr.length == 3) {
                        int length = startTime.length();
                        startTime = startTime.substring(0, length - 3);
                    }
                    String endTime = available.optString("endtime");
                    String[] endTimeArr = endTime.split(":");
                    if (endTimeArr.length == 3) {
                        int length = endTime.length();
                        endTime = endTime.substring(0, length - 3);
                    }

                    //Set
                    newDetails.need_work_time = "on".equalsIgnoreCase(available.optString("status"));
                    newDetails.start_time = startTime;
                    newDetails.end_time = endTime;
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "ssd get rule details", true);
                }


            }

            //Input
            ArrayList<ConditionInfo> conditionInfos = (ArrayList<ConditionInfo>) new ConditionFunc(ConfigCubeDatabaseHelper.getInstance(context)).getTriggerConditionInfoListByTriggerId(info.mPrimaryId);
            if (conditionInfos == null || conditionInfos.size() == 0) {
                Loger.print(TAG, "ssd get rule details conditionInfos is null", Thread.currentThread());
            } else {
                ConditionInfo conditionInfo = conditionInfos.get(conditionInfos.size() - 1);
                if (conditionInfo != null) {
                    if (conditionInfo.mModuleType == ModelEnum.MODULE_TYPE_ROOM) {
                        newDetails.condition_type = ModelEnum.MODULE_TYPE_ROOM;

                        RoomLoop roomLoop = RoomLoopFunc.getInstance(context).getRoomLoopByPrimaryId(conditionInfo.mLoopPrimaryId);
                        newDetails.condition_name = roomLoop.mRoomName;
                        MenuRuleConditionRoom conditionRoom = new MenuRuleConditionRoom();
                        conditionRoom.room_name = roomLoop.mRoomName;
                        conditionRoom.room_id = roomLoop.mPrimaryId;
                        String actioninfoStr = conditionInfo.mActionInfo;
                        if (actioninfoStr != null && !"".equalsIgnoreCase(actioninfoStr)) {
                            try {
                                JSONObject conditionJson = new JSONObject(actioninfoStr);
                                conditionRoom.room_condition = RoomManager.transferRuleConditionRoomType(context, conditionJson.optString("keytype"));
                                conditionRoom.room_trigger_mode = RoomManager.transferRuleConditionRoomTrigger(context, conditionJson.optString("type"));
                                conditionRoom.room_value = RoomManager.transferRuleConditionRoomValue(context, conditionJson.optString("keyvalue"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtil.e(TAG, "ssd get rule details", true);
                            }
                        }
                        newDetails.conditionRoom = conditionRoom;
                    } else {
                        //初始化 room
                        newDetails = initRuleConditionRoomPart(context, newDetails);

                        //Sensor
                        Object sensor = DeviceManager.getDeviceFromModuleTypeWithTypeAndID(context, conditionInfo.mModuleType, conditionInfo.mLoopPrimaryId);
                        if (sensor != null) {
                            newDetails.sensor_object = sensor;
                            if (sensor instanceof BasicLoop) {
                                BasicLoop sensorLoop = (BasicLoop) sensor;
                                newDetails.condition_name = sensorLoop.mLoopName;
                            } else {
                                newDetails.condition_name = "";
                            }
                        }
                    }
                }
            }


            //out put
            //scenario
            ArrayList<TriggerScenarioInfo> triggerScenarioInfos = (ArrayList<TriggerScenarioInfo>) new TriggerScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context)).getDeviceControlInfoListByTriggerId(info.mPrimaryId);
            if (triggerScenarioInfos != null && triggerScenarioInfos.size() > 0) {
                TriggerScenarioInfo triggerScenarioInfo = triggerScenarioInfos.get(triggerScenarioInfos.size() - 1);
                if (triggerScenarioInfo != null) {
                    String actionInfo = triggerScenarioInfo.mActionInfo;
                    if (actionInfo != null && !"".equalsIgnoreCase(actionInfo)) {
                        try {
                            JSONObject object = new JSONObject(actionInfo);
                            if (object != null) {
                                int scenarioId = object.optInt("scenarioid");
                                ScenarioLoop scenarioLoop = ScenarioController.getScenarioFromScenarioID(context, scenarioId);
                                newDetails.action_scenario = scenarioLoop;
                                newDetails.action_type = 0;
                                newDetails.action_name = scenarioLoop == null ? "" : ScenarioController.getScenarioNameFromScenarioId(context, scenarioId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e(TAG, "ssd get rule details", true);
                        }

                    }
                }
            }

            //device
            ArrayList<TriggerDeviceInfo> triggerDeviceInfos = (ArrayList<TriggerDeviceInfo>) new TriggerDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getDeviceControlInfoListByTriggerId(info.mPrimaryId);
            if (triggerDeviceInfos != null && triggerDeviceInfos.size() > 0) {
                TriggerDeviceInfo triggerDeviceInfo = triggerDeviceInfos.get(triggerDeviceInfos.size() - 1);
                Object model = DeviceManager.getDeviceFromModuleTypeWithTypeAndID(context, triggerDeviceInfo.mModuleType, triggerDeviceInfo.mLoopPrimaryId);
                if (model == null) {
                    Loger.print(TAG, "ssd get rule details output device list is null", Thread.currentThread());
                } else {
                    String actionInfo = triggerDeviceInfo.mActionInfo;
                    if (actionInfo != null && !"".equalsIgnoreCase(actionInfo)) {
                        try {
                            //设置控制属性
                            JSONObject actionInfoJSON = new JSONObject(actionInfo);
                            DeviceManager.updateTriggerDeviceWithInfo(model, actionInfoJSON);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e(TAG, "ssd get rule details", true);
                        }
                    }

                    newDetails.action_type = 1;
                    newDetails.action_name = DeviceManager.getDeviceTitleWithObject(context, model);
                    newDetails.action_device = model;
                }
            }
            return newDetails;
        }
    }

    private static MenuRuleAddDetails initRuleConditionRoomPart(Context context, MenuRuleAddDetails newDetails) {
        newDetails.conditionRoom.room_condition = context.getString(R.string.rule_room_type_temperature);
        newDetails.conditionRoom.room_trigger_mode = context.getString(R.string.rule_room_trigger_high);
        ArrayList<Integer> roomIds = CommonCache.getRoomIdList(context);
        ArrayList<String> roomNames = CommonCache.getRoomNameList(context);
        newDetails.conditionRoom.room_id = roomIds.size() > 1 ? roomIds.get(1) : -1;
        newDetails.conditionRoom.room_name = roomNames.size() > 1 ? roomNames.get(1) : "";
        return newDetails;
    }

    /**
     * 获取 zone 列表
     *
     * @param context
     * @return
     */
    public static ArrayList<MenuScheduleDeviceObject> getZoneList(Context context) {
        ArrayList<Object> list = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, ModelEnum.MAIN_ZONE);
        ArrayList<MenuScheduleDeviceObject> returnValue = new ArrayList<>();

        MenuScheduleDeviceObject title = new MenuScheduleDeviceObject();
        title.type = ModelEnum.UI_TYPE_TITLE;
        title.section = context.getString(R.string.main_zone);
        returnValue.add(title);

        if (list.size() == 0) {
            return returnValue;
        } else {
            for (int i = 0; i < list.size(); i++) {
                MenuScheduleDeviceObject device = new MenuScheduleDeviceObject();
                device.type = ModelEnum.UI_TYPE_OTHER;
                device.section = context.getString(R.string.main_zone);
                device.title = DeviceManager.getDeviceTitleWithObject(context, list.get(i));
                if (list.get(i) instanceof WiredZoneLoop) {
                    device.loopType = ModelEnum.LOOP_ZONE;
                } else if (list.get(i) instanceof IpvdpZoneLoop) {
                    device.loopType = ModelEnum.LOOP_IPVDP;
                } else if (list.get(i) instanceof SparkLightingLoop) {
                    device.loopType = ModelEnum.SPARKLIGHTING;
                } else if (list.get(i) instanceof Wireless315M433MLoop) {
                    device.loopType = ModelEnum.WIRELESS_315_433;
                }
                device.loop = list.get(i);
                returnValue.add(device);
            }
            return returnValue;
        }
    }

    /**
     * 获取 Rule 设备列表
     *
     * @param context
     * @return
     */
    public static ArrayList<MenuScheduleDeviceObject> getRuleDeviceList(Context context) {
        return DeviceManager.getDeviceList(context, true);
    }

    /**
     * 将 plist文件中数据转换为 显示的String 用于显示 ”重复“ 点击后显示的数据
     * 后面放到 DeviceManager中
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getWeekArrayList(Context context) {
        ArrayList<Object> weekList = PlistUtil.parseArrayPlistWithName("WeekArray.plist");
        if (weekList == null || weekList.size() == 0) {
            Loger.print(TAG, "ssd 解析 weekArray 失败", Thread.currentThread());
            return null;
        }
        ArrayList<String> returnValue = new ArrayList<>();
        for (Object object : weekList) {
            if (object instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) object;
                String protocol = (String) map.get("protocol");
                String value = DeviceManager.getWeekStrFromProtocol(context, protocol);
                returnValue.add(value);
            }
        }
        return returnValue;
    }

    /**
     * 获取 rule 房间 条件类型
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getRuleRoomTypeList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.rule_room_type_AirQ));
        returnValue.add(context.getString(R.string.rule_room_type_PM));
        returnValue.add(context.getString(R.string.rule_room_type_temperature));
        returnValue.add(context.getString(R.string.rule_room_type_humidity));
        return returnValue;
    }

    /**
     * 获取 rule 房间 触发条件
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getRuleRoomTriggerList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.rule_room_trigger_high));
        returnValue.add(context.getString(R.string.rule_room_trigger_low));
        return returnValue;
    }

    /**
     * 获取 rule 房间的 值 列表
     *
     * @param context
     * @param type
     * @return
     */
    public static ArrayList<String> getRuleRoomValue(Context context, String type) {
        ArrayList<String> returnValue = new ArrayList<>();
        if (context.getString(R.string.rule_room_type_AirQ).equalsIgnoreCase(type)) {
            returnValue.add(context.getString(R.string.rule_roomAQ_value_clean));
            returnValue.add(context.getString(R.string.rule_roomAQ_value_slight));
            returnValue.add(context.getString(R.string.rule_roomAQ_value_moderate));
            returnValue.add(context.getString(R.string.rule_roomAQ_value_serious));
            return returnValue;
        } else if (context.getString(R.string.rule_room_type_PM).equalsIgnoreCase(type)) {
            for (int i = 80; i < 301; i++) {
                returnValue.add("" + i);
            }
            return returnValue;
        } else if (context.getString(R.string.rule_room_type_temperature).equalsIgnoreCase(type)) {
            for (int i = 10; i < 41; i++) {
                returnValue.add("" + i);
            }
            return returnValue;
        } else if (context.getString(R.string.rule_room_type_humidity).equalsIgnoreCase(type)) {
            for (int i = 10; i < 81; i++) {
                returnValue.add("" + i);
            }
            return returnValue;
        }
        return returnValue;
    }


    /**
     * 发送 Rule 打开关闭的命令
     *
     * @param context
     * @param isOn
     * @param info
     */
    public static void enableRule(Context context, boolean isOn, ScenarioTriggerInfo info) {
        if (info == null) {
            Loger.print(TAG, "ssd send switch status info is null", Thread.currentThread());
            return;
        }
        isCheckEnable = true;
        String msg = MessageManager.getInstance(context).sendRuleStatus(isOn, info);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(msg);
    }

    /**
     * 发送Delete rule的命令
     *
     * @param context
     * @param info
     */
    public static void deleteRule(Context context, ScenarioTriggerInfo info) {
        if (info == null) {
            Loger.print(TAG, "ssd send delete rule info is null", Thread.currentThread());
            return;
        }
        String msg = MessageManager.getInstance(context).deleteRule(info);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(msg);
    }

    /**
     * 编辑 或者 删除 rule 的命令
     * 调用这条命令之前，如果是
     *
     * @param context
     * @param details
     */
    public static void modifyRule(Context context, MenuRuleAddDetails details) {
        if (details == null) {
            Loger.print(TAG, "ssd send modify rule parameters is null", Thread.currentThread());
            return;
        }
        if (details.info != null) {
            //有就删除，没有就不删除
            ScenarioTriggerFunc func = new ScenarioTriggerFunc(ConfigCubeDatabaseHelper.getInstance(context));
            //主界面 info
            ArrayList<ScenarioTriggerInfo> infos = (ArrayList<ScenarioTriggerInfo>) func.getScenarioTriggerInfoAllList();
            if (infos.size() != 0) {
                for (int i = 0; i < infos.size(); i++) {
                    if (infos.get(i).mPrimaryId == details.info.mPrimaryId) {
                        MenuRuleController.deleteRule(context, details.info);
                        break;
                    }
                }
            }
        }
        isConfigRule = true;

        Map<String, Object> conditionMap = new HashMap<>();

        //condition
        if (details.condition_type == ModelEnum.MODULE_TYPE_ROOM) {
            MenuRuleConditionRoom conditionRoom = details.conditionRoom;
            String type = RoomManager.getRuleConditionRoomTypeProtocol(context, conditionRoom.room_condition);
            String trigger = RoomManager.getRuleConditionRoomTriggerProtocol(context, conditionRoom.room_trigger_mode);
            String value = RoomManager.getRuleConditionRoomValue(context, type, conditionRoom.room_value);
            //room
            Map<String, Object> actionInfo = new HashMap<>();
            actionInfo.put("type", trigger);
            actionInfo.put("keytype", type);
            actionInfo.put("keyvalue", value);

            conditionMap.put("moduletype", "room");
            conditionMap.put("roomid", conditionRoom.room_id);
            conditionMap.put("actioninfo", actionInfo);
        } else {
            Object sensorObject = details.sensor_object;
            if (sensorObject instanceof WiredZoneLoop) {
                WiredZoneLoop loop = (WiredZoneLoop) sensorObject;
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);
                if (device == null) {
                    Loger.print(TAG, "ssd get periphera error", Thread.currentThread());
                }

                Map<String, Object> actionInfo = new HashMap<>();
                actionInfo.put("type", "alarm");
                actionInfo.put("keytype", loop.mAlarmType);
                actionInfo.put("keyvalue", "trigger");

                conditionMap.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                conditionMap.put("primaryid", loop.mLoopSelfPrimaryId);
                conditionMap.put("actioninfo", actionInfo);
            } else if (sensorObject instanceof IpvdpZoneLoop) {
                IpvdpZoneLoop loop = (IpvdpZoneLoop) sensorObject;
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

                if (device == null) {
                    Loger.print(TAG, "ssd ipvdp get periphera error", Thread.currentThread());
                }

                Map<String, Object> actionInfo = new HashMap<>();
                actionInfo.put("type", "alarm");
                actionInfo.put("keytype", loop.mAlarmType);
                actionInfo.put("keyvalue", "trigger");

                conditionMap.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                conditionMap.put("primaryid", loop.mLoopSelfPrimaryId);
                conditionMap.put("actioninfo", actionInfo);
            } else if (sensorObject instanceof SparkLightingLoop) {
                SparkLightingLoop loop = (SparkLightingLoop) sensorObject;
                if (loop.mLoopType == ModelEnum.LOOP_TYPE_SENSOR_INT) {
                    PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

                    if (device == null) {
                        Loger.print(TAG, "ssd ipvdp get periphera error", Thread.currentThread());
                    }

                    Map<String, Object> actionInfo = new HashMap<>();
                    actionInfo.put("type", "event");
                    actionInfo.put("keytype", "motion");
                    actionInfo.put("keyvalue", "trigger");

                    conditionMap.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    conditionMap.put("primaryid", loop.mLoopSelfPrimaryId);
                    conditionMap.put("actioninfo", actionInfo);
                }
            } else if (sensorObject instanceof Wireless315M433MLoop) {
                Wireless315M433MLoop loop = (Wireless315M433MLoop) sensorObject;
                if (loop.mLoopType == ModelEnum.LOOP_TYPE_5800PIRAP_INT || loop.mLoopType == ModelEnum.LOOP_TYPE_5804EU_INT || loop.mLoopType == ModelEnum.LOOP_TYPE_5816EU_INT) {
                    PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

                    if (device == null) {
                        Loger.print(TAG, "ssd ipvdp get periphera error", Thread.currentThread());
                    }

                    Map<String, Object> actionInfo = new HashMap<>();
                    actionInfo.put("type", "alarm");
                    actionInfo.put("keytype", loop.mAlarmType);
                    actionInfo.put("keyvalue", "trigger");

                    conditionMap.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    conditionMap.put("primaryid", loop.mLoopSelfPrimaryId);
                    conditionMap.put("actioninfo", actionInfo);
                }
            }
        }

        if (conditionMap.keySet().size() == 0) {
            Loger.print(TAG, "ssd condition map count is 0", Thread.currentThread());
            return;
        }

        //action
        Map<String, Object> actionMap = new HashMap<>();
        if (details.action_type == 0) {
            //scenario
            ScenarioLoop loop = details.action_scenario;
            Map<String, Object> actionInfo = new HashMap<>();
            actionInfo.put("scenarioid", (loop == null ? 1 : loop.mScenarioId));
            actionInfo.put("securitypwd", NetConstant.MAIN_SCENARIO_PASSWORD);

            actionMap.put("type", "scenariocontrol");
            actionMap.put("actioninfo", actionInfo);
        } else {
            //device
            ArrayList<Object> deviceArr = new ArrayList<>();
            deviceArr.add(details.action_device);
            ArrayList<Map<String, Object>> deviceloopMap = ScenarioController.getScenarioDevicesLoopmapWithArray(context, deviceArr, false);

            actionMap.put("type", "devicecontrol");
            actionMap.put("deviceloopmap", deviceloopMap);
        }
        if (actionMap.keySet().size() == 0) {
            Loger.print(TAG, "ssd action map count is 0", Thread.currentThread());
            return;
        }
        //重复 时间
        //repeat
        ArrayList<Map> repeatArr = DeviceManager.getCellWeekProtocolFromStr(context, details.repeat);
        ArrayList<Map<String, Object>> repeatDic = new ArrayList<>();
        for (int i = 0; i < repeatArr.size(); i++) {
            String day = (String) repeatArr.get(i).get("day");
            Map<String, Object> item = new HashMap<>();
            item.put("day", day);
            repeatDic.add(item);
        }

        //有效时间段
        String startTimeStr = details.start_time + ":00";
        String endTimeStr = details.end_time + ":00";

        //Set
        Map<String, Object> availableTime = new HashMap<>();
        availableTime.put("status", details.need_work_time ? "on" : "off");
        availableTime.put("starttime", startTimeStr);
        availableTime.put("endtime", endTimeStr);
        availableTime.put("frequency", repeatDic.size() > 0 ? "customize" : "single");
        availableTime.put("customizedays", repeatDic);

        ArrayList<Map<String, Object>> conditionArray = new ArrayList<>();
        conditionArray.add(conditionMap);
        ArrayList<Map<String, Object>> actionArray = new ArrayList<>();
        actionArray.add(actionMap);

        String message = MessageManager.getInstance(context).addRule(availableTime, details.delay_time, details.name, conditionArray, actionArray);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /***************************** handle responce **********************************/
    /**
     * 处理Configdevice 命令操作
     *
     * @param context
     * @param data
     */
    public static void handleRuleResponceConfigDeviceWithInfo(Context context, JSONObject data) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(data);
        if (errorCode != 0) {
            Loger.print(TAG, "ssd handle rule responce have error", Thread.currentThread());
            if (isConfigRule) {
                EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.CONFIG_RULE_STATE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            } else {
                EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.CONFIG_RULE_STATE_DELETE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            }
            return;
        }
        String configtype = data.optString("configtype");
        if ("add".equalsIgnoreCase(configtype)) {
            int triggerid = data.optInt("responseprimaryid");
            ScenarioTriggerInfo info = new ScenarioTriggerInfo();
            info.mPrimaryId = triggerid;
            info.mSwitchStatus = data.optString("status");
            info.mAvaibleTime = data.optString("availabletime");
            info.mDelayTime = data.optInt("delaytime");
            info.mDescription = data.optString("description");
            info.mName = data.optString("aliasname");
            info.mType = data.optString("moduletype");
            new ScenarioTriggerFunc(ConfigCubeDatabaseHelper.getInstance(context)).addScenarioTriggerInfo(info);
            //condition
            JSONArray conditionArray = data.optJSONArray("condition");
            if (conditionArray == null || conditionArray.length() == 0) {
                Loger.print(TAG, "ssd responce config device add condition size is 0", Thread.currentThread());
            } else {
                for (int i = 0; i < conditionArray.length(); i++) {
                    JSONObject conditionMap = conditionArray.optJSONObject(i);
                    if (conditionMap != null) {
                        ConditionInfo conditionInfo = new ConditionInfo();
                        conditionInfo.mPrimaryId = conditionMap.optInt("responseprimaryid");
                        conditionInfo.mActionInfo = conditionMap.optString("actioninfo");
                        conditionInfo.mModuleType = DeviceManager.getModuleTypeIntFromString(conditionMap.optString("moduletype"));
                        conditionInfo.mTriggerOrRuleId = triggerid;
                        //loop primary id
                        if (conditionInfo.mModuleType == ModelEnum.MODULE_TYPE_ROOM) {
                            RoomLoop roomLoop = RoomLoopFunc.getInstance(context).getRoomLoopByPrimaryId(conditionMap.optInt("roomid"));
                            if (roomLoop != null) {
                                conditionInfo.mLoopPrimaryId = roomLoop.mPrimaryId;
                            }
                        } else {
                            conditionInfo.mLoopPrimaryId = conditionMap.optInt("primaryid");
                        }

                        new ConditionFunc(ConfigCubeDatabaseHelper.getInstance(context)).addTriggerConditionInfo(conditionInfo);
                    }
                }
            }
            //action
            JSONArray actionArray = data.optJSONArray("triggeraction");
            if (actionArray == null || actionArray.length() == 0) {
                Loger.print(TAG, "ssd responce handle config rule action is null", Thread.currentThread());
            } else {
                for (int i = 0; i < actionArray.length(); i++) {
                    JSONObject actionobject = actionArray.optJSONObject(i);
                    if (actionobject != null) {
                        if ("scenariocontrol".equalsIgnoreCase(actionobject.optString("type"))) {
                            TriggerScenarioInfo triggerScenarioInfo = new TriggerScenarioInfo();
                            triggerScenarioInfo.mPrimaryId = actionobject.optInt("responseprimaryid");
                            triggerScenarioInfo.mTriggerOrRuleId = triggerid;
                            triggerScenarioInfo.mActionInfo = actionobject.optString("actioninfo");
                            new TriggerScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context)).addTriggerScenario(triggerScenarioInfo);
                        } else if ("devicecontrol".equalsIgnoreCase(actionobject.optString("type"))) {
                            JSONArray deviceloopMap = actionobject.optJSONArray("deviceloopmap");
                            if (deviceloopMap != null && deviceloopMap.length() > 0) {
                                for (int j = 0; j < deviceloopMap.length(); j++) {
                                    JSONObject deviceloopmap = deviceloopMap.optJSONObject(j);
                                    TriggerDeviceInfo triggerDeviceInfo = new TriggerDeviceInfo();
                                    triggerDeviceInfo.mPrimaryId = deviceloopmap.optInt("responseprimaryid");
                                    triggerDeviceInfo.mTriggerOrRuleId = triggerid;
                                    triggerDeviceInfo.mActionInfo = deviceloopmap.optString("actioninfo");
                                    triggerDeviceInfo.mModuleType = DeviceManager.getModuleTypeIntFromString(deviceloopmap.optString("moduletype"));
                                    triggerDeviceInfo.mLoopPrimaryId = deviceloopmap.optInt("primaryid");
                                    new TriggerDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).addTriggerDevice(triggerDeviceInfo);
                                }
                            }

                        }
                    }
                }
            }
        } else if ("delete".equalsIgnoreCase(configtype)) {
            int triggerid = data.optInt("primaryid");
            //操作数据库
            new ScenarioTriggerFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteScenarioTriggerInfoByPrimaryID(triggerid);
            //condition
            new ConditionFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteTriggerConditionInfoByTriggerId(triggerid);
            //action Scenario
            new TriggerScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteTriggerScenarioControlInfoByTriggerId(triggerid);
            //action device
            new TriggerDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteTriggerDeviceControlInfoByTriggerId(triggerid);
            if (!isConfigRule) {
                EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.CONFIG_RULE_STATE_DELETE, true, "删除成功"));
            }
            return;
        } else if ("modify".equalsIgnoreCase(configtype)) {
            int triggerid = data.optInt("primaryid");
            //操作数据库
            ScenarioTriggerFunc scenarioTriggerFunc = new ScenarioTriggerFunc(ConfigCubeDatabaseHelper.getInstance(context));
            ScenarioTriggerInfo info = scenarioTriggerFunc.getScenarioTriggerInfoByPrimaryId(triggerid);
            info.mSwitchStatus = data.optString("status");
            scenarioTriggerFunc.updateScenarioTriggerInfoByPrimaryId(triggerid, info);
            EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.ENABLE_RULE, true, "操作成功"));
            return;
        }
        isConfigRule = false;
        EventBus.getDefault().post(new CubeRuleEvent(CubeEvents.CubeRuleEventType.CONFIG_RULE_STATE, true, "操作成功"));
    }
}
