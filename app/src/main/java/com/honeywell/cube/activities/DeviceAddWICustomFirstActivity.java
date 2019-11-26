package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.SelectIRCustomIconAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRUIItem;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.lib.widgets.GridViewGallery;

import java.util.ArrayList;

public class DeviceAddWICustomFirstActivity extends CubeTitleBarActivity {
    GridViewGallery mContent;
    Button mClear;
    SelectIRCustomIconAdapter mAdapter;
    MenuDeviceIRUIItem mMenuDeviceIRUIItem;

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mMenuDeviceIRUIItem = getIntent().getParcelableExtra(Constants.CONTENT);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_device_add_wi_custom_first;
    }


    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        right.setImageResource(R.mipmap.nav_next);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<MenuDeviceIRIconItem> selectItems = mAdapter.getSelectItems();
                if (selectItems == null || selectItems.size() == 0) {
                    showToastShort(R.string.select_one_item);
                } else {
                    Intent intent = new Intent(DeviceAddWICustomFirstActivity.this, DeviceAddWICustomSecondActivity.class);
                    intent.putExtra(Constants.TITLE, getString(R.string.code_mode));
                    intent.putExtra(Constants.CONTENT, mAdapter.getSelectItems());
                    DeviceHelper.addObject2Intent(intent, Constants.CONTENT2, mMenuDeviceIRUIItem);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.select_button);
    }

    @Override
    protected void initView() {
        super.initView();
        mContent = (GridViewGallery) findViewById(R.id.gvg_content);
        mAdapter = new SelectIRCustomIconAdapter(this, MenuDeviceController.getDefaultIconList(this), SelectIRCustomIconAdapter.ICON_SELECT_FIRST, getLoadingDialog());
        mContent.setAdapter(mAdapter);
        mContent.setNumColumns(4);
        mContent.setNumRows(4);
        mClear = (Button) findViewById(R.id.btn_clear);
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
                mContent.notifyDataSetChanged();
            }
        });
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        final int index = mContent.getCurrentIndex();
        mContent.setAdapter(mAdapter);
        mContent.setCurrentIndex(index);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString(Constants.RESULT);
            if (Constants.SUCCESS.equalsIgnoreCase(result)) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
