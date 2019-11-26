package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ModuleListAdapter;
import com.honeywell.cube.adapter.ScheduleListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleUIItem;
import com.honeywell.cube.controllers.menus.MenuScheduleController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScheduleEvent;

import java.util.ArrayList;
import java.util.List;

public class ScheduleListActivity extends CubeTitleBarActivity {
    private final static String TAG = ScheduleListActivity.class.getSimpleName();
    ScheduleListAdapter mAdapter;
    ListView mContent;

    @Override
    protected int getContent() {
        return R.layout.activity_list;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_add);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleListActivity.this, ScheduleEditActivity.class);
                intent.putExtra(Constants.TITLE, getString(R.string.schedule_create));
//        intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_COMMON);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.menu_schedule);
    }

    protected void initView() {
        mContent = (ListView) findViewById(R.id.lv_list);
        mAdapter = new ScheduleListAdapter(this, null, getLoadingDialog());
        mContent.setAdapter(mAdapter);
    }

    @Override
    protected void getData() {
        super.getData();
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                super.run();
                MenuScheduleController.getScheduleList(ScheduleListActivity.this);
            }
        }.start();

    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeScheduleEvent) {
            CubeScheduleEvent ev = (CubeScheduleEvent) event;
            if (ev.type == CubeEvents.CubeScheduleEventType.GET_SCHEDULE_LIST) {
                mAdapter.setDataList(getDataList(ev));
                mAdapter.notifyDataSetChanged();
                dismissLoadingDialog();
            }
//            else if (ev.type == CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE) {
//                dismissLoadingDialog();
//                if (ev.success) {
//                     showToastShort(ev.object.toString());
//                    getData();
//                } else {
//                     showToastShort(ev.object.toString());
//                }
//            }
            else if (ev.type == CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE_DELETE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    mAdapter.updateDeleteUI();
                } else {
                    showToastShort(R.string.operation_failed_tip);
                }
            } else if (ev.type == CubeEvents.CubeScheduleEventType.ENABLE_SCHEDULE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(ev.object.toString());
                } else {
                    showToastShort(ev.object.toString());
                }
            }
        }
    }

    public List<ScheduleListAdapter.ItemBean> getDataList(CubeScheduleEvent event) {
        List<ScheduleListAdapter.ItemBean> dataList = new ArrayList<>();
        if (event != null) {
            ArrayList<MenuScheduleUIItem> arrayList = (ArrayList<MenuScheduleUIItem>) event.object;
            if (arrayList != null && arrayList.size() > 0) {
                final int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    final MenuScheduleUIItem item = arrayList.get(i);
                    dataList.add(new ScheduleListAdapter.ItemBean(-1, null, "", item, ""));
                }
            }
        }
        return dataList;
    }

    public List<ScheduleListAdapter.ItemBean> getDataList() {
        List<ScheduleListAdapter.ItemBean> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            dataList.add(new ModuleListAdapter.ItemBean(-1, null, "", new MenuScheduleUIItem(), ""));
        }
        return dataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString("result");
            if ("success".equalsIgnoreCase(result)) {
                getData();
            }
        }
    }
}
