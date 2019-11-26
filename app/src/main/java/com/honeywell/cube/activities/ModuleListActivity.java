package com.honeywell.cube.activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ModuleListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.controllers.menus.MenuModuleController;
import com.honeywell.cube.net.easylink.EasyLinkManager;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DialogUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;
import com.honeywell.lib.dialogs.BottomDialog;

import java.util.ArrayList;
import java.util.List;

public class ModuleListActivity extends SwipeToLoadActivity {
    public static String MODULE_AUTO_SEARCH = "";
    public static String MODULE_SPARKLIGHTING = "";
    public static String MODULE_BACNET = "";
    public static String MODULE_IPVDP = "";

    BottomDialog mAddModuleDialog;

    List<BottomDialog.ItemBean> mAddModuleDataList;
    ModuleListAdapter mAdapter;
    ListView mContent;


    @Override
    public void onRefresh() {
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                MenuModuleController.findNewModule(ModuleListActivity.this);
            }
        });

    }


    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_add);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddModuleDialog();
            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.menu_module);
    }

    protected void initView() {
        super.initView();
        MODULE_AUTO_SEARCH = getString(R.string.module_add_discover);
        MODULE_SPARKLIGHTING = getString(R.string.module_add_sparklighting);
        MODULE_BACNET = getString(R.string.module_add_bacnet);
        MODULE_IPVDP = getString(R.string.module_add_ipvdp);
        mContent = (ListView) findViewById(R.id.swipe_target);
        mAdapter = new ModuleListAdapter(this, null, null);
        mContent.setAdapter(mAdapter);
    }

    @Override
    protected void getData() {
        super.getData();
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                super.run();
                MenuModuleController.getAllModuleList(ModuleListActivity.this);
            }
        }.start();

    }

    private List<BottomDialog.ItemBean> getAddModuleDataList() {
        if (null == mAddModuleDataList) {
            mAddModuleDataList = new ArrayList<>();
            ArrayList<String> list = MenuModuleController.getAddModuleList(this);
            if (list != null && list.size() > 0) {
                final int size = list.size();
                for (int i = 0; i < size; i++) {
                    mAddModuleDataList.add(new BottomDialog.ItemBean(list.get(i), null));
                }
            }
        }
        return mAddModuleDataList;
    }

    private void showAddModuleDialog() {
        if (null == mAddModuleDialog) {
            mAddModuleDialog = new BottomDialog(this);
            mAddModuleDialog.setViewCreateListener(
                    new BottomDialog.ViewCreateListener() {
                        @Override
                        public void initTop(TextView top) {
                            top.setVisibility(View.GONE);
                        }

                        @Override
                        public void initContent(ListView content) {
                            content.setAdapter(new BottomDialog.ListAdapter(ModuleListActivity.this, -1, getAddModuleDataList(), true, new BottomDialog.ListAdapter.OnItemClickListener() {
                                @Override
                                public void itemClick(View view, int position, int index) {
                                    final String text = mAddModuleDataList.get(position).mText;
                                    if (MODULE_AUTO_SEARCH.equalsIgnoreCase(text)) {
//                                        showLoadingDialog();
//                                        MenuModuleController.findNewModule(ModuleListActivity.this);
                                        String wifiName = getWifiSSID(ModuleListActivity.this);
                                        popDialog(wifiName);
//                                         ToastUtil.showShort(ModuleListActivity.this, "自动搜索");
                                    } else {
                                        Intent intent = new Intent(ModuleListActivity.this, ModuleEditActivity.class);
                                        intent.putExtra(Constants.TITLE, text);
                                        if (MODULE_SPARKLIGHTING.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_SPARK_LIGHTING);
                                        } else if (MODULE_BACNET.equalsIgnoreCase(text)) {
                                            intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_BACNET);
                                        } else if (MODULE_IPVDP.equalsIgnoreCase(text)) {
                                            intent = new Intent(ModuleListActivity.this, ModuleEditIPVDPActivity.class);
                                            intent.putExtra(Constants.TITLE, text);
//                                            intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_IPVDP);
                                        }
                                        ModuleListActivity.this.startActivityForResult(intent, 1);
                                    }
                                    mAddModuleDialog.dismiss();
                                }
                            }));
                        }
                    });
        }
        if (mAddModuleDialog != null && !mAddModuleDialog.isShowing()) {
            mAddModuleDialog.show();
        }
    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeModuleEvent) {
            CubeModuleEvent ev = (CubeModuleEvent) event;
            if (ev.type == CubeEvents.CubeModuleEventType.GET_MODULE_LIST) {
                mAdapter.setDataList(getDataList(ev));
                mAdapter.notifyDataSetChanged();
                dismissLoadingDialog();
            } else if (ev.type == CubeEvents.CubeModuleEventType.ADD_FIND_NEW_MODULE) {
                dismissLoadingDialog();
                if (ev.success) {
//                    showToastShort(ev.object.toString());
                    getData();
                } else {
                    showToastShort(ev.object.toString());
                }
            } else if (ev.type == CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE_DELETE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    mAdapter.updateDeleteUI();
                } else {
                    showToastShort((String) ev.object);
                }
            }
        }
    }

    public List<ModuleListAdapter.ItemBean> getDataList(CubeModuleEvent event) {
        List<ModuleListAdapter.ItemBean> dataList = new ArrayList<>();
        if (event != null) {
            ArrayList<MenuModuleUIItem> arrayList = (ArrayList<MenuModuleUIItem>) event.object;
            if (arrayList != null && arrayList.size() > 0) {
                final int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    final MenuModuleUIItem item = arrayList.get(i);
                    dataList.add(new ModuleListAdapter.ItemBean(-1, null, "", item, ""));
                }
            }
        }
        return dataList;
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

    //H157925
    private String getWifiSSID(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiSIID = info != null ? info.getSSID() : null;
        //获取Android版本号
        int deviceVersion = Build.VERSION.SDK_INT;
        if (deviceVersion >= 17) {
            if (wifiSIID.startsWith("\"") && wifiSIID.endsWith("\"")) {
                wifiSIID = wifiSIID.substring(1, wifiSIID.length() - 1);
            }
        }

        return wifiSIID;
    }

    private void popDialog(final String wifiSSID) {
        DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, wifiSSID);
        build.createDialog().createSearchDeviceView(new DialogUtil.DialogBuild.DialogEditListener() {
            @Override
            public void confirm(final String password) {

//
                new Thread() {
                    public void run() {
                        EasyLinkManager.newInstance(ModuleListActivity.this).startEasyLink(password);

                        EasyLinkManager.newInstance(ModuleListActivity.this).startConfigBroadLink(wifiSSID, password);
                    }
                }.start();

                DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(ModuleListActivity.this, null);
                build.createDialog().createSearchDeviceViewGif(new DialogUtil.DialogBuild.DialogListener() {
                    @Override
                    public void confirm() {

                    }
                });
            }
        });
    }
}
