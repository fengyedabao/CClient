package com.honeywell.cube.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.LoginController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhujunyu on 16/6/1.
 */
public class CountrySelectActivity extends CubeTitleBarActivity {


    private ListView mListView;
    ArrayList<HashMap<String, ?>> mDataList;

    @Override
    protected int getContent() {
        return R.layout.activity_country;
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setText(R.string.select_country_title);
    }

    @Override
    public void initView() {
        getRemoteDate();
        mListView = (ListView) findViewById(R.id.cube_select_country);

        mListView.setAdapter(
                new SimpleAdapter(this, mDataList, R.layout.list_device_ac, new String[]{"name"}, new int[]{R.id.tv_text}));
        initEvent();
    }

    public void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                HashMap map = mDataList.get(position);
                String name = (String) map.get("name");
                String phone_prefix = (String) map.get("phone_prefix");
                intent.putExtra("country", name + phone_prefix);
                intent.putExtra("code", phone_prefix);
                CountrySelectActivity.this.setResult(0, intent);
                finish();
            }
        });
    }


    private void getRemoteDate() {

        List<Object> list = LoginController.getInstance(getApplicationContext()).getPhoneCountryMapList(this);
        mDataList = new ArrayList<>();
        for (Object object : list) {

            Map map = (Map) object;
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", (String) map.get("name"));
            hashMap.put("phone_prefix", (String) map.get("phone_prefix"));
            mDataList.add(hashMap);
        }

    }

}
