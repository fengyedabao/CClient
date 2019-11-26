package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.WebsocketMessageController;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485LoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
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
 * Created by H157925 on 16/5/14. 14:29
 * Email:Shodong.Sun@honeywell.com
 */
public class Wifi485Controller {
    public static final String TAG = Wifi485Controller.class.getSimpleName();

    /**
     * 根据给出的DeviceList发送请求，获取对应的状态信息
     *
     * @param context
     * @param loops
     */
    public static void checkoutDeviceListState(Context context, ArrayList<Wifi485Loop> loops) {
        ArrayList<Map<String, Object>> deviceLoopMap = new ArrayList<>();
        for (Wifi485Loop loop : loops) {
            long dev_id = loop.mModulePrimaryId;
            PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
            if (mainDevice == null) {
                Loger.print(TAG, "ssd checkoutDeviceListState error", Thread.currentThread());
            }
            Map<String, Object> item = new HashMap<>();
            item.put("modulemacaddr", mainDevice.mMacAddr);
            item.put("portid", loop.mPortId);
            item.put("slaveaddr", loop.mSlaveAddr);
            item.put("loopid", loop.mLoopId);
            item.put("looptype", loop.mLoopType);
            item.put("brandname", loop.mBrandName);
            deviceLoopMap.add(item);
        }

        if (deviceLoopMap.size() > 0) {
            String message = MessageManager.getInstance(context).checkOutDeviceListStatus(deviceLoopMap, "485", null);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }


    /**
     * 处理 485 读取设备后的返回数据
     *
     * @param body
     */
    public static void handle485ReadDeviceWithBody(JSONObject body) {
        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            Loger.print(TAG, "handle485ReadDeviceWithBody checkHaveOneSuccessWithBody error", Thread.currentThread());
            AirController.updateDeviceStatusWithInfo(null, null, ModelEnum.MODULE_TYPE_WIFI485);
            return;
        }
        Loger.print(TAG, "ssd read wifi 485 device status with info : " + body, Thread.currentThread());

        JSONArray array = body.optJSONArray("deviceloopmap");
        AirController.updateDeviceStatusWithInfo(null, array, ModelEnum.MODULE_TYPE_WIFI485);
        DeviceController.updateDeviceStatusWithInfo(null, array, null);
    }


    /**
     * 处理编辑后返回的数据
     *
     * @param context
     * @param body
     * @throws JSONException
     */
    public static void handleWifi485ConfigDeviceWithBody(Context context, JSONObject body) throws JSONException {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configType = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_485, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_485, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }
        }

        Wifi485LoopFunc func = new Wifi485LoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configType)) {
            //add module
            PeripheralDevice moduleDevice = (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralByPrimaryId(body.getLong(CommonData.JSON_COMMAND_PRIMARYID));
            long dev_id = 0;
            if (moduleDevice != null) {
                dev_id = moduleDevice.mPrimaryID;
            }

            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_485, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                Wifi485Loop loop = new Wifi485Loop();
                loop.mLoopSelfPrimaryId = object.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                loop.mModulePrimaryId = dev_id;
                loop.mBrandName = object.optString(CommonData.JSON_COMMAND_BRANDNAME);
                loop.mSubDevId = object.optInt(CommonData.JSON_COMMAND_DEVID);
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                loop.mLoopId = object.optInt(CommonData.JSON_COMMAND_LOOPID);
                loop.mPortId = object.optInt(CommonData.JSON_COMMAND_PORTID);
                loop.mSlaveAddr = object.optInt(CommonData.JSON_COMMAND_SLAVEADDR);
                func.addWifi485Loop(loop);
            }
        } else if ("delete".equalsIgnoreCase(configType)) {
            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_485, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                func.deleteWifi485LoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            }
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_485, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(configType)) {
            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_485, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                Wifi485Loop loop = func.getWifi485LoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                func.updateWifi485Loop(loop);
            }
        }

        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_485, "操作成功"));
    }
}
