package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;
import android.util.Log;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.WebsocketMessageController;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/14. 11:25
 * Email:Shodong.Sun@honeywell.com
 */
public class Wireless315M433MController {
    public static final String TAG = Wireless315M433MController.class.getSimpleName();

    /**
     * 根据给出的DeviceList发送请求，获取对应的状态信息
     *
     * @param context
     * @param loops
     */
    public static void checkoutDeviceListState(Context context, ArrayList<Wireless315M433MLoop> loops) {
        ArrayList<Map<String, Object>> deviceLoopMap = new ArrayList<>();
        for (Wireless315M433MLoop loop : loops) {
            if (loop.mLoopType == ModelEnum.LOOP_TYPE_CURTAIN_INT) {
                continue;
            }
            long dev_id = loop.mModulePrimaryId;
            PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
            if (mainDevice == null) {
                Loger.print(TAG, "ssd checkoutDeviceListState error", Thread.currentThread());
            }

            //只要MAIA
            if (loop.mDeviceType.equals("maia2")) {
                Map<String, Object> item = new HashMap<>();
                item.put("modulemacaddr", mainDevice.mMacAddr);
                item.put("deviceid", loop.mSubDevId);
                item.put("loopid", loop.mLoopId);
                item.put("looptype", DeviceManager.getDeviceTypeStringFromInt(loop.mLoopType));

                deviceLoopMap.add(item);
            }
        }

        if (deviceLoopMap.size() > 0) {
            Map<String, Object> item = new HashMap<>();
            item.put("devicetype", "maia2");
            String message = MessageManager.getInstance(context).checkOutDeviceListStatus(deviceLoopMap, CommonData.JSON_COMMAND_MODULETYPE_315M433, item);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }

    /**
     * 点击开关后，Wireless315M433M type=0 关，type =1, 开
     * 复用接口，当作为调光接口 value 大于0 type 为 1(打开状态)
     * 不可以存在type =0 value >0 或者type = 1,value <= 0 等状态
     *
     * @param context
     * @param loop
     * @param type
     * @param value
     */
    public static void sendWireless315M433MState(Context context, Wireless315M433MLoop loop, int type, int value) {
        //状态检测
        if ((type == 0 && value > 0) || (type == 1 && value < 0)) {
            Loger.print(TAG, "ssd sendWireless315M433MState 状态值不对", Thread.currentThread());
            return;
        }
        long dev_id = loop.mModulePrimaryId;
        PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
        if (mainDevice == null) {
            Loger.print(TAG, "ssd sendWireless315M433MState error", Thread.currentThread());
        }

        //set body
        loop.customStatus.status = (type == 1);
        loop.customStatus.openClosePercent = (value > 0 ? 0 : value);


        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();

        Map<String, Object> loopDic = new HashMap<>();
        loopDic.put("modulemacaddr", mainDevice.mMacAddr);
        loopDic.put("deviceid", loop.mSubDevId);
        loopDic.put("loopid", loop.mLoopId);
        loopDic.put("looptype", DeviceManager.getDeviceTypeStringFromInt(loop.mLoopType));
        loopDic.put("status", DeviceManager.transferStatusFromBoolToStr(loop.mLoopType, loop.customStatus.status));

        //是否是调光机器
        if (loop.mLoopType == ModelEnum.LOOP_TYPE_LIGHT_INT) {
            loopDic.put("openclosepercent", loop.customStatus.openClosePercent);
        }
        deviceloopmap.add(loopDic);

        Map<String, Object> subItem = new HashMap<>();
        subItem.put("devicetype", "maia2");
        String message = MessageManager.getInstance(context).sendDeviceStatus(deviceloopmap, "315M433M", subItem);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 发送窗帘的状态 2 = closing, 3 = stopped,其他的 ＝ opening
     *
     * @param context
     * @param loop
     * @param type
     */
    public static void sendCurtainStatus(Context context, Wireless315M433MLoop loop, int type) {
        long dev_id = loop.mModulePrimaryId;
        PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
        if (mainDevice == null) {
            Loger.print(TAG, "ssd getCurtainStatus error", Thread.currentThread());
        }

        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
        Map<String, Object> loopDic = new HashMap<>();
        loopDic.put("modulemacaddr", mainDevice.mMacAddr);
        loopDic.put("deviceid", loop.mSubDevId);
        loopDic.put("loopid", loop.mLoopId);
        loopDic.put("looptype", DeviceManager.getDeviceTypeStringFromInt(loop.mLoopType));
        loopDic.put("status", DeviceManager.getCurtainControlStatusWithType(type));
        deviceloopmap.add(loopDic);

        Map<String, Object> subItem = new HashMap<>();
        subItem.put("devicetype", "maia2");

        String message = MessageManager.getInstance(context).sendDeviceStatus(deviceloopmap, "315M433M", subItem);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 处理 315_433 读取设备后的返回数据
     *
     * @param body
     */
    public static void handle315433MReadDeviceWithBody(JSONObject body) {
        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            Loger.print(TAG, "handle315433MReadDeviceWithBody checkHaveOneSuccessWithBody error", Thread.currentThread());
            return;
        }

        JSONArray array = body.optJSONArray("deviceloopmap");
        DeviceController.updateDeviceStatusWithInfo(null, array, null);

    }


    /**
     * 处理编辑后返回的数据
     *
     * @param context
     * @param body
     * @throws JSONException
     */
    public static void handle315M433MConfigDeviceWithBody(Context context, JSONObject body) throws JSONException {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configType = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_315M433, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_315M433, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }

        }

        Wireless315M433MLoopFunc func = new Wireless315M433MLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configType)) {
            //add module
            PeripheralDevice moduleDevice = (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralByPrimaryId(body.getLong(CommonData.JSON_COMMAND_PRIMARYID));
            long dev_id = 0;
            if (moduleDevice != null) {
                dev_id = moduleDevice.mPrimaryID;
            }

            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_315M433, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                Wireless315M433MLoop loop = new Wireless315M433MLoop();
                loop.mLoopSelfPrimaryId = object.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                loop.mModulePrimaryId = dev_id;
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                loop.mLoopId = object.optInt(CommonData.JSON_COMMAND_LOOPID);
                loop.mZoneType = object.optString(CommonData.JSON_COMMAND_ZONETYPE);
                loop.mAlarmType = object.optString(CommonData.JSON_COMMAND_ALARMTYPE);
                loop.mDelayTimer = object.optInt(CommonData.JSON_COMMAND_ALARMTIMER);
                loop.mIsEnable = object.optInt(CommonData.JSON_COMMAND_ALARMENABLE);
                loop.mSubDevId = object.optInt(CommonData.JSON_COMMAND_DEVID);
                loop.mSerialnumber = object.optString(CommonData.JSON_COMMAND_SERIALNO);
                loop.mDeviceType = object.optString(CommonData.JSON_COMMAND_DEVICETYPE);
                loop.mLoopType = DeviceManager.getDeviceTypeIntFromString(object.optString(CommonData.JSON_COMMAND_LOOPTYPE));
                func.addWireless315M433MLoop(loop);
            }
        } else if ("delete".equalsIgnoreCase(configType)) {
            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_315M433, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                func.deleteWireless315M433MLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            }
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_315M433, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(configType)) {
            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_315M433, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                Wireless315M433MLoop loop = func.getWireless315M433MLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                func.updateWireless315M433M(loop);
            }
        }

        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_315M433, "操作成功"));
    }
}
