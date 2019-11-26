package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.IconTextBaseAdapter;
import com.honeywell.cube.adapter.IconTextGridAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;

import java.util.ArrayList;
import java.util.List;

public class DeviceAddWirelessInfraredActivity extends CubeTitleBarActivity {
    GridView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_grid;
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.configure);
    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (GridView) findViewById(R.id.gv_content);
        mContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeviceAddWirelessInfraredActivity.this, DeviceAddWirelessInfraredFirstActivity.class);
                final IconTextGridAdapter adapter = (IconTextGridAdapter) mContent.getAdapter();
                intent.putExtra(Constants.TYPE, ModelEnum.IR_TYPE_MAP.get(((IconTextBaseAdapter.ItemBean) adapter.getItem(position)).getType()));
                intent.putExtra(Constants.TITLE, ((IconTextBaseAdapter.ItemBean) adapter.getItem(position)).getText());
                DeviceHelper.addObject2Intent(intent, Constants.CONTENT, ((IconTextBaseAdapter.ItemBean) adapter.getItem(position)).mObject);
                startActivity(intent);
            }
        });

    }

    @Override
    public void getData() {
        super.getData();
        ArrayList<MenuDeviceIRUIItem> dataList = MenuDeviceController.getMenuIrList(this);
        if (dataList != null) {
            mContent.setAdapter(new IconTextGridAdapter(getDataList(dataList)));
        }
    }

    public List<IconTextBaseAdapter.ItemBean> getDataList(ArrayList<MenuDeviceIRUIItem> list) {

        List<IconTextBaseAdapter.ItemBean> dataList = new ArrayList<>();

        if (list != null && list.size() > 0) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                final MenuDeviceIRUIItem data = list.get(i);
                dataList.add(new IconTextBaseAdapter.ItemBean(data.IR_image, null, data.IR_name, data.IR_type, data));
            }
        }
        return dataList;
    }
}
