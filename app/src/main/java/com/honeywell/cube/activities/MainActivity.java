
package com.honeywell.cube.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.ipc.Constants;
import com.honeywell.cube.ipc.StoragePath;
import com.honeywell.cube.ipc.nativeapi.P2PConn;
import com.honeywell.cube.net.service.CubeAppService;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeAlarmEvent;
import com.honeywell.cube.utils.events.CubeCallEvent;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ResourceUtil;
import com.honeywell.lib.utils.ToastUtil;
import com.nineoldandroids.view.ViewHelper;
import com.honeywell.cube.R;
import com.honeywell.cube.fragments.DeviceRootFragment;
import com.honeywell.cube.fragments.HomeRootFragment;
import com.honeywell.cube.fragments.RoomRootFragment;
import com.honeywell.cube.fragments.ScenarioRootFragment;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends CubeBaseFragmentActivity implements android.widget.TabHost.OnTabChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;

    /**
     * FragmentTabhost
     */
    private FragmentTabHost mTabHost;

    /**
     * 布局填充器
     */
    private LayoutInflater mLayoutInflater;

    /**
     * Fragment数组界面
     */
    public Class mFragmentArray[] = {
            HomeRootFragment.class, ScenarioRootFragment.class,
            DeviceRootFragment.class, RoomRootFragment.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        initCommonUtils();
//        startService();
        CommonCache.sP2PUUID = P2PConn.initConnLib(StoragePath.checkP2PDir(this));
        LogUtil.e(TAG, "-----------------> initConnLib  end sP2PUUID = " + CommonCache.sP2PUUID);
        showLoadingDialog();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLayoutInflater = LayoutInflater.from(this);
        // 找到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        // 得到fragment的个数
        int count = mFragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 给每个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(ResourceUtil.getStringArray(getResources(), R.array.main_tabbar_title)[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
            // 设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.main_tabbar_item_background);
        }
        mTabHost.getTabWidget().setDividerDrawable(null);
        final String customContent = PreferenceUtil.getCallNotificationCustomConent(MainActivity.this);
        if (!TextUtils.isEmpty(customContent)) {
            try {
//                showToastShort("---------customContent = " + customContent);
                ResponderController.newInstance(MainActivity.this).dealNetData(new JSONObject(customContent));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PreferenceUtil.saveCallNotificationCustomConent(MainActivity.this, "");
        }
    }

    public void setListener() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("START")) {

                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }

    /**
     * 给每个Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = mLayoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(ResourceUtil.getResourceIdArray(getResources(), R.array.main_tabbar_icon)[index]);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(ResourceUtil.getStringArray(getResources(), R.array.main_tabbar_title)[index]);

        return view;
    }

    /**
     * 初始化程序所需要的各种工具组件
     */
    private void initCommonUtils() {
        PreferenceUtil.init(MainActivity.this);
        Intent i = new Intent(this, LoginActivity.class);
        PreferenceUtil.commitBoolean(CommonData.APP_LOGIN_VIEW_IS_ADDED, true);
//        startActivity(i);
    }

    @Override
    public void onTabChanged(String arg0) {
        LogUtil.e("milton", "onTabChanged -> " + arg0);

    }

    public void openLeftMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.LEFT);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
            Loger.print(TAG, "ssd onBackPressed responce", Thread.currentThread());
            LoginController.getInstance(getApplicationContext()).logout(getApplicationContext());
        }
    }

    /**
     * 启动服务
     */
    public void startService() {
        CubeAppService.mContext = getApplicationContext();
        Intent intent = new Intent(MainActivity.this, CubeAppService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private CubeAppService myservice = null;//绑定的service对象
    //连接对象，重写OnserviceDisconnected和OnserviceConnected方法
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Loger.print("ssd", " service disconnect ********************** ", Thread.currentThread());
            myservice = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Loger.print("ssd", " service connect ********************** ", Thread.currentThread());
            myservice = ((CubeAppService.MyBinder) service).getService();
        }
    };

    @Override
    public void onEventMainThread(CubeEvents event) {
        if (event instanceof CubeBasicEvent) {
            final CubeBasicEvent cubeBasicEvent = (CubeBasicEvent) event;
            if (cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.NORMAL_MSG_EVENT) {
                //播放特定音乐
            }
            if (cubeBasicEvent.getMessage() != null) {
                final String str = cubeBasicEvent.getMessage();
                showToastShort(str);
            }
            if (cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.TIME_OUT) {
                ToastUtil.showShort(this, cubeBasicEvent.getMessage());
                dismissLoadingDialog();
            } else if (cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.CONNECTING_LOST) {
                dismissLoadingDialog();
                finish();
                startActivity(new Intent(this, WelcomeActivity.class));
            }
        } else if (event instanceof CubeCallEvent) {
            CubeCallEvent callEvent = (CubeCallEvent) event;
            if (callEvent.type == CubeEvents.CubeCallEventType.CALL_START) {
                //呼叫进入
                Loger.print(TAG, "ssd service incoming call", Thread.currentThread());
                //启动新的Activity 目前测试
                Intent dialogIntent = new Intent(this, CallScreenActivity.class);
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.EXTRA_DATA_CALLMSG, callEvent.cameraInfo);
                dialogIntent.putExtras(bundle);
                startActivity(dialogIntent);
            }
        } else if (event instanceof CubeAlarmEvent) {
            CubeAlarmEvent event1 = (CubeAlarmEvent) event;
            ToastUtil.showShort(this, event1.alarmLoopName, getString(R.string.alarm) + ": " + event1.alarmType, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        P2PConn.unInitConnLib();
//        XGPushManager.unregisterPush(getApplicationContext(), new XGIOperateCallback() {
//            @Override
//            public void onSuccess(Object o, int i) {
//                LogUtil.d("TPush", "注销成功，设备token为：" + o);
//            }
//
//            @Override
//            public void onFail(Object o, int i, String s) {
//                LogUtil.d("TPush", "注销失败，错误码：" + i + ",错误信息：" + s);
//            }
//        });
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    ToastUtil.showShort(MainActivity.this, R.string.double_click_exit, true);
                    firstTime = secondTime;
                    return true;
                } else {
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
