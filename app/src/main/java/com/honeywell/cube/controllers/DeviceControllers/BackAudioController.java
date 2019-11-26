package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.WebsocketMessageController;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDeviceFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfoFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/14. 11:08
 * Email:Shodong.Sun@honeywell.com
 */
public class BackAudioController {
    public static final String TAG = BackAudioController.class.getSimpleName();

    /**
     * 根据给出的DeviceList发送请求，获取对应的状态信息
     *
     * @param context
     * @param backaudioLoops
     */
    public static void checkoutDeviceListState(Context context, ArrayList<BackaudioLoop> backaudioLoops) {
        for (BackaudioLoop loop : backaudioLoops) {
            long dev_id = loop.mModulePrimaryId;
            ArrayList<BackaudioDevice> devices = (ArrayList<BackaudioDevice>) (new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceAllList());
            BackaudioDevice device = new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceByPrimaryId(dev_id);
            if (device == null) {
                Loger.print(TAG, "ssd checkoutDeviceListState BackAudioController error", Thread.currentThread());
            }
            String message = MessageManager.getInstance(context).checkoutBackAudioStatus(device.mSerialNumber, loop.mLoopId);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }

    /**
     * 设置音量
     *
     * @param context
     * @param loop
     * @param value
     */
    public static void volumeValueChangedWithBody(Context context, BackaudioLoop loop, int value) {
        loop.customModel.volume = value;
        long dev_id = loop.mModulePrimaryId;
        BackaudioDevice device = new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceByPrimaryId(dev_id);
        if (device == null) {
            Loger.print(TAG, "ssd volumeValueChangedWithBody error", Thread.currentThread());
        }

        ArrayList<Map<String, Object>> control = new ArrayList<>();
        Map<String, Object> loopDic = new HashMap<>();
        loopDic.put("keytype", "volume");
        loopDic.put("keyvalue", value);
        control.add(loopDic);

        String message = MessageManager.getInstance(context).sendBackAudioLoopStatus(control, device, loop);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 设置backAudio各种状态
     *
     * @param context
     * @param loop
     * @param type
     */
    public static void setbackAudioStatusWithBody(Context context, BackaudioLoop loop, int type) {
        long dev_id = loop.mModulePrimaryId;
        BackaudioDevice device = new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getBackaudioDeviceByPrimaryId(dev_id);
        if (device == null) {
            Loger.print(TAG, "ssd setbackAudioStatusWithBody error", Thread.currentThread());
        }
        ArrayList<Map<String, Object>> control = new ArrayList<>();

        //开始
        if (type == ModelEnum.BACKAUDIO_STATUS_START) {
            loop.customModel.power = "on";
            loop.customModel.playstatus = "play";
            Map<String, Object> loopDic = new HashMap<>();
            loopDic.put("keytype", "power");
            loopDic.put("keyvalue", "on");

            Map<String, Object> loopDic2 = new HashMap<>();
            loopDic2.put("keytype", "playstatus");
            loopDic2.put("keyvalue", "play");

            control.add(loopDic);
            control.add(loopDic2);
        }
        //暂停
        else if (type == ModelEnum.BACKAUDIO_STATUS_PAUSE) {
            loop.customModel.playstatus = "pause";

            Map<String, Object> loopDic = new HashMap<>();
            loopDic.put("keytype", "playstatus");
            loopDic.put("keyvalue", "pause");
            control.add(loopDic);
        }
        //上一首
        else if (type == ModelEnum.BACKAUDIO_STATUS_PREVIOUS) {
            loop.customModel.playstatus = "play";

            Map<String, Object> loopDic = new HashMap<>();
            loopDic.put("keytype", "switchsong");
            loopDic.put("keyvalue", "previous");
            control.add(loopDic);
        }
        //下一首
        else if (type == ModelEnum.BACKAUDIO_STATUS_NEXT) {
            loop.customModel.playstatus = "play";

            Map<String, Object> loopDic = new HashMap<>();
            loopDic.put("keytype", "switchsong");
            loopDic.put("keyvalue", "next");
            control.add(loopDic);
        }
        //静音
        else if (type == ModelEnum.BACKAUDIO_STATUS_MUTE) {
            loop.customModel.mute = "on";

            Map<String, Object> loopDic = new HashMap<>();
            loopDic.put("keytype", "mute");
            loopDic.put("keyvalue", "on");
            control.add(loopDic);
        }
        //取消静音
        else if (type == ModelEnum.BACKAUDIO_STATUS_NO_MUTE) {
            loop.customModel.mute = "off";

            Map<String, Object> loopDic = new HashMap<>();
            loopDic.put("keytype", "mute");
            loopDic.put("keyvalue", "off");
            control.add(loopDic);
        }

        for (Map<String, Object> item : control) {
            ArrayList<Map<String, Object>> sendArr = new ArrayList<>();
            sendArr.add(item);
            String message = MessageManager.getInstance(context).sendBackAudioLoopStatus(sendArr, device, loop);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }

    }

    /**
     * 处理 BackAudio 读取设备后的返回数据
     *
     * @param body
     */
    public static void handleBackAudioReadDeviceWithBody(JSONObject body) {
        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            Loger.print(TAG, "handleBackAudioReadDeviceWithBody checkHaveOneSuccessWithBody error", Thread.currentThread());
            return;
        }
        JSONArray array = new JSONArray();
        array.put(body);
        DeviceController.updateDeviceStatusWithInfo(null, array, null);
    }

    public static void handleBackAudioSetDeviceWithBody(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }
//        JSONArray array = body.optJSONArray("keytypeloop");
//        if (array == null || array.length() == 0) {
//            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, "请求超时"));
//            return;
//        }
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject object = array.optJSONObject(i);
//            if (object.optInt("errorcode") != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
//                EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, "请求超时"));
//                return;
//            }
//        }
        EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, true, null));
    }


    /**
     * 处理编辑后返回的数据
     *
     * @param context
     * @param body
     * @throws JSONException
     */
    public static void handleBackAudioConfigDeviceWithBody(Context context, JSONObject body) throws JSONException {

        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        String configType = body.optString(CommonData.JSON_COMMAND_CONFIGTYPE);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(configType)) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }
        }

        BackaudioLoopFunc func = new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(configType)) {
            //add module
            PeripheralDevice moduleDevice = (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralByPrimaryId(body.getLong(CommonData.JSON_COMMAND_PRIMARYID));
            long dev_id = 0;
            if (moduleDevice != null) {
                dev_id = moduleDevice.mPrimaryID;
            }

            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                BackaudioLoop loop = new BackaudioLoop();
                loop.mLoopSelfPrimaryId = object.optInt(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                loop.mModulePrimaryId = dev_id;
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                loop.mLoopId = object.optInt(CommonData.JSON_COMMAND_LOOPID);
                func.addBackaudioLoop(loop);
            }
        } else if ("delete".equalsIgnoreCase(configType)) {
            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                func.deleteBackaudioLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
            }
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(configType)) {
            JSONArray array = body.optJSONArray(CommonData.JSON_COMMAND_DEVLOOPMAP);
            if (array == null || array.length() == 0) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "数据为空 ，操作失败"));
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                BackaudioLoop loop = func.getBackaudioLoopByPrimaryId(object.optLong(CommonData.JSON_COMMAND_PRIMARYID));
                loop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                func.updateBackaudioLoop(loop);
            }
        }
        //发送通知，通知界面更新
        EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_BACKAUDIO, "操作成功"));
    }


    /**
     * 处理后台Websocket接受到backAudio更新的消息
     *
     * @param context
     * @param body
     * @throws JSONException
     */
    public static void handleBackAudioUpdateInfoWithBody(Context context, JSONObject body) throws JSONException {
        JSONArray array = new JSONArray();
        array.put(body);
        DeviceController.updateBackAudioInfoFromEvent(context, array);
    }

    /**
     * 处理 back audio Config module responce
     *
     * @param context
     * @param body
     */
    public static void handleBackAudioConfigModuleWithBody(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }
        BackaudioDeviceFunc backaudioDeviceFunc = new BackaudioDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if ("add".equalsIgnoreCase(body.optString("configtype"))) {
            //Back audio device
            BackaudioDevice backaudioDevice = new BackaudioDevice();
            backaudioDevice.mPrimaryID = body.optInt("responseprimaryid");
            backaudioDevice.mSerialNumber = body.optString("moduleserialnum");
            backaudioDevice.mName = body.optString("aliasname");
            backaudioDevice.mMachineType = body.optInt("machinetype");
            backaudioDevice.mloopNum = body.optInt("loopnum");
            backaudioDevice.mIsOnline = body.optInt("isonline");
            //先删除
            backaudioDeviceFunc.getBackaudioDeviceBySerialNumber(backaudioDevice.mSerialNumber);
            //后添加
            backaudioDeviceFunc.addBackaudioDevice(backaudioDevice);
        } else if ("delete".equalsIgnoreCase(body.optString("configtype"))) {
            BackaudioDevice device = backaudioDeviceFunc.getBackaudioDeviceByPrimaryId(body.optInt("primaryid"));
            //删除关联的设备
            new BackaudioLoopFunc(ConfigCubeDatabaseHelper.getInstance(context)).deleteBackaudioLoopByDevId(device.mPrimaryID);
            //删除模块
            backaudioDeviceFunc.deleteBackaudioDeviceByPrimaryId(device.mPrimaryID);
            EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE, true, "操作成功"));
            return;
        } else if ("modify".equalsIgnoreCase(body.optString("configtype"))) {
            BackaudioDevice device = backaudioDeviceFunc.getBackaudioDeviceByPrimaryId(body.optInt("primaryid"));
            device.mName = body.optString("aliasname");
            backaudioDeviceFunc.updateBackaudioDeviceByPrimaryId(device.mPrimaryID, device);
        }
        EventBus.getDefault().post(new CubeModuleEvent(CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE, true, "操作成功"));
    }
}
