package com.honeywell.cube.ipc;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.honeywell.cube.IAIDLJsonTcpService;
import com.honeywell.lib.utils.LogUtil;

public class JsonTcpService extends Service implements SocketCloseCallback {

    protected static final String TAG = JsonTcpService.class.getSimpleName() + " IPCTAG";
    private String mIpAddr = "192.168.31.189";// "127.0.0.1";//"192.168.1.59";//"192.168.31.243";//"192.168.31.166";
    private int mPort = 554;
    private CubeNetConnect mNetConnect = null;
    private int mConnectCount = 0;
    private static final int MAX_RETRY_COUNT = 5;
    private static final int DELAY_TIME = 5 * 1000;
    private static final int RESULT_OK = 0;
    private static final int RESULT_ERROR = -1;

    @Override
    public IBinder onBind(Intent arg0) {
        mIpAddr = arg0.getStringExtra(com.honeywell.cube.utils.Constants.IPC_IP_ADDR);
        LogUtil.e(TAG, "-------JsonTcpService -------->     onBind  mIpAddr = " + mIpAddr);
        return mBinder;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "-------JsonTcpService -------->     JsonTcpService onDestroy");
        mConnectCount = 0;
        if (null != mNetConnect) {
            mNetConnect.closeSocket(CubeNetConnect.SOCKETCLOSEREASON_SERVICEDESTORY);
            mNetConnect = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "-------JsonTcpService -------->     JsonTcpService onUnbind");
        return super.onUnbind(intent);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            new Thread() {
                @Override
                public void run() {
                    mNetConnect = startConnetCube();
                    if (null == mNetConnect && mConnectCount < MAX_RETRY_COUNT) {
                        mHandler.postDelayed(mRunnable, DELAY_TIME);
                        mConnectCount++;
                    } else {
                        mConnectCount = 0;
                    }
                }
            }.start();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // start connect to Cube
        LogUtil.e(TAG, " -------JsonTcpService -------->    onCreate ");
        mHandler.postDelayed(mRunnable, DELAY_TIME);
        mConnectCount++;
    }

    private CubeNetConnect startConnetCube() {
        LogUtil.e(TAG, "-------JsonTcpService -------->     startConnetCube()  mIpAddr = " + mIpAddr);
        CubeNetConnect cubeNetConnect = null;
        // judge parameter and if this cube has been connected
        if (CommonUtils.ISNULL(mIpAddr) || mNetConnect != null) {
            LogUtil.e(TAG, "-------JsonTcpService -------->     SocketConnect error pam or has connected! ");
            return cubeNetConnect;
//            return mNetConnect;
        }

        int port = (mPort > 0 ? mPort : Constants.DEFAULT_PORT);
        // connect to device
        try {
            Socket socket = new Socket(mIpAddr, port);
            LogUtil.d(TAG, "-------JsonTcpService -------->    startConnetCube  new Socket(mIpAddr, port); mIpAddr = " + mIpAddr + " , port = " + port);
            cubeNetConnect = new CubeNetConnect(socket, mIpAddr, port, getApplicationContext());
            LogUtil.d(TAG, "-------JsonTcpService -------->    startConnetCube  new CubeNetConnect(socket, mIpAddr, port, getApplicationContext())");
        } catch (UnknownHostException e) {
            LogUtil.e(TAG, "-------JsonTcpService -------->     SocketConnect() UnknownHostException!" + e.getMessage());
            return cubeNetConnect;
        } catch (IOException e1) {
            LogUtil.e(TAG, "-------JsonTcpService -------->     SocketConnect() IOException!" + e1.getMessage());
            return cubeNetConnect;
        } catch (Exception e) {
            LogUtil.e(TAG, "-------JsonTcpService -------->     SocketConnect() Exception " + e.getMessage());
        }
        LogUtil.d(TAG, "-------JsonTcpService -------->     cubeNetConnect = " + cubeNetConnect);
        if (null != cubeNetConnect) {
            cubeNetConnect.mCallback = JsonTcpService.this;
            // update cube online status
            // start read and write thread for requese and response
            cubeNetConnect.startConnect();
            LogUtil.e(TAG, mIpAddr + "  has been conncet!!!");
        }
        return cubeNetConnect;
    }

    private IAIDLJsonTcpService.Stub mBinder = new IAIDLJsonTcpService.Stub() {
        @Override
        public void JSONPacketSend(String msgId, byte[] jsonData, int len) throws RemoteException {
            insertToSocketProcessQueue(msgId, jsonData, len);
        }
    };

    public int insertToSocketProcessQueue(String msgId, byte[] jsonData, int len) {
        LogUtil.e(TAG, "-------JsonTcpService -------->     JIAIDLJsonTcpService.Stub SONPacketSend  insertToSocketProcessQueue msgId = " + msgId + " , jsonData = " + jsonData + " , len = " + len + " , mNetConnect.mSocket.isConnected() = " + mNetConnect.mSocket.isConnected() +
                " , mNetConnect = " + mNetConnect);
        int result = RESULT_ERROR;
        if ((null == jsonData) || (len <= 0) || null == mNetConnect || !mNetConnect.mSocket.isConnected()) {
            // 以后估计要考虑这块
            LogUtil.e(TAG, "-------JsonTcpService -------->     insertToSocketProcessQueue() parameter error!");
            return result;
        }

        byte[] data = jsonData;
        String jsonString = new String(jsonData);
        // 如果没有头，则需要加下协议头
        boolean isRequest = false;
        if (!jsonString.contains(NetUtil.FIXSTRING_MOBILE)) {
            String log = "-------JsonTcpService -------->     insertToSocketProcessQueue() no fixstring,so add!!";
            LogUtil.e(TAG, log);
            data = NetUtil.appendSendingBytes(jsonData, jsonData.length);
            try {
                isRequest = new JSONObject(jsonString).getString(Constants.JSON_COMMAND_ACTION).equals(Constants.JSON_COMMAND_ACTION_REQUEST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            isRequest = jsonString.contentEquals(Constants.JSON_COMMAND_ACTION_REQUEST);
        }
        if (mNetConnect.mCommandQueue.offer(new ControlCommandInfo(msgId, data, isRequest))) {
            result = RESULT_OK;
        } else {
            LogUtil.e(TAG, "-------JsonTcpService -------->     insertToSocketProcessQueue() insert request fail for no capacity!");
        }
        return result;
    }

    @Override
    public void removeCubeNet(String ipAddr) {
        LogUtil.d(TAG, "-------JsonTcpService -------->     removeCubeNet() ipAddr = " + ipAddr);
        if (!CommonUtils.ISNULL(ipAddr) && null != mNetConnect) {
            mNetConnect.mInit = false;
            mNetConnect = null;
            LogUtil.d(TAG, "-------JsonTcpService -------->     removeCubeNet() net start retry");
            mHandler.postDelayed(mRunnable, DELAY_TIME);
            mConnectCount = 1;
        }
    }
}
