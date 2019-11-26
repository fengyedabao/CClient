
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.ModuleListActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceLoopObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.SelectItem;

public class DeviceAddBackaudioAdapter extends DeviceAddDetailBaseAdapter {


    public DeviceAddBackaudioAdapter(Context context, MenuDeviceUIItem item, Dialog dialog) {
        super(context, item, dialog);
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_device_add_header;
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
                holder.mHeader.setName(R.string.module_backaudio);
                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mName.setTextName(R.string.name);
                holder.mRoom.setName(R.string.room);
                holder.mUsing.setTextName(R.string.using);
                break;
        }
        return holder;
    }

    @Override
    public void initView(DeviceAddDetailBaseAdapter.ItemHolder holder, int position) {
        final ItemHolder itemHolder = holder;
        final DeviceAddDetailBaseAdapter.ItemBean itemBean = mDataList.get(position);
        final int itemType = itemBean.mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                initHeader(itemHolder.mHeader, itemBean, ModelEnum.MODULE_TYPE_BACKAUDIO);
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
        final ItemBean myItemBean = (ItemBean) itemBean;
        header.setDataList(DeviceHelper.getBackaudioModuleList(mContext, type));
        if (myItemBean.mBackaudioDevice != null) {
            header.setContent(myItemBean.mBackaudioDevice.mName);
        } else {
            header.setContent("");
        }
        header.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(mContext, ModuleListActivity.class);
                    mContext.startActivity(intent);
                } else {
                    header.setContent(position);
                    myItemBean.mBackaudioDevice = (BackaudioDevice) header.getDataList().get(position).mData;
                }
            }
        });
    }

    @Override
    protected void getHeaderData() {
        super.getHeaderData();
        mMenuDeviceUIItem.backaudioDevice = ((ItemBean) mDataList.get(0)).mBackaudioDevice;
    }

    @Override
    protected void initHeaderData() {
        mDataList.add(new ItemBean(TYPE_HEADER, "", null, mMenuDeviceUIItem.backaudioDevice));
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        BackaudioDevice mBackaudioDevice;

        public ItemBean(int itemType, String section, MenuDeviceLoopObject menuDeviceLoopObject, BackaudioDevice device) {
            super(itemType, section, menuDeviceLoopObject, null);
            mBackaudioDevice = device;
        }
    }
}
