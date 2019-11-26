
package com.honeywell.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.honeywell.cube.R;

import java.util.List;


public class SelectScenarioIconAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<ItemBean> mDataList;
    int mSelectedPosition = 0;

    public SelectScenarioIconAdapter(Context context, List<ItemBean> list) {
        mInflater = LayoutInflater.from(context);
        mDataList = list;
    }


    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.grid_select_scenario_icon, null);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        holder.mIcon.setImageResource(mDataList.get(position).mIconid);
        holder.mSelectedIcon.setVisibility(mSelectedPosition == position ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    static class ItemHolder {
        ImageView mIcon;
        ImageView mSelectedIcon;

        public ItemHolder(View view) {
            mIcon = (ImageView) view.findViewById(R.id.iv_icon);
            mSelectedIcon = (ImageView) view.findViewById(R.id.iv_selected);
        }
    }

    public static class ItemBean {
        public String mText;
        public int mIconid;

        public ItemBean(String text, int iconId) {
            mText = text;
            mIconid = iconId;
        }


    }

}
