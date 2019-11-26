package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.DeviceControllers.BackAudioController;
import com.honeywell.cube.controllers.DeviceControllers.BacnetController;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.DeviceControllers.RelayController;
import com.honeywell.cube.controllers.DeviceControllers.SparkLightingController;
import com.honeywell.cube.controllers.DeviceControllers.Wifi485Controller;
import com.honeywell.cube.controllers.DeviceControllers.Wireless315M433MController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoopFunc;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.RoomManager;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRoomEvent;
import com.honeywell.cube.utils.events.CubeScheduleEvent;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/1. 13:42
 * Email:Shodong.Sun@honeywell.com
 */
public class RoomController {
    private static final String TAG = RoomController.class.getSimpleName();

    /**
     * UI 获取首页房间列表
     *
     * @param context
     * @return
     */
    public static void getAllRoomList(Context context) {
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.GET_ROOM_LIST, false, null));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.GET_ROOM_LIST, false, null));
                return;
            }
        }
        ArrayList<RoomLoop> loops = CommonCache.getRoomListLoop(context);
        if (loops.size() == 0) {
            EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.GET_ROOM_LIST, false, null));
        } else {
            for (RoomLoop loop : loops) {
                loop.mRoomName = RoomManager.checkDefaultNameWithProtocolName(context, loop.mRoomName);
            }
            EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.GET_ROOM_LIST, true, loops));
        }
    }

    /**
     * UI 使用房间名称来请求数据，获取房间内设备列表,并获取设备状态
     *
     * @param context
     * @param roomid
     */
    public static void getRoomDeviceStateWithRoomName(Context context, long roomid) {
        Loger.print(TAG, "ssd get room device", Thread.currentThread());
        Map<String, Object> items = DeviceManager.getRoomDeviceListFromDatabaseForMap(context, (int) roomid);
        if (items.size() == 0) {
            EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.UPDATE_ROOM_DEVICE_STATE, false, "无设备"));
            return;
        }
        //获取设备状态
        DeviceController.getDeviceStatusWithDeviceList(context, items, true);
    }


    /**
     * UI 删除房间 根据roomLoop删除
     *
     * @param context
     * @param loop
     */
    public static void deleteRoomWithRoomLoop(Context context, RoomLoop loop) {
        if (loop == null) {
            Loger.print(TAG, "ssd deleteRoomWithRoomLoop loop is null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).deleteRoomWithId(loop.mPrimaryId);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * UI 编辑或者增加ROOM
     *
     * @param context
     * @param loop
     */
    public static void addOrEditRoomWithInfo(Context context, RoomLoop loop) {
        //无网络状态 不返回数据
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.CONFIG_ROOM_STATE, false, context.getString(R.string.error_time_out)));
            return;
        }
        //云端登录 设备不在线 不返回数据
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info == null || info.online != 1) {
                EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.CONFIG_ROOM_STATE, false, context.getString(R.string.error_offline)));
                return;
            }
        }
        if (loop == null) {
            Loger.print(TAG, "ssd addOrEditRoomWithInfo loop is null", Thread.currentThread());
            return;
        }
        boolean isEdit = false;
        if (loop.mPrimaryId > 0) {
            isEdit = true;
        } else {
            isEdit = false;
        }

        loop.mRoomName = RoomManager.getProtocolNameWithInfo(context, loop.mRoomName);

        String message = MessageManager.getInstance(context).editOrAddRoom(isEdit, loop);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /************** response 接口 ************************/
    /**
     * 处理Configure 房间 后回调接口
     *
     * @param context
     * @param data
     */
    public static void handleResponceRoomConfigModuleWithBody(Context context, JSONObject data) throws JSONException {
        int errorcode = ResponderController.checkHaveOneFailWithBody(data);
        if (errorcode != 0) {
            EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.CONFIG_ROOM_STATE, false, MessageErrorCode.transferErrorCode(context, errorcode)));
            return;
        }

        RoomLoopFunc func = RoomLoopFunc.getInstance(context);
        if (data.getString(CommonData.JSON_COMMAND_CONFIGTYPE).equals("add")) {
            RoomLoop loop = new RoomLoop();
            loop.mPrimaryId = data.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
            loop.mRoomName = data.optString(CommonData.JSON_COMMAND_ALIAS);
            loop.mImageName = data.optString(CommonData.JSON_COMMAND_IMAGENAME);
            func.addRoomLoop(loop);
        } else if (data.getString(CommonData.JSON_COMMAND_CONFIGTYPE).equals("delete")) {
            long primaryId = data.optLong("primaryid");
            func.deleteRoomByPrimaryId(primaryId);
        } else if (data.getString(CommonData.JSON_COMMAND_CONFIGTYPE).equals("modify")) {
            int primaryId = data.optInt(CommonData.JSON_COMMAND_PRIMARYID);
            RoomLoop loop = func.getRoomLoopByPrimaryId(primaryId);
            if (loop != null) {
                loop.mRoomName = data.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mImageName = data.optString(CommonData.JSON_COMMAND_IMAGENAME);
                Loger.print(TAG, "ssd 111111111 id " + primaryId + " Room loop name " + loop.mRoomName + " , image name : " + loop.mImageName, Thread.currentThread());
                func.updateRoomLoopByPrimaryId((long) primaryId, loop);
            } else {
                Loger.print(TAG, "ssd handle Responce RoomConfig DeviceWithBody modify loop is  null", Thread.currentThread());
            }

        }
        //缓存数据
        CommonCache.updateRooomList(context);
        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeRoomEvent(CubeEvents.CubeRoomEventType.CONFIG_ROOM_STATE, true, null));
    }

    /**
     * 处理read 房间内设备状态
     *
     * @param data
     * @throws JSONException
     */
    public static void handleResponceRoomReadDeviceWithBody(Context context, JSONObject data) throws JSONException {
        if (!ResponderController.checkHaveOneSuccessWithBody(data)) {
            Loger.print(TAG, "handleBacnetReadDeviceWithBody checkHaveOneSuccessWithBody error", Thread.currentThread());
            return;
        }
        JSONArray array = data.optJSONArray("roomloopmap");
        HomeController.getInstance(context).updateRoomListState(context, array);
    }
}
