
package com.honeywell.cube.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;

import java.util.List;


public class IconSelectAdapter extends BaseAdapter {
    List<MenuDeviceIRIconItem> mDataList;

    private LayoutInflater mInflater;
    int selectPosition;

    public IconSelectAdapter(List<MenuDeviceIRIconItem> list) {
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
        holder.mIcon.setImageResource(item.IR_icon_select ? item.IR_icon_imageSelectId : item.IR_icon_imageId);
        return convertView;
    }

    public List<MenuDeviceIRIconItem> getDataList() {
        return mDataList;
    }

    public void setDataList(List<MenuDeviceIRIconItem> itemList) {
        this.mDataList = itemList;
    }

    public void updateResult() {
        final int size = getCount();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                final MenuDeviceIRIconItem item = mDataList.get(i);
                item.IR_icon_select = selectPosition == i;
            }
            notifyDataSetChanged();
        }
    }

    public void setSelectPosition(int position) {
        selectPosition = position;
    }

    protected static class ItemHolder {
        public ImageView mIcon;

        ItemHolder(View view) {
            mIcon = (ImageView) view.findViewById(R.id.icon);
        }
    }


}
