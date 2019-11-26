
package com.honeywell.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.configuredatabase.BasicLoop;

import java.util.ArrayList;


public class ScenarioEditZoneAdapter extends BaseAdapter {
    //    public List<ItemBean> mDataList;
    public ArrayList<UIItems> mDataList;
    private LayoutInflater mInflater;
    private CheckBox mSelectAll;

    public ScenarioEditZoneAdapter(Context context, ArrayList<UIItems> list, CheckBox cb) {
        mInflater = LayoutInflater.from(context);
        mDataList = list;
        mSelectAll = cb;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_scenario_edit_zone, null);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
                mDataList.get(position).isSelcet = holder.mCheckBox.isChecked();
                if (mSelectAll.isChecked()) {
                    if (!holder.mCheckBox.isChecked()) {
                        mSelectAll.setChecked(false);
                    }
                }
            }
        });
        holder.mCheckBox.setChecked(mDataList.get(position).isSelcet);
        holder.mText.setText(((BasicLoop) mDataList.get(position).object).mLoopName);
        return convertView;
    }

    public ArrayList<UIItems> getDataList() {
        return mDataList;
    }

    public void setItemList(ArrayList<UIItems> itemList) {
        this.mDataList = itemList;
    }


    public static class ItemHolder {
        TextView mText;
        CheckBox mCheckBox;

        public ItemHolder(View view) {
            mText = (TextView) view.findViewById(R.id.tv_text);
            mCheckBox = (CheckBox) view.findViewById(R.id.slide_switch);
            mCheckBox.setClickable(false);
        }
    }

}
