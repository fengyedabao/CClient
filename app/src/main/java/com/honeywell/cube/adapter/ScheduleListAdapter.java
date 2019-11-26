
package com.honeywell.cube.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.ScheduleEditActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleUIItem;
import com.honeywell.cube.controllers.menus.MenuScheduleController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class ScheduleListAdapter extends DeviceListBaseAdapter {
    public ScheduleListAdapter(Context context, List<? extends ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public int getItemLayout(int position) {
        return R.layout.list_schedule;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, int position) {
        ItemHolder holder = new ItemHolder(slideView);
        return holder;
    }

    public int getSlideViewLayout() {
        return R.layout.layout_slide_view_edit_delete_schedule;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        final MenuScheduleUIItem item = (MenuScheduleUIItem) mDataList.get(position).mLoop;
        itemHolder.mTime.setText(item.start_time_str);

        itemHolder.mSwitch.setChecked(item.isOn);

        itemHolder.mSchedule.setText(item.details_name + " " + item.details_repeat);

        itemHolder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.isOn = ((CheckBox) v).isChecked();
                showDialog();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        MenuScheduleController.enableSchedule(mContext, item);
                    }
                }.start();

            }
        });


    }

    @Override
    protected void edit(int position) {
        Intent intent = new Intent(mContext, ScheduleEditActivity.class);
        intent.putExtra(Constants.TITLE, mContext.getString(R.string.schedule_edit));
//        intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_COMMON);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CONTENT, ((MenuScheduleUIItem) mDataList.get(position).mLoop));
        intent.putExtras(bundle);
        ((Activity) mContext).startActivityForResult(intent, 1);
        if (mLastSlideViewWithStatusOn != null) {
            mLastSlideViewWithStatusOn.shrinkImmediately();
        }
    }

    @Override
    protected void delete(final int position) {
        MenuScheduleController.deleteSchedule(mContext, (MenuScheduleUIItem) mDataList.get(position).mLoop);
    }

    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public TextView mTime;
        public TextView mSchedule;
        public CheckBox mSwitch;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            mTime = (TextView) convertView.findViewById(R.id.tv_time);
            mSchedule = (TextView) convertView.findViewById(R.id.tv_schedule);
            mSwitch = (CheckBox) convertView.findViewById(R.id.cb_switch);
        }
    }

}
