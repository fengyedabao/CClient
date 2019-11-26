package com.honeywell.cube.activities;

import android.content.Context;

import com.honeywell.cube.application.BaseApplication;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.AlarmController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.ConfigCubeDatabaseHelper;
import com.honeywell.cube.ipc.StoragePath;
import com.honeywell.cube.ipc.nativeapi.P2PConn;
import com.honeywell.cube.net.Socket.SocketController;
import com.honeywell.cube.net.easylink.EasyLinkManager;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.net.webSocket.cube_websocket.CubeAutoBahnWebsocketClient;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PlistUtil;
import com.honeywell.lib.utils.LogUtil;

/**
 * Created by H157925 on 16/4/13. 13:26
 * Email:Shodong.Sun@honeywell.com
 * 用语处理全局变量
 */
public class CubeApplication extends BaseApplication {
    private static final String TAG = CubeApplication.class.getSimpleName();
    private static CubeApplication instance;
    private static Context appContext;

    public static Context getContext() {
        return appContext;
    }


    private ConfigCubeDatabaseHelper databaseHelper;

    public static CubeApplication getInstance() {
        if (null == instance) {
        }
        return instance;
    }

    public CubeApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = this;
        //工具类的初始化
        PlistUtil.newInstance(appContext);
        initNeedFile();

    }

    @Override
    public void onTerminate() {
        stopAllRequest("App terminate");
        // 程序终止的时候执行
        LogUtil.d("CubeApplication", "onTerminate");
        P2PConn.unInitConnLib();
        super.onTerminate();
    }

    /**
     * 停止一切请求
     */
    public void stopAllRequest(String reason) {
        Loger.print(TAG, "ssd stop all request with reason : " + reason, Thread.currentThread());
        LoginController.getInstance(getApplicationContext()).setLoginType(LoginController.LOGIN_TYPE_DISCONNECT);
        CommandQueueManager.getInstance(this).stopRun();
        SocketController.newInstance(this).stopSocketConnect(reason);
        CubeAutoBahnWebsocketClient.getInstance(this).stopConnection(reason);
        EasyLinkManager.newInstance(this).stopGroupListener();
        EasyLinkManager.newInstance(this).stopConfigBroadLink();
        EasyLinkManager.newInstance(this).stopEasyLink();
    }

    private void initNeedFile() {
        //用户基本信息
        AppInfoFunc func = new AppInfoFunc(ConfigCubeDatabaseHelper.getInstance(this));
        AppInfo info = func.getAppInfoByUserName(ModelEnum.GuestNum);
        if (info == null) {
            AppInfo info1 = new AppInfo();
            info1.current_scenario_id = 1;
            info1.username = ModelEnum.GuestNum;
            info1.version = CommonUtils.getVersion(this);
            func.addAppInfo(info1);
        } else {
            info.version = CommonUtils.getVersion(this);
            func.updateAppInfoByUserName(ModelEnum.GuestNum, info);
        }
    }
}
