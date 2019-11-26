
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.widgets.DelayItem;
import com.honeywell.cube.widgets.TimeSelectorItem;

public class DeviceAddWifiRelayAdapter extends DeviceAddDetailBaseAdapter {


    public DeviceAddWifiRelayAdapter(Context context, MenuDeviceUIItem item, Dialog dialog) {
        super(context, item, dialog);
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_device_add_header;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_loop_wifi_relay;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                holder.mHeader.setName(R.string.module_wifi_relay);
                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mName.setTextName(R.string.name);
                holder.mRoom.setName(R.string.room);
                holder.mUsing.setTextName(R.string.using);
                holder.mDelay.setTextName(R.string.delay);
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
                initHeader(itemHolder.mHeader, itemBean, ModelEnum.MODULE_TYPE_WIFIRELAY);
                break;
            case TYPE_SECTION:
                itemHolder.mSection.setText(itemBean.mSection);
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initName(itemHolder.mName, itemBean);
                initUsing(itemHolder.mUsing, itemBean);
                initDelay(itemHolder.mDelay, itemBean);
                break;
        }
    }

    protected void initDelay(final DelayItem delayItem, final DeviceAddDetailBaseAdapter.ItemBean itemBean) {
        delayItem.setChecked(itemBean.mMenuDeviceLoopObject.isDelay);
        delayItem.setCheckBoxClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemBean.mMenuDeviceLoopObject.isDelay = ((CheckBox) v).isChecked();
            }
        });
        delayItem.getTimeSelectorItem().setTime(itemBean.mMenuDeviceLoopObject.delaytime);
        delayItem.getTimeSelectorItem().setOnTimeSelectListener(new TimeSelectorItem.OnTimeSelectListener() {
            @Override
            public void timeSelect(int seconds) {
                itemBean.mMenuDeviceLoopObject.delaytime = seconds;
            }

            @Override
            public void timeSelect(String time) {
            }
        });
    }


    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public DelayItem mDelay;

        public ItemHolder(View view) {
            super(view);
            mDelay = (DelayItem) view.findViewById(R.id.di_delay);
        }
    }

}
