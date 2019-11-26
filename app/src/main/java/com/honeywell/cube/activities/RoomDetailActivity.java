package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.lib.adapter.ExpandableListAdapter;
import com.honeywell.cube.adapter.RoomDetailAdapter;
import com.honeywell.cube.controllers.RoomController;
import com.honeywell.cube.controllers.UIItem.IPCameraListDetail;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RoomLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.BackAudioCustom;
import com.honeywell.cube.fragments.RoomRootFragment;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRoomEvent;
import com.honeywell.lib.widgets.ListViewCompat;
import com.honeywell.lib.widgets.SlideView;

import java.util.ArrayList;

public class RoomDetailActivity extends SwipeToLoadActivity {
    protected RoomLoop mRoomLoop;
    private ListViewCompat mContent;
    private RoomDetailAdapter mAdapter;

    @Override
    protected void initTitle(TextView title) {
        title.setText(mRoomLoop.mRoomName);
    }

    @Override
    protected void initIntentValue() {
        mRoomLoop = getIntent().getParcelableExtra(RoomRootFragment.ROOM_LOOP);

    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (ListViewCompat) findViewById(R.id.swipe_target);
    }

    @Override
    protected void getData() {
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                RoomController.getRoomDeviceStateWithRoomName(RoomDetailActivity.this, mRoomLoop.mPrimaryId);
            }
        });
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeRoomEvent) {
            CubeRoomEvent ev = (CubeRoomEvent) event;
            if (ev.type == CubeEvents.CubeRoomEventType.UPDATE_ROOM_DEVICE_STATE) {
                mAdapter = new RoomDetailAdapter(this, getDataList(ev), getLoadingDialog());
                mContent.setAdapter(getExpandableListAdapter(mAdapter));
                dismissLoadingDialog();
            }
        } else if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            if (ev.getType() == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE) {
                dismissLoadingDialog();
                if (ev.getSuccess()) {
                    mAdapter.updateDeleteUI();
                } else {
                    showToastShort(ev.getMessage());
                }
            }
        } else if (event instanceof CubeBasicEvent) {
            if (((CubeBasicEvent) event).getType() == CubeEvents.CubeBasicEventType.PROGRESS_STATUS) {
                dismissLoadingDialog();
            }
        }

    }

    public ArrayList<RoomDetailAdapter.ItemBean> getDataList(CubeRoomEvent event) {
        ArrayList<RoomDetailAdapter.ItemBean> dataList = new ArrayList<>();
        ArrayList<UIItems> list = (ArrayList<UIItems>) event.object;
        if (list != null && list.size() > 0) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                final UIItems item = list.get(i);
                RoomDetailAdapter.ItemBean itemBean = null;
                BasicLoop loop = null;
//                LogUtil.e("alinmi21","(BasicLoop) item.object = "+(BasicLoop) item.object);
                if (item.object instanceof BasicLoop) {
                    loop = (BasicLoop) item.object;
                }
                switch (item.looptype) {
                    case ModelEnum.SPARKLIGHTING:
                        final SparkLightingLoop l = (SparkLightingLoop) item.object;

                        switch (l.mLoopType) {
                            case ModelEnum.LOOP_TYPE_LIGHT_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_EXPANDABLE_Light, R.mipmap.device_type_light, l.mLoopName, item.looptype, l.customStatus.status, l.customStatus.openClosePercent, l);
                                break;
                            case ModelEnum.LOOP_TYPE_CURTAIN_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_EXPANDABLE_CURTAIN, R.mipmap.device_type_curtain, l.mLoopName, item.looptype, l);
                                break;
                            case ModelEnum.LOOP_TYPE_RELAY_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_DEFAULT, R.mipmap.device_type_relay, l.mLoopName, item.looptype, l.customStatus.status, l);
                                break;
                            case ModelEnum.LOOP_TYPE_SWITCH_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_DEFAULT, R.mipmap.device_type_light, l.mLoopName, item.looptype, l.customStatus.status, l);
                                break;
                            case ModelEnum.LOOP_TYPE_SENSOR_INT:
                            case ModelEnum.LOOP_TYPE_5800PIRAP_INT:
                            case ModelEnum.LOOP_TYPE_5804EU_INT:
                            case ModelEnum.LOOP_TYPE_5816EU_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_ZONE, R.mipmap.device_type_zone, l.mLoopName, item.looptype, l);
                                break;
                            default:
                                break;
                        }

                        break;
                    case ModelEnum.WIRELESS_315_433:
                        Wireless315M433MLoop l2 = (Wireless315M433MLoop) item.object;
                        switch (l2.mLoopType) {
                            case ModelEnum.LOOP_TYPE_LIGHT_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_EXPANDABLE_Light, R.mipmap.device_type_light, l2.mLoopName, item.looptype, l2.customStatus.status, l2.customStatus.openClosePercent, l2);
                                break;
                            case ModelEnum.LOOP_TYPE_CURTAIN_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_EXPANDABLE_CURTAIN, R.mipmap.device_type_curtain, l2.mLoopName, item.looptype, l2);
                                break;
                            case ModelEnum.LOOP_TYPE_RELAY_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_DEFAULT, R.mipmap.device_type_relay, l2.mLoopName, item.looptype, l2.customStatus.status, l2);
                                break;
                            case ModelEnum.LOOP_TYPE_SWITCH_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_DEFAULT, R.mipmap.device_type_light, l2.mLoopName, item.looptype, l2.customStatus.status, l2);
                                break;
                            case ModelEnum.LOOP_TYPE_SENSOR_INT:
                            case ModelEnum.LOOP_TYPE_5800PIRAP_INT:
                            case ModelEnum.LOOP_TYPE_5804EU_INT:
                            case ModelEnum.LOOP_TYPE_5816EU_INT:
                                itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_ZONE, R.mipmap.device_type_zone, l2.mLoopName, item.looptype, l2);
                                break;
                            default:
                                break;
                        }
                        break;
                    case ModelEnum.LOOP_RELAY:
                        RelayLoop l4 = (RelayLoop) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_DEFAULT, R.mipmap.device_type_relay, l4.mLoopName, item.looptype, l4.customStatus.status, l4);
                        break;
                    case ModelEnum.LOOP_ZONE:
//                        WiredZoneLoop l5 = (WiredZoneLoop) item.object;
//                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_ZONE,R.mipmap.device_type_zone, loop.mLoopName,item.looptype, loop);
//                        break;
                    case ModelEnum.LOOP_IPVDP:
//                        IpvdpZoneLoop l6 = (IpvdpZoneLoop) item.object;
//                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_ZONE,R.mipmap.device_type_zone, l6.mLoopName,item.looptype, l6);
//                        break;
                    case ModelEnum.LOOP_5804EU:
//                        Wireless315M433MLoop l7 = (Wireless315M433MLoop) item.object;
//                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_ZONE,R.mipmap.device_type_zone, l7.mLoopName,item.looptype, l7);
//                        break;
                    case ModelEnum.LOOP_5816EU:
//                        Wireless315M433MLoop l8 = (Wireless315M433MLoop) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_ZONE, R.mipmap.device_type_zone, loop.mLoopName, item.looptype, loop);
                        break;
                    case ModelEnum.WIFI_485:
                        Wifi485Loop l3 = (Wifi485Loop) item.object;
                        if (l3.mLoopType.equalsIgnoreCase(ModelEnum.LOOP_485_VENTILATION)) {
                            //TODO LOOP_485_VENTILATION
                        } else {
                            itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.device_type_air_conditioner, loop.mLoopName, item.looptype, loop);
                        }
                        break;
                    case ModelEnum.LOOP_BACNET:
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.device_type_air_conditioner, loop.mLoopName, item.looptype, loop);
                        break;
                    case ModelEnum.LOOP_BACKAUDIO:
                        BackaudioLoop l10 = (BackaudioLoop) item.object;
                        final BackAudioCustom bc = l10.customModel;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_EXPANDABLE_BACKAUDIO, R.mipmap.device_type_backaudio, item.looptype, l10.mLoopName, ModelEnum.BACKAUDIO_STATUS_PAUSE_S.equalsIgnoreCase(bc.playstatus), ModelEnum.BACKAUDIO_STATUS_MUTE_S.equalsIgnoreCase(bc.mute)
                                , ModelEnum.BACKAUDIO_STATUS_POWER_ON.equalsIgnoreCase(bc.power) ? bc.songname : getString(R.string.offline), 0, bc.playTimeStr + " | " + bc.allplaytimeStr, bc.volume, bc.volume + "", l10);
                        break;
                    case ModelEnum.LOOP_IPC:
                        IPCameraListDetail l11 = (IPCameraListDetail) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_IPC, R.mipmap.device_type_ip_camera, l11.title, l11.details, item.looptype, l11.ipcStreamInfo);
                        break;
                    case ModelEnum.LOOP_IR_TV:
//                        IrLoop l16 = (IrLoop) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.ir_television, loop.mLoopName, item.looptype, loop);
                        break;
                    case ModelEnum.LOOP_IR_STB:
//                        IrLoop l16 = (IrLoop) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.ir_stb, loop.mLoopName, item.looptype, loop);
                        break;
                    case ModelEnum.LOOP_IR_AC:
//                        IrLoop l16 = (IrLoop) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.device_type_air_conditioner, loop.mLoopName, item.looptype, loop);
                        break;
                    case ModelEnum.LOOP_IR_DVD:
//                        IrLoop l16 = (IrLoop) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.ir_dvd, loop.mLoopName, item.looptype, loop);
                        break;
                    case ModelEnum.LOOP_IR_CUSTOM:
//                        IrLoop l16 = (IrLoop) item.object;
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.ir_customize, loop.mLoopName, item.looptype, loop);
                        break;
                    case ModelEnum.LOOP_VENTILATION:
                        itemBean = new RoomDetailAdapter.ItemBean(RoomDetailAdapter.TYPE_SIMPLE_JUMP, R.mipmap.device_type_ventilation, loop.mLoopName, item.looptype, loop);
                        break;
                    default:
                        break;

                }
                if (itemBean != null) {
                    dataList.add(itemBean);
                }
            }
        }
        return dataList;
    }

    private ExpandableListAdapter getExpandableListAdapter(BaseAdapter adapter) {
        final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(adapter, R.id.iv_expandable, R.id.thumb_layout, R.id.expandable_layout);
        expandableListAdapter.setOnItemExpandCollapseListener(new ExpandableListAdapter.OnItemExpandCollapseListener() {

            @Override
            public void onItemExpandCollapse(View view, int position, int type) {
//                Log.e("alinmi", "onItemExpandCollapse   type = " + expandableListAdapter.getItemViewType(position));
                final int itemType = expandableListAdapter.getItemViewType(position);
                if (itemType == RoomDetailAdapter.TYPE_EXPANDABLE_BACKAUDIO) {
                    initViewBackground(view, type);
                    view.findViewById(R.id.rl_close_title).setVisibility(type == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE ? View.VISIBLE : View.GONE);
                    view.findViewById(R.id.tv_music_name_secondary).setVisibility(type == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE ? View.GONE : View.VISIBLE);
                } else if (itemType == RoomDetailAdapter.TYPE_EXPANDABLE_CURTAIN || itemType == RoomDetailAdapter.TYPE_EXPANDABLE_Light) {
                    initViewBackground(view, type);
                } else {
                    initViewBackground(view, ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE);
                }
            }

            @Override
            public void onItemInitStatus(View view, int position, int type) {
                initViewBackground(view, type);
            }

            @Override
            public boolean onItemNeedExpand(View view, int position) {
                if (view instanceof SlideView) {
                    return !((SlideView) view).isSlided();
                } else {
                    return true;
                }
            }
        });
        return expandableListAdapter;
    }

    private void initViewBackground(View view, int type) {
        view.setBackgroundResource(type == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE ? R.color.item_collapse_background : R.color.item_expand_background);
        if (view instanceof SlideView) {
            ((SlideView) view).setSlidable(type == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
                getData();
            }
        }
    }
}
