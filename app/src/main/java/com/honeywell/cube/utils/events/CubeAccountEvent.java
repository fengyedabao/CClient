package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/6/7. 15:12
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeAccountEvent extends  CubeEvents{

    public CubeEvents.CubeAccountEventType type;//事件类型
    public Object object;//传输对象
    public Boolean success;//事件状态

    public CubeAccountEvent(CubeEvents.CubeAccountEventType type, boolean success, Object object) {
        this.type = type;
        this.success = success;
        this.object = object;
    }
}
