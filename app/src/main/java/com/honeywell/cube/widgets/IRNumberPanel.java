package com.honeywell.cube.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by milton on 16/6/7.
 */
public class IRNumberPanel extends RelativeLayout {
    public static final String TAG = IRNumberPanel.class.getSimpleName();
    ArrayList<ImageView> mViewList;
    ArrayList<MenuDeviceIRIconItem> mDataList;
    OnItemClickListener mOnItemClickListener;

    public IRNumberPanel(Context context) {
        super(context);
    }

    public IRNumberPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.widget_ir_control_number_panel, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        if (mViewList != null && mViewList.size() > 0) {
            final int size = mViewList.size();
            for (int i = 0; i < size; i++) {
                final int position = i;
                mViewList.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.click(v, position);
                        }
                    }
                });
            }
        }
    }

    public IRNumberPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initView() {
        mViewList = new ArrayList<>();
        mViewList.add((ImageView) findViewById(R.id.siv_num1));
        mViewList.add((ImageView) findViewById(R.id.siv_num2));
        mViewList.add((ImageView) findViewById(R.id.siv_num3));
        mViewList.add((ImageView) findViewById(R.id.siv_num4));
        mViewList.add((ImageView) findViewById(R.id.siv_num5));
        mViewList.add((ImageView) findViewById(R.id.siv_num6));
        mViewList.add((ImageView) findViewById(R.id.siv_num7));
        mViewList.add((ImageView) findViewById(R.id.siv_num8));
        mViewList.add((ImageView) findViewById(R.id.siv_num9));
        mViewList.add((ImageView) findViewById(R.id.siv_asterisk));
        mViewList.add((ImageView) findViewById(R.id.siv_num0));
        mViewList.add((ImageView) findViewById(R.id.siv_ok));
    }

    public void setDataList(ArrayList<MenuDeviceIRIconItem> dataList) {
        if (dataList == null || mViewList.size() != dataList.size()) {
            LogUtil.e(TAG, "dataList is illegal , dataList = " + dataList, true);
            return;
        }
        mDataList = dataList;
        final int size = mDataList.size();
        for (int i = 0; i < size; i++) {
            final MenuDeviceIRIconItem item = mDataList.get(i);
            mViewList.get(i).setImageResource(item.IR_icon_enable ? item.IR_icon_imageSelectId : item.IR_icon_imageId);
        }
    }

    public void updateView(int position, boolean enabled) {

        if (mDataList == null || mDataList.size() != mViewList.size()) {
            LogUtil.e(TAG, "mDataList is illegal , mDataList = " + mDataList, true);
            return;
        }
        if (position >= mDataList.size() || position < 0) {
            LogUtil.e(TAG, "position is illegal , position = " + position, true);
            return;
        }
        final MenuDeviceIRIconItem item = mDataList.get(position);
        item.IR_icon_enable = enabled;
        mViewList.get(position).setImageResource(item.IR_icon_enable ? item.IR_icon_imageSelectId : item.IR_icon_imageId);
    }

    public ArrayList<MenuDeviceIRIconItem> getDataList() {
        return mDataList;
    }

    public interface OnItemClickListener {
        public void click(View v, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
