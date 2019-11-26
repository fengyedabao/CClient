package com.honeywell.cube.activities;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonCache;
import com.honeywell.cube.controllers.DeviceControllers.IPCameraController;
import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.IpcStreamInfo;
import com.honeywell.cube.ipc.Constants;
import com.honeywell.cube.ipc.HVideoDecoder;
import com.honeywell.cube.ipc.ISimplePlayer;
import com.honeywell.cube.ipc.P2PCallBack;
import com.honeywell.cube.ipc.TextureViewListener;
import com.honeywell.cube.ipc.Util;
import com.honeywell.cube.ipc.VStreamBuffer;
import com.honeywell.cube.ipc.VideoInfo;
import com.honeywell.cube.IAIDLJsonTcpService;
import com.honeywell.cube.ipc.nativeapi.P2PConn;
import com.honeywell.cube.utils.events.CubeDeviceEvent;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.lib.utils.LogUtil;
import com.honeywell.lib.utils.ToastUtil;

public class IpcPlayerActivity extends CubeTitleBarActivity implements ISimplePlayer, P2PCallBack {
    private static final String TAG = IpcPlayerActivity.class.getSimpleName() + " IPCTAG";
    private static final int MAX_QUEUE_SIZE = 20;
    private TextView mLoadingText;
    // p2p
    private String mP2PUUID = null;
    // multimedia
    private VideoInfo mIpcVideoInfo = null;
    private TextureView mDecoderView;
    private TextureViewListener mDecoderTextureListener;
    private VideoDecoderThread mDecThread;
    private BlockingQueue<VStreamBuffer> mVideoPlayQueue = new LinkedBlockingQueue<VStreamBuffer>(MAX_QUEUE_SIZE);
    // tcp jason service
    private ServiceConnection mJsonTcpServiceConnect = new ServiceConnectionImpl();
    public IAIDLJsonTcpService mIaidJsonTcpService = null;

    private IpcStreamInfo mIpcStreamInfo = null;

    @Override
    protected int getContent() {
        return R.layout.activity_ipc_palyer;
    }

    @Override
    protected void initIntentValue() {
        super.initIntentValue();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
        mIpcStreamInfo = getIntent().getParcelableExtra(com.honeywell.cube.utils.Constants.IPC_STREAM_INFO);
    }

    @Override
    protected void initTitle(TextView title) {
        super.initTitle(title);
        title.setTextColor(Color.WHITE);
    }

    @Override
    protected void initLeftIcon(ImageView left) {
        super.initLeftIcon(left);
//        left.setImageResource();
    }

    @Override
    protected void initView() {
        super.initView();
        // init open gl
        if (!Util.detectOpenGLES20(this)) {
            LogUtil.e(TAG, "--------IpcPlayerActivity---->    Do not support open gl", true);
        }
        mDecoderView = (TextureView) findViewById(R.id.ipc_video_texture); // mediacodec
        mLoadingText = (TextView) findViewById(R.id.tv_loading_tip);

        mP2PUUID = CommonCache.sP2PUUID;
        LogUtil.e(TAG, "--------IpcPlayerActivity---->  mP2PUUID =  " + mP2PUUID);
        P2PConn.setIpcCallBack(this); // nalu 回调

        // multimedia
        mDecoderTextureListener = new TextureViewListener();
        mDecoderView.setSurfaceTextureListener(mDecoderTextureListener);
        mDecThread = new VideoDecoderThread();
        new Thread(mDecThread).start();
        // start json service/bind
        startAndBindService(Constants.ACTION_SERVICE_JSONTCP, mJsonTcpServiceConnect);

    }


    @Override
    protected void onDestroy() {
        sendJsonStopMonitor();
        mDecThread.stop();
//        P2PConn.unInitConnLib();
        P2PConn.setRender(null);
        LogUtil.e(TAG, "--------IpcPlayerActivity----> unbindService mJsonTcpServiceConnect start");
        unbindService(mJsonTcpServiceConnect);
        LogUtil.e(TAG, "--------IpcPlayerActivity----> unbindService mJsonTcpServiceConnect end");
        super.onDestroy();
    }


    @Override
    public void onPlayStart() {
        LogUtil.e(TAG, "--------IpcPlayerActivity------ISimplePlayer------>    onPlayStart", true);
    }

    @Override
    public void onReceiveState(int state) {
        LogUtil.e(TAG, "--------IpcPlayerActivity------ISimplePlayer------>    onReceiveState", true);
    }

    public void onEventMainThread(CubeEvents event) {
        if (event instanceof CubeDeviceEvent) {
            CubeDeviceEvent deviceEvent = (CubeDeviceEvent) event;
            if (deviceEvent.getType() == CubeEvents.CubeDeviceEventType.DEVICE_IPC_UPDATE) {
                if (!deviceEvent.getSuccess()) {
                    finish();
                }
            }
        }
    }

    // start service and bind
    private void startAndBindService(String serviceAction, ServiceConnection connection) {
        LogUtil.e(TAG, "--------IpcPlayerActivity---->   startAndBindService ", true);
        if (null == serviceAction || null == connection) {
            return;
        }
        Intent intent = new Intent(serviceAction);
        intent.setPackage(getPackageName());
        intent.putExtra(com.honeywell.cube.utils.Constants.IPC_IP_ADDR, mIpcStreamInfo.mIPAddr);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    // send json monitor cmd
    private void sendJsonStartMonitor() {
//        if (null == info && null == mIpcStreamInfo) {
        if (null == mIpcStreamInfo) {
            LogUtil.e(TAG, "--------IpcPlayerActivity---->    IPC info is null!", true);
            return;
        }
        IPCameraController.updateIPCWithInfo(this, mIpcStreamInfo, mP2PUUID, true);
    }

    private void sendJsonStopMonitor() {
        if (null == mIpcStreamInfo) {
            LogUtil.e(TAG, "--------IpcPlayerActivity---->    IPC info is null!", true);
            return;
        }
        IPCameraController.updateIPCWithInfo(this, mIpcStreamInfo, mP2PUUID, false);
    }

    @Override
    public void dataCallBack(byte[] data, int dataLen, String uuid) {
        LogUtil.e(TAG, "--------IpcPlayerActivity----- P2PCallBack  -------------->     dataCallBack");
        // IPC call back
        VStreamBuffer stream = new VStreamBuffer();
        stream.StreamData = ByteBuffer.wrap(data);
        stream.StreamLen = dataLen;
        stream.UUID = uuid;
        mVideoPlayQueue.offer(stream);
    }

    @Override
    public void dataCallBackCallV(byte[] data, int dataLen, String uuid) {
        LogUtil.e(TAG, "--------IpcPlayerActivity------ P2PCallBack  -------------->     dataCallBackCallV", true);
    }

    @Override
    public void dataCallBackCallA(byte[] data, int dataLen, String uuid) {
        LogUtil.e(TAG, "--------IpcPlayerActivity------- P2PCallBack  -------------->     dataCallBackCallA", true);
    }

    @Override
    public void updateVideoInfo(VideoInfo info) {
        LogUtil.e(TAG, "--------IpcPlayerActivity------- P2PCallBack  -------------->     updateVideoInfo", true);
        mIpcVideoInfo = info;
    }

    @Override
    public void connCallBack(int type, int status) {
        LogUtil.e(TAG, "--------IpcPlayerActivity------- P2PCallBack  -------------->     connCallBack", true);
    }


    // internal class
    class ServiceConnectionImpl implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.e(TAG, " --------IpcPlayerActivity-------   ServiceConnectionImpl  ------------>   onServiceConnected", true);
            String serviceClassName = name.getClassName();
            if (serviceClassName.equals(Constants.ACTION_SERVICE_JSONTCP)) {
                mIaidJsonTcpService = IAIDLJsonTcpService.Stub.asInterface(service);
                sendJsonStartMonitor();
                LogUtil.d(TAG, "--------IpcPlayerActivity------- ServiceConnectionImpl  ------------> sendJsonStartMonitor", true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e(TAG, " --------IpcPlayerActivity-------    ServiceConnectionImpl  ------------>   onServiceDisconnected", true);
            String serviceClassName = name.getClassName();
            if (serviceClassName.equals(Constants.ACTION_SERVICE_JSONTCP)) {
                mIaidJsonTcpService = null;
            }
        }
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
                    LogUtil.e(TAG, " --------IpcPlayerActivity-------  VideoDecoderThread  --------->   InterruptedException e =  " + e.getMessage(), true);
                    continue;
                }
                LogUtil.d(TAG, " --------IpcPlayerActivity-------  VideoDecoderThread  --------->    bufferItem " + bufferItem, true);
                if (null != bufferItem) {
                    LogUtil.e("alinmi23", "--- null != bufferItem");
                    // decode
                    if (null == mDecoder) {
                        if (null != mDecoderTextureListener) {
                            mDecoder = mDecoderTextureListener.getVideoDecoder();
                        } else {
                            continue; // decoder还没有准备好？？
                        }
                    }
                    if (!mDecInit) { //等待I帧
                        // need init first, find sps/pps
                        if (mIpcVideoInfo != null && mIpcVideoInfo.mCsdInfo != null) {
                            LogUtil.e(TAG, " --------IpcPlayerActivity-------  VideoDecoderThread  --------->   init decoder, width=" + mIpcVideoInfo.mWidth + ", height" + mIpcVideoInfo.mHeight, true);
                            LogUtil.e("alinmi23", "---  mDecoder.decoder_init(mIpcVideoInfo.mCsdInfo, mIpcVideoInfo.mWidth, mIpcVideoInfo.mHeight)", true);
                            mDecoder.decoder_init(mIpcVideoInfo.mCsdInfo, mIpcVideoInfo.mWidth, mIpcVideoInfo.mHeight);
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
                }
            }
            if (null != mDecoder) {
                mDecoder.closeDecoder();
            }
        }
    }
}