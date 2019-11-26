package com.honeywell.cube.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.MainActivity;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;
import com.honeywell.lib.utils.ResourceUtil;
import com.honeywell.lib.utils.ToastUtil;
import com.honeywell.lib.widgets.swipetoloadlayout.OnLoadMoreListener;
import com.honeywell.lib.widgets.swipetoloadlayout.OnRefreshListener;
import com.honeywell.lib.widgets.swipetoloadlayout.SwipeToLoadLayout;

import de.greenrobot.event.EventBus;

public abstract class SwipeToLoadFragment extends BaseRootFragment implements OnRefreshListener, OnLoadMoreListener {
    private static final String TAG = SwipeToLoadFragment.class.getSimpleName();
    protected SwipeToLoadLayout swipeToLoadLayout;

    @Override
    public void onPause() {
        super.onPause();
        if (swipeToLoadLayout != null) {
            if (swipeToLoadLayout.isRefreshing()) {
                swipeToLoadLayout.setRefreshing(false);
            }
            if (swipeToLoadLayout.isLoadingMore()) {
                swipeToLoadLayout.setLoadingMore(false);
            }
        }
//        dismissLoadingDialog();
    }

    @Override
    public void onRefresh() {
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
//                MenuModuleController.findNewModule(ModuleListActivity.this);
                LoginController.getInstance(getContext()).updateCubeConfig();
            }
        });

    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLyaout(), container, false);
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        mLeft = (ImageView) view.findViewById(R.id.iv_left);
        mRight = (ImageView) view.findViewById(R.id.iv_right);
        mTitle = (TextView) view.findViewById(R.id.tv_title);
        final int index = getIndex();
        final int leftId = ResourceUtil.getResourceIdArray(getResources(), R.array.main_header_icon_left)[index];
        final int rightId = ResourceUtil.getResourceIdArray(getResources(), R.array.main_header_icon_right)[index];
        final String title = ResourceUtil.getStringArray(getResources(), R.array.main_header_title)[index];
        if (leftId > 0) {
            mLeft.setImageResource(leftId);
        }
        if (rightId > 0) {
            mRight.setImageResource(rightId);
        }
        mTitle.setText(title);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView(view);
        getData();
        return view;
    }

    protected void startAsynchronousOperation(Runnable run) {
        showLoadingDialog();
        new Thread(run).start();
    }

    public void onEventMainThread(CubeEvents event) {
        if (event instanceof CubeBasicEvent) {
            final CubeBasicEvent cubeBasicEvent = (CubeBasicEvent) event;

            if (cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.TIME_OUT) {
                ToastUtil.showShort(getActivity(), cubeBasicEvent.getMessage());
                dismissLoadingDialog();

            }
        } else if (event instanceof CubeLoginEvent) {
            final CubeLoginEvent ev = (CubeLoginEvent) event;
            if (ev.type == CubeEvents.CubeLoginEventType.LOGIN_UPDATE_CONFIG) {
                dismissLoadingDialog();
                getData();
            }
        }
    }

    public void showLoadingDialog() {
        ((MainActivity) getActivity()).showLoadingDialog();
    }

    public void dismissLoadingDialog() {
        ((MainActivity) getActivity()).dismissLoadingDialog();
        if (swipeToLoadLayout != null && swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }
}
