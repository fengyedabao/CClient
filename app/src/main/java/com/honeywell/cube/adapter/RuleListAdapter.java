
package com.honeywell.cube.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.RuleEditActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleUIItem;
import com.honeywell.cube.controllers.menus.MenuRuleController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class RuleListAdapter extends DeviceListBaseAdapter {
    public RuleListAdapter(Context context, List<? extends ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public int getItemLayout(int position) {
        return R.layout.list_rule;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, int position) {
        ItemHolder holder = new ItemHolder(slideView);
        return holder;
    }

    public int getSlideViewLayout() {
        return R.layout.layout_slide_view_edit_delete_rule;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, final int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        final MenuRuleUIItem item = (MenuRuleUIItem) mDataList.get(position).mLoop;
        itemHolder.mSchedule.setText(item.info_details);
        itemHolder.mContent.setText(item.info_title);
        itemHolder.mTvDevice.setText(item.info_input_name);
        itemHolder.mIvDevice.setImageResource(item.info_input_imagename);
        itemHolder.mIvScenario.setImageResource(item.info_output_imagename);
        itemHolder.mTvScenario.setText(item.info_output_name);
        itemHolder.mSwith.setChecked(item.info_states);
        itemHolder.mSwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.info_states = ((CheckBox) v).isChecked();
                showDialog();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        MenuRuleController.enableRule(mContext, item.info_states, ((MenuRuleUIItem) mDataList.get(position).mLoop).info);
                    }
                }.start();

            }
        });

    }

    @Override
    protected void edit(int position) {
        Intent intent = new Intent(mContext, RuleEditActivity.class);
        intent.putExtra(Constants.TITLE, mContext.getString(R.string.rule_edit));
//        intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_COMMON);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CONTENT, ((MenuRuleUIItem) mDataList.get(position).mLoop));
        intent.putExtras(bundle);
        ((Activity) mContext).startActivityForResult(intent, 1);
        if (mLastSlideViewWithStatusOn != null) {
            mLastSlideViewWithStatusOn.shrinkImmediately();
        }
    }

    @Override
    protected void delete(int position) {
        MenuRuleController.deleteRule(mContext, ((MenuRuleUIItem) mDataList.get(position).mLoop).info);
    }

    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public TextView mSchedule;
        ImageView mIvDevice;
        TextView mTvDevice;
        ImageView mIvScenario;
        TextView mTvScenario;
        public TextView mContent;
        public CheckBox mSwith;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            mSchedule = (TextView) convertView.findViewById(R.id.tv_schedule);
            mIvDevice = (ImageView) convertView.findViewById(R.id.iv_device);
            mTvDevice = (TextView) convertView.findViewById(R.id.tv_device);
            mIvScenario = (ImageView) convertView.findViewById(R.id.iv_scenario);
            mTvScenario = (TextView) convertView.findViewById(R.id.tv_scenario);
            mContent = (TextView) convertView.findViewById(R.id.tv_content);
            mSwith = (CheckBox) convertView.findViewById(R.id.cb_switch);
        }
    }
}
