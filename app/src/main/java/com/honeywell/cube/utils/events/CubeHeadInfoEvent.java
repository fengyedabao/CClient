package com.honeywell.cube.utils.events;

/**
 * Created by shushunsakai on 16/7/1.
 */
public class CubeHeadInfoEvent extends CubeEvents{

    public CubeEvents.CubeHeadINfoUpdateEventType type;//事件类型

    public CubeHeadInfoEvent(CubeHeadINfoUpdateEventType type) {
        this.type = type;
    }
}
