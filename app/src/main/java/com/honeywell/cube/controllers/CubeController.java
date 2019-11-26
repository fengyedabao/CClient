package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeBaseConfig;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeBaseConfigFunc;
import com.honeywell.cube.net.MessageErrorCode;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeAccountEvent;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/6/18. 17:58
 * Email:Shodong.Sun@honeywell.com
 * 有关Cube设置的部分放在这里
 */
public class CubeController {
    public static final String TAG = CubeController.class.getSimpleName();
    public static String cubeNewVersion = "";


    /**
     * 处理主动请求的升级事件
     *
     * @param context
     * @param object
     */
    public static void handleRequestCubeUpgrade(Context context, JSONObject object) {
        int errorcode = ResponderController.checkHaveOneFailWithBody(object);
        if (errorcode != 0) {
            Loger.print(TAG, "ssd handle Request Cube Update have error", Thread.currentThread());
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_UPGRADE, false, MessageErrorCode.transferErrorCode(context, errorcode)));
            return;
        }

        //添加获取信息
        String message = MessageManager.getInstance(context).getCubeInfoFromLocal(context);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);

        String version = object.optString("version");
        if ("".equalsIgnoreCase(version)) {
            cubeNewVersion = "";
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_UPGRADE, false, "have error"));
            return;
        }
        cubeNewVersion = version;
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info != null && version.equalsIgnoreCase(info.version)) {
            //强制升级
            if ("yes".equalsIgnoreCase(object.optString("forceupgrade"))) {
                //强制升级 需要弹出提示框
                EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_RECEIVE_UPGRADE, true, cubeNewVersion));
                return;
            }
        }
        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_UPGRADE, true, "Cube 开始升级"));
    }

    /**
     * 处理备份事件
     *
     * @param context
     * @param data
     */
    public static void handleResponceBackUp(Context context, JSONObject data) {
        int errorcode = ResponderController.checkHaveOneFailWithBody(data);
        if (errorcode != 0) {
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_BACKUP, false, MessageErrorCode.transferErrorCode(context, errorcode)));
            return;
        }
        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_BACKUP, true, context.getString(R.string.backup_in_progress)));
    }

    /**
     * 处理 恢复到最新版本 的行为
     *
     * @param context
     * @param data
     */
    public static void handleResponceRecovery(Context context, JSONObject data) {
        int errorcode = ResponderController.checkHaveOneFailWithBody(data);
        if (errorcode != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
            if (errorcode == MessageErrorCode.MESSAGE_ERROR_CODE_RECOVERY_REBOOT) {
                //重启命令 发送命令 重新登录
                Loger.print(TAG, "ssd 开启重新登录", Thread.currentThread());
                EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_RECOVERY, false, context.getString(R.string.restoring_relogin)));
                return;
            }
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_RECOVERY, false, MessageErrorCode.transferErrorCode(context, errorcode)));
            return;
        }
        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_RECOVERY, true, context.getString(R.string.restoring_backup)));
    }

    /**
     * 处理 语音开关 后的数据
     *
     * @param context
     * @param data
     */
    public static void handleResponceVoiceRecgnize(Context context, JSONObject data) {
        int errorcode = ResponderController.checkHaveOneFailWithBody(data);
        if (errorcode != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
            Loger.print(TAG, "ssd handle responce recovery have error", Thread.currentThread());
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_VOICE_REGNIZE, false, MessageErrorCode.transferErrorCode(context, errorcode)));
            return;
        }
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (info == null) {
            info = AppInfoFunc.getGuestUser(context);
        }
        info.cube_voice_recognize = "on".equalsIgnoreCase(data.optString("status")) ? 1 : 0;
        new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_VOICE_REGNIZE, true, "成功"));
    }

    /**
     * 收到Event事件 升级
     *
     * @param context
     * @param body
     */
    public static void handleEventCubeUpgrade(Context context, JSONObject body) {
        int errorCode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorCode != 0) {
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_RECEIVE_UPGRADE, false, MessageErrorCode.transferErrorCode(context, errorCode)));
            return;
        }

        String version = body.optString("version");
        if (version.length() <= 0) {
            return;
        }
        cubeNewVersion = version;
        AppInfo info = AppInfoFunc.getCurrentUser(context);
        if (LoginController.getInstance(context).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            info = AppInfoFunc.getGuestUser(context);
        }
        if (info.cube_version != null && !info.cube_version.equalsIgnoreCase(version)) {
            //强制升级
            if ("yes".equalsIgnoreCase(body.optString("forceupgrade"))) {
                EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_RECEIVE_UPGRADE, true, "强制升级"));
            }
        }
        //强制升级 需要弹出提示框
        EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_RECEIVE_UPGRADE, true, "强制升级"));
    }

    /**
     * 处理responce config module 修改登陆信息
     *
     * @param context
     * @param body
     */
    public static void handleConfigModuleResponceWithInfo(Context context, JSONObject body) {
        int errorcode = ResponderController.checkHaveOneFailWithBody(body);
        if (errorcode != MessageErrorCode.MESSAGE_ERROR_CODE_OK) {
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING, false, MessageErrorCode.transferErrorCode(context, errorcode)));
            return;
        }

        //修改登陆密码
        if (body.optString("cubeoldpwd") != null) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            info.cube_local_password = body.optString("cubepwd");
            new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_PWD, true, "操作成功"));
        }

        //修改别名
        if (body.optString("aliasname") != null) {
            AppInfo info = AppInfoFunc.getCurrentUser(context);
            info.cube_local_nickname = body.optString("aliasname");
            new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(context)).updateAppInfoByUserName(info.username, info);
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_NAME, true, "操作成功"));
        }

        //设置 cube base 社区网络
        if ("ethernet".equalsIgnoreCase(body.optString("configtype"))) {
            CubeBaseConfigFunc func = new CubeBaseConfigFunc(ConfigCubeDatabaseHelper.getInstance(context));
            ArrayList<CubeBaseConfig> cubeBaseConfigs = (ArrayList<CubeBaseConfig>) func.getCubeBaseConfigList();
            String ip = body.optString("ethip");
            String mask = body.optString("ethmask");
            String router = body.optString("ethgw");
            for (CubeBaseConfig config : cubeBaseConfigs) {
                if ("ETHIP".equalsIgnoreCase(config.conf_name)) {
                    config.conf_value = ip;
                    func.updateCubeBaseConfig(config);
                    ip = null;
                } else if ("ETHMASK".equalsIgnoreCase(config.conf_name)) {
                    config.conf_value = mask;
                    func.updateCubeBaseConfig(config);
                    mask = null;
                } else if ("ETHGW".equalsIgnoreCase(config.conf_name)) {
                    config.conf_value = router;
                    func.updateCubeBaseConfig(config);
                    router = null;
                }
            }

            if (ip != null) {
                CubeBaseConfig config = new CubeBaseConfig();
                config.conf_name = "ETHIP";
                config.conf_value = ip;
                config.primaryid = 1000;
                func.addCubeBaseConfig(config);
            }
            if (mask != null) {
                CubeBaseConfig config = new CubeBaseConfig();
                config.conf_name = "ETHMASK";
                config.conf_value = mask;
                config.primaryid = 1001;
                func.addCubeBaseConfig(config);
            }
            if (router != null) {
                CubeBaseConfig config = new CubeBaseConfig();
                config.conf_name = "ETHGW";
                config.conf_value = router;
                config.primaryid = 1002;
                func.addCubeBaseConfig(config);
            }
            EventBus.getDefault().post(new CubeAccountEvent(CubeEvents.CubeAccountEventType.CUBE_SETTING_ETHERNET, true, "操作成功"));
        }
    }

}
