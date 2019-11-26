
package com.honeywell.cube.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;


public class IRIconStudyAdapter extends BaseAdapter {
    final static String TAG = IRIconStudyAdapter.class.getSimpleName();
    ArrayList<MenuDeviceIRIconItem> mDataList;

    private LayoutInflater mInflater;

    public IRIconStudyAdapter(ArrayList<MenuDeviceIRIconItem> list) {
        mDataList = list;
    }

    public int getItemLayout() {
        return R.layout.list_icon_square;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mInflater.inflate(getItemLayout(), null);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        final MenuDeviceIRIconItem item = mDataList.get(position);
        if (item.IR_icon_imageId == -1) {
            holder.mIcon.setVisibility(View.INVISIBLE);
            convertView.setEnabled(false);
        } else {
            holder.mIcon.setImageResource(item.IR_icon_enable ? item.IR_icon_imageSelectId : item.IR_icon_imageId);
        }
        return convertView;
    }

    public void updateView(int position, boolean enabled) {

        if (position >= mDataList.size() || position < 0) {
            LogUtil.e(TAG, "position is illegal , position = " + position, true);
            return;
        }
        final MenuDeviceIRIconItem item = mDataList.get(position);
        item.IR_icon_enable = enabled;
        notifyDataSetChanged();
    }

    public ArrayList<MenuDeviceIRIconItem> getDataList() {
        return mDataList;
    }

    public void setDataList(ArrayList<MenuDeviceIRIconItem> itemList) {
        this.mDataList = itemList;
    }

    protected static class ItemHolder {
        public ImageView mIcon;

        ItemHolder(View view) {
            mIcon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if (mDataList.get(position).IR_icon_imageSelectId == -1) {
            return false;
        }
        return super.isEnabled(position);
    }
}
