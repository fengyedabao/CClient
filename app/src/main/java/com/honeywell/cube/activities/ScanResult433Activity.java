package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.ScanController;
import com.honeywell.cube.controllers.UIItem.Scan433UIItem;
import com.honeywell.cube.controllers.UIItem.ScanUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.cube.widgets.TimeSelectorItem;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.utils.LogUtil;

public class ScanResult433Activity extends CubeTitleBarActivity {
    public static final String TAG = ScanResult433Activity.class.getSimpleName();
    SelectItem mSensorType;
    EditTextItem mSensorID;
    SelectItem mZoneModule;

    EditTextItem mName;
    SelectItem mRoom;
    SelectItem mZoneType;
    TimeSelectorItem mDelay;
    View mDelayGroup;
    ScanUIItem mScanUIItem;
    Scan433UIItem mScan433UIItem;

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.configure);
    }

    @Override
    protected void initView() {
        super.initView();
        mSensorType = (SelectItem) findViewById(R.id.si_sensor_type);
        mSensorID = (EditTextItem) findViewById(R.id.ei_sensor_id);
        mZoneModule = (SelectItem) findViewById(R.id.si_zone_module);

        mName = (EditTextItem) findViewById(R.id.ei_name);
        mRoom = (SelectItem) findViewById(R.id.si_room);
        mZoneType = (SelectItem) findViewById(R.id.si_zone_type);
        mDelay = (TimeSelectorItem) findViewById(R.id.tsi_delay);
        mDelayGroup = findViewById(R.id.ll_delay);

        mSensorType.setName(R.string.sensor_type);
        mSensorType.setDataList(DeviceHelper.getSensorType(this));
        mSensorType.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                mScan433UIItem.sensor_433_type = item.mText;
            }
        });

        mSensorID.setTextName(R.string.sensor_id);
        mSensorID.getEditName().setClickable(false);

        mZoneModule.setName(R.string.zone_module);
        DeviceHelper.initModuleData(mZoneModule, ModelEnum.MODULE_TYPE_WIFI315M433M);
        mZoneModule.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(ScanResult433Activity.this, ModuleListActivity.class);
                    startActivity(intent);
                } else {
                    mZoneModule.setContent(position);
                    BottomDialog.ItemBean item = mZoneModule.getItem(position);
                    mScan433UIItem.mainDevice = (PeripheralDevice) item.mData;
                    mScan433UIItem.mainDeviceName = item.mText;
                }
            }
        });
        mName.setTextName(R.string.name);
        mName.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                mScan433UIItem.name = s;
            }
        });

        DeviceHelper.initRoom(mRoom);
        mRoom.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                mScan433UIItem.roomName = item.mText;
                mScan433UIItem.roomId = (int) item.mData;

            }
        });

        mZoneType.setName(R.string.zone_type);
        mZoneType.setDataList(DeviceHelper.getZoneTypeList(this));
        mZoneType.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                mScan433UIItem.zoneType = item.mText;
                mDelayGroup.setVisibility(item.mText.equals(mZoneType.getItem(1).mText) ? View.VISIBLE : View.GONE);
            }
        });

        mDelay.setName(R.string.delay_time);
        mDelay.setOnTimeSelectListener(new TimeSelectorItem.OnTimeSelectListener() {
            @Override
            public void timeSelect(int seconds) {
                mScan433UIItem.delaytime = seconds;
            }

            @Override
            public void timeSelect(String time) {
            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mScanUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected void getData() {
        super.getData();
        if (mScanUIItem != null) {
            mScan433UIItem = ScanController.getDefault433UIItem(this, mScanUIItem.id);
            mSensorType.setContent(mScan433UIItem.sensor_433_type);
            mSensorID.setEditName(mScan433UIItem.id);
            mZoneModule.setContent(mScan433UIItem.mainDeviceName);

            mName.setEditName(mScan433UIItem.name);
            mRoom.setContent(mScan433UIItem.roomName);
            mZoneType.setContent(mScan433UIItem.zoneType);
            mDelay.setTime(mScan433UIItem.delaytime);
            mDelayGroup.setVisibility(mScan433UIItem.zoneType.equals(mZoneType.getItem(1).mText) ? View.VISIBLE : View.GONE);
        } else {
            LogUtil.e(TAG, "initIntentValue  mScanUIItem =  " + mScanUIItem, true);
        }
    }

    @Override
    protected int getContent() {
        return R.layout.activity_scan_result_433;
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
                        ScanController.add433Device(ScanResult433Activity.this, mScan433UIItem);
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
            if (ev.type == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE) {
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
