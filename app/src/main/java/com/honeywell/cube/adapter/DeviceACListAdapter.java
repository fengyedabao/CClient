
package com.honeywell.cube.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.honeywell.cube.R;

import java.util.List;

public class DeviceACListAdapter extends BaseAdapter {
    List<ItemBean> mDataList;
    private LayoutInflater mInflater;

    public DeviceACListAdapter(List<ItemBean> dataList) {
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i).mData;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewgroup) {
        TextView tv;
        if (view == null) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(viewgroup.getContext());
            }
            tv = (TextView) mInflater.inflate(R.layout.list_device_ac, null);
        } else {
            tv = (TextView) view;
        }
        tv.setText(mDataList.get(position).mName);
        return tv;
    }


    public List<ItemBean> getDataList() {
        return mDataList;
    }

    public void setDataList(List<ItemBean> dataList) {
        mDataList = dataList;
    }

    public static class ItemBean {
        public String mName;
        public Object mData;

        public ItemBean(String name, Object data) {
            mName = name;
            mData = data;
        }
    }
}
