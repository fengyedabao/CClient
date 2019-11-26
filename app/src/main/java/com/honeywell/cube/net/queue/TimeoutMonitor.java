package com.honeywell.cube.net.queue;

import com.honeywell.cube.net.NetConstant;

/**
 * Created by H157925 on 16/5/28. 11:20
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 每发送一次，将创建这个对象，当收到回复时 这个对象将被置null
 */
public class TimeoutMonitor {
    private static final String TAG = TimeoutMonitor.class.getSimpleName();

    private Object command;
    private long sendMillis;
    private long waitMillis = 0;

    public TimeoutMonitor(Object cmd) {
        this.command = cmd;
        this.sendMillis = System.currentTimeMillis();
    }

    public long getWaitMillis() {
        waitMillis = System.currentTimeMillis() - sendMillis;
        return waitMillis;
    }

    public Object getCommand() {
        return command;
    }

    public int getTimeOutValue() {
        return NetConstant.MsgTimeOut;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("        command: ").append(getCommand()).append("\n");
        res.append("        maxTimeoutMissis: ").append(getTimeOutValue()).append("\n");
        res.append("        waitMillis: ").append(waitMillis).append("\n");
        return res.toString();
    }

}
