package com.honeywell.cube.utils.Loger;

import android.annotation.SuppressLint;
import android.util.Log;

import com.honeywell.lib.utils.DebugUtil;

/**
 * Created by h157925 on 4/6/2016.10:22
 * Email:Shoudong.Sun@Honeywell.com
 */
public class Loger {
    private LogImp instance;
    // log name
    private static final String logerName = "[ssd Cube Loger]";

    // debugger flag
    protected static boolean isOpen = DebugUtil.DEBUG;

//    protected static boolean isOpen = true;
    private static Loger loger = new Loger();


    @SuppressLint("SimpleDateFormat")

    /**
     * start log print
     *
     */
    public static void openPrint() {
        if (isOpen) {
            LogImp.instance.startRun();
        }
    }

    /**
     * close log print
     */
    public static void closePrint() {
        if (isOpen) {
            LogImp.instance.stopRun();
        }
    }

    /**
     * print log
     */
    public synchronized static void print(String tag, String msg, Thread currThread) {
        if (isOpen) {
            loger.output(tag, msg, currThread);
        }
    }

    /**
     * print the detail of exception
     *
     * @param msg
     * @param e
     */
    public synchronized static void print(String tag, String msg, Exception e) {
        if (isOpen) {
            loger.output(tag, msg, e);
        }
    }

    /**
     * construction method
     *
     * @param name
     */
    public Loger() {
        instance = LogImp.getInstance(this);
    }


    /**
     * print log
     *
     * @param msg
     */
    public synchronized void output(String tag, String msg, Thread currThread) {
        if (isOpen) {
            Log.e(logerName + " -- " + tag + "--" + currThread.getName(), msg);
            instance.submitMsg(logerName + " -- " + tag + "--" + currThread.getName() + "\n" + msg);
        }
    }

    /**
     * print the detail of exception
     *
     * @param msg
     * @param e
     */
    public synchronized void output(String tag, String msg, Exception e) {
        if (isOpen) {
            Log.i(logerName + " -- " + tag, msg, e);
            StringBuffer buf = new StringBuffer(msg);
            buf.append(logerName + " -- " + tag).append(" : ").append(msg).append("\n");
            buf.append(e.getClass()).append(" : ");
            buf.append(e.getLocalizedMessage());
            buf.append("\n");
            StackTraceElement[] stack = e.getStackTrace();

            for (StackTraceElement trace : stack) {
                buf.append("\t at ").append(trace.toString()).append("\n");
            }

            instance.submitMsg(buf.toString());
        }
    }

    /**
     * print memory info
     */
    public void printCurrentMemory() {
        if (isOpen) {
            StringBuilder logs = new StringBuilder();

            long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
            long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
            long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
            logs.append("\t[Memory_free]: ").append(freeMemory).append(" kb");
            logs.append("\t[Memory_total]: ").append(totalMemory).append(" kb");
            logs.append("\t[Memory_max]: ").append(maxMemory).append(" kb");
            Log.i(logerName, logs.toString());
            instance.submitMsg(logerName + " " + logs.toString());
        }
    }


}
