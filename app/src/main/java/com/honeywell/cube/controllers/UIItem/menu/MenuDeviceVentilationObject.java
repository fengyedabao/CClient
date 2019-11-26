package com.honeywell.cube.controllers.UIItem.menu;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.RelayLoop;

/**
 * Created by H157925 on 16/8/2. 09:43
 * Email:Shodong.Sun@honeywell.com
 * 新风系统侧边栏 添加设备
 * 下面的 开关需要全部配置 否则 无法添加该设备
 */
public class MenuDeviceVentilationObject {

    public String room = "";//房间
    public String name = "";//名称
    public int roomId = -1;

    public RelayLoop power = new RelayLoop();//开关
    public RelayLoop cycle = new RelayLoop();//内外循环
    public RelayLoop fan_speed_high = new RelayLoop();//风速高
    public RelayLoop fan_speed_middle = new RelayLoop();//风速中
    public RelayLoop fan_speed_low = new RelayLoop();//风速低
    public RelayLoop mode_humidity = new RelayLoop();//模式 加湿
    public RelayLoop mode_dehumidity = new RelayLoop();//模式 除湿

    public MenuDeviceVentilationObject() {
    }

    /**
     * 检查Relay参数是否为空
     * <p/>
     * 为空则返回true
     *
     * @return
     */
    public boolean checkRelay() {
        if (power == null || cycle == null || fan_speed_high == null || fan_speed_low == null || fan_speed_middle == null || mode_dehumidity == null || mode_humidity == null) {
            return true;
        }
        return false;
    }

}
