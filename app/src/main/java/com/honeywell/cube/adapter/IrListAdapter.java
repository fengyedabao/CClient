
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class IrListAdapter extends DeviceListBaseAdapter {

    public IrListAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, final int position) {
        return new ItemHolder(slideView);
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        final ItemBean item = getDataList().get(position);
        itemHolder.mIcon.setImageResource(item.mIconId);
        itemHolder.mText.setText(item.mText);
    }

    public int getItemLayout(int position) {
        return R.layout.list_ir;
    }


    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView mIcon;
        public TextView mText;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            mIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            mText = (TextView) convertView.findViewById(R.id.tv_text);
        }
    }

    @Override
    protected void delete(int position) {
        super.delete(position);
    }

    @Override
    protected void edit(int position) {
        super.edit(position);
    }


}
