package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/7/11. 10:36
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeScanEvent extends CubeEvents {
    public CubeEvents.CubeScanEventType type;
    public boolean success = false;
    public String message = "";

    public CubeScanEvent(CubeEvents.CubeScanEventType type, boolean success, String message) {
        this.type = type;
        this.success = success;
        this.message = message;
    }

}
