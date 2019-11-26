package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.DeviceACListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuIRCode;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DeviceAddWIAirConditionerSecondActivity extends CubeTitleBarActivity {
    ListView mContent = null;
    DeviceACListAdapter mAdapter;
    Button mAdd;
    MenuDeviceIRUIItem mMenuDeviceIRUIItem;

    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_ac_second;
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuDeviceIRUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        MenuDeviceController.addIRWithInfo(DeviceAddWIAirConditionerSecondActivity.this, mMenuDeviceIRUIItem);
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (ListView) findViewById(R.id.lv_list);
        List<MenuIRCode> list = mMenuDeviceIRUIItem.IR_Code_List;
        List<DeviceACListAdapter.ItemBean> result = new ArrayList<>();
        if (list != null) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                result.add(new DeviceACListAdapter.ItemBean(list.get(i).name, list.get(i)));
            }
        }
        mAdapter = new DeviceACListAdapter(result);
        mContent.setAdapter(mAdapter);
        mAdd = (Button) findViewById(R.id.btn_add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            dismissLoadingDialog();
            if (ev.getType() == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE) {
                if (ev.getSuccess()) {
                    showToastShort(R.string.operation_success_tip);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToastShort(ev.getMessage());
                }
            }
        }
    }
}
