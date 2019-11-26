package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.UIItem.IPCameraInfo;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.ipc.CallMsgDetailInfo;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeCallEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;


import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/4. 09:45
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 处理呼叫事务
 */
public class CallController {
    private static final String TAG = CallController.class.getSimpleName();

    private volatile static CallController instance;
    private Context mContext;

    private CallController(Context context) {
        mContext = context;
    }

    public static CallController getInstance(Context context) {
        if (instance == null) {
            synchronized (CallController.class) {
                if (instance == null) {
                    instance = new CallController(context);
                }
            }
        }
        return instance;
    }

    /**
     * 挂断电话
     *
     * @param context
     */
    public void stopCallSession(Context context, CallMsgDetailInfo info) {
        if (info == null || CommonUtils.ISNULL(info.mCallSessionId)) {
            Loger.print(TAG, "ssd call session id is null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).disconnectCall(info.mCallSessionId);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 打开门
     *
     * @param context
     * @param info
     */
    public void openDoor(Context context, CallMsgDetailInfo info) {
        if (info == null || CommonUtils.ISNULL(info.mCallSessionId)) {
            Loger.print(TAG, "ssd open the door id is null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).openDoor(info.mCallSessionId);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }

    /**
     * 开始语音会话
     *
     * @param context
     * @param info
     */
    public void startCallSession(Context context, CallMsgDetailInfo info) {
        if (info == null || CommonUtils.ISNULL(info.mCallSessionId)) {
            Loger.print(TAG, "ssd start call is null", Thread.currentThread());
            return;
        }
        String message = MessageManager.getInstance(context).startCallSession("" + info.mVideoPort, info.mUuid, CommonUtils.getLocalIpAddr(context), info.mCallSessionId);
        CommandQueueManager.getInstance(context).addNormalCommandToQueue(message);
    }


    public void handleEventCallWithBody(JSONObject object) {
        Loger.print(TAG, "ssd ****** handle Event Call WithBody", Thread.currentThread());
        String callmsg = null;
        JSONArray excludefds = null;
        Loger.print(TAG, "ssd ****** handle Event Call WithBody : " + object, Thread.currentThread());

        if (object.has("callmsg")) {
            callmsg = object.optString("callmsg");
            if ("terminatecall".equalsIgnoreCase(callmsg)) {
                excludefds = object.optJSONArray("excludefds");
                if (excludefds != null) {
                    AppInfo info = AppInfoFunc.getCurrentUser(mContext);
                    for (int i = 0; i < excludefds.length(); i++) {
                        String object1 = excludefds.optString(i);
                        if (object1.equalsIgnoreCase("" + info.phoneId)) {
                            return;
                        }
                    }
                }
                //挂断电话
                EventBus.getDefault().post(new CubeCallEvent(CubeEvents.CubeCallEventType.CALL_STOP, true, null));
            } else if ("incomingcall".equalsIgnoreCase(callmsg)) {
                //启用电话
                LogUtil.e("CallController", "2222222 object = " + object);
                String callsessionid = object.optString("callsessionid");
                String calltype = object.optString("calltype");
                String callname = object.optString("aliasname");
                String uuid = object.optString("uuid");

                CallMsgDetailInfo info = new CallMsgDetailInfo();
                info.mCallSessionId = callsessionid;
                info.mAliasName = callname;
                info.mCallType = calltype;
                info.mUuid = uuid;
                info.mCallTypeTitle = CommonUtils.transferCallTypeFromProtocol(mContext, calltype);
                Loger.print(TAG, "ssd incomming call with info : " + object, Thread.currentThread());
                EventBus.getDefault().post(new CubeCallEvent(CubeEvents.CubeCallEventType.CALL_START, true, info));
            }
        }
    }
}
