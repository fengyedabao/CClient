package com.honeywell.cube.controllers.DeviceControllers;

/**
 * Created by H157925 on 16/5/12. 14:53
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这个类用于组织Device界面显示的名称和图片名称
 */
public class DeviceListType {
    public String deviceName;
    public String deviceImageName;

    public DeviceListType() {
    }

    @Override
    public String toString() {
        return "  deviceName :" + deviceName + " deviceImageName : " + deviceImageName;
    }
}
