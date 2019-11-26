package com.honeywell.cube.activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.ScanController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.net.easylink.EasyLinkManager;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeLoginEvent;
import com.honeywell.lib.qrcode.CaptureActivity;
import com.honeywell.cube.utils.DialogUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScanEvent;
import com.honeywell.cube.controllers.UIItem.ScanUIItem;
import com.honeywell.lib.utils.ToastUtil;

/**
 * Created by zhujunyu on 16/7/7.
 */
public class CaptureResultActivity extends CubeTitleBarActivity {

    private TextView mTextViewTitle;
    private TextView resultTextView;
    private EditText mEtPassword;
    private Button mButtonLink;
    private String resultString;
    private Bundle bundle;

    private static int CAPTURE_TYPE;

    @Override
    protected int getContent() {
        switch (CAPTURE_TYPE) {
            case ModelEnum.SCANED_TYPE_BIND_CUBE:
            case ModelEnum.SCANED_TYPE_WIFI_LOGIN:
            case ModelEnum.SCANED_TYPE_REPLACE_CUBE:
                return R.layout.activity_result;
            case ModelEnum.SCANED_TYPE_ADD_CUBE_DEVICE:
                ScanUIItem scanUIItem = ScanController.checkScanResult(resultString);
                if (scanUIItem.scanType == ModelEnum.SCAN_TYPE_433) {
                    Intent intent = new Intent(this, ScanResult433Activity.class);
                    DeviceHelper.addObject2Intent(intent, Constants.CONTENT, scanUIItem);
                    startActivityForResult(intent, 1);
                } else if (scanUIItem.scanType == ModelEnum.SCAN_TYPE_MAIA) {
                    Intent intent = new Intent(this, ScanResultMaiaActivity.class);
                    DeviceHelper.addObject2Intent(intent, Constants.CONTENT, scanUIItem);
                    startActivityForResult(intent, 1);
                }

                return R.layout.activity_result;
        }
        return R.layout.activity_result;
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setText(R.string.link_cubee);
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        resultString = bundle.getString("resultString");
        CAPTURE_TYPE = bundle.getInt("capture_type");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void initView() {
        resultTextView = (TextView) findViewById(R.id.id_contents_text_view);
        mTextViewTitle = (TextView) findViewById(R.id.tv_title);
        mEtPassword = (EditText) findViewById(R.id.password_contents_text_view);
        mButtonLink = (Button) findViewById(R.id.btn_link);
        setView();
        initEvent();

    }

    public void initEvent() {
        mButtonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtPassword.getText())) {
                    ToastUtil.showShort(CaptureResultActivity.this, getApplicationContext().getString(R.string.input_password));
                    return;
                }
                showLoadingDialog();
                ScanController.connectToCube(CaptureResultActivity.this, resultString, mEtPassword.getText().toString(), CAPTURE_TYPE);

            }
        });
    }


    private void setView() {
        resultTextView.setText(resultString);
        mTextViewTitle.setText(getApplicationContext().getString(R.string.main_connect));
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            runBack();
        }
        return false;
    }

    public void runBack() {
        Intent intent = new Intent(CaptureResultActivity.this, CaptureActivity.class);
        startActivity(intent);
        CaptureResultActivity.this.finish();
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeScanEvent) {
            dismissLoadingDialog();
            CubeScanEvent cubeScanEvent = (CubeScanEvent) event;
            if (cubeScanEvent.type == CubeEvents.CubeScanEventType.START_EASY_LINK) {
                String wifiName = getWifiSSID(CaptureResultActivity.this);
                popDialog(wifiName);
            } else if (cubeScanEvent.type == CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT) {
                ToastUtil.showShort(getApplicationContext(), cubeScanEvent.message);
            }
        }
        if (event instanceof CubeLoginEvent) {
            dismissLoadingDialog();
            CubeLoginEvent loginEvent = (CubeLoginEvent) event;
            if (loginEvent.type == CubeEvents.CubeLoginEventType.LOGIN_SOCKET) {
                if (loginEvent.success) {
                    finish();
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    ToastUtil.showShort(this, (String) loginEvent.item);
                }
            }
        }
        if (event instanceof CubeBasicEvent) {
            CubeBasicEvent basicEvent = (CubeBasicEvent) event;
            if (basicEvent.type == CubeEvents.CubeBasicEventType.TIME_OUT) {
                dismissLoadingDialog();
                ToastUtil.showShort(this, getString(R.string.error_time_out));
            }
        }
    }


    private String getWifiSSID(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiSIID = info != null ? info.getSSID() : null;
        //获取Android版本号
        int deviceVersion = Build.VERSION.SDK_INT;
        if (deviceVersion >= 17) {
            if (wifiSIID.startsWith("\"") && wifiSIID.endsWith("\"")) {
                wifiSIID = wifiSIID.substring(1, wifiSIID.length() - 1);
            }
        }

        return wifiSIID;
    }

    private void popDialog(final String wifiSSID) {
        DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, wifiSSID);
        build.createDialog().createSearchDeviceView(new DialogUtil.DialogBuild.DialogEditListener() {
            @Override
            public void confirm(final String password) {

//
                new Thread() {
                    public void run() {
                        EasyLinkManager.newInstance(CaptureResultActivity.this).startEasyLink(password);

                        EasyLinkManager.newInstance(CaptureResultActivity.this).startConfigBroadLink(wifiSSID, password);
                    }
                }.start();

                DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(CaptureResultActivity.this, null);
                build.createDialog().createSearchDeviceViewGif(new DialogUtil.DialogBuild.DialogListener() {
                    @Override
                    public void confirm() {

                    }
                });
            }
        });
    }
}
