
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.DeviceControllers.RelayController;
import com.honeywell.cube.controllers.DeviceControllers.SparkLightingController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.fragments.DeviceRootFragment;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class RelayListAdapter extends DeviceListBaseAdapter {

    public RelayListAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, final int position) {
        ItemHolder holder = new ItemHolder(slideView);
        slideView.setOnSlideListener(null);

        //TODO
        return holder;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, final int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        final ItemBean item = (ItemBean) getDataList().get(position);
        itemHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceRootFragment.DEBUG_NO_NET) {
                    return;
                }
                final ItemBean item = (ItemBean) getDataList().get(position);
                item.bSwitch = ((CheckBox) v).isChecked();
                if (ModelEnum.LOOP_RELAY.equalsIgnoreCase(item.mDeviceType)) {
                    showDialog();
                    RelayController.sendRelayState(null, (RelayLoop) item.mLoop, item.bSwitch ? 1 : 0);
                } else {
                    showDialog();
                    SparkLightingController.sendSparkLightingState(null, (SparkLightingLoop) item.mLoop, item.bSwitch ? 1 : 0, 1);
                }
            }
        });
        itemHolder.icon.setImageResource(item.mIconId);
        itemHolder.text.setText(item.mText);
//        holder.slideSwitch.setState(mDataList.get(position).isSwitch());
        itemHolder.checkBox.setChecked(item.bSwitch);

    }

    public int getItemLayout(int position) {
        return R.layout.list_relay;
    }


    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView icon;
        public TextView text;
        public CheckBox checkBox;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            text = (TextView) convertView.findViewById(R.id.tv_text);
            checkBox = (CheckBox) convertView.findViewById(R.id.slide_switch);
        }
    }

    public static class ItemBean extends DeviceListBaseAdapter.ItemBean {
        boolean bSwitch;

        public ItemBean(int iconId, Drawable icon, String text, boolean slideSwitch, Object loop, String type) {
            super(iconId, icon, text, loop, type);
            bSwitch = slideSwitch;

        }


    }

}
