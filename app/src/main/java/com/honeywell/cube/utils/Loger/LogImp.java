package com.honeywell.cube.utils.Loger;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by h157925 on 4/6/2016.10:23
 * Email:Shoudong.Sun@Honeywell.com
 */
public class LogImp implements Runnable{
    private static final String TAG = "LogImp";
    private Loger loger = null;
    static LogImp instance = null;

    //log store quere
    private List<String> printOutList = new ArrayList<>();

    //log file
    private FileOutputStream fos = null;

    //log print stream
    private PrintStream print = null;

    //time format
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    // polling thread flag
    private boolean runFlag = false;
    // current day
    private int currDay = -1;

    /**
     * get singleton object
     *
     * @param loger
     * @return
     */
    public static LogImp getInstance(Loger loger) {
        instance = new LogImp(loger);
        return instance;
    }
    /**
     * construction method
     *
     * @param loger
     */
    private LogImp(Loger loger) {
        this.loger = loger;
    }

    @SuppressLint("SimpleDateFormat")
    private void initPrint() {
        Calendar date = Calendar.getInstance();
        currDay = date.get(Calendar.DAY_OF_YEAR);
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
        //DateFormat dfm = DateFormat.getDateInstance();
        String fileName = dfm.format(date.getTime()) + ".log";
        String path = null;
        try {
            if (null != print) {
                close();
            }

            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HmiApp/";
            File dir = new File(path);

            if (!dir.exists()) {
                dir.mkdir();
            }

            fos = new FileOutputStream(path + fileName, true);

            print = new PrintStream(fos, true);
        } catch (Exception e) {
            loger.output(TAG, "[LogerImp] Can't open file:" + path + " File name:  " + fileName +
                    " Error description:" + e.getLocalizedMessage(), Thread.currentThread());
        }
    }

    /**
     * open thread
     *
     */
    public void startRun() {
        Loger.print(TAG, "Start Run", Thread.currentThread());
        if (!runFlag) {
            runFlag = true;
            new Thread(this).start();
        } else {
            loger.output(TAG, "[LogerImp] <warn> Thread already run, ", Thread.currentThread());
        }
    }

    /**
     * stop thread
     *
     */
    public void stopRun() {
        Loger.print(TAG, "Stop Run", Thread.currentThread());
        if (runFlag) {
            //gcRun.flag = false;
            runFlag = false;
            synchronized (printOutList) {
                printToFile("[LogerImp] <info> Thread stop, " + "queue size:" + printOutList.size());
                while(0 < printOutList.size()) {
                    runMethod();
                }
            }
            close();
        }

    }

    /**
     * add log into queue
     *
     * @param msg
     */
    protected synchronized void submitMsg(String msg) {
        synchronized (printOutList) {
            printOutList.add(msg);
        }
    }

    /**
     * close stream
     *
     */
    private void close() {
        try {
            if(print != null) {
                print.flush();
                print.close();
                print = null;
            }
            if(fos != null) {
                fos.close();
                fos = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        initPrint();
        try {
            printToFile("[LogerImp] <info> Start new thread ... ");
            while (runFlag) {
                runMethod();
            }
            runFlag = false;
        } catch (Exception e) {
            printToFile("[LogerImp] <warn> Thread error : " + e.getLocalizedMessage());

            if (runFlag) {
                printToFile("[LogerImp] Thread forced interrupt " + e.getLocalizedMessage());
                new Thread(this).start();
            }
        }
    }

    /**
     * loop in thread
     * @throws InterruptedException
     *
     * @throws Exception
     */
    private void runMethod() {
        String line = null;
        synchronized (printOutList) {
            if (!printOutList.isEmpty()) {
                line = printOutList.remove(0);
            }
        }

        if (null != line) {
            printToFile(line);
        } else {
            Utility.threadSleep(10);
        }
    }

    /**
     * print log into file
     *
     * @param line
     */
    private void printToFile(String line) {
        Calendar date = Calendar.getInstance();
        int day = date.get(Calendar.DAY_OF_YEAR);
        if (day != currDay) {
            initPrint();
        }

        if (null == print) {
            return;
        }

        print.println(">>> " + format.format(date.getTime()) + " -- " + line);
        print.flush();
    }
}
