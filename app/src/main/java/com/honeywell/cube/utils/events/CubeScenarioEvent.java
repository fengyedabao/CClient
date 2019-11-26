package com.honeywell.cube.utils.events;

import android.support.annotation.Nullable;

/**
 * Created by H157925 on 16/5/10. 14:50
 * Email:Shodong.Sun@honeywell.com
 * //传递Scenario 事件
 */
public class CubeScenarioEvent extends CubeEvents {
    public CubeEvents.CubeScenarioEventType type;
    public Object object = null;
    public boolean success = false;

    public CubeScenarioEvent(CubeEvents.CubeScenarioEventType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public CubeScenarioEvent(CubeEvents.CubeScenarioEventType type, boolean success, Object object) {
        this.type = type;
        this.success = success;
        this.object = object;
    }


}
