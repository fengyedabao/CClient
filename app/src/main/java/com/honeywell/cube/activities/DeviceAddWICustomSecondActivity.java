package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.SelectIRCustomIconAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuIRCode;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.GridViewGallery;

import java.util.ArrayList;

public class DeviceAddWICustomSecondActivity extends CubeTitleBarActivity {
    GridViewGallery mContent;
    Button mClear;
    SelectIRCustomIconAdapter mAdapter;
    ArrayList<MenuDeviceIRIconItem> mDataList;
    MenuDeviceIRUIItem mMenuDeviceIRUIItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_custom_first;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMenuDeviceIRUIItem.IR_Code_List == null || mMenuDeviceIRUIItem.IR_Code_List.size() == 0) {
                    showToastShort(R.string.study_one_item);
                    return;
                }
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        MenuDeviceController.addIRWithInfo(DeviceAddWICustomSecondActivity.this, mMenuDeviceIRUIItem);
                    }
                });

            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mDataList = getIntent().getParcelableArrayListExtra(Constants.CONTENT);
        mMenuDeviceIRUIItem = getIntent().getParcelableExtra(Constants.CONTENT2);

    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (GridViewGallery) findViewById(R.id.gvg_content);
        mAdapter = new SelectIRCustomIconAdapter(this, mDataList, SelectIRCustomIconAdapter.ICON_SELECT_SECONDARY, getLoadingDialog());
        mContent.setAdapter(mAdapter);
        mContent.setNumColumns(4);
        mContent.setNumRows(4);
        mClear = (Button) findViewById(R.id.btn_clear);
        mClear.setVisibility(View.GONE);
    }

    int mPosition = -1;

    public void studyIrCode(int position, final MenuDeviceIRIconItem item) {
        mPosition = position;
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                MenuDeviceController.studyIrCodeWithInfo(DeviceAddWICustomSecondActivity.this, mMenuDeviceIRUIItem, item);
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
                if (ev.getSuccess()) {
                    showToastShort(R.string.operation_success_tip);
                    mMenuDeviceIRUIItem.addMenuCodeList((MenuIRCode) ev.getUpdateStatusData(), false);
                    mAdapter.update(mPosition, true);
                    mContent.notifyDataSetChanged();
                } else {
                    showToastShort(ev.getMessage());
                }

            } else if (ev.getType() == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE) {
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
