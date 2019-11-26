package com.honeywell.cube.controllers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.UIItem.menu.MenuAccountUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuCity;
import com.honeywell.cube.controllers.UIItem.menu.MenuCubeSettingBackup;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeBaseConfig;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeBaseConfigFunc;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeDevice;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeDeviceFunc;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.http.HttpClientHelper;
import com.honeywell.cube.net.http.MyHttpResponseHandler;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.DeviceManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PlistUtil;
import com.honeywell.cube.utils.events.CubeAccountEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/7. 14:11
 * Email:Shodong.Sun@honeywell.com
 * 账户管理的各种参数
 */
public class AccountController {

    private static final String TAG = AccountController.class.getSimpleName();
    private static HttpClientHelper httpClientHelper = HttpClientHelper.newInstance();

    /**
     * 定位策略，进入定位就进行一次定位
     */
    private static LocationManagerProxy aMapManager;
    private static AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location != null) {
                //需要的参数
//                Double geoLat = location.getLatitude();
//                Double geoLng = location.getLongitude();
//                String cityCode = "";
//                String desc = "";
//                Bundle locBundle = location.getExtras();
//                if (locBundle != null) {
//                    cityCode = locBundle.getString("citycode");
//                    desc = locBundle.getString("desc");
//                }
//                String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
//                        + "\n精    度    :" + location.getAccuracy() + "米"
//                        + "\n定位方式:" + location.getProvider() + "\n定位时间:"
//                        + new Date(location.getTime()).toLocaleString() + "\n城市编码:"
//                        + cityCode + "\n位置描述:" + desc + "\n省:"
//                        + location.getProvince() + "\n市:" + location.getCity()
//                        + "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
//                        .getAdCode());

                String city = location.getCity();//获取城市
                if (city == null) city = "";
                Loger.print(TAG, "ssd set location city : " + city, Thread.currentThread());
                //stop location
                if (aMapManager != null) {
                    aMapManager.removeUpdates(mAMapLocationListener);
                    aMapManager.destory();
                }
                aMapManager = null;
                EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_GET_LOCATION, true, city));
            }
        }
    };

    /**
     * 获取左上角账户登录
     *
     * @param context
     * @return
     */
    public static MenuAccountUIItem getAccountLoginInfo(Context context) {
        MenuAccountUIItem accountUIItem = new MenuAccountUIItem();
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        AppInfo guestInfo = AppInfoFunc.getGuestUser(context);
        if (info == null && guestInfo == null) {
            accountUIItem.loginName_up = "";
            accountUIItem.loginName_down = context.getString(R.string.account_unlogin);
            accountUIItem.userImageName = R.mipmap.account_default;
            return accountUIItem;
        } else {
            if (info == null) {
                info = guestInfo;
            }
            String local_nickName = info.cube_local_nickname;
            String cloud_nickname = info.nickname;
            if (local_nickName == null || local_nickName.length() <= 0)
                local_nickName = info.cube_local_id;
            if (cloud_nickname == null || cloud_nickname.length() <= 0)
                cloud_nickname = info.username;
            int userImage = R.mipmap.account_default;
            int loginImage = -1;
            if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_DISCONNECT) {
                cloud_nickname = "";
                local_nickName = context.getString(R.string.account_unlogin);
            } else if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
                cloud_nickname = "";
                loginImage = R.mipmap.account_login_wifi;
            } else {
                loginImage = R.mipmap.account_login_cloud;
            }
            accountUIItem.loginName_down = cloud_nickname;
            accountUIItem.loginName_up = local_nickName;
            accountUIItem.loginImageName = loginImage;
            accountUIItem.userImageName = userImage;
            return accountUIItem;
        }
    }


    /**
     * 获取 local id
     *
     * @param context
     * @return
     */
    public static String getAppInfoLocalId(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            return "";
        }
        return info.cube_local_id;
    }

    /**
     * 获取 账户 手机号
     *
     * @param context
     * @return
     */
    public static String getAccountPhoneNum(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        return info.username;
    }

    /**
     * 获取 账户 昵称
     *
     * @param context
     * @return
     */
    public static String getAccountUserName(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            return "";
        }
        if (info.nickname.length() <= 0) {
            return info.username;
        } else {
            return info.nickname;
        }

    }

    /**
     * 修改别名
     *
     * @param context
     * @param aliasname--修改后的名称
     * @param loginType
     */
    public static void setAliasName(Context context, final String aliasname, int loginType) {
        final AppInfo info = AppInfoFunc.getCurrentUser(context);
        final AppInfoFunc func = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if (loginType == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            //局域网登陆 还没有处理，后面进行处理

        } else if (loginType == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            //云端登陆
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", "ModifyUser");
            map.put("name", aliasname);
            map.put("currentPassword", info.password);
            map.put("language", "zh-CN");
            final MyHttpResponseHandler handlerForChangeNickname = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Loger.print(TAG, "ssd setAliasName success", Thread.currentThread());
                    info.nickname = aliasname;
                    func.updateAppInfoByUserName(info.username, info);

                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.SET_ALIASNAME, true, "操作成功"));
                    super.onSuccess(i, headers, bytes);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    Loger.print(TAG, "ssd setAliasName failure", Thread.currentThread());
                    String str = new String(bytes);
                    //未做错误处理，后面补上
                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.SET_ALIASNAME, false, str));
                }
            };
            try {
                httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.COOKIE, handlerForChangeNickname, HttpClientHelper.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改密码，新密码和旧密码
     *
     * @param context
     * @param loginType--登陆方式
     * @param oldPwd
     * @param newPwd
     */
    public static void setPassword(Context context, int loginType, String oldPwd, final String newPwd) {
        final AppInfo info = AppInfoFunc.getCurrentUser(context);
        final AppInfoFunc func = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context));
        if (oldPwd != info.password) {
            Loger.print(TAG, "ssd setPassword 修改密码 原密码错误", Thread.currentThread());
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.SET_NEW_PWD, false, "旧密码错误"));
            return;
        }

        if (loginType == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            //局域网登陆 还没有处理，后面进行处理

        } else if (loginType == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            //云端连接
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", "ModifyUser");
            map.put("newPassword", newPwd);
            map.put("currentPassword", oldPwd);
            map.put("language", "zh-CN");

            final MyHttpResponseHandler handlerForChangePwd = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Loger.print(TAG, "ssd setPassword success", Thread.currentThread());
                    info.password = newPwd;
                    func.updateAppInfoByUserName(info.username, info);

                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.SET_NEW_PWD, true, "操作成功"));
                    super.onSuccess(i, headers, bytes);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    Loger.print(TAG, "ssd setPassword failure", Thread.currentThread());
                    String str = new String(bytes);
                    //未做错误处理，后面补上
                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.SET_NEW_PWD, false, str));
                }
            };
            try {
                httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.COOKIE, handlerForChangePwd, HttpClientHelper.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取该账号绑定的Cube信息
     *
     * @param context
     */
    public static void getCubeList(final Context context) {
        //ios端采用的是在线获取的方式 android与之保持同步
        MyHttpResponseHandler handlerForRequestCubeDevices = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String str = new String(bytes);
                    Loger.print(TAG, "ssd 请求CUBE信息的responceHandler :" + str, Thread.currentThread());
                    JSONObject object = new JSONObject(str);
                    //更新Cube设备列表 发送通知
                    final JSONArray array = object.optJSONArray(CommonData.JSON_LOGIN_GET_CUBE_RESPONCE_DEVICES);
                    if (array == null || array.length() == 0) {
                        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.GET_CUBE_LIST, true, null));
                        return;
                    }
                    //获取Cube
                    CubeDeviceFunc func = new CubeDeviceFunc(ConfigCubeDatabaseHelper.getInstance(context));
                    AppInfo info = AppInfoFunc.getCurrentUser(context);
                    ArrayList<MenuDeviceUIItem> cubeItems = new ArrayList<>();
                    func.deleteAllCubeDevice();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object1 = array.getJSONObject(j);
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

                        if (device.mDeviceId == info.deviceId && device.mInfo_serialNumber == info.cube_local_id) {
                            //当前选择的Cube
                            MenuDeviceUIItem item = new MenuDeviceUIItem();
                            item.object = device;
                            item.select = true;
                            cubeItems.add(item);
                        } else {
                            //其他未选择的Cube
                            MenuDeviceUIItem item = new MenuDeviceUIItem();
                            item.object = device;
                            item.select = false;
                            cubeItems.add(item);
                        }
                    }
                    //发送数据
                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.GET_CUBE_LIST, true, cubeItems));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(i, headers, bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                super.onFailure(i, headers, bytes, throwable);
                EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.GET_CUBE_LIST, false, "error :"));
            }
        };
        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_DEVICE_LIST, null, httpClientHelper.COOKIE, handlerForRequestCubeDevices, HttpClientHelper.GET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /********************** Cube Setting **********************/

    /**
     * 获取 cube 地理位置
     *
     * @param context
     * @return
     */
    public static String getCubelocation(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            return "";
        }
        return info.cube_location;
    }

    /**
     * 获取Cube 显示的名称
     *
     * @param context
     * @return
     */
    public static String getCubeName(Context context) {
        AppInfo info = null;
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            info = AppInfoFunc.getCurrentUser(context);
        } else if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            info = AppInfoFunc.getGuestUser(context);
        }
        if (info == null) {
            return "";
        }
        String name = info.cube_local_nickname;
        if (name == null || name.length() == 0) {
            return info.cube_local_id;
        } else {
            return name;
        }
    }


    /**
     * 获取绑定CUBE的device id
     *
     * @param context
     * @return
     */
    public static int getBindCubeDeviceId(Context context) {
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            return 0;
        }
        return info.deviceId;
    }

    /**
     * 获取Cube 的版本
     *
     * @param context
     * @return
     */
    public static String getCubeVersionNum(Context context) {
        AppInfo info = null;
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            info = AppInfoFunc.getCurrentUser(context);
        } else if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            info = AppInfoFunc.getGuestUser(context);
        }
        if (info == null) {
            return "";
        }
        String nowVersion = info.cube_version;
        if (nowVersion != null && nowVersion.equalsIgnoreCase(CubeController.cubeNewVersion)) {
            return CubeController.cubeNewVersion;
        } else return "";
    }

    /**
     * 设置Cube名称 ios 端默认是局域网登陆 后面再考虑
     *
     * @param context
     * @param name
     * @param loginType
     */
    public static void setCubeName(final Context context, final String name, int loginType) {
        final AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            Loger.print(TAG, "ssd set cube name is not login", Thread.currentThread());
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_NAME, false, context.getString(R.string.error_operation_failed)));
            return;
        }
        if (loginType == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            //局域网登陆
            String message = MessageManager.getInstance(context).setCubeName(name, info.cube_local_id);
            //发送数据
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        } else if (loginType == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            //云端连接
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", "ModifyUser");
            map.put("name", name);
            map.put("currentPassword", info.password);
            map.put("language", "zh-CN");

            final MyHttpResponseHandler handlerForSetCube = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Loger.print(TAG, "ssd set cube name success", Thread.currentThread());
                    info.nickname = name;
                    new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);

                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_NAME, true, "操作成功"));
                    super.onSuccess(i, headers, bytes);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    Loger.print(TAG, "ssd setPassword failure", Thread.currentThread());
                    String str = "";
                    if (bytes != null || bytes.length > 0) {
                        str = new String(bytes);
                    }
                    //未做错误处理，后面补上
                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_NAME, false, str));
                }
            };

            try {
                httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.COOKIE, handlerForSetCube, HttpClientHelper.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取 cube 的信息
     *
     * @param context
     * @return
     */
    public static MenuAccountUIItem getCubeInfo(Context context) {
        MenuAccountUIItem menuAccountUIItem = new MenuAccountUIItem();
        AppInfo info = null;
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            info = AppInfoFunc.getCurrentUser(context);
        } else if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            info = AppInfoFunc.getGuestUser(context);
        }
        if (info == null) {
            return menuAccountUIItem;
        }
        menuAccountUIItem.cube_version = info.cube_version;
        menuAccountUIItem.cube_ip = info.cube_ip;
        menuAccountUIItem.cube_mac = info.cube_mac;

        ArrayList<CubeBaseConfig> configs = (ArrayList<CubeBaseConfig>) new CubeBaseConfigFunc(ConfigCubeDatabaseHelper.getInstance(context)).getCubeBaseConfigList();
        if (configs.size() > 0) {
            for (CubeBaseConfig config : configs) {
                if ("HNS_SERVER_IP".equalsIgnoreCase(config.conf_name)) {
                    menuAccountUIItem.cube_hns = config.conf_value;
                    break;
                }
            }
        }
        return menuAccountUIItem;
    }

    /**
     * 修改Cube本地密码
     *
     * @param context
     * @param oldPwd
     * @param newPwd
     * @param loginType
     */
    public static void setCubePassword(final Context context, final String oldPwd, final String newPwd, int loginType) {
        final AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            Loger.print(TAG, "ssd set cube pwd is not login", Thread.currentThread());
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_PWD, false, context.getString(R.string.error_operation_failed)));
            return;
        }
        if (loginType == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            //局域网登陆
            String message = MessageManager.getInstance(context).setCubePassword(oldPwd, newPwd, info.cube_local_id);
            CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
        } else if (loginType == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            //云端连接
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", "ModifyUser");
            map.put("newPassword", newPwd);
            map.put("currentPassword", oldPwd);
            map.put("language", "zh-CN");

            final MyHttpResponseHandler handlerForSetCube = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Loger.print(TAG, "ssd set cube name success", Thread.currentThread());
                    info.password = newPwd;
                    new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);

                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_PWD, true, "操作成功"));
                    super.onSuccess(i, headers, bytes);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    Loger.print(TAG, "ssd setPassword failure", Thread.currentThread());
                    String str = "";
                    if (bytes != null || bytes.length > 0) {
                        str = new String(bytes);
                    }
                    //未做错误处理，后面补上
                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_PWD, false, str));
                }
            };

            try {
                httpClientHelper.httpRequest(context, NetConstant.URI_USER, map, httpClientHelper.COOKIE, handlerForSetCube, HttpClientHelper.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 设置安防密码  走云端
     *
     * @param context
     * @param oldPwd
     * @param newPwd
     */
    public static void setCubeAlarmPassword(Context context, String oldPwd, final String newPwd) {
        if (CommonUtils.ISNULL(oldPwd) || CommonUtils.ISNULL(newPwd)) {
            //新旧密码不能为空
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.SET_ALARM_PWD, false, "密码格式不正确"));
            return;
        }
        String message = MessageManager.getInstance(context).setAlarmPwd(oldPwd, newPwd);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 获取物业网络 参数
     *
     * @param context
     * @return
     */
    public static MenuAccountUIItem getEtherNetInfo(Context context) {
        MenuAccountUIItem menuAccountUIItem = new MenuAccountUIItem();
        ArrayList<CubeBaseConfig> cubeBaseConfigs = (ArrayList<CubeBaseConfig>) new CubeBaseConfigFunc(ConfigCubeDatabaseHelper.getInstance(context)).getCubeBaseConfigList();
        if (cubeBaseConfigs.size() == 0) return menuAccountUIItem;
        for (CubeBaseConfig config : cubeBaseConfigs) {
            if ("ETHIP".equalsIgnoreCase(config.conf_name)) {
                menuAccountUIItem.ethip = config.conf_value;
            } else if ("ETHMASK".equalsIgnoreCase(config.conf_name)) {
                menuAccountUIItem.ethmask = config.conf_value;
            } else if ("ETHGW".equalsIgnoreCase(config.conf_name)) {
                menuAccountUIItem.ethgw = config.conf_value;
            }
        }
        return menuAccountUIItem;
    }

    /**
     * 更新物业网络信息
     *
     * @param context
     * @param accountUIItem
     */
    public static void updateEtherNetInfo(Context context, MenuAccountUIItem accountUIItem) {
        if (accountUIItem == null) return;
        String message = MessageManager.getInstance(context).changeEtherNetInfo(accountUIItem.ethip, accountUIItem.ethmask, accountUIItem.ethgw);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 语音识别，是否打开
     *
     * @param context
     * @param isOn
     */
    public static void setCubeVoiceRecognizeState(Context context, boolean isOn) {
        String message = MessageManager.getInstance(context).setVoiceRecognizeState(isOn);
        Loger.print(TAG, "ssd set voice recognize state : " + (isOn ? "on" : "off"), Thread.currentThread());
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 获取 CUBE 语音识别 的状态
     *
     * @param context
     * @return
     */
    public static boolean getVoiceRecgStatus(Context context) {
        AppInfo info = null;
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            info = AppInfoFunc.getCurrentUser(context);
        } else if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            info = AppInfoFunc.getGuestUser(context);
        }
        if (info == null) {
            return false;
        } else {
            return info.cube_voice_recognize == 1 ? true : false;
        }
    }


    /**
     * 获取城市列表
     *
     * @param context
     * @return
     */
    public static ArrayList<MenuCity> getDefaultCities(Context context) {
        ArrayList<Object> items = PlistUtil.parceMultiArrayWithName("Citys.plist");
        ArrayList<MenuCity> returnArray = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = (Map<String, Object>) items.get(i);
            ArrayList<Object> cityArr = (ArrayList<Object>) item.get("Cities");
            String province = (String) item.get("ProvinceName");
            MenuCity menuCity = new MenuCity();
            menuCity.citiesStr = cityArr;
            menuCity.provinceStr = province;
            returnArray.add(menuCity);
        }
        return returnArray;
    }


    /**
     * 设置城市
     *
     * @param context
     * @param city
     */
    public static void setCity(final Context context, String city) {
        if (city == null) {
            Loger.print(TAG, "ssd set city is null", Thread.currentThread());
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_SET_LOCATION, false, context.getString(R.string.error_operation_failed)));
            return;
        }
        if (city.substring(city.length() - 1).equalsIgnoreCase("市")) {
            city = city.substring(0, city.length() - 1);
        }
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", "GetLocationInfo");
            map.put("name", city);

            Loger.print(TAG, "ssd get location " + map, Thread.currentThread());
            final MyHttpResponseHandler handlerForSetCity = new MyHttpResponseHandler(context) {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    if (bytes != null) {
                        String data = new String(bytes);
                        Loger.print(TAG, "查询Location List 获取到的数据 : " + data, Thread.currentThread());
                        try {
                            JSONObject dataJson = new JSONObject(data);
                            JSONArray citys = dataJson.optJSONArray("list");
                            if (citys != null && citys.length() == 0) {
                                Loger.print(TAG, "查询Location List 获取到的数据 为 空", Thread.currentThread());
                                return;
                            }
                            if (citys.length() == 1) {
                                JSONObject cityDic = citys.optJSONObject(0);
                                String location = cityDic.optString("location");
                                String name = cityDic.optString("name");
                                Loger.print(TAG, "ssd set city with info location : " + location + " name : " + name, Thread.currentThread());
                                AccountController.setCubeCityWithLocation(context, location, name);
                            } else {
                                //city 多于一个
                                Loger.print(TAG, "ssd set city location 多于一个 没处理", Thread.currentThread());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    super.onSuccess(i, headers, bytes);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    super.onFailure(i, headers, bytes, throwable);
                    String str = "";
                    if (bytes != null || bytes.length > 0) {
                        str = new String(bytes);
                    }
                    Loger.print(TAG, "ssd set city failure" + str, Thread.currentThread());
                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_SET_LOCATION, false, str));
                }
            };

            try {
                httpClientHelper.httpRequest(context, NetConstant.URI_LOCATION_LIST, map, httpClientHelper.COOKIE, handlerForSetCity, HttpClientHelper.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setCubeCityWithLocation(final Context context, String location, final String cityName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "SetLocation");
        map.put("deviceId", "" + AppInfoFunc.getBindDeviceId(context));
        map.put("location", location);

        final MyHttpResponseHandler handlerForSetCity = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null) {
                    String data = new String(bytes);
                    Loger.print(TAG, "设置城市 成功", Thread.currentThread());
                    try {
                        AppInfo info = AppInfoFunc.getCurrentUser(context);
                        info.cube_location = cityName;
                        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
                        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_SET_LOCATION, true, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                super.onSuccess(i, headers, bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                super.onFailure(i, headers, bytes, throwable);
                String str = "";
                if (bytes != null || bytes.length > 0) {
                    str = new String(bytes);
                }
                Loger.print(TAG, "ssd set city  failure " + str, Thread.currentThread());
                EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_SET_LOCATION, false, str));
            }
        };

        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_CUBE_LOCATION, map, httpClientHelper.COOKIE, handlerForSetCity, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定位当前城市，使用的是高德地图，到时候看需求吧
     *
     * @param context
     */
    public static void startLocation(Context context) {
        aMapManager = LocationManagerProxy.getInstance(context);

        /*
        * mAMapLocManager.setGpsEnable(false);
        * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
        * API定位采用GPS和网络混合定位方式
        * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
        */
        aMapManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, mAMapLocationListener);
    }

    /**
     * 升级操作
     *
     * @param context
     */
    public static void updateCubeVersion(Context context) {
        String message = MessageManager.getInstance(context).updateCubeVersion();
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 升级操作 Menu 中显示的参数 最新的版本
     *
     * @return
     */
    public static String getNewCubeVersion() {
        return CubeController.cubeNewVersion;
    }

    /**
     * 备份Cube信息
     *
     * @param context
     * @param messsage
     */
    public static void backupCubeVersion(Context context, String messsage) {
        if (messsage == null) messsage = "";
        String message = MessageManager.getInstance(context).backupCubeVersion(messsage);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 恢复Cube版本
     * 恢复到最新版本 --- dataid 传 ""
     * 恢复到指定版本 --- dataid 传对应的 dataId
     *
     * @param context
     * @param dataid
     */
    public static void recoveryCubeVersion(Context context, String dataid) {
        String message = MessageManager.getInstance(context).recoveryCubeVersion(dataid);
        //发送远端请求
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    /**
     * 获取备份历史
     *
     * @param context
     */
    public static void getBackupHistory(Context context) {
        if (!CommonUtils.isConnectNetwork(context)) {
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_GET_BACKUP_HISTORY, false, "网络不通"));
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "ListConfigBackup");
        map.put("deviceId", "" + AppInfoFunc.getBindDeviceId(context));
        final MyHttpResponseHandler handlerForGetbackupHistory = new MyHttpResponseHandler(context) {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String value = null;
                if (bytes != null) {
                    value = new String(bytes);
                }
                try {
                    JSONObject object = new JSONObject(value);
                    JSONArray listArray = object.optJSONArray("list");
                    if (listArray == null) {
                        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_GET_BACKUP_HISTORY, true, new ArrayList<>()));
                        return;
                    }
                    ArrayList<MenuCubeSettingBackup> returnValue = new ArrayList<>();
                    for (int j = 0; j < listArray.length(); j++) {
                        JSONObject object1 = listArray.optJSONObject(j);
                        String time = object1.optString("timestamp");
                        time = CommonUtils.transformTimeZoneFromNormalZoneToLocalZone(time);
                        MenuCubeSettingBackup backup = new MenuCubeSettingBackup();
                        backup.dataId = object1.optString("dataId");
                        backup.description = object1.optString("description");
                        backup.retention = object1.optString("retention");
                        backup.timestamp = time;
                        returnValue.add(backup);
                    }
                    Loger.print(TAG, "ssd backup history " + returnValue, Thread.currentThread());
                    EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_GET_BACKUP_HISTORY, true, returnValue));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(i, headers, bytes);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                super.onFailure(i, headers, bytes, throwable);
                String str = "";
                if (bytes != null) {
                    str = new String(bytes);
                }
                Loger.print(TAG, "ssd cube backup" + str, Thread.currentThread());
                EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_GET_BACKUP_HISTORY, false, str));
            }
        };
        try {
            httpClientHelper.httpRequest(context, NetConstant.URI_CUBE, map, httpClientHelper.COOKIE, handlerForGetbackupHistory, HttpClientHelper.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}