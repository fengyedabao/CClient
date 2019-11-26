package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.UIItem.notifi.NotifiFilterObject;
import com.honeywell.cube.controllers.UIItem.notifi.NotifiUIItem;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.http.HttpClientHelper;
import com.honeywell.cube.net.http.MyHttpResponseHandler;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeAccountEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeNotifiEvent;
import com.honeywell.cube.utils.plist_parser.xml.plist.domain.Array;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/7/12. 10:27
 * Email:Shodong.Sun@honeywell.com
 */
public class NotificationController {
    public static final String TAG = NotificationController.class.getSimpleName();
    private static int whichPage = 0;
    private static ArrayList<Map<String, Object>> notifiMap = new ArrayList<>();//用于页面显示数据
    private static int previousNum = 0;

    /**
     * 获取过滤选项的数组
     *
     * @param context
     * @return
     */
    public static ArrayList<NotifiFilterObject> getNotifiFilterList(Context context) {
        String[] list = {
                CommonData.ZONE_ALARM_STATUS_FIRE,
                CommonData.ZONE_ALARM_STATUS_HELP,
                CommonData.ZONE_ALARM_STATUS_GAS,
                CommonData.ZONE_ALARM_STATUS_THIEF,
                CommonData.ZONE_ALARM_STATUS_EMERGENCY,
        };
        ArrayList<NotifiFilterObject> returnValue = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            NotifiFilterObject object = new NotifiFilterObject();
            object.name = CommonUtils.transferZoneAlarmType(context, list[i]);
            object.type = list[i];
            returnValue.add(object);
        }
        return returnValue;
    }

    /**
     * 视频按钮点击
     *
     * @param context
     * @param uiItem
     */
    public static void videoButtonPressed(Context context, NotifiUIItem uiItem) {
        if (uiItem == null) {
            Loger.print(TAG, "ssd video button pressed uiitem is null", Thread.currentThread());
            return;
        }
        JSONObject CellDic = uiItem.CellDic;
        JSONObject content = CellDic.optJSONObject("Content");
        if (CellDic == null) {
            Loger.print(TAG, "ssd video button content is null ", Thread.currentThread());
            return;
        }
        PeripheralDeviceFunc func = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
        PeripheralDevice device = func.getPeripheralDeviceByMac(content.optString("moduleAddr"));
        if (device == null) {
            device = func.getPeripheralDeviceByIp(content.optString("moduleAddr"));
        }
        if (device == null) {
            Loger.print(TAG, "ssd video button device is null", Thread.currentThread());
        }

        ArrayList<Object> deviceArr = DeviceManager.getDeviceListFromDatabaseWithNameForArray(context, ModelEnum.MAIN_ZONE);
        Object zoneModel = null;
        if (deviceArr.size() > 0) {
            for (Object object : deviceArr) {
                BasicLoop basicLoop = (BasicLoop) object;
                if (device.mPrimaryID == basicLoop.mModulePrimaryId &&
                        content.optInt("loopId") == basicLoop.mLoopId) {
                    zoneModel = object;
                    break;
                }
            }
        }

        //是否找到IPC
        IpcStreamInfo info = DeviceManager.getIPCFromVideoRecordZone(context, zoneModel);
        if (info != null) {
            PeripheralDevice device1 = new PeripheralDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context)).getPeripheralDeviceByPrimaryId(info.mDevId);
            String time = CommonUtils.transformTimeZoneFromLocalZoneToNormalZone(CellDic.optString("Time"));
            String message = MessageManager.getInstance(context).sendIPCVideoButton(device1.mIpAddr, time);
            //发送远端请求
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        }
    }

    /**
     * 获取最新的数据
     *
     * @param context
     * @param filterObjects
     * @param isRefresh--是否是刷新
     */
    public static void requestNotification(final Context context, final ArrayList<NotifiFilterObject> filterObjects, final boolean isRefresh) {
        if (isRefresh) {
            whichPage = 0;
            notifiMap.clear();
        }


        String nowDate = CommonUtils.genISO8601TimeStampForCurrTime();
        final String agoDate = CommonUtils.genISO8601TimeStampFor1970();

        Map<String, Object> map = new HashMap<>();
        map.put("startTime", agoDate);
        map.put("endTime", nowDate);
        map.put("startNum", "" + (whichPage * ModelEnum.NOTIFICATION_PAGE_COUNT + 1));
        map.put("endNum", "" + ((whichPage + 1) * ModelEnum.NOTIFICATION_PAGE_COUNT));
        map.put("deviceId", "" + AppInfoFunc.getBindDeviceId(context));

        MyHttpResponseHandler responseHandler = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = "";
                if (bytes != null) {
                    str = new String(bytes);
                }
                Loger.print(TAG, "********** receive data : " + str, Thread.currentThread());
                if (str != null && !"".equalsIgnoreCase(str)) {
                    try {
                        JSONObject object = new JSONObject(str);
                        JSONArray array = object.optJSONArray("alarms");
                        if (array != null) {
                            whichPage++;
                            updateNotifiList(context, filterObjects, array, isRefresh);
                        } else {
                            if (isRefresh) {
                                EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_REFRESH));
                            } else {
                                EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_LOADMORE));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                super.onSuccess(i, headers, bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                String str = "";
                if (bytes != null) {
                    str = new String(bytes);
                }
                Loger.print(TAG, "ssd 获取通知 失败 信息 : " + str, Thread.currentThread());
                if (isRefresh) {
                    EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_REFRESH));
                } else {
                    EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_LOADMORE));
                }
                super.onFailure(i, headers, bytes, throwable);
            }
        };

        try {
            HttpClientHelper.newInstance().httpRequest(context, NetConstant.URI_ALARMHISTORY, map, HttpClientHelper.newInstance().COOKIE, responseHandler, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateNotifiList(Context context, ArrayList<NotifiFilterObject> filterObjects, JSONArray JSONArray, boolean isRefresh) throws JSONException {
        if (filterObjects == null || filterObjects.size() == 0) {
            if (isRefresh) {
                EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_REFRESH));
            } else {
                EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_LOADMORE));
            }
            return;
        }
        if (JSONArray == null || JSONArray.length() == 0) {
            if (isRefresh) {
                EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_REFRESH));
            } else {
                EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_LOADMORE));
            }
            return;
        }
        for (int i = 0; i < JSONArray.length(); i++) {
            //排除筛选下的一些Alarm信息
            boolean discardAlarm = false;
            JSONObject object = JSONArray.optJSONObject(i);
            if (object == null) continue;

            //最新一条报警
            if (whichPage == 0 && i == 0) {
                AppInfo info = AppInfoFunc.getCurrentUser(context);
                info.last_read_time = object.optString("Time");
                new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
            }

            JSONObject content = object.optJSONObject("Content");
            if (content == null) continue;
            for (NotifiFilterObject filterObject : filterObjects) {
                String alarmType = content.optString("alarmType");
                if (filterObject.type.equalsIgnoreCase(alarmType)) {
                    discardAlarm = true;
                    break;
                }
            }
            //不在需要范围内
            if (!discardAlarm) continue;

            String time = object.optString("Time");
            time = CommonUtils.transformTimeZoneFromNormalZoneToLocalZone(time);
            Loger.print(TAG, "ssd 更新通知数据 : " + time, Thread.currentThread());
            object.put("Time", time);

            String[] sections = time.split(" ");
            String section = sections[0];

            if (notifiMap.size() == 0) {
                ArrayList<JSONObject> JSONObjects = new ArrayList<>();
                JSONObjects.add(object);
                Map<String, Object> map = new HashMap<>();
                map.put("section", section);
                map.put("array", JSONObjects);
                notifiMap.add(map);
                continue;
            }

            //不属于最后一个区
            Map<String, Object> lastMap = notifiMap.get(notifiMap.size() - 1);
            if (!section.equalsIgnoreCase((String) lastMap.get("section"))) {
                ArrayList<JSONObject> JSONObjects = new ArrayList<>();
                JSONObjects.add(object);
                Map<String, Object> map = new HashMap<>();
                map.put("section", section);
                map.put("array", JSONObjects);
                notifiMap.add(map);
                continue;
            }

            //属于最后一个区
            ArrayList<JSONObject> list = (ArrayList<JSONObject>) lastMap.get("array");
            if (list == null) list = new ArrayList<JSONObject>();
            list.add(object);
            lastMap.put("array", list);
            notifiMap.remove(notifiMap.size() - 1);
            notifiMap.add(lastMap);

        }
        //解析UI层需要的数据
        ArrayList<NotifiUIItem> allValue = new ArrayList<>();
        //全部转换为需要的数据
        int upCount = 0;
        for (int i = 0; i < notifiMap.size(); i++) {
            Map<String, Object> map = notifiMap.get(i);
            String section = (String) map.get("section");
            String[] title_day = CommonUtils.getDayAndMonthFromDateString(section);

            ArrayList<JSONObject> list = (ArrayList<JSONObject>) map.get("array");
            //添加头
            NotifiUIItem uiItem = new NotifiUIItem();
            uiItem.item_type = ModelEnum.UI_TYPE_TITLE;//头
            uiItem.title_day = title_day[0];
            uiItem.title_month = title_day[1];
            uiItem.unReaded = AlarmController.getInstance(context).getUnReadAlarmCount() > upCount ? true : false;
            if (i == 0) {
                //头
                if (uiItem.unReaded) {
                    //未读
                    uiItem.image_id = R.mipmap.notification_table_header_blue;
                } else {
                    //已读
                    uiItem.image_id = R.mipmap.notification_table_header_grey;
                }
            } else {
                if (uiItem.unReaded) {
                    //未读
                    uiItem.image_id = R.mipmap.notification_section_blue;
                } else {
                    //已读
                    uiItem.image_id = R.mipmap.notification_section_grey;
                }
            }

            allValue.add(uiItem);
            for (int j = 0; j < list.size(); j++) {
                boolean unread = AlarmController.getInstance(context).getUnReadAlarmCount() > upCount ? true : false;
                JSONObject object = list.get(j);
                JSONObject content = object.optJSONObject("Content");
                NotifiUIItem cellItem = new NotifiUIItem();
                cellItem.item_type = ModelEnum.UI_TYPE_OTHER;
                cellItem.image_id = CommonUtils.getAlarmImageIdFromType(context, content.optString("alarmType"), unread);
                cellItem.unReaded = unread;
                cellItem.room_str = content.optString("roomName");
                String[] timeArray = object.optString("Time").split(" ");
                if (timeArray.length >= 2) {
                    cellItem.time_str = timeArray[1];
                } else {
                    Loger.print(TAG, "ssd ******* notification parameter time : " + object.optString("Time"), Thread.currentThread());
                }
                cellItem.title_cell = content.optString("loopName") + ": " + CommonUtils.transferZoneAlarmType(context, content.optString("alarmType"));
                cellItem.show_video_btn = "0".equalsIgnoreCase(content.optString("recordingVideo")) ? false : true;
                cellItem.CellDic = object;
                allValue.add(cellItem);
                upCount++;
            }
        }

        ArrayList<NotifiUIItem> returnValue = new ArrayList();
        upCount = 0;
        for (int i = allValue.size() - 1; i >= 0; i--) {
            NotifiUIItem item = allValue.get(i);
            if (item.item_type == ModelEnum.UI_TYPE_TITLE) {
                //如果是Title 就直接添加
                returnValue.add(0, item);
                continue;
            }
            if (upCount > JSONArray.length() - 1) break;
            upCount++;
            returnValue.add(0, item);
        }
        if (isRefresh) {
            EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_REFRESH, returnValue));
        } else {
            EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_LOADMORE, returnValue));
        }
    }


    /**
     * 处理视频播放
     */
    public static void handleResponceForIpcPlay(Context context, JSONObject data) {
        int errorcode = ResponderController.checkHaveOneFailWithBody(data);
        if (errorcode != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
            EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.NOTIFI_PLAY_IPC_VIDEO, false, MessageErrorCode.transferErrorCode(context, errorcode)));
            return;
        }
        EventBus.getDefault().post(new CubeNotifiEvent(CubeEvents.CubeNotifiEventType.NOTIFI_PLAY_IPC_VIDEO, true, context.getString(R.string.operation_success_tip)));
    }


}
