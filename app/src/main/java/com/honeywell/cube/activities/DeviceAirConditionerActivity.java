package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.IconSelectAdapter;
import com.honeywell.cube.controllers.DeviceControllers.AirController;
import com.honeywell.cube.controllers.UIItem.AirControllerUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScenarioEvent;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.CircleSeekBar;

import de.greenrobot.event.EventBus;


public class DeviceAirConditionerActivity extends CubeTitleBarActivity {
    GridView mMode;
    GridView mSpeed;
    IconSelectAdapter mModeAdapter;
    IconSelectAdapter mSpeedAdapter;
    BasicLoop mLoop;
    AirControllerUIItem mAirControllerUIItem;
    ImageView mPower;
    CircleSeekBar mTemperature;
    TextView mCurrentTemperature;
    private int mOperationType;
    private final int TYPE_POWER = 0;
    private final int TYPE_TEMPERATURE = 1;
    private final int TYPE_MODE = 2;
    private final int TYPE_SPEED = 3;

    private int mLastTemperature;
    MenuScheduleDeviceObject mMenuScheduleDeviceObject;
    private boolean isFromScenario = false;

    @Override
    protected int getContent() {
        return R.layout.activity_air_conditioner;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        if (isSetStatus()) {
            mRight.setImageResource(R.mipmap.nav_done);
            mRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AirController.updateLoopFromUIItem(mLoop, mAirControllerUIItem);
                    if (isFromScenario) {
                        EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_WI, mLoop));
                    } else {
                        mMenuScheduleDeviceObject = new MenuScheduleDeviceObject();
                        mMenuScheduleDeviceObject.loop = mLoop;
                        mMenuScheduleDeviceObject.title = mLoop.mLoopName;
                        if (mLoop instanceof BacnetLoop) {
                            mMenuScheduleDeviceObject.loopType = ModelEnum.LOOP_BACNET;
                        } else if (mLoop instanceof Wifi485Loop) {
                            mMenuScheduleDeviceObject.loopType = ModelEnum.WIFI_485;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_DEVICE);
                        DeviceHelper.addObject2Intent(intent, Constants.CONTENT, mMenuScheduleDeviceObject);
                        intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }
            });
        }

    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        if (isSetStatus()) {
            isFromScenario = getIntent().getBooleanExtra(Constants.IS_FROM_SCENARIO, false);
            if (isFromScenario) {
                mLoop = getIntent().getParcelableExtra(Constants.CONTENT);//BacnetLoop    Wifi485Loop
            } else {
                MenuScheduleDeviceObject object = getIntent().getParcelableExtra(Constants.CONTENT2);
                mLoop = (BasicLoop) object.loop;
            }

        } else {
            mLoop = getIntent().getParcelableExtra(Constants.CONTENT);//BacnetLoop    Wifi485Loop
        }

    }

    @Override
    protected void getData() {
        super.getData();
        if (!isSetStatus()) {
            startAsynchronousOperation(new Runnable() {
                @Override
                public void run() {
                    startAsynchronousOperation(new Runnable() {
                        @Override
                        public void run() {
                            AirController.getAirControllerUIItemAndReadStatusWithInfo(DeviceAirConditionerActivity.this, mLoop);
                        }
                    });
                }
            });
        }
    }

    protected void initView() {
        mAirControllerUIItem = AirController.getAirControllerUIItem(this, mLoop);
        mPower = (ImageView) findViewById(R.id.cb_power);
        mPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSetStatus()) {
                    final boolean isSelect = mAirControllerUIItem.powerIconUIItem.IR_icon_select;
                    mAirControllerUIItem.powerIconUIItem.IR_icon_select = !isSelect;
                    updatePower(mAirControllerUIItem.powerIconUIItem);
                } else {
                    startAsynchronousOperation(new Runnable() {
                        @Override
                        public void run() {
                            mOperationType = TYPE_POWER;
                            final boolean isSelect = mAirControllerUIItem.powerIconUIItem.IR_icon_select;
                            mAirControllerUIItem.powerIconUIItem.IR_icon_select = !isSelect;
                            AirController.sendControlIconItem(DeviceAirConditionerActivity.this, mLoop, mAirControllerUIItem, mAirControllerUIItem.powerIconUIItem);
                        }
                    });
                }
            }
        });
        updatePower(mAirControllerUIItem.powerIconUIItem);
        mTemperature = (CircleSeekBar) findViewById(R.id.sb_temperature);
        mTemperature.setSeekBarChangerListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircleSeekBar seekBar, int progress) {
//                LogUtil.e("alinmi21", "onProgressChanged progress = " + progress);
                if (isSetStatus()) {
                    mAirControllerUIItem.setTemp = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(CircleSeekBar seekBar) {
//                LogUtil.e("alinmi21", "onStartTrackingTouch progress = " + seekBar.getCurrentProgress());
                mLastTemperature = seekBar.getCurrentProgress();
            }

            @Override
            public void onStopTrackingTouch(final CircleSeekBar seekBar, final int progress) {
                LogUtil.e("alinmi21", "onStopTrackingTouch progress = " + progress);
                if (!isSetStatus()) {
                    startAsynchronousOperation(new Runnable() {
                        @Override
                        public void run() {
                            mOperationType = TYPE_TEMPERATURE;
                            mAirControllerUIItem.curTemp = progress;
                            AirController.sendControlTemperature(DeviceAirConditionerActivity.this, mLoop, mAirControllerUIItem, mAirControllerUIItem.curTemp);
                        }
                    });
                }

            }
        });
        mCurrentTemperature = (TextView) findViewById(R.id.tv_current_temperature);
        mCurrentTemperature.setText(mAirControllerUIItem.curTemp <= 0 ? "_ _" : (mAirControllerUIItem.curTemp + ""));
        mMode = (GridView) findViewById(R.id.gv_mode);
        mModeAdapter = new IconSelectAdapter(mAirControllerUIItem.airModeIconItemList);
        mMode.setAdapter(mModeAdapter);
        mMode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (isSetStatus()) {
                    mModeAdapter.setSelectPosition(position);
                    mModeAdapter.updateResult();
                } else {
                    startAsynchronousOperation(new Runnable() {
                        @Override
                        public void run() {
                            mOperationType = TYPE_MODE;
                            mModeAdapter.setSelectPosition(position);
                            AirController.sendControlIconItem(DeviceAirConditionerActivity.this, mLoop, mAirControllerUIItem, mModeAdapter.getDataList().get(position));
                        }
                    });
                }
            }
        });

        mSpeed = (GridView) findViewById(R.id.gv_speed);
        mSpeedAdapter = new IconSelectAdapter(mAirControllerUIItem.fanspeedIconItemList);
        mSpeed.setAdapter(mSpeedAdapter);
        mSpeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (isSetStatus()) {
                    mSpeedAdapter.setSelectPosition(position);
                    mSpeedAdapter.updateResult();
                } else {
                    startAsynchronousOperation(new Runnable() {
                        @Override
                        public void run() {
                            mOperationType = TYPE_SPEED;
                            mSpeedAdapter.setSelectPosition(position);
                            AirController.sendControlIconItem(DeviceAirConditionerActivity.this, mLoop, mAirControllerUIItem, mSpeedAdapter.getDataList().get(position));
                        }
                    });
                }
            }
        });

    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {

            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            if (ev.getType() == CubeEvents.CubeDeviceEventType.UPDATE_AIR_CONTROLLER_STATE) {
                dismissLoadingDialog();
                mAirControllerUIItem = (AirControllerUIItem) ev.getUpdateStatusData();
                LogUtil.e("alinmi22", "CubeDeviceEventType.UPDATE_DEVICE_STATE mAirControllerUIItem = " + mAirControllerUIItem);

                updatePower(mAirControllerUIItem.powerIconUIItem);

                mTemperature.setProgress(mAirControllerUIItem.setTemp);

                mCurrentTemperature.setText(mAirControllerUIItem.curTemp <= 0 ? "_ _" : (mAirControllerUIItem.curTemp + ""));

                mModeAdapter.setDataList(mAirControllerUIItem.airModeIconItemList);
                mModeAdapter.notifyDataSetChanged();

                mSpeedAdapter.setDataList(mAirControllerUIItem.fanspeedIconItemList);
                mSpeedAdapter.notifyDataSetChanged();

            }

        } else if (event instanceof CubeBasicEvent) {
            dismissLoadingDialog();
            CubeBasicEvent ev = (CubeBasicEvent) event;
            if (ev.getType() == CubeEvents.CubeBasicEventType.PROGRESS_STATUS) {
                dismissLoadingDialog();
                final boolean success = ev.getIsSuccessed();
                switch (mOperationType) {
                    case TYPE_POWER:

                        final boolean select = mAirControllerUIItem.powerIconUIItem.IR_icon_select;
                        mAirControllerUIItem.powerIconUIItem.IR_icon_select = success ? select : (!select);
                        if (success) {
                            updatePower(mAirControllerUIItem.powerIconUIItem);
                        }
                        break;
                    case TYPE_SPEED:
                        LogUtil.e("alinmi22", "TYPE_SPEED success = " + success);
                        if (success) {
                            mSpeedAdapter.updateResult();
                        }
                        break;
                    case TYPE_MODE:
                        LogUtil.e("alinmi22", "TYPE_MODE success = " + success);
                        if (success) {
                            mModeAdapter.updateResult();
                        }
                        break;
                    case TYPE_TEMPERATURE:
                        if (!success) {
                            mTemperature.setProgress(mLastTemperature);
                        }
                        break;
                }
                if (!success) {
                    showToastShort(ev.getMessage());
                }

            }
        }
    }

    private void updatePower(MenuDeviceIRIconItem powerItem) {
        mPower.setImageResource(powerItem.IR_icon_select ? powerItem.IR_icon_imageSelectId : powerItem.IR_icon_imageId);
    }

    private boolean isSetStatus() {
        return mType == Constants.AC_TYPE_SET_STATUS;
    }
}
