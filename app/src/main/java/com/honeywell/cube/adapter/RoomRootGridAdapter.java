package com.honeywell.cube.adapter;

import com.honeywell.cube.R;

import java.util.List;

public class RoomRootGridAdapter extends IconTextBaseAdapter {


    public RoomRootGridAdapter(List<ItemBean> list) {
        super(list);
    }

    @Override
    public int getItemLayout() {
        return R.layout.grid_main_room;
    }
}
