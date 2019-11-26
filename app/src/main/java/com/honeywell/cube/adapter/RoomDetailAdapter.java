
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.activities.DeviceAirConditionerActivity;
import com.honeywell.cube.activities.DeviceVentilationActivity;
import com.honeywell.cube.activities.DeviceWIACActivity;
import com.honeywell.cube.activities.DeviceWICommonActivity;
import com.honeywell.cube.activities.DeviceWICustomActivity;
import com.honeywell.cube.activities.IpcPlayerActivity;
import com.honeywell.cube.controllers.DeviceControllers.BackAudioController;
import com.honeywell.cube.controllers.DeviceControllers.DeviceController;
import com.honeywell.cube.controllers.DeviceControllers.RelayController;
import com.honeywell.cube.controllers.DeviceControllers.SparkLightingController;
import com.honeywell.cube.controllers.DeviceControllers.Wireless315M433MController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.SparkLightingLoop;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.Wireless315M433MLoop;
import com.honeywell.cube.fragments.DeviceRootFragment;
import com.honeywell.cube.utils.Constants;
import com.honeywell.cube.utils.DeviceHelper;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.SlideView;
import com.honeywell.lib.widgets.TimeTickerView;

import java.util.List;


public class RoomDetailAdapter extends DeviceListBaseAdapter {
    public static final int TYPE_EXPANDABLE_Light = 0;

    public static final int TYPE_EXPANDABLE_CURTAIN = 1;
    public static final int TYPE_EXPANDABLE_BACKAUDIO = 2;
    public static final int TYPE_SIMPLE_DEFAULT = 3;
    public static final int TYPE_SIMPLE_ZONE = 4;
    public static final int TYPE_SIMPLE_IPC = 5;
    public static final int TYPE_SIMPLE_JUMP = 6;


    public RoomDetailAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    public int getItemLayout(int position) {
        int type = getItemViewType(position);
        int layoutId;
        switch (type) {
            case TYPE_EXPANDABLE_Light:
                layoutId = R.layout.list_light_expandable;
                break;
            case TYPE_EXPANDABLE_CURTAIN:
                layoutId = R.layout.list_curtain;
                break;
            case TYPE_EXPANDABLE_BACKAUDIO:
                layoutId = R.layout.list_backaudio;
                break;
            case TYPE_SIMPLE_DEFAULT:
                layoutId = R.layout.list_light_simple;
                break;
            case TYPE_SIMPLE_ZONE:
                layoutId = R.layout.list_zone;
                break;
            case TYPE_SIMPLE_IPC:
                layoutId = R.layout.list_ip_camera;
                break;
            case TYPE_SIMPLE_JUMP:
                layoutId = R.layout.list_ir;
                break;
            default:
                layoutId = R.layout.list_light_simple;
                break;
        }
        return layoutId;
    }

    @Override
    public ItemHolder initItemHolder(SlideView slideView, final int position) {
        return new ItemHolder(slideView);
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder holder, final int position) {
        super.initView(holder, position);
        final ItemBean data = (ItemBean) mDataList.get(position);
        final int type = getItemViewType(position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.tvName.setText(data.mText);
        switch (type) {
            case TYPE_EXPANDABLE_Light:
                itemHolder.ivIcon.setImageResource(data.mIconId);
                itemHolder.sbProcess.setProgress(data.mPercent);
                itemHolder.cbOpen.setChecked(data.bIsOpen);
                itemHolder.cbOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (DeviceRootFragment.DEBUG_NO_NET) {
                            return;
                        }
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.bIsOpen = ((CheckBox) v).isChecked();
                        if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(mDataList.get(position).mDeviceType)) {
                            showDialog();
                            Wireless315M433MController.sendWireless315M433MState(null, (Wireless315M433MLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mPercent);
                        } else {
                            showDialog();
                            SparkLightingController.sendSparkLightingState(null, (SparkLightingLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mPercent);
                        }
                    }
                });
                itemHolder.sbProcess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.mPercent = seekBar.getProgress();
                        if (progress == 0) {
                            if (item.bIsOpen) {
                                itemHolder.cbOpen.setChecked(false);
                                item.bIsOpen = false;
                            }
                        } else {
                            if (!item.bIsOpen) {
                                itemHolder.cbOpen.setChecked(true);
                                item.bIsOpen = true;
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (DeviceRootFragment.DEBUG_NO_NET) {
                            return;
                        }
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.mPercent = seekBar.getProgress();
                        if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(mDataList.get(position).mDeviceType)) {
                            showDialog();
                            Wireless315M433MController.sendWireless315M433MState(null, (Wireless315M433MLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mPercent);
                        } else {
                            showDialog();
                            SparkLightingController.sendSparkLightingState(null, (SparkLightingLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mPercent);
                        }
                    }
                });
                break;
            case TYPE_EXPANDABLE_CURTAIN:
                itemHolder.ivIcon.setImageResource(data.mIconId);
                itemHolder.iconSecondaryLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCurtainStatus(position, ModelEnum.CURTAIN_STATUS_OPENING);
                    }
                });
                itemHolder.iconSecondaryMiddle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCurtainStatus(position, ModelEnum.CURTAIN_STATUS_PAUSING);
                    }
                });
                itemHolder.iconSecondaryRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCurtainStatus(position, ModelEnum.CURTAIN_STATUS_CLOSING);
                    }
                });
                break;
            case TYPE_EXPANDABLE_BACKAUDIO:
                itemHolder.cbPausePrimary.setChecked(data.bIsPause);
                itemHolder.cbPauseSecondary.setChecked(data.bIsPause);
                itemHolder.tvName.setText(data.mMusicName);
                itemHolder.tvMusicNameSecondary.setText(data.mMusicName);
                itemHolder.tvNameSecondary.setText(data.mStatus);
                itemHolder.tvStateSecondary.setText(data.mStatus);
                itemHolder.pbProgress.setProgress(data.mProgress);
                itemHolder.pbProgress.setSecondaryProgress(data.mProgress);
                itemHolder.tvProgressTip.setText(data.mProgressTip);
                itemHolder.tvProgressTip.init(((BackaudioLoop) data.mLoop).customModel.playtime, ((BackaudioLoop) data.mLoop).customModel.allplaytime);
                itemHolder.sbVolume.setProgress(data.mVolume);
                itemHolder.tvVolumeTip.setText(data.mVolumeTip);
                itemHolder.tvProgressTip.setOnTimerListener(new TimeTickerView.TimerListener() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onProgress(int progress, int currentTime) {
                        itemHolder.pbProgress.setProgress(progress);
                        itemHolder.pbProgress.setSecondaryProgress(progress);
                        ((BackaudioLoop) data.mLoop).customModel.playtime = currentTime;
                    }
                });
                checkProgressTip(itemHolder, data);
                itemHolder.cbPausePrimary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.bIsPause = ((CheckBox) v).isChecked();
                        itemHolder.cbPauseSecondary.setChecked(data.bIsPause);
                        setbackAudioStatusWithBody(position, data.bIsPause ? ModelEnum.BACKAUDIO_STATUS_PAUSE : ModelEnum.BACKAUDIO_STATUS_START);
                        checkProgressTip(itemHolder, data);
                    }
                });

                itemHolder.cbPauseSecondary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.bIsPause = ((CheckBox) v).isChecked();
                        itemHolder.cbPausePrimary.setChecked(data.bIsPause);
                        setbackAudioStatusWithBody(position, data.bIsPause ? ModelEnum.BACKAUDIO_STATUS_PAUSE : ModelEnum.BACKAUDIO_STATUS_START);
                        checkProgressTip(itemHolder, data);
                    }
                });

                itemHolder.ivPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setbackAudioStatusWithBody(position, ModelEnum.BACKAUDIO_STATUS_PREVIOUS);
                    }
                });

                itemHolder.ivNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setbackAudioStatusWithBody(position, ModelEnum.BACKAUDIO_STATUS_NEXT);
                    }
                });

                itemHolder.cbMute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.bIsMute = ((CheckBox) v).isChecked();
                        setbackAudioStatusWithBody(position, data.bIsMute ? ModelEnum.BACKAUDIO_STATUS_MUTE : ModelEnum.BACKAUDIO_STATUS_NO_MUTE);
                    }
                });

                itemHolder.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        itemHolder.tvVolumeTip.setText("" + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (DeviceRootFragment.DEBUG_NO_NET) {
                            return;
                        }
                        showDialog();
                        data.mVolume = seekBar.getProgress();
                        ((BackaudioLoop) data.mLoop).customModel.volume = seekBar.getProgress();
                        BackAudioController.volumeValueChangedWithBody(null, ((BackaudioLoop) data.mLoop), seekBar.getProgress());
                    }
                });
                break;
            case TYPE_SIMPLE_DEFAULT:
                itemHolder.ivIcon.setImageResource(data.mIconId);
                itemHolder.tvName.setText(data.mText);
                itemHolder.cbOpen.setChecked(data.bIsOpen);
                itemHolder.cbOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (DeviceRootFragment.DEBUG_NO_NET) {
                            return;
                        }
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        item.bIsOpen = ((CheckBox) v).isChecked();
                        showDialog();
                        if (ModelEnum.LOOP_RELAY.equalsIgnoreCase(item.mDeviceType)) {
                            showDialog();
                            RelayController.sendRelayState(null, (RelayLoop) item.mLoop, item.bIsOpen ? 1 : 0);
                        } else if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(mDataList.get(position).mDeviceType)) {
                            showDialog();
                            Wireless315M433MController.sendWireless315M433MState(null, (Wireless315M433MLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mPercent);
                        } else {
                            showDialog();
                            SparkLightingController.sendSparkLightingState(null, (SparkLightingLoop) item.mLoop, item.bIsOpen ? 1 : 0, item.mPercent);
                        }
                    }
                });
                break;
            case TYPE_SIMPLE_ZONE:
                itemHolder.tvName.setText(data.mText);
                itemHolder.mZoneIcon.setVisibility(DeviceController.checkZoneTypeIf24Hour(mDataList.get(position).mLoop) ? View.VISIBLE : View.GONE);
                break;
            case TYPE_SIMPLE_IPC:
                itemHolder.ivIcon.setImageResource(data.mIconId);
                itemHolder.tvName.setText(data.mText);
                itemHolder.tvNameSecondary.setText(data.mTextSecondary);
                break;
            case TYPE_SIMPLE_JUMP:
                itemHolder.ivIcon.setImageResource(data.mIconId);
                itemHolder.tvName.setText(data.mText);
                break;
            default:
                break;
        }
    }

    private void checkProgressTip(ItemHolder holder, ItemBean item) {
        if (item.bIsPause) {
            holder.tvProgressTip.stop();
        } else {
            holder.tvProgressTip.start();
        }
    }

    @Override
    protected View.OnClickListener getItemClickListener(final int position) {
        final int type = getItemViewType(position);
        switch (type) {
            case TYPE_SIMPLE_IPC:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, IpcPlayerActivity.class);
                        intent.putExtra(Constants.TITLE, mDataList.get(position).mText);
                        Bundle bundle = new Bundle();
                        final IpcStreamInfo ipcStreamInfo = (IpcStreamInfo) mDataList.get(position).mLoop;
                        ipcStreamInfo.mIPAddr = ((ItemBean) mDataList.get(position)).mTextSecondary;
                        bundle.putParcelable(Constants.IPC_STREAM_INFO, ipcStreamInfo);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                };
            case TYPE_SIMPLE_JUMP:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        final ItemBean item = (ItemBean) mDataList.get(position);
                        switch (item.mDeviceType) {
                            case ModelEnum.LOOP_IR_TV:
                                intent = new Intent(mContext, DeviceWICommonActivity.class);
                                intent.putExtra(Constants.DEVICE_TYPE, ModelEnum.MAIN_IR_TELEVISION);
                                break;
                            case ModelEnum.LOOP_IR_STB:
                                intent = new Intent(mContext, DeviceWICommonActivity.class);
                                intent.putExtra(Constants.DEVICE_TYPE, ModelEnum.MAIN_IR_STB);
                                break;
                            case ModelEnum.LOOP_IR_DVD:
                                intent = new Intent(mContext, DeviceWICommonActivity.class);
                                intent.putExtra(Constants.DEVICE_TYPE, ModelEnum.MAIN_IR_DVD);
                                break;
                            case ModelEnum.LOOP_IR_AC:
                                intent = new Intent(mContext, DeviceWIACActivity.class);
                                intent.putExtra(Constants.DEVICE_TYPE, ModelEnum.MAIN_IR_AC);
                                break;
                            case ModelEnum.LOOP_IR_CUSTOM:
                                intent = new Intent(mContext, DeviceWICustomActivity.class);
                                intent.putExtra(Constants.DEVICE_TYPE, ModelEnum.MAIN_IR_CUSTOMIZE);
                                break;
                            case ModelEnum.LOOP_VENTILATION:
                                intent = new Intent(mContext, DeviceVentilationActivity.class);
                                intent.putExtra(Constants.TITLE, item.mText);
                                break;
                            case ModelEnum.WIFI_485:
                            case ModelEnum.LOOP_BACNET:
                                intent = new Intent(mContext, DeviceAirConditionerActivity.class);
                                intent.putExtra(Constants.TITLE, mContext.getString(R.string.device_type_air_conditioner));
                                break;
                            default:
                                break;
                        }
                        if (intent != null) {
                            DeviceHelper.addObject2Intent(intent, Constants.CONTENT, item.mLoop);
                            mContext.startActivity(intent);
                        }
                    }
                };

            default:
                return null;
        }

    }

    @Override
    public int getItemViewType(int position) {

        return ((ItemBean) mDataList.get(position)).mType;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    protected void delete(int position) {
        super.delete(position);
        final int type = getItemViewType(position);
        switch (type) {
            case TYPE_EXPANDABLE_Light:
                break;
            case TYPE_EXPANDABLE_CURTAIN:
                break;
            case TYPE_EXPANDABLE_BACKAUDIO:
                break;
            case TYPE_SIMPLE_DEFAULT:
                break;
            case TYPE_SIMPLE_ZONE:
                break;
            case TYPE_SIMPLE_IPC:
                break;
            case TYPE_SIMPLE_JUMP:
                break;
            default:
                break;
        }
    }

    @Override
    protected void edit(int position) {
        super.edit(position);
    }

    private void sendCurtainStatus(int position, int status) {
        if (DeviceRootFragment.DEBUG_NO_NET) {
            return;
        }
        final DeviceListBaseAdapter.ItemBean item = getDataList().get(position);
        if (ModelEnum.WIRELESS_315_433.equalsIgnoreCase(item.mDeviceType)) {
            showDialog();
            Wireless315M433MController.sendCurtainStatus(null, (Wireless315M433MLoop) item.mLoop, status);
        } else {
            showDialog();
            SparkLightingController.sendCurtainStatus(null, (SparkLightingLoop) item.mLoop, status);
        }
    }

    private void setbackAudioStatusWithBody(int position, int type) {
        if (DeviceRootFragment.DEBUG_NO_NET) {
            return;
        }
        showDialog();
        ItemBean item = (ItemBean) mDataList.get(position);
        BackAudioController.setbackAudioStatusWithBody(null, (BackaudioLoop) item.mLoop, type);
    }

    protected static class ItemHolder extends DeviceListBaseAdapter.ItemHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public ImageView ivExpandable;

        public TextView tvNameSecondary;

        public SeekBar sbProcess;
        public CheckBox cbOpen;

        public ImageView iconSecondaryLeft;
        public ImageView iconSecondaryMiddle;
        public ImageView iconSecondaryRight;


        public CheckBox cbPausePrimary;
        //        public TextView tvMusicNamePrimary;  tvName
//        public TextView tvStatePrimary;  tvNameSecondary
        public RelativeLayout rlCloseTitle;
        public TextView tvMusicNameSecondary;
        public TextView tvStateSecondary;
        public TimeTickerView tvProgressTip;
        public ProgressBar pbProgress;
        public ImageView ivPrevious;
        public CheckBox cbPauseSecondary;
        public ImageView ivNext;
        public CheckBox cbMute;
        public SeekBar sbVolume;
        public TextView tvVolumeTip;

        public ImageView mZoneIcon;

        public ItemHolder(SlideView view) {
            super(view);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            tvName = (TextView) view.findViewById(R.id.tv_text);
            ivExpandable = (ImageView) view.findViewById(R.id.iv_expandable);

            tvNameSecondary = (TextView) view.findViewById(R.id.tv_text_secondary);

            sbProcess = (SeekBar) view.findViewById(R.id.sb_light);
            cbOpen = (CheckBox) view.findViewById(R.id.cb_switch);

            iconSecondaryLeft = (ImageView) view.findViewById(R.id.iv_secondary_left);
            iconSecondaryMiddle = (ImageView) view.findViewById(R.id.iv_secondary_middle);
            iconSecondaryRight = (ImageView) view.findViewById(R.id.iv_secondary_right);

            cbPausePrimary = (CheckBox) view.findViewById(R.id.cb_pause_primary);
//            tvMusicNamePrimary = (TextView) view.findViewById(R.id.tv_text);
//            tvStatePrimary = (TextView) view.findViewById(R.id.tv_text_secondary);
            rlCloseTitle = (RelativeLayout) view.findViewById(R.id.rl_close_title);
            tvMusicNameSecondary = (TextView) view.findViewById(R.id.tv_music_name_secondary);
            tvStateSecondary = (TextView) view.findViewById(R.id.tv_state_secondary);
            tvProgressTip = (TimeTickerView) view.findViewById(R.id.tv_progress_tip);
            pbProgress = (ProgressBar) view.findViewById(R.id.pb_progress);
            ivPrevious = (ImageView) view.findViewById(R.id.iv_previous);
            cbPauseSecondary = (CheckBox) view.findViewById(R.id.cb_pause_secondary);
            ivNext = (ImageView) view.findViewById(R.id.iv_next);
            cbMute = (CheckBox) view.findViewById(R.id.cb_mute);
            sbVolume = (SeekBar) view.findViewById(R.id.sb_volume);
            tvVolumeTip = (TextView) view.findViewById(R.id.tv_volume_tip);

            mZoneIcon = (ImageView) view.findViewById(R.id.iv_zone_icon);
        }
    }


    public static class ItemBean extends DeviceListBaseAdapter.ItemBean {
        public int mPercent;
        public boolean bIsOpen;
        public int mType;
        String mTextSecondary;

        public String mMusicName;
        public boolean bIsPause;
        public boolean bIsMute;
        public String mStatus;
        public int mProgress;
        public String mProgressTip;
        public int mVolume;
        public String mVolumeTip;

        public ItemBean(int type, String text, Object loop) {
            this(type, -1, text, "", loop);
        }

        public ItemBean(int type, int iconId, String text, String deviceType, Object loop) {
            this(type, iconId, text, "", deviceType, loop);
        }

        public ItemBean(int type, int iconId, String text, String textSecondary, String deviceType, Object loop) {
            super(iconId, null, text, loop, deviceType);
            mType = type;
            mTextSecondary = textSecondary;
        }

        public ItemBean(int type, int iconId, String text, String deviceType, boolean isOpen, Object loop) {
            this(type, iconId, text, deviceType, isOpen, 0, loop);
        }

        public ItemBean(int type, int iconId, String text, String deviceType, boolean isOpen, int percent, Object loop) {
            super(iconId, null, text, loop, deviceType);
            mType = type;
            bIsOpen = isOpen;
            mPercent = percent;
        }

        public ItemBean(int type, int iconId, String deviceType, String musicName, boolean isPause, boolean isMute, String status, int progress, String progressTip, int volume, String volumeTip, Object loop) {
            super(iconId, null, musicName, loop, deviceType);
            mType = type;
            mMusicName = musicName;
            bIsPause = isPause;
            bIsMute = isMute;
            mStatus = status;
            mProgress = progress;
            mProgressTip = progressTip;
            mVolume = volume;
            mVolumeTip = volumeTip;
        }
    }

}
