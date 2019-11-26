package com.honeywell.cube.activities;

import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.SelectIRCustomIconAdapter;
import com.honeywell.cube.controllers.DeviceControllers.IRLoopController;
import com.honeywell.lib.widgets.GridViewGallery;


public class DeviceWICustomActivity extends DeviceWIBaseActivity {
    GridViewGallery mContent;
    SelectIRCustomIconAdapter mAdapter;

    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_custom_first;
    }


    @Override
    protected void initView() {
        super.initView();
        mContent = (GridViewGallery) findViewById(R.id.gvg_content);
        findViewById(R.id.btn_clear).setVisibility(View.GONE);
        onTypeChanged();
    }

    @Override
    protected void onTypeChanged() {
        super.onTypeChanged();
        mAdapter = new SelectIRCustomIconAdapter(this, IRLoopController.getCustomIrIcons(this, mCurrentIrLoop), SelectIRCustomIconAdapter.ICON_CONTROL, getLoadingDialog());
        mContent.setAdapter(mAdapter);
        mContent.setNumColumns(4);
        mContent.setNumRows(4);
    }


}
