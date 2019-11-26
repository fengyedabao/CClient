package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.UIItem.VentilationUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoopFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/8/2. 15:19
 * Email:Shodong.Sun@honeywell.com
 * 用于处理新风系统过来的一些事件
 */
public class VentilationController {
    public static final String TAG = VentilationController.class.getSimpleName();

    //用于存储读取设备状态时的对象
    private static VentilationLoop ventilationLoop = null;


    /**
     * 进入页面，读取状态
     *
     * @param context
     * @param loop
     */
    public static void getVentilationUIItemsAndReadDeviceState(Context context, VentilationLoop loop) {
        if (loop == null) {
            Loger.print(TAG, "ssd read device status loop is null", Thread.currentThread());
            return;
        }
        ventilationLoop = loop;
        String message = MessageManager.getInstance(context).readVentilationDeviceStatus(loop.mLoopSelfPrimaryId);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 发送切换内外循环
     *
     * @param context
     * @param ventilationUIItem
     */
    public static void sendControlCycle(Context context, VentilationUIItem ventilationUIItem) {
        if (ventilationUIItem == null) {
            return;
        }
        String cycleType = "";
        if (ventilationUIItem.cycleInner) {
            ventilationUIItem.ventilationLoop.customModel.cycletype = ModelEnum.VENTILATION_INNER;
            cycleType = ModelEnum.VENTILATION_INNER;
        } else {
            ventilationUIItem.ventilationLoop.customModel.cycletype = ModelEnum.VENTILATION_OUTSIDE;
            ventilationUIItem.ventilationLoop.customModel.mode = "";
            cycleType = ModelEnum.VENTILATION_OUTSIDE;
        }
        sendControlMap(context, ventilationUIItem.ventilationLoop.mLoopSelfPrimaryId, "cycletype", cycleType);
    }

    /**
     * 发送其他控制命令 风速 开关 模式
     *
     * @param context
     * @param ventilationLoop
     * @param iconItem
     */
    public static void sendControlIconItem(Context context, VentilationLoop ventilationLoop, MenuDeviceIRIconItem iconItem) {
        if (ventilationLoop == null || iconItem == null) {
            return;
        }
        if ("fanspeed".equalsIgnoreCase(iconItem.ventilation_type)) {
            ventilationLoop.customModel.fanspeed = iconItem.ventilation_type_value;
        } else if ("mode".equalsIgnoreCase(iconItem.ventilation_type)) {
            ventilationLoop.customModel.fanspeed = iconItem.ventilation_type_value;
        } else if ("on".equalsIgnoreCase(ventilationLoop.customModel.power)) {
            ventilationLoop.customModel.power = "off";
        } else {
            ventilationLoop.customModel.power = "on";
        }
        sendControlMap(context, ventilationLoop.mLoopSelfPrimaryId, iconItem.ventilation_type, iconItem.ventilation_type_value);
    }


    /*********************
     * private method
     *****************/
    /**
     * UI 获取 新风界面的UIItem
     *
     * @param context
     * @param loop
     * @return
     */
    private static VentilationUIItem updateVentilationUIItems(Context context, VentilationLoop loop) {
        VentilationUIItem ventilationUIItem = new VentilationUIItem();
        ventilationUIItem.powerItem = getPowerIconItem(loop);
        ventilationUIItem.fanSpeedItems = getFanSpeedList(loop);
        if ("inner".equalsIgnoreCase(loop.customModel.cycletype)) {
            ventilationUIItem.cycleInner = true;
        } else {
            ventilationUIItem.cycleInner = false;
        }
        ventilationUIItem.modeItems = getModeList(context, loop);
        ventilationUIItem.ventilationLoop = loop;
        return ventilationUIItem;
    }

    /**
     * 发送控制命令
     *
     * @param context
     * @param primaryId
     * @param key
     * @param value
     */
    private static void sendControlMap(Context context, long primaryId, String key, String value) {
        Map<String, Object> controlDic = new HashMap<>();
        controlDic.put("primaryid", primaryId);
        controlDic.put(key, value);
        String message = MessageManager.getInstance(context).sendControlVentilation(controlDic);
        //发送数据
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 获取模式列表
     *
     * @param loop
     * @return
     */
    private static ArrayList<MenuDeviceIRIconItem> getModeList(Context context, VentilationLoop loop) {
        String[] modeList = {ModelEnum.VENTILATION_HUMIDITY, ModelEnum.VENTILATION_DEHUMIDITY};
        String mode = "";
        if (loop != null) {
            mode = "".equalsIgnoreCase(loop.customModel.mode) ? "" : loop.customModel.mode;
        }
        ArrayList<MenuDeviceIRIconItem> returnValue = new ArrayList<>();
        for (int i = 0; i < modeList.length; i++) {
            String fun = modeList[i];
            MenuDeviceIRIconItem item = new MenuDeviceIRIconItem();
            item.IR_icon_name = DeviceManager.transferVetilationNameFromProtocol(context, fun);
            item.IR_icon_imageName = fun;
            item.ventilation_type = "mode";
            item.ventilation_type_value = fun;
            item.IR_icon_enable = true;
            DeviceManager.transferIRIconImage(fun, item);
            if (!"".equalsIgnoreCase(mode)) {
                if (fun.equalsIgnoreCase(mode)) {
                    item.IR_icon_select = true;
                } else {
                    item.IR_icon_select = false;
                }
            }
            returnValue.add(item);
        }
        return returnValue;
    }

    /**
     * 获取风速列表
     *
     * @param loop
     * @return
     */
    private static ArrayList<MenuDeviceIRIconItem> getFanSpeedList(VentilationLoop loop) {
        String[] fan_speed = {CommonData.AC_FAN_SPPED_LOW, CommonData.AC_FAN_SPPED_MIDDLE, CommonData.AC_FAN_SPPED_HIGH};
        ArrayList<MenuDeviceIRIconItem> returnValue = new ArrayList<>();
        String fanSpeed = "";
        if (loop != null) {
            fanSpeed = "".equalsIgnoreCase(loop.customModel.fanspeed) ? "" : loop.customModel.fanspeed;
        }
        for (int i = 0; i < fan_speed.length; i++) {
            String fun = fan_speed[i];
            MenuDeviceIRIconItem item = new MenuDeviceIRIconItem();
            item.IR_icon_name = null;
            item.IR_icon_imageName = fun;
            item.ventilation_type = "fanspeed";
            item.ventilation_type_value = fun;
            item.IR_icon_enable = true;
            DeviceManager.transferIRIconImage(fun, item);
            if (!"".equalsIgnoreCase(fanSpeed)) {
                if (fun.equalsIgnoreCase(fanSpeed)) {
                    item.IR_icon_select = true;
                } else {
                    item.IR_icon_select = false;
                }
            }
            returnValue.add(item);
        }
        return returnValue;
    }

    /**
     * 获取开关按钮
     *
     * @param loop
     * @return
     */
    private static MenuDeviceIRIconItem getPowerIconItem(VentilationLoop loop) {
        MenuDeviceIRIconItem iconItem = new MenuDeviceIRIconItem();
        iconItem.IR_icon_name = DeviceManager.getNameWithProtocol("IR_POWER");
        iconItem.IR_icon_imageName = "IR_POWER";
        DeviceManager.transferIRIconImage(DeviceManager.getImageNameWithprotocol("IR_POWER"), iconItem);
        iconItem.IR_icon_enable = true;
        iconItem.ventilation_type = "power";
        iconItem.ventilation_type_value = "off";

        if (loop == null) {
            return iconItem;
        } else {
            if ("on".equalsIgnoreCase(loop.customModel.power)) {
                iconItem.IR_icon_select = true;
            }
        }
        return iconItem;
    }


    /**************
     * handle response
     *****************/
    /**
     * 读取设备状态
     *
     * @param body
     */
    public static void handleReadDeviceStatus(Context context, JSONObject body) {
        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            Loger.print(TAG, "handleReadDeviceStatus checkHaveOneSuccessWithBody error", Thread.currentThread());
            return;
        }

        JSONArray array = body.optJSONArray("deviceloopmap");
        if (ventilationLoop != null) {
            if (array.length() > 0) {
                JSONObject object = array.optJSONObject(array.length() - 1);
                if (object.has("cycletype")) {
                    ventilationLoop.customModel.cycletype = object.optString("cycletype");
                }
                if (object.has("fanspeed")) {
                    ventilationLoop.customModel.fanspeed = object.optString("fanspeed");
                }
                if (object.has("mode")) {
                    ventilationLoop.customModel.mode = object.optString("mode");
                }
                if (object.has("power")) {
                    ventilationLoop.customModel.power = object.optString("power");
                }
                VentilationUIItem uiItem = updateVentilationUIItems(context, ventilationLoop);
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.UPDATE_VENTILATION_STATUS, true, uiItem, "操作成功"));
            }
        }
    }

    /**
     * 处理编辑设备事件
     *
     * @param context
     * @param body
     */
    public static void handleConfigDeviceState(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configType = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_BACNET, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACNET, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }

        }
        VentilationLoopFunc func = new VentilationLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configType)) {
            VentilationLoop loop = new VentilationLoop();
            loop.mLoopSelfPrimaryId = body.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
            loop.mLoopName = body.optString(CommonData.JSON_COMMAND_ALIAS);
            loop.mRoomId = body.optInt(CommonData.JSON_COMMAND_ROOMID);
            loop.controltype = body.optString("controltype");
            loop.power = body.optString("power");
            loop.fanspeed = body.optString("fanspeed");
            loop.cycletype = body.optString("cycletype");
            loop.humidity = body.optString("humidity");
            loop.dehumidity = body.optString("dehumidity");
            func.addVentilationLoop(loop);
        } else if ("delete".equalsIgnoreCase(configType)) {
            int primaryId = body.optInt(CommonData.JSON_COMMAND_PRIMARYID);
            func.deleteVentilationLoopByPrimaryId(primaryId);
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_VENTILATION, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(configType)) {
            int primaryId = body.optInt(CommonData.JSON_COMMAND_PRIMARYID);
            VentilationLoop loop = func.getVentilationLoopByPrimaryId(primaryId);
            loop.mLoopName = body.optString(CommonData.JSON_COMMAND_ALIAS);
            loop.mRoomId = body.optInt(CommonData.JSON_COMMAND_ROOMID);
            func.updateVentilationLoopByPrimary(primaryId, loop);
        }
        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_VENTILATION, "操作成功"));
    }
}
