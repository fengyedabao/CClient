package com.honeywell.cube.activities;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.DeviceACListAdapter;
import com.honeywell.cube.controllers.DeviceControllers.IRLoopController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrCode;

import java.util.ArrayList;
import java.util.List;

public class DeviceWIACActivity extends DeviceWIBaseActivity {
    ListView mContent = null;
    DeviceACListAdapter mAdapter;

    @Override
    protected int getContent() {
        return R.layout.activity_list;
    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (ListView) findViewById(R.id.lv_list);
        onTypeChanged();
    }

    @Override
    protected void onTypeChanged() {
        super.onTypeChanged();
        List<IrCode> list = IRLoopController.getIRCodeArray(this, mCurrentIrLoop);
        List<DeviceACListAdapter.ItemBean> result = new ArrayList<>();
        if (list != null) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                result.add(new DeviceACListAdapter.ItemBean(list.get(i).mName, list.get(i)));
            }
        }
        mAdapter = new DeviceACListAdapter(result);
        mContent.setAdapter(mAdapter);
        mContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        IRLoopController.sendIRAirController(DeviceWIACActivity.this, (IrCode) mAdapter.getItem(position));
                    }
                });

            }
        });
    }

}
