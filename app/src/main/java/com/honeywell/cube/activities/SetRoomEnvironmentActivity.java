package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ModuleListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleConditionRoom;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;
import com.honeywell.cube.widgets.SelectItem;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SetRoomEnvironmentActivity extends CubeTitleBarActivity {

    private static final String TAG = SetRoomEnvironmentActivity.class.getSimpleName();
    private SelectItem mRoom;
    private SelectItem mType;
    private SelectItem mCondition;
    private SelectItem mValue;

    private MenuRuleConditionRoom mMenuRuleConditionRoom;

    @Override
    protected int getContent() {
        return R.layout.activity_set_room_environment;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuRuleConditionRoom.room_name = mRoom.getContentText();
                mMenuRuleConditionRoom.room_id = Integer.parseInt(mRoom.getDataList().get(mRoom.getSelectedPosition()).mData.toString());
                mMenuRuleConditionRoom.room_trigger_mode = mCondition.getContentText();
                mMenuRuleConditionRoom.room_condition = mType.getContentText();
                mMenuRuleConditionRoom.room_value = mValue.getContentText();
                Intent intent = new Intent();
                intent.putExtra(Constants.RESULT, "");
                intent.putExtra(Constants.TYPE, Constants.SELECT_TYPE_ROOM_ENVIRONMENT);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.CONTENT, mMenuRuleConditionRoom);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuRuleConditionRoom = getIntent().getParcelableExtra(Constants.CONTENT);
        if (mMenuRuleConditionRoom == null) {
            LogUtil.e(TAG, "mMenuRuleConditionRoom is null", true);
            mMenuRuleConditionRoom = new MenuRuleConditionRoom();
        }
    }

    protected void initView() {
        mRoom = (SelectItem) findViewById(R.id.si_room);
        DeviceHelper.initRoom(mRoom);
        mRoom.setContent(mMenuRuleConditionRoom.room_name);

        mType = (SelectItem) findViewById(R.id.si_type);
        mType.setName(R.string.type);
        mType.setDataList(DeviceHelper.getRoomEnvironmentTypeList(this));
        mType.setContent(mMenuRuleConditionRoom.room_condition);
        mType.setOnItemClickListener(new SelectItem.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                initValueItem(position, false);
                mType.setContent(position);
            }
        });

        mCondition = (SelectItem) findViewById(R.id.si_condition);
        mCondition.setName(R.string.condition);
        mCondition.setDataList(DeviceHelper.getRoomEnvironmentConditionList(this));
        mCondition.setContent(mMenuRuleConditionRoom.room_trigger_mode);


        mValue = (SelectItem) findViewById(R.id.si_value);
        mValue.setName(R.string.value);
        initValueItem(mType.getSelectedPosition(), true);
        mValue.setContent(mMenuRuleConditionRoom.room_value);
        mValue.setOnNumberSelectedListener(new SelectItem.OnNumberSelectedListener() {
            @Override
            public void numberSelected(int number) {
                switch (mType.getSelectedPosition()) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initValueItem(int position, boolean isFirst) {
        switch (position) {
            case 0:
                mValue.setType(SelectItem.TYPE_NORMAL);
                mValue.setDataList(DeviceHelper.getAirQualityList(this));
                mValue.setContent((isFirst ? mMenuRuleConditionRoom.room_value : mValue.getDataList().get(2).mText));
                break;
            case 1:
                mValue.setType(SelectItem.TYPE_INTEGER);
                mValue.setDataList(DeviceHelper.getPM25List(this));
                mValue.setContentValue(isFirst ? Integer.parseInt(mMenuRuleConditionRoom.room_value) : 190);
                break;
            case 2:
                mValue.setType(SelectItem.TYPE_INTEGER);
                mValue.setDataList(DeviceHelper.getTemperatureList(this));
                mValue.setContentValue(isFirst ? Integer.parseInt(mMenuRuleConditionRoom.room_value) : 25);
                break;
            case 3:
                mValue.setType(SelectItem.TYPE_INTEGER);
                mValue.setDataList(DeviceHelper.getHumidityList(this));
                mValue.setContentValue(isFirst ? Integer.parseInt(mMenuRuleConditionRoom.room_value) : 45);
                break;
            default:
                break;
        }
    }

    @Override
    protected void getData() {
        super.getData();
//        showLoadingDialog();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                MenuModuleController.getAllModuleList(RuleCreateActivity.this);
//            }
//        }.start();

    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeModuleEvent) {
//            CubeModuleEvent ev = (CubeModuleEvent) event;
//            if (ev.type == CubeEvents.CubeModuleEventType.GET_MODULE_LIST) {
//                mAdapter.setDataList(getDataList(ev));
//                mAdapter.notifyDataSetChanged();
//                dismissLoadingDialog();
//            } else if (ev.type == CubeEvents.CubeModuleEventType.ADD_FIND_NEW_MODULE) {
//                dismissLoadingDialog();
//                if (ev.success) {
//                    Toast.makeText(this, ev.object.toString(), Toast.LENGTH_SHORT).show();
//                    getData();
//                } else {
//                    Toast.makeText(this, ev.object.toString(), Toast.LENGTH_SHORT).show();
//                }
//            } else if (ev.type == CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE_DELETE) {
//                dismissLoadingDialog();
//                if (ev.success) {
//                    Toast.makeText(this, R.string.operation_success_tip, Toast.LENGTH_SHORT).show();
//                    mAdapter.updateDeleteUI();
//                } else {
//                    Toast.makeText(this, R.string.operation_failed_tip, Toast.LENGTH_SHORT).show();
//                }
//            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString("result");
            if ("success".equalsIgnoreCase(result)) {
//                getData();
            }
        }
    }
}
