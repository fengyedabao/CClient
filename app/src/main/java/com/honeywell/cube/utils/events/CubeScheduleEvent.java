package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/6/29. 10:58
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeScheduleEvent extends CubeEvents {
    public CubeEvents.CubeScheduleEventType type;//事件类型
    public Object object;//传输对象
    public Boolean success;//事件状态

    public CubeScheduleEvent(CubeEvents.CubeScheduleEventType type, Object object, Boolean success) {
        super();
        this.type = type;
        this.object = object;
        this.success = success;
    }
}
