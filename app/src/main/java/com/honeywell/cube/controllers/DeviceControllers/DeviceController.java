package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;
import android.support.annotation.Nullable;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.UIItem.IPCameraListDetail;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PlistUtil;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRoomEvent;
import com.honeywell.cube.utils.events.CubeScheduleEvent;
import com.honeywell.cube.utils.plist_parser.xml.plist.domain.Array;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/12. 14:44
 * Email:Shodong.Sun@honeywell.com
 * 设备视图中接口
 */
public class DeviceController {
    private static final String TAG = DeviceController.class.getSimpleName();

    //记录请求
    private static Map<String, Object> getStatusArrayList = null;
    private static boolean isRoom = false;
    private static int updateCount = 0;
    private static int curCount = 0;

    //记录请求的BackAudio的状态，请求的loop个数
    private static int backAudioEventCount = 0;//总数超过3，则发送

    //测试 获取设备在线状态时设置为false.
    private static boolean DEBUG_UPDATE_STATE = false;

    /**
     * 获取设备首页上设备列表需要显示的信息，用于刷新设备列表
     *
     * @param context
     * @return DeviceListType--定义的类型 包含name 和 图标名称
     */
    public static void getAllDeviceTypeList(Context context) {
        ArrayList<DeviceListType> returnValue = new ArrayList<DeviceListType>();
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.GET_DEVICE_TYPE_LIST, returnValue));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.GET_DEVICE_TYPE_LIST, returnValue));
                return;
            }
        }
        ArrayList<Object> deviceCategoryArray = PlistUtil.parseArrayPlistWithName("DeviceCategory.plist");
        for (Object item : deviceCategoryArray) {
            if (item instanceof Map) {
                //获取Map参数
                Map<String, Object> temp = (Map<String, Object>) item;
                String name = (String) temp.get("title");
                String imageName = (String) temp.get("image");

                //构建对象
                DeviceListType type = new DeviceListType();
                type.deviceName = name;
                type.deviceImageName = imageName;

                //如果是电梯，判断是否添加过IPVDP
                if (ModelEnum.MAIN_CALL_ELEVATOR.equalsIgnoreCase(type.deviceName)) {
                    ArrayList<PeripheralDevice> peripheralDevices = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByType(ModelEnum.MODULE_TYPE_IPVDP);
                    if (peripheralDevices.size() > 0) {
                        Loger.print(TAG, "ssd getAllDeviceTypeList deviceType:" + type.toString(), Thread.currentThread());
                        //看到 iOS端 居然是假回复，我决定等等再做，先屏蔽掉
//                        returnValue.add(type);
                    }
                } else {
                    //判断当前设备列表中是否存在这种设备
                    ArrayList<Object> deviceList = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, name);

                    if (deviceList.size() > 0) {
                        Loger.print(TAG, "ssd getAllDeviceTypeList deviceType:" + type.toString(), Thread.currentThread());
                        returnValue.add(type);
                    }
                }
            }
        }
        //发送本地数据
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.GET_DEVICE_TYPE_LIST, returnValue));
    }

    /**
     * 获取本地数据库中对应的设备列表，请求参数设备类型,同时去请求状态信息
     *
     * @param context
     * @param name
     */
    public static void getDeviceListWithName(Context context, String name) {
        if (DEBUG_UPDATE_STATE) {
            getDeviceListFromDataBaseWithName(context, name);
            return;
        }
        Map<String, Object> list = DeviceManager.getDeviceListFromDatabaseWithNameForMap(context, name);

        if (name.equals(ModelEnum.MAIN_CURTAIN) || name.equals(ModelEnum.MAIN_ZONE) || name.equalsIgnoreCase(ModelEnum.MAIN_AIR_CONDITION) || name.equalsIgnoreCase(ModelEnum.MAIN_VENTILATION)) {
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_DEVICE_STATE, updateLoopState(context, list)));
        } else if (name.equals(ModelEnum.MAIN_IP_CAMERA)) {
            ArrayList<IpcStreamInfo> streamInfos = (ArrayList<IpcStreamInfo>) list.get(ModelEnum.LOOP_IPC);
            ArrayList<IPCameraListDetail> arrayList = new ArrayList<>();
            if (streamInfos != null && streamInfos.size() > 0) {
                for (IpcStreamInfo info : streamInfos) {
                    IPCameraListDetail detail = new IPCameraListDetail();
                    PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(info.mDevId);
                    if (device == null) {
                        Loger.print(TAG, "ssd getDeviceListWithName ipcamera device is null", Thread.currentThread());
                    } else {
                        detail.title = device.mName;
                        detail.details = device.mIpAddr;
                        detail.ipcStreamInfo = info;
                        arrayList.add(detail);
                    }
                }
            }
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_DEVICE_STATE, arrayList));
        } else {
            getDeviceStatusWithDeviceList(context, list, false);
        }
    }

    /**
     * 用于判断 zone 是否展示 24Hour
     *
     * @param loop
     * @return
     */
    public static boolean checkZoneTypeIf24Hour(Object loop) {
        if (loop == null) return false;
        if (loop instanceof WiredZoneLoop) {
            WiredZoneLoop zoneLoop = (WiredZoneLoop) loop;
            if (CommonData.ZONE_TYPE_SECURITY_24HOURS.equalsIgnoreCase(zoneLoop.mZoneType)) {
                return true;
            }
        } else if (loop instanceof IpvdpZoneLoop) {
            IpvdpZoneLoop zoneLoop = (IpvdpZoneLoop) loop;
            if (CommonData.ZONE_TYPE_SECURITY_24HOURS.equalsIgnoreCase(zoneLoop.mZoneType)) {
                return true;
            }
        } else if (loop instanceof Wireless315M433MLoop) {
            Wireless315M433MLoop zoneLoop = (Wireless315M433MLoop) loop;
            if (CommonData.ZONE_TYPE_SECURITY_24HOURS.equalsIgnoreCase(zoneLoop.mZoneType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询设备是否在线
     *
     * @param context
     * @param loop
     * @return
     */
    public static Object checkISOffline(Context context, Object loop) {
        Boolean isOffLine = false;
        if (loop == null) return loop;
        if (loop instanceof BasicLoop) {
            if (loop instanceof BackaudioLoop) {
                BackaudioLoop backAudioLoop = (BackaudioLoop) loop;
                BackaudioDevice backAudioDevice = new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceByPrimaryId(backAudioLoop.mModulePrimaryId);
                if (backAudioDevice == null) return isOffLine;
                if (backAudioDevice.mIsOnline == CommonData.ONLINE) {
                    backAudioLoop.isOnline = true;
                    return backAudioLoop;
                } else {
                    return backAudioLoop;
                }
            }
            BasicLoop basicLoop = (BasicLoop) loop;
            PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(basicLoop.mModulePrimaryId);
            if (loop instanceof IrLoop) {
                Loger.print(TAG, "ssd check is offline is Ir loop", Thread.currentThread());
            }
            if (device == null) return loop;
            if (device.mIsOnline == CommonData.ONLINE) {
                basicLoop.isOnline = true;
                return basicLoop;
            } else {
                return basicLoop;
            }
        } else if (loop instanceof IpcStreamInfo) {
            return loop;
        }
        return loop;
    }

    /**
     * 更新Map组织的Loop状态
     *
     * @param context
     * @param list
     * @return
     */
    public static Map<String, Object> updateLoopState(Context context, Map<String, Object> list) {
        if (list == null) return list;
        Map<String, Object> returnValue = new HashMap<>();
        for (String key : list.keySet()) {
            ArrayList<Object> arrayList = (ArrayList<Object>) list.get(key);
            if (arrayList.size() == 0) {
                continue;
            }
            ArrayList<Object> returnArray = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) instanceof Wifi485Loop) {
                    Wifi485Loop loop = (Wifi485Loop) arrayList.get(i);
                    if ("ventilation".equalsIgnoreCase(loop.mLoopType)) {
                        continue;
                    }
                }
                Object loop = checkISOffline(context, arrayList.get(i));
                returnArray.add(loop);
            }
            returnValue.put(key, returnArray);
        }
        return returnValue;
    }


    /**
     * 根据云端返回的数据更新目前device的状态
     *
     * @param context
     * @param returnArray---云端数据
     * @param mainDevices---目前正在显示的设备列表
     */
    public static void updateDeviceStatusWithInfo(Context context, JSONArray returnArray, Map<String, Object> mainDevices) {
        curCount++;
        if (mainDevices == null && getStatusArrayList != null) {
            mainDevices = getStatusArrayList;
        }
        if (mainDevices == null) {
            return;
        }
        if (returnArray == null) {
            //直接返回对应的Map
            Loger.print(TAG, "ssd updateDeviceStatusWithInfo returnArray is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_DEVICE_STATE, mainDevices));
            return;
        }

        ArrayList<Object> deviceList = new ArrayList<>();
        Iterator iter = mainDevices.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            deviceList.addAll((ArrayList<Object>) mainDevices.get(key));
        }

        for (int j = 0; j < returnArray.length(); j++) {
            JSONObject item;
            try {
                item = (JSONObject) returnArray.get(j);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            for (Object object : deviceList) {
                //SparkLighting
                if (object instanceof SparkLightingLoop) {
                    SparkLightingLoop loop = (SparkLightingLoop) object;
                    if ((item.optInt("deviceid") == loop.mSubDevId && ((item.optInt("loopid"))) == loop.mLoopId)) {

                        //ios端检查错误，显示没有运行错误检测，直接break了，android现在不先做处理，等需要的时候在进行错误检测
                        loop.customStatus.status = DeviceManager.transferStatusFromStrToBool(item.optString("status"));
                        loop.customStatus.openClosePercent = item.optInt("openclosepercent");
                        break;
                    }
                } else if (object instanceof RelayLoop) {
                    //Wifi relay
                    RelayLoop loop1 = (RelayLoop) object;
                    //Periphera
                    PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(loop1.mModulePrimaryId);
                    if (mainDevice == null) {
                        Loger.print(TAG, "ssd updateDeviceStatusWithInfo relay error", Thread.currentThread());
                        break;
                    }
                    if ((item.optInt("loopid")) == loop1.mLoopId && (item.optString("modulemacaddr")).equals(mainDevice.mMacAddr)) {
                        //ios端检查错误，显示没有运行错误检测，直接break了，android现在不先做处理，等需要的时候在进行错误检测
                        loop1.customStatus.status = DeviceManager.transferStatusFromStrToBool(item.optString("status"));
                        break;
                    }
                } else if (object instanceof Wireless315M433MLoop) {
                    //315_433
                    Wireless315M433MLoop loop = (Wireless315M433MLoop) object;
                    //Periphera
                    PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(loop.mModulePrimaryId);
                    if (mainDevice == null) {
                        Loger.print(TAG, "ssd updateDeviceStatusWithInfo Wireless315M433MLoop error", Thread.currentThread());
                        break;
                    }
                    int loopid = item.optInt("loopid");
                    int deviceid = item.optInt("deviceid");
                    String macAddr = item.optString("modulemacaddr");
                    if (loopid == loop.mLoopId && deviceid == loop.mSubDevId && mainDevice.mMacAddr.equals(macAddr)) {
                        //ios端检查错误，显示没有运行错误检测，直接break了，android现在不先做处理，等需要的时候在进行错误检测
                        loop.customStatus.status = DeviceManager.transferStatusFromStrToBool(item.optString("status"));
                        loop.customStatus.openClosePercent = item.optInt("openclosepercent");
                        break;
                    }
                } else if (object instanceof BacnetLoop) {
                    //Bacnet
                    BacnetLoop loop = (BacnetLoop) object;
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
                        break;
                    }
                } else if (object instanceof Wifi485Loop) {
                    //wifi 485
                    Wifi485Loop loop = (Wifi485Loop) object;
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
                    }
                } else if (object instanceof BackaudioLoop) {
                    BackaudioLoop loop = (BackaudioLoop) object;
                    //Periphera
                    BackaudioDevice mainDevice = new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceByPrimaryId(loop.mModulePrimaryId);
                    if (mainDevice == null) {
                        Loger.print(TAG, "ssd updateDeviceStatusWithInfo Wireless315M433MLoop error", Thread.currentThread());
                        break;
                    }
                    int loopid = item.optInt("loopid");
                    String moduleSerialnum = item.optString("moduleserialnum");
                    if (loopid == loop.mLoopId && moduleSerialnum.equals(mainDevice.mSerialNumber)) {
                        //ios端检查错误，显示没有运行错误检测，直接break了，android现在不先做处理，等需要的时候在进行错误检测
                        JSONArray keyTypeLoop = item.optJSONArray("keytypeloop");
                        if (keyTypeLoop == null || keyTypeLoop.length() == 0) break;
                        for (int i = 0; i < keyTypeLoop.length(); i++) {
                            JSONObject map;
                            try {
                                map = (JSONObject) keyTypeLoop.get(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                            String keyType = map.optString("keytype");
                            String keyValue = map.optString("keyvalue");
                            if (keyType.equals("songname")) {
                                loop.customModel.songname = keyValue;
                            } else if (keyType.equals("allplaytime")) {
                                loop.customModel.allplaytimeStr = keyValue;
                                keyValue = keyValue.replaceAll(" ", "");
                                String[] min_sec = keyValue.split(":");
                                if (min_sec.length == 2) {
                                    loop.customModel.allplaytime = Integer.parseInt(min_sec[0]) * 60 + Integer.parseInt(min_sec[1]);
                                }
                            } else if (keyType.equals("playtime")) {
                                loop.customModel.playTimeStr = keyValue;
                                keyValue = keyValue.replaceAll(" ", "");
                                String[] min_sec = keyValue.split(":");
                                if (min_sec.length == 2) {
                                    loop.customModel.playtime = Integer.parseInt(min_sec[0]) * 60 + Integer.parseInt(min_sec[1]);
                                }
                            } else if (keyType.equals("power")) {
                                loop.customModel.power = keyValue;
                            } else if (keyType.equals("mute")) {
                                loop.customModel.mute = keyValue;
                            } else if (keyType.equals("singlecycle")) {
                                loop.customModel.singlecycle = keyValue;
                            } else if (keyType.equals("playstatus")) {
                                loop.customModel.playstatus = keyValue;
                            } else if (keyType.equals("source")) {
                                loop.customModel.source = keyValue;
                            } else if (keyType.equals("volume")) {
                                loop.customModel.volume = Integer.parseInt(keyValue);
                            }
                        }
                    }
                }
            }
        }

//        curCount >= (updateCount/2)
        if (curCount >= 1) {
            ArrayList<SparkLightingLoop> sparkLightingLoops = new ArrayList<>();
            ArrayList<RelayLoop> relayLoops = new ArrayList<>();
            ArrayList<BackaudioLoop> backaudioLoops = new ArrayList<>();
            ArrayList<Wireless315M433MLoop> wireless315M433MLoops = new ArrayList<>();
            ArrayList<BacnetLoop> bacnetLoops = new ArrayList<>();
            ArrayList<Wifi485Loop> wifi485Loops = new ArrayList<>();

            for (Object device : deviceList) {
                if (device instanceof RelayLoop) {
                    //Relay
                    RelayLoop loop = (RelayLoop) device;
                    relayLoops.add(loop);
                } else if (device instanceof SparkLightingLoop) {
                    //SparkLightingLoop
                    SparkLightingLoop loop = (SparkLightingLoop) device;
                    sparkLightingLoops.add(loop);
                } else if (device instanceof BackaudioLoop) {
                    //back audio
                    BackaudioLoop loop = (BackaudioLoop) device;
                    backaudioLoops.add(loop);
                } else if (device instanceof Wireless315M433MLoop) {
                    //315_433
                    Wireless315M433MLoop loop = (Wireless315M433MLoop) device;
                    wireless315M433MLoops.add(loop);
                } else if (device instanceof BacnetLoop) {
                    //Bacnet
                    BacnetLoop loop = (BacnetLoop) device;
                    bacnetLoops.add(loop);
                } else if (device instanceof Wifi485Loop) {
                    //485
                    Wifi485Loop loop = (Wifi485Loop) device;
                    wifi485Loops.add(loop);
                }
            }

            //EventBus 发送更新状态后的数组, 根据是否是room来分别发不同的事件
            if (!isRoom) {
                //device 部分数据
                Map<String, Object> sendItem = new HashMap<>();
                if (sparkLightingLoops.size() > 0) {
                    sendItem.put(ModelEnum.SPARKLIGHTING, sparkLightingLoops);
                }
                if (relayLoops.size() > 0) {
                    sendItem.put(ModelEnum.LOOP_RELAY, relayLoops);
                }
                if (backaudioLoops.size() > 0) {
                    sendItem.put(ModelEnum.LOOP_BACKAUDIO, backaudioLoops);
                }
                if (wireless315M433MLoops.size() > 0) {
                    sendItem.put(ModelEnum.WIRELESS_315_433, wireless315M433MLoops);
                }
                if (bacnetLoops.size() > 0) {
                    sendItem.put(ModelEnum.LOOP_BACNET, bacnetLoops);
                }
                if (wifi485Loops.size() > 0) {
                    sendItem.put(ModelEnum.WIFI_485, wifi485Loops);
                }
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_DEVICE_STATE, updateLoopState(context, sendItem)));
            } else {
                //Room 部分数据
                ArrayList<UIItems> returnValue = new ArrayList<>();
                if (sparkLightingLoops.size() > 0) {
                    for (SparkLightingLoop loop : sparkLightingLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.SPARKLIGHTING;
                        returnValue.add(uiItems);
                    }
                }

                if (relayLoops.size() > 0) {
                    for (RelayLoop loop : relayLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_RELAY;
                        returnValue.add(uiItems);
                    }
                }
                if (wireless315M433MLoops.size() > 0) {
                    for (Wireless315M433MLoop loop : wireless315M433MLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.WIRELESS_315_433;
                        returnValue.add(uiItems);
                    }
                }
                //增加没有查询的部分
                ArrayList<WiredZoneLoop> zoneLoops = (ArrayList<WiredZoneLoop>) mainDevices.get(ModelEnum.LOOP_ZONE);
                if (zoneLoops != null && zoneLoops.size() > 0) {
                    for (WiredZoneLoop loop : zoneLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_ZONE;
                        returnValue.add(uiItems);
                    }
                }


                ArrayList<IpvdpZoneLoop> ipvdpLoops = (ArrayList<IpvdpZoneLoop>) mainDevices.get(ModelEnum.LOOP_IPVDP);
                if (ipvdpLoops != null && ipvdpLoops.size() > 0) {
                    for (IpvdpZoneLoop loop : ipvdpLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_IPVDP;
                        returnValue.add(uiItems);
                    }
                }
                if (bacnetLoops.size() > 0) {
                    for (BacnetLoop loop : bacnetLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_BACNET;
                        returnValue.add(uiItems);
                    }
                }
                if (wifi485Loops.size() > 0) {
                    for (Wifi485Loop loop : wifi485Loops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.WIFI_485;
                        returnValue.add(uiItems);
                    }
                }
                ArrayList<VentilationLoop> ventilationLoops = (ArrayList<VentilationLoop>) mainDevices.get(ModelEnum.LOOP_VENTILATION);
                if (ventilationLoops != null && ventilationLoops.size() > 0) {
                    for (VentilationLoop loop : ventilationLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_VENTILATION;
                        returnValue.add(uiItems);
                    }
                }
                if (backaudioLoops.size() > 0) {
                    for (BackaudioLoop loop : backaudioLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_BACKAUDIO;
                        returnValue.add(uiItems);
                    }
                }
                ArrayList<IPCameraListDetail> ipcLoops = (ArrayList<IPCameraListDetail>) mainDevices.get(ModelEnum.LOOP_IPC);
                if (ipcLoops != null && ipcLoops.size() > 0) {
                    for (IPCameraListDetail loop : ipcLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = loop;
                        uiItems.looptype = ModelEnum.LOOP_IPC;
                        returnValue.add(uiItems);
                    }
                }

                ArrayList<IrLoop> customLoops = (ArrayList<IrLoop>) mainDevices.get(ModelEnum.LOOP_IR_CUSTOM);
                if (customLoops != null && customLoops.size() > 0) {
                    for (IrLoop loop : customLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_IR_CUSTOM;
                        returnValue.add(uiItems);
                    }
                }
                ArrayList<IrLoop> dvdLoops = (ArrayList<IrLoop>) mainDevices.get(ModelEnum.LOOP_IR_DVD);
                if (dvdLoops != null && dvdLoops.size() > 0) {
                    for (IrLoop loop : dvdLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_IR_DVD;
                        returnValue.add(uiItems);
                    }
                }
                ArrayList<IrLoop> tvLoops = (ArrayList<IrLoop>) mainDevices.get(ModelEnum.LOOP_IR_TV);
                if (tvLoops != null && tvLoops.size() > 0) {
                    for (IrLoop loop : tvLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_IR_TV;
                        returnValue.add(uiItems);
                    }
                }
                ArrayList<IrLoop> stbLoops = (ArrayList<IrLoop>) mainDevices.get(ModelEnum.LOOP_IR_STB);
                if (stbLoops != null && stbLoops.size() > 0) {
                    for (IrLoop loop : stbLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_IR_STB;
                        returnValue.add(uiItems);
                    }
                }
                ArrayList<IrLoop> acLoops = (ArrayList<IrLoop>) mainDevices.get(ModelEnum.LOOP_IR_AC);
                if (acLoops != null && acLoops.size() > 0) {
                    for (IrLoop loop : acLoops) {
                        UIItems uiItems = new UIItems();
                        uiItems.object = checkISOffline(context, loop);
                        uiItems.looptype = ModelEnum.LOOP_IR_AC;
                        returnValue.add(uiItems);
                    }
                }
                EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.UPDATE_ROOM_DEVICE_STATE, true, returnValue));
            }
        }
    }

    /**
     * 接受来自后台时的BackAudio信息
     *
     * @param context
     * @param returnArray
     */
    public static void updateBackAudioInfoFromEvent(Context context, JSONArray returnArray) {
        Loger.print(TAG, "updateBackAudioInfoFromEvent JSON Array " + returnArray.toString(), Thread.currentThread());
        if (getStatusArrayList == null || getStatusArrayList.size() == 0) {
            return;
        }
        ArrayList<BackaudioLoop> list = (ArrayList<BackaudioLoop>) getStatusArrayList.get(ModelEnum.LOOP_BACKAUDIO);
        if (list == null || list.size() == 0) {
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

            for (BackaudioLoop loop : list) {
                //Periphera
                BackaudioDevice mainDevice = new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceByPrimaryId(loop.mModulePrimaryId);
                if (mainDevice == null) {
                    Loger.print(TAG, "ssd updateBackAudioInfoFromEvent  error", Thread.currentThread());
                    break;
                }
                int loopid = item.optInt("loopid");
                String moduleSerialnum = item.optString("moduleserialnum");
                if (loopid == loop.mLoopId && moduleSerialnum.equals(mainDevice.mSerialNumber)) {
                    JSONArray keyTypeLoop = item.optJSONArray("keytypeloop");
                    if (keyTypeLoop == null || keyTypeLoop.length() == 0) break;
                    for (int i = 0; i < keyTypeLoop.length(); i++) {
                        JSONObject map;
                        try {
                            map = (JSONObject) keyTypeLoop.get(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        String keyType = map.optString("keytype");
                        String keyValue = map.optString("keyvalue");
                        if (keyType.equals("songname")) {
                            loop.customModel.songname = keyValue;
                        } else if (keyType.equals("allplaytime")) {
                            loop.customModel.allplaytimeStr = keyValue;
                            keyValue = keyValue.replaceAll(" ", "");
                            String[] min_sec = keyValue.split(":");
                            if (min_sec.length == 2) {
                                loop.customModel.allplaytime = Integer.parseInt(min_sec[0]) * 60 + Integer.parseInt(min_sec[1]);
                            }
                        } else if (keyType.equals("playtime")) {
                            loop.customModel.playTimeStr = keyValue;
                            keyValue = keyValue.replaceAll(" ", "");
                            String[] min_sec = keyValue.split(":");
                            if (min_sec.length == 2) {
                                loop.customModel.playtime = Integer.parseInt(min_sec[0]) * 60 + Integer.parseInt(min_sec[1]);
                            }
                        } else if (keyType.equals("power")) {
                            loop.customModel.power = keyValue;
                        } else if (keyType.equals("mute")) {
                            loop.customModel.mute = keyValue;
                        } else if (keyType.equals("singlecycle")) {
                            loop.customModel.singlecycle = keyValue;
                        } else if (keyType.equals("playstatus")) {
                            loop.customModel.playstatus = keyValue;
                        } else if (keyType.equals("source")) {
                            loop.customModel.source = keyValue;
                        } else if (keyType.equals("volume")) {
                            loop.customModel.volume = Integer.parseInt(keyValue);
                        }
                    }
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put(ModelEnum.LOOP_BACKAUDIO, list);

        if (backAudioEventCount > 1) {
            backAudioEventCount = 0;
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_BACKAUDIO_STATE_FROM_EVENT, map));
        } else {
            backAudioEventCount++;
        }
    }

    /**
     * setdevice 结果处理
     */
    public static void handleSetDeviceResponce(Context context, JSONArray returnArray) throws JSONException {
        if (returnArray == null || returnArray.length() == 0) {
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, null));
            return;
        }
        for (int i = 0; i < returnArray.length(); i++) {
            JSONObject object = (JSONObject) returnArray.get(i);
            int errorCode = object.getInt("errorcode");
            if (errorCode > 0) {
                String msg = handleErrorCode(context, errorCode);
                EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, msg));
                return;
            }
        }

        //发送成功通知
        EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, true, null));
    }

    /**
     * 处理device loop中的错误
     *
     * @param code
     * @return
     */
    public static String handleErrorCode(Context context, int code) {
        switch (code) {
            case 2:
                return context.getString(R.string.error_request_timeout);
            case 1:
                return context.getString(R.string.error_invalid_param);
        }
        return context.getString(R.string.responce_success);
    }


    /**
     * 操作 编辑 删除 设备 loop
     *
     * @param context
     * @param bodyItem
     * @param changeStatus
     * @param room--编辑时    需要传入的room 名称 Ipcamera
     * @param name--编辑时    name 名称 Ipcamera , back audio device
     */
    public static void changeDeviceStatusWithBody(Context context, UIItems bodyItem, int changeStatus, int room, @Nullable String name) {
        if (bodyItem == null || bodyItem.object == null) {
            Loger.print(TAG, "ssd changeDeviceStatusWithBody bodyItem is null", Thread.currentThread());
            return;
        }
        Object object = bodyItem.object;
        String message = null;//发送的字段
        if (object instanceof SparkLightingLoop) {
            SparkLightingLoop loop = (SparkLightingLoop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name);
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "sparklighting");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "sparklighting");
            }
        } else if (object instanceof Wireless315M433MLoop) {
            Wireless315M433MLoop loop = (Wireless315M433MLoop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name == null ? loop.mLoopName : name);
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "315M433M");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "315M433M");
            }
        } else if (object instanceof RelayLoop) {
            RelayLoop loop = (RelayLoop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name == null ? loop.mLoopName : name);
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "relay");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "relay");
            }
        } else if (object instanceof WiredZoneLoop) {
            WiredZoneLoop loop = (WiredZoneLoop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name == null ? loop.mLoopName : name);
                deviceloop.put("alarmenable", loop.mIsEnable == CommonData.ARM_TYPE_ENABLE ? "on" : "off");
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "wiredzone");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "wiredzone");
            }
        } else if (object instanceof IpvdpZoneLoop) {
            IpvdpZoneLoop loop = (IpvdpZoneLoop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name == null ? loop.mLoopName : name);
                deviceloop.put("alarmenable", loop.mIsEnable == CommonData.ARM_TYPE_ENABLE ? "on" : "off");
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "ipvdp");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "ipvdp");
            }
        } else if (object instanceof BacnetLoop) {
            BacnetLoop loop = (BacnetLoop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name == null ? loop.mLoopName : name);
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "bacnet");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "bacnet");
            }
        } else if (object instanceof Wifi485Loop) {
            Wifi485Loop loop = (Wifi485Loop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name == null ? loop.mLoopName : name);
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "485");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "485");
            }
        } else if (object instanceof BackaudioLoop) {
            BackaudioLoop loop = (BackaudioLoop) object;
            Map<String, Object> deviceloop = new HashMap<>();
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            deviceloop.put("primaryid", loop.mLoopSelfPrimaryId);
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                //编辑
                deviceloop.put(CommonData.JSON_COMMAND_ROOMID, room < 0 ? loop.mRoomId : room);
                deviceloop.put("aliasname", name == null ? loop.mLoopName : name);
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "modify", "backaudio");
            } else {
                arrayList.add(deviceloop);
                message = MessageManager.getInstance(context).changeDeviceStatusWithInfo(arrayList, "delete", "backaudio");
            }
        } else if (object instanceof IpcStreamInfo) {
            IpcStreamInfo info = (IpcStreamInfo) object;
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                message = MessageManager.getInstance(context).modifyIpcameraStatusWithInfo(info.mPrimaryId, room < 0 ? info.mRoomId : room, name == null ? info.mMainStream : name);
            } else {
                message = MessageManager.getInstance(context).deleteIpcameraStatusWithInfo(info.mId);
            }
        } else if (object instanceof IrLoop) {
            IrLoop loop = (IrLoop) object;
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                message = MessageManager.getInstance(context).modifyIrStatusWithInfo(loop.mLoopSelfPrimaryId, room < 0 ? loop.mRoomId : room, name == null ? loop.mLoopName : name);
            } else {
                message = MessageManager.getInstance(context).deleteIrStatusWithInfo(loop.mLoopSelfPrimaryId);
            }
        } else if (object instanceof PeripheralDevice) {
            //外围设备
            PeripheralDevice device = (PeripheralDevice) object;
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                message = MessageManager.getInstance(context).modifyPeripheraStatusWithInfo(device.mPrimaryID, DeviceManager.getModuleTypeProtocolFromInt(device.mType), name == null ? device.mName : name);
            } else {
                message = MessageManager.getInstance(context).deletePeripheraStatusWithInfo(device.mPrimaryID, DeviceManager.getModuleTypeProtocolFromInt(device.mType));
            }
        } else if (object instanceof BackaudioDevice) {
            BackaudioDevice device = (BackaudioDevice) object;
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                message = MessageManager.getInstance(context).modifyBackAudioDeviceStatusWithInfo(device.mPrimaryID, name == null ? device.mName : name);
            } else {
                message = MessageManager.getInstance(context).deleteBackAudioDeviceStatusWithInfo(device.mPrimaryID);
            }
        } else if (object instanceof VentilationLoop) {
            //新风设备
            VentilationLoop loop = (VentilationLoop) object;
            if (changeStatus == ModelEnum.CHANGE_DEVICE_STATUS_MODIFY) {
                loop.mLoopName = name;
                loop.mRoomId = room;
                message = MessageManager.getInstance(context).modifyVentilation(loop);
            } else {
                message = MessageManager.getInstance(context).deleteVentilation(loop);
            }
        }

        if (message != null) {
            //发送数据
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }

    /**
     * 获取Loop的信息
     *
     * @param context
     * @param object
     * @return
     */
    public static ArrayList<String> getLoopDetailInfo(Context context, Object object) {
        if (object == null) return new ArrayList<>();
        if (object instanceof SparkLightingLoop) {
            SparkLightingLoop loop = (SparkLightingLoop) object;
            return loop.getDetailInfo();
        } else if (object instanceof BackaudioLoop) {
            BackaudioLoop loop = (BackaudioLoop) object;
            return loop.getDetailInfo();
        } else if (object instanceof BacnetLoop) {
            BacnetLoop loop = (BacnetLoop) object;
            return loop.getDetailInfo();
        } else if (object instanceof IpcStreamInfo) {
            IpcStreamInfo info = (IpcStreamInfo) object;
            return info.getDetailInfo();
        } else if (object instanceof IpvdpZoneLoop) {
            IpvdpZoneLoop loop = (IpvdpZoneLoop) object;
            return loop.getDetailInfo();
        } else if (object instanceof IrLoop) {
            IrLoop loop = (IrLoop) object;
            return loop.getDetailInfo();
        } else if (object instanceof RelayLoop) {
            RelayLoop loop = (RelayLoop) object;
            return loop.getDetailInfo();
        } else if (object instanceof Wifi485Loop) {
            Wifi485Loop loop = (Wifi485Loop) object;
            return loop.getDetailInfo();
        } else if (object instanceof WiredZoneLoop) {
            WiredZoneLoop loop = (WiredZoneLoop) object;
            return loop.getDetailInfo();
        } else if (object instanceof Wireless315M433MLoop) {
            Wireless315M433MLoop loop = (Wireless315M433MLoop) object;
            return loop.getDetailInfo();
        }
        return new ArrayList<>();
    }

    /**
     * 获取编辑页面 Room列表
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getLoopDetailRoom(Context context) {
        return CommonCache.getRoomNameList(context);
    }

    public static ArrayList<Integer> getLoopDetailRoomId(Context context) {
        return CommonCache.getRoomIdList(context);
    }

    /**
     * 呼叫电梯
     */
    public static void callElevator(Context context) {
        String message = MessageManager.getInstance(context).callElevator();
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /*********************** private method *************************/

    /**
     * 从本地获取数据库中对应的设备列表，请求参数设备类型
     */
    private static void getDeviceListFromDataBaseWithName(Context context, String name) {
        Map<String, Object> list = DeviceManager.getDeviceListFromDatabaseWithNameForMap(context, name);
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.GET_DEVICES_FROM_DATABASE, list));
    }

    /**
     * 根据给出的设备列表 去更新设备的状态，需要去请求云端, 这个接口用于在设备二级目录下刷新各种设备的状态时调用
     *
     * @param devices--设备列表
     */
    public static void getDeviceStatusWithDeviceList(Context context, Map<String, Object> devices, boolean room) {

        if (devices == null || devices.size() == 0) {
            Loger.print(TAG, "ssd getDeviceStatusWithDeviceList list null", Thread.currentThread());
            return;
        }
        getStatusArrayList = devices;
        isRoom = room;
        curCount = 0;
        updateCount = 0;
        ArrayList<Object> deviceList = new ArrayList<>();
        ArrayList<Object> allDeviceList = new ArrayList<>();
        Iterator iter = devices.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (!key.equalsIgnoreCase(ModelEnum.LOOP_IPC)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_IPVDP)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_ZONE)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_IR_DVD)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_IR_STB)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_IR_TV)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_IR_CUSTOM)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_IR_AC)
                    && !key.equalsIgnoreCase(ModelEnum.LOOP_VENTILATION)
                    && !key.equalsIgnoreCase(ModelEnum.WIFI_485)) {
                deviceList.addAll((ArrayList<Object>) devices.get(key));
            }
            allDeviceList.addAll((ArrayList<Object>) devices.get(key));
        }
        if (room) {
            if (allDeviceList.size() == 0) {
                Loger.print(TAG, "ssd room 没有设备", Thread.currentThread());
                EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.UPDATE_ROOM_DEVICE_STATE, false, new ArrayList<>()));
                return;
            }
            if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
                //本地登录
            }
            if (allDeviceList.size() > 0 && deviceList.size() == 0) {
                //当前数据是不需要走云端请求的
                updateDeviceStatusWithInfo(context, new JSONArray(), null);
                return;
            }
        }

        ArrayList<SparkLightingLoop> sparkLightingLoops = new ArrayList<>();
        ArrayList<RelayLoop> relayLoops = new ArrayList<>();
        ArrayList<BackaudioLoop> backaudioLoops = new ArrayList<>();
        ArrayList<Wireless315M433MLoop> wireless315M433MLoops = new ArrayList<>();
        ArrayList<BacnetLoop> bacnetLoops = new ArrayList<>();
//        ArrayList<Wifi485Loop> wifi485Loops = new ArrayList<>();

        for (Object device : deviceList) {
            if (device instanceof SparkLightingLoop) {
                //SparkLightingLoop
                SparkLightingLoop loop = (SparkLightingLoop) device;
                if (loop.mLoopType != 2) {
                    sparkLightingLoops.add(loop);
                }
            } else if (device instanceof RelayLoop) {
                //Relay
                RelayLoop loop = (RelayLoop) device;
                relayLoops.add(loop);
            } else if (device instanceof BackaudioLoop) {
                //back audio
                BackaudioLoop loop = (BackaudioLoop) device;
                backaudioLoops.add(loop);
            } else if (device instanceof Wireless315M433MLoop) {
                //315_433
                Wireless315M433MLoop loop = (Wireless315M433MLoop) device;
                if (loop.mLoopType != 2) {
                    wireless315M433MLoops.add(loop);
                }
            } else if (device instanceof BacnetLoop) {
                //Bacnet
                BacnetLoop loop = (BacnetLoop) device;
                bacnetLoops.add(loop);
            }
//            else if (device instanceof Wifi485Loop) {
//                //485
//                Wifi485Loop loop = (Wifi485Loop) device;
//                wifi485Loops.add(loop);
//            }
        }

        Loger.print(TAG, "ssd start check device status", Thread.currentThread());
        //SparkLight
        if (sparkLightingLoops.size() > 0) {
            updateCount++;
            SparkLightingController.checkoutDeviceListState(context, sparkLightingLoops);
        }

        //WIFI relay
        if (relayLoops.size() > 0) {
            updateCount++;
            RelayController.checkoutDeviceListState(context, relayLoops);
        }

        //back audio
        if (backaudioLoops.size() > 0) {
            updateCount++;
            BackAudioController.checkoutDeviceListState(context, backaudioLoops);
        }

        //315_433
        if (wireless315M433MLoops.size() > 0) {
            ArrayList<Wireless315M433MLoop> loops = new ArrayList<>();
            for (Wireless315M433MLoop loop : wireless315M433MLoops) {
                if (loop.mDeviceType.equals("maia2")) {
                    loops.add(loop);
                }
            }
            if (loops.size() > 0) {
                updateCount++;
                Wireless315M433MController.checkoutDeviceListState(context, loops);
            }
        }

        //Bacnet
        if (bacnetLoops.size() > 0) {
            updateCount++;
            BacnetController.checkoutDeviceListState(context, bacnetLoops);
        }

//        //485
//        if (wifi485Loops.size() > 0) {
//            updateCount++;
//            Wifi485Controller.checkoutDeviceListState(context, wifi485Loops);
//        }
    }
}
