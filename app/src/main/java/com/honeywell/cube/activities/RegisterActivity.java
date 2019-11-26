package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.StringUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;

public class RegisterActivity extends ForgotPwdActivity {

    public static final int REGISTER_TYPE = 1;
    public static final int FIND_PWD_TYPE = 2;


    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.register_title);
    }

    @Override
    public void initEvent() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LogUtil.e("NOW_STEP", "" + NOW_STEP);

                if (NOW_STEP == FIRST_STEP) {
                    //检查号码是否正确
                    String num = mEditTextNumber.getText().toString().trim();
                    if (!StringUtil.isMobileNO(num)) {
                        showToastShort(R.string.illegal_number);
                        return;
                    }
                    telePhone = num;
                    popDialog(telePhone);
                    return;
                }

                //检查验证码是否存在
                if (!TextUtils.isEmpty(mTextValidateCode.getText())) {
                    registerAccoundCode(telePhone);
                }

            }
        });

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, CountrySelectActivity.class);
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


    private void registerAccoundCode(String phone) {
        LoginController.getInstance(getApplicationContext()).loginHttpWithDataForRegister(phone, mEditTextValidateCode.getText().toString(), mEditTextPassword.getText().toString(), countryCode);

    }


    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeLoginEvent) {

            CubeLoginEvent mEvent = (CubeLoginEvent) event;
            if (mEvent.type == CubeEvents.CubeLoginEventType.LOGIN_REGISTER) {

                if (mEvent.success) {
                    showToastShort(R.string.operation_success_tip);
                    RegisterActivity.this.finish();
                } else {
                    showToastShort((String) mEvent.item);
                }

            }

        }
    }

}
