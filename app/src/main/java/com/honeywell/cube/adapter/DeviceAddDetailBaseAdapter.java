
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.honeywell.cube.activities.ModuleListActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceLoopObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.SwitchItem;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.widgets.HeadListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DeviceAddDetailBaseAdapter extends BaseAdapter implements SectionIndexer,
        HeadListView.HeaderAdapter, OnScrollListener {
    public static final int TYPE_SECTION = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_LOOP = 2;
    private List<Integer> mPositions;
    private List<String> mSections;

    public ArrayList<ItemBean> mDataList;

    private LayoutInflater mInflater;
    private Dialog mDialog;
    protected Context mContext;
    MenuDeviceUIItem mMenuDeviceUIItem;

    public DeviceAddDetailBaseAdapter(Context context, MenuDeviceUIItem item, Dialog dialog) {
        mInflater = LayoutInflater.from(context);
        mDialog = dialog;
        mContext = context;
        if (item != null) {
            mMenuDeviceUIItem = item;
            initDataList();
            initDateHead();
        }
    }

    public int getSectionLayout() {
        return R.layout.list_device_add_detail_section;
    }

    abstract int getHeaderLayout();

    abstract int getLoopLayout();

    public int getItemLayout(int position) {
        int layoutId = -1;
        final int itemType = getItemViewType(position);
        if (itemType == TYPE_SECTION) {
            layoutId = getSectionLayout();
        } else if (itemType == TYPE_HEADER) {
            layoutId = getHeaderLayout();
        } else if (itemType == TYPE_LOOP) {
            layoutId = getLoopLayout();
        }
        return layoutId;
    }

    public abstract ItemHolder initItemHolder(View view, int position);

    public abstract void initView(final ItemHolder holder, int position);

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
        return mDataList.get(position).mItemType;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    /* 获取头部head标签数据 */
    protected void initDateHead() {
        mSections = new ArrayList<>();
        mPositions = new ArrayList<>();
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final ItemBean item = mDataList.get(i);
                if (item.mItemType == TYPE_SECTION) {
                    mSections.add(item.mSection);
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

    protected void setData(MenuDeviceUIItem item) {
        mMenuDeviceUIItem = item;
        initDataList();
    }

    public MenuDeviceUIItem getData() {
        if (mMenuDeviceUIItem != null) {
            getHeaderData();
            ArrayList<MenuDeviceLoopObject> loops = new ArrayList<>();
            if (mDataList != null) {
                final int size = mDataList.size();
                for (int i = 0; i < size; i++) {
                    final ItemBean itemBean = mDataList.get(i);
                    if (itemBean.mItemType == TYPE_LOOP) {
                        loops.add(itemBean.mMenuDeviceLoopObject);
                    }

                }
            }
            mMenuDeviceUIItem.loopObjects = loops;
        }
        return mMenuDeviceUIItem;
    }

    public boolean isDeviceSelected() {
        if (mMenuDeviceUIItem != null) {
            if (mDataList != null) {
                final int size = mDataList.size();
                for (int i = 0; i < size; i++) {
                    final ItemBean itemBean = mDataList.get(i);
                    if (itemBean.mItemType == TYPE_LOOP) {
                        if (itemBean.mMenuDeviceLoopObject == null || itemBean.mMenuDeviceLoopObject.enable) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected void getHeaderData() {
        mMenuDeviceUIItem.peripheraDevice = mDataList.get(0).mDevice;
    }

    protected void initDataList() {
        mDataList = new ArrayList<>();
        if (mMenuDeviceUIItem != null) {
            initHeaderData();
            final ArrayList<MenuDeviceLoopObject> list = mMenuDeviceUIItem.loopObjects;
            if (list != null && list.size() > 0) {
                final int size = list.size();
                for (int i = 0; i < size; i++) {
                    final MenuDeviceLoopObject loop = list.get(i);

                    mDataList.add(new DeviceAddDetailBaseAdapter.ItemBean(TYPE_SECTION, loop.section, null, null));
                    mDataList.add(new DeviceAddDetailBaseAdapter.ItemBean(TYPE_LOOP, "", loop, null));
                }
            }
        }

    }

    protected void initHeaderData() {
        mDataList.add(new ItemBean(TYPE_HEADER, "", null, mMenuDeviceUIItem.peripheraDevice));
    }

    protected void initRoom(final SelectItem room, final DeviceAddDetailBaseAdapter.ItemBean itemBean) {
        DeviceHelper.initRoom(room);
        room.setContent(itemBean.mMenuDeviceLoopObject.room);
        room.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                itemBean.mMenuDeviceLoopObject.room = item.mText;
                itemBean.mMenuDeviceLoopObject.roomId = (int) item.mData;
            }
        });
    }

    protected void initName(final EditTextItem name, final ItemBean itemBean) {
        name.setOnEditTextChangedListener(null);
        name.getEditName().setText(itemBean.mMenuDeviceLoopObject.name);
        name.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                itemBean.mMenuDeviceLoopObject.name = s;
            }
        });
    }

    protected void initUsing(final SwitchItem using, final ItemBean itemBean) {
        using.setChecked(itemBean.mMenuDeviceLoopObject.enable);
        using.setCheckBoxClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemBean.mMenuDeviceLoopObject.enable = ((CheckBox) v).isChecked();
            }
        });
    }

    protected void initHeader(final SelectItem header, final ItemBean itemBean, int type) {
        DeviceHelper.initModuleData(header, type);
        if (itemBean.mDevice != null) {
            header.setContent(itemBean.mDevice.mName);
        } else {
            header.setContent("");
        }
        header.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(mContext, ModuleListActivity.class);
                    mContext.startActivity(intent);
                } else {
                    header.setContent(position);
                    itemBean.mDevice = (PeripheralDevice) header.getDataList().get(position).mData;
                }
            }
        });
    }

    protected static class ItemHolder {
        public SelectItem mHeader;
        public TextView mSection;
        public EditTextItem mName;
        public SelectItem mRoom;
        public SwitchItem mUsing;

        public ItemHolder(View view) {
            mHeader = (SelectItem) view.findViewById(R.id.si_header);
            mSection = (TextView) view.findViewById(R.id.tv_section);
            mName = (EditTextItem) view.findViewById(R.id.ei_name);
            mRoom = (SelectItem) view.findViewById(R.id.si_room);
            mUsing = (SwitchItem) view.findViewById(R.id.ci_using);
        }
    }

    public static class ItemBean {
        int mItemType;
        String mSection;
        MenuDeviceLoopObject mMenuDeviceLoopObject;
        PeripheralDevice mDevice;

        public ItemBean(int itemType, String section, MenuDeviceLoopObject menuDeviceLoopObject, PeripheralDevice device) {
            mItemType = itemType;
            mSection = section;
            mMenuDeviceLoopObject = menuDeviceLoopObject;
            mDevice = device;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if (TYPE_SECTION == getItemViewType(position)) {
            return false;
        } else {
            return super.isEnabled(position);
        }
    }
}
