package com.honeywell.cube.activities;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ScenarioEditConfigureWIAdapter;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScenarioEvent;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.HeadListView;


import de.greenrobot.event.EventBus;

public class ScenarioEditConfigureWIActivity extends CubeTitleBarActivity {
    private static final String TAG = ScenarioEditConfigureWIActivity.class.getSimpleName();
    HeadListView mContent;
    View mLayoutBottom;
    View mLayoutEdit;
    Button mBtnComplete;
    Button mBtnAdd;
    Button mBtnDelete;
    IrLoop mIrLoop;
    ScenarioEditConfigureWIAdapter adapter = null;

    @Override
    protected int getContent() {
        return R.layout.activity_scenario_edit_configure_wi;
    }


    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIrLoop = ScenarioController.updateScenarioIrLoopList(mIrLoop, adapter.getScenarioDeviceIrUIItems());
                EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_WI, mIrLoop));
                finish();

            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mIrLoop = getIntent().getParcelableExtra(Constants.CONTENT);
        if (mIrLoop == null) {
            LogUtil.e(TAG, "initIntentValue mIrLoop is null", true);
        } else {
            mTitle = mIrLoop.mLoopName;
        }

    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (HeadListView) findViewById(R.id.lv_list);
        mLayoutBottom = findViewById(R.id.layout_bottom);
        mLayoutEdit = findViewById(R.id.layout_edit);
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mBtnDelete = (Button) findViewById(R.id.btn_delete);
        mBtnComplete = (Button) findViewById(R.id.btn_complete);
        mLayoutBottom.setVisibility(View.VISIBLE);
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeleteMode(false);

            }
        });
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem();
                mContent.setSelection(adapter.getCount() - 1);
            }
        });
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeleteMode(true);
            }
        });
    }

    private void setDeleteMode(boolean deleteMode) {
        mBtnComplete.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
        adapter.setDeleteMode(deleteMode);
        View headerView = mContent.getPinnedHeaderView();
        if (headerView != null) {
            View delete = headerView.findViewById(R.id.iv_delete);
            if (delete != null) {
                delete.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
            }
        }
    }


    @Override
    protected void getData() {
        super.getData();

        adapter = new ScenarioEditConfigureWIAdapter(this, ScenarioController.getScenarioIRDeviceList(this, mIrLoop), getLoadingDialog(), ScenarioController.getScenarioIRLoopList(this, mIrLoop));
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
                    Intent intent = new Intent();
                    intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToastShort(R.string.operation_failed_tip);
                }
            }
        }
    }

}
