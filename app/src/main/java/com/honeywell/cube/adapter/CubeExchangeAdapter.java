package com.honeywell.cube.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.ViewHolder;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeDevice;

import java.util.List;

/**
 * Created by shushunsakai on 16/7/5.
 */
public class CubeExchangeAdapter extends CommonAdapter<MenuDeviceUIItem> {
    public CubeExchangeAdapter(Context context, List<MenuDeviceUIItem> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, MenuDeviceUIItem item) {

        TextView textViewCubeName = helper.getView(R.id.tv_cube_name);
        TextView textViewCubeDis = helper.getView(R.id.tv_cube_description);
        ImageView imageView = helper.getView(R.id.iv_done);
        CubeDevice cd = (CubeDevice) item.object;
        textViewCubeName.setText(cd.mInfo_aliasName);

        textViewCubeDis.setText(cd.mInfo_serialNumber);
        if (item.select) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}
