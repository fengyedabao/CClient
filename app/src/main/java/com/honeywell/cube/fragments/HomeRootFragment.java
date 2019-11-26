package com.honeywell.cube.fragments;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.MainActivity;
import com.honeywell.cube.activities.NotificationListActivity;
import com.honeywell.cube.adapter.ScenarioRootGridViewAdapter;
import com.honeywell.cube.controllers.AlarmController;
import com.honeywell.cube.controllers.HomeController;
import com.honeywell.cube.controllers.HomeController.WeatherResponceController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.controllers.UIItem.HomeRoomDetailsUIItem;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeAlarmEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;
import com.honeywell.cube.utils.events.CubeScenarioEvent;
import com.honeywell.cube.voicerecogadapter.VoiceRecgnitionController;
import com.honeywell.cube.widgets.WeatherGroup;
import com.honeywell.lib.dialogs.PasswordDialog;
import com.honeywell.lib.dialogs.PasswordDialog.DataCallback;
import com.honeywell.lib.dialogs.VoiceDialog;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.BadgeView;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class HomeRootFragment extends SwipeToLoadFragment {
    private static final String TAG = HomeRootFragment.class.getSimpleName();
    TextView mCurrentScenario;
    GridView mScenarioGrid;
    Button mVoice;
    BadgeView mBadgeView;
    ImageView mZoneIcon;
    private ScenarioRootGridViewAdapter adapter;

    //数据
    private ArrayList<ScenarioLoop> scenarioLoops = new ArrayList<>();//获取数据库的最新scenario列表

    private WeatherGroup mWeatherGroup;
    VoiceDialog mVoiceDialog = null;

    @Override
    public int getLyaout() {
        return R.layout.fragment_main_home;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mWeatherGroup = (WeatherGroup) view.findViewById(R.id.weather_group);
        mWeatherGroup.setTitle(mTitle);
        mZoneIcon = (ImageView) view.findViewById(R.id.iv_zone_icon);
        mZoneIcon.setImageResource(HomeController.getInstance(getContext()).getScenarioSecurityStatusImageId(getContext()));

        mCurrentScenario = (TextView) view.findViewById(R.id.tv_scenario_name);
        mScenarioGrid = (GridView) view.findViewById(R.id.swipe_target);
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openLeftMenu();
            }
        });
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationListActivity.class);
                intent.putExtra(Constants.TITLE, getString(R.string.notification));
                startActivity(intent);
            }
        });
        mRight.setVisibility(LoginController.getInstance(getContext()).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD ? View.VISIBLE : View.INVISIBLE);
        mBadgeView = new BadgeView(getActivity(), mRight);
        mBadgeView.setTextSize(10);
        adapter = new ScenarioRootGridViewAdapter(getContext(), scenarioLoops, false, 6);
        mScenarioGrid.setAdapter(adapter);
        //点击
        mScenarioGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                /**
                 * add by H157925
                 * 增加判断，增加Scenario的执行
                 */
                final int scenarioId = adapter.getScenarioId(position);
                enableScenarioWithId(scenarioId);
            }
        });
        //长按
        mScenarioGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Loger.print(TAG, "ssd initView setOnItemLongClickListener onItemLongClick", Thread.currentThread());
                return true;
            }
        });

        mVoice = (Button) view.findViewById(R.id.btn_voice);
        mVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoiceDialog();
            }
        });
    }


    private void showVoiceDialog() {
        if (mVoiceDialog == null) {
            mVoiceDialog = new VoiceDialog(getActivity());
            mVoiceDialog.setRequestVoiceListener(new VoiceDialog.RequestVoiceListener() {
                @Override
                public boolean requestVoice(String result) {
                    LogUtil.e(TAG, "voice result = " + result);
                    if (!"".equalsIgnoreCase(result)) {
                        ScenarioLoop loop = VoiceRecgnitionController.manageVoiceControlWithResult(getContext(), result);
                        if (loop != null) {
                            Loger.print(TAG, "ssd find 需要的 loop ,loop name : " + loop.mScenarioName, Thread.currentThread());
                            enableScenarioWithId(loop.mScenarioId);
                            return true;
                        }
                    }
                    return false;
                }
            });

        }
        if (mVoiceDialog != null && !mVoiceDialog.isShowing()) {
            mVoiceDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVoiceDialog != null) {
            mVoiceDialog.destroy();
        }
    }

    @Override
    public void getData() {
        super.getData();
        /**
         * add by H157925 刷新天气
         */
        if (LoginController.getInstance(getContext()).getLoginType() != LoginController.LOGIN_TYPE_DISCONNECT) {
            getWeatherData();
        }
        updateView("初始化");
    }

    private void enableScenario(String password, int id) {
        ScenarioController.enableScenarioIdWithId(getContext(), id, password);
    }

    private void updateView(String reason) {
        Loger.print(TAG, "ssd updateView " + reason, Thread.currentThread());

        /**
         * add by H157925 add 当前Scenario
         */
        scenarioLoops = ScenarioController.getHomeScenarioList(getContext());
        adapter.setScenarioLoops(scenarioLoops);
        updateScenarioView();
    }

    private void enableScenarioWithId(final int scenarioId) {
        if (ScenarioController.checkIfNeedPassWord(getContext(), scenarioId)) {
            PasswordDialog passwordDialog = new PasswordDialog(getContext(), new DataCallback() {
                @Override
                public void getPassword(String password) {
                    enableScenario(password, scenarioId);
                }
            });
            if (passwordDialog != null && !passwordDialog.isShowing()) {
                passwordDialog.show();
            }
        } else {
            enableScenario(null, scenarioId);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeScenarioEvent) {
            final CubeScenarioEvent scenarioEvent = (CubeScenarioEvent) event;
            if (scenarioEvent.type == CubeEvents.CubeScenarioEventType.ENABLE_SCENARIO_SUCCESS) {
                if (scenarioEvent.success) {
                    updateScenarioView();
                } else {
                    showToastShort((String) scenarioEvent.object);
                }
            } else if (scenarioEvent.type == CubeEvents.CubeScenarioEventType.UPDATE_SYSTEM_SCENARIO_STATUS) {
                mZoneIcon.setImageResource(HomeController.getInstance(getContext()).getScenarioSecurityStatusImageId(getActivity()));
            }
        } else if (event instanceof CubeLoginEvent) {
            CubeLoginEvent loginEvent = (CubeLoginEvent) event;
            if (loginEvent.type == CubeEvents.CubeLoginEventType.LOGIN_WEBSOCKET_SUCCESS) {
                Loger.print(TAG, "ssd login websocket success", Thread.currentThread());
                ((MainActivity) getActivity()).dismissLoadingDialog();
                if (loginEvent.success) {
                    getData();
                } else {
                    String str = (String) loginEvent.item;
                    showToastShort(str);
                }

            }
        } else if (event instanceof CubeAlarmEvent) {
            CubeAlarmEvent ev = (CubeAlarmEvent) event;
            if (ev.type == CubeEvents.CubeAlarmEventType.GET_ALARM) {
                LogUtil.e("alinmi22", "CubeAlarmEvent =  " + AlarmController.getInstance(getActivity()).getUnReadAlarmCount());
                updateBadgeView();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.e("alinmi22", "onStart() getUnReadAlarmCount =  " + AlarmController.getInstance(getActivity()).getUnReadAlarmCount());
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("alinmi22", "onResume() getUnReadAlarmCount =  " + AlarmController.getInstance(getActivity()).getUnReadAlarmCount());
        updateBadgeView();

    }

    private void getWeatherData() {

        HomeController.getInstance(getContext()).getCubeDetailData(new WeatherResponceController() {
            @Override
            public void ResponceForCubeWeather(JSONObject jsonObject) {
                LogUtil.e("alinmi22", "ResponceForCubeWeather " + jsonObject.toString());
                mWeatherGroup.setTodayWeatherData(jsonObject);
            }

            @Override
            public void ResponceForWeatherInWeek(JSONObject jsonObject) {
                LogUtil.e("alinmi22", "ResponceForWeatherInWeek" + jsonObject.toString());
                mWeatherGroup.setWeekWeatherData(jsonObject);
            }

            @Override
            public void ResponceForAlarmCount(int count) {
                LogUtil.e("alinmi22", "ResponceForAlarmCount  count = " + count);
                updateBadgeView(count);
            }

            @Override
            public void ResponceForCubeLocation(String name) {
                mWeatherGroup.setLocation(name);
            }

            @Override
            public void ResponceForUpdateRoomState(final ArrayList<HomeRoomDetailsUIItem> roomDetailsUIItems) {
                mWeatherGroup.post(new Runnable() {
                    @Override
                    public void run() {
                        mWeatherGroup.setRoomLoop(roomDetailsUIItems);
                    }
                });
            }

            @Override
            public void ResponceForError(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private void updateScenarioTextView(int curScenarioId) {
        String scenarioName = ScenarioController.getScenarioNameFromScenarioId(getContext(), curScenarioId);
        Loger.print(TAG, "ssd ************** scenario name" + scenarioName, Thread.currentThread());
        mCurrentScenario.setText(scenarioName);
    }

    private void updateScenarioView() {
        int curScenarioId = ScenarioController.getCurrentScenarioID(getContext());
        Log.e(TAG, "curScenarioId:" + curScenarioId);
        adapter.setCurScenarioID(curScenarioId);
        adapter.notifyDataSetChanged();
        updateScenarioTextView(curScenarioId);
    }

    private void updateBadgeView() {
        updateBadgeView(AlarmController.getInstance(getActivity()).getUnReadAlarmCount());
    }

    private void updateBadgeView(int count) {
        if (LoginController.getInstance(getContext()).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            if (count > 0) {
                if (!mBadgeView.isShown()) {
                    mBadgeView.show();
                }
                if (count < 100) {
                    mBadgeView.setText(count + "");
                } else {
                    mBadgeView.setText("99+");
                }
            } else {
                if (mBadgeView.isShown()) {
                    mBadgeView.hide();
                }
            }
        }
    }
}
