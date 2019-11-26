
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class ZoneListAdapter extends DeviceListBaseAdapter {
    public ZoneListAdapter(Context context, List<? extends ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public int getItemLayout(int position) {
        return R.layout.list_zone;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, int position) {
        ItemHolder holder = new ItemHolder(slideView);
        slideView.setOnSlideListener(null);
        return holder;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.mText.setText(mDataList.get(position).mText);
        itemHolder.mZoneIcon.setVisibility(DeviceController.checkZoneTypeIf24Hour(mDataList.get(position).mLoop) ? View.VISIBLE : View.GONE);

    }

    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public TextView mText;
        public ImageView mZoneIcon;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            mText = (TextView) convertView.findViewById(R.id.tv_text);
            mZoneIcon = (ImageView) convertView.findViewById(R.id.iv_zone_icon);
        }
    }


}
