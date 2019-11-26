package com.honeywell.cube.utils;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.RegisterActivity;

/**
 * Created by zhujunyu on 16/6/2.
 */
public class CubeCount extends CubeCountTimer {

    public TextView mTextView;
    private Context mContext;

    public CubeCount(Context context, long millisInFuture, long countDownInterval, TextView textView) {
        super(millisInFuture, countDownInterval);
        mTextView = textView;
        this.mContext = context;
    }

    @Override
    public void onFinish() {
        mTextView.setEnabled(true);
        mTextView.setText(R.string.retry);
    }

    @Override
    public void onTick(long millisUntilFinished, int percent) {
        long myhour = (millisUntilFinished / 1000) / 3600;
        long myminute = ((millisUntilFinished / 1000) - myhour * 3600) / 60;
        long mysecond = millisUntilFinished / 1000 - myhour * 3600
                - myminute * 60;
        PreferenceUtil.setTimerCount(mContext, mysecond,
                RegisterActivity.REGISTER_TYPE);
        mTextView.setText(mContext.getString(R.string.retry) + "(" + mysecond + ")");
    }
}
