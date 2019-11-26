
package com.honeywell.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;


import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.lib.widgets.HeadListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScenarioEditDeviceSelectAdapter extends BaseAdapter implements SectionIndexer,
        HeadListView.HeaderAdapter, OnScrollListener {
    public static final int TYPE_SECTION = 0;
    public static final int TYPE_NORMAL = 1;
    private Context mContext;
    public ArrayList<UIItems> mDataList;
    private LayoutInflater mInflater = null;
    private List<Integer> mPositions;
    private List<String> mSections;

    public ScenarioEditDeviceSelectAdapter(Context mContext, ArrayList<UIItems> dataList) {
        this.mContext = mContext;
        this.mDataList = dataList;
        mInflater = LayoutInflater.from(mContext);
        initDateHead();
    }

    public int getItemLayout(int position) {
        return TYPE_SECTION == getItemViewType(position) ? R.layout.list_scenario_select_device_section : R.layout.list_scenario_select_device;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemType(mDataList.get(position));
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private void initDateHead() {
        mSections = new ArrayList<>();
        mPositions = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            if (getItemViewType(i) == TYPE_SECTION) {
                mSections.add(mContext.getString(Constants.DEVICE_TYPE_MAP.get((String) mDataList.get(i).object)));
                mPositions.add(i);
            }
//            if (i == 0) {
//                mSections.add(String.valueOf(mDataList.get(i).mSection));
//                mPositions.add(i);
//                continue;
//            }
//            if (i != mDataList.size()) {
//                if (mDataList.get(i).mSection != mDataList.get(i - 1).mSection) {
//                    mSections.add(String.valueOf(mDataList.get(i).mSection));
//                    mPositions.add(i);
//                }
//            }
        }
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public UIItems getItem(int position) {
        if (mDataList != null && mDataList.size() != 0) {
            return mDataList.get(position);
        }
        return null;
    }

    private int getItemType(UIItems item) {
        if (item.type == ModelEnum.UI_TYPE_TITLE) {
            return TYPE_SECTION;
        } else {
            return TYPE_NORMAL;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemHolder holder;
        final int type = getItemViewType(position);
        final UIItems itemBean = mDataList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(getItemLayout(position), null);
            holder = new ItemHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        View item = convertView.findViewById(R.id.ll_item);
        if (item != null) {
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mSelect.setChecked(!holder.mSelect.isChecked());
                    mDataList.get(position).isSelcet = holder.mSelect.isChecked();
                }
            });
        }
        if (TYPE_SECTION == type) {
            holder.mSection.setText(mContext.getString(Constants.DEVICE_TYPE_MAP.get((String) itemBean.object)));

        } else {
            holder.mText.setText(((BasicLoop) itemBean.object).mLoopName);
            holder.mSelect.setChecked(itemBean.isSelcet);
        }

        return convertView;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof HeadListView) {
            ((HeadListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public int getHeaderState(int position) {
        int realPosition = position;
        if (realPosition < 0 || position >= getCount()) {
            return HEADER_GONE;
        }
        int section = getSectionForPosition(realPosition);
        int nextSectionPosition = getPositionForSection(section + 1);
        if (nextSectionPosition != -1 && realPosition == nextSectionPosition - 1) {
            return HEADER_PUSHED_UP;
        }
        return HEADER_VISIBLE;
    }

    @Override
    public void configureHeader(View header, int position, int alpha) {
        int realPosition = position;
        int section = getSectionForPosition(realPosition);
        String title = (String) getSections()[section];
        ((TextView) header.findViewById(R.id.tv_section)).setText(title);

    }

    @Override
    public Object[] getSections() {
        return mSections.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndex < 0 || sectionIndex >= mPositions.size()) {
            return -1;
        }
        return mPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position < 0 || position >= getCount()) {
            return -1;
        }
        int index = Arrays.binarySearch(mPositions.toArray(), position);
        return index >= 0 ? index : -index - 2;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public ArrayList<UIItems> getSelectedDevices() {
        if (mDataList == null || mDataList.size() < 1) {
            return null;
        } else {
            ArrayList<UIItems> result = new ArrayList<>();
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final UIItems item = mDataList.get(i);
                if (item.type == ModelEnum.UI_TYPE_TITLE || item.isSelcet) {
                    result.add(item);
                }
            }
            if (result.size() > 1) {
                ArrayList<UIItems> result2 = new ArrayList<>();
                final int size2 = result.size();
                for (int i = 0; i < size2; i++) {
                    final UIItems item2 = result.get(i);
                    if (item2.type == ModelEnum.UI_TYPE_TITLE) {
                        if (i != size2 - 1 && item2.deviceType.equalsIgnoreCase(result.get(i + 1).deviceType)) {
                            result2.add(item2);
                        }
                    } else {
                        result2.add(item2);
                    }
                }
                return result2;
            } else {
                return null;
            }
        }
    }

    class ItemHolder {
        TextView mSection;
        CheckBox mSelect;
        TextView mText;

        public ItemHolder(View view) {
            mSection = (TextView) view.findViewById(R.id.tv_section);
            mText = (TextView) view.findViewById(R.id.tv_text);
            mSelect = (CheckBox) view.findViewById(R.id.cb_selected);
        }
    }
}
