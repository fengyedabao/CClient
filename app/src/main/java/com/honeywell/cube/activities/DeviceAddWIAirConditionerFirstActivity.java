package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuIRCode;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.utils.LogUtil;

import java.util.List;

public class DeviceAddWIAirConditionerFirstActivity extends CubeTitleBarActivity {
    GridView mContent;
    EditTextItem mName;
    SelectItem mTemperature;
    SelectItem mMode;
    Button mLearn;
    MenuDeviceIRUIItem mMenuDeviceIRUIItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_air_conditioner;
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.code_mode);
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuDeviceIRUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected void initView() {
        super.initView();
        mName = (EditTextItem) findViewById(R.id.ei_name);
        mName.setTextName(R.string.mode_name);
        mName.setEditName(R.string.air_conditioner);

        mTemperature = (SelectItem) findViewById(R.id.si_temperature);
        List<BottomDialog.ItemBean> list = DeviceHelper.getACTemperatureList(this);
        mTemperature.setDataList(list);
        mTemperature.setName(R.string.temperature);
        mTemperature.setContent(list.size() > 0 ? list.get(0).mText : "");


        mMode = (SelectItem) findViewById(R.id.si_mode);
        List<BottomDialog.ItemBean> list2 = DeviceHelper.getACModeTypeList(this);
        mMode.setDataList(list2);
        mMode.setName(R.string.mode);
        mMode.setContent(list2.size() > 0 ? list2.get(0).mText : "");

        mLearn = (Button) findViewById(R.id.btn_learn);
        mLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mName.getContent())) {
                    showToastShort(R.string.input_name);
                    return;
                }
                mMenuDeviceIRUIItem.IR_AC_name = mName.getContent();
                mMenuDeviceIRUIItem.IR_AC_temperature = (int) mTemperature.getContentObject();
                mMenuDeviceIRUIItem.IR_AC_mode = CommonUtils.tansferIrAcModeProtocolFromStr(DeviceAddWIAirConditionerFirstActivity.this, mMode.getContentText());
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        MenuDeviceController.studyAirControllerCodeWithInfo(DeviceAddWIAirConditionerFirstActivity.this, mMenuDeviceIRUIItem);
                    }
                });

            }
        });
    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            dismissLoadingDialog();
            if (ev.getType() == CubeEvents.CubeDeviceEventType.DEVICE_IR_STUDY) {
                LogUtil.e("alinmi22", "DEVICE_IR_STUDY  ev.getSuccess() = " + ev.getSuccess());
                if (ev.getSuccess()) {
                    showToastShort(R.string.operation_success_tip);
                    mMenuDeviceIRUIItem.addMenuCodeList((MenuIRCode) ev.getUpdateStatusData(), true);
                    Intent intent = new Intent(DeviceAddWIAirConditionerFirstActivity.this, DeviceAddWIAirConditionerSecondActivity.class);
                    intent.putExtra(Constants.TITLE, getString(R.string.configure));
                    DeviceHelper.addObject2Intent(intent, Constants.CONTENT, mMenuDeviceIRUIItem);
                    startActivityForResult(intent, 1);
                } else {
                    showToastShort(ev.getMessage());
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
