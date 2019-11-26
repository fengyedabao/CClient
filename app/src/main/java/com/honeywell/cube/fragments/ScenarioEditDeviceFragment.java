package com.honeywell.cube.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.DeviceAirConditionerActivity;
import com.honeywell.cube.activities.DeviceVentilationActivity;
import com.honeywell.cube.activities.DeviceWIACActivity;
import com.honeywell.cube.activities.DeviceWICommonActivity;
import com.honeywell.cube.activities.DeviceWICustomActivity;
import com.honeywell.cube.activities.ScenarioEditConfigureWIActivity;
import com.honeywell.cube.activities.ScenarioEditSecondActivity;
import com.honeywell.cube.activities.ScenarioEditSelectDeviceActivity;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IrLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScenarioEvent;
import com.honeywell.lib.adapter.ExpandableListAdapter;
import com.honeywell.cube.adapter.ScenarioEditDeviceAdapter;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.widgets.HeadListViewCompat;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.SlideView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by milton on 16/5/31.
 */
public class ScenarioEditDeviceFragment extends Fragment {
    HeadListViewCompat mHeadListView;
    private int currentPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scenario_edit_device, container, false);
        initView(view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ScenarioEditSecondActivity) getActivity()).getData();
    }

    private void initView(View view) {
        mHeadListView = (HeadListViewCompat) view.findViewById(R.id.lv_content);
        ScenarioEditDeviceAdapter adapter = new ScenarioEditDeviceAdapter(getActivity(), getDataList(), null);
        mHeadListView.setAdapter(getExpandableListAdapter(adapter));
        mHeadListView.setOnScrollListener(adapter);
        mHeadListView.setPinnedHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.list_scenario_select_device_section, mHeadListView, false));
        mHeadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UIItems item = ((ScenarioEditDeviceAdapter) ((ExpandableListAdapter) mHeadListView.getAdapter()).getWrappedAdapter()).getDataList().get(position);
                LogUtil.e("alinmi22", "onItemClick position = " + position + " , item.type = " + item.type + " , item.looptype " + item.looptype);
                if (((ExpandableListAdapter) mHeadListView.getAdapter()).getWrappedAdapter().getItemViewType(position) == ScenarioEditDeviceAdapter.TYPE_JUMP && ModelEnum.UI_TYPE_OTHER == item.type) {
                    Intent intent = null;
                    switch (item.looptype) {
                        case ModelEnum.LOOP_IR_TV:
                        case ModelEnum.LOOP_IR_STB:
                        case ModelEnum.LOOP_IR_DVD:
                        case ModelEnum.LOOP_IR_AC:
                        case ModelEnum.LOOP_IR_CUSTOM:
                        case ModelEnum.LOOP_IR:
                            intent = new Intent(getActivity(), ScenarioEditConfigureWIActivity.class);
                            break;
                        case ModelEnum.LOOP_VENTILATION:
                            break;
                        case ModelEnum.WIFI_485:
                        case ModelEnum.LOOP_BACNET:
                            intent = new Intent(getActivity(), DeviceAirConditionerActivity.class);
                            intent.putExtra(Constants.TITLE, getActivity().getString(R.string.set_air_conditioner));
                            intent.putExtra(Constants.TYPE, Constants.AC_TYPE_SET_STATUS);
                            intent.putExtra(Constants.IS_FROM_SCENARIO, true);

                            break;
                        default:
                            break;
                    }
                    if (intent != null) {
                        DeviceHelper.addObject2Intent(intent, Constants.CONTENT, item.object);
                        currentPosition = position;
                        getActivity().startActivityForResult(intent, 1);
                    }
                }
            }
        });
        Button selectDevice = (Button) view.findViewById(R.id.btn_select_device);
        selectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScenarioEditSelectDeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(ScenarioRootFragment.SCENARIO_LOOP, ((ScenarioEditSecondActivity) getActivity()).getScenarioLoop());
                intent.putExtras(bundle);
                intent.putExtra(ScenarioEditSelectDeviceActivity.DELETE_ITME, ((ScenarioEditDeviceAdapter) ((ExpandableListAdapter) mHeadListView.getAdapter()).getWrappedAdapter()).getDeleteItems());
                intent.putExtra(ScenarioEditSelectDeviceActivity.SELECTED_ITME, ((ScenarioEditDeviceAdapter) ((ExpandableListAdapter) mHeadListView.getAdapter()).getWrappedAdapter()).getSelectedList());
                getActivity().startActivity(intent);
            }
        });
    }

    private ArrayList<UIItems> getDataList() {
        if (DeviceRootFragment.DEBUG_NO_NET) {
            ArrayList<UIItems> selectedItems = new ArrayList<>();
            selectedItems.add(new UIItems(ModelEnum.UI_TYPE_TITLE, "main_backaudio", "main_backaudio", "music", false));
            BackaudioLoop loop = new BackaudioLoop();
            loop.mLoopName = "music";
            selectedItems.add(new UIItems(ModelEnum.UI_TYPE_OTHER, loop, "main_backaudio", "main_backaudio", true));
            selectedItems.add(new UIItems(ModelEnum.UI_TYPE_OTHER, loop, "main_backaudio", "main_backaudio", true));
            return selectedItems;
        } else {
            return ((ScenarioEditSecondActivity) getActivity()).getDeviceList();
        }
    }

    private ExpandableListAdapter getExpandableListAdapter(BaseAdapter adapter) {
        final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(adapter, R.id.iv_expandable, R.id.thumb_layout, R.id.expandable_layout);
        expandableListAdapter.setOnItemExpandCollapseListener(new ExpandableListAdapter.OnItemExpandCollapseListener() {
            @Override
            public void onItemExpandCollapse(View view, int position, int type) {
                final int itemType = expandableListAdapter.getItemViewType(position);
                if (itemType == ScenarioEditDeviceAdapter.TYPE_EXPANDABLE) {
                    initViewBackground(view, type, itemType);
                }
            }

            @Override
            public void onItemInitStatus(View view, int position, int type) {
                final int itemType = expandableListAdapter.getItemViewType(position);
                initViewBackground(view, type, itemType);
            }

            @Override
            public boolean onItemNeedExpand(View view, int position) {
                if (view instanceof SlideView) {
                    return !((SlideView) view).isSlided();
                } else {
                    return true;
                }
            }
        });
        return expandableListAdapter;
    }

    private void initViewBackground(View view, int status, int itemType) {
        view.setBackgroundResource(status == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE ? R.color.item_collapse_background : R.color.item_expand_background);
        if (view instanceof SlideView) {
            if (itemType != ScenarioEditDeviceAdapter.TYPE_SECTION) {
                ((SlideView) view).setSlidable(status == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE);
            }
        }
    }

    public void updateUI() {
        ArrayList<UIItems> items = getDataList();
//        if (items == null) {
//            Log.e("alinmi", "items.size() = " + items);
//        } else {
//            Log.e("alinmi", "items.size() = " + items.size());
//        }
        ScenarioEditDeviceAdapter adapter = new ScenarioEditDeviceAdapter(getActivity(), items, null);
        mHeadListView.setAdapter(getExpandableListAdapter(adapter));
        mHeadListView.setOnScrollListener(adapter);
    }

    public ArrayList<UIItems> getDeviceDataList() {
        return ((ScenarioEditDeviceAdapter) ((ExpandableListAdapter) mHeadListView.getAdapter()).getWrappedAdapter()).getDataList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    public void onEventMainThread(CubeEvents event) {
//        if (event instanceof CubeBasicEvent) {
//            final CubeBasicEvent cubeBasicEvent = (CubeBasicEvent) event;
//            if (cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.TIME_OUT) {
////                ToastUtil.showShort(this, cubeBasicEvent.getMessage());
//                dismissLoadingDialog();
//            }
//        }else
        if (event instanceof CubeScenarioEvent) {
            CubeScenarioEvent ev = (CubeScenarioEvent) event;
            if (ev.type == CubeEvents.CubeScenarioEventType.CONFIG_WI) {
                LogUtil.e("alinmi22", "CubeEvents.CubeScenarioEventType.CONFIG_WI");
                ((ScenarioEditDeviceAdapter) ((ExpandableListAdapter) mHeadListView.getAdapter()).getWrappedAdapter()).getDataList().get(currentPosition).object = ev.object;
            }
        }
    }
}
