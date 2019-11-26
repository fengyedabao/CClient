package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.UIItem.AirControllerUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.BacnetAirConditioner;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.plist_parser.xml.plist.domain.PListObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/7/27. 15:55
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 空调设备
 * <p/>
 * 用于综合Bacnet和WiFi485空调
 */
public class AirController {
    public static final String TAG = AirController.class.getSimpleName();

    private static BacnetLoop readDeviceBacnetLoop = null;//用于记录查询状态时存储这个对象
    private static Wifi485Loop readDeviceWifi485Loop = null;//用于记录查询状态时存储这个对象


    /**
     * 根据选择的值 更新状态
     *
     * @param loop
     * @param uiItem
     * @return
     */
    public static Object updateLoopFromUIItem(Object loop, AirControllerUIItem uiItem) {
        if (loop == null || uiItem == null) return loop;
        BacnetAirConditioner airConditioner = new BacnetAirConditioner();
        airConditioner.status = uiItem.powerIconUIItem.IR_icon_select;
        airConditioner.set_temp = uiItem.setTemp;
        airConditioner.current_temp = uiItem.curTemp;
        for (int i = 0; i < uiItem.fanspeedIconItemList.size(); i++) {
            MenuDeviceIRIconItem iconItem = uiItem.fanspeedIconItemList.get(i);
            if (iconItem.IR_icon_select) {
                airConditioner.fan_speed = iconItem.ventilation_type_value;
                break;
            }
        }
        for (int i = 0; i < uiItem.airModeIconItemList.size(); i++) {
            MenuDeviceIRIconItem iconItem = uiItem.airModeIconItemList.get(i);
            if (iconItem.IR_icon_select) {
                airConditioner.mode = iconItem.ventilation_type_value;
                break;
            }
        }
        if (loop instanceof BacnetLoop) {
            BacnetLoop bacnetLoop = (BacnetLoop) loop;
            bacnetLoop.ac_customModel.status = airConditioner.status;
            bacnetLoop.ac_customModel.mode = airConditioner.mode;
            bacnetLoop.ac_customModel.fan_speed = airConditioner.fan_speed;
            bacnetLoop.ac_customModel.current_temp = airConditioner.current_temp;
            bacnetLoop.ac_customModel.set_temp = airConditioner.set_temp;
            return bacnetLoop;
        } else if (loop instanceof Wifi485Loop) {
            Wifi485Loop wifi485Loop = (Wifi485Loop) loop;
            wifi485Loop.customModel.status = airConditioner.status;
            wifi485Loop.customModel.mode = airConditioner.mode;
            wifi485Loop.customModel.fan_speed = airConditioner.fan_speed;
            wifi485Loop.customModel.current_temp = airConditioner.current_temp;
            wifi485Loop.customModel.set_temp = airConditioner.set_temp;
            return wifi485Loop;
        }
        return loop;
    }

    /**
     * 读取对应设备的状态
     *
     * @param context
     * @param loop
     */
    public static void getAirControllerUIItemAndReadStatusWithInfo(Context context, Object loop) {
        if (loop == null) {
            Loger.print(TAG, "ssd read device status loop is null", Thread.currentThread());
            return;
        }
        if (!(loop instanceof BacnetLoop) && !(loop instanceof Wifi485Loop)) {
            Loger.print(TAG, "ssd read air device status : loop type is not correct", Thread.currentThread());
            return;
        }

        readDeviceWifi485Loop = null;
        readDeviceBacnetLoop = null;

        BasicLoop basicLoop = (BasicLoop) loop;
        PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(basicLoop.mModulePrimaryId);

        if (device == null) {
            Loger.print(TAG, "ssd read air device status : peripheraDevice is null", Thread.currentThread());
        }
        if (loop instanceof BacnetLoop) {
            BacnetLoop bacnetLoop = (BacnetLoop) loop;
            ArrayList<BacnetLoop> bacnetLoops = new ArrayList<>();
            bacnetLoops.add(bacnetLoop);
            readDeviceBacnetLoop = bacnetLoop;

            //读取状态
            BacnetController.checkoutDeviceListState(context, bacnetLoops);
        } else if (loop instanceof Wifi485Loop) {
            //Wifi485空调
            Wifi485Loop wifi485Loop = (Wifi485Loop) loop;
            ArrayList<Wifi485Loop> wifi485Loops = new ArrayList<>();
            wifi485Loops.add(wifi485Loop);
            readDeviceWifi485Loop = wifi485Loop;

            //读取状态
            Wifi485Controller.checkoutDeviceListState(context, wifi485Loops);
        }
    }

    /**
     * 发送 Icon 控制
     *
     * @param context
     * @param uiItem
     * @param iconItem
     */
    public static void sendControlIconItem(Context context, Object loop, AirControllerUIItem uiItem, MenuDeviceIRIconItem iconItem) {
        BacnetAirConditioner airConditioner = uiItem.airConditioner;
        if (airConditioner == null) {
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, null));
            return;
        }
        if ("fan_speed".equalsIgnoreCase(iconItem.ventilation_type)) {
            airConditioner.fan_speed = iconItem.ventilation_type_value;
        } else if ("mode".equalsIgnoreCase(iconItem.ventilation_type)) {
            airConditioner.mode = iconItem.ventilation_type_value;
        } else if (airConditioner.status) {
            airConditioner.status = false;
        } else {
            airConditioner.status = true;
        }
        uiItem.airConditioner = airConditioner;
        sendControlOfLoop(context, loop, airConditioner);
    }

    /**
     * 发送控制温度的接口
     *
     * @param context
     * @param loop
     * @param uiItem
     * @param temperature
     */
    public static void sendControlTemperature(Context context, Object loop, AirControllerUIItem uiItem, int temperature) {
        BacnetAirConditioner airConditioner = uiItem.airConditioner;
        airConditioner.set_temp = temperature;
        uiItem.airConditioner = airConditioner;
        sendControlOfLoop(context, loop, airConditioner);
    }

    /**
     * 读取状态后更新设备状态
     *
     * @param context
     * @param returnArray
     * @param type
     */
    public static void updateDeviceStatusWithInfo(Context context, JSONArray returnArray, int type) {
        if (returnArray == null || returnArray.length() == 0) {
            AirControllerUIItem airControllerUIItem = getAirControllerUIItem(context, null);
            //发送Event事件 更新界面状态
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_AIR_CONTROLLER_STATE, airControllerUIItem));
            return;
        }

        for (int j = 0; j < returnArray.length(); j++) {
            JSONObject item;
            try {
                item = (JSONObject) returnArray.get(j);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (type == ModelEnum.MODULE_TYPE_BACNET) {
                //Bacnet
                if (readDeviceBacnetLoop != null) {
                    //Bacnet
                    BacnetLoop loop = readDeviceBacnetLoop;
                    //Periphera
                    PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(loop.mModulePrimaryId);
                    if (mainDevice == null) {
                        Loger.print(TAG, "ssd updateDeviceStatusWithInfo Wireless315M433MLoop error", Thread.currentThread());
                        break;
                    }

                    int loopid = item.optInt("loopid");
                    int deviceid = item.optInt("deviceid");
                    int bacnetdeviceId = item.optInt("bacnetdeviceid");
                    if (loopid == loop.mLoopId && deviceid == loop.mSubDevId && mainDevice.mBacnetId == bacnetdeviceId) {
                        //ios端检查错误，显示没有运行错误检测，直接break了，android现在不先做处理，等需要的时候在进行错误检测
                        loop.ac_customModel.set_temp = item.optInt("settemp");
                        loop.ac_customModel.current_temp = item.optInt("currenttemp");
                        loop.ac_customModel.mode = item.optString("mode");
                        loop.ac_customModel.fan_speed = item.optString("fanspeed");
                        loop.ac_customModel.status = "on".equalsIgnoreCase(item.optString("status")) ? true : false;

                        AirControllerUIItem airControllerUIItem = getAirControllerUIItem(context, loop);
                        //发送Event事件 更新界面状态
                        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_AIR_CONTROLLER_STATE, airControllerUIItem));
                        return;
                    }
                }
            } else if (type == ModelEnum.MODULE_TYPE_WIFI485) {
                //Wifi485
                if (readDeviceWifi485Loop != null) {
                    Wifi485Loop loop = readDeviceWifi485Loop;
                    //Periphera
                    PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(loop.mModulePrimaryId);
                    if (mainDevice == null) {
                        Loger.print(TAG, "ssd updateDeviceStatusWithInfo Wireless315M433MLoop error", Thread.currentThread());
                        break;
                    }

                    int loopid = item.optInt("loopid");
                    int portId = item.optInt("portid");
                    int slaveAddr = item.optInt("slaveaddr");
                    String moduleAddr = item.optString("modulemacaddr");

                    if (loopid == loop.mLoopId && portId == loop.mPortId && slaveAddr == loop.mSlaveAddr && moduleAddr.equals(mainDevice.mMacAddr)) {
                        //ios端检查错误，显示没有运行错误检测，直接break了，android现在不先做处理，等需要的时候在进行错误检测
                        loop.customModel.set_temp = item.optInt("settemp");
                        loop.customModel.current_temp = item.optInt("currenttemp");
                        loop.customModel.mode = item.optString("mode");
                        loop.customModel.fan_speed = item.optString("fanspeed");
                        loop.customModel.status = "on".equalsIgnoreCase(item.optString("status")) ? true : false;
                        AirControllerUIItem airControllerUIItem = getAirControllerUIItem(context, loop);
                        //发送Event事件 更新界面状态
                        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_AIR_CONTROLLER_STATE, airControllerUIItem));
                        return;
                    }
                }
            }
        }
    }


    /************* private method *************/
    /**
     * 获取loop 对应的 AirConditioner
     *
     * @param loop
     * @return
     */
    private static BacnetAirConditioner getLoopAirConditioner(Object loop) {
        BacnetAirConditioner conditioner = null;
        if (loop instanceof BacnetLoop) {
            BacnetLoop bacnetLoop = (BacnetLoop) loop;
            conditioner = bacnetLoop.ac_customModel;
        } else if (loop instanceof Wifi485Loop) {
            Wifi485Loop wifi485Loop = (Wifi485Loop) loop;
            conditioner = wifi485Loop.customModel;
        }
        return conditioner;
    }

    /**
     * 更新 loop 的 custom model
     *
     * @param loop
     * @param conditioner
     * @return
     */
    private static Object updateLoopAirConditioner(Object loop, BacnetAirConditioner conditioner) {
        if (loop instanceof BacnetLoop) {
            BacnetLoop bacnetLoop = (BacnetLoop) loop;
            bacnetLoop.ac_customModel = conditioner;
            return bacnetLoop;
        } else if (loop instanceof Wifi485Loop) {
            Wifi485Loop wifi485Loop = (Wifi485Loop) loop;
            wifi485Loop.customModel = conditioner;
            return wifi485Loop;
        }
        return loop;
    }

    /**
     * 获取AirController 参数
     *
     * @param context
     * @param loop
     * @return
     */
    public static AirControllerUIItem getAirControllerUIItem(Context context, Object loop) {
        AirControllerUIItem uiItem = new AirControllerUIItem();
        if (loop == null) {
            uiItem.powerIconUIItem = getPowerIconItem(null);
            uiItem.airModeIconItemList = getAirModeList(context, loop);
            uiItem.fanspeedIconItemList = getAirFanSpeedList(context, loop);
            uiItem.airConditioner = null;
            return uiItem;
        } else {
            BacnetAirConditioner airConditioner = getLoopAirConditioner(loop);
            uiItem.powerIconUIItem = getPowerIconItem(airConditioner);
            uiItem.setTemp = airConditioner.set_temp;
            uiItem.curTemp = airConditioner.current_temp;
            uiItem.airModeIconItemList = getAirModeList(context, loop);
            uiItem.fanspeedIconItemList = getAirFanSpeedList(context, loop);
            uiItem.airConditioner = airConditioner;
            return uiItem;
        }
    }

    /**
     * 获取空调 风速 
     *
     * @param context
     * @param loop
     * @return
     */
    private static ArrayList<MenuDeviceIRIconItem> getAirFanSpeedList(Context context, Object loop) {
        String value = AirController.getDefaultAirForKey(context, "fan_speed", loop);
        if (value == null || "".equalsIgnoreCase(value)) {
            Loger.print(TAG, "ssd get air fun speed is null", Thread.currentThread());
            return new ArrayList<>();
        }
        ArrayList<MenuDeviceIRIconItem> list = AirController.getSplitIconItems("fan_speed", value);
        BacnetAirConditioner conditioner = getLoopAirConditioner(loop);
        if (conditioner == null || "".equalsIgnoreCase(conditioner.mode) || "".equalsIgnoreCase(conditioner.fan_speed))
            return list;
        return updateIconItems(list, conditioner.fan_speed);
    }

    /**
     * 获取 开关 按钮
     *
     * @param airConditioner
     * @return
     */
    private static MenuDeviceIRIconItem getPowerIconItem(BacnetAirConditioner airConditioner) {
        MenuDeviceIRIconItem iconItem = new MenuDeviceIRIconItem();
        iconItem.IR_icon_name = DeviceManager.getNameWithProtocol("IR_POWER");
        iconItem.IR_icon_imageName = "IR_POWER";
        DeviceManager.transferIRIconImage(DeviceManager.getImageNameWithprotocol("IR_POWER"), iconItem);
        iconItem.IR_icon_enable = true;
        iconItem.ventilation_type = "power";
        iconItem.ventilation_type_value = "off";
        iconItem.IR_icon_select = false;
        if (airConditioner == null) {
            return iconItem;
        } else {
            if (airConditioner.status) {
                iconItem.IR_icon_select = true;
            }
        }
        return iconItem;
    }

    /**
     * 获取空调 模式
     *
     * @param context
     * @param loop
     * @return
     */
    private static ArrayList<MenuDeviceIRIconItem> getAirModeList(Context context, Object loop) {
        String value = AirController.getDefaultAirForKey(context, "mode", loop);
        if (value == null || "".equalsIgnoreCase(value)) {
            Loger.print(TAG, "ssd get air mode is null", Thread.currentThread());
            return new ArrayList<>();
        }
        ArrayList<MenuDeviceIRIconItem> list = AirController.getSplitIconItems("mode", value);
        BacnetAirConditioner conditioner = getLoopAirConditioner(loop);
        if (conditioner == null || "".equalsIgnoreCase(conditioner.mode) || "".equalsIgnoreCase(conditioner.fan_speed))
            return list;
        return updateIconItems(list, conditioner.mode);
    }


    /**
     * 根据分割的数据返回more的数据
     *
     * @param value
     * @return
     */
    private static ArrayList<MenuDeviceIRIconItem> getSplitIconItems(String key, String value) {
        ArrayList<MenuDeviceIRIconItem> returnValue = new ArrayList<>();
        String[] funspeed = value.split(",");
        for (int i = 0; i < funspeed.length; i++) {
            String fun = funspeed[i];
            MenuDeviceIRIconItem item = new MenuDeviceIRIconItem();
            item.IR_icon_name = null;
            item.IR_icon_imageName = fun;
            item.ventilation_type = key;
            item.ventilation_type_value = fun;
            DeviceManager.transferIRIconImage(fun, item);
            returnValue.add(item);
        }
        return returnValue;
    }

    /**
     * 更新 icon list 的状态
     *
     * @param list
     * @return
     */
    private static ArrayList<MenuDeviceIRIconItem> updateIconItems(ArrayList<MenuDeviceIRIconItem> list, String message) {
        for (int i = 0; i < list.size(); i++) {
            MenuDeviceIRIconItem item = list.get(i);
            if (item.IR_icon_imageName.equalsIgnoreCase(message)) {
                item.IR_icon_select = true;
            }
        }
        return list;
    }


    /**
     * 获取默认的配置信息
     *
     * @param context
     * @param key
     * @param loop
     * @return
     */
    private static String getDefaultAirForKey(Context context, String key, Object loop) {
        if (loop == null) {
            Loger.print(TAG, "ssd getDefaultAirForKey : loop is null", Thread.currentThread());
            return "";
        }
        if (!(loop instanceof BacnetLoop) && !(loop instanceof Wifi485Loop)) {
            Loger.print(TAG, "ssd getDefaultAirForKey : loop type is not correct", Thread.currentThread());
            return "";
        }

        BasicLoop basicLoop = (BasicLoop) loop;
        PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(basicLoop.mModulePrimaryId);

        if (device == null) {
            Loger.print(TAG, "ssd getDefaultAirForKey : peripheraDevice is null", Thread.currentThread());
        }
        String module = DeviceManager.getModuleTypeProtocolFromInt(device.mType);

        String branchname = null;
        Map<String, PListObject> config = null;
        if (loop instanceof BacnetLoop) {
            branchname = device.mBrandName;
            Loger.print(TAG, "ssd getDefaultAirForKey : " + branchname, Thread.currentThread());
            //获取配置信息
            config = DeviceManager.getAirConditionerConfigFromModule(context, module, branchname);
        } else if (loop instanceof Wifi485Loop) {
            //Wifi485空调
            Wifi485Loop wifi485Loop = (Wifi485Loop) loop;
            branchname = wifi485Loop.mBrandName;
            if (branchname.length() <= 0) {
                branchname = device.mBrandName;
            }
            Loger.print(TAG, "ssd getDefaultAirForKey : " + branchname, Thread.currentThread());
            //获取配置信息
            config = DeviceManager.getAirConditionerConfigFromModule(context, module, branchname);
        }
        if (config == null) return "";
        com.honeywell.cube.utils.plist_parser.xml.plist.domain.String value = (com.honeywell.cube.utils.plist_parser.xml.plist.domain.String) config.get(key);
        String valueStr = value.getValue();
        return valueStr;
    }

    /**
     * 发送控制命令
     *
     * @param context
     * @param loop
     */
    private static void sendControlOfLoop(Context context, Object loop, BacnetAirConditioner conditioner) {
        if (loop == null) return;
        if (!(loop instanceof BacnetLoop) && !(loop instanceof Wifi485Loop)) {
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, null));
            return;
        }
        if (conditioner == null) {
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, null));
            return;
        }
        BasicLoop basicLoop = (BasicLoop) loop;
        PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(basicLoop.mModulePrimaryId);
        if (device == null) {
            Loger.print(TAG, "ssd send device control status : peripheraDevice is null", Thread.currentThread());
        }

        float set_temp = 10;
        String mode = CommonData.MODE_TYPE_COOL;
        String fan_speed = CommonData.AC_FAN_SPPED_LOW;
        String status = "on";
        if (conditioner.set_temp > 0) set_temp = conditioner.set_temp;
        if (conditioner.mode.length() > 0) mode = conditioner.mode;
        if (conditioner.fan_speed.length() > 0) fan_speed = conditioner.fan_speed;
        if (!conditioner.status) status = "off";


        if (loop instanceof BacnetLoop) {
            BacnetLoop bacnetLoop = (BacnetLoop) loop;
            Map<String, Object> controlMap = new HashMap<>();
            controlMap.put("deviceid", "" + bacnetLoop.mSubDevId);
            controlMap.put("loopid", "" + bacnetLoop.mLoopSelfPrimaryId);
            controlMap.put("bacnetdeviceid", device.mBacnetId);
            controlMap.put("settemp", "" + set_temp);
            controlMap.put("mode", mode);
            controlMap.put("fanspeed", fan_speed);
            controlMap.put("status", status);

            ArrayList<Map<String, Object>> list = new ArrayList<>();
            list.add(controlMap);
            //发送命令
            String message = MessageManager.getInstance(context).sendDeviceStatus(list, "bacnet", null);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        } else if (loop instanceof Wifi485Loop) {
            Wifi485Loop wifi485Loop = (Wifi485Loop) loop;
            Map<String, Object> controlMap = new HashMap<>();
            controlMap.put("modulemacaddr", device.mMacAddr);
            controlMap.put("portid", "" + wifi485Loop.mPortId);
            controlMap.put("slaveaddr", "" + wifi485Loop.mSlaveAddr);
            controlMap.put("loopid", "" + wifi485Loop.mLoopId);
            controlMap.put("looptype", wifi485Loop.mLoopType);
            controlMap.put("brandname", wifi485Loop.mBrandName);
            controlMap.put("settemp", "" + set_temp);
            controlMap.put("mode", mode);
            controlMap.put("fanspeed", fan_speed);
            controlMap.put("status", status);
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            list.add(controlMap);
            //发送命令
            String message = MessageManager.getInstance(context).sendDeviceStatus(list, "485", null);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }

}
