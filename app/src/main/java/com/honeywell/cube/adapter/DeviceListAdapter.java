
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.honeywell.cube.R;

import java.util.List;


public class DeviceListAdapter extends BaseAdapter {
    public List<ItemBean> mDataList;

    private LayoutInflater mInflater;
    private Dialog mDialog;

    public DeviceListAdapter(Context context, List<ItemBean> list, Dialog dialog) {
        mInflater = LayoutInflater.from(context);
        mDialog = dialog;
        mDataList = list;
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

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_device_list, null);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.mText.setText(mDataList.get(position).mText);
        return convertView;
    }

    public List<? extends ItemBean> getDataList() {
        return mDataList;
    }

    public void setDataList(List<ItemBean> dataList) {
        this.mDataList = dataList;
    }

    public void showDialog() {
        if (mDialog != null) {//&& !mDialog.isShowing()
            mDialog.show();
        }
    }

    public static class ItemHolder {
        TextView mText;

        public ItemHolder(View view) {
            mText = (TextView) view.findViewById(R.id.tv_text);
        }
    }

    public static class ItemBean {
        //        public int mIconId;
//        public Drawable mIcon;
        public String mText;
        public Object mLoop;
        public String mDeviceType;

        public ItemBean(String text, Object loop, String type) {
//            mIconId = iconId;
//            mIcon = icon;
            mText = text;
            mLoop = loop;
            mDeviceType = type;

        }
    }

}
