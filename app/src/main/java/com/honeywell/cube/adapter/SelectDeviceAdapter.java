
package com.honeywell.cube.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.SelectDeviceActivity;
import com.honeywell.cube.activities.SetDeviceStatusActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuScheduleDeviceObject;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.ScenarioLoop;
import com.honeywell.cube.utils.Constants;

import java.util.ArrayList;

public class SelectDeviceAdapter extends DeviceAddDetailBaseAdapter {
    private int mSelectType;

    public SelectDeviceAdapter(Context context, int selectType, ArrayList<DeviceAddDetailBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, null, dialog);
        mSelectType = selectType;
        mDataList = list;
        initDateHead();
    }

    @Override
    int getHeaderLayout() {
        return -1;
    }

    @Override
    int getLoopLayout() {
        return R.layout.list_select_device;
    }

    @Override
    public DeviceAddDetailBaseAdapter.ItemHolder initItemHolder(View view, int position) {
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void initView(DeviceAddDetailBaseAdapter.ItemHolder holder, final int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        final ItemBean itemBean = (ItemBean) mDataList.get(position);
        final int itemType = itemBean.mItemType;
        switch (itemType) {
            case TYPE_HEADER:
                break;
            case TYPE_SECTION:
                itemHolder.mSection.setText(itemBean.mSection);
                break;
            case TYPE_LOOP:
                itemHolder.mText.setText(itemBean.mText);
                itemHolder.mText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mSelectType) {
                            case Constants.SELECT_TYPE_SCENARIO:
                                Intent intent = new Intent();
                                intent.putExtra(Constants.RESULT, itemBean.mText);
                                intent.putExtra(Constants.TYPE, mSelectType);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.CONTENT, (ScenarioLoop) itemBean.mObject);
                                intent.putExtras(bundle);
                                ((Activity) mContext).setResult(Activity.RESULT_OK, intent);
                                ((Activity) mContext).finish();
                                break;
                            case Constants.SELECT_TYPE_DEVICE:
                                MenuScheduleDeviceObject item = (MenuScheduleDeviceObject) itemBean.mObject;
                                if (ModelEnum.LOOP_IPC.equalsIgnoreCase(item.loopType)) {
                                    ((SelectDeviceActivity) mContext).selectDevice(item);
                                } else {
                                    Intent intent2 = new Intent(mContext, SetDeviceStatusActivity.class);
                                    intent2.putExtra(Constants.TITLE, mContext.getString(R.string.set_status));
                                    intent2.putExtra(Constants.TYPE, mSelectType);
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putParcelable(Constants.CONTENT, item);
                                    intent2.putExtras(bundle2);
                                    ((Activity) mContext).startActivityForResult(intent2, 1);
                                }
                                break;
                            case Constants.SELECT_TYPE_ZONE:
                                Intent intent3 = new Intent();
                                intent3.putExtra(Constants.TYPE, mSelectType);
                                Bundle bundle3 = new Bundle();
                                bundle3.putParcelable(Constants.CONTENT, (MenuScheduleDeviceObject) itemBean.mObject);
                                intent3.putExtras(bundle3);
                                ((Activity) mContext).setResult(Activity.RESULT_OK, intent3);
                                ((Activity) mContext).finish();
                                break;
                            default:
                                break;
                        }
                    }
                });

                break;
        }
    }


    protected static class ItemHolder extends DeviceAddDetailBaseAdapter.ItemHolder {
        public TextView mText;

        public ItemHolder(View view) {
            super(view);
            mText = (TextView) view.findViewById(R.id.tv_text);
        }
    }

    public static class ItemBean extends DeviceAddDetailBaseAdapter.ItemBean {
        String mText;
        Object mObject;

        public ItemBean(int itemType, String section, String text, Object object) {
            super(itemType, section, null, null);
            mText = text;
            mObject = object;
        }
    }

}
