package com.honeywell.cube.utils.events;

/**
 * Created by H157925 on 16/7/7. 14:10
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeAlarmEvent extends CubeEvents {

    public CubeEvents.CubeAlarmEventType type;
    public String alarmLoopName = "";
    public String alarmType = "";


    public CubeAlarmEvent(CubeEvents.CubeAlarmEventType type, String name, String alarmType) {
        this.type = type;
        this.alarmLoopName = name;
        this.alarmType = alarmType;
    }

}
