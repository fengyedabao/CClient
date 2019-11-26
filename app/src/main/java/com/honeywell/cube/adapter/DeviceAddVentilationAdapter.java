
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceVentilationObject;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.dialogs.BottomDialog;

import java.util.ArrayList;

public class DeviceAddVentilationAdapter extends DeviceAddDetailBaseAdapter {

    MenuDeviceVentilationObject mMenuDeviceVentilationObject;
    ArrayList<BottomDialog.ItemBean> mRelayList;

    public DeviceAddVentilationAdapter(Context context, MenuDeviceVentilationObject item, Dialog dialog) {
        super(context, null, dialog);
        mMenuDeviceVentilationObject = item;
        initRelayList();
        initDataList();
        initDateHead();

    }

    private void initRelayList() {
        mRelayList = new ArrayList<>();
        ArrayList<RelayLoop> loops = MenuDeviceController.getVentilationRelayLoop(mContext);
        if (loops != null) {
            final int size = loops.size();
            for (int i = 0; i < size; i++) {
                RelayLoop loop = loops.get(i);
                mRelayList.add(new BottomDialog.ItemBean(loop.mLoopName, loop));
            }
        }

    }

    protected void initDataList() {
        mDataList = new ArrayList<>();
        if (mMenuDeviceVentilationObject != null) {
            initHeaderData();
            mDataList.add(new ItemBean(TYPE_SECTION, mContext.getString(R.string.power)));
            mDataList.add(new ItemBean(TYPE_LOOP, mMenuDeviceVentilationObject.power));

            mDataList.add(new ItemBean(TYPE_SECTION, mContext.getString(R.string.loop)));
            mDataList.add(new ItemBean(TYPE_LOOP, mMenuDeviceVentilationObject.cycle));

            mDataList.add(new ItemBean(TYPE_SECTION, mContext.getString(R.string.wind_speed)));
            mDataList.add(new ItemBean(TYPE_LOOP, mMenuDeviceVentilationObject.fan_speed_high, mMenuDeviceVentilationObject.fan_speed_middle, mMenuDeviceVentilationObject.fan_speed_low));

            mDataList.add(new ItemBean(TYPE_SECTION, mContext.getString(R.string.mode)));
            mDataList.add(new ItemBean(TYPE_LOOP, mMenuDeviceVentilationObject.mode_humidity, mMenuDeviceVentilationObject.mode_dehumidity));
        }
    }

    protected void initHeaderData() {
        mDataList.add(new ItemBean(TYPE_HEADER, mMenuDeviceVentilationObject.name, mMenuDeviceVentilationObject.room, mMenuDeviceVentilationObject.roomId));
    }

    @Override
    int getHeaderLayout() {
        return R.layout.list_device_add_header_ventilation;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_device_add_ventilation;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        final int itemType = mDataList.get(position).mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                holder.mName.setTextName(R.string.name);
                holder.mRoom.setName(R.string.room);

                break;
            case TYPE_SECTION:
                break;
            case TYPE_LOOP:
                if (position == 2) {
                    holder.mFirst.setDataList(mRelayList);
                } else if (position == 4) {
                    holder.mFirst.setDataList(mRelayList);
                } else if (position == 6) {
                    holder.mFirst.setDataList(mRelayList);
                    holder.mSecond.setDataList(mRelayList);
                    holder.mThird.setDataList(mRelayList);
                } else if (position == 8) {
                    holder.mFirst.setDataList(mRelayList);
                    holder.mSecond.setDataList(mRelayList);
                }
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
                itemHolder.mName.setOnEditTextChangedListener(null);
                itemHolder.mName.setEditName(itemBean.mName);
                itemHolder.mName.setOnEditTextChangedListener(new EditTextItem.OnEditTextChangedListener() {
                    @Override
                    public void afterTextChanged(String s) {
                        itemBean.mName = s;
                    }
                });

                DeviceHelper.initRoom(itemHolder.mRoom);
                itemHolder.mRoom.setContent(itemBean.mRoom);
                itemHolder.mRoom.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
                    @Override
                    public void contentChanged(BottomDialog.ItemBean item) {
                        itemBean.mRoom = item.mText;
                        itemBean.mRoomId = (int) item.mData;
                    }
                });

                break;
            case TYPE_SECTION:
                itemHolder.mSection.setText(itemBean.mSection);
                break;
            case TYPE_LOOP:
                if (position == 2) {
                    itemHolder.setSingleLine();
                    itemHolder.mFirst.setName(R.string.on_off);
                } else if (position == 4) {
                    itemHolder.setSingleLine();
                    itemHolder.mFirst.setName(R.string.inside_outside);
                } else if (position == 6) {
                    itemHolder.setThreeLines();
                    itemHolder.mFirst.setName(R.string.high);
                    itemHolder.mSecond.setName(R.string.middle);
                    itemHolder.mSecond.setContent(itemBean.mSecond.mLoopName);
                    itemHolder.mSecond.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
                        @Override
                        public void contentChanged(BottomDialog.ItemBean item) {
                            itemBean.mSecond = (RelayLoop) item.mData;
                        }
                    });
                    itemHolder.mThird.setName(R.string.low);
                    itemHolder.mThird.setContent(itemBean.mThird.mLoopName);
                    itemHolder.mThird.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
                        @Override
                        public void contentChanged(BottomDialog.ItemBean item) {
                            itemBean.mThird = (RelayLoop) item.mData;
                        }
                    });
                } else if (position == 8) {
                    itemHolder.setTwoLines();
                    itemHolder.mFirst.setName(R.string.humidity);
                    itemHolder.mSecond.setName(R.string.dehumidity);
                    itemHolder.mSecond.setContent(itemBean.mSecond.mLoopName);
                    itemHolder.mSecond.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
                        @Override
                        public void contentChanged(BottomDialog.ItemBean item) {
                            itemBean.mSecond = (RelayLoop) item.mData;
                        }
                    });
                }
                itemHolder.mFirst.setContent(itemBean.mFirst.mLoopName);
                itemHolder.mFirst.setOnContentChangedListener(new SelectItem.OnContentChangedListener() {
                    @Override
                    public void contentChanged(BottomDialog.ItemBean item) {
                        itemBean.mFirst = (RelayLoop) item.mData;
                    }
                });
                break;
        }
    }


    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public SelectItem mFirst;
        public SelectItem mSecond;
        public SelectItem mThird;
        public View mDividerSecond;
        public View mDividerThird;

        public ItemHolder(View view) {
            super(view);
            mFirst = (SelectItem) view.findViewById(R.id.si_first);
            mSecond = (SelectItem) view.findViewById(R.id.si_third);
            mThird = (SelectItem) view.findViewById(R.id.si_second);
            mDividerSecond = view.findViewById(R.id.divider_second);
            mDividerThird = view.findViewById(R.id.divider_third);
        }

        public void setSingleLine() {
            mSecond.setVisibility(View.GONE);
            mThird.setVisibility(View.GONE);
            mDividerSecond.setVisibility(View.GONE);
            mDividerThird.setVisibility(View.GONE);
        }

        public void setTwoLines() {
            mSecond.setVisibility(View.VISIBLE);
            mThird.setVisibility(View.GONE);
            mDividerSecond.setVisibility(View.VISIBLE);
            mDividerThird.setVisibility(View.GONE);

        }

        public void setThreeLines() {
            mSecond.setVisibility(View.VISIBLE);
            mThird.setVisibility(View.VISIBLE);
            mDividerSecond.setVisibility(View.VISIBLE);
            mDividerThird.setVisibility(View.VISIBLE);
        }
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        RelayLoop mFirst;
        RelayLoop mSecond;
        RelayLoop mThird;
        String mRoom = "";
        String mName = "";
        int mRoomId = -1;

        public ItemBean(int itemType, String name, String room, int roomId) {
            this(itemType, "");
            mName = name;
            mRoom = room;
            mRoomId = roomId;
        }

        public ItemBean(int itemType, String section) {
            this(itemType, section, null, null, null);
        }

        public ItemBean(int itemType, RelayLoop first) {
            this(itemType, first, null, null);
        }

        public ItemBean(int itemType, RelayLoop first, RelayLoop second) {
            this(itemType, first, second, null);
        }

        public ItemBean(int itemType, RelayLoop first, RelayLoop second, RelayLoop third) {
            this(itemType, "", first, second, third);
        }

        public ItemBean(int itemType, String section, RelayLoop first, RelayLoop second, RelayLoop third) {
            super(itemType, section, null, null);
            mFirst = first;
            mSecond = second;
            mThird = third;
        }

    }

    public MenuDeviceVentilationObject getVentilationData() {
        if (mMenuDeviceVentilationObject != null) {
            mMenuDeviceVentilationObject.room = ((ItemBean) mDataList.get(0)).mRoom;
            mMenuDeviceVentilationObject.roomId = ((ItemBean) mDataList.get(0)).mRoomId;
            mMenuDeviceVentilationObject.name = ((ItemBean) mDataList.get(0)).mName;

            mMenuDeviceVentilationObject.power = ((ItemBean) mDataList.get(2)).mFirst;

            mMenuDeviceVentilationObject.cycle = ((ItemBean) mDataList.get(4)).mFirst;

            mMenuDeviceVentilationObject.fan_speed_high = ((ItemBean) mDataList.get(6)).mFirst;
            mMenuDeviceVentilationObject.fan_speed_middle = ((ItemBean) mDataList.get(6)).mSecond;
            mMenuDeviceVentilationObject.fan_speed_low = ((ItemBean) mDataList.get(6)).mThird;

            mMenuDeviceVentilationObject.mode_humidity = ((ItemBean) mDataList.get(8)).mFirst;
            mMenuDeviceVentilationObject.mode_dehumidity = ((ItemBean) mDataList.get(8)).mSecond;
        }
        return mMenuDeviceVentilationObject;
    }


}
