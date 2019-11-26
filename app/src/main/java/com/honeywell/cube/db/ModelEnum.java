package com.honeywell.cube.db;

import com.honeywell.cube.R;

import java.util.HashMap;

/**
 * Created by H157925 on 16/5/6. 16:07
 * Email:Shodong.Sun@honeywell.com
 * 所有的枚举  和 常量  这部分转换
 */

public class ModelEnum {

    /**
     * 呼叫的类型
     */
    public static final String CALL_TYPE_LOBBYPHONE = "lobby";
    public static final String CALL_TYPE_LOBBYPHONE3 = "lobby3";
    public static final String CALL_TYPE_OFFICEPHONE = "officephone";
    public static final String CALL_TYPE_GUARDPHONE = "guardphone";
    public static final String CALL_TYPE_PCOFFICE = "pcoffice";
    public static final String CALL_TYPE_PCOFFICE3 = "pcoffice3";
    public static final String CALL_TYPE_PCGUARD = "pcguard";
    public static final String CALL_TYPE_PCGUARD3 = "pcguard3";
    public static final String CALL_TYPE_DOORCAMERAFRONT = "doorcamerafront";
    public static final String CALL_TYPE_DOORCAMERABACK = "doorcameraback";
    public static final String CALL_TYPE_DOORCAMERAFRONT3 = "doorcamerafront3";
    public static final String CALL_TYPE_DOORCAMERABACK3 = "doorcameraback3";
    public static final String CALL_TYPE_NEIGHBOUR = "neighbour";
    public static final String TYPE_CALL_EXTENSION = "extension";

    /**
     * 首页房间参数
     */
    public static final int HOME_ROOM_TYPE_PM2_5 = 1;//pm2_5
    public static final int HOME_ROOM_TYPE_TEMPERATURE = 2;//temperature
    public static final int HOME_ROOM_TYPE_HUMIDITY = 3;//humidity
    public static final int HOME_ROOM_TYPE_CO_2 = 4;//Co2

    /**
     * 模块类型
     */
    public static final int MODULE_TYPE_SPARKLIGHTING = 1;
    public static final int MODULE_TYPE_BACNET = 2;
    public static final int MODULE_TYPE_IPC = 3;
    public static final int MODULE_TYPE_WIFIIR = 4;
    public static final int MODULE_TYPE_WIFIAIR = 5;
    public static final int MODULE_TYPE_WIFIRELAY = 6;
    public static final int MODULE_TYPE_WIFI485 = 7;
    public static final int MODULE_TYPE_WIREDZONE = 8;
    public static final int MODULE_TYPE_WIFI315M433M = 9;
    public static final int MODULE_TYPE_BACKAUDIO = 10;
    public static final int MODULE_TYPE_ALARMZONE = 11;
    public static final int MODULE_TYPE_IPVDP = 12;
    public static final int MODULE_TYPE_SCENARIO = 13;
    public static final int MODULE_TYPE_SCENARIOTRIGGER = 14;
    public static final int MODULE_TYPE_MUTEXRULE = 15;
    public static final int MODULE_TYPE_SCHEDULERULE = 16;
    public static final int MODULE_TYPE_ROOM = 17;
    public static final int MODULE_TYPE_VENTILATION = 18;

    /**
     * Cube统一Loop类型
     */
    public static final int LOOP_TYPE_LIGHT_INT = 1; // light
    public static final int LOOP_TYPE_CURTAIN_INT = 2; // curtain
    public static final int LOOP_TYPE_RELAY_INT = 3; // relay
    public static final int LOOP_TYPE_SWITCH_INT = 4; // switch
    public static final int LOOP_TYPE_SENSOR_INT = 5; // sensor
    public static final int LOOP_TYPE_5800PIRAP_INT = 6; // 5800-PIR-AP
    public static final int LOOP_TYPE_5804EU_INT = 7; // 5804EU
    public static final int LOOP_TYPE_5816EU_INT = 8; // 5816EU

    /**
     * 外接设备类型
     */
    public static final int DEVICE_TYPE_SWITCH = 0;
    public static final int DEVICE_TYPE_LIGHT = 1;
    public static final int DEVICE_TYPE_CURTAIN = 2;
    public static final int DEVICE_TYPE_RELAY = 3;
    public static final int DEVICE_TYPE_ZONE = 4;
    public static final int DEVICE_TYPE_IP_CAMERA = 5;
    public static final int DEVICE_TYPE_AC = 6;
    public static final int DEVICE_TYPE_BACKAUDIO = 7;
    public static final int DEVICE_TYPE_IR = 8;
    public static final int DEVICE_TYPE_IR_DVD = 9;
    public static final int DEVICE_TYPE_IR_TV = 10;
    public static final int DEVICE_TYPE_IR_STB = 11;
    public static final int DEVICE_TYPE_IR_AC = 12;
    public static final int DEVICE_TYPE_IR_CUSTOMIZE = 13;

    /**
     * 搜索索引
     */
    //常用设备
    public static final String MAIN_MODULE = "main_module";
    public static final String MAIN_IPVD = "main_ipvdp";
    public static final String MAIN_LIGHT = "main_light";
    public static final String MAIN_DIMMER = "main_dimmer";
    public static final String MAIN_CURTAIN = "main_curtain";
    public static final String MAIN_RELAY = "main_relay";
    public static final String MAIN_RELAY_MODULE = "main_relay_module";
    public static final String MAIN_ZONE = "main_zone";
    public static final String MAIN_ZONE_MODULE = "main_zone_module";
    public static final String MAIN_IPVDP_MODULE = "main_ipvdp_module";
    public static final String MAIN_AIR_CONDITION = "main_air_conditioner";
    public static final String MAIN_VENTILATION = "main_ventilation";
    public static final String MAIN_485_DEVICE = "main_485_device";
    public static final String MAIN_485_PORT = "main_485_port";
    public static final String MAIN_PURIFIER = "main_purifier";
    public static final String MAIN_IP_CAMERA = "main_ip_camera";
    public static final String MAIN_58_SENSOR = "main_58_sensor";
    public static final String MAIN_26_SENSOR = "main_26_sensor";
    public static final String MAIN_BACKAUDIO = "main_backaudio";
    public static final String MAIN_IR_DVD = "main_ir_dvd";
    public static final String MAIN_IR_TELEVISION = "main_ir_television";
    public static final String MAIN_IR_STB = "main_ir_stb";
    public static final String MAIN_IR_AC = "main_ir_ac";
    public static final String MAIN_IR_CUSTOMIZE = "main_ir_customize";
    public static final String MAIN_CALL_ELEVATOR = "main_call_elevator";

    /**
     * 系统类型
     */
    public static final String SPARKLIGHTING = "spark_lighting";
    public static final String WIRELESS_315_433 = "315_433m";
    public static final String WIFI_485 = "wifi485";

    public static final String LOOP_VENTILATION = "ventilation";
    public static final String LOOP_RELAY = "loop_relay";
    public static final String LOOP_ZONE = "loop_zone";
    public static final String LOOP_IPVDP = "loop_ipvdp";
    public static final String LOOP_5804EU = "loop_5804EU";
    public static final String LOOP_5816EU = "loop_5816EU";
    public static final String LOOP_BACNET = "loop_bacnet";
    public static final String LOOP_BACKAUDIO = "loop_backaudio";
    public static final String LOOP_IPC = "loop_ipc";
    public static final String LOOP_IR_DVD = "loop_ir_dvd";
    public static final String LOOP_IR_TV = "loop_ir_tv";
    public static final String LOOP_IR_STB = "loop_ir_stb";
    public static final String LOOP_IR_AC = "loop_ir_ac";
    public static final String LOOP_IR_CUSTOM = "loop_ir_custom";
    public static final String LOOP_IR = "loop_ir";


    /**
     * 窗帘状态
     */
    public static final int CURTAIN_STATUS_OPENING = 1;
    public static final int CURTAIN_STATUS_CLOSING = 2;
    public static final int CURTAIN_STATUS_PAUSING = 3;


    /**
     * Backaudio status
     */
    public static final int BACKAUDIO_STATUS_START = 1;
    public static final int BACKAUDIO_STATUS_PAUSE = 2;
    public static final int BACKAUDIO_STATUS_PREVIOUS = 3;
    public static final int BACKAUDIO_STATUS_NEXT = 4;
    public static final int BACKAUDIO_STATUS_MUTE = 5;
    public static final int BACKAUDIO_STATUS_NO_MUTE = 6;

    public static final int BACKAUDIO_TYPE_INT = 10;


    public static final String BACKAUDIO_STATUS_START_S = "play";
    public static final String BACKAUDIO_STATUS_PAUSE_S = "pause";
    public static final String BACKAUDIO_STATUS_MUTE_S = "on";
    public static final String BACKAUDIO_STATUS_NO_MUTE_S = "off";
    public static final String BACKAUDIO_STATUS_POWER_ON = "on";
    public static final String BACKAUDIO_STATUS_POWER_OFF = "off";


    /**
     * Relay 类型 灯光，开关
     */
    public static final String RELAYTYPE_RELAY = "relay";
    public static final String RELAYTYPE_SWITCH = "switch";


    /**
     * UI
     */
    public static final int UI_TYPE_TITLE = 1;
    public static final int UI_TYPE_OTHER = 2;

    public static final int UI_TYPE_LIST = 3;
    public static final int UI_TYPE_NO_LIST = 4;

    /**
     * 设备操作状态 删除，编辑
     */
    public static final int CHANGE_DEVICE_STATUS_MODIFY = 1;
    public static final int CHANGE_DEVICE_STATUS_DELETE = 2;


    /**
     * 访客号码
     */
    public static final String GuestNum = "999999999";

    /**
     * 扫描的设备类型
     */
    public static final int SCAN_TYPE_CUBE = 0;
    public static final int SCAN_TYPE_433 = 1;
    public static final int SCAN_TYPE_MAIA = 2;

    /**
     * 扫描后事件处理类型
     */
    public static final int SCANED_TYPE_BIND_CUBE = 1;//扫描后绑定Cube
    public static final int SCANED_TYPE_WIFI_LOGIN = 2;//局域网登陆
    public static final int SCANED_TYPE_REPLACE_CUBE = 3;//扫描后替换Cube
    public static final int SCANED_TYPE_ADD_CUBE_DEVICE = 4;//扫描添加Cube设备

    /**
     * CUBE编号前缀
     */
    public static final String CUBE_PREFIX_DEVICE_SERIAL = "00100001";//CUBE 前置序列号


    /**
     * 默认 delay 时间
     */
    public static final int ZONE_DEFAULT_DELAY_TIME = 20;

    /**
     * 通知 长度
     */
    public static final int NOTIFICATION_PAGE_COUNT = 10;

    /**
     * 485设备类型
     */

    public static final String LOOP_485_VENTILATION = "ventilation";
    public static final String LOOP_485_AC = "aircondition";
    public static final String LOOP_485_THERMOSTAT = "thermostat";

    /**
     * Bacnet 空调类型
     */
    public static final String BACNET_TYPE_DAKIN = "dakin";
    public static final String BACNET_TYPE_SANLING = "sanling";

    /**
     * rule 部分
     */
    public static final String RULE_ROOM_TRIGGER_HIGH = "valuehigherthan";
    public static final String RULE_ROOM_TRIGGER_LOW = "valuelowerthan";

    public static final String RULE_ROOM_TYPE_PM25 = "pm2_5";
    public static final String RULE_ROOM_TYPE_TEMPERATURE = "temperature";
    public static final String RULE_ROOM_TYPE_HUMIDITY = "humidity";

    /**
     * IR 部分
     */
    public static final String DEVICE_IR_ADD_CUSTOMIZE = "IR_ADD_CUSTOMIZE";

    // IR 类型
    public static final String IR_TYPE_CUSTOMIZE_S = "customize";
    public static final String IR_TYPE_AC_S = "ac";
    public static final String IR_TYPE_DVD_S = "dvd";
    public static final String IR_TYPE_TV_S = "television";
    public static final String IR_TYPE_TV_COMPLEX_S = "television_complex";
    public static final String IR_TYPE_STB_S = "stb";

    public static final int IR_TYPE_CUSTOMIZE = 0;
    public static final int IR_TYPE_AC = 1;
    public static final int IR_TYPE_DVD = 2;
    public static final int IR_TYPE_TV = 3;
    public static final int IR_TYPE_TV_COMPLEX = 4;
    public static final int IR_TYPE_STB = 5;
    public static final HashMap<String, Integer> IR_TYPE_MAP = new HashMap<>();

    //新风 风速
    public static String VENTILATION_FAN_SPEED_LOW = "low";
    public static String VENTILATION_FAN_SPEED_MIDDLE = "middle";
    public static String VENTILATION_FAN_SPEED_HIGH = "high";

    //新风 模式
    public static String VENTILATION_HUMIDITY = "humidity";
    public static String VENTILATION_DEHUMIDITY = "dehumidity";

    //新风 内 外 循环
    public static String VENTILATION_INNER = "inner";
    public static String VENTILATION_OUTSIDE = "outside";

    static {
        IR_TYPE_MAP.put(IR_TYPE_CUSTOMIZE_S, IR_TYPE_CUSTOMIZE);
        IR_TYPE_MAP.put(IR_TYPE_AC_S, IR_TYPE_AC);
        IR_TYPE_MAP.put(IR_TYPE_DVD_S, IR_TYPE_DVD);
        IR_TYPE_MAP.put(IR_TYPE_TV_S, IR_TYPE_TV);
        IR_TYPE_MAP.put(IR_TYPE_TV_COMPLEX_S, IR_TYPE_TV_COMPLEX);
        IR_TYPE_MAP.put(IR_TYPE_STB_S, IR_TYPE_STB);
    }
}
