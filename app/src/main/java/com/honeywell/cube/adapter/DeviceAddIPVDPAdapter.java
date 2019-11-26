
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.cube.widgets.TimeSelectorItem;

public class DeviceAddIPVDPAdapter extends DeviceAddDetailBaseAdapter {
    boolean mIsIPVDP;

    public DeviceAddIPVDPAdapter(Context context, MenuDeviceUIItem item, Dialog dialog, boolean isIPVDP) {
        super(context, item, dialog);
        mIsIPVDP = isIPVDP;
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_device_add_header;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_loop_ipvdp;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                holder.mHeader.setName(mIsIPVDP ? R.string.module_ipvdp : R.string.module_wired_zone);
                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mName.setTextName(R.string.name);
                holder.mRoom.setName(R.string.room);
                holder.mUsing.setTextName(R.string.using);
                holder.mAlarm.setName(R.string.alarm_type);
                holder.mZone.setName(R.string.zone);
                holder.mDelay.setName(R.string.delay_time);
                break;
        }
        return holder;
    }

    @Override
    public void initView(DeviceAddDetailBaseAdapter.ItemHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        final ItemBean itemBean = mDataList.get(position);
        final int itemType = itemBean.mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                initHeader(itemHolder.mHeader, itemBean, ModelEnum.MODULE_TYPE_IPVDP);
                break;
            case TYPE_SECTION:
                itemHolder.mSection.setText(itemBean.mSection);
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initName(itemHolder.mName, itemBean);
                initUsing(itemHolder.mUsing, itemBean);
                initZoneGroup(itemHolder.mAlarm, itemHolder.mZone, itemHolder.mDelay, itemHolder.mDivider, itemBean);
                break;
        }
    }

    protected void initZoneGroup(final SelectItem alarmItem, final SelectItem zoneItem, final TimeSelectorItem delayItem, final View divider, final ItemBean itemBean) {
        alarmItem.setDataList(DeviceHelper.getAlarmTypeList(mContext));
        alarmItem.setContent(itemBean.mMenuDeviceLoopObject.alarmType);
        alarmItem.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                alarmItem.setContent(position);
                if (position == 3) {
                    zoneItem.setContent(2);
                    zoneItem.setSelectable(true);
                    itemBean.mMenuDeviceLoopObject.zoneType = zoneItem.getContent().getText().toString();

                } else {
                    zoneItem.setContent(2);
                    zoneItem.setSelectable(false);

                    itemBean.mMenuDeviceLoopObject.zoneType = zoneItem.getContent().getText().toString();
                    hideDelayItem(delayItem, divider);
                }
                itemBean.mMenuDeviceLoopObject.alarmType = alarmItem.getDataList().get(position).mText;
            }
        });

        zoneItem.setDataList(DeviceHelper.getZoneTypeList(mContext));
        zoneItem.setContent(itemBean.mMenuDeviceLoopObject.zoneType);
        zoneItem.setSelectable(alarmItem.getSelectedPosition() == 3);
        if (zoneItem.getSelectedPosition() == 1) {
            showDelayItem(delayItem, divider);
        } else {
            hideDelayItem(delayItem, divider);
        }
        zoneItem.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                zoneItem.setContent(position);
                itemBean.mMenuDeviceLoopObject.zoneType = zoneItem.getDataList().get(position).mText;
                if (position == 1) {
                    showDelayItem(delayItem, divider);
                } else {
                    hideDelayItem(delayItem, divider);
                }
                itemBean.mMenuDeviceLoopObject.alarmType = alarmItem.getDataList().get(position).mText;
            }
        });
        delayItem.setTime(itemBean.mMenuDeviceLoopObject.delaytime);
        delayItem.setOnTimeSelectListener(new TimeSelectorItem.OnTimeSelectListener() {
            @Override
            public void timeSelect(int seconds) {
                itemBean.mMenuDeviceLoopObject.delaytime = seconds;
            }

            @Override
            public void timeSelect(String time) {
            }
        });
    }

    private void showDelayItem(final TimeSelectorItem delayItem, final View divider) {
        delayItem.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
    }

    private void hideDelayItem(final TimeSelectorItem delayItem, final View divider) {
        delayItem.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
    }

    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public SelectItem mAlarm;
        public SelectItem mZone;
        public TimeSelectorItem mDelay;
        public View mDivider;

        public ItemHolder(View view) {
            super(view);
            mAlarm = (SelectItem) view.findViewById(R.id.si_alarm_type);
            mZone = (SelectItem) view.findViewById(R.id.si_zone_type);
            mDelay = (TimeSelectorItem) view.findViewById(R.id.tsi_time_selector);
            mDivider = view.findViewById(R.id.divider);
        }
    }


}
