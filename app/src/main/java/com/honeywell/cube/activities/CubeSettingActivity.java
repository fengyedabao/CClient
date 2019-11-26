package com.honeywell.cube.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.honeywell.cube.R;
import com.honeywell.cube.controllers.AccountController;
import com.honeywell.cube.controllers.CubeController;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.net.easylink.EasyLinkManager;
import com.honeywell.cube.utils.events.CubeAccountEvent;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.qrcode.CaptureActivity;
import com.honeywell.cube.utils.DialogUtil;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeHeadInfoEvent;
import com.honeywell.lib.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujunyu on 16/6/22.
 */
public class CubeSettingActivity extends CubeTitleBarActivity implements View.OnClickListener {

    private RelativeLayout mRelativeLayoutHead;
    private BottomDialog mPicSelectDialog;
    private RelativeLayout mRelativeLayoutInfo;
    private RelativeLayout mRelativeLayoutName;
    private RelativeLayout mRelativeLayoutchangeLoginPwd;
    private RelativeLayout mRelativeLayoutchangeSafetyPwd;
    private RelativeLayout mRelativeLayoutPropertyNet;
    private RelativeLayout mRelativeLayoutVoiceRecognition;
    private RelativeLayout mRelativeLayoutSearchDevice;
    private RelativeLayout mRelativeLayoutSetCity;

    private RelativeLayout mRelativeLayoutUpdate;
    private RelativeLayout mRelativeLayoutBackUp;
    private RelativeLayout mRelativeLayoutRecoverToNew;
    private RelativeLayout mRelativeLayoutRecoverToHistory;

    private ImageView mImageView;
    private TextView mUpdateVersion;
    private ImageView mImageViewUpdateFlag;
    private TextView mTextViewLocation;
    private CheckBox mCheckBoxVoice;

    private Uri mOutPutFileUri;

    private static final int CLOUD = 100;
    private static final int WIFI = 200;
    private int flag;

    @Override
    protected int getContent() {
        return R.layout.activity_cube_setting;
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setText(R.string.cube_setting_title);
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        flag = getIntent().getIntExtra("into_type", 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextViewLocation.setText(AccountController.getCubelocation(getApplicationContext()));
    }


    @Override
    public void initView() {
        mRelativeLayoutHead = (RelativeLayout) findViewById(R.id.rl_cube_head);
        mRelativeLayoutInfo = (RelativeLayout) findViewById(R.id.rl_cube_info);
        mRelativeLayoutName = (RelativeLayout) findViewById(R.id.rl_setting_name);
        mRelativeLayoutchangeLoginPwd = (RelativeLayout) findViewById(R.id.rl_change_login_pwd);
        mRelativeLayoutchangeSafetyPwd = (RelativeLayout) findViewById(R.id.rl_change_safety_pwd);
        mRelativeLayoutPropertyNet = (RelativeLayout) findViewById(R.id.rl_property_net);
        mRelativeLayoutVoiceRecognition = (RelativeLayout) findViewById(R.id.rl_voice_recognition);
        mRelativeLayoutSearchDevice = (RelativeLayout) findViewById(R.id.rl_search_device);
        mRelativeLayoutSetCity = (RelativeLayout) findViewById(R.id.rl_set_city);

        mRelativeLayoutUpdate = (RelativeLayout) findViewById(R.id.rl_update);
        mRelativeLayoutBackUp = (RelativeLayout) findViewById(R.id.rl_backup);
        mRelativeLayoutRecoverToNew = (RelativeLayout) findViewById(R.id.rl_recover_backup);
        mRelativeLayoutRecoverToHistory = (RelativeLayout) findViewById(R.id.rl_recover_history_backup);
        mImageView = (ImageView) findViewById(R.id.iv_head);
//        mUpdateVersion = (TextView) findViewById(R.id.tv_update_version);
        mImageViewUpdateFlag = (ImageView) findViewById(R.id.iv_update_flag);
        mTextViewLocation = (TextView) findViewById(R.id.tv_location);
        mCheckBoxVoice = (CheckBox) findViewById(R.id.cb_show_password);
        if (flag == CLOUD) {
            //全部显示

        } else if (flag == WIFI) {
            mRelativeLayoutSetCity.setVisibility(View.GONE);
            mRelativeLayoutSearchDevice.setVisibility(View.GONE);
        }
        mCheckBoxVoice.setChecked(AccountController.getVoiceRecgStatus(this));
        initEvent();
        initData();
    }

    public void initEvent() {
        mRelativeLayoutHead.setOnClickListener(this);
        mRelativeLayoutInfo.setOnClickListener(this);
        mRelativeLayoutName.setOnClickListener(this);
        mRelativeLayoutchangeLoginPwd.setOnClickListener(this);
        mRelativeLayoutchangeSafetyPwd.setOnClickListener(this);
        mRelativeLayoutPropertyNet.setOnClickListener(this);
//        mRelativeLayoutVoiceRecognition.setOnClickListener(this);
        mRelativeLayoutSearchDevice.setOnClickListener(this);
        mRelativeLayoutSetCity.setOnClickListener(this);
        mRelativeLayoutUpdate.setOnClickListener(this);
        mRelativeLayoutBackUp.setOnClickListener(this);
        mRelativeLayoutRecoverToNew.setOnClickListener(this);
        mRelativeLayoutRecoverToHistory.setOnClickListener(this);
        mCheckBoxVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    showLoadingDialog();
                    AccountController.setCubeVoiceRecognizeState(CubeSettingActivity.this, true);
                } else {
                    showLoadingDialog();
                    AccountController.setCubeVoiceRecognizeState(CubeSettingActivity.this, false);
                }

            }
        });
    }

    public void initData() {


//        mImageViewUpdateFlag

        String version = AccountController.getCubeVersionNum(getApplicationContext());
//        mUpdateVersion.setText(version);
        String imgPath = PreferenceUtil.getUserHeadPic(this);
        if (TextUtils.isEmpty(imgPath)) {
            mImageView.setImageResource(R.mipmap.account_default);
            return;
        }
        Glide.with(CubeSettingActivity.this).load(imgPath).into(mImageView);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CubeSettingActivity.this, CubeSettingCommeActivity.class);

        if (v == mRelativeLayoutHead) {
            showSelectDialog();

        } else if (v == mRelativeLayoutInfo) {
            intent.putExtra("intent_tag", CubeSettingCommeActivity.CUBE_INFO);
            startActivity(intent);

        } else if (v == mRelativeLayoutName) {
            intent.putExtra("intent_tag", CubeSettingCommeActivity.SETTING_NAME);
            startActivity(intent);
        } else if (v == mRelativeLayoutchangeLoginPwd) {
            intent.putExtra("intent_tag", CubeSettingCommeActivity.CHANGE_LOGIN_PWD);
            startActivity(intent);
        } else if (v == mRelativeLayoutchangeSafetyPwd) {
            intent.putExtra("intent_tag", CubeSettingCommeActivity.CHANGE_SAFETY_PWD);
            startActivity(intent);
        } else if (v == mRelativeLayoutPropertyNet) {

            Intent intentProperty = new Intent(CubeSettingActivity.this, CubePropertyNetActivity.class);
            startActivity(intentProperty);
        } else if (v == mRelativeLayoutVoiceRecognition) {


        } else if (v == mRelativeLayoutSearchDevice) {

            Intent i = new Intent(this, CaptureActivity.class);
            i.putExtra("capture_type", ModelEnum.SCANED_TYPE_REPLACE_CUBE);
            startActivity(i);

        } else if (v == mRelativeLayoutSetCity) {

            Intent i = new Intent(CubeSettingActivity.this, SetCityActivity.class);
            startActivity(i);

        } else if (v == mRelativeLayoutUpdate) {
            AppInfo info = AppInfoFunc.getCurrentUser(this);
            if (LoginController.getInstance(getApplicationContext()).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
                info = AppInfoFunc.getGuestUser(this);
            }
            String nowVersion = info.cube_version;
            if (nowVersion != null && !nowVersion.equalsIgnoreCase(CubeController.cubeNewVersion)) {
                DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, "");
                build.createDialog().createUpdateRecoverNewDialog(new DialogUtil.DialogBuild.DialogListener() {
                    @Override
                    public void confirm() {
                        AccountController.updateCubeVersion(getApplicationContext());
                        showLoadingDialog();
                    }
                }, getString(R.string.please_confirm_upgrade_cube));
            } else {
                DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, "");
                build.createDialog().createUpdateRecoverNewDialog(new DialogUtil.DialogBuild.DialogListener() {
                    @Override
                    public void confirm() {
                    }
                }, getString(R.string.no_new_version));
            }


        } else if (v == mRelativeLayoutBackUp) {
            DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, "");
            build.createDialog().createRecoverDialog(new DialogUtil.DialogBuild.DialogEditListener() {
                @Override
                public void confirm(String string) {
                    AccountController.backupCubeVersion(CubeSettingActivity.this, string);
                    showLoadingDialog();
                }
            });
        } else if (v == mRelativeLayoutRecoverToNew) {
            DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, "");
            build.createDialog().createUpdateRecoverNewDialog(new DialogUtil.DialogBuild.DialogListener() {
                @Override
                public void confirm() {
                    AccountController.recoveryCubeVersion(getApplicationContext(), "");
                    showLoadingDialog();
                }
            }, getString(R.string.sure_restore_to_latest));

        } else if (v == mRelativeLayoutRecoverToHistory) {
            intent.putExtra("intent_tag", CubeSettingCommeActivity.BACKUP_HISTORY_VERSION);
            startActivity(intent);
        }
    }

    private void showSelectDialog() {
        final List<BottomDialog.ItemBean> dataList = new ArrayList<>();
        dataList.add(new BottomDialog.ItemBean(getString(R.string.select_picture), null));
        dataList.add(new BottomDialog.ItemBean(getString(R.string.take_picture), null));
        if (null == mPicSelectDialog) {
            mPicSelectDialog = new BottomDialog(this);
            mPicSelectDialog.setViewCreateListener(
                    new BottomDialog.ViewCreateListener() {
                        @Override
                        public void initTop(TextView top) {
                            top.setVisibility(View.GONE);
                        }

                        @Override
                        public void initContent(ListView content) {
                            content.setAdapter(new BottomDialog.ListAdapter(CubeSettingActivity.this, -1, dataList, false, new BottomDialog.ListAdapter.OnItemClickListener() {
                                @Override
                                public void itemClick(View view, int position, int index) {
                                    if (position == 0) {
                                        Intent intent = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
                                        startActivityForResult(intent, 2);
                                    } else {
                                        String path = Environment.getExternalStorageDirectory().toString() + "/cube";
                                        File path1 = new File(path);
                                        if (!path1.exists()) {
                                            path1.mkdirs();
                                        }
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机

                                        File file = new File(path1, System.currentTimeMillis() + ".jpg");
                                        mOutPutFileUri = Uri.fromFile(file);

                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
                                        startActivityForResult(intent, 1);
                                    }
                                    mPicSelectDialog.dismiss();
                                }
                            }));
                        }
                    }

            );
        }

        if (mPicSelectDialog != null && !mPicSelectDialog.isShowing()) {
            mPicSelectDialog.show();
        }
    }

    private void popDialog(final String wifiSSID) {
        DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(this, wifiSSID);
        build.createDialog().createSearchDeviceView(new DialogUtil.DialogBuild.DialogEditListener() {
            @Override
            public void confirm(final String password) {

//
                new Thread() {
                    public void run() {
                        EasyLinkManager.newInstance(CubeSettingActivity.this).startEasyLink(password);

                        EasyLinkManager.newInstance(CubeSettingActivity.this).startConfigBroadLink(wifiSSID, password);
                    }
                }.start();

                DialogUtil.DialogBuild build = new DialogUtil.DialogBuild(CubeSettingActivity.this, null);
                build.createDialog().createSearchDeviceViewGif(new DialogUtil.DialogBuild.DialogListener() {
                    @Override
                    public void confirm() {

                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case Activity.RESULT_OK://照相完成点击确定
                        String sdStatus = Environment.getExternalStorageState();
                        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                            LogUtil.v("TestFile", "SD card is not avaiable/writeable right now.");
                            return;
                        }

                        Glide.with(CubeSettingActivity.this).load(mOutPutFileUri).into(mImageView);
                        PreferenceUtil.saveUserHeadPic(CubeSettingActivity.this, mOutPutFileUri.toString());
                        EventBus.getDefault().post(new CubeHeadInfoEvent(CubeEvents.CubeHeadINfoUpdateEventType.HEAD_ICON_CHANGE));
                        break;
                    case Activity.RESULT_CANCELED:// 取消
                        break;
                }
                break;
            case 2:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Uri uri = data.getData();
                        Cursor cursor = this.getContentResolver().query(uri, null,
                                null, null, null);
                        cursor.moveToFirst();
                        String imgNo = cursor.getString(0); // 图片编号
                        String imgPath = cursor.getString(1); // 图片文件路径
                        String imgSize = cursor.getString(2); // 图片大小
                        String imgName = cursor.getString(3); // 图片文件名
                        cursor.close();
//                        ToastUtil.show(this, imgPath, 1000);
                        Glide.with(CubeSettingActivity.this).load(imgPath).into(mImageView);
                        PreferenceUtil.saveUserHeadPic(CubeSettingActivity.this, imgPath);
                        EventBus.getDefault().post(new CubeHeadInfoEvent(CubeEvents.CubeHeadINfoUpdateEventType.HEAD_ICON_CHANGE));
                        break;
                    case Activity.RESULT_CANCELED:// 取消
                        break;
                }


                break;
        }


    }

    @Override
    public void onEventMainThread(CubeEvents cubeEvents) {
        super.onEventMainThread(cubeEvents);
        if (cubeEvents instanceof CubeAccountEvent) {
            CubeAccountEvent accountEvent = (CubeAccountEvent) cubeEvents;
            if (accountEvent.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_VOICE_REGNIZE) {
                dismissLoadingDialog();
                showToastShort((String) accountEvent.object);
            } else if (accountEvent.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_RECOVERY ||
                    accountEvent.type == CubeEvents.CubeAccountEventType.CUBE_SETTING_BACKUP) {
                dismissLoadingDialog();
                showToastShort((String) accountEvent.object);
            }
        }

    }


}
