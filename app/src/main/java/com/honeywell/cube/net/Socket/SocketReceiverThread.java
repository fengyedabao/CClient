package com.honeywell.cube.net.Socket;

import android.content.Context;

import com.honeywell.cube.db.ModelEnum;
import com.honeywell.cube.net.NetConstant;
import com.honeywell.cube.utils.Loger.Loger;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by H157925 on 16/7/7. 16:45
 * Email:Shodong.Sun@honeywell.com
 */
public class SocketReceiverThread {
    public static final String TAG = SocketReceiverThread.class.getSimpleName();

    private SocketClient socketClient;
    private Timer timer;
    private Context mContext;
    private byte[] resultData;


    public SocketReceiverThread(Context context, SocketClient client) {
        this.mContext = context;
        this.socketClient = client;
    }

    /**
     * start time task to receive response
     */
    public void startRun() {
        Loger.print(TAG, "ssd start socket receiver", Thread.currentThread());
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new OverrideTask(), 50, 2000);
    }

    public void stopRun() {
        Loger.print(TAG, "Stop Run", Thread.currentThread());
        if (timer != null) {
            timer.cancel();
        }
    }

    public class OverrideTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!socketClient.isConnectToCube()) {
                    this.cancel();
                    return;
                }

                BufferedInputStream inputStream = socketClient.getBufferedInputStream();
                if (null == inputStream) {
                    return;
                }
                int length = inputStream.available();
                if (length == 0) {
                    return;
                }

                byte[] b = new byte[length];
                inputStream.read(b, 0, length);
                boolean ifcheck = SocketController.newInstance(mContext).dealSocketReceiveData(b);
                byte[] resultData = SocketController.newInstance(mContext).resultData;
                byte[] result = null;
                if (!ifcheck && resultData != null) {
                    result = new byte[resultData.length + length - 1];
                    for (int i = 0; i < resultData.length - 1; i++) {
                        result[i] = resultData[i];
                    }
                    for (int i = 0; i < length; i++) {
                        result[resultData.length - 1 + i] = b[i];
                    }
                    SocketController.newInstance(mContext).resultData = result;
                }
                SocketController.newInstance(mContext).dealSocketReceiveData(SocketController.newInstance(mContext).resultData);
            } catch (Exception e) {
                Loger.print(TAG, "Exception in OverrideTask run()", e);
                //socket.resetSocket();
            }
        }
    }

}
