
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;

import java.util.List;


public class SparkLightingTypeAdapter extends BaseAdapter {
    public List<MenuDeviceUIItem> mDataList;

    private LayoutInflater mInflater;
    private Dialog mDialog;

    public SparkLightingTypeAdapter(Context context, List<MenuDeviceUIItem> list, Dialog dialog) {
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
            convertView = mInflater.inflate(R.layout.list_spark_lighting_type, null);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.mTextPrimary.setText(mDataList.get(position).sparkTitle);
        holder.mTextSecondary.setText(mDataList.get(position).sparkDetails);
        return convertView;
    }

    public List<MenuDeviceUIItem> getDataList() {
        return mDataList;
    }

    public void setDataList(List<MenuDeviceUIItem> dataList) {
        this.mDataList = dataList;
    }

    public void showDialog() {
        if (mDialog != null) {//&& !mDialog.isShowing()
            mDialog.show();
        }
    }

    public static class ItemHolder {
        TextView mTextPrimary;
        TextView mTextSecondary;

        public ItemHolder(View view) {
            mTextPrimary = (TextView) view.findViewById(R.id.tv_text_primary);
            mTextSecondary = (TextView) view.findViewById(R.id.tv_text_secondary);
        }
    }

}
