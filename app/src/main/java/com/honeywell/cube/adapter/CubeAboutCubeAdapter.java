package com.honeywell.cube.adapter;

import android.content.Context;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.ViewHolder;
import com.honeywell.cube.controllers.UIItem.menu.CubeTitleTextItem;

import java.util.List;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/5.
 */
public class CubeAboutCubeAdapter extends CommonAdapter<CubeTitleTextItem> {

    public CubeAboutCubeAdapter(Context context, List<CubeTitleTextItem> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, CubeTitleTextItem item) {

        TextView textViewCubeName = helper.getView(R.id.tv_cube_name);
        TextView textViewCubeDis = helper.getView(R.id.tv_cube_description);
        textViewCubeName.setText(item.itemTitle);
        textViewCubeDis.setText(item.itemText);
    }
}
