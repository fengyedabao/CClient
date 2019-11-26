package com.honeywell.cube.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.IconSelectAdapter;
import com.honeywell.cube.controllers.DeviceControllers.VentilationController;
import com.honeywell.cube.controllers.UIItem.VentilationUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;

public class DeviceVentilationActivity extends CubeTitleBarActivity {
    GridView mSpeed;
    RadioGroup mGroupLoop;
    RadioGroup mGroupLoopInside;
    ImageView mPower;
    IconSelectAdapter mSpeedAdapter;
    VentilationUIItem mVentilationUIItem;
    VentilationLoop mVentilationLoop;

    private int mOperationType;
    private final int TYPE_POWER = 0;
    private final int TYPE_SPEED = 1;
    private final int TYPE_CIRCLE_LOOP = 2;
    private final int TYPE_MODE = 3;

    @Override
    protected int getContent() {
        return R.layout.activity_ventilation;
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mVentilationLoop = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected void getData() {
        super.getData();
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                VentilationController.getVentilationUIItemsAndReadDeviceState(DeviceVentilationActivity.this, mVentilationLoop);
            }
        });

    }

    protected void initView() {
        mSpeed = (GridView) findViewById(R.id.gv_speed);
        mGroupLoop = (RadioGroup) findViewById(R.id.rg_loop);
        mGroupLoopInside = (RadioGroup) findViewById(R.id.rg_inside_loop);
        mGroupLoop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_inside_loop) {
                    mGroupLoopInside.setVisibility(View.VISIBLE);
                } else {
                    mGroupLoopInside.setVisibility(View.INVISIBLE);
                }
            }
        });

        RadioButton inside = (RadioButton) findViewById(R.id.rb_inside_loop);
        RadioButton outside = (RadioButton) findViewById(R.id.rb_outside_loop);
        inside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        mOperationType = TYPE_CIRCLE_LOOP;
                        mVentilationUIItem.cycleInner = true;
                        VentilationController.sendControlCycle(DeviceVentilationActivity.this, mVentilationUIItem);
                    }
                });
            }
        });
        outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        mOperationType = TYPE_CIRCLE_LOOP;
                        mVentilationUIItem.cycleInner = false;
                        VentilationController.sendControlCycle(DeviceVentilationActivity.this, mVentilationUIItem);
                    }
                });
            }
        });

        RadioButton humidity = (RadioButton) findViewById(R.id.rb_humidity);
        RadioButton dehumidity = (RadioButton) findViewById(R.id.rb_dehumidity);
        humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        mOperationType = TYPE_MODE;
                        mVentilationUIItem.modeItems.get(0).IR_icon_select = true;
                        mVentilationUIItem.modeItems.get(1).IR_icon_select = false;
                        VentilationController.sendControlCycle(DeviceVentilationActivity.this, mVentilationUIItem);
                    }
                });
            }
        });
        dehumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        mOperationType = TYPE_MODE;
                        mVentilationUIItem.modeItems.get(0).IR_icon_select = false;
                        mVentilationUIItem.modeItems.get(1).IR_icon_select = true;
                        VentilationController.sendControlCycle(DeviceVentilationActivity.this, mVentilationUIItem);
                    }
                });
            }
        });

        mSpeedAdapter = new IconSelectAdapter(null);
        mSpeed.setAdapter(mSpeedAdapter);
        mSpeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        mOperationType = TYPE_SPEED;
                        mSpeedAdapter.setSelectPosition(position);
                        VentilationController.sendControlIconItem(DeviceVentilationActivity.this, mVentilationLoop, mSpeedAdapter.getDataList().get(position));
                    }
                });
            }
        });
        mPower = (ImageView) findViewById(R.id.cb_power);
        mPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        mOperationType = TYPE_POWER;
                        final boolean isSelect = mVentilationUIItem.powerItem.IR_icon_select;
                        mVentilationUIItem.powerItem.IR_icon_select = !isSelect;
                        VentilationController.sendControlIconItem(DeviceVentilationActivity.this, mVentilationLoop, mVentilationUIItem.powerItem);
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
            if (ev.getType() == CubeEvents.CubeDeviceEventType.UPDATE_VENTILATION_STATUS) {
                dismissLoadingDialog();
                mVentilationUIItem = (VentilationUIItem) ev.getUpdateStatusData();
                updatePower(mVentilationUIItem.powerItem);
                mSpeedAdapter.setDataList(mVentilationUIItem.fanSpeedItems);
                mSpeedAdapter.notifyDataSetChanged();

                ((RadioButton) mGroupLoop.getChildAt(mVentilationUIItem.cycleInner ? 1 : 0)).setChecked(true);

                ArrayList<MenuDeviceIRIconItem> modeItems = mVentilationUIItem.modeItems;
                if (modeItems != null) {
                    final int size = modeItems.size();
                    for (int i = 0; i < size; i++) {
                        MenuDeviceIRIconItem item = modeItems.get(i);
                        if (item.IR_icon_select) {
                            ((RadioButton) mGroupLoopInside.getChildAt(i)).setChecked(true);
                        }
                    }
                }

            }

        } else if (event instanceof CubeBasicEvent) {
            dismissLoadingDialog();
            CubeBasicEvent ev = (CubeBasicEvent) event;
            if (ev.getType() == CubeEvents.CubeBasicEventType.PROGRESS_STATUS) {
                dismissLoadingDialog();
                final boolean success = ev.getIsSuccessed();
                switch (mOperationType) {
                    case TYPE_POWER:

                        final boolean select = mVentilationUIItem.powerItem.IR_icon_select;
                        mVentilationUIItem.powerItem.IR_icon_select = success ? select : (!select);
                        if (success) {
                            updatePower(mVentilationUIItem.powerItem);
                        }
                        break;
                    case TYPE_SPEED:
                        if (success) {
                            mSpeedAdapter.updateResult();
//                            mSpeedAdapter.notifyDataSetChanged();
                        }
                        break;
                    case TYPE_CIRCLE_LOOP:
                        break;
                    case TYPE_MODE:
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
}
