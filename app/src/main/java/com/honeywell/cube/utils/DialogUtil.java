
package com.honeywell.cube.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.honeywell.cube.R;
import com.honeywell.lib.utils.ToastUtil;

import java.security.PublicKey;

public class DialogUtil {
    public static ProgressDialog showProgress(Activity activity, String hintText) {
        Activity mActivity = null;
        if (activity.getParent() != null) {
            mActivity = activity.getParent();
            if (mActivity.getParent() != null) {
                mActivity = mActivity.getParent();
            }
        } else {
            mActivity = activity;
        }
        final Activity finalActivity = mActivity;
        ProgressDialog window = ProgressDialog.show(finalActivity, "", hintText);
        window.getWindow().setGravity(Gravity.CENTER);

        window.setCancelable(false);
        return window;
    }

    public static class DialogBuild {

        private Context mContext;
        private Dialog mDialog;
        private String mPhoneNum;
        private EditText mEditText;

        public DialogBuild(Context context, String phoneNum) {
            this.mContext = context;
            this.mPhoneNum = phoneNum;
        }

        public DialogBuild createDialog() {

            mDialog = new Dialog(mContext, R.style.dialog);
            mDialog = new AlertDialog.Builder((Activity) mContext).create();
            mDialog.show();
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            return this;
        }

        public DialogBuild createView(final DialogListener dialogListener) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.regist_dialog_layout, null);
            mDialog.getWindow().setContentView(layout);
            mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mDialog.getWindow().setGravity(Gravity.CENTER);

            TextView mTextView = (TextView) layout.findViewById(R.id.dialog_tv_phone);

            mTextView.setText(mPhoneNum);

            Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);
            Button confirmBtn = (Button) layout.findViewById(R.id.dialog_btn_confim);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    dialogListener.confirm();
                }
            });

            return this;

        }

        public DialogBuild createSearchDeviceView(final DialogEditListener dialogListener) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.search_device_layout, null);
            mDialog.getWindow().setContentView(layout);
            mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mDialog.getWindow().setGravity(Gravity.CENTER);
            TextView textView = (TextView) layout.findViewById(R.id.tv_wifi_name);
            mEditText = (EditText) layout.findViewById(R.id.et_wifi_pwd);
            textView.setText(mPhoneNum);

            Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);
            Button confirmBtn = (Button) layout.findViewById(R.id.dialog_btn_confim);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(TextUtils.isEmpty(mEditText.getText().toString())){
                        ToastUtil.showLong(mContext,"请输入密码");
                    }else{
                        mDialog.dismiss();
                        dialogListener.confirm(mEditText.getText().toString());
                    }


                }
            });
            return this;

        }


        public DialogBuild createSearchDeviceViewGif(final DialogListener dialogListener) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.search_device_loading_layout, null);
            mDialog.getWindow().setContentView(layout);
            mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mDialog.getWindow().setGravity(Gravity.CENTER);
            ImageView imageView = (ImageView) layout.findViewById(R.id.iv_gif);

            Glide.with(mContext).load(R.mipmap.easy_link).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);


            Button confirmBtn = (Button) layout.findViewById(R.id.dialog_btn_confim);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    dialogListener.confirm();
                }
            });
            return this;

        }


        public DialogBuild createUpdateRecoverNewDialog(final DialogListener dialogListener, String dialogTitle) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.only_title_dialog_layout, null);
            mDialog.getWindow().setContentView(layout);
            mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mDialog.getWindow().setGravity(Gravity.CENTER);
            TextView textView = (TextView) layout.findViewById(R.id.tv_dialog_title);
            textView.setText(dialogTitle);



            Button confirmBtn = (Button) layout.findViewById(R.id.dialog_btn_confim);
            Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    dialogListener.confirm();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            return this;
        }

        public DialogBuild createRecoverDialog(final DialogEditListener dialogListener) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.recover_description_layout, null);
            mDialog.getWindow().setContentView(layout);
            mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mDialog.getWindow().setGravity(Gravity.CENTER);
            final EditText mEditText = (EditText) layout.findViewById(R.id.et_discription);
            Button confirmBtn = (Button) layout.findViewById(R.id.dialog_btn_confim);
            Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);



                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String str = mEditText.getText().toString();
                        if (TextUtils.isEmpty(str)) {
                            ToastUtil.showShort(mContext, "请输入描述");
                        } else {
                            mDialog.dismiss();
                            dialogListener.confirm(str);
                        }
                    }
                });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            return this;

        }


        public DialogBuild setListener() {


            return this;
        }


        public interface DialogListener {
            void confirm();

        }

        public interface DialogEditListener {
            void confirm(String string);

        }

    }



}
