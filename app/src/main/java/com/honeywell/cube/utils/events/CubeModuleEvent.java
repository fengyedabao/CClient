package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/6/2. 10:41
 * Email:Shodong.Sun@honeywell.com
 * <p>
 * 用于控制模块的消息
 */
public class CubeModuleEvent extends CubeEvents {
    public CubeEvents.CubeModuleEventType type;//事件类型
    public Object object;//传输对象
    public Boolean success;//事件状态

    public CubeModuleEvent(CubeEvents.CubeModuleEventType type, boolean success, Object object) {
        this.type = type;
        this.success = success;
        this.object = object;
    }
}
