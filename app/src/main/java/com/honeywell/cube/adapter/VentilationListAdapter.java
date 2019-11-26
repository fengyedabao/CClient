
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.honeywell.cube.activities.DeviceVentilationActivity;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;

import java.util.List;


public class VentilationListAdapter extends AirConditionerListAdapter {

    public VentilationListAdapter(Context context, List<? extends ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }


    @Override
    protected View.OnClickListener getItemClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ItemBean item = mDataList.get(position);
                Intent intent = new Intent(mContext, DeviceVentilationActivity.class);
                intent.putExtra(Constants.TITLE, item.mText);
                DeviceHelper.addObject2Intent(intent, Constants.CONTENT, item.mLoop);
                mContext.startActivity(intent);
            }
        };
    }

}
