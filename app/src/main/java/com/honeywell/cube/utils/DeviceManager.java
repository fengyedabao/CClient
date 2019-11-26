package com.honeywell.cube.utils;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.UIItem.IPCameraListDetail;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
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
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.TriggerDeviceInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485LoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoopFunc;
import com.honeywell.cube.R;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.plist_parser.xml.plist.domain.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by H157925 on 16/5/2. 12:16
 * Email:Shodong.Sun@honeywell.com
 */
public class DeviceManager {
    private static final String TAG = "DeviceManager";

    /**
     * 查找IPC，根据带有录像联动的Zone
     *
     * @param context
     * @param zoneObj
     * @return
     */
    public static IpcStreamInfo getIPCFromVideoRecordZone(Context context, Object zoneObj) {
        if (zoneObj == null) return null;
        BasicLoop basicLoop = (BasicLoop) zoneObj;
        PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(basicLoop.mModulePrimaryId);

        if (device == null) return null;
        //所有的IPC
        ArrayList<TriggerDeviceInfo> deviceInfos = (ArrayList<TriggerDeviceInfo>) new TriggerDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getDeviceControlInfoListByModuleType(ModelEnum.MODULE_TYPE_IPC);

        if (deviceInfos.size() > 0) {
            for (TriggerDeviceInfo deviceInfo : deviceInfos) {
                ArrayList<ConditionInfo> infos = (ArrayList<ConditionInfo>) new ConditionFunc(ConfigCubeDatabaseHelper.getInstance(context)).getTriggerConditionInfoListByTriggerId(deviceInfo.mTriggerOrRuleId);
                if (infos.size() > 0) {
                    ConditionInfo info = infos.get(infos.size() - 1);
                    if (info.mModuleType == device.mType && info.mLoopPrimaryId == basicLoop.mLoopSelfPrimaryId) {
                        //找到并返回联动的IPC
                        return new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpcStreamInfoByPrimaryId(deviceInfo.mLoopPrimaryId);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取设备列表
     *
     * @param context
     * @return
     */
    public static ArrayList<MenuScheduleDeviceObject> getDeviceList(Context context, boolean isRule) {
        ArrayList<Map<String, Object>> items = DeviceManager.getAllScenarioDeviceArray(context);
        ArrayList<MenuScheduleDeviceObject> returnValue = new ArrayList<>();

        if (items.size() == 0) {
            //目前数据库并没有Scenario设备列表
            return returnValue;
        }
        //数据转换
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            String title = (String) item.get("title");
            ArrayList<Object> map_array = (ArrayList<Object>) item.get("map_array");
            if (!isRule) {
                if (ModelEnum.MAIN_LIGHT.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_CURTAIN.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_RELAY.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_AIR_CONDITION.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_BACKAUDIO.equalsIgnoreCase(title)) {
                    if (map_array.size() > 0) {
                        MenuScheduleDeviceObject titleobj = new MenuScheduleDeviceObject();
                        titleobj.type = ModelEnum.UI_TYPE_TITLE;
                        titleobj.section = title;
                        returnValue.add(titleobj);
                        for (int j = 0; j < map_array.size(); j++) {
                            Object object = map_array.get(j);
                            String looptype = "";
                            if (object instanceof SparkLightingLoop) {
                                looptype = ModelEnum.SPARKLIGHTING;
                            } else if (object instanceof RelayLoop) {
                                looptype = ModelEnum.LOOP_RELAY;
                            } else if (object instanceof BackaudioLoop) {
                                looptype = ModelEnum.LOOP_BACKAUDIO;
                            } else if (object instanceof Wireless315M433MLoop) {
                                looptype = ModelEnum.WIRELESS_315_433;
                            } else if (object instanceof BacnetLoop) {
                                looptype = ModelEnum.LOOP_BACNET;
                            } else if (object instanceof IrLoop) {
                                looptype = ModelEnum.LOOP_IR;
                            } else if (object instanceof Wifi485Loop) {
                                looptype = ModelEnum.WIFI_485;
                            }
                            MenuScheduleDeviceObject deviceDetail = new MenuScheduleDeviceObject();
                            deviceDetail.section = title;
                            deviceDetail.type = ModelEnum.UI_TYPE_OTHER;
                            deviceDetail.loop = object;
                            deviceDetail.loopType = looptype;
                            deviceDetail.title = DeviceManager.getDeviceTitleWithObject(context, object);
                            returnValue.add(deviceDetail);
                        }
                    }
                }
            } else {
                if (ModelEnum.MAIN_LIGHT.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_CURTAIN.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_RELAY.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_AIR_CONDITION.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_BACKAUDIO.equalsIgnoreCase(title) ||
                        ModelEnum.MAIN_IP_CAMERA.equalsIgnoreCase(title)) {
                    if (map_array.size() > 0) {
                        MenuScheduleDeviceObject titleobj = new MenuScheduleDeviceObject();
                        titleobj.type = ModelEnum.UI_TYPE_TITLE;
                        titleobj.section = title;
                        returnValue.add(titleobj);
                        for (int j = 0; j < map_array.size(); j++) {
                            Object object = map_array.get(j);
                            String looptype = "";
                            if (object instanceof SparkLightingLoop) {
                                looptype = ModelEnum.SPARKLIGHTING;
                            } else if (object instanceof RelayLoop) {
                                looptype = ModelEnum.LOOP_RELAY;
                            } else if (object instanceof BackaudioLoop) {
                                looptype = ModelEnum.LOOP_BACKAUDIO;
                            } else if (object instanceof Wireless315M433MLoop) {
                                looptype = ModelEnum.WIRELESS_315_433;
                            } else if (object instanceof BacnetLoop) {
                                looptype = ModelEnum.LOOP_BACNET;
                            } else if (object instanceof IrLoop) {
                                looptype = ModelEnum.LOOP_IR;
                            } else if (object instanceof Wifi485Loop) {
                                looptype = ModelEnum.WIFI_485;
                            } else if (object instanceof IpcStreamInfo) {
                                looptype = ModelEnum.LOOP_IPC;
                            }
                            MenuScheduleDeviceObject deviceDetail = new MenuScheduleDeviceObject();
                            deviceDetail.section = title;
                            deviceDetail.type = ModelEnum.UI_TYPE_OTHER;
                            deviceDetail.loop = object;
                            deviceDetail.loopType = looptype;
                            deviceDetail.title = DeviceManager.getDeviceTitleWithObject(context, object);
                            returnValue.add(deviceDetail);
                        }
                    }
                }
            }
        }
        return returnValue;
    }

    /**
     * 通过Module和厂商，获取空调配置信息
     *
     * @param context
     * @param module
     * @param branchname
     * @return
     */
    public static Map<String, PListObject> getAirConditionerConfigFromModule(Context context, String module, String branchname) {
        Map<String, PListObject> items = PlistUtil.parseDictPlistWithName("AirConditionerConfig.plist");
        Array subItem = (Array) items.get(module);
        if (branchname == null || "".equalsIgnoreCase(branchname)) {
            Loger.print(TAG, "ssd getAirConditionerConfigFromModule branchname is null", Thread.currentThread());
            return null;
        }
        if (subItem == null) {
            Loger.print(TAG, "ssd getAirConditionerConfigFromModule item is null", Thread.currentThread());
            return null;
        }

        for (int i = 0; i < subItem.size(); i++) {
            Dict dict = (Dict) subItem.get(i);
            Map<String, PListObject> detailItem = dict.configMap;
            com.honeywell.cube.utils.plist_parser.xml.plist.domain.String branch = (com.honeywell.cube.utils.plist_parser.xml.plist.domain.String) detailItem.get("brandname");
            String device = branch.getValue();
            if (branchname.equalsIgnoreCase(device)) {
                return detailItem;
            }
        }
        return null;
    }

    /**
     * 通过protocol（imagename) 获取对应的图片名称
     *
     * @param protocol
     * @return
     */
    public static String getImageNameWithprotocol(String protocol) {
        if (protocol == null || "".equalsIgnoreCase(protocol)) {
            return protocol;
        }
        if (protocol.startsWith(ModelEnum.DEVICE_IR_ADD_CUSTOMIZE)) {
            protocol = ModelEnum.DEVICE_IR_ADD_CUSTOMIZE;
        }
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("IRImageAndName.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            String imagename = item.get("imagename");
            if (protocol.equalsIgnoreCase(imagename)) {
                return item.get("image");
            }
        }
        return protocol;
    }

    /**
     * 通过protocol获取名称
     *
     * @param protocol
     * @return
     */
    public static String getNameWithProtocol(String protocol) {
        if (protocol == null || "".equalsIgnoreCase(protocol)) {
            return protocol;
        }
        if (protocol.startsWith(ModelEnum.DEVICE_IR_ADD_CUSTOMIZE)) {
            protocol = ModelEnum.DEVICE_IR_ADD_CUSTOMIZE;
        }
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("IRImageAndName.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            String imagename = item.get("imagename");
            if (protocol.equalsIgnoreCase(imagename)) {
                return item.get("name");
            }
        }
        return protocol;
    }


    /**
     * 转换 IR 部分名称
     *
     * @param context
     * @param name
     * @return
     */
    public static String transferIrName(Context context, String name) {
        if ("ir_customize".equalsIgnoreCase(name)) {
            return context.getString(R.string.device_type_ir_customize);
        } else if ("ir_ac".equalsIgnoreCase(name)) {
            return context.getString(R.string.device_type_ir_ac);
        } else if ("ir_dvd".equalsIgnoreCase(name)) {
            return context.getString(R.string.device_type_ir_dvd);
        } else if ("ir_television".equalsIgnoreCase(name)) {
            return context.getString(R.string.device_type_ir_television);
        } else if ("ir_stb".equalsIgnoreCase(name)) {
            return context.getString(R.string.device_type_ir_stb);
        }
        return "";
    }

    /**
     * 将 IR Image 名称转换为资源ID
     *
     * @param imageName
     * @return
     */
    public static int transferIrImagename(String imageName) {
        if ("ir_customize.png".equalsIgnoreCase(imageName)) {
            return R.mipmap.ir_customize;
        } else if ("ir_ac.png".equalsIgnoreCase(imageName)) {
            return R.mipmap.ir_ac;
        } else if ("ir_dvd.png".equalsIgnoreCase(imageName)) {
            return R.mipmap.ir_dvd;
        } else if ("ir_television.png".equalsIgnoreCase(imageName)) {
            return R.mipmap.ir_television;
        } else if ("ir_stb.png".equalsIgnoreCase(imageName)) {
            return R.mipmap.ir_stb;
        }
        return -1;
    }

    /**
     * 获取 key board image name
     *
     * @param imageName
     * @param uiitem
     * @return
     */
    public static void transferTVKeyboardImageName(String imageName, MenuDeviceIRIconItem uiitem) {
        if (uiitem == null) {
            return;
        }
        if ("ir_control_up.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_up_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_up_b;
        } else if ("ir_control_down.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_down_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_down_b;
        } else if ("ir_control_tap.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_tap_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_tap_b;
        } else if ("ir_control_left.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_left_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_left_b;
        } else if ("ir_control_right.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_right_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_right_b;
        }
    }


    public static void transferIRIconImage(String imageName, MenuDeviceIRIconItem uiitem) {
        if (uiitem == null) {
            return;
        }

        //空调界面 模式
        if (CommonData.MODE_TYPE_AUTO.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_mode_auto_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_mode_auto_focused_selected;
        } else if (CommonData.MODE_TYPE_COOL.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_mode_cool_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_mode_cool_focused_selected;
        } else if (CommonData.MODE_TYPE_HEAT.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_mode_heat_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_mode_heat_focused_selected;
        } else if (CommonData.MODE_TYPE_VENLITATION.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_mode_fun_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_mode_fun_focused_selected;
        } else if (CommonData.MODE_TYPE_DEHUMIDIFY.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_mode_dry_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_mode_dry_focused_selected;
        }
        //空调界面 风速
        else if (CommonData.AC_FAN_SPPED_LOW.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_fun_1_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_fun_1_focused_selected;
        } else if (CommonData.AC_FAN_SPPED_MIDDLE.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_fun_2_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_fun_2_focused_selected;
        } else if (CommonData.AC_FAN_SPPED_AUTO.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_fun_auto_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_fun_auto_focused_selected;
        } else if (CommonData.AC_FAN_SPPED_HIGH.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_fun_3_focused;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_fun_3_focused_selected;
        }
        //新风
        else if (ModelEnum.VENTILATION_DEHUMIDITY.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ventilation_dehumidity;
            uiitem.IR_icon_imageSelectId = R.mipmap.ventilation_dehumidity_s;
        } else if (ModelEnum.VENTILATION_HUMIDITY.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ventilation_humidity;
            uiitem.IR_icon_imageSelectId = R.mipmap.ventilation_humidity_s;
        }
        //其他界面
        else if ("ir_control_change.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_change_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_change_b;
        } else if ("ir_control_power.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ac_start_gray;
            uiitem.IR_icon_imageSelectId = R.mipmap.ac_start_r;
        } else if ("ir_control_mute.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_mute_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_mute_b;
        } else if ("ir_control_back.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_back_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_back_b;
        } else if ("ir_control_menu.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_menu_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_menu_b;
        } else if ("ir_control_volume_up.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_volume_up_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_volume_up_b;
        } else if ("ir_control_volume_down.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_volume_down_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_volume_down_b;
        } else if ("ir_control_up.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_up_c_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_up_c_b;
        } else if ("ir_control_down.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_down_c_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_down_c_b;
        } else if ("ir_control_ok.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_ok_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_ok_b;
        } else if ("ir_control_left.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_left_c_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_left_c_b;
        } else if ("ir_control_right.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_right_c_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_right_c_b;
        } else if ("ir_control_home.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_home_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_home_b;
        } else if ("ir_control_fast_reverse.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_fast_reverse_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_fast_reverse_b;
        } else if ("ir_control_play.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_play_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_play_b;
        } else if ("ir_control_fast_forward.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_fast_forward_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_fast_forward_b;
        } else if ("ir_control_previous.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_previous_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_previous_b;
        } else if ("ir_control_pause.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_pause_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_pause_b;
        } else if ("ir_control_next.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_next_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_next_b;
        } else if ("ir_control_stop.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_stop_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_stop_b;
        } else if ("ir_control_pause.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_pause_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_pause_b;
        } else if ("ir_control_dvd_out.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_dvd_out_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_dvd_out_b;
        } else if ("ir_control_1.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_1_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_1_b;
        } else if ("ir_control_2.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_2_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_2_b;
        } else if ("ir_control_3.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_3_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_3_b;
        } else if ("ir_control_4.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_4_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_4_b;
        } else if ("ir_control_5.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_5_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_5_b;
        } else if ("ir_control_6.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_6_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_6_b;
        } else if ("ir_control_7.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_7_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_7_b;
        } else if ("ir_control_8.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_8_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_8_b;
        } else if ("ir_control_9.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_9_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_9_b;
        } else if ("ir_control_0.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_0_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_0_b;
        } else if ("ir_control_customize_add.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_customize_add_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_customize_add_b;
        } else if (ModelEnum.DEVICE_IR_ADD_CUSTOMIZE.equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_customize_add_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_customize_add_b;
        } else if ("ir_control_customize.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_customize_add_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_customize_add_b;
        } else if ("ir_control_channel_down.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_channel_down_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_channel_down_b;
        } else if ("ir_control_channel_up.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_channel_up_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_channel_up_b;
        } else if ("ir_control_keypad.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_keypad_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_keypad_b;
        } else if ("ir_control_keypad01.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_keypad_b;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_touch_b;
        } else if ("ir_control_tap.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_tap_c_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_tap_c_b;
        } else if ("ir_control_touch.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_touch_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_touch_b;
        } else if ("ir_control_asterisk.png".equalsIgnoreCase(imageName)) {
            uiitem.IR_icon_imageId = R.mipmap.ir_control_asterisk_g;
            uiitem.IR_icon_imageSelectId = R.mipmap.ir_control_asterisk_b;
        }
    }

    /**
     * 将名称转换为协议
     *
     * @param name
     * @return
     */
    public static String getIrProtocolFromName(String name) {
        if (name == null || "".equalsIgnoreCase(name))
            return name;
        if (name.equalsIgnoreCase(ModelEnum.MAIN_IR_AC)) {
            return ModelEnum.IR_TYPE_AC_S;
        } else if (name.equalsIgnoreCase(ModelEnum.MAIN_IR_CUSTOMIZE)) {
            return ModelEnum.IR_TYPE_CUSTOMIZE_S;
        } else if (name.equalsIgnoreCase(ModelEnum.MAIN_IR_DVD)) {
            return ModelEnum.IR_TYPE_DVD_S;
        } else if (name.equalsIgnoreCase(ModelEnum.MAIN_IR_STB)) {
            return ModelEnum.IR_TYPE_STB_S;
        } else if (name.equalsIgnoreCase(ModelEnum.MAIN_IR_TELEVISION)) {
            return ModelEnum.IR_TYPE_TV_S;
        }
        return name;
    }

    /**
     * 名称转换为协议
     *
     * @param context
     * @param name
     * @return
     */
    public static String transferIrNameToProtocol(Context context, String name) {
        return "";
    }

    public static String transferVetilationNameFromProtocol(Context context, String protocol) {
        if (ModelEnum.VENTILATION_HUMIDITY.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.ventilation_humidity);
        } else if (ModelEnum.VENTILATION_DEHUMIDITY.equalsIgnoreCase(protocol)) {
            return context.getString(R.string.ventilation_dehumidity);
        }
        return protocol;
    }

    /**
     * 根据定义plist文件中 imagename 与 protocol的关系，返回 protocol
     *
     * @param imagename
     * @return
     */
    public static String get_IR_ProtocolImageNameWithImageName(String imagename) {
        if (imagename == null) {
            return imagename;
        }
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("IRImageAndName.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> map = (Map<String, String>) items.get(i);
            if (map != null) {
                String image = map.get("image");
                if (imagename.equalsIgnoreCase(image)) {
                    return map.get("imagename");
                }
            }
        }
        return imagename;
    }

    /**
     * 转换 status 状态
     *
     * @param status
     * @return
     */
    public static boolean transferStatusFromStrToBool(String status) {
        if ("on".equalsIgnoreCase(status) || "opening".equalsIgnoreCase(status)) {
            return true;
        } else return false;
//        return "on".equalsIgnoreCase(status) || "opening".equalsIgnoreCase(status);
    }

    /**
     * 转换 status 状态
     *
     * @param looptype
     * @param status
     * @return
     */
    public static String transferStatusFromBoolToStr(int looptype, boolean status) {
        if (looptype == ModelEnum.LOOP_TYPE_CURTAIN_INT) {
            return status ? "opening" : "closing";
        } else {
            return status ? "on" : "off";
        }
    }


    /**
     * 将图片的名称转换为资源ID，用于scenario
     *
     * @param imageName
     * @param selector
     * @return 0 失败
     */
    public static int transferImageStrToInt(String imageName, boolean selector) {
        if (imageName == null) {
            return 0;
        }
        if (imageName.equals("scenario_home")) {
            return selector ? R.mipmap.scenario_home_selected : R.mipmap.scenario_home;
        } else if (imageName.equals("scenario_away")) {
            return selector ? R.mipmap.scenario_away_selected : R.mipmap.scenario_away;
        } else if (imageName.equals("scenario_arm")) {
            return selector ? R.mipmap.scenario_arm_selected : R.mipmap.scenario_arm;
        } else if (imageName.equals("scenario_disarm")) {
            return selector ? R.mipmap.scenario_disarm_selected : R.mipmap.scenario_disarm;
        } else if (imageName.equals("scenario_entertainment")) {
            return selector ? R.mipmap.scenario_entertainment_selected : R.mipmap.scenario_entertainment;
        } else if (imageName.equals("scenario_music")) {
            return selector ? R.mipmap.scenario_music_selected : R.mipmap.scenario_music;
        } else if (imageName.equals("scenario_reading")) {
            return selector ? R.mipmap.scenario_reading_selected : R.mipmap.scenario_reading;
        } else if (imageName.equals("scenario_sleep")) {
            return selector ? R.mipmap.scenario_sleep_selected : R.mipmap.scenario_sleep;
        } else if (imageName.equals("scenario_star")) {
            return selector ? R.mipmap.scenario_star_selected : R.mipmap.scenario_star;
        } else if (imageName.equals("scenario_tea")) {
            return selector ? R.mipmap.scenario_tea_selected : R.mipmap.scenario_tea;
        } else if (imageName.equals("scenario_leave")) {
            return selector ? R.mipmap.scenario_away_selected : R.mipmap.scenario_away;
        } else if (imageName.equals("scenario_customize")) {
            return selector ? R.mipmap.scenario_customize_selected : R.mipmap.scenario_customize;
        } else if (imageName.equals("scenario_relax")) {
            return selector ? R.mipmap.scenario_relax_selected : R.mipmap.scenario_relax;
        } else if (imageName.equals("scenario_movie")) {
            return selector ? R.mipmap.scenario_movie_selected : R.mipmap.scenario_movie;
        } else if (imageName.equals("scenario_read")) {
            return selector ? R.mipmap.scenario_read_selected : R.mipmap.scenario_read;
        } else if (imageName.equals("scenario_read")) {
            return selector ? R.mipmap.scenario_read_selected : R.mipmap.scenario_read;
        } else if (imageName.equals("scenario_party")) {
            return selector ? R.mipmap.scenario_party_selected : R.mipmap.scenario_party;
        } else if (imageName.equals("scenario_party")) {
            return selector ? R.mipmap.scenario_party_selected : R.mipmap.scenario_party;
        } else if (imageName.equals("scenario_dinner")) {
            return selector ? R.mipmap.scenario_dinner_selected : R.mipmap.scenario_dinner;
        }
        return 0;
    }

    /**
     * 过滤json数组，将已经存在的对象去掉
     * 应用：scenarioloop
     *
     * @param array
     * @return
     */
    public static ArrayList<ScenarioLoop> filterArray(ArrayList<ScenarioLoop> array) {
        if (array.size() == 0 || array.size() == 1) {
            return null;
        }
        ArrayList<ScenarioLoop> returnValue = new ArrayList<ScenarioLoop>();
        try {
            returnValue.add(array.get(0));
            for (int i = 1; i < array.size(); i++) {
                ScenarioLoop object = array.get(i);
                boolean isExit = false;
                for (int j = 0; j < returnValue.size(); j++) {
                    ScenarioLoop object1 = returnValue.get(j);
                    if (object1.mScenarioId == object.mScenarioId) {
                        //表示存在
                        isExit = true;
                        break;
                    }
                }
                if (!isExit) {
                    returnValue.add(object);
                }
            }
        } catch (Exception e) {
            Loger.print(TAG, "ssd filterJsonArray error", Thread.currentThread());
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * 设备类型String转Int
     *
     * @param string
     * @return
     */
    public static int getDeviceTypeIntFromString(String string) {
        int type = 0;
        Map<String, PListObject> items = PlistUtil.parseDictPlistWithName("DeviceProtocolType.plist");
        for (String key : items.keySet()) {
            com.honeywell.cube.utils.plist_parser.xml.plist.domain.String DeviceType = (com.honeywell.cube.utils.plist_parser.xml.plist.domain.String) items.get(key);
            String device = DeviceType.getValue();
            if (device.equals(string)) {
                type = Integer.parseInt(key);
                break;
            }
        }
        return type;
    }

    public static String getDeviceTypeStringFromInt(int type) {
        Map<String, PListObject> items = PlistUtil.parseDictPlistWithName("DeviceProtocolType.plist");
        com.honeywell.cube.utils.plist_parser.xml.plist.domain.String DeviceType = (com.honeywell.cube.utils.plist_parser.xml.plist.domain.String) items.get("" + type);
        String device = DeviceType.getValue();
        return device;
    }

    /**
     * 获取curtain状态对应的字符串，2 = closing, 3 = stopped,其他的 ＝ opening
     *
     * @param type
     * @return
     */
    public static String getCurtainControlStatusWithType(int type) {
        String status = "opening";
        if (type == 2) {
            status = "closing";
        } else if (type == 3) {
            status = "stopped";
        }
        return status;
    }

    /**
     * Module类型String转Int
     *
     * @param str
     * @return
     */
    public static int getModuleTypeIntFromString(String str) {
        int type = 0;
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("ModuleType.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            if (str.equalsIgnoreCase(item.get("type_protocol"))) {
                type = Integer.parseInt(item.get("tpye_int"));
                break;
            }
        }
        return type;
    }

    /**
     * Module类型Int转String
     *
     * @param type
     * @return
     */
    public static String getModuleTypeProtocolFromInt(int type) {
        String typeStr = "";
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("ModuleType.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            int tpye_int = Integer.parseInt(item.get("tpye_int"));
            if (tpye_int == type) {
                typeStr = item.get("type_protocol");
                break;
            }
        }
        return typeStr;
    }

    /**
     * 设备 protocol国际化
     *
     * @param context
     * @param protocol
     * @return
     */
    public static String getModuleTypeStringFromProtocol(Context context, String protocol) {
        if (protocol == null) return "";
        if ("sparklighting".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_spark_lighting);
        } else if ("relay".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_wifi_relay);
        } else if ("wiredzone".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_wired_zone);
        } else if ("315M433M".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_315_433);
        } else if ("485".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_485_device);
        } else if ("ipvdp".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_ipvdp_zone);
        } else if ("ir".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_wifi_ir);
        } else if ("backaudio".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_backaudio);
        } else if ("bacnet".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_bacnet_ac);
        } else if ("ipc".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.menu_device_add_ip_camera);
        }
        return "";
    }


    /**
     * Cell中星期数组转词组(工作日、周末、每天)
     *
     * @param array
     * @return
     */
    public static String getCellWeekShortNameFromProtocol(Context context, JSONArray array) {
        if (array == null || array.length() == 0) {
            return context.getString(R.string.rule_repeat_type_never);
        }
        //每天
        else if (array.length() == 7) {
            return context.getString(R.string.rule_repeat_type_everyday);
        }
        //周末
        else if (array.length() == 2) {
            int check = 0;
            ArrayList<String> checkdays = new ArrayList<>();
            checkdays.add("saturday");
            checkdays.add("sunday");
            for (String day : checkdays) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject dayDic = array.optJSONObject(i);
                    if (dayDic.optString("day").equalsIgnoreCase(day)) {
                        check++;
                        break;
                    }
                }
            }

            if (check == 2) return context.getString(R.string.rule_repeat_type_weekend);
        }
        //工作日
        else if (array.length() == 5) {
            int check = 0;
            ArrayList<String> checkdays = new ArrayList<>();
            checkdays.add("monday");
            checkdays.add("tuesday");
            checkdays.add("wednesday");
            checkdays.add("thursday");
            checkdays.add("friday");
            for (String day : checkdays) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject dayDic = array.optJSONObject(i);
                    if (dayDic.optString("day").equalsIgnoreCase(day)) {
                        check++;
                        break;
                    }
                }
            }
            if (check == 5) return context.getString(R.string.rule_repeat_type_workday);
        }

        //其他
        String detailsDay = "";
        ArrayList<Object> weekArray = PlistUtil.parseArrayPlistWithName("WeekArray.plist");

        for (int i = 0; i < array.length(); i++) {
            JSONObject dayMap = (JSONObject) array.opt(i);
            for (int j = 0; j < weekArray.size(); j++) {
                Map<String, String> weekObj = (Map<String, String>) weekArray.get(j);
                if (weekObj.get("protocol").equalsIgnoreCase(dayMap.optString("day"))) {
                    if (detailsDay.equalsIgnoreCase("")) {
                        detailsDay = getWeekStrFromProtocol(context, weekObj.get("protocol"));
                    } else {
                        detailsDay = detailsDay + "," + getWeekStrFromProtocol(context, weekObj.get("protocol"));
                    }
                    break;
                }
            }
        }
        return detailsDay;
    }

    /**
     * 将 repeat
     *
     * @param context
     * @param cellweek
     * @return
     */
    public static ArrayList<Map> getCellWeekProtocolFromStr(Context context, String cellweek) {
        ArrayList<Map> returnValue = new ArrayList<>();
        //从不
        if (context.getString(R.string.rule_repeat_type_never).equalsIgnoreCase(cellweek)) {
            return returnValue;
        }
        //全部
        if (context.getString(R.string.rule_repeat_type_everyday).equalsIgnoreCase(cellweek)) {
            ArrayList<Object> weekArray = PlistUtil.parseArrayPlistWithName("WeekArray.plist");
            for (int i = 0; i < weekArray.size(); i++) {
                Map<String, String> weekObj = (Map<String, String>) weekArray.get(i);
                returnValue.add(transferMap(weekObj.get("protocol")));
            }
            return returnValue;
        }
        //周末
        if (context.getString(R.string.rule_repeat_type_weekend).equalsIgnoreCase(cellweek)) {
            returnValue.add(transferMap("saturday"));
            returnValue.add(transferMap("sunday"));
            return returnValue;
        }

        //工作日
        if (context.getString(R.string.rule_repeat_type_workday).equalsIgnoreCase(cellweek)) {
            returnValue.add(transferMap("monday"));
            returnValue.add(transferMap("tuesday"));
            returnValue.add(transferMap("wednesday"));
            returnValue.add(transferMap("thursday"));
            returnValue.add(transferMap("friday"));
            return returnValue;
        }

        //其他
        String[] splitArr = cellweek.split(",");
        for (int i = 0; i < splitArr.length; i++) {
            String body = splitArr[i];
            if (context.getString(R.string.rule_repeat_type_Monday).equalsIgnoreCase(body)) {
                returnValue.add(transferMap("monday"));
            } else if (context.getString(R.string.rule_repeat_type_Tuesday).equalsIgnoreCase(body)) {
                returnValue.add(transferMap("tuesday"));
            } else if (context.getString(R.string.rule_repeat_type_Wednesday).equalsIgnoreCase(body)) {
                returnValue.add(transferMap("wednesday"));
            } else if (context.getString(R.string.rule_repeat_type_Thursday).equalsIgnoreCase(body)) {
                returnValue.add(transferMap("thursday"));
            } else if (context.getString(R.string.rule_repeat_type_Friday).equalsIgnoreCase(body)) {
                returnValue.add(transferMap("friday"));
            } else if (context.getString(R.string.rule_repeat_type_Saturday).equalsIgnoreCase(body)) {
                returnValue.add(transferMap("saturday"));
            } else if (context.getString(R.string.rule_repeat_type_Sunday).equalsIgnoreCase(body)) {
                returnValue.add(transferMap("sunday"));
            }
        }
        return returnValue;
    }

    private static Map transferMap(String value) {
        Map<String, Object> map = new HashMap<>();
        map.put("day", value);
        return map;
    }


    /**
     * 将 Protocol 转换为 每周时间
     *
     * @param context
     * @param protocol
     * @return
     */
    public static String getWeekStrFromProtocol(Context context, String protocol) {
        if (protocol == null) {
            return "";
        }
        if ("monday".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.rule_repeat_type_Monday);
        } else if ("tuesday".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.rule_repeat_type_Tuesday);
        } else if ("wednesday".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.rule_repeat_type_Wednesday);
        } else if ("thursday".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.rule_repeat_type_Thursday);
        } else if ("friday".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.rule_repeat_type_Friday);
        } else if ("saturday".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.rule_repeat_type_Saturday);
        } else if ("sunday".equalsIgnoreCase(protocol)) {
            return context.getString(R.string.rule_repeat_type_Sunday);
        }
        return "";
    }

    /**
     * 通过解析DeviceCategory.plist来获取所有需要的设备，并进行字段组织
     *
     * @param context
     * @return
     */
    public static ArrayList<Map<String, Object>> getAllScenarioDeviceArray(Context context) {
        ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("DeviceCategory.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            String title = item.get("title");
            //添加数组（去除不需要的设备)
            if (!title.equals(ModelEnum.MAIN_ZONE)) {
                ArrayList<Object> deviceArr = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, title);
                if (deviceArr.size() > 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", title);
                    map.put("map_array", deviceArr);
                    arrayList.add(map);
                }
            }
        }
        return arrayList;
    }

    /*************************
     * SQLite 相关
     ***************************/

    /**
     * 获取 Relay 除去添加到新风的部分
     *
     * @param context
     * @return
     */
    public static ArrayList<RelayLoop> getRelayListWithOutVentilation(Context context) {
        ArrayList<RelayLoop> loops = (ArrayList<RelayLoop>) new RelayLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getRelayLoopAllList();
        ArrayList<RelayLoop> values = new ArrayList<>();
        ArrayList<VentilationLoop> ventilationLoops = new VentilationLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getVentilationLoopAllList();
        if (loops == null || loops.size() == 0) {
            return values;
        }
        if (ventilationLoops == null || ventilationLoops.size() == 0) {
            return loops;
        }
        for (RelayLoop loop : loops) {
            boolean insert = false;
            for (VentilationLoop ventilationLoop : ventilationLoops) {
                String speedStr = ventilationLoop.fanspeed;
                String powerStr = ventilationLoop.power;
                String cycleStr = ventilationLoop.cycletype;
                String humidityStr = ventilationLoop.humidity;
                String dehumidityStr = ventilationLoop.dehumidity;
                try {
                    JSONArray speedJson = new JSONArray(speedStr);
                    if (speedJson.length() != 3) continue;
                    JSONObject high = speedJson.optJSONObject(0);
                    if (loop.mLoopSelfPrimaryId == high.optLong("primaryid")) {
                        insert = true;
                        break;
                    }
                    JSONObject middle = speedJson.optJSONObject(1);
                    if (loop.mLoopSelfPrimaryId == middle.optLong("primaryid")) {
                        insert = true;
                        break;
                    }
                    JSONObject low = speedJson.optJSONObject(2);
                    if (loop.mLoopSelfPrimaryId == low.optLong("primaryid")) {
                        insert = true;
                        break;
                    }
                    JSONObject powerObj = new JSONObject(powerStr);
                    if (loop.mLoopSelfPrimaryId == powerObj.optLong("primaryid")) {
                        insert = true;
                        break;
                    }
                    JSONObject cycleObj = new JSONObject(cycleStr);
                    if (loop.mLoopSelfPrimaryId == cycleObj.optLong("primaryid")) {
                        insert = true;
                        break;
                    }

                    JSONObject humidityObj = new JSONObject(humidityStr);
                    if (loop.mLoopSelfPrimaryId == humidityObj.optLong("primaryid")) {
                        insert = true;
                        break;
                    }

                    JSONObject dehumidityObj = new JSONObject(dehumidityStr);
                    if (loop.mLoopSelfPrimaryId == dehumidityObj.optLong("primaryid")) {
                        insert = true;
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (!insert) {
                values.add(loop);
            }
        }
        return values;
    }

    /**
     * 获取数据库设备列表(通过设备类型 "device")
     *
     * @param context
     * @param name
     * @return
     */
    public static Map<String, Object> getDeviceListFromDatabaseWithNameForMap(Context context, String name) {
        SparkLightingLoopFunc func = new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        Wireless315M433MLoopFunc func1 = new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        Map<String, Object> returnValue = new HashMap<>();
        if (name.equals(ModelEnum.MAIN_LIGHT)) {
            //Spark Light
            ArrayList<SparkLightingLoop> loops = func.getSparkLightingLoopByType(ModelEnum.LOOP_TYPE_LIGHT_INT, ModelEnum.LOOP_TYPE_SWITCH_INT);
            returnValue.put(ModelEnum.SPARKLIGHTING, loops);
            //315M 433M
            ArrayList<Wireless315M433MLoop> loops1 = func1.getWireless315M433MByTypes(ModelEnum.LOOP_TYPE_LIGHT_INT, ModelEnum.LOOP_TYPE_SWITCH_INT);
            returnValue.put(ModelEnum.WIRELESS_315_433, loops1);
        }
        //Curtain
        else if (name.equals(ModelEnum.MAIN_CURTAIN)) {
            //Spark Curtain
            ArrayList<SparkLightingLoop> loops = func.getSparkLightingLoopByLoopType(ModelEnum.LOOP_TYPE_CURTAIN_INT);

            //315
            ArrayList<Wireless315M433MLoop> loops1 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_CURTAIN_INT);

            returnValue.put(ModelEnum.SPARKLIGHTING, loops);
            returnValue.put(ModelEnum.WIRELESS_315_433, loops1);

        }
        // Relay
        else if (name.equals(ModelEnum.MAIN_RELAY)) {
            //WIFI RELAY
            ArrayList<RelayLoop> loops = getRelayListWithOutVentilation(context);
            //Spark Curtain
            ArrayList<SparkLightingLoop> loops01 = func.getSparkLightingLoopByLoopType(ModelEnum.LOOP_TYPE_RELAY_INT);

            returnValue.put(ModelEnum.LOOP_RELAY, loops);
            returnValue.put(ModelEnum.SPARKLIGHTING, loops01);
        }
        //防区
        else if (name.equals(ModelEnum.MAIN_ZONE)) {
            //wired zone
            ArrayList<WiredZoneLoop> loops = (ArrayList<WiredZoneLoop>) new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWiredZoneLoopAllList();
            returnValue.put(ModelEnum.LOOP_ZONE, loops);

            //IpVDP zone
            ArrayList<IpvdpZoneLoop> loops1 = (ArrayList<IpvdpZoneLoop>) new IpvdpZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpvdpZoneLoopAllList();
            returnValue.put(ModelEnum.LOOP_IPVDP, loops1);

            //SparkLighting zone
            ArrayList<SparkLightingLoop> loops2 = func.getSparkLightingLoopByLoopType(ModelEnum.LOOP_TYPE_SENSOR_INT);
            returnValue.put(ModelEnum.SPARKLIGHTING, loops2);

            //315
            ArrayList<Wireless315M433MLoop> loops4 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_5800PIRAP_INT);
            returnValue.put(ModelEnum.WIRELESS_315_433, loops4);
            loops4 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_5804EU_INT);
            returnValue.put(ModelEnum.LOOP_5804EU, loops4);
            loops4 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_5816EU_INT);
            returnValue.put(ModelEnum.LOOP_5816EU, loops4);
        }
        //AC
        else if (name.equals(ModelEnum.MAIN_AIR_CONDITION)) {
            //Bacnet
            ArrayList<BacnetLoop> loops4 = (ArrayList<BacnetLoop>) (new BacnetLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getBacnetLoopAllList();
            returnValue.put(ModelEnum.LOOP_BACNET, loops4);

            //485 AC
            ArrayList<Wifi485Loop> loops5 = (new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getWifi485LoopByLoopType(ModelEnum.LOOP_485_AC);
            returnValue.put(ModelEnum.WIFI_485, loops5);

            //485 Thermostat
            ArrayList<Wifi485Loop> loops6 = (new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getWifi485LoopByLoopType(ModelEnum.LOOP_485_THERMOSTAT);
            returnValue.put(ModelEnum.WIFI_485, loops6);
        }
        //新风
        else if (name.equals(ModelEnum.MAIN_VENTILATION)) {
            ArrayList<VentilationLoop> loops = new VentilationLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getVentilationLoopAllList();
            returnValue.put(ModelEnum.LOOP_VENTILATION, loops);

            //485 ventilation
            ArrayList<Wifi485Loop> loops6 = (new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getWifi485LoopByLoopType(ModelEnum.LOOP_485_VENTILATION);
            returnValue.put(ModelEnum.WIFI_485, loops6);
        }
        //Back Audio
        else if (name.equals(ModelEnum.MAIN_BACKAUDIO)) {
            ArrayList<BackaudioLoop> loops = (ArrayList<BackaudioLoop>) (new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getBackaudioLoopAllList();
            returnValue.put(ModelEnum.LOOP_BACKAUDIO, loops);

        }
        //IPC
        else if (name.equals(ModelEnum.MAIN_IP_CAMERA)) {
            ArrayList<IpcStreamInfo> loops = (ArrayList<IpcStreamInfo>) (new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIpcStreamInfoAllList();
            returnValue.put(ModelEnum.LOOP_IPC, loops);
        }
        // IR DVD
        else if (name.equals(ModelEnum.MAIN_IR_DVD)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_DVD_S);
            returnValue.put(ModelEnum.LOOP_IR_DVD, loops);

        }
        // IR television
        else if (name.equals(ModelEnum.MAIN_IR_TELEVISION)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_TV_S);
            returnValue.put(ModelEnum.LOOP_IR_TV, loops);

        }
        //IR STB
        else if (name.equals(ModelEnum.MAIN_IR_STB)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_STB_S);
            returnValue.put(ModelEnum.LOOP_IR_STB, loops);

        }
        //IR AC
        else if (name.equals(ModelEnum.MAIN_IR_AC)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_AC_S);
            returnValue.put(ModelEnum.LOOP_IR_AC, loops);

        }
        // IR Customize
        else if (name.equals(ModelEnum.MAIN_IR_CUSTOMIZE)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_CUSTOMIZE_S);
            returnValue.put(ModelEnum.LOOP_IR_CUSTOM, loops);
        }
        return returnValue;
    }

    //这部分和上面一部分重复 ，后面会进行代码合并
    public static ArrayList<Object> getDeviceListFromDatabaseWithNameForArray(Context context, String name) {
        SparkLightingLoopFunc func = new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        Wireless315M433MLoopFunc func1 = new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<Object> returnArray = new ArrayList<>();
        if (name.equals(ModelEnum.MAIN_LIGHT)) {
            //Spark Light
            ArrayList<SparkLightingLoop> loops = func.getSparkLightingLoopByType(ModelEnum.LOOP_TYPE_LIGHT_INT, ModelEnum.LOOP_TYPE_SWITCH_INT);
            returnArray.addAll(loops);
            //315M 433M
            ArrayList<Wireless315M433MLoop> loops1 = func1.getWireless315M433MByTypes(ModelEnum.LOOP_TYPE_LIGHT_INT, ModelEnum.LOOP_TYPE_SWITCH_INT);
            returnArray.addAll(loops1);
        }
        //Curtain
        else if (name.equals(ModelEnum.MAIN_CURTAIN)) {
            //Spark Curtain
            ArrayList<SparkLightingLoop> loops = func.getSparkLightingLoopByLoopType(ModelEnum.LOOP_TYPE_CURTAIN_INT);

            //315
            ArrayList<Wireless315M433MLoop> loops1 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_CURTAIN_INT);

            returnArray.addAll(loops);
            returnArray.addAll(loops1);

        }
        // Relay
        else if (name.equals(ModelEnum.MAIN_RELAY)) {
            //Spark Curtain
            ArrayList<SparkLightingLoop> loops01 = func.getSparkLightingLoopByLoopType(ModelEnum.LOOP_TYPE_RELAY_INT);
            returnArray.addAll(loops01);

            //WIFI RELAY
            ArrayList<RelayLoop> loops = getRelayListWithOutVentilation(context);
            returnArray.addAll(loops);
        }
        //防区
        else if (name.equals(ModelEnum.MAIN_ZONE)) {
            //wired zone
            ArrayList<WiredZoneLoop> loops = (ArrayList<WiredZoneLoop>) new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWiredZoneLoopAllList();
            returnArray.addAll(loops);

            //IpVDP zone
            ArrayList<IpvdpZoneLoop> loops1 = (ArrayList<IpvdpZoneLoop>) new IpvdpZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpvdpZoneLoopAllList();
            returnArray.addAll(loops1);

            //SparkLighting zone
            ArrayList<SparkLightingLoop> loops2 = func.getSparkLightingLoopByLoopType(ModelEnum.LOOP_TYPE_SENSOR_INT);
            returnArray.addAll(loops2);

            //315
            ArrayList<Wireless315M433MLoop> loops4 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_5800PIRAP_INT);
            returnArray.addAll(loops4);
            loops4 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_5804EU_INT);
            returnArray.addAll(loops4);
            loops4 = func1.getWireless315M433MByLoopLoopType(ModelEnum.LOOP_TYPE_5816EU_INT);
            returnArray.addAll(loops4);
        }
        //AC
        else if (name.equals(ModelEnum.MAIN_AIR_CONDITION)) {
            //Bacnet
            ArrayList<BacnetLoop> loops4 = (ArrayList<BacnetLoop>) (new BacnetLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getBacnetLoopAllList();
            returnArray.addAll(loops4);

            //485 AC
            ArrayList<Wifi485Loop> loops5 = (new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getWifi485LoopByLoopType(ModelEnum.LOOP_485_AC);
            returnArray.addAll(loops5);

            //485 Thermostat
            ArrayList<Wifi485Loop> loops6 = (new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getWifi485LoopByLoopType(ModelEnum.LOOP_485_THERMOSTAT);
            returnArray.addAll(loops6);

        }
        //新风
        else if (name.equals(ModelEnum.MAIN_VENTILATION)) {
            ArrayList<VentilationLoop> loops = new VentilationLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getVentilationLoopAllList();
            returnArray.addAll(loops);

            //485 ventilation
            ArrayList<Wifi485Loop> loops6 = (new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getWifi485LoopByLoopType(ModelEnum.LOOP_485_VENTILATION);
            returnArray.addAll(loops6);
        }
        //Back Audio
        else if (name.equals(ModelEnum.MAIN_BACKAUDIO)) {
            ArrayList<BackaudioLoop> loops = (ArrayList<BackaudioLoop>) (new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getBackaudioLoopAllList();
            returnArray.addAll(loops);
        }
        //IPC
        else if (name.equals(ModelEnum.MAIN_IP_CAMERA)) {
            ArrayList<IpcStreamInfo> loops = (ArrayList<IpcStreamInfo>) (new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIpcStreamInfoAllList();
            returnArray.addAll(loops);

        }
        // IR DVD
        else if (name.equals(ModelEnum.MAIN_IR_DVD)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_DVD_S);
            returnArray.addAll(loops);

        }
        // IR television
        else if (name.equals(ModelEnum.MAIN_IR_TELEVISION)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_TV_S);
            returnArray.addAll(loops);

        }
        //IR STB
        else if (name.equals(ModelEnum.MAIN_IR_STB)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_STB_S);
            returnArray.addAll(loops);

        }
        //IR AC
        else if (name.equals(ModelEnum.MAIN_IR_AC)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_AC_S);
            returnArray.addAll(loops);

        }
        // IR Customize
        else if (name.equals(ModelEnum.MAIN_IR_CUSTOMIZE)) {
            ArrayList<IrLoop> loops = (ArrayList<IrLoop>) (new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getIrLoopListByLoopType(ModelEnum.IR_TYPE_CUSTOMIZE_S);
            returnArray.addAll(loops);
        }
        return returnArray;
    }


    /**
     * 通过房间名称在数据库中获取设备列表, 返回Array
     *
     * @param context
     * @return
     */
    public static ArrayList<Object> getRoomDeviceListFromDatabaseForArray(Context context, int roomid) {
        ArrayList<Object> returnValue = new ArrayList<>();

        //SparkLighting
        ArrayList<SparkLightingLoop> loops = new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getSparkLightingLoopByRoom(roomid);
        returnValue.addAll(loops);

        //MAIA
        ArrayList<Wireless315M433MLoop> loops1 = new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWireless315M433MByRoom(roomid);
        returnValue.addAll(loops1);

        //wifi relay
        ArrayList<RelayLoop> loops2 = getRelayListWithOutVentilation(context);
        returnValue.addAll(loops2);

        //wifi zone
        ArrayList<WiredZoneLoop> loops3 = new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWiredZoneLoopByRoom(roomid);
        returnValue.addAll(loops3);

        //IPVDP Zone
        ArrayList<IpvdpZoneLoop> loops4 = new IpvdpZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpvdpZoneLoopByRoom(roomid);
        returnValue.addAll(loops4);

        //Bacnet
        ArrayList<BacnetLoop> loops5 = new BacnetLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBacnetLoopByRoom(roomid);
        returnValue.addAll(loops5);

        //Back Audio
        ArrayList<BackaudioLoop> loops6 = new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioLoopByRoom(roomid);
        returnValue.addAll(loops6);

        //IPC
        ArrayList<IpcStreamInfo> loops7 = new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpcStreamInfoByRoom(roomid);
        returnValue.addAll(loops7);

        //IR
        ArrayList<IrLoop> loops8 = new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrLoopByRoom(roomid);
        returnValue.addAll(loops8);

        //Ventilation
        ArrayList<VentilationLoop> loops9 = new VentilationLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getVentilationLoopByRoomId(roomid);
        returnValue.addAll(loops9);

        return returnValue;
    }

    /**
     * 通过房间名称在数据库中获取设备列表, 返回MAP
     *
     * @param context
     * @return
     */
    public static Map<String, Object> getRoomDeviceListFromDatabaseForMap(Context context, int roomid) {
        Map<String, Object> returnValue = new HashMap<>();

        //SparkLighting
        ArrayList<SparkLightingLoop> loops = new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getSparkLightingLoopByRoom(roomid);
        if (loops.size() > 0) {
            returnValue.put(ModelEnum.SPARKLIGHTING, loops);
        }

        //MAIA
        ArrayList<Wireless315M433MLoop> loops1 = new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWireless315M433MByRoom(roomid);
        if (loops1.size() > 0) {
            returnValue.put(ModelEnum.WIRELESS_315_433, loops1);
        }

        //wifi relay
        ArrayList<RelayLoop> loops2 = getRelayListWithOutVentilation(context);
        if (loops2.size() > 0) {
            returnValue.put(ModelEnum.LOOP_RELAY, loops2);
        }

        //wifi zone
        ArrayList<WiredZoneLoop> loops3 = new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWiredZoneLoopByRoom(roomid);
        returnValue.put(ModelEnum.LOOP_ZONE, loops3);

        //IPVDP Zone
        ArrayList<IpvdpZoneLoop> loops4 = new IpvdpZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpvdpZoneLoopByRoom(roomid);
        if (loops4.size() > 0) {
            returnValue.put(ModelEnum.LOOP_IPVDP, loops4);
        }

        //Bacnet
        ArrayList<BacnetLoop> loops5 = new BacnetLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBacnetLoopByRoom(roomid);
        if (loops5.size() > 0) {
            returnValue.put(ModelEnum.LOOP_BACNET, loops5);
        }

        //Wifi 485
        ArrayList<Wifi485Loop> loops9 = new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWifi485LoopByRoom(roomid);
        if (loops9.size() > 0) {
            returnValue.put(ModelEnum.WIFI_485, loops9);
        }
        //新风
        ArrayList<VentilationLoop> loops7 = new VentilationLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getVentilationLoopByRoomId(roomid);
        if (loops7.size() > 0) {
            returnValue.put(ModelEnum.LOOP_VENTILATION, loops7);
        }

        //Back Audio
        ArrayList<BackaudioLoop> loops6 = new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioLoopByRoom(roomid);
        if (loops6.size() > 0) {
            returnValue.put(ModelEnum.LOOP_BACKAUDIO, loops6);
        }

        //IPC
        ArrayList<IpcStreamInfo> streamInfos = new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpcStreamInfoByRoom(roomid);
        ArrayList<IPCameraListDetail> arrayList = new ArrayList<>();
        if (streamInfos != null && streamInfos.size() > 0) {
            for (IpcStreamInfo info : streamInfos) {
                IPCameraListDetail detail = new IPCameraListDetail();
                PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(info.mDevId);
                if (device == null) {
                    Loger.print(TAG, "ssd getRoomDeviceListFromDatabaseForMap ipcamera device is null", Thread.currentThread());
                } else {
                    detail.title = device.mName;
                    detail.details = device.mIpAddr;
                    detail.ipcStreamInfo = info;
                    arrayList.add(detail);
                }
            }
        }
        if (arrayList.size() > 0) {
            returnValue.put(ModelEnum.LOOP_IPC, arrayList);
        }

        //IR
        ArrayList<IrLoop> loops8 = new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrLoopByRoom(roomid);
        ArrayList<IrLoop> loop_ir_custom = new ArrayList<>();
        ArrayList<IrLoop> loop_ir_ac = new ArrayList<>();
        ArrayList<IrLoop> loop_ir_tv = new ArrayList<>();
        ArrayList<IrLoop> loop_ir_stb = new ArrayList<>();
        ArrayList<IrLoop> loop_ir_dvd = new ArrayList<>();

        if (loops8.size() > 0) {
            for (int i = 0; i < loops8.size(); i++) {
                IrLoop loop = loops8.get(i);
                if (loop.mLoopType.equalsIgnoreCase("customize")) {
                    loop_ir_custom.add(loop);
                } else if (loop.mLoopType.equalsIgnoreCase("ac")) {
                    loop_ir_ac.add(loop);
                } else if (loop.mLoopType.equalsIgnoreCase("television")) {
                    loop_ir_tv.add(loop);
                } else if (loop.mLoopType.equalsIgnoreCase("stb")) {
                    loop_ir_stb.add(loop);
                } else if (loop.mLoopType.equalsIgnoreCase("dvd")) {
                    loop_ir_dvd.add(loop);
                }
            }

            if (loop_ir_ac.size() > 0) {
                returnValue.put(ModelEnum.LOOP_IR_AC, loop_ir_ac);
            }

            if (loop_ir_custom.size() > 0) {
                returnValue.put(ModelEnum.LOOP_IR_CUSTOM, loop_ir_custom);
            }

            if (loop_ir_stb.size() > 0) {
                returnValue.put(ModelEnum.LOOP_IR_STB, loop_ir_stb);
            }

            if (loop_ir_dvd.size() > 0) {
                returnValue.put(ModelEnum.LOOP_IR_DVD, loop_ir_dvd);
            }

            if (loop_ir_tv.size() > 0) {
                returnValue.put(ModelEnum.LOOP_IR_TV, loop_ir_tv);
            }
        }


        return returnValue;
    }


    /**
     * 通过Module type 和 primary id 来获取module对象
     *
     * @param context
     * @param moduleType
     * @param primaryId
     * @return
     */
    public static Object getDeviceFromModuleTypeWithTypeAndID(Context context, int moduleType, long primaryId) {
        switch (moduleType) {
            case ModelEnum.MODULE_TYPE_SPARKLIGHTING: {
                return new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getSparkLightingLoopByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_BACNET: {
                return new BacnetLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBacnetLoopByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_IPC: {
                return new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpcStreamInfoByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_WIFIIR: {
                return new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIrLoop(primaryId);
            }
            case ModelEnum.MODULE_TYPE_WIFIRELAY: {
                return new RelayLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getRelayLoopByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_WIFI485: {
                return new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWifi485LoopByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_WIREDZONE: {
                return new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWiredZoneLoopByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_WIFI315M433M: {
                return new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getWireless315M433MLoopByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_BACKAUDIO: {
                return new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioLoopByPrimaryId(primaryId);
            }
            case ModelEnum.MODULE_TYPE_IPVDP: {
                return new IpvdpZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getIpvdpZoneLoopByPrimaryId(primaryId);
            }
            default:
                break;
        }
        return null;
    }

    public static int getDeviceTypeFromLoop(Object loop){
        if (loop == null) return ModelEnum.DEVICE_TYPE_SWITCH;
        if (loop instanceof SparkLightingLoop){
            SparkLightingLoop loop1 = (SparkLightingLoop) loop;
            if (loop1.mLoopId == ModelEnum.LOOP_TYPE_SWITCH_INT){
                return ModelEnum.DEVICE_TYPE_SWITCH;
            }else if (loop1.mLoopId == ModelEnum.LOOP_TYPE_CURTAIN_INT){
                return ModelEnum.DEVICE_TYPE_CURTAIN;
            }else if (loop1.mLoopId == ModelEnum.LOOP_TYPE_RELAY_INT){
                return ModelEnum.DEVICE_TYPE_RELAY;
            }else if (loop1.mLoopId == ModelEnum.LOOP_TYPE_LIGHT_INT){
                return ModelEnum.DEVICE_TYPE_LIGHT;
            }
        }
        if (loop instanceof RelayLoop){
            return ModelEnum.DEVICE_TYPE_RELAY;
        }
        if (loop instanceof BacnetLoop || loop instanceof Wifi485Loop){
            return ModelEnum.DEVICE_TYPE_AC;
        }
        if (loop instanceof BackaudioLoop){
            return ModelEnum.DEVICE_TYPE_BACKAUDIO;
        }
        if (loop instanceof Wireless315M433MLoop){
            return ModelEnum.DEVICE_TYPE_LIGHT;
        }
        if (loop instanceof IpcStreamInfo){
            return ModelEnum.DEVICE_TYPE_IP_CAMERA;
        }
        if (loop instanceof VentilationLoop){
            return ModelEnum.DEVICE_TYPE_IR_CUSTOMIZE;//新风没有处理
        }
        if (loop instanceof WiredZoneLoop || loop instanceof IpvdpZoneLoop){
            return ModelEnum.DEVICE_TYPE_ZONE;
        }
        return ModelEnum.DEVICE_TYPE_LIGHT;
    }

    public static int getTitleImageFromDeviceType(int deviceType, boolean isRule) {
        switch (deviceType) {
            case ModelEnum.DEVICE_TYPE_SWITCH: {
                return isRule ? R.mipmap.rule_device_type_light : R.mipmap.device_type_light;
            }
            case ModelEnum.DEVICE_TYPE_LIGHT: {
                return isRule ? R.mipmap.rule_device_type_light : R.mipmap.device_type_light;
            }
            case ModelEnum.DEVICE_TYPE_CURTAIN: {
                return isRule ? R.mipmap.rule_device_type_curtain : R.mipmap.device_type_curtain;
            }
            case ModelEnum.DEVICE_TYPE_RELAY: {
                return isRule ? R.mipmap.rule_device_type_relay : R.mipmap.device_type_relay;
            }
            case ModelEnum.DEVICE_TYPE_ZONE: {
                return isRule ? R.mipmap.rule_device_type_zone : R.mipmap.device_type_zone;
            }
            case ModelEnum.DEVICE_TYPE_IP_CAMERA: {
                return isRule ? R.mipmap.rule_device_type_ip_camera : R.mipmap.device_type_ip_camera;
            }
            case ModelEnum.DEVICE_TYPE_AC: {
                return isRule ? R.mipmap.rule_device_type_air_conditioner : R.mipmap.device_type_air_conditioner;
            }
            case ModelEnum.DEVICE_TYPE_BACKAUDIO: {
                return isRule ? R.mipmap.rule_device_type_backaudio : R.mipmap.device_type_backaudio;
            }
            case ModelEnum.DEVICE_TYPE_IR: {
                return isRule ? R.mipmap.rule_ir_customize : R.mipmap.ir_customize;
            }
            case ModelEnum.DEVICE_TYPE_IR_DVD: {
                return isRule ? R.mipmap.rule_ir_dvd : R.mipmap.ir_dvd;
            }
            case ModelEnum.DEVICE_TYPE_IR_TV: {
                return isRule ? R.mipmap.rule_ir_television : R.mipmap.ir_television;
            }
            case ModelEnum.DEVICE_TYPE_IR_STB: {
                return isRule ? R.mipmap.rule_ir_stb : R.mipmap.ir_stb;
            }
            case ModelEnum.DEVICE_TYPE_IR_AC: {
                return isRule ? R.mipmap.rule_ir_ac : R.mipmap.ir_ac;
            }
            case ModelEnum.DEVICE_TYPE_IR_CUSTOMIZE: {
                return isRule ? R.mipmap.rule_ir_customize : R.mipmap.ir_customize;
            }
            default:
                break;
        }
        return R.mipmap.rules_sensor;
    }

    /**
     * 用于对设备获取Device的列表
     *
     * @param context
     * @param object
     * @return
     */
    public static String getDeviceTitleWithObject(Context context, Object object) {
        if (object == null) {
            return "";
        }
        if (object instanceof BasicLoop) {
            BasicLoop loop = (BasicLoop) object;
            return loop.mLoopName;
        } else if (object instanceof IpcStreamInfo) {
            IpcStreamInfo info = (IpcStreamInfo) object;
            PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(info.mDevId);
            if (mainDevice != null) {
                return mainDevice.mName;
            } else {
                return info.mMainStream;
            }
        }
        return "";
    }

    /**
     * 更新 Rule device  部分 custom model
     *
     * @param object
     * @param actionInfo
     */
    public static Object updateTriggerDeviceWithInfo(Object object, JSONObject actionInfo) {
        if (object == null || actionInfo == null) {
            return object;
        }
//        String openclosepercent = null;
//        String status = null;//"on"-"opening"
//        int time = 0;
//
//        //wifi 485
//        int settemp = 0;
//        String fanspeed = null;//"high"
//        String mode = null;//"heating"
//
//        //back audio
//        String power = null;
//        String playstatus = null;
//        int volume = 0;

        if (object instanceof SparkLightingLoop) {
            SparkLightingLoop loop = (SparkLightingLoop) object;
            Iterator it = actionInfo.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("openclosepercent".equalsIgnoreCase(key)) {
                    loop.customStatus.openClosePercent = actionInfo.optInt(key);
                } else if ("status".equalsIgnoreCase(key)) {
                    loop.customStatus.status = "on".equalsIgnoreCase(actionInfo.optString(key)) ? true : false;
                }
            }
        } else if (object instanceof RelayLoop) {
            RelayLoop loop = (RelayLoop) object;
            Iterator it = actionInfo.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("status".equalsIgnoreCase(key)) {
                    loop.customStatus.status = "on".equalsIgnoreCase(actionInfo.optString(key)) ? true : false;
                }
            }
        } else if (object instanceof BackaudioLoop) {
            BackaudioLoop loop = (BackaudioLoop) object;
            Iterator it = actionInfo.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("power".equalsIgnoreCase(key)) {
                    loop.customModel.power = actionInfo.optString(key);
                } else if ("playstatus".equalsIgnoreCase(key)) {
                    loop.customModel.playstatus = actionInfo.optString(key);
                } else if ("volume".equalsIgnoreCase(key)) {
                    loop.customModel.volume = actionInfo.optInt(key);
                }
            }
        } else if (object instanceof BacnetLoop) {
            BacnetLoop loop = (BacnetLoop) object;
            Iterator it = actionInfo.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("settemp".equalsIgnoreCase(key)) {
                    loop.ac_customModel.set_temp = actionInfo.optInt(key);
                } else if ("fanspeed".equalsIgnoreCase(key)) {
                    loop.ac_customModel.fan_speed = actionInfo.optString(key);
                } else if ("mode".equalsIgnoreCase(key)) {
                    loop.ac_customModel.mode = actionInfo.optString(key);
                }
            }
        } else if (object instanceof Wifi485Loop) {
            Wifi485Loop loop = (Wifi485Loop) object;
            Iterator it = actionInfo.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("settemp".equalsIgnoreCase(key)) {
                    loop.customModel.set_temp = actionInfo.optInt(key);
                } else if ("fanspeed".equalsIgnoreCase(key)) {
                    loop.customModel.fan_speed = actionInfo.optString(key);
                } else if ("mode".equalsIgnoreCase(key)) {
                    loop.customModel.mode = actionInfo.optString(key);
                }
            }
        } else if (object instanceof Wireless315M433MLoop) {
            Wireless315M433MLoop loop = (Wireless315M433MLoop) object;
            Iterator it = actionInfo.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if ("openclosepercent".equalsIgnoreCase(key)) {
                    loop.customStatus.openClosePercent = actionInfo.optInt(key);
                } else if ("status".equalsIgnoreCase(key)) {
                    loop.customStatus.status = "on".equalsIgnoreCase(actionInfo.optString(key)) ? true : false;
                }
            }
        }
        return object;
    }
}
