package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.controllers.ScenarioController;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.fragments.ScenarioEditDeviceFragment;
import com.honeywell.cube.fragments.ScenarioEditZoneFragment;
import com.honeywell.cube.fragments.ScenarioRootFragment;
import com.honeywell.cube.fragments.TabViewPagerAdapter;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeScenarioEvent;
import com.honeywell.lib.widgets.slidetablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Map;

public class ScenarioEditSecondActivity extends CubeBaseFragmentActivity {
    protected ImageView mLeft;
    protected ImageView mRight;
    protected TextView mTitle;
    protected ScenarioLoop mScenarioLoop;
    ViewPager mViewPager;
    ArrayList<UIItems> mDeviceList;
    ArrayList<UIItems> mZoneList;
    boolean mUseZone = false;
    private ArrayList<UIItems> mSelectedDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_edit_second);
        initView();
    }


    protected void initView() {
        Intent intent = getIntent();
        mScenarioLoop = intent.getParcelableExtra(ScenarioRootFragment.SCENARIO_LOOP);
        initTitleBar();
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TabViewPagerAdapter(getSupportFragmentManager(), this));
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);// 是否填充满屏幕的宽度
        // slidingTabLayout.setTabViewBackground(R.drawable.milton_background_selector);
        slidingTabLayout.setTabTextColor(R.color.scenario_edit_text_color);
        // slidingTabLayout.setTabTextSize(20);
        slidingTabLayout.setViewPager(mViewPager);

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.base_color_blue);
//                        getResources().getColor(R.color.base_color_blue, getTheme());
//                return Color.RED;
            }
        });
    }

    protected void initTitleBar() {
        mLeft = (ImageView) findViewById(R.id.iv_left);
        mRight = (ImageView) findViewById(R.id.iv_right);
        mTitle = (TextView) findViewById(R.id.tv_title);
        initLeftIcon(mLeft);
        initRightIcon(mRight);
        initTitle(mTitle);
    }

    protected void initLeftIcon(ImageView left) {
        left.setImageResource(R.mipmap.nav_back_normal);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                final ArrayList<UIItems> device = getDeviceDataList();
                final ArrayList<UIItems> zone = getZoneDataList();
                if ((device == null || device.size() == 0) && (zone == null || zone.size() == 0)) {
                    dismissLoadingDialog();
                    showToastShort(R.string.scenario_need_device_zone_tip);
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            ScenarioController.addOrEditScenarioWithInfo(ScenarioEditSecondActivity.this, mScenarioLoop, getDeviceDataList(), getZoneDataList());
                        }
                    }.start();

                }
            }
        });
    }


    protected void initTitle(TextView title) {
        title.setText(R.string.edit_scenario_setting);
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeScenarioEvent) {
            CubeScenarioEvent ev = (CubeScenarioEvent) event;
            if (ev.type == CubeEvents.CubeScenarioEventType.EDIT_GET_DEVICE_ZONE_ARR) {
                Map<String, Object> map = (Map<String, Object>) ev.object;
                ArrayList<UIItems> deviceList = (ArrayList<UIItems>) map.get(CommonData.SCENARIO_EDIT_GET_DEVICES);
                ArrayList<UIItems> zoneList = (ArrayList<UIItems>) map.get(CommonData.SCENARIO_EDIT_GET_ZONES);
                initZoneList(zoneList);
                initDeviceList(deviceList);
                ((TabViewPagerAdapter) mViewPager.getAdapter()).updateUI();
            } else if (ev.type == CubeEvents.CubeScenarioEventType.CONFIG_SCENARIO_STATE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                    ScenarioEditSecondActivity.this.setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToastShort((String) ev.object);
                }
            } else if (ev.type == CubeEvents.CubeScenarioEventType.CONFIG_SELECTED_DEVICES) {
                mSelectedDeviceList = (ArrayList<UIItems>) ev.object;
                ((TabViewPagerAdapter) mViewPager.getAdapter()).updateUI();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getData() {
        ScenarioController.getScenarioDeviceAndZoneList(this, mScenarioLoop);
    }

    public ScenarioLoop getScenarioLoop() {
        return mScenarioLoop;
    }

    public void initDeviceList(ArrayList<UIItems> deviceList) {
        mDeviceList = deviceList;
    }

    public void initZoneList(ArrayList<UIItems> zoneList) {

//        mZoneList = new ArrayList<UIItems>();
        if (zoneList != null && zoneList.size() > 0) {
            mUseZone = zoneList.get(0).type == ModelEnum.UI_TYPE_LIST;
            zoneList.remove(0);
            mZoneList = zoneList;

        } else {
            mUseZone = false;
            mZoneList = null;
        }


    }

    public ArrayList<UIItems> getZoneList() {
        return mZoneList;
    }


    public ArrayList<UIItems> getDeviceList() {
        if (mDeviceList == null || mDeviceList.size() == 0) {
            return mSelectedDeviceList;
        } else {
            if (mSelectedDeviceList == null || mSelectedDeviceList.size() == 0) {
                return mDeviceList;
            } else {
                final int size = mSelectedDeviceList.size();
                for (int i = 0; i < size; i++) {
                    final UIItems item = mSelectedDeviceList.get(i);
                    if (item.type == ModelEnum.UI_TYPE_TITLE) {
                        continue;
                    } else {
                        BasicLoop loop = (BasicLoop) item.object;
                        final int size2 = mDeviceList.size();
                        for (int j = 0; j < size2; j++) {
                            final UIItems item2 = mDeviceList.get(j);
                            if (item2.type != ModelEnum.UI_TYPE_TITLE) {
                                final BasicLoop loop2 = (BasicLoop) item2.object;
                                if (loop.mLoopId == loop2.mLoopId && loop.mModuleType == loop2.mModuleType) {
                                    mSelectedDeviceList.remove(i);
                                    mSelectedDeviceList.add(i, item2);
                                    mDeviceList.remove(j);
                                    break;
                                }
                            }
                        }
                    }
                }
                return mSelectedDeviceList;
            }
        }
    }

    public ArrayList<UIItems> getDeviceDataList() {
        return ((ScenarioEditDeviceFragment) ((TabViewPagerAdapter) mViewPager.getAdapter()).getFragment(0)).getDeviceDataList();
    }

    public ArrayList<UIItems> getZoneDataList() {
        return ((ScenarioEditZoneFragment) ((TabViewPagerAdapter) mViewPager.getAdapter()).getFragment(1)).getZoneDataList();
    }

    //    public
    public boolean isUseZone() {
        return mUseZone;
    }


}
