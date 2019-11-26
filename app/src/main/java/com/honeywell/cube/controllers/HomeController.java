package com.honeywell.cube.controllers;

import android.content.Context;
import android.util.Log;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.UIItem.HomeRoomDetailsUIItem;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoopFunc;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.RoomCustom;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.http.HttpClientHelper;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.lib.utils.ResourceUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by H157925 on 16/5/17. 09:46
 * Email:Shodong.Sun@honeywell.com
 */


public class HomeController {

    private static final String TAG = HomeController.class.getSimpleName();

    private static HttpClientHelper httpClientHelper = HttpClientHelper.newInstance();

    private volatile static HomeController controller;
    private WeatherResponceController mResponceController;
    private Context mContext;
    private static ArrayList<RoomLoop> updateRoomList = new ArrayList<>();//更新过房间状态的数组

    /**
     * 单例
     *
     * @param context
     * @return
     */
    public static HomeController getInstance(Context context) {
        if (controller == null) {
            synchronized (HomeController.class) {
                if (controller == null) {
                    controller = new HomeController(context);
                }
            }
        }
        return controller;
    }

    private HomeController(Context context) {
        mContext = context;
    }

    /**
     * 暴露给UI层接口，用于更新Cube 周边的数据，包括，相关天气，一周天气预报，报警数量，房间内部参数
     *
     * @param responceController
     */
    public void getCubeDetailData(WeatherResponceController responceController) {
        this.mResponceController = responceController;
        getCubeWeather();
        getWeekWeatherForecast();
        getAlarmCount();
        checkRoomEnvironment();
    }

    /**
     * 更新Home页面防护状态，SystemSceanrioSecurityState 具体参数回传看UI需求 字符，数字，或者图片
     * 通过Event事件 Scenario事件中 UPDATE_SYSTEM_SCENARIO_STATUS 事件处理接口
     *
     * @param context
     * @return
     */
    public int getScenarioSecurityStatusImageId(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) return -1;
        if (info.current_security_status == CommonData.ARM_TYPE_ENABLE) {
            //enable
            return R.mipmap.scenario_arm_select_02;
        } else {
            //disable
            return R.mipmap.scenario_disarm;
        }
    }

    /**
     * 获取 房间细节
     *
     * @param context
     * @param roomLoops
     * @return
     */
    private ArrayList<HomeRoomDetailsUIItem> getHomeRoomDetails(Context context, ArrayList<RoomLoop> roomLoops) {
        ArrayList<HomeRoomDetailsUIItem> homeRoomDetailsUIItems = new ArrayList<>();
        if (roomLoops == null || roomLoops.size() == 0) {
            return homeRoomDetailsUIItems;
        }
        for (RoomLoop loop : roomLoops) {
            RoomCustom roomCustom = loop.customModel;
            if (roomCustom.pm2_5 != -1 && roomCustom.pm2_5 != 0) {
                HomeRoomDetailsUIItem detailsUIItem = new HomeRoomDetailsUIItem();
                detailsUIItem.roomImageId = R.mipmap.home_pm2_5;
                detailsUIItem.roomTitle = loop.mRoomName;
                detailsUIItem.roomBackgroundImageId = ResourceUtil.getResIdFromName(context, loop.mImageName);
                detailsUIItem.name = context.getString(R.string.home_room_detail_pm2_5);
                detailsUIItem.type = ModelEnum.HOME_ROOM_TYPE_PM2_5;
                detailsUIItem.value = roomCustom.pm2_5;
                homeRoomDetailsUIItems.add(detailsUIItem);
            } else if (roomCustom.temperature != -1 && roomCustom.temperature != 0) {
                HomeRoomDetailsUIItem detailsUIItem = new HomeRoomDetailsUIItem();
                detailsUIItem.roomImageId = R.mipmap.home_temperature;
                detailsUIItem.roomTitle = loop.mRoomName;
                detailsUIItem.roomBackgroundImageId = ResourceUtil.getResIdFromName(context, loop.mImageName);
                detailsUIItem.name = context.getString(R.string.home_room_detail_temperature);
                detailsUIItem.type = ModelEnum.HOME_ROOM_TYPE_TEMPERATURE;
                detailsUIItem.value = roomCustom.temperature;
                homeRoomDetailsUIItems.add(detailsUIItem);
            } else if (roomCustom.humidity != -1 && roomCustom.humidity != 0) {
                HomeRoomDetailsUIItem detailsUIItem = new HomeRoomDetailsUIItem();
                detailsUIItem.roomImageId = R.mipmap.home_humidity;
                detailsUIItem.roomTitle = loop.mRoomName;
                detailsUIItem.roomBackgroundImageId = ResourceUtil.getResIdFromName(context, loop.mImageName);
                detailsUIItem.name = context.getString(R.string.home_room_detail_humidity);
                detailsUIItem.type = ModelEnum.HOME_ROOM_TYPE_HUMIDITY;
                detailsUIItem.value = roomCustom.humidity;
                homeRoomDetailsUIItems.add(detailsUIItem);
            } else if (roomCustom.co_2 != -1 && roomCustom.co_2 != 0) {
                HomeRoomDetailsUIItem detailsUIItem = new HomeRoomDetailsUIItem();
                detailsUIItem.roomImageId = R.mipmap.home_co_2;
                detailsUIItem.roomTitle = loop.mRoomName;
                detailsUIItem.roomBackgroundImageId = ResourceUtil.getResIdFromName(context, loop.mImageName);
                detailsUIItem.name = context.getString(R.string.home_room_detail_co2);
                detailsUIItem.type = ModelEnum.HOME_ROOM_TYPE_CO_2;
                detailsUIItem.value = roomCustom.co_2;
                homeRoomDetailsUIItems.add(detailsUIItem);
            }
        }
        return homeRoomDetailsUIItems;
    }

    /**
     * 更新房间状态，根据发送Socket命令后返回的数据跟新房间状态  用于其他Controller 更新数据
     *
     * @param roomLoopMap
     */
    public void updateRoomListState(Context context, JSONArray roomLoopMap) {
        updateRoomList.clear();
        for (int i = 0; i < roomLoopMap.length(); i++) {
            JSONObject object = roomLoopMap.optJSONObject(i);
            if (object == null) continue;
            if (object.optInt("errorcode") == MessageErrorCode.MESSAGE_ERROR_CODE_OK && object.optInt("value") > 0) {
                addRoomWithEnvironmentDictionary(mContext, object);
            }
        }
        ArrayList<HomeRoomDetailsUIItem> uiItems = getHomeRoomDetails(context, updateRoomList);
        updateRoomList.clear();
        if (mResponceController != null) {
            mResponceController.ResponceForUpdateRoomState(uiItems);
        }
    }


    /**
     * 根据查询返回的数据 进行数据更新
     *
     * @param context
     * @param object
     */
    private void addRoomWithEnvironmentDictionary(Context context, JSONObject object) {
        int roomid = object.optInt("roomid");
        //先检查是否添加过
        if (updateRoomList.size() > 0) {
            for (RoomLoop loop : updateRoomList) {
                if (roomid == loop.mPrimaryId) {
                    return;
                }
            }
        }
        //添加
        ArrayList<RoomLoop> allRoom = (ArrayList<RoomLoop>) RoomLoopFunc.getInstance(context).getRoomLoopAllList();
        for (RoomLoop loop : allRoom) {
            if (roomid == loop.mPrimaryId) {
                String type = object.optString("type");
                int value = object.optInt("value");
                if ("pm2_5".equalsIgnoreCase(type)) {
                    loop.customModel.pm2_5 = value;
                }
                if ("co_2".equalsIgnoreCase(type)) {
                    loop.customModel.co_2 = value;
                }
                if ("temperature".equalsIgnoreCase(type)) {
                    loop.customModel.temperature = value;
                }
                if ("humidity".equalsIgnoreCase(type)) {
                    loop.customModel.humidity = value;
                }
                updateRoomList.add(loop);
                return;
            }
        }
    }

    /**
     * 查询房间环境,发送 Socket 命令查询数据
     */
    private void checkRoomEnvironment() {
        if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_DISCONNECT)
            return;
        AppInfo info = null;
        if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            info = AppInfoFunc.getCurrentUser(mContext);
        } else if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            info = AppInfoFunc.getGuestUser(mContext);
        }
        if (info == null || info.online != 1) {
            //当前离线状态
            return;
        }
        Loger.print(TAG, "ssd check room environment start", Thread.currentThread());
        ArrayList<RoomLoop> roomLoops = (ArrayList<RoomLoop>) RoomLoopFunc.getInstance(mContext).getRoomLoopAllList();
        ArrayList<Map<String, Object>> roomloopMap = new ArrayList<>();
        for (RoomLoop loop : roomLoops) {
            Map<String, Object> item = new HashMap<>();
            item.put("roomid", "" + loop.mPrimaryId);
            item.put("type", "temperature");
            roomloopMap.add(item);

            Map<String, Object> item02 = new HashMap<>();
            item02.put("roomid", "" + loop.mPrimaryId);
            item02.put("type", "humidity");
            roomloopMap.add(item02);

            Map<String, Object> item03 = new HashMap<>();
            item03.put("roomid", "" + loop.mPrimaryId);
            item03.put("type", "pm2_5");
            roomloopMap.add(item03);

            Map<String, Object> item04 = new HashMap<>();
            item04.put("roomid", "" + loop.mPrimaryId);
            item04.put("type", "co_2");
            roomloopMap.add(item04);
        }
        if (roomloopMap.size() == 0) return;
        String message = MessageManager.getInstance(mContext).checkRoomStatus(roomloopMap);
        //发送远端请求
        CommandQueueManager.getInstance(mContext).addNormalCommandToQueue(message);
    }


    /**
     * 获取当前绑定CUBE报警的数量
     */
    private void getAlarmCount() {
        if (LoginController.getInstance(mContext).getLoginType() != LoginController.LOGIN_TYPE_CONNECT_CLOUD)
            return;
        int deviceid = AppInfoFunc.getBindDeviceId(mContext);
        if (deviceid <= 0) return;
        AppInfo info = AppInfoFunc.getCurrentUser(mContext);
        if (info == null) return;

        //如果不在线 定时查询
        if (info.online == 0) return;

        //获取未读报警条数
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startTime", info.last_read_time.length() > 0 ? info.last_read_time : CommonUtils.genISO8601TimeStampForCurrTime());
        map.put("deviceId", "" + deviceid);
        try {
            httpClientHelper.httpRequest(mContext, NetConstant.URI_ALARM_COUNT, map, "", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Loger.print(TAG, "ssd 读取报警数目 " + str, Thread.currentThread());
                    try {
                        JSONObject object = new JSONObject(str);
                        int count = object.optInt("count");
                        if (count > 0 && AlarmController.getInstance(mContext).getUnReadAlarmCount() < count) {
                            AlarmController.getInstance(mContext).setUnReadAlarmCount(count);
                            if (mResponceController != null) {
                                mResponceController.ResponceForAlarmCount(count);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    String str = "";
                    if (bytes != null) {
                        str = new String(bytes);
                    }
                    Loger.print(TAG, "get cube weather failed: " + str, Thread.currentThread());
                    throwable.printStackTrace();
                    if (mResponceController != null) {
                        mResponceController.ResponceForError(i, headers, bytes, throwable);
                    }
                }
            }, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //请求CUBE所在的城市
        map = new HashMap<String, Object>();
        map.put("type", "GetLocation");
        map.put("deviceId", "" + deviceid);
        try {
            httpClientHelper.httpRequest(mContext, NetConstant.URI_CUBE_LOCATION, map, "", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Loger.print(TAG, "ssd 获取Cube城市返回数据 " + str, Thread.currentThread());
                    try {
                        JSONObject object = new JSONObject(str);
                        String city = object.optString("name");
                        AppInfo info1 = AppInfoFunc.getCurrentUser(mContext);
                        info1.cube_location = city;
                        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(mContext)).updateAppInfoByUserName(info1.username, info1);
                        if (mResponceController != null) {
                            mResponceController.ResponceForCubeLocation(city);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    String str = "";
                    if (bytes != null) {
                        str = new String(bytes);
                    }
                    Loger.print(TAG, "get cube weather failed: " + str, Thread.currentThread());
                    throwable.printStackTrace();
                    if (mResponceController != null) {
                        mResponceController.ResponceForError(i, headers, bytes, throwable);
                    }
                }
            }, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送查看CUBE天气请求 Http请求
     */
    private void getCubeWeather() {
        int deviceId = AppInfoFunc.getBindDeviceId(mContext);
        if (deviceId < 0) return;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "GetWeather");
        map.put("deviceId", "" + deviceId);
        try {
            httpClientHelper.httpRequest(mContext, NetConstant.URI_CUBE_WEATHER, map, "", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Loger.print(TAG, "ssd URI_CUBE_WEATHER success " + str, Thread.currentThread());
                    try {
                        JSONObject object = new JSONObject(str);
                        if (mResponceController != null) {
                            mResponceController.ResponceForCubeWeather(object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                    if (mResponceController != null) {
                        mResponceController.ResponceForError(i, headers, bytes, throwable);
                    }
                }
            }, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送查询一周地天气 Http 请求
     */
    private void getWeekWeatherForecast() {
        int deviceId = AppInfoFunc.getBindDeviceId(mContext);
        if (deviceId < 0) return;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "GetForecast");
        map.put("deviceId", "" + deviceId);

        try {
            httpClientHelper.httpRequest(mContext, NetConstant.URI_CUBE_WEATHER, map, "", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String str = new String(bytes);
                    Loger.print(TAG, "ssd URI_WEATHER_LIST success  " + str, Thread.currentThread());
                    try {
                        JSONObject object = new JSONObject(str);
                        if (mResponceController != null) {
                            mResponceController.ResponceForWeatherInWeek(object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                    if (mResponceController != null) {
                        mResponceController.ResponceForError(i, headers, bytes, throwable);
                    }
                }
            }, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface WeatherResponceController {
        void ResponceForCubeWeather(JSONObject JSONObject);

        void ResponceForWeatherInWeek(JSONObject JSONObject);

        void ResponceForError(int i, Header[] headers, byte[] bytes, Throwable throwable);

        void ResponceForAlarmCount(int count);

        void ResponceForCubeLocation(String name);

        void ResponceForUpdateRoomState(ArrayList<HomeRoomDetailsUIItem> roomDetailsUIItems);

    }

}
