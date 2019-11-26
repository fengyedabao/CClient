package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.webSocket.autobaln_websocket.WebSocketConnection;
import com.honeywell.cube.net.webSocket.cube_websocket.CubeAutoBahnWebsocketClient;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/1. 11:29
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这里负责组织Web socket 按照特定的数据结构进行发送,以及网络状态的分析
 */
public class WebsocketMessageController {
    private static final String TAG = WebsocketMessageController.class.getSimpleName();

    private static boolean isSendingMsg = false;
    private static Timer timer = new Timer();//定时器
    private static TimerTask task;

    private static WebsocketMessageController instance = null;
    public static Context mContext;

    private WebsocketMessageController(Context context) {
        mContext = context;
    }

    public static WebsocketMessageController getInstance(Context context) {
        if (instance == null) {
            synchronized (WebsocketMessageController.class) {
                if (instance == null) {
                    instance = new WebsocketMessageController(context);
                }
            }
        }
        return instance;
    }

    /**
     * 发送String类型的数据
     *
     * @param context
     * @param message
     * @param tag
     * @return
     */
    public boolean sendMessageWithInfo(Context context, final String message, String tag) {
        if (!checkIfLogin(context, tag)) {
            Loger.print(TAG, "ssd " + tag + "  sendMessageWithInfo not login :" + message, Thread.currentThread());
            return false;
        }
//        if (startTimeOutMonitor(tag)) {
//            if (message != null && !message.equals("")) {
//                //message 正确
//                Loger.print(TAG, "ssd " + tag + "  send meesage success message:" + message, Thread.currentThread());
//                WebSocketConnection connection = CubeAutoBahnWebsocketClient.getConnection();
//                connection.sendTextMessage(message);
//                return true;
//            } else {
//                Loger.print(TAG, "ssd " + tag + " 发送命令格式错误", Thread.currentThread());
//                return false;
//            }
//        }

        if (message != null && !message.equals("")) {
            Loger.print(TAG, "11111111-ssd send message : " + message, Thread.currentThread());
            //message 正确
            WebSocketConnection connection = CubeAutoBahnWebsocketClient.getConnection();
            if (connection.isConnected()) {
                connection.sendTextMessage(message);
            } else {
                LogUtil.e("ssd websocket", "connect lost", true);
                return false;
            }
            return true;
        } else {
            Loger.print(TAG, "ssd " + tag + " 发送命令格式错误", Thread.currentThread());
            return false;
        }
    }


    /*******************
     * private method
     ***************************/
    private boolean checkIfLogin(Context context, String msg) {
        LoginController loginController = LoginController.getInstance(context);
        //必须登陆后才能发送其他命令
        if (loginController.getLoginType() == LoginController.LOGIN_TYPE_DISCONNECT) {
            Loger.print(TAG, msg + " 用户没有登陆,发送失败", Thread.currentThread());
            //发送通知
            return false;
        }
        return true;
    }

    private boolean startTimeOutMonitor(final String tag) {
        Loger.print("ssd test", "startTimeOutMonitor :", Thread.currentThread());
        if (isSendingMsg) {
            Loger.print("ssd test", "startTimeOutMonitor :" + tag + ": is sending message", Thread.currentThread());
            return false;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        task = new TimerTask() {
            @Override
            public void run() {
                Loger.print("ssd test ", " ********* time out", Thread.currentThread());
                if (isSendingMsg) {
                    isSendingMsg = false;
                    Loger.print("ssd test", "time out ********************", Thread.currentThread());
                    EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.TIME_OUT, false, mContext.getString(R.string.error_time_out)));
                }
                cancel();
            }
        };
        timer.schedule(task, 100, NetConstant.MsgTimeOut);
        return true;
    }
}
