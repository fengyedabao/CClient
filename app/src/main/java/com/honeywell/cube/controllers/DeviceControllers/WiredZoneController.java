package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/20. 13:40
 * Email:Shodong.Sun@honeywell.com
 */
public class WiredZoneController {

    private static final String TAG = WiredZoneController.class.getSimpleName();

    /**
     * 处理编辑后返回的数据
     *
     * @param context
     * @param body
     * @throws JSONException
     */
    public static void handleWiredZoneConfigDeviceWithBody(Context context, JSONObject body) throws JSONException {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configType = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, MessageErrorCode.transferErrorCode(context, errorCode)));
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, MessageErrorCode.transferErrorCode(context, errorCode)));
            }
            return;
        }

        WiredZoneLoopFunc func = new WiredZoneLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configType)) {
            //add module
            PeripheralDevice moduleDevice = (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralByPrimaryId(body.getLong(CommonData.JSON_COMMAND_PRIMARYID));
            long dev_id = 0;
            if (moduleDevice != null) {
                dev_id = moduleDevice.mPrimaryID;
            }
            JSONArray array = body.getJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                WiredZoneLoop loop = new WiredZoneLoop();
                loop.mLoopSelfPrimaryId = object.optLong(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                loop.mModulePrimaryId = dev_id;
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                loop.mLoopId = object.optInt(CommonData.JSON_COMMAND_LOOPID);
                loop.mZoneType = object.optString(CommonData.JSON_COMMAND_ZONETYPE);
                loop.mAlarmType = object.optString(CommonData.JSON_COMMAND_ALARMTYPE);
                loop.mDelayTimer = object.optInt(CommonData.JSON_COMMAND_ALARMTIMER);
                loop.mIsEnable = object.optInt(CommonData.JSON_COMMAND_ALARMENABLE);
                func.addWiredZoneLoop(loop);
            }
        } else if ("delete".equalsIgnoreCase(configType)) {
            JSONArray array = body.getJSONArray("deviceloopmap");
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, "数据为空 ，操作失败"));
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                func.deleteWiredZoneLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            }
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, "操作成功"));
        } else if ("modify".equalsIgnoreCase(configType)) {
            JSONArray array = body.getJSONArray("deviceloopmap");
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, "数据为空 ，操作失败"));
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                WiredZoneLoop loop = func.getWiredZoneLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                func.updateWiredZoneLoopByPrimaryId(loop.mLoopSelfPrimaryId, loop);
            }
        }
        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_WIREDZONE, "操作成功"));
    }
}
