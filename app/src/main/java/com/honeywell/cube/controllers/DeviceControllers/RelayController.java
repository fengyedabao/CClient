package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.WebsocketMessageController;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
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
 * Created by H157925 on 16/5/14. 10:56
 * Email:Shodong.Sun@honeywell.com
 */
public class RelayController {
    public static final String TAG = RelayController.class.getSimpleName();

    /**
     * 对给出的devicelist查询目前的状态
     *
     * @param context
     * @param relayLoops
     */
    public static void checkoutDeviceListState(Context context, ArrayList<RelayLoop> relayLoops) {
        ArrayList<Map<String, Object>> deviceLoopMap = new ArrayList<>();
        for (RelayLoop loop : relayLoops) {
            long dev_id = loop.mModulePrimaryId;
            PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
            if (mainDevice == null) {
                Loger.print(TAG, "ssd checkoutDeviceListState RelayController error", Thread.currentThread());
            }
            Map<String, Object> item = new HashMap<>();
            item.put("modulemacaddr", mainDevice.mMacAddr);
            item.put("loopid", loop.mLoopId);
            deviceLoopMap.add(item);
        }
        if (deviceLoopMap.size() > 0) {
            String message = MessageManager.getInstance(context).checkOutDeviceListStatus(deviceLoopMap, "relay", null);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }


    /**
     * 点击开关后，发送Relay的状态 type=0 关，type =1, 开
     *
     * @param context
     * @param loop
     * @param type
     */
    public static void sendRelayState(Context context, RelayLoop loop, int type) {
        long dev_id = loop.mModulePrimaryId;
        PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
        if (mainDevice == null) {
            Loger.print(TAG, "ssd sendRelayState error", Thread.currentThread());
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, null));
        }

        //set body
        loop.customStatus.status = (type == 1);

        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();

        Map<String, Object> loopDic = new HashMap<>();
        loopDic.put("status", DeviceManager.transferStatusFromBoolToStr(loop.mLoopType, loop.customStatus.status));
        loopDic.put("time", 0);
        loopDic.put("loopid", loop.mLoopId);
        loopDic.put("modulemacaddr", mainDevice.mMacAddr);
        deviceloopmap.add(loopDic);

        String message = MessageManager.getInstance(context).sendDeviceStatus(deviceloopmap, "relay", null);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 处理 Relay 读取设备后的返回数据
     *
     * @param body
     */
    public static void handleRelayReadDeviceWithBody(JSONObject body) {
        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            Loger.print(TAG, "handleRelayReadDeviceWithBody checkHaveOneSuccessWithBody error", Thread.currentThread());
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
    public static void handleRelayConfigDeviceWithBody(Context context, JSONObject body) throws JSONException {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configtype = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configtype)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_RELAY, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_RELAY, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }

        }

        RelayLoopFunc func = new RelayLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configtype)) {
            //add module
            PeripheralDevice moduleDevice = (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralByPrimaryId(body.getLong(CommonData.JSON_COMMAND_PRIMARYID));
            long dev_id = 0;
            if (moduleDevice != null) {
                dev_id = moduleDevice.mPrimaryID;
            }
            JSONArray array = body.getJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_RELAY, "操作失败"));
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                Loger.print(TAG, "handleRelayConfigDeviceWithBody for JSONObject" + object.toString(), Thread.currentThread());
                RelayLoop loop = new RelayLoop();
                loop.mLoopSelfPrimaryId = object.optLong(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                loop.mModulePrimaryId = dev_id;
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                loop.mSubDevId = object.optInt(CommonData.JSON_COMMAND_DEVID);
                loop.mLoopId = object.optInt(CommonData.JSON_COMMAND_LOOPID);
                loop.mTriggerTime = object.optInt(CommonData.JSON_COMMAND_TIMER);

                func.addRelayLoop(loop);
            }
        } else if ("delete".equalsIgnoreCase(configtype)) {
            //删除
            JSONArray array = body.getJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_RELAY, "数据为空"));
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                //删除数据--依照自有的primaryId,而不是模块的device_id
                func.deleteRelayLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            }
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_RELAY, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(configtype)) {
            JSONArray array = body.getJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_RELAY, "数据为空"));
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                RelayLoop loop = func.getRelayLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
                if (loop != null) {
                    loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                    loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                    //更新数据
                    func.updateRelayLoopByPrimaryId(loop.mLoopSelfPrimaryId, loop);
                } else {
                    Loger.print(TAG, "ssd relay is not exit", Thread.currentThread());
                }
            }
        }

        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_RELAY, "操作成功"));
    }
}
