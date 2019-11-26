package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.AirConditionerListAdapter;
import com.honeywell.cube.adapter.BackaudioListAdapter;
import com.honeywell.cube.adapter.CurtainListAdapter;
import com.honeywell.cube.adapter.DeviceListBaseAdapter;
import com.honeywell.cube.adapter.VentilationListAdapter;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.VentilationLoop;
import com.honeywell.lib.adapter.ExpandableListAdapter;
import com.honeywell.cube.adapter.IPCameraListAdapter;
import com.honeywell.cube.adapter.LightListAdapter;
import com.honeywell.cube.adapter.RelayListAdapter;
import com.honeywell.cube.adapter.ZoneListAdapter;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.UIItem.IPCameraListDetail;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BacnetLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpvdpZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wifi485Loop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.WiredZoneLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.db.configuredatabase.DeviceStatus.BackAudioCustom;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.SlideView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceDetailActivity extends SwipeToLoadActivity {
    ListView mListView;
    String mItemType;
    String mItemTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = (ListView) findViewById(R.id.swipe_target);
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        Intent intent = getIntent();
        mItemType = intent.getStringExtra(Constants.DEVICE_TYPE);
        mItemTitle = getString(Constants.DEVICE_TYPE_MAP.get(mItemType));
        LogUtil.e("alinmi", "mItemType = " + mItemType + " , mItemTitle = " + mItemTitle);
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(mItemTitle);
    }

    @Override
    protected void getData() {
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                DeviceController.getDeviceListWithName(DeviceDetailActivity.this, mItemType);
            }
        });
    }

    private void initDataList(String type, CubeDeviceEvent event) {
        if (getString(R.string.device_type_light).equalsIgnoreCase(type)) {
            initLightList(event);
        } else if (getString(R.string.device_type_curtain).equalsIgnoreCase(type)) {
            initCurtainList(event);
        } else if (getString(R.string.device_type_relay).equalsIgnoreCase(type)) {
            initRelayList(event);
        } else if (getString(R.string.device_type_zone).equalsIgnoreCase(type)) {
            initZoneList(event);
        } else if (getString(R.string.device_type_ip_camera).equalsIgnoreCase(type)) {
            initIPCameraList(event);
        } else if (getString(R.string.device_type_air_conditioner).equalsIgnoreCase(type)) {
            initAirConditionerList(event);
        } else if (getString(R.string.device_type_backaudio).equalsIgnoreCase(type)) {
            initBackaudioList(event);
        } else if (getString(R.string.device_type_purifier).equalsIgnoreCase(type)) {
            initPurifierList(event);
        } else if (getString(R.string.device_type_ir_dvd).equalsIgnoreCase(type)) {
            initIrDVDList(event);
        } else if (getString(R.string.device_type_ir_stb).equalsIgnoreCase(type)) {
            initIrStbList(event);
        } else if (getString(R.string.device_type_ir_customize).equalsIgnoreCase(type)) {
            initIrCustomizeList(event);
        } else if (getString(R.string.device_type_ir_ac).equalsIgnoreCase(type)) {
            initIrAcList(event);
        } else if (getString(R.string.device_type_ir_television).equalsIgnoreCase(type)) {
            initIrTelevisionList(event);
        } else if (getString(R.string.device_type_ventilation).equalsIgnoreCase(type)) {
            initVentilationList(event);
        }
    }

    private void initVentilationList(CubeDeviceEvent event) {
        mListView.setAdapter(new VentilationListAdapter(this, getVentilationDataList(event), getLoadingDialog()));
    }

    private ArrayList<VentilationListAdapter.ItemBean> getVentilationDataList(CubeDeviceEvent event) {
        ArrayList<VentilationListAdapter.ItemBean> dataList = new ArrayList<>();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            final ArrayList<Object> loop1 = map.get(ModelEnum.LOOP_VENTILATION);
            if (null != loop1 && loop1.size() > 0) {
                for (Object loop : loop1) {
                    final VentilationLoop l = (VentilationLoop) loop;
                    dataList.add(new VentilationListAdapter.ItemBean(R.mipmap.device_type_ventilation, null, l.mLoopName, l, ModelEnum.LOOP_VENTILATION));
                }
            }
        }
        return dataList;
    }

    private void initIrDVDList(CubeDeviceEvent event) {

    }

    private void initIrStbList(CubeDeviceEvent event) {

    }

    private void initIrCustomizeList(CubeDeviceEvent event) {

    }

    private void initIrAcList(CubeDeviceEvent event) {

    }

    private void initIrTelevisionList(CubeDeviceEvent event) {

    }

    private void initRelayList(CubeDeviceEvent event) {
        mListView.setAdapter(new RelayListAdapter(this, getRelayDataList(event), getLoadingDialog()));
    }

    private ArrayList<DeviceListBaseAdapter.ItemBean> getRelayDataList(CubeDeviceEvent event) {
        ArrayList<DeviceListBaseAdapter.ItemBean> dataList = new ArrayList<>();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            final ArrayList<Object> loopRelay = map.get(ModelEnum.LOOP_RELAY);
            final ArrayList<Object> loopSparkLighting = map.get(ModelEnum.SPARKLIGHTING);
            if (null != loopRelay && loopRelay.size() > 0) {
                for (Object relay : loopRelay) {
                    final RelayLoop l = (RelayLoop) relay;
                    dataList.add(new RelayListAdapter.ItemBean(R.mipmap.device_type_relay, null, l.mLoopName, l.customStatus.status, l, ModelEnum.LOOP_RELAY));
                }
            }
            if (null != loopSparkLighting && loopSparkLighting.size() > 0) {
                for (Object relay : loopSparkLighting) {
                    final SparkLightingLoop l = (SparkLightingLoop) relay;
                    dataList.add(new RelayListAdapter.ItemBean(R.mipmap.device_type_relay, null, l.mLoopName, l.customStatus.status, l, ModelEnum.SPARKLIGHTING));
                }
            }
        }
        return dataList;
    }

    private void initLightList(CubeDeviceEvent event) {
        LightListAdapter adapter = new LightListAdapter(DeviceDetailActivity.this, getLightDataList(event), getLoadingDialog());
        mListView.setAdapter(getExpandableListAdapter(adapter));
    }

    private ArrayList<LightListAdapter.ItemBean> getLightDataList(CubeDeviceEvent event) {
        ArrayList<LightListAdapter.ItemBean> dataList = new ArrayList<>();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            final ArrayList<Object> loopWirekess = map.get(ModelEnum.WIRELESS_315_433);
            final ArrayList<Object> loopSparkLighting = map.get(ModelEnum.SPARKLIGHTING);
            if (null != loopWirekess && loopWirekess.size() > 0) {
                for (Object relay : loopWirekess) {
                    final Wireless315M433MLoop l = (Wireless315M433MLoop) relay;
                    dataList.add(new LightListAdapter.ItemBean(l.mLoopType == ModelEnum.LOOP_TYPE_SWITCH_INT ? LightListAdapter.TYPE_SIMPLE : LightListAdapter.TYPE_EXPANDABLE, R.mipmap.device_type_light, null, l.mLoopName, l.customStatus.openClosePercent, l.customStatus.status, l, ModelEnum.WIRELESS_315_433));
                }
            }
            if (null != loopSparkLighting && loopSparkLighting.size() > 0) {
                for (Object relay : loopSparkLighting) {
                    final SparkLightingLoop l = (SparkLightingLoop) relay;
                    dataList.add(new LightListAdapter.ItemBean(l.mLoopType == ModelEnum.LOOP_TYPE_SWITCH_INT ? LightListAdapter.TYPE_SIMPLE : LightListAdapter.TYPE_EXPANDABLE, R.mipmap.device_type_light, null, l.mLoopName, l.customStatus.openClosePercent, l.customStatus.status, l, ModelEnum.SPARKLIGHTING));
                }
            }
        }
        return dataList;
    }

    private void initCurtainList(CubeDeviceEvent event) {
        CurtainListAdapter adapter = new CurtainListAdapter(this, getCurtainDataList(event), getLoadingDialog());
        mListView.setAdapter(getExpandableListAdapter(adapter));
    }

    private ArrayList<CurtainListAdapter.ItemBean> getCurtainDataList(CubeDeviceEvent event) {
        ArrayList<CurtainListAdapter.ItemBean> dataList = new ArrayList<>();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            final ArrayList<Object> loopWirekess = map.get(ModelEnum.WIRELESS_315_433);
            final ArrayList<Object> loopSparkLighting = map.get(ModelEnum.SPARKLIGHTING);
            if (null != loopWirekess && loopWirekess.size() > 0) {
                for (Object relay : loopWirekess) {
                    final Wireless315M433MLoop l = (Wireless315M433MLoop) relay;
                    dataList.add(new CurtainListAdapter.ItemBean(R.mipmap.device_type_curtain, null, l.mLoopName, l, ModelEnum.WIRELESS_315_433));
                }
            }
            if (null != loopSparkLighting && loopSparkLighting.size() > 0) {
                for (Object relay : loopSparkLighting) {
                    final SparkLightingLoop l = (SparkLightingLoop) relay;
                    dataList.add(new CurtainListAdapter.ItemBean(R.mipmap.device_type_curtain, null, l.mLoopName, l, ModelEnum.SPARKLIGHTING));
                }
            }
        }
        return dataList;
    }

    private void initZoneList(CubeDeviceEvent event) {
        mListView.setAdapter(new ZoneListAdapter(this, getZoneDataList(event), getLoadingDialog()));
    }

    private ArrayList<ZoneListAdapter.ItemBean> getZoneDataList(CubeDeviceEvent event) {
        ArrayList<ZoneListAdapter.ItemBean> dataList = new ArrayList<>();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            //LOOP_ZONE
            final ArrayList<Object> loop1 = map.get(ModelEnum.LOOP_ZONE);
            if (null != loop1 && loop1.size() > 0) {
                for (Object zone : loop1) {
                    final WiredZoneLoop l = (WiredZoneLoop) zone;
                    dataList.add(new ZoneListAdapter.ItemBean(-1, null, l.mLoopName, l, ""));
                }
            }
            //LOOP_IPVDP
            final ArrayList<Object> loop2 = map.get(ModelEnum.LOOP_IPVDP);
            if (null != loop2 && loop2.size() > 0) {
                for (Object zone : loop2) {
                    final IpvdpZoneLoop l = (IpvdpZoneLoop) zone;
                    dataList.add(new ZoneListAdapter.ItemBean(-1, null, l.mLoopName, l, ""));
                }
            }
            //SPARKLIGHTING
            final ArrayList<Object> loop3 = map.get(ModelEnum.SPARKLIGHTING);
            if (null != loop3 && loop3.size() > 0) {
                for (Object zone : loop3) {
                    final SparkLightingLoop l = (SparkLightingLoop) zone;
                    dataList.add(new ZoneListAdapter.ItemBean(-1, null, l.mLoopName, l, ""));
                }
            }
            //WIRELESS_315_433
            final ArrayList<Object> loop4 = map.get(ModelEnum.WIRELESS_315_433);
            if (null != loop4 && loop4.size() > 0) {
                for (Object zone : loop4) {
                    final Wireless315M433MLoop l = (Wireless315M433MLoop) zone;
                    dataList.add(new ZoneListAdapter.ItemBean(-1, null, l.mLoopName, l, ""));
                }
            }
            //LOOP_5804EU
            final ArrayList<Object> loop5 = map.get(ModelEnum.LOOP_5804EU);
            if (null != loop5 && loop5.size() > 0) {
                for (Object zone : loop5) {
                    final Wireless315M433MLoop l = (Wireless315M433MLoop) zone;
                    dataList.add(new ZoneListAdapter.ItemBean(-1, null, l.mLoopName, l, ""));
                }
            }
            //LOOP_5816EU
            final ArrayList<Object> loop6 = map.get(ModelEnum.LOOP_5816EU);
            if (null != loop6 && loop6.size() > 0) {
                for (Object zone : loop6) {
                    final Wireless315M433MLoop l = (Wireless315M433MLoop) zone;
                    dataList.add(new ZoneListAdapter.ItemBean(-1, null, l.mLoopName, l, ""));
                }
            }

        }
        return dataList;
    }

    private void initIPCameraList(CubeDeviceEvent event) {
        mListView.setAdapter(new IPCameraListAdapter(this, getIPCameraDataList(event), getLoadingDialog()));
    }

    private ArrayList<IPCameraListAdapter.ItemBean> getIPCameraDataList(CubeDeviceEvent event) {
        ArrayList<IPCameraListAdapter.ItemBean> dataList = new ArrayList<>();
        //TODO
        ArrayList<IPCameraListDetail> list = (ArrayList<IPCameraListDetail>) event.getUpdateStatusData();
        if (list != null && list.size() > 0) {
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                final IPCameraListDetail item = list.get(i);
                dataList.add(new IPCameraListAdapter.ItemBean(R.mipmap.device_type_ip_camera, item.title, item.details, item.ipcStreamInfo));
            }
        }
        return dataList;
    }

    private void initAirConditionerList(CubeDeviceEvent event) {
        mListView.setAdapter(new AirConditionerListAdapter(this, getAirConditionerDataList(event), getLoadingDialog()));
    }

    private ArrayList<AirConditionerListAdapter.ItemBean> getAirConditionerDataList(CubeDeviceEvent event) {
        ArrayList<AirConditionerListAdapter.ItemBean> dataList = new ArrayList<>();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            final ArrayList<Object> loop1 = map.get(ModelEnum.LOOP_BACNET);
            final ArrayList<Object> loop2 = map.get(ModelEnum.WIFI_485);
            if (null != loop1 && loop1.size() > 0) {
                for (Object loop : loop1) {
                    final BacnetLoop l = (BacnetLoop) loop;
                    dataList.add(new AirConditionerListAdapter.ItemBean(R.mipmap.device_type_air_conditioner, null, l.mLoopName, l, ModelEnum.LOOP_BACNET));
                }
            }
            if (null != loop2 && loop2.size() > 0) {
                for (Object loop : loop2) {
                    final Wifi485Loop l = (Wifi485Loop) loop;
                    dataList.add(new AirConditionerListAdapter.ItemBean(R.mipmap.device_type_air_conditioner, null, l.mLoopName, l, ModelEnum.WIFI_485));
                }
            }
        }
        return dataList;
    }

    private void initBackaudioList(CubeDeviceEvent event) {
        BackaudioListAdapter adapter = new BackaudioListAdapter(this, getBackaudioDataList(event), getLoadingDialog());
        mListView.setAdapter(getBackaudioExpandableListAdapter(adapter));
    }

    private ArrayList<BackaudioListAdapter.ItemBean> getBackaudioDataList(CubeDeviceEvent event) {
        ArrayList<BackaudioListAdapter.ItemBean> dataList = new ArrayList<>();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            final ArrayList<Object> loopBackaudio = map.get(ModelEnum.LOOP_BACKAUDIO);
            if (null != loopBackaudio && loopBackaudio.size() > 0) {
                for (Object backaudio : loopBackaudio) {
                    final BackaudioLoop l = (BackaudioLoop) backaudio;
                    final BackAudioCustom bc = l.customModel;
                    dataList.add(new BackaudioListAdapter.ItemBean(l.mLoopName, ModelEnum.BACKAUDIO_STATUS_PAUSE_S.equalsIgnoreCase(bc.playstatus), ModelEnum.BACKAUDIO_STATUS_MUTE_S.equalsIgnoreCase(bc.mute)
                            , ModelEnum.BACKAUDIO_STATUS_POWER_ON.equalsIgnoreCase(bc.power) ? bc.songname : getString(R.string.offline), 0, bc.playTimeStr + " | " + bc.allplaytimeStr, bc.volume, bc.volume + "", l));
                }
            }
        }
        return dataList;
    }


    private void initPurifierList(CubeDeviceEvent event) {

    }


    private ArrayList<CurtainListAdapter.ItemBean> getPurifierDataList(CubeDeviceEvent event) {
        ArrayList<CurtainListAdapter.ItemBean> dataList = new ArrayList<>();
        //TODO
        ArrayList<RelayLoop> list = (ArrayList<RelayLoop>) event.getUpdateStatusData();
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            RelayLoop data = list.get(i);
//            dataList.add(new IconTextSwitchListAdapter.ItemBean(R.mipmap.device_type_relay, null, data.mLoopName, false));
        }
        return dataList;
    }

    private ExpandableListAdapter getBackaudioExpandableListAdapter(BaseAdapter adapter) {
        final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(adapter, R.id.iv_expandable, R.id.thumb_layout, R.id.expandable_layout);
        expandableListAdapter.setOnItemExpandCollapseListener(new ExpandableListAdapter.OnItemExpandCollapseListener() {
            @Override
            public void onItemExpandCollapse(View view, int position, int type) {
//                Log.e("alinmi", "onItemExpandCollapse   type = " + expandableListAdapter.getItemViewType(position));
                if (expandableListAdapter.getItemViewType(position) != LightListAdapter.TYPE_SIMPLE) {
                    initViewBackground(view, type);
                    view.findViewById(R.id.rl_close_title).setVisibility(type == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE ? View.VISIBLE : View.GONE);
                    view.findViewById(R.id.tv_music_name_secondary).setVisibility(type == ExpandableListAdapter.ExpandCollapseAnimation.COLLAPSE ? View.GONE : View.VISIBLE);
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

    private ExpandableListAdapter getExpandableListAdapter(BaseAdapter adapter) {
        final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(adapter, R.id.iv_expandable, R.id.thumb_layout, R.id.expandable_layout);
        expandableListAdapter.setOnItemExpandCollapseListener(new ExpandableListAdapter.OnItemExpandCollapseListener() {
            @Override
            public void onItemExpandCollapse(View view, int position, int type) {
                if (expandableListAdapter.getItemViewType(position) != LightListAdapter.TYPE_SIMPLE) {
                    initViewBackground(view, type);
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
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        LogUtil.e("alinmi", "onEventMainThread  event = " + event);
        if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            LogUtil.e("alinmi", "onEventMainThread   event.getType() = " + ((CubeDeviceEvent) event).getType());
            if (((CubeDeviceEvent) event).getType() == CubeEvents.CubeDeviceEventType.UPDATE_DEVICE_STATE) {
                initDataList(mItemTitle, (CubeDeviceEvent) event);
                dismissLoadingDialog();
            } else if (((CubeDeviceEvent) event).getType() == CubeEvents.CubeDeviceEventType.UPDATE_BACKAUDIO_STATE_FROM_EVENT) {
                if (mListView.getAdapter() != null) {
                    final BackaudioListAdapter backaudioListAdapter = (BackaudioListAdapter) ((ExpandableListAdapter) mListView.getAdapter()).getWrappedAdapter();
                    updateBackaudioUI((CubeDeviceEvent) event, backaudioListAdapter);
                }
            } else if (ev.getType() == CubeEvents.CubeDeviceEventType.CONFIGURE_DEVICE_STATE_DELETE) {
                dismissLoadingDialog();
                if (ev.getSuccess()) {
                    if (mListView.getAdapter() instanceof ExpandableListAdapter) {
                        ((DeviceListBaseAdapter) ((ExpandableListAdapter) mListView.getAdapter()).getWrappedAdapter()).updateDeleteUI();
                    } else {
                        ((DeviceListBaseAdapter) mListView.getAdapter()).updateDeleteUI();
                    }
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

    private void updateBackaudioUI(CubeDeviceEvent event, BackaudioListAdapter backaudioListAdapter) {
        final List<? extends DeviceListBaseAdapter.ItemBean> dataList = backaudioListAdapter.getDataList();
        Map<String, ArrayList<Object>> map = (Map<String, ArrayList<Object>>) event.getUpdateStatusData();
        if (null != map) {
            final ArrayList<Object> loopBackaudio = map.get(ModelEnum.LOOP_BACKAUDIO);
            if (null != loopBackaudio && loopBackaudio.size() > 0) {
                for (Object backaudio : loopBackaudio) {
                    final BackaudioLoop l = (BackaudioLoop) backaudio;
                    final int loopId = l.mLoopId;
                    for (int i = 0; i < dataList.size(); i++) {
                        final BackaudioListAdapter.ItemBean itemBean = (BackaudioListAdapter.ItemBean) dataList.get(i);
                        final BackaudioLoop loop = (BackaudioLoop) itemBean.mLoop;
                        if (loopId == loop.mLoopId) {
                            itemBean.mProgressTip = l.customModel.playTimeStr + " | " + l.customModel.allplaytimeStr;
                            itemBean.mStatus = l.customModel.songname;
                            itemBean.mProgress = l.customModel.playtime / l.customModel.allplaytime;
                            break;
                        }
                    }
                }
            }
        }
        backaudioListAdapter.notifyDataSetChanged();
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