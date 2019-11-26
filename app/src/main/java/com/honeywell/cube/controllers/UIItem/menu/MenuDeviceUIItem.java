package com.honeywell.cube.controllers.UIItem.menu;

import com.honeywell.cube.db.configuredatabase.DeviceAndFunc.BackaudioDevice;
import com.honeywell.cube.db.configuredatabase.PeripheralDevice;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/6/12. 10:12
 * Email:Shodong.Sun@honeywell.com
 */
public class MenuDeviceUIItem {
    public String deviceType="";
    public Object object=null;
    public boolean select = false;

    /**
     * 侧边栏 设备列表
     */
    public String deviceName = "";
    /**
     * 侧边栏 添加SparkLighting
     */
    //第一级页面 SparkLighting 列表
    public String sparkTitle = "";
    public String sparkDetails = "";
    public String sparkType = "";
    public int sparkLoop = 0;

    //第二级页面
    public int sparkDeviceId = 1;

    public PeripheralDevice peripheraDevice = null;
    public BackaudioDevice backaudioDevice = null;
    public ArrayList<MenuDeviceLoopObject> loopObjects = new ArrayList<>();


    /**
     * IP Camera
     */
    public String IPC_Title = "";
    public String IPC_Room = "";
    public int IPC_Roomid = 0;
    public String IPC_Name = "";
    public String IPC_Ip = "";
    public String IPC_type = "";
    public String IPC_User = "";
    public String IPC_Password = "";



    //默认构造函数
    public MenuDeviceUIItem(){}
    /**
     * 在侧边栏获取设备列表时构造方法
     *
     * @param object
     * @param deviceType
     */
    public MenuDeviceUIItem(Object object, String deviceType) {
        this.object = object;
        this.deviceType = deviceType;
    }

    /**
     * 侧边栏 二级界面 设备列表的选择
     *
     * @param object
     * @param deviceType
     * @param deviceName－－UI用于显示的名称
     */
    public MenuDeviceUIItem(Object object, String deviceType, String deviceName) {
        this.object = object;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
    }

    /**
     * Spark light 界面获取的参数
     *
     * @param title
     * @param details
     * @param loop
     * @param type
     */
    public MenuDeviceUIItem(String title, String details, String loop, String type) {
        this.sparkTitle = title;
        this.sparkDetails = details;
        this.sparkLoop = Integer.parseInt(loop);
        this.sparkType = type;
    }

}
