package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.SelectRoomIconAdapter;
import com.honeywell.cube.controllers.RoomController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.fragments.RoomRootFragment;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRoomEvent;
import com.honeywell.lib.utils.ResourceUtil;
import com.honeywell.lib.widgets.GridViewGallery;

import java.util.ArrayList;
import java.util.List;

public class RoomEditActivity extends CubeTitleBarActivity {
    protected RoomLoop mRoomLoop;
    private EditText mEditName;
    private SelectRoomIconAdapter mAdapter;
    private String mOperationType;


    @Override
    protected int getContent() {
        return R.layout.activity_room_edit;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO

                if (TextUtils.isEmpty(mEditName.getText().toString())) {
                    showToastShort(R.string.room_need_name_tip);
                    return;
                }
                showLoadingDialog();
                if (Constants.OPERATION_ADD.equalsIgnoreCase(mOperationType)) {
                    mRoomLoop = new RoomLoop();
                }
                mRoomLoop.mRoomName = mEditName.getText().toString();

                mRoomLoop.mImageName = ResourceUtil.getResName(RoomEditActivity.this, getDataList().get(mAdapter.getSelectedPosition()).mIconid);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        RoomController.addOrEditRoomWithInfo(RoomEditActivity.this, mRoomLoop);
                    }
                }.start();
            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.main_tabbar_text_room);
    }

    protected void initView() {
        Intent intent = getIntent();
        mOperationType = intent.getStringExtra(Constants.OPERATION_TYPE);
        mEditName = (EditText) findViewById(R.id.et_name);
        final GridViewGallery gvg = (GridViewGallery) findViewById(R.id.gv_icon);
        mAdapter = new SelectRoomIconAdapter(this, getDataList());
        gvg.setAdapter(mAdapter);
        gvg.setNumColumns(2);
        gvg.setNumRows(2);
        gvg.setOnItemClickListener(new GridViewGallery.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedPosition(position);
                gvg.notifyDataSetChanged();
            }
        });
        if (Constants.OPERATION_EDIT.equalsIgnoreCase(mOperationType)) {
            mRoomLoop = intent.getParcelableExtra(RoomRootFragment.ROOM_LOOP);
            mEditName.setText(mRoomLoop.mRoomName);
            mAdapter.setSelectedPosition(getSelectedIconPosition(mRoomLoop.mImageName));
        }
    }

    public int getSelectedIconPosition(String imageName) {
        int pos = 0;
        List<SelectRoomIconAdapter.ItemBean> list = getDataList();
        if (list != null && list.size() > 0) {
            final int resId = ResourceUtil.getResIdFromName(this, imageName);
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                if (resId == list.get(i).mIconid) {
                    pos = i;
                    break;
                }
            }
        }
        return pos;
    }

    private List<SelectRoomIconAdapter.ItemBean> getDataList() {
        List<SelectRoomIconAdapter.ItemBean> list = new ArrayList<SelectRoomIconAdapter.ItemBean>();
        final int[] res = ResourceUtil.getResourceIdArray(getResources(), R.array.room_icon_list);
        final int length = res.length;
        for (int i = 0; i < length; i++) {
            list.add(new SelectRoomIconAdapter.ItemBean("", res[i]));
        }
        return list;
    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeRoomEvent) {
            CubeRoomEvent ev = (CubeRoomEvent) event;
            if (ev.type == CubeEvents.CubeRoomEventType.CONFIG_ROOM_STATE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    finish();
                } else {
                    showToastShort((String) ev.object);
                }
            }
        }
    }
}
