package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.utils.ToastUtil;

import java.util.List;

public class DeviceAddWirelessInfraredFirstActivity extends CubeTitleBarActivity {

    EditTextItem mName;
    SelectItem mRoom;
    SelectItem mWI;
    MenuDeviceIRUIItem mMenuDeviceIRUIItem;

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuDeviceIRUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_first;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_next);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mName.getContent())) {
                    ToastUtil.showShort(DeviceAddWirelessInfraredFirstActivity.this, R.string.input_name);
                    return;
                }
                mMenuDeviceIRUIItem.IR_name = mName.getContent();
                mMenuDeviceIRUIItem.IR_room_id = (int) mRoom.getSeletedData();
                mMenuDeviceIRUIItem.IR_room_name = mRoom.getContentText();
                mMenuDeviceIRUIItem.IR_module_device = (PeripheralDevice) mWI.getSeletedData();
                mMenuDeviceIRUIItem.IR_Module_name = mWI.getContentText();

                Intent intent = null;
                switch (mType) {
                    case ModelEnum.IR_TYPE_CUSTOMIZE:
                        intent = new Intent(DeviceAddWirelessInfraredFirstActivity.this, DeviceAddWICustomFirstActivity.class);

                        break;
                    case ModelEnum.IR_TYPE_AC:
                        intent = new Intent(DeviceAddWirelessInfraredFirstActivity.this, DeviceAddWIAirConditionerFirstActivity.class);

                        break;
                    case ModelEnum.IR_TYPE_DVD:
                        intent = new Intent(DeviceAddWirelessInfraredFirstActivity.this, DeviceAddWICommonActivity.class);
                        intent.putExtra(Constants.TITLE, mTitle);
                        intent.putExtra(Constants.TYPE, mType);
                        break;
                    case ModelEnum.IR_TYPE_TV:
                        intent = new Intent(DeviceAddWirelessInfraredFirstActivity.this, DeviceAddWICommonActivity.class);
                        intent.putExtra(Constants.TITLE, mTitle);
                        intent.putExtra(Constants.TYPE, mType);
                        break;
                    case ModelEnum.IR_TYPE_STB:
                        intent = new Intent(DeviceAddWirelessInfraredFirstActivity.this, DeviceAddWICommonActivity.class);
                        intent.putExtra(Constants.TITLE, mTitle);
                        intent.putExtra(Constants.TYPE, mType);
                        break;
                }
                if (intent != null) {
                    MenuDeviceController.updateMenuDeviceIRRoom(mMenuDeviceIRUIItem);
                    DeviceHelper.addObject2Intent(intent, Constants.CONTENT, mMenuDeviceIRUIItem);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        mName = (EditTextItem) findViewById(R.id.ei_name);
        mName.setTextName(R.string.name);
        mName.setEditName(mTitle);

        mRoom = (SelectItem) findViewById(R.id.si_room);
        DeviceHelper.initRoom(mRoom);

        mWI = (SelectItem) findViewById(R.id.si_wi);
        DeviceHelper.initModuleData(mWI, ModelEnum.MODULE_TYPE_WIFIIR);
        mWI.setName(R.string.wireless_infrared);
        mWI.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(DeviceAddWirelessInfraredFirstActivity.this, ModuleListActivity.class);
                    startActivity(intent);
                } else {
                    mWI.setContent(position);
                }
            }
        });
    }

    @Override
    protected void getData() {
        super.getData();
        MenuDeviceController.getMenuIrDefaultSecondItem(this, mMenuDeviceIRUIItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
//                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
