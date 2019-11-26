package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.DeviceListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.qrcode.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends CubeTitleBarActivity {
    public static String DEVICE_SCAN_QR_CODE = "扫描设备二维码";
    public static String DEVICE_SPARK_LIGHTING = "Spark Lighting";
    public static String DEVICE_WIRELESS_INFRARED = "无线红外";
    public static String DEVICE_WIRELESS_SWITCH = "无线开关";
    public static String DEVICE_WIRED_AREA = "有线防区";
    public static String DEVICE_BACNET_AIR_CONDITIONER = "Bacnet 空调";
    public static String DEVICE_IP_CAMERA = "IP 摄像头";
    public static String DEVICE_IPVDP_AREA = "IPVDP 防区";
    public static String DEVICE_MUSIC = "音乐";
    public static String DEVICE_485_DEVICE = "485 设备";
    public static String DEVICE_VENTILATION = "空气净化器";

    BottomDialog mAddDeviceDialog;

    List<BottomDialog.ItemBean> mAddDeviceDataList;
    DeviceListAdapter mAdapter;
    ListView mContent;

    @Override
    protected int getContent() {
        return R.layout.activity_list;
    }


    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_add);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDeviceDialog();
            }
        });
    }


    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.menu_device);
    }

    protected void initView() {
        DEVICE_SCAN_QR_CODE = getString(R.string.menu_device_add_device_scan);
        DEVICE_SPARK_LIGHTING = getString(R.string.menu_device_add_spark_lighting);
        DEVICE_WIRELESS_INFRARED = getString(R.string.menu_device_add_wifi_ir);
        DEVICE_WIRELESS_SWITCH = getString(R.string.menu_device_add_wifi_relay);
        DEVICE_WIRED_AREA = getString(R.string.menu_device_add_wired_zone);
        DEVICE_BACNET_AIR_CONDITIONER = getString(R.string.menu_device_add_bacnet_ac);
        DEVICE_IP_CAMERA = getString(R.string.menu_device_add_ip_camera);
        DEVICE_IPVDP_AREA = getString(R.string.menu_device_add_ipvdp_zone);
        DEVICE_MUSIC = getString(R.string.menu_device_add_backaudio);
        DEVICE_485_DEVICE = getString(R.string.menu_device_add_485_device);
        DEVICE_VENTILATION = getString(R.string.menu_device_add_ventilation);
        mContent = (ListView) findViewById(R.id.lv_list);
        mAdapter = new DeviceListAdapter(this, getDataList(null), null);
        mContent.setAdapter(mAdapter);
        mContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeviceListActivity.this, DeviceSubListActivity.class);
                intent.putExtra(Constants.DEVICE_TYPE, mAdapter.getDataList().get(position).mDeviceType);
                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void getData() {
//        super.getData();
//        MenuDeviceController.getAllDeviceList(this);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        MenuDeviceController.getAllDeviceList(this);
    }

    private List<BottomDialog.ItemBean> getAddDeviceDataList() {
        if (null == mAddDeviceDataList) {
            mAddDeviceDataList = new ArrayList<>();
            ArrayList<String> list = MenuDeviceController.getAddDeviceList(this);
            if (list != null && list.size() > 0) {
                final int size = list.size();
                for (int i = 0; i < size; i++) {
                    mAddDeviceDataList.add(new BottomDialog.ItemBean(list.get(i), null));
                }
            }
        }
        return mAddDeviceDataList;
    }

    private void showAddDeviceDialog() {

        if (null == mAddDeviceDialog) {
            mAddDeviceDialog = new BottomDialog(this);
            mAddDeviceDialog.setViewCreateListener(
                    new BottomDialog.ViewCreateListener() {
                        @Override
                        public void initTop(TextView top) {
                            top.setVisibility(View.GONE);
                        }

                        @Override
                        public void initContent(ListView content) {
                            content.setAdapter(new BottomDialog.ListAdapter(DeviceListActivity.this, -1, getAddDeviceDataList(), true, new BottomDialog.ListAdapter.OnItemClickListener() {
                                @Override
                                public void itemClick(View view, int position, int index) {
                                    final String text = mAddDeviceDataList.get(position).mText;
                                    if (DEVICE_SCAN_QR_CODE.equalsIgnoreCase(text)) {
                                        Intent intent = new Intent(DeviceListActivity.this, CaptureActivity.class);
                                        intent.putExtra("capture_type", ModelEnum.SCANED_TYPE_ADD_CUBE_DEVICE);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(DeviceListActivity.this, DeviceAddDetailActivity.class);
                                        intent.putExtra(Constants.TITLE, getString(R.string.configure));
                                        if (DEVICE_SPARK_LIGHTING.equalsIgnoreCase(text)) {
                                            intent = new Intent(DeviceListActivity.this, DeviceAddListActivity.class);
                                            intent.putExtra(Constants.TITLE, text);
                                        } else if (DEVICE_WIRELESS_INFRARED.equalsIgnoreCase(text)) {
                                            intent = new Intent(DeviceListActivity.this, DeviceAddWirelessInfraredActivity.class);
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_WIFIAIR);
                                        } else if (DEVICE_WIRELESS_SWITCH.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_WIFIRELAY);
                                        } else if (DEVICE_WIRED_AREA.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_WIREDZONE);
                                        } else if (DEVICE_IP_CAMERA.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_IPC);
                                        } else if (DEVICE_IPVDP_AREA.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_IPVDP);
                                        } else if (DEVICE_MUSIC.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_BACKAUDIO);
                                        } else if (DEVICE_BACNET_AIR_CONDITIONER.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_BACNET);
                                        } else if (DEVICE_485_DEVICE.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_WIFI485);
                                        } else if (DEVICE_VENTILATION.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, ModelEnum.MODULE_TYPE_VENTILATION);
                                        }
//                                        if (!DEVICE_WIRELESS_INFRARED.equalsIgnoreCase(text)) {
//                                            if (DEVICE_SPARK_LIGHTING.equalsIgnoreCase(text) || DEVICE_WIRELESS_SWITCH.equalsIgnoreCase(text)) {
                                        DeviceListActivity.this.startActivityForResult(intent, 1);
//                                            } else {
//                                                ToastUtil.showShort(DeviceListActivity.this, "click " + text);
//                                            }
//                                        }
                                    }
                                    mAddDeviceDialog.dismiss();
                                }
                            }));
                        }
                    });
        }

        if (mAddDeviceDialog != null && !mAddDeviceDialog.isShowing()) {
            mAddDeviceDialog.show();
        }
    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent ev = (CubeDeviceEvent) event;
            if (ev.getType() == CubeEvents.CubeDeviceEventType.MENU_GET_ALL_DEVICE_LIST) {
                mAdapter.setDataList(getDataList(ev));
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public List<DeviceListAdapter.ItemBean> getDataList(CubeDeviceEvent event) {
        List<DeviceListAdapter.ItemBean> dataList = new ArrayList<DeviceListAdapter.ItemBean>();
        if (event != null) {
            ArrayList<MenuDeviceUIItem> arrayList = (ArrayList<MenuDeviceUIItem>) event.getUpdateStatusData();
            if (arrayList != null && arrayList.size() > 0) {
                final int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    final MenuDeviceUIItem item = arrayList.get(i);
                    dataList.add(new DeviceListAdapter.ItemBean(getString(Constants.DEVICE_TYPE_MAP.get(item.deviceType)), item.object, item.deviceType));
                }
            }
        }
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "灯光", null, ""));
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "窗帘", null, ""));
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "开关", null, ""));
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "防区", null, ""));
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "IP 摄像头", null, ""));
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "空调", null, ""));
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "音乐播放", null, ""));
//        dataList.add(new DeviceListAdapter.ItemBean(-1, null, "自定义红外设备", null, ""));
        return dataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
                MenuDeviceController.getAllDeviceList(this);
            }
        }
    }
}
