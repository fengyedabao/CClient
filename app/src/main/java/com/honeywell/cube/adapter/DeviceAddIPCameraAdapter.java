
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.dialogs.BottomDialog;

import java.util.ArrayList;

public class DeviceAddIPCameraAdapter extends DeviceAddDetailBaseAdapter {


    public DeviceAddIPCameraAdapter(Context context, MenuDeviceUIItem item, Dialog dialog) {
        super(context, item, dialog);
    }

    @Override
    int getHeaderLayout() {
        return -1;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_loop_ip_camera;
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
                holder.mName.setTextName(R.string.name);
                holder.mRoom.setName(R.string.room);
                holder.mIpAddr.setTextName(R.string.ip_addr);
                holder.mIpAddr.setHint(R.string.ip_addr_hint);
                holder.mType.setName(R.string.type);
                holder.mUsername.setTextName(R.string.username);
                holder.mUsername.setHint(R.string.ipc_username_hint);
                holder.mPassword.setTextName(R.string.password);
                holder.mPassword.setHint(R.string.ipc_password_hint);
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
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initName(itemHolder.mName, itemBean);
                initIpAddr(itemHolder.mIpAddr, itemBean);
                initType(itemHolder.mType, itemBean);
                initUsername(itemHolder.mUsername, itemBean);
                initPassword(itemHolder.mPassword, itemBean);
                break;
        }
    }

    protected void initRoom(final SelectItem room, final ItemBean itemBean) {
        room.setContent(itemBean.mItem.IPC_Room);
        DeviceHelper.initRoom(room);
        room.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                itemBean.mItem.IPC_Room = item.mText;
                itemBean.mItem.IPC_Roomid = (int) item.mData;
            }
        });
    }

    protected void initName(EditTextItem name, final ItemBean itemBean) {
        name.setOnEditTextChangedListener(null);
        name.getEditName().setText(itemBean.mItem.IPC_Name);
        name.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                itemBean.mItem.IPC_Name = s;
            }
        });
    }

    protected void initIpAddr(final EditTextItem ipAddrItem, final ItemBean itemBean) {
        ipAddrItem.setOnEditTextChangedListener(null);
        ipAddrItem.getEditName().setText(itemBean.mItem.IPC_Ip);
        ipAddrItem.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                itemBean.mItem.IPC_Ip = s;
            }
        });
    }

    protected void initType(final SelectItem typeItem, final ItemBean itemBean) {
        typeItem.setContent(itemBean.mItem.IPC_type);
        typeItem.setDataList(DeviceHelper.getIpcTypeList(mContext));
        typeItem.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                typeItem.setContent(position);
                itemBean.mItem.IPC_type = typeItem.getDataList().get(position).mText;
            }
        });
    }

    protected void initUsername(final EditTextItem usernameItem, final ItemBean itemBean) {
        usernameItem.setOnEditTextChangedListener(null);
        usernameItem.getEditName().setText(itemBean.mItem.IPC_User);
        usernameItem.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                itemBean.mItem.IPC_User = s;
            }
        });
    }

    protected void initPassword(final EditTextItem passwordItem, final ItemBean itemBean) {
        passwordItem.setOnEditTextChangedListener(null);
        passwordItem.getEditName().setText(itemBean.mItem.IPC_Password);
        passwordItem.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                itemBean.mItem.IPC_Password = s;
            }
        });
    }

    @Override
    protected void initDataList() {
        mDataList = new ArrayList<>();
        if (mMenuDeviceUIItem != null) {
            mDataList.add(new ItemBean(TYPE_LOOP, mMenuDeviceUIItem));
        }

    }

    @Override
    public MenuDeviceUIItem getData() {
        if (mMenuDeviceUIItem != null) {
            return ((ItemBean) mDataList.get(0)).mItem;
        }
        return mMenuDeviceUIItem;
    }

    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public EditTextItem mIpAddr;
        public SelectItem mType;
        public EditTextItem mUsername;
        public EditTextItem mPassword;

        public ItemHolder(View view) {
            super(view);
            mIpAddr = (EditTextItem) view.findViewById(R.id.ei_ip_addr);
            mType = (SelectItem) view.findViewById(R.id.si_type);
            mUsername = (EditTextItem) view.findViewById(R.id.ei_user_name);
            mPassword = (EditTextItem) view.findViewById(R.id.ei_password);

        }
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        MenuDeviceUIItem mItem;

        public ItemBean(int itemType, MenuDeviceUIItem item) {
            super(itemType, "", null, null);
            mItem = item;
        }
    }

}
