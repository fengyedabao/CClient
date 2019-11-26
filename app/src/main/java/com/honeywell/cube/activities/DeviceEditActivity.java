package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.widgets.DetailItem;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;

import java.util.Map;

public class DeviceEditActivity extends CubeTitleBarActivity {
    public static final String DEVICE_LOOP = "device_loop";
    Object mObject;
    EditTextItem mEditItem;
    SelectItem mSelectItem;

    @Override
    protected void initView() {
        super.initView();
        mObject = getIntent().getParcelableExtra(DEVICE_LOOP);
        Map<String, Object> map = MenuDeviceController.getDeviceRoomAndNameInfo(this, mObject);

        mEditItem = (EditTextItem) findViewById(R.id.item_edit_name);
        mEditItem.setTextName(R.string.name);
        mEditItem.setEditName((String) map.get(MenuDeviceController.CELL_NAME));
        mSelectItem = (SelectItem) findViewById(R.id.item_select_room);
        mSelectItem.setName(R.string.room);
        DeviceHelper.initRoom(mSelectItem);
        mSelectItem.setContent((String) map.get(MenuDeviceController.CELL_ROOM));

        DetailItem detailItem = (DetailItem) findViewById(R.id.item_detail);
        detailItem.setContent(R.string.more_info);
        detailItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceEditActivity.this, DeviceEditMoreDetailActivity.class);
                DeviceHelper.addObject2Intent(intent, DEVICE_LOOP, mObject);
                startActivity(intent);
            }
        });

    }

    @Override
    protected int getContent() {
        return R.layout.activity_device_edit;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                MenuDeviceController.modifyDevice(DeviceEditActivity.this, mObject, mEditItem.getEditName().getText().toString(), (int) mSelectItem.getContentObject());

            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.configure);
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent ev = (CubeDeviceEvent) event;

            if (ev.getType() == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE) {
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
