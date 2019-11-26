
package com.honeywell.cube.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.ModuleEditActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.controllers.menus.MenuModuleController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class ModuleListAdapter extends DeviceListBaseAdapter {
    public ModuleListAdapter(Context context, List<? extends ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public int getItemLayout(int position) {
        return R.layout.list_module;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, int position) {
        ItemHolder holder = new ItemHolder(slideView);
        return holder;
    }

    public int getSlideViewLayout() {
        return R.layout.layout_slide_view_edit_delete_module;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, int position) {
        super.initView(holder, position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        final MenuModuleUIItem item = (MenuModuleUIItem) mDataList.get(position).mLoop;
        itemHolder.mName.setText(item.title);
        itemHolder.mName.setEnabled(item.state);

        itemHolder.mVersion.setText(item.version);
        itemHolder.mVersion.setEnabled(item.state);

        itemHolder.mModule.setText(item.type);
        itemHolder.mModule.setEnabled(item.state);

        itemHolder.mIpAddr.setText(item.ipAddr);
        itemHolder.mIpAddr.setEnabled(item.state);

        itemHolder.mFlag.setBackgroundResource(item.state ? R.color.module_enable : R.color.module_disable);

    }

    @Override
    protected void edit(int position) {
        Intent intent = new Intent(mContext, ModuleEditActivity.class);
        intent.putExtra(Constants.TITLE, mContext.getString(R.string.configure));
        intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_COMMON);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CONTENT, ((MenuModuleUIItem) mDataList.get(position).mLoop));
        intent.putExtras(bundle);
        ((Activity) mContext).startActivityForResult(intent, 1);
        if (mLastSlideViewWithStatusOn != null) {
            mLastSlideViewWithStatusOn.shrinkImmediately();
        }
    }

    @Override
    protected void delete(int position) {
        MenuModuleController.deleteModule(mContext, ((MenuModuleUIItem) mDataList.get(position).mLoop).moduleObject);
    }

    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public TextView mName;
        public TextView mVersion;
        public TextView mModule;
        public TextView mIpAddr;
        public View mFlag;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            mName = (TextView) convertView.findViewById(R.id.tv_name);
            mVersion = (TextView) convertView.findViewById(R.id.tv_version);
            mModule = (TextView) convertView.findViewById(R.id.tv_module);
            mIpAddr = (TextView) convertView.findViewById(R.id.tv_ip_addr);
            mFlag = convertView.findViewById(R.id.flag);
        }
    }
}
