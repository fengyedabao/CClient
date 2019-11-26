
package com.honeywell.cube.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.honeywell.cube.R;
import com.honeywell.cube.net.autoupdate.UpdateManager;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ToastUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends Activity {
    private String mToken;
    private TimeOutThread mTimeOutThread;
    private static final int TIMEOUT = 2000;
    private String mCustomContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        checkUpdate();

    }

    public class TimeOutThread extends Thread {
        boolean mStart = true;
        long mStartTime = System.currentTimeMillis();
        int mTimeout;

        public TimeOutThread(int timeout) {
            mTimeout = timeout;
        }

        public void cancel() {
            mStart = false;
        }

        @Override
        public void run() {
            super.run();
            while (mStart) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long current = System.currentTimeMillis();
                if (current - mStartTime > mTimeout * 8) {
                    LogUtil.d("TPush", "current - mStartTime > mTimeout * 8");
                    gotoLoginActivity("", true);
                    mStart = false;
                    return;
                } else if (current - mStartTime > mTimeout) {
                    LogUtil.d("TPush", "XGPushManager.registerPush TimeOut");
//                    ToastUtil.showShort(WelcomeActivity.this, "XGPushManager.registerPush TimeOut");
                    String token = PreferenceUtil.getXGToken(WelcomeActivity.this);
                    if (!TextUtils.isEmpty(token)) {
                        LogUtil.d("TPush", "!TextUtils.isEmpty(token)");
                        gotoLoginActivity(token, true);
                        mStart = false;
                        return;
                    }

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 判断是否从推送通知栏打开的
        XGPushClickedResult message = XGPushManager.onActivityStarted(this);
        LogUtil.e("alinmi33", " XGPushClickedResult message = " + message);
        LogUtil.e("alinmi33", " isTaskRoot() = " + isTaskRoot());
        if (message != null) {
            // 获取自定义key-value
            try {
                String action = "";
                String subaction = "";
                String callmsg = "";
                JSONObject jsonObject = new JSONObject(message.getCustomContent());
                action = jsonObject.getString(Constants.N_ACTION_TYPE);
                subaction = jsonObject.getString(Constants.N_SUBACTION_TYPE);
                if (Constants.SUBACTION_CALL.equalsIgnoreCase(subaction) && Constants.ACTION_EVENT.equalsIgnoreCase(action)) {
                    callmsg = jsonObject.getString(Constants.N_CALL_MSG_TYPE);
                    if (Constants.CALL_MSG_INCOMING_CALL.equalsIgnoreCase(callmsg)) {
//                        ToastUtil.showShort(WelcomeActivity.this, "CALL_MSG_INCOMING_CALL msgID = " + message.getMsgId() + " --- msgId = " + PreferenceUtil.getCallNotificationId(WelcomeActivity.this));
                        if (PreferenceUtil.getCallNotificationState(WelcomeActivity.this)) {
                            if (message.getMsgId() == PreferenceUtil.getCallNotificationId(WelcomeActivity.this)) {
//                                mCustomContent = message.getCustomContent();
                                PreferenceUtil.saveCallNotificationCustomConent(WelcomeActivity.this, message.getCustomContent());
                            }
                        }
                    } else if (Constants.CALL_MSG_TERMINATE_CALL.equalsIgnoreCase(callmsg)) {

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            //拿到数据自行处理
//            if (isTaskRoot()) {
//                return;
//            }
//            //如果有面板存在则关闭当前的面板
//            finish();
        } else {
            PreferenceUtil.saveCallNotificationCustomConent(WelcomeActivity.this, "");
        }
    }

    private void gotoLoginActivity(String token, boolean hasRegister) {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        intent.putExtra(Constants.TOKEN, token);
        intent.putExtra(Constants.REGISTER, hasRegister);
        startActivity(intent);
        finish();
    }

    private void checkUpdate() {
        UpdateManager updateMan = new UpdateManager(WelcomeActivity.this, false);
        updateMan.setUpdateListener(new UpdateManager.UpdateListener() {
            @Override
            public void noUpdate() {
                login();
            }

            @Override
            public void updateNext() {
                login();
            }

            @Override
            public void update() {

            }
        });
        updateMan.checkUpdate();
    }

    private void login() {
        final String[] userInfo = PreferenceUtil.getUserInfo(this);
        if (!TextUtils.isEmpty(userInfo[0]) && !TextUtils.isEmpty(userInfo[1])) {
            registerPush(userInfo[0]);
        } else {
            gotoLoginActivity("", false);
        }
    }

    private void registerPush(final String account) {
        mTimeOutThread = new TimeOutThread(TIMEOUT);
        XGPushManager.registerPush(getApplicationContext(), account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                mToken = o.toString();
                LogUtil.d("TPush", "注册成功，设备token为：" + o + " , account is " + account);
//                ToastUtil.showShort(WelcomeActivity.this, "注册成功，设备token为：" + o);
                //未超时
                if (mTimeOutThread != null && mTimeOutThread.isAlive()) {
                    mTimeOutThread.cancel();
                    mTimeOutThread = null;
                    gotoLoginActivity(mToken, true);
                } else {
                    if (TextUtils.isEmpty(PreferenceUtil.getXGToken(WelcomeActivity.this))) {
                        gotoLoginActivity(mToken, true);
                    }
                }
                PreferenceUtil.saveXGToken(WelcomeActivity.this, mToken);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtil.d("TPush", "注册失败，错误码：" + i + ",错误信息：" + s);
                ToastUtil.showShort(WelcomeActivity.this, "注册失败，错误码：" + i + ",错误信息：" + s);
                if (mTimeOutThread != null && mTimeOutThread.isAlive()) {
                    mTimeOutThread.cancel();
                    mTimeOutThread = null;
                    gotoLoginActivity(PreferenceUtil.getXGToken(WelcomeActivity.this), true);
                } else {
                    if (TextUtils.isEmpty(PreferenceUtil.getXGToken(WelcomeActivity.this))) {
                        gotoLoginActivity(mToken, true);
                    }
                }

            }
        });
        mTimeOutThread.start();
    }
}
