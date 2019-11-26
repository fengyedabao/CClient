package com.honeywell.cube.utils.events;

import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by H157925 on 16/5/3. 10:37
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeBasicEvent extends CubeEvents {
    public CubeEvents.CubeBasicEventType type;
    public String message;
    public boolean isSuccessed;

    /**
     * 普通消息
     * @param type
     * @param isSuccessed
     * @param message
     */
    public CubeBasicEvent(CubeEvents.CubeBasicEventType type, @Nullable Boolean isSuccessed, @Nullable String message){
        this.type = type;
        this.isSuccessed = isSuccessed;
        this.message = message;
    }


    /**
     * 升级使用
     */
    private Map<String, String> info;
    public CubeBasicEvent(CubeEvents.CubeBasicEventType type, Map<String, String> updateInfo)
    {
        this.type = type;
        this.info = updateInfo;
    }


    public Boolean getIsSuccessed() {
        return isSuccessed;
    }

    public String getMessage() {
        return message;
    }

    public CubeBasicEventType getType() {
        return type;
    }

    public Map<String, String> getInfo(){return info;}

}
