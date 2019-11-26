package com.honeywell.cube.activities;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.AccountController;
import com.honeywell.cube.controllers.UIItem.menu.MenuAccountUIItem;
import com.honeywell.cube.utils.events.CubeAccountEvent;
import com.honeywell.cube.utils.events.CubeEvents;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/8.
 */
public class CubePropertyNetActivity extends CubeTitleBarActivity {

    private MenuAccountUIItem mMenuAccountUIItemP;
    private EditText mEtIpAddress;
    private EditText mEtMacAdress;
    private EditText mEtGateWay;

    @Override
    protected int getContent() {
        return R.layout.activity_property_net;
    }

    @Override
    public void initView() {
        mEtIpAddress = (EditText) findViewById(R.id.tv_cube_ip_content);
        mEtMacAdress = (EditText) findViewById(R.id.tv_cube_mac_content);
        mEtGateWay = (EditText) findViewById(R.id.tv_cube_gateway_content);
        initData();
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setText(R.string.cube_property_net);
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();

                mMenuAccountUIItemP.ethgw = mEtGateWay.getText().toString();

                mMenuAccountUIItemP.ethip = mEtIpAddress.getText().toString();

                mMenuAccountUIItemP.ethmask = mEtMacAdress.getText().toString();

                AccountController.updateEtherNetInfo(getApplicationContext(), mMenuAccountUIItemP);
            }
        });
    }

    public void initData() {
        mMenuAccountUIItemP = AccountController.getEtherNetInfo(this);
        mEtIpAddress.setText(mMenuAccountUIItemP.ethip);
        mEtMacAdress.setText(mMenuAccountUIItemP.ethmask);
        mEtGateWay.setText(mMenuAccountUIItemP.ethgw);

    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        if (event instanceof CubeAccountEvent) {
            final CubeAccountEvent event1 = (CubeAccountEvent) event;
            if (event1.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_ETHERNET) {
                dismissLoadingDialog();
                if (event1.success) {
                    showToastShort(R.string.operation_success_tip);
                } else {
                    showToastShort((String) event1.object);
                }

            }
        }

    }

}
