
package com.honeywell.cube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.DeviceControllers.BackAudioController;
import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioLoop;
import com.honeywell.cube.fragments.DeviceRootFragment;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.widgets.SlideView;
import com.honeywell.lib.widgets.TimeTickerView;

import java.util.List;


public class BackaudioListAdapter extends DeviceListBaseAdapter {

    public BackaudioListAdapter(Context context, List<? extends DeviceListBaseAdapter.ItemBean> list, Dialog dialog) {
        super(context, list, dialog);
    }

    @Override
    public int getItemLayout(int position) {
        return R.layout.list_backaudio;
    }

    @Override
    public DeviceListBaseAdapter.ItemHolder initItemHolder(SlideView slideView, final int position) {
        final ItemHolder holder = new ItemHolder(slideView);
        slideView.setOnSlideListener(null);
        final ItemBean item = (ItemBean) getDataList().get(position);
        return holder;
    }

    @Override
    public void initView(DeviceListBaseAdapter.ItemHolder h, final int position) {
        super.initView(h, position);
        final ItemHolder holder = (ItemHolder) h;
        final ItemBean item = (ItemBean) mDataList.get(position);

        holder.cbPausePrimary.setChecked(item.bIsPause);
        holder.cbPauseSecondary.setChecked(item.bIsPause);
        holder.tvMusicNamePrimary.setText(item.mMusicName);
        holder.tvMusicNameSecondary.setText(item.mMusicName);
        holder.tvStatePrimary.setText(item.mStatus);
        holder.tvStateSecondary.setText(item.mStatus);
        holder.pbProgress.setProgress(item.mProgress);
        holder.pbProgress.setSecondaryProgress(item.mProgress);
        holder.tvProgressTip.setText(item.mProgressTip);
        holder.tvProgressTip.init(((BackaudioLoop) item.mLoop).customModel.playtime, ((BackaudioLoop) item.mLoop).customModel.allplaytime);
        holder.tvProgressTip.setOnTimerListener(new TimeTickerView.TimerListener() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onProgress(int progress, int currentTime) {
                holder.pbProgress.setProgress(progress);
                holder.pbProgress.setSecondaryProgress(progress);
                ((BackaudioLoop) item.mLoop).customModel.playtime = currentTime;
            }
        });
        checkProgressTip(holder, item);
        holder.sbVolume.setProgress(item.mVolume);
        holder.tvVolumeTip.setText(item.mVolumeTip);

        holder.cbPausePrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.bIsPause = ((CheckBox) v).isChecked();
                holder.cbPauseSecondary.setChecked(item.bIsPause);
                setbackAudioStatusWithBody(position, item.bIsPause ? ModelEnum.BACKAUDIO_STATUS_PAUSE : ModelEnum.BACKAUDIO_STATUS_START);
                checkProgressTip(holder, item);
            }
        });

        holder.cbPauseSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.bIsPause = ((CheckBox) v).isChecked();
                holder.cbPausePrimary.setChecked(item.bIsPause);
                setbackAudioStatusWithBody(position, item.bIsPause ? ModelEnum.BACKAUDIO_STATUS_PAUSE : ModelEnum.BACKAUDIO_STATUS_START);
                checkProgressTip(holder, item);
            }
        });

        holder.ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setbackAudioStatusWithBody(position, ModelEnum.BACKAUDIO_STATUS_PREVIOUS);
            }
        });

        holder.ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setbackAudioStatusWithBody(position, ModelEnum.BACKAUDIO_STATUS_NEXT);
            }
        });

        holder.cbMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.bIsMute = ((CheckBox) v).isChecked();
                setbackAudioStatusWithBody(position, item.bIsMute ? ModelEnum.BACKAUDIO_STATUS_MUTE : ModelEnum.BACKAUDIO_STATUS_NO_MUTE);
            }
        });

        holder.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.tvVolumeTip.setText("" + progress);
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
                item.mVolume = seekBar.getProgress();
                ((BackaudioLoop) item.mLoop).customModel.volume = seekBar.getProgress();
                BackAudioController.volumeValueChangedWithBody(null, ((BackaudioLoop) item.mLoop), seekBar.getProgress());
            }
        });
    }

    private void checkProgressTip(ItemHolder holder, ItemBean item) {
        if (item.bIsPause) {
            holder.tvProgressTip.stop();
        } else {
            holder.tvProgressTip.start();
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
        public CheckBox cbPausePrimary;
        public TextView tvMusicNamePrimary;
        public TextView tvStatePrimary;

        public RelativeLayout rlCloseTitle;

        public ImageView ivExpandable;
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

        public ItemHolder(SlideView convertView) {
            super(convertView);
            cbPausePrimary = (CheckBox) convertView.findViewById(R.id.cb_pause_primary);
            tvMusicNamePrimary = (TextView) convertView.findViewById(R.id.tv_text);
            tvStatePrimary = (TextView) convertView.findViewById(R.id.tv_text_secondary);
            rlCloseTitle = (RelativeLayout) convertView.findViewById(R.id.rl_close_title);
            ivExpandable = (ImageView) convertView.findViewById(R.id.iv_expandable);
            tvMusicNameSecondary = (TextView) convertView.findViewById(R.id.tv_music_name_secondary);
            tvStateSecondary = (TextView) convertView.findViewById(R.id.tv_state_secondary);
            tvProgressTip = (TimeTickerView) convertView.findViewById(R.id.tv_progress_tip);
            pbProgress = (ProgressBar) convertView.findViewById(R.id.pb_progress);
            ivPrevious = (ImageView) convertView.findViewById(R.id.iv_previous);
            cbPauseSecondary = (CheckBox) convertView.findViewById(R.id.cb_pause_secondary);
            ivNext = (ImageView) convertView.findViewById(R.id.iv_next);
            cbMute = (CheckBox) convertView.findViewById(R.id.cb_mute);
            sbVolume = (SeekBar) convertView.findViewById(R.id.sb_volume);
            tvVolumeTip = (TextView) convertView.findViewById(R.id.tv_volume_tip);
        }
    }

    public static class ItemBean extends DeviceListBaseAdapter.ItemBean {
        public String mMusicName;
        public boolean bIsPause;
        public boolean bIsMute;
        public String mStatus;
        public int mProgress;
        public String mProgressTip;
        public int mVolume;
        public String mVolumeTip;

        public ItemBean(String musicName, boolean isPause, boolean isMute, String status, int progress, String progressTip, int volume, String volumeTip, Object loop) {
            super(-1, null, null, loop, null);
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
