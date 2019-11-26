package com.honeywell.cube.net.queue;

import android.content.Context;

import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.ResponderController;
import com.honeywell.cube.controllers.WebsocketMessageController;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.Loger.Utility;

import org.json.JSONObject;

/**
 * Created by H157925 on 16/6/16. 06:39
 * Email:Shodong.Sun@honeywell.com
 */
public class ReceiverQueueManager implements Runnable {

    private static final String TAG = ReceiverQueueManager.class.getSimpleName();

    private static ReceiverQueueManager instance = null;
    private Context mContext;

    //接受线程
    private CommandCollection.ReceiverCommandCollection receiverCommandCollection;

    //线程运行标志
    private static boolean runFlag = false;

    private ReceiverQueueManager(Context context) {
        mContext = context;
        receiverCommandCollection = CommandCollection.ReceiverCommandCollection.getInstance();
    }

    /**
     * get thread safe singleton instance
     */
    public static ReceiverQueueManager getInstance(Context context) {
        if (instance == null) {
            instance = new ReceiverQueueManager(context);
        }
        return instance;
    }

    /**
     * 是否运行
     *
     * @return
     */
    public boolean isRunFlag() {
        return runFlag;
    }

    @Override
    public void run() {
        while (runFlag) {
            isDealCommandSucceed("receiver", receiverCommandCollection);
            Utility.threadSleep(10);
        }
    }

    /**
     * 开始
     */
    public void startRun() {
        Loger.print(TAG, "Start Run ", Thread.currentThread());
        if (!runFlag) {
            runFlag = true;
            new Thread(this).start();
        } else {
            Loger.print(TAG, "[] <warn> Thread already run !", Thread.currentThread());
        }
    }

    /**
     * 停止
     */
    public void stopRun() {
        Loger.print(TAG, "Stop Run", Thread.currentThread());
        if (runFlag) {
            runFlag = false;
            Loger.print(TAG, "Thread queue receiver command size:" + receiverCommandCollection.count(), Thread.currentThread());
            receiverCommandCollection.clearCommand();
            Loger.print(TAG, "[] <info> Thread stop !", Thread.currentThread());
        }
    }

    /**
     * 添加普通命令
     *
     * @param cmd
     */
    public void addRecevierToQueue(String cmd) {
        if (cmd == null || cmd.equalsIgnoreCase("")) return;
        receiverCommandCollection.addCommand(cmd);
    }

    /**************
     * private method
     ********************/
    private boolean isDealCommandSucceed(String tag, CommandCollection.CommandList commandList) {
        boolean result = false;
        Object command = null;

        synchronized (commandList) {
            if (!commandList.isEmpty()) {
                command = commandList.removeCommand(0);
            }
        }
        if (command != null) {
            if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
                //处理
                ResponderController.newInstance(mContext).dealWithWebSocketResponce((String) command);
                return true;
            } else if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
                Loger.print(TAG, "ssd local socket deal command " + command, Thread.currentThread());
                try {
                    JSONObject object = new JSONObject((String) command);
                    //socket
                    ResponderController.newInstance(mContext).dealNetData(object);
                    result = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                result = false;
            }
        }
        return result;

    }

}
