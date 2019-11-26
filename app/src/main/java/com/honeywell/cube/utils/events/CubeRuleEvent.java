package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/6/2. 12:53
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeRuleEvent extends CubeEvents {
    public CubeEvents.CubeRuleEventType type;//事件类型
    public Object object;//传输对象
    public Boolean success;//事件状态

    public CubeRuleEvent(CubeEvents.CubeRuleEventType type, boolean success, Object object) {
        this.type = type;
        this.success = success;
        this.object = object;
    }
}
