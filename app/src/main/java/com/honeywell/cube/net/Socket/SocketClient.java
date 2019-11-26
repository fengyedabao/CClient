package com.honeywell.cube.net.Socket;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.common.utils.CommonUtils;
import com.honeywell.cube.net.service.CubeAppService;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import de.greenrobot.event.EventBus;

/**
 * Created by h157925 on 4/7/2016.15:23
 * Email:Shoudong.Sun@Honeywell.com
 * to do local communicatin
 */
public class SocketClient {
    private static final String TAG = "SocketClient";
    private static final int TIME_OUT = 15000;
    private static final int CONNECTION_TIME_OUT = 8000;

    public String cube_ip = "";
    public int cube_port = 500;


    private Socket socket;
    private BufferedInputStream bufferedInputStream = null;
    private BufferedOutputStream bufferedOutputStream = null;

    private CubeAppService appService;

    private static SocketClient instance = new SocketClient();

    public static SocketClient newInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    //判断是否连接
    public synchronized boolean isConnectToCube() {
        if (socket == null) {
            return false;
        }
        if (socket.isConnected()) {
            return true;
        }
        return false;
    }

    //建立Socket连接
    public synchronized boolean connectToServer(Context context) {
        boolean connectResult = false;

        if (socket == null || !socket.isConnected()) {
            socket = null;
            if (cube_ip == null || "".equalsIgnoreCase(cube_ip)) {
                Loger.print(TAG, "ssd socket client host is null", Thread.currentThread());
                return false;
            }
            InetSocketAddress address = new InetSocketAddress(cube_ip, cube_port);
            try {
                socket = new Socket();
                socket.setSoTimeout(TIME_OUT);
                socket.connect(address, CONNECTION_TIME_OUT);
                connectResult = true;
                //设置登陆方式
                LoginController.getInstance(context).setLoginType(LoginController.LOGIN_TYPE_CONNECT_WIFI);
            } catch (Exception e) {
                Loger.print(TAG, "ssd local socket connect to Cube exception: " + e.toString(), Thread.currentThread());
                EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.TIME_OUT, true, ""));
                socket = null;
                connectResult = false;
            }
        }
        return connectResult;
    }

    //关闭 Socket
    public synchronized void disconnectSocket(String reason) {
        Loger.print(TAG, "ssd disconnectSocket with reason : " + reason, Thread.currentThread());
        try {
            if (null != bufferedInputStream) {
                bufferedInputStream.close();
            }
        } catch (IOException e) {
            Loger.print(TAG, "Error ssd disconnect Socket --close bufferedInputStream " + reason + " exception:" + e.toString(), Thread.currentThread());
            e.printStackTrace();
        } finally {
            bufferedInputStream = null;
        }

        try {
            if (null != bufferedOutputStream) {
                bufferedOutputStream.close();
            }
        } catch (IOException e) {
            Loger.print(TAG, "Error ssd disconnect Socket --close bufferedOutputStream " + reason + " exception:" + e.toString(), Thread.currentThread());
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.TIME_OUT, true, ""));
            e.printStackTrace();
        } finally {
            bufferedOutputStream = null;
        }

        try {
            if (null != socket) {
                socket.close();
            }
        } catch (IOException e) {
            Loger.print(TAG, "Error ssd disconnect Socket --close close socket " + reason + " exception:" + e.toString(), Thread.currentThread());
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.TIME_OUT, true, ""));
            e.printStackTrace();
        } finally {
            socket = null;
        }
    }

    //获取输入口
    public synchronized BufferedInputStream getBufferedInputStream() {
        if (!socket.isConnected()) {
            Loger.print(TAG, "Error ssd getBufferedInputStream  socket disconnect", Thread.currentThread());
        }
        bufferedInputStream = null;
        try {
            if (socket == null) {
                Loger.print(TAG, "ssd getBufferedInputStream socket is null", Thread.currentThread());
            } else {
                bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            }
        } catch (IOException e) {
            Loger.print(TAG, "Error ssd getBufferedInputStream  Exception: " + e.toString(), Thread.currentThread());
            e.printStackTrace();
        }
        return bufferedInputStream;
    }

    //获取输出口
    public synchronized BufferedOutputStream getBufferedOutputStream() {
        if (socket != null && !socket.isConnected()) {
            Loger.print(TAG, "Error ssd getBufferedOutputStream  socket disconnect", Thread.currentThread());
        }
        bufferedOutputStream = null;
        try {
            if (socket == null) {
                Loger.print(TAG, "ssd getBufferedOutputStream socket is null", Thread.currentThread());
            } else {
                bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            }
        } catch (IOException e) {
            Loger.print(TAG, "Error ssd BufferedOutputStream  Exception : " + e.toString(), Thread.currentThread());
            e.printStackTrace();
        }
        return bufferedOutputStream;
    }

    //CubeAppService setter and getter
    public void setAppService(CubeAppService service) {
        this.appService = service;
    }

    public CubeAppService getAppService(CubeAppService service) {
        return this.appService;
    }

    //通过Socket 发送请求, 发送String
    public synchronized boolean postRequestCommand(String reason, String strCommand) {
        if (CommonUtils.ISNULL(strCommand)) {
            Loger.print(TAG, "Error , the command string is null", Thread.currentThread());
            return false;
        }
        try {
            BufferedOutputStream out = getBufferedOutputStream();
            out.write(strCommand.getBytes());
            out.flush();
            Loger.print(TAG, "ssd postRequestCommand with reason" + reason, Thread.currentThread());
            return true;
        } catch (IOException e) {
            //发送广播,通知发送失败
            Loger.print(TAG, "Error , ssd postRequestCommand with reason" + reason + " failed with exception :" + e.toString(), Thread.currentThread());
            return false;
        }
    }

    //通过Socket 发送请求 ,发送 bytes
    public synchronized boolean postRequestCommand(Context context, String reason, byte[] bytes) {
        if (bytes.length == 0 || bytes == null) {
            Loger.print(TAG, "Error , the command string is null", Thread.currentThread());
            return false;
        }
        try {
            Loger.print(TAG, "ssd send socket msg : " + new String(bytes, "UTF8"), Thread.currentThread());
            BufferedOutputStream outputStream = getBufferedOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            Loger.print(TAG, "ssd postRequestCommand with reason" + reason, Thread.currentThread());
            return true;
        } catch (IOException e) {
            Loger.print(TAG, "Error , ssd postRequestCommand with reason" + reason + " failed with exception :" + e.toString(), Thread.currentThread());
            LoginController.getInstance(context).logout(null);
            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.CONNECTING_LOST, false, "Socket 中断"));
            return false;
        }
    }

}
