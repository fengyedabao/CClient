package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.utils.CubeCount;
import com.honeywell.cube.utils.DialogUtil;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.StringUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;

/**
 * Created by zhujunyu on 16/6/1.
 */
public class ForgotPwdActivity extends CubeTitleBarActivity {

    protected Button mButtonNext;
    protected RelativeLayout mRelativeLayout;
    protected EditText mEditTextNumber;

    protected TextView mTextValidateCode;
    protected EditText mEditTextValidateCode;
    protected EditText mEditTextPassword;
    protected TextView mTextViewCountryCode;

    //定时器类
    protected CubeCount mCubeCount;
    protected String telePhone;
    protected String countryCode = "+86";

    protected static final int FIRST_STEP = 1;
    protected static final int SECOND_STEP = 2;
    protected static final int THIRD_STEP = 3;
    protected static int NOW_STEP = FIRST_STEP;

    @Override
    protected int getContent() {
        return R.layout.activity_register;
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setText(R.string.forgot_title);
    }

    @Override
    public void initView() {
        mButtonNext = (Button) findViewById(R.id.btn_next);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.select_country);
        mEditTextNumber = (EditText) findViewById(R.id.register_tele_num);
        mTextValidateCode = (TextView) findViewById(R.id.validate_retry);
        mEditTextValidateCode = (EditText) findViewById(R.id.validate_code_edit);
        mEditTextPassword = (EditText) findViewById(R.id.et_password);
        mTextViewCountryCode = (TextView) findViewById(R.id.country_code);

        mEditTextNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mButtonNext.setEnabled(StringUtil.isMobileNO(s == null ? "" : s.toString()));
            }
        });
        showStepView(FIRST_STEP);
        initEvent();
//        initData();
    }


    public void initEvent() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查号码是否正确
                String num = mEditTextNumber.getText().toString().trim();


                if (NOW_STEP == FIRST_STEP) {
                    if (!StringUtil.isMobileNO(num)) {
                        showToastShort(R.string.illegal_number);
                        return;
                    }
                    telePhone = num;
                    popDialog(telePhone);
                    return;
                }
                //检查验证码是否存在
                Log.e(telePhone, countryCode);
                if (!TextUtils.isEmpty(mEditTextValidateCode.getText())) {
                    resetPassword(telePhone);
                }


            }
        });
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ForgotPwdActivity.this, CountrySelectActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mTextValidateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查号码是否正确
                SendSmsTOGetCode(telePhone);
            }
        });
    }

    public void initData() {
        mCubeCount = new CubeCount(this, 60000, 1000, mTextValidateCode);
        mCubeCount.start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("Register", "requestCode:" + requestCode + "resultCode:" + resultCode);

        if (requestCode == 0 && resultCode == 0) {
            String str = "";
            if (data == null) {
                str = "中国 +86";
                countryCode = "+86";
            } else {
                str = data.getStringExtra("country");
                countryCode = data.getStringExtra("code");
            }

            mTextViewCountryCode.setText(str);


        }


    }

    protected void popDialog(final String telePhoneNum) {
        DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, telePhoneNum);
        build.createDialog().createView(new DialogUtil.DialogBuild.DialogListener() {
            @Override
            public void confirm() {
                showStepView(SECOND_STEP);
                //开始倒计时
                SendSmsTOGetCode(telePhoneNum);
            }
        });

    }

    protected void showStepView(int step) {

        switch (step) {
            case FIRST_STEP:
                NOW_STEP = FIRST_STEP;
                this.findViewById(R.id.first_step).setVisibility(View.VISIBLE);
                this.findViewById(R.id.second_step).setVisibility(View.GONE);

                break;
            case SECOND_STEP:
                NOW_STEP = SECOND_STEP;
                this.findViewById(R.id.first_step).setVisibility(View.GONE);
                this.findViewById(R.id.second_step).setVisibility(View.VISIBLE);
                break;
            case THIRD_STEP:

                break;
        }
    }

    protected void SendSmsTOGetCode(String phone) {
        mTextValidateCode.setEnabled(false);
        mCubeCount = new CubeCount(this, 60000, 1000, mTextValidateCode);
        mCubeCount.start();

        //调用
        LoginController.getInstance(getApplicationContext()).loginHttpWithDataForValidatenum(phone, countryCode);

    }

    protected void resetPassword(String telePhone) {
        Log.e(telePhone, countryCode);
        LoginController.getInstance(getApplicationContext()).loginHttpWithDataForResetPwd(telePhone, mEditTextValidateCode.getText().toString(), mEditTextPassword.getText().toString(), countryCode);
    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);

        if (event instanceof CubeLoginEvent) {

            CubeLoginEvent mEvent = (CubeLoginEvent) event;
            if (mEvent.type == CubeEvents.CubeLoginEventType.LOGIN_RESET_PWD) {

                if (mEvent.success) {
                    showToastShort(R.string.operation_success_tip);
                    ForgotPwdActivity.this.finish();
                } else {
                    showToastShort((String) mEvent.item);
                }

            }

        }
    }


}
