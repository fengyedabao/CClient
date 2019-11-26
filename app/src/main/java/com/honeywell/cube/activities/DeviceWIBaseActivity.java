package com.honeywell.cube.activities;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.OneLineTextListAdapter;
import com.honeywell.cube.controllers.DeviceControllers.IRLoopController;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ScreenUtil;
import com.honeywell.lib.widgets.MaxHeightListView;

import java.util.ArrayList;

public class DeviceWIBaseActivity extends CubeTitleBarActivity {
    static final String TAG = DeviceWIBaseActivity.class.getSimpleName();
    protected PopupWindow mPopupWindow;

    protected String mDeviceType;
    protected ArrayList<IrLoop> mIrLoopList = null;
    protected IrLoop mCurrentIrLoop = null;

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
        if (mCurrentIrLoop == null) {
            right.setImageResource(R.mipmap.nav_type);
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTypePopupWindow(v);
                }
            });
        }
    }

    private void showTypePopupWindow(View view) {
        if (mPopupWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.window_wi_type, null);
            final MaxHeightListView listview = (MaxHeightListView) contentView.findViewById(R.id.lv_list);

            listview.setListViewHeight(ScreenUtil.getScreenHeight(this) * 2 / 3);
            listview.setAdapter(new OneLineTextListAdapter(mIrLoopList));
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCurrentIrLoop = mIrLoopList.get(position);
                    mTextViewTitle.setText(mCurrentIrLoop.mLoopName);
                    onTypeChanged();
                    mPopupWindow.dismiss();
                }
            });
            mPopupWindow = new PopupWindow(contentView, View.MeasureSpec.makeMeasureSpec(ScreenUtil.getScreenWidth(this) / 2, View.MeasureSpec.AT_MOST), ViewGroup.LayoutParams.WRAP_CONTENT, true);

            mPopupWindow.setOutsideTouchable(true);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            mPopupWindow.showAsDropDown(view, 0, 0);
        }
    }

    protected void onTypeChanged() {

    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mDeviceType = getIntent().getStringExtra(Constants.DEVICE_TYPE);
        mCurrentIrLoop = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected void initView() {
        super.initView();
        if (mCurrentIrLoop == null) {
            mIrLoopList = IRLoopController.getIrLoopWithLoopType(this, mDeviceType);
            if (mIrLoopList == null || mIrLoopList.size() == 0) {
                LogUtil.e(TAG, "mIrLoopList is null or size == 0", true);
                finish();
                return;
            }
            mCurrentIrLoop = mIrLoopList.get(mIrLoopList.size() - 1);
        }
        mTextViewTitle.setText(mCurrentIrLoop.mLoopName);
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {
            dismissLoadingDialog();
            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            if (ev.getType() == CubeEvents.CubeDeviceEventType.DEVICE_IR_SEND) {
                if (ev.getSuccess()) {
                    showToastShort(R.string.operation_success_tip);
                } else {
                    showToastShort(ev.getMessage());
                }

            }
        }
    }

    public void sendIRMessage(final MenuDeviceIRIconItem item) {
        if (!item.IR_icon_enable) {
            showToastShort(R.string.no_study);
            return;
        }
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                IRLoopController.sendIRMessage(DeviceWIBaseActivity.this, mCurrentIrLoop, item);
            }
        });

    }
}
