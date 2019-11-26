package com.honeywell.cube.ipc;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

public class Util {

	public static boolean detectOpenGLES20(Context context) {  
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
	    ConfigurationInfo info = am.getDeviceConfigurationInfo();  
	    return (info.reqGlEsVersion >= 0x20000);  
	}
}
