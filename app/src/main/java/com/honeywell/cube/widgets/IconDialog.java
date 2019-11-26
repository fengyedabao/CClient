package com.honeywell.cube.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.lib.dialogs.BottomDialog;
import com.honeywell.lib.utils.ScreenUtil;
import com.honeywell.lib.widgets.GridViewGallery;

import java.util.List;

/**
 * Created by milton on 16/5/27.
 */
public class IconDialog extends Dialog {
    public interface ViewCreateListener {
        public void initTop(TextView top);

        public void initContent(GridViewGallery content);
    }

    public IconDialog(Context context) {
        super(context, R.style.bottomDialogStyle);
    }

    private TextView mTitle;
    GridViewGallery mContent;
    private ViewCreateListener mViewCreateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setContentView(R.layout.dialog_wi_icon);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, View.MeasureSpec.makeMeasureSpec(ScreenUtil.getScreenHeight(getContext()) * 2 / 3, View.MeasureSpec.AT_MOST));
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimBottom);
        setCanceledOnTouchOutside(true);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mContent = (GridViewGallery) findViewById(R.id.gvg_content);
        if (null != mViewCreateListener) {
            mViewCreateListener.initTop(mTitle);
            mViewCreateListener.initContent(mContent);
        }
    }

    public ViewCreateListener getmViewCreateListener() {
        return mViewCreateListener;
    }

    public void setViewCreateListener(ViewCreateListener iewCreateListener) {
        this.mViewCreateListener = iewCreateListener;
    }

    public TextView getTop() {
        return mTitle;
    }

    public GridViewGallery getContent() {
        return mContent;
    }

    public static class IconAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        private List<BottomDialog.ItemBean> mDataList;
        Context mContext;
        private OnItemClickListener mOnItemClickListener;

        public interface OnItemClickListener {
            public void itemClick(View view, int position, int index);
        }

        public IconAdapter(Context context, List<BottomDialog.ItemBean> dataList, OnItemClickListener itemClickListener) {
            mInflater = LayoutInflater.from(context);
            mDataList = dataList;
            mContext = context;
            mOnItemClickListener = itemClickListener;
        }


        @Override
        public int getCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ItemHolder holder;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.grid_dialog_icon_item, null);
                holder = new ItemHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }
            final MenuDeviceIRIconItem item = (MenuDeviceIRIconItem) mDataList.get(position).mData;
            holder.mText.setText(item.IR_icon_name);
            holder.mIcon.setImageResource(item.IR_icon_imageSelectId);
            holder.mIconGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.itemClick(v, position, position);
                    }
                }
            });
            return convertView;
        }

        static class ItemHolder {
            ImageView mIcon;
            View mIconGroup;
            TextView mText;

            public ItemHolder(View view) {
                mIcon = (ImageView) view.findViewById(R.id.iv_icon);
                mIconGroup = view.findViewById(R.id.fl_icon);
                mText = (TextView) view.findViewById(R.id.tv_name);
            }
        }

        public List<BottomDialog.ItemBean> getDataList() {
            return mDataList;
        }

        public void setDataList(List<BottomDialog.ItemBean> dataList) {
            this.mDataList = dataList;
        }
    }


}
