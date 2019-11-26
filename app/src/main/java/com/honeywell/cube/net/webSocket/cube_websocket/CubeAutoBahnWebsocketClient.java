package com.honeywell.cube.net.webSocket.cube_websocket;


import android.content.Context;
import android.os.Looper;

import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.R;
import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.Socket.SocketController;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.net.queue.ReceiverQueueManager;
import com.honeywell.cube.net.webSocket.autobaln_websocket.WebSocket;
import com.honeywell.cube.net.webSocket.autobaln_websocket.WebSocketConnection;
import com.honeywell.cube.net.webSocket.autobaln_websocket.WebSocketException;
import com.honeywell.cube.net.webSocket.autobaln_websocket.WebSocketOptions;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/4/30. 13:57
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeAutoBahnWebsocketClient {
    private static final String TAG = "CubeAutoBahnWebsocketClient";
    private static WebSocketConnection connection = new WebSocketConnection();
    private static Context mContext;
    private AppInfo curInfo;
    private Thread connectionThread = null;
    private static volatile boolean isProcessRun = false;
    private static volatile boolean isReconnectToServer = false;
    private int reconnectCount = -1;//重连次数 超过10就可以不连了


    /**
     * 回调接口
     */
    private WebSocket.ConnectionHandler handler = new WebSocket.ConnectionHandler() {
        @Override
        public void onOpen() {
            Loger.print(TAG, "ssd****************onOpen", Thread.currentThread());
            LoginController.getInstance(mContext).checkCubeOnlineStatus();
        }


        @Override
        public void onClose(WebSocketCloseNotification code, String reason) {
            Loger.print(TAG, "ssd****************onClose with code :" + code + " reason :" + reason, Thread.currentThread());
            CubeAutoBahnWebsocketClient.solvedOnClose(CubeAutoBahnWebsocketClient.getContext(), code, reason);

//            stopConnection(reason);
//            Loger.print(TAG, "ssd websocket is restart connect", Thread.currentThread());
//            reconnectToserver();
        }

        @Override
        public void onTextMessage(String payload) {
            Loger.print(TAG, "ssd****************onTextMessage message:" + payload, Thread.currentThread());
            final String str = payload;
            //使用队列操作
            ReceiverQueueManager.getInstance(mContext).addRecevierToQueue(str);

            //使用线程操作
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    ResponderController.newInstance(mContext).dealWithWebSocketResponce(str);
//                }
//            }).start();
        }

        @Override
        public void onRawTextMessage(byte[] payload) {
            Loger.print(TAG, "11111111-ssd****************onRawTextMessage message :" + (new String(payload)), Thread.currentThread());
            String receiveString = new String(payload);
            if (receiveString != null && receiveString.length() > 0) {
                //使用队列操作
                ReceiverQueueManager.getInstance(mContext).addRecevierToQueue(receiveString);
            }
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            Loger.print(TAG, "ssd****************onBinaryMessage message :" + (new String(payload)), Thread.currentThread());
        }
    };

    private CubeAutoBahnWebsocketClient(Context context) {
        this.mContext = context;
    }

    private static CubeAutoBahnWebsocketClient cubeAutoBahnWebsocketClient = null;

    public static CubeAutoBahnWebsocketClient getInstance(Context context) {
        if (cubeAutoBahnWebsocketClient == null) {
            cubeAutoBahnWebsocketClient = new CubeAutoBahnWebsocketClient(context);
        }
        return cubeAutoBahnWebsocketClient;
    }

    public static WebSocketConnection getConnection() {
        return connection;
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 连接服务器主体
     *
     * @param info
     */
    private void connectServer(AppInfo info) {
        if (connection == null) {
            connection = new WebSocketConnection();
        }
        Loger.print(TAG, "ssd websocket cookie : " + info.all_header_fields_cookie, Thread.currentThread());
        ArrayList<BasicNameValuePair> list = new ArrayList<>();
        BasicNameValuePair pair01 = new BasicNameValuePair(CommonData.JSON_WEBSOCKET_USER_ORIGIN, NetConstant.URI_WEBSOCKET_ORIGIN);
        BasicNameValuePair pair02 = new BasicNameValuePair(CommonData.JSON_WEBSOCKET_USER_COOKIE, info.all_header_fields_cookie);
        list.add(pair01);
        list.add(pair02);
        try {
            if (connection.isConnected()) {
                Loger.print(TAG, "ssd WebSocket error has connected", Thread.currentThread());
                getConfigInfo();
                return;
            }
            /**
             * 设置有效的最大负载 否则会报错 code 4
             */
//            WampOptions options = new WampOptions();
//            options.setReceiveTextMessagesRaw(false);
//            options.setMaxMessagePayloadSize(512 * 1024);
//            options.setMaxFramePayloadSize(512 * 1024);
//            options.setTcpNoDelay(true);
            Loger.print(TAG, "ssd websocket url : " + NetConstant.URI_WEBSOCKET, Thread.currentThread());
            WebSocketOptions options = new WebSocketOptions();
            connection.connect(NetConstant.URI_WEBSOCKET, null, handler, options, list);
//            connection.connect(NetConstant.URI_WEBSOCKET, new String[]{"wamp"}, handler, options, list);
            //启动命令发送线程
            CommandQueueManager.getInstance(mContext).startRun();
            if (!ReceiverQueueManager.getInstance(mContext).isRunFlag()) {
                ReceiverQueueManager.getInstance(mContext).startRun();
            }
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送获取配置信息
     */
    public void getConfigInfo() {
        //获取所绑定的CUBE配置信息
        String msg = MessageManager.getInstance(mContext).getCubeInfo(mContext);
        CommandQueueManager manager = CommandQueueManager.getInstance(mContext);
        manager.timeoutMonitor = null;
        manager.addNormalCommandToQueue(msg);
    }


    /**
     * 通过EventBus发送WebSocket关闭原因
     *
     * @param context
     * @param code
     * @param reason
     */
    private static void solvedOnClose(Context context, WebSocket.ConnectionHandler.WebSocketCloseNotification code, String reason) {
        String errorStr = "";
        if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.NORMAL) {
            return;
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.CANNOT_CONNECT) {
            //无法链接
            errorStr = context.getString(R.string.websocket_close_cannot_connect);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.CONNECTION_LOST) {
            //链接丢失
            errorStr = context.getString(R.string.websocket_close_connect_lost);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.PROTOCOL_ERROR) {
            //违反协议
            errorStr = context.getString(R.string.websocket_close_protocol_error);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.INTERNAL_ERROR) {
            //内部错误
            errorStr = context.getString(R.string.websocket_close_internal_error);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.SERVER_ERROR) {
            //服务器错误
            errorStr = context.getString(R.string.websocket_close_server_error);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.RECONNECT) {
            //重连
            errorStr = context.getString(R.string.websocket_close_reconnect);
        } else {
            //未知错误
            errorStr = context.getString(R.string.error_unknown);
        }
        Loger.print(TAG, "ssd websocket断了，并发出了Event事件", Thread.currentThread());
        if (isProcessRun){
            LoginController.getInstance(context).logout(mContext);
            isProcessRun = false;
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.CONNECTING_LOST, false, mContext.getString(R.string.error_time_out) + " : " + errorStr));
        }
    }

    /**
     * 分线程启动WebSocket
     *
     * @param info
     */
    public synchronized void startConnectServer(AppInfo info) {
        if (info != null) {
            this.curInfo = info;
        } else {
            Loger.print(TAG, "connect server user can not be null", Thread.currentThread());
        }
        isProcessRun = true;
        if (connection == null) {
            connection = new WebSocketConnection();
        }
        connectionThread = new Thread(connectionRunnable);
        connectionThread.start();
        if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            SocketController.newInstance(mContext).stopSocketConnect("Login webSocket");
        }
    }

    /**
     * 关闭当前连接
     *
     * @param reason
     */
    public void stopConnection(String reason) {
        isProcessRun = false;
//        ReceiverQueueManager.getInstance(mContext).stopRun();
        if (connectionThread != null && connectionThread.isAlive()) {
            connectionThread.interrupt();
            connectionThread = null;
        }
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
        connection = null;
    }

    /**
     * 重连服务器
     */
    private synchronized void reconnectToServer() {
        isReconnectToServer = true;//重连
        isProcessRun = true;
        connectionThread = new Thread(reConnectionRunnable);
        connectionThread.start();
    }

    private Runnable reConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            while (isReconnectToServer) {
                if (isProcessRun) {
                    Loger.print(TAG, "Web Socket reConnect to server run() executed + current connect countr : " + reconnectCount, Thread.currentThread());
                    if (reconnectCount > 10) {
                        reconnectCount = -1;
                        isReconnectToServer = false;
                    } else {
                        reconnectCount++;
                        connectServer(curInfo);
                    }
                }
            }
        }
    };

    private Runnable connectionRunnable = new Runnable() {
        @Override
        public void run() {
            if (isProcessRun) {
                Loger.print(TAG, "Web Socket Connect to server run() executed", Thread.currentThread());
                if (!connection.isConnected()) {
                    connectServer(curInfo);
                }
            }
        }
    };
}
