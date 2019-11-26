package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.IRIconStudyAdapter;
import com.honeywell.cube.controllers.DeviceControllers.IRLoopController;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuIRCode;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.widgets.IRControlPanel;
import com.honeywell.cube.widgets.IRNumberPanel;

public class DeviceAddWICommonActivity extends CubeTitleBarActivity {
    GridView mGridTop;
    GridView mGridBottom;
    MenuDeviceIRUIItem mMenuDeviceIRUIItem;
    IRIconStudyAdapter mAdapterTop;
    IRIconStudyAdapter mAdapterBottom;
    IRNumberPanel mIRNumberPanel;
    IRControlPanel mIRControlPanel;

    final static int TYPE_TOP = 0;
    final static int TYPE_BOTTOM = 1;
    final static int TYPE_PANEL_NUMBER = 2;
    final static int TYPE_PANEL_CONTROL = 3;

    int mTouchType;
    int mTouchPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_common;
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
                        MenuDeviceController.addIRWithInfo(DeviceAddWICommonActivity.this, mMenuDeviceIRUIItem);
                    }
                });
            }
        });
    }


    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuDeviceIRUIItem = getIntent().getParcelableExtra(Constants.CONTENT);

    }

    @Override
    protected void initView() {
        super.initView();
        mGridTop = (GridView) findViewById(R.id.gv_top);
        mGridBottom = (GridView) findViewById(R.id.gv_bottom);
        mAdapterTop = new IRIconStudyAdapter(IRLoopController.getIRViewUpIconItems(this, mMenuDeviceIRUIItem.IR_type, mMenuDeviceIRUIItem.IR_loop));
        mAdapterBottom = new IRIconStudyAdapter(IRLoopController.getIRViewBottomIconItems(this, mMenuDeviceIRUIItem.IR_type, mMenuDeviceIRUIItem.IR_loop));

        mGridTop.setAdapter(mAdapterTop);
        mGridBottom.setAdapter(mAdapterBottom);
        mGridTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ModelEnum.IR_TYPE_TV_S.equalsIgnoreCase(mMenuDeviceIRUIItem.IR_type) && position == 1) {
                    final MenuDeviceIRIconItem item = (MenuDeviceIRIconItem) mAdapterTop.getItem(position);
                    item.IR_icon_enable = !item.IR_icon_enable;
                    mIRControlPanel.setVisibility(item.IR_icon_enable ? View.GONE : View.VISIBLE);
                    mIRNumberPanel.setVisibility(item.IR_icon_enable ? View.VISIBLE : View.GONE);
                    mAdapterTop.notifyDataSetChanged();
                } else {
                    studyIrCode(TYPE_TOP, position, mAdapterTop.getDataList().get(position));

                }
            }
        });
        mGridBottom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                studyIrCode(TYPE_BOTTOM, position, mAdapterBottom.getDataList().get(position));
            }
        });

        mIRControlPanel = (IRControlPanel) findViewById(R.id.ir_panel);
        mIRControlPanel.setDataList(IRLoopController.getIRViewTVMiddleKeyboard(this, null));
        mIRControlPanel.setOnItemClickListener(new IRControlPanel.OnItemClickListener() {
            @Override
            public void click(View v, int position) {
                studyIrCode(TYPE_PANEL_CONTROL, position, mIRControlPanel.getDataList().get(position));
            }
        });
        mIRNumberPanel = (IRNumberPanel) findViewById(R.id.ir_number_panel);
        mIRNumberPanel.setDataList(IRLoopController.getIR_TV_keyboard(this, mMenuDeviceIRUIItem.IR_loop));
        mIRNumberPanel.setOnItemClickListener(new IRNumberPanel.OnItemClickListener() {
            @Override
            public void click(View v, int position) {
                studyIrCode(TYPE_PANEL_NUMBER, position, mIRNumberPanel.getDataList().get(position));
            }
        });
    }

    public void studyIrCode(int type, int position, final MenuDeviceIRIconItem item) {
        recordPostion(type, position);
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                MenuDeviceController.studyIrCodeWithInfo(DeviceAddWICommonActivity.this, mMenuDeviceIRUIItem, item);
            }
        });
    }

    private void recordPostion(int type, int position) {
        mTouchType = type;
        mTouchPosition = position;

    }

    private void updateUI() {
        switch (mTouchType) {
            case TYPE_TOP:
                mAdapterTop.updateView(mTouchPosition, true);
                break;
            case TYPE_BOTTOM:
                mAdapterBottom.updateView(mTouchPosition, true);
                break;
            case TYPE_PANEL_CONTROL:
                mIRControlPanel.updateView(mTouchPosition, true);
                break;
            case TYPE_PANEL_NUMBER:
                mIRNumberPanel.updateView(mTouchPosition, true);
                break;
            default:
                break;
        }


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
                    updateUI();
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
