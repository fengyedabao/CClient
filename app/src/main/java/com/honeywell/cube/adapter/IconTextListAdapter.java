
package com.honeywell.cube.adapter;



import com.honeywell.cube.R;

import java.util.List;


public class IconTextListAdapter extends IconTextBaseAdapter {
    public IconTextListAdapter(List<ItemBean> list){
        super(list);
    }
    @Override
    public int getItemLayout() {
        return R.layout.list_icon_text;
    }

}
