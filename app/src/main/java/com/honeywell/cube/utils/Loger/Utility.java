package com.honeywell.cube.utils.Loger;

/**
 * Created by h157925 on 4/6/2016.11:46
 * Email:Shoudong.Sun@Honeywell.com
 */
public class Utility {

    //private static final String TAG = "Utility";

    public static void threadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static boolean isEmpty(String value)
    {
        return isEmpty(value, true);

    }

    public static boolean isEmpty(String value, boolean isNullEnabeld) {
        if (value == null) {
            if (isNullEnabeld) {
                return true;
            } else {
                throw new NullPointerException();
            }
        }
        return value.length() == 0;
    }





}
