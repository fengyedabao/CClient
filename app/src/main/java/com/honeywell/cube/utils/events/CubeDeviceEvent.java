package com.honeywell.cube.utils.events;

import org.json.JSONArray;

/**
 * Created by H157925 on 16/5/16. 14:11
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 与设备相关的通知
 */
public class CubeDeviceEvent extends CubeEvents {

    /**
     * 返回readdevice后传输的数据
     */
    public CubeEvents.CubeDeviceEventType type;
    public Object updateStatusData;
    public String deviceType;
    public int responceDataType;//返回的数据类型


    private boolean success;//判断
    private String message;//附加消息


    public CubeDeviceEvent() {
    }

    /**
     * 整理获取readDevice后数据更新的通知
     *
     * @param type
     * @param updateStatusArray
     * @param deviceType
     * @param dataType
     */
    public CubeDeviceEvent(CubeEvents.CubeDeviceEventType type, Object updateStatusArray, String deviceType, int dataType) {
        this.type = type;
        this.updateStatusData = updateStatusArray;
        this.deviceType = deviceType;
        this.responceDataType = dataType;
    }

    /**
     * 传输需要的参数
     *
     * @param type
     * @param updateStatusArray
     */
    public CubeDeviceEvent(CubeEvents.CubeDeviceEventType type, Object updateStatusArray) {
        this.type = type;
        this.updateStatusData = updateStatusArray;
    }

    public CubeDeviceEvent(CubeEvents.CubeDeviceEventType type, boolean success, String deviceType, String msg) {
        this.type = type;
        this.success = success;
        this.deviceType = deviceType;
        this.message = msg;
    }

    /**
     * 用于给IPC设备发送消息
     *
     * @param type
     * @param success
     * @param object
     * @param msg
     */
    public CubeDeviceEvent(CubeEvents.CubeDeviceEventType type, boolean success, Object object, String msg) {
        this.type = type;
        this.success = success;
        this.updateStatusData = object;
        this.message = msg;
    }
    public CubeDeviceEvent(CubeEvents.CubeDeviceEventType type, boolean success, Object object, String msg,Object data) {
        this.type = type;
        this.success = success;
        this.updateStatusData = object;
        this.message = msg;
        this.updateStatusData= data;
    }

    public Object getUpdateStatusData() {
        return updateStatusData;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public CubeEvents.CubeDeviceEventType getType() {
        return type;
    }

    public int getResponceDataType() {
        return responceDataType;
    }


    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }


}
