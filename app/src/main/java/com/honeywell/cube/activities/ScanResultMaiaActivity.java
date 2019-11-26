package com.honeywell.cube.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ScanResultMaiaAdapter;
import com.honeywell.cube.controllers.ScanController;
import com.honeywell.cube.controllers.UIItem.ScanMaiaUIItem;
import com.honeywell.cube.controllers.UIItem.ScanUIItem;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.HeadListView;


public class ScanResultMaiaActivity extends CubeTitleBarActivity {
    HeadListView mContent;
    ScanUIItem mScanUIItem;
    ScanMaiaUIItem mScanMaiaUIItem;
    ScanResultMaiaAdapter adapter = null;

    @Override
    protected int getContent() {
        return R.layout.activity_add_device_detail;
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.configure);
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
                        ScanController.addMaiaDevice(ScanResultMaiaActivity.this, adapter.getMaiaData());
                    }
                });
            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mScanUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (HeadListView) findViewById(R.id.lv_list);

    }

    @Override
    protected void getData() {
        super.getData();
        mScanMaiaUIItem = ScanController.getDefaultMaiaUIItem(this, mScanUIItem);
        LogUtil.e("alinmi22", "mScanMaiaUIItem = " + mScanMaiaUIItem);
        adapter = new ScanResultMaiaAdapter(this, mScanMaiaUIItem, getLoadingDialog());
        mContent.setAdapter(adapter);
        mContent.setOnScrollListener(adapter);
        mContent.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_device_add_detail_section, mContent, false));

    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {

            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            if (((CubeDeviceEvent) event).getType() == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE) {
                dismissLoadingDialog();
                if (ev.getSuccess()) {
                    showToastShort(R.string.operation_success_tip);
                    finishSuccess();
                } else {
                    showToastShort(R.string.operation_failed_tip);
                }
            }
        }
    }

}
