package com.honeywell.cube.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by milton on 16/6/7.
 */
public class IRControlPanel extends RelativeLayout {
    final static String TAG = IRControlPanel.class.getSimpleName();
    ArrayList<View> mViewGroupList;
    ArrayList<ImageView> mViewIconList;
    ArrayList<TextView> mViewTextList;
    ArrayList<MenuDeviceIRIconItem> mDataList;
    OnItemClickListener mOnItemClickListener;

    public IRControlPanel(Context context) {
        super(context);
    }

    public IRControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.widget_ir_control_panel, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        if (mViewGroupList != null && mViewGroupList.size() > 0) {
            final int size = mViewGroupList.size();
            for (int i = 0; i < size; i++) {
                final int position = i;
                View view = mViewGroupList.get(i);
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {
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
        mViewIconList.get(2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.click(v, 2);
                }
            }
        });
        mViewTextList.get(2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.click(v, 2);
                }
            }
        });

    }

    public IRControlPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        mViewGroupList = new ArrayList<>();
        mViewGroupList.add(findViewById(R.id.rl_up));
        mViewGroupList.add(findViewById(R.id.rl_left));
        mViewGroupList.add(findViewById(R.id.siv_num0));
        mViewGroupList.add(findViewById(R.id.rl_right));
        mViewGroupList.add(findViewById(R.id.rl_down));

        mViewIconList = new ArrayList<>();
        mViewIconList.add((ImageView) findViewById(R.id.iv_up));
        mViewIconList.add((ImageView) findViewById(R.id.iv_left));
        mViewIconList.add((ImageView) findViewById(R.id.iv_click));
        mViewIconList.add((ImageView) findViewById(R.id.iv_right));
        mViewIconList.add((ImageView) findViewById(R.id.iv_down));

        mViewTextList = new ArrayList<>();
        mViewTextList.add((TextView) findViewById(R.id.tv_up));
        mViewTextList.add((TextView) findViewById(R.id.tv_left));
        mViewTextList.add((TextView) findViewById(R.id.tv_click));
        mViewTextList.add((TextView) findViewById(R.id.tv_right));
        mViewTextList.add((TextView) findViewById(R.id.tv_down));
    }

    public void setDataList(ArrayList<MenuDeviceIRIconItem> dataList) {
        if (dataList == null || mViewIconList.size() != dataList.size()) {
            LogUtil.e(TAG, "dataList is illegal , dataList = " + dataList, true);
            return;
        }
        mDataList = dataList;
        final int size = mDataList.size();
        for (int i = 0; i < size; i++) {
            final MenuDeviceIRIconItem item = mDataList.get(i);
            mViewIconList.get(i).setImageResource(item.IR_icon_enable ? item.IR_icon_imageSelectId : item.IR_icon_imageId);
            mViewTextList.get(i).setTextColor(getResources().getColor(item.IR_icon_enable ? R.color.btn_background_blue : R.color.text_color_secondary));
        }
    }

    public void updateView(int position, boolean enabled) {

        if (mDataList == null || mDataList.size() != mViewIconList.size()) {
            LogUtil.e(TAG, "mDataList is illegal , mDataList = " + mDataList, true);
            return;
        }
        if (position >= mDataList.size() || position < 0) {
            LogUtil.e(TAG, "position is illegal , position = " + position, true);
            return;
        }
        final MenuDeviceIRIconItem item = mDataList.get(position);
        item.IR_icon_enable = enabled;
        mViewIconList.get(position).setImageResource(item.IR_icon_enable ? item.IR_icon_imageSelectId : item.IR_icon_imageId);
        mViewTextList.get(position).setTextColor(getResources().getColor(item.IR_icon_enable ? R.color.btn_background_blue : R.color.text_color_secondary));
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
