package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleAddDetails;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleUIItem;
import com.honeywell.cube.controllers.menus.MenuScheduleController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScheduleEvent;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.cube.widgets.WeekSelectItem;
import com.honeywell.lib.pickerview.adapter.NumericWheelAdapter;
import com.honeywell.lib.pickerview.lib.WheelView;
import com.honeywell.lib.utils.TimeUtil;

public class ScheduleEditActivity extends CubeTitleBarActivity {
    ListView mContent;
    WheelView mHour;
    WheelView mMinute;
    EditTextItem mEditName;
    SelectItem mTask;
    WeekSelectItem mSchedule;
    MenuScheduleUIItem mMenuScheduleUIItem;
    MenuScheduleAddDetails mMenuScheduleAddDetails;

    @Override
    protected int getContent() {
        return R.layout.activity_create_schedule;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDataegal()) {
                    showLoadingDialog();
                    startAsynchronousOperation(new Runnable() {
                        @Override
                        public void run() {
                            MenuScheduleController.sendModifySchedule(ScheduleEditActivity.this, mMenuScheduleAddDetails);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuScheduleUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    protected void initView() {
        mMenuScheduleAddDetails = MenuScheduleController.getScheduleDetails(ScheduleEditActivity.this, mMenuScheduleUIItem);
        String[] time = TimeUtil.getStringDate(TimeUtil.FORMAT_HOUR_MINUTE).split(":");
        mHour = (WheelView) findViewById(R.id.hour);
        mHour.setAdapter(new NumericWheelAdapter(0, 23));
//        wv_hours.setLabel(context.getString(com.bigkoo.pickerview.R.string.pickerview_hours));// 添加文字
        if (time != null && time.length == 2) {
            mHour.setCurrentItem(Integer.parseInt(time[0]));
        }
        mHour.setCyclic(true);
        mMinute = (WheelView) findViewById(R.id.minute);
        mMinute.setAdapter(new NumericWheelAdapter(0, 59));
//        mMinute.setLabel(context.getString(com.bigkoo.pickerview.R.string.pickerview_minutes));// 添加文字
        if (time != null && time.length == 2) {
            mMinute.setCurrentItem(Integer.parseInt(time[1]));
        }
        mHour.setCyclic(true);

        mEditName = (EditTextItem) findViewById(R.id.ei_name);
        mEditName.setTextName(R.string.name);
        mEditName.setEditName(mMenuScheduleAddDetails.name);

        mTask = (SelectItem) findViewById(R.id.si_task);
        mTask.setName(R.string.task);
        mTask.setDataList(DeviceHelper.getTaskList(this));
        mTask.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                Intent intent = new Intent(ScheduleEditActivity.this, SelectDeviceActivity.class);
                intent.putExtra(Constants.TYPE_STRING, SelectDeviceActivity.TYPE_SCHEDULE);
                if (position == 0) {
                    intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_SCENARIO);
                    intent.putExtra(Constants.TITLE, getString(R.string.select_scenario));
                } else {
                    intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_DEVICE);
                    intent.putExtra(Constants.TITLE, getString(R.string.select_device));
                }
                startActivityForResult(intent, 1);
            }
        });
        mTask.setContent(mMenuScheduleAddDetails.action_title);

        mSchedule = (WeekSelectItem) findViewById(R.id.wsi_schedule);
        mSchedule.setName(R.string.repeat);
        mSchedule.setContent(mMenuScheduleAddDetails.repeat);
    }

    private boolean checkDataegal() {
        mMenuScheduleAddDetails.action_title = mTask.getContentText();
        mMenuScheduleAddDetails.name = mEditName.getContent();
        mMenuScheduleAddDetails.repeat = mSchedule.getContentText();
        mMenuScheduleAddDetails.action_time = formatTime();
        if (TextUtils.isEmpty(mMenuScheduleAddDetails.action_title)) {
            showToastShort(R.string.select_one_task);
            return false;
        }
        if (TextUtils.isEmpty(mMenuScheduleAddDetails.name)) {
            showToastShort(R.string.illegal_input_string);
            return false;
        }
        return true;
    }

    //    @Override
//    protected void getData() {
//        super.getData();
//
//        startAsynchronousOperation(new Runnable() {
//            @Override
//            public void run() {
//                mMenuScheduleAddDetails = MenuScheduleController.getScheduleDetails(ScheduleCreateActivity.this, mMenuScheduleUIItem);
//            }
//        });
//
//    }
    private String formatTime() {
        String hour = mHour.getAdapter().getItem(mHour.getCurrentItem()).toString();
        String mintue = mMinute.getAdapter().getItem(mMinute.getCurrentItem()).toString();
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (mintue.length() == 1) {
            mintue = "0" + mintue;
        }
        return hour + ":" + mintue + ":00";
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeScheduleEvent) {
            CubeScheduleEvent ev = (CubeScheduleEvent) event;
            if (ev.type == CubeEvents.CubeScheduleEventType.CONFIG_SCHEDULE_STATE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToastShort(ev.object.toString());
                }
            }
        }
    }

//    public List<ModuleListAdapter.ItemBean> getDataList(CubeModuleEvent event) {
//        List<ModuleListAdapter.ItemBean> dataList = new ArrayList<>();
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
//        return dataList;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            String result = data.getExtras().getString(Constants.RESULT);
            int type = data.getExtras().getInt(Constants.TYPE, -1);
            switch (type) {
                case Constants.SELECT_TYPE_SCENARIO:
                    mTask.setContent(result);
                    mMenuScheduleAddDetails.action_title = mTask.getContentText();
                    mMenuScheduleAddDetails.action_type = 0;
                    ScenarioLoop loop = data.getParcelableExtra(Constants.CONTENT);
                    mMenuScheduleAddDetails.action_scenarioloop = loop;
                    break;
                case Constants.SELECT_TYPE_DEVICE:
                    MenuScheduleDeviceObject object = data.getParcelableExtra(Constants.CONTENT);
                    mTask.setContent(object.title);
                    mMenuScheduleAddDetails.action_title = mTask.getContentText();
                    mMenuScheduleAddDetails.action_type = 1;
                    mMenuScheduleAddDetails.action_device = object.loop;
                    break;
                case Constants.SELECT_TYPE_ZONE:
                    break;
                default:
                    break;
            }
        }
    }
}
