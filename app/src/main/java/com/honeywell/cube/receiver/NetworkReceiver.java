package com.honeywell.cube.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/8/16. 09:46
 * Email:Shodong.Sun@honeywell.com
 */
public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isAvailable()) {
            //网络连接
            String name = activeInfo.getTypeName();
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //Wifi 连接
            } else if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //3/4G 网络
            }
        } else {
            if (LoginController.getInstance(context).getLoginType() != LoginController.LOGIN_TYPE_DISCONNECT) {
                LoginController.getInstance(context).setLoginType(LoginController.LOGIN_TYPE_DISCONNECT);
                LoginController.getInstance(context).logout(context);
                Loger.print("ssd ******* ", "网络连接失败", Thread.currentThread());
                EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.CONNECTING_LOST, false, "Socket 中断"));
            }
        }
    }  //如果无网络连接activeInfo为null
}
