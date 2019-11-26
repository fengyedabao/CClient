package com.honeywell.cube.activities;

import com.honeywell.cube.R;
import com.honeywell.lib.widgets.swipetoloadlayout.OnLoadMoreListener;
import com.honeywell.lib.widgets.swipetoloadlayout.OnRefreshListener;
import com.honeywell.lib.widgets.swipetoloadlayout.SwipeToLoadLayout;

public class SwipeToLoadActivity extends CubeTitleBarActivity implements OnRefreshListener, OnLoadMoreListener {

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
        dismissLoadingDialog();
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    protected int getContent() {
        return R.layout.activity_list_swipe_to_load;
    }


    protected void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
    }

    @Override
    public void dismissLoadingDialog() {
        super.dismissLoadingDialog();
        if (swipeToLoadLayout != null && swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }
}
