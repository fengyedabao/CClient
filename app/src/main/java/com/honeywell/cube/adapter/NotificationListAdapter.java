
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.NotificationListActivity;
import com.honeywell.cube.controllers.UIItem.notifi.NotifiUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.lib.widgets.HeadListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationListAdapter extends BaseAdapter implements SectionIndexer,
        HeadListView.HeaderAdapter, AbsListView.OnScrollListener {
    public static final int TYPE_SECTION = 0;
    public static final int TYPE_NORMAL = 1;
    private List<Integer> mPositions;
    private List<NotifiUIItem> mSections;

    public ArrayList<NotifiUIItem> mDataList;

    private LayoutInflater mInflater;
    private Dialog mDialog;
    protected Context mContext;

    public NotificationListAdapter(Context context, ArrayList<NotifiUIItem> list, Dialog dialog) {
        mInflater = LayoutInflater.from(context);
        mDialog = dialog;
        mDataList = list;
        mContext = context;
        initDateHead();
    }

    public int getItemLayout(int position) {
        int layoutId = -1;
        final int itemType = getItemViewType(position);
        if (itemType == TYPE_SECTION) {
            layoutId = getSectionLayout();
        } else if (itemType == TYPE_NORMAL) {
            layoutId = getNormalLayout();
        }
        return layoutId;
    }


    public int getSectionLayout() {
        return R.layout.list_notification_section;
    }

    int getNormalLayout() {
        return R.layout.list_notification;
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
        ItemHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(getItemLayout(position), null);
            holder = initItemHolder(convertView, position);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        initView(holder, position);
        return convertView;
    }

    public void showDialog() {
        if (mDialog != null) {//&& !mDialog.isShowing()
            mDialog.show();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).item_type == ModelEnum.UI_TYPE_TITLE ? TYPE_SECTION : TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /* 获取头部head标签数据 */
    protected void initDateHead() {
        mSections = new ArrayList<>();
        mPositions = new ArrayList<>();
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final NotifiUIItem item = mDataList.get(i);
                if (item.item_type == ModelEnum.UI_TYPE_TITLE) {
                    mSections.add(item);
                    mPositions.add(i);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof HeadListView) {
            ((HeadListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public int getHeaderState(int position) {
        int realPosition = position;
        if (realPosition < 0 || position >= getCount()) {
            return HEADER_GONE;
        }
        int section = getSectionForPosition(realPosition);
        if (section < 0) {
            return HEADER_GONE;
        }
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
        NotifiUIItem item = (NotifiUIItem) getSections()[section];
//        LogUtil.e("alinmi22", "position = " + position + " , item.title_day = " + item.title_day + " , item.title_month = " + item.title_month);
        ((TextView) header.findViewById(R.id.tv_section_day)).setText(item.title_day);
        ((TextView) header.findViewById(R.id.tv_section_month)).setText(item.title_month);
        ((ImageView) header.findViewById(R.id.iv_icon)).setImageResource(item.image_id);
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

    public ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    public void initView(ItemHolder holder, final int position) {
        final ItemHolder itemHolder = holder;
        final NotifiUIItem itemBean = mDataList.get(position);
        final int itemType = getItemViewType(position);
        switch (itemType) {
            case TYPE_SECTION:
                itemHolder.mIcon.setImageResource(itemBean.image_id);
                itemHolder.mDay.setText(itemBean.title_day);
                itemHolder.mMonth.setText(itemBean.title_month);
                break;
            case TYPE_NORMAL:
                itemHolder.mIcon.setImageResource(itemBean.image_id);
                itemHolder.mTitle.setText(itemBean.title_cell);
                itemHolder.mAddr.setText(itemBean.room_str);
                itemHolder.mTime.setText(itemBean.time_str);
                itemHolder.mPlayVideo.setVisibility(itemBean.show_video_btn ? View.VISIBLE : View.GONE);
                itemHolder.mPlayVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((NotificationListActivity) mContext).playVideo(position);
                    }
                });
                itemHolder.mTitle.setEnabled(itemBean.unReaded);
                itemHolder.mAddr.setEnabled(itemBean.unReaded);
                itemHolder.mTime.setEnabled(itemBean.unReaded);
                itemHolder.mItem.setEnabled(itemBean.unReaded);
                break;
        }
    }

    public ArrayList<NotifiUIItem> getDataList() {
        return mDataList;
    }

    @Override
    public boolean isEnabled(int position) {
        if (TYPE_SECTION == getItemViewType(position)) {
            return false;
        } else {
            return super.isEnabled(position);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        initDateHead();
        super.notifyDataSetChanged();
    }

    protected static class ItemHolder {
        ImageView mIcon;
        TextView mTitle;
        TextView mAddr;
        TextView mTime;
        TextView mDay;
        TextView mMonth;
        ImageView mPlayVideo;
        View mItem;

        public ItemHolder(View view) {
            mIcon = (ImageView) view.findViewById(R.id.iv_icon);
            mTitle = (TextView) view.findViewById(R.id.tv_title);
            mAddr = (TextView) view.findViewById(R.id.tv_addr);
            mTime = (TextView) view.findViewById(R.id.tv_time);
            mDay = (TextView) view.findViewById(R.id.tv_section_day);
            mMonth = (TextView) view.findViewById(R.id.tv_section_month);
            mPlayVideo = (ImageView) view.findViewById(R.id.iv_play_video);
            mItem = view;
        }
    }

}
