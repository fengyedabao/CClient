package com.honeywell.cube.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.AccountController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ScanController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.CubeDevice;
import com.honeywell.cube.net.service.CubeAppService;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.qrcode.CaptureActivity;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ToastUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/4/16. 07:09
 * Email:Shodong.Sun@honeywell.com
 */
public class LoginActivity extends CubeTitleBarActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private LoginController loginController;

    private EditText mEditUsername;
    private EditText mEditPassword;
    private Button mBtnUserLogin;
    private Button mBtnGuestLogin;
    private CheckBox mShowPassword;
    private TextView mTextForgetPassword;
    private TextView mTextNoAccount;
    private TextView mTextCreateAccount;

    private BottomDialog mSelectCubeDialog;

    private String mToken = null;
    private boolean bRegister = false;

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        mToken = getIntent().getStringExtra(Constants.TOKEN);
        bRegister = getIntent().getBooleanExtra(Constants.REGISTER, false);
    }

    @Override
    protected int getContent() {
        return R.layout.activity_login;
    }

    @Override
    protected void initLeftIcon(ImageView left) {
        left.setVisibility(View.GONE);
    }

    @Override
    protected void initTitle(TextView title) {
        title.setText(R.string.login_title);
    }

    protected void initView() {
        //启动服务
        startService();

        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        mEditUsername = (EditText) findViewById(R.id.et_username);
        mEditPassword = (EditText) findViewById(R.id.et_password);
        mEditUsername.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtnUserLogin.setEnabled((!TextUtils.isEmpty(s)) && (!TextUtils.isEmpty(mEditPassword.getText().toString())));
            }
        });
        mEditPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtnUserLogin.setEnabled((!TextUtils.isEmpty(s)) && (!TextUtils.isEmpty(mEditUsername.getText().toString())));
            }
        });
        mShowPassword = (CheckBox) findViewById(R.id.cb_show_password);
        mShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEditPassword.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        mTextForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mTextForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Forget Password
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ForgotPwdActivity.class);
                startActivity(intent);

            }
        });

        ScanController.checkScanResult("");

        mTextNoAccount = (TextView) findViewById(R.id.tv_no_account);
        mTextNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO No Account
            }
        });

        mTextCreateAccount = (TextView) findViewById(R.id.tv_create_account);
        mTextCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Create Account
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mBtnUserLogin = (Button) findViewById(R.id.btn_user_login);
        mBtnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                } else {
                    login();
                }
            }
        });

        mBtnGuestLogin = (Button) findViewById(R.id.btn_guest_login);
        mBtnGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                intent.putExtra("capture_type", ModelEnum.SCANED_TYPE_WIFI_LOGIN);
                startActivity(intent);


//                finish();
            }
        });
        loginController = LoginController.getInstance(getApplicationContext());
        initLoginValues();
        if (!TextUtils.isEmpty(mEditUsername.getText().toString()) && !TextUtils.isEmpty(mEditPassword.getText().toString())) {
            mBtnUserLogin.performClick();
        }
    }

    //
    private void sendUserNameAndPassword() {
        loginController.loginHttpWithNameAndPassword(mEditUsername.getText().toString(), mEditPassword.getText().toString(), "+86", mToken);
    }

    /**
     * 启动服务
     */
    public void startService() {
        CubeAppService.mContext = getApplicationContext();
        Intent intent = new Intent(LoginActivity.this, CubeAppService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private CubeAppService myservice = null;//绑定的service对象
    //连接对象，重写OnserviceDisconnected和OnserviceConnected方法
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Loger.print("ssd", " service disconnect ********************** ", Thread.currentThread());
            myservice = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Loger.print("ssd", " service connect ********************** ", Thread.currentThread());
            myservice = ((CubeAppService.MyBinder) service).getService();
        }
    };

    @Override
    public void onEventMainThread(CubeEvents event) {
        if (event instanceof CubeBasicEvent) {
            final CubeBasicEvent cubeBasicEvent = (CubeBasicEvent) event;
            if (cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.TIME_OUT) {
                showToastShort(cubeBasicEvent.getMessage());
                dismissLoadingDialog();
            }
        } else if (event instanceof CubeLoginEvent) {
            CubeLoginEvent loginEvent = (CubeLoginEvent) event;
            if (loginEvent.type == CubeEvents.CubeLoginEventType.GET_CUBE_DEVICES_SUCCESS) {
                //Cube列表更新完成 需要从数据库获取设备列表
                if (loginEvent.success) {
                    int deviceid = AccountController.getBindCubeDeviceId(getApplicationContext());
                    if (deviceid > 0) {
                        Loger.print("ssd", "get cube device id > 0", Thread.currentThread());
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        ArrayList<CubeDevice> list = loginController.getAllCubeDeviceFromDataBase();
                        if (list == null || list.size() == 0) {
                            //需要显示登录成功，调用扫描界面
                            Loger.print(TAG, "ssd ****** 0", Thread.currentThread());

                            Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                            intent.putExtra("capture_type", ModelEnum.SCANED_TYPE_BIND_CUBE);
                            startActivity(intent);


                        } else if (list.size() == 1) {
                            //更新数据 显示主界面
                            CubeDevice device = list.get(0);
                            loginController.loginWithUpdateAppInfo(device);
                            finish();
                            startActivity(new Intent(this, MainActivity.class));
                            //远程登陆成功
                        } else {
                            //临时处理
                            showSelectDialog(list);
                        }
                    }
                } else {
                    Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                    intent.putExtra("capture_type", ModelEnum.SCANED_TYPE_BIND_CUBE);
                    startActivity(intent);
                }
                dismissLoadingDialog();
            } else if (loginEvent.type == CubeEvents.CubeLoginEventType.LOGIN_WITH_NAME_AND_PWD) {
                if (!loginEvent.success) {
                    showToastShort(loginEvent.item.toString());
                    dismissLoadingDialog();
                }
            }
        }
    }

    private void showSelectDialog(ArrayList<CubeDevice> list) {
        final List<BottomDialog.ItemBean> dataList = new ArrayList<BottomDialog.ItemBean>();
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            dataList.add(new BottomDialog.ItemBean(list.get(i).mInfo_aliasName, list.get(i)));
        }
        if (null == mSelectCubeDialog) {
            mSelectCubeDialog = new BottomDialog(this);
            mSelectCubeDialog.setViewCreateListener(new BottomDialog.ViewCreateListener() {
                @Override
                public void initTop(TextView top) {
                    top.setVisibility(View.GONE);
                }

                @Override
                public void initContent(ListView content) {
                    content.setAdapter(new BottomDialog.ListAdapter(LoginActivity.this, -1, dataList, false, new BottomDialog.ListAdapter.OnItemClickListener() {
                        @Override
                        public void itemClick(View view, int position, int index) {
                            loginController.loginWithUpdateAppInfo((CubeDevice) dataList.get(position).mData);
                            mSelectCubeDialog.dismiss();
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }));
                }
            });
        }
        if (mSelectCubeDialog != null && !mSelectCubeDialog.isShowing()) {
            mSelectCubeDialog.show();
        }
    }

    private void login() {
        String nameStr = mEditUsername.getText().toString();
        String pwdStr = mEditPassword.getText().toString();
        if (CommonUtils.ISNULL(nameStr) || CommonUtils.ISNULL(pwdStr)) {
//            showToastShort("请输入用户名和密码");
            return;
        }
        showLoadingDialog();
        if (!bRegister) {
            registerPush(nameStr);
        } else {
            sendUserNameAndPassword();
        }
    }

    TimeOutThread mTimeOutThread;
    private static final int TIMEOUT = 5000;

    private void registerPush(final String account) {
        mTimeOutThread = new TimeOutThread(TIMEOUT);
        XGPushManager.registerPush(getApplicationContext(), account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                mToken = o.toString();
                LogUtil.e("TPush", "注册成功，设备token为：" + o + " , account is " + account);

                //未超时
                if (mTimeOutThread != null && mTimeOutThread.isAlive()) {
                    mTimeOutThread.cancel();
                    mTimeOutThread = null;
                    sendUserNameAndPassword();
                } else {
                    if (TextUtils.isEmpty(PreferenceUtil.getXGToken(LoginActivity.this))) {
//                        sendUserNameAndPassword();
                    }
                }
                PreferenceUtil.saveXGToken(LoginActivity.this, mToken);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtil.d("TPush", "注册失败，错误码：" + i + ",错误信息：" + s);
                ToastUtil.showShort(LoginActivity.this, "注册失败，错误码：" + i + ",错误信息：" + s);
                if (mTimeOutThread != null && mTimeOutThread.isAlive()) {
                    mTimeOutThread.cancel();
                    mTimeOutThread = null;
                    sendUserNameAndPassword();
                } else {
                    if (TextUtils.isEmpty(PreferenceUtil.getXGToken(LoginActivity.this))) {
//                        sendUserNameAndPassword();
                    }
                }

            }
        });
        mTimeOutThread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    login();

                }
                return;
            }
        }
    }

    private void initLoginValues() {
        final String[] userInfo = PreferenceUtil.getUserInfo(this);
        mEditUsername.setText(userInfo[0]);
        mEditPassword.setText(userInfo[1]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        EventBus.getDefault().unregister(this);
    }

    public class TimeOutThread extends Thread {
        boolean mStart = true;
        long mStartTime = System.currentTimeMillis();
        int mTimeout;

        public TimeOutThread(int timeout) {
            mTimeout = timeout;
        }

        public void cancel() {
            mStart = false;
        }

        @Override
        public void run() {
            super.run();
            while (mStart) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long current = System.currentTimeMillis();
                if (current - mStartTime > mTimeout * 8) {
                    LogUtil.d("TPush", "current - mStartTime > mTimeout * 8");
                    sendUserNameAndPassword();
                    mStart = false;
                    return;
                } else if (current - mStartTime > mTimeout) {
                    LogUtil.d("TPush", "XGPushManager.registerPush TimeOut");
                    String token = PreferenceUtil.getXGToken(LoginActivity.this);
                    if (!TextUtils.isEmpty(token)) {
                        LogUtil.d("TPush", "!TextUtils.isEmpty(token)");
                        sendUserNameAndPassword();
                        mStart = false;
                        return;
                    }

                }
            }
        }
    }
}
