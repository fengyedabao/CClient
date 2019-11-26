package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ModuleListAdapter;
import com.honeywell.cube.adapter.SetDeviceStatusAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.HeadListView;

import java.util.ArrayList;
import java.util.List;

public class SetDeviceStatusActivity extends CubeTitleBarActivity {


    HeadListView mContent;
    MenuScheduleDeviceObject mItem;
    SetDeviceStatusAdapter mAdapter;

    @Override
    protected int getContent() {
        return R.layout.activity_set_device_status;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_DEVICE);
                DeviceHelper.addObject2Intent(intent, Constants.CONTENT, mAdapter.getDataList().get(1));
                intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mItem = getIntent().getParcelableExtra(Constants.CONTENT);
        LogUtil.e("alinmi2", " MenuScheduleDeviceObject = " + mItem + " -----item.section = " + mItem.section + "---item.loopType = " + mItem.loopType);
    }

    protected void initView() {
        mContent = (HeadListView) findViewById(R.id.lv_content);
        mAdapter = new SetDeviceStatusAdapter(this, getDataList(), null);
        mContent.setAdapter(mAdapter);
        mContent.setOnScrollListener(mAdapter);
        mContent.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_scenario_select_device_section, mContent, false));
        mContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });
    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
    }

    public List<ModuleListAdapter.ItemBean> getDataList(CubeModuleEvent event) {
        List<ModuleListAdapter.ItemBean> dataList = new ArrayList<>();
//        if (event != null) {
//            ArrayList<MenuModuleUIItem> arrayList = (ArrayList<MenuModuleUIItem>) event.object;
//            if (arrayList != null && arrayList.size() > 0) {
//                final int size = arrayList.size();
//                for (int i = 0; i < size; i++) {
//                    final MenuModuleUIItem item = arrayList.get(i);
//                    dataList.add(new ModuleListAdapter.ItemBean(-1, null, "", item, ""));
//                }
//            }
//        }
        return dataList;
    }

    private ArrayList<MenuScheduleDeviceObject> getDataList() {
        ArrayList<MenuScheduleDeviceObject> list = new ArrayList<>();
        MenuScheduleDeviceObject section = new MenuScheduleDeviceObject();
        section.section = mItem.section;
        section.type = ModelEnum.UI_TYPE_TITLE;
        list.add(section);
        list.add(mItem);
        return list;
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
