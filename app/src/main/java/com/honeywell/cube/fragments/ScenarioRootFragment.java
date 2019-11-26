package com.honeywell.cube.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.activities.ScenarioEditFirstActivity;
import com.honeywell.cube.adapter.ScenarioRootGridViewAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.R;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScenarioEvent;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.dialogs.PasswordDialog;

import java.util.ArrayList;
import java.util.List;


public class ScenarioRootFragment extends SwipeToLoadFragment {
    private static final String TAG = ScenarioRootFragment.class.getSimpleName();
    public static final String SCENARIO_LOOP = "scenario_loop";
    BottomDialog mEditDialog;
    private GridView myGridview;
    private ScenarioRootGridViewAdapter adapter;

    //数据
    private ArrayList<ScenarioLoop> scenarioLoops = new ArrayList<>();//获取数据库的最新scenario列表

    //TODO 测试
    private ArrayList<Object> irList = new ArrayList<>();
    private MenuDeviceIRUIItem customItem;

    @Override
    public int getIndex() {
        return 1;
    }

    @Override
    public int getLyaout() {
        return R.layout.fragment_main_scenario;
    }

    @Override
    public void getData() {
        super.getData();
        updateView("初始化");
    }

    /*************************
     * private method
     *****************************/

    private void updateView(String reason) {
        Loger.print(TAG, "ssd updateView " + reason, Thread.currentThread());
        scenarioLoops = ScenarioController.getScenarioList(getContext());
        adapter.setScenarioLoops(scenarioLoops);
        adapter.setCurScenarioID(ScenarioController.getCurrentScenarioID(getContext()));
        adapter.notifyDataSetChanged();
    }

    public void initView(View view) {
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScenarioEditFirstActivity.class);
                intent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_ADD);
                getActivity().startActivity(intent);
            }
        });
        myGridview = (GridView) view.findViewById(R.id.swipe_target);
        adapter = new ScenarioRootGridViewAdapter(getContext(), scenarioLoops, true, -1);
        myGridview.setAdapter(adapter);
        myGridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //点击
        myGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.PROGRESS_STATUS, true, null));
                final int scenarioId = adapter.getScenarioId(position);
                /**
                 * add by H157925
                 * Scenario 密码框调用
                 */
                if (ScenarioController.checkIfNeedPassWord(getContext(), scenarioId)) {
                    PasswordDialog passwordDialog = new PasswordDialog(getContext(), new PasswordDialog.DataCallback() {
                        @Override
                        public void getPassword(String password) {
                            adapter.updateState(position);
                            ScenarioController.enableScenarioIdWithId(getContext(), scenarioId, password);
                        }
                    });
                    if (passwordDialog != null && !passwordDialog.isShowing()) {
                        passwordDialog.show();
                    }
                } else {
                    adapter.updateState(position);
                    ScenarioController.enableScenarioIdWithId(getContext(), scenarioId, null);
                }
            }
        });
        //长按
        myGridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showSelectDialog(position);
                return true;
            }
        });
    }

    private void showSelectDialog(final int index) {
        final List<BottomDialog.ItemBean> dataList = new ArrayList<BottomDialog.ItemBean>();
        dataList.add(new BottomDialog.ItemBean(getString(R.string.edit), null));
        dataList.add(new BottomDialog.ItemBean(getString(R.string.delete), null));
        if (null == mEditDialog) {
            mEditDialog = new BottomDialog(getActivity());
            mEditDialog.setViewCreateListener(
                    new BottomDialog.ViewCreateListener() {
                        @Override
                        public void initTop(TextView top) {
                            top.setVisibility(View.GONE);
                        }

                        @Override
                        public void initContent(ListView content) {
                            content.setAdapter(new BottomDialog.ListAdapter(getActivity(), index, dataList, false, new BottomDialog.ListAdapter.OnItemClickListener() {
                                @Override
                                public void itemClick(View view, int position, int index) {
                                    if (position == 0) {
                                        Intent intent = new Intent(getActivity(), ScenarioEditFirstActivity.class);
                                        intent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_EDIT);
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(SCENARIO_LOOP, adapter.getItem(index));
                                        intent.putExtras(bundle);
                                        getActivity().startActivity(intent);
                                    } else {
                                        //TODO Improve
                                        ScenarioController.deleteScenarioWithId(getActivity(), adapter.getScenarioId(index));
                                    }
                                    mEditDialog.dismiss();
                                }
                            }));
                        }
                    }

            );
        } else {
            ((BottomDialog.ListAdapter) mEditDialog.getContent().getAdapter()).setIndex(index);
        }

        if (mEditDialog != null && !mEditDialog.isShowing()) {
            mEditDialog.show();
        }
    }


    @SuppressWarnings("unused")
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeScenarioEvent) {
            final CubeScenarioEvent scenarioEvent = (CubeScenarioEvent) event;
            if (scenarioEvent.type == CubeEvents.CubeScenarioEventType.CONFIG_SCENARIO_STATE) {
                if(!scenarioEvent.success){
                    showToastShort((String) scenarioEvent.object);
                }else {
                    updateView("初始化");
                }
            } else if (scenarioEvent.type == CubeEvents.CubeScenarioEventType.ENABLE_SCENARIO_SUCCESS) {
                if (scenarioEvent.success) {
                    updateView("初始化");
                } else {
                    showToastShort((String) scenarioEvent.object);
                }
            }
        }
    }

}
