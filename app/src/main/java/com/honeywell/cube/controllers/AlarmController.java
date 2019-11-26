package com.honeywell.cube.controllers;

import android.content.Context;

import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeAlarmEvent;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/4. 09:37
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 负责报警消息的业务
 */
public class AlarmController {
    private static final String TAG = AlarmController.class.getSimpleName();

    private int unReadAlarmCount = 0;//未读消息条数

    private volatile static AlarmController alarmController;
    private Context mContext;

    private AlarmController(Context context) {
        mContext = context;
    }

    public static AlarmController getInstance(Context context) {
        if (alarmController == null) {
            synchronized (AlarmController.class){
                if (alarmController == null){
                    alarmController = new AlarmController(context);
                }
            }
        }
        return alarmController;
    }

    public int getUnReadAlarmCount() {
        return PreferenceUtil.getAlarmCount(mContext);
    }

    public synchronized void setUnReadAlarmCount(int count) {
        PreferenceUtil.setAlarmCount(mContext, count);
    }

    /************************************ responce handler***********************************/

    /**
     * 处理回传消息
     *
     * @param object
     */
    public void handleEventAlarmWithBody(JSONObject object) {
        Loger.print(TAG, " handleEventAlarmWithBody", Thread.currentThread());
        //显示报警消息
        if (object == null) return;
        String name = object.optString("loopName");
        String type = object.optString("alarmType");
        type = CommonUtils.transferZoneAlarmType(mContext, type);

        Loger.print(TAG, "ssd ***** alarm thing " + object, Thread.currentThread());

        //未读条数加一
        alarmController.unReadAlarmCount = getUnReadAlarmCount();
        alarmController.unReadAlarmCount++;
        setUnReadAlarmCount(alarmController.unReadAlarmCount);

        //通知显示警告
        EventBus.getDefault().post(new CubeAlarmEvent(CubeEvents.CubeAlarmEventType.GET_ALARM, name, type));
    }


    /**
     * 收到修改安防密码后返回数据的处理
     *
     * @param body
     */
    public void ResponceForChangeAlarmPasswordWithBody(JSONObject body) {

        if (!ResponderController.checkHaveOneSuccessWithBody(body)) {
            //发送请求失败
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, false, "返回中含有错误码"));
            return;
        }

        //发送成功请求
        EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, true, "成功"));
    }

}
