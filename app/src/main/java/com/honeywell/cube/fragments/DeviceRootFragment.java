package com.honeywell.cube.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.DeviceDetailActivity;
import com.honeywell.cube.activities.DeviceWIACActivity;
import com.honeywell.cube.activities.DeviceWICommonActivity;
import com.honeywell.cube.activities.DeviceWICustomActivity;
import com.honeywell.cube.adapter.IconTextBaseAdapter;
import com.honeywell.cube.adapter.IconTextGridAdapter;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.DeviceControllers.DeviceListType;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Constants;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ResourceUtil;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;


import java.util.ArrayList;
import java.util.List;

public class DeviceRootFragment extends SwipeToLoadFragment {
    private static final String TAG = DeviceRootFragment.class.getSimpleName();
    GridView mGridDevice;
    public static final boolean DEBUG_NO_NET = false;

    @Override
    public int getLyaout() {
        return R.layout.fragment_main_device;
    }

    @Override
    public int getIndex() {
        return 2;
    }

    public void initView(View view) {
        mGridDevice = (GridView) view.findViewById(R.id.swipe_target);


        mGridDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String type = ((IconTextBaseAdapter.ItemBean) mGridDevice.getAdapter().getItem(position)).getType();
                LogUtil.e(TAG, " type = " + type);
                Intent intent = null;

                switch (type) {
                    case ModelEnum.MAIN_IR_DVD:
                    case ModelEnum.MAIN_IR_STB:
                    case ModelEnum.MAIN_IR_TELEVISION:
                        intent = new Intent(getActivity(), DeviceWICommonActivity.class);
                        break;
                    case ModelEnum.MAIN_IR_AC:
                        intent = new Intent(getActivity(), DeviceWIACActivity.class);
                        break;
                    case ModelEnum.MAIN_IR_CUSTOMIZE:
                        intent = new Intent(getActivity(), DeviceWICustomActivity.class);
                        break;
                    case ModelEnum.MAIN_CALL_ELEVATOR:
                        DeviceController.callElevator(getContext());
                        return;
                    default:
                        intent = new Intent(getActivity(), DeviceDetailActivity.class);
                        break;
                }
                intent.putExtra(Constants.DEVICE_TYPE, type);
                getActivity().startActivity(intent);
            }
        });
    }

    public List<IconTextBaseAdapter.ItemBean> getDataListDummy() {

        List<IconTextBaseAdapter.ItemBean> dataList = new ArrayList<IconTextBaseAdapter.ItemBean>();

//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_light, null, "灯光", "main_light"));
//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_curtain, null, "窗帘", "main_curtain"));
//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_relay, null, "开关", "main_relay"));
//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_zone, null, "防区", "main_zone"));
//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_ip_camera, null, "IP摄像头", "main_ip_camera"));
//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_air_conditioner, null, "空调", "main_air_conditioner"));
//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_backaudio, null, "背景音乐", "main_backaudio"));
//        dataList.add(new IconTextBaseAdapter.ItemBean(R.mipmap.device_type_purifier, null, "机顶盒", "main_purifier"));

        return dataList;

    }

    public List<IconTextBaseAdapter.ItemBean> getDataList(CubeDeviceEvent event) {

        List<IconTextBaseAdapter.ItemBean> dataList = new ArrayList<IconTextBaseAdapter.ItemBean>();

        ArrayList<DeviceListType> datas = (ArrayList<DeviceListType>) event.getUpdateStatusData();
        if (datas != null && datas.size() > 0) {
            final int size = datas.size();
            for (int i = 0; i < size; i++) {
                final DeviceListType data = datas.get(i);
//                final int resId=ResourceUtil.getResIdFromName(getActivity(), data.deviceImageName);
//                final String type=getString(NAME_MAP.get(data.deviceName));
                dataList.add(new IconTextBaseAdapter.ItemBean(ResourceUtil.getResIdFromName(getActivity(), data.deviceImageName), null, getString(Constants.DEVICE_TYPE_MAP.get(data.deviceName)), data.deviceName, null));
            }
        }

        return dataList;

    }

    @Override
    public void getData() {
        super.getData();
        if (DEBUG_NO_NET) {
            mGridDevice.setAdapter(new IconTextGridAdapter(getDataListDummy()));
        } else {
            DeviceController.getAllDeviceTypeList(getActivity());
        }
    }

    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeDeviceEvent) {
            if (((CubeDeviceEvent) event).getType() == CubeEvents.CubeDeviceEventType.GET_DEVICE_TYPE_LIST) {
                mGridDevice.setAdapter(new IconTextGridAdapter(getDataList((CubeDeviceEvent) event)));
            }
        }
    }
}
