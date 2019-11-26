package com.honeywell.cube.controllers.menus;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.controllers.UIItem.menu.MenuAddModuleIpvdpListObj;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCodeFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485LoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/19. 09:47
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 模块部分控制接口
 */
public class MenuModuleController {
    public static final String TAG = MenuModuleController.class.getSimpleName();

    /**
     * 获取Module页面数据
     *
     * @param context
     * @return
     */
    public static void getAllModuleList(Context context) {
        ArrayList<MenuModuleUIItem> returnValue = new ArrayList<>();

        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)){
            EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.GET_MODULE_LIST, true, returnValue));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD){
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1){
                EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.GET_MODULE_LIST, true, returnValue));
                return;
            }
        }
        //外围设备表
        ArrayList<PeripheralDevice> alldevices = (ArrayList<PeripheralDevice>) new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceAllList();
        if (alldevices.size() > 0) {
            for (PeripheralDevice device : alldevices) {
                //不显示 IPC／IPVDP/场景面板
                if (device.mType != 3 && device.mType != 11 && device.mType != 11) {
                    MenuModuleUIItem menuModuleUIItem = new MenuModuleUIItem();
                    menuModuleUIItem.title = device.mName;
                    menuModuleUIItem.version = device.mVersion.length() > 0 ? "V" + device.mVersion : "";
                    String type = DeviceManager.getModuleTypeProtocolFromInt(device.mType);
                    menuModuleUIItem.type = DeviceManager.getModuleTypeStringFromProtocol(context, type);
                    menuModuleUIItem.ipAddr = device.mIpAddr;
                    menuModuleUIItem.state = (device.mIsOnline == CommonData.ONLINE);
                    menuModuleUIItem.deviceType = ModelEnum.MAIN_MODULE;
                    menuModuleUIItem.moduleObject = device;
                    returnValue.add(menuModuleUIItem);
                }
            }
        }

        //BackAudio device
        ArrayList<BackaudioDevice> backaudioDevices = (ArrayList<BackaudioDevice>) new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceAllList();
        if (backaudioDevices.size() > 0) {
            for (BackaudioDevice device : backaudioDevices) {
                MenuModuleUIItem menuModuleUIItem = new MenuModuleUIItem();
                menuModuleUIItem.title = device.mName;
                menuModuleUIItem.version = "";
                String type = DeviceManager.getModuleTypeProtocolFromInt(ModelEnum.MODULE_TYPE_BACKAUDIO);
                menuModuleUIItem.type = DeviceManager.getModuleTypeStringFromProtocol(context, type);
                menuModuleUIItem.ipAddr = device.mSerialNumber.length() > 10 ? device.mSerialNumber.substring(device.mSerialNumber.length() - 10) : device.mSerialNumber;
                menuModuleUIItem.state = (device.mIsOnline == CommonData.ONLINE);
                menuModuleUIItem.deviceType = ModelEnum.MAIN_BACKAUDIO;
                menuModuleUIItem.moduleObject = device;
                returnValue.add(menuModuleUIItem);
            }
        }
        EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.GET_MODULE_LIST, true, returnValue));
    }

    /**
     * 获取弹出窗选项
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getAddModuleList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.module_add_discover));
        returnValue.add(context.getString(R.string.module_add_sparklighting));
        returnValue.add(context.getString(R.string.module_add_bacnet));
        returnValue.add(context.getString(R.string.module_add_ipvdp));

        return returnValue;
    }

    /**
     * 添加Spark Lighting 模块 返回默认的参数
     *
     * @param context
     * @return
     */
    public static MenuModuleUIItem getDefaultSparkLightingModule(Context context) {
        MenuModuleUIItem uiItem = new MenuModuleUIItem();
        uiItem.title = "Spark Lighting";
        uiItem.ipAddr = CommonUtils.getLocalIpAddr(context);
        uiItem.sub_gateway_id = 1;
        return uiItem;
    }

    /**
     * 添加Bacnet 默认的参数
     *
     * @return
     */
    public static MenuModuleUIItem getDefaultBacnetModule() {
        MenuModuleUIItem uiItem = new MenuModuleUIItem();
        uiItem.title = "Bacnet 空调";
        uiItem.bacnet_type = ModelEnum.BACNET_TYPE_DAKIN;
        uiItem.cube_bacnet_id = 1;
        uiItem.bacnet_device_id = 1;
        return uiItem;
    }

    /**
     * 添加 IPVDP 获取默认参数
     *
     * @param context
     * @return
     */
    public static MenuModuleUIItem getDefaultIPVDP(Context context) {
        MenuModuleUIItem uiItem = new MenuModuleUIItem();
        uiItem.title = "IPVDP";
        uiItem.ipAddr = CommonUtils.getLocalIpAddr(context);
        uiItem.hns_ip = uiItem.ipAddr;
        int roomid = CommonCache.getRoomIdList(context).get(1);
        String roomname = CommonCache.getRoomNameList(context).get(1);
        uiItem.ipvdp_roomId = roomid;
        uiItem.ipvdp_roomName = roomname;

        if (uiItem.ipvdpListObjs == null) {
            uiItem.ipvdpListObjs = new ArrayList<>();
        }
        if (uiItem.ipvdpListObjs.size() > 0) {
            uiItem.ipvdpListObjs.clear();
        }
        for (int i = 0; i < 7; i++) {
            MenuAddModuleIpvdpListObj obj = new MenuAddModuleIpvdpListObj();
            obj.sectionName = context.getString(R.string.menu_module_add_ipvdp_extension) + " " + (i + 1);
            obj.roomId = roomid;
            obj.roomName = roomname;
            uiItem.ipvdpListObjs.add(obj);
        }
        return uiItem;
    }

    /**
     * 获取Bacnet 空调的数组列表
     *
     * @return
     */
    public static ArrayList<String> getBacnetTypeList(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();
        returnValue.add(context.getString(R.string.device_dakin));
        returnValue.add(context.getString(R.string.device_sanling));
        return returnValue;
    }

    /**
     * 添加新模块 发现新模块
     *
     * @param context
     */
    public static void findNewModule(Context context) {
        String message = MessageManager.getInstance(context).findNewModule();
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 添加新模块 添加SParkLighting
     *
     * @param context
     */
    public static void addModuleSparkLighting(Context context, MenuModuleUIItem uiItem) {
        //传入参数不能为null
        if (uiItem == null) {
            Loger.print(TAG, "ssd 添加新模块 添加SParkLighting 传入参数为null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).addModuleSparkLighting(uiItem.title, uiItem.ipAddr, "" + uiItem.sub_gateway_id);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 添加新模块 添加BacnetAC
     *
     * @param context
     */
    public static void addModuleBacnetAC(Context context, MenuModuleUIItem uiItem) {

        if (uiItem == null) {
            Loger.print(TAG, "ssd 添加新模块 添加BacnetAC 传入参数为null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).addModuleBacnetAC(uiItem.cube_bacnet_id, uiItem.bacnet_device_id, uiItem.title, CommonUtils.transferBacnetType(context, uiItem.bacnet_type));
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 添加新模块 添加IPVDP模块
     *
     * @param context
     */
    public static void addModuleIPVDP(Context context, MenuModuleUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd 添加新模块 添加IPVDP模块 传入参数为null", Thread.currentThread());
            return;
        }
        ArrayList<Map> ipvdproomloop = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("ipvdpname", "ipvdp0");
        item.put("roomid", uiItem.ipvdp_roomId);
        ipvdproomloop.add(item);
        for (int i = 0; i < uiItem.ipvdpListObjs.size(); i++) {
            MenuAddModuleIpvdpListObj listObj = uiItem.ipvdpListObjs.get(i);
            if (listObj.isEnable == true) {
                Map<String, Object> subitem = new HashMap<>();
                subitem.put("ipvdpname", "ipvdp" + (i + 1));
                subitem.put("roomid", listObj.roomId);
                ipvdproomloop.add(subitem);
            }
        }
        String message = MessageManager.getInstance(context).addModuleIPVDP(CommonData.DEFAULTSECURITYPWD, uiItem.ipAddr, uiItem.hns_ip, uiItem.title, ipvdproomloop);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 删除 模块
     *
     * @param context
     * @param object
     */
    public static void deleteModule(Context context, Object object) {
        if (object == null) {
            Loger.print(TAG, "ssd delet module : object is null", Thread.currentThread());
            return;
        }
        UIItems items = new UIItems();
        items.object = object;
        DeviceController.changeDeviceStatusWithBody(context, items, ModelEnum.CHANGE_DEVICE_STATUS_DELETE, -1, null);
    }

    /**
     * 编辑 模块
     *
     * @param context
     * @param name
     */
    public static void modifyModule(Context context, Object object, String name) {
        if (object == null) {
            Loger.print(TAG, "ssd modify module : object is null", Thread.currentThread());
            return;
        }
        UIItems items = new UIItems();
        items.object = object;
        DeviceController.changeDeviceStatusWithBody(context, items, ModelEnum.CHANGE_DEVICE_STATUS_MODIFY, -1, name);
    }


    /********** responce *************/
    /**
     * 处理发现设备反馈消息
     * 界面需要刷线模块列表
     *
     * @param context
     * @param body
     */
    public static void handleResponceFindNewModule(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.ADD_FIND_NEW_MODULE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }

        if (body.has(CommonData.JSON_COMMAND_CONFIGDATA)) {
            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_CONFIGDATA);
            if (array != null && array.length() > 0) {
                //清空表
                new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).clearPeripheraDevices();
                new IrInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).clearIRInfo();
                new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).clearIpcstreamInfo();
                new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).clearBackAudioDevice();

                ResponderController.handleGetConfigWithBody(context, array);

                EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.ADD_FIND_NEW_MODULE, true, "数据刷新成功"));
                return;
            }
        }
        EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.ADD_FIND_NEW_MODULE, false, "未发现任何设备"));
    }

    /**
     * 处理 Config Module responce
     *
     * @param context
     * @param body
     */
    public static void handleResponceConfigModuleWithBody(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configType = body.optString("configtype");
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE_DELETE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            } else {
                EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            }
            return;
        }

        PeripheralDeviceFunc func = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(body.optString("configtype"))) {
            PeripheralDevice device = new PeripheralDevice();
            device.mPrimaryID = body.optInt("responseprimaryid");
            device.mType = DeviceManager.getModuleTypeIntFromString(body.optString("moduletype"));
            device.mName = body.optString("aliasname");
            device.mIpAddr = body.optString("moduleipaddr");
            device.mPort = body.optInt("port");
            device.mIsConfig = body.optInt("isconfig");
            device.mIsOnline = body.optInt("isonline");
            device.mBacnetId = body.optInt("bacnetid");
            device.mBrandName = body.optString("brandname");
            device.mMaskId = body.optInt("maskid");
            device.mIsOnline = 1;
            func.addPeripheralDevice(device);
        } else if ("delete".equalsIgnoreCase(body.optString("configtype"))) {
            PeripheralDevice device = func.getPeripheralDeviceByPrimaryId(body.optInt("primaryid"));
            //删除关联的设备
            if (device.mType == ModelEnum.MODULE_TYPE_SPARKLIGHTING) {
                //spark lighting
                new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteSparkLightingLoopByDevId(device.mPrimaryID);
            } else if (device.mType == ModelEnum.MODULE_TYPE_WIFIRELAY) {
                //Wi-Fi relay
                new RelayLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteRelayLoopByDevID(device.mPrimaryID);
            } else if (device.mType == ModelEnum.MODULE_TYPE_WIFIIR) {
                //Ir
                new IrInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteIrInfoByDevId(device.mPrimaryID);
                IrLoopFunc func1 = new IrLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
                ArrayList<IrLoop> irLoops = (ArrayList<IrLoop>) func1.getIrLoopListByDevId(device.mPrimaryID);
                for (IrLoop irLoop : irLoops) {
                    IrCodeFunc codeFunc = new IrCodeFunc(ConfigCubeDatabaseHelper.getInstance(context));
                    codeFunc.deleteIrCodeByLoopId(irLoop.mLoopSelfPrimaryId);
                }
                func1.getIrLoopListByDevId(device.mPrimaryID);
            } else if (device.mType == ModelEnum.MODULE_TYPE_WIREDZONE) {
                //zone
                new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteWiredZoneLoopByDevId(device.mPrimaryID);
            } else if (device.mType == ModelEnum.MODULE_TYPE_WIFI315M433M) {
                //315M_433M
                new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteWireless315M433MLoopByDevId(device.mPrimaryID);
            } else if (device.mType == ModelEnum.MODULE_TYPE_BACKAUDIO) {
                //TODO
                //这里和IOS有一点区别 BackAudiodevice 其实没必要处理 iOS做了处理，Android端目前不做处理

                //Back audio
                new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteBackaudioLoopByDevId(device.mPrimaryID);
            } else if (device.mType == ModelEnum.MODULE_TYPE_IPVDP) {
                //IPVDP
                new IpvdpInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteIpvdpInfoByDevId(device.mPrimaryID);
            } else if (device.mType == ModelEnum.MODULE_TYPE_WIFI485) {
                //485
                new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteWifi485LoopByDevId(device.mPrimaryID);
            }
            func.deletePeripheralDeviceByPrimaryId(device.mPrimaryID);
            EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE_DELETE, true, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(body.optString("configtype"))) {
            PeripheralDevice device = func.getPeripheralDeviceByPrimaryId(body.optInt("primaryid"));
            device.mName = body.optString("aliasname");
            func.updatePeripheralDeviceByPrimaryId(device.mPrimaryID, device);
        }
        EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE, true, "操作成功"));
    }
}
