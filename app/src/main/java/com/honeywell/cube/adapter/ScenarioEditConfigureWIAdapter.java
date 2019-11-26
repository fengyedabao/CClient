
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.ScenarioEditConfigureWIActivity;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.controllers.UIItem.ScenarioDeviceIrUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.dialogs.BottomDialog;

import java.util.ArrayList;

public class ScenarioEditConfigureWIAdapter extends DeviceAddDetailBaseAdapter {
    ArrayList<BottomDialog.ItemBean> mIconList;
    ArrayList<ScenarioDeviceIrUIItem> mScenarioDeviceIrUIItems;

    public ScenarioEditConfigureWIAdapter(Context context, ArrayList<ScenarioDeviceIrUIItem> items, Dialog dialog, ArrayList<MenuDeviceIRIconItem> icons) {
        super(context, null, dialog);
        mScenarioDeviceIrUIItems = items;
        initDataList();
        initDateHead();
        initIconList(icons);
    }

    protected void initDataList() {
        mDataList = new ArrayList<>();
        if (mScenarioDeviceIrUIItems != null && mScenarioDeviceIrUIItems.size() > 0) {
            final int size = mScenarioDeviceIrUIItems.size();
            for (int i = 0; i < size; i++) {
                final ScenarioDeviceIrUIItem loop = mScenarioDeviceIrUIItems.get(i);
                mDataList.add(new ItemBean(TYPE_SECTION, loop.sectionTitle, null));
                mDataList.add(new ItemBean(TYPE_LOOP, "", loop));
            }
        }

    }

    private void initIconList(ArrayList<MenuDeviceIRIconItem> icons) {
        mIconList = new ArrayList<>();
        if (icons != null) {
            final int size = icons.size();
            for (int i = 0; i < size; i++) {
                final MenuDeviceIRIconItem item = icons.get(i);
                mIconList.add(new BottomDialog.ItemBean(item.IR_icon_name, item));
            }
        }

    }

    @Override
    int getLoopLayout() {
        return R.layout.list_scenario_edit_configure_wi;
    }

    private boolean mDeleteMode = false;


    @Override
    int getHeaderLayout() {
        return R.layout.list_device_add_header;
    }


    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mButtonName.setName(R.string.name);
                holder.mButtonName.setDialogTitile(((ScenarioEditConfigureWIActivity) mContext).getTitleString());
                holder.mButtonName.setType(SelectItem.TYPE_ICON);
                holder.mButtonName.setDataList(mIconList);
                break;
        }
        return holder;
    }

    @Override
    public void initView(DeviceAddDetailBaseAdapter.ItemHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        final ItemBean itemBean = (ItemBean) mDataList.get(position);
        final int itemType = itemBean.mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                break;
            case TYPE_SECTION:
                initSection(itemHolder, itemBean, position);
                break;
            case TYPE_LOOP:
                initButtonName(itemHolder.mButtonName, itemBean);
                break;
        }
    }

    protected void initButtonName(final SelectItem buttonName, final ItemBean itemBean) {
        buttonName.setContent(itemBean.mScenarioDeviceIrUIItem.cellTitle);
        buttonName.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                itemBean.mScenarioDeviceIrUIItem.iconItem = (MenuDeviceIRIconItem) item.mData;
                itemBean.mScenarioDeviceIrUIItem.cellTitle = item.mText;
            }
        });
    }

    protected void initSection(ItemHolder itemHolder, ItemBean itemBean, final int position) {
        itemHolder.mSection.setText(itemBean.mSection);
        itemHolder.mDelete.setVisibility(mDeleteMode ? View.VISIBLE : View.GONE);
        itemHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataList.remove(position);
                mDataList.remove(position);
                initDateHead();
                notifyDataSetChanged();
            }
        });
    }


    public void setDeleteMode(boolean deleteMode) {
        mDeleteMode = deleteMode;
        notifyDataSetChanged();
    }

    public void addItem() {
        ScenarioDeviceIrUIItem lastItem = null;
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            lastItem = ((ItemBean) mDataList.get(size - 1)).mScenarioDeviceIrUIItem;
        }
        ScenarioDeviceIrUIItem loop = ScenarioController.getScenarioIRNextItem(mContext, lastItem);
        if (loop != null) {
            mDataList.add(new ItemBean(TYPE_SECTION, loop.sectionTitle, null));
            mDataList.add(new ItemBean(TYPE_LOOP, "", loop));
        }
        initDateHead();
        notifyDataSetChanged();
    }

    protected void initHeaderData() {

    }

    public ArrayList<ScenarioDeviceIrUIItem> getScenarioDeviceIrUIItems() {
        ArrayList<ScenarioDeviceIrUIItem> result = new ArrayList<>();
        if (mDataList != null) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final ItemBean item = (ItemBean) mDataList.get(i);
                if (item.mItemType == TYPE_LOOP && item.mScenarioDeviceIrUIItem.iconItem != null) {
                    result.add(item.mScenarioDeviceIrUIItem);
                }
            }
        }
        return result;
    }

    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public SelectItem mButtonName;
        public ImageView mDelete;

        public ItemHolder(View view) {
            super(view);
            mButtonName = (SelectItem) view.findViewById(R.id.si_name);
            mDelete = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        ScenarioDeviceIrUIItem mScenarioDeviceIrUIItem;

        public ItemBean(int itemType, String section, ScenarioDeviceIrUIItem scenarioDeviceIrUIItem) {
            super(itemType, section, null, null);
            mScenarioDeviceIrUIItem = scenarioDeviceIrUIItem;
        }
    }

}

