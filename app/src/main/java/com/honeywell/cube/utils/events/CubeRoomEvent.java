package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/6/1. 13:48
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeRoomEvent extends CubeEvents{
    public CubeEvents.CubeRoomEventType type;//事件类型
    public Object object;//传输对象
    public Boolean success;//事件状态

    public CubeRoomEvent(CubeEvents.CubeRoomEventType type, boolean success, Object object) {
        this.type = type;
        this.success = success;
        this.object = object;
    }
}
