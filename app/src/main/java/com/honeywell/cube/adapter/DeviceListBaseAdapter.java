
package com.honeywell.cube.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.DeviceEditActivity;
import com.honeywell.cube.db.configuredatabase.BasicLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.cube.controllers.menus.MenuDeviceController;
import com.honeywell.lib.widgets.SlideView;

import java.util.List;


public abstract class DeviceListBaseAdapter extends BaseAdapter {
    public List<? extends ItemBean> mDataList;

    private LayoutInflater mInflater;
    private Dialog mDialog;
    protected SlideView mLastSlideViewWithStatusOn;
    protected Context mContext;
    protected int mDeletePosition = -1;

    public DeviceListBaseAdapter(Context context, List<? extends ItemBean> list, Dialog dialog) {
        mInflater = LayoutInflater.from(context);
        mDialog = dialog;
        mDataList = list;
        mContext = context;
    }

    public abstract int getItemLayout(int position);

    public int getSlideViewLayout() {
        return R.layout.layout_slide_view_edit_delete;
    }

    public abstract ItemHolder initItemHolder(SlideView slideView, int position);

    protected void initView(ItemHolder holder, int position) {
        Object object = mDataList.get(position).mLoop;
        if (object instanceof BasicLoop) {
            enableItem(holder, ((BasicLoop) object).isOnline);
        } else if (object instanceof IpcStreamInfo) {
            enableItem(holder, ((IpcStreamInfo) object).isOnline);
        }
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        SlideView slideView = (SlideView) convertView;
        if (null == slideView) {
            View itemView = mInflater.inflate(getItemLayout(position), null);
            slideView = new SlideView(parent.getContext(), getSlideViewLayout());
            slideView.setContentView(itemView);
            holder = initItemHolder(slideView, position);

            slideView.setTag(holder);
            slideView.setOnSlideListener(new SlideView.OnSlideListener() {
                @Override
                public void onSlide(View view, int status) {
                    if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
                        mLastSlideViewWithStatusOn.shrink();
                    }

                    if (status == SLIDE_STATUS_ON) {
                        mLastSlideViewWithStatusOn = (SlideView) view;
                    }
                }
            });
        } else {
            holder = (ItemHolder) slideView.getTag();
        }
        if (holder.mIconDelete != null) {
            holder.mIconDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    asyncelete(position);
                }
            });
        }
        if (holder.mIconEdit != null) {
            holder.mIconEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit(position);
                }
            });
        }
        slideView.shrink();
        slideView.setOnClickListener(getItemClickListener(position));
        initView(holder, position);
        return slideView;
    }

    protected View.OnClickListener getItemClickListener(int position) {
        return null;
    }

    public List<? extends ItemBean> getDataList() {
        return mDataList;
    }

    public void setDataList(List<ItemBean> dataList) {
        this.mDataList = dataList;
    }

    public void showDialog() {
        if (mDialog != null) {//&& !mDialog.isShowing()
            mDialog.show();
        }
    }

    protected void edit(int position) {
        Intent intent = new Intent(mContext, DeviceEditActivity.class);
        DeviceHelper.addObject2Intent(intent, DeviceEditActivity.DEVICE_LOOP, mDataList.get(position).mLoop);
        ((Activity) mContext).startActivityForResult(intent, 1);
        if (mLastSlideViewWithStatusOn != null) {
            mLastSlideViewWithStatusOn.shrinkImmediately();
        }
    }

    protected void asyncelete(final int position) {
        showDialog();
        mDeletePosition = position;
        new Thread() {
            @Override
            public void run() {
                super.run();
                delete(position);
            }
        }.start();
    }

    protected void delete(int position) {
        MenuDeviceController.deleteDevice(mContext, mDataList.get(position).mLoop);
    }

    protected void enableItem(ItemHolder itemHolder, boolean enable) {
        if (itemHolder.mDisableIcon != null) {
            itemHolder.mDisableIcon.setVisibility(enable ? View.GONE : View.VISIBLE);
            final SlideView view = itemHolder.mConvertView;
            view.setEnabled(enable);
            view.setSlidable(enable);
            enableView(view, R.id.tv_text, enable);
            enableView(view, R.id.cb_switch, enable);
            enableView(view, R.id.iv_expandable, enable);
            enableView(view, R.id.thumb_layout, enable);
            enableView(view, R.id.iv_navigation, enable);
            enableView(view, R.id.tv_text_secondary, enable);
        }
    }

    protected void enableView(View view, int id, boolean enable) {
        final View v = view.findViewById(id);
        if (v != null && v.getVisibility() == ViewGroup.VISIBLE) {
            v.setEnabled(enable);
        }
    }

    public void updateDeleteUI() {
        if (mDeletePosition != -1) {
            mDataList.remove(mDeletePosition);
            notifyDataSetChanged();
            mDeletePosition = -1;
        }
    }

    public static class ItemHolder {
        protected ImageView mIconDelete;
        protected ImageView mIconEdit;
        protected SlideView mConvertView;
        protected ImageView mDisableIcon;

        public ItemHolder(SlideView view) {
            mConvertView = view;
            mIconDelete = (ImageView) view.findViewById(R.id.iv_delete);
            mIconEdit = (ImageView) view.findViewById(R.id.iv_edit);
            mDisableIcon = (ImageView) view.findViewById(R.id.iv_disable);
        }
    }

    public static class ItemBean {
        public int mIconId;
        public Drawable mIcon;
        public String mText;
        public Object mLoop;
        public String mDeviceType;

        public ItemBean(int iconId, Drawable icon, String text, Object loop, String type) {
            mIconId = iconId;
            mIcon = icon;
            mText = text;
            mLoop = loop;
            mDeviceType = type;

        }
    }

}
