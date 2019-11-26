
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuAddModuleIpvdpListObj;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.ViewUtil;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.cube.widgets.SwitchItem;
import com.honeywell.lib.dialogs.BottomDialog;

import java.util.ArrayList;

public class ModuleAddIPVDPAdapter extends DeviceAddDetailBaseAdapter {

    MenuModuleUIItem mMenuModuleUIItem;

    public ModuleAddIPVDPAdapter(Context context, MenuModuleUIItem item, Dialog dialog) {
        super(context, null, dialog);
        mMenuModuleUIItem = item;
        initDataList();
        initDateHead();

    }

    protected void initDataList() {
        mDataList = new ArrayList<>();
        if (mMenuModuleUIItem != null) {
            initHeaderData();
            final ArrayList<MenuAddModuleIpvdpListObj> list = mMenuModuleUIItem.ipvdpListObjs;
            if (list != null && list.size() > 0) {
                final int size = list.size();
                for (int i = 0; i < size; i++) {
                    final MenuAddModuleIpvdpListObj loop = list.get(i);

                    mDataList.add(new ItemBean(TYPE_SECTION, loop.sectionName, null));
                    mDataList.add(new ItemBean(TYPE_LOOP, "", loop));
                }
            }
        }
    }

    protected void initHeaderData() {
        mDataList.add(new ItemBean(TYPE_HEADER, "", null));
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_module_add_ipvdp_header;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_module_add_ipvdp;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:

                holder.mName.setTextName(R.string.name);

                holder.mEditIpAddr.setTextName(R.string.ip_addr);
                ViewUtil.initIPAddrEditItem(holder.mEditIpAddr);

                holder.mEditHNSIp.setTextName(R.string.hns_service_ip);
                ViewUtil.initIPAddrEditItem(holder.mEditHNSIp);

                holder.mRoom.setName(R.string.room);

                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mRoom.setName(R.string.room);
                holder.mUsing.setTextName(R.string.using);
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
                initHeader(itemHolder);
                break;
            case TYPE_SECTION:
                itemHolder.mSection.setText(itemBean.mSection);
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initUsing(itemHolder.mUsing, itemBean);
                break;
        }
    }


    protected void initHeader(ItemHolder itemHolder) {

        itemHolder.mName.setOnEditTextChangedListener(null);
        itemHolder.mName.setEditName(mMenuModuleUIItem.title);
        itemHolder.mName.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                mMenuModuleUIItem.title = s;
            }
        });

        itemHolder.mEditIpAddr.setOnEditTextChangedListener(null);
        itemHolder.mEditIpAddr.setEditName(mMenuModuleUIItem.ipAddr);
        itemHolder.mEditIpAddr.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                mMenuModuleUIItem.ipAddr = s;
            }
        });

        itemHolder.mEditHNSIp.setOnEditTextChangedListener(null);
        itemHolder.mEditHNSIp.setEditName(mMenuModuleUIItem.hns_ip);
        itemHolder.mEditHNSIp.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                mMenuModuleUIItem.hns_ip = s;
            }
        });

        DeviceHelper.initRoom(itemHolder.mRoom);
        itemHolder.mRoom.setContent(mMenuModuleUIItem.ipvdp_roomName);
        itemHolder.mRoom.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                mMenuModuleUIItem.ipvdp_roomName = item.mText;
                mMenuModuleUIItem.ipvdp_roomId = (int) item.mData;
            }
        });
    }

    protected void initRoom(final SelectItem room, final ItemBean itemBean) {
        DeviceHelper.initRoom(room);
        room.setContent(itemBean.mMenuAddModuleIpvdpListObj.roomName);
        room.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                itemBean.mMenuAddModuleIpvdpListObj.roomName = item.mText;
                itemBean.mMenuAddModuleIpvdpListObj.roomId = (int) item.mData;
            }
        });
    }

    protected void initUsing(final SwitchItem using, final ItemBean itemBean) {
        using.setChecked(itemBean.mMenuAddModuleIpvdpListObj.isEnable);
        using.setCheckBoxClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemBean.mMenuAddModuleIpvdpListObj.isEnable = ((CheckBox) v).isChecked();
            }
        });
    }

    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public EditTextItem mEditIpAddr;
        public EditTextItem mEditHNSIp;

        public ItemHolder(View view) {
            super(view);
            mEditIpAddr = (EditTextItem) view.findViewById(R.id.ei_ip_addr);
            mEditHNSIp = (EditTextItem) view.findViewById(R.id.ei_hns_id);
        }
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        //        MenuModuleUIItem mMenuModuleUIItem;
        MenuAddModuleIpvdpListObj mMenuAddModuleIpvdpListObj;

        public ItemBean(int itemType, String section, MenuAddModuleIpvdpListObj menuAddModuleIpvdpListObj) {
            super(itemType, section, null, null);
//            mMenuModuleUIItem = menuModuleUIItem;
            mMenuAddModuleIpvdpListObj = menuAddModuleIpvdpListObj;
        }
    }

    public MenuModuleUIItem getModuleData() {
        if (mMenuModuleUIItem != null) {
//            getHeaderData(mMenuDeviceUIItem);
            ArrayList<MenuAddModuleIpvdpListObj> loops = new ArrayList<>();
            if (mDataList != null) {
                final int size = mDataList.size();
                for (int i = 0; i < size; i++) {
                    final ItemBean itemBean = (ItemBean) mDataList.get(i);
                    if (itemBean.mItemType == TYPE_LOOP) {
                        loops.add(itemBean.mMenuAddModuleIpvdpListObj);
                    }

                }
            }
            mMenuModuleUIItem.ipvdpListObjs = loops;
        }
        return mMenuModuleUIItem;
    }

    public boolean isDeviceSelected() {
        if (mMenuModuleUIItem != null) {
            if (mDataList != null) {
                final int size = mDataList.size();
                for (int i = 0; i < size; i++) {
                    final ItemBean itemBean = (ItemBean) mDataList.get(i);
                    if (itemBean.mItemType == TYPE_LOOP) {
                        if (itemBean.mMenuAddModuleIpvdpListObj == null || itemBean.mMenuAddModuleIpvdpListObj.isEnable) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
