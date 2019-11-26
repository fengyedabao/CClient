package com.honeywell.cube.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.CheckTextAdapter;
import com.honeywell.cube.adapter.NotificationListAdapter;
import com.honeywell.cube.controllers.NotificationController;
import com.honeywell.cube.controllers.UIItem.notifi.NotifiFilterObject;
import com.honeywell.cube.controllers.UIItem.notifi.NotifiUIItem;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeNotifiEvent;
import com.honeywell.cube.widgets.CheckItem;
import com.honeywell.lib.utils.ScreenUtil;
import com.honeywell.lib.utils.ToastUtil;
import com.honeywell.lib.widgets.HeadListView;
import com.honeywell.lib.widgets.MaxHeightListView;
import com.honeywell.lib.widgets.swipetoloadlayout.OnLoadMoreListener;
import com.honeywell.lib.widgets.swipetoloadlayout.OnRefreshListener;
import com.honeywell.lib.widgets.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;

public class NotificationListActivity extends CubeTitleBarActivity implements OnRefreshListener, OnLoadMoreListener {
    HeadListView mListView;
    NotificationListAdapter mAdapter;
    private PopupWindow mPopupWindow;
    ArrayList<NotifiFilterObject> mFilter;

    public static final String TAG = NotificationListActivity.class.getSimpleName();

    private SwipeToLoadLayout swipeToLoadLayout;

    @Override
    public void onPause() {
        super.onPause();
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onRefresh() {
//        LogUtil.e("alinmi22", mFilter == null ? " null " : (" mFilter = " + mFilter.size() + mFilter));
        NotificationController.requestNotification(NotificationListActivity.this, mFilter, true);
    }

    @Override
    public void onLoadMore() {
        NotificationController.requestNotification(NotificationListActivity.this, mFilter, false);
    }

    protected void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        mFilter = NotificationController.getNotifiFilterList(this);
        mListView = (HeadListView) findViewById(R.id.swipe_target);
        mAdapter = new NotificationListAdapter(this, getDataList(), getLoadingDialog());
        mListView.setAdapter(mAdapter);
        mListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_notification_section, mListView, false));
        mListView.addScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1 && !ViewCompat.canScrollVertically(view, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mListView.addScrollListener(mAdapter);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected void initRightIcon(final ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.notification_check);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopupWindow(right);
            }
        });

    }

    @Override
    protected int getContent() {
        return R.layout.activity_notification_list;
    }

    private void filterNotification(ArrayList<NotifiFilterObject> list) {
        mFilter = list;
        swipeToLoadLayout.setRefreshing(true);
    }


    private void showFilterPopupWindow(View view) {
        if (mPopupWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.window_notification_filter, null);
            final CheckItem selectAll = (CheckItem) contentView.findViewById(R.id.ci_select_all);
            final MaxHeightListView listview = (MaxHeightListView) contentView.findViewById(R.id.lv_list);

            selectAll.setTextName(R.string.select_all);
            selectAll.setOnCkeckItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckTextAdapter) listview.getAdapter()).selectAll(selectAll.isChecked());
                }
            });

            listview.setListViewHeight(ScreenUtil.getScreenHeight(this) * 2 / 3);
            listview.setAdapter(new CheckTextAdapter(this, getFilterDataList(), selectAll));


            Button sure = (Button) contentView.findViewById(R.id.btn_sure);
            sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    CheckTextAdapter adapter = (CheckTextAdapter) listview.getAdapter();
                    adapter.backupData();
                    filterNotification(adapter.getCheckedList());
                }
            });


            mPopupWindow = new PopupWindow(contentView, View.MeasureSpec.makeMeasureSpec(ScreenUtil.getScreenWidth(this) * 2 / 3, View.MeasureSpec.AT_MOST), ViewGroup.LayoutParams.WRAP_CONTENT, true);

            mPopupWindow.setOutsideTouchable(true);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            ((CheckTextAdapter) ((ListView) mPopupWindow.getContentView().findViewById(R.id.lv_list)).getAdapter()).restoreData();
            mPopupWindow.showAsDropDown(view, 0, 0);
        }
    }

    private ArrayList<NotifiFilterObject> getFilterDataList() {
        return mFilter;
    }

    public ArrayList<NotifiUIItem> getDataList() {
        ArrayList<NotifiUIItem> dataList = new ArrayList<>();
        return dataList;
    }

    public ArrayList<NotifiUIItem> getDataList(CubeNotifiEvent event) {
        ArrayList<NotifiUIItem> dataList = new ArrayList<>();
        if (event != null) {
            dataList = event.list;
        }
//        LogUtil.e("alinmi22", dataList == null ? " null " : (" dataList = " + dataList.size() + dataList));
        return dataList;
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeNotifiEvent) {
            CubeNotifiEvent ev = (CubeNotifiEvent) event;
            if (ev.type == CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_REFRESH) {
                mAdapter = new NotificationListAdapter(this, getDataList(ev), getLoadingDialog());
//                mAdapter.notifyDataSetChanged();
                mListView.setAdapter(mAdapter);
                // 隐藏头布局
                swipeToLoadLayout.setRefreshing(false);
                //清除原有AlarmCount 数量
                PreferenceUtil.setAlarmCount(getApplicationContext(), 0);
            } else if (ev.type == CubeEvents.CubeNotifiEventType.GET_NOTIFI_LIST_LOADMORE) {
                mAdapter.getDataList().addAll(ev.list);
                mAdapter.notifyDataSetChanged();
                swipeToLoadLayout.setLoadingMore(false);
            } else if (ev.type == CubeEvents.CubeNotifiEventType.NOTIFI_PLAY_IPC_VIDEO) {
                dismissLoadingDialog();
                if (!ev.success) {
                    ToastUtil.showShort(this, ev.message);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("alinmi222", "onActivityResult data = " + data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
                getData();
            }
        }
    }

    public void playVideo(final int position) {
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                NotificationController.videoButtonPressed(getApplicationContext(), (NotifiUIItem) mAdapter.getItem(position));
            }
        });
    }
}
