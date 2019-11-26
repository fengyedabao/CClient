package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.DeviceAddBackaudioAdapter;
import com.honeywell.cube.adapter.DeviceAddBacnetAdapter;
import com.honeywell.cube.adapter.DeviceAddDetailBaseAdapter;
import com.honeywell.cube.adapter.DeviceAddIPCameraAdapter;
import com.honeywell.cube.adapter.DeviceAddIPVDPAdapter;
import com.honeywell.cube.adapter.DeviceAddSparkLightingAdapter;
import com.honeywell.cube.adapter.DeviceAddVentilationAdapter;
import com.honeywell.cube.adapter.DeviceAddWifi485Adapter;
import com.honeywell.cube.adapter.DeviceAddWifiRelayAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.widgets.HeadListView;


public class DeviceAddDetailActivity extends CubeTitleBarActivity {
    HeadListView mContent;
    int mSparkLoop;
    String mSparkTitle;
    String mSparkType;
    MenuDeviceUIItem mItem;
    View mLayoutBottom;
    View mLayoutEdit;
    Button mBtnComplete;
    Button mBtnAdd;
    Button mBtnDelete;

    @Override
    protected int getContent() {
        return R.layout.activity_add_device_detail;
    }


    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ModelEnum.MODULE_TYPE_BACNET != mType && ModelEnum.MODULE_TYPE_WIFI485 != mType&&ModelEnum.MODULE_TYPE_VENTILATION != mType) {
                    if (!((DeviceAddDetailBaseAdapter) mContent.getAdapter()).isDeviceSelected()) {
                        showToastShort(R.string.device_add_should_select_device);
                        return;
                    }
                }
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        switch (mType) {
                            case ModelEnum.MODULE_TYPE_SPARKLIGHTING:
                                MenuDeviceController.addSparkLightingLoop(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_WIFIRELAY:
                                MenuDeviceController.addRelayLoop(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_WIREDZONE:
                                MenuDeviceController.addWiredZoneLoop(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_IPVDP:
                                MenuDeviceController.addIPVDPZone(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_BACKAUDIO:
                                MenuDeviceController.addBackaudio(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_BACNET:
                                MenuDeviceController.addBacnet(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_WIFI485:
                                MenuDeviceController.addWifi485(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_IPC:
                                MenuDeviceController.addIpcamera(DeviceAddDetailActivity.this, ((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                            case ModelEnum.MODULE_TYPE_VENTILATION:
                                MenuDeviceController.addVentilation(DeviceAddDetailActivity.this, ((DeviceAddVentilationAdapter) mContent.getAdapter()).getVentilationData());
                                break;
                            case ModelEnum.MODULE_TYPE_WIFIIR:
//                                MenuDeviceController.addw(DeviceAddDetailActivity.this,((DeviceAddDetailBaseAdapter) mContent.getAdapter()).getData());
                                break;
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        final Intent intent = getIntent();
        if (ModelEnum.MODULE_TYPE_SPARKLIGHTING == mType) {
            mSparkLoop = intent.getIntExtra(Constants.SPARK_LOOP, 0);
            mSparkTitle = intent.getStringExtra(Constants.SPARK_TITLE);
            mSparkType = intent.getStringExtra(Constants.SPARK_TYPE);
        }

    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (HeadListView) findViewById(R.id.lv_list);
        if (ModelEnum.MODULE_TYPE_BACNET == mType || ModelEnum.MODULE_TYPE_WIFI485 == mType) {
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
                    ((DeviceAddBacnetAdapter) adapter).addItem();
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
    }

    private void setDeleteMode(boolean deleteMode) {
        mBtnComplete.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
        ((DeviceAddBacnetAdapter) adapter).setDeleteMode(deleteMode);
        View headerView = mContent.getPinnedHeaderView();
        if (headerView != null) {
            View delete = headerView.findViewById(R.id.iv_delete);
            if (delete != null) {
                delete.setVisibility(deleteMode ? View.VISIBLE : View.GONE);
            }
        }
    }

    DeviceAddDetailBaseAdapter adapter = null;

    @Override
    protected void getData() {
        super.getData();

        switch (mType) {

            case ModelEnum.MODULE_TYPE_SPARKLIGHTING:
                mItem = MenuDeviceController.getSparkLightingDetails(this, mSparkLoop, mSparkTitle, mSparkType);
                adapter = new DeviceAddSparkLightingAdapter(this, mItem, getLoadingDialog());
                break;
            case ModelEnum.MODULE_TYPE_WIFIRELAY:
                mItem = MenuDeviceController.getRelayDetails(this);
                adapter = new DeviceAddWifiRelayAdapter(this, mItem, getLoadingDialog());
                break;
            case ModelEnum.MODULE_TYPE_WIREDZONE:
                mItem = MenuDeviceController.getWiredZoneDetails(this);
                adapter = new DeviceAddIPVDPAdapter(this, mItem, getLoadingDialog(), false);
                break;
            case ModelEnum.MODULE_TYPE_IPVDP:
                mItem = MenuDeviceController.getIPVDPZoneDetails(this);
                adapter = new DeviceAddIPVDPAdapter(this, mItem, getLoadingDialog(), true);
                break;
            case ModelEnum.MODULE_TYPE_BACKAUDIO:
                mItem = MenuDeviceController.getBackaudioDetails(this);
                adapter = new DeviceAddBackaudioAdapter(this, mItem, getLoadingDialog());
                break;
            case ModelEnum.MODULE_TYPE_BACNET:
                mItem = MenuDeviceController.getBacnectLoopDetails(this);
                adapter = new DeviceAddBacnetAdapter(this, mItem, getLoadingDialog());
                break;
            case ModelEnum.MODULE_TYPE_WIFI485:
                mItem = MenuDeviceController.getWifi485Details(this);
                adapter = new DeviceAddWifi485Adapter(this, mItem, getLoadingDialog());
                break;
            case ModelEnum.MODULE_TYPE_IPC:
                mItem = MenuDeviceController.getIpcamera(this);
                adapter = new DeviceAddIPCameraAdapter(this, mItem, getLoadingDialog());
                break;
            case ModelEnum.MODULE_TYPE_VENTILATION:
                adapter = new DeviceAddVentilationAdapter(this, MenuDeviceController.getDefaultVentilationObject(this), getLoadingDialog());
                break;
            case ModelEnum.MODULE_TYPE_WIFIIR:
                break;
        }
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
                    showToastShort(ev.getMessage());
                }
            }
        }
    }
}
