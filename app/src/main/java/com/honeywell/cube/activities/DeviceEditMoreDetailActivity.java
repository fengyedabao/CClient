package com.honeywell.cube.activities;

import android.widget.GridView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.DeviceDetailGridAdapter;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.menus.MenuDeviceController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceEditMoreDetailActivity extends CubeTitleBarActivity {
    Object mObject;

    @Override
    protected int getContent() {
        return R.layout.activity_device_edit_more_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        GridView content = (GridView) findViewById(R.id.gv_content);

        content.setAdapter(new DeviceDetailGridAdapter(getDataList()));
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        mObject = getIntent().getParcelableExtra(DeviceEditActivity.DEVICE_LOOP);
        Map<String, Object> map = MenuDeviceController.getDeviceRoomAndNameInfo(this, mObject);
        title.setText((String) map.get(MenuDeviceController.CELL_NAME));
    }


    private List<DeviceDetailGridAdapter.ItemBean> getDataList() {
        List<DeviceDetailGridAdapter.ItemBean> dataList = new ArrayList<>();
        ArrayList<String> list = DeviceController.getLoopDetailInfo(this, mObject);
        if (list != null && list.size() > 0) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                dataList.add(new DeviceDetailGridAdapter.ItemBean(list.get(i)));
            }
        }
        return dataList;


    }
}
