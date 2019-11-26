package com.honeywell.cube.net.queue;

import android.content.Context;

import com.honeywell.cube.R;
import com.honeywell.cube.controllers.LoginController;
import com.honeywell.cube.controllers.WebsocketMessageController;
import com.honeywell.cube.net.Socket.SocketController;
import com.honeywell.cube.utils.Loger.Loger;
import com.honeywell.cube.utils.Loger.Utility;
import com.honeywell.cube.utils.events.CubeBasicEvent;
import com.honeywell.cube.utils.events.CubeEvents;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.greenrobot.event.EventBus;

/**
 * Created by H157925 on 16/5/28. 10:07
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 命令发送线程，WebSocket和Socket共用
 */
public class CommandQueueManager implements Runnable {
    private static final String TAG = CommandQueueManager.class.getSimpleName();

    private static CommandQueueManager instance = null;
    private Context mContext;


    //队列，普通队列和Ping包
    private CommandCollection.NormalCommandCollection normalCommandCollection;
    private CommandCollection.PingCommandCollection pingCommandCollection;


    //超时对象
    public TimeoutMonitor timeoutMonitor = null;
    private final Lock lock = new ReentrantLock();
    //线程运行标志
    private static boolean runFlag = false;

    private CommandQueueManager(Context context) {
        mContext = context;
        normalCommandCollection = CommandCollection.NormalCommandCollection.getInstance();
        pingCommandCollection = CommandCollection.PingCommandCollection.getInstance();
    }

    /**
     * get thread safe singleton instance
     */
    public static CommandQueueManager getInstance(Context context) {
        if (instance == null) {
            instance = new CommandQueueManager(context);
        }
        return instance;
    }

    @Override
    public void run() {
        Loger.print(TAG, "[] <info> Start new thread ... ", Thread.currentThread());
        while (runFlag) {
            if (timeoutMonitor != null) {
                checkIfTimeOut();
            }
//            else {
//                boolean sendCommand = isSendCommandSucceed("normal", normalCommandCollection);
//                if (!sendCommand) {
//                    boolean sendOccupty = isSendCommandSucceed("ping", pingCommandCollection);
//                    if (!sendOccupty) {
//                        Utility.threadSleep(50);
//                    }
//                }
//            }
            /**
             * 测试
             */
            boolean sendCommand = isSendCommandSucceed("normal", normalCommandCollection);
            if (!sendCommand) {
                boolean sendOccupty = isSendCommandSucceed("ping", pingCommandCollection);
                if (!sendOccupty) {
                    Utility.threadSleep(50);
                }
            }
            Utility.threadSleep(10);
        }
    }


    /**
     * 开始
     */
    public void startRun() {
        if (!runFlag) {
            Loger.print(TAG, "receiver queue start run", Thread.currentThread());
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
            Loger.print(TAG, "Thread queue normal command size:" + normalCommandCollection.count(), Thread.currentThread());
            normalCommandCollection.clearCommand();
            Loger.print(TAG, "Thread queue ping count size:" + pingCommandCollection.count(), Thread.currentThread());
            pingCommandCollection.clearCommand();
            Loger.print(TAG, "[] <info> Thread stop !", Thread.currentThread());
            timeoutMonitor = null;
        }
    }

    /**
     * 添加普通命令
     *
     * @param cmd
     */
    public void addNormalCommandToQueue(String cmd) {
        if (cmd == null || cmd.equalsIgnoreCase("")) return;
        normalCommandCollection.addCommand(cmd);
    }

    /**
     * 添加心跳包
     *
     * @param cmd
     */
    public void addPingCommandToQueue(String cmd) {
        if (cmd == null || cmd.equalsIgnoreCase("")) return;
        pingCommandCollection.addCommand(cmd);
    }

    /**************
     * private method
     ********************/
    private boolean isSendCommandSucceed(String tag, CommandCollection.CommandList commandList) {
        boolean result = false;
        Object command = null;

        synchronized (commandList) {
            if (!commandList.isEmpty()) {
                command = commandList.removeCommand(0);
            }
        }

        if (command != null) {
            if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_CLOUD) {
                //WebSocket
                result = WebsocketMessageController.getInstance(mContext).sendMessageWithInfo(mContext, (String) command, tag);
                if (result && tag.equalsIgnoreCase("normal")) {
                    timeoutMonitor = new TimeoutMonitor(command);
                }
                result = true;
            } else if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
                //socket
                Loger.print(TAG, "ssd socket send str :" + command, Thread.currentThread());
                result = SocketController.newInstance(mContext).sendSocketCommand((String) command);
                if (result && tag.equalsIgnoreCase("normal")) {
                    timeoutMonitor = new TimeoutMonitor(command);
                } else {
                    //发送失败，发送Event事件
//                    EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.CONNECTING_LOST, false, mContext.getString(R.string.error_time_out)));
                }
                result = true;
            } else {
                result = false;
            }
        }
        return result;

    }

    /**
     * 检查上一条命令是否超时
     */
    private synchronized void checkIfTimeOut() {

        if (timeoutMonitor == null) {
            return;
        } else {
//            lock.lock();
            if (timeoutMonitor.getWaitMillis() > timeoutMonitor.getTimeOutValue()) {
                //已经超时
                Loger.print(TAG, "ssd time out : " + timeoutMonitor.toString(), Thread.currentThread());
                timeoutMonitor = null;

                //清空发送队列
                normalCommandCollection.clearCommand();
                pingCommandCollection.clearCommand();
                //发送超时Event
                EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.TIME_OUT, true, mContext.getString(R.string.error_time_out)));
            }
//            lock.unlock();
        }
    }
}
