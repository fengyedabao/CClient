package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.RuleListAdapter;
import com.honeywell.cube.controllers.UIItem.menu.MenuRuleUIItem;
import com.honeywell.cube.controllers.menus.MenuRuleController;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeRuleEvent;

import java.util.ArrayList;
import java.util.List;

public class RuleListActivity extends CubeTitleBarActivity {

    RuleListAdapter mAdapter;
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
                Intent intent = new Intent(RuleListActivity.this, RuleEditActivity.class);
                intent.putExtra(Constants.TITLE, getString(R.string.rule_create));
//        intent.putExtra(Constants.TYPE, Constants.MODULE_EDIT_TYPE_COMMON);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.menu_rule);
    }

    protected void initView() {
        mContent = (ListView) findViewById(R.id.lv_list);
        mAdapter = new RuleListAdapter(this, null, getLoadingDialog());
        mContent.setAdapter(mAdapter);
    }

    @Override
    protected void getData() {
        startAsynchronousOperation(new Runnable() {
            @Override
            public void run() {
                MenuRuleController.getRuleList(RuleListActivity.this);
            }
        });
    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeRuleEvent) {
            CubeRuleEvent ev = (CubeRuleEvent) event;
            if (ev.type == CubeEvents.CubeRuleEventType.GET_RULE_LIST) {
                mAdapter.setDataList(getDataList(ev));
                mAdapter.notifyDataSetChanged();
                dismissLoadingDialog();
            } else if (ev.type == CubeEvents.CubeRuleEventType.CONFIG_RULE_STATE_DELETE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(R.string.operation_success_tip);
                    mAdapter.updateDeleteUI();
                } else {
                    showToastShort(R.string.operation_failed_tip);
                }
            } else if (ev.type == CubeEvents.CubeRuleEventType.ENABLE_RULE) {
                dismissLoadingDialog();
                if (ev.success) {
                    showToastShort(ev.object.toString());
                } else {
                    showToastShort(ev.object.toString());
                }
            }
        }
    }

    public List<RuleListAdapter.ItemBean> getDataList(CubeRuleEvent event) {
        List<RuleListAdapter.ItemBean> dataList = new ArrayList<>();
        if (event != null) {
            ArrayList<MenuRuleUIItem> arrayList = (ArrayList<MenuRuleUIItem>) event.object;
            if (arrayList != null && arrayList.size() > 0) {
                final int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    final MenuRuleUIItem item = arrayList.get(i);
                    dataList.add(new RuleListAdapter.ItemBean(-1, null, "", item, ""));
                }
            }
        }
        return dataList;
    }

    public List<RuleListAdapter.ItemBean> getDataList() {
        List<RuleListAdapter.ItemBean> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            dataList.add(new RuleListAdapter.ItemBean(-1, null, "", new MenuRuleUIItem(), ""));
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
}
