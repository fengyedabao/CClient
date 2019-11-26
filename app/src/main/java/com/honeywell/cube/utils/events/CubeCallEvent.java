package com.honeywell.cube.utils.events;

import com.honeywell.cube.controllers.UIItem.IPCameraInfo;
import com.honeywell.cube.ipc.CallMsgDetailInfo;

/**
 * Created by H157925 on 16/6/29. 09:46
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeCallEvent extends CubeEvents {
    public CubeEvents.CubeCallEventType type;
    public boolean success = false;

    public CallMsgDetailInfo cameraInfo = new CallMsgDetailInfo();

    public CubeCallEvent(CubeEvents.CubeCallEventType type, boolean success, CallMsgDetailInfo cameraInfo) {
        this.type = type;
        this.success = success;
        this.cameraInfo = cameraInfo;
    }
}
