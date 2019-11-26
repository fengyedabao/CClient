package com.honeywell.cube.net.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.honeywell.cube.net.easylink.EasyLinkManager;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;

import de.greenrobot.event.EventBus;

/**
 * Service for setup all thread with network
 * socket and webSocket
 * Created by H157925 on 16/4/13. 15:21
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeAppService extends Service {
    private static final String TAG = "CubeAppService";

    public static Context mContext = null;
    private IBinder binder = new MyBinder();
    private Intent intent = null;

    public CubeAppService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        LogUtil.e(TAG, "------CubeAppService---------> onCreate ");
        //开始监听
        if (mContext != null) {
            EasyLinkManager.newInstance(mContext).startGroupListener();
            //注册Event
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //停止监听
        if (mContext != null) {
            EasyLinkManager.newInstance(mContext).stopGroupListener();
        }
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CubeEvents event) {
//        if (event instanceof CubeCallEvent) {
//            CubeCallEvent callEvent = (CubeCallEvent) event;
//            if (callEvent.type == CubeEvents.CubeCallEventType.CALL_START) {
//                //呼叫进入
//                Loger.print(TAG, "ssd service incoming call", Thread.currentThread());
//                //启动新的Activity 目前测试
//                Intent dialogIntent = new Intent(getBaseContext(), IpcCallScreenActivity.class);
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable(Constants.EXTRA_DATA_CALLMSG, callEvent.cameraInfo);
//                dialogIntent.putExtras(bundle);
//                getApplication().startActivity(dialogIntent);
//            }
//        }
    }

    public class MyBinder extends Binder {
        public CubeAppService getService() {
            return CubeAppService.this;
        }
    }

//    /**
//     * 把用户名和密码保存到手机ROM中 ，以及后续的一些私有参数
//     *
//     * @param password 密码
//     * @param username 用户名
//     * @param filename 文件名称
//     */
//    public boolean saveUserParamToROM(String password, String username, String filename) {
//        try {
//            //私有方式打开一个文件
//            FileOutputStream fos = mContext.openFileOutput(filename, MODE_PRIVATE);
//            String result = username + ":" + password;
//            fos.write(result.getBytes());
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            Loger.print(TAG, "ssd saveUserParamToROM exception", Thread.currentThread());
//            e.printStackTrace();
//        }
//        return true;
//    }
//
//    /**
//     * 读取用户名 密码
//     */
//    public String readUserParamDataFromROM(String fileName) {
//        String res = "";
//        try {
//            FileInputStream fin = openFileInput(fileName);
//            int length = fin.available();
//            byte[] buffer = new byte[length];
//            fin.read(buffer);
//            res = EncodingUtils.getString(buffer, "UTF-8");
//            fin.close();
//        } catch (Exception e) {
//            Loger.print(TAG, "ssd readUserParamData exception", Thread.currentThread());
//            e.printStackTrace();
//        }
//        return res;
//    }
}
