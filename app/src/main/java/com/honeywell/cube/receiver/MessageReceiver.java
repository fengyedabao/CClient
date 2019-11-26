package com.honeywell.cube.receiver;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ToastUtil;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class MessageReceiver extends XGPushBaseReceiver {
    private Intent intent = new Intent("com.honeywell.cube.activities.MainActivity");
    public static final String TAG = MessageReceiver.class.getSimpleName();

    private void show(Context context, String text) {
//        ToastUtil.showShort(context, text);
    }

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {
        String text = "onNotifactionShowedResult 收到消息:" + notifiShowedRlt.toString();
        LogUtil.e("alinmi34", "onNotifactionShowedResult -----> " + text);
        try {
            String action = "";
            String subaction = "";
            String callmsg = "";
            JSONObject jsonObject = new JSONObject(notifiShowedRlt.getCustomContent());
            action = jsonObject.getString(Constants.N_ACTION_TYPE);
            subaction = jsonObject.getString(Constants.N_SUBACTION_TYPE);
            if (Constants.SUBACTION_CALL.equalsIgnoreCase(subaction) && Constants.ACTION_EVENT.equalsIgnoreCase(action)) {
                callmsg = jsonObject.getString(Constants.N_CALL_MSG_TYPE);
                if (Constants.CALL_MSG_INCOMING_CALL.equalsIgnoreCase(callmsg)) {
//                    ToastUtil.showShort(context, "CALL_MSG_INCOMING_CALL msgID = " + notifiShowedRlt.getMsgId());
                    PreferenceUtil.saveCallNotification(context, true, notifiShowedRlt.getMsgId());
                } else if (Constants.CALL_MSG_TERMINATE_CALL.equalsIgnoreCase(callmsg)) {
//                    ToastUtil.showShort(context, "CALL_MSG_TERMINATE_CALL msgID = " + notifiShowedRlt.getMsgId());
                    PreferenceUtil.saveCallNotification(context, false, notifiShowedRlt.getMsgId());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        ToastUtil.showShort(context, text);
        if (context == null || notifiShowedRlt == null) {
            return;
        }
//        XGPushManager.setDefaultNotificationBuilder(context, null);
//        XGCustomPushNotificationBuilder build = new XGCustomPushNotificationBuilder();
//        XGPushManager.getDefaultNotificationBuilder(context).setIcon(R.mipmap.ac_mode_auto_focused_selected);
//        XGPushManager.getDefaultNotificationBuilder(context).setNotificationLargeIcon(R.mipmap.ac_mode_auto_focused_selected);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, notifiShowedRlt.getNotifactionId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        XGPushManager.getDefaultNotificationBuilder(context).setContentIntent(contentIntent);
//        notifiShowedRlt.
//        XGNotification notific = new XGNotification();
//        notific.setMsg_id(notifiShowedRlt.getMsgId());
//        notific.setTitle(notifiShowedRlt.getTitle());
//        notific.setContent(notifiShowedRlt.getContent());
//        // notificationActionType==1为Activity，2为url，3为intent
//        notific.setNotificationActionType(notifiShowedRlt
//                .getNotificationActionType());
//        // Activity,url,intent都可以通过getActivity()获得
//        notific.setActivity(notifiShowedRlt.getActivity());
//        notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                .format(Calendar.getInstance().getTime()));
//        NotificationService.getInstance(context).save(notific);
//        context.sendBroadcast(intent);
//        show(context, "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString());
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        String text = "errorCode = " + errorCode;
        LogUtil.e("alinmi33", "onUnregisterResult -----> " + text);
        if (context == null) {
            return;
        }
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        LogUtil.d(TAG, text);
//        show(context, text);

    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        String text = "tagName";
        LogUtil.e("alinmi33", "onSetTagResult -----> " + text);
        if (context == null) {
            return;
        }
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        LogUtil.d(TAG, text);
//        show(context, text);

    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        String text = "tagName";
        LogUtil.e("alinmi33", "onDeleteTagResult -----> " + text);
        if (context == null) {
            return;
        }
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        LogUtil.d(TAG, text);
//        show(context, text);

    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
        String text = "收到消息:" + message.toString();
        LogUtil.e("alinmi33", "onNotifactionClickedResult -----> " + text);
//        if (context == null || message == null) {
//            return;
//        }
//        String text = "";
//        sendIconCountMessage(context);
////        samsungShortCut(context, "25");
//        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
//            // 通知在通知栏被点击啦。。。。。
//            // APP自己处理点击的相关动作
//            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
//            text = "通知被打开 :" + message;
//        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
//            // 通知被清除啦。。。。
//            // APP自己处理通知被清除后的相关动作
//            text = "通知被清除 :" + message;
//        }
//        ToastUtil.showShort(context, "广播接收到通知被点击:" + message.toString());
//        // 获取自定义key-value
//        String customContent = message.getCustomContent();
//        if (customContent != null && customContent.length() != 0) {
//            try {
//                JSONObject obj = new JSONObject(customContent);
//                // key1为前台配置的key
//                if (!obj.isNull("ID")) {
//                    String value = obj.getString("ID");
//                    Log.d(LogTag, "get custom value:" + value);
//                }
//                // ...
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        // APP自主处理的过程。。。
//        Log.d(LogTag, text);
//        show(context, text);
    }

    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult message) {
        String text = "收到消息:" + message.toString();
        LogUtil.e("alinmi33", "onRegisterResult -----> " + text);
//        // TODO Auto-generated method stub
//        if (context == null || message == null) {
//            return;
//        }
//        String text = "";
//        if (errorCode == XGPushBaseReceiver.SUCCESS) {
//            text = message + "注册成功";
//            // 在这里拿token
//            String token = message.getToken();
//        } else {
//            text = message + "注册失败，错误码：" + errorCode;
//        }
//        Log.d(LogTag, text);
//        show(context, text);
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        // TODO Auto-generated method stub
        show(context, "haha");
        String text = "onTextMessage 收到消息:" + message.toString();
        LogUtil.e("alinmi33", "onTextMessage -----> " + text);
        ToastUtil.showShort(context, text);
        // 获取自定义key-value
//        String customContent = message.getCustomContent();
//        if (customContent != null && customContent.length() != 0) {
//            try {
//                JSONObject obj = new JSONObject(customContent);
//                // key1为前台配置的key
//                if (!obj.isNull("key")) {
//                    String value = obj.getString("key");
//                    Log.d(LogTag, "get custom value:" + value);
//                }
//                // ...
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        // APP自主处理消息的过程...
//        XGLocalMessage localMessage = new XGLocalMessage();
//        localMessage.setTitle("haha");
//        localMessage.setContent(message.getContent());
//        XGCustomPushNotificationBuilder build = new XGCustomPushNotificationBuilder();
//        build.setSound(
//                RingtoneManager.getActualDefaultRingtoneUri(
//                        context, RingtoneManager.TYPE_ALARM)) // 设置声音
//                // setSound(
//                // Uri.parse("android.resource://" + getPackageName()
//                // + "/" + R.raw.wind)) 设定Raw下指定声音文件
//                .setDefaults(Notification.DEFAULT_VIBRATE) // 振动
//                .setFlags(Notification.FLAG_NO_CLEAR); // 是否可清除
//        // 设置自定义通知layout,通知背景等可以在layout里设置
//        build.setLayoutId(R.layout.layout_notification);
//        // 设置自定义通知内容id
//        build.setLayoutTextId(R.id.ssid);
//        // 设置自定义通知标题id
//        build.setLayoutTitleId(R.id.title);
//        // 设置自定义通知图片id
//        build.setLayoutIconId(R.id.icon);
//        // 设置自定义通知图片资源
//        build.setLayoutIconDrawableId(R.drawable.ic_launcher);
//        // 设置状态栏的通知小图标
//        build.setIcon(R.drawable.ic_launcher);
//        // 设置时间id
//        build.setLayoutTimeId(R.id.time);
//        // 若不设定以上自定义layout，又想简单指定通知栏图片资源
//        build.setNotificationLargeIcon(R.drawable.tenda_icon);
//        // 客户端保存build_id
//        XGPushManager.setDefaultNotificationBuilder(context, build);
//
//        XGPushManager.addLocalNotification(context, localMessage);
//        Log.d(LogTag, text);
//        show(context, text);
    }

    private void sendIconCountMessage(Context context) {
        Intent it = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
        it.putExtra("android.intent.extra.update_application_component_name", "com.example.wujie.xungetest/.MainActivity");
        String iconCount = "50";
        it.putExtra("android.intent.extra.update_application_message_text", iconCount);
        context.sendBroadcast(it);
    }
}