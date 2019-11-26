
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.DeviceAirConditionerActivity;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public class AirConditionerListAdapter extends DeviceListBaseAdapter {

    public AirConditionerListAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public int getItemLayout(int position) {
        return R.layout.list_air_conditioner;
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
        itemHolder.mPrimaryText.setText(item.mText);
    }

    @Override
    protected View.OnClickListener getItemClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DeviceAirConditionerActivity.class);
                intent.putExtra(Constants.TITLE, mContext.getString(R.string.device_type_air_conditioner));
                DeviceHelper.addObject2Intent(intent, Constants.CONTENT, mDataList.get(position).mLoop);
                mContext.startActivity(intent);
            }
        };
    }

    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView mIcon;
        public TextView mPrimaryText;

        public ItemHolder(SlideView convertView) {
            super(convertView);
            mIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            mPrimaryText = (TextView) convertView.findViewById(R.id.tv_text);
        }
    }

}
