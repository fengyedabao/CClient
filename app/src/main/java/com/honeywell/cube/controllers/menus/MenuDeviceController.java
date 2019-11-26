package com.honeywell.cube.controllers.menus;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceLoopObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceVentilationObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuIRCode;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
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
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRuleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/10. 15:02
 * Email:Shodong.Sun@honeywell.com
 * 侧边栏 Device列表
 */
public class MenuDeviceController {

    private static final String TAG = MenuDeviceController.class.getSimpleName();

    /**
     * 侧边栏进入后获取设备列表
     * 列表参数是否需要传入数组，待商榷
     *
     * @param context
     */
    public static void getAllDeviceList(Context context) {
        ArrayList<MenuDeviceUIItem> arrayList = new ArrayList<>();
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)){
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.MENU_GET_ALL_DEVICE_LIST, arrayList));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD){
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1){
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.MENU_GET_ALL_DEVICE_LIST, arrayList));
                return;
            }
        }

        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("DeviceCategory.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            String title = item.get("title");
            ArrayList<Object> deviceArr = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, title);
            if (deviceArr.size() > 0) {
                MenuDeviceUIItem items1 = new MenuDeviceUIItem(deviceArr, title);
                arrayList.add(items1);
            }
        }
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.MENU_GET_ALL_DEVICE_LIST, arrayList));
    }

    /**
     * 根据设备类型 获取设备列表
     *
     * @param context
     * @param deviceType
     */
    public static void getDeviceListWithType(Context context, String deviceType) {
        ArrayList<Object> deviceArr = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, deviceType);
        ArrayList<MenuDeviceUIItem> returnValue = new ArrayList<>();
        if (deviceArr.size() > 0) {
            for (int i = 0; i < deviceArr.size(); i++) {
                Object object = deviceArr.get(i);
                String deviceName = DeviceManager.getDeviceTitleWithObject(context, object);
                MenuDeviceUIItem items = new MenuDeviceUIItem(object, deviceType, deviceName);
                returnValue.add(items);
            }
        }
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.MENU_GET_DEVICE_WITH_TYPE, returnValue));
    }

    /**
     * 编辑设备， 获取页面对应的房间名和名称
     *
     * @param object
     * @return Map参数
     */
    public static final String CELL_NAME = "name";
    public static final String CELL_ROOM = "room";

    public static Map<String, Object> getDeviceRoomAndNameInfo(Context context, Object object) {
        if (object == null) {
            Loger.print(TAG, "ssd getDeviceRoomAndNameInfo object is null", Thread.currentThread());
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        String name = "";
        String room = "";
        RoomLoopFunc roomLoopFunc = RoomLoopFunc.getInstance(context);
        if (object instanceof SparkLightingLoop) {
            SparkLightingLoop loop = (SparkLightingLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof Wireless315M433MLoop) {
            Wireless315M433MLoop loop = (Wireless315M433MLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof RelayLoop) {
            RelayLoop loop = (RelayLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof WiredZoneLoop) {
            WiredZoneLoop loop = (WiredZoneLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof IpvdpZoneLoop) {
            IpvdpZoneLoop loop = (IpvdpZoneLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof BacnetLoop) {
            BacnetLoop loop = (BacnetLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof Wifi485Loop) {
            Wifi485Loop loop = (Wifi485Loop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof BackaudioLoop) {
            BackaudioLoop loop = (BackaudioLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof IpcStreamInfo) {
            IpcStreamInfo info = (IpcStreamInfo) object;
            PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(info.mDevId);
            name = device.mName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(info.mRoomId).mRoomName;
        } else if (object instanceof IrLoop) {
            IrLoop loop = (IrLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        } else if (object instanceof VentilationLoop) {
            VentilationLoop loop = (VentilationLoop) object;
            name = loop.mLoopName;
            room = roomLoopFunc.getRoomLoopByPrimaryId(loop.mRoomId).mRoomName;
        }
        map.put(CELL_NAME, name);
        map.put(CELL_ROOM, room);
        return map;
    }

    /**
     * 设备 删除
     *
     * @param context
     * @param object
     */
    public static void deleteDevice(Context context, Object object) {
        UIItems items = new UIItems();
        items.object = object;
        DeviceController.changeDeviceStatusWithBody(context, items, ModelEnum.CHANGE_DEVICE_STATUS_DELETE, -1, null);
    }


    /**
     * 编辑 设备
     *
     * @param context
     * @param object
     * @param name
     * @param room
     */
    public static void modifyDevice(Context context, Object object, String name, int room) {
        Loger.print(TAG, "ssd modify device : name " + name + " room : " + room, Thread.currentThread());
        UIItems items = new UIItems();
        items.object = object;
        DeviceController.changeDeviceStatusWithBody(context, items, ModelEnum.CHANGE_DEVICE_STATUS_MODIFY, room, name);
    }

    /*******************               添加设备部分                ***********************/
    /**
     * 获取添加设备的列表
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getAddDeviceList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.menu_device_add_device_scan));
        returnValue.add(context.getString(R.string.menu_device_add_spark_lighting));
        returnValue.add(context.getString(R.string.menu_device_add_wifi_ir));
        returnValue.add(context.getString(R.string.menu_device_add_wifi_relay));
        returnValue.add(context.getString(R.string.menu_device_add_wired_zone));
        returnValue.add(context.getString(R.string.menu_device_add_bacnet_ac));
        returnValue.add(context.getString(R.string.menu_device_add_ip_camera));
        returnValue.add(context.getString(R.string.menu_device_add_ipvdp_zone));
        returnValue.add(context.getString(R.string.menu_device_add_backaudio));
        returnValue.add(context.getString(R.string.menu_device_add_485_device));
        returnValue.add(context.getString(R.string.menu_device_add_ventilation));
        return returnValue;
    }


    /**
     * 添加 SparkLighting 返回界面使用的数据 一级界面，显示所有的SparkLighting
     *
     * @return
     */
    public static ArrayList<MenuDeviceUIItem> getSparkLightingList() {
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("SparkType.plist");
        ArrayList<MenuDeviceUIItem> returnValue = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            String title = item.get("title");
            String type = item.get("type");
            String loop = item.get("loop");
            String detail = item.get("detail");
            MenuDeviceUIItem items1 = new MenuDeviceUIItem(title, detail, loop, type);
            returnValue.add(items1);
        }
        return returnValue;
    }

    /**
     * Spark lighting 根据一级页面的Items来获取第二级页面需要的Items
     *
     * @param context
     * @param sparkLoop
     * @param sparkTitle---需要是本地化后的数据
     * @param sparkType
     * @return
     */
    public static MenuDeviceUIItem getSparkLightingDetails(Context context, int sparkLoop, String sparkTitle, String sparkType) {
        ArrayList<PeripheralDevice> devices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_SPARKLIGHTING);
        PeripheralDevice mainDevice = null;
        String title = "";
        if (devices.size() > 0) {
            Loger.print(TAG, "ssd get sparklighting periphera is " + devices.size(), Thread.currentThread());
            mainDevice = devices.get(0);
            title = mainDevice.mName;
        }
        if (sparkLoop == 1) {
            //只有一个回路
            MenuDeviceUIItem items = new MenuDeviceUIItem();
            items.sparkTitle = sparkTitle;

            //device
            items.sparkDeviceId = 1;
            items.peripheraDevice = mainDevice;
            items.deviceName = title;
            //loop
            ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
            MenuDeviceLoopObject loopObject = new MenuDeviceLoopObject();
            loopObject.name = sparkType;
            //room
            RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
            loopObject.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
            loopObject.roomId = loop == null ? -1 : loop.mPrimaryId;

            loopObject.loopType = sparkType;
            loopObject.loopid = 1;
            list.add(loopObject);
            items.loopObjects = list;
            return items;
        } else {
            //loop 数超过1
            MenuDeviceUIItem items = new MenuDeviceUIItem();
            items.sparkTitle = sparkTitle;

            //device
            items.sparkDeviceId = 1;
            items.peripheraDevice = mainDevice;
            items.deviceName = title;

            //loop
            int loopCount = sparkLoop;
            ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
            for (int i = 0; i < loopCount; i++) {
                MenuDeviceLoopObject object = new MenuDeviceLoopObject();
                object.section = context.getString(R.string.main_loop) + " " + (i + 1);
                object.loopid = i + 1;

                //room
                RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
                object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
                object.roomId = loop == null ? -1 : loop.mPrimaryId;
                object.name = "" + sparkType + " " + (i + 1);
                object.loopType = sparkType;
                if (sparkType.equalsIgnoreCase("relay")) {
                    object.loopType = ModelEnum.RELAYTYPE_RELAY;
                }
                object.enable = false;

                list.add(object);
            }
            items.loopObjects = list;
            return items;
        }
    }

    /**
     * 改变 SparkLighting 的状态  这个用于第二级页面添加Spark lighting 编辑第二级页面
     * 单路 多路设计
     *
     * @param items
     */
    public static void addSparkLightingLoop(Context context, MenuDeviceUIItem items) {
        if (items == null) {
            Loger.print(TAG, "ssd updateSparkLightingState parameter is null", Thread.currentThread());
            return;
        }
        if (items.peripheraDevice == null) {
            Loger.print(TAG, "ssd updateSparkLightingState periphera device is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, context.getString(R.string.device_need_module)));
            return;
        }
        ArrayList<MenuDeviceLoopObject> loopObjects = items.loopObjects;
        if (loopObjects.size() == 1) {
            //单回路
            MenuDeviceLoopObject object = loopObjects.get(0);
            if (object == null) {
                Loger.print(TAG, "ssd updateSparkLightingState loop object is null", Thread.currentThread());
                return;
            }
            String looptype = object.loopType;
            int loopid = items.sparkDeviceId;
            int roomid = object.roomId;
            String name = object.name;
            String subdevtype = items.sparkTitle;

            Map<String, Object> deviceloop = new HashMap();
            deviceloop.put("loopid", loopid);
            deviceloop.put("roomid", roomid);
            deviceloop.put("aliasname", name);
            deviceloop.put("looptype", looptype);

            ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
            deviceloopmap.add(deviceloop);

            String message = MessageManager.getInstance(context).modifySparkLight(deviceloopmap, false, items.sparkDeviceId, subdevtype, items.peripheraDevice.mPrimaryID);
            //发送数据
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        } else {
            //多路设计
            ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
            for (int i = 0; i < loopObjects.size(); i++) {
                MenuDeviceLoopObject object = loopObjects.get(i);
                if (object.enable) {
                    String loopTypeStr = object.loopType;
                    Map<String, Object> map = new HashMap<>();
                    map.put("loopid", object.loopid);
                    map.put("roomid", object.roomId);
                    map.put("aliasname", object.name);
                    map.put("looptype", loopTypeStr);
                    deviceloopmap.add(map);
                }
            }
            //至少选择一个设备
            if (deviceloopmap.size() == 0) {
                Loger.print(TAG, "ssd deviceloopmap size is 0", Thread.currentThread());
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, "至少选择一个回路!"));
                return;
            }
            String subdevtype = items.sparkTitle;
            String message = MessageManager.getInstance(context).modifySparkLight(deviceloopmap, false, items.sparkDeviceId, subdevtype, items.peripheraDevice.mPrimaryID);
            //发送数据
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }


    /*******   无线开关  **********/

    /**
     * 获取Relay页面需求参数
     *
     * @param context
     * @return
     */
    private static final int RELAY_LOOP_COUNT = 4;

    public static MenuDeviceUIItem getRelayDetails(Context context) {
        MenuDeviceUIItem deviceUIItem = new MenuDeviceUIItem();
        ArrayList<PeripheralDevice> devices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_WIFIRELAY);
        if (devices.size() == 0) {
            Loger.print(TAG, "ssd 当前没有Relay模块", Thread.currentThread());
            deviceUIItem.deviceName = "";
            deviceUIItem.peripheraDevice = null;
        } else {
            deviceUIItem.deviceName = devices.get(0).mName;
            deviceUIItem.peripheraDevice = devices.get(0);
        }

        //loop
        ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
        for (int i = 0; i < RELAY_LOOP_COUNT; i++) {
            MenuDeviceLoopObject object = new MenuDeviceLoopObject();
            object.section = context.getString(R.string.main_loop) + " " + (i + 1);
            object.loopid = i + 1;
            RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
            object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
            object.roomId = loop == null ? -1 : loop.mPrimaryId;
            object.name = context.getString(R.string.main_relay) + " " + (i + 1);
            object.isDelay = false;
            object.delaytime = 0;
            object.enable = false;
            list.add(object);
        }
        deviceUIItem.loopObjects = list;
        return deviceUIItem;
    }

    /**
     * 添加 relay loop
     *
     * @param context
     * @param uiItem
     */
    public static void addRelayLoop(Context context, MenuDeviceUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd add relay loop parameter is null", Thread.currentThread());
            return;
        }
        PeripheralDevice device = uiItem.peripheraDevice;
        if (device == null) {
            Loger.print(TAG, "ssd add relay loop parameter object is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_RELAY, context.getString(R.string.device_need_module)));
            return;
        }
        ArrayList<MenuDeviceLoopObject> loopObjects = uiItem.loopObjects;
        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
        for (MenuDeviceLoopObject object : loopObjects) {
            if (object.enable) {
                Map<String, Object> item = new HashMap<>();
                item.put("loopid", "" + object.loopid);
                item.put("roomid", object.roomId);
                item.put("aliasname", object.name);
                item.put("time", "" + object.delaytime);
                deviceloopmap.add(item);
            }
        }
        if (deviceloopmap.size() == 0) {
            Loger.print(TAG, "ssd add relay loopmap count could not be 0", Thread.currentThread());
            return;
        }

        String message = MessageManager.getInstance(context).modifyRelayLoopState(deviceloopmap, false, device.mPrimaryID);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /*******   有线开关  **********/

    /**
     * 获取 wired zone 页面需求参数
     *
     * @param context
     * @return
     */
    private static final int ZONE_LOOP_COUNT = 8;

    public static MenuDeviceUIItem getWiredZoneDetails(Context context) {
        MenuDeviceUIItem deviceUIItem = new MenuDeviceUIItem();
        ArrayList<PeripheralDevice> devices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_WIREDZONE);
        if (devices.size() == 0) {
            Loger.print(TAG, "ssd 当前没有zone模块", Thread.currentThread());
            deviceUIItem.deviceName = "";
            deviceUIItem.peripheraDevice = null;
        } else {
            deviceUIItem.deviceName = devices.get(0).mName;
            deviceUIItem.peripheraDevice = devices.get(0);
        }

        //loop
        ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
        for (int i = 0; i < ZONE_LOOP_COUNT; i++) {
            MenuDeviceLoopObject object = new MenuDeviceLoopObject();
            object.section = context.getString(R.string.main_loop) + " " + (i + 1);
            object.loopid = i + 1;
            RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
            object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
            object.roomId = loop == null ? -1 : loop.mPrimaryId;
            object.name = context.getString(R.string.main_zone) + " " + (i + 1);
            object.alarmType = CommonData.ZONE_ALARM_STATUS_THIEF;
            object.zoneType = CommonData.ZONE_TYPE_SECURITY_INSTANT;
            object.delaytime = 0;
            object.enable = false;
            list.add(object);
        }
        deviceUIItem.loopObjects = list;
        return deviceUIItem;
    }

    /**
     * 获取报警类型
     *
     * @return
     */
    public static ArrayList<String> getZoneAlarmList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.zone_alarm_fire));
        returnValue.add(context.getString(R.string.zone_alarm_help));
        returnValue.add(context.getString(R.string.zone_alarm_gas));
        returnValue.add(context.getString(R.string.zone_alarm_thief));
        returnValue.add(context.getString(R.string.zone_alarm_emergency));
        return returnValue;
    }

    /**
     * 获取防区类型
     *
     * @return
     */
    public static ArrayList<String> getZoneTypeList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(CommonUtils.transferProtocolToName(context, CommonData.ZONE_TYPE_SECURITY_INSTANT));
        returnValue.add(CommonUtils.transferProtocolToName(context, CommonData.ZONE_TYPE_SECURITY_DELAY));
        returnValue.add(CommonUtils.transferProtocolToName(context, CommonData.ZONE_TYPE_SECURITY_24HOURS));
        return returnValue;
    }


    /**
     * 防区类型和报警类型的关系
     * fire---24hours
     * help---24hours
     * gas----24hours
     * thief--对应3个
     * emergency ---24hours
     */
    /**
     * 判断防区类型是否可编辑
     *
     * @param alarmType
     * @return
     */
    public static boolean judgeZoneTypeEnable(String alarmType) {
        if (CommonData.ZONE_ALARM_STATUS_THIEF.equalsIgnoreCase(alarmType)) {
            return true;
        }
        return false;
    }

    /**
     * 添加 wired zone loop
     *
     * @param context
     * @param uiItem
     */
    public static void addWiredZoneLoop(Context context, MenuDeviceUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd add wired zone loop parameter is null", Thread.currentThread());
            return;
        }
        PeripheralDevice device = uiItem.peripheraDevice;
        if (device == null) {
            Loger.print(TAG, "ssd add wired zone loop parameter object is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, context.getString(R.string.device_need_module)));
            return;
        }

        ArrayList<MenuDeviceLoopObject> loopObjects = uiItem.loopObjects;
        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
        for (MenuDeviceLoopObject object : loopObjects) {
            if (object.enable) {
                Map<String, Object> item = new HashMap<>();
                item.put("loopid", "" + object.loopid);
                item.put("roomid", object.roomId);
                item.put("aliasname", object.name);
                item.put("zonetype", object.zoneType);
                item.put("alarmtype", object.alarmType);
                item.put("alarmtimer", object.delaytime);
                item.put("alarmenable", "on");
                deviceloopmap.add(item);
            }
        }
        if (deviceloopmap.size() == 0) {
            Loger.print(TAG, "ssd add wired zone loopmap count could not be 0", Thread.currentThread());
            return;
        }

        String message = MessageManager.getInstance(context).modifyWiredZoneLoopState(deviceloopmap, false, device.mPrimaryID);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /********* IP camera **********/

    /**
     * 获取IPC界面信息
     *
     * @param context
     * @return
     */
    public static MenuDeviceUIItem getIpcamera(Context context) {
        MenuDeviceUIItem item = new MenuDeviceUIItem();
        item.IPC_Ip = "";
        item.IPC_type = CommonData.IPCAMERA_TYPE_PHOENIX;
        RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
        item.IPC_Room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
        item.IPC_Roomid = loop == null ? -1 : loop.mPrimaryId;
        item.IPC_Name = "";
        item.IPC_User = "admin";
        item.IPC_Password = "123456";
        return item;
    }

    /**
     * 返回 IPC 获取 类型数组
     *
     * @return
     */
    public static ArrayList<String> getIpcTypeList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.device_ipc_phoenix));
        returnValue.add(context.getString(R.string.device_ipc_super_hd));
        return returnValue;
    }

    /**
     * 添加 IPC
     *
     * @param context
     * @param uiItem
     */
    public static void addIpcamera(Context context, MenuDeviceUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd add ipcamera parameter is null", Thread.currentThread());
            return;
        }
        String mainStream = NetConstant.IPC_MAIN_STREAM;
        String subStream = NetConstant.IPC_SUB_STREAM;

        if (CommonData.IPCAMERA_TYPE_PHOENIX.equalsIgnoreCase(uiItem.IPC_type)) {
            mainStream = NetConstant.IPC_STREAM_PHOENIX;
            subStream = NetConstant.IPC_SUB_STREAM_PHOENIX;
        }
        String message = MessageManager.getInstance(context).modifyIpcameraState(false, uiItem.IPC_Ip, NetConstant.IPC_PORT, CommonUtils.transferIpcType(context, uiItem.IPC_type),
                mainStream, subStream, uiItem.IPC_User, uiItem.IPC_Password, uiItem.IPC_Roomid, uiItem.IPC_Name);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /********************** IPVDP zone ********************/
    /**
     * 获取 IPVDP zone 页面需求参数
     * <p/>
     * 要求如下
     * i.	B1~B6 对应6个防区，其loopid依次为1~6；
     * ii.	E1,E2 共用Emergency1报警，其loopid为7；
     * iii.	求助触摸按键对应Emergency2报警，其loopid为8；
     * iv.	Fire对应火灾报警，其loopid为9；
     * v.	Gas对应煤气报警，其loopid为10；
     *
     * @param context
     * @return
     */
    private static final int IPVDP_LOOP_COUNT = 10;

    public static MenuDeviceUIItem getIPVDPZoneDetails(Context context) {
        MenuDeviceUIItem deviceUIItem = new MenuDeviceUIItem();
        ArrayList<PeripheralDevice> devices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_IPVDP);
        if (devices.size() == 0) {
            Loger.print(TAG, "ssd 当前没有zone模块", Thread.currentThread());
            deviceUIItem.deviceName = "";
            deviceUIItem.peripheraDevice = null;
        } else {
            deviceUIItem.deviceName = devices.get(0).mName;
            deviceUIItem.peripheraDevice = devices.get(0);
        }

        //loop
        ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
        for (int i = 0; i < IPVDP_LOOP_COUNT; i++) {
            MenuDeviceLoopObject object = new MenuDeviceLoopObject();
            object.section = context.getString(R.string.main_loop) + " " + (i + 1);
            object.loopid = i + 1;
            RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
            object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
            object.roomId = loop == null ? -1 : loop.mPrimaryId;
            object.name = context.getString(R.string.main_zone) + " " + (i + 1);
            object.isEdit = false;
            object.zoneType = CommonData.ZONE_TYPE_SECURITY_24HOURS;
            object.enable = false;

            switch (i) {
                case 6: {
                    //emergency
                    object.alarmType = CommonData.ZONE_ALARM_STATUS_EMERGENCY;
                }
                break;
                case 7: {
                    //Help
                    object.alarmType = CommonData.ZONE_ALARM_STATUS_HELP;
                    object.enable = true;
                }
                break;
                case 8: {
                    //火灾
                    object.alarmType = CommonData.ZONE_ALARM_STATUS_FIRE;
                }
                break;
                case 9: {
                    //煤气
                    object.alarmType = CommonData.ZONE_ALARM_STATUS_GAS;
                }
                break;
                default: {
                    object.alarmType = CommonData.ZONE_ALARM_STATUS_THIEF;
                    object.zoneType = CommonData.ZONE_TYPE_SECURITY_INSTANT;
                    object.delaytime = 0;
                    object.enable = false;
                    object.isEdit = true;
                }
            }
            list.add(object);
        }
        deviceUIItem.loopObjects = list;
        return deviceUIItem;
    }

    /**
     * 添加 IPVDP
     *
     * @param context
     * @param uiItem
     */
    public static void addIPVDPZone(Context context, MenuDeviceUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd add ipvdp zone loop parameter is null", Thread.currentThread());
            return;
        }
        PeripheralDevice device = uiItem.peripheraDevice;
        if (device == null) {
            Loger.print(TAG, "ssd add ipvdp zone loop parameter object is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_IPVDP, context.getString(R.string.device_need_module)));
            return;
        }

        ArrayList<MenuDeviceLoopObject> loopObjects = uiItem.loopObjects;
        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
        for (MenuDeviceLoopObject object : loopObjects) {
            if (object.enable) {
                Map<String, Object> item = new HashMap<>();
                item.put("loopid", "" + object.loopid);
                item.put("roomid", object.roomId);
                item.put("aliasname", object.name);
                item.put("zonetype", object.zoneType);
                item.put("alarmtype", object.alarmType);
                item.put("alarmtimer", object.delaytime);
                item.put("alarmenable", "on");
                deviceloopmap.add(item);
            }
        }
        if (deviceloopmap.size() == 0) {
            Loger.print(TAG, "ssd add ipvdp zone loopmap count could not be 0", Thread.currentThread());
            return;
        }

        String message = MessageManager.getInstance(context).modifyIPVDPZoneLoopState(deviceloopmap, false, device.mPrimaryID);
        //发送数据
        Loger.print(TAG, "ssd add ipvdp", Thread.currentThread());
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /******
     * back audio
     ***********/

    public static final int BACKAUDIO_LOOP_COUNT = 8;

    /**
     * 获取 back audio 界面信息
     *
     * @param context
     * @return
     */
    public static MenuDeviceUIItem getBackaudioDetails(Context context) {
        MenuDeviceUIItem deviceUIItem = new MenuDeviceUIItem();
        ArrayList<BackaudioDevice> devices = (ArrayList<BackaudioDevice>) new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceAllList();
        if (devices.size() == 0) {
            Loger.print(TAG, "ssd 当前没有backAudio模块", Thread.currentThread());
            deviceUIItem.deviceName = "";
            deviceUIItem.backaudioDevice = null;
        } else {
            deviceUIItem.deviceName = devices.get(0).mName;
            deviceUIItem.backaudioDevice = devices.get(0);
        }

        //loop
        ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
        for (int i = 0; i < BACKAUDIO_LOOP_COUNT; i++) {
            MenuDeviceLoopObject object = new MenuDeviceLoopObject();
            object.section = context.getString(R.string.main_loop) + " " + (i + 1);
            object.loopid = i + 1;
            RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
            object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
            object.roomId = loop == null ? -1 : loop.mPrimaryId;
            object.name = context.getString(R.string.main_back_audio) + " " + (i + 1);
            object.enable = false;
            list.add(object);
        }
        deviceUIItem.loopObjects = list;
        return deviceUIItem;
    }

    /**
     * 添加音乐
     *
     * @param context
     * @param uiItem
     */
    public static void addBackaudio(Context context, MenuDeviceUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd add back audio loop parameter is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "参数错误"));
            return;
        }
        BackaudioDevice device = uiItem.backaudioDevice;
        if (device == null) {
            Loger.print(TAG, "ssd add back audio loop parameter object is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "主设备不存在"));
            return;
        }

        ArrayList<MenuDeviceLoopObject> loopObjects = uiItem.loopObjects;
        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
        for (MenuDeviceLoopObject object : loopObjects) {
            if (object.enable) {
                Map<String, Object> item = new HashMap<>();
                item.put("loopid", "" + object.loopid);
                item.put("roomid", object.roomId);
                item.put("aliasname", object.name);
                deviceloopmap.add(item);
            }
        }
        if (deviceloopmap.size() == 0) {
            Loger.print(TAG, "ssd add ipvdp zone loopmap count could not be 0", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "参数错误"));
            return;
        }
        String message = MessageManager.getInstance(context).modifyBackaudioLoopState(deviceloopmap, false, device.mPrimaryID);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /****** Bacnet ******/

    /**
     * 获取Bacnet初始值
     *
     * @param context
     * @return
     */
    public static MenuDeviceUIItem getBacnectLoopDetails(Context context) {
        MenuDeviceUIItem deviceUIItem = new MenuDeviceUIItem();
        ArrayList<PeripheralDevice> devices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_BACNET);
        if (devices.size() == 0) {
            Loger.print(TAG, "ssd 当前没有backAudio模块", Thread.currentThread());
            deviceUIItem.deviceName = "";
            deviceUIItem.peripheraDevice = null;
        } else {
            deviceUIItem.deviceName = devices.get(0).mName;
            deviceUIItem.peripheraDevice = devices.get(0);
        }

        //loop
        ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
        MenuDeviceLoopObject object = new MenuDeviceLoopObject();
        object.section = context.getString(R.string.main_air_conditioner) + " " + (1);
        object.loopid = 1;
        RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
        object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
        object.roomId = loop == null ? -1 : loop.mPrimaryId;
        object.name = context.getString(R.string.main_air_conditioner) + " " + (1);
        object.bacnet_device_id = 0;
        object.bacnet_loop_id = 0;
        list.add(object);
        deviceUIItem.loopObjects = list;
        return deviceUIItem;
    }

    /**
     * 添加 获取一个Loop object
     *
     * @param context
     * @param lastObject
     * @return
     */
    public static MenuDeviceLoopObject getDefaultBacnetLoopObject(Context context, MenuDeviceLoopObject lastObject) {
        MenuDeviceLoopObject object = new MenuDeviceLoopObject();
        int loopid = (lastObject == null ? 1 : (lastObject.loopid + 1));
        object.section = context.getString(R.string.main_air_conditioner) + " " + (loopid);
        object.loopid = loopid;

        RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
        object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
        object.roomId = loop == null ? -1 : loop.mPrimaryId;
        object.name = context.getString(R.string.main_air_conditioner) + " " + (loopid);
        object.bacnet_device_id = 0;
        object.bacnet_loop_id = 0;
        return object;
    }


    /**
     * Bacnet 完成 添加设备
     *
     * @param context
     * @param uiItem
     */
    public static void addBacnet(Context context, MenuDeviceUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd add bacnet 参数错误", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACNET, "参数错误"));
            return;
        }
        ArrayList<MenuDeviceLoopObject> loopObjects = uiItem.loopObjects;
        if (loopObjects.size() < 1) {
            Loger.print(TAG, "ssd add bacnet loop object 参数错误", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACNET, "参数错误"));
            return;
        }

        PeripheralDevice device = uiItem.peripheraDevice;
        if (device == null) {
            Loger.print(TAG, "ssd add bacnet loop parameter object is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACNET, "主设备不存在"));
            return;
        }
        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();

        for (MenuDeviceLoopObject object : loopObjects) {
            Map<String, Object> item = new HashMap<>();
            item.put("loopid", "" + object.bacnet_loop_id);
            item.put("deviceid", "" + object.bacnet_device_id);
            item.put("roomid", object.roomId);
            item.put("aliasname", object.name);
            item.put("looptype", "aircondtion");
            item.put("brandname", device.mBrandName);
            deviceloopmap.add(item);
        }
        String message = MessageManager.getInstance(context).modifyBacnetLoopState(deviceloopmap, false, device.mPrimaryID);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /*******
     * 485 device
     ******/

    /**
     * 获取Wi-Fi485界面细节
     *
     * @param context
     * @return
     */
    public static MenuDeviceUIItem getWifi485Details(Context context) {
        MenuDeviceUIItem deviceUIItem = new MenuDeviceUIItem();
        ArrayList<PeripheralDevice> devices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_WIFI485);
        if (devices.size() == 0) {
            Loger.print(TAG, "ssd 当前没有 Wifi 485 模块", Thread.currentThread());
            deviceUIItem.deviceName = "";
            deviceUIItem.peripheraDevice = null;
        } else {
            deviceUIItem.deviceName = devices.get(0).mName;
            deviceUIItem.peripheraDevice = devices.get(0);
        }
        //loop
        ArrayList<MenuDeviceLoopObject> list = new ArrayList<>();
        MenuDeviceLoopObject object = new MenuDeviceLoopObject();
        object.section = context.getString(R.string.main_485_device) + " " + (1);
        object.loopid = 1;
        RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
        object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
        object.roomId = loop == null ? -1 : loop.mPrimaryId;
        object.name = context.getString(R.string.main_485_device) + " " + (1);
        object.loopType = CommonData.DEVICETYPE_AIRCONDITION;
        object.wifi485_branchName = CommonData.WIFI485_AC_TYPE_ECC_O1;
        object.wifi485_port_id = 1;
        object.wifi485_slave_address = "";
        object.wifi485_loop_id = 0;
        list.add(object);
        deviceUIItem.loopObjects = list;
        return deviceUIItem;
    }

    /**
     * 添加LoopObject时，返回默认的LoopObject
     *
     * @param context
     * @param lastObject
     * @return
     */
    public static MenuDeviceLoopObject getDefaultWifi485LoopObject(Context context, MenuDeviceLoopObject lastObject) {
        int loopid = 1;
        if (lastObject != null) {
            loopid = lastObject.loopid + 1;
        }
        MenuDeviceLoopObject object = new MenuDeviceLoopObject();
        object.section = context.getString(R.string.main_485_device) + " " + (loopid);
        object.loopid = loopid;
        RoomLoop loop = CommonCache.getRoomListLoop(context).size() == 0 ? null : CommonCache.getRoomListLoop(context).get(0);
        object.room = loop == null ? "" : ("".equalsIgnoreCase(RoomManager.transferRoomStr(context, loop.mImageName)) ? loop.mRoomName : RoomManager.transferRoomStr(context, loop.mImageName));
        object.roomId = loop == null ? -1 : loop.mPrimaryId;
        object.name = context.getString(R.string.main_485_device) + " " + (loopid);
        object.loopType = CommonData.DEVICETYPE_AIRCONDITION;
        object.wifi485_branchName = CommonData.WIFI485_AC_TYPE_ECC_O1;
        object.wifi485_port_id = 1;
        object.wifi485_slave_address = "";
        object.wifi485_loop_id = 0;
        return object;
    }

    /**
     * 返回Wi-Fi485 loop type
     *
     * @return
     */
    public static ArrayList<String> getWifi485LooptypeList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.device_aircondition));
        returnValue.add(context.getString(R.string.device_thermostat));
        returnValue.add(context.getString(R.string.device_ventilation));
        return returnValue;
    }

    /**
     * 获取 Wi-Fi485 branch name
     *
     * @return
     */
    public static ArrayList<String> getWifi485BranchNameList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.device_ecc_o1));
        returnValue.add(context.getString(R.string.device_htc961d3200));
        returnValue.add(context.getString(R.string.device_ecc_dt300));
        return returnValue;
    }

    /**
     * 添加 485 模块数据
     *
     * @param context
     * @param uiItem
     */
    public static void addWifi485(Context context, MenuDeviceUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd add wifi 485 参数错误", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_485, "参数错误"));
            return;
        }
        ArrayList<MenuDeviceLoopObject> loopObjects = uiItem.loopObjects;
        if (loopObjects.size() < 1) {
            Loger.print(TAG, "ssd add wifi 485 loop object 参数错误", Thread.currentThread());
            return;
        }

        PeripheralDevice device = uiItem.peripheraDevice;
        if (device == null) {
            Loger.print(TAG, "ssd add wifi 485 loop parameter object is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_485, context.getString(R.string.device_need_module)));
            return;
        }
        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();

        for (MenuDeviceLoopObject object : loopObjects) {
            Map<String, Object> item = new HashMap<>();
            item.put("portid", "" + object.wifi485_port_id);
            item.put("loopid", "" + object.wifi485_loop_id);
            item.put("roomid", object.roomId);
            item.put("aliasname", object.name);
            item.put("looptype", CommonUtils.transferWifi485LoopType(context, object.loopType));
            item.put("brandname", CommonUtils.transferWifi485BranchName(context, object.wifi485_branchName));
            item.put("slaveaddr", object.wifi485_slave_address);

            deviceloopmap.add(item);
        }

        String message = MessageManager.getInstance(context).modifyWifi485LoopState(deviceloopmap, false, device.mPrimaryID);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /******************** 添加红外设备 *****************/
    /**
     * 获取配置Ir 首页列表
     *
     * @param context
     * @return
     */
    public static ArrayList<MenuDeviceIRUIItem> getMenuIrList(Context context) {
        ArrayList<MenuDeviceIRUIItem> arrayList = new ArrayList<>();
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("IRCategory.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            MenuDeviceIRUIItem uiItem = new MenuDeviceIRUIItem();
            uiItem.IR_name = DeviceManager.transferIrName(context, item.get("title"));
            uiItem.IR_image = DeviceManager.transferIrImagename(item.get("image"));
            uiItem.IR_type = item.get("type");
            arrayList.add(uiItem);
        }
        return arrayList;
    }

    /**
     * 获取 二级页面中 默认的参数
     *
     * @param context
     * @param menuDeviceIRUIItem
     * @return
     */
    public static MenuDeviceIRUIItem getMenuIrDefaultSecondItem(Context context, MenuDeviceIRUIItem menuDeviceIRUIItem) {
        if (menuDeviceIRUIItem == null) {
            return new MenuDeviceIRUIItem();
        }
        //房间
        ArrayList<RoomLoop> roomLoops = CommonCache.getRoomListLoop(context);
        if (roomLoops.size() > 0) {
            menuDeviceIRUIItem.IR_room_name = CommonCache.getRoomNameList(context).get(1);
            menuDeviceIRUIItem.IR_room_id = CommonCache.getRoomIdList(context).get(1);
        }

        //Module
        ArrayList<PeripheralDevice> moduleDevices = MenuDeviceController.getPeripheraListWithType(context, ModelEnum.MODULE_TYPE_WIFIIR);
        if (moduleDevices.size() > 0) {
            menuDeviceIRUIItem.IR_module_device = moduleDevices.get(0);
            menuDeviceIRUIItem.IR_Module_name = moduleDevices.get(0).mName;
        }
        return menuDeviceIRUIItem;
    }

    /**
     * 第三级界面 更新数据 然后进行下一步操作
     *
     * @param menuDeviceIRUIItem
     * @return
     */
    public static MenuDeviceIRUIItem updateMenuDeviceIRRoom(MenuDeviceIRUIItem menuDeviceIRUIItem) {
        if (menuDeviceIRUIItem == null) {
            Loger.print(TAG, "ssd get Ir loop parameters is null", Thread.currentThread());
            return null;
        }
        //Ir loop
        IrLoop loop = new IrLoop();
        loop.mLoopName = menuDeviceIRUIItem.IR_name;
        loop.mRoomId = menuDeviceIRUIItem.IR_room_id;
        menuDeviceIRUIItem.IR_loop = loop;
        return menuDeviceIRUIItem;
    }

    /**
     * custom 获取IR 自定义界面 默认的数据列表
     *
     * @return
     */
    public static ArrayList<MenuDeviceIRIconItem> getDefaultIconList(Context context) {
        ArrayList<MenuDeviceIRIconItem> arrayList = new ArrayList<>();
        ArrayList<Object> items = PlistUtil.parseArrayPlistWithName("IRImageAndName.plist");
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> item = (Map<String, String>) items.get(i);
            String imageName = item.get("imagename");
            String name = item.get("name");
            if (ModelEnum.DEVICE_IR_ADD_CUSTOMIZE.equalsIgnoreCase(imageName)) continue;
            MenuDeviceIRIconItem uiItem = new MenuDeviceIRIconItem();
            uiItem.IR_icon_name = name;
            uiItem.IR_icon_imageName = imageName;
            DeviceManager.transferIRIconImage(item.get("image"), uiItem);
            arrayList.add(uiItem);
        }
        return arrayList;
    }

    /**
     * custom 按键学习 IR 红外 码值
     *
     * @param context
     * @param menuDeviceIRUIItem--只用到了一个参数，看需要传什么吧
     * @param iconItem
     */
    public static void studyIrCodeWithInfo(Context context, MenuDeviceIRUIItem menuDeviceIRUIItem, MenuDeviceIRIconItem iconItem) {
        String image_path = iconItem.IR_icon_imageName;
        String name = iconItem.IR_icon_name;
        PeripheralDevice device = menuDeviceIRUIItem.IR_module_device;
        if (device == null) {
            Loger.print(TAG, "ssd study code module is null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).studyIRCode(image_path, device.mMacAddr, name);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 添加 IR custom
     * 这个接口可用于自定义，DVD，STB，TV 空调
     *
     * @param context
     * @param menuDeviceIRUIItem
     */
    public static void addIRWithInfo(Context context, MenuDeviceIRUIItem menuDeviceIRUIItem) {
        ArrayList<MenuIRCode> codes = menuDeviceIRUIItem.IR_Code_List;
        if (codes == null || codes.size() == 0) {
            Loger.print(TAG, "ssd 学习的码值数量为0， 不符合要求", Thread.currentThread());
            return;
        }
        ArrayList<Map> codeList = new ArrayList<>();
        for (int i = 0; i < codes.size(); i++) {
            MenuIRCode menuIRCode = codes.get(i);
            ArrayList<Map> codeDataList = new ArrayList<>();
            for (int j = 0; j < menuIRCode.wifiirdata.size(); j++) {
                Map<String, Object> dataMap = new HashMap<>();
                String data = menuIRCode.wifiirdata.get(j);
                dataMap.put("data", data);
                codeDataList.add(dataMap);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("name", menuIRCode.name);
            map.put("imagename", menuIRCode.imagename);
            map.put("wifiirdata", codeDataList);
            codeList.add(map);
        }
        PeripheralDevice device = menuDeviceIRUIItem.IR_module_device;
        int primaryid = device.mPrimaryID;
        String type = menuDeviceIRUIItem.IR_type;
        String loopname = menuDeviceIRUIItem.IR_loop.mLoopName;
        int roomid = menuDeviceIRUIItem.IR_room_id;
        String message = MessageManager.getInstance(context).addIRCustom(primaryid, loopname, roomid, type, codeList);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 获取模式数组
     *
     * @return
     */
    public static ArrayList<String> getIR_AC_modeList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(CommonUtils.transferIrAcModeStr(context, CommonData.MODE_TYPE_COOL));
        returnValue.add(CommonUtils.transferIrAcModeStr(context, CommonData.MODE_TYPE_HEAT));
        returnValue.add(CommonUtils.transferIrAcModeStr(context, CommonData.MODE_TYPE_VENLITATION));
        returnValue.add(CommonUtils.transferIrAcModeStr(context, CommonData.MODE_TYPE_DEHUMIDIFY));
        returnValue.add(CommonUtils.transferIrAcModeStr(context, CommonData.MODE_TYPE_AUTO));
        return returnValue;
    }

    /**
     * 学习Air Controller
     *
     * @param context
     * @param menuDeviceIRUIItem
     */
    public static void studyAirControllerCodeWithInfo(Context context, MenuDeviceIRUIItem menuDeviceIRUIItem) {
        if (menuDeviceIRUIItem == null) {
            Loger.print(TAG, "ssd study air controller --- parameter is null", Thread.currentThread());
            return;
        }
        String ac_title = menuDeviceIRUIItem.IR_AC_name;
        String ac_details = menuDeviceIRUIItem.IR_AC_temperature + " " + menuDeviceIRUIItem.IR_AC_mode;
        String name = ac_title + "\n" + ac_details;
        PeripheralDevice device = menuDeviceIRUIItem.IR_module_device;
        if (device == null) {
            Loger.print(TAG, "ssd study air controller parameters is null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).studyIRCode("custom", device.mMacAddr, name);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /****************
     * 新风系统
     ******************/

    /**
     * 获取默认的新风界面参数
     *
     * @param context
     * @return
     */
    public static MenuDeviceVentilationObject getDefaultVentilationObject(Context context) {
        MenuDeviceVentilationObject ventilationObject = new MenuDeviceVentilationObject();
        ventilationObject.room = CommonCache.getRoomNameList(context).get(1);
        ventilationObject.roomId = CommonCache.getRoomIdList(context).get(1);
        return ventilationObject;
    }

    /**
     * 获取下面弹出窗 参数列表
     * 调取一次就可以了
     *
     * @param context
     * @return
     */
    public static ArrayList<RelayLoop> getVentilationRelayLoop(Context context) {
        return (ArrayList<RelayLoop>) new RelayLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).getRelayLoopAllList();
    }

    /**
     * 添加新风系统
     *
     * @param context
     * @param object
     */
    public static void addVentilation(Context context, MenuDeviceVentilationObject object) {
        if (object == null) {
            Loger.print(TAG, "ssd addVentilation param is null", Thread.currentThread());
            return;
        }
        if (object.checkRelay()) {
            Loger.print(TAG, "ssd addVentilation relay is null", Thread.currentThread());
            return;
        }

        String message = MessageManager.getInstance(context).addVentilation(object);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /***********************************************/
    /**
     * 设备选择 第二个页面 设备类型列表
     *
     * @param context
     * @param type
     * @return
     */
    public static ArrayList<PeripheralDevice> getPeripheraListWithType(Context context, int type) {
        PeripheralDeviceFunc func = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<PeripheralDevice> list = (ArrayList<PeripheralDevice>) func.getPeripheralDeviceAllList();
        ArrayList<PeripheralDevice> returnValue = new ArrayList<>();
        if (list.size() > 0) {
            for (PeripheralDevice device : list) {
                if (device.mType == type) {
                    returnValue.add(device);
                }
            }
        }
        return returnValue;
    }

    /**
     * 选择 Module模块 列表 默认的是第一个参数
     * <p/>
     * MenuDeviceUIItem 参数 peripheraDevice deviceName
     *
     * @param context
     * @return
     */
    public static ArrayList<MenuDeviceUIItem> getAddModuleList(Context context, int type) {
        ArrayList<MenuDeviceUIItem> returnValue = new ArrayList<>();
        if (type != ModelEnum.MODULE_TYPE_BACKAUDIO) {
            MenuDeviceUIItem uiItem = new MenuDeviceUIItem();
            uiItem.peripheraDevice = null;
            uiItem.deviceName = context.getString(R.string.menu_device_edit_add);
            returnValue.add(uiItem);
            ArrayList<PeripheralDevice> devices = (ArrayList<PeripheralDevice>) new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceAllList();
            if (devices.size() == 0) return returnValue;
            for (PeripheralDevice device : devices) {
                if (device.mType == type) {
                    MenuDeviceUIItem uiItem01 = new MenuDeviceUIItem();
                    uiItem01.peripheraDevice = device;
                    uiItem01.deviceName = device.mName;
                    returnValue.add(uiItem01);
                }
            }
            return returnValue;
        } else {
            MenuDeviceUIItem uiItem = new MenuDeviceUIItem();
            uiItem.backaudioDevice = null;
            uiItem.deviceName = context.getString(R.string.menu_device_edit_add);
            returnValue.add(uiItem);
            ArrayList<BackaudioDevice> devices = (ArrayList<BackaudioDevice>) new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceAllList();
            if (devices.size() == 0) return returnValue;
            for (BackaudioDevice device : devices) {
                MenuDeviceUIItem uiItem01 = new MenuDeviceUIItem();
                uiItem01.backaudioDevice = device;
                uiItem01.deviceName = device.mName;
                returnValue.add(uiItem01);
            }
            return returnValue;
        }

    }
}
