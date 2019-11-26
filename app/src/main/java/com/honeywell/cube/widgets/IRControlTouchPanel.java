package com.honeywell.cube.widgets;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by milton on 16/6/7.
 */
public class IRControlTouchPanel extends TextView {
    private OnTouchListener mOnTouchListener;
    int mStartX;
    int mStartY;
    int mEndX;
    int mEndY;
    int mTouchSlop;
    ArrayList<MenuDeviceIRIconItem> mDataList;

    public IRControlTouchPanel(Context context) {
        super(context);
    }

    public IRControlTouchPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public IRControlTouchPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getRawX();
                mStartY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mEndX = (int) event.getRawX();
                mEndY = (int) event.getRawY();
                calculateTouchPosition();
                break;
            default:
                break;
        }
        return true;
    }

    private void calculateTouchPosition() {
        if (mOnTouchListener != null) {
            final int deltaX = mEndX - mStartX;
            final int deltaY = mEndY - mStartY;
            LogUtil.e("IRControlTouchPanel", " deltax = " + deltaX + " , deltaY = " + deltaY + " , mTouchSlop = " + mTouchSlop);
            if (Math.abs(deltaX) <= mTouchSlop && Math.abs(deltaY) <= mTouchSlop) {
                mOnTouchListener.onClicked();
            } else if (Math.abs(deltaX) < Math.abs(deltaY)) {
                if (deltaY > 0) {
                    mOnTouchListener.onDownTouched();
                } else {
                    mOnTouchListener.onUpTouched();
                }
            } else {
                if (deltaX > 0) {
                    mOnTouchListener.onRightTouched();
                } else {
                    mOnTouchListener.onLeftTouched();
                }
            }
        }
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    public interface OnTouchListener {
        public void onLeftTouched();

        public void onRightTouched();

        public void onUpTouched();

        public void onDownTouched();

        public void onClicked();
    }

    public void setDataList(ArrayList<MenuDeviceIRIconItem> dataList) {
        mDataList = dataList;
    }

    public ArrayList<MenuDeviceIRIconItem> getDataList() {
        return mDataList;
    }
}
