package com.honeywell.cube.activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.AccountController;
import com.honeywell.cube.utils.events.CubeAccountEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shushunsakai on 16/6/23.
 */
public class SetCityActivity extends CubeTitleBarActivity implements View.OnClickListener {


    private String[] mHotCities;
    private TableRow.LayoutParams LP_WW = new TableRow.LayoutParams(0,
            TableRow.LayoutParams.WRAP_CONTENT, 1);
    private List<Button> btnList = new ArrayList<Button>();
    private String currentCityName;

    private LinearLayout mLinearLayoutAuto;
    private LinearLayout mLinearLayoutManual;
    private EditText mEditTextSearch;

    private TextView mTextViewLocationResult;
    private ProgressBar mProgressBarLoading;

    @Override
    protected int getContent() {
        return R.layout.activity_setting_city_draft;
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setText(R.string.cube_setting_city);
    }

    @Override
    public void initView() {
        TableLayout tl = (TableLayout) findViewById(R.id.tl_hotlocation);
        mHotCities = getResources().getStringArray(R.array.city);
        createTabView(tl, mHotCities);
        mLinearLayoutAuto = (LinearLayout) findViewById(R.id.ll_auto_location);
        mLinearLayoutManual = (LinearLayout) findViewById(R.id.ll_manual_location);
        mEditTextSearch = (EditText) findViewById(R.id.et_search);
        mTextViewLocationResult = (TextView) findViewById(R.id.city_selected);
        mProgressBarLoading = (ProgressBar) findViewById(R.id.pb_location);
        initEvent();
        initData();
    }

    public void initEvent() {
        mTextViewLocationResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextViewLocationResult.getText().toString().equals(getString(R.string.location_failure))) {
                    return;
                }
                currentCityName = mTextViewLocationResult.getText().toString().trim();
                AccountController.setCity(SetCityActivity.this, currentCityName);

            }
        });
        mEditTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                AccountController.getDefaultCities(SetCityActivity.this);

            }
        });
    }

    public void initData() {
        AccountController.startLocation(this);
    }


    private void createTabView(TableLayout tl, String[] hotCities) {
        // 下面是生成表单的操作
        int totalRow = hotCities.length / 4 - 1;
        addRow(tl, totalRow, hotCities);

    }


    private void addRow(TableLayout tl, int totalRow, String[] hotCities) {
        int index = 0;
        for (int i = 0; i <= totalRow; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < 4; j++) {

                Button button = new Button(this);
                button.setText(hotCities[index]);
                button.setTextColor(getResources().getColor(R.color.light_gray));
                button.setTag(hotCities[index]);
                button.setOnClickListener(this);
                LP_WW.setMargins(10, 10, 10, 10);
                button.setLayoutParams(LP_WW);
                row.addView(button);// 添加列
                btnList.add(button);
                index++;
            }
            tl.addView(row);// 添加行
            Log.e("行数：", "" + i);
        }
    }


    @Override
    public void onClick(View v) {

        currentCityName = (String) v.getTag();
        AccountController.setCity(this, currentCityName);

    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeAccountEvent) {
            CubeAccountEvent event1 = (CubeAccountEvent) event;
            if (event1.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_GET_LOCATION) {
                if (event1.success) {
                    String city = (String) event1.object;
                    mTextViewLocationResult.setText(city);
                    mProgressBarLoading.setVisibility(View.GONE);
                } else {
                    mTextViewLocationResult.setText(R.string.location_failure);
                }

            }

            if (event1.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_SET_LOCATION) {
                if (event1.success) {
                    String city = (String) event1.object;
                    showToastShort(R.string.operation_success_tip);
                    SetCityActivity.this.finish();

                } else {
                    showToastShort((String) event1.object);
                }
            }


        }
    }


}
