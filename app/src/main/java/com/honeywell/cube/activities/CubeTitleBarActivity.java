package com.honeywell.cube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.dialogs.LoadingDialog;
import com.honeywell.lib.utils.ToastUtil;

import de.greenrobot.event.EventBus;

public abstract class CubeTitleBarActivity extends CubeBaseActivity {
    protected ImageView mLeft;
    protected ImageView mRight;
    protected TextView mTextViewTitle;
    private LoadingDialog mLoadingDialog;
    protected String mTitle;
    protected int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initIntentValue();
        setContentView(getContent());
        initTitleBar();
        initView();
        getData();
    }

    protected void initView() {
    }

    protected void initIntentValue() {
        mTitle = getIntent().getStringExtra(Constants.TITLE);
        mType = getIntent().getIntExtra(Constants.TYPE, -1);
    }

    protected void getData() {
    }

    protected abstract int getContent();

    protected void initTitleBar() {
        mLeft = (ImageView) findViewById(R.id.iv_left);
        mRight = (ImageView) findViewById(R.id.iv_right);
        mTextViewTitle = (TextView) findViewById(R.id.tv_title);
        initLeftIcon(mLeft);
        initRightIcon(mRight);
        initTitle(mTextViewTitle);
    }

    protected void initLeftIcon(ImageView left) {
        left.setImageResource(R.mipmap.nav_back_normal);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void initRightIcon(ImageView right) {

    }

    protected void initTitle(TextView title) {
        if (!TextUtils.isEmpty(mTitle)) {
            title.setText(mTitle);
        }
    }

    public void showLoadingDialog() {
        if (null == mLoadingDialog) {
            initLoadingDialog();
        }
        mLoadingDialog.show();
    }

    protected LoadingDialog getLoadingDialog() {
        if (null == mLoadingDialog) {
            initLoadingDialog();
        }
        return mLoadingDialog;
    }

    private void initLoadingDialog() {
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(CubeEvents event) {
        if (event instanceof CubeBasicEvent) {
            final CubeBasicEvent cubeBasicEvent = (CubeBasicEvent) event;
            if (cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.TIME_OUT) {
//                ToastUtil.showShort(this, cubeBasicEvent.getMessage());
                dismissLoadingDialog();
            }else if(cubeBasicEvent.getType() == CubeEvents.CubeBasicEventType.CONNECTING_LOST) {
                finish();
            }
        }
    }

    protected void startAsynchronousOperation(Runnable run) {
        showLoadingDialog();
        new Thread(run).start();

    }

    public void showToastShort(int res) {
        showToastShort(getString(res));
    }

    public void showToastShort(String text) {
        ToastUtil.showShort(this, text, true);
    }

    public void showToastLong(int res) {
        showToastLong(getString(res));
    }

    public void showToastLong(String text) {
        ToastUtil.showLong(this, text, true);
    }

    public String getTitleString() {
        return mTitle;
    }

    public void finishSuccess() {
        finishWithResult(Constants.SUCCESS);
    }

    public void finishWithResult(String result) {
        Intent intent = new Intent();
        intent.putExtra(Constants.RESULT, result);
        setResult(RESULT_OK, intent);
        finish();
    }
}
