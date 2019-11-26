
package com.honeywell.cube.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.DeviceAddWICustomFirstActivity;
import com.honeywell.cube.activities.DeviceAddWICustomSecondActivity;
import com.honeywell.cube.activities.DeviceWICustomActivity;
import com.honeywell.cube.controllers.UIItem.menu.MenuDeviceIRIconItem;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.lib.dialogs.LoadingDialog;
import com.honeywell.lib.utils.ToastUtil;

import java.util.ArrayList;


public class SelectIRCustomIconAdapter extends BaseAdapter {
    public final static int ICON_SELECT_FIRST = 0;
    public final static int ICON_SELECT_SECONDARY = 1;
    public final static int ICON_CONTROL = 2;
    LayoutInflater mInflater;
    ArrayList<MenuDeviceIRIconItem> mDataList;
    Context mContext;
    int mType;
    LoadingDialog mLoadingDialog;

    public SelectIRCustomIconAdapter(Context context, ArrayList<MenuDeviceIRIconItem> list, int type, LoadingDialog dialog) {
        mInflater = LayoutInflater.from(context);
        mDataList = list;
        mContext = context;
        mType = type;
        mLoadingDialog = dialog;
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
            convertView = mInflater.inflate(R.layout.grid_select_ir_custom_icon, null);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }
        final MenuDeviceIRIconItem item = mDataList.get(position);
        holder.mText.setText(item.IR_icon_name);
        if (mType == ICON_SELECT_SECONDARY) {
            holder.mIcon.setImageResource(item.IR_icon_enable ? item.IR_icon_imageSelectId : item.IR_icon_imageId);
        } else {
            holder.mIcon.setImageResource(item.IR_icon_imageSelectId);
        }
        holder.mSelectedFlag.setVisibility(item.IR_icon_select && mType == ICON_SELECT_FIRST ? View.VISIBLE : View.INVISIBLE);
        holder.mIconGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == ICON_SELECT_FIRST) {
                    if (position == getCount() - 1) {
                        showInputDialog();
                    } else {
                        item.IR_icon_select = !item.IR_icon_select;
                        holder.mSelectedFlag.setVisibility(item.IR_icon_select ? View.VISIBLE : View.INVISIBLE);
                    }
                } else if (mType == ICON_SELECT_SECONDARY) {
                    ((DeviceAddWICustomSecondActivity) mContext).studyIrCode(position, item);
                } else if (mType == ICON_CONTROL) {
                    ((DeviceWICustomActivity) mContext).sendIRMessage(item);
                }
            }
        });
        return convertView;
    }

    public void showLoadingDialog() {
        if (mLoadingDialog != null && (!mLoadingDialog.isShowing())) {
            mLoadingDialog.show();
        }
    }

    public void update(int position, boolean enabled) {
        boolean enable = mDataList.get(position).IR_icon_enable;
        mDataList.get(position).IR_icon_enable = enabled;
    }

    public void clear() {
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                mDataList.get(i).IR_icon_select = false;
            }
        }
    }

    public ArrayList<MenuDeviceIRIconItem> getSelectItems() {
        ArrayList<MenuDeviceIRIconItem> result = new ArrayList<>();
        if (mDataList != null && mDataList.size() > 0) {
            final int size = mDataList.size();
            for (int i = 0; i < size; i++) {
                final MenuDeviceIRIconItem item = mDataList.get(i);
                if (item.IR_icon_select) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    private AlertDialog mInputDialog;

    private void showInputDialog() {
        if (null == mInputDialog) {
            mInputDialog = new AlertDialog.Builder(mContext).create();
            mInputDialog.show();
            Window window = mInputDialog.getWindow();
            window.setContentView(R.layout.recover_description_layout);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        window.setWindowAnimations(R.style.AnimBottom);
            TextView title = (TextView) window.findViewById(R.id.tv_dialog_title);
            title.setText(R.string.input_custom_name);
            final EditText name = (EditText) window.findViewById(R.id.et_discription);
            Button ok = (Button) window.findViewById(R.id.dialog_btn_confim);
            Button cancel = (Button) window.findViewById(R.id.dialog_btn_cancel);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String text = name.getText().toString();
                    if (TextUtils.isEmpty(text)) {
                        ToastUtil.showShort(mContext, R.string.input_name);
                    } else {
                        //TODO check
                        MenuDeviceIRIconItem newItem = new MenuDeviceIRIconItem(name.getText().toString(), ModelEnum.DEVICE_IR_ADD_CUSTOMIZE + " " + name.getText().toString(), R.mipmap.ir_control_customize_g, R.mipmap.ir_control_customize_b, true);
                        final int size = mDataList.size();
                        for (int i = 0; i < size; i++) {
                            final MenuDeviceIRIconItem item = mDataList.get(i);
                            if (item.compareIrImageName(newItem)) {
                                ToastUtil.showShort(mContext, R.string.exist_name, true);
                                return;
                            }
                        }
                        mDataList.add(mDataList.size() - 1, newItem);
                        ((DeviceAddWICustomFirstActivity) mContext).notifyDataSetChanged();
                        mInputDialog.dismiss();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInputDialog.dismiss();
                }
            });
        } else {
            mInputDialog.show();
        }

    }

    static class ItemHolder {
        ImageView mIcon;
        View mSelectedFlag;
        View mIconGroup;
        TextView mText;

        public ItemHolder(View view) {
            mIcon = (ImageView) view.findViewById(R.id.iv_icon);
            mSelectedFlag = view.findViewById(R.id.fl_selected);
            mIconGroup = view.findViewById(R.id.fl_icon);
            mText = (TextView) view.findViewById(R.id.tv_name);
        }
    }

}
