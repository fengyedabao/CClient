package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ScenarioEditDeviceSelectAdapter;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.fragments.ScenarioRootFragment;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScenarioEvent;
import com.honeywell.lib.widgets.HeadListView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ScenarioEditSelectDeviceActivity extends CubeTitleBarActivity {
    ScenarioLoop mScenarioLoop;
    HeadListView mHeadListView;
    public static final String DELETE_ITME = "delete_item";
    public static final String SELECTED_ITME = "selected_item";
    private ArrayList<UIItems> mDeleteItems;
    private ArrayList<UIItems> mSelectedItems;

    @Override
    protected int getContent() {
        return R.layout.activity_scenario_edit_select_device;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<UIItems> selectedDevices = ((ScenarioEditDeviceSelectAdapter) mHeadListView.getAdapter()).getSelectedDevices();
                if (selectedDevices == null || selectedDevices.size() == 0) {
                    showToastShort(R.string.device_add_should_select_device);
                } else {
                    EventBus.getDefault().post(new CubeScenarioEvent(CubeEvents.CubeScenarioEventType.CONFIG_SELECTED_DEVICES, selectedDevices));
//                bundle.putSerializable("service", mAdapter.getData().get(mPosition));
//                intent.putExtras(bundle);
//                setResult(ServiceProvider.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.edit_scenario_device);
    }

    protected void initView() {
        Intent intent = getIntent();
        mDeleteItems = intent.getParcelableArrayListExtra(DELETE_ITME);
        mSelectedItems = intent.getParcelableArrayListExtra(SELECTED_ITME);
        mScenarioLoop = intent.getParcelableExtra(ScenarioRootFragment.SCENARIO_LOOP);
        mHeadListView = (HeadListView) findViewById(R.id.hlv_content);
//        if (DeviceRootFragment.DEBUG_NO_NET) {
//            ScenarioEditDeviceSelectAdapter adapter = new ScenarioEditDeviceSelectAdapter(this, null);
//            mHeadListView.setAdapter(adapter);
//            mHeadListView.setOnScrollListener(adapter);
//        }
        mHeadListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_scenario_select_device_section, mHeadListView, false));
        mHeadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ScenarioController.getScenarioDeviceWithScenarioID(this);
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeScenarioEvent) {
            CubeScenarioEvent ev = (CubeScenarioEvent) event;
            if (ev.type == CubeEvents.CubeScenarioEventType.CONFIG_DEVICE_LIST) {
                ArrayList<UIItems> list = (ArrayList<UIItems>) ev.object;
//                if (!DeviceRootFragment.DEBUG_NO_NET) {
                ScenarioEditDeviceSelectAdapter adapter = new ScenarioEditDeviceSelectAdapter(this, handleDeviceList2(list, mSelectedItems));

                mHeadListView.setAdapter(adapter);
                mHeadListView.setOnScrollListener(adapter);
//                }
            }
        }
    }

    public ArrayList<UIItems> handleDeviceList(ArrayList<UIItems> deviceList, ArrayList<UIItems> delete) {
        if (delete == null || delete.size() == 0 || deviceList == null || deviceList.size() == 0) {
            return deviceList;
        } else {
            ArrayList<UIItems> deleteItems = (ArrayList<UIItems>) delete.clone();
            final int size = deviceList.size();
            for (int i = 0; i < size; i++) {
                if (deleteItems == null || deleteItems.size() == 0) {
                    return deviceList;
                } else {
                    if (deviceList.get(i).type == ModelEnum.UI_TYPE_TITLE) {
                        continue;
                    }
                    final BasicLoop deviceLoop = (BasicLoop) deviceList.get(i).object;
                    for (int j = 0; j < deleteItems.size(); j++) {
                        final BasicLoop deleteLoop = (BasicLoop) deleteItems.get(j).object;
                        //TODO
                        if (deviceLoop.mLoopId == deleteLoop.mLoopId && deviceLoop.mModuleType == deleteLoop.mModuleType) {
                            deviceList.get(i).isSelcet = false;
                            deleteItems.remove(j);
                            break;
                        }
                    }
                }
            }
        }
        return deviceList;
    }

    public ArrayList<UIItems> handleDeviceList2(ArrayList<UIItems> deviceList, ArrayList<UIItems> selected) {
        if (selected == null || selected.size() == 0 || deviceList == null || deviceList.size() == 0) {
            return deviceList;
        } else {
            ArrayList<UIItems> selectedItems = new ArrayList<UIItems>();
            final int selectedSize = selected.size();
            for (int i = 0; i < selectedSize; i++) {
                final UIItems item = selected.get(i);
                if (item.type != ModelEnum.UI_TYPE_TITLE) {
                    selectedItems.add(item);
                }
            }

            final int size = deviceList.size();
            for (int i = 0; i < size; i++) {
                deviceList.get(i).isSelcet = false;
                if (selectedItems == null || selectedItems.size() == 0) {
                    continue;
                } else {
                    if (deviceList.get(i).type == ModelEnum.UI_TYPE_TITLE) {
                        continue;
                    }
                    final BasicLoop deviceLoop = (BasicLoop) deviceList.get(i).object;
                    for (int j = 0; j < selectedItems.size(); j++) {
                        final BasicLoop deleteLoop = (BasicLoop) selectedItems.get(j).object;
                        if (deviceLoop.mLoopId == deleteLoop.mLoopId && deviceLoop.mModuleType == deleteLoop.mModuleType&& deleteLoop.mLoopSelfPrimaryId == deviceLoop.mLoopSelfPrimaryId && deleteLoop.mModulePrimaryId == deviceLoop.mModulePrimaryId) {
                            deviceList.get(i).isSelcet = true;
                            selectedItems.remove(j);
                            break;
                        }
                    }
                }
            }
        }
        return deviceList;
    }

}
