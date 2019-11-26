package com.honeywell.cube.net.Socket;

import android.content.Context;
import android.os.Handler;

import com.google.gson.*;

import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.db.AppInfo;
import com.honeywell.cube.db.AppInfoFunc;
import com.honeywell.cube.db.MessageManager;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.net.queue.CommandQueueManager;
import com.honeywell.cube.net.queue.ReceiverQueueManager;
import com.honeywell.cube.net.webSocket.cube_websocket.CubeAutoBahnWebsocketClient;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.PreferenceUtil;
import com.honeywell.cube.utils.events.CubeEvents;
import com.honeywell.cube.utils.events.CubeLoginEvent;
import com.honeywell.cube.utils.events.CubeScanEvent;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/7/7. 14:34
 * Email:Shodong.Sun@honeywell.com
 */
public class SocketController {
    private static final String TAG = SocketController.class.getSimpleName();
    private Context mContext = null;
    private SocketClient socketClient = null;
    private SocketReceiverThread receiverThread = null;

    public static boolean isProcessRun = false;
    public static boolean isSendPing = false;
    private Thread connectThread = null;

    public byte[] resultData = null;


    /**
     * 心跳 ping 每隔15秒发送一次心跳包
     */
    private Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            while (isProcessRun) {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String pingMsg = MessageManager.getInstance(mContext).heartPing();
                if (socketClient != null && socketClient.isConnectToCube()) {
                    Loger.print(TAG + "12345", "ssd add ping to socket  pingMsg = " + pingMsg, Thread.currentThread());
                    isSendPing = true;
                    CommandQueueManager.getInstance(mContext).addPingCommandToQueue(pingMsg);
                }
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isSendPing = false;
        }
    };

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            Loger.print(TAG, "ssd start login socket", Thread.currentThread());
            AppInfo info = AppInfoFunc.getGuestUser(mContext);
            if (info == null || info.cube_local_id.length() <= 0 || info.cube_local_password.length() <= 0) {
                Loger.print(TAG, "ssd Cloud 未登陆 无法进行本地登陆", Thread.currentThread());
                //弹出登陆界面
                return;
            }
            if (socketClient == null || !socketClient.isConnectToCube()) {
                socketClient = SocketClient.newInstance();
                //获取Cube网络接口
                String host = info.cube_ip;
                int port = info.cube_port;
                if (port <= 0) port = NetConstant.TCP_IP_PORT;
                Loger.print(TAG, "ssd connect socket host : " + host + "; port : " + port, Thread.currentThread());
                socketClient.cube_ip = host;
                socketClient.cube_port = port;
                socketClient.connectToServer(mContext);
                if (receiverThread == null) {
                    receiverThread = new SocketReceiverThread(mContext, socketClient);
                    receiverThread.startRun();
                }
                CommandQueueManager.getInstance(mContext).startRun();
                LoginController.getInstance(mContext).setLoginType(LoginController.LOGIN_TYPE_CONNECT_WIFI);
                loginCube();
            } else {
                loginCube();
            }
        }
    };
    /**
     * 单例模式
     */
    private static SocketController socketController = null;

    private SocketController() {
    }

    public static SocketController newInstance(Context context) {
        if (socketController == null) {
            socketController = new SocketController();
            socketController.mContext = context;
        }
        return socketController;
    }

    /**
     * 登陆 SOCKET 主要入口
     */
    public void loginToTCPSocket() {
        if (!isProcessRun) {
            connectThread = new Thread(connectRunnable);
            connectThread.start();
            isProcessRun = true;
        } else {
            loginCube();
        }
        if (!ReceiverQueueManager.getInstance(mContext).isRunFlag()) {
            ReceiverQueueManager.getInstance(mContext).startRun();
        }
    }

    public void checkIfLogin() {
        if (!isProcessRun) {
            connectThread = new Thread(connectRunnable);
            connectThread.start();
            isProcessRun = true;
        }
        if (!ReceiverQueueManager.getInstance(mContext).isRunFlag()) {
            ReceiverQueueManager.getInstance(mContext).startRun();
        }
    }

    public void loginTCPSocketSuccess() {
        LoginController.getInstance(mContext).setLoginType(LoginController.LOGIN_TYPE_CONNECT_WIFI);
        //清除登陆纪录
        PreferenceUtil.clearUserLoginInfo(mContext);
        CubeAutoBahnWebsocketClient.getInstance(mContext).stopConnection("本地登陆");
        setRequestAfterLogin();
        //发送Event本地登陆成功
//        EventBus.getDefault().post(new CubeLoginEvent(CubeEvents.CubeLoginEventType.LOGIN_SOCKET, true, "本地登陆成功"));
//        EventBus.getDefault().post(new CubeScanEvent(CubeEvents.CubeScanEventType.SCAN_CUBE_EVENT, true, "本地登陆成功"));
    }


    public void stopSocketConnect(String reason) {
        Loger.print(TAG, "ssd stop socket loggin", Thread.currentThread());
        if (socketClient != null && socketClient.isConnectToCube()) {
            socketClient.disconnectSocket(reason);
            socketClient = null;
        }
        if (receiverThread != null) {
            receiverThread.stopRun();
            receiverThread.stopRun();
            receiverThread = null;
        }
        ReceiverQueueManager.getInstance(mContext).stopRun();
        isProcessRun = false;
        isSendPing = false;
    }

    /**
     * Socket 发送数据
     *
     * @param cmd
     */
    public boolean sendSocketCommand(String cmd) {
        //判断登陆
        checkIfLogin();

        //组装数据
        byte[] header = NetConstant.TCP_HEAD.getBytes();
        byte[] body = cmd.getBytes();
        int length = header.length + 1 + 4 + body.length;
        byte[] msg = new byte[length];
        int i = 0;
        for (; i < header.length; i++) {
            msg[i] = header[i];
        }
        msg[i] = transferOneByte(0);
        i++;
        int j = 0;
        byte[] bodyLength = transferDecimalToByte(body.length);
        for (; i < header.length + 1 + 4; i++) {
            msg[i] = bodyLength[j++];
        }
        j = 0;
        for (; i < header.length + 1 + 4 + body.length; i++) {
            msg[i] = body[j++];
        }
        return socketClient.postRequestCommand(mContext, "ssd", msg);
    }


    /**
     * 获取Config
     */
    public void loginCube() {
        String message = MessageManager.getInstance(mContext).loginCubeFromLocal(mContext);
        CommandQueueManager.getInstance(mContext).addNormalCommandToQueue(message);
    }

    public synchronized void setRequestAfterLogin() {

        String message = MessageManager.getInstance(mContext).checkCubeUpdateFromLocal(mContext);
        CommandQueueManager.getInstance(mContext).addNormalCommandToQueue(message);


        //Socket 启动心跳线程
        if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
            if (!isSendPing) {
                new Thread(pingRunnable).start();
            }
        }
    }


    /**
     * 处理返回的数据
     *
     * @param data
     */
    public boolean dealSocketReceiveData(byte[] data) {
        byte[] header = NetConstant.TCP_HEAD.getBytes();
        boolean ifCheckedHead = true;
        if (data == null) return ifCheckedHead;
        for (int i = 0; i < data.length - header.length; i++) {
            if (data.length < i + header.length) break;
            if (checkByteisEqual(subDataWithInfo(data, i, header.length), header)) {
                ifCheckedHead = true;
                if (data.length < i + header.length + data.length - i - header.length) break;
                resultData = subDataWithInfo(data, i + header.length, data.length - i - header.length);
                break;
            }
        }

        //是否读取完
        if (resultData == null || resultData.length < 5) return ifCheckedHead;
        byte[] lengthData = subDataWithInfo(resultData, 1, 4);
        long length = lBytesToIntNoChange(lengthData);

        //处理数据包
        if (resultData.length - 5 >= length) {
            //带上是否加密
            byte[] packageData = subDataWithInfo(resultData, 4, length + 1);
            //处理一个完整的包
            dealWithPackageData(packageData);
            //沾包处理
            if (resultData.length > length + 5) {
                resultData = subDataWithInfo(resultData, (int) length + 5, resultData.length - length - 5);
                //继续处理
                dealSocketReceiveData(resultData);
            } else {
                resultData = null;
            }
        }
        return ifCheckedHead;
    }

    //处理包内容
    private void dealWithPackageData(byte[] data) {
        if (data == null || data.length < 2) return;
        byte[] body = subDataWithInfo(data, 1, data.length - 1);
        try {
            String bodyStr = new String(body, "UTF-8");
//            bodyStr = bodyStr.replaceAll("[\\t\\n\\r]", " ");//将 回车 进行替换
            Loger.print(TAG, "ssd **********onSocket recever ******" + bodyStr, Thread.currentThread());
            if (bodyStr != null && !"".equalsIgnoreCase(bodyStr)) {
                ReceiverQueueManager.getInstance(mContext).addRecevierToQueue(bodyStr);
//                JSONObject object = new JSONObject(bodyStr);
//                ResponderController.newInstance(mContext).dealNetData(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /******************
     * private method
     *******************/

    //Bytes 转Int
    private long lBytesToIntNoChange(byte[] data) {
        return (data[3] & 0xff) + ((data[2] & 0xff) << 8) + ((data[1] & 0xff) << 16) + ((data[0] & 0xff) << 24);
    }

    //截取部分数据
    private byte[] subDataWithInfo(byte[] org, int start, long length) {
        if (length <= 0 || start < 0) return null;
        byte[] result = new byte[(int) length];
        for (int i = start; i < start + length; i++)
            result[i - start] = org[i];
        return result;
    }

    //检查两个byte数组是否想等
    private boolean checkByteisEqual(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        boolean isEqual = true;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                isEqual = false;
                break;
            }
        }
        return isEqual;
    }

    //数据转换
    private byte transferOneByte(int source) {
        if (source == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * 将int 十进制转换为byte数据
     *
     * @param source
     * @return
     */
    private byte[] transferDecimalToByte(int source) {
        byte[] returnValue = new byte[4];
        returnValue[0] = (byte) ((source >> 24) & 0xFF);
        returnValue[1] = (byte) ((source >> 16) & 0xFF);
        returnValue[2] = (byte) ((source >> 8) & 0xFF);
        returnValue[3] = (byte) ((source) & 0xFF);
        return returnValue;
    }
}
