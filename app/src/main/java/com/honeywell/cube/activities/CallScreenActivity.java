package com.honeywell.cube.activities;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.honeywell.cube.IAIDLJsonTcpService;
import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.controllers.CallController;
import com.honeywell.cube.ipc.AudioProcess;
import com.honeywell.cube.ipc.CallMsgDetailInfo;
import com.honeywell.cube.ipc.Constants;
import com.honeywell.cube.ipc.HVideoDecoder;
import com.honeywell.cube.ipc.ISimplePlayer;
import com.honeywell.cube.ipc.P2PCallBack;
import com.honeywell.cube.ipc.TextureViewListener;
import com.honeywell.cube.ipc.Util;
import com.honeywell.cube.ipc.VStreamBuffer;
import com.honeywell.cube.ipc.VideoInfo;
import com.honeywell.cube.ipc.nativeapi.P2PConn;
import com.honeywell.cube.utils.events.CubeCallEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;

import de.greenrobot.event.EventBus;


public class CallScreenActivity extends Activity implements OnClickListener, ISimplePlayer, P2PCallBack {
    private static final String TAG = CallScreenActivity.class.getSimpleName() + " IPCTAG_CALL";
    private static final int MSG_WHAT_TAKE_CALL = 100;
    private static final int MAX_QUEUE_SIZE = 20;
    private static final int MSG_WHAT_TERMINATE_CALL = MSG_WHAT_TAKE_CALL + 1;
    private static final int MSG_WHAT_ELAPSE_TIME = MSG_WHAT_TAKE_CALL + 2;

    private TextView mLoadingText;
    // open gl
    //private GLFrameRenderer mGLFRenderer;
    //private GLFrameSurface mGLSurface;
    // p2p
    private String mP2PUUID = null;
    private String mP2pConfDir = null;
    // multimedia
    private VideoInfo mVideoInfo = null;
    private TextureView mDecoderView;
    private TextureViewListener mDecoderTextureListener;
    private VideoDecoderThread mDecThread;
    private BlockingQueue<VStreamBuffer> mVideoPlayQueue = new LinkedBlockingQueue<VStreamBuffer>(MAX_QUEUE_SIZE);

    private ImageButton mDoorOpenBtn = null;
    private ImageButton mCallAnswerBtn = null;
    private ImageButton mCallEndBtn = null;
    private TextView mTvDown = null;
    private TextView mTvUp = null;
    private TextView mTvCountTime = null;
    private int mCallState = Constants.CALL_STATE_INCOMING;
    private BroadcastReceiver mBroadcastReceiver = null;
    private CallMsgDetailInfo mCallMsgDetailInfo = null;
    private ServiceConnection mJsonTcpServiceConnect = new ServiceConnectionImpl();
    public IAIDLJsonTcpService mIaidJsonTcpService = null;
    private String mTakeCallMsgId = null;
    private Timer mShowEclapseTimer = null;
    private long mLastTime = 0;
    private AudioProcess mAudioProcess = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallMsgDetailInfo = getIntent().getParcelableExtra(Constants.EXTRA_DATA_CALLMSG);
        if (null == mCallMsgDetailInfo) {
            LogUtil.i(TAG, "----IpcCallScreenActivity----->  onCreate() info null!");
            finish();
            return;
        }

        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
        setContentView(R.layout.activity_ipc_call_screen);
        if (!Util.detectOpenGLES20(this)) {
            LogUtil.e(TAG, "--------IpcCallScreenActivity---->    Do not support open gl", true);
        }
        //mGLSurface = (GLFrameSurface)findViewById(R.id.ipvdpsufaceview); // opengl
        mDecoderView = (TextureView) findViewById(R.id.ipvdp_texture_view); // mediacodec
        mLoadingText = (TextView) findViewById(R.id.tv_loading_tip);

        mDoorOpenBtn = (ImageButton) findViewById(R.id.opendoor_btn);
        mCallAnswerBtn = (ImageButton) findViewById(R.id.call_answer_btn);
        mCallEndBtn = (ImageButton) findViewById(R.id.call_reject_btn);

        mDoorOpenBtn.setOnClickListener(this);
        mCallAnswerBtn.setOnClickListener(this);
        mCallEndBtn.setOnClickListener(this);

        mTvDown = (TextView) findViewById(R.id.tv_down);
        mTvUp = (TextView) findViewById(R.id.tv_up);
        mTvCountTime = (TextView) findViewById(R.id.tv_count_time);

        mP2PUUID = CommonCache.sP2PUUID;
        LogUtil.i(TAG, "----IpcCallScreenActivity----->  onCreate()  mP2PUUID = " + mP2PUUID);
        if (mCallMsgDetailInfo != null) {
            mCallMsgDetailInfo.mUuid = mP2PUUID;
        }
        P2PConn.setIpVdpCallBack(this);

        // multimedia
        mDecoderTextureListener = new TextureViewListener();
        mDecoderView.setSurfaceTextureListener(mDecoderTextureListener);
        mDecThread = new VideoDecoderThread();

        startAndBindService(Constants.ACTION_SERVICE_JSONTCP, mJsonTcpServiceConnect);
        mAudioProcess = new AudioProcess(getApplicationContext(), "");//暂时是无参数构造函数

    }

    private void startAndBindService(String serviceAction, ServiceConnection connection) {
        LogUtil.d(TAG, "----IpcCallScreenActivity----->  startAndBindService  mCallMsgDetailInfo = " + mCallMsgDetailInfo.toString());
        if (null == serviceAction || null == connection) {
            return;
        }
        Intent intent = new Intent(serviceAction);
        intent.setPackage(getPackageName());
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    private void setAllViewState(int callState) {
        LogUtil.d(TAG, "----IpcCallScreenActivity----->  setAllViewState() callState=" + callState);
        switch (callState) {
            case Constants.CALL_STATE_INCOMING:
                mDecoderView.setVisibility(View.INVISIBLE);
                setBtnEnable(mDoorOpenBtn, true);
                setBtnEnable(mCallAnswerBtn, true);
                setBtnEnable(mCallEndBtn, true);

                setTextViewBright(true);
                mTvUp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                mTvDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                break;
            case Constants.CALL_STATE_ANSWERED:
//                mDecoderView.setVisibility(View.VISIBLE);
                setBtnEnable(mDoorOpenBtn, true);
                setBtnEnable(mCallAnswerBtn, false);
                mCallAnswerBtn.setVisibility(View.INVISIBLE);
                setBtnEnable(mCallEndBtn, true);
//                setTextViewBright(true);
//                mTvUp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
//                mTvDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                mTvCountTime.setVisibility(View.VISIBLE);
                mTvUp.setVisibility(View.GONE);
                mLoadingText.setVisibility(View.VISIBLE);
                break;
            case Constants.CALL_STATE_END:
                mDecoderView.setVisibility(View.VISIBLE);
                setBtnEnable(mDoorOpenBtn, false);
                setBtnEnable(mCallAnswerBtn, false);
                mCallAnswerBtn.setVisibility(View.INVISIBLE);
                setBtnEnable(mCallEndBtn, false);
                setTextViewBright(false);
                mTvUp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                mTvDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                break;
            default:
                break;
        }
    }

    private void setTextViewBright(boolean bBright) {
        int bright = bBright ? Constants.ALPHA_BRIGHT : Constants.ALPHA_DIM;
        mTvUp.setTextColor(Color.argb(bright, 255, 255, 255));
        mTvDown.setTextColor(Color.argb(bright, 255, 255, 255));
    }

    private void setBtnEnable(ImageButton btn, boolean bEnable) {
        if (null == btn) {
            return;
        }
        if (bEnable) {
            btn.getBackground().setAlpha(Constants.ALPHA_BRIGHT);
        } else {
            btn.getBackground().setAlpha(Constants.ALPHA_DIM);
        }
        btn.setEnabled(bEnable);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String sessionId = (String) msg.obj;
            switch (msg.what) {
                case MSG_WHAT_TERMINATE_CALL:
                    LogUtil.i(TAG, "----IpcCallScreenActivity----->  handleMessage() MSG_WHAT_TERMINATE_CALL 111");
                    terminateCall(sessionId);
                    break;
                case MSG_WHAT_TAKE_CALL:
                    LogUtil.i(TAG, "----IpcCallScreenActivity----->  handleMessage() MSG_WHAT_TAKE_CALL 222222222222");
                    mCallState = Constants.CALL_STATE_ANSWERED;
                    setAllViewState(mCallState);
                    mTvUp.setText(getString(R.string.lobby));
                    mTvDown.setText("00:00:00");
                    mLastTime = System.currentTimeMillis();
                    mShowEclapseTimer = new Timer();
                    mShowEclapseTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message m = Message.obtain();
                            m.what = MSG_WHAT_ELAPSE_TIME;
                            mHandler.sendMessage(m);
                        }
                    }, 1000, 1000);
                    if (null != mAudioProcess) {
                        try {
                            mAudioProcess.startPhoneRecordAndPlay();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_WHAT_ELAPSE_TIME: {
                    long nowTime = System.currentTimeMillis();
                    long l = nowTime - mLastTime;

                    long hour = (l / (60 * 60 * 1000));
                    long min = ((l / (60 * 1000)) - hour * 60);
                    long s = (l / 1000 - hour * 60 * 60 - min * 60);

                    String hourString = String.valueOf(hour);
                    if (hour < 10) {
                        hourString = "0" + hour;
                    }
                    String minString = String.valueOf(min);
                    if (min < 10) {
                        minString = "0" + min;
                    }


                    String sString = String.valueOf(s);
                    if (s < 10) {
                        sString = "0" + s;
                    }
                    String timeString = hourString + ":" + minString + ":" + sString;
                    if (null != mTvDown) {
                        mTvDown.setText(timeString);
                    }

                }
                default:
                    break;
            }

            return true;
        }
    });

    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, "----IpcCallScreenActivity----->  onPlayStart");
    }

    @Override
    public void onReceiveState(int state) {
        LogUtil.d(TAG, "----IpcCallScreenActivity----->  onReceiveState");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//判断按键键值做出相应操作
            //TODO: 后台通话？挂断？
            mCallEndBtn.performClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onEventMainThread(CubeEvents event) {
        LogUtil.d(TAG, "----IpcCallScreenActivity----->  event = " + event);
        if (event instanceof CubeCallEvent) {
            CubeCallEvent ev = (CubeCallEvent) event;
            if (ev.type == CubeEvents.CubeCallEventType.CALL_STOP) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mStartCount = false;
        EventBus.getDefault().unregister(this);
//        P2PConn.unInitConnLib();
        P2PConn.setRender(null);
        mDecThread.stop();
        mHandler.removeCallbacksAndMessages(null);
        if (null != mBroadcastReceiver) {
            unregisterReceiver(mBroadcastReceiver);
        }
        if (null != mJsonTcpServiceConnect) {
            unbindService(mJsonTcpServiceConnect);
        }
        if (null != mShowEclapseTimer) {
            mShowEclapseTimer.cancel();
        }
        try {
            if (null != mAudioProcess) {
                mAudioProcess.stopPhoneRecordAndPlay();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, " ----IpcCallScreenActivity---onDestroy-->  " + e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.opendoor_btn:
                setBtnEnable(mDoorOpenBtn, false);
                sendDoorOpenCallEvent();
                break;
            case R.id.call_answer_btn:
                mCallState = Constants.CALL_STATE_ANSWERED;
                setAllViewState(mCallState);
                //send teminate call json package
                new Thread(mDecThread).start();
                if (null != mAudioProcess) {
                    try {
                        mAudioProcess.startPhoneRecordAndPlay();
                    } catch (Exception e) {
                        LogUtil.e(TAG, "Exception e =  " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                new CounterThread().start();
                sendTakeCallEvent();
                break;
            case R.id.call_reject_btn:
                mCallState = Constants.CALL_STATE_END;
                setAllViewState(mCallState);
                mTvUp.setText(getString(R.string.end));
                mTvDown.setText(getString(R.string.lobby));
                sendTeminateCallEvent();
                terminateCall(mCallMsgDetailInfo.mCallSessionId);
                break;
            default:
                break;
        }
    }

    private void sendDoorOpenCallEvent() {
        CallController.getInstance(getApplicationContext()).openDoor(this, mCallMsgDetailInfo);
    }

    private void sendTeminateCallEvent() {
        CallController.getInstance(getApplicationContext()).stopCallSession(this, mCallMsgDetailInfo);
    }

    private void terminateCall(String sessionId) {
        if (null == sessionId) {
            return;
        }
        if (sessionId.equals(mCallMsgDetailInfo.mCallSessionId)) {
            mCallState = Constants.CALL_STATE_END;
            setAllViewState(mCallState);
            finish();
        }
    }

    @Override
    public void dataCallBack(byte[] data, int dataLen, String uuid) {
        LogUtil.e(TAG, "----IpcCallScreenActivity-----> dataCallBack");
        // ipc video
    }

    @Override
    public void dataCallBackCallV(byte[] data, int dataLen, String uuid) {
        LogUtil.e(TAG, "----IpcCallScreenActivity-----> dataCallBackCallV  mVideoPlayQueue size = " + mVideoPlayQueue.size());
        // ipvdp video
        VStreamBuffer stream = new VStreamBuffer();
        stream.StreamData = ByteBuffer.wrap(data);
        stream.StreamLen = dataLen;
        stream.UUID = uuid;
        mVideoPlayQueue.offer(stream);
    }

    String mUUID = null;

    @Override
    public void dataCallBackCallA(byte[] data, int dataLen, String uuid) {
        if (mUUID == null) {
            mUUID = uuid;
            mAudioProcess.setUuid(uuid);
            LogUtil.e(TAG, "----IpcCallScreenActivity-----> 2222222222 =  mAudioProcess.setUuid(uuid);  " + uuid);
        }
        //Log.e(TAG, "dataCallBackCallA() data len:" + dataLen);
        LogUtil.e(TAG, "----IpcCallScreenActivity-----> dataCallBackCallA   mAudioProcess.mPlayQueue size = " + mAudioProcess.mPlayQueue.size());
        if (mAudioProcess.mPlayQueue.size() < 200) {
            mAudioProcess.mPlayQueue.add(data);
        }
    }

    @Override
    public void updateVideoInfo(VideoInfo info) {
        LogUtil.d(TAG, "----IpcCallScreenActivity-----> updateVideoInfo");
        mVideoInfo = info;
    }

    @Override
    public void connCallBack(int type, int status) {
        LogUtil.d(TAG, "----IpcCallScreenActivity-----> connCallBack");
    }

    class ServiceConnectionImpl implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "----IpcCallScreenActivity--ServiceConnectionImpl---> onServiceConnected");
            String serviceClassName = name.getClassName();
            if (serviceClassName.equals(Constants.ACTION_SERVICE_JSONTCP)) {
                mIaidJsonTcpService = IAIDLJsonTcpService.Stub.asInterface(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "----IpcCallScreenActivity--ServiceConnectionImpl---> onServiceDisconnected");
            String serviceClassName = name.getClassName();
            if (serviceClassName.equals(Constants.ACTION_SERVICE_JSONTCP)) {
                mIaidJsonTcpService = null;
            }
        }
    }

    private void sendTakeCallEvent() {
        CallController.getInstance(getApplicationContext()).startCallSession(this, mCallMsgDetailInfo);
    }

    // decode thread, for media codec (hard decode)
    class VideoDecoderThread implements Runnable {
        private final int QUEUEPOLLTMOUT = 1000;// 2s
        private volatile boolean mRunning;
        private boolean mDecInit;
        private HVideoDecoder mDecoder = null;

        public void stop() {
            mRunning = false;
        }

        @Override
        public void run() {
            mRunning = true;
            mDecInit = false;
            VStreamBuffer bufferItem = null;
            while (mRunning) {
                try {
                    bufferItem = mVideoPlayQueue.poll(QUEUEPOLLTMOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, "----IpcCallScreenActivity-----> InterruptedException e " + e.getMessage());
                    continue;
                }
                LogUtil.d(TAG, "----IpcCallScreenActivity-----> bufferItem =  " + bufferItem);
                if (null != bufferItem) {
                    // decode
                    if (null == mDecoder) {
                        if (null != mDecoderTextureListener) {
                            mDecoder = mDecoderTextureListener.getVideoDecoder();
                            if (null == mDecoder) {
                                LogUtil.e(TAG, "----IpcCallScreenActivity----->  decoder is null ???");
                                continue;
                            }
                        } else {
                            LogUtil.e(TAG, "----IpcCallScreenActivity----->  texture is null ???");
                            continue; // decoder还没有准备好？？
                        }
                    }
                    if (!mDecInit) { //等待I帧
                        // need init first, find sps/pps
                        if (mVideoInfo != null && mVideoInfo.mCsdInfo != null) {
                            LogUtil.d(TAG, "----IpcCallScreenActivity----->  init decoder, width=" + mVideoInfo.mWidth + ", height" + mVideoInfo.mHeight);
                            mDecoder.decoder_init(mVideoInfo.mCsdInfo, mVideoInfo.mWidth, mVideoInfo.mHeight);
                            mDecInit = true;
                        } else {
                            // 没有得到sps，无法初始化？？
                            continue;
                        }
                    }
                    if (mLoadingText.getVisibility() == View.VISIBLE && mDecoder.isDecodeSuccess()) {
                        mLoadingText.post(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingText.setVisibility(View.GONE);
                            }
                        });
                    }
                    mDecoder.onFrameProcess(bufferItem.StreamData.array(), bufferItem.StreamLen, bufferItem.TimeSTP);
                    LogUtil.i(TAG, "----IpcCallScreenActivity----->  decode one frame.");
                }
            }
            if (null != mDecoder) {
                mDecoder.closeDecoder();
            }
        }
    }

    boolean mStartCount = true;
    int mCountTime = 0;

    private class CounterThread extends Thread {

        @Override
        public void run() {
            while (mStartCount) {
                try {
                    Thread.sleep(1000);
                    mCountTime += 1;
                    mTvCountTime.post(new Runnable() {
                        @Override
                        public void run() {
                            mTvCountTime.setText(formatTime(mCountTime));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String formatTime(int t) {
        final int minute = t / 60;
        final int second = t % 60;
        StringBuffer buffer = new StringBuffer((minute < 10 ? "0" : "") + minute).append(":").append((second < 10 ? "0" : "") + second);
        return buffer.toString();
    }
}