
package com.honeywell.cube.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;

import java.util.ArrayList;

public class OneLineTextListAdapter extends BaseAdapter {
    ArrayList<IrLoop> mDataList;
    private LayoutInflater mInflater;

    public OneLineTextListAdapter(ArrayList<IrLoop> dataList) {
        mDataList = dataList;
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
    public View getView(int position, View view, ViewGroup viewgroup) {
        TextView tv;
        if (view == null) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(viewgroup.getContext());
            }
            tv = (TextView) mInflater.inflate(R.layout.list_one_line_text, null);
        } else {
            tv = (TextView) view;
        }
        tv.setText(mDataList.get(position).mLoopName);
        return tv;
    }


    public ArrayList<IrLoop> getDataList() {
        return mDataList;
    }

    public void setDataList(ArrayList<IrLoop> dataList) {
        mDataList = dataList;
    }
}
