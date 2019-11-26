package com.honeywell.cube.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.RoomDetailActivity;
import com.honeywell.cube.activities.RoomEditActivity;
import com.honeywell.cube.adapter.IconTextBaseAdapter;
import com.honeywell.cube.adapter.RoomRootGridAdapter;
import com.honeywell.cube.controllers.RoomController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRoomEvent;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.utils.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class RoomRootFragment extends SwipeToLoadFragment {
    private static final String TAG = RoomRootFragment.class.getSimpleName();
    BottomDialog mEditDialog;
    public static final String ROOM_LOOP = "room_loop";
    private RoomRootGridAdapter mAdapter;
    private GridView mContent;
    private ArrayList<RoomLoop> mRoomLoops;

    @Override
    public int getLyaout() {
        return R.layout.fragment_main_room;
    }

    @Override
    public int getIndex() {
        return 3;
    }

    public void initView(View view) {
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RoomEditActivity.class);
                intent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_ADD);
                getActivity().startActivity(intent);
            }
        });
        mContent = (GridView) view.findViewById(R.id.swipe_target);

        mContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RoomDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(ROOM_LOOP, mRoomLoops.get(position));
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
        mContent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                                        Intent intent = new Intent(getActivity(), RoomEditActivity.class);
                                        intent.putExtra(Constants.OPERATION_TYPE, Constants.OPERATION_EDIT);
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(ROOM_LOOP, mRoomLoops.get(index));
                                        intent.putExtras(bundle);
                                        getActivity().startActivity(intent);
                                    } else {
                                        RoomController.deleteRoomWithRoomLoop(getActivity(), mRoomLoops.get(index));
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

    private List<IconTextBaseAdapter.ItemBean> getDataList(CubeRoomEvent event) {
        List<IconTextBaseAdapter.ItemBean> list = new ArrayList<>();
        mRoomLoops = (ArrayList<RoomLoop>) event.object;
        if (mRoomLoops != null && mRoomLoops.size() > 0) {
            final int size = mRoomLoops.size();
            for (int i = 0; i < size; i++) {
                final RoomLoop room = mRoomLoops.get(i);
                list.add(new IconTextBaseAdapter.ItemBean(ResourceUtil.getResIdFromName(getActivity(), room.mImageName), null, room.mRoomName, "", null));
            }
        }
        return list;
    }

    @Override
    public void getData() {
        super.getData();
        RoomController.getAllRoomList(getActivity());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeRoomEvent) {
            final CubeRoomEvent roomEvent = (CubeRoomEvent) event;
            if (roomEvent.type == CubeEvents.CubeRoomEventType.GET_ROOM_LIST) {
                mAdapter = new RoomRootGridAdapter(getDataList(roomEvent));
                mContent.setAdapter(mAdapter);
            } else if (roomEvent.type == CubeEvents.CubeRoomEventType.CONFIG_ROOM_STATE) {
                getData();
            }
        }
    }
}
