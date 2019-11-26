
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.ModuleEditActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceLoopObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;

public class DeviceAddSparkLightingAdapter extends DeviceAddDetailBaseAdapter {


    public DeviceAddSparkLightingAdapter(Context context, MenuDeviceUIItem item, Dialog dialog) {
        super(context, item, dialog);
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_device_add_header_spark_lighting;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_loop;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                holder.mDeviceId.setTextName(R.string.device_id);
                holder.mDeviceId.setHint(R.string.device_id_tip);
                holder.mHeader.setName(R.string.module_spark_lighting);
                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mName.setTextName(R.string.name);
                holder.mName.setHint(R.string.edit_scenario_name_hint);
                holder.mRoom.setName(R.string.room);
                holder.mUsing.setTextName(R.string.using);
                break;
        }
        return holder;
    }

    @Override
    public void initView(DeviceAddDetailBaseAdapter.ItemHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        final DeviceAddDetailBaseAdapter.ItemBean itemBean = mDataList.get(position);
        final int itemType = itemBean.mItemType;
        switch (itemType) {
            case TYPE_HEADER:

                itemHolder.mDeviceId.getEditName().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                itemHolder.mDeviceId.setEditName("" + ((ItemBean) itemBean).mSparkDeviceId);
                itemHolder.mDeviceId.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
                    @Override
                    public void afterTextChanged(String s) {
                        if (TextUtils.isEmpty(s)) {
                            ((ItemBean) itemBean).mSparkDeviceId = 0;
                        } else {
                            ((ItemBean) itemBean).mSparkDeviceId = Integer.parseInt(s);
                        }
                    }
                });
                initHeader(itemHolder.mHeader, itemBean, ModelEnum.MODULE_TYPE_SPARKLIGHTING);
                break;
            case TYPE_SECTION:
                itemHolder.mSection.setText(itemBean.mSection);
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initName(itemHolder.mName, itemBean);
                initUsing(itemHolder.mUsing, itemBean);
                break;
        }
    }

    @Override
    protected void initHeader(final SelectItem header, final DeviceAddDetailBaseAdapter.ItemBean itemBean, int type) {
        DeviceHelper.initModuleData(header, type);
        if (itemBean.mDevice != null) {
            header.setContent(itemBean.mDevice.mName);
        } else {
            header.setContent("");
        }
        header.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(mContext, ModuleEditActivity.class);
                    intent.putExtra(Constants.TITLE, mContext.getString(R.string.module_add_sparklighting));
                    intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_SPARK_LIGHTING);
                    mContext.startActivity(intent);
                } else {
                    header.setContent(position);
                    itemBean.mDevice = (PeripheralDevice) header.getDataList().get(position).mData;
                }
            }
        });
    }

    @Override
    protected void getHeaderData() {
        super.getHeaderData();
        mMenuDeviceUIItem.sparkDeviceId = ((ItemBean) mDataList.get(0)).mSparkDeviceId;
    }

    @Override
    protected void initHeaderData() {
        mDataList.add(new ItemBean(TYPE_HEADER, "", null, mMenuDeviceUIItem.peripheraDevice, mMenuDeviceUIItem.sparkDeviceId));
    }


    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public EditTextItem mDeviceId;

        public ItemHolder(View view) {
            super(view);
            mDeviceId = (EditTextItem) view.findViewById(R.id.ei_device_id);
        }
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        int mSparkDeviceId;

        public ItemBean(int itemType, String section, MenuDeviceLoopObject menuDeviceLoopObject, PeripheralDevice device, int sparkDeviceId) {
            super(itemType, section, menuDeviceLoopObject, device);
            mSparkDeviceId = sparkDeviceId;
        }
    }

}
