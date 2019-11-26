package com.honeywell.cube.ipc;

public class Constants {
    // broadcast type / filter type. 协议Adapter发送的广播的类型，比如，配置/布撤防/控制等。
    public final static String ACTION_DEV_CONFIG = "com.honeywell.cubebase.broadcast.devconfig"; // 设备/小模块配�?
    public final static String ACTION_DEV_CONTROL = "com.honeywell.cubebase.broadcast.devcontrol";// 灯光/窗帘/空调等控�?
    public final static String ACTION_DEV_ARMCTL = "com.honeywell.cubebase.broadcast.armcontrol"; // 布防/撤防控制�?
    public final static String ACTION_DEV_ALARM = "com.honeywell.cubebase.broadcast.alarm"; // 报警�?
    public final static String ACTION_DEV_SCENARIO = "com.honeywell.cubebase.broadcast.scenario"; // scenario control
    public final static String ACTION_DEV_CALL = "com.honeywell.cubebase.broadcast.call"; // 通话
    public final static String ACTION_DEV_IPC = "com.honeywell.cubebase.broadcast.ipc"; // ipcamera
    public final static String ACTION_DEV_MISC = "com.honeywell.cubebase.broadcast.misc"; // misc
    public final static String ACTION_DEV_DISCOVERY = "com.honeywell.cubebase.broadcast.discovery"; // 发现新设备后，广播到config
    // center�?
    public final static String ACTION_DEV_ALLSERVICEREADY = "com.honeywell.cubebase.broadcast.allserviceready"; //�?有模块启动完�?
    public final static String ACTION_DEV_PHONEADDDEVICE = "com.honeywell.cubebase.broadcast.phoneadddevice"; //手机在收到小模块发现后，发�?�广播给小模块建立长连接
    public final static String ACTION_DEV_PHONEDELDEVICE = "com.honeywell.cubebase.broadcast.phonedeldevice"; //手机在收到小模块发现后，发�?�广播给小模块建立长连接
    public final static String ACTION_LOG_CUBE_SUCCESS = "com.honeywell.cubebase.broadcast.logcubesuccess";// 灯光/窗帘/空调等控�?

    // response broadcast
    public final static String ACTION_RSP_THIRDPARTY_SPARKLIGHTING = "com.honeywell.cubebase.thirdpartyadapter.sparklighting.rsp";
    public final static String ACTION_RSP_THIRDPARTY_BACNETAC = "com.honeywell.cubebase.thirdpartyadapter.bacnetac.rsp";
    public final static String ACTION_RSP_THIRDPARTY_WIFIIR = "com.honeywell.cubebase.thirdpartyadapter.wifiir.rsp";
    public final static String ACTION_RSP_THIRDPARTY_BACKAUDIO = "com.honeywell.cubebase.thirdpartyadapter.backaudio.rsp";
    //通知请求命令，小模块已经回复
    public final static String ACTION_DEV_CONTROL_RESPONSE = "com.honeywell.cubebase.broadcast.wifimodule.controlresponse";

    //notify control center, response of arm control
    public final static String ACTION_DEV_ARMCTL_REPONSE = "com.honeywell.cubebase.securitycenter.armcontrol.response";

    //wiredzone，sparklighting�?433 wireless zone 配置修改
    public final static String ACTION_DEV_MODIFYARMZONECONFIG = "com.honeywell.cubebase.broadcast.configservice.modifyarmzoneconfig";


    // Full path name for services name
    public final static String ACTION_SERVICE_LOGCENTER = "com.honeywell.cubeapp.logcenter.LogService";
    public final static String ACTION_SERVICE_JSONTCP = "com.honeywell.cube.ipc.JsonTcpService";


    // JSON协议解析：发送广播时，二进制JSON数据，对应的名字
    public final static String EXTRA_DATA_NAME = "JSONDATA";
    public final static String EXTRA_DATA_LEN = "JSONDATALEN";
    // The Json message comes from cloud or LAN
    public final static String EXTRA_DATA_FROM = "JSONDATAFROM";
    public final static String EXTRA_DATA_FROM_LAN = "DATAFROMLAN";
    public final static String EXTRA_DATA_FROM_CLOUD = "DATAFROMCLOUD";
    public final static String EXTRA_DATA_FROM_THIRDPARTY = "DATAFROMTHIRDPARTY";
    public final static String EXTRA_DATA_FROM_CUBEBASE = "DATAFROMCUBEBASE";
    public final static String EXTRA_DATA_FROM_WIFIMODULE = "DATAFROMWIFIMODULE";
    public final static String EXTRA_DATA_FROM_CONFIGSERVICE = "DATAFROMCONFIGSERVICE";
    public final static String EXTRA_DATA_FROM_SECURITYSERVICE = "DATAFROMSECURITYSERVICE";
    public final static String EXTRA_DATA_FROM_CONTROLSERVICE = "DATAFROMCONTROLSERVICE";
    public final static String EXTRA_DATA_ID = "JSONCLIENTID";

    public final static String EXTRA_DATA_IPC_DETAIL_INFO = "DATAFORIPCDETAILINFO";

    // 发现新设备：发�?�广播时，搜索到的数据序列化后传输，对应名字
    public final static String DISC_DATA = "DISCOVERYDATA";
    // 发现的新设备（小模块）�?�直接放键�?�对到intent比穿序列化对象简单�??

    //新设备手机添加后，新设备的PerrialDevice对象
    public final static String EXTRA_DEVICE_NAME = "extradevicename";
    //通知请求命令，小模块已经回复的数据的key
    public final static String EXTRA_DATA_REQUESTID = "REQUESTID";
    /**
     * ==== Cube & Phone JSON Parser filed name ====
     */

    // Field name for JSON Parser
    public static final String JSON_COMMAND_MSGID = "msgid";
    public static final String JSON_COMMAND_ACTION = "action";
    public static final String JSON_COMMAND_SUBACTION = "subaction";
    public static final String JSON_COMMAND_CUBEID = "cubeid";
    public static final String JSON_COMMAND_CUBEPWD = "cubepwd";
    public static final String JSON_COMMAND_CUBEOLDPWD = "cubeoldpwd";
    public static final String JSON_COMMAND_CLOUDID = "cloudid";
    public static final String JSON_COMMAND_CLOUDPWD = "cloudpwd";
    public static final String JSON_COMMAND_MODULETYPE = "moduletype";
    public static final String JSON_COMMAND_MODULEMACADDR = "modulemacaddr";
    public static final String JSON_COMMAND_MODULEIPADDR = "moduleipaddr";
    public static final String JSON_COMMAND_DEVICETYPE = "devicetype";
    public static final String JSON_COMMAND_ALIAS = "aliasname";
    public static final String JSON_COMMAND_ROOMNAME = "roomname";
    public static final String JSON_COMMAND_ERRORCODE = "errorcode";
    public static final String JSON_COMMAND_DEVLOOPMAP = "deviceloopmap";
    public static final String JSON_COMMAND_DEVID = "deviceid";
    public static final String JSON_COMMAND_LOOPID = "loopid";
    public static final String JSON_COMMAND_LOOPTYPE = "looptype";
    public static final String JSON_COMMAND_MASKID = "maskid";
    public static final String JSON_COMMAND_SPARKLIGHTINGDEVMAP = "sparklightingdevmap";
    public static final String JSON_COMMAND_PORTID = "portid";
    public static final String JSON_COMMAND_PORTRATE = "portrate";
    public static final String JSON_COMMAND_CUBEBACNETID = "cubebacnetid";//对应于Cube 配置表的BACNET_ID
    public static final String JSON_COMMAND_BACNETDEVID = "bacnetdeviceid";//对应于peripheral device的bacnetid
    public static final String JSON_COMMAND_BRANDNAME = "brandname";//对应于peripheral device的brandname
    public static final String JSON_COMMAND_SETTEMP = "settemp";
    public static final String JSON_COMMAND_CURRTEMP = "currenttemp";
    public static final String JSON_COMMAND_AIRCONMODE = "mode";
    public static final String JSON_COMMAND_FANSPEED = "fanspeed";
    public static final String JSON_COMMAND_SWITCHSTATUS = "status";
    public static final String JSON_COMMAND_PERCENT = "openclosepercent";
    public static final String JSON_COMMAND_ZONETYPE = "zonetype";
    public static final String JSON_COMMAND_ALARMTIMER = "alarmtimer";
    public static final String JSON_COMMAND_ALARMTYPE = "alarmtype";
    public static final String JSON_COMMAND_ALARMTIMESTAMP = "timestamp";  // time that alarm happens
    public static final String JSON_COMMAND_ARMTYPE = "armtype";
    public static final String JSON_COMMAND_RTSPURL = "rtspurl";
    public static final String JSON_COMMAND_AIR = "airtype";
    public static final String JSON_COMMAND_CURRTIME = "currenttime";
    public static final String JSON_COMMAND_CURRCO2 = "currentco2";
    public static final String JSON_COMMAND_CURRPM25 = "currentpm2_5";
    public static final String JSON_COMMAND_CURRHUMID = "currenthumid";
    public static final String JSON_COMMAND_HNSADDR = "hnsserveraddr";
    public static final String JSON_COMMAND_CALLMSG = "callmsg";
    public static final String JSON_COMMAND_CALLTYPE = "calltype";
    public static final String JSON_COMMAND_VIDEOPORT = "videoport";
    public static final String JSON_COMMAND_VIDEOCODETYPE = "videocodectype";
    public static final String JSON_COMMAND_AUDIOPORT = "audioport";
    public static final String JSON_COMMAND_AUDIOCODETYPE = "audiocodectype";
    public static final String JSON_COMMAND_VIDEORATE = "videoratio";
    public static final String JSON_COMMAND_CALLSESSIONID = "callsessionid";
    public static final String JSON_COMMAND_TAKECALLIPADDR = "takecallipaddr";
    public static final String JSON_COMMAND_DOOROPENTYPE = "dooropentype";
    public static final String JSON_COMMAND_DOOROPENWAY = "dooropenway";
    public static final String JSON_COMMAND_DOOROPENROLE = "dooropenrole";
    public static final String JSON_COMMAND_DOOROPENID = "dooropenid";
    public static final String JSON_COMMAND_IPCTYPE = "ipctype";
    public static final String JSON_COMMAND_IPCURL = "ipcurl";
    public static final String JSON_COMMAND_IPCUSERNAME = "ipcusername";
    public static final String JSON_COMMAND_IPCPWD = "ipcpassword";
    public static final String JSON_COMMAND_DISTURBTYPE = "disturbtype";
    public static final String JSON_COMMAND_KEYID = "keyid";
    public static final String JSON_COMMAND_VERSION = "version";
    public static final String JSON_COMMAND_UPGRADETYPE = "upgradetype";
    public static final String JSON_COMMAND_CONTROLTYPE = "controltype";
    public static final String JSON_COMMAND_CONFIGDATA = "configdata";
    public static final String JSON_COMMAND_CONFIGTYPE = "configtype";
    public static final String JSON_COMMAND_PORT = "port";
    public static final String JSON_COMMAND_TIMER = "time";
    public static final String JSON_COMMAND_SUBDEVTYPE = "subdevtype";
    public static final String JSON_COMMAND_MODULELOOPMAP = "moduleloopmap";
    public static final String JSON_COMMAND_ISCONFIG = "isconfig";
    public static final String JSON_COMMAND_ISONLINE = "isonline";
    public static final String JSON_COMMAND_ISENABLE = "isenable";
    public static final String JSON_COMMAND_SERIALNO = "serialnumber";
    public static final String JSON_COMMAND_SCENARIOID = "scenarioid";
    public static final String JSON_COMMAND_SECURITY_PWD = "securitypwd";
    public static final String JSON_COMMAND_ISARM = "isarm";
    public static final String JSON_COMMAND_MODULESERIALNUM = "moduleserialnum";
    public static final String JSON_COMMAND_MACHINETYPE = "machinetype";
    public static final String JSON_COMMAND_LOOPNUM = "loopnum";
    public static final String JSON_COMMAND_KEYTYPE = "keytype";
    public static final String JSON_COMMAND_KEYVALUE = "keyvalue";
    public static final String JSON_COMMAND_KEYTYPELOOP = "keytypeloop";
    public static final String JSON_COMMAND_BACKAUDIODEVMAP = "backaudiodevmap";
    public static final String JSON_COMMAND_POWER = "power";
    public static final String JSON_COMMAND_VOLUME = "volume";
    public static final String JSON_COMMAND_PLAYSTATUS = "playstatus";
    public static final String JSON_COMMAND_SINGLECYCLE = "singlecycle";
    public static final String JSON_COMMAND_SOURCE = "source";
    public static final String JSON_COMMAND_SWITCHSONG = "switchsong";
    public static final String JSON_COMMAND_SONGNAME = "songname";
    public static final String JSON_COMMAND_PLAYTIME = "playtime";
    public static final String JSON_COMMAND_ALLPLAYTIME = "allplaytime";

    public static final String JSON_COMMAND_ALARMENABLE = "alarmenable";
    public static final String JSON_COMMAND_SLAVEADDR = "slaveaddr";
    public static final String JSON_COMMAND_PWD_VERIFY = "pwdverify";
    public static final String JSON_COMMAND_SCENARIOLOOMAP = "scenarioloopmap";
    public static final String JSON_COMMAND_WIFIIRCMD = "ircommand";
    public static final String JSON_COMMAND_IRCODE = "codeloopmap";

    /* ACTION maybe values */
    public static final String JSON_COMMAND_ACTION_REQUEST = "request";
    public static final String JSON_COMMAND_ACTION_RESPONSE = "response";
    public static final String JSON_COMMAND_ACTION_EVENT = "event";

    /* SUBACTION maybe values */
    public static final String JSON_COMMAND_SUBACTION_UNKNOWN = "unknown";
    public static final String JSON_COMMAND_SUBACTION_SETDEV = "setdevice";
    public static final String JSON_COMMAND_SUBACTION_READDEV = "readdevice";
    public static final String JSON_COMMAND_SUBACTION_RELAYINFO = "relayinfo";
    public static final String JSON_COMMAND_SUBACTION_CONFIGDEV = "configdevice";
    public static final String JSON_COMMAND_SUBACTION_CONFIGMODULE = "configmodule";
    public static final String JSON_COMMAND_SUBACTION_GETDEVCONF = "getdeviceconfig";
    public static final String JSON_COMMAND_SUBACTION_DELDEV = "deletedevice";
    public static final String JSON_COMMAND_SUBACTION_ALARMINFO = "alarminfo";
    public static final String JSON_COMMAND_SUBACTION_ARM = "arm";
    public static final String JSON_COMMAND_SUBACTION_IPCMONITOR = "ipcmonitor";
    public static final String JSON_COMMAND_SUBACTION_CALL = "call";
    public static final String JSON_COMMAND_SUBACTION_UPGRADE = "upgrade";
    public static final String JSON_COMMAND_SUBACTION_DISTURB = "disturb";
    public static final String JSON_COMMAND_SUBACTION_SCENARIOCTRL = "scenariocontrol";
    public static final String JSON_COMMAND_SUBACTION_SCENARIOTRIGGER = "scenariotrigger";
    public static final String JSON_COMMAND_SUBACTION_HDMICTRL = "hdmicontrol";
    public static final String JSON_COMMAND_SUBACTION_REGISTER = "register";
    public static final String JSON_COMMAND_SUBACTION_HEARTBEAT = "heartbeat";
    public static final String JSON_COMMAND_SUBACTION_GETNEWMODULELIST = "getnewmodulelist";
    public static final String JSON_COMMAND_SUBACTION_DISCOVERMODULE = "discovermodule";

    public static final String JSON_COMMAND_SUBACTION_GETARMZONECONFIG = "getarmzoneconfig";
    public static final String JSON_COMMAND_SUBACTION_MODIFYARMZONECONFIG = "modifyarmzoneconfig";
    public static final String JSON_COMMAND_SUBACTION_GETGETSCENARIOCONFIG = "getscenarioconfig";
    public static final String JSON_COMMAND_MESSAGEID = "msgid";
    public static final String JSON_COMMAND_PWDVERIFY = "pwdverify";

    /* WIFI IR Specific command (sub-action) */
    public static final String JSON_COMMAND_ACTION_IRCMD = "ircommand";
    public static final String JSON_COMMAND_ACTION_IRDATA = "wifiirdata";
    public static final String JSON_COMMAND_ACTION_WIFIIR_SND = "send";
    public static final String JSON_COMMAND_ACTION_WIFIIR_STDY = "study";
    public static final String JSON_COMMAND_ACTION_WIFIIR_WICFG = "wificonfig";
    public static final String JSON_COMMAND_WIFIIRTYPE = "wifiirtype";
    public static final String JSON_COMMAND_WIFIIRNAME = "wifiirname";
    public static final String JSON_COMMAND_WIFIIRLOCK = "wifiirlock";
    public static final String JSON_COMMAND_WIFIIRPWD = "wifiirpassword";
    public static final String JSON_COMMAND_WIFIIRID = "wifiirid";
    public static final String JSON_COMMAND_WIFIIRSUBDEVICE = "wifiirsubdevice";
    public static final String JSON_COMMAND_WIFIIRKEY = "wifiirkey";
    public static final String JSON_COMMAND_TYPE = "type";
    public static final String JSON_COMMAND_NAME = "name";
    public static final String JSON_COMMAND_WIFIIRDATA = "wifiirdata";
    public static final String JSON_COMMAND_DATA = "data";

    public static final String JSON_COMMAND_MODULEPORT = "moduleport";
    public static final String JSON_COMMAND_MAINSTREAM = "mainstream";
    public static final String JSON_COMMAND_SUBSTREAM = "substream";

    public static final String JSON_COMMAND_DATA_P2P_UUID = "uuid";
    public static final String JSON_COMMAND_DATA_P2P_IP = "phoneip";

    /* loop type */
    public static final String LOOP_TYPE_LIGHT = "light";
    public static final String LOOP_TYPE_CURTAIN = "curtain";
    public static final String LOOP_TYPE_RELAY = "relay";
    public static final String LOOP_TYPE_SWITCH = "switch";//maia 分light 和 switch
    public static final String LOOP_TYPE_SENSOR = "sensor";
    public static final String LOOP_TYPE_5800PIRAP = "5800-PIR-AP";
    public static final String LOOP_TYPE_5804EU = "5804EU";
    public static final String LOOP_TYPE_5816EU = "5816EU";


    public static final int LOOP_TYPE_LIGHT_INT = 1;
    public static final int LOOP_TYPE_CURTAIN_INT = 2;
    public static final int LOOP_TYPE_RELAY_INT = 3;
    public static final int LOOP_TYPE_SWITCH_INT = 4;
    public static final int LOOP_TYPE_SENSOR_INT = 5;
    public static final int LOOP_TYPE_5800PIRAP_INT = 6;
    public static final int LOOP_TYPE_5804EU_INT = 7;
    public static final int LOOP_TYPE_5816EU_INT = 8;

    /* switch status type */
    public static final String SWITCHSTATUS_TYPE_ON = "on";
    public static final String SWITCHSTATUS_TYPE_OFF = "off";
    public static final String SWITCHSTATUS_TYPE_OPEN = "opening";
    public static final String SWITCHSTATUS_TYPE_CLOSE = "closing";
    public static final String SWITCHSTATUS_TYPE_STOP = "stopped";
    public static final String MODE_TYPE_COOL = "cooling";
    public static final String MODE_TYPE_HEAT = "heating";
    public static final String MODE_TYPE_VENLITATION = "ventilation";
    public static final String MODE_TYPE_AUTO = "auto";
    public static final String MODE_TYPE_DEHUMIDIFY = "dehumidifying";

    /* aircon fan speed */
    public static final String FAN_SPEED_LOW = "low";
    public static final String FAN_SPEED_MIDDLE = "middle";
    public static final String FAN_SPEED_HIGH = "high";

    public static final String BACKAUDIO_LOOP_POWER_ON = SWITCHSTATUS_TYPE_ON;
    public static final String BACKAUDIO_LOOP_POWER_OFF = SWITCHSTATUS_TYPE_OFF;
    public static final String BACKAUDIO_LOOP_PLAYSTATUS_PLAY = "play";
    public static final String BACKAUDIO_LOOP_PLAYSTATUS_PAUSE = "pause";
    public static final String BACKAUDIO_LOOP_SINGLECYCLE_ON = SWITCHSTATUS_TYPE_ON;
    public static final String BACKAUDIO_LOOP_SINGLECYCLE_OFF = SWITCHSTATUS_TYPE_OFF;
    public static final String BACKAUDIO_LOOP_SOURCE_MP3 = "mp3";
    public static final String BACKAUDIO_LOOP_SOURCE_FM2 = "fm2";
    public static final String BACKAUDIO_LOOP_SOURCE_FM = "fm";
    public static final String BACKAUDIO_LOOP_SOURCE_DVD = "dvd";
    public static final String BACKAUDIO_LOOP_SOURCE_AUX = "aux";
    public static final String BACKAUDIO_LOOP_SOURCE_IPOD = "ipod";
    public static final String BACKAUDIO_LOOP_SOURCE_NETRADIO = "netradio";
    public static final String BACKAUDIO_LOOP_SOURCE_CLOUDMUSIC = "cloudmusic";
    public static final String BACKAUDIO_LOOP_SWITCHSONG_PRE = "previous";
    public static final String BACKAUDIO_LOOP_SWITCHSONG_NEXT = "next";

    /**
     * Zone status detail types
     */

    // general alarms 
    public static final String ZONE_ALARM_STATUS_GENERAL_NORMAL = "normal";
    public static final String ZONE_ALARM_STATUS_GENERAL_TEMPER = "temper";
    public static final String ZONE_ALARM_STATUS_GENERAL_SUPERVISION = "supervision";
    public static final String ZONE_ALARM_STATUS_GENERAL_LOWBATTR = "lowbattery";
    public static final String ZONE_ALARM_STATUS_GENERAL_INTRUSION = "intrusion";
    public static final String ZONE_ALARM_STATUS_GENERAL_ZONETROUBLE = "zonetrouble";
    public static final String ZONE_ALARM_STATUS_GENERAL_PM2P5 = "pm2.5limit";
    public static final String ZONE_ALARM_STATUS_GENERAL_MAGNETIC = "door_magnetic";//门磁
    public static final String ZONE_ALARM_STATUS_GENERAL_HEARTHEAT = "heartheat";//
    public static final String ZONE_ALARM_STATUS_GENERAL_RESET = "reset";//sensor has been reset

    // ALARMS from ipvdp system
    public static final String ZONE_ALARM_STATUS_IPVDP_GAS = "gas";
    public static final String ZONE_ALARM_STATUS_IPVDP_FIRE = "fire";
    public static final String ZONE_ALARM_STATUS_IPVDP_EMERGENCY = "emergency";
    public static final String ZONE_ALARM_STATUS_IPVDP_MEDICALAID = "medicalaid";
    public static final String ZONE_ALARM_STATUS_IPVDP_LOCKTEMPER = ZONE_ALARM_STATUS_GENERAL_TEMPER;
    public static final String ZONE_ALARM_STATUS_IPVDP_LOCKLOWBATTR = ZONE_ALARM_STATUS_GENERAL_LOWBATTR;
    public static final String ZONE_ALARM_STATUS_IPVDP_LOCK_SUPERVISION = ZONE_ALARM_STATUS_GENERAL_SUPERVISION;
    public static final String ZONE_ALARM_STATUS_IPVDP_LOCK_MAXWRONGPWD = "wrongpwdlimited";
    public static final String ZONE_ALARM_STATUS_IPVDP_DOOR_OPEN = "dooropen";
    public static final String ZONE_ALARM_STATUS_IPVDP_DOOR_CLOSE = "doorclose";

    // Alarms from wired zone
    public static final String ZONE_ALARM_STATUS_WIRED_HIGH = "open";
    public static final String ZONE_ALARM_STATUS_WIRED_LOW = "close";

    // Alarms from wireless zone 
    public static final String ZONE_ALARM_STATUS_WIRELESS_LOWBATTR = ZONE_ALARM_STATUS_GENERAL_LOWBATTR;
    public static final String ZONE_ALARM_STATUS_WIRELESS_TEMPER = ZONE_ALARM_STATUS_GENERAL_TEMPER;


    public static final String ZONE_ALARM_TYPE_INSTANT = "instant";
    public static final String ZONE_ALARM_TYPE_24HOURS = "24hours";
    public static final String ZONE_ALARM_TYPE_DELAY = "delay";
    public static final String ZONE_ALARM_TYPE_SCENARIO = "scenario";
    /**
     * ERROR CODEs
     **/
    // general error codes
    public final static int MESSAGE_ERROR_CODE_OK = 0;
    public final static int MESSAGE_ERROR_CODE_INVALID_PARAM = 1;
    public final static int MESSAGE_ERROR_CODE_TIMEOUT = 2;
    // auth
    public final static int MESSAGE_ERROR_CODE_AUTH_AUTHED = 100;  // already authed
    public final static int MESSAGE_ERROR_CODE_AUTH_WRONGPWD = 101; // UserID or Password error
    public final static int MESSAGE_ERROR_CODE_AUTH_REQUIRED = 102; // required for auth

    // sparklighting specified errors
    public final static int MESSAGE_ERROR_CODE_NO_LOOP = 200; // No the loop 
    public final static int MESSAGE_ERROR_CODE_EXIST_LOOP = 202; // The loop has already exist.
    public final static int MESSAGE_ERROR_CODE_EXIST_DEVICE = MESSAGE_ERROR_CODE_EXIST_LOOP; //
    public final static int MESSAGE_ERROR_CODE_OPERATION_FAIL = 203; // This operation failed.

    // security center service error code
    public final static int MESSAGE_ERROR_CODE_SECURITY_ZONE_ABNORMAL = 300; // zone abnormal when arm
    public final static int MESSAGE_ERROR_CODE_SECURITY_PWD_INVALID = 301; // pwd wrong when disarm
    public final static int MESSAGE_ERROR_CODE_SECURITY_NOSUCH_SCENARIO = 302; // can't find specified scenario

    /**
     * ports for tcp/udp communication
     **/

    // sparklighting system udp communication port
    public static final int DEFAULT_PORT_SPARKLIGHING = 6000;
    public static final int DEFAULT_PORT_TCPMODULE = 9000;
    public static final int DEFAULT_PORT = 9000;
    public static final int DEFAULT_BACNETID = -1;
    public static final int LOG_LEVEL_DEBUG = 0;
    public static final int LOG_LEVEL_WARNING = 1;
    public static final int LOG_LEVEL_ERROR = 2;

    /* module type maybe values */
    public static final String JSON_COMMAND_MODULETYPE_485 = "485";
    public static final String JSON_COMMAND_MODULETYPE_BACNET = "bacnet";
    public static final String JSON_COMMAND_MODULETYPE_SPARKLIGHT = "sparklighting";
    public static final String JSON_COMMAND_MODULETYPE_WIREDZONE = "wiredzone";
    public static final String JSON_COMMAND_MODULETYPE_315N433 = "315M433M";
    public static final String JSON_COMMAND_MODULETYPE_IPC = "ipc";
    public static final String JSON_COMMAND_MODULETYPE_IR = "ir";
    public static final String JSON_COMMAND_MODULETYPE_RELAY = "relay";
    public static final String JSON_COMMAND_MODULETYPE_AIR = "air";
    public static final String JSON_COMMAND_MODULETYPE_IPVDP = "ipvdp";
    public static final String JSON_COMMAND_MODULETYPE_NFC = "nfc";
    public static final String JSON_COMMAND_MODULETYPE_BACKAUDIO = "backaudio";
    public static final String JSON_COMMAND_MODULETYPE_CUBE = "cube";
    public static final String JSON_COMMAND_MODULETYPE_DIGITALLOCK = "digitallock";
    public static final String JSON_COMMAND_MODULETYPE_ELEVATOR = "elevator";
    public static final String JSON_COMMAND_MODULETYPE_SCENARIO = "scenario";
    public static final String JSON_COMMAND_MODULETYPE_ALARMZONE = "alarmzone";

    //for wifi module type value(5~10为wifi module)
    public static final int MODULE_TYPE_SPARKLIGHTING = 1;
    public static final int MODULE_TYPE_BACNET = 2;
    public static final int MODULE_TYPE_IPC = 3;
    public static final int MODULE_TYPE_WIFIIR = 4;
    public static final int MODULE_TYPE_WIFIAIR = 5;
    public static final int MODULE_TYPE_WIFIRELAY = 6;
    public static final int MODULE_TYPE_WIFI485 = 7;
    public static final int MODULE_TYPE_WIREDZONE = 8;
    public static final int MODULE_TYPE_WIFI315M433M = 9;
    public static final int MODULE_TYPE_IPVDP = 10;
    public static final int MODULE_TYPE_SCENARIO = 11;
    public static final int MODULE_TYPE_ALARMZONE = 12;
    public static final int MODULE_TYPE_BACKAUDIO = 13;

    //configtype
    public static final String CONFIG_TYPE_ADD = "add";
    public static final String CONFIG_TYPE_DEL = "delete";
    public static final String CONFIG_TYPE_MODIFY = "modify";

    public static final String PERIPHERALDEVICE = "peripheraldevice";

    public static final String IPCSTREAMINFO = "ipcstreaminfo";
    public static final String IRINFO = "irinfo";
    public static final String SPARKLIGHTINGLOOP = "sparklightingloopmap";
    public static final String RELAYLOOP = "relayloopmap";
    public static final String BACNETLOOP = "bacnetloopmap";
    public static final String WIREDZONELOOP = "wiredzoneloopmap";
    public static final String WIRELESS315M433MLOOP = "wireless315m433mloop";

    public static final String WIFI485LOOP = "485loopmap";
    public static final String ALARMZONELOOP = "alarmzoneloopmap";
    public static final String MAIALOOP = "maialoopmap";

    public static final String SCENARIOLOOP = "scenarioloopmap";
    public static final String IRLOOP = "irloopmap";
    public static final String BACKAUDIOLOOP = "backaudioloopmap";

    public static final int HASCONFIG = 1;
    public static final int NOTCONFIG = 0;

    public static final int ONLINE = 1;
    public static final int NOTONLINE = 0;


    public static final String ARM_TYPE_DISABLE_STR = "off";
    public static final String ARM_TYPE_ENABLE_STR = "on";

    public static final int ARM_TYPE_DISABLE = 0;
    public static final int ARM_TYPE_ENABLE = 1;

    public static final String ARM_TYPE_ARM_STR = "arm";
    public static final String ARM_TYPE_DISARM_STR = "disarm";
    public static final int SENSOR_KEY_ARM_ALL = 0x12;
    public static final int SENSOR_KEY_DISARM_ALL = 0x22;
    public static final int SENSOR_KEY_PERIMETER = 0x42;
    public static final int SENSOR_KEY_EMERGENCY = 0x82;
    public static final int SENSOR_ALARM_TAMPER = (1 << 1);
    public static final int SENSOR_ALARM_MAGNETIC = (1 << 2);
    public static final int SENSOR_ALARM_LOWBATTERY = (1 << 4);
    public static final int SENSOR_ALARM_HEARTHEAT = (1 << 5);
    public static final int SENSOR_ALARM_RESET = (1 << 6);

    //for security center comminicate with logceter (alarm log add)
    public final static String ALARMLOG_MODULETYPE = JSON_COMMAND_MODULETYPE;//string,
    public final static String ALARMLOG_MODULEADDR = "moduleaddr";//str,ip or mac
    public final static String ALARMLOG_LOOPID = JSON_COMMAND_LOOPID;//str
    public final static String ALARMLOG_ALARMINFO = "alarminfo";//str
    public final static String ALARMLOG_TIMESTAMP = "timestamp";//long
    public final static String ALARMLOG_ROOMNAME = JSON_COMMAND_ROOMNAME;//str
    public final static String ALARMLOG_LOOPNAME = JSON_COMMAND_ALIAS;//str

    //for all sparklighting sub_dev_type
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSD0403 = "HBLS-D0403";//string,
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSD0602 = "HBLS-D0602";//string,   
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSD0610DC = "HBLS-D0610-DC";//string, 
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSD0605_LED = "HBLS-D0605-LED";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSC02 = "HBLS-C02";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSR0410 = "HBLS-R0410";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSR0810 = "HBLS-R0810";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSR1210 = "HBLS-R1210";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSI04LED = "HBLS-I04-LED";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSI08 = "HBLS-I08";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSSIR = "HBLS-SIR";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSSTLA = "HBLS-STLA";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSGW = "HBLS-GW";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSP750 = "HBLS-P750";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSP2400 = "HBLS-P2400";
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSSoftware = "HBLS-Software";

    public final static String DEVICETYPE_MAIA2 = "maia2";
    public final static String DEVICETYPE_SENSOR = "sensor";
    public final static String DEVICETYPE_AIRCONDITION = "aircondition";
    public final static String DEVICETYPE_THERMOSTAT = "thermostat";
    public final static String DEVICETYPE_VENTILATION = "ventilation";

    public static final String FIRST_SEPARATOR = ":";
    public static final String SECOND_SEPARATOR = ",";

    public final static int SCENARIO_ID_HOME = 1;
    public final static int SCENARIO_ID_LEAVE = 2;
    public final static int SCENARIO_ID_ARMALL = 3;
    public final static int SCENARIO_ID_DISARMALL = 4;

    public final static String SCENARIO_HOME_NAME = "home";
    public final static String SCENARIO_LEAVE_NAME = "leave";
    public final static String SCENARIO_ARMALL_NAME = "armall";
    public final static String SCENARIO_DISARMALL_NAME = "disarmall";

    public final static int SCENARIO_ID_BASIC = 100;
    public final static int IRLOOPID_BASIC = 0;
    public final static String DEFAULTSECURITYPWD = "1123445";

    public final static String DEFAULTIPCUSER = "admin";
    public final static String DEFAULTIPCPWD = DEFAULTSECURITYPWD;
    public final static int DEFAULTIPCSTREAMPORT = 554;
    public final static String IPC_MAIN_STREAM_SUPPER_HD = "ch01.264?ptype=udp";
    public final static String IPC_SUB_STREAM_SUPPER_HD = "ch01_sub.264?ptype=udp";
    public final static String IPC_MAIN_STREAM_PHOENIX = "h264";
    public final static String IPC_SUB_STREAM_PHOENIX = "h264_2";

    // const value for handler
    public final static int IPC_HANDLER_FRAME_CNT = 1;

    // SD卡存储路径
    public final static String APPSDCARDDIR = "CubeApp";
    public final static int ALPHA_DIM = 100;
    public final static int ALPHA_BRIGHT = 255;

    public static final int CALL_STATE_INCOMING = 0;
    public static final int CALL_STATE_ANSWERED = CALL_STATE_INCOMING + 1;
    public static final int CALL_STATE_END = CALL_STATE_ANSWERED + 1;
    public static final String EXTRA_DATA_CALLMSG = "extra_data_callmsg";
    public final static String CALL_TYPE_LOBBYPHONE = "lobby";
    public final static String CALL_TYPE_LOBBYPHONE3 = "lobby3";
    public final static String CALL_TYPE_OFFICEPHONE = "officephone";
    public final static String CALL_TYPE_GUARDPHONE = "guardphone";
    public final static String CALL_TYPE_PCOFFICE = "pcoffice";
    public final static String CALL_TYPE_PCOFFICE3 = "pcoffice3";
    public final static String CALL_TYPE_PCGUARD = "pcguard";
    public final static String CALL_TYPE_PCGUARD3 = "pcguard3";
    public final static String CALL_TYPE_DOORCAMERAFRONT = "doorcamerafront";
    public final static String CALL_TYPE_DOORCAMERABACK = "doorcameraback";
    public final static String CALL_TYPE_DOORCAMERAFRONT3 = "doorcamerafront3";
    public final static String CALL_TYPE_DOORCAMERABACK3 = "doorcameraback3";
    public final static String CALL_TYPE_NEIGHBOUR = "neighbour";
    public final static String TYPE_CALL_EXTENSION = "extension";
    public final static String CALL_MSG_INCOMING_CALL = "incomingcall";
    public final static String CALL_MSG_TAKE_CALL = "takecall";
    public final static String CALL_MSG_TERMINATE_CALL = "terminatecall";
    public final static String CALL_MSG_CANCEL_CALL = "cancelcall";
    public final static String CALL_MSG_ACK = "ack";
    public final static String CALL_MSG_INFO = "info";
    public final static String CALL_MSG_OPEN_DOOR = "opendoor";
    public final static String CALL_MSG_ELEVATOR = "elevator";
    public final static String CALL_MSG_COMMONRSP = "commonrsp";
    public static final String ADD_MODULE_INFO = "add_module_info";
    public static final String EXTRA_DATA_SPARKLIGHT_LOOPTYPE = "sparklighting_loop_type";

    public class DiscDev {
        public final static String devName = "DISDEVNAME";
        public final static String devIP = "DISDEVIP";
        public final static String devMAC = "DISDEVMAC";
        public final static String devPort = "DISDEVPORT";
        public final static String devMaf = "DISDEVMAF";
        public final static String devVer = "DISDEVVER";
    }

    public static final String MASTERBEDROOM = "masterbedroom";
    public static final String SECONDBEDROOM = "secondbedroom";
    public static final String GUESTBEDROOM = "guestbedroom";
    public static final String KITCHEN = "kitchen";
    public static final String DININGROOM = "diningroom";
    public static final String LIVINGROOM = "livingroom";
    public static final String MASTERBATHROOM = "masterbathroom";
    public static final String SECONDBATHROOM = "secondbathroom";

    // Similar to __FILE__ and __LINE__ in "C"
    public static String FILE_() {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return ste.getFileName();
    }

    public static int LINE_() {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return ste.getLineNumber();
    }


}