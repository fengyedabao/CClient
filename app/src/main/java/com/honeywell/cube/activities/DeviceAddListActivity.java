package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.SparkLightingTypeAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Constants;

import java.util.ArrayList;

public class DeviceAddListActivity extends CubeTitleBarActivity {
    ListView mContent;
    SparkLightingTypeAdapter mAdapter;

    @Override
    protected int getContent() {
        return R.layout.activity_list;
    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (ListView) findViewById(R.id.lv_list);
        mAdapter = new SparkLightingTypeAdapter(this, null, getLoadingDialog());
        mContent.setAdapter(mAdapter);
        mContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeviceAddListActivity.this, DeviceAddDetailActivity.class);
                intent.putExtra(Constants.TITLE, mAdapter.getDataList().get(position).sparkTitle);
                intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_SPARKLIGHTING);
                intent.putExtra(Constants.SPARK_LOOP, mAdapter.getDataList().get(position).sparkLoop);
                intent.putExtra(Constants.SPARK_TITLE, mAdapter.getDataList().get(position).sparkTitle);
                intent.putExtra(Constants.SPARK_TYPE, mAdapter.getDataList().get(position).sparkType);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void getData() {
        super.getData();
        ArrayList<MenuDeviceUIItem> arrayList = MenuDeviceController.getSparkLightingList();
        mAdapter.setDataList(arrayList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
