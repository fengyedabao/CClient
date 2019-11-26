package com.honeywell.cube.net;

import com.honeywell.lib.utils.DebugUtil;

/**
 * Created by H157925 on 16/4/14. 14:44
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 这个类用于纪录网络请求中的所有常量，包括URL，Port 等
 */
public class NetConstant {

    //服务请求超时时间
    public static final int MsgTimeOut = 18000;

    //心跳包请求事件间隔
    public static final int PingInteval = 20000;
    public static final String TCP_HEAD = "PHONE_CUBE_PROTOCOL";
    /**
     * 服务器地址
     */
    //#define IP_ALL @"qa.acscloud.honeywell.com.cn" // QA测试
    //总IP 用于网络请求
//    public static final String IP_ALL = "115.159.221.25:80";
    //    public static final String IP_ALL = "115.159.150.115:80";
//    public static final String IP_ALL = "dev.acscloud.honeywell.com.cn";
//    public static final String IP_ALL = "qa.acscloud.honeywell.com.cn";
    public static final String IP_ALL = DebugUtil.DEBUG ? "qa.acscloud.honeywell.com.cn" : "acscloud.honeywell.com.cn";

    //Origin 用于Websocket
//    public static final String URI_WEBSOCKET_ORIGIN = "http://115.159.221.25";
    public static final String URI_WEBSOCKET_ORIGIN = "https://" + IP_ALL;

    //总地址
    public static final String URI_ALL = "https://" + IP_ALL + "/v1/00100001";

    //用户列表
    public static final String URI_USER_LIST = URI_ALL + "/user/list";

    // CUBE天气
    public static final String URI_CUBE_WEATHER = URI_ALL + "/user/device/weather";
    public static final String URI_CUBE_ONLINE_STATE = URI_ALL + "/user/device/online";

    //天气列表
    public static final String URI_WEATHER_LIST = URI_ALL + "/weather/list";

    //用户
    public static final String URI_USER = URI_ALL + "/user";

    //位置列表
    public static final String URI_LOCATION_LIST = URI_ALL + "/location/list";

    //cube 位置
    public static final String URI_CUBE_LOCATION = URI_ALL + "/user/device/location";
    //手机设备列表
    public static final String URI_PHONE_LIST = URI_ALL + "/phone/list";

    //已绑定Cube 设备列表
    public static final String URI_DEVICE_LIST = URI_ALL + "/user/device/list";

    //报警记录
    public static final String URI_ALARMHISTORY = URI_ALL + "/user/alarm/history";

    //报警数量
    public static final String URI_ALARM_COUNT = URI_ALL + "/user/alarm/count";
    //CUBE 相关
    public static final String URI_CUBE = URI_ALL + "/user/device";

    //webSocket 地址信息
    public static final String URI_WEBSOCKET = "wss://" + IP_ALL + "/v1/00100001/phone/connect";

    //Socket
    public static final String TCP_IP_ADRESS = "192.168.31.243";
    public static final int TCP_IP_PORT = 9000;

    // CUBE 编码
    public static final String TestDeviceSerial = "001000015555";


    // IPC 端口
    public static final int IPC_PORT = 554;

    //Backaudio Type Int
    public static final int BackaudioTypeInt = 10;

    // 主码流
    public static final String IPC_MAIN_STREAM = "ch01.264?ptype=udp";
    public static final String IPC_STREAM_PHOENIX = "h264";

    // 辅码流
    public static final String IPC_SUB_STREAM = "ch01_sub.264?ptype=udp";
    public static final String IPC_SUB_STREAM_PHOENIX = "h264_2";

    //默认的IPVDP密码
    public static final String MAIN_IPVDP_PASSWORD = "1123445";
    //默认场景密码
    public static final String MAIN_SCENARIO_PASSWORD = "123456";


    /**
     * easy link 相关
     */
    public static int EASY_LINK_GROUP_LISTENER_TIME_INTEVAL = 2000;//组播监听线程暂停时间
    public static int EASY_LINK_SEND_GROUP = 9999;
    public static int EASY_LINK_GROUP_LISTENER_PORT = 5350;//组播监听端口 原demo是6767 iOS端是5350 到时候需要确认下

}
