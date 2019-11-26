
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.DeviceControllers.SparkLightingController;
import com.honeywell.cube.controllers.DeviceControllers.Wireless315M433MController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.fragments.DeviceRootFragment;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class CurtainListAdapter extends DeviceListBaseAdapter {


    public CurtainListAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    public int getItemLayout(int position) {
        return R.layout.list_curtain;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, final int position) {
        ItemHolder holder = new ItemHolder(slideView);
        slideView.setOnSlideListener(null);

        return holder;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, final int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        final ItemBean item = getDataList().get(position);
        itemHolder.ivIcon.setImageResource(item.mIconId);
        itemHolder.tvName.setText(item.mText);
        itemHolder.iconSecondaryLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCurtainStatus(position, ModelEnum.CURTAIN_STATUS_OPENING);
            }
        });
        itemHolder.iconSecondaryMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCurtainStatus(position, ModelEnum.CURTAIN_STATUS_PAUSING);
            }
        });
        itemHolder.iconSecondaryRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCurtainStatus(position, ModelEnum.CURTAIN_STATUS_CLOSING);
            }
        });
    }


    private void sendCurtainStatus(int position, int status) {
        if (DeviceRootFragment.DEBUG_NO_NET) {
            return;
        }
        final ItemBean item = getDataList().get(position);
        if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(item.mDeviceType)) {
            showDialog();
            Wireless315M433MController.sendCurtainStatus(null, (Wireless315M433MLoop) item.mLoop, status);
        } else {
            showDialog();
            SparkLightingController.sendCurtainStatus(null, (SparkLightingLoop) item.mLoop, status);
        }
    }


    public static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public ImageView ivExpandable;
        public ImageView iconSecondaryLeft;
        public ImageView iconSecondaryMiddle;
        public ImageView iconSecondaryRight;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            tvName = (TextView) convertView.findViewById(R.id.tv_text);
            ivExpandable = (ImageView) convertView.findViewById(R.id.iv_expandable);
            iconSecondaryLeft = (ImageView) convertView.findViewById(R.id.iv_secondary_left);
            iconSecondaryMiddle = (ImageView) convertView.findViewById(R.id.iv_secondary_middle);
            iconSecondaryRight = (ImageView) convertView.findViewById(R.id.iv_secondary_right);
        }
    }
}
