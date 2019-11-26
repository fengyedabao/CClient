
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class DeviceSubListAdapter extends DeviceListBaseAdapter {
    public DeviceSubListAdapter(Context context, List<? extends ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public int getItemLayout(int position) {
        return R.layout.list_device_sub;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, int position) {
        ItemHolder holder = new ItemHolder(slideView);
//        slideView.setOnSlideListener(null);
        return holder;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.text.setText(mDataList.get(position).mText);

    }

    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public TextView text;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            text = (TextView) convertView.findViewById(R.id.tv_text);
        }
    }
}
