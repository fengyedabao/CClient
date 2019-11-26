package com.honeywell.cube.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.IRIconStudyAdapter;
import com.honeywell.cube.controllers.DeviceControllers.IRLoopController;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.widgets.IRControlPanel;
import com.honeywell.cube.widgets.IRControlTouchPanel;
import com.honeywell.cube.widgets.IRNumberPanel;

public class DeviceWICommonActivity extends DeviceWIBaseActivity {
    GridView mGridTop;
    GridView mGridBottom;
    IRIconStudyAdapter mAdapterTop;
    IRIconStudyAdapter mAdapterBottom;
    IRNumberPanel mIRNumberPanel;
    IRControlPanel mIRControlPanel;
    IRControlTouchPanel mIRControlTouchPanel;


    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_common;
    }

    @Override
    protected void initView() {
        super.initView();
        mGridTop = (GridView) findViewById(R.id.gv_top);
        mGridTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((ModelEnum.MAIN_IR_TELEVISION.equalsIgnoreCase(mDeviceType) || ModelEnum.LOOP_IR_TV.equalsIgnoreCase(mDeviceType)) && position == 1) {
                    final MenuDeviceIRIconItem item = (MenuDeviceIRIconItem) mAdapterTop.getItem(position);
                    item.IR_icon_select = !item.IR_icon_select;
                    mIRControlTouchPanel.setVisibility(item.IR_icon_select ? View.GONE : View.VISIBLE);
                    mIRNumberPanel.setVisibility(item.IR_icon_select ? View.VISIBLE : View.GONE);
                    mAdapterTop.notifyDataSetChanged();
                } else {
                    sendIRMessage(mAdapterTop.getDataList().get(position));
                }
            }
        });

        mGridBottom = (GridView) findViewById(R.id.gv_bottom);
        mGridBottom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendIRMessage(mAdapterBottom.getDataList().get(position));
            }
        });
        mIRControlPanel = (IRControlPanel) findViewById(R.id.ir_panel);
        mIRControlPanel.setVisibility(View.GONE);


        mIRControlTouchPanel = (IRControlTouchPanel) findViewById(R.id.ir_touch_panel);
        mIRControlTouchPanel.setVisibility(View.VISIBLE);

        mIRControlTouchPanel.setOnTouchListener(new IRControlTouchPanel.OnTouchListener() {
            @Override
            public void onLeftTouched() {
                sendIRMessage(mIRControlTouchPanel.getDataList().get(1));
            }

            @Override
            public void onRightTouched() {
                sendIRMessage(mIRControlTouchPanel.getDataList().get(3));
            }

            @Override
            public void onUpTouched() {
                sendIRMessage(mIRControlTouchPanel.getDataList().get(0));
            }

            @Override
            public void onDownTouched() {
                sendIRMessage(mIRControlTouchPanel.getDataList().get(4));
            }

            @Override
            public void onClicked() {
                sendIRMessage(mIRControlTouchPanel.getDataList().get(2));
            }
        });
        mIRNumberPanel = (IRNumberPanel) findViewById(R.id.ir_number_panel);
        mIRNumberPanel.setOnItemClickListener(new IRNumberPanel.OnItemClickListener() {
            @Override
            public void click(View v, int position) {
                sendIRMessage(mIRNumberPanel.getDataList().get(position));
            }
        });
        onTypeChanged();
    }

    @Override
    protected void onTypeChanged() {
        mAdapterTop = new IRIconStudyAdapter(IRLoopController.getIRViewUpIconItems(this, mDeviceType, mCurrentIrLoop));
        mAdapterBottom = new IRIconStudyAdapter(IRLoopController.getIRViewBottomIconItems(this, mDeviceType, mCurrentIrLoop));
        mGridTop.setAdapter(mAdapterTop);
        mGridBottom.setAdapter(mAdapterBottom);
        mIRNumberPanel.setDataList(IRLoopController.getIR_TV_keyboard(this, mCurrentIrLoop));
        mIRControlTouchPanel.setDataList(IRLoopController.getIRViewTVMiddleKeyboard(this, mCurrentIrLoop));

    }

}
