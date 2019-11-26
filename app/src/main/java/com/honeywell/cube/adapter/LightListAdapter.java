
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
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


public class LightListAdapter extends DeviceListBaseAdapter {
    public static final int TYPE_EXPANDABLE = 0;
    public static final int TYPE_SIMPLE = 1;

    public LightListAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    public int getItemLayout(int position) {
        return TYPE_EXPANDABLE == getItemViewType(position) ? R.layout.list_light_expandable : R.layout.list_light_simple;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, final int position) {
        slideView.setOnSlideListener(null);
        final int type = getItemViewType(position);
        switch (type) {
            case TYPE_SIMPLE:
                SimpleItemHolder simpleItemHolder = new SimpleItemHolder(slideView);
                return simpleItemHolder;
            case TYPE_EXPANDABLE:
                final ExpandableItemHolder expandableItemHolder = new ExpandableItemHolder(slideView);
                return expandableItemHolder;
            default:
                return null;
        }
    }

    @Override
    public void initView(ItemHolder holder, final int position) {
        super.initView(holder, position);
        final ItemBean data = (ItemBean) mDataList.get(position);
        final int type = getItemViewType(position);
        switch (type) {
            case TYPE_SIMPLE:
                final SimpleItemHolder simpleItemHolder = (SimpleItemHolder) holder;
                simpleItemHolder.ivIcon.setImageResource(data.mIconId);
                simpleItemHolder.cbSwitch.setChecked(data.bIsOpen);
                simpleItemHolder.tvName.setText(data.mText);
                simpleItemHolder.cbSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (DeviceRootFragment.DEBUG_NO_NET) {
                            return;
                        }
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.bIsOpen = ((CheckBox) v).isChecked();
                        showDialog();
                        if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(mDataList.get(position).mDeviceType)) {
                            showDialog();
                            Wireless315M433MController.sendWireless315M433MState(null, (Wireless315M433MLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mLight);
                        } else {
                            showDialog();
                            SparkLightingController.sendSparkLightingState(null, (SparkLightingLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mLight);
                        }
                    }
                });
                break;
            case TYPE_EXPANDABLE:
                final ExpandableItemHolder expandableItemHolder = (ExpandableItemHolder) holder;
                expandableItemHolder.ivIcon.setImageResource(data.mIconId);
                expandableItemHolder.cbSwitch.setChecked(data.bIsOpen);
                expandableItemHolder.tvName.setText(data.mText);
                expandableItemHolder.sbLight.setProgress(data.mLight);
                expandableItemHolder.cbSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (DeviceRootFragment.DEBUG_NO_NET) {
                            return;
                        }
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.bIsOpen = ((CheckBox) v).isChecked();
                        if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(mDataList.get(position).mDeviceType)) {
                            showDialog();
                            Wireless315M433MController.sendWireless315M433MState(null, (Wireless315M433MLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mLight);
                        } else {
                            showDialog();
                            SparkLightingController.sendSparkLightingState(null, (SparkLightingLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mLight);
                        }
                    }
                });
                expandableItemHolder.sbLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                            if (DeviceRootFragment.DEBUG_NO_NET) {
//                                return;
//                            }
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.mLight = seekBar.getProgress();
                        if (progress == 0) {
                            if (item.bIsOpen) {
                                expandableItemHolder.cbSwitch.setChecked(false);
                                item.bIsOpen = false;
                            }
                        } else {
                            if (!item.bIsOpen) {
                                expandableItemHolder.cbSwitch.setChecked(true);
                                item.bIsOpen = true;
                            }

                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (DeviceRootFragment.DEBUG_NO_NET) {
                            return;
                        }
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.mLight = seekBar.getProgress();
                        if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(mDataList.get(position).mDeviceType)) {
                            showDialog();
                            Wireless315M433MController.sendWireless315M433MState(null, (Wireless315M433MLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mLight);
                        } else {
                            showDialog();
                            SparkLightingController.sendSparkLightingState(null, (SparkLightingLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mLight);
                        }
                    }
                });
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ((ItemBean) mDataList.get(position)).mType;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    protected static class SimpleItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public CheckBox cbSwitch;

        public SimpleItemHolder(SlideView view) {
            super(view);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            tvName = (TextView) view.findViewById(R.id.tv_text);
            cbSwitch = (CheckBox) view.findViewById(R.id.cb_switch);
        }
    }

    protected static class ExpandableItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public ImageView ivExpandable;
        public SeekBar sbLight;
        public CheckBox cbSwitch;

        public ExpandableItemHolder(SlideView view) {
            super(view);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            tvName = (TextView) view.findViewById(R.id.tv_text);
            ivExpandable = (ImageView) view.findViewById(R.id.iv_expandable);
            sbLight = (SeekBar) view.findViewById(R.id.sb_light);
            cbSwitch = (CheckBox) view.findViewById(R.id.cb_switch);
        }
    }


    public static class ItemBean extends DeviceListBaseAdapter.ItemBean {
        public int mLight;
        public boolean bIsOpen;
        public int mType;

        public ItemBean(int type, int iconId, Drawable icon, String text, int light, boolean isOpen, Object loop, String deviceType) {
            super(iconId, icon, text, loop, deviceType);
            this.mType = type;
            this.mLight = light;
            this.bIsOpen = isOpen;
        }

    }

}
