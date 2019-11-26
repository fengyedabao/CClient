package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.AccountController;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.lib.qrcode.CaptureActivity;

/**
 * Created by shushunsakai on 16/6/20.
 */
public class AccountSettingActivity extends CubeTitleBarActivity implements View.OnClickListener {

    private RelativeLayout mRelativeLayoutSetName;
    private RelativeLayout mRelativeLayoutChangePwd;
    private RelativeLayout mRelativeLayoutChangeCube;
    private RelativeLayout mRelativeLayoutCubeSetting;
    private TextView mTextViewTelePhone;
    private TextView mTextViewCubeName;

    @Override
    protected int getContent() {
        return R.layout.activity_account_setting;
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.account_setting_title);
    }


    @Override
    public void initView() {
        mRelativeLayoutSetName = (RelativeLayout) findViewById(R.id.rl_device_name);
        mRelativeLayoutChangePwd = (RelativeLayout) findViewById(R.id.rl_change_pwd);
        mRelativeLayoutChangeCube = (RelativeLayout) findViewById(R.id.rl_change_cube);
        mRelativeLayoutCubeSetting = (RelativeLayout) findViewById(R.id.rl_cube_setting);
        mTextViewTelePhone = (TextView) findViewById(R.id.tv_telephone);
        mTextViewCubeName = (TextView) findViewById(R.id.tv_change_cube);
        initEvent();
        initData();
    }

    public void initEvent() {
        mRelativeLayoutSetName.setOnClickListener(this);
        mRelativeLayoutChangePwd.setOnClickListener(this);
        mRelativeLayoutChangeCube.setOnClickListener(this);
        mRelativeLayoutCubeSetting.setOnClickListener(this);
    }

    public void initData() {
        mTextViewTelePhone.setText(AccountController.getAccountPhoneNum(this));
        mTextViewCubeName.setText(AccountController.getCubeName(this));

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        if (v == mRelativeLayoutSetName) {
            intent = new Intent(AccountSettingActivity.this, CubeSettingCommeActivity.class);
            intent.putExtra("intent_tag", CubeSettingCommeActivity.SETTING_DEVICE_NAME);
        } else if (v == mRelativeLayoutChangePwd) {
            intent = new Intent(AccountSettingActivity.this, CubeSettingCommeActivity.class);
            intent.putExtra("intent_tag", CubeSettingCommeActivity.CHANGE_PWD);

        } else if (v == mRelativeLayoutChangeCube) {

            if (AppInfoFunc.getBindDeviceId(this) == 0) {
                Intent i = new Intent(this, CaptureActivity.class);
                i.putExtra("capture_type", ModelEnum.SCANED_TYPE_BIND_CUBE);
                startActivity(i);
            } else {
                intent = new Intent(AccountSettingActivity.this, CubeSettingCommeActivity.class);
                intent.putExtra("intent_tag", CubeSettingCommeActivity.EXCHANGE_CUBE);
            }

        } else if (v == mRelativeLayoutCubeSetting) {
            intent = new Intent();
            intent.setClass(this, CubeSettingActivity.class);

        }
        startActivity(intent);

    }
}
