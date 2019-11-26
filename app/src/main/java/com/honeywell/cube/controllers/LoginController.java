package com.honeywell.cube.controllers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeDeviceFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.Socket.SocketController;
import com.honeywell.cube.net.http.HttpClientHelper;
import com.honeywell.cube.net.http.MyHttpResponseHandler;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.net.queue.ReceiverQueueManager;
import com.honeywell.cube.net.webSocket.cube_websocket.CubeAutoBahnWebsocketClient;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PlistUtil;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;
import com.honeywell.lib.utils.LogUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;


import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/4/28. 13:57
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这类  负责 登陆注册相关的业务逻辑
 */
public class LoginController {
    public static final int LOGIN_TYPE_DISCONNECT = 0;
    public static final int LOGIN_TYPE_CONNECT_WIFI = 1;
    public static final int LOGIN_TYPE_CONNECT_CLOUD = 2;
    private static final String TAG = LoginController.class.getSimpleName();


    public Context context;
    public Boolean isUpdateConfig = false;

    //本地登陆还是远程登陆 （0:未登陆， 1:本地登陆  2:云端登陆)
    private int loginType = LOGIN_TYPE_DISCONNECT;
    private String userName = null;
    private String password = null;
    private String phone_prefix = null;
    private String token = null;
    /**
     * 事件处理回调接口
     */
    private MyHttpResponseHandler handlerForLoginWithNameAndPwd = null;//使用用户名密码登录处理接口
    private MyHttpResponseHandler handlerForRequestCubeDevices = null;//请求Cube 设备列表时回调接口
    private MyHttpResponseHandler handlerForRequestValidNum = null;//获取手机验证码处理
    private MyHttpResponseHandler handlerForRequestResetPwd = null;//重置密码处理
    private MyHttpResponseHandler handlerForRequestRegister = null;//注册接口处理
    private HttpClientHelper httpClientHelper = HttpClientHelper.newInstance();


    private volatile static LoginController loginController = null;

    private LoginController(Context context) {
        this.context = context;
        initResponceHandler();
    }

    public static LoginController getInstance(Context context) {
        if (loginController == null) {
            synchronized (LoginController.class) {
                if (loginController == null) {
                    loginController = new LoginController(context);
                }
            }
        }
        return loginController;
    }

    /**
     * 返回当前登陆状态
     */
    public int getLoginType() {
        return loginType;

    }

    public void setLoginType(int type) {
        loginType = type;
    }

    /**
     * 开启登陆 进入登陆界面后 可以调用这个接口
     */
    public void startLogin() {
        Loger.print(TAG, "ssd start login", Thread.currentThread());
        if (!CommonUtils.isConnectNetwork(context)) {
            Loger.print(TAG, "ssd start login wifi is not connect", Thread.currentThread());
            //TODO 发出EVent事件，提示当前没有网络连接
        }
        //如果之前是WI-FI连接，直接登陆WI-FI
        if (getLoginType() == LOGIN_TYPE_CONNECT_WIFI) {
            SocketController.newInstance(context).loginToTCPSocket();
            return;
        }

        //如果之前纪录了登陆账号和密码
        final String[] userInfo = PreferenceUtil.getUserInfo(context);
        String name = userInfo[0];
        String pwd = userInfo[1];
        if (!"".equalsIgnoreCase(name) && !"".equalsIgnoreCase(pwd)) {
//            //直接登陆Websocket
//            loginSucessAndStartConnect(null);
            //云端登陆
            loginHttpWithNameAndPassword(name, pwd, "+86", token == null ? "" : token);
        }
    }

    /**
     * 远程登陆行为，使用用户名，密码，
     *
     * @param userName
     * @param pwd
     * @param phonePrefix----中国为+86
     */
    public void loginHttpWithNameAndPassword(String userName, String pwd, String phonePrefix, String token) {
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_WITH_NAME_AND_PWD, false, context.getString(R.string.error_time_out)));
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "LoginUser");
        map.put("password", pwd);
        map.put("phoneNumber", phonePrefix + userName);
        map.put("phoneUuid", CommonUtils.getIMEI(this.context));
        map.put("phoneType", "android");
        map.put("pushVendor", "Xinge");
        map.put("pushId", token);
        map.put("language", "zh-CN");
        try {
            this.userName = userName;
            this.password = pwd;
            this.phone_prefix = phonePrefix;
            this.token = token;

            httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.NO_COOKIE, handlerForLoginWithNameAndPwd, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Http请求，用于获取手机验证码, 注册或者忘记密码
     * 好像这个接口服务器端有BUG，根本不判断该手机号是否注册过就发来验证码，找回密码这么操作就是浪费
     *
     * @param phoneNum
     * @param phonePrefix--中国手机前端记得配上+86，这个要根据国家来添加，依据是PhoneCountry.plist
     */
    public void loginHttpWithDataForValidatenum(String phoneNum, String phonePrefix) {
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_REQUEST_VALID_NUM, false, context.getString(R.string.error_time_out)));
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "SendVCode");
        map.put("language", "zh-CN");
        map.put("phoneNumber", phonePrefix + phoneNum);
        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.COOKIE, handlerForRequestValidNum, HttpClientHelper.POST);
        } catch (Exception e) {
            Loger.print(TAG, "ssd loginHttpWithDataForValidatenum exception", Thread.currentThread());
            e.printStackTrace();
        }
    }

    /**
     * Http请求，用于重置密码
     *
     * @param phoneNum
     * @param checkCode
     * @param newPwd
     * @param phonePrefix--中国手机前端记得配上+86，这个要根据国家来添加，依据是PhoneCountry.plist
     */
    public void loginHttpWithDataForResetPwd(String phoneNum, String checkCode, String newPwd, String phonePrefix) {
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_RESET_PWD, false, context.getString(R.string.error_time_out)));
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "ResetPassword");
        map.put("phoneVCode", checkCode);
        map.put("phoneNumber", phonePrefix + phoneNum);
        map.put("newPassword", newPwd);
        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.NO_COOKIE, handlerForRequestResetPwd, HttpClientHelper.POST);
        } catch (Exception e) {
            Loger.print(TAG, "ssd loginHttpWithDataForValidatenum exception", Thread.currentThread());
            e.printStackTrace();
        }
    }


    /**
     * 注册使用接口
     *
     * @param phoneNum
     * @param checkCode
     * @param password
     * @param phonePrefix--中国手机前端记得配上+86，这个要根据国家来添加，依据是PhoneCountry.plist
     */
    public void loginHttpWithDataForRegister(String phoneNum, String checkCode, String password, String phonePrefix) {
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_REGISTER, false, context.getString(R.string.error_time_out)));
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "RegisterUser");
        map.put("phoneVCode", checkCode);
        map.put("language", "zh-CN");
        map.put("name", phoneNum);
        map.put("password", password);
        map.put("phoneNumber", phonePrefix + phoneNum);

        try {
            this.userName = phoneNum;
            this.password = password;
            this.phone_prefix = phonePrefix;
            httpClientHelper.httpRequest(context, NetConstant.URI_USER_LIST, map, httpClientHelper.NO_COOKIE, handlerForRequestRegister, HttpClientHelper.POST);
        } catch (Exception e) {
            Loger.print(TAG, "ssd loginHttpWithDataForValidatenum exception", Thread.currentThread());
            e.printStackTrace();
        }
    }


    /**
     * 登陆成功后，更新当前用户的CUBE 信息
     *
     * @param device
     */
    public void loginWithUpdateAppInfo(CubeDevice device) {
        AppInfoFunc func = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info != null) {
            info.deviceId = device.mDeviceId;
            info.cube_local_id = device.mInfo_serialNumber;
            func.updateAppInfoByUserName(info.username, info);

            loginSucessAndStartConnect(info);
        } else {
            Loger.print(TAG, "ssd loginWithGetCubeDevicesInfo getCurrentUser err", Thread.currentThread());
        }
    }

    /**
     * 开启WebSocket或者Socket连接
     *
     * @param info
     */
    public void loginSucessAndStartConnect(AppInfo info) {
        if (info == null) {
            info = AppInfoFunc.getCurrentUser(context);
        }

        if (SocketController.isProcessRun) {
            SocketController.newInstance(context).stopSocketConnect("websocket start");
        }
        //开启WebSocket
        (CubeAutoBahnWebsocketClient.getInstance(context)).startConnectServer(info);
    }


    /**
     * 获取本地数据库中所有的Cube设备
     *
     * @return
     */
    public ArrayList<CubeDevice> getAllCubeDeviceFromDataBase() {
        return (ArrayList<CubeDevice>) (new CubeDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context))).getAllCubeDevices();
    }

    /**
     * 获取本地国家信息，信息来源 PhoneCountry.plist
     * Array中每一个Map结构
     * key : name
     * key : phone_prefix
     */
    public ArrayList<Object> getPhoneCountryMapList(Context context) {
        return PlistUtil.parseArrayPlistWithName("PhoneCountry.plist");
    }

    /**
     * 左侧菜单栏 底部点击  登出 时调用接口
     *
     * @param context
     */
    public synchronized void logout(Context context) {
        Log.e(TAG,"logout",new Exception());
        if (getLoginType() == LOGIN_TYPE_CONNECT_WIFI) {
            AppInfo info = AppInfoFunc.getGuestUser(context);
            if (info != null) {
                info.cube_location = "";
                info.cube_local_nickname = "";
                info.cube_local_password = "";
                info.cube_local_id = "0";
                info.last_read_time = "";
                info.online = 0;

                new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
                SocketController.newInstance(context).stopSocketConnect("normal logout");
            }
        } else {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            if (info != null && !"0".equalsIgnoreCase(info.cube_local_id)) {
                //系统
                info.database_version = "0";
                info.all_header_fields_cookie = "";

                //用户
                info.password = "";
                info.nickname = "";
                info.phoneId = 0;
                info.deviceId = 0;

                //cube
                info.cube_location = "";
                info.current_scenario_id = 1;
                info.cube_local_nickname = "";
                info.cube_local_password = "";
                info.cube_local_id = "0";
                info.last_read_time = "";

                info.online = 0;
                new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
                //这里没有清理数据库 感觉没必要

                //停止网络请求
                CubeAutoBahnWebsocketClient.getInstance(context).stopConnection("normal logout");
            }
        }
        PreferenceUtil.clearUserLoginInfo(context);
        unregisterXG(context);
        setLoginType(LOGIN_TYPE_DISCONNECT);
        CommandQueueManager.getInstance(context).stopRun();
        ReceiverQueueManager.getInstance(context).stopRun();
        EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGOUT, true, null));
    }

    public static void unregisterXG(Context context) {
        XGPushManager.unregisterPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                LogUtil.e(TAG, "unregisterPush onSuccess " + o, true);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtil.e(TAG, "unregisterPush onFail " + o, true);
            }
        });
    }

    /**
     * 判断目前是否登陆,不区分远端登陆或者本地登陆
     */
    public static boolean islogin() {
        if (loginController == null) {
            return false;
        }
        if (loginController.loginType == LOGIN_TYPE_DISCONNECT) {
            return false;
        }
        return true;
    }

    /**
     * 获取配置信息
     */
    public synchronized void updateCubeConfig() {
        isUpdateConfig = true;
        //添加获取信息
        String message = MessageManager.getInstance(context).getCubeInfoFromLocal(context);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 检查CUBE 在线状态
     */
    public void checkCubeOnlineStatus() {
        if (getLoginType() != LOGIN_TYPE_CONNECT_CLOUD) return;
        Loger.print(TAG, "ssd check cube online state", Thread.currentThread());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "QueryOnline");
        map.put("deviceId", "" + AppInfoFunc.getBindDeviceId(context));
        MyHttpResponseHandler responseHandler = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                super.onSuccess(i, headers, bytes);
                try {
                    String str = new String(bytes);
                    Loger.print(TAG, "查询是否在线:" + str, Thread.currentThread());
                    if (str != null && !"".equalsIgnoreCase(str)) {
                        JSONObject object = new JSONObject(str);
                        int online = object.optBoolean("online") ? 1 : 0;
                        AppInfo info = AppInfoFunc.getCurrentUser(context);
                        info.online = online;
                        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
                        if (online == 1) {
                            SocketController.newInstance(context).setRequestAfterLogin();
                        } else {
                            handler.postDelayed(runnable, TIME);
                        }
                    } else {
                        handler.postDelayed(runnable, TIME);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                super.onFailure(i, headers, bytes, throwable);
                //清空表数据
                ConfigCubeDatabaseHelper.getInstance(context).clearTable();
                String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.TIME_OUT, false, message));
            }
        };
        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_CUBE_ONLINE_STATE, map, httpClientHelper.COOKIE, responseHandler, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时器部分 用于处理 CUBE 目前不在线，隔两秒请求一次
     */
    private static final int TIME = 2000;
    private static int RepeatCount = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                if (RepeatCount >= 10) {
                    RepeatCount = 0;
                    //清空表数据
                    ConfigCubeDatabaseHelper.getInstance(context).clearTable();
                    EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.TIME_OUT, false, context.getString(R.string.error_offline)));
                    return;
                } else {
                    RepeatCount++;
                    AppInfo info = AppInfoFunc.getCurrentUser(context);
                    if (info != null) {
                        if (info.online != 1) {
                            checkCubeOnlineStatus();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*************************** private method **************************/

    /**
     * 获取绑定的CUBE 数据信息  发送Http请求
     */
    private void getBindCube(AsyncHttpResponseHandler handler) {
        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_DEVICE_LIST, null, httpClientHelper.COOKIE, handler, HttpClientHelper.GET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Cube Device信息后需要处理的事情  将数据存储到数据库中
     *
     * @param jsonStr--success 带回来的数据
     */
    private void loginWithGetCubeDevicesInfo(String jsonStr) throws JSONException {
        JSONObject object = new JSONObject(jsonStr);
        final JSONArray array = object.getJSONArray(CommonData.JSON_LOGIN_GET_CUBE_RESPONCE_DEVICES);
        if (array == null || array.length() == 0) {
            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.GET_CUBE_DEVICES_SUCCESS, false, null));
            return;
        }
        CubeDeviceFunc func = new CubeDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
        func.deleteAllCubeDevice();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object1 = array.getJSONObject(i);
            JSONObject deviceInfo = object1.optJSONObject("deviceInfo");
            CubeDevice device = new CubeDevice();
            device.mDeviceId = object1.optInt(CommonData.JSON_LOGIN_GET_CUBE_RESPONCE_DEVICEID);
            device.mDeviceSerial = object1.optString("deviceSerial");
            device.mInfo_aliasName = deviceInfo.optString(CommonData.JSON_COMMAND_ALIAS);
            device.mInfo_macAddress = deviceInfo.optString("macaddress");
            device.mInfo_serialNumber = deviceInfo.optString(CommonData.JSON_LOGIN_GET_CUBE_RESPONCE_DEVICE_CUBE_SERIAL_NUMBER);
            device.mInfo_applicationVersion = deviceInfo.optString("applicationversion");
            device.mInfo_firmWareVersion = deviceInfo.optString("firmwareversion");
            func.addCubeDevice(device);
        }

        //发送通知 Cube本地数据库更新完成
        EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.GET_CUBE_DEVICES_SUCCESS, true, null));
    }


    /**
     * 远程登陆成功后需要处理的业务
     * 更新数据库，username ,pwd, phoneId,这里不做检查，直接update 数据库
     */
    private void loginHttpSuccess(String username, String pwd, int phoneId) {
        AppInfoFunc infoFunc = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<AppInfo> infos = (ArrayList<AppInfo>) infoFunc.getAppInfoAllList();
        AppInfo info = null;
        //更新用户数据
        if (infos.size() > 0) {
            for (int i = 0; i < infos.size(); i++) {
                AppInfo temp = infos.get(i);
                if (temp.username.equals(username)) {
                    //之前存储过这个用户
                    Log.e(TAG, "之前存储过这个用户");
                    temp.phoneId = phoneId;
                    temp.cube_local_password = "";
                    temp.last_read_time = CommonUtils.genISO8601TimeStampForCurrTime();
                    //更新数据库
                    infoFunc.updateAppInfoByUserName(username, temp);
                    info = temp;
                    break;
                }
            }
        }
        if (info == null) {
            //目前没有添加到数据库，创建一个添加进去
            Log.e(TAG, "目前没有添加到数据库，创建一个添加进去");
            info = new AppInfo();
            info.current_scenario_id = 1;
            info.version = "1.0";
            info.username = username;
            info.password = pwd;
            info.phoneId = phoneId;
            info.cube_local_password = "";
            info.last_read_time = CommonUtils.genISO8601TimeStampForCurrTime();

            //更新数据库
            infoFunc.addAppInfo(info);
        }

        //云端登陆成功
        this.loginType = LOGIN_TYPE_CONNECT_CLOUD;
        EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_WITH_NAME_AND_PWD, true, "登陆云端成功!"));

    }

    /**
     * 获取云端账号信息
     *
     * @param phoneNum
     * @param phone_prefix
     */
    public void getCloudAccountInfo(String phoneNum, String phone_prefix) {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNumber", phone_prefix + phoneNum);
        MyHttpResponseHandler responseHandler = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                super.onSuccess(i, headers, bytes);
                try {
                    String str = new String(bytes);
                    Loger.print(TAG, "ssd 请求Cloud 信息的:" + str, Thread.currentThread());
                    if (str != null && !"".equalsIgnoreCase(str)) {
                        JSONObject object = new JSONObject(str);
                        AppInfo info = AppInfoFunc.getCurrentUser(context);
                        info.nickname = object.optString("name");
                        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                super.onFailure(i, headers, bytes, throwable);
            }
        };
        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.COOKIE, responseHandler, HttpClientHelper.GET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册成功后处理接口--然后远端登陆
     */
    private void registerSuccess(String phoneNum, String password, String phone_prefix) {
        AppInfoFunc infoFunc = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        ArrayList<AppInfo> infos = (ArrayList<AppInfo>) infoFunc.getAppInfoAllList();
        AppInfo info = null;
        //更新用户数据
        if (infos.size() > 0) {
            for (int i = 0; i < infos.size(); i++) {
                AppInfo temp = infos.get(i);
                if (temp.username.equals(phoneNum)) {
                    //之前存储过这个用户
                    info.phone_prefix = phone_prefix;
                    info.username = phoneNum;
                    info.nickname = phoneNum;
                    info.password = password;
                    //更新数据库
                    infoFunc.updateAppInfoByUserName(phoneNum, temp);
                    info = temp;
                    break;
                }
            }
        }
        if (info == null) {
            //目前没有添加到数据库，创建一个添加进去
            info = new AppInfo();
            info.current_scenario_id = 1;
            info.version = "1.0";
            info.phone_prefix = phone_prefix;
            info.username = phoneNum;
            info.nickname = phoneNum;
            info.password = password;

            //更新数据库
            infoFunc.addAppInfo(info);
        }

        //发送通知 Register成功
        EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_REGISTER, true, "注册成功"));
        //开启登陆
//        loginHttpWithNameAndPassword(phoneNum, password, phone_prefix);
    }

    /**
     * 初始化回调接口
     */
    private void initResponceHandler() {
        if (handlerForRequestCubeDevices == null) {
            handlerForRequestCubeDevices = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    super.onSuccess(i, headers, bytes);
                    try {
                        String str = new String(bytes);
                        Loger.print(TAG, "ssd 请求CUBE信息的responceHandler :" + str, Thread.currentThread());
                        //更新Cube设备列表 发送通知
                        loginWithGetCubeDevicesInfo(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                    EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.GET_CUBE_DEVICES_SUCCESS, false, message));
                }
            };
        }

        if (handlerForLoginWithNameAndPwd == null) {
            handlerForLoginWithNameAndPwd = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Loger.print(TAG, "ssd handlerForLoginWithNameAndPwd login success", Thread.currentThread());
                    try {
                        String str = new String(bytes);
                        JSONObject object = new JSONObject(str);
                        int phoneId = object.getInt(CommonData.JSON_LOGIN_RESPONCE_PHONEID);
                        Log.e(TAG, "ssd handlerForLoginWithNameAndPwd phoneId:" + phoneId);
                        PreferenceUtil.saveUserLoginInfo(context, userName, password);
                        //响应登陆成功
                        loginController.loginHttpSuccess(userName, password, phoneId);
                        super.onSuccess(i, headers, bytes);
                        //获取Cloud账号信息
                        loginController.getCloudAccountInfo(userName, phone_prefix);
                        if (AccountController.getBindCubeDeviceId(context) > 0) {
                            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.GET_CUBE_DEVICES_SUCCESS, true, null));
                            loginController.loginSucessAndStartConnect(null);
                        } else {
                            //获取CUBE所有的配置信息
                            loginController.getBindCube(handlerForRequestCubeDevices);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                    Loger.print(TAG, "ssd handlerForLoginWithNameAndPwd login failure: " + message + "bytes : " + bytes + "\n" + "head : " + headers, Thread.currentThread());
                    //发送失败通知
                    EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_WITH_NAME_AND_PWD, false, message));
                }
            };
        }

        if (handlerForRequestValidNum == null) {
            handlerForRequestValidNum = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Loger.print(TAG, "ssd handlerForRequestValidNum login success", Thread.currentThread());
                    super.onSuccess(i, headers, bytes);
                    //测试
                    EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_REQUEST_VALID_NUM, true, null));
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                    //未做错误处理，后面补上
                    EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_REQUEST_VALID_NUM, false, message));
                }
            };
        }

        if (handlerForRequestResetPwd == null) {
            handlerForRequestResetPwd = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Loger.print(TAG, "ssd handlerForRequestResetPwd login success", Thread.currentThread());
                    EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_RESET_PWD, true, null));
                    super.onSuccess(i, headers, bytes);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                    //未做错误处理，后面补上
                    EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_RESET_PWD, false, message));
                    Loger.print(TAG, "ssd handlerForRequestResetPwd login failure", Thread.currentThread());
                }
            };
        }

        if (handlerForRequestRegister == null) {
            handlerForRequestRegister = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    super.onSuccess(i, headers, bytes);
                    Loger.print(TAG, "ssd handlerForRequestRegister login success", Thread.currentThread());
                    try {
                        //注册成功
                        registerSuccess(userName, password, phone_prefix);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    String message = MessageErrorCode.transferHttpErrorCode(context, bytes, headers);
                    //未做错误处理，后面补上
                    EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_REGISTER, false, message));
                    Loger.print(TAG, "ssd handlerForRequestRegister login failure :", Thread.currentThread());
                }
            };
        }

    }


    /*****************
     * responce handler
     ****************/
    public void handleResponceLoginWithBody(Context context, JSONObject data) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(data);
        if (errorCode != 0) {
            Loger.print(TAG, "ssd local login failed : " + MessageErrorCode.transferErrorCode(context, errorCode), Thread.currentThread());
            if (errorCode == 100) {
                SocketController.newInstance(context).loginTCPSocketSuccess();
                return;
            }
            EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_SOCKET, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }
        EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_SOCKET, true, ""));
        SocketController.newInstance(context).loginTCPSocketSuccess();
    }
}
