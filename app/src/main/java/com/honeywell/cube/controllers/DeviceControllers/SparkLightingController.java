package com.honeywell.cube.controllers.DeviceControllers;

import android.content.Context;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.WebsocketMessageController;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
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
import com.honeywell.cube.utils.plist_parser.xml.plist.domain.Array;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/13. 16:06
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 所有有关SparkLighting的 设置
 */
public class SparkLightingController {
    public static final String TAG = SparkLightingController.class.getSimpleName();

    /**
     * 根据给出的DeviceList发送请求，获取对应的状态信息
     *
     * @param context
     * @param sparkLightingLoops
     */
    public static void checkoutDeviceListState(Context context, ArrayList<SparkLightingLoop> sparkLightingLoops) {
        ArrayList<Map<String, Object>> deviceLoopMap = new ArrayList<>();
        for (SparkLightingLoop loop : sparkLightingLoops) {
            if (loop.mLoopType == ModelEnum.LOOP_TYPE_CURTAIN_INT) {
                continue;
            }

            long dev_id = loop.mModulePrimaryId;
            PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
            if (mainDevice == null) {
                Loger.print(TAG, "ssd checkoutDeviceListState error", Thread.currentThread());
            }
            Map<String, Object> item = new HashMap<>();
            item.put("maskid", mainDevice.mMaskId);
            item.put("deviceid", loop.mSubDevId);
            item.put("loopid", loop.mLoopId);
            item.put("looptype", DeviceManager.getDeviceTypeStringFromInt(loop.mLoopType));

            deviceLoopMap.add(item);
        }

        //Body
        if (deviceLoopMap.size() > 0) {
            String message = MessageManager.getInstance(context).checkOutDeviceListStatus(deviceLoopMap, "sparklighting", null);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }


    /**
     * 点击开关后，发送SparkLighting的状态 type=0 关，type =1, 开 value 为针对调光时值的设置
     * 复用接口，当开关时value赋值为小于等于0的值，当为调光接口，type = 1, value > 0
     * 不可以存在type =0 value >0 或者type = 1,value <= 0 等状态
     *
     * @param loop
     * @param type
     */
    public static void sendSparkLightingState(Context context, SparkLightingLoop loop, int type, int value) {
        //状态检测
//        if ((type == 1 && value <= 0) )
//        {
//            Loger.print(TAG, "ssd sendWireless315M433MState 状态值不对", Thread.currentThread());
//            return;
//        }
        long dev_id = loop.mModulePrimaryId;
        PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
        if (mainDevice == null) {
            Loger.print(TAG, "ssd sendSparkLightingState error", Thread.currentThread());
        }

        //set body
        loop.customStatus.status = (type == 1);
        if (value > 0) {
            loop.customStatus.openClosePercent = value;
        }

        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();

        Map<String, Object> loopDic = new HashMap<>();
        loopDic.put("maskid", mainDevice.mMaskId);
        loopDic.put("deviceid", loop.mSubDevId);
        loopDic.put("loopid", loop.mLoopId);
        loopDic.put("looptype", DeviceManager.getDeviceTypeStringFromInt(loop.mLoopType));
        loopDic.put("status", DeviceManager.transferStatusFromBoolToStr(loop.mLoopType, loop.customStatus.status));

        //是否是调光机器
        if (loop.mLoopType == ModelEnum.LOOP_TYPE_LIGHT_INT) {
            loopDic.put("openclosepercent", loop.customStatus.openClosePercent);
        }
        deviceloopmap.add(loopDic);
        String message = MessageManager.getInstance(context).sendDeviceStatus(deviceloopmap, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, null);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 设置窗帘的状态 2 = closing, 3 = stopped,其他的 ＝ opening
     *
     * @param context
     * @param loop
     * @param type
     */
    public static void sendCurtainStatus(Context context, SparkLightingLoop loop, int type) {
        long dev_id = loop.mModulePrimaryId;
        PeripheralDevice mainDevice = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralByPrimaryId(dev_id);
        if (mainDevice == null) {
            Loger.print(TAG, "ssd sendSparkLightingState error", Thread.currentThread());
        }

        ArrayList<Map<String, Object>> deviceloopmap = new ArrayList<>();
        Map<String, Object> loopDic = new HashMap<>();
        loopDic.put("maskid", mainDevice.mMaskId);
        loopDic.put("deviceid", loop.mSubDevId);
        loopDic.put("loopid", loop.mLoopId);
        loopDic.put("looptype", CommonData.LOOP_TYPE_CURTAIN);
        loopDic.put("status", DeviceManager.getCurtainControlStatusWithType(type));
        deviceloopmap.add(loopDic);

        String message = MessageManager.getInstance(context).sendDeviceStatus(deviceloopmap, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, null);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /********************************* responce data ***********************************/
    /**
     * responce configdevice
     *
     * @param body
     */
    public static void handleSparkLightingConfigDeviceWithBody(Context context, JSONObject body) {
        //有错误码
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            if ("delete".equalsIgnoreCase(body.optString("configtype"))) {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, false, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            } else {
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, false, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, MessageErrorCode.transferErrorCode(context, errorCode)));
                return;
            }

        }

        try {
            if ("add".equalsIgnoreCase(body.optString("configtype"))) {
                //module
                PeripheralDevice moduleDevice = (new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getPeripheralByPrimaryId(body.getLong("primaryid"));
                long dev_id = 0;
                if (moduleDevice != null) {
                    dev_id = moduleDevice.mPrimaryID;
                }
                JSONArray array = body.optJSONArray("deviceloopmap");
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        Loger.print(TAG, "handleSparkLightingDeviceWithBody for JSONObject" + object.toString(), Thread.currentThread());
                        SparkLightingLoop sparkLightingLoop = new SparkLightingLoop();
                        sparkLightingLoop.mLoopSelfPrimaryId = object.optLong(CommonData.JSON_COMMAND_RESPONSEPRIMARYID);
                        sparkLightingLoop.mModulePrimaryId = dev_id;
                        sparkLightingLoop.mLoopName = object.optString(CommonData.JSON_COMMAND_ALIAS);
                        sparkLightingLoop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                        sparkLightingLoop.mSubDevId = object.optInt(CommonData.JSON_COMMAND_DEVID);
                        sparkLightingLoop.mSubDevType = object.optString(CommonData.JSON_COMMAND_SUBDEVTYPE);
                        sparkLightingLoop.mLoopId = object.optInt(CommonData.JSON_COMMAND_LOOPID);
                        sparkLightingLoop.mLoopType = DeviceManager.getDeviceTypeIntFromString(object.optString(CommonData.JSON_COMMAND_LOOPTYPE));
                        SparkLightingLoopFunc func = new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context));
                        func.addSparkLightingLoop(dev_id, sparkLightingLoop);
                    }
                }
            } else if ("delete".equalsIgnoreCase(body.optString("configtype"))) {
                JSONArray array = body.optJSONArray("deviceloopmap");
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        long primaryId = object.optLong("primaryid");
                        (new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).deleteSparkLightingLoopByPrimary(primaryId);
                    }
                }
                EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE, true, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, "操作成功"));
                return;
            } else if ("modify".equalsIgnoreCase(body.optString("configtype"))) {
                JSONArray array = body.optJSONArray("deviceloopmap");
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        long primaryId = object.optLong("primaryid");
                        SparkLightingLoop loop = (new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).getSparkLightingLoopByPrimaryId(primaryId);
                        loop.mLoopName = object.optString("aliasname");
                        loop.mRoomId = object.optInt(CommonData.JSON_COMMAND_ROOMID);
                        (new SparkLightingLoopFunc(ConfigCubeDatabaseHelper.getInstance(context))).updateSparkLightingLoopByPrimaryId(primaryId, loop);
                    }
                }
            }
            EventBus.getDefault().post(new CubeDeviceEvent(CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE, true, CommonData.JSON_COMMAND_MODULETYPE_SPARKLIGHT, "操作成功"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理 SparkLighting 读取设备后的返回数据
     *
     * @param body
     */
    public static void handleSparkLightingReadDeviceWithBody(JSONObject body) {
        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            Loger.print(TAG, "handleSparkLightingReadDeviceWithBody checkHaveOneSuccessWithBody error", Thread.currentThread());
            return;
        }

        JSONArray array = body.optJSONArray("deviceloopmap");
        DeviceController.updateDeviceStatusWithInfo(null, array, null);
    }

}
