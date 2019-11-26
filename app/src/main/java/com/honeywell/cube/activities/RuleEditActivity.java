package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ModuleListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleAddDetails;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleConditionRoom;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.controllers.menus.MenuRuleController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;
import com.honeywell.cube.utils.events.CubeRuleEvent;
import com.honeywell.cube.widgets.SwitchItem;
import com.honeywell.cube.widgets.EditTextItem;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.cube.widgets.TimeSelectorItem;
import com.honeywell.cube.widgets.WeekSelectItem;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class RuleEditActivity extends CubeTitleBarActivity {
    private EditTextItem mName;
    private TimeSelectorItem mDelay;
    private SwitchItem mWorkTime;
    private View mHide;
    private TimeSelectorItem mStartTime;
    private TimeSelectorItem mEndTime;
    private WeekSelectItem mRepeat;
    private SelectItem mCondition;
    private SelectItem mTask;
    private MenuRuleAddDetails mMenuRuleAddDetails;
    private MenuRuleUIItem mMenuRuleUIItem;

    @Override
    protected int getContent() {
        return R.layout.activity_create_rule;
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
                            MenuRuleController.modifyRule(RuleEditActivity.this, mMenuRuleAddDetails);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuRuleUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    protected void initView() {
        mMenuRuleAddDetails = MenuRuleController.getRuleDetails(RuleEditActivity.this, mMenuRuleUIItem == null ? null : mMenuRuleUIItem.info);
        mName = (EditTextItem) findViewById(R.id.ei_name);
        mName.setTextName(R.string.rule_name);
        mName.setEditName(mMenuRuleAddDetails.name);

        mDelay = (TimeSelectorItem) findViewById(R.id.tsi_delay);
        mDelay.setName(R.string.delay_time);
        mDelay.setTime(mMenuRuleAddDetails.delay_time);

        mHide = findViewById(R.id.ll_hide);

        mWorkTime = (SwitchItem) findViewById(R.id.ci_work_time);
        mWorkTime.setTextName(R.string.work_time);
        mWorkTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHide.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mWorkTime.setChecked(mMenuRuleAddDetails.need_work_time);

        mStartTime = (TimeSelectorItem) findViewById(R.id.tsi_start_time);
        mStartTime.setName(R.string.start_time);
        mStartTime.setType(TimeSelectorItem.TYPE_HOUR_MINUTE);
        mStartTime.setTime(mMenuRuleAddDetails.start_time);

        mEndTime = (TimeSelectorItem) findViewById(R.id.tsi_end_time);
        mEndTime.setName(R.string.end_time);
        mEndTime.setType(TimeSelectorItem.TYPE_HOUR_MINUTE);
        mEndTime.setTime(mMenuRuleAddDetails.end_time);

        mRepeat = (WeekSelectItem) findViewById(R.id.wsi_repeat);
        mRepeat.setName(R.string.repeat);
        mRepeat.setContent(mMenuRuleAddDetails.repeat);

        mCondition = (SelectItem) findViewById(R.id.si_condition);
        mCondition.setName(R.string.condition);
        mCondition.setDataList(DeviceHelper.getConditionList(this));
        mCondition.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                Intent intent;
                if (position == 0) {
                    intent = new Intent(RuleEditActivity.this, SelectDeviceActivity.class);
                    intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_ZONE);
                    intent.putExtra(Constants.TITLE, getString(R.string.select_zone));
                } else {
                    intent = new Intent(RuleEditActivity.this, SetRoomEnvironmentActivity.class);
                    intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_ROOM_ENVIRONMENT);
                    intent.putExtra(Constants.TITLE, getString(R.string.room_environment));
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.CONTENT, mMenuRuleAddDetails.conditionRoom);
                    intent.putExtras(bundle);
                }
                startActivityForResult(intent, 1);
            }
        });
        mCondition.setContent(mMenuRuleAddDetails.condition_name);


        mTask = (SelectItem) findViewById(R.id.si_task);
        mTask.setName(R.string.task);
        mTask.setDataList(DeviceHelper.getTaskList(this));
        mTask.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                Intent intent = new Intent(RuleEditActivity.this, SelectDeviceActivity.class);
                intent.putExtra(Constants.TYPE_STRING, SelectDeviceActivity.TYPE_RULE);
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
        mTask.setContent(mMenuRuleAddDetails.action_name);
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeRuleEvent) {
            CubeRuleEvent ev = (CubeRuleEvent) event;
            if (ev.type == CubeEvents.CubeRuleEventType.CONFIG_RULE_STATE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    finishSuccess();
                } else {
                    showToastShort(ev.object.toString());
                }
            }
        }
    }

    public List<ModuleListAdapter.ItemBean> getDataList(CubeModuleEvent event) {
        List<ModuleListAdapter.ItemBean> dataList = new ArrayList<>();
        if (event != null) {
            ArrayList<MenuModuleUIItem> arrayList = (ArrayList<MenuModuleUIItem>) event.object;
            if (arrayList != null && arrayList.size() > 0) {
                final int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    final MenuModuleUIItem item = arrayList.get(i);
                    dataList.add(new ModuleListAdapter.ItemBean(-1, null, "", item, ""));
                }
            }
        }
        return dataList;
    }

    private boolean checkDataegal() {
        mMenuRuleAddDetails.name = mName.getContent();
        mMenuRuleAddDetails.delay_time = mDelay.getTimeSecond();
        mMenuRuleAddDetails.need_work_time = mWorkTime.isChecked();
        mMenuRuleAddDetails.start_time = mStartTime.getTime();
        mMenuRuleAddDetails.end_time = mEndTime.getTime();
        mMenuRuleAddDetails.repeat = mRepeat.getContentText();
        mMenuRuleAddDetails.condition_name = mCondition.getContentText();
        mMenuRuleAddDetails.action_name = mTask.getContentText();
        if (TextUtils.isEmpty(mMenuRuleAddDetails.name)) {
            showToastShort(R.string.set_input);
            return false;
        }
        if (TextUtils.isEmpty(mMenuRuleAddDetails.condition_name)) {
            showToastShort(R.string.set_input);
            return false;
        }
        if (TextUtils.isEmpty(mMenuRuleAddDetails.action_name)) {
            showToastShort(R.string.set_input);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            String result = data.getExtras().getString(Constants.RESULT);
//                getData();
            int type = data.getExtras().getInt(Constants.TYPE, -1);
            switch (type) {
                case Constants.SELECT_TYPE_SCENARIO:
                    mTask.setContent(result);
                    mMenuRuleAddDetails.action_name = mTask.getContentText();
                    mMenuRuleAddDetails.action_type = 0;
                    ScenarioLoop loop = data.getParcelableExtra(Constants.CONTENT);
                    mMenuRuleAddDetails.action_scenario = loop;
                    break;
                case Constants.SELECT_TYPE_DEVICE:
                    MenuScheduleDeviceObject object = data.getParcelableExtra(Constants.CONTENT);
                    mTask.setContent(object.title);
                    mMenuRuleAddDetails.action_name = mTask.getContentText();
                    mMenuRuleAddDetails.action_type = 1;
                    mMenuRuleAddDetails.action_device = object.loop;
                    break;
                case Constants.SELECT_TYPE_ZONE:
                    MenuScheduleDeviceObject object2 = data.getParcelableExtra(Constants.CONTENT);
                    mCondition.setContent(object2.title);
                    mMenuRuleAddDetails.condition_name = mCondition.getContentText();
                    mMenuRuleAddDetails.condition_type = 0;
                    mMenuRuleAddDetails.sensor_object = object2.loop;
                    break;
                case Constants.SELECT_TYPE_ROOM_ENVIRONMENT:
                    MenuRuleConditionRoom room = data.getParcelableExtra(Constants.CONTENT);
                    mCondition.setContent(room.room_name);
                    mMenuRuleAddDetails.condition_name = mCondition.getContentText();
                    mMenuRuleAddDetails.condition_type = ModelEnum.MODULE_TYPE_ROOM;
                    mMenuRuleAddDetails.conditionRoom = room;
                    break;
                default:
                    break;
            }
        }
    }
}
