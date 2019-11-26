
package com.honeywell.cube.application;

import android.app.Application;

import com.honeywell.lib.utils.DebugUtil;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        if (DebugUtil.DEBUG) {
            LogcatHelper.getInstance(this).start();
        }
    }
}
