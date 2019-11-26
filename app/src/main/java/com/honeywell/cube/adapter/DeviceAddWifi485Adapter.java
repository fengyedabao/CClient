
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceLoopObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;

public class DeviceAddWifi485Adapter extends DeviceAddBacnetAdapter {


    public DeviceAddWifi485Adapter(Context context, MenuDeviceUIItem item, Dialog dialog) {
        super(context, item, dialog);
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_loop_wifi_485;
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
                holder.mLoopType.setName(R.string.loop_type);
                holder.mBrandName.setName(R.string.brand_name);
                holder.mPort.setName(R.string.port);
                holder.mSubIpAddr.setTextName(R.string.sub_ip_addr);
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
                initHeader(itemHolder.mHeader, itemBean, ModelEnum.MODULE_TYPE_WIFI485);
                break;
            case TYPE_SECTION:
                initSection(itemHolder, itemBean, position);
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initName(itemHolder.mName, itemBean);
                initLoopId(itemHolder.mLoopId, itemBean);
                initLoopType(itemHolder.mLoopType, itemBean);
                initBrandName(itemHolder.mBrandName, itemBean);
                initPort(itemHolder.mPort, itemBean);
                initSubIpAddr(itemHolder.mSubIpAddr, itemBean);
                break;
        }
    }

    protected void initLoopId(final EditTextItem loopIdItem, final ItemBean itemBean) {
        loopIdItem.setOnEditTextChangedListener(null);
        loopIdItem.getEditName().setText(itemBean.mMenuDeviceLoopObject.wifi485_loop_id == 0 ? "" : "" + itemBean.mMenuDeviceLoopObject.wifi485_loop_id);
        loopIdItem.getEditName().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        loopIdItem.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                if (TextUtils.isEmpty(s)) {
                    itemBean.mMenuDeviceLoopObject.wifi485_loop_id = 0;
                } else {
                    itemBean.mMenuDeviceLoopObject.wifi485_loop_id = Integer.parseInt(s);
                }
            }
        });
    }

    protected void initSubIpAddr(final EditTextItem subIpAddrItem, final ItemBean itemBean) {
        subIpAddrItem.setOnEditTextChangedListener(null);
        subIpAddrItem.getEditName().setText(itemBean.mMenuDeviceLoopObject.wifi485_slave_address);
        subIpAddrItem.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                itemBean.mMenuDeviceLoopObject.wifi485_slave_address = s;
            }
        });
    }

    protected void initLoopType(final SelectItem loopTypeItem, final ItemBean itemBean) {
        loopTypeItem.setContent(itemBean.mMenuDeviceLoopObject.loopType);
        loopTypeItem.setDataList(DeviceHelper.getLoopTypeList(mContext));
        loopTypeItem.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                loopTypeItem.setContent(position);
                itemBean.mMenuDeviceLoopObject.loopType = loopTypeItem.getDataList().get(position).mText;
            }
        });
    }

    protected void initBrandName(final SelectItem brandNameItem, final ItemBean itemBean) {
        brandNameItem.setContent(itemBean.mMenuDeviceLoopObject.wifi485_branchName);
        brandNameItem.setDataList(DeviceHelper.getBrandNameList(mContext));
        brandNameItem.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                brandNameItem.setContent(position);
                itemBean.mMenuDeviceLoopObject.wifi485_branchName = brandNameItem.getDataList().get(position).mText;
            }
        });
    }

    protected void initPort(final SelectItem portItem, final ItemBean itemBean) {
        portItem.setContent(itemBean.mMenuDeviceLoopObject.wifi485_port_id + "");
        portItem.setDataList(DeviceHelper.getPortList(mContext));
        portItem.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                portItem.setContent(position);
                itemBean.mMenuDeviceLoopObject.wifi485_port_id = Integer.valueOf(portItem.getDataList().get(position).mText);
            }
        });
    }

    @Override
    public void addItem() {
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            MenuDeviceLoopObject loop = MenuDeviceController.getDefaultWifi485LoopObject(mContext, mDataList.get(size - 1).mMenuDeviceLoopObject);
            if (loop != null) {
                mDataList.add(new ItemBean(TYPE_SECTION, loop.section, null, null));
                mDataList.add(new ItemBean(TYPE_LOOP, "", loop, null));
            }
        }
        initDateHead();
        notifyDataSetChanged();
    }

    protected static class ItemHolder extends DeviceAddBacnetAdapter.ItemHolder {

        public SelectItem mLoopType;
        public SelectItem mBrandName;
        public SelectItem mPort;
        public EditTextItem mSubIpAddr;

        public ItemHolder(View view) {
            super(view);

            mLoopType = (SelectItem) view.findViewById(R.id.si_loop_type);
            mBrandName = (SelectItem) view.findViewById(R.id.si_brand_type);
            mPort = (SelectItem) view.findViewById(R.id.si_port);
            mSubIpAddr = (EditTextItem) view.findViewById(R.id.ei_sub_ip_addr);
        }
    }


}
