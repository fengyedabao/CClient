
package com.honeywell.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.notifi.NotifiFilterObject;
import com.honeywell.cube.widgets.CheckItem;

import java.util.ArrayList;
import java.util.List;


public class CheckTextAdapter extends BaseAdapter {
    ArrayList<NotifiFilterObject> mDataList;

    private LayoutInflater mInflater;
    protected Context mContext;
    CheckItem mSelectAll;
    List<Boolean> mDataListBak = new ArrayList<>();

    public CheckTextAdapter(Context context, ArrayList<NotifiFilterObject> list, CheckItem selectAll) {
        mInflater = LayoutInflater.from(context);
        mDataList = list;
        mContext = context;
        mSelectAll = selectAll;
        backupData();
        mSelectAll.setChecked(isAllChecked());
    }

    public void backupData() {
        mDataListBak.clear();
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                mDataListBak.add(mDataList.get(i).isChecked);
            }
        }
    }

    public void restoreData() {
        if (mDataList != null && mDataList.size() == mDataListBak.size()) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                mDataList.get(i).isChecked = mDataListBak.get(i);
            }
        }
    }

    public int getItemLayout() {
        return R.layout.list_check_text;
    }

    public ItemHolder initItemHolder(View view) {
        return new ItemHolder(view);
    }

    public void initView(final ItemHolder holder, final int position) {
        holder.mCheckItem.setTextName(mDataList.get(position).name);
        holder.mCheckItem.setChecked(mDataList.get(position).isChecked);
        holder.mCheckItem.setOnCkeckItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataList.get(position).isChecked = ((CheckItem) v).isChecked();
                final boolean isAllChecked = isAllChecked();
                if (mSelectAll.isChecked() != isAllChecked) {
                    mSelectAll.setChecked(isAllChecked);
                }
            }
        });
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


    //然后重写getView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(getItemLayout(), null);
            holder = initItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        initView(holder, position);
        return convertView;
    }

    public ArrayList<NotifiFilterObject> getDataList() {
        return mDataList;
    }

    public ArrayList<NotifiFilterObject> getCheckedList() {
        ArrayList<NotifiFilterObject> list = new ArrayList<>();
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final NotifiFilterObject item = mDataList.get(i);
                if (item.isChecked) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    public boolean isAllChecked() {
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                if (!mDataList.get(i).isChecked) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void setDataList(ArrayList<NotifiFilterObject> itemList) {
        this.mDataList = itemList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void selectAll(boolean selectAll) {
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                mDataList.get(i).isChecked = selectAll;
            }
        }
        notifyDataSetChanged();
    }


    protected static class ItemHolder {
        public CheckItem mCheckItem;

        ItemHolder(View view) {
            mCheckItem = (CheckItem) view.findViewById(R.id.ci_filter);
        }
    }

}
