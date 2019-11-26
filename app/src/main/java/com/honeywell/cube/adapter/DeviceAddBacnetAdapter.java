
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceLoopObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.widgets.EditTextItem;

public class DeviceAddBacnetAdapter extends DeviceAddDetailBaseAdapter {

    private boolean mDeleteMode = false;

    public DeviceAddBacnetAdapter(Context context, MenuDeviceUIItem item, Dialog dialog) {
        super(context, item, dialog);
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_device_add_header;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_loop_bacnet;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                holder.mHeader.setName(R.string.module_bacnet);
                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mName.setTextName(R.string.name);
                holder.mRoom.setName(R.string.room);
                holder.mLoopId.setTextName(R.string.loop_id);
                holder.mLoopId.setHint(R.string.loop_id_hint);
                break;
        }
        return holder;
    }

    @Override
    public void initView(DeviceAddDetailBaseAdapter.ItemHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        final ItemBean itemBean = mDataList.get(position);
        final int itemType = itemBean.mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                initHeader(itemHolder.mHeader, itemBean, ModelEnum.MODULE_TYPE_BACNET);
                break;
            case TYPE_SECTION:
                initSection(itemHolder, itemBean, position);
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initName(itemHolder.mName, itemBean);
                initLoopId(itemHolder.mLoopId, itemBean);
                break;
        }
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


    protected void initLoopId(final EditTextItem loopIdItem, final ItemBean itemBean) {
        loopIdItem.setOnEditTextChangedListener(null);
        loopIdItem.getEditName().setText(itemBean.mMenuDeviceLoopObject.bacnet_loop_id == 0 ? "" : "" + itemBean.mMenuDeviceLoopObject.bacnet_loop_id);
        loopIdItem.getEditName().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        loopIdItem.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                if (TextUtils.isEmpty(s)) {
                    itemBean.mMenuDeviceLoopObject.bacnet_loop_id = 0;
                } else {
                    itemBean.mMenuDeviceLoopObject.bacnet_loop_id = Integer.parseInt(s);
                }
            }
        });
    }


    public void setDeleteMode(boolean deleteMode) {
        mDeleteMode = deleteMode;
        notifyDataSetChanged();
    }

    public void addItem() {
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            MenuDeviceLoopObject loop = MenuDeviceController.getDefaultBacnetLoopObject(mContext, mDataList.get(size - 1).mMenuDeviceLoopObject);
            if (loop != null) {
                mDataList.add(new DeviceAddDetailBaseAdapter.ItemBean(TYPE_SECTION, loop.section, null, null));
                mDataList.add(new DeviceAddDetailBaseAdapter.ItemBean(TYPE_LOOP, "", loop, null));
            }
        }
        initDateHead();
        notifyDataSetChanged();
    }

    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public EditTextItem mLoopId;
        public ImageView mDelete;

        public ItemHolder(View view) {
            super(view);
            mLoopId = (EditTextItem) view.findViewById(R.id.ei_loop_id);
            mDelete = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }


}
