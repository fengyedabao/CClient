package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.UIItem.IPCameraInfo;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoopFunc;
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

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/15. 18:39
 * Email:Shodong.Sun@honeywell.com
 */
public class IPCameraController {

    public static final String TAG = IPCameraController.class.getSimpleName();

    /**
     * 打开IPC/关闭IPC 发送请求
     *
     * @param context
     * @param info
     * @param uuidStr
     * @param isOpen
     */
    public static void updateIPCWithInfo(Context context, IpcStreamInfo info, String uuidStr, boolean isOpen) {
        if (info == null) {
            Loger.print(TAG, "ssd updateIPCWithInfo ipc info is null", Thread.currentThread());
            return;
        }

        PeripheralDevice device = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(info.mDevId);
        if (device == null) {
            Loger.print(TAG, "ssd updateIPCWithInfo PeripheralDevice info is null", Thread.currentThread());
            return;
        }

        //获取本机IP地址
        String ipStr = CommonUtils.getLocalIpAddr(context);
        Loger.print(TAG, "ssd updateIPCWithInfo 本机Ip ： " + ipStr, Thread.currentThread());

        String message = MessageManager.getInstance(context).updateIPCInfo(info, device, uuidStr, ipStr, isOpen);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 处理收到的消息
     *
     * @param context
     * @param body
     */
    public static void handleResponceWithBody(Context context, JSONObject body) {
        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IPC_UPDATE, false, null, context.getString(R.string.error_operation_failed)));
            return;
        }

        String status = body.optString("status");
        if ("off".equalsIgnoreCase(status)) {
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IPC_UPDATE, false, null, context.getString(R.string.error_operation_failed)));
            return;
        }

        IPCameraInfo info = new IPCameraInfo();
        info.video_width = body.optInt("VideoWidth");
        info.video_height = body.optInt("VideoHeight");
        if (info.video_width == 0 || info.video_height == 0) {
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IPC_UPDATE, false, null, context.getString(R.string.error_operation_failed)));
            return;
        }
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.DEVICE_IPC_UPDATE, true, info, context.getString(R.string.error_operation_failed)));
    }

    /**
     * 处理 Config module
     *
     * @param context
     * @param body
     */
    public static void handleConfigModuleWithBody(Context context, JSONObject body) {
        //有错误码
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configType = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_IPC, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_IPC, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }

        }

        PeripheralDeviceFunc peripheralDeviceFunc = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
        IpcStreamInfoFunc ipcStreamInfoFunc = new IpcStreamInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configType)) {
            //module
            PeripheralDevice device = new PeripheralDevice();
            device.mPrimaryID = body.optInt("responseprimaryid");
            device.mType = ModelEnum.MODULE_TYPE_IPC;
            device.mName = body.optString("aliasname");
            device.mIpAddr = body.optString("moduleipaddr");
            peripheralDeviceFunc.addPeripheralDevice(device);

            //IPC
            IpcStreamInfo info = new IpcStreamInfo();
            info.mPrimaryId = body.optInt("subresponseprimaryid");
            info.mDevId = body.optInt("responseprimaryid");
            info.mRoomId = body.optInt(CommonData.JSON_COMMAND_ROOMID);
            info.mIpcType = body.optString("ipctype");
            info.mMainStream = body.optString("mainstream");
            info.mSubStream = body.optString("substream");
            info.mStreamPort = body.optInt("moduleport");
            info.mUser = body.optString("ipcusername");
            info.mPassword = body.optString("ipcpassword");
            ipcStreamInfoFunc.addIpcStreamInfo(info);
        } else if ("delete".equalsIgnoreCase(configType)) {
            IpcStreamInfo info = ipcStreamInfoFunc.getIpcStreamInfoByPrimaryId(body.optInt("primaryid"));
            peripheralDeviceFunc.deletePeripheralDeviceByPrimaryId(info.mDevId);
            ipcStreamInfoFunc.deleteIpcStreamInfoByPrimaryId(info.mPrimaryId);
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_IPC, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(configType)) {
            IpcStreamInfo info = ipcStreamInfoFunc.getIpcStreamInfoByPrimaryId(body.optInt("primaryid"));
            info.mRoomId = body.optInt(CommonData.JSON_COMMAND_ROOMID);
            PeripheralDevice device = peripheralDeviceFunc.getPeripheralDeviceByPrimaryId(info.mDevId);
            device.mName = body.optString("aliasname");
            ipcStreamInfoFunc.updateIpcStreamInfo(info);
            peripheralDeviceFunc.updatePeripheralDeviceByPrimaryId(device.mPrimaryID, device);
        }
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_IPC, "操作成功"));
    }
}
