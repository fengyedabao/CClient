package com.honeywell.cube.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.ScenarioEditSecondActivity;
import com.honeywell.cube.adapter.ScenarioEditZoneAdapter;
import com.honeywell.cube.controllers.UIItem.UIItems;
import com.honeywell.cube.db.ModelEnum;

import java.util.ArrayList;

/**
 * Created by milton on 16/5/31.
 */
public class ScenarioEditZoneFragment extends Fragment {
    ScenarioEditZoneAdapter mAdapter;
    CheckBox mUseZone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scenario_edit_zone, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUI();
    }

    private void initView(View view) {
        mUseZone = (CheckBox) view.findViewById(R.id.cb_use_zone);
        final LinearLayout llContent = (LinearLayout) view.findViewById(R.id.ll_content);
        mUseZone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                llContent.setVisibility(isChecked && mAdapter.getCount() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });


        final RelativeLayout rlUseZone = (RelativeLayout) view.findViewById(R.id.rl_use_zone);
        rlUseZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUseZone.setChecked(!mUseZone.isChecked());
            }
        });
//        final RelativeLayout rlAllUseZone = (RelativeLayout) view.findViewById(R.id.rl_all_use_zone);
//        rlAllUseZone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        TextView tv = (TextView) view.findViewById(R.id.tv_text);
        tv.setText(R.string.edit_scenario_all);
        CheckBox cbAllUseZone = (CheckBox) view.findViewById(R.id.slide_switch);
        cbAllUseZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                ArrayList<UIItems> list = mAdapter.getDataList();
                final int size = list.size();
                for (int i = 0; i < size; i++) {
                    list.get(i).isSelcet = isChecked;
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        final ListView lvContent = (ListView) view.findViewById(R.id.lv_content);


        mAdapter = new ScenarioEditZoneAdapter(getContext(), getDataList(), cbAllUseZone);
        lvContent.setAdapter(mAdapter);
    }

    private ArrayList<UIItems> getDataList() {
        if (getActivity() == null) {
            return null;
        } else {
            return ((ScenarioEditSecondActivity) getActivity()).getZoneList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateUI() {
//        final List
        if (getActivity() != null) {
            mAdapter.setItemList(getDataList());
            mAdapter.notifyDataSetChanged();
//        if(null==((ScenarioEditSecondActivity) getActivity()).getZoneList())
            if (getActivity() != null) {
                mUseZone.setChecked(((ScenarioEditSecondActivity) getActivity()).isUseZone());
            }
        }
    }

    public ArrayList<UIItems> getZoneDataList() {
        ArrayList<UIItems> result = new ArrayList<UIItems>();
        if (mUseZone.isChecked()) {
            result.add(new UIItems(ModelEnum.UI_TYPE_LIST, "", "", "", false));
            if (mAdapter.getDataList() != null) {
                result.addAll(mAdapter.getDataList());
            }
            return result;
        } else {
            return null;
        }
    }
}
