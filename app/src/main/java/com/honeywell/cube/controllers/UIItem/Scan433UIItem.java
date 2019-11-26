package com.honeywell.cube.controllers.UIItem;

import com.honeywell.cube.common.CommonData;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;

/**
 * Created by H157925 on 16/7/14. 21:43
 * Email:Shodong.Sun@honeywell.com
 */
public class Scan433UIItem {
    public String id = "";//扫描获取的ID
    public String sensor_433_type = CommonData.SENSOR_TYPE_433_INFRADE;

    public int loopCount = 1;
    public String section="";
    public String name = "";//昵称
    public String roomName = "";//房间
    public int roomId = 0;
    public String zoneType = CommonData.ZONE_TYPE_SECURITY_INSTANT;//防区类型
    public String alarmType = CommonData.ZONE_ALARM_STATUS_FIRE;//报警类型
    public int delaytime = 0;//单位是秒 只有在Zone type 为 delay时才使用，UI上才可以刷新出来

    public String mainDeviceName = "";
    public PeripheralDevice mainDevice = null;

    public Scan433UIItem() {
    }
}
