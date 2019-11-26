package com.honeywell.cube.common;

/**
 * Created by H157925 on 16/4/8.
 */
public class CommonData {

    // user password fileName
    public final static String APP_LOGIN_VIEW_IS_ADDED = "app_login_view_is_added";
    public final static String APP_USER_PASSWORD_FILENAME = "app_user_password_file";


    // wiredzone，sparklighting，433 wireless zone 配置修改
    public final static String ACTION_DEV_MODIFYARMZONECONFIG = "com.honeywell.cubebase.broadcast.configservice.modifyarmzoneconfig";
    public final static String ACTION_DEV_MODIFYTRIGGERRULECONFIG = "com.honeywell.cubebase.broadcast.configservice.modifytriggerruleconfig";
    public final static String ACTION_DEV_MODIFYMUTEXRULECONFIG = "com.honeywell.cubebase.broadcast.configservice.modifymutexruleconfig";
    public final static String ACTION_DEV_MODIFYSCHEDULERULECONFIG = "com.honeywell.cubebase.broadcast.configservice.modifyscheduleruleconfig";

    // JSON协议解析：发送广播时，二进制JSON数据，对应的名字
    public final static String ACTION_SCHEDULE_SYSTEM_ALARM = "com.honeywell.cubebase.configcenter.schedule";
    public final static String EXTRA_DATA_NAME = "JSONDATA";
    public final static String EXTRA_DATA_LEN = "JSONDATALEN";
    // The Json message comes from cloud or LAN
    public final static String EXTRA_DATA_FROM = "JSONDATAFROM";
    public final static String EXTRA_DATA_FROM_LAN = "DATAFROMLAN";
    public final static String EXTRA_DATA_FROM_CLOUD = "DATAFROMCLOUD";
    public final static String EXTRA_DATA_FROM_THIRDPARTY = "DATAFROMTHIRDPARTY";
    public final static String EXTRA_DATA_FROM_WIFIMODULE = "DATAFROMWIFIMODULE";
    public final static String EXTRA_DATA_FROM_CONFIGSERVICE = "DATAFROMCONFIGSERVICE";
    public final static String EXTRA_DATA_FROM_SECURITYSERVICE = "DATAFROMSECURITYSERVICE";
    public final static String EXTRA_DATA_FROM_CONTROLSERVICE = "DATAFROMCONTROLSERVICE";
    public final static String EXTRA_DATA_FROM_EXTENSIONTELSERVICE = "DATAFROMEXTENSIONTELSERVICE";
    public final static String EXTRA_DATA_FROM_VOICERECGONIZE = "DATAFROMVOICERECGONIZE";
    public final static String EXTRA_DATA_ID = "JSONCLIENTID";


    //Cube database info
    public static final String APPDATABASEFILE = "cube_database_file";
    public static final int APPDATABASEVersion = 1;


    public static final int ONLINE = 1;
    public static final int NOTONLINE = 0;

    public static final int ARM_TYPE_DISABLE = 0;
    public static final int ARM_TYPE_ENABLE = 1;

    public static final int HASCONFIG = 1;
    public static final int NOTCONFIG = 0;

    public static final String ARM_TYPE_DISABLE_STR = "off";
    public static final String ARM_TYPE_ENABLE_STR = "on";

    public static final String ARM_TYPE_ARM_STR = "arm";
    public static final String ARM_TYPE_DISARM_STR = "disarm";

    /**
     * Field name for JSON Parser
     */
    //AppInfo Login responce
    public static final String JSON_LOGIN_RESPONCE_PHONEID = "phoneId";
    public static final String JSON_HTTP_SET_USER_COOKIE = "Set-Cookie";

    //cookie for websocket
    public static final String JSON_WEBSOCKET_USER_COOKIE = "Cookie";
    public static final String JSON_WEBSOCKET_USER_ORIGIN = "Origin";


    //Login get bind cube
    public static final String JSON_LOGIN_GET_CUBE_RESPONCE_DEVICES = "devices";
    public static final String JSON_LOGIN_GET_CUBE_RESPONCE_DEVICEID = "deviceId";
    public static final String JSON_LOGIN_GET_CUBE_RESPONCE_DEVICEINFO = "deviceInfo";
    public static final String JSON_LOGIN_GET_CUBE_RESPONCE_DEVICE_CUBE_SERIAL_NUMBER = "serialnumber";


    public static final String JSON_COMMAND_MSGID = "msgid";
    public static final String JSON_COMMAND_ACTION = "action";
    public static final String JSON_COMMAND_ACTIONINFO = "actioninfo";
    public static final String JSON_COMMAND_SUBACTION = "subaction";
    public static final String JSON_COMMAND_CUBEID = "cubeid";
    public static final String JSON_COMMAND_CUBEPWD = "cubepwd";
    public static final String JSON_COMMAND_CUBEOLDPWD = "cubeoldpwd";
    public static final String JSON_COMMAND_CLOUDID = "cloudid";
    public static final String JSON_COMMAND_CLOUDPWD = "cloudpwd";
    public static final String JSON_COMMAND_MODULETYPE = "moduletype";
    public static final String JSON_COMMAND_MODULEMACADDR = "modulemacaddr";
    public static final String JSON_COMMAND_MODULEIPADDR = "moduleipaddr";
    public static final String JSON_COMMAND_MODULEPORT = "moduleport";
    public static final String JSON_COMMAND_DEVICETYPE = "devicetype";
    public static final String JSON_COMMAND_ALIAS = "aliasname";
    public static final String JSON_COMMAND_ROOMNAME = "roomname";
    public static final String JSON_COMMAND_ROOMID = "roomid";

    public static final String JSON_COMMAND_ERRORCODE = "errorcode";
    public static final String JSON_COMMAND_DEVLOOPMAP = "deviceloopmap";
    public static final String JSON_COMMAND_DEVID = "deviceid";
    public static final String JSON_COMMAND_LOOPID = "loopid";
    public static final String JSON_COMMAND_LOOPTYPE = "looptype";
    public static final String JSON_COMMAND_MASKID = "maskid";
    public static final String JSON_COMMAND_SPARKLIGHTINGDEVMAP = "sparklightingdevmap";
    public static final String JSON_COMMAND_PORTID = "portid";
    public static final String JSON_COMMAND_PORTRATE = "portrate";
    public static final String JSON_COMMAND_CUBEBACNETID = "cubebacnetid";// 对应于Cube配置表的BACNET_ID
    public static final String JSON_COMMAND_BACNETDEVID = "bacnetdeviceid";// 对应于peripheral device的bacnetid
    public static final String JSON_COMMAND_BRANDNAME = "brandname";// 对应于peripheral device的brandname
    public static final String JSON_COMMAND_SETTEMP = "settemp";
    public static final String JSON_COMMAND_CURRTEMP = "currenttemp";
    public static final String JSON_COMMAND_AIRCONMODE = "mode";
    public static final String JSON_COMMAND_FANSPEED = "fanspeed";
    public static final String JSON_COMMAND_SWITCHSTATUS = "status";
    public static final String JSON_COMMAND_PERCENT = "openclosepercent";
    public static final String JSON_COMMAND_ZONETYPE = "zonetype";
    public static final String JSON_COMMAND_ALARMTIMER = "alarmtimer";
    public static final String JSON_COMMAND_ALARMTYPE = "alarmtype";
    public static final String JSON_COMMAND_ALARMTIMESTAMP = "timestamp"; // time that alarm happens
    public static final String JSON_COMMAND_ARMTYPE = "armtype";
    public static final String JSON_COMMAND_RTSPURL = "rtspurl";
    public static final String JSON_COMMAND_AIR = "airtype";
    public static final String JSON_COMMAND_CURRTIME = "currenttime";
    public static final String JSON_COMMAND_CURRCO2 = "currentco2";
    public static final String JSON_COMMAND_CURRPM25 = "currentpm2_5";
    public static final String JSON_COMMAND_CURRHUMID = "currenthumid";
    public static final String JSON_COMMAND_CURRAIRQUAL = "currentairqual";
    public static final String JSON_COMMAND_CURRAIRLUMI = "currentluminance";
    public static final String JSON_COMMAND_HNSADDR = "hnsserveraddr";
    public static final String JSON_COMMAND_CALLMSG = "callmsg";
    public static final String JSON_COMMAND_CALLTYPE = "calltype";
    public static final String JSON_COMMAND_RESPONSECMD = "responsecmd";
    public static final String JSON_COMMAND_VIDEOPORT = "videoport";
    public static final String JSON_COMMAND_AUDIOPORT = "audioport";
    public static final String JSON_COMMAND_VIDEOCODECTYPE = "videocodectype";
    public static final String JSON_COMMAND_AUDIOCODECTYPE = "audiocodectype";
    public static final String JSON_COMMAND_VIDEORATIO = "videoratio";
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
    public static final String JSON_COMMAND_URL = "url";
    public static final String JSON_COMMAND_UPGRADECMD = "upgradecmd";
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
    public static final String JSON_COMMAND_MUTE = "mute";
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
    public static final String JSON_COMMAND_ROOMLOOPMAP = "roomloopmap";
    public static final String JSON_COMMAND_WIFIIRCMD = "ircommand";
    public static final String JSON_COMMAND_IRCODE = "codeloopmap";
    public static final String JSON_COMMAND_UUID = "uuid";
    public static final String JSON_COMMAND_CALLSESSIONID = "callsessionid";
    public static final String JSON_COMMAND_PUSHEXCLUDES = "excludefds";
    public static final String JSON_COMMAND_DESCRITPION = "description";
    public static final String JSON_COMMAND_CONDITION = "condition";
    public static final String JSON_COMMAND_VALUE = "value";

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
    public static final String JSON_COMMAND_SUBACTION_DEVICEINFO = "deviceinfo";
    public static final String JSON_COMMAND_SUBACTION_CONFIGMODULE = "configmodule";
    public static final String JSON_COMMAND_SUBACTION_GETDEVCONF = "getdeviceconfig";
    public static final String JSON_COMMAND_SUBACTION_GETDEVCONFIPVDP = "getdeviceconfigipvdp";
    public static final String JSON_COMMAND_SUBACTION_DELDEV = "deletedevice";
    public static final String JSON_COMMAND_SUBACTION_ALARMINFO = "alarminfo";
    public static final String JSON_COMMAND_SUBACTION_EVIROINFO = "enviroinfo";
    public static final String JSON_COMMAND_SUBACTION_ARM = "arm";
    public static final String JSON_COMMAND_SUBACTION_IPCMONITOR = "ipcmonitor";
    public static final String JSON_COMMAND_SUBACTION_CALL = "call";
    public static final String JSON_COMMAND_SUBACTION_BACKUPCONF = "configbackup";
    public static final String JSON_COMMAND_SUBACTION_REVERTCONF = "configrecovery";
    public static final String JSON_COMMAND_SUBACTION_DISTURB = "disturb";
    public static final String JSON_COMMAND_SUBACTION_SCENARIOCTRL = "scenariocontrol";
    public static final String JSON_COMMAND_SUBACTION_TRIGGER = "trigger";
    public static final String JSON_COMMAND_SUBACTION_HDMICTRL = "hdmicontrol";
    public static final String JSON_COMMAND_SUBACTION_REGISTER = "register";
    public static final String JSON_COMMAND_SUBACTION_HEARTBEAT = "heartbeat";
    public static final String JSON_COMMAND_SUBACTION_GETNEWMODULELIST = "getnewmodulelist";
    public static final String JSON_COMMAND_SUBACTION_DISCOVERMODULE = "discovermodule";
    public static final String JSON_COMMAND_SUBACTION_BACKAUDIOINFO = "backaudioinfo";
    public static final String JSON_COMMAND_SUBACTION_CONFIGSECURITY = "configsecurity";


    public static final String JSON_COMMAND_SUBACTION_GETARMZONECONFIG = "getarmzoneconfig";
    public static final String JSON_COMMAND_SUBACTION_MODIFYARMZONECONFIG = "modifyarmzoneconfig";
    public static final String JSON_COMMAND_SUBACTION_GETGETSCENARIOCONFIG = "getscenarioconfig";
    public static final String JSON_COMMAND_SUBACTION_GETTRIGGERRULECONFIG = "gettriggerruleconfig";
    public static final String JSON_COMMAND_SUBACTION_GETALLTRIGGERRULECONFIG = "getalltriggerruleconfig";
    public static final String JSON_COMMAND_SUBACTION_GETALLMUTEXRULECONFIG = "getallmutexruleconfig";
    public static final String JSON_COMMAND_SUBACTION_GETMUTEXRULECONFIG = "getmutexruleconfig";
    public static final String JSON_COMMAND_SUBACTION_GETSCHEDULERULECONFIG = "getscheduleruleconfig";
    public static final String JSON_COMMAND_SUBACTION_SUBPHONEINFO = "subphoneinfo";
    public static final String JSON_COMMAND_SUBACTION_CUBEDEVEVENT = "cubedevevent";
    public static final String JSON_COMMAND_SUBACTION_UPGRADE = "upgrade";
    public static final String JSON_COMMAND_SUBACTION_SYSTEMSECURITYSTATE = "systemsecuritystate";

    public static final String JSON_COMMAND_MESSAGEID = "msgid";
    public static final String JSON_COMMAND_PWDVERIFY = "pwdverify";

    /* WIFI IR Specific command (sub-action) */
    public static final String JSON_COMMAND_ACTION_IRCMD = "ircommand";
    // public static final String JSON_COMMAND_ACTION_IRDATA = "wifiirdata";
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
    public static final String JSON_COMMAND_IMAGENAME = "imagename";
    public static final String JSON_COMMAND_WIFIIRDATA = "wifiirdata";
    public static final String JSON_COMMAND_DATA = "data";
    public static final String JSON_COMMAND_WIFI_SSID = "wifissid";
    public static final String JSON_COMMAND_WIFI_PWD = "wifipassword";
    public static final String JSON_COMMAND_THRD_IR_SUBSERVICE = "WiFiIRSubService";
    public static final String JSON_COMMAND_THRD_IR_CONTROL = "WIFIIRControl";
    public static final String JSON_COMMAND_REMOTECONTROL_ID = "remotecontrolid";

    /* IPC Specific command */
    public static final String JSON_COMMAND_ACTION_IPCCMD = "IPCCMD";
    public static final String JSON_COMMAND_ACTION_IPCREC_START = "StartRecord";
    public static final String JSON_COMMAND_ACTION_IPCREC_STOP = "StopRecord";
    public static final String JSON_COMMAND_ACTION_IPCDISP_START = "StartRemoteDisplay";
    public static final String JSON_COMMAND_ACTION_IPCDISP_STOP = "StopRemoteDisplay";
    public static final String JSON_COMMAND_ACTION_IPC_GETINFO = "param";
    public static final String JSON_COMMAND_DATA_IPC_IP = JSON_COMMAND_MODULEIPADDR;
    public static final String JSON_COMMAND_DATA_IPC_PORT = JSON_COMMAND_MODULEPORT;
    public static final String JSON_COMMAND_DATA_IPC_URL = JSON_COMMAND_IPCURL;
    public static final String JSON_COMMAND_DATA_IPC_RTTYPE = "RemoteType";
    public static final String JSON_COMMAND_DATA_P2P_UUID = "uuid";
    public static final String JSON_COMMAND_DATA_P2P_IP = "phoneip";
    public static final String JSON_COMMAND_DATA_IPC_VWIDTH = "VideoWidth";
    public static final String JSON_COMMAND_DATA_IPC_VHEIGHT = "VideoHeight";
    public static final String JSON_COMMAND_DATA_IPC_VCODEC = "CodecType";
    public static final String JSON_COMMAND_DATA_IPC_RECTIME = "RecordTime";
    public static final String JSON_COMMAND_MAINSTREAM = "mainstream";
    public static final String JSON_COMMAND_SUBSTREAM = "substream";
    public static final String JSON_COMMAND_AVAILABLETIME = "availabletime";
    public static final String JSON_COMMAND_DELAYTIME = "delaytime";
    public static final String JSON_COMMAND_STARTTIME = "starttime";
    public static final String JSON_COMMAND_ENDTIME = "endtime";
    public static final String JSON_COMMAND_FREQUENCY = "frequency";
    public static final String JSON_COMMAND_TRIGGERTYPE = "triggertype";
    public static final String JSON_COMMAND_CUSTOMIZEDAYS = "customizedays";
    public static final String JSON_COMMAND_TRIGGERNAME = JSON_COMMAND_ALIAS;
    public static final String JSON_COMMAND_TRIGGERDESCRIPTION = JSON_COMMAND_DESCRITPION;
    public static final String JSON_COMMAND_TRIGGERCONDITION = JSON_COMMAND_CONDITION;
    public static final String JSON_COMMAND_SCHEDULEACTION = "scheduleaction";
    public static final String JSON_COMMAND_TRIGGERACTION = "triggeraction";
    public static final String JSON_COMMAND_TRIGGERID = "triggerid";
    public static final String JSON_COMMAND_SCHEDULEID = "scheduleid";
    public static final String JSON_COMMAND_MUTEXID = "mutexid";

    // upgrade commands
    public static final String JSON_COMMAND_UPGRADE_GETPACK = "getpackage";
    public static final String JSON_COMMAND_UPGRADE_NEWVER = "newversion";
    public static final String JSON_COMMAND_UPGRADE_START = "startupgrade";
    public static final String JSON_COMMAND_UPGRADE_QUERY = "queryupgrade";
    public static final String JSON_COMMAND_UPGRADE_GETVER = "getversion";

    // backup
    // public static final String JSON_COMMON_DATABASE_FILE = "cubeconfig.db";
    public static final String JSON_COMMAND_BACKUP_RETENTION = "retention";
    public static final String JSON_COMMAND_BACKUP_CREATE = "createbackup";
    public static final String JSON_COMMAND_BACKUP_ID = "dataid";

    public static final String JSON_COMMAND_CONTROLMAP = "controlmap";

    public static final String JSON_COMMAND_PRIMARYID = "primaryid";
    public static final String JSON_COMMAND_RESPONSEPRIMARYID = "responseprimaryid";
    public static final String JSON_COMMAND_SUBRESPONSEPRIMARYID = "subresponseprimaryid";

    public static final String JSON_COMMAND_CURRAIRQUAL_INVALID = "invalid";
    public static final String JSON_COMMAND_CURRAIRQUAL_CLEAN = "clean";
    public static final String JSON_COMMAND_CURRAIRQUAL_SLIGHT = "slight";
    public static final String JSON_COMMAND_CURRAIRQUAL_MODERATE = "moderate";
    public static final String JSON_COMMAND_CURRAIRQUAL_SERIOUS = "serious";

    public static final String JSON_COMMAND_PORTMAP = "portmap";
    public static final String JSON_COMMAND_CUBEDEV_EVENT_TYPE = "eventtype";
    public static final String JSON_COMMAND_DATA_SDCARD_IN = "sdcardin";   //sd
    public static final String JSON_COMMAND_DATA_SDCARD_OUT = "sdcardout";
    public static final String JSON_COMMAND_DATA_SDCARD_ERR = "sdcarderr";
    public static final String JSON_COMMAND_DATA_USBDISK_IN = "usbdiskin"; //usb disk
    public static final String JSON_COMMAND_DATA_USBDISK_OUT = "usbdiskout";
    public static final String JSON_COMMAND_DATA_RECORD_FAIL = "recordfail";  //录像失败，例如无卡/卡损坏
    public static final String JSON_COMMAND_RECORDTIME = "RecordTime";

    /* alarm history specified keys for cloud */
    public static final String ALARM_MESSAGE_SENSORTYPE = "sensorType";
    public static final String ALARM_MESSAGE_ALARMTYPE = "alarmType";
    public static final String ALARM_MESSAGE_ALARMTIMESTAMP = "timeStamp"; // time that alarm happens
    public static final String ALARM_MESSAGE_ALARMMSGID = "messageId";
    public static final String ALARM_MESSAGE_LOOPNAME = "loopName";
    public static final String ALARM_MESSAGE_ROOMNAME = "roomName";
    public static final String ALARM_MESSAGE_MODULEADDR = "moduleAddr";
    public static final String ALARM_MESSAGE_MODULETYPE = "moduleType";
    public static final String ALARM_MESSAGE_LOOPID = "loopId";
    /* frequency */
    public static final String FREQUENCY_SINGLE = "single";
    public static final String FREQUENCY_EVERYDAY = "everyday";
    public static final String FREQUENCY_EVERYWORKDAY = "everyworkday";
    public static final String FREQUENCY_EVERYWEEKEND = "everyweekend";
    public static final String FREQUENCY_CUSTOMIZE = "customize";
    public static final String FREQUENCY_CUSTOMIZE_DAY = "day";
    public static final String FREQUENCY_CUSTOMIZE_SUNDAY = "sunday";
    public static final String FREQUENCY_CUSTOMIZE_MONDAY = "monday";
    public static final String FREQUENCY_CUSTOMIZE_TUESDAY = "tuesday";
    public static final String FREQUENCY_CUSTOMIZE_WEDNESDAY = "wednesday";
    public static final String FREQUENCY_CUSTOMIZE_THURSDAY = "thursday";
    public static final String FREQUENCY_CUSTOMIZE_FRIDAY = "friday";
    public static final String FREQUENCY_CUSTOMIZE_SATURDAY = "saturday";

    /* loop type */
    public static final String LOOP_TYPE_LIGHT = "light";
    public static final String LOOP_TYPE_CURTAIN = "curtain";
    public static final String LOOP_TYPE_RELAY = "relay";
    public static final String LOOP_TYPE_SWITCH = "switch";// maia分light和 switch
    public static final String LOOP_TYPE_SENSOR = "sensor";

    /* 433 type */
    public static final String SENSOR_TYPE_433_INFRADE = "5800-PIR-AP";
    public static final String SENSOR_TYPE_433_KEYFOB = "5816EU";
    public static final String SENSOR_TYPE_433_DOORMAGNETI = "5804EU";

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

    public static final String AC_FAN_SPPED_LOW = "low";
    public static final String AC_FAN_SPPED_MIDDLE = "middle";
    public static final String AC_FAN_SPPED_HIGH = "high";
    public static final String AC_FAN_SPPED_AUTO = "auto";


    // for wifi module type value(5~10为wifi module)
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

    /* module type maybe values */
    public static final String JSON_COMMAND_MODULETYPE_485 = "485";
    public static final String JSON_COMMAND_MODULETYPE_BACNET = "bacnet";
    public static final String JSON_COMMAND_MODULETYPE_SPARKLIGHT = "sparklighting";
    public static final String JSON_COMMAND_MODULETYPE_WIREDZONE = "wiredzone";
    public static final String JSON_COMMAND_MODULETYPE_315M433 = "315M433M";
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
    public static final String JSON_COMMAND_MODULETYPE_SCENARIOTRIGGER = "scenariotrigger";
    public static final String JSON_COMMAND_MODULETYPE_MUTEXRULE = "mutexrule";
    public static final String JSON_COMMAND_MODULETYPE_SCHEDULERULE = "schedulerule";
    public static final String JSON_COMMAND_MODULETYPE_ROOM = "room";
    public static final String JSON_COMMAND_MODULETYPE_VENTILATION = "ventilation";

    // for all sparklighting sub_dev_type
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSD0403 = "HBLS-D0403";// string,
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSD0602 = "HBLS-D0602";// string,
    public final static String SPARKLIGHT_SUBDEVTYPE_HBLSD0610DC = "HBLS-D0610-DC";// string,
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


    // ALARMS from ipvdp system
    public static final String ZONE_ALARM_STATUS_GAS = "gas";
    public static final String ZONE_ALARM_STATUS_FIRE = "fire";
    public static final String ZONE_ALARM_STATUS_HELP = "help";
    public static final String ZONE_ALARM_STATUS_THIEF = "thief";
    public static final String ZONE_ALARM_STATUS_EMERGENCY = "emergency";


    public static final String ZONE_TYPE_SECURITY_INSTANT = "instant";
    public static final String ZONE_TYPE_SECURITY_24HOURS = "24hours";
    public static final String ZONE_TYPE_SECURITY_DELAY = "delay";

    public static final String IPCAMERA_TYPE_PHOENIX = "phoenix";
    public static final String IPCAMERA_TYPE_SUPER_HD = "super hd";

    public static final String WIFI485_AC_TYPE_ECC_O1 = "ecc_o1";
    public static final String WIFI485_AC_TYPE_HTC = "htc961d3200";
    public static final String WIFI485_AC_TYPE_ECC_DT300 = "ecc_dt300";


    public final static String DEVICETYPE_MAIA2 = "maia2";
    public final static String DEVICETYPE_SENSOR = "sensor";
    public final static String DEVICETYPE_AIRCONDITION = "aircondition";
    public final static String DEVICETYPE_THERMOSTAT = "thermostat";
    public final static String DEVICETYPE_VENTILATION = "ventilation";

    public static final String FIRST_SEPARATOR = "::::";
    public static final String SECOND_SEPARATOR = ",";
    public static final String DAY_SEPARATOR = "|";
    public static final String CONTROLMAP_SEPARATOR = "*";

    public final static int SCENARIO_ID_HOME = 1;
    public final static int SCENARIO_ID_LEAVE = 2;
    public final static int SCENARIO_ID_ARMALL = 3;
    public final static int SCENARIO_ID_DISARMALL = 4;

    public final static String SCENARIO_HOME_NAME = "回家";
    public final static String SCENARIO_LEAVE_NAME = "离家";
    public final static String SCENARIO_ARMALL_NAME = "全部布防";
    public final static String SCENARIO_DISARMALL_NAME = "全部撤防";

    public final static String IRLOOPTYPE_DVD = "DVD";
    public final static String IRLOOPTYPE_TV = "TV";
    public final static String IRLOOPTYPE_FAN = "FAN";
    public final static String IRLOOPTYPE_AIR = "AIR";
    public final static int SCENARIO_ID_BASIC = 100;

    public final static String DEFAULTSECURITYPWD = "1123445";
    public final static String DEFAULT_DEV_SERIAL_NUMBER = "5555";
    public final static String DEFAULTIPCUSER = "admin";
    public final static String DEFAULTIPCPWD = DEFAULTSECURITYPWD;
    public final static int DEFAULTIPCSTREAMPORT = 554;

    /**
     * upgrade info
     */
    public final static String UPGRADE_RELEASEDATE = "releasedate";
    public final static String UPGRADE_UPGRADECMD = "upgradecmd";
    public final static String UPGRADE_DESCRIPTION = "description";
    public final static String UPGRADE_VERSION = "description";

    /**
     * Scenario 部分，三级页面获取设备列表和Zone列表，用于Event返回数据
     */
    public final static String SCENARIO_EDIT_GET_DEVICES = "devices";
    public final static String SCENARIO_EDIT_GET_ZONES = "zones";


}
