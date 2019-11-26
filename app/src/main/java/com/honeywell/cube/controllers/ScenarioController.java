package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.DeviceControllers.IRLoopController;
import com.honeywell.cube.controllers.UIItem.ScenarioDeviceIrUIItem;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCodeFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.IrScenarioCodeCustom;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoopFunc;
import com.honeywell.cube.R;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRuleEvent;
import com.honeywell.cube.utils.events.CubeScenarioEvent;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/3. 15:44
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这个handler负责处理Scenario里面的一些业务逻辑和工具方法，不负责Websocket数据的组织
 */
public class ScenarioController {

    private static final String TAG = ScenarioController.class.getSimpleName();

    /**
     * UI 编辑 第三级页面
     * 根据传入的Scenario Event返回页面需要展示的Device列表和Zone列表，loop不能为null,为null 则不返回数据
     * Event数据类型 Map<String, Arraylist> key:"device
     *
     * @param context
     * @param loop
     */
    public static void getScenarioDeviceAndZoneList(Context context, ScenarioLoop loop) {
        if (loop == null || loop.mScenarioId <= 0) {
            Loger.print(TAG, "ssd scenario loop is null", Thread.currentThread());
            Map<String, Object> map = new HashMap<>();

            map.put(CommonData.SCENARIO_EDIT_GET_DEVICES, null);
            map.put(CommonData.SCENARIO_EDIT_GET_ZONES, ScenarioController.getDefaultZoneList(context));

            //发送数据
            EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.EDIT_GET_DEVICE_ZONE_ARR, map));
            return;
        }
        //获取数据库场景
        ArrayList<ScenarioLoop> loops = (ArrayList<ScenarioLoop>) new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getScenarioLoopListByScenarioId(loop.mScenarioId);

        //将scenario 分割成 device 和 zone
        ArrayList<ScenarioLoop> deviceArr = new ArrayList<>();
        ArrayList<ScenarioLoop> zoneArr = new ArrayList<>();

        for (ScenarioLoop loop1 : loops) {
            if (loop1.mModuleType == ModelEnum.MODULE_TYPE_WIREDZONE) {
                zoneArr.add(loop1);
            } else {
                deviceArr.add(loop1);
            }
        }

        ArrayList<Object> zoneState = updateScenarioZoneState(context, zoneArr);
        ArrayList<Map<String, Object>> deviceState = updateScenarioDeviceState(context, deviceArr);
        //数据做转换
        ArrayList<UIItems> deviceReturn = new ArrayList<>();
        if (deviceState.size() > 0) {
            for (int i = 0; i < deviceState.size(); i++) {
                Map<String, Object> map01 = deviceState.get(i);
                String title = (String) map01.get("title");
                ArrayList<Object> map_array = (ArrayList<Object>) map01.get("map_array");
                UIItems items = new UIItems(ModelEnum.UI_TYPE_TITLE, title, title, "", false);
                deviceReturn.add(items);
                for (int j = 0; j < map_array.size(); j++) {
                    Object object = map_array.get(j);
                    UIItems items1 = null;
                    if (object instanceof SparkLightingLoop) {
                        items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.SPARKLIGHTING, false);
                    } else if (object instanceof RelayLoop) {
                        items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_RELAY, false);
                    } else if (object instanceof BackaudioLoop) {
                        items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_BACKAUDIO, false);
                    } else if (object instanceof Wireless315M433MLoop) {
                        items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.WIRELESS_315_433, false);
                    } else if (object instanceof BacnetLoop) {
                        items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_BACNET, false);
                    } else if (object instanceof IrLoop) {
                        items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_IR, false);
                    }
                    if (items1 != null) {
                        deviceReturn.add(items1);
                    }
                }
            }
        }
        ArrayList<UIItems> zoneReturn = new ArrayList<>();
        if (zoneState.size() > 0) {
            UIItems uiItem = new UIItems(ModelEnum.UI_TYPE_LIST, "", ModelEnum.LOOP_ZONE, "", false);
            zoneReturn.add(uiItem);
            for (int i = 0; i < zoneState.size(); i++) {
                Object zoneObjcet = zoneState.get(i);
                UIItems uiItem1 = null;
                if (zoneObjcet instanceof WiredZoneLoop) {
                    WiredZoneLoop loop2 = (WiredZoneLoop) zoneObjcet;
                    uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.LOOP_ZONE, ModelEnum.LOOP_ZONE, loop2.customZoneStatus.is_arm);
                } else if (zoneObjcet instanceof IpvdpZoneLoop) {
                    IpvdpZoneLoop loop2 = (IpvdpZoneLoop) zoneObjcet;
                    uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.LOOP_IPVDP, ModelEnum.LOOP_IPVDP, loop2.customZoneStatus.is_arm);
                } else if (zoneObjcet instanceof SparkLightingLoop) {
                    SparkLightingLoop loop2 = (SparkLightingLoop) zoneObjcet;
                    uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.SPARKLIGHTING, ModelEnum.SPARKLIGHTING, loop2.customStatus.is_arm);
                } else if (zoneObjcet instanceof Wireless315M433MLoop) {
                    Wireless315M433MLoop loop2 = (Wireless315M433MLoop) zoneObjcet;
                    uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.WIRELESS_315_433, ModelEnum.WIRELESS_315_433, loop2.customStatus.is_arm);
                }
                if (uiItem1 != null) {
                    zoneReturn.add(uiItem1);
                }
            }
        } else {
            //Zone列表为空
            zoneReturn = ScenarioController.getDefaultZoneList(context);
        }
        Map<String, Object> map = new HashMap<>();
        map.put(CommonData.SCENARIO_EDIT_GET_DEVICES, deviceReturn);
        map.put(CommonData.SCENARIO_EDIT_GET_ZONES, zoneReturn);
        //发送数据
        EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.EDIT_GET_DEVICE_ZONE_ARR, map));
    }


    /**
     * UI 编辑 第四级界面
     * 获取第四页面设备列表
     *
     * @param context
     */
    public static void getScenarioDeviceWithScenarioID(Context context) {
        ArrayList<UIItems> returnValue = new ArrayList<>();
        ArrayList<Map<String, Object>> items = DeviceManager.getAllScenarioDeviceArray(context);
        if (items.size() == 0) {
            //目前数据库并没有Scenario设备列表
            EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_DEVICE_LIST, new ArrayList<>()));
            return;
        }
        //数据转换
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            String title = (String) item.get("title");
            //过滤 Ip camera
            if (title.equalsIgnoreCase(ModelEnum.MAIN_IP_CAMERA) || title.equalsIgnoreCase(ModelEnum.MAIN_VENTILATION)) {
                continue;
            }
            ArrayList<Object> map_array = (ArrayList<Object>) item.get("map_array");
            UIItems uiItems = new UIItems(ModelEnum.UI_TYPE_TITLE, title, title, "", false);
            returnValue.add(uiItems);
            for (int j = 0; j < map_array.size(); j++) {
                Object object = map_array.get(j);
                UIItems items1 = null;
                if (object instanceof SparkLightingLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.SPARKLIGHTING, false);
                } else if (object instanceof RelayLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_RELAY, false);
                } else if (object instanceof BackaudioLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_BACKAUDIO, false);
                } else if (object instanceof Wireless315M433MLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.WIRELESS_315_433, false);
                } else if (object instanceof BacnetLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_BACNET, false);
                } else if (object instanceof IrLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_IR, false);
                } else if (object instanceof BacnetLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_BACNET, false);
                } else if (object instanceof Wifi485Loop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.WIFI_485, false);
                } else if (object instanceof VentilationLoop) {
                    items1 = new UIItems(ModelEnum.UI_TYPE_OTHER, object, title, ModelEnum.LOOP_VENTILATION, false);
                }
                if (items1 != null) {
                    returnValue.add(items1);
                }
            }
        }
        EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_DEVICE_LIST, returnValue));
    }

    /**
     * 获取数据库中的scenario list  这个接口后期废弃掉,后期会使用getScenarioListFromDataBase
     *
     * @param context
     * @return
     */
    public static ArrayList<ScenarioLoop> getScenarioList(Context context) {
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)) {
            return getBasicScenarioList(context);
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                return getBasicScenarioList(context);
            }
        }
        ScenarioLoopFunc func = new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<ScenarioLoop> scenarioLoops = (ArrayList<ScenarioLoop>) func.getScenarioLoopAllList();
        scenarioLoops = DeviceManager.filterArray(scenarioLoops);

        for (int i = 0; i < scenarioLoops.size(); i++) {
            ScenarioLoop loop = scenarioLoops.get(i);
            if (loop.mScenarioId == 1) {
                loop.mImageName = "scenario_home";
                loop.mScenarioName = context.getString(R.string.scenario_home);
            } else if (loop.mScenarioId == 2) {
                loop.mImageName = "scenario_away";
                loop.mScenarioName = context.getString(R.string.scenario_away);
            } else if (loop.mScenarioId == 3) {
                loop.mImageName = "scenario_arm";
                loop.mScenarioName = context.getString(R.string.scenario_arm);
            } else if (loop.mScenarioId == 4) {
                loop.mImageName = "scenario_disarm";
                loop.mScenarioName = context.getString(R.string.scenario_disarm);
            }
        }
        return scenarioLoops;
    }

    /**
     * \获取 默认的数据
     *
     * @param context
     * @return
     */
    private static ArrayList<ScenarioLoop> getBasicScenarioList(Context context) {
        ArrayList<ScenarioLoop> scenarioLoops = new ArrayList<>();
        scenarioLoops.add(getBasicScenario(1, "scenario_home", context.getString(R.string.scenario_home)));
        scenarioLoops.add(getBasicScenario(2, "scenario_away", context.getString(R.string.scenario_away)));
        scenarioLoops.add(getBasicScenario(3, "scenario_arm", context.getString(R.string.scenario_arm)));
        scenarioLoops.add(getBasicScenario(4, "scenario_disarm", context.getString(R.string.scenario_disarm)));
        return scenarioLoops;
    }

    private static ScenarioLoop getBasicScenario(int id, String imageName, String scenarioName) {
        ScenarioLoop loop01 = new ScenarioLoop();
        loop01.mScenarioId = id;
        loop01.mImageName = imageName;
        loop01.mScenarioName = scenarioName;
        return loop01;
    }

    /**
     * 获取首页Scenario列表
     *
     * @param context
     * @return
     */
    public static ArrayList<ScenarioLoop> getHomeScenarioList(Context context) {
        if (!CommonUtils.isConnectNetwork(context)) {
            return getBasicScenarioList(context);
        }
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                return getBasicScenarioList(context);
            }
        }
        ScenarioLoopFunc func = new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<ScenarioLoop> scenarioLoops = (ArrayList<ScenarioLoop>) func.getScenarioLoopAllList();
        scenarioLoops = DeviceManager.filterArray(scenarioLoops);

        int scenarioId = ScenarioController.getCurrentScenarioID(context);
        for (int i = 0; i < scenarioLoops.size(); i++) {
            ScenarioLoop loop = scenarioLoops.get(i);
            if (loop.mScenarioId == 1) {
                loop.mImageName = "scenario_home";
                loop.mScenarioName = context.getString(R.string.scenario_home);
            } else if (loop.mScenarioId == 2) {
                loop.mImageName = "scenario_away";
                loop.mScenarioName = context.getString(R.string.scenario_away);
            } else if (loop.mScenarioId == 3) {
                loop.mImageName = "scenario_arm";
                loop.mScenarioName = context.getString(R.string.scenario_arm);
            } else if (loop.mScenarioId == 4) {
                loop.mImageName = "scenario_disarm";
                loop.mScenarioName = context.getString(R.string.scenario_disarm);
            }
        }
        for (int i = 0; i < scenarioLoops.size(); i++) {
            ScenarioLoop loop = scenarioLoops.get(i);
            for (int j = i; j < scenarioLoops.size(); j++) {
                if (scenarioLoops.get(j).mClickedCount > loop.mClickedCount) {
                    ScenarioLoop temp = scenarioLoops.get(j);
                    scenarioLoops.set(i, temp);
                    scenarioLoops.set(j, loop);
                    loop = temp;
                }
            }
        }
        return scenarioLoops;
    }

    /**
     * 通过场景ID找到场景
     *
     * @param scenarioId
     */
    public static ScenarioLoop getScenarioFromScenarioID(Context context, int scenarioId) {
        if (scenarioId <= 0) {
            Loger.print(TAG, "ssd scenario id is " + scenarioId + " can not be used", Thread.currentThread());
            return null;
        }
        //默认场景
        if (scenarioId == 1) {
            ScenarioLoop loop = new ScenarioLoop();
            loop.mScenarioId = 1;
            loop.mImageName = "scenario_home";
            loop.mScenarioName = context.getString(R.string.scenario_home);
            return loop;
        } else if (scenarioId == 2) {
            ScenarioLoop loop = new ScenarioLoop();
            loop.mScenarioId = 2;
            loop.mImageName = "scenario_away";
            loop.mScenarioName = context.getString(R.string.scenario_away);
            return loop;
        } else if (scenarioId == 3) {
            ScenarioLoop loop = new ScenarioLoop();
            loop.mScenarioId = 3;
            loop.mImageName = "scenario_arm";
            loop.mScenarioName = context.getString(R.string.scenario_arm);
            return loop;
        } else if (scenarioId == 4) {
            ScenarioLoop loop = new ScenarioLoop();
            loop.mScenarioId = 4;
            loop.mImageName = "scenario_disarm";
            loop.mScenarioName = context.getString(R.string.scenario_disarm);
            return loop;
        } else {
            ArrayList<ScenarioLoop> loops = (ArrayList<ScenarioLoop>) new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getScenarioLoopListByScenarioId(scenarioId);
            if (loops.size() > 0) {
                return loops.get(loops.size() - 1);
            } else {
                return null;
            }
        }
    }

//    /**
//     * 获取数据库中的scenario list
//     *
//     * @param context
//     * @return
//     */
//    public static void getScenarioListFromDataBase(Context context) {
//        ScenarioLoopFunc func = new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
//        ArrayList<ScenarioLoop> scenarioLoops = (ArrayList<ScenarioLoop>) func.getScenarioLoopAllList();
//        scenarioLoops = DeviceManager.filterArray(scenarioLoops);
//
//        if (scenarioLoops == null) {
//            Loger.print(TAG, "ssd getScenarioList null", Thread.currentThread());
//            EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.GET_SCENARIO_LIST, null));
//            return;
//        }
//        for (int i = 0; i < scenarioLoops.size(); i++) {
//            ScenarioLoop loop = scenarioLoops.get(i);
//            if (loop.mScenarioId == 1) {
//                loop.mImageName = "scenario_home";
//                loop.mScenarioName = context.getString(R.string.scenario_home);
//            } else if (loop.mScenarioId == 2) {
//                loop.mImageName = "scenario_away";
//                loop.mScenarioName = context.getString(R.string.scenario_away);
//            } else if (loop.mScenarioId == 3) {
//                loop.mImageName = "scenario_arm";
//                loop.mScenarioName = context.getString(R.string.scenario_arm);
//            } else if (loop.mScenarioId == 4) {
//                loop.mImageName = "scenario_disarm";
//                loop.mScenarioName = context.getString(R.string.scenario_disarm);
//            }
//
//        }
//        EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.GET_SCENARIO_LIST, scenarioLoops));
//    }

    /**
     * 通过 Scenario id 获取 scenario 名字
     *
     * @param context
     * @param scenarioId
     * @return
     */
    public static String getScenarioNameFromScenarioId(Context context, int scenarioId) {
        if (scenarioId == 1) {
            return context.getString(R.string.scenario_home);
        } else if (scenarioId == 2) {
            return context.getString(R.string.scenario_away);
        } else if (scenarioId == 2) {
            return context.getString(R.string.scenario_arm);
        } else if (scenarioId == 2) {
            return context.getString(R.string.scenario_disarm);
        } else {
            ScenarioLoop loop = ScenarioController.getScenarioFromScenarioID(context, scenarioId);
            if (loop != null) {
                return loop.mScenarioName;
            } else {
                return "";
            }
        }
    }

    /**
     * 获取当前用户的Scenario id
     *
     * @param context
     * @return
     */
    public static int getCurrentScenarioID(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        return null == info ? -1 : info.current_scenario_id;
    }


    /******** scenario Ir custom 界面 **************/
    /**
     * 用于获取Scenario IR device 界面 数据
     *
     * @param context
     * @param loop
     * @return
     */
    public static ArrayList<ScenarioDeviceIrUIItem> getScenarioIRDeviceList(Context context, IrLoop loop) {
        ArrayList<ScenarioDeviceIrUIItem> list = new ArrayList<>();
        if (loop == null) {
            Loger.print(TAG, "ssd get scenario Ir device list loop is null", Thread.currentThread());
            return list;
        }
        ArrayList<IrScenarioCodeCustom> codesList = loop.customModel.scenarioCodes;
        if (codesList.size() == 0) {
            ScenarioDeviceIrUIItem uiItem = new ScenarioDeviceIrUIItem();
            uiItem.sectionTitle = context.getString(R.string.task) + " " + 1;
            list.add(uiItem);
            return list;
        } else {
            for (int i = 0; i < codesList.size(); i++) {
                IrScenarioCodeCustom codeCustom = codesList.get(i);
                IrCode code = codeCustom.code;

                MenuDeviceIRIconItem menuDeviceIRIconItem = new MenuDeviceIRIconItem();
                menuDeviceIRIconItem.IR_iconCode = code;
                menuDeviceIRIconItem.IR_icon_name = DeviceManager.getNameWithProtocol(code.mName);
                menuDeviceIRIconItem.IR_icon_imageName = code.mImageName;
                DeviceManager.transferIRIconImage(DeviceManager.getImageNameWithprotocol(code.mImageName), menuDeviceIRIconItem);
                menuDeviceIRIconItem.IR_icon_enable = true;

                ScenarioDeviceIrUIItem uiItem = new ScenarioDeviceIrUIItem();
                uiItem.iconItem = menuDeviceIRIconItem;
                uiItem.cellTitle = menuDeviceIRIconItem.IR_icon_name;
                list.add(uiItem);
            }
            return list;
        }
    }

    /**
     * 获取下方弹出窗 Menu icon 列表
     *
     * @param context
     * @param loop
     * @return
     */
    public static ArrayList<MenuDeviceIRIconItem> getScenarioIRLoopList(Context context, IrLoop loop) {
        ArrayList<MenuDeviceIRIconItem> list = IRLoopController.getCustomIrIcons(context, loop);
        return list;
    }

    /**
     * 添加一个IR任务
     *
     * @param context
     * @param lastOne
     * @return
     */
    public static ScenarioDeviceIrUIItem getScenarioIRNextItem(Context context, ScenarioDeviceIrUIItem lastOne) {
        ScenarioDeviceIrUIItem uiItem = new ScenarioDeviceIrUIItem();
        if (lastOne == null) {
            uiItem.sectionTitle = context.getString(R.string.task) + " " + 1;
        } else {
            String sectionTitle = lastOne.sectionTitle;
            String[] values = sectionTitle.split(" ");
            if (values.length != 2) {
                uiItem.sectionTitle = context.getString(R.string.task) + " " + 1;
            } else {
                int section = Integer.parseInt(values[1]);
                uiItem.sectionTitle = context.getString(R.string.task) + " " + (section + 1);
            }
        }
        return uiItem;
    }

    /**
     * 添加完对应的list
     *
     * @param loop
     * @param list
     * @return
     */
    public static IrLoop updateScenarioIrLoopList(IrLoop loop, ArrayList<ScenarioDeviceIrUIItem> list) {
        if (list == null) {
            return loop;
        }
        if (list.size() == 0) {
            loop.customModel.scenarioCodes.clear();
            return loop;
        }
        ArrayList<IrScenarioCodeCustom> scenarioCodeList = loop.customModel.scenarioCodes;
        scenarioCodeList.clear();
        for (ScenarioDeviceIrUIItem deviceIrUIItem : list) {
            IrScenarioCodeCustom codeCustom = new IrScenarioCodeCustom();
            if (deviceIrUIItem.iconItem != null) {
                codeCustom.code = deviceIrUIItem.iconItem.IR_iconCode;
                scenarioCodeList.add(codeCustom);
            }
        }
        return loop;
    }

    /**
     * 判断是否需要输入密码--返回false不需要输入密码 返回true需要输入密码
     *
     * @param scenarioId
     */
    public static boolean checkIfNeedPassWord(Context context, int scenarioId) {
        if (scenarioId < 4) return true;
        ArrayList<ScenarioLoop> scenarioLoops = (ArrayList<ScenarioLoop>) new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getScenarioLoopListByScenarioId(scenarioId);
        if (scenarioLoops.size() > 0) {
            for (ScenarioLoop loop : scenarioLoops) {
                Object body = DeviceManager.getDeviceFromModuleTypeWithTypeAndID(context, (int) loop.mModuleType, (int) loop.mDeviceLoopPrimaryId);
                if (body instanceof WiredZoneLoop) {
                    return true;
                } else if (body instanceof IpvdpZoneLoop) {
                    return true;
                } else if (body instanceof SparkLightingLoop) {
                    SparkLightingLoop loop1 = (SparkLightingLoop) body;
                    if (loop1.mLoopType == ModelEnum.LOOP_TYPE_SENSOR_INT) {
                        return true;
                    }
                } else if (body instanceof Wireless315M433MLoop) {
                    Wireless315M433MLoop loop1 = (Wireless315M433MLoop) body;
                    if (loop1.mLoopType == ModelEnum.LOOP_TYPE_5800PIRAP_INT || loop1.mLoopType == ModelEnum.LOOP_TYPE_5804EU_INT || loop1.mLoopType == ModelEnum.LOOP_TYPE_5816EU_INT) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 执行场景
     *
     * @param context
     * @param scenarioId
     * @param pwd---密码   scenario设置的 ，为null 则使用默认密码
     */
    public static void enableScenarioIdWithId(Context context, int scenarioId, String pwd) {
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.ENABLE_SCENARIO_SUCCESS, false, context.getString(R.string.error_time_out)));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                return;
            }
        }
        //更新用户数据库
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info != null) {
            info.current_scenario_id = scenarioId;
            new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
            String message = MessageManager.getInstance(context).enableScenarioWithId(scenarioId, pwd);
            //发送远端请求
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }

    }

    /**
     * 删除某一个Scenario
     *
     * @param context
     * @param scenarioId
     */
    public static void deleteScenarioWithId(Context context, int scenarioId) {
        String message = MessageManager.getInstance(context).deleteScenarioWithId(scenarioId);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 发送编辑或者添加Scenario的命令 传入参数为3级页面的Devices 和 zone
     *
     * @param context
     * @param loop
     * @param deviceArr
     * @param zoneArra
     */
    public static void addOrEditScenarioWithInfo(final Context context, final ScenarioLoop loop, ArrayList<UIItems> deviceArr, ArrayList<UIItems> zoneArra) {
        ArrayList<Object> deviceArray = new ArrayList<>();
        ArrayList<Object> zoneArray = new ArrayList<>();
        if (deviceArr != null && deviceArr.size() > 0) {
            for (int i = 0; i < deviceArr.size(); i++) {
                UIItems items = deviceArr.get(i);
                if (items.type == ModelEnum.UI_TYPE_OTHER) {
                    deviceArray.add(items.object);
                }
            }
        }
        if (zoneArra != null && zoneArra.size() > 0) {
            for (int i = 0; i < zoneArra.size(); i++) {
                UIItems items = zoneArra.get(i);
                if (items.type == ModelEnum.UI_TYPE_OTHER) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("object", items.object);
                    map.put("select", items.isSelcet);
                    zoneArray.add(map);
                }
            }
        }
        ArrayList<Map<String, Object>> scenarioDeviceLoopMap = ScenarioController.getScenarioDevicesLoopmapWithArray(context, deviceArray, false);
        //zone
        if (zoneArray.size() > 0) {
            ArrayList<Map<String, Object>> scenarioZoneLoopMap = ScenarioController.getScenarioDevicesLoopmapWithArray(context, zoneArray, true);
            scenarioDeviceLoopMap.addAll(scenarioZoneLoopMap);
        }
        //不能添加一个空场景
        if (scenarioDeviceLoopMap.size() == 0) {
            //发送通知 给出提示
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.NORMAL_MSG_EVENT, false, context.getString(R.string.scenario_add_not_empty)));
            return;
        }
        boolean isEdit = false;
        //判断是否是新增 还是 编辑
        if (loop.mScenarioId > 0) {
            //编辑
            isEdit = true;
            //更新本地数据库
            ScenarioController.updateScenarioLoopMap(context, loop, scenarioDeviceLoopMap);
        } else {
            //新增
            isEdit = false;
        }
        String message = MessageManager.getInstance(context).editOrAddScenario(isEdit, loop, scenarioDeviceLoopMap);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /****************************************Repsonce********************************/

    /**
     * 解析Responce返回的结果 configdevice
     *
     * @param body
     */
    public static void handleResponceScenarioWithBody(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_SCENARIO_STATE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }

        try {
            String configType = body.optString("configtype");
            int scenarioId = body.optInt("scenarioid");
            if (configType.equals("add")) {
                JSONArray array = body.optJSONArray("scenarioloopmap");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.opt(i);
                    //add
                    ScenarioLoop loop = new ScenarioLoop();
                    loop.mScenarioLoopPrimaryId = object.optInt("responseprimaryid");
                    loop.mScenarioId = scenarioId;
                    loop.mScenarioName = body.optString("aliasname");
                    loop.mModuleType = DeviceManager.getModuleTypeIntFromString(object.optString("moduletype"));
                    loop.mDeviceLoopPrimaryId = object.optInt("primaryid");
                    loop.mActionInfo = object.optString("actioninfo");
                    loop.mIsArm = object.optString("isarm").equals("arm") ? 1 : 0;
                    loop.mImageName = body.optString("imagename");
                    new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).addScenarioLoop(loop);
                }
                Loger.print(TAG, "ssd handleResponceScenarioWithBody add", Thread.currentThread());
                //界面消息响应
                EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_SCENARIO_STATE, true, "操作成功"));

            } else if (configType.equals("delete")) {
                new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteScenarioLoopByScenarioId(scenarioId);
                //更新Current scenario id
                AppInfo info = AppInfoFunc.getCurrentUser(context);
                if (info.current_scenario_id == scenarioId) {
                    info.current_scenario_id = ScenarioController.getScenarioList(context).get(0).mScenarioId;
                }
                //界面消息响应
                Loger.print(TAG, "ssd handleResponceScenarioWithBody delete", Thread.currentThread());
                EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_SCENARIO_STATE, true, "操作成功"));

            } else if (configType.equals("modify")) {
                JSONArray array = body.getJSONArray("scenarioloopmap");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.opt(i);
                    String configtype = object.optString("configtype");
                    if (configtype.equals("add")) {
                        ScenarioLoop loop = new ScenarioLoop();
                        loop.mScenarioLoopPrimaryId = object.optInt("responseprimaryid");
                        loop.mScenarioId = scenarioId;
                        loop.mScenarioName = body.optString("aliasname");
                        loop.mModuleType = DeviceManager.getModuleTypeIntFromString(object.optString("moduletype"));
                        loop.mDeviceLoopPrimaryId = object.optInt("primaryid");
                        loop.mActionInfo = object.optString("actioninfo");
                        loop.mIsArm = object.optString("isarm").equals("arm") ? 1 : 0;
                        loop.mImageName = body.optString("imagename");
                        new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).addScenarioLoop(loop);
                    } else if (configtype.equals("modify")) {
                        ScenarioLoop loop = new ScenarioLoop();
                        loop.mScenarioLoopPrimaryId = object.optInt("responseprimaryid");
                        loop.mScenarioId = scenarioId;
                        loop.mScenarioName = body.optString("aliasname");
                        loop.mModuleType = DeviceManager.getModuleTypeIntFromString(object.optString("moduletype"));
                        loop.mDeviceLoopPrimaryId = object.optInt("primaryid");
                        if (object.has("actioninfo")) {
                            JSONObject object1 = object.optJSONObject("actioninfo");
                            if (object1 != null) {
                                loop.mActionInfo = object1.toString();
                            } else {
                                loop.mActionInfo = "";
                            }
                        }
                        loop.mIsArm = object.optString("isarm").equals("arm") ? 1 : 0;
                        loop.mImageName = object.optString("imagename");
                        new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateScenarioLoop(loop, false);
                    } else if (configtype.equals("delete")) {
                        new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deletScenarioLoopByPrimaryId(object.optInt("responseprimaryid"));
                    }
                }
                //发送通知，更新界面信息
                EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_SCENARIO_STATE, true, "操作成功"));
            }
        } catch (JSONException e) {
            EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_SCENARIO_STATE, false, "操作失败"));
            e.printStackTrace();
        }

    }

    /**
     * 解析Responce返回的结果 setdevice
     *
     * @param context
     * @param body
     */
    public static void handleControlResponceScenarioWithBody(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.ENABLE_SCENARIO_SUCCESS, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }

        int scenarioId = body.optInt("scenarioid");
        //更新AppInfo
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        info.current_scenario_id = scenarioId;
        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);

        //更新 Scenario click count
        ScenarioLoopFunc loopFunc = new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<ScenarioLoop> loops = (ArrayList<ScenarioLoop>) loopFunc.getScenarioLoopListByScenarioId(scenarioId);
        if (loops.size() > 0) {
            int clickCount = loops.get(0).mClickedCount + 1;
            Loger.print(TAG, "ssd click scenario id : " + scenarioId + "  click count : " + clickCount, Thread.currentThread());
            loopFunc.updateScenarioClickCount(scenarioId, clickCount);
        }
        EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.ENABLE_SCENARIO_SUCCESS, true, "操作成功"));
    }

    /**
     * 处理Event事件中收到 system scenario state 事件
     * 接收到Security场景变更
     *
     * @param context
     * @param body
     */
    public static void handleEventSystemScenarioState(Context context, JSONObject body) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info != null) {
            info.current_security_status = body.optInt("armstatus");
        }
        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);

        //Home 页面 要刷新状态   页面刷新接口在HomeController.java文件中
        EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.UPDATE_SYSTEM_SCENARIO_STATUS, true, "更新system scenario status 状态"));

    }

    /**************************************** private method ***************************/

    /**
     * 获取默认的 Zone 列表
     *
     * @param context
     * @return
     */
    private static ArrayList<UIItems> getDefaultZoneList(Context context) {
        ArrayList<UIItems> zoneReturn = new ArrayList<>();
        ArrayList<Object> array = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, ModelEnum.MAIN_ZONE);
        UIItems uiItem = new UIItems(ModelEnum.UI_TYPE_NO_LIST, "", ModelEnum.LOOP_ZONE, "", false);
        zoneReturn.add(uiItem);
        for (int i = 0; i < array.size(); i++) {
            Object zoneObjcet = array.get(i);
            UIItems uiItem1 = null;
            if (zoneObjcet instanceof WiredZoneLoop) {
                WiredZoneLoop loop2 = (WiredZoneLoop) zoneObjcet;
                uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.LOOP_ZONE, ModelEnum.LOOP_ZONE, false);
            } else if (zoneObjcet instanceof IpvdpZoneLoop) {
                IpvdpZoneLoop loop2 = (IpvdpZoneLoop) zoneObjcet;
                uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.LOOP_IPVDP, ModelEnum.LOOP_IPVDP, false);
            } else if (zoneObjcet instanceof SparkLightingLoop) {
                SparkLightingLoop loop2 = (SparkLightingLoop) zoneObjcet;
                uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.SPARKLIGHTING, ModelEnum.SPARKLIGHTING, false);
            } else if (zoneObjcet instanceof Wireless315M433MLoop) {
                Wireless315M433MLoop loop2 = (Wireless315M433MLoop) zoneObjcet;
                uiItem1 = new UIItems(ModelEnum.UI_TYPE_OTHER, loop2, ModelEnum.WIRELESS_315_433, ModelEnum.WIRELESS_315_433, false);
            }
            if (uiItem1 != null) {
                zoneReturn.add(uiItem1);
            }
        }
        return zoneReturn;
    }

    /**
     * 根据Scenario更新Zone列表的状态
     *
     * @param zoneArr
     */
    private static ArrayList<Object> updateScenarioZoneState(Context context, ArrayList<ScenarioLoop> zoneArr) {
        ArrayList<Object> array = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, ModelEnum.MAIN_ZONE);
        if (array == null || array.size() == 0) {
            Loger.print(TAG, "ssd updateScenarioZoneState array nil", Thread.currentThread());
            return new ArrayList<>();
        }
        if (zoneArr == null || zoneArr.size() == 0) {
            Loger.print(TAG, "ssd updateScenarioZoneState zoneArray nil", Thread.currentThread());
            return new ArrayList<>();
        }

        for (ScenarioLoop scenario : zoneArr) {
            for (Object body : array) {
                BasicLoop loop1 = (BasicLoop) body;
                long dev_id = loop1.mModulePrimaryId;
                long id = loop1.mLoopSelfPrimaryId;

                //Periphera
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(dev_id);
                if (device == null) {
                    Loger.print(TAG, "ssd updateScenarioZoneState PeripheralDevice nil", Thread.currentThread());
                    break;
                }
                if (device.mType == scenario.mModuleType && id == scenario.mDeviceLoopPrimaryId) {
                    if (body instanceof WiredZoneLoop) {
                        WiredZoneLoop loop2 = (WiredZoneLoop) body;
                        loop2.customZoneStatus.is_arm = scenario.mIsArm == CommonData.ARM_TYPE_DISABLE ? false : true;
                    } else if (body instanceof IpvdpZoneLoop) {
                        IpvdpZoneLoop loop2 = (IpvdpZoneLoop) body;
                        loop2.customZoneStatus.is_arm = scenario.mIsArm == CommonData.ARM_TYPE_DISABLE ? false : true;
                    } else if (body instanceof SparkLightingLoop) {
                        SparkLightingLoop loop2 = (SparkLightingLoop) body;
                        loop2.customStatus.is_arm = scenario.mIsArm == CommonData.ARM_TYPE_DISABLE ? false : true;
                    } else if (body instanceof Wireless315M433MLoop) {
                        Wireless315M433MLoop loop2 = (Wireless315M433MLoop) body;
                        loop2.customStatus.is_arm = scenario.mIsArm == CommonData.ARM_TYPE_DISABLE ? false : true;
                    }
                }
            }
        }
        return array;
    }

    /**
     * 更新Scenario 设备的状态
     *
     * @param context
     * @param deviceArr
     * @return
     */
    private static ArrayList<Map<String, Object>> updateScenarioDeviceState(Context context, ArrayList<ScenarioLoop> deviceArr) {
        ArrayList<Map<String, Object>> items = DeviceManager.getAllScenarioDeviceArray(context);
        ArrayList<Object> haveAddDeviceArray = new ArrayList<>();

        for (ScenarioLoop deviceScenario : deviceArr) {
            ScenarioController.updateScenarioDeviceTableData(deviceScenario, haveAddDeviceArray, items);
        }

        ScenarioController.removeNotAddToScenarioDevices(haveAddDeviceArray, items);
        return items;
    }

    private static void updateScenarioDeviceTableData(ScenarioLoop scenarioLoop, ArrayList<Object> haveAddDeviceArray, ArrayList<Map<String, Object>> items) {
        ArrayList<SparkLightingLoop> sparkLightingLoops = new ArrayList<>();
        ArrayList<RelayLoop> relayLoops = new ArrayList<>();
        ArrayList<BackaudioLoop> backaudioLoops = new ArrayList<>();
        ArrayList<Wireless315M433MLoop> wireless315M433MLoops = new ArrayList<>();
        ArrayList<BacnetLoop> bacnetLoops = new ArrayList<>();
        ArrayList<IrLoop> irLoops = new ArrayList<>();

        for (Map<String, Object> item : items) {
            ArrayList<Object> mainArray = (ArrayList<Object>) item.get("map_array");
            if (mainArray == null || mainArray.size() == 0) break;
            for (Object object : mainArray) {
                if (object instanceof SparkLightingLoop) {
                    sparkLightingLoops.add((SparkLightingLoop) object);
                } else if (object instanceof RelayLoop) {
                    relayLoops.add((RelayLoop) object);
                } else if (object instanceof BackaudioLoop) {
                    backaudioLoops.add((BackaudioLoop) object);
                } else if (object instanceof Wireless315M433MLoop) {
                    wireless315M433MLoops.add((Wireless315M433MLoop) object);
                } else if (object instanceof BacnetLoop) {
                    bacnetLoops.add((BacnetLoop) object);
                } else if (object instanceof IrLoop) {
                    irLoops.add((IrLoop) object);
                }
            }
        }
        if (sparkLightingLoops.size() > 0) {
            for (int i = 0; i < sparkLightingLoops.size(); i++) {
                SparkLightingLoop loop1 = sparkLightingLoops.get(i);
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(null)).getPeripheralDeviceByPrimaryId(loop1.mModulePrimaryId);
                if (device.mType == scenarioLoop.mModuleType && loop1.mLoopSelfPrimaryId == scenarioLoop.mDeviceLoopPrimaryId) {
                    try {
                        JSONObject actionInfo = new JSONObject(scenarioLoop.mActionInfo);
                        Loger.print(TAG, "ssd status SparkLightingLoop  openclosepercent " + actionInfo.optString("status") + " " + actionInfo.optInt("openclosepercent"), Thread.currentThread());
                        loop1.customStatus.status = DeviceManager.transferStatusFromStrToBool(actionInfo.optString("status"));
                        loop1.customStatus.openClosePercent = actionInfo.optInt("openclosepercent");
                        //已经添加到场景的设备
                        haveAddDeviceArray.add(loop1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (relayLoops.size() > 0) {
            for (int i = 0; i < relayLoops.size(); i++) {
                RelayLoop loop1 = relayLoops.get(i);
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(null)).getPeripheralDeviceByPrimaryId(loop1.mModulePrimaryId);
                if (device.mType == scenarioLoop.mModuleType && loop1.mLoopSelfPrimaryId == scenarioLoop.mDeviceLoopPrimaryId) {
                    try {
                        JSONObject actionInfo = new JSONObject(scenarioLoop.mActionInfo);
                        Loger.print(TAG, "ssd status RelayLoop  openclosepercent " + actionInfo.optString("status"), Thread.currentThread());

                        loop1.customStatus.status = DeviceManager.transferStatusFromStrToBool(actionInfo.optString("status"));
                        loop1.mTriggerTime = actionInfo.optInt("time");
                        //已经添加到场景的设备
                        haveAddDeviceArray.add(loop1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (backaudioLoops.size() > 0) {
            for (int i = 0; i < backaudioLoops.size(); i++) {
                BackaudioLoop loop1 = backaudioLoops.get(i);
                if (loop1.mLoopSelfPrimaryId == scenarioLoop.mDeviceLoopPrimaryId) {
                    try {
                        if (scenarioLoop.mActionInfo == null || scenarioLoop.mActionInfo.equalsIgnoreCase("")) {
                            //已经添加到场景的设备
//                            haveAddDeviceArray.add(loop1);
                        } else {
                            JSONObject actionInfo = new JSONObject(scenarioLoop.mActionInfo);

                            //control map
                            JSONArray controlMap = actionInfo.optJSONArray("controlmap");

                            if (controlMap == null) {
                                //已经添加到场景的设备
                            } else {
                                //value
                                for (int j = 0; j < controlMap.length(); j++) {
                                    JSONObject object = controlMap.optJSONObject(j);
                                    if ("power".equalsIgnoreCase(object.optString("keytype"))) {
                                        loop1.customModel.power = object.optString("keyvalue");
                                    }
                                    if ("playstatus".equalsIgnoreCase(object.optString("keytype"))) {
                                        loop1.customModel.playstatus = object.optString("keyvalue");
                                    }
                                    if ("volume".equalsIgnoreCase(object.optString("keytype"))) {
                                        loop1.customModel.volume = object.optInt("keyvalue");
                                    }
                                }
                            }
                            haveAddDeviceArray.add(loop1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        //315_433
        if (wireless315M433MLoops.size() > 0) {
            for (int i = 0; i < wireless315M433MLoops.size(); i++) {
                Wireless315M433MLoop loop1 = wireless315M433MLoops.get(i);
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(null)).getPeripheralDeviceByPrimaryId(loop1.mModulePrimaryId);
                if (loop1.mDeviceType.equalsIgnoreCase("maia2")) {
                    if (device.mType == scenarioLoop.mModuleType && loop1.mLoopSelfPrimaryId == scenarioLoop.mDeviceLoopPrimaryId) {
                        try {
                            JSONObject actionInfo = new JSONObject(scenarioLoop.mActionInfo);
                            Loger.print(TAG, "ssd status Wireless315M433MLoop  openclosepercent " + actionInfo.optString("status") + " " + actionInfo.optInt("openclosepercent"), Thread.currentThread());

                            loop1.customStatus.status = DeviceManager.transferStatusFromStrToBool(actionInfo.optString("status"));
                            loop1.customStatus.openClosePercent = actionInfo.optInt("openclosepercent");
                            //已经添加到场景的设备
                            haveAddDeviceArray.add(loop1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        //Bacnet
        if (bacnetLoops.size() > 0) {
            for (int i = 0; i < bacnetLoops.size(); i++) {
                BacnetLoop loop1 = bacnetLoops.get(i);
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(null)).getPeripheralDeviceByPrimaryId(loop1.mModulePrimaryId);
                if (device.mType == scenarioLoop.mModuleType && loop1.mLoopSelfPrimaryId == scenarioLoop.mDeviceLoopPrimaryId) {
                    try {
                        JSONObject actionInfo = new JSONObject(scenarioLoop.mActionInfo);
                        Loger.print(TAG, "ssd status BacnetLoop " + scenarioLoop.mActionInfo, Thread.currentThread());

                        loop1.ac_customModel.status = "on".equalsIgnoreCase(actionInfo.optString("status")) ? true : false;
                        loop1.ac_customModel.mode = actionInfo.optString("mode");
                        loop1.ac_customModel.fan_speed = actionInfo.optString("fan_speed");
                        loop1.ac_customModel.set_temp = actionInfo.optInt("set_temp");

                        //已经添加到场景的设备
                        haveAddDeviceArray.add(loop1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //Ir loop
        if (irLoops.size() > 0) {
            //ssd TO DO
            //Ir这部分需要梳理 等下次好了再写
            for (int i = 0; i < irLoops.size(); i++) {
                IrLoop loop1 = irLoops.get(i);
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(null)).getPeripheralDeviceByPrimaryId(loop1.mModulePrimaryId);
                if (device.mType != scenarioLoop.mModuleType) continue;

                ArrayList<IrCode> irCodes = (ArrayList<IrCode>) new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(null)).getIrCode(loop1.mLoopSelfPrimaryId, scenarioLoop.mDeviceLoopPrimaryId);

                if (irCodes.size() == 0) continue;

                for (IrCode code : irCodes) {
                    IrLoop loop = new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(null)).getIrLoopByPrimaryId(code.mLoopId);
                    if (loop1.mLoopSelfPrimaryId == loop.mLoopSelfPrimaryId) {
                        try {
                            JSONObject actionInfo = new JSONObject(scenarioLoop.mActionInfo);
                            IrScenarioCodeCustom scenarioCodeCustom = new IrScenarioCodeCustom();
                            scenarioCodeCustom.timer = actionInfo.optString("time");
                            scenarioCodeCustom.code = code;
                            loop1.customModel.scenarioCodes.add(scenarioCodeCustom);
                            //已经添加到场景的设备
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        haveAddDeviceArray.add(loop1);
                    }
                }
            }
        }

    }

    private static void removeNotAddToScenarioDevices(ArrayList<Object> haveAddDeviceArray, ArrayList<Map<String, Object>> items) {
        ArrayList<Object> deletedevices = new ArrayList<>();
        for (Map<String, Object> map : items) {
            ArrayList<Object> devices = (ArrayList<Object>) map.get("map_array");
            if (ScenarioController.checkIfNeedDevices(devices, haveAddDeviceArray)) {
                deletedevices.add(map);
            }
        }
        if (deletedevices.size() > 0) {
            for (Object object : deletedevices) {
                items.remove(object);
            }
        }
    }

    private static boolean checkIfNeedDevices(ArrayList<Object> devices, ArrayList<Object> checkArray) {
        //检查哪些需要删除
        ArrayList<Object> deleteDevices = new ArrayList<>();
        for (Object device : devices) {
            Boolean need = false;
            for (Object model : checkArray) {
                if (device.equals(model)) {
                    need = true;
                    break;
                }
            }
            if (!need) deleteDevices.add(device);
        }
        for (Object object : deleteDevices) {
            devices.remove(object);
        }
        return devices.size() == 0 ? true : false;
    }

    /**
     * 将编辑过的Scenario设备列表转换为LoopMap 用于发送
     */
    public static ArrayList<Map<String, Object>> getScenarioDevicesLoopmapWithArray(Context context, ArrayList<Object> deviceArr, boolean isZone) {
        if (deviceArr == null || deviceArr.size() == 0) {
            Loger.print(TAG, "ssd getScenarioDevicesLoopmapWithArray null", Thread.currentThread());
            return new ArrayList<>();
        }
        ArrayList<Map<String, Object>> returnValue = new ArrayList<>();
        //device array
        ArrayList<SparkLightingLoop> sparkLightingLoops = new ArrayList<>();
        ArrayList<RelayLoop> relayLoops = new ArrayList<>();
        ArrayList<BackaudioLoop> backaudioLoops = new ArrayList<>();
        ArrayList<Wireless315M433MLoop> wireless315M433MLoops = new ArrayList<>();
        ArrayList<BacnetLoop> bacnetLoops = new ArrayList<>();
        ArrayList<Wifi485Loop> AC485Arr = new ArrayList<>();
        ArrayList<IrLoop> irLoops = new ArrayList<>();
        ArrayList<WiredZoneLoop> wiredZoneLoops = new ArrayList<>();
        ArrayList<IpvdpZoneLoop> ipvdpZoneLoops = new ArrayList<>();
        ArrayList<IpcStreamInfo> ipcStreamInfos = new ArrayList<>();

        if (!isZone) {
            for (Object object : deviceArr) {
                if (object instanceof SparkLightingLoop) {
                    sparkLightingLoops.add((SparkLightingLoop) object);
                } else if (object instanceof RelayLoop) {
                    relayLoops.add((RelayLoop) object);
                } else if (object instanceof BackaudioLoop) {
                    backaudioLoops.add((BackaudioLoop) object);
                } else if (object instanceof Wireless315M433MLoop) {
                    wireless315M433MLoops.add((Wireless315M433MLoop) object);
                } else if (object instanceof BacnetLoop) {
                    bacnetLoops.add((BacnetLoop) object);
                } else if (object instanceof IrLoop) {
                    irLoops.add((IrLoop) object);
                } else if (object instanceof WiredZoneLoop) {
                    wiredZoneLoops.add((WiredZoneLoop) object);
                } else if (object instanceof IpvdpZoneLoop) {
                    ipvdpZoneLoops.add((IpvdpZoneLoop) object);
                } else if (object instanceof IpcStreamInfo) {
                    ipcStreamInfos.add((IpcStreamInfo) object);
                } else if (object instanceof Wifi485Loop) {
                    AC485Arr.add((Wifi485Loop) object);
                }
            }
        } else {
            for (Object obj : deviceArr) {
                Map map = (Map) obj;
                Object object = map.get("object");
                boolean select = (boolean) map.get("select");

                if (object instanceof SparkLightingLoop) {
                    SparkLightingLoop loop = (SparkLightingLoop) object;
                    loop.customStatus.is_arm = select;
                    sparkLightingLoops.add(loop);
                } else if (object instanceof RelayLoop) {
                    relayLoops.add((RelayLoop) object);
                } else if (object instanceof BackaudioLoop) {
                    backaudioLoops.add((BackaudioLoop) object);
                } else if (object instanceof Wireless315M433MLoop) {
                    Wireless315M433MLoop loops = (Wireless315M433MLoop) object;
                    loops.customStatus.is_arm = select;
                    wireless315M433MLoops.add(loops);
                } else if (object instanceof BacnetLoop) {
                    bacnetLoops.add((BacnetLoop) object);
                } else if (object instanceof IrLoop) {
                    irLoops.add((IrLoop) object);
                } else if (object instanceof WiredZoneLoop) {
                    WiredZoneLoop loop = (WiredZoneLoop) object;
                    loop.customZoneStatus.is_arm = select;
                    wiredZoneLoops.add(loop);
                } else if (object instanceof IpvdpZoneLoop) {
                    IpvdpZoneLoop loop = (IpvdpZoneLoop) object;
                    loop.customZoneStatus.is_arm = select;
                    ipvdpZoneLoops.add(loop);
                } else if (object instanceof IpcStreamInfo) {
                    ipcStreamInfos.add((IpcStreamInfo) object);
                }
            }
        }

        if (sparkLightingLoops.size() > 0) {
            for (SparkLightingLoop loop : sparkLightingLoops) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);
                if (device == null) {
                    Loger.print(TAG, "ssd getScenarioDevicesLoopmapWithArray PeripheralDevice null", Thread.currentThread());
                    continue;
                }
                //Spark Lighting zone
                if (loop.mLoopType == ModelEnum.LOOP_TYPE_SENSOR_INT) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("primaryid", loop.mLoopSelfPrimaryId);
                    body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    body.put("isarm", loop.customStatus.is_arm ? "arm" : "disarm");
                    returnValue.add(body);
                } else {
                    String status = DeviceManager.transferStatusFromBoolToStr(loop.mLoopType, loop.customStatus.status);
                    //action info
                    Map<String, Object> actionInfo = new HashMap<>();
                    actionInfo.put("status", status);
                    actionInfo.put("openclosepercent", loop.customStatus.openClosePercent);

                    //Body dic
                    Map<String, Object> body = new HashMap<>();
                    body.put("primaryid", loop.mLoopSelfPrimaryId);
                    body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    body.put("actioninfo", actionInfo);
                    returnValue.add(body);
                }
            }
        }
        //wifi relay
        if (relayLoops.size() > 0) {
            for (RelayLoop loop : relayLoops) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);
                if (device == null) {
                    Loger.print(TAG, "ssd getScenarioDevicesLoopmapWithArray PeripheralDevice null", Thread.currentThread());
                    continue;
                }

                String status = DeviceManager.transferStatusFromBoolToStr(loop.mLoopType, loop.customStatus.status);
                //action info
                Map<String, Object> actionInfo = new HashMap<>();
                actionInfo.put("status", status);
                actionInfo.put("time", loop.mTriggerTime);

                //Body dic
                Map<String, Object> body = new HashMap<>();
                body.put("primaryid", loop.mLoopSelfPrimaryId);
                body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                body.put("actioninfo", actionInfo);
                returnValue.add(body);
            }
        }
        if (backaudioLoops.size() > 0) {
            for (BackaudioLoop loop : backaudioLoops) {
                ArrayList<Map<String, Object>> controlmap = new ArrayList<>();
                if ("play".equalsIgnoreCase(loop.customModel.playstatus)) {
                    //开机
                    Map<String, Object> item01 = new HashMap<>();
                    item01.put("keytype", "power");
                    item01.put("keyvalue", "on");
                    controlmap.add(item01);

                    //播放
                    Map<String, Object> item02 = new HashMap<>();
                    item02.put("keytype", "playstatus");
                    item02.put("keyvalue", "play");
                    controlmap.add(item02);

                } else {
                    //暂停
                    Map<String, Object> item01 = new HashMap<>();
                    item01.put("keytype", "playstatus");
                    item01.put("keyvalue", "pause");
                    controlmap.add(item01);
                }

                Map<String, Object> item01 = new HashMap<>();
                item01.put("keytype", "volume");
                item01.put("keyvalue", loop.customModel.volume);
                controlmap.add(item01);


                //action info
                Map<String, Object> actionInfo = new HashMap<>();
                actionInfo.put("controlmap", controlmap);

                //Body dic
                Map<String, Object> body = new HashMap<>();
                body.put("primaryid", loop.mLoopSelfPrimaryId);
                body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(ModelEnum.BACKAUDIO_TYPE_INT));
                body.put("actioninfo", actionInfo);
                returnValue.add(body);
            }
        }
        if (wireless315M433MLoops.size() > 0) {
            for (Wireless315M433MLoop loop : wireless315M433MLoops) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);
                if ("maia2".equalsIgnoreCase(loop.mDeviceType)) {
                    String status = DeviceManager.transferStatusFromBoolToStr(loop.mLoopType, loop.customStatus.status);

                    //action info
                    Map<String, Object> actionInfo = new HashMap<>();
                    actionInfo.put("status", status);
                    actionInfo.put("openclosepercent", loop.customStatus.openClosePercent);

                    //Body dic
                    Map<String, Object> body = new HashMap<>();
                    body.put("primaryid", loop.mLoopSelfPrimaryId);
                    body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    body.put("actioninfo", actionInfo);
                    returnValue.add(body);
                }
                //433
                if ("sensor".equalsIgnoreCase(loop.mDeviceType)) {
                    //Body dic
                    Map<String, Object> body = new HashMap<>();
                    body.put("primaryid", loop.mLoopSelfPrimaryId);
                    body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    body.put("isarm", loop.customStatus.is_arm ? "arm" : "disarm");
                    returnValue.add(body);
                }
            }
        }
        if (bacnetLoops.size() > 0) {
            for (BacnetLoop loop : bacnetLoops) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

                //action info
                Map<String, Object> actionInfo = new HashMap<>();
                actionInfo.put("status", loop.ac_customModel.status ? "on" : "off");
                actionInfo.put("mode", "" + loop.ac_customModel.mode);
                actionInfo.put("fanspeed", loop.ac_customModel.fan_speed);
                actionInfo.put("settemp", loop.ac_customModel.set_temp);


                //Body dic
                Map<String, Object> body = new HashMap<>();
                body.put("primaryid", loop.mLoopSelfPrimaryId);
                body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                body.put("actioninfo", actionInfo);
                returnValue.add(body);
            }
        }
        if (AC485Arr.size() > 0) {
            for (Wifi485Loop loop : AC485Arr) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

                //action info
                Map<String, Object> actionInfo = new HashMap<>();
                actionInfo.put("status", loop.customModel.status ? "on" : "off");
                actionInfo.put("mode", "" + loop.customModel.mode);
                actionInfo.put("fanspeed", loop.customModel.fan_speed);
                actionInfo.put("settemp", loop.customModel.set_temp);

                //Body dic
                Map<String, Object> body = new HashMap<>();
                body.put("primaryid", loop.mLoopSelfPrimaryId);
                body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                body.put("actioninfo", actionInfo);
                returnValue.add(body);
            }
        }

        //Ir
        if (irLoops.size() > 0) {
            for (IrLoop loop : irLoops) {
                ArrayList<IrScenarioCodeCustom> scenarioCodes = loop.customModel.scenarioCodes;
                if (scenarioCodes.size() == 0) {
                    continue;
                }
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

                for (int i = 0; i < scenarioCodes.size(); i++) {
                    IrScenarioCodeCustom map = scenarioCodes.get(i);
                    IrCode code = map.code;

                    Map<String, Object> actinInfo = new HashMap<>();
                    actinInfo.put("time", map.timer);

                    //Body dic
                    Map<String, Object> body = new HashMap<>();
                    body.put("primaryid", code.mId);
                    body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    body.put("actioninfo", actinInfo);
                    returnValue.add(body);
                }

            }
        }
        if (wiredZoneLoops.size() > 0) {
            for (WiredZoneLoop loop : wiredZoneLoops) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);
                if (device != null) {
                    //Body dic
                    Map<String, Object> body = new HashMap<>();
                    body.put("primaryid", loop.mLoopSelfPrimaryId);
                    body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                    body.put("isarm", loop.customZoneStatus.is_arm ? "arm" : "disarm");
                    returnValue.add(body);
                }

            }
        }
        //IPVDP
        if (ipvdpZoneLoops.size() > 0) {
            for (IpvdpZoneLoop loop : ipvdpZoneLoops) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mModulePrimaryId);

                //Body dic
                Map<String, Object> body = new HashMap<>();
                body.put("primaryid", loop.mLoopSelfPrimaryId);
                body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                body.put("isarm", loop.customZoneStatus.is_arm ? "arm" : "disarm");
                returnValue.add(body);
            }
        }
        //IPC
        if (ipcStreamInfos.size() > 0) {
            for (IpcStreamInfo loop : ipcStreamInfos) {
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(loop.mDevId);

                //Body dic
                Map<String, Object> body = new HashMap<>();
                body.put("primaryid", loop.mId);
                body.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(device.mType));
                returnValue.add(body);
            }
        }
        return returnValue;
    }

    /**
     * 更新本地数据库的Scenario Loop
     *
     * @param loopmap
     */
    private static void updateScenarioLoopMap(Context context, ScenarioLoop loop, ArrayList<Map<String, Object>> loopmap) {
        //本地数据库场景
        ArrayList<ScenarioLoop> loops = (ArrayList<ScenarioLoop>) new ScenarioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getScenarioLoopListByScenarioId(loop.mScenarioId);
        //更新设备状态(add,edit)
        for (Map<String, Object> item : loopmap) {
            boolean haveAdd = ScenarioController.checkHaveAddDevice(context, item, loops);
            if (haveAdd) {
                item.put("configtype", "modify");
            } else {
                item.put("configtype", "add");
            }
        }
        //添加要删除的设备
        for (ScenarioLoop loop1 : loops) {
            if (ScenarioController.checkNotAddModel(context, loop1, loopmap)) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("moduletype", DeviceManager.getModuleTypeProtocolFromInt(loop1.mModuleType));
                map.put("primaryid", loop1.mDeviceLoopPrimaryId);
                map.put("configtype", "delete");
                loopmap.add(map);
            }
        }
    }

    //检查设备是否添加过
    private static boolean checkHaveAddDevice(Context context, Map<String, Object> checkDic, ArrayList<ScenarioLoop> loopmap) {
        String moduleType = (String) checkDic.get("moduletype");
        long primaryId = (long) checkDic.get("primaryid");
        for (ScenarioLoop item : loopmap) {
            if (DeviceManager.getModuleTypeIntFromString(moduleType) == item.mModuleType && primaryId == item.mDeviceLoopPrimaryId) {
                return true;
            }
        }
        return false;
    }

    //检查没有添加的设备
    private static boolean checkNotAddModel(Context context, ScenarioLoop loop, ArrayList<Map<String, Object>> loops) {
        if (loop.mModuleType == 0) return false;
        for (Map<String, Object> item : loops) {
            String moduleType = (String) item.get("moduletype");
            long primaryid = (long) item.get("primaryid");
            if (DeviceManager.getModuleTypeIntFromString(moduleType) == loop.mModuleType && primaryid == loop.mDeviceLoopPrimaryId) {
                return false;
            }
        }
        return true;
    }

}
