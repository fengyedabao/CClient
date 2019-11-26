package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.DeviceAddDetailBaseAdapter;
import com.honeywell.cube.adapter.SelectDeviceAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.controllers.menus.MenuRuleController;
import com.honeywell.cube.controllers.menus.MenuScheduleController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;
import com.honeywell.lib.widgets.HeadListView;

import java.util.ArrayList;

public class SelectDeviceActivity extends CubeTitleBarActivity {
    HeadListView mContent;
    SelectDeviceAdapter mAdapter;
    public static final String TYPE_SCHEDULE = "schedule";
    public static final String TYPE_RULE = "rule";
    String mTypeString;

    @Override
    protected int getContent() {
        return R.layout.activity_header_list;
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mTypeString = getIntent().getStringExtra(Constants.TYPE_STRING);
    }

    protected void initView() {
        mContent = (HeadListView) findViewById(R.id.lv_list);
        switch (mType) {
            case Constants.SELECT_TYPE_SCENARIO:
                break;
            case Constants.SELECT_TYPE_DEVICE:
                break;
            case Constants.SELECT_TYPE_ZONE:
                break;
            default:
                break;
        }
        mAdapter = new SelectDeviceAdapter(this, mType, getDataList(), getLoadingDialog());
        mContent.setAdapter(mAdapter);
        mContent.setOnScrollListener(mAdapter);
        mContent.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_scenario_select_device_section, mContent, false));
    }

    @Override
    protected void getData() {
        super.getData();
//        showLoadingDialog();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                MenuModuleController.getAllModuleList(SelectDeviceActivity.this);
//            }
//        }.start();

    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
    }

    public ArrayList<DeviceAddDetailBaseAdapter.ItemBean> getDataList(CubeModuleEvent event) {
        ArrayList<DeviceAddDetailBaseAdapter.ItemBean> dataList = new ArrayList<>();
//        if (event != null) {
//            ArrayList<SelectDeviceAdapter> arrayList = (ArrayList<SelectDeviceAdapter>) event.object;
//            if (arrayList != null && arrayList.size() > 0) {
//                final int size = arrayList.size();
//                for (int i = 0; i < size; i++) {
////                    final MenuModuleUIItem item = arrayList.get(i);
//                    dataList.add(new SelectDeviceAdapter.ItemBean(-1, "", ""));
//                }
//            }
//        }
        return dataList;
    }

    public ArrayList<DeviceAddDetailBaseAdapter.ItemBean> getDataList() {
        ArrayList<DeviceAddDetailBaseAdapter.ItemBean> dataList = new ArrayList<>();
        switch (mType) {
            case Constants.SELECT_TYPE_SCENARIO:
                ArrayList<ScenarioLoop> scenarioList = MenuScheduleController.getActionScenarios(this);
                if (scenarioList != null && scenarioList.size() > 0) {
                    final int size = scenarioList.size();
                    for (int i = 0; i < size; i++) {
                        dataList.add(new SelectDeviceAdapter.ItemBean(SelectDeviceAdapter.TYPE_LOOP, "", scenarioList.get(i).mScenarioName, scenarioList.get(i)));
                    }
                }
                break;
            case Constants.SELECT_TYPE_DEVICE:
                ArrayList<MenuScheduleDeviceObject> deviceList = TYPE_SCHEDULE.equalsIgnoreCase(mTypeString) ? MenuScheduleController.getScheduleDeviceList(this) : MenuRuleController.getRuleDeviceList(this);
                if (deviceList != null && deviceList.size() > 0) {
                    final int size = deviceList.size();
                    for (int i = 0; i < size; i++) {
                        final MenuScheduleDeviceObject item = deviceList.get(i);
                        item.section = TextUtils.isEmpty(item.section) ? "" : getString(Constants.DEVICE_TYPE_MAP.get(item.section));
                        dataList.add(new SelectDeviceAdapter.ItemBean(item.type == ModelEnum.UI_TYPE_TITLE ? SelectDeviceAdapter.TYPE_SECTION : SelectDeviceAdapter.TYPE_LOOP, item.section, item.title, item));
                    }
                }
                break;
            case Constants.SELECT_TYPE_ZONE:
                ArrayList<MenuScheduleDeviceObject> zoneList = MenuRuleController.getZoneList(this);
                if (zoneList != null && zoneList.size() > 0) {
                    final int size = zoneList.size();
                    for (int i = 0; i < size; i++) {
                        final MenuScheduleDeviceObject item = zoneList.get(i);
//                        LogUtil.e("alinmi", "i = " + i + "MenuScheduleDeviceObject item.section =  " + item.section + "-----------");
//                        item.section = TextUtils.isEmpty(item.section) ? "" : getString(Constants.DEVICE_TYPE_MAP.get(item.section));
                        dataList.add(new SelectDeviceAdapter.ItemBean(item.type == ModelEnum.UI_TYPE_TITLE ? SelectDeviceAdapter.TYPE_SECTION : SelectDeviceAdapter.TYPE_LOOP, item.section, item.title, item));
                    }
                }
                break;
            default:
                break;
        }

        return dataList;
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

    public void selectDevice(Object object) {
        Intent intent = new Intent();
        intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_DEVICE);
        DeviceHelper.addObject2Intent(intent, Constants.CONTENT, object);
        intent.putExtra(Constants.RESULT, Constants.SUCCESS);
        setResult(RESULT_OK, intent);
        finish();
    }
}
