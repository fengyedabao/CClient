package com.honeywell.cube.controllers.menus;

import android.content.Context;
import android.text.TextUtils;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleAddDetails;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleUIItem;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCodeFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleDeviceInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleRuleFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleRuleInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleScenarioFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScheduleScenarioInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485LoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.IrScenarioCodeCustom;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRuleEvent;
import com.honeywell.cube.utils.events.CubeScheduleEvent;
import com.honeywell.lib.utils.LogUtil;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/29. 10:23
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuScheduleController {
    private static final String TAG = MenuScheduleController.class.getSimpleName();

    private static boolean isCheckEnable = false;

    /**
     * 获取首页需求的参数
     *
     * @param context
     * @return
     * @throws JSONException
     */
    public static void getScheduleList(Context context) {
        ArrayList<MenuScheduleUIItem> returnValue = new ArrayList<>();
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.GET_SCHEDULE_LIST, returnValue, true));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.GET_SCHEDULE_LIST, returnValue, true));
                return;
            }
        }
        ScheduleRuleFunc scheduleRuleFunc = new ScheduleRuleFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<ScheduleRuleInfo> scheduleRuleInfos = (ArrayList<ScheduleRuleInfo>) scheduleRuleFunc.getScheduleRuleInfoAllList();
        if (scheduleRuleInfos.size() == 0) {
            Loger.print(TAG, "ssd schedule info is 0", Thread.currentThread());
            EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.GET_SCHEDULE_LIST, returnValue, true));
            return;
        }
        for (ScheduleRuleInfo info : scheduleRuleInfos) {
            MenuScheduleUIItem uiItem = new MenuScheduleUIItem();
            String availableTime = info.mAvaibleTime;
            if (availableTime != null && !"".equalsIgnoreCase(availableTime)) {
                try {
                    JSONObject object = new JSONObject(availableTime);

                    String startTime = object.optString("starttime");//去掉 :00
                    if (TextUtils.isEmpty(startTime)) {
                        Loger.print(TAG, "ssd start time : " + startTime + "  is error", Thread.currentThread());
                        uiItem.start_time_str = "00:00";
                    } else {
                        startTime = startTime.substring(0, startTime.length() - 3);//这部分有可能有问题
                        uiItem.start_time_str = startTime;
                    }

                    uiItem.details_name = info.mName;
                    JSONArray array = object.optJSONArray("customizedays");
                    String timeStr = DeviceManager.getCellWeekShortNameFromProtocol(context, array);
                    if (!"".equalsIgnoreCase(timeStr)) {
                        uiItem.details_repeat = timeStr;
                    }
                    if (CommonData.SWITCHSTATUS_TYPE_ON.equalsIgnoreCase(info.mSwitchStatus)) {
                        uiItem.isOn = true;
                    } else {
                        uiItem.isOn = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "JSONException e " + e.getMessage(), true);
                }
            }
            uiItem.scheduleRuleInfo = info;
            returnValue.add(uiItem);
        }
        EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.GET_SCHEDULE_LIST, returnValue, true));
    }

    /**
     * 执行 或者 关闭 计划
     *
     * @param context
     * @param uiItem
     */
    public static void enableSchedule(Context context, MenuScheduleUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd enable schedule parameters is null", Thread.currentThread());
            return;
        }
        ScheduleRuleInfo info = uiItem.scheduleRuleInfo;
        if (info == null) {
            Loger.print(TAG, "ssd enable schedule paramenters info is null", Thread.currentThread());
            return;
        }
        boolean ison = uiItem.isOn;
        String message = MessageManager.getInstance(context).enableSchedule(info.mPrimaryId, ison);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);

        //状态位
        isCheckEnable = true;
    }

    /**
     * 删除设备
     *
     * @param context
     * @param uiItem
     */
    public static void deleteSchedule(Context context, MenuScheduleUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd delete schedule parameters is null", Thread.currentThread());
            return;
        }
        ScheduleRuleInfo info = uiItem.scheduleRuleInfo;
        if (info == null) {
            Loger.print(TAG, "ssd delete schedule paramenters info is null", Thread.currentThread());
            return;
        }

        String message = MessageManager.getInstance(context).deleteSchedule(info.mPrimaryId);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    public static void deleteSchedule(Context context, ScheduleRuleInfo scheduleRuleInfo) {
        if (scheduleRuleInfo == null) {
            Loger.print(TAG, "ssd delete schedule parameters is null", Thread.currentThread());
            return;
        }

        String message = MessageManager.getInstance(context).deleteSchedule(scheduleRuleInfo.mPrimaryId);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 添加 或者 编辑 计划 Schedule
     *
     * @param context
     * @param uiItem--为Null时为添加，其他为编辑
     * @return
     */
    public static MenuScheduleAddDetails getScheduleDetails(Context context, MenuScheduleUIItem uiItem) {
        //添加
        MenuScheduleAddDetails details = new MenuScheduleAddDetails();
        if (uiItem == null) {
            details.title = context.getString(R.string.schedule_create);
            details.name = context.getString(R.string.menu_schedule);
            details.repeat = context.getString(R.string.rule_repeat_type_never);

            //测试
            ArrayList<ScenarioLoop> scenarioLoops = (ArrayList<ScenarioLoop>) new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getScenarioLoopListByScenarioId(1);
            if (scenarioLoops.size() > 0) {
                details.action_scenarioloop = scenarioLoops.get(0);
            }
            return details;
        } else {
            details.title = context.getString(R.string.rule_edit);
            //编辑
            ScheduleRuleInfo info = uiItem.scheduleRuleInfo;
            if (info == null) {
                Loger.print(TAG, "ssd modify schedule parameters is null", Thread.currentThread());
                return details;
            }
            details.scheduleRuleInfo = info;
            details.name = info.mName;

            //repeat
            String availableTime = info.mAvaibleTime;
            if (availableTime != null && !"".equalsIgnoreCase(availableTime)) {
                try {
                    JSONObject object = new JSONObject(availableTime);
                    if (object != null) {
                        JSONArray JSONArray = object.optJSONArray("customizedays");
                        details.repeat = DeviceManager.getCellWeekShortNameFromProtocol(context, JSONArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "ssd get schedule details exception 01", true);
                }

            }
            //action scenario
            ArrayList<ScheduleScenarioInfo> scheduleScenarioInfos = (ArrayList<ScheduleScenarioInfo>) new ScheduleScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context)).getScheduleScenarioInfoListByRuleId(info.mPrimaryId);
            if (scheduleScenarioInfos.size() == 0) {
                Loger.print(TAG, "ssd schedule scenario info size is 0", Thread.currentThread());
            } else {
                for (ScheduleScenarioInfo info1 : scheduleScenarioInfos) {
                    //action info
                    details.action_type = 0;
                    String actionInfoStr = info1.mActionInfo;
                    if (actionInfoStr != null && !"".equalsIgnoreCase(actionInfoStr)) {
                        try {
                            JSONObject actionJson = new JSONObject(actionInfoStr);
                            details.action_scenario_actioninfo = actionJson;
                            details.action_schedule_scenario_id = info1.mPrimaryId;
                            ArrayList<ScenarioLoop> scenarioLoops = (ArrayList<ScenarioLoop>) new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getScenarioLoopListByScenarioId(actionJson.optInt("scenarioid"));
                            if (scenarioLoops.size() > 0) {
                                details.action_scenarioloop = scenarioLoops.get(0);
                                details.action_title = details.action_scenarioloop.mScenarioName;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            LogUtil.e(TAG, "ssd get schedule details exception 02", true);
                        }

                    }
                }
            }

            //action device
            ArrayList<ScheduleDeviceInfo> scheduleDeviceInfos = (ArrayList<ScheduleDeviceInfo>) new ScheduleDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getDeviceControlInfoListByTriggerId(info.mPrimaryId);
            if (scheduleDeviceInfos.size() == 0) {
                Loger.print(TAG, "ssd schedule device info size is 0", Thread.currentThread());
            } else {
                //DeviceLoopMap
                details.action_type = 1;
                for (ScheduleDeviceInfo info1 : scheduleDeviceInfos) {
                    try {
                        String actionInfoStr = info1.mActionInfo;
                        if (actionInfoStr != null && !"".equalsIgnoreCase(actionInfoStr)) {
                            JSONObject actionJson = new JSONObject(actionInfoStr);
                            details.action_device_actioninfo = actionJson;
                        }
                        details.action_device_module_type = DeviceManager.getModuleTypeProtocolFromInt(info1.mModuleType);
                        details.action_schedule_device_loop_id = info1.mLoopPrimaryId;
                        details.action_schedule_device_id = info1.mPrimaryId;
                        details.action_device = MenuScheduleController.getDeviceActionBodyWith(context, info1);
                        details.action_title = ((BasicLoop) details.action_device).mLoopName;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e(TAG, "ssd get schedule details exception 03", true);
                    }

                }
            }
        }
        return details;
    }

    /**
     * 发送 编辑 Schedule
     *
     * @param context
     * @param details
     */
    public static void sendModifySchedule(Context context, MenuScheduleAddDetails details) {
        if (details == null) {
            Loger.print(TAG, "ssd send modify schedule parameters is null", Thread.currentThread());
            return;
        }
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE, context.getString(R.string.error_time_out), false));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE, context.getString(R.string.error_offline), false));
                return;
            }
        }
        if (isCheckEnable) isCheckEnable = !isCheckEnable;
        Map<String, Object> bodyDic = new HashMap<>();
        bodyDic.put("action", "request");
        bodyDic.put("subaction", "configdevice");
        bodyDic.put("configtype", "add");
        bodyDic.put("moduletype", "schedulerule");
        bodyDic.put("status", "on");
        bodyDic.put("aliasname", details.name);
        bodyDic.put("description", "");

        ArrayList<Map> weekArray = DeviceManager.getCellWeekProtocolFromStr(context, details.repeat);
        String timeStr = details.action_time;
        Map<String, Object> available = new HashMap<>();
        available.put("starttime", timeStr);
        available.put("frequency", weekArray.size() > 0 ? "customize" : "single");
        available.put("customizedays", weekArray);
        bodyDic.put("availabletime", available);

        //action
        Map<String, Object> actionInfo = new HashMap<>();
        if (details.action_type == 0) {
            //scenario
            ScenarioLoop loop = details.action_scenarioloop;
            if (loop == null) {
                Loger.print(TAG, "ssd action scenario is null", Thread.currentThread());
            }
            Map<String, Object> actionDetail = new HashMap<>();
            actionDetail.put("scenarioid", loop.mScenarioId);
            actionDetail.put("securitypwd", NetConstant.MAIN_SCENARIO_PASSWORD);

            actionInfo.put("type", "scenariocontrol");
            actionInfo.put("actioninfo", actionDetail);
        } else {
            ArrayList<Object> devicelist = new ArrayList<>();
            devicelist.add(details.action_device);
            ArrayList<Map<String, Object>> devicesloopmap = ScenarioController.getScenarioDevicesLoopmapWithArray(context, devicelist, false);

            actionInfo.put("type", "devicecontrol");
            actionInfo.put("deviceloopmap", devicesloopmap);
        }
        ArrayList<Map> actionInfoList = new ArrayList<>();
        actionInfoList.add(actionInfo);
        bodyDic.put("scheduleaction", actionInfoList);

        ScheduleRuleInfo info = details.scheduleRuleInfo;
        if (info != null) {
            bodyDic = MenuScheduleController.updateEditAction(bodyDic, details);
        }
        String message = MessageManager.getInstance(context).modifySchedule(bodyDic);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 编辑 Action
     *
     * @param bodyDic
     */
    private static Map updateEditAction(Map bodyDic, MenuScheduleAddDetails details) {
        ScheduleRuleInfo info = details.scheduleRuleInfo;
        bodyDic.put("configtype", "modify");
        bodyDic.put("primaryid", "" + info.mPrimaryId);

        //send 有且只能有一个输出
        ArrayList<Object> list = (ArrayList<Object>) bodyDic.get("scheduleaction");
        if (list == null || list.size() == 0) return bodyDic;
        Map scheduleactionDic = (Map) list.get(list.size() - 1);
        //Scenario Action
        String sendType = (String) scheduleactionDic.get("type");
        if ("scenariocontrol".equalsIgnoreCase(sendType)) {
            list = MenuScheduleController.updateScenarioAction(scheduleactionDic, list, details);
        } else if ("devicecontrol".equalsIgnoreCase(sendType)) {
            //device action
            ArrayList<Map> deviceloopmap = (ArrayList<Map>) scheduleactionDic.get("deviceloopmap");
            MenuScheduleController.updateDeviceActionWithDeviceloopmap(deviceloopmap, list, details);
        }
        bodyDic.put("scheduleaction", list);
        return bodyDic;
    }

    /**
     * 更新 Scenario action
     *
     * @param scheduleactionDic
     * @param list
     */
    private static ArrayList<Object> updateScenarioAction(Map scheduleactionDic, ArrayList<Object> list, MenuScheduleAddDetails details) {
        Map actionInfo = (Map) scheduleactionDic.get("actioninfo");
        list.remove(list.size() - 1);
        if (details.action_scenario_actioninfo != null) {
            int scenarioId01 = (int) actionInfo.get("scenarioid");
            int scenarioId02 = details.action_scenario_actioninfo.optInt("scenarioid");
            if (scenarioId01 == scenarioId02) {
                //相同则modify
                scheduleactionDic.put("configtype", "modify");
                scheduleactionDic.put("primaryid", details.action_schedule_scenario_id);
                list.add(scheduleactionDic);
            } else {
                //不相同这Delete 以前的 Add 现在的
                //add
                scheduleactionDic.put("configtype", "add");
                list.add(scheduleactionDic);

                Map map = new HashMap();
                map.put("type", "scenariocontrol");
                map.put("configtype", "delete");
                map.put("primaryid", details.action_schedule_scenario_id);
                list.add(map);
            }
        } else {
            //add
            scheduleactionDic.put("configtype", "add");
            list.add(scheduleactionDic);

            //以前设备 现在改场景, 删除设备
            if (details.action_device_actioninfo != null) {
                //Edit Actioninfo 有且只有一个

                Map map = new HashMap();
                map.put("configtype", "delete");
                map.put("primaryid", details.action_schedule_device_id);

                ArrayList<Map> temp = new ArrayList<>();
                temp.add(map);

                Map map1 = new HashMap();
                map1.put("type", "devicecontrol");
                map1.put("deviceloopmap", temp);

                list.add(map1);
            }
        }
        return list;

    }

    private static ArrayList<Object> updateDeviceActionWithDeviceloopmap(ArrayList<Map> deviceLoopMap, ArrayList list, MenuScheduleAddDetails details) {
        //Send action info 有且只有一个
        if (deviceLoopMap.size() <= 0) return list;
        Map scheduleactionDic = (Map) list.get(list.size() - 1);
        Map actioninfo = deviceLoopMap.get(deviceLoopMap.size() - 1);
        if (details.action_device_actioninfo != null) {
            //Edit action info 有且只有一个
//            int loopid = Integer.parseInt((String)actioninfo.get("primaryid"));
            long primaryid = (long) actioninfo.get("primaryid");
            String moduleType = (String) actioninfo.get("moduletype");
            if (moduleType.equalsIgnoreCase(details.action_device_module_type) && (details.action_schedule_device_loop_id) == (primaryid)) {
                //相同 则 modify
                actioninfo.put("configtype", "modify");
                actioninfo.put("primaryid", details.action_schedule_device_loop_id);
                actioninfo.put("actioninfo", details.action_device_actioninfo);
                deviceLoopMap.remove(deviceLoopMap.size() - 1);
                deviceLoopMap.add(actioninfo);
                scheduleactionDic.put("deviceloopmap", deviceLoopMap);
                list.remove(list.size() - 1);
                list.add(scheduleactionDic);
            } else {
                //不相同则Delete以前的，add现在的
                //add
                actioninfo.put("configtype", "add");
                deviceLoopMap.remove(deviceLoopMap.size() - 1);
                deviceLoopMap.add(actioninfo);
                //Delete
                Map map = new HashMap();
                map.put("configtype", "delete");
                map.put("primaryid", details.action_schedule_device_loop_id);

                deviceLoopMap.add(map);
                scheduleactionDic.put("deviceloopmap", deviceLoopMap);
                list.remove(list.size() - 1);
                list.add(scheduleactionDic);
            }
        } else {
            //add
            actioninfo.put("configtype", "add");
            deviceLoopMap.remove(deviceLoopMap.size() - 1);
            deviceLoopMap.add(actioninfo);
            scheduleactionDic.put("deviceloopmap", deviceLoopMap);
            list.remove(list.size() - 1);
            list.add(scheduleactionDic);
            //以前场景 现在改设备，删除场景
            if (details.action_scenario_actioninfo != null) {
                Map map1 = new HashMap();
                map1.put("type", "scenariocontrol");
                map1.put("configtype", "delete");
                map1.put("primaryid", "" + details.action_schedule_scenario_id);

                list.add(map1);
            }
        }
        return list;
    }

    /**
     * 获取 Scenario 列表
     *
     * @param context
     * @return
     */
    public static ArrayList<ScenarioLoop> getActionScenarios(Context context) {
        return ScenarioController.getScenarioList(context);
    }

    /**
     * 获取 Schedule 设备列表
     *
     * @param context
     * @return
     */
    public static ArrayList<MenuScheduleDeviceObject> getScheduleDeviceList(Context context) {
        return DeviceManager.getDeviceList(context, false);
    }


    /**
     * 获取 Schedule device 对应的 Device 对象
     *
     * @param context
     * @param info
     * @return
     * @throws JSONException
     */
    private static Object getDeviceActionBodyWith(Context context, ScheduleDeviceInfo info) throws JSONException {
        if (info == null)
            return null;
        String actionInfo = info.mActionInfo;
        JSONObject actionJson = new JSONObject(actionInfo);
        if (info.mModuleType == ModelEnum.MODULE_TYPE_SPARKLIGHTING) {
            //Spark lighting
            SparkLightingLoop loop = new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getSparkLightingLoopByPrimaryId(info.mLoopPrimaryId);
            loop.customStatus.status = "on".equalsIgnoreCase(actionJson.optString("status")) || "opening".equalsIgnoreCase(actionJson.optString("status"));
            loop.customStatus.openClosePercent = actionJson.optInt("openclosepercent");
            return loop;
        } else if (info.mModuleType == ModelEnum.MODULE_TYPE_WIFIRELAY) {
            RelayLoop loop = new RelayLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getRelayLoopByPrimaryId(info.mLoopPrimaryId);
            loop.customStatus.status = "on".equalsIgnoreCase(actionJson.optString("status"));
            loop.mTriggerTime = actionJson.optInt("time");
            return loop;
        } else if (info.mModuleType == ModelEnum.MODULE_TYPE_BACKAUDIO) {
            BackaudioLoop loop = new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioLoopByPrimaryId(info.mLoopPrimaryId);
            //control map
            JSONArray controlMap = actionJson.getJSONArray("controlmap");

            //Value
            for (int i = 0; i < controlMap.length(); i++) {
                JSONObject control = controlMap.optJSONObject(i);
                if ("power".equalsIgnoreCase(control.optString("keytype"))) {
                    loop.customModel.power = control.optString("keyvalue");
                } else if ("playstatus".equalsIgnoreCase(control.optString("keytype"))) {
                    loop.customModel.playstatus = control.optString("keyvalue");
                } else if ("volume".equalsIgnoreCase(control.optString("keytype"))) {
                    loop.customModel.volume = control.optInt("keyvalue");
                }
            }
            return loop;
        } else if (info.mModuleType == ModelEnum.MODULE_TYPE_WIFI315M433M) {
            Wireless315M433MLoop loop = new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWireless315M433MLoopByPrimaryId(info.mLoopPrimaryId);
            //value
            loop.customStatus.status = "on".equalsIgnoreCase(actionJson.optString("status")) || "opening".equalsIgnoreCase(actionJson.optString("status"));
            loop.customStatus.openClosePercent = actionJson.optInt("openclosepercent");
            return loop;
        } else if (info.mModuleType == ModelEnum.MODULE_TYPE_BACNET) {
            BacnetLoop loop = new BacnetLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBacnetLoopByPrimaryId(info.mLoopPrimaryId);
            //value
            loop.ac_customModel.status = "on".equalsIgnoreCase(actionJson.optString("status")) ? true : false;
            loop.ac_customModel.mode = actionJson.optString("mode");
            loop.ac_customModel.fan_speed = actionJson.optString("fanspeed");
            loop.ac_customModel.set_temp = actionJson.optInt("settemp");
            return loop;
        } else if (info.mModuleType == ModelEnum.MODULE_TYPE_WIFI485) {
            Wifi485Loop loop = new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWifi485LoopByPrimaryId(info.mLoopPrimaryId);
            //value
            loop.customModel.status = "on".equalsIgnoreCase(actionJson.optString("status")) ? true : false;
            loop.customModel.mode = actionJson.optString("mode");
            loop.customModel.fan_speed = actionJson.optString("fanspeed");
            loop.customModel.set_temp = actionJson.optInt("settemp");
            return loop;
        } else if (info.mModuleType == ModelEnum.MODULE_TYPE_WIFIIR) {
            IrLoop loop = new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrLoopByPrimaryId(info.mLoopPrimaryId);

            //Codes
            if (loop.customModel.scenarioCodes != null && loop.customModel.scenarioCodes.size() != 0) {

            }

            //每次遍历场景中的IRCode一遍
            ArrayList<IrCode> IrcodeArr = (ArrayList<IrCode>) new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrCodeByLoopId((int) loop.mLoopSelfPrimaryId);

            for (int i = 0; i < IrcodeArr.size(); i++) {
                IrCode code = IrcodeArr.get(i);
                //value
                IrScenarioCodeCustom scenarioCodeCustom = new IrScenarioCodeCustom();
                scenarioCodeCustom.timer = actionJson.optString("time");
                scenarioCodeCustom.code = code;
                loop.customModel.scenarioCodes.add(scenarioCodeCustom);
            }
            return loop;
        }
        return null;
    }


    /***
     * 处理返回的结果
     *
     * @param context
     * @param object
     */
    public static void handleScheduleResponce(Context context, JSONObject object) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(object);
        String configType = object.optString("configtype");
        if (errorCode != 0) {
            Loger.print(TAG, "ssd handle schedule have failed ", Thread.currentThread());
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE_DELETE, null, true));
            } else {
                EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE, MessageErrorCode.transferErrorCode(context, errorCode), false));
            }
            return;
        }
        ScheduleRuleFunc func = new ScheduleRuleFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configType)) {
            //增加
            ScheduleRuleInfo info = new ScheduleRuleInfo();
            info.mPrimaryId = object.optInt("responseprimaryid");
            info.mSwitchStatus = object.optString("status");
            info.mAvaibleTime = object.optString("availabletime");
            info.mName = object.optString("aliasname");
            info.mDescription = object.optString("description");
            func.addScheduleRuleInfo(info);

            //Schedule action
            JSONArray array = object.optJSONArray("scheduleaction");
            if (array == null || array.length() == 0) {
                Loger.print(TAG, "ssd handle schedule schedule action is null", Thread.currentThread());
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject scheduleactionDic = (JSONObject) array.opt(i);
                if (scheduleactionDic == null) {
                    Loger.print(TAG, "ssd handle schedule schedule action is null 02", Thread.currentThread());
                }
                String sendType = scheduleactionDic.optString("type");

                if ("scenariocontrol".equalsIgnoreCase(sendType)) {
                    ScheduleScenarioInfo scheduleScenarioInfo = new ScheduleScenarioInfo();
                    scheduleScenarioInfo.mPrimaryId = scheduleactionDic.optInt("responseprimaryid");
                    scheduleScenarioInfo.mTriggerOrRuleId = object.optInt("responseprimaryid");
                    scheduleScenarioInfo.mActionInfo = scheduleactionDic.optString("actioninfo");
                    new ScheduleScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context)).addScheduleScenario(scheduleScenarioInfo);
                } else if ("devicecontrol".equalsIgnoreCase(sendType)) {
                    JSONArray deviceLoopMap = scheduleactionDic.optJSONArray("deviceloopmap");
                    if (deviceLoopMap == null || deviceLoopMap.length() == 0) {
                        Loger.print(TAG, "ssd device loop map size is 0", Thread.currentThread());
                    }
                    JSONObject deviceLoopDic = (JSONObject) deviceLoopMap.opt(deviceLoopMap.length() - 1);

                    ScheduleDeviceInfo deviceInfo = new ScheduleDeviceInfo();
                    deviceInfo.mPrimaryId = deviceLoopDic.optInt("responseprimaryid");
                    deviceInfo.mTriggerOrRuleId = object.optInt("responseprimaryid");
                    deviceInfo.mActionInfo = deviceLoopDic.optString("actioninfo");
                    deviceInfo.mLoopPrimaryId = deviceLoopDic.optInt("primaryid");
                    deviceInfo.mModuleType = DeviceManager.getModuleTypeIntFromString(deviceLoopDic.optString("moduletype"));
                    new ScheduleDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).addScheduleRuleDeviceControlInfo(deviceInfo);
                }
            }
        } else if ("delete".equalsIgnoreCase(configType)) {
            //删除
            int scheduleRule_id = object.optInt("primaryid");
            func.deleteScheduleRuleInfoByScheduleRuleId(scheduleRule_id);

            new ScheduleScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteScheduleScenarioControlInfoByRule(scheduleRule_id);
            new ScheduleDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteScheduleRuleDeviceControlInfoByRule(scheduleRule_id);
            EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE_DELETE, null, true));
            return;
        } else if ("modify".equalsIgnoreCase(configType)) {
            //编辑
            int scheduleRule_id = object.optInt("primaryid");

            ScheduleRuleInfo info = new ScheduleRuleInfo();
            info.mPrimaryId = scheduleRule_id;
            info.mSwitchStatus = object.optString("status");
            info.mAvaibleTime = object.optString("availabletime");
            info.mName = object.optString("aliasname");
            info.mDescription = object.optString("description");
            func.updateScheduleRuleInfo(scheduleRule_id, info);

            //Schedule action
            JSONArray scheduleAction = object.optJSONArray("scheduleaction");
            if (scheduleAction == null || scheduleAction.length() == 0) {
                if (isCheckEnable) {
                    isCheckEnable = false;
                    EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.ENABLE_SCHEDULE, context.getString(R.string.operation_success_tip), true));
                } else {
                    EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE, context.getString(R.string.operation_success_tip), true));
                }
                return;
            }
            for (int i = 0; i < scheduleAction.length(); i++) {
                JSONObject scheduleactionDic = scheduleAction.optJSONObject(i);
                String sendType = scheduleactionDic.optString("type");
                if ("scenariocontrol".equalsIgnoreCase(sendType)) {
                    String configtype = scheduleactionDic.optString("configtype");
                    ScheduleScenarioFunc func1 = new ScheduleScenarioFunc(ConfigCubeDatabaseHelper.getInstance(context));
                    if ("add".equalsIgnoreCase(configtype)) {
                        ScheduleScenarioInfo scheduleScenarioInfo = new ScheduleScenarioInfo();
                        scheduleScenarioInfo.mPrimaryId = scheduleactionDic.optInt("responseprimaryid");
                        scheduleScenarioInfo.mTriggerOrRuleId = scheduleRule_id;
                        scheduleScenarioInfo.mActionInfo = scheduleactionDic.optString("actioninfo");
                        func1.addScheduleScenario(scheduleScenarioInfo);
                    } else if ("edit".equalsIgnoreCase(configtype)) {
                        ScheduleScenarioInfo scheduleScenarioInfo = new ScheduleScenarioInfo();
                        scheduleScenarioInfo.mPrimaryId = scheduleactionDic.optInt("primaryid");
                        scheduleScenarioInfo.mActionInfo = scheduleactionDic.optString("actioninfo");
                        func1.updateScheduleScenarioInfo(scheduleScenarioInfo.mPrimaryId, scheduleScenarioInfo.mActionInfo, false);
                    } else if ("delete".equalsIgnoreCase(configtype)) {
                        int primaryId = scheduleactionDic.optInt("primaryid");
                        func1.deleteScheduleScenarioControlInfo(primaryId);
                    }
                } else if ("devicecontrol".equalsIgnoreCase(sendType)) {
                    JSONArray deviceloopmap = scheduleactionDic.optJSONArray("deviceloopmap");
                    if (deviceloopmap == null || deviceloopmap.length() == 0) {
                        Loger.print(TAG, "ssd device loop map is null", Thread.currentThread());
                    }
                    for (int j = 0; j < deviceloopmap.length(); j++) {
                        JSONObject deviceLoopDic = deviceloopmap.optJSONObject(j);
                        String configtype = deviceLoopDic.optString("configtype");
                        ScheduleDeviceFunc func1 = new ScheduleDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
                        if ("add".equalsIgnoreCase(configtype)) {
                            ScheduleDeviceInfo info1 = new ScheduleDeviceInfo();
                            info1.mPrimaryId = deviceLoopDic.optInt("responseprimaryid");
                            info1.mTriggerOrRuleId = scheduleRule_id;
                            info1.mActionInfo = deviceLoopDic.optString("actioninfo");
                            info1.mLoopPrimaryId = deviceLoopDic.optInt("primaryid");
                            info1.mModuleType = DeviceManager.getModuleTypeIntFromString(deviceLoopDic.optString("moduletype"));
                            func1.addScheduleRuleDeviceControlInfo(info1);
                        } else if ("edit".equalsIgnoreCase(configtype)) {
                            ScheduleDeviceInfo info1 = func1.getScheduleDeviceInfoByPrimaryId(deviceLoopDic.optInt("primaryid"));
                            info1.mActionInfo = deviceLoopDic.optString("actioninfo");
                            func1.updateScheduleDeviceInfo(info1, false);
                        } else if ("delete".equalsIgnoreCase(configtype)) {
                            func1.deleteScheduleRuleDeviceByPrimaryId(deviceLoopDic.optInt("primaryid"));
                        }
                    }
                }
            }
        }

        //发送通知 操作成功
        EventBus.getDefault().post(new CubeScheduleEvent(CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE, context.getString(R.string.operation_success_tip), true));
    }

}
