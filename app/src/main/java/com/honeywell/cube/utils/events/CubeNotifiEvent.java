package com.honeywell.cube.utils.events;

import com.honeywell.cube.controllers.UIItem.notifi.NotifiUIItem;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/7/12. 16:46
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeNotifiEvent extends CubeEvents {
    public ArrayList<NotifiUIItem> list = new ArrayList<>();
    public CubeEvents.CubeNotifiEventType type;
    public String message = null;
    public boolean success = false;

    public CubeNotifiEvent(CubeEvents.CubeNotifiEventType type, ArrayList<NotifiUIItem> list) {
        this.type = type;
        this.list = list;
    }

    public CubeNotifiEvent(CubeEvents.CubeNotifiEventType type) {
        this.type = type;
    }

    public CubeNotifiEvent(CubeEvents.CubeNotifiEventType type, boolean success, String message) {
        this.type = type;
        this.message = message;
        this.success = success;
    }

}
