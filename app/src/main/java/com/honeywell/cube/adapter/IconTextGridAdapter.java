package com.honeywell.cube.adapter;

import com.honeywell.cube.R;

import java.util.List;

public class IconTextGridAdapter extends IconTextBaseAdapter {
    protected boolean bHasDivider = true;

    public IconTextGridAdapter(List<ItemBean> list, boolean hasDivider) {
        super(list);
        bHasDivider = hasDivider;
    }

    public IconTextGridAdapter(List<ItemBean> list) {
        super(list);
    }

    @Override
    public int getItemLayout() {
        return bHasDivider ? R.layout.grid_icon_text : R.layout.grid_icon_text_no_divider;
    }
}
