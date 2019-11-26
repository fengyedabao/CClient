package com.honeywell.cube.activities;

import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.adapter.CommonAdapter;
import com.honeywell.cube.adapter.CubeAboutCubeAdapter;
import com.honeywell.cube.adapter.CubeExchangeAdapter;
import com.honeywell.cube.adapter.CubePropertyNetAdapter;
import com.honeywell.cube.adapter.CudeBackUpHistoryAdapter;
import com.honeywell.cube.controllers.AccountController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ScanController;
import com.honeywell.cube.controllers.UIItem.menu.CubeTitleTextItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuAccountUIItem;
import com.honeywell.cube.controllers.UIItem.menu.MenuCubeSettingBackup;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceUIItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeAccountEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeHeadInfoEvent;
import com.honeywell.lib.qrcode.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by zhujunyu on 16/6/24.
 */
public class CubeSettingCommeActivity extends CubeTitleBarActivity {

    private ListView mListView;
    private String TAG = CubeSettingCommeActivity.class.getSimpleName();
    public static final int CUBE_INFO = 100;
    public static final int SETTING_NAME = 101;
    public static final int CHANGE_LOGIN_PWD = 102;
    public static final int CHANGE_SAFETY_PWD = 103;
    public static final int PROPERTY_NET = 104;
    public static final int BACKUP_HISTORY_VERSION = 105;
    public static final int SET_CITY = 106;

    public static final int SETTING_DEVICE_NAME = 201;
    public static final int CHANGE_PWD = 202;
    public static final int EXCHANGE_CUBE = 203;
    public static int INTENT_TAG;


    private EditText mEditTextViewCubeName;
    private EditText mEditTextViewOldPwd;
    private EditText mEditTextViewNewPwd;
    private CheckBox mShowPasswordOld;
    private CheckBox mShowPasswordNew;

    private CommonAdapter commonAdapter;
    private ArrayList<MenuCubeSettingBackup> listBackup = new ArrayList<>();
    private ArrayList<MenuDeviceUIItem> listDevice = new ArrayList<>();

    @Override
    protected int getContent() {
        return R.layout.activity_cube_setting_comme;
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        switch (INTENT_TAG) {
            case CUBE_INFO:
                title.setText(R.string.cube_info);
                break;
            case SETTING_NAME:
                title.setText(R.string.cube_setting_name);
                break;
            case CHANGE_LOGIN_PWD:
                title.setText(R.string.cube_change_log_pwd);
                break;
            case CHANGE_SAFETY_PWD:
                title.setText(R.string.cube_change_safety_pwd);
                break;
            case PROPERTY_NET:
                title.setText(R.string.cube_property_net);
                break;
            case BACKUP_HISTORY_VERSION:
                title.setText(R.string.cube_backup_history);
                break;
            case SETTING_DEVICE_NAME:
                title.setText(R.string.cube_setting_name);
                break;
            case CHANGE_PWD:
                title.setText(R.string.cube_change_log_pwd);
                break;
            case EXCHANGE_CUBE:
                title.setText(R.string.cube_exchange);
                break;
        }
    }

    @Override
    protected void initRightIcon(ImageView right) {
        super.initRightIcon(right);
        switch (INTENT_TAG) {
            case CUBE_INFO:
                break;
            case SETTING_NAME:
                right.setImageResource(R.mipmap.nav_done);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingDialog();
                        //请求修改信息接口
                        Loger.print(TAG, "11111" + mEditTextViewCubeName.getText().toString(), Thread.currentThread());
                        AccountController.setCubeName(CubeSettingCommeActivity.this, mEditTextViewCubeName.getText().toString(), LoginController.LOGIN_TYPE_CONNECT_WIFI);
                    }
                });
                break;
            case CHANGE_LOGIN_PWD:
                right.setImageResource(R.mipmap.nav_done);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingDialog();
                        //请求修改登录密码信息接口
                        String oldPwd = mEditTextViewOldPwd.getText().toString();
                        String newPwd = mEditTextViewNewPwd.getText().toString();
                        AccountController.setCubePassword(CubeSettingCommeActivity.this, oldPwd, newPwd, LoginController.LOGIN_TYPE_CONNECT_WIFI);
                    }
                });
                break;
            case CHANGE_SAFETY_PWD:
                right.setImageResource(R.mipmap.nav_done);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //请求修改安防密码信息接口
                        showLoadingDialog();
                        String oldPwd = mEditTextViewOldPwd.getText().toString();
                        String newPwd = mEditTextViewNewPwd.getText().toString();
                        AccountController.setCubeAlarmPassword(CubeSettingCommeActivity.this, oldPwd, newPwd);
                    }
                });
                break;
            case PROPERTY_NET:
                right.setImageResource(R.mipmap.nav_done);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        AccountController.updateEtherNetInfo(getApplicationContext(),mMenuAccountUIItemP);
                    }
                });
                break;
            case BACKUP_HISTORY_VERSION:
                break;
            case SETTING_DEVICE_NAME:
                right.setImageResource(R.mipmap.nav_done);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingDialog();
                        //请求修改信息接口
                        AccountController.setCubeName(CubeSettingCommeActivity.this, mEditTextViewCubeName.getText().toString(), LoginController.LOGIN_TYPE_CONNECT_CLOUD);
                    }
                });
                break;
            case CHANGE_PWD:
                right.setImageResource(R.mipmap.nav_done);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingDialog();
                        //请求修改登录密码信息接口
                        String oldPwd = mEditTextViewOldPwd.getText().toString();
                        String newPwd = mEditTextViewNewPwd.getText().toString();
                        AccountController.setCubePassword(CubeSettingCommeActivity.this, oldPwd, newPwd, LoginController.LOGIN_TYPE_CONNECT_CLOUD);
                    }
                });
                break;
            case EXCHANGE_CUBE:
                right.setImageResource(R.mipmap.nav_add);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CubeSettingCommeActivity.this, CaptureActivity.class);
                        intent.putExtra("capture_type", ModelEnum.SCANED_TYPE_REPLACE_CUBE);
                        startActivity(intent);
                    }
                });
                break;
        }
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        Intent intent = getIntent();
        INTENT_TAG = intent.getIntExtra("intent_tag", 100);
    }

    @Override
    public void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        commonAdapter = new CudeBackUpHistoryAdapter(CubeSettingCommeActivity.this, listBackup, R.layout.backup_history_item);
        View view;
        switch (INTENT_TAG) {
            case CUBE_INFO:
                List<CubeTitleTextItem> listItem = new ArrayList<>();
                MenuAccountUIItem mMenuAccountUIItem = AccountController.getCubeInfo(this);
                for (int i = 0; i < 3; i++) {
                    if (i == 0) {
                        CubeTitleTextItem cubeTitleTextItem = new CubeTitleTextItem();
                        cubeTitleTextItem.itemTitle = getResources().getString(R.string.cube_version);
                        cubeTitleTextItem.itemText = mMenuAccountUIItem.cube_version;
                        listItem.add(cubeTitleTextItem);
                    } else if (i == 1) {
                        CubeTitleTextItem cubeTitleTextItem = new CubeTitleTextItem();
                        cubeTitleTextItem.itemTitle = getResources().getString(R.string.cube_ip);
                        cubeTitleTextItem.itemText = mMenuAccountUIItem.cube_ip;
                        listItem.add(cubeTitleTextItem);
                    } else if (i == 2) {
                        CubeTitleTextItem cubeTitleTextItem = new CubeTitleTextItem();
                        cubeTitleTextItem.itemTitle = getResources().getString(R.string.cube_mac);
                        cubeTitleTextItem.itemText = mMenuAccountUIItem.cube_mac;
                        listItem.add(cubeTitleTextItem);
                    } else if (i == 3) {
                        CubeTitleTextItem cubeTitleTextItem = new CubeTitleTextItem();
                        cubeTitleTextItem.itemTitle = getResources().getString(R.string.cube_hns_mac);
                        cubeTitleTextItem.itemText = mMenuAccountUIItem.cube_hns;
                        listItem.add(cubeTitleTextItem);
                    }
                }
                commonAdapter = new CubeAboutCubeAdapter(CubeSettingCommeActivity.this, listItem, R.layout.activity_about_cube);
                break;
            case SETTING_NAME:
                view = LayoutInflater.from(this).inflate(R.layout.activity_setting_name, null);
                mEditTextViewCubeName = (EditText) view.findViewById(R.id.register_tele_num);
                mListView.addHeaderView(view);
                break;
            case CHANGE_LOGIN_PWD:
                view = LayoutInflater.from(this).inflate(R.layout.activity_change_login_pwd, null);
                mEditTextViewOldPwd = (EditText) view.findViewById(R.id.et_password_old);
                mEditTextViewNewPwd = (EditText) view.findViewById(R.id.et_password_new);

                mShowPasswordOld = (CheckBox) view.findViewById(R.id.cb_show_password_old);
                mShowPasswordOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mEditTextViewOldPwd.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                });

                mShowPasswordNew = (CheckBox) view.findViewById(R.id.cb_show_password_new);
                mShowPasswordNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mEditTextViewNewPwd.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                });
                mListView.addHeaderView(view);
                break;
            case CHANGE_SAFETY_PWD:
                view = LayoutInflater.from(this).inflate(R.layout.activity_change_login_pwd, null);
                mEditTextViewOldPwd = (EditText) view.findViewById(R.id.et_password_old);
                mEditTextViewNewPwd = (EditText) view.findViewById(R.id.et_password_new);
                mEditTextViewOldPwd.setHint(R.string.text_hint_6c);
                mEditTextViewNewPwd.setHint(R.string.text_hint_6c);
                mShowPasswordOld = (CheckBox) view.findViewById(R.id.cb_show_password_old);
                mShowPasswordOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mEditTextViewOldPwd.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                });

                mShowPasswordNew = (CheckBox) view.findViewById(R.id.cb_show_password_new);
                mShowPasswordNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mEditTextViewNewPwd.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                });
                mListView.addHeaderView(view);
                break;
            case PROPERTY_NET:
                List<CubeTitleTextItem> listItemProperty = new ArrayList<>();
                final MenuAccountUIItem mMenuAccountUIItemP = AccountController.getEtherNetInfo(this);
                for (int i = 0; i < 3; i++) {
                    if (i == 0) {
                        CubeTitleTextItem cubeTitleTextItem = new CubeTitleTextItem();
                        cubeTitleTextItem.itemTitle = getResources().getString(R.string.property_ip);
                        cubeTitleTextItem.itemText = mMenuAccountUIItemP.ethip;
                        listItemProperty.add(cubeTitleTextItem);
                    } else if (i == 1) {
                        CubeTitleTextItem cubeTitleTextItem = new CubeTitleTextItem();
                        cubeTitleTextItem.itemTitle = getResources().getString(R.string.property_subnet_mask);
                        cubeTitleTextItem.itemText = mMenuAccountUIItemP.ethmask;
                        listItemProperty.add(cubeTitleTextItem);
                    } else if (i == 2) {
                        CubeTitleTextItem cubeTitleTextItem = new CubeTitleTextItem();
                        cubeTitleTextItem.itemTitle = getResources().getString(R.string.property_port);
                        cubeTitleTextItem.itemText = mMenuAccountUIItemP.ethgw;
                        listItemProperty.add(cubeTitleTextItem);
                    }
                }
                commonAdapter = new CubePropertyNetAdapter(CubeSettingCommeActivity.this, listItemProperty, R.layout.activity_property_net);
                break;
            case BACKUP_HISTORY_VERSION:

                break;

            case SETTING_DEVICE_NAME:
                view = LayoutInflater.from(this).inflate(R.layout.activity_setting_name, null);
                mEditTextViewCubeName = (EditText) view.findViewById(R.id.register_tele_num);
                mListView.addHeaderView(view);
                break;
            case CHANGE_PWD:
                view = LayoutInflater.from(this).inflate(R.layout.activity_change_login_pwd, null);
                mEditTextViewOldPwd = (EditText) view.findViewById(R.id.et_password_old);
                mEditTextViewNewPwd = (EditText) view.findViewById(R.id.et_password_new);
                mShowPasswordOld = (CheckBox) view.findViewById(R.id.cb_show_password_old);
                mShowPasswordOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mEditTextViewOldPwd.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                });

                mShowPasswordNew = (CheckBox) view.findViewById(R.id.cb_show_password_new);
                mShowPasswordNew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mEditTextViewNewPwd.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                });
                mListView.addHeaderView(view);
                break;
            case EXCHANGE_CUBE:
                commonAdapter = new CubeExchangeAdapter(CubeSettingCommeActivity.this, listDevice, R.layout.activity_exchange_cube);
                break;

        }

        mListView.setAdapter(commonAdapter);
        initData();
    }

    public void initData() {

        switch (INTENT_TAG) {
            case BACKUP_HISTORY_VERSION:
                //获取备份列表
                AccountController.getBackupHistory(this);
                break;
            case SETTING_NAME:
                Loger.print(TAG, "获取的值" + AccountController.getCubeName(this), Thread.currentThread());
                mEditTextViewCubeName.setText(AccountController.getCubeName(this));
                break;
            case SETTING_DEVICE_NAME:

                mEditTextViewCubeName.setText(AccountController.getAccountUserName(this));
                break;
            case EXCHANGE_CUBE:
                AccountController.getCubeList(this);
                break;
        }

    }

    @Override
    public void onEventMainThread(CubeEvents event) {
        super.onEventMainThread(event);
        if (event instanceof CubeAccountEvent) {
            dismissLoadingDialog();
            final CubeAccountEvent event1 = (CubeAccountEvent) event;
            if (event1.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_GET_BACKUP_HISTORY) {
                if (event1.success) {


                    List<MenuCubeSettingBackup> list = (List<MenuCubeSettingBackup>) event1.object;
                    listBackup.addAll(list);
                    commonAdapter.notifyDataSetChanged();

                }

            } else if (event1.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_NAME) {

                if (event1.success) {
                    EventBus.getDefault().post(new CubeHeadInfoEvent(CubeEvents.CubeHeadINfoUpdateEventType.IN_SIDE_INFO_CHANGE));
                    CubeSettingCommeActivity.this.finish();
                    showToastShort(R.string.operation_success_tip);
                } else {
                    showToastShort((String) event1.object);
                }

            } else if (event1.type == CubeEvents.CubeAccountEventType.SET_ALIASNAME) {
                EventBus.getDefault().post(new CubeHeadInfoEvent(CubeEvents.CubeHeadINfoUpdateEventType.OUT_SIDE_INFO_CHANGE));
                CubeSettingCommeActivity.this.finish();
            } else if (event1.type == CubeEvents.CubeAccountEventType.GET_CUBE_LIST) {

                if (event1.success) {
                    List<MenuDeviceUIItem> list = (List<MenuDeviceUIItem>) event1.object;
                    listDevice.addAll(list);
                    commonAdapter.notifyDataSetChanged();
                }
            } else if (event1.type == CubeEvents.CubeAccountEventType.CUBE_SETTING) {
                if (event1.success) {
                    showToastShort(R.string.operation_success_tip);
                    CubeSettingCommeActivity.this.finish();
                } else {
                    showToastShort((String) event1.object);
                }
            } else if (event1.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_PWD) {
                if (event1.success) {
                    showToastShort(R.string.operation_success_tip);
                    CubeSettingCommeActivity.this.finish();
                } else {
                    showToastShort((String) event1.object);
                }
            } else if (event1.type == CubeEvents.CubeAccountEventType.SET_ALARM_PWD) {
                if (event1.success) {
                    showToastShort(R.string.operation_success_tip);
                    CubeSettingCommeActivity.this.finish();
                } else {
                    showToastShort((String) event1.object);
                }
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQUEST_CODE) {

            //处理请求回来的值

            ScanController.connectToCube(getApplicationContext(), "", "", ModelEnum.SCANED_TYPE_REPLACE_CUBE);

        }
    }


}


