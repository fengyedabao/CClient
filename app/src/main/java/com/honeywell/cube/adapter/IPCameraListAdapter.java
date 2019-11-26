
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.IpcPlayerActivity;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.utils.Constants;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class IPCameraListAdapter extends DeviceListBaseAdapter {

    public IPCameraListAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
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
        final ItemBean item = (ItemBean) getDataList().get(position);
        itemHolder.mIcon.setImageResource(item.mIconId);
        itemHolder.mPrimaryText.setText(item.mText);
        itemHolder.mSecondaryText.setText(item.mTextSecondary);
    }

    public int getItemLayout(int position) {
        return R.layout.list_ip_camera;
    }


    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView mIcon;
        public TextView mPrimaryText;
        public TextView mSecondaryText;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            mIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            mPrimaryText = (TextView) convertView.findViewById(R.id.tv_text);
            mSecondaryText = (TextView) convertView.findViewById(R.id.tv_text_secondary);
        }
    }

    @Override
    protected View.OnClickListener getItemClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, IpcPlayerActivity.class);
                intent.putExtra(Constants.TITLE, mDataList.get(position).mText);
                Bundle bundle = new Bundle();
                final IpcStreamInfo ipcStreamInfo = (IpcStreamInfo) mDataList.get(position).mLoop;
                ipcStreamInfo.mIPAddr = ((ItemBean) mDataList.get(position)).mTextSecondary;
                bundle.putParcelable(Constants.IPC_STREAM_INFO, ipcStreamInfo);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        };
    }

    @Override
    protected void delete(int position) {
        super.delete(position);
    }

    @Override
    protected void edit(int position) {
        super.edit(position);
    }

    public static class ItemBean extends DeviceListBaseAdapter.ItemBean {
        String mTextSecondary;

        public ItemBean(int iconId, String textPrimary, String textSecondary, Object loop) {
            super(iconId, null, textPrimary, loop, "");
            mTextSecondary = textSecondary;
        }
    }

}
