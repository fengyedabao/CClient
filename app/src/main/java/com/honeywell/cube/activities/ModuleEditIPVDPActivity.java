package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.ModuleAddIPVDPAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuModuleUIItem;
import com.honeywell.cube.controllers.menus.MenuModuleController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeModuleEvent;
import com.honeywell.lib.widgets.HeadListView;

public class ModuleEditIPVDPActivity extends CubeTitleBarActivity {
    HeadListView mContent;
    private MenuModuleUIItem mMenuModuleUIItem;
    ModuleAddIPVDPAdapter adapter = null;

    @Override
    protected void initView() {
        super.initView();
        mContent = (HeadListView) findViewById(R.id.lv_list);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_add_device_detail;
    }

    @Override
    protected void getData() {
        super.getData();
        mMenuModuleUIItem = MenuModuleController.getDefaultIPVDP(this);
        adapter = new ModuleAddIPVDPAdapter(this, mMenuModuleUIItem, getLoadingDialog());
        mContent.setAdapter(adapter);
        mContent.setOnScrollListener(adapter);
        mContent.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_device_add_detail_section, mContent, false));
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuModuleUIItem = adapter.getModuleData();
                if (TextUtils.isEmpty(mMenuModuleUIItem.title)) {
                    showToastShort(R.string.name_not_null);
                    return;
                }
                if (TextUtils.isEmpty(mMenuModuleUIItem.ipAddr)) {
                    showToastShort(R.string.ip_not_null);
                    return;
                }
                if (TextUtils.isEmpty(mMenuModuleUIItem.hns_ip)) {
                    showToastShort(R.string.hns_ip_not_null);
                    return;
                }
                startAsynchronousOperation(new Runnable() {
                    @Override
                    public void run() {
                        MenuModuleController.addModuleIPVDP(ModuleEditIPVDPActivity.this, mMenuModuleUIItem);
                    }
                });

            }
        });
    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeModuleEvent) {
            CubeModuleEvent ev = (CubeModuleEvent) event;
            if (ev.type == CubeEvents.CubeModuleEventType.CONFIG_MODULE_STATE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.RESULT, Constants.SUCCESS);
                    ModuleEditIPVDPActivity.this.setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showToastShort(ev.object.toString());
                }
            }
        }
    }
}
