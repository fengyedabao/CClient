package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.controllers.menus.MenuModuleController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.ViewUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;

public class ModuleEditActivity extends CubeTitleBarActivity {

    //    Object mObject;
    EditTextItem mEditName;
    EditTextItem mEditIpAddr;
    EditTextItem mEditSubIpId;

    SelectItem mSelectBacentType;
    EditTextItem mEditBacnetId;
    EditTextItem mEditBacnetDeviceId;

    EditTextItem mEditHNSIp;
    private MenuModuleUIItem mMenuModuleUIItem;

    @Override
    protected void initView() {
        super.initView();
        switch (mType) {
            case Constants.MODULE_EDIT_TYPE_SPARK_LIGHTING:
                mMenuModuleUIItem = MenuModuleController.getDefaultSparkLightingModule(this);
                mEditName = (EditTextItem) findViewById(R.id.ei_name);
                mEditName.setEditName(mMenuModuleUIItem.title);

                mEditIpAddr = (EditTextItem) findViewById(R.id.ei_ip_addr);
                mEditIpAddr.setTextName(R.string.ip_addr);
                ViewUtil.initIPAddrEditItem(mEditIpAddr);
                mEditIpAddr.setEditName(mMenuModuleUIItem.ipAddr);

                mEditSubIpId = (EditTextItem) findViewById(R.id.ei_sub_ip_id);
                mEditSubIpId.setTextName(R.string.sub_ip_id);
                ViewUtil.initIntegetEditItem(mEditSubIpId);
                mEditSubIpId.setEditName("" + mMenuModuleUIItem.sub_gateway_id);
                break;
            case Constants.MODULE_EDIT_TYPE_BACNET:
                mMenuModuleUIItem = MenuModuleController.getDefaultBacnetModule();
                mEditName = (EditTextItem) findViewById(R.id.ei_name);
                mEditName.setEditName(mMenuModuleUIItem.title);

                mSelectBacentType = (SelectItem) findViewById(R.id.si_bacnet_type);
                mSelectBacentType.setDataList(DeviceHelper.getBacnetTypeList(this));
                mSelectBacentType.setName(R.string.bacnet_type);
                mSelectBacentType.setContent(mMenuModuleUIItem.bacnet_type);

                mEditBacnetId = (EditTextItem) findViewById(R.id.ei_bacnet_id);
                mEditBacnetId.setTextName(R.string.cube_bacnet_id);
                ViewUtil.initIntegetEditItem(mEditBacnetId);
                mEditBacnetId.setEditName("" + mMenuModuleUIItem.cube_bacnet_id);

                mEditBacnetDeviceId = (EditTextItem) findViewById(R.id.ei_device_id);
                mEditBacnetDeviceId.setTextName(R.string.bacnet_device_id);
                ViewUtil.initIntegetEditItem(mEditBacnetDeviceId);
                mEditBacnetDeviceId.setEditName("" + mMenuModuleUIItem.bacnet_device_id);
                break;

            case Constants.MODULE_EDIT_TYPE_IPVDP:
                mMenuModuleUIItem = MenuModuleController.getDefaultIPVDP(this);
                mEditName = (EditTextItem) findViewById(R.id.ei_name);
                mEditName.setEditName(mMenuModuleUIItem.title);

                mEditIpAddr = (EditTextItem) findViewById(R.id.ei_ip_addr);
                mEditIpAddr.setTextName(R.string.ip_addr);
                ViewUtil.initIPAddrEditItem(mEditIpAddr);
                mEditIpAddr.setEditName(mMenuModuleUIItem.ipAddr);

                mEditHNSIp = (EditTextItem) findViewById(R.id.ei_hns_id);
                mEditHNSIp.setTextName(R.string.hns_service_ip);
                ViewUtil.initIPAddrEditItem(mEditHNSIp);
                mEditHNSIp.setEditName(mMenuModuleUIItem.hns_ip);
                break;
            case Constants.MODULE_EDIT_TYPE_COMMON:

            default:
                mMenuModuleUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
                mEditName = (EditTextItem) findViewById(R.id.item_edit_name);
                mEditName.setEditName(mMenuModuleUIItem.title);
                //TODO
                break;
        }
    }


    @Override
    protected void getData() {
        super.getData();
    }

    @Override
    protected int getContent() {
        switch (mType) {
            case Constants.MODULE_EDIT_TYPE_SPARK_LIGHTING:
                return R.layout.activity_module_edit_sparklighting;
            case Constants.MODULE_EDIT_TYPE_BACNET:
                return R.layout.activity_module_edit_bacnet;
//            case Constants.MODULE_EDIT_TYPE_IPVDP:
//                return R.layout.activity_module_edit_ipvdp;
            case Constants.MODULE_EDIT_TYPE_COMMON:
            default:
                return R.layout.activity_module_edit_common;
        }
    }


    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mType) {
                    case Constants.MODULE_EDIT_TYPE_SPARK_LIGHTING:
                        mMenuModuleUIItem.title = mEditName.getContent();
                        mMenuModuleUIItem.ipAddr = mEditIpAddr.getContent();
                        mMenuModuleUIItem.sub_gateway_id = mEditSubIpId.getIntegerContent();
                        if (TextUtils.isEmpty(mMenuModuleUIItem.title)) {
                            showToastShort(R.string.name_not_null);
                            return;
                        }
                        if (TextUtils.isEmpty(mMenuModuleUIItem.ipAddr)) {
                            showToastShort(R.string.ip_not_null);
                            return;
                        }
                        if (mMenuModuleUIItem.sub_gateway_id < 1) {
                            showToastShort(R.string.sub_id_illegal);
                            return;
                        }
                        showLoadingDialog();
                        MenuModuleController.addModuleSparkLighting(ModuleEditActivity.this, mMenuModuleUIItem);
                        break;
                    case Constants.MODULE_EDIT_TYPE_BACNET:
                        mMenuModuleUIItem.title = mEditName.getContent();
                        mMenuModuleUIItem.bacnet_type = mSelectBacentType.getContentText();
                        mMenuModuleUIItem.cube_bacnet_id = mEditBacnetId.getIntegerContent();
                        mMenuModuleUIItem.bacnet_device_id = mEditBacnetDeviceId.getIntegerContent();

                        if (TextUtils.isEmpty(mMenuModuleUIItem.title)) {
                            showToastShort(R.string.name_not_null);
                            return;
                        }
                        if (TextUtils.isEmpty(mMenuModuleUIItem.bacnet_type)) {
                            showToastShort(R.string.bacnet_type_not_null);
                            return;
                        }
                        if (mMenuModuleUIItem.cube_bacnet_id < 1) {
                            showToastShort(R.string.bacnet_id_illegal);
                            return;
                        }
                        if (mMenuModuleUIItem.bacnet_device_id < 1) {
                            showToastShort(R.string.bacnet_device_id_illegal);
                            return;
                        }
                        showLoadingDialog();
                        MenuModuleController.addModuleBacnetAC(ModuleEditActivity.this, mMenuModuleUIItem);
                        break;
                    case Constants.MODULE_EDIT_TYPE_IPVDP:
                        mMenuModuleUIItem.title = mEditName.getContent();
                        mMenuModuleUIItem.ipAddr = mEditIpAddr.getContent();
                        mMenuModuleUIItem.hns_ip = mEditHNSIp.getContent();

                        if (TextUtils.isEmpty(mMenuModuleUIItem.title)) {
                            showToastShort(R.string.name_not_null);
                            return;
                        }
                        if (TextUtils.isEmpty(mMenuModuleUIItem.ipAddr)) {
                            showToastShort(R.string.ip_not_null);
                            return;
                        }
                        if (TextUtils.isEmpty(mMenuModuleUIItem.hns_ip)) {
                            showToastShort(R.string.hns_ip_not_null);
                            return;
                        }
                        showLoadingDialog();
                        MenuModuleController.addModuleIPVDP(ModuleEditActivity.this, mMenuModuleUIItem);
                        break;
                    case Constants.MODULE_EDIT_TYPE_COMMON:
                    default:
                        mMenuModuleUIItem.title = mEditName.getContent();
                        if (TextUtils.isEmpty(mMenuModuleUIItem.title)) {
                            showToastShort(R.string.name_not_null);
                            return;
                        }
                        showLoadingDialog();
                        MenuModuleController.modifyModule(ModuleEditActivity.this, mMenuModuleUIItem.moduleObject, mMenuModuleUIItem.title);
                        break;
                }
//                MenuDeviceController.modifyDevice(ModuleEditActivity.this, mObject, mEditItem.getEditName().getText().toString(), mSelectItem.getContent().getText().toString());
//                finish();
            }
        });
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeModuleEvent) {
            CubeModuleEvent ev = (CubeModuleEvent) event;
            if (ev.type == CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                    ModuleEditActivity.this.setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToastShort(ev.object.toString());
                }
            }
        }
    }
}
