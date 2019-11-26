
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.ModuleListActivity;
import com.honeywell.cube.controllers.UIItem.ScanMaiaLoopObject;
import com.honeywell.cube.controllers.UIItem.ScanMaiaUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.cube.widgets.SwitchItem;
import com.honeywell.lib.dialogs.BottomDialog;

import java.util.ArrayList;

public class ScanResultMaiaAdapter extends DeviceAddDetailBaseAdapter {

    ScanMaiaUIItem mScanMaiaUIItem;

    public ScanResultMaiaAdapter(Context context, ScanMaiaUIItem item, Dialog dialog) {
        super(context, null, dialog);
        mScanMaiaUIItem = item;
        initDataList();
        initDateHead();
    }

    @Override
    protected void initDataList() {
        mDataList = new ArrayList<>();
        if (mScanMaiaUIItem != null) {
            initHeaderData();
            final ArrayList<ScanMaiaLoopObject> list = mScanMaiaUIItem.deviceloops;
            if (list != null && list.size() > 0) {
                final int size = list.size();
                for (int i = 0; i < size; i++) {
                    final ScanMaiaLoopObject loop = list.get(i);

                    mDataList.add(new ItemBean(TYPE_SECTION, loop.section, null, null, "", ""));
                    mDataList.add(new ItemBean(TYPE_LOOP, "", loop, null, "", ""));
                }
            }
        }
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_scan_result_maia_header;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_loop;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                holder.mMaiaType.setTextName(R.string.maia_type);
                holder.mMaiaType.getEditName().setClickable(false);
                holder.mMaiaId.setTextName(R.string.maia_id);
                holder.mMaiaId.getEditName().setClickable(false);
                holder.mHeader.setName(R.string.maia_module);
                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                holder.mName.setTextName(R.string.name);
                holder.mName.setHint(R.string.edit_scenario_name_hint);
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

                itemHolder.mMaiaType.setEditName(itemBean.mMaiaType);
                itemHolder.mMaiaId.setEditName(itemBean.mMaiaId);
                initHeader(itemHolder.mHeader, itemBean, ModelEnum.MODULE_TYPE_SPARKLIGHTING);
                break;
            case TYPE_SECTION:
                itemHolder.mSection.setText(itemBean.mSection);
                break;
            case TYPE_LOOP:
                initRoom(itemHolder.mRoom, itemBean);
                initName(itemHolder.mName, itemBean);
                initUsing(itemHolder.mUsing, itemBean);
                break;
        }
    }

    @Override
    protected void initHeader(final SelectItem header, final DeviceAddDetailBaseAdapter.ItemBean itemBean, int type) {
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

    protected void initRoom(final SelectItem room, final ItemBean itemBean) {
        DeviceHelper.initRoom(room);
        room.setContent(itemBean.mScanMaiaLoopObject.roomName);
        room.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
            @Override
            public void contentChanged(BottomDialog.ItemBean item) {
                itemBean.mScanMaiaLoopObject.roomName = item.mText;
                itemBean.mScanMaiaLoopObject.roomId = (int) item.mData;
            }
        });
    }

    protected void initName(final EditTextItem name, final ItemBean itemBean) {
        name.setOnEditTextChangedListener(null);
        name.getEditName().setText(itemBean.mScanMaiaLoopObject.name);
        name.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
            @Override
            public void afterTextChanged(String s) {
                itemBean.mScanMaiaLoopObject.name = s;
            }
        });
    }

    protected void initUsing(final SwitchItem using, final ItemBean itemBean) {
        using.setChecked(itemBean.mScanMaiaLoopObject.enable);
        using.setCheckBoxClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemBean.mScanMaiaLoopObject.enable = ((CheckBox) v).isChecked();
            }
        });
    }

    @Override
    protected void getHeaderData() {
        mScanMaiaUIItem.id = ((ItemBean) mDataList.get(0)).mMaiaId;
        mScanMaiaUIItem.type = ((ItemBean) mDataList.get(0)).mMaiaType;
        mScanMaiaUIItem.mainDevice = ((ItemBean) mDataList.get(0)).mDevice;
        mScanMaiaUIItem.mainDeviceName = mScanMaiaUIItem.mainDevice == null ? "" : mScanMaiaUIItem.mainDevice.mName;
    }

    public ScanMaiaUIItem getMaiaData() {
        if (mScanMaiaUIItem != null) {
            getHeaderData();
            ArrayList<ScanMaiaLoopObject> loops = new ArrayList<>();
            if (mDataList != null) {
                final int size = mDataList.size();
                for (int i = 0; i < size; i++) {
                    final ItemBean itemBean = (ItemBean) mDataList.get(i);
                    if (itemBean.mItemType == TYPE_LOOP) {
                        loops.add(itemBean.mScanMaiaLoopObject);
                    }

                }
            }
            mScanMaiaUIItem.deviceloops = loops;
        }
        return mScanMaiaUIItem;
    }

    @Override
    protected void initHeaderData() {
        mDataList.add(new ItemBean(TYPE_HEADER, "", null, mScanMaiaUIItem.mainDevice, mScanMaiaUIItem.type, mScanMaiaUIItem.id));
    }


    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public EditTextItem mMaiaType;
        public EditTextItem mMaiaId;

        public ItemHolder(View view) {
            super(view);
            mMaiaType = (EditTextItem) view.findViewById(R.id.ei_maia_type);
            mMaiaId = (EditTextItem) view.findViewById(R.id.ei_maia_id);
        }
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        ScanMaiaLoopObject mScanMaiaLoopObject;
        String mMaiaType;
        String mMaiaId;

        public ItemBean(int itemType, String section, ScanMaiaLoopObject scanMaiaLoopObject, PeripheralDevice device, String maiaType, String maiaId) {
            super(itemType, section, null, device);
            mScanMaiaLoopObject = scanMaiaLoopObject;
            mMaiaType = maiaType;
            mMaiaId = maiaId;
        }
    }

}
