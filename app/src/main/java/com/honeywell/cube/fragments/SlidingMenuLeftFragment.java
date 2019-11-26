package com.honeywell.cube.fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.honeywell.cube.R;
import com.honeywell.cube.activities.AboutActivity;
import com.honeywell.cube.activities.AccountSettingActivity;
import com.honeywell.cube.activities.CubeSettingActivity;
import com.honeywell.cube.activities.DeviceListActivity;
import com.honeywell.cube.activities.LoginActivity;
import com.honeywell.cube.activities.ModuleListActivity;
import com.honeywell.cube.activities.RuleListActivity;
import com.honeywell.cube.activities.ScheduleListActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuAccountUIItem;
import com.honeywell.cube.widgets.CircleImageView;
import com.honeywell.cube.adapter.IconTextBaseAdapter;
import com.honeywell.cube.adapter.IconTextListAdapter;
import com.honeywell.cube.controllers.AccountController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeHeadInfoEvent;
import com.honeywell.cube.utils.events.CubeLoginEvent;
import com.honeywell.lib.utils.ResourceUtil;
import com.honeywell.lib.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class SlidingMenuLeftFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View rootView;// 缓存Fragment view
    private IconTextListAdapter mAdapter;
    List<IconTextBaseAdapter.ItemBean> mDataList = new ArrayList<>();
    private AlertDialog mLogoutDialog;
    private TextView textViewNameOut;
    private TextView textViewNameIn;
    private CircleImageView circleImageView;
    private ImageView mImageViewLoginStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_sliding_left_menu, null);
            Button logout = (Button) rootView.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutDialog();
                }
            });
            ListView lv = (ListView) rootView.findViewById(R.id.list_menu);
            mAdapter = new IconTextListAdapter(getDataList(getResources()));
            lv.setAdapter(mAdapter);
            lv.setOnItemClickListener(this);
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        circleImageView = (CircleImageView) rootView.findViewById(R.id.circle_avatar_view);
        textViewNameOut = (TextView) rootView.findViewById(R.id.tv_name_outside);
        textViewNameIn = (TextView) rootView.findViewById(R.id.tv_name_inside);
        mImageViewLoginStatus = (ImageView) rootView.findViewById(R.id.iv_login_status);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (LoginController.getInstance(getContext()).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI || LoginController.getInstance(getContext()).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
            updateAccountInfo();
        }
    }

    public void onEventMainThread(CubeEvents event) {
        if (event instanceof CubeLoginEvent) {
            CubeLoginEvent loginEvent = (CubeLoginEvent) event;
            if (loginEvent.type == CubeEvents.CubeLoginEventType.LOGIN_WEBSOCKET_SUCCESS) {
                updateAccountInfo();
            }
        }
        if (event instanceof CubeHeadInfoEvent) {
            CubeHeadInfoEvent event1 = (CubeHeadInfoEvent) event;
            if (event1.type == CubeEvents.CubeHeadINfoUpdateEventType.HEAD_ICON_CHANGE) {
                String imgPath = PreferenceUtil.getUserHeadPic(getContext());
                if (!TextUtils.isEmpty(imgPath)) {
                    Glide.with(getContext()).load(imgPath).into(circleImageView);
                }
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getContext(), AccountSettingActivity.class);
                        startActivity(intent);


                    }
                });
            } else if (event1.type == CubeEvents.CubeHeadINfoUpdateEventType.OUT_SIDE_INFO_CHANGE) {
//                textViewNameOut.setText(AccountController.getAccountUserName(getContext()));
//                textViewNameIn.setText(AccountController.getCubeName(getContext()));

            } else if (event1.type == CubeEvents.CubeHeadINfoUpdateEventType.IN_SIDE_INFO_CHANGE) {
//                textViewNameOut.setText(AccountController.getAccountUserName(getContext()));
//                textViewNameIn.setText(AccountController.getCubeName(getContext()));

            }

        }
    }


    /**
     * add by H157925
     */
    private void updateAccountInfo() {

        MenuAccountUIItem menuAccountUIItem = AccountController.getAccountLoginInfo(this.getActivity());

        String imgPath = PreferenceUtil.getUserHeadPic(getContext());
        if (!TextUtils.isEmpty(imgPath)) {
            Glide.with(getContext()).load(imgPath).into(circleImageView);
        } else {

            circleImageView.setImageResource(R.mipmap.account_default);
//            menuAccountUIItem.loginImageName;
        }


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (LoginController.getInstance(getContext()).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), AccountSettingActivity.class);
                    startActivity(intent);
                } else if (LoginController.getInstance(getContext()).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), CubeSettingActivity.class);
                    intent.putExtra("into_type", 200);
                    startActivity(intent);
                }

            }
        });

        textViewNameOut.setText(menuAccountUIItem.loginName_down);
        textViewNameIn.setText(menuAccountUIItem.loginName_up);


        if (menuAccountUIItem.loginImageName == -1) {
            mImageViewLoginStatus.setVisibility(View.GONE);
            return;
        }

        Log.e("~~~~~~~~~~~~", "+++++" + menuAccountUIItem.loginImageName);
        mImageViewLoginStatus.setVisibility(View.VISIBLE);
        mImageViewLoginStatus.setImageResource(menuAccountUIItem.loginImageName);

    }

    private void showLogoutDialog() {
        if (null == mLogoutDialog) {
            mLogoutDialog = new AlertDialog.Builder(
                    getActivity()).create();
            mLogoutDialog.show();
            Window window = mLogoutDialog.getWindow();
            window.setContentView(R.layout.dialog_logout);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.AnimBottom);
            Button ok = (Button) window.findViewById(R.id.btn_ok);
            Button cancel = (Button) window.findViewById(R.id.btn_cancel);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginController.getInstance(getContext()).logout(getActivity().getApplicationContext());
                    mLogoutDialog.cancel();
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLogoutDialog.dismiss();
                }
            });
        } else {
            mLogoutDialog.show();
        }

    }

    private List<IconTextBaseAdapter.ItemBean> getDataList(Resources res) {
        mDataList.clear();
        final int resourceId[] = ResourceUtil.getResourceIdArray(res, R.array.sliding_menu_left_icon);
        final String text[] = ResourceUtil.getStringArray(res, R.array.sliding_menu_left_title);
        final int length = resourceId.length;
        for (int i = 0; i < length; i++) {
            mDataList.add(new IconTextBaseAdapter.ItemBean(resourceId[i], null, text[i], "", null));
        }
        return mDataList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            getActivity().startActivity(new Intent(getActivity(), RuleListActivity.class));
        } else if (position == 1) {
            getActivity().startActivity(new Intent(getActivity(), ScheduleListActivity.class));
        } else if (position == 2) {
            getActivity().startActivity(new Intent(getActivity(), ModuleListActivity.class));
        } else if (position == 3) {
            getActivity().startActivity(new Intent(getActivity(), DeviceListActivity.class));
        } else if (position == 4) {
            getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
        } else {
            ToastUtil.showShort(parent.getContext(), mAdapter.getItemList().get(position).getText() + "is clicked");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

}
