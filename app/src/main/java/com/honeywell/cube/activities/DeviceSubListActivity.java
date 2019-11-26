package com.honeywell.cube.activities;

import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.DeviceSubListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import java.util.ArrayList;
import java.util.List;

public class DeviceSubListActivity extends CubeTitleBarActivity {
    ListView mListView;
    String mItemType;
    String mItemTitle;
    DeviceSubListAdapter mAdapter;

    protected void initView() {
        mListView = (ListView) findViewById(R.id.lv_list);
        mAdapter = new DeviceSubListAdapter(this, getDataList(null), getLoadingDialog());
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        Intent intent = getIntent();
        mItemType = intent.getStringExtra(Constants.DEVICE_TYPE);
        mItemTitle = getString(Constants.DEVICE_TYPE_MAP.get(mItemType));
//        Log.e("alinmi", "mItemType = " + mItemType + " , mItemTitle = " + mItemTitle);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_list;
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(mItemTitle);
    }

    @Override
    protected void getData() {
        super.getData();
        MenuDeviceController.getDeviceListWithType(this, mItemType);
    }

    public List<DeviceSubListAdapter.ItemBean> getDataList(CubeDeviceEvent event) {
        List<DeviceSubListAdapter.ItemBean> dataList = new ArrayList<>();
        if (event != null) {
            ArrayList<MenuDeviceUIItem> arrayList = (ArrayList<MenuDeviceUIItem>) event.getUpdateStatusData();
            if (arrayList != null && arrayList.size() > 0) {
                final int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    final MenuDeviceUIItem item = arrayList.get(i);
                    dataList.add(new DeviceSubListAdapter.ItemBean(-1, null, item.deviceName, item.object, item.deviceType));
                }
            }
        }
        return dataList;
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            if (ev.getType() == CubeEvents.CubeDeviceEventType.MENU_GET_DEVICE_WITH_TYPE) {
                mAdapter.setDataList(getDataList(ev));
                mAdapter.notifyDataSetChanged();
            } else if (ev.getType() == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE) {
                dismissLoadingDialog();
                if (ev.getSuccess()) {
                    mAdapter.updateDeleteUI();
                } else {
                    showToastShort(ev.getMessage());
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("alinmi222", "onActivityResult data = " + data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
                getData();
            }
        }
    }
}
