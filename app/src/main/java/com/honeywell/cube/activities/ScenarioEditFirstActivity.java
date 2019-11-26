package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.SelectScenarioIconAdapter;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.fragments.ScenarioRootFragment;
import com.honeywell.cube.utils.Constants;
import com.honeywell.lib.utils.ResourceUtil;
import com.honeywell.lib.widgets.GridViewGallery;

import java.util.ArrayList;
import java.util.List;

public class ScenarioEditFirstActivity extends CubeTitleBarActivity {
    protected ScenarioLoop mScenarioLoop;
    private EditText mEditScenarioName;
    private SelectScenarioIconAdapter mAdapter;
    private String mOperationType;

    @Override
    protected int getContent() {
        return R.layout.activity_scenario_edit_first;
    }

    @Override
    protected void initRightIcon(ImageView right) {
        right.setImageResource(R.mipmap.nav_next);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEditScenarioName.getText().toString())) {
                    showToastShort(R.string.scenario_need_name_tip);
                    return;
                }
                if (Constants.OPERATION_ADD.equalsIgnoreCase(mOperationType)) {
                    mScenarioLoop = new ScenarioLoop();
                }
                mScenarioLoop.mScenarioName = mEditScenarioName.getText().toString();

                mScenarioLoop.mImageName = ResourceUtil.getResName(ScenarioEditFirstActivity.this, getDataList().get(mAdapter.getSelectedPosition()).mIconid);
                Intent intent = new Intent(ScenarioEditFirstActivity.this, ScenarioEditSecondActivity.class);
                intent.putExtra(Constants.OPERATION_TYPE, mOperationType);
                Bundle bundle = new Bundle();
                bundle.putParcelable(ScenarioRootFragment.SCENARIO_LOOP, mScenarioLoop);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.edit_scenario);
    }

    protected void initView() {
        Intent intent = getIntent();
        mOperationType = intent.getStringExtra(Constants.OPERATION_TYPE);
        mEditScenarioName = (EditText) findViewById(R.id.et_scenario_name);
        mEditScenarioName.setText("场景");
        final GridViewGallery gvg = (GridViewGallery) findViewById(R.id.gv_icon);

        mAdapter = new SelectScenarioIconAdapter(this, getDataList());
        if (Constants.OPERATION_EDIT.equalsIgnoreCase(mOperationType)) {
            mScenarioLoop = intent.getParcelableExtra(ScenarioRootFragment.SCENARIO_LOOP);
            mEditScenarioName.setText(mScenarioLoop.mScenarioName);
            mAdapter.setSelectedPosition(getSelectedIconPosition(mScenarioLoop.mImageName));
        }
        gvg.setAdapter(mAdapter);
        gvg.setOnItemClickListener(new GridViewGallery.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedPosition(position);
                gvg.notifyDataSetChanged();
            }
        });
    }

    public int getSelectedIconPosition(String imageName) {
        int pos = 0;
        List<SelectScenarioIconAdapter.ItemBean> list = getDataList();
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

    private List<SelectScenarioIconAdapter.ItemBean> getDataList() {
        List<SelectScenarioIconAdapter.ItemBean> list = new ArrayList<SelectScenarioIconAdapter.ItemBean>();
        final int[] res = ResourceUtil.getResourceIdArray(getResources(), R.array.scenario_icon_list);
        final int length = res.length;
        for (int i = 0; i < length; i++) {
            list.add(new SelectScenarioIconAdapter.ItemBean("", res[i]));
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getExtras().getString("result");
            if ("success".equalsIgnoreCase(result)) {
                finish();
            }
        }
    }
}
