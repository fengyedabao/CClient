package com.honeywell.cube.adapter;

import android.content.Context;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.ViewHolder;
import com.honeywell.cube.controllers.UIItem.menu.MenuCubeSettingBackup;

import java.util.List;

/**
 * Created by zhujunyu on 16/7/5.
 */
public class CudeBackUpHistoryAdapter extends CommonAdapter<MenuCubeSettingBackup> {
    public CudeBackUpHistoryAdapter(Context context, List mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }


    @Override
    public void convert(ViewHolder helper, MenuCubeSettingBackup item) {
        TextView backUpName = helper.getView(R.id.tv_backup_name);
        TextView backUpTime = helper.getView(R.id.tv_backup_time);

        backUpName.setText(item.description);
        backUpTime.setText(item.timestamp);
    }
}
