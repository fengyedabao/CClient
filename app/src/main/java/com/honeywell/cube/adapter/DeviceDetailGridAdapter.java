package com.honeywell.cube.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;

import java.util.List;

public class DeviceDetailGridAdapter extends BaseAdapter {


    public int getItemLayout() {
        return R.layout.grid_device_detail;
    }

    List<ItemBean> mItemList;

    private LayoutInflater mInflater;

    public DeviceDetailGridAdapter(List<ItemBean> list) {
        mItemList = list;
    }


    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView holder;
        if (convertView == null) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mInflater.inflate(getItemLayout(), null);
            holder = (TextView) convertView.findViewById(R.id.tv_text);
            convertView.setTag(holder);
        } else {
            holder = (TextView) convertView.getTag();
        }
        holder.setText(mItemList.get(position).mText);
        return convertView;
    }

    public List<ItemBean> getItemList() {
        return mItemList;
    }

    public void setItemList(List<ItemBean> itemList) {
        this.mItemList = itemList;
    }

    protected static class ItemHolder {
        public ImageView icon;
        public TextView text;
    }

    public static class ItemBean {
        public String mText;

        public ItemBean(String text) {
            mText = text;

        }


    }
}
